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
import ru.it.lecm.reports.editor.ReportsEditorModel;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 25.06.13
 * Time: 10:15
 */
public class ReportTemplatePolicy implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeCreateNodePolicy {

    protected PolicyComponent policyComponent;
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected DictionaryService dictionaryService;
    private CopyService copyService;

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
        // удаляем предыдущие записи
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeCreateNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, new JavaBehaviour(this, "beforeCreateNode"));
        // создаем ассоциацию на шаблон для отчета
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssociationRef) {
        NodeRef parent = childAssociationRef.getParentRef();
        QName parentType = nodeService.getType(parent);
        try {
            if (parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR)) {
                nodeService.createAssociation(parent, childAssociationRef.getChildRef(), ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_TEMPLATE);

                //копируем файл с шаблоном из общей директории в отчет
                copyTemplateFile(childAssociationRef.getChildRef(), parent);

                // берем тип отчета из шаблона
                NodeRef reportType;
                List<AssociationRef> typeAssoc = nodeService.getTargetAssocs(childAssociationRef.getChildRef(), ReportsEditorModel.ASSOC_REPORT_TEMPLATE_TYPE);
                if (typeAssoc != null && !typeAssoc.isEmpty()) {
                    reportType = typeAssoc.get(0).getTargetRef();
                    List<NodeRef> types = new ArrayList<NodeRef>();
                    types.add(reportType);
                    //сохраняем в отчете
                    nodeService.setAssociations(parent, ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_REPORT_TYPE, types);
                }
            }
        } catch (AssociationExistsException ignored) {
        }
    }

    @Override
    public void beforeCreateNode(NodeRef nodeRef, QName assocTypeQName, QName assocQName, QName nodeTypeQName) {
        QName parentType = nodeService.getType(nodeRef);
        if (parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR)) {
            List<AssociationRef> templates = nodeService.getTargetAssocs(nodeRef, ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_TEMPLATE);
            if (templates != null && !templates.isEmpty()) {
                for (AssociationRef template : templates) {
                    NodeRef oldTemplate = template.getTargetRef();
                    nodeService.removeAssociation(nodeRef, oldTemplate, ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_TEMPLATE);

                    NodeRef templateFile = nodeService.getTargetAssocs(oldTemplate, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE).get(0).getTargetRef();

                    nodeService.addAspect(oldTemplate, ContentModel.ASPECT_TEMPORARY, null);
                    nodeService.deleteNode(oldTemplate);

                    List<AssociationRef> assocs = nodeService.getSourceAssocs(templateFile, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
                    if (assocs != null && assocs.size() == 0) { // на шаблон более не ссылаются - удаляем его
                        nodeService.addAspect(templateFile, ContentModel.ASPECT_TEMPORARY, null);
                        nodeService.deleteNode(templateFile);
                    }
                }
            }
        }
    }

    private void copyTemplateFile(NodeRef template, NodeRef report) {
        NodeRef templateFile = nodeService.getTargetAssocs(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE).get(0).getTargetRef();
        NodeRef newTemplateFile = copyService.copyAndRename(templateFile, report, ContentModel.ASSOC_CONTAINS, null, false);
        if (newTemplateFile != null) {
            nodeService.setAssociations(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, Arrays.asList(newTemplateFile));
        }
    }
}