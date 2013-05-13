package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.jasper.DSProdiverApproval.ApproveQNameHelper;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.jasper.utils.Utils;

/**
 * Отчёт "Список согласования"
 * параметр node_id списка согласования (типа )
 * Входной параметр:
 *	•	NodeId списка Согласования для формирования отчёта (с типом "lecm-al:approval-list")
 *
 * @author rabdullin
 */
public class DSProviderApprovalById extends DSProviderSearchQueryReportBase {

		private static final Logger logger = LoggerFactory.getLogger(DSProviderApprovalById.class);

		private NodeRef approveListNodeId;

		public String getNodeRef() {
			return (approveListNodeId == null) ? "" : approveListNodeId.toString(); 
		}

		public void setNodeRef(String noderef) {
			this.approveListNodeId = (noderef == null || noderef.trim().length() == 0)
					? null : new NodeRef(noderef);
		}

		@Override
		/**
		 * Формируется запрос вида:
		 * TYPE:"lecm-al:approval-list" AND ID:"workspace://SpacesStore/11a08758-6eb7-450f-9f17-3f168d981629"
		 */
		protected String buildQueryText() {
			if (logger.isDebugEnabled()) {
				logger.debug( String.format("Quering approve list by ref: %s\n", this.approveListNodeId));
			}
			final StringBuilder bquery = new StringBuilder();
			final QName qTYPE = QName.createQName(DSProdiverApproval.TYPE_APPROVAL_LIST, this.serviceRegistry.getNamespaceService());
			bquery.append( "TYPE:"+ quoted(qTYPE.toString()));
			bquery.append( " AND ID:"+ quoted(Utils.coalesce( this.approveListNodeId, "-1")) );

			return bquery.toString();
		}

		@Override
		protected AlfrescoJRDataSource newJRDataSource( Iterator<ResultSetRow> iterator) {
			final ApprovalItemsDS result = new ApprovalItemsDS(iterator);
			result.setSubstitudeService(substitudeService);
			result.setRegistryService(serviceRegistry);
			result.setJrSimpleProps(jrSimpleProps);
			if (conf != null)
				result.setMetaFields(conf.getMetaFields());
			result.buildData();
			return result;
		}

		/**
		 * Контейнер для хранения данных по списку согласования
		 */
		private class DocInfo {
			String docProjectNumber; // номер проекта договора
			String docVersion; // номер версии документа
			String docApproveResult; // результат согласования
			Date docStartApprove, docEndApprove; // начало и конец согласования
			BasicEmployeeInfo docExecutor; // Исполнитель
		}

		/**
		 * Контейнер для Согласующего
		 */
		private class ApprovalInfo extends BasicEmployeeInfo {

			/* данные по основному документу */
			final DocInfo docInfo;

			public String approveResult; // результат согласования
			public String approveNotes; // замечания 
			public Date approvedAt; // дата Согласования

			public ApprovalInfo(NodeRef employeeId, DocInfo docInfo) {
				super(employeeId);
				this.docInfo = docInfo;
			}
			
		}

		/**
		 * Набор данных для отчёта "Печать листа согласования"
		 * Входные данные
		 * •	Объекта Список Соласования, который будет разворачиваться в
		 * набор Согласующих
		 * 
		 * Выходные данные - развёрнутый список Согласующих:
		 * •	данные по документу (номер проекта договора, номер версии документа, дата начала согласования, дата согласования, результат согласования)
		 * •	данные сотрудника-исполнителя (ФИО, должность, подразделение)
		 * •	данные по согласующему (ФИО, должность сотрудника, Отдел, Результат согласования, Замечения, Дата согласования) 
		 */
		protected class ApprovalItemsDS extends AlfrescoJRDataSource {

			private List<ApprovalInfo> data;
			private Iterator<ApprovalInfo> iterData;

			public ApprovalItemsDS( Iterator<ResultSetRow> iterator) {
				super(iterator);
				// buildData();
			}

			@Override
			public boolean next() throws JRException {
				while (iterData != null && iterData.hasNext()) {
					final ApprovalInfo item = iterData.next();
					curProps = makeCurProps( item);
					return true;
				} // while
				// NOT FOUND MORE - DONE
				curProps = null;
				return false;
			}

			/* Названия полей для JR-jrxml */
			final static String JRName_DOC_PROJECTNUM = "col_Doc.ProjectNum"; 
			final static String JRName_DOC_VERSION = "col_AproveList.Version";
			final static String JRName_DOC_APPROVE_RESULT = "col_AproveList.ApproveResult";

			final static String JRName_DOC_APPROVE_START = "col_AproveList.ApproveStart";
			final static String JRName_DOC_APPROVE_END = "col_AproveList.ApproveEnd";

			final static String JRName_EXEC_FIRSTNAME  = "col_ExecEmployee.FirstName";
			final static String JRName_EXEC_MIDDLENAME = "col_ExecEmployee.MiddleName";
			final static String JRName_EXEC_LASTNAME   = "col_ExecEmployee.LastName";

