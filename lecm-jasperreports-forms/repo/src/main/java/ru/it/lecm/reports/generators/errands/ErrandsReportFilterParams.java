package ru.it.lecm.reports.generators.errands;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.utils.Utils;

/**
 * Параметры НД для построения отчёта по поручениям (фильтр данных)
 */
public class ErrandsReportFilterParams {

	private static final Logger logger = LoggerFactory.getLogger(ErrandsReportFilterParams.class);

	/**
	 * XML Config-параметр с ссылками на assoc-атрибут в Поручении для выборки
	 * группирующих значений:
	 * Ключ = название группы, Значение = стд qname-ссылка вида "тип:атрибут"
	 * (см также XMLGROUPBY_FORMATS_MAP - и там и здесь должны быть одинаковые Ключи)
	 */
	final public static String XMLGROUPBY_SOURCE_MAP = "groupBy.source";

	/**
	 * XML Config-параметр с форматами (для substBean), применяемыми для 
	 * получения группирующих значений:
	 * Ключ = название группы, Значение = форматная строка для substituteBean
	 * будет применяться к объектам, полученным через ассоциацию из "groupBy.source".
	 * (см также XMLGROUPBY_SOURCE_MAP - и там и здесь должны быть одинаковые Ключи)
	 */
	final public static String XMLGROUPBY_FORMATS_MAP = "groupBy.formats";

	/**
	 * форматы и исходные поля для групп (ключ=название группы) 
	 */
	private LinkedHashMap<String, GroupByInfo> groupByMap; 

	/** Названия колонок с параметрами "GroupBy" и "Period" */
	final private String colnameGroupBy, colnamePeriod;

	public ErrandsReportFilterParams( String colnamePeriod, String colnameGroupBy) {
		super();
		this.colnamePeriod = colnamePeriod;
		this.colnameGroupBy = colnameGroupBy;
	}

	/** Название колонки с параметром "GroupBy" */
	public String getColnameGroupBy() {
		return colnameGroupBy;
	}

	/** Название колонки с параметром "Period" */
	public String getColnamePeriod() {
		return colnamePeriod;
	}

	private ColumnDescriptor getCheckedPeriodColumn(DataSourceDescriptor ds) {
		final ColumnDescriptor period = ds.findColumnByName(colnamePeriod);
		if (period == null || period.getParameterValue() == null) {
			logger.warn( String.format("Dataset not contains parameterized column '%s'", colnamePeriod));
		}
		return period;
	}

	/** Получить параметр-начало периода */
	public Date getParamPeriodEnd(DataSourceDescriptor ds) {
		final ColumnDescriptor period = getCheckedPeriodColumn(ds);
		return (period != null && period.getParameterValue() != null) 
						? (Date) period.getParameterValue().getBound2()
						: null;
	}

	/** Получить параметр-конец периода */
	public Date getParamPeriodStart(DataSourceDescriptor ds) {
		final ColumnDescriptor period = getCheckedPeriodColumn(ds);
		return (period != null && period.getParameterValue() != null) 
						? (Date) period.getParameterValue().getBound1()
						: null;
	}

	/**
	 * Контейнерный класс с инфой по группирующему объекту
	 * Тип группировки задаётся колонокой (@link{COL_GROUP_BY}) в наборе данных
	 */
	public class GroupByInfo {
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
	 * Получить характеристику groupBy согласно текущему состоянию НД 
	 * @param ds набор данных
	 * @param colnameGroupBy название колонки с groupBy-параметром
	 * @return
	 */
	public GroupByInfo getCurrentGroupBy(DataSourceDescriptor ds) {
		String groupBy = null; // not found

		{ // поиск способа группировки в колонке НД ...
			final ColumnDescriptor colGroupBy = ds.findColumnByName(colnameGroupBy);
			if (colGroupBy != null) {
				groupBy = colGroupBy.getExpression();
				if (groupBy != null)
					groupBy = groupBy.trim();
			}
		}
		if (Utils.isStringEmpty(groupBy)) {
			// не задан параметр группирования ... -> выбираем первый попавшийся

			if (groupByMap.isEmpty()) // вообще не сконфигурировано - отвалиться ...
				throw new RuntimeException( String.format("GroupBy is not set (check data column '%s' value)", colnameGroupBy) );

			groupBy = groupByMap.keySet().iterator().next(); // первый элемент
			logger.warn( String.format( "Column parameter '%s' is not present or has empty expression -> groupBy is set to '%s'"
					, colnameGroupBy, groupBy ));
		}

		final GroupByInfo result = groupByMap.get(groupBy);
		if (result == null) {
			final String msg = String.format( "Invalid groupBy criteria '%s':\n\tColumn '%s' contains expression that was not defined at config sections '%s'/'%s'\n\tUsable defines are: %s"
					, groupBy, colnameGroupBy, XMLGROUPBY_FORMATS_MAP, XMLGROUPBY_SOURCE_MAP, groupByMap );
			logger.error( msg);
			throw new RuntimeException( msg);
		}

		return result;
	}

	/**
	 * Загрузка параметром группирования из конфигурации
	 * @param config
	 */
	public void scanGroupByInfo(JRDSConfigXML config) {
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

}
