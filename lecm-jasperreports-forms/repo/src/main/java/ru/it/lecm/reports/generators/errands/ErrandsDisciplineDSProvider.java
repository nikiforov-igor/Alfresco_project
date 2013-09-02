package ru.it.lecm.reports.generators.errands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.jasperreports.engine.JRException;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.calc.AvgValue;
import ru.it.lecm.reports.calc.DataGroupCounter;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.generators.errands.ErrandsReportFilterParams.DSGroupByInfo;
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
	 *    2) атрибут-источник для группирования:
	 * в колонке данных DsDisciplineColumnNames.COL_PARAM_GROUP_BY должно
	 * быть строковое значение с названием способа группировки. Это название
	 * строго не регламентируется, но:
	 * 1) оно должно быть описано двух xml-секциях:
	 *    ErrandsReportFilterParams.XMLGROUPBY_FORMATS_MAP ("groupBy.formats")
	 *  и ErrandsReportFilterParams.XMLGROUPBY_SOURCE_MAP ("groupBy.source")
	 * 2) для группировки по Подразделениям, оно ДОЛЖНО СОДЕРЖАТЬ подстроку:
	 *    DsDisciplineColumnNames.CONTAINS_GROUP_BY_OU ("OrgUnit")
	 */
	final private ErrandsReportFilterParams paramsFilter = new ErrandsReportFilterParams(
				DsDisciplineColumnNames.COL_PARAM_PERIOD
				, DsDisciplineColumnNames.COL_PARAM_GROUP_BY
				, DsDisciplineColumnNames.CONTAINS_GROUP_BY_OU
				, DsDisciplineColumnNames.COL_PARAM_EXEC_ORGUNIT
	);


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
			logger.error(e.getMessage(), e);
		}
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

		private DSGroupByInfo groupBy; // способ группировки, заполняется внутри buildJoin


		public ExecDisciplineJRDataSource(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}


		/**
		 * Получить характерное для текущей группировки название объекта:
		 * название Подразделения или инициалы Сотрудника.
		 * @param item
		 * @return
		 */
		String getItemName(DisciplineGroupInfo item) {
			return (groupBy.isUseOUFilter() ? item.employee.unitName : item.employee.ФамилияИО());
		}

		/**
		 * Прогрузить строку отчёта
		 */
		@Override
		protected Map<String, Serializable> getReportContextProps(DisciplineGroupInfo item)
		{
			final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название подразделения или имя пользователя ... */
			result.put( DsDisciplineColumnNames.COL_NAMEATAG, getItemName(item) );

			result.put( DsDisciplineColumnNames.COL_PARAM_GROUP_BY, this.groupBy.getGroupByInfo().grpName);

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
				this.groupBy = paramsFilter.findGroupByInfo( getReportDescriptor().getDsDescriptor());
				qnames().setQN_ASSOC_REF( groupBy.getGroupByInfo().grpAssocQName); // задать название ассоциации для получения Инициаторов или Подразделений

				final OrgstructureBean beanOU;
				if (this.groupBy.isUseOUFilter()) {
					beanOU = getServices().getOrgstructureService();
					logger.info( String.format("group by OU, filter is [%s]", Utils.nonblank(this.groupBy.getNodesIdsLine(), "*")));
				} else {
					beanOU = null;
					logger.info( "group by executors");
				}

				/* проход по всем загруженным Поручениям ... */
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

					// прогружаем атрибуты Поручения и корректируем данные ...
					final Map<QName, Serializable> props = nodeSrv.getProperties(errandId);

					for (int i = 0; i < employees.size(); i++) {
						final NodeRef executorId = employees.get(i).getTargetRef(); // id Сотрудника-Исполнителя

						final BasicEmployeeInfo execEmployee = new BasicEmployeeInfo( executorId);

						// грузим данные по подразделениям, только если надо по
						// ним группировать (указав beanOU != null)
						execEmployee.loadProps( nodeSrv, beanOU);

						if (!this.groupBy.isOUEnabled(execEmployee.unitId)) { // фильтрование по ID Подразделения
							logger.info( String.format("Filtered out OU '%s' for executor %s '%s'", execEmployee.unitName, execEmployee.staffName, execEmployee.ФамилияИО()));
							continue; // for i
						}

						// ипользуем как ключ либо Сотрудника, либо его Подразделение ...
						// TODO: иметь в виду несколько должностей Сотрудников и вложенность подразделений
						final NodeRef keyId = (this.groupBy.isUseOUFilter()) ? execEmployee.unitId : execEmployee.employeeId;

						final DisciplineGroupInfo executor;
						if (result.containsKey(keyId)) {
							executor = result.get(keyId);
						} else { // создание нового Сотрудника-Исполнителя
							executor = new DisciplineGroupInfo(execEmployee);
							result.put(keyId, executor);
						}

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

				// DEBUG: добавляем данные, чтобы было достаточно данных для вывода на несколько страниц
				// debugAddManyData(result, 128);

				// (!) перенос в основной блок
				this.setData( getSortedItemsList(result.values()) );
			} // if

			if (this.getData() != null)
				this.setIterData(this.getData().iterator());

			logger.info( String.format( "found %s row(s)", result.size()));

			return result.size();
		}


		private void debugAddManyData( final Map<NodeRef, DisciplineGroupInfo> result, int maxCount)
		{
			if (!result.isEmpty()) {
				int i = 1000;
				final Random rnd = new Random();
				do {
					i++;
					final NodeRef fakeId = new NodeRef( String.format( "workspace://SpacesStore/%s", GUID.generate()) );
					final BasicEmployeeInfo empl = new BasicEmployeeInfo( fakeId);
					{	// фейковые данные по Сотруднику ...
						empl.firstName = String.format( "Name_%s", i);
						empl.lastName = String.format( "Family_%s", i);
						empl.middleName = String.format( "Otchestvo_%s", i);

						empl.unitId = empl.employeeId;
						empl.staffName = String.format( "staff_%s", i);
						empl.unitName = String.format( "Unit_%s", i);
						empl.userLogin = String.format( "login_%s", i);
					}

					final DisciplineGroupInfo executor = new DisciplineGroupInfo(empl);
					// fake-случайная последовательность выполнения поручений ...
					for (int j = 0; j< 1+ rnd.nextInt(10); j++)
						executor.avgExecTimeInHours.adjust(10 + 5*rnd.nextFloat());

					result.put(fakeId, executor);
				} while (result.size() <= maxCount);
			}
		}

		private List<DisciplineGroupInfo> getSortedItemsList(
				Collection<DisciplineGroupInfo> rawItems) {
			final ArrayList<DisciplineGroupInfo> result = new ArrayList<DisciplineGroupInfo>();
			if (rawItems != null) {
				result.addAll(rawItems);

				// сортировка по Алфавиту ...
				Collections.sort(result, new GrpComparator(this));
			}
			return result;
		}

		final private class GrpComparator implements Comparator<DisciplineGroupInfo> {

			final ExecDisciplineJRDataSource ds;

			public GrpComparator(ExecDisciplineJRDataSource ds) {
				super();
				this.ds = ds;
			}

			@Override
			public int compare(DisciplineGroupInfo o1, DisciplineGroupInfo o2) {
				final String s1 = ds.getItemName(o1);
				final String s2 = ds.getItemName(o2);
				return (s1 == null) 
							? (s2 == null ? 0 : 1)
							: (s2 == null ? -1 : s1.compareToIgnoreCase(s2));
			}
		}

	}

}
