package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.jasper.DSProdiverApprovalSummaryByPeriod.ApproveQNameHelper;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.utils.Utils;

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

		public DSProviderApprovalById() {
			super();
			setPreferedType(DSProdiverApprovalSummaryByPeriod.TYPE_APPROVAL_LIST);
		}

		@Override
		/**
		 * Формируется запрос вида:
		 * TYPE:"lecm-al:approval-list" AND ID:"workspace://SpacesStore/11a08758-6eb7-450f-9f17-3f168d981629"
		 */
		protected String buildQueryText() {
			// выполнить проверку заполнения Id узла
			if (getNodeRef() == null)
				throw new RuntimeException( "NodeRef not specified" );

			return super.buildQueryText();
		}

		@Override
		protected AlfrescoJRDataSource newJRDataSource( Iterator<ResultSetRow> iterator) {
			final ApprovalItemsDS result = new ApprovalItemsDS(iterator);
			result.context.setSubstitudeService(getServices().getSubstitudeService());
			result.context.setRegistryService(getServices().getServiceRegistry());
			result.context.setJrSimpleProps(jrSimpleProps);
			if (conf() != null)
				result.context.setMetaFields(conf().getMetaFields());
			result.buildJoin();
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
		 *   • данные по документу (номер проекта договора, номер версии документа, дата начала согласования, дата согласования, результат согласования)
		 *   • данные сотрудника-исполнителя (ФИО, должность, подразделение)
		 *   • данные по согласующему (ФИО, должность сотрудника, Отдел, Результат согласования, Замечения, Дата согласования) 
		 */
		protected class ApprovalItemsDS extends TypedJoinDS<ApprovalInfo> {

			public ApprovalItemsDS( Iterator<ResultSetRow> iterator) {
				super(iterator);
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
			@Override
			protected Map<String, Serializable> getReportContextProps(ApprovalInfo item) {

				final Map<String, Serializable> result = new HashMap<String, Serializable>();

				// исполнитель
				if (item.docInfo != null) {
					result.put( JRName_DOC_PROJECTNUM, item.docInfo.docProjectNumber);
					result.put( JRName_DOC_VERSION, item.docInfo.docVersion);
					result.put( JRName_DOC_APPROVE_RESULT, item.docInfo.docApproveResult);

					result.put( JRName_DOC_APPROVE_START, item.docInfo.docStartApprove);
					result.put( JRName_DOC_APPROVE_END, item.docInfo.docEndApprove);

					if (item.docInfo.docExecutor != null) {
						result.put( JRName_EXEC_FIRSTNAME, item.docInfo.docExecutor.firstName);
						result.put( JRName_EXEC_MIDDLENAME, item.docInfo.docExecutor.middleName);
						result.put( JRName_EXEC_LASTNAME, item.docInfo.docExecutor.lastName);

						result.put( JRName_EXEC_STAFF_ID
								, (item.docInfo.docExecutor.staffId != null) ? item.docInfo.docExecutor.staffId.getId() : "" );
						result.put( JRName_EXEC_STAFF_NAME, item.docInfo.docExecutor.staffName);

						result.put( JRName_EXEC_OU_ID
								, (item.docInfo.docExecutor.unitId != null) ? item.docInfo.docExecutor.unitId.getId() : "" );
						result.put( JRName_EXEC_OU_NAME, item.docInfo.docExecutor.unitName);
					}
				}

				result.put( JRName_ITEM_FIRSTNAME, item.firstName);
				result.put( JRName_ITEM_MIDDLENAME, item.middleName);
				result.put( JRName_ITEM_LASTNAME, item.lastName);

				result.put( JRName_ITEM_APPROVE_RESULT, item.approveResult);
				result.put( JRName_ITEM_APPROVE_NOTES, item.approveNotes);
				result.put( JRName_ITEM_APPROVE_DATE, item.approvedAt);

				result.put( JRName_ITEM_STAFF_ID, (item.staffId != null) ? item.staffId.getId() : "" );
				result.put( JRName_ITEM_STAFF_NAME, item.staffName);

				result.put( JRName_ITEM_OU_ID, (item.unitId != null) ? item.unitId.getId() : "" );
				result.put( JRName_ITEM_OU_NAME, item.unitName);

				return result;
			}

			/**
			 * Собираем статистику по всем перечисленным в this.rsIter объектах-согласованиях
			 */
			@Override
			public int buildJoin() {
				final ArrayList<ApprovalInfo> result = new ArrayList<ApprovalInfo>();

				if (context.getRsIter() != null) {

					final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
					final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

					final ApproveQNameHelper approveQNames = new ApproveQNameHelper(ns);

					while(context.getRsIter().hasNext()) { // тут только одна запись будет по-идее
						final ResultSetRow rs = context.getRsIter().next();

						final NodeRef approveListId = rs.getNodeRef(); // id Списка Согласований 

						final Map<QName, Serializable> realProps = nodeSrv.getProperties(approveListId); // получение отдельных Согласований внутри списка ... 

						final DocInfo docInfo = new DocInfo();

						/* получение данных списка */

						// дата начала согласования 
						docInfo.docStartApprove = (Date) realProps.get(approveQNames.QFLD_STARTAPPROVE);

						// дата завершения согласования 
						docInfo.docEndApprove = (Date) realProps.get(approveQNames.QFLD_ENDAPPROVE);

						// результат согласования
						docInfo.docApproveResult = makeL12_ApproveResult(
								Utils.coalesce( realProps.get(approveQNames.QFLD_APPROVE_RESULT), null));

						// версия и номер проекта договора
						docInfo.docVersion =  Utils.coalesce( realProps.get(approveQNames.QFLD_APPROVE_DOCVER), null);

						{ /* получение данных из основного документа */
							final NodeRef mainDocRef = ApproveQNameHelper.getMainDocByApproveListId(approveListId, nodeSrv);
							final Map<QName, Serializable> docProps = nodeSrv.getProperties(mainDocRef); // если надо станет несколько свойств получать

							docInfo.docProjectNumber = Utils.coalesce( docProps.get(approveQNames.QFLD_DOC_PROJECTNUM), null);

							// Исполнитель:
							final String loginCreator = Utils.coalesce( docProps.get(approveQNames.QFLD_CREATOR), null);
							if (loginCreator != null) { // получение Исполнителя по его login
								final NodeRef person = getServices().getServiceRegistry().getPersonService().getPerson(loginCreator);
								if (person != null) {
									final NodeRef executorEmplId = getServices().getOrgstructureService().getEmployeeByPerson(person);
									docInfo.docExecutor = new BasicEmployeeInfo(executorEmplId);
									docInfo.docExecutor.loadProps(nodeSrv, getServices().getOrgstructureService());
								}
							}
						}

						// получение списка ...
						final List<ChildAssociationRef> childItems = nodeSrv.getChildAssocs(approveListId, approveQNames.childApproveSet);
						if (childItems == null || childItems.isEmpty()) {
							// если списка нет - добавим один пустой элемент только ...
							result.add( new ApprovalInfo(null, docInfo));
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

							apprInfo.loadProps(nodeSrv, getServices().getOrgstructureService());

							// <!-- дата фактического Согласования Сотрудником -->
							apprInfo.approvedAt = (Date) childProps.get(approveQNames.QFLD_USER_APPROVED);

							// результат согласования
							apprInfo.approveResult = makeL12_ApproveResult(
									Utils.coalesce( childProps.get(approveQNames.QFLD_USER_RESULT), null));

							// замечания 
							apprInfo.approveNotes = Utils.coalesce( childProps.get(approveQNames.QFLD_USER_COMMENT), null);

							result.add( apprInfo);
						}
					} // while
				}

				setData(result);
				setIterData( result.iterator());

				return result.size();
			}

		} // ApprovalItemDS

		// TODO: сделать работу L18 через properties-файл 
		/**
		 * Локализация для "<!-- результат согласования документа -->" и "<!-- Результат согласования сотрудником -->"
		 * @param listValue см lecm-approval-list-model.xml::"lecm-al:approval-item-decision" и "lecm-al:approval-list-decision"
		 * @return
		 */
		static String makeL12_ApproveResult(String listValue) {
			final String l18 = getL18ApproveResultMap().get(listValue);
			return (l18 != null) ? l18 : listValue;
		}

		// TODO: вынести в бин
		static Map<String, String> l18ResultApproveMap;
		static Map<String, String> getL18ApproveResultMap() {
			if (l18ResultApproveMap == null) {
				l18ResultApproveMap = new HashMap<String, String>();
				l18ResultApproveMap.put("NO_DECISION", "решение пока не принято");
				l18ResultApproveMap.put("APPROVED", "согласован");
				l18ResultApproveMap.put("REJECTED", "отклонен");
				l18ResultApproveMap.put("APPROVED_WITH_REMARK", "согласован с замечанием");
				l18ResultApproveMap.put("REJECTED_FORCE", "отозван с согласования");
				l18ResultApproveMap.put("APPROVED_FORCE", "согласование принудительно завершено");
			}
			return l18ResultApproveMap;
		}

}
