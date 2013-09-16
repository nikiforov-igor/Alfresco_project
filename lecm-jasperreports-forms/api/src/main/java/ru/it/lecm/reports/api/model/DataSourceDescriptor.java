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

	/**
	 * для отчётов, задаваемых типом объектов:
	 * "type" тип выбираемых объектов
	 */
	final static public String COLNAME_TYPE = "TYPE";
	
	/**
	 * для отчётов по одному объекту:
	 * "id" колонка-параметр отчёта, в которой предполагается задание id объекта альфреско (в виде noderef-текста)
	 * если его нет, тогда будет просмотрена параметр-колонка "nodeRef" 
	 */
	final static public String COLNAME_ID = "ID";
	// final static public String COLNAME_NODEREF = "nodeRef";

	/**
	 * "Что сгенерировать" = название колонки (типа строка) с целевым форматом файла
	 * Название используется везде в генерации отчётов, так что здесь описано вместе с основными именами ID/TYPE.  
	 */
	final static public String COLNAME_REPORT_TARGETFORMAT = "targetFormat";

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
