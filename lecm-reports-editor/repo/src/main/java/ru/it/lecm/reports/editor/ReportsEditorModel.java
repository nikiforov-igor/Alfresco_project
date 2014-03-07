package ru.it.lecm.reports.editor;

import org.alfresco.service.namespace.QName;

/**
 * User: dbashmakov
 * Date: 18.06.13
 * Time: 17:39
 */
public class ReportsEditorModel {

    public final static String REPORTS_EDITOR_URI = "http://www.it.ru/logicECM/reports/editor/1.0";

    public final static QName TYPE_REPORT_TYPE = QName.createQName(REPORTS_EDITOR_URI, "reportType");
    public final static QName TYPE_REPORT_PROVIDER = QName.createQName(REPORTS_EDITOR_URI, "reportProvider");
    public final static QName TYPE_REPORT_COLUMN_TYPE = QName.createQName(REPORTS_EDITOR_URI, "reportColumnType");
    public final static QName TYPE_REPORT_PARAMETER_TYPE = QName.createQName(REPORTS_EDITOR_URI, "reportParameterType");
    public final static QName TYPE_REPORT_TEMPLATE= QName.createQName(REPORTS_EDITOR_URI, "reportTemplate");
    public final static QName TYPE_REPORT_DESCRIPTOR = QName.createQName(REPORTS_EDITOR_URI, "reportDescriptor");
    public final static QName TYPE_SUB_REPORT_DESCRIPTOR = QName.createQName(REPORTS_EDITOR_URI, "subReportDescriptor");
    public final static QName TYPE_REPORT_DATA_SOURCE = QName.createQName(REPORTS_EDITOR_URI, "reportDataSource");
    public final static QName TYPE_REPORT_DATA_COLUMN = QName.createQName(REPORTS_EDITOR_URI, "reportDataColumn");

    public final static QName ASSOC_REPORT_DESCRIPTOR_TEMPLATE = QName.createQName(REPORTS_EDITOR_URI, "reportTemplateAssoc");
    public final static QName ASSOC_REPORT_TEMPLATE_TYPE = QName.createQName(REPORTS_EDITOR_URI, "reportTemplateType");
    public final static QName ASSOC_REPORT_TEMPLATE_FILE = QName.createQName(REPORTS_EDITOR_URI, "reportTemplateFile");

    public final static QName PROP_REPORT_DESCRIPTOR_IS_DEPLOYED = QName.createQName(REPORTS_EDITOR_URI, "reportIsDeployed");
    public final static QName PROP_REPORT_DESRIPTOR_CODE = QName.createQName(REPORTS_EDITOR_URI, "reportCode");
    public final static QName PROP_REPORT_COLUMN_TYPE_CLASS = QName.createQName(REPORTS_EDITOR_URI, "reportColumnTypeClass");

    public final static QName PROP_REPORT_DATA_COLUMN_CODE = QName.createQName(REPORTS_EDITOR_URI, "dataColumnCode");
    public final static QName PROP_REPORT_DATA_COLUMN_EXPRESSION = QName.createQName(REPORTS_EDITOR_URI, "dataColumnExpression");
    public final static QName ASSOC_REPORT_DATA_COLUMN_COLUMN_TYPE = QName.createQName(REPORTS_EDITOR_URI, "columnTypeAssoc");

    public final static QName PROP_DATA_SOURCE_CODE = QName.createQName(REPORTS_EDITOR_URI, "dataSourceCode");

    public final static  QName PROP_RTEMPLATE_CODE = QName.createQName(REPORTS_EDITOR_URI, "templateCode");
}
