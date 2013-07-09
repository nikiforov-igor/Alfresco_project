package ru.it.lecm.reports.api.model.DAO;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportTemplate;

/**
 * Служба получения дексрипторов отчётов из Альфреско
 * 
 * @author rabdullin
 *
 */
public interface ReportDAO {

	final public String TYPE_ReportDescriptor = "lecm-rpeditor:reportDescriptor";

	/* "lecm-rpeditor:reportDescriptor" */
	/*
	 *   PROP_T_XXX   (text) текстовое поле
	 *   PROP_D_XXX   (date) поле с датой
	 *   PROP_B_XXX   (boolean) логическое числовое поле
	 *   PROP_I_XXX   (int) целое числовое поле
	 *   PROP_F_XXX   (float) вещественное числовое поле
	 */
	final static public String PROP_T_REPORT_CODE = "lecm-rpeditor:reportCode";

	/**
	 * тип документов для выборки
	 * в принципе, имеет вид сслыки на тип, например: "lecm-contract:document"
	 */
	final static public String PROP_T_REPORT_DOCTYPE = "lecm-rpeditor:reportDocType";

	final static public String PROP_B_REPORT_MULTIPLICITY = "lecm-rpeditor:reportObjectsMultiplicity";

	final static public String PROP_T_REPORT_QUERY = "lecm-rpeditor:reportQuery";
	final static public String PROP_I_REPORT_QUERY_OFFSET = "lecm-rpeditor:reportQueryOffset";
	final static public String PROP_I_REPORT_QUERY_LIMIT = "lecm-rpeditor:reportQueryLimit";

	final static public String PROP_I_REPORT_QUERY_PGSIZE = "lecm-rpeditor:reportQueryPgSize";

	/* @NOTE: (системное поле) для сортировки
	 * final static public String PROP_T_REPORT_TYPE_ASSOC_TEXT = "lecm-rpeditor:reportTypeAssoc-text-content";
	 */

	/* "lecm-rpeditor:reportType" */
	final static public String PROP_T_RTYPE_CODE = "lecm-rpeditor:reportTypeCode";

	/* <!-- Тип провайдера--> "lecm-rpeditor:reportProvider" */
	final static public String PROP_T_RPROVIDER_CODE = "lecm-rpeditor:reportProviderCode";
	final static public String PROP_T_RPROVIDER_CLASS = "lecm-rpeditor:reportProviderClass";

	/* <!-- Шаблон отчета --> "lecm-rpeditor:reportTemplate" */
	// @NOTE: (	системные поля) для сортировки/поиска в гридах, не исп для шаблонирования
	final static public String PROP_T_RTEMPLATE_TYPE_TEXT_CONTENT = "lecm-rpeditor:reportTemplateType-text-content";
	final static public String PROP_T_RTEMPLATE_TYPE_REF = "lecm-rpeditor:reportTemplateType-ref";
	final static public String PROP_T_RTEMPLATE_FILE_CONTENT = "lecm-rpeditor:reportTemplateFile-text-content";


	/* <!-- Набор данных -->, parent: "cm:folder" */
	final static public String TYPE_REPORT_DATASOURCE = "lecm-rpeditor:reportDataSource";
	final static public String PROP_T_RDS_CODE = "lecm-rpeditor:dataSourceCode";


	/* <!-- Колонка данных --> "lecm-rpeditor:reportDataColumn", parent: "cm:object" */
	final static public String TYPE_RDS_COLUMN = "lecm-rpeditor:reportDataColumn";
	final static public String PROP_T_RDS_COLUMN_CODE = "lecm-rpeditor:dataColumnCode";
	final static public String PROP_T_RDS_COLUMN_EXPR = "lecm-rpeditor:dataColumnExpression";
	final static public String PROP_T_RDS_COLUMN_CLASS = "lecm-rpeditor:dataColumnClass";

	/* <!-- Тип колонок в отчете--> "lecm-rpeditor:reportDataColumn", parent: "lecm-dic:plane_dictionary_values" */
	final static public String TYPE_RDS_COLTYPE = "lecm-rpeditor:reportColumnType";
	final static public String PROP_T_RDS_COLTYPE_CODE = "lecm-rpeditor:reportColumnTypeCode";
	final static public String PROP_T_RDS_COLTYPE_CLASS = "lecm-rpeditor:reportColumnTypeClass";

