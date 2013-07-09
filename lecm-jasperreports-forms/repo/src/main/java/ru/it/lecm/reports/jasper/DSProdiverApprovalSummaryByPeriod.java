package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
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
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.jasper.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * Отчёт "10.5.2 Исполнительская дисциплина по согласованиям за период." 
 * @author rabdullin
 *
 */
public class DSProdiverApprovalSummaryByPeriod extends DSProviderSearchQueryReportBase {

	private static final Logger logger = LoggerFactory.getLogger(DSProdiverApprovalSummaryByPeriod.class);

	private Date periodStartDate, periodEndDate;

	public Date getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate( final Date value) {
		periodStartDate = value;
	}

	public String getPeriodStart() {
		return (periodStartDate != null) ? ArgsHelper.dateToStr( periodStartDate, "periodStart") : null;
	}

	public void setPeriodStart( final String value) {
		periodStartDate = ArgsHelper.makeDate(value, "periodStart");
	}

	public Date getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate( final Date value) {
		periodEndDate = value;
	}

	public String getPeriodEnd() {
		return (periodEndDate != null) ? ArgsHelper.dateToStr( periodEndDate, "periodEnd") : null;
	}

	public void setPeriodEnd( final String value) {
		periodEndDate = ArgsHelper.makeDate(value, "periodEnd");
	}

	final static String VALUE_STATUS_NOTREADY = "NO_DECISION";

	final static String TYPE_APPROVAL_LIST = "lecm-al:approval-list";
	final static String TYPE_APPROVAL_ITEM = "lecm-al:approval-item";
	final static String ASSOC_EMPLOYEE_OF_APPROVE = "lecm-al:approval-item-employee-assoc";

	final static String FLD_Status = "lecm-al:approval-list-decision"; // <!-- результат согласования документа -->
	final static String FLD_StartApprove = "lecm-al:approval-list-approve-start"; // <!-- дата начала согласования --> у Списка Согласования
	final static String FLD_EndApprove = "lecm-al:approval-list-approve-date"; // 	<!-- дата согласования по документу -->

	final static String FLD_UserStartApprove = "lecm-al:approval-item-start-date"; // <!-- дата начала согласования Сотрудником -->
	final static String FLD_UserEndApprove = "lecm-al:approval-item-approve-date"; // <!-- Дата Cогласования Сотрудником (фактического выполнения) -->
	final static String FLD_UserDueDate = "lecm-al:approval-item-due-date"; // <!-- допустимый срок согласования сотрудником -->

	final static String FLD_UserResult = "lecm-al:approval-item-decision"; // <!-- Результат согласования сотрудником -->
	final static String FLD_UserCommet = "lecm-al:approval-item-comment"; // <!-- замечания сотрудника -->

	final static String FLD_APPROVE_RESULT = "lecm-al:approval-list-decision"; // <!-- результат согласования документа --> 
	final static String FLD_APPROVE_DOCVER = "lecm-al:approval-list-document-version"; // <!-- номер версии документа, по которой проводилось согласование -->
	final static String FLD_DOC_PROJECTNUM = "lecm-contract:regNumProject"; // <!-- Регистрационный номер проекта договора--> 

	@Override
	protected String buildQueryText() {

		final StringBuilder bquery = new StringBuilder();
		final QName qTYPE = QName.createQName(TYPE_APPROVAL_LIST, this.getServices().getServiceRegistry().getNamespaceService());
		bquery.append( "TYPE:"+ Utils.quoted(qTYPE.toString()));

		// выполненные Согласования -> вне статуса 'NO_DECISION'
		bquery.append( " AND NOT @lecm\\-al\\:approval\\-list\\-decision:"+ VALUE_STATUS_NOTREADY+ " ");

		// [начало..окончание] для <!-- дата начала согласования -->
		final String cond = Utils.emmitDateIntervalCheck("lecm\\-al\\:approval\\-list\\-approve\\-start", getPeriodStartDate(), getPeriodEndDate());
		if (cond != null) { // "AND X TO Y" ...
			// bquery.append( " AND( (@lecm\\-contract\\:unlimited:true) OR (...) )");
			bquery.append( " AND "+ cond);
		}

		return bquery.toString();
	}

	@Override
	protected AlfrescoJRDataSource createDS(JasperReport report)
			throws JRException {
		// задаём период выборки в переменные отчёта ...
		setPeriodDates(report.getVariables());
		// report.setProperty("start", this.getStart());
		// report.setProperty("end", this.getEnd());
		return super.createDS(report);
	}

//	final static String VARNAME_START = "PERIOD_START";
//	final static String VARNAME_END = "PERIOD_END";

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
	protected class AvgValue {
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
	protected class EmployeeInfo extends BasicEmployeeInfo {
		 // просроченные Согласования
		final AvgValue missedApproves = new AvgValue();