			final static String JRName_EXEC_STAFF_ID = "col_ExecEmployee.StaffPosition.Id";
			final static String JRName_EXEC_STAFF_NAME = "col_ExecEmployee.StaffPosition.Name";

			final static String JRName_EXEC_OU_ID = "col_ExecEmployee.Unit.Id";
			final static String JRName_EXEC_OU_NAME = "col_ExecEmployee.Unit.Name";

			final static String JRName_ITEM_FIRSTNAME  = "col_Item.Employee.FirstName";
			final static String JRName_ITEM_MIDDLENAME = "col_Item.Employee.MiddleName";
			final static String JRName_ITEM_LASTNAME   = "col_Item.Employee.LastName";

			final static String JRName_ITEM_APPROVE_RESULT = "col_Item.ApproveResult";
			final static String JRName_ITEM_APPROVE_NOTES = "col_Item.ApproveNotes";
			final static String JRName_ITEM_APPROVE_DATE = "col_Item.ApproveDate";

			final static String JRName_ITEM_STAFF_ID = "col_Item.Employee.StaffPosition.Id";
			final static String JRName_ITEM_STAFF_NAME = "col_Item.Employee.StaffPosition.Name";

			final static String JRName_ITEM_OU_ID = "col_Item.Employee.Unit.Id";
			final static String JRName_ITEM_OU_NAME = "col_Item.Employee.Unit.Name";

/*
			final static String JRName_ = "";
 */
			/**
			 * Сформировать контйнер с данными для jr
			 * @param item
			 */
			private Map<String, Serializable> makeCurProps(ApprovalInfo item) {
				final Map<String, Serializable> result = new HashMap<String, Serializable>();


				// исполнитель
				if (item.docInfo != null) {
					result.put( getAlfAttrNameByJRKey(JRName_DOC_PROJECTNUM), item.docInfo.docProjectNumber);
					result.put( getAlfAttrNameByJRKey(JRName_DOC_VERSION), item.docInfo.docVersion);
					result.put( getAlfAttrNameByJRKey(JRName_DOC_APPROVE_RESULT), item.docInfo.docApproveResult);

					result.put( getAlfAttrNameByJRKey(JRName_DOC_APPROVE_START), item.docInfo.docStartApprove);
					result.put( getAlfAttrNameByJRKey(JRName_DOC_APPROVE_END), item.docInfo.docEndApprove);

					if (item.docInfo.docExecutor != null) {
						result.put( getAlfAttrNameByJRKey(JRName_EXEC_FIRSTNAME), item.docInfo.docExecutor.firstName);
						result.put( getAlfAttrNameByJRKey(JRName_EXEC_MIDDLENAME), item.docInfo.docExecutor.middleName);
						result.put( getAlfAttrNameByJRKey(JRName_EXEC_LASTNAME), item.docInfo.docExecutor.lastName);

						result.put( getAlfAttrNameByJRKey(JRName_EXEC_STAFF_ID)
								, (item.docInfo.docExecutor.staffId != null) ? item.docInfo.docExecutor.staffId.getId() : "" );
						result.put( getAlfAttrNameByJRKey(JRName_EXEC_STAFF_NAME), item.docInfo.docExecutor.staffName);

						result.put( getAlfAttrNameByJRKey(JRName_EXEC_OU_ID)
								, (item.docInfo.docExecutor.unitId != null) ? item.docInfo.docExecutor.unitId.getId() : "" );
						result.put( getAlfAttrNameByJRKey(JRName_EXEC_OU_NAME), item.docInfo.docExecutor.unitName);
					}
				}

				result.put( getAlfAttrNameByJRKey(JRName_ITEM_FIRSTNAME), item.firstName);
				result.put( getAlfAttrNameByJRKey(JRName_ITEM_MIDDLENAME), item.middleName);
				result.put( getAlfAttrNameByJRKey(JRName_ITEM_LASTNAME), item.lastName);

				result.put( getAlfAttrNameByJRKey(JRName_ITEM_APPROVE_RESULT), item.approveResult);
				result.put( getAlfAttrNameByJRKey(JRName_ITEM_APPROVE_NOTES), item.approveNotes);
				result.put( getAlfAttrNameByJRKey(JRName_ITEM_APPROVE_DATE), item.approvedAt);

				result.put( getAlfAttrNameByJRKey(JRName_ITEM_STAFF_ID)
						, (item.staffId != null) ? item.staffId.getId() : "" );
				result.put( getAlfAttrNameByJRKey(JRName_ITEM_STAFF_NAME), item.staffName);

				result.put( getAlfAttrNameByJRKey(JRName_ITEM_OU_ID)
						, (item.unitId != null) ? item.unitId.getId() : "" );
				result.put( getAlfAttrNameByJRKey(JRName_ITEM_OU_NAME), item.unitName);

				return result;
			}

