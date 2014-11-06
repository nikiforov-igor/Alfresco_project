package ru.it.lecm.reports.api.model.DAO;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.ReportTemplate;

/**
 * Служба получения дексрипторов отчётов lecm-редактора отчётов из Альфреско
 *
 * @author rabdullin
 */
public interface ReportEditorDAO {
    public final static String REPORTS_EDITOR_URI = "http://www.it.ru/logicECM/reports/editor/1.0";

    final public QName TYPE_REPORT_DESCRIPTOR = QName.createQName(REPORTS_EDITOR_URI, "reportDescriptor");
    final public QName TYPE_SUB_REPORT_DESCRIPTOR = QName.createQName(REPORTS_EDITOR_URI, "subReportDescriptor");

    final static public QName PROP_REPORT_CODE = QName.createQName(REPORTS_EDITOR_URI, "reportCode");
    final static public QName PROP_REPORT_DOCTYPE = QName.createQName(REPORTS_EDITOR_URI, "reportDocType");
    final static public QName PROP_REPORT_MULTIPLICITY = QName.createQName(REPORTS_EDITOR_URI, "reportObjectsMultiplicity");
    final static public QName PROP_REPORT_RUN_AS_SYSTEM = QName.createQName(REPORTS_EDITOR_URI, "runAsSystem");
    final static public QName PROP_REPORT_INCLUDE_ALL_ORGS = QName.createQName(REPORTS_EDITOR_URI, "includeAllOrganizations");

    final static public QName PROP_REPORT_QUERY = QName.createQName(REPORTS_EDITOR_URI, "reportQuery");
    final static public QName PROP_REPORT_QUERY_SORT = QName.createQName(REPORTS_EDITOR_URI, "reportQuerySort");
    final static public QName PROP_REPORT_QUERY_OFFSET = QName.createQName(REPORTS_EDITOR_URI, "reportQueryOffset");
    final static public QName PROP_REPORT_QUERY_LIMIT = QName.createQName(REPORTS_EDITOR_URI, "reportQueryLimit");
    final static public QName PROP_REPORT_QUERY_PGSIZE = QName.createQName(REPORTS_EDITOR_URI, "reportQueryPgSize");

    final static public QName PROP_RPROVIDER_CODE = QName.createQName(REPORTS_EDITOR_URI, "reportProviderCode");
    final static public QName PROP_RPROVIDER_CLASS = QName.createQName(REPORTS_EDITOR_URI, "reportProviderClass");

    final static public QName TYPE_REPORT_DATASOURCE = QName.createQName(REPORTS_EDITOR_URI, "reportDataSource");
    final static public QName PROP_REPORT_DATASOURSE_CODE = QName.createQName(REPORTS_EDITOR_URI, "dataSourceCode");

    final static public QName TYPE_RDS_COLUMN = QName.createQName(REPORTS_EDITOR_URI, "reportDataColumn");
    final static public QName PROP_RDS_COLUMN_CODE = QName.createQName(REPORTS_EDITOR_URI, "dataColumnCode");
    final static public QName PROP_RDS_COLUMN_EXPR = QName.createQName(REPORTS_EDITOR_URI, "dataColumnExpression");
    final static public QName PROP_RDS_COLUMN_CLASS = QName.createQName(REPORTS_EDITOR_URI, "dataColumnClass");
    final static public QName PROP_RDS_COLUMN_ORDER = QName.createQName(REPORTS_EDITOR_URI, "dataColumnOrder");
    final static public QName PROP_RDS_COLUMN_MANDATORY = QName.createQName(REPORTS_EDITOR_URI, "dataColumnMandatory");
    final static public QName PROP_RDS_COLUMN_CONTROL_PARAMS = QName.createQName(REPORTS_EDITOR_URI, "dataColumnControlParams");

    final static public QName PROP_RDS_COLTYPE_CODE = QName.createQName(REPORTS_EDITOR_URI, "reportColumnTypeCode");
    final static public QName PROP_RDS_COLTYPE_CLASS = QName.createQName(REPORTS_EDITOR_URI, "reportColumnTypeClass");

    final static public QName PROP_T_RDS_PARTYPE_CODE = QName.createQName(REPORTS_EDITOR_URI, "reportParameterTypeCode");
    final static public QName PROP_T_RDS_PARTYPE_LABEL1 = QName.createQName(REPORTS_EDITOR_URI, "reportParameterTypeLabel1");
    final static public QName PROP_T_RDS_PARTYPE_LABEL2 = QName.createQName(REPORTS_EDITOR_URI, "reportParameterTypeLabel2");

    final static public QName ASSOC_RDS_COLUMN_TYPE = QName.createQName(REPORTS_EDITOR_URI, "columnTypeAssoc");
    final static public QName ASSOC_RDS_COLUMN_PARAMTYPE = QName.createQName(REPORTS_EDITOR_URI, "columnParameterTypeAssoc");

    final static public QName ASSOC_RTEMPLATE_TYPE = QName.createQName(REPORTS_EDITOR_URI, "reportTemplateType");
    final static public QName PROP_RTEMPLATE_CODE = QName.createQName(REPORTS_EDITOR_URI, "templateCode");
    final static public QName ASSOC_RTEMPLATE_FILE = QName.createQName(REPORTS_EDITOR_URI, "reportTemplateFile");

    final static public QName ASSOC_REPORT_PROVIDER = QName.createQName(REPORTS_EDITOR_URI, "reportProviderAssoc");

    final static public QName ASSOC_REPORT_TEMLATE = QName.createQName(REPORTS_EDITOR_URI, "reportTemplateAssoc");
    final static public QName ASSOC_REPORT_ROLES = QName.createQName(REPORTS_EDITOR_URI, "businessRolesAssoc");
    final static public QName ASSOC_PARENT_TEMPLATE_ASSOC = QName.createQName(REPORTS_EDITOR_URI, "parentReportTemplateAssoc");

    final static public QName PROP_T_REPORT_FLAGS = QName.createQName(REPORTS_EDITOR_URI, "reportExtFlags");

    final static public QName PROP_RTYPE_CODE = QName.createQName(REPORTS_EDITOR_URI, "reportTypeCode");

    public final static QName PROP_REPORT_DESCRIPTOR_IS_DEPLOYED = QName.createQName(REPORTS_EDITOR_URI, "reportIsDeployed");

    /**
     * Получить "Описатеть отчёта" по id узла типа "lecm-rpeditor:reportDescriptor"
     *
     * @param id узел Альфреско (типа "lecm-rpeditor:reportDescriptor")
     * @return описатель отчёта или null, если его нет
     */
    ReportDescriptor getReportDescriptor(NodeRef id);

    /**
     * Получить "Описатеть отчёта" по id узла типа "lecm-rpeditor:reportDescriptor"
     *
     * @param id узел Альфреско (типа "lecm-rpeditor:reportDescriptor")
     * @return описатель отчёта или null, если его нет
     */
    ReportDescriptor getReportDescriptor(NodeRef id, boolean withoutSubs);

    NodeRef getReportDescriptorNodeByCode(String reportCode);

    /*
     * Получить "Шаблон отчета" (файл)
     * @param id узел типа <type name="lecm-rpeditor:reportTemplate">
     */
    ReportTemplate getReportTemplate(NodeRef id);

    ColumnDescriptor createColumnDescriptor(NodeRef node);

    void markAsDeployed(NodeRef reportId);
}
