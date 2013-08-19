package ru.it.lecm.reports.generators.errands;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.calc.AvgValue;
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
 * Продуктивность по исполнителям
 * Фильтры отчета:
 * 	•	За Период
 * 	•	Исполнители
 * 		o	Сотрудники (шаблон отчёта первый)
 * 		o	Подразделения (шаблон отчёта второй/другой)
 * Выводимые показатели:
 * 	•	Среднее время исполнения поручения
 * 		(время нахождения поручения между статусами "В работе" до любого финального статуса)
 * 		в зависимости от даты
 * @author rabdullin
 */
public class ErrandsProductionsGraphDSProvider 
		extends GenericDSProviderBase
{

	private static final Logger logger = LoggerFactory.getLogger(ErrandsProductionsGraphDSProvider.class);


	/**
	 * Способ группировки элементов определяется параметрами отчёта
	 * Конфигурируется:
	 *    1) формат ссылки
	 *    2) атрибут-источник для группирования
	 */
	final private ErrandsReportFilterParams paramsFilter = new ErrandsReportFilterParams(
			DsProductionsColumnNames.COL_PARAM_PERIOD , DsProductionsColumnNames.COL_PARAM_GROUP_BY);

	private Date periodStart, periodEnd;

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
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		final ExecProductionsJRDataSource result = new ExecProductionsJRDataSource(iterator);
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
		 * Критерий двойной:
		 * 		Время Начала 
		 * 		или Время Окончания заданы внутри указанного интервала
		 * Формируется в виде
		 * 		"AND ( start_inside OR end_inside )"
		 */
		final DataSourceDescriptor ds = getReportDescriptor().getDsDescriptor();

		applyPeriodParams(ds);

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
	 * Вычисление не пустых начала и конца в this.periodStart/periodEnd
	 * @param ds надо данных
	 */
	private void applyPeriodParams(final DataSourceDescriptor ds) {
		this.periodStart = paramsFilter.getParamPeriodStart(ds);
		this.periodEnd = paramsFilter.getParamPeriodEnd(ds);
		if (periodEnd == null) // берём текущий момент ...
			periodEnd = new Date();
		if (periodStart == null) // одно-недельный период
			periodStart = new Date( periodEnd.getTime() - 7 * Utils.MILLIS_PER_DAY);
	}

	/**
	 * Названия колонок в наборе данных для отчёта.
	 */
	final private class DsProductionsColumnNames {

		/** Колонка "Группировка" - по часам, дням и  т.д. */
		final static String COL_PARAM_GROUP_BY = "Col_GroupBy"; // String значение долно быть в секцииях конфы "groupBy.xxx"

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

		/** Колонка "даты" в строке данных*/
		final static String COL_DATETIME = "Col_DateTime"; // Date/Timestamp

		/**
		 * Колонка "Показатель: среднее время исполнения поручения"
		 * (его единицы измерения - см колонку COL_AVG_EXECUTION_UNITS) 
		 */
		final static String COL_AVG_EXECUTION = "Col_Avg_Execution.Value"; // java.lang.Float 

		/** Колонка "Показатель: название единицы измерения среднего времени исполнения" */
		final static String COL_AVG_EXECUTION_UNITS = "Col_Avg_Execution.Units"; // String 
	}


	/** QName-ссылки на данные Альфреско **************************************/
	private class LocalQNamesHelper extends ErrandsQNamesHelper
	{
		/**
		 * Параметр отчёта в НД: Исполнитель или Подразделение, по которому 
		 * фактически будет выполняться группировка ... 
		 */
		QName QN_ASSOC_REF;

		LocalQNamesHelper(NamespaceService ns) {
			super(ns);
			this.QN_ASSOC_REF = QName.createQName(ASSOC_EXECUTOR, this.ns); // by default = по Исполнителю
		}

		public void setQN_ASSOC_REF(String assocQName) {
			this.QN_ASSOC_REF = super.makeQN(assocQName);
		}
	}

	/**
	 * Структура для хранения данных о статистике по Сотруднику:
	 * Средние значения накапливаются в обычном индексированном списке.
	 */
	private class ProductEmployeeInfo extends BasicEmployeeInfo {

		/** 
		 * Список из элементов типа "Среднее время исполнения, часов"
		 * (индексы: по дням, неделям, месяцам и т.п.)
		 */
		final List<AvgValue> avgExecTimeInHours; // new AvgValue("Avg exec time, h");

		/**
		 * Зарегистрировать 
		 * @param employeeId
		 * @param maxListSize кол-во элементов в списке накопления
		 */
		public ProductEmployeeInfo(NodeRef employeeId, int maxListSize) {
			super(employeeId);
			this.avgExecTimeInHours = new ArrayList<AvgValue>(maxListSize);
			for (int i = 0; i < maxListSize; i++) {
				this.avgExecTimeInHours.add( new AvgValue( String.format("Avg exec time [%s], h", i)) );
			}
		}

		final static long MILLIS_PER_HOUR = 1000 * 60 * 60;

		public void registerDuration(int index, long duration_ms) {
			if (duration_ms <= 0) // нельзя определить
				return; 
			final float fact = (float) duration_ms / MILLIS_PER_HOUR;  
			this.avgExecTimeInHours.get(index).adjust(fact);
		}

		/**
		 * Зарегистрировать длительность исполнения (работы) 
		 * @param start время начала
		 * @param end время конца
		 */
		public void registerDuration(int index, Date start, Date end) {
			if (start == null || end == null) // нельзя определить
				return; 
			registerDuration( index, end.getTime() - start.getTime());
		}
	}

	/**
	 * Точка на графике.
	 */
	private class GraphPoint {
		private String Col_NameTag;
		private java.sql.Timestamp Col_DateTime;

		/**
		 * Показатель: среднее время исполнения поручения
		 * (его единицы измерения - см колонку COL_AVG_EXECUTION_UNITS) 
		 */
		private Float Col_Avg_Execution_Value; 

		/** Колонка "Показатель: название единицы измерения среднего времени исполнения" */
		private String Col_Avg_Execution_Units;


		private GraphPoint(String col_NameTag, Timestamp col_DateTime,
				Float col_Avg_Execution_Value, String col_Avg_Execution_Units) {
			super();
			Col_NameTag = col_NameTag;
			Col_DateTime = col_DateTime;
			Col_Avg_Execution_Value = col_Avg_Execution_Value;
			Col_Avg_Execution_Units = col_Avg_Execution_Units;
		}

		public String getCol_NameTag() {
			return Col_NameTag;
		}

		public void setCol_NameTag(String col_NameTag) {
			Col_NameTag = col_NameTag;
		}

		public java.sql.Timestamp getCol_DateTime() {
			return Col_DateTime;
		}

		public void setCol_DateTime(java.sql.Timestamp col_DateTime) {
			Col_DateTime = col_DateTime;
		}

		public Float getCol_Avg_Execution_Value() {
			return Col_Avg_Execution_Value;
		}

		public void setCol_Avg_Execution_Value(Float col_Avg_Execution_Value) {
			Col_Avg_Execution_Value = col_Avg_Execution_Value;
		}

		public String getCol_Avg_Execution_Units() {
			return Col_Avg_Execution_Units;
		}

		public void setCol_Avg_Execution_Units(String col_Avg_Execution_Units) {
			Col_Avg_Execution_Units = col_Avg_Execution_Units;
		}

	} 

	
	/**
	 * Jasper-НД для вычисления статистики
	 */
	private class ExecProductionsJRDataSource extends TypedJoinDS<GraphPoint> {

		public ExecProductionsJRDataSource(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}


		/**
		 * Прогрузить строку отчёта
		 * Time Series Dataset:
		 * seriesExpression: java.lang.Comparable = название Сотрудника/Подразделения
		 * timePeriodExpression: java.util.Date = время
		 * valueExpression: java.lang.Number = значение
		 * labelExpression: String = (необ) метка точки, если не указано - используется метка по-умолчанию
		 * itemHyperlink: sets hyperlinks associated with chart items.
		 */
		@Override
		protected Map<String, Serializable> getReportContextProps(GraphPoint item)
		{
			final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();

			/* Название ... */
			result.put( DsProductionsColumnNames.COL_NAMEATAG, item.getCol_NameTag());

			/* Время ... */
			result.put( DsProductionsColumnNames.COL_DATETIME, item.getCol_DateTime());

			/* Значение ... */
			result.put( DsProductionsColumnNames.COL_AVG_EXECUTION, item.getCol_Avg_Execution_Value());

			/* Ед измерения ... */
			result.put( DsProductionsColumnNames.COL_AVG_EXECUTION_UNITS, item.getCol_Avg_Execution_Units());

			return result;
		}

		/**
		 * Найти полное кол-во суток между указанными датами
		 * @param dstart
		 * @param dnd
		 * @return разницу в днях ("больше или равно")
		 */
		private int countDeltaInDays(Date dstart, Date dend) {
			if (dstart == null || dend == null) // одной из дат нет ...
				return 0;

			// (!) первую дату выравниваем на начало дня, 
			// вторую - не трогаем, т.к. будем ровнять delta_h сверху на 24ч
			final Calendar cstart = Calendar.getInstance();
			cstart.setTime(dstart);
			cstart.set(Calendar.HOUR, 0);
			cstart.set(Calendar.MINUTE, 0);
			cstart.set(Calendar.SECOND, 0);
			cstart.set(Calendar.MILLISECOND, 0);

			// вычисление разницы в часах ...
			final float delta_h = Utils.getDurationInHours(dend.getTime() - cstart.getTimeInMillis());
			if (delta_h == 0)
				return 0;

			return (int) Math.ceil(delta_h/24); // "с округлением вверх" на 24ч границу
		}

		@Override
		/** построить  в groups список объектов сгруппированных по названиям Измерения (ключ в groups) */
		public int buildJoin() {
			// final DataSourceDescriptor ds = getReportDescriptor().getDsDescriptor();

			final List<GraphPoint> result = new ArrayList<GraphPoint>();

			final int maxTimeCounter = 1+ countDeltaInDays(periodStart, periodEnd); // кол-во отметок времени

			// TODO: RANDOM DATA FILL FOR TEST
			/*
			// (!) перенос в основной блок с разбивкой по датам ...
			{
				final Calendar curDay = Calendar.getInstance();
				curDay.setTime(periodStart);
				final Random r = new Random(); 
				for (int i = 0; i < maxTimeCounter; i++) { // цикл по дням
					final Timestamp curStamp = new Timestamp(curDay.getTimeInMillis());

					for (String outerName: new String[] {"Иванов", "Петров", "Сидоров"} ) { // цикл по объектам
						final float avg = r.nextFloat()* 2f + 20f;
						result.add( new GraphPoint( outerName, curStamp, avg, "h"));
					}
					curDay.add(Calendar.HOUR, 24); // сутки добавляем
				}

				this.setData( result );
			}
			 */

			// проход по данным ...
			if (context.getRsIter() != null && result.isEmpty()) {

				// series: собранные данные по объекта (Сотрудникам или Подразделениям)
				// Ключ здесь это название измерения (tag)
				final Map<NodeRef, ProductEmployeeInfo> series = new HashMap<NodeRef, ProductEmployeeInfo>();

				final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();

				/* Получение формата и ссылки для выбранного groupby-Измерения из конфигурации ... */
				final GroupByInfo groupByInfo = paramsFilter.getCurrentGroupBy( getReportDescriptor().getDsDescriptor());
				qnames().setQN_ASSOC_REF( groupByInfo.grpAssocQName); // задать название ассоциации для получения Инициаторов или Подразделений

				/* проход по все загруженным Поручениям ... */
				while(context.getRsIter().hasNext()) {

					final ResultSetRow rs = context.getRsIter().next();

					final NodeRef errandId = rs.getNodeRef(); // id Поручения 
					if (context.getFilter() != null && !context.getFilter().isOk(errandId)) {
						if (logger.isDebugEnabled())
							logger.debug( String.format("{%s} filtered out", errandId));
						continue;
					}

					// Исполнители
					final List<AssociationRef> employees = nodeSrv.getTargetAssocs(errandId, qnames().QN_ASSOC_REF);
					if (employees == null || employees.isEmpty() ) // (!?) с Поручением не связан никакой Сотрудник-Исполнитель ...
					{
						logger.warn( String.format( "No execution eployee found for errand item %s", errandId));
						continue;
					}

					final NodeRef executorId = employees.get(0).getTargetRef(); // id Сотрудника-Исполнителя 

					final ProductEmployeeInfo executor;
					if (series.containsKey(executorId)) {
						executor = series.get(executorId); // уже такой был ...
					} else { // создание нового Сотрудника-Исполнителя
						executor = new ProductEmployeeInfo(executorId, maxTimeCounter);
						executor.loadProps( nodeSrv, null); // если надо будет - грузим данные по подразделениям, указав второй аргумент: getServices().getOrgstructureService()
						series.put(executorId, executor);
					}

					// прогружаем атрибуты Поручения и корректируем данные ...
					final Map<QName, Serializable> props = nodeSrv.getProperties(errandId);

					if (!qnames().isПоручениеЗакрыто(props)) // поручение ещё в работе ...
						continue;

					// точка X: "дата завершения" отчёта ...
					final Date endErrand = (Date) props.get(qnames().QNFLD_END_DATE);
					if (endErrand == null) // пропускаем не завершённые поручения
						continue;

					// точка Y: "среднее время исполнения на (!) дату закрытия" ...
					final int index = countDeltaInDays(periodStart, endErrand);
					executor.registerDuration( index, qnames().getВремяИсполнения_мсек(props));

				} // while по НД

				// (!) перенос в основной блок с разбивкой по датам ...
				final Calendar x_curDay = Calendar.getInstance();
				x_curDay.setTime(periodStart);
				for (int i = 0; i < maxTimeCounter; i++) { // цикл по дням
					final Timestamp x_curStamp = new Timestamp(x_curDay.getTimeInMillis());
					for (Map.Entry<NodeRef, ProductEmployeeInfo> e: series.entrySet()) { // цикл по объектам
						final AvgValue avg = e.getValue().avgExecTimeInHours.get(i);
						// вместо отсутствующих значениё выводим ноль - чтобы 
						// график не "схлопывался до точки" ...
						final float y_value = (avg != null && avg.getCount() > 0) 
								? avg.getAvg()
								: 0;
						result.add( new GraphPoint(
								  e.getValue().ФамилияИО()
								, x_curStamp
								, y_value
								, "h"));
					}
					x_curDay.add(Calendar.HOUR, 24); // сутки добавляем
				}

				this.setData( result );
			} // if

			if (this.getData() != null)
				this.setIterData(this.getData().iterator());

			logger.info( String.format( "found %s data items", result.size()));

			return result.size();
		}


	}
}
