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
public class ReportTemplatePolicy implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy{

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
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TEMPLATE, new JavaBehaviour(this, "beforeDeleteNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssociationRef) {
        NodeRef parent = childAssociationRef.getParentRef();
        QName parentType = nodeService.getType(parent);
        try {
            if (parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR) || parentType.equals(ReportsEditorModel.TYPE_SUB_REPORT_DESCRIPTOR)) {
                nodeService.createAssociation(parent, childAssociationRef.getChildRef(), ReportsEditorModel.ASSOC_REPORT_DESCRIPTOR_TEMPLATE);
                //копируем файл с шаблоном из общей директории в отчет (или генерим новый на основании источника данных)
                copyTemplateFile(childAssociationRef.getChildRef(), parent);
            }
        } catch (AssociationExistsException ignored) {
        }
    }

    private void copyTemplateFile(NodeRef template, NodeRef report) {
        List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
        NodeRef templateFile;
        if (targetAssocs != null && !targetAssocs.isEmpty()) {
            templateFile = targetAssocs.get(0).getTargetRef();  //файл шаблона задан - используем
            templateFile = copyService.copyAndRename(templateFile, report, ContentModel.ASSOC_CONTAINS, null, false);
        } else {
            templateFile = reportsManager.produceDefaultTemplate(report, template);   //файл шаблона не задан - генерируем по источнику данных
        }

        if (templateFile != null) {
            nodeService.setAssociations(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE, Arrays.asList(templateFile));
        }
    }

    @Override
    public void beforeDeleteNode(NodeRef template) {
        List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(template, ReportsEditorModel.ASSOC_REPORT_TEMPLATE_FILE);
        NodeRef templateFile;
        if (targetAssocs != null && !targetAssocs.isEmpty()) {
            templateFile = targetAssocs.get(0).getTargetRef();
            if (nodeService.exists(templateFile)) {
                nodeService.addAspect(templateFile, ContentModel.ASPECT_TEMPORARY, null);
            }
        }
    }
}