package ru.it.lecm.reports.generators.errands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.calc.AvgValue;
import ru.it.lecm.reports.calc.DataGroupCounter;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.generators.errands.ErrandsReportFilterParams.GroupByInfo;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.utils.LuceneSearchBuilder;

/**
 * Исполнительская дисциплина по исполнителям
 * Фильтры отчета:
 * 	•	За Период
 * 	•	Исполнители
 * 		o	Подразделения
 * 		o	Сотрудники
 * Выводимые значения:
 * 	•	Выдано поручений за период (из них важных)
 * 	•	Закрытых поручений за период (из них важных)
 * 	•	Процент исполнения в срок
 * 	•	Процент поручений, отклоненных руководителем
 * 	•	Процент (количество) важных поручений, неисполненных в срок
 * 	•	Среднее время исполнения поручения
 *
 * @author rabdullin
 */
public class ErrandsDisciplineDSProvider
		extends GenericDSProviderBase
{

	private static final Logger logger = LoggerFactory.getLogger(ErrandsDisciplineDSProvider.class);

	/**
	 * Способ группировки элементов определяется параметрами отчёта
	 * Конфигурируется:
	 *    1) формат ссылки
	 *    2) атрибут-источник для группирования
	 */
	final private ErrandsReportFilterParams paramsFilter = new ErrandsReportFilterParams(
			DsDisciplineColumnNames.COL_PARAM_PERIOD, DsDisciplineColumnNames.COL_PARAM_GROUP_BY);


	/** для упрощения работы с QName-объектами */
	private LocalQNamesHelper _qnames;


	final protected LocalQNamesHelper qnames() {
		if (this._qnames == null) {
			this._qnames = new LocalQNamesHelper( this.getServices().getServiceRegistry().getNamespaceService());
		}
		return this._qnames;
	}


	@Override
	protected void setXMLDefaults(Map<String, Object> defaults) {
		super.setXMLDefaults(defaults);
		defaults.put( ErrandsReportFilterParams.XMLGROUPBY_FORMATS_MAP, null);
		defaults.put( ErrandsReportFilterParams.XMLGROUPBY_SOURCE_MAP, null);
	}

	private void loadConfig() {
		try {
			conf().setConfigName( DSXMLProducer.makeDsConfigFileName( this.getReportDescriptor().getMnem()) );
			conf().loadConfig();
			this.paramsFilter.scanGroupByInfo( conf());
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected DataFilter newDataFilter() {
		return super.newDataFilter();
	}

	@Override
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		final ExecDisciplineJRDataSource result = new ExecDisciplineJRDataSource(iterator);
		return result;
	}


	@Override
	protected ResultSet execQuery() {
		loadConfig();
		return super.execQuery();
	}


	@Override
	protected LucenePreparedQuery buildQuery() {

		final LucenePreparedQuery result = super.buildQuery(); // new LucenePreparedQuery();

		final LuceneSearchBuilder builder = new LuceneSearchBuilder( getServices().getServiceRegistry().getNamespaceService());
		builder.emmit( result.luceneQueryText());

		// hasData: становится true после внесения первого любого условия в builder
		boolean hasData = !builder.isEmpty();

		/*
		// задам тип
		if (builder.emmitTypeCond( getReportDescriptor().getFlags().getPreferedNodeType(), null))
			hasData = true;

		// доп критерии из текста запроса
		if (getReportDescriptor().getFlags().getText() != null && !getReportDescriptor().getFlags().getText().isEmpty()) {
			builder.emmit(hasData ? " AND " : "").emmit(getReportDescriptor().getFlags().getText());
			hasData = true;
		}
		 */

		/* 
		 * Критерий двойной:
		 * 		Время Начала 
		 * 		или Время Окончания заданы внутри указанного интервала
		 * Формируется в виде
		 * 		"AND ( start_inside OR end_inside )"
		 */
		final DataSourceDescriptor ds = getReportDescriptor().getDsDescriptor();
		final Date periodStart = paramsFilter.getParamPeriodStart(ds)
				 , periodEnd = paramsFilter.getParamPeriodEnd(ds);
		{
			final String condStart = Utils.emmitDateIntervalCheck( Utils.luceneEncode(LocalQNamesHelper.FLD_START_DATE), periodStart, periodEnd);
			final String condEnd = Utils.emmitDateIntervalCheck( Utils.luceneEncode(LocalQNamesHelper.FLD_END_DATE), periodStart, periodEnd);
			final boolean hasStart = !Utils.isStringEmpty(condStart);
			final boolean hasEnd = !Utils.isStringEmpty(condEnd);
			if (hasStart || hasEnd) {
				final String condBoth;
				if (hasStart && hasEnd) {
					condBoth = String.format( "\n\t( (%s) OR (%s) )\n\t", condStart, condEnd);
				} else if (hasStart) {
					condBoth = String.format( "\n\t(%s)\n\t", condStart);
				} else { // here  (true == hasEnd) ...
					condBoth = String.format( "\n\t(%s)\n\t", condEnd);
				}
				builder.emmit(hasData ? " AND " : "").emmit( condBoth);
				hasData = true;
			}
		}

		/* Формирование */
		result.setLuceneQueryText( builder.toString());
		return result;
	}

	/**
	 * Коды/Названия колонок в наборе данных для отчёта.
	 */
	final private static class DsDisciplineColumnNames {

		/** Колонка "Группировка" - по Подразделениям или по Исполнителям */
		final static String COL_PARAM_GROUP_BY = "Col_GroupBy"; // String значение долно быть в секцииях конфы "groupBy.xxx"
		final static String CONTAINS_GROUP_BY_OU = "OrgUnit"; // подстрока, которая означает группировку по организации

		/** Период с ... по ... */
		final static String COL_PARAM_PERIOD = "Col_Period"; // date, PARAM_DELTA

		/**
		 * Колонка "Cотрудник"
		 * (!) Реальное содержимое определяется настройкой отчёта - Инициатор или Исполнитель
		 */
		final static String COL_PARAM_EXEC_PERSON = "Col_Param_Person"; // alfrescoType="lecm-orgstr:employee", PARAM_TYPE_LIST

		/** Колонка "Исполняющее подразделение" */
		final static String COL_PARAM_EXEC_ORGUNIT = "Col_Param_Exec_OrgUnit"; // alfrescoType="lecm-links:link", PARAM_TYPE_LIST

		/** Колонка результата "Название" в строке данных*/
		final static String COL_NAMEATAG = "Col_NameTag"; // String

		/** Колонка "Выдано всего поручений за период" */
		final static String COL_COUNT_TOTAL = "Col_Count_Total"; // "java.lang.Integer" 

		/** Колонка "Выдано важных поручений за период" */
		final static String COL_COUNT_TOTAL_IMPORTANT = "Col_Count_Total_Important"; // "java.lang.Integer"

		/** Колонка "Всего закрытых поручений за период" */
		final static String COL_COUNT_CLOSED = "Col_Count_Closed"; // "java.lang.Integer" 

		/** Колонка "Закрытых важных поручений за период" */
		final static String COL_COUNT_CLOSED_IMPORTANT = "Col_Count_Closed_important"; // "java.lang.Integer" 

		/** Колонка "Выполнено в срок" */
		final static String COL_COUNT_INTIME = "Col_Count_Intime"; // int

		/** Вычисляемая колонка "Процент исполнения в срок" */
		// final static String CALC_COL_PERCENTS_INTIME = "Col_Percents_Intime"; // java.lang.Float 

		/** Колонка "Кол-во поручений, отклонённых руководителем" */
		final static String COL_COUNT_BOSS_REFUSED = "Col_Count_Boss_Refused"; // int 

		/** Вычисляемая колонка "Процент поручений, отклонённых руководителем" */
		// final static String CALC_COL_PERCENTS_BOSS_REFUSED = "Col_Percents_Boss_Refused"; // java.lang.Float 

		/** Колонка "Кол-во важных поручений, неисполненных в срок" */
		final static String COL_COUNT_IMPORTANT_REFUSED = "Col_Count_Important_Refused"; // java.lang.Integer 

		/** Колонка "Среднее время исполнения поручения" */
		final static String COL_AVG_EXECUTION = "Col_Avg_Execution.Value"; // java.lang.Float 

		/** Колонка "Название единицы измерения среднего времени исполнения" */
		final static String COL_AVG_EXECUTION_UNITS = "Col_Avg_Execution.Units"; // String 
	}

	/** QName-ссылки на данные Альфреско **************************************/
	private class LocalQNamesHelper extends ErrandsQNamesHelper
	{
		/**
		 * Параметр отчёта в НД: Исполнитель, Инициатор или Подразделение, по 
		 * которому фактически будет выполняться группировка ... 
		 */
		QName QN_ASSOC_REF;

		LocalQNamesHelper(NamespaceService ns) {
			super(ns);
			this.QN_ASSOC_REF = QName.createQName(ASSOC_EXECUTOR, this.ns); // by default = по Исполнителю
		}

		public void setQN_ASSOC_REF(String assocQName) {
			this.QN_ASSOC_REF = super.makeQN(assocQName); // QName.createQName(assocQName, ns);
		}

	}

	/**
	 * Структура для хранения данных о статистике по Сотруднику
	 */
	protected class DisciplineGroupInfo {

		final private BasicEmployeeInfo employee;

		/* Счётчики данной персоны */
		final DataGroupCounter counters; // = new DataGroupCounter("");

		/** Среднее время исполнения, часов */
		final AvgValue avgExecTimeInHours = new AvgValue("Avg exec time, h");

		// public DisciplineGroupInfo(NodeRef employeeId) {
		public DisciplineGroupInfo(BasicEmployeeInfo empl) {
			// this.employee = new BasicEmployeeInfo(employeeId);
			this.employee = empl;
			final NodeRef employeeId = empl.employeeId;

			// регим атрибуты ...
			this.counters = new DataGroupCounter( (employeeId != null) ? employeeId.getId() : "");
			// числовые колонки
			this.counters.regAttributes(
					  DsDisciplineColumnNames.COL_COUNT_TOTAL, DsDisciplineColumnNames.COL_COUNT_TOTAL_IMPORTANT
					, DsDisciplineColumnNames.COL_COUNT_CLOSED, DsDisciplineColumnNames.COL_COUNT_CLOSED_IMPORTANT
					, DsDisciplineColumnNames.COL_COUNT_INTIME, DsDisciplineColumnNames.COL_COUNT_BOSS_REFUSED
					, DsDisciplineColumnNames.COL_COUNT_IMPORTANT_REFUSED);
		}

		public void registerDuration(long duration_ms) {
			if (duration_ms <= 0) // нельзя определить
				return; 
			final float fact = Utils.getDurationInHours(duration_ms);  
			this.avgExecTimeInHours.adjust(fact);
		}

		/**
		 * Зарегистрировать длительность исполнения (работы) 
		 * @param start время начала
		 * @param end время конца
		 */
		public void registerDuration(Date start, Date end) {
			if (start == null || end == null) // нельзя определить
				return; 
			registerDuration(end.getTime() - start.getTime());
		}
	}

	/**
	 * Jasper-НД для вычисления статистики
	 */
	private class ExecDisciplineJRDataSource extends TypedJoinDS<DisciplineGroupInfo> {

		private GroupByInfo groupByInfo; // способ группировки, заполняется внутри buildJoin
		private boolean useOUFilter; // true если использовать группировку по Подразделениям, заполняется внутри buildJoin

		public ExecDisciplineJRDataSource(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}


		/**
		 * Прогрузить строку отчёта
		 */
		@Override
		protected Map<String, Serializable> getReportContextProps(DisciplineGroupInfo item)
		{
			final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название подразделения или имя пользователя ... */
			result.put( DsDisciplineColumnNames.COL_NAMEATAG
					, (useOUFilter ? item.employee.unitName : item.employee.ФамилияИО()) );

			result.put( DsDisciplineColumnNames.COL_PARAM_GROUP_BY, this.groupByInfo.grpName);

			/* Сотрудник и его Подразделение */
			result.put( DsDisciplineColumnNames.COL_PARAM_EXEC_PERSON, item.employee.employeeId);
			result.put( DsDisciplineColumnNames.COL_PARAM_EXEC_ORGUNIT, item.employee.unitId);

			/* Счётчики ... */
			// Имена колонок совпадают с названиями счётчиков
			for(Map.Entry<String, Integer> entry: item.counters.getAttrCounters().entrySet()) {
				result.put( entry.getKey(), entry.getValue());
			}

			/* Среднее время исполнения */
			result.put( DsDisciplineColumnNames.COL_AVG_EXECUTION, item.avgExecTimeInHours.getAvg());
			result.put( DsDisciplineColumnNames.COL_AVG_EXECUTION_UNITS, "ч");

			/* (!) Проценты вычисляются непосредственно в jasper-отчёте */

			return result;
		}


		@Override
		public int buildJoin() {
			// построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups)

			// Ключ здесь это Сотрудник или Подразделение, в ~ от группировки (см this.useOUFilter)
			final Map<NodeRef, DisciplineGroupInfo> result = new HashMap<NodeRef, DisciplineGroupInfo>();

			if (context.getRsIter() != null) {

				final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
				// final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

				/* Получение формата и ссылки для выбранного groupby-Измерения из конфигурации ... */
				this.groupByInfo = paramsFilter.getCurrentGroupBy( getReportDescriptor().getDsDescriptor());
				qnames().setQN_ASSOC_REF( groupByInfo.grpAssocQName); // задать название ассоциации для получения Инициаторов или Подразделений

				// использовать фильтр по организациям, если указан тип фильтрации ...  
				this.useOUFilter = (groupByInfo != null) && (groupByInfo.grpName != null)
							&& groupByInfo.grpName.toLowerCase().contains(DsDisciplineColumnNames.CONTAINS_GROUP_BY_OU.toLowerCase());
				final OrgstructureBean beanOU = (useOUFilter) ? getServices().getOrgstructureService() : null;
				String ouNodesList = null; // перечисление выбранных ID Подразделений (если указаны, то будет списком через запятую)
				if (useOUFilter) {
					// выбираем значения из соот-щей колонки ...
					ouNodesList = getUONodesFromParams();
					logger.info( String.format("group by OU, filter is [%s]", Utils.coalesce(ouNodesList, "*")));
				} else {
					logger.info( "group by executors");
				}

				/* проход по все загруженным Поручениям ... */
				while(context.getRsIter().hasNext()) {

					final ResultSetRow rs = context.getRsIter().next();

					final NodeRef errandId = rs.getNodeRef(); // id Поручения 

					// (!) Фильтрование
					if (context.getFilter() != null && !context.getFilter().isOk(errandId)) {
						if (logger.isDebugEnabled())
							logger.debug( String.format("{%s} filtered out", errandId));
						continue;
					}

					// Исполнители
					final List<AssociationRef> employees = nodeSrv.getTargetAssocs(errandId, qnames().QN_ASSOC_REF);
					if (employees == null || employees.isEmpty() ) // (!?) с Поручением не связан никакой Сотрудник-Исполнитель ...
					{
						logger.warn( String.format( "No execution employee found for errand item %s", errandId));
						continue;
					}

					for (int i = 0; i < employees.size(); i++) {
						final NodeRef executorId = employees.get(i).getTargetRef(); // id Сотрудника-Исполнителя 

						final BasicEmployeeInfo execEmployee = new BasicEmployeeInfo( executorId); 

						// если надо будет - грузим данные по подразделениям, 
						// указав второй аргумент: getServices().getOrgstructureService()
						execEmployee.loadProps( nodeSrv, beanOU);

						// ипользуем как ключ либо Сотрудника, либо его Подразделение ...
						// TODO: иметь в виду несколько должностей Сотрудников и вложенность подразделений
						final NodeRef keyId = (this.useOUFilter) ? execEmployee.unitId : execEmployee.employeeId;

						if (this.useOUFilter && ouNodesList != null) { // фильтрование по ID Подразделения
							if ( (execEmployee.unitId == null) || !ouNodesList.contains(execEmployee.unitId.getId())) {
								logger.info( String.format("OU '%s' for executor '%s' filtered out ...", execEmployee.unitName, execEmployee.ФамилияИО()));
								continue; // for i
							}
						}

						final DisciplineGroupInfo executor;
						if (result.containsKey(keyId)) {
							executor = result.get(keyId);
						} else { // создание нового Сотрудника-Исполнителя
							executor = new DisciplineGroupInfo(execEmployee);
							result.put(keyId, executor);
						}

						// прогружаем атрибуты Поручения и корректируем данные ...
						final Map<QName, Serializable> props = nodeSrv.getProperties(errandId);

						// среднее время исполнения ...
						// [ props.get(QNFLD_START_DATE), props.get(QNFLD_END_DATE) ]
						executor.registerDuration( qnames().getВремяИсполнения_мсек(props));

						/* остальные счётчики ... */
						executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_TOTAL); // общее кол-во

						final boolean важное = qnames().isПоручениеВажное(props);
						final boolean вСрок = qnames().isПоручениеИсполненоВСрок(props);

						if (важное)
							executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_TOTAL_IMPORTANT);

						if (qnames().isПоручениеЗакрыто(props)) {
							executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_CLOSED);
							if (важное)
								executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_CLOSED_IMPORTANT);
							if (вСрок)
								executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_INTIME); // исполнено вСрок
						} else { // Поручение НЕ Закрыто
							if (важное && !вСрок) { 
								// важное И неисполненное в срок ...
								executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_IMPORTANT_REFUSED);
							}
						}
						if (qnames().isПоручениеБылоОтклоненоБоссом(props)) {
							executor.counters.incCounter(DsDisciplineColumnNames.COL_COUNT_BOSS_REFUSED);
						}
					}
				} // while

				// (!) перенос в основной блок
				this.setData( new ArrayList<DisciplineGroupInfo>(result.values()) );
			} // if

			if (this.getData() != null)
				this.setIterData(this.getData().iterator());

			logger.info( String.format( "found %s row(s)", result.size()));

			return result.size();
		}


		/**
		 * Получить список Подразделений организации, по которым надо фильтровать ...
		 * @return
		 */
		
		private String getUONodesFromParams() {
			final ColumnDescriptor colOU = getReportDescriptor().getDsDescriptor()
						.findColumnByParameter(DsDisciplineColumnNames.COL_PARAM_EXEC_ORGUNIT);
			if (colOU != null && colOU.getParameterValue() != null) {
				final Object val = colOU.getParameterValue().getBound1();
				if (val != null) {
					final String result;
					if (val instanceof String)
						result = (String) val;
					else if (val instanceof String[]) {
						final String[] arr = (String[]) val;
						result = (arr.length > 0) ? arr[0] : null;
					} else
						result = val.toString();

					if (result != null && result.trim().length() > 0)
						return result.trim(); // (!) FOUND non-empty
				}
			}
			return null; // not present or is empty
		}

	}

}
