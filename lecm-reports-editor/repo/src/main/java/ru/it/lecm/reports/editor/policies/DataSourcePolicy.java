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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: dbashmakov
 * Date: 28.06.13
 * Time: 10:15
 */
public class DataSourcePolicy implements NodeServicePolicies.BeforeCreateNodePolicy {

    protected PolicyComponent policyComponent;
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected DictionaryService dictionaryService;

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

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeCreateNodePolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_DATA_SOURCE, new JavaBehaviour(this, "beforeCreateNode"));
    }

    @Override
    public void beforeCreateNode(NodeRef nodeRef, QName qName, QName qName2, QName qName3) {
        QName parentType = nodeService.getType(nodeRef);
        if (parentType.equals(ReportsEditorModel.TYPE_REPORT_DESCRIPTOR)) {
            //удаляем прежний набор (так как у отчета набор может быть лишь один)
            Set<QName> types = new HashSet<QName>();
            types.add(ReportsEditorModel.TYPE_REPORT_DATA_SOURCE);
            List<ChildAssociationRef> childs = nodeService.getChildAssocs(nodeRef, types);
            for (ChildAssociationRef child : childs) {
                NodeRef childRef = child.getChildRef();
                nodeService.addAspect(childRef, ContentModel.ASPECT_TEMPORARY, null);
                nodeService.deleteNode(childRef);
            }
        }
    }
}