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

import java.util.Arrays;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 25.06.13
 * Time: 10:15
 */
public class ReportTemplatePolicy implements NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.OnCreateAssociationPolicy{

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
        // создаем ассоциацию на шаблон для отчета
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, new JavaBehaviour(this, "onCreateAssociation"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssociationRef) {
        NodeRef parent = childAssociationRef.getParentRef();
        QName parentType = nodeService.getType(parent);
        try {
            if (parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR) || parentType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)) {
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
                // подчистить директорию с отчетом
                String newFileName = (String) nodeService.getProperty(targetFile, ContentModel.PROP_NAME);
                List<ChildAssociationRef> childs = nodeService.getChildAssocs(report);
                for (ChildAssociationRef child : childs) {
                    QName childType = nodeService.getType(child.getChildRef());
                    if (childType.equals(ContentModel.TYPE_CONTENT) &&
                            newFileName.equals(nodeService.getProperty(child.getChildRef(), ContentModel.PROP_NAME))) {
                        nodeService.addAspect(child.getChildRef(), ContentModel.ASPECT_TEMPORARY, null);
                        nodeService.deleteNode(child.getChildRef());
                    }
                }
                // если родитель - не шаблон, переносим файл и удаляем из прежнего места
                NodeRef copiedFile = copyService.copyAndRename(targetFile, report, ContentModel.ASSOC_CONTAINS, null, false);
                if (copiedFile != null) {
                    nodeService.setAssociations(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, Arrays.asList(copiedFile));
                    nodeService.addAspect(targetFile, ContentModel.ASPECT_TEMPORARY, null);
                    nodeService.deleteNode(targetFile);
                }
            }
        }
    }
}