			/**
			 * Получить название поля (в списке атрибутов curProps объекта), 
			 * соот-щее jasper-названию колонки  
			 * @param jrFldName название колонки для Jasper (оно упрощено относительно "полного" названия в curProps)
			 * @return
			 */
			private String getAlfAttrNameByJRKey(String jrFldName) {
				return (!getMetaFields().containsKey(jrFldName)) ? jrFldName : getMetaFields().get(jrFldName).getValueLink();
			}

			/**
			 * Собираем статистику по всем перечисленным в this.rsIter объектах-согласованиях
			 */
			void buildData() {
				this.data = new ArrayList<ApprovalInfo>();
				if (rsIter != null) {

					final NodeService nodeSrv = serviceRegistry.getNodeService();
					final NamespaceService ns = serviceRegistry.getNamespaceService();

					final ApproveQNameHelper approveQNames = new ApproveQNameHelper(ns);

					while(rsIter.hasNext()) { // тут только одна запись будет
						final ResultSetRow rs = rsIter.next();

						final NodeRef approveListId = rs.getNodeRef(); // id Списка Согласований 

						final Map<QName, Serializable> realProps = nodeSrv.getProperties(approveListId); // получение отдельных Согласований внутри списка ... 


						final DocInfo docInfo = new DocInfo();

						/* получение данных списка */

						// дата начала согласования 
						docInfo.docStartApprove = (Date) realProps.get(approveQNames.QFLD_STARTAPPROVE);

						// дата завершения согласования 
						docInfo.docEndApprove = (Date) realProps.get(approveQNames.QFLD_ENDAPPROVE);

						// результат согласования
						docInfo.docApproveResult = Utils.coalesce( realProps.get(approveQNames.QFLD_APPROVE_RESULT), null);

						// версия и номер проекта договора
						docInfo.docVersion =  Utils.coalesce( realProps.get(approveQNames.QFLD_APPROVE_DOCVER), null);

						{ /* получение данных из основного документа */
							final NodeRef mainDocRef = ApproveQNameHelper.getMainDocByApproveListId(approveListId, nodeSrv);
							final Map<QName, Serializable> docProps = nodeSrv.getProperties(mainDocRef); // если надо станет несколько свойств получать

							docInfo.docProjectNumber = Utils.coalesce( docProps.get(approveQNames.QFLD_DOC_PROJECTNUM), null);

							// Исполнитель:
							final String loginCreator = Utils.coalesce( docProps.get(approveQNames.QFLD_CREATOR), null);
							if (loginCreator != null) { // получение Исполнителя по его login
								final NodeRef person = getServiceRegistry().getPersonService().getPerson(loginCreator);
								if (person != null) {
									final NodeRef executorEmplId = getOrgstructureService().getEmployeeByPerson(person);
									docInfo.docExecutor = new BasicEmployeeInfo(executorEmplId);
									docInfo.docExecutor.loadProps(nodeSrv, getOrgstructureService());
								}
							}
						}

						// получение списка ...
						final List<ChildAssociationRef> childItems = nodeSrv.getChildAssocs(approveListId, approveQNames.childApproveSet);
						if (childItems == null || childItems.isEmpty()) {
							// если списка нет - доавим один пустой элемент только ...
							this.data.add( new ApprovalInfo(null, docInfo));
							continue;
						}

						// поочерёдно грузим данные вложенных согласований
						for (ChildAssociationRef child: childItems) {
							final NodeRef childId = child.getChildRef();
							final Map<QName, Serializable> childProps = nodeSrv.getProperties(childId); // свойства "Согласующего Сотрудника"
							if (childProps == null || childProps.isEmpty()) continue;
							// nodeSrv.getChildAssocs(childId, childEmployeeSet); 
							final List<AssociationRef> employees = nodeSrv.getTargetAssocs(childId, approveQNames.QASSOC_APPROVAL_ITEM_TO_EMPLOYEE);
							if (employees == null || employees.isEmpty() ) // (!?) с Согласованием не связан никакой сотрудник ...
							{
								logger.warn( String.format( "No eployee found for approve item %s", childId));
								continue;
							}
							
							final NodeRef emplyeeId = employees.get(0).getTargetRef();
							final ApprovalInfo apprInfo = new ApprovalInfo(emplyeeId, docInfo);

							apprInfo.loadProps(nodeSrv, getOrgstructureService());

							// <!-- дата фактического Согласования Сотрудником -->
							apprInfo.approvedAt = (Date) childProps.get(approveQNames.QFLD_USER_APPROVED);

							// результат согласования
							apprInfo.approveResult = Utils.coalesce( childProps.get(approveQNames.QFLD_USER_RESULT), null);

							// замечания 
							apprInfo.approveNotes = Utils.coalesce( childProps.get(approveQNames.QFLD_USER_COMMENT), null);

							this.data.add( apprInfo);
						}
					} // while
				}

				this.iterData = this.data.iterator();
			}

		} // ApprovalItemDS
}