		// Согласования, выполненные в срок
		final AvgValue normalApproves = new AvgValue();

		public EmployeeInfo(NodeRef employeeId) {
			super(employeeId);
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

	final static String JRName_PERIOD_START = "col_Period.Start";
	final static String JRName_PERIOD_END = "col_Period.End";


	// final String JRName_ = "";

	/**
 	 * Очень вспомогательный класс для именования объектов, касающихся согласований
 	 */
 	static class ApproveQNameHelper {

 		final NamespaceService ns;

		final QName QFLD_CREATOR;

		// ссылки на отдельные Согласования из списка 
		// final QName ASSOC_APPROVEITEM = QName.createQName("lecm-al:approval-list-contains-approval-item", ns);
		final QName QTYPE_APPROVE_ITEM;
		final Set<QName> childApproveSet;

		// <!-- ссылка на элемент справочника "Сотрудники" --> у Списка Согл
		// final QName ASSOC_EMPLOYEE = QName.createQName("lecm-al:approval-item-employee-assoc", ns);
		// final QName TYPE_EMPLOYEE = QName.createQName("lecm-orgstr:employee", ns);
		final Set<QName> childEmployeeSet;

		// <!-- дата начала согласования --> у Списка Согласования
		final QName QFLD_STARTAPPROVE;

		// 	<!-- дата согласования по документу -->
		final QName QFLD_ENDAPPROVE;

	 	final QName QASSOC_APPROVAL_ITEM_TO_EMPLOYEE;

		// <!-- Результат согласования сотрудником -->
		final QName QFLD_USER_RESULT;

		// <!-- замечания сотрудника -->
		final QName QFLD_USER_COMMENT;
		
		// <!-- дата начала согласования -->
		// DONE: заменить на модельную дату получения задачи
		// TODO: см также ApprovalListService QName consts
		final QName QFLD_USER_APPROVE_START;

		// <!-- Дата Cогласования Сотрудником (фактического выполнения) -->
		final QName QFLD_USER_APPROVED;

		// <!-- допустимый срок согласования сотрудником -->
		final QName QFLD_USER_DUE_DATE;

		// <!-- результат согласования документа --> 
		final QName QFLD_APPROVE_RESULT;

		// "lecm-al:approval-list-document-version"; // <!-- номер версии документа, по которой проводилось согласование -->
		final QName QFLD_APPROVE_DOCVER;

		// "lecm-contract:regNumProject"; // <!-- Регистрационный номер проекта договора-->
		final QName QFLD_DOC_PROJECTNUM;


		public ApproveQNameHelper(NamespaceService ns) {
			this.ns = ns;

			this.QFLD_CREATOR = QName.createQName("cm:creator", ns);

			this.QTYPE_APPROVE_ITEM = QName.createQName(TYPE_APPROVAL_ITEM, ns);
			this.childApproveSet = new HashSet<QName>(Arrays.asList(QTYPE_APPROVE_ITEM));
			this.childEmployeeSet = new HashSet<QName>(Arrays.asList(OrgstructureBean.TYPE_EMPLOYEE));

			this.QFLD_STARTAPPROVE = QName.createQName(FLD_StartApprove, ns); // <!-- дата согласования по документу -->
			this.QFLD_ENDAPPROVE = QName.createQName(FLD_EndApprove, ns); // <!-- дата согласования по документу -->

			this.QFLD_APPROVE_RESULT = QName.createQName(FLD_APPROVE_RESULT, ns); // <!-- результат согласования документа -->
			this.QFLD_APPROVE_DOCVER = QName.createQName(FLD_APPROVE_DOCVER, ns); // <!-- номер версии документа, по которой проводилось согласование -->
			this.QFLD_DOC_PROJECTNUM = QName.createQName(FLD_DOC_PROJECTNUM, ns);  // <!-- Регистрационный номер проекта договора-->

			this.QASSOC_APPROVAL_ITEM_TO_EMPLOYEE = QName.createQName(ASSOC_EMPLOYEE_OF_APPROVE, ns);

			this.QFLD_USER_RESULT = QName.createQName(FLD_UserResult, ns);
			this.QFLD_USER_COMMENT = QName.createQName(FLD_UserCommet, ns);

			this.QFLD_USER_APPROVE_START = QName.createQName(FLD_UserStartApprove, ns); // "cm:created"
			this.QFLD_USER_APPROVED = QName.createQName(FLD_UserEndApprove, ns);

			this.QFLD_USER_DUE_DATE = QName.createQName(FLD_UserDueDate, ns);
		}

		/**
		 * Получить главный документ исходя из списка Согласования.
		 * Главный документ типа "lecm-contract:document" находится на три (!) 
		 * уровня выше чем "lecm-al:approval-list"
		 * @param approveListId
		 * @param nodeSrv
		 * @return
		 */
		static public NodeRef getMainDocByApproveListId(NodeRef approveListId, NodeService nodeSrv) {
			// approveListId: тип "lecm-al:approval-list", название "cm:title"="Лист согласования версия 0.0" 

			final NodeRef parent1 = nodeSrv.getPrimaryParent(approveListId).getParentRef(); // папка типа "cm:folder", cm:name="Параллельное согласование"
			final NodeRef parent2 = nodeSrv.getPrimaryParent(parent1).getParentRef(); // папка типа "cm:folder", cm:name="Согласование"
			final NodeRef parent3 = nodeSrv.getPrimaryParent(parent2).getParentRef(); // папка типа "lecm-contract:document"

			return parent3; // о как
		}
 	}

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
	protected class ApprovalDS extends AlfrescoJRDataSource {

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
				context.setCurNodeProps( makeCurProps( item));
				return true;
			} // while
			// NOT FOUND MORE - DONE
			context.setCurNodeProps(null);
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

			// период согласования включаем как данные
			final Date now = new Date();
			result.put( getAlfAttrNameByJRKey(JRName_PERIOD_START), (periodStartDate != null) ? periodStartDate : null);
			result.put( getAlfAttrNameByJRKey(JRName_PERIOD_END), (periodEndDate != null) ? periodEndDate : now);

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

		// TODO: заменить потом на модельное значение
		final static float NORMAL_APPROVE_DURATION = 2; // за норму принимаем срок в два дня на задачу (Y)

		/**
		 * Собираем статистику по всем перечисленным в this.rsIter объектах-согласованиях
		 */
		private void buildData() {
			this.data = new ArrayList<EmployeeInfo>();
			if (context.getRsIter() != null) {

				final Map<NodeRef, EmployeeInfo> statistic = new HashMap<NodeRef, EmployeeInfo>(); // накопленная статистика по Позователю

				final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
				final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

				final ApproveQNameHelper approveQNames = new ApproveQNameHelper(ns);

				while(context.getRsIter().hasNext()) {
					final ResultSetRow rs = context.getRsIter().next();

					final NodeRef approveListId = rs.getNodeRef(); // id Списка Согласований 

					// final Map<QName, Serializable> realProps = nodeSrv.getProperties(approveListId); // получение отдельных Согласований внутри списка ... 

					// <!-- дата начала согласования --> у Списка Согласования 
					// final Date startApprov = (Date) realProps.get(approveQNames.QFLD_STARTAPPROVE);

					final List<ChildAssociationRef> childItems = nodeSrv.getChildAssocs(approveListId, approveQNames.childApproveSet);
					if (childItems == null || childItems.isEmpty())
						continue;

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

						// <!-- Дата получения задачи на Согласование -->
						// DONE: заменить на модельную дату получения задачи
						final Date userApprovStartAt = (Date) childProps.get(approveQNames.QFLD_USER_APPROVE_START);

						// <!-- дата фактического Согласования Сотрудником -->
						final Date userApprovedAt = (Date) childProps.get(approveQNames.QFLD_USER_APPROVED);

						// Плановая дата завершения задачи
						final Date userDueAt = (Date) childProps.get(approveQNames.QFLD_USER_DUE_DATE);

						/*
						 * из доки "Договорная деятельность ТЗ.docx"
						 * 10.5.2	Исполнительская дисциплина по согласованиям за период.
						 * Пусть, X = количество рабочих дней между Датой фактического согласования и Датой получения задачи на Согласование. 
						 * Это фактический срок согласования.
						 * Y = значение атрибута «Плановое время согласования»,
						 * Если X > Y, то считаем такое согласование Просроченным, 
						 * и  величина, равная X – Y будет составлять Срок просрочки.
						 */
						final float fact_duration = Utils.calcDurationInDays( userApprovStartAt, userApprovedAt, 0); // X
						final float norm_duration = Utils.calcDurationInDays( userApprovStartAt, userDueAt, NORMAL_APPROVE_DURATION); // Y
						// final boolean isOutOfTime = fact_duration > norm_duration;

						final EmployeeInfo userInfo;
						if (statistic.containsKey(emplyeeId)) {
							userInfo = statistic.get(emplyeeId);
						} else { // создание стр-ры о Пользователе и наполнение всякой инфой (как зовут, где и кем работает, ...)
							userInfo = new EmployeeInfo(emplyeeId);
							userInfo.loadProps(nodeSrv, getServices().getOrgstructureService());
							statistic.put(emplyeeId, userInfo);
						}
						userInfo.registerDuration( norm_duration, fact_duration); // X,Y регистрируется
					}
				} // while
				this.data.addAll(statistic.values()); // перенос в основной блок
			}

			this.iterData = this.data.iterator();
		}
	}

