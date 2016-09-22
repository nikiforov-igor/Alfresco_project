package ru.it.lecm.reports.editor.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.editor.ReportsEditorModel;
import ru.it.lecm.reports.editor.ReportsEditorService;

import java.io.Serializable;
import java.util.*;

/**
 * User: DBashmakov
 * Date: 14.11.13
 * Time: 11:19
 */
public class ReportDescriptorPolicy implements NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.BeforeDeleteNodePolicy {
    final static protected Logger logger = LoggerFactory.getLogger(ReportDescriptorPolicy.class);

    protected PolicyComponent policyComponent;
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    private ReportsEditorService reportsEditorService;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setReportsEditorService(ReportsEditorService reportsEditorService) {
        this.reportsEditorService = reportsEditorService;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_DESCRIPTOR, new JavaBehaviour(this, "beforeDeleteNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssociationRef) {
        NodeRef mainReport = childAssociationRef.getParentRef();
        NodeRef subReport = childAssociationRef.getChildRef();

        // получаем набор данных основного отчета
        NodeRef mainDS;
        Set<QName> source = new HashSet<QName>();
        source.add(ReportsEditorModel.TYPE_REPORT_DATA_SOURCE);
        List<ChildAssociationRef> sourcesList = nodeService.getChildAssocs(mainReport, source);
        if (sourcesList.size() > 0) { // есть набор данных - получаем его
            mainDS = sourcesList.get(0).getChildRef();
        } else {
            mainDS = nodeService.createNode(mainReport, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                    ReportsEditorModel.TYPE_REPORT_DATA_SOURCE).getChildRef();
        }

        // в основной отчет добавляем в НД колонку с данными подотчета
        addColumnToDS(mainDS, subReport);
    }

    private NodeRef addColumnToDS(NodeRef mainDS, NodeRef subReport) {
        String subReportName = (String) nodeService.getProperty(subReport, ContentModel.PROP_NAME);
        String subReportCode = (String) nodeService.getProperty(subReport, ReportsEditorModel.PROP_REPORT_DESRIPTOR_CODE);

        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, subReportName);
        // код колонки должен совпадать с кодом подотчета
        properties.put(ReportsEditorModel.PROP_REPORT_DATA_COLUMN_CODE, subReportCode);
        // выражение заглушка по определенным правилам {{subreport::<код_подотчета>}}
        properties.put(ReportsEditorModel.PROP_REPORT_DATA_COLUMN_EXPRESSION, "{{subreport::" + subReportCode + "}}");

        NodeRef column = nodeService.getChildByName(mainDS, ContentModel.ASSOC_CONTAINS, subReportName);
        if (column == null) {
            column = nodeService.createNode(mainDS, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                    ReportsEditorModel.TYPE_REPORT_DATA_COLUMN, properties).getChildRef();

            NodeRef dataColumnStringType = null;
            List<NodeRef> types = reportsEditorService.getDataColumnTypeByClass(String.class.getName());
            if (types.size() > 0) {
                dataColumnStringType = types.get(0);
            }
            nodeService.createAssociation(column, dataColumnStringType, ReportsEditorModel.ASSOC_REPORT_DATA_COLUMN_COLUMN_TYPE);
        }
        return column;
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        NodeRef mainReport = nodeService.getPrimaryParent(nodeRef).getParentRef();
        Object beforeCode = before.get(ReportsEditorModel.PROP_REPORT_DESRIPTOR_CODE);
        Object afterCode = after.get(ReportsEditorModel.PROP_REPORT_DESRIPTOR_CODE);
        if (beforeCode != null && !afterCode.equals(beforeCode)) { // Код изменился - обработаем название колонки
            try {
                // получаем набор данных основного отчета
                NodeRef mainDS = null;
                Set<QName> source = new HashSet<QName>();
                source.add(ReportsEditorModel.TYPE_REPORT_DATA_SOURCE);
                List<ChildAssociationRef> sourcesList = nodeService.getChildAssocs(mainReport, source);
                if (sourcesList.size() > 0) { // есть набор данных - получаем его
                    mainDS = sourcesList.get(0).getChildRef();
                }
                if (mainDS != null) {
                    String columnName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
                    NodeRef subColumn = nodeService.getChildByName(mainDS, ContentModel.ASSOC_CONTAINS, columnName);
                    if (subColumn != null) {
                        nodeService.setProperty(subColumn, ReportsEditorModel.PROP_REPORT_DATA_COLUMN_CODE, afterCode.toString());
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void beforeDeleteNode(NodeRef report) {
        final ReportsManager reportsManager = reportsEditorService.getReportsManager();
        final String reportCode = (String) nodeService.getProperty(report, ReportsEditorModel.PROP_REPORT_DESRIPTOR_CODE);

        if (!reportCode.trim().isEmpty()) {
            //есть код -> реальный отчет, подчистим его "следы" в системе
            nodeService.addAspect(report,ContentModel.ASPECT_TEMPORARY, null);

            ReportDescriptor desc;
            if (reportsManager.getDescriptors().contains(reportCode)) {
                desc = reportsManager.getDescriptors().get(reportCode);
            } else {
                desc = reportsManager.getReportEditorDAO().getReportDescriptor(report);
            }
            //удалим задеплоенный отчет и файлы шаблон(-а)-ов (из Директории Сервси построения отчетов)
//            reportsManager.unregisterReportDescriptor(desc);
        }

        //для подотчетов - удалим из набора данных родителя, ссылку на него
        if (nodeService.getType(report).equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)) {
            NodeRef mainReport = nodeService.getPrimaryParent(report).getParentRef();
            // если мы не удаляем родителя - значит удаляется только подотчет
            if (nodeService.exists(mainReport) && !nodeService.hasAspect(mainReport, ContentModel.ASPECT_PENDING_DELETE)) {
                // получаем набор данных основного отчета
                NodeRef mainDS = null;
                Set<QName> source = new HashSet<>();
                source.add(ReportsEditorModel.TYPE_REPORT_DATA_SOURCE);
                List<ChildAssociationRef> sourcesList = nodeService.getChildAssocs(mainReport, source);
                if (sourcesList.size() > 0) { // есть набор данных - получаем его
                    mainDS = sourcesList.get(0).getChildRef();
                }
                if (mainDS != null) {
                    try {
                        String subReportName = (String) nodeService.getProperty(report, ContentModel.PROP_NAME);
                        NodeRef columnRef = nodeService.getChildByName(mainDS, ContentModel.ASSOC_CONTAINS, subReportName);
                        if (columnRef != null) {
                            nodeService.addAspect(columnRef, ContentModel.ASPECT_TEMPORARY, null);
                            nodeService.deleteNode(columnRef);
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }
    }
}
