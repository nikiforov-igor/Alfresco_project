package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReport;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.jasper.utils.Utils;

public class DSProdiverApproval extends DSProviderSearchQueryReportBase {

	private static final Logger logger = LoggerFactory.getLogger(DSProdiverApproval.class);

	private Date periodStart, periodEnd;

	public Date getStart() {
		return periodStart;
	}

	public void setStart( final String value) {
		periodStart = ArgsHelper.makeDate(value, "periodStart");
	}

	public void setEnd( final String value) {
		periodEnd = ArgsHelper.makeDate(value, "periodEnd");
	}

	public Date getEnd() {
		return periodEnd;
	}

	final static String TYPE_APPROVAL_ITEM = "lecm-al:approval-item";
	final static String TYPE_APPROVAL_LIST = "lecm-al:approval-list";
	final static String FLDStatus = "lecm-al:approval-list-decision"; // <!-- результат согласования документа -->
	final static String VALUE_STATUS_NOTREADY = "NO_DECISION";

	@Override
	protected String buildQueryText() {

		final StringBuilder bquery = new StringBuilder();
		final QName qTYPE = QName.createQName(TYPE_APPROVAL_LIST, this.serviceRegistry.getNamespaceService());
		bquery.append( "TYPE:"+ quoted(qTYPE.toString()));

		// выполненные Согласования -> вне статуса 'NO_DECISION'
		bquery.append( " AND NOT @lecm\\-al\\:approval\\-list\\-decision:"+ VALUE_STATUS_NOTREADY+ " ");

		// начало == <!-- дата начала согласования -->
		if (getStart() != null) { // "X to MAX"
			final String stMIN = ArgsHelper.dateToStr( getStart(), "MIN");
			bquery.append( " AND @lecm\\-al\\:approval\\-list\\-approve\\-start:[" + stMIN + " TO MAX]");
		}

		// окончание == <!-- дата начала согласования -->
		if (getEnd() != null) { // "MIN to X"
			final String stMAX = ArgsHelper.dateToStr( getEnd(), "MAX");
			// bquery.append( " AND( (@lecm\\-contract\\:unlimited:true) OR (...) )");
			bquery.append( " AND @lecm\\-al\\:approval\\-list\\-approve\\-start:[ MIN TO " + stMAX + "]");
		}

		return bquery.toString();
	}

	@Override
	protected AlfrescoJRDataSource createDS(JasperReport report)
			throws JRException {
		// задаём период выборки в переменные отчёта ...
		setPeriodDates(report.getVariables());
		return super.createDS(report);
	}

	final static String VARNAME_START = "PERIOD_START";
	final static String VARNAME_END = "PERIOD_END";
	private void setPeriodDates(JRVariable[] variables) {
		if (variables == null || variables.length == 0)
			return;
		// TODO: задасть свойства properties, т.к. variables проблематично изменять
//		for(JRVariable var: variables) { 
//			if ( VARNAME_START.equalsIgnoreCase(var.getName())) {
//				// ((JRBaseVariable) var).setValue(this.periodStart) ;
//			} else if ( VARNAME_END.equalsIgnoreCase(var.getName())) {
//				// ((JRBaseVariable) var).setValue(this.periodEnd) ;
//			} 
//		}
	}


	@Override
	protected AlfrescoJRDataSource newJRDataSource( Iterator<ResultSetRow> iterator) {
		return new ApprovalDS(iterator);
	}

	/**
	 * Структура для накопления и хранения средних значений некоторой величины
	 */
	class AvgValue {
		int count; // кол-во
		float avg; // текущее среднее

		public void clear() {
			count = 0;
			avg = 0;
		}

		/**
		 * Скорректировать среднее значение с учётом очердного "замера"
		 * @param value
		 */
		public void adjust(float value) {
			if (++count == 1) { // первая порция данных
				avg = value;
			} else { // корректировка ср значения
				avg = (avg * (count - 1) + value)/count;
			}
		}
	}


	/**
	 * Структура для хранения данных о статистике по Сотруднику
	 */
	private class EmployeeInfo {
		final NodeRef employeeId;

		// ФИО
		String firstName, middleName, lastName;

		// Название основной должности и соот-го подразделения
		String staffName, unitName;

		 // просроченные Согласования
		final AvgValue missedApproves = new AvgValue();

		// Согласования, выполненные в срок
		final AvgValue normalApproves = new AvgValue();