	private void openOfficeExecute() {
		final String ooFileNameTemplate = "/reportdefinitions/oo-templates/ExampleArgsOfTheDoc.odt";
		final String ooFileNameResult = "/reportdefinitions/oo-templates/generated.odt";
		try {
			ooConvert( ooFileNameTemplate, ooFileNameResult);
		} catch(Throwable tx) {
			logger.error(String.format( "Error generating ooffice new file\n\t '{%s}'\n\t from '{%s}'\n\t error %s", ooFileNameResult, ooFileNameTemplate, tx.getMessage()), tx);
		}
	}

	private static void ooConvert(String namein, String nameout)
			throws BootstrapException, com.sun.star.io.IOException, Exception, MalformedURLException
	{

		// final File sourceFile = new java.io.File(args[1]);
		// String sSaveUrl = "file:///"+ sourceFile.getCanonicalPath().replace('\\', '/');

		final String sLoadUrl = "file:///"+ namein.replace('\\', '/');
		final String sSaveUrl = "file:///"+ nameout.replace('\\', '/');

		// ensureOfficeLocally();

		// Get the remote office component context
		XComponentContext xContext = Bootstrap.bootstrap();
		logger.info("Connected to a running office ...");

		// Get the remote office service manager
		XMultiComponentFactory xMCF = xContext.getServiceManager();

		// Get the root frame (i.e. desktop) of openoffice framework.
		Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);

		// Desktop has 3 interfaces. The XComponentLoader interface provides ability to load components.
		XComponentLoader xCLoader =  ( XComponentLoader ) UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);

		XComponent document =  null;
		{ // OPEN FILE
			final PropertyValue[] props = new PropertyValue[4];
			props[0] = newPropertyValue("МоёПолеТекст", "абвгдеёжзик");
			props[1] = newPropertyValue("МояДата", (new SimpleDateFormat("yyyy/MM/dd")).parse("2012/03/22") );
			props[2] = newPropertyValue("МyFieldText", "abcdefghijk");
			props[3] = newPropertyValue("MyFieldNumber", 123);

			// Create a document
			document = xCLoader.loadComponentFromURL(sLoadUrl, "_blank", 0, props);
			// Object oDocToStore = xCLoader.loadComponentFromURL( sLoadUrl.toString(), "_blank", 0, propertyValue );
			logger.info("\nDocument \"" + sLoadUrl + "\" saved under \"" + sSaveUrl + "\"\n");
		}


		// Saving a document
		com.sun.star.frame.XStorable xStorable = null;
		if (document != null) {
			xStorable = (com.sun.star.frame.XStorable)
					UnoRuntime.queryInterface(com.sun.star.frame.XStorable.class, document );

			/*
			storeProps = new com.sun.star.beans.PropertyValue[ 2 ];
			storeProps[0] = new com.sun.star.beans.PropertyValue();
			storeProps[0].Name = "Overwrite";
			storeProps[0].Value = new Boolean(true);
			storeProps[1] = new com.sun.star.beans.PropertyValue();
			storePropse[1].Name = "FilterName";
			storeProps[1].Value = "StarOffice XML (Writer)";
			*/
			final PropertyValue[] storeProps = new PropertyValue[0];
			storeProps[0].Name = "FilterName";
			storeProps[0].Value = "Rich Text Format";
			xStorable.storeAsURL( sSaveUrl.toString(), storeProps);
			logger.info("\nDocument \"" + sLoadUrl + "\" saved under \"" +sSaveUrl + "\"\n");
		}

		// Get the textdocument
		// XTextDocument aTextDocument = ( XTextDocument )UnoRuntime.queryInterface(com.sun.star.text.XTextDocument.class, document);

		{
			// Closing the converted document. Use XCloseable.close if the
			// interface is supported, otherwise use XComponent.dispose
			com.sun.star.util.XCloseable xCloseable =
					(com.sun.star.util.XCloseable)UnoRuntime.queryInterface(
							com.sun.star.util.XCloseable.class, xStorable);

			if ( xCloseable != null ) {
				xCloseable.close(false);
			} else {
				com.sun.star.lang.XComponent xComp =
						(com.sun.star.lang.XComponent)UnoRuntime.queryInterface(
								com.sun.star.lang.XComponent.class, xStorable);
				xComp.dispose();
			}
		}
	}

	static PropertyValue newPropertyValue(String propName, Object propVal) {
		final PropertyValue result = new PropertyValue();
		result.Name = propName;
		result.Value = propVal;
		return result;
	}
}