	/* <!-- Тип параметра--> "lecm-rpeditor:reportParameterType", parent: "lecm-dic:plane_dictionary_values" */
	final static public String TYPE_RDS_PARTYPE = "lecm-rpeditor:reportParameterType";
	final static public String PROP_T_RDS_PARTYPE_CODE = "lecm-rpeditor:reportParameterTypeCode";
	final static public String PROP_T_RDS_PARTYPE_LABEL1 = "lecm-rpeditor:reportParameterTypeLabel1";
	final static public String PROP_T_RDS_PARTYPE_LABEL2 = "lecm-rpeditor:reportParameterTypeLabel2";

	/**
	 * M1-Ссылка на target: "lecm-rpeditor:reportColumnType"
	 */
	final static public String ASSOC_RDS_COLUMN_TYPE = "lecm-rpeditor:columnTypeAssoc";

	/**
	 * M1-Ссылка на target: "lecm-rpeditor:reportParameterType"
	 */
	final static public String ASSOC_RDS_COLUMN_PARAMTYPE = "lecm-rpeditor:columnParameterTypeAssoc";

	/**
	 * M1-Ссылка на target: "cm:content"
	 */
	final static public String ASSOC_RTEMPLATE_FILE = "lecm-rpeditor:reportTemplateFile";

	/**
	 * M1-Ссылка на target: "lecm-rpeditor:reportType"
	 */
	final static public String ASSOC_RTEMPLATE_TYPE = "lecm-rpeditor:reportTemplateType";

	// final static public String PROP_X_REPORT_ = ;

	/**
	 * M1-Ссылка на target: "lecm-rpeditor:reportType"
	 */
	final static public String ASSOC_REPORT_TYPE = "lecm-rpeditor:reportTypeAssoc";
	final public String TYPE_ReportType = "lecm-rpeditor:reportType";

	/**
	 * M1-Ссылка на target: "lecm-rpeditor:reportProvider"
	 */
	final static public String ASSOC_REPORT_PROVIDER = "lecm-rpeditor:reportProviderAssoc";
	final public String TYPE_ReportProvider = "lecm-rpeditor:reportProvider";

	/**
	 * M1-Ссылка на target: "lecm-rpeditor:reportTemplate"
	 */
	final static public String ASSOC_REPORT_TEMLATE = "lecm-rpeditor:reportTemplateAssoc";
	final public String TYPE_ReportTemplate = "lecm-rpeditor:reportTemplate";

	// TODO: ASSOC_REPORT_DATASOURCE
	// final static public String ASSOC_REPORT_DATASOURCE = "lecm-rpeditor:reportXXX";
	// final public String TYPE_ReportDataSource = "lecm-rpeditor:reportXXX";

	// final static public String ASSOC_XXX = ;


	/**
	 * Получить "Описатеть отчёта" по id узла типа "lecm-rpeditor:reportDescriptor"
	 * @param id узел Альфреско (типа "lecm-rpeditor:reportDescriptor")
	 * @return описатель отчёта или null, если его нет
	 */
	ReportDescriptor getReportDescriptor(NodeRef id);

	/**
	 * Получить описатеть отчёта по названию
	 * @param mnemo мнемонический код отчёта (уникальный)
	 * @return описатель отчёта или null, если его нет
	 */
	ReportDescriptor getReportDescriptor(String mnemo);

	/*
	 * Получить "Шаблон отчета" (файл)
	 * @param id узел типа <type name="lecm-rpeditor:reportTemplate">
	 */
	ReportTemplate getReportTemplate(NodeRef id);

	/*
	 * Получить "Шаблон файла отчета" по названию
	 * @param rtMnemo  мнемоника объекта типа <type name="lecm-rpeditor:reportTemplate">
	 */
	ReportTemplate getReportTemplate(String rtMnemo);

	/*
	 * Набор данных
	 * "lecm-rpeditor:reportDataSource"
	 */

	/*
	 * Колонка данных
	 * <type name="lecm-rpeditor:reportDataColumn">
	 */

	/*
	 * Справочники: Тип отчета
	 * <type name="lecm-rpeditor:reportType">
	 */

	/*
	 * Справочники: Тип провайдера
	 * <type name="lecm-rpeditor:reportProvider">
	 */

	/*
	 * Справочники: Тип колонок в отчете
	 * <type name="lecm-rpeditor:reportColumnType">
	 */

	/*
	 * Справочники: Тип параметра
	 * <type name="lecm-rpeditor:reportParameterType">
	 */

	/*
	 */
}
