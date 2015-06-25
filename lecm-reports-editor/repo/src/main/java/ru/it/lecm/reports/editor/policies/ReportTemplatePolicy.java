package ru.it.lecm.reports.editor.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.editor.ReportsEditorModel;
import ru.it.lecm.reports.editor.ReportsEditorService;

import java.util.Arrays;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 25.06.13
 * Time: 10:15
 */
public class ReportTemplatePolicy implements NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteNodePolicy {

    protected PolicyComponent policyComponent;
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected DictionaryService dictionaryService;
    private CopyService copyService;
    private ReportsManager reportsManager;

    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setCopyService(CopyService copyService) {
        this.copyService = copyService;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, new JavaBehaviour(this, "onDeleteNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssociationRef) {
        NodeRef parent = childAssociationRef.getParentRef(); // директория "Шаблоны" или дескриптор Отчета
        QName parentType = nodeService.getType(parent);
        try {
            // Вариант 1 - нода шаблона создается внутри дескриптора отчета
            if (parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR)
                    || parentType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)) {
                // связываем с отчетом
                nodeService.createAssociation(parent, childAssociationRef.getChildRef(), ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_TEMPLATE);

                //генерим новый на основании источника данных, если шаблон не задан
                List<AssociationRef> targetAssocs =
                        nodeService.getTargetAssocs(childAssociationRef.getChildRef(), ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
                NodeRef templateFile;
                if (targetAssocs == null || targetAssocs.isEmpty()) {
                    templateFile = reportsManager.produceDefaultTemplate(parent, childAssociationRef.getChildRef());
                    nodeService.setAssociations(childAssociationRef.getChildRef(),
                            ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, Arrays.asList(templateFile));
                }
            } else {// Вариант 2 - нода шаблона создается внутри справочника Шаблонов (путем копирования или реального создания)
                NodeRef template = childAssociationRef.getChildRef();
                List<AssociationRef> targetAssocs =
                        nodeService.getTargetAssocs(childAssociationRef.getChildRef(), ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
                NodeRef templateFile;
                if (targetAssocs != null && !targetAssocs.isEmpty()) {
                    templateFile = targetAssocs.get(0).getTargetRef();

                    if (nodeService.exists(templateFile)) {
                        NodeRef parentReport = nodeService.getPrimaryParent(templateFile).getParentRef();
                        QName parentReportType = nodeService.getType(parentReport);
                        // если мы скопировали шаблон - нужно скопировать и файл шаблона
                        if (parentReportType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR) ||
                                parentReportType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)) {
                            NodeRef copiedFile = copyService.copyAndRename(templateFile, parent, ContentModel.ASSOC_CONTAINS, null, false);
                            if (copiedFile != null) {
                                nodeService.setAssociations(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, Arrays.asList(copiedFile));
                            }
                        }
                    }
                }
            }
        } catch (AssociationExistsException ignored) {
        }
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef targetFile = nodeAssocRef.getTargetRef();
        NodeRef template = nodeAssocRef.getSourceRef();
        NodeRef report = nodeService.getPrimaryParent(nodeAssocRef.getSourceRef()).getParentRef();

        if (nodeService.exists(targetFile)) {
            NodeRef parent = nodeService.getPrimaryParent(targetFile).getParentRef();
            QName parentType = nodeService.getType(parent);
            if (!parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR) && !parentType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)) {
                // 2 случая: загружаем новый и берем из temp или берем из готовых шаблонов
                QName templateParentType = nodeService.getType(report);
                if (templateParentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR) ||
                        templateParentType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)) {
                    // подчистить директорию с отчетом
                    List<ChildAssociationRef> childs = nodeService.getChildAssocs(report);
                    for (ChildAssociationRef child : childs) {
                        QName childType = nodeService.getType(child.getChildRef());
                    if (childType.equals(ContentModel.TYPE_CONTENT)) {
                        if (nodeService.getSourceAssocs(child.getChildRef(), ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE).isEmpty()) {
                            nodeService.addAspect(child.getChildRef(), ContentModel.ASPECT_TEMPORARY, null);
                            nodeService.deleteNode(child.getChildRef());
                        }
                    }
                }
                // так как родитель - не шаблон, переносим файл и удаляем. если файл был взят не из шаблонов!
                    NodeRef copiedFile = copyService.copyAndRename(targetFile, report, ContentModel.ASSOC_CONTAINS, null, false);
                    if (copiedFile != null) {
                        List<AssociationRef> oldFiles = nodeService.getTargetAssocs(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
                        for (AssociationRef oldFile : oldFiles) { // oldTemplate и targetFile
                            NodeRef oldFileRef = oldFile.getTargetRef();
                            if (nodeService.exists(oldFileRef)
                                    && !nodeService.getProperty(parent, ContentModel.PROP_NAME).equals(ReportsEditorService.RE_TEMPLATES_ROOT_NAME)
                                    && !nodeService.hasAspect(oldFileRef, ContentModel.ASPECT_PENDING_DELETE)) {
                                nodeService.addAspect(oldFileRef, ContentModel.ASPECT_TEMPORARY, null);
                                nodeService.deleteNode(oldFileRef);
                            }
                        }
                        nodeService.setAssociations(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, Arrays.asList(copiedFile));
                    }
                }
            }
        }
    }

    @Override
    public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
        NodeRef report = childAssocRef.getParentRef();

        if (!nodeService.hasAspect(report, ContentModel.ASPECT_PENDING_DELETE)) {
            QName parentType = nodeService.getType(report);
            if (parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR) ||
                    parentType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)){
                List<ChildAssociationRef> childs = nodeService.getChildAssocs(report);
                for (ChildAssociationRef child : childs) {
                    QName childType = nodeService.getType(child.getChildRef());
                    if (childType.equals(ContentModel.TYPE_CONTENT)) {
                        List<AssociationRef> templateAssocs =
                                nodeService.getSourceAssocs(child.getChildRef(), ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
                        if (templateAssocs.isEmpty()) {
                            nodeService.addAspect(child.getChildRef(), ContentModel.ASPECT_TEMPORARY, null);
                            nodeService.deleteNode(child.getChildRef());
                        }
                    }
                }
            }
        }
    }
}