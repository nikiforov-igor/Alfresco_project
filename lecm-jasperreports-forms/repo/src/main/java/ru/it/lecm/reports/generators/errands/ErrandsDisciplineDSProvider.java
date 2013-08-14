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

import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.calc.AvgValue;
import ru.it.lecm.reports.calc.DataGroupCounter;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.jasper.utils.JRUtils;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.utils.LuceneSearchBuilder;

/**
 * Исполнительская дисциплина по исполнителям
 * Фильтры отчета
 * 	•	Исполнители
 * 		o	Подразделения
 * 		o	Сотрудники
 * 	•	За Период
 * Выводимые значения
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

	/** XML Config */
	final static String XMLGROUPBY_FORMATS_MAP = "groupBy.formats";
	final static String XMLGROUPBY_SOURCE_MAP = "groupBy.source";

	/** Коды/названия колонок в НД */

	/** Колонка "Групировка" - по Подразделениям или по Исполнителям */
	final static String COL_GROUP_BY = "Col_GroupBy"; // String значение долно быть в секцииях конфы "groupBy.xxx"

	/** Название */
	final static String COL_NAMEATAG = "Col_NameTag"; // String

	/** Период с ... по ... */
	final static String COL_PERIOD = "Col_Period"; // date, PARAM_DELTA

	/**
	 * Колонка "Cотрудник"
	 * (!) Реальное содержимое определяется настройкой отчёта - Инициатор или Исполнитель
	 */
	final static String COL_PARAM_EXEC_PERSON = "Col_Param_Person"; // alfrescoType="lecm-orgstr:employee", PARAM_TYPE_LIST

	/** Колонка "Исполняющее подразделение" */
	final static String COL_PARAM_EXEC_ORGUNIT = "Col_Param_Exec_OrgUnit"; // alfrescoType="lecm-links:link", PARAM_TYPE_LIST

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
	final static String CALC_COL_PERCENTS_INTIME = "Col_Percents_Intime"; // java.lang.Float 

	/** Колонка "Кол-во поручений, отклонённых руководителем" */
	final static String COL_COUNT_BOSS_REFUSED = "Col_Count_Boss_Refused"; // int 

	/** Вычисляемая колонка "Процент поручений, отклонённых руководителем" */
	final static String CALC_COL_PERCENTS_BOSS_REFUSED = "Col_Percents_Boss_Refused"; // java.lang.Float 

	/** Колонка "Кол-во важных поручений, неисполненных в срок" */
	final static String COL_COUNT_IMPORTANT_REFUSED = "Col_Count_Important_Refused"; // java.lang.Integer 

	/** Колонка "Среднее время исполнения поручения" */
	final static String COL_AVG_EXECUTION = "Col_Avg_Execution.Value"; // java.lang.Float 

	/** Колонка "Название единицы измерения среднего времени исполнения" */
	final static String COL_AVG_EXECUTION_UNITS = "Col_Avg_Execution.Units"; // String 

	/**
	 * Способ группировки элементов определяется параметрами отчёта
	 * Конфигурируется:
	 *    1) формат ссылки
	 */
	// private enum EGroupBy { byPerson, byOrgUnit; }
	// private EGroupBy groupBy = EGroupBy.byPerson;

	/**
	 * форматы и исходные поля для групп (ключ=название группы) 
	 */
	private LinkedHashMap<String, GroupByInfo> groupByMap; 

	/** для упрощения работы с QName-объектами */
	private ErrandQNamesHelper _qnames;


	@Override
	protected void setXMLDefaults(Map<String, Object> defaults) {
		super.setXMLDefaults(defaults);
		defaults.put( XMLGROUPBY_FORMATS_MAP, null);
		defaults.put( XMLGROUPBY_SOURCE_MAP, null);
	}


	/**
	 * Получить характеристику groupBy согласно текущему состоянию НД 
	 * @return
	 */
	private GroupByInfo getCurrentGroupBy() {
		String groupBy = null; // not found

		{ // поиск способа группировки в колонке НД ...
			final ColumnDescriptor colGroupBy = getReportDescriptor().getDsDescriptor().findColumnByName(COL_GROUP_BY);
			if (colGroupBy != null) {
				groupBy = colGroupBy.getExpression();
				if (groupBy != null)
					groupBy = groupBy.trim();
			}
		}
		if (Utils.isStringEmpty(groupBy)) {
			// не задан параметр группирования ... -> выбираем первый попавшийся

			if (groupByMap.isEmpty()) // вообще не сконфигурировано - отвалиться ...
				throw new RuntimeException( String.format("GroupBy is not set (check data column '%s' value)", COL_GROUP_BY) );

			groupBy = groupByMap.keySet().iterator().next(); // первый элемент
			logger.warn( String.format( "Column parameter '%s' is not present or has empty expression -> groupBy is set to '%s'"
					, COL_GROUP_BY, groupBy ));
		}

		final GroupByInfo result = groupByMap.get(groupBy);
		if (result == null) {
			final String msg = String.format( "Invalid groupBy criteria '%s':\n\tColumn '%s' contains expression that was not defined at config sections '%s'/'%s'\n\tUsable defines are: %s"
					, groupBy, COL_GROUP_BY, XMLGROUPBY_FORMATS_MAP, XMLGROUPBY_SOURCE_MAP, groupByMap );
			logger.error( msg);
			throw new RuntimeException( msg);
		}

		return result;
	}

	private void loadConfig() {
		try {
			conf().setConfigName( DSXMLProducer.makeDsConfigFileName( this.getReportDescriptor().getMnem()) );
			conf().loadConfig();
			scanGroupByInfo( conf());
		} catch (JRException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Загрузка параметром группирования из конфигурации
	 * @param config
	 */
	private void scanGroupByInfo(JRDSConfigXML config) {
		if (config == null)
			return;
		this.groupByMap = new LinkedHashMap<String, GroupByInfo>();

		{ /* Название-Формат */
			final Map<String, Object> grpFormats = config.getMap(XMLGROUPBY_FORMATS_MAP);
			if (grpFormats != null) {
				for(Map.Entry<String, Object> entry: grpFormats.entrySet()) {
					if (!this.groupByMap.containsKey(entry.getKey())) {
						// новый элемент ...
						this.groupByMap.put( entry.getKey(), new GroupByInfo(entry.getKey())); 
					}
					this.groupByMap.get(entry.getKey()).grpFmt = Utils.coalesce( entry.getValue(), "");
				}
			}
		}

		{ /* Название-Источник */
			final Map<String, Object> grpAssocs = config.getMap(XMLGROUPBY_SOURCE_MAP);
			if (grpAssocs != null) {
				for(Map.Entry<String, Object> entry: grpAssocs.entrySet()) {
					if (!this.groupByMap.containsKey(entry.getKey())) {
						// новый элемент здесь выглядит странно ...
						logger.warn( String.format("Config section '%s' contains item '%s' that was not present at section '%s'"
								, XMLGROUPBY_SOURCE_MAP, entry.getKey(), XMLGROUPBY_FORMATS_MAP));
						this.groupByMap.put( entry.getKey(), new GroupByInfo(entry.getKey())); 
					}
					this.groupByMap.get(entry.getKey()).grpAssocQName = Utils.coalesce( entry.getValue(), "");
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "Config loaded for groupBy:\n%s", this.groupByMap));
		} 
	}


	/** QName-ссылки на данные Альфреско **************************************/
	private class ErrandQNamesHelper {

		/** namespace "Поручения" */
		final static String NSURI_ERRANDS = "lecm-errands"; 

		/** type "Поручение" */
		final static String TYPE_ERRANDS = "lecm-errands:document"; 

		/** assoc Исполнитель (1) */
		final static String ASSOC_EXECUTOR = "lecm-errands:executor-assoc"; // default value for QN_ASSOC_REF

		/** Дата выдачи поручения: date */
		final static String FLD_START_DATE = "lecm-errands:start-date";

		/** Дата завершения поручения: date */
		final static String FLD_END_DATE = "lecm-errands:end-date";

		/** Требуемый срок исполнения поручения: date */
		final static String FLD_LIMIT_DATE = "lecm-errands:limitation-date";

		/** Признак того, что поручение было когда-либо отклонено: boolean, default=false */
		final static String FLD_WAS_REJECTED ="lecm-errands:was-rejected";

		/** "Важность": boolean, default = false */
		final static String FLD_IS_IMPORTANT = "lecm-errands:is-important";

		/** Просрочено: boolean, default=false */
		final static String FLD_IS_EXPIRED = "lecm-errands:is-expired";

		/** type "Поручение" */
		final QName QN_TYPE_ERRANDS; 

		/** Исполнитель или что-то иное ... */
		QName QN_ASSOC_REF;

		/** Дата выдачи поручения: date */
		final QName QNFLD_START_DATE;

		/** Дата завершения поручения: date */
		final QName QNFLD_END_DATE;

		/** Требуемый срок исполнения поручения: date */
		final QName QNFLD_LIMIT_DATE;

		/** Признак того, что поручение было когда-либо отклонено: boolean, default=false */
		final QName QNFLD_WAS_REJECTED;

		/** "Важность": boolean, default = false */
		final QName QNFLD_IS_IMPORTANT;

		/** Просрочено: boolean, default=false */
		final QName QNFLD_IS_EXPIRED;

		final NamespaceService ns;

		ErrandQNamesHelper(NamespaceService ns) {

			this.ns = ns;
			this.QN_TYPE_ERRANDS = QName.createQName(TYPE_ERRANDS, this.ns);
			this.QN_ASSOC_REF = QName.createQName(ASSOC_EXECUTOR, this.ns);

			this.QNFLD_START_DATE = QName.createQName(FLD_START_DATE, this.ns);
			this.QNFLD_END_DATE = QName.createQName(FLD_END_DATE, this.ns);
			this.QNFLD_LIMIT_DATE = QName.createQName(FLD_LIMIT_DATE, this.ns);

			this.QNFLD_WAS_REJECTED = QName.createQName(FLD_WAS_REJECTED, this.ns);
			this.QNFLD_IS_IMPORTANT = QName.createQName(FLD_IS_IMPORTANT, this.ns);
			this.QNFLD_IS_EXPIRED = QName.createQName(FLD_IS_EXPIRED, this.ns);
		}

		/**
		 * Проверить установлен ли boolean-флажок в свойствах
		 * @param props набор свойств
		 * @param flagId имя свойства с флажком
		 * @return
		 */
		final boolean isFlaged(Map<QName, Serializable> props, QName flagId) {
			return (props != null)
					&& props.containsKey(flagId) 
					&& Boolean.TRUE.equals(props.get(flagId));
		}

//		final boolean isFlaged(Map<QName, Serializable> props, String flag) {
//			return isFlaged( props, this.getFldQname(flag));
//		}

		final boolean isПоручениеВажное(Map<QName, Serializable> props) {
			return isFlaged( props, QNFLD_IS_IMPORTANT);
		}

		final boolean isПоручениеБылоОтклонено(Map<QName, Serializable> props) {
			return isFlaged( props, QNFLD_WAS_REJECTED);
		}

		final boolean isПоручениеИсполненоВСрок( Map<QName, Serializable> props) {
			return !isFlaged( props, QNFLD_IS_EXPIRED);
		}

		/**
		 * Проверить закрыто ли поручение - считаем закрытыми такие, у которых дата закрытия определена (не null).
		 * @param props
		 * @return
		 */
		public boolean isПоручениеЗакрыто(Map<QName, Serializable> props) {
			return props.get(QNFLD_END_DATE) != null;
		}

		public void setQN_ASSOC_REF(String assocQName) {
			this.QN_ASSOC_REF = QName.createQName(assocQName, this.ns);
		}

	}

	final protected ErrandQNamesHelper qnames() {
		if (this._qnames == null) {
			this._qnames = new ErrandQNamesHelper( this.getServices().getServiceRegistry().getNamespaceService());
		}

		return this._qnames;
	}

	@Override
	protected ResultSet execQuery() {
		loadConfig();
		return super.execQuery();
	}

	@Override
	protected LucenePreparedQuery buildQuery() {
		final LuceneSearchBuilder builder = new LuceneSearchBuilder( getServices().getServiceRegistry().getNamespaceService());

		/* задам тип */
		// hasData: становится true после внесения первого любого условия в builder
		boolean hasData = builder.emmitTypeCond( getReportDescriptor().getFlags().getPreferedNodeType(), null);

		/* доп критерии из текста запроса */
		if (getReportDescriptor().getFlags().getText() != null && !getReportDescriptor().getFlags().getText().isEmpty()) {
			builder.emmit(hasData ? " AND " : "").emmit(getReportDescriptor().getFlags().getText());
			hasData = true;
		}

		/* 
		 * Время начала или время окончания задано внутри указанного интервала
		 * в виде AND ( start_inside OR end_inside )
		 */
		final Date periodStart = getParamPeriodStart(), periodEnd = getParamPeriodEnd();
		{
			final String condStart = Utils.emmitDateIntervalCheck( Utils.luceneEncode(ErrandQNamesHelper.FLD_START_DATE), periodStart, periodEnd);
			final String condEnd = Utils.emmitDateIntervalCheck( Utils.luceneEncode(ErrandQNamesHelper.FLD_END_DATE), periodStart, periodEnd);
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
		final LucenePreparedQuery result = new LucenePreparedQuery();
		result.setLuceneQueryText( builder.toString());
		return result;
	}

	private ColumnDescriptor getCheckedPeriodColumn() {
		final ColumnDescriptor period = getReportDescriptor().getDsDescriptor().findColumnByName(COL_PERIOD);
		if (period == null || period.getParameterValue() == null) {
			logger.warn( String.format("Dataset not contains parameterized column '%s'", COL_PERIOD));
		}
		return period;
	}

	/** Получить параметр-начало периода */
	private Date getParamPeriodEnd() {
		final ColumnDescriptor period = getCheckedPeriodColumn();
		return (period != null && period.getParameterValue() != null) 
						? (Date) period.getParameterValue().getBound1()
						: null;
	}

	/** Получить параметр-конец периода */
	private Date getParamPeriodStart() {
		final ColumnDescriptor period = getCheckedPeriodColumn();
		return (period != null && period.getParameterValue() != null) 
						? (Date) period.getParameterValue().getBound2()
						: null;
	}

	/**
	 * Нет фильтра
	 */
	@Override
	protected DataFilter newDataFilter() {
		return null; // super.newDataFilter();
	}

	@Override
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		final ExecDisciplineJRDataSource result = new ExecDisciplineJRDataSource(iterator);
		result.getContext().setSubstitudeService( getServices().getSubstitudeService());
		result.getContext().setRegistryService( getServices().getServiceRegistry());
		// result.getContext().setJrSimpleProps( jrSimpleProps);
		result.getContext().setMetaFields( JRUtils.getDataFields(getReportDescriptor()));
		// if (filter != null) result.getContext().setFilter( filter.makeAssocFilter());
		result.buildJoin();
		return result;
	}

	/**
	 * Структура для хранения данных о статистике по Сотруднику
	 */
	protected class EmployeeInfo extends BasicEmployeeInfo {

		/* Счётчики данной персоны */
		final DataGroupCounter counters; // = new DataGroupCounter("");

		/** Среднее время исполнения, часов */
		final AvgValue avgExecTime = new AvgValue("Avg exec time, h");

		public EmployeeInfo(NodeRef employeeId) {
			super(employeeId);
			// регим атрибуты ...
			this.counters = new DataGroupCounter( (employeeId != null) ? employeeId.getId() : "");
			// числовые колонки
			this.counters.regAttributes(
					  COL_COUNT_TOTAL, COL_COUNT_TOTAL_IMPORTANT
					, COL_COUNT_CLOSED, COL_COUNT_CLOSED_IMPORTANT
					, COL_COUNT_INTIME, COL_COUNT_BOSS_REFUSED
					, COL_COUNT_IMPORTANT_REFUSED);
		}

		final static long MILLIS_PER_HOUR = 1000 * 60 * 60;

		/**
		 * Зарегистрировать длительность исполнения (работы) 
		 * @param start время начала
		 * @param end время конца
		 */
		public void registerDuration(Date start, Date end) {
			if (start == null || end == null) // нельзя определить
				return; 
			final float fact = (float) (end.getTime() - start.getTime()) / MILLIS_PER_HOUR;  
			this.avgExecTime.adjust(fact);
		}

		/**
		 * @return Фамилия И.О.
		 */
		public String ФамилияИО() {
			/// JASPER jrxml format: $F{col_Employee.LastName}+ " "+ ($F{col_Employee.FirstName}+ " ").substring( 0, 1 )+ "."+ ($F{col_Employee.MiddleName}+ " ").substring(0,1) + "."
			final StringBuilder result = new StringBuilder();
			result.append( Фамилия());
			// Далее первые буквы Имени и отчества в верхнем регистре
			if (!Utils.isStringEmpty(this.firstName))
				result.append( String.format(" %s.", ИмяИнициал()) );
			if (!Utils.isStringEmpty(this.middleName))
				result.append( String.format("%s.", ОтчествоИнициал()) );
			return result.toString();
		}

		/**
		 * @return (none null) Фамилия или пустая строка 
		 */
		public String Фамилия() {
			return Utils.coalesce( this.lastName, "");
		}

		/**
		 * @return (none null) Первая буква фамилии в верхнем регистре или пусто 
		 */
		public String ФамилияИнициал() {
			return (Utils.isStringEmpty(this.lastName))
					? ""
					: this.lastName.toUpperCase().substring(0,1);
		}

		/**
		 * @return (none null) Имя или пустая строка 
		 */
		public String Имя() {
			return Utils.coalesce( this.firstName, "");
		}

		/**
		 * @return (none null) Первая буква имени в верхнем регистре или пусто 
		 */
		public String ИмяИнициал() {
			return (Utils.isStringEmpty(this.firstName))
					? ""
					: this.firstName.toUpperCase().substring(0,1);
		}

		/**
		 * @return (none null) Отчество или пустая строка 
		 */
		public String Отчество() {
			return Utils.coalesce(this.middleName, "");
		}

		/**
		 * @return (none null) Первая буква отчества в верхнем регистре или пусто 
		 */
		public String ОтчествоИнициал() {
			return (Utils.isStringEmpty(this.middleName))
					? ""
					: this.middleName.toUpperCase().substring(0,1);
		}
	}

	/**
	 * Контейнерный класс с инфой по группирующему объекту
	 * Тип группировки задаётся колонокой (@link{COL_GROUP_BY}) в наборе данных
	 */
	private class GroupByInfo {
		/** Название, Форматная строка и ассоциативная ссылка на поле группировки */
		String grpName, grpFmt, grpAssocQName;

		public GroupByInfo(String groupName) {
			this.grpName = groupName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			// result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((grpName == null) ? 0 : grpName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final GroupByInfo other = (GroupByInfo) obj;
			// if (!getOuterType().equals(other.getOuterType()))
			// 	return false;
			if (grpName == null) {
				if (other.grpName != null)
					return false;
			} else if (!grpName.equals(other.grpName))
				return false;
			return true;
		}

//		private ErrandsDisciplineDSProvider getOuterType() {
//			return ErrandsDisciplineDSProvider.this;
//		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("GroupByInfo ['");
			builder.append(grpName);
			builder.append("', assocQName {").append(grpAssocQName);
			builder.append("}, format '").append(grpFmt);
			builder.append("']");
			return builder.toString();
		}

	}

	/**
	 * Базовый класс для вычисления статистики
	 * @author rabdullin
	 */
	private class ExecDisciplineJRDataSource extends TypedJoinDS<EmployeeInfo> {

		/**
		 * Ключ здесь это название измерения (tag)
		 */
		// final protected Map<String, EmployeeInfo> found = new LinkedHashMap<String, EmployeeInfo>();

		public ExecDisciplineJRDataSource(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}


		@Override
		public int buildJoin() {
			// построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups)

			// Ключ здесь это название измерения (tag)
			final Map<NodeRef, EmployeeInfo> result = new HashMap<NodeRef, EmployeeInfo>();

			if (context.getRsIter() != null) {

				final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
				// final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

				/* Получение формата и ссылки для выбранного groupby-Измерения из конфигурации ... */
				final GroupByInfo groupByInfo = getCurrentGroupBy();
				qnames().setQN_ASSOC_REF( groupByInfo.grpAssocQName);


				/* проход по все загруженным Поручениям ... */
				while(context.getRsIter().hasNext()) {

					final ResultSetRow rs = context.getRsIter().next();

					final NodeRef errandId = rs.getNodeRef(); // id Поручения 

					// Исполнители
					final List<AssociationRef> employees = nodeSrv.getTargetAssocs(errandId, qnames().QN_ASSOC_REF);
					if (employees == null || employees.isEmpty() ) // (!?) с Поручением не связан никакой Сотрудник-Исполнитель ...
					{
						logger.warn( String.format( "No execution eployee found for errand item %s", errandId));
						continue;
					}

					final NodeRef executorId = employees.get(0).getTargetRef(); // id Сотрудника-Исполнителя 

					final EmployeeInfo executor;
					if (result.containsKey(executorId)) {
						executor = result.get(executorId);
					} else { // создание нового Сотрудника-Исполнителя
						executor = new EmployeeInfo(executorId);
						executor.loadProps( nodeSrv, null); // если надо будет - грузим данные по подразделениям, указав второй аргумент: getServices().getOrgstructureService()
						result.put(executorId, executor);
					}

					// прогружаем атрибуты объекта и для этого Исполнителя корректируем данные ...
					final Map<QName, Serializable> props = nodeSrv.getProperties(executorId);

					// среднее время исполнения ...
					executor.registerDuration( (Date) props.get(qnames().QNFLD_START_DATE), (Date) props.get(qnames().QNFLD_END_DATE));

					/* остальные счётчики ... */
					executor.counters.incCounter(COL_COUNT_TOTAL); // общее кол-во

					final boolean важное = qnames().isПоручениеВажное(props);
					final boolean вСрок = qnames().isПоручениеИсполненоВСрок(props);

					if (важное)
						executor.counters.incCounter(COL_COUNT_TOTAL_IMPORTANT);

					if (qnames().isПоручениеЗакрыто(props)) {
						executor.counters.incCounter(COL_COUNT_CLOSED);
						if (важное)
							executor.counters.incCounter(COL_COUNT_CLOSED_IMPORTANT);
						if (вСрок)
							executor.counters.incCounter(COL_COUNT_INTIME); // исполнено вСрок
					} else { // Поручение НЕ Закрыто
						if (важное && !вСрок) { 
							// важное И неисполненное в срок ...
							executor.counters.incCounter(COL_COUNT_IMPORTANT_REFUSED);
						}
					}
					if (qnames().isПоручениеБылоОтклонено(props)) {
						executor.counters.incCounter(COL_COUNT_BOSS_REFUSED);
					}
				} // while

				// (!) перенос в основной блок
				this.setData( new ArrayList<EmployeeInfo>(result.values()) );
			} // if

			if (this.getData() != null)
				this.setIterData(this.getData().iterator());

			logger.info( String.format( "found %s row(s)", result.size()));

			return result.size();
		}

		/**
		 * Прогрузить строку отчёта
		 */
		@Override
		protected Map<String, Serializable> getReportContextProps(EmployeeInfo item)
		{
			final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название ... */
			result.put( COL_NAMEATAG, item.ФамилияИО() );

			/* Счётчики ... */

			// Имена колонок совпадают с названиями счётчиков
			for(Map.Entry<String, Integer> entry: item.counters.getAttrCounters().entrySet()) {
				result.put( entry.getKey(), entry.getValue());
			}

			/* Среднее время исполнения */
			result.put( COL_AVG_EXECUTION, item.avgExecTime.getAvg());
			result.put( COL_AVG_EXECUTION_UNITS, "ч");

			/* (!) Проценты вычисляются непосредственно в jasper-отчёте */

			return result;
		}

	}

}
