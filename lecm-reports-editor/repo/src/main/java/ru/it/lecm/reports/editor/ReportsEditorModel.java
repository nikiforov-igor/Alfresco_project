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
    public final static QName TYPE_REPORT_DATA_SOURCE = QName.createQName(REPORTS_EDITOR_URI, "reportDataSource");
    public final static QName TYPE_REPORT_DATA_COLUMN = QName.createQName(REPORTS_EDITOR_URI, "reportDataColumn");
}