		public EmployeeInfo(NodeRef employeeId) {
			super();
			this.employeeId = employeeId;
		}

		/**
		 * Зарегистрировать длительность работы 
		 * @param plan длительность по плану
		 * @param fact длительность по факту
		 * Если plan >= fact, задача выполнена во время (вычисляем avgApproves)
		 * иначе со срывами срока и вычисляется avdMissed (как fact - plan)
		 */
		public void registerDuration(float plan, float fact) {
			final boolean isOk = (plan >= fact);
			if (isOk) { // средний срок выполнения
				this.normalApproves.adjust(fact);
			} else { // средний срок просрочки
				this.missedApproves.adjust(fact - plan);
			}
		}
	}

	/* Названия полей для JR-jrxml */
	final static String JRName_FIRSTNAME = "col_Employee.FirstName";
	final static String JRName_MIDDLENAME = "col_Employee.MiddleName";
	final static String JRName_LASTNAME = "col_Employee.LastName";

	final static String JRName_STAFFPOSNAME = "col_Employee.StaffPosition";
	final static String JRName_OU = "col_Employee.Unit"; 

	final static String JRName_MISSED_APPROVES_COUNT = "col_Employee.MissedApproves"; // Кол-во просроченных согласований
	final static String JRName_AVG_APPROVE_DAYS = "col_Employee.AvgApproved"; // Средний срок согласований, дней
	final static String JRName_AVG_MISSED_DAYS = "col_Employee.AvgMissed"; // Средний срок просрочки, дней

	// final String JRName_ = "";
/*
	<field name="col_ApproveEnd" class="java.util.Date">
		<fieldDescription><![CDATA[Дата согласования Сотрудником]]></fieldDescription>
	</field>
	<field name="col_ApproveStart" class="java.util.Date">
		<fieldDescription><![CDATA[Дата начала согласования по списку]]></fieldDescription>
	</field>
	<field name="col_ApproveStartByDoc" class="java.util.Date">
		<fieldDescription><![CDATA[Дата начала согласования по документу]]></fieldDescription>
	</field>
 * */
	// название Должностной Позиции
	// "lecm-orgstr:staffPosition"::"lecm-orgstr:staffPosition-code"
	static final QName PROP_DP_INFO = ContentModel.PROP_NAME; // QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "staffPosition-code");

