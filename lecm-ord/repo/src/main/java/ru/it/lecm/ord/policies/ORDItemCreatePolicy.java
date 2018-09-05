package ru.it.lecm.ord.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vkuprin
 */
public class ORDItemCreatePolicy implements NodeServicePolicies.OnCreateNodePolicy {

    private final static Logger logger = LoggerFactory.getLogger(ORDItemCreatePolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DictionaryBean dictionaryService;
    private OrgstructureBean orgstructureService;
    private DocumentTableService documentTableService;

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public DictionaryBean getDictionaryService() {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
        PropertyCheck.mandatory(this, "documentTableService", documentTableService);
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
            ORDModel.TYPE_ORD_TABLE_ITEM,
            new JavaBehaviour(this, "onCreateNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef node = childAssocRef.getChildRef();
        if (nodeService.getTargetAssocs(node, ORDModel.ASSOC_ORD_TABLE_ITEM_STATUS).isEmpty()) {
            nodeService.createAssociation(node, dictionaryService.getDictionaryValueByParam(EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.dictionaries.ordPoints.name", ORDModel.ORD_POINT_DICTIONARY_NAME), ORDModel.PROP_ORD_DIC_POINT_STATUS_CODE, ORDModel.P_STATUSES.WAIT_PERFORMANCE_STATUS.toString()), ORDModel.ASSOC_ORD_TABLE_ITEM_STATUS);
        }
        //заполнение составителя
        NodeRef compiler = orgstructureService.getCurrentEmployee();
        nodeService.createAssociation(node, compiler, ORDModel.ASSOC_ORD_TABLE_ITEM_COMPILER);
    }

}
