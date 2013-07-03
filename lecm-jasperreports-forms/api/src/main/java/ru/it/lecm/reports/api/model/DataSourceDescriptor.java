package ru.it.lecm.reports.api.model;

import java.util.List;

/**
 * Описатель набора данных.
 * Мета-описания колонок соедержат их названия, способ получения данных для 
 * колонки (простое поле или по ссылке), параметризацию колонки.
 * @author rabdullin
 *
 */
public interface DataSourceDescriptor extends Mnemonicable, L18able {

	/** Стандартные названия колонок и параметров */
	final static public String COLNAME_TYPE = "type";
	final static public String COLNAME_ID = "id";
	final static public String COLNAME_NODEREF = "nodeRef";

	/**
	 * Описания колонок данного НД.
	 * @return
	 */
	List<ColumnDescriptor> getColumns();

	/**
	 * Найти описание колонки по названию-мнемонике
	 * @param colName
	 * @return найденную колонку или null
	 */
	ColumnDescriptor findColumnByName(String colName);

	/**
	 * Найти описание колонки по названию-мнемонике её параметра
	 * @param colName
	 * @return найденную колонку или null
	 */
	ColumnDescriptor findColumnByParameter(String paramName);
}