	// "lecm-orgstr:organization-element"::"element-short-name"
	static final QName PROP_ORGUNIT_NAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "element-short-name");

	// "lecm-orgstr:organization-unit"::"unit-code"
	static final QName PROP_ORGUNIT_CODE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "unit-code");

	// <!-- Сотрудник организации -->
 	static final QName PROP_EMPLOYEE_NAME1 = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-first-name");
 	static final QName PROP_EMPLOYEE_NAME2 = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-middle-name");
 	static final QName PROP_EMPLOYEE_NAME3 = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-last-name");

	/**
	 * 10.5.2	Исполнительская дисциплина по согласованиям за период.
	 * Входные данные
	 * •	Список Соласований за некоторый Период согласований.
	 * 
	 * Выходные данные - перегруппированный список по ФИО на основных должностях:
	 * •	ФИО сотрудника
	 * •	Должность сотрудника 
	 * •	Отдел 
	 * •	Количество просроченных согласований
	 * •	Средний срок согласования
	 * •	Средний срок просрочки
	 */
	private class ApprovalDS extends AlfrescoJRDataSource {

		private List<EmployeeInfo> data;
		private Iterator<EmployeeInfo> iterData;

		public ApprovalDS( Iterator<ResultSetRow> iterator) {
			super(iterator);
			buildData();
		}

		@Override
		public void clear() {
			super.clear();
			
		}

		@Override
		public boolean next() throws JRException {
			while (iterData != null && iterData.hasNext()) {
				final EmployeeInfo item = iterData.next();
				curProps = makeCurProps( item);
				return true;
			} // while
			// NOT FOUND MORE - DONE
			curProps = null;
			return false;
		}

		/**
		 * Сформировать контйнер с данными для jr
		 * @param item
		 */
		private Map<String, Serializable> makeCurProps(EmployeeInfo item) {
			final Map<String, Serializable> result = new HashMap<String, Serializable>();

			result.put( getAlfAttrNameByJRKey(JRName_FIRSTNAME), item.firstName);
			result.put( getAlfAttrNameByJRKey(JRName_MIDDLENAME), item.middleName);
			result.put( getAlfAttrNameByJRKey(JRName_LASTNAME), item.lastName);

			result.put( getAlfAttrNameByJRKey(JRName_STAFFPOSNAME), item.staffName );
			result.put( getAlfAttrNameByJRKey(JRName_OU), item.unitName); 

			result.put( getAlfAttrNameByJRKey(JRName_MISSED_APPROVES_COUNT), (int) item.missedApproves.count);
			result.put( getAlfAttrNameByJRKey(JRName_AVG_MISSED_DAYS), (float) roundToHumanRead(item.missedApproves.avg) );
			result.put( getAlfAttrNameByJRKey(JRName_AVG_APPROVE_DAYS), (float) roundToHumanRead(item.normalApproves.avg) );

			return result;
		}


		/**
		 * Округление до четвертей дней 
		 * @param avg
		 * @return
		 */
		float roundToHumanRead(float avg) {
			return (float) (Math.ceil(avg/0.25) * 0.25);
		}

		private String getAlfAttrNameByJRKey(String jrFldName) {
			return (!getMetaFields().containsKey(jrFldName)) ? jrFldName : getMetaFields().get(jrFldName).getValueLink();
		}

		// TODO: заменить потом на модельное значение
		final static float NORMAL_APPROVE_DURATION = 2; // за норму принимаем срок в два дня на задачу (Y)

		/**
		 * Собираем статистику по всем перечисленным в this.rsIter объектах-согласованиях
		 */
		private void buildData() {
			this.data = new ArrayList<EmployeeInfo>();
			if (rsIter != null) {

				final Map<NodeRef, EmployeeInfo> statistic = new HashMap<NodeRef, EmployeeInfo>(); // накопленная статистика по Позователю

				final NodeService nodeSrv = serviceRegistry.getNodeService();
				final NamespaceService ns = serviceRegistry.getNamespaceService();

				// ссылки на отдельные Согласования из списка 
				// final QName ASSOC_APPROVEITEM = QName.createQName("lecm-al:approval-list-contains-approval-item", ns);
				final QName QTYPE_APPROVE_ITEM = QName.createQName(TYPE_APPROVAL_ITEM, ns);
				final Set<QName> childApproveSet = new HashSet<QName>(Arrays.asList(QTYPE_APPROVE_ITEM));

				// <!-- ссылка на элемент справочника "Сотрудники" --> у Списка Согл
				// final QName ASSOC_EMPLOYEE = QName.createQName("lecm-al:approval-item-employee-assoc", ns);
				// final QName TYPE_EMPLOYEE = QName.createQName("lecm-orgstr:employee", ns);
				final Set<QName> childEmployeeSet = new HashSet<QName>(Arrays.asList(OrgstructureBean.TYPE_EMPLOYEE));

				// <!-- дата начала согласования --> у Списка Согласования 
				final QName FLD_STARTAPPROVE = QName.createQName("lecm-al:approval-list-approve-start", ns);

				final QName ASSOC_APPROVAL_ITEM_TO_EMPLOYEE = QName.createQName("lecm-al:approval-item-employee-assoc", ns);

				// <!-- дата начала согласования -->
				// DONE: заменить на модельную дату получения задачи
				// TODO: см также ApprovalListService QName consts
				final QName FLD_USER_APPROVE_START = QName.createQName("lecm-al:approval-item-start-date", ns); // "cm:created"

				// <!-- Дата Cогласования Сотрудником (фактического выполнения) -->
				final QName FLD_USER_APPROVED = QName.createQName("lecm-al:approval-item-approve-date", ns);

				// <!-- допустимый срок согласования сотрудником -->
				final QName FLD_USER_DUE_DATE = QName.createQName("lecm-al:approval-item-due-date", ns);

				while(rsIter.hasNext()) {
					final ResultSetRow rs = rsIter.next();

					final NodeRef approveListId = rs.getNodeRef(); // id Списка Согласований 

					final Map<QName, Serializable> realProps = nodeSrv.getProperties(approveListId); // получение отдельных Согласований внутри списка ... 

					// <!-- дата начала согласования --> у Списка Согласования 
					final Date startApprov = (Date) realProps.get(FLD_STARTAPPROVE);

					final List<ChildAssociationRef> childItems = nodeSrv.getChildAssocs(approveListId, childApproveSet);
					if (childItems == null || childItems.isEmpty())
						continue;

					// поочерёдно грузим данные вложенных согласований
					for (ChildAssociationRef child: childItems) {
						final NodeRef childId = child.getChildRef();
						final Map<QName, Serializable> childProps = nodeSrv.getProperties(childId); // свойства "Согласующего Сотрудника"
						if (childProps == null || childProps.isEmpty()) continue;
						// nodeSrv.getChildAssocs(childId, childEmployeeSet); 
						final List<AssociationRef> employees = nodeSrv.getTargetAssocs(childId,  ASSOC_APPROVAL_ITEM_TO_EMPLOYEE);
						if (employees == null || employees.isEmpty() ) // (!?) с Согласованием не связан никакой сотрудник ...
						{
							logger.warn( String.format( "No eployee found approve item %s", childId));
							continue;
						}
						final NodeRef emplyeeId = employees.get(0).getTargetRef();

						// <!-- Дата получения задачи на Согласование -->
						// DONE: заменить на модельную дату получения задачи
						final Date userApprovStartAt = (Date) childProps.get(FLD_USER_APPROVE_START);

						// <!-- дата фактического Согласования Сотрудником -->
						final Date userApprovedAt = (Date) childProps.get(FLD_USER_APPROVED);

						// Плановая дата завершения задачи
						final Date userDueAt = (Date) childProps.get(FLD_USER_DUE_DATE);

						/*
						 * из доки "Договорная деятельность ТЗ.docx"
						 * 10.5.2	Исполнительская дисциплина по согласованиям за период.
						 * Пусть, X = количество рабочих дней между Датой фактического согласования и Датой получения задачи на Согласование. 
						 * Это фактический срок согласования.
						 * Y = значение атрибута «Плановое время согласования»,
						 * Если X > Y, то считаем такое согласование Просроченным, 
						 * и  величина, равная X – Y будет составлять Срок просрочки.
						 */
						final float fact_duration = calcDuration( userApprovStartAt, userApprovedAt, 0); // X
						final float norm_duration = calcDuration( userApprovStartAt, userDueAt, NORMAL_APPROVE_DURATION); // Y
						final boolean isOutOfTime = fact_duration > norm_duration;

						final EmployeeInfo userInfo;
						if (statistic.containsKey(emplyeeId)) {
							userInfo = statistic.get(emplyeeId);
						} else { // создание стр-ры о Пользователе и наполнение всякой инфой (как зовут, где и кем работает, ...)
							userInfo = new EmployeeInfo(emplyeeId);
							userInfo.firstName = Utils.coalesce( nodeSrv.getProperty( emplyeeId, PROP_EMPLOYEE_NAME1), "");
							userInfo.middleName = Utils.coalesce( nodeSrv.getProperty( emplyeeId, PROP_EMPLOYEE_NAME2), "");
							userInfo.lastName = Utils.coalesce( nodeSrv.getProperty( emplyeeId, PROP_EMPLOYEE_NAME3), "");

							final List<NodeRef> staffList = getOrgstructureService().getEmployeeStaffs(emplyeeId);
							if (staffList != null) {
								final NodeRef staffId = staffList.get(0); // занимаемая Должность

								// название Подразделения ...
								final NodeRef unitId = getOrgstructureService().getUnitByStaff(staffId);
								userInfo.unitName = (unitId == null) ? "" : Utils.coalesce( nodeSrv.getProperty( unitId, PROP_ORGUNIT_NAME), "");

								// получить словарное значение Должности по штатной позиции 
								final NodeRef dpId = orgstructureService.getPositionByStaff(staffId);
								userInfo.staffName = (dpId == null) ? "" : Utils.coalesce( nodeSrv.getProperty( dpId, PROP_DP_INFO), "");
							}
							statistic.put(emplyeeId, userInfo);
						}
						userInfo.registerDuration( norm_duration, fact_duration);
					}
				} // while
				this.data.addAll(statistic.values()); // перенос в основной блок
			}

			this.iterData = this.data.iterator();
		}
	}

	final static int MILLIS_PER_DAY = 86400000;

	/**
	 * Вычислить длительность в днях между парой дат
	 * @param startAt
	 * @param endAt
	 * @return
	 */
	final static float calcDuration(Date startAt, Date endAt, float defaultValue) {
		if (startAt == null || endAt == null)
			return defaultValue;
		final double duration_ms = (endAt.getTime() - startAt.getTime());
		return (float) (duration_ms / MILLIS_PER_DAY);
	} 

}
