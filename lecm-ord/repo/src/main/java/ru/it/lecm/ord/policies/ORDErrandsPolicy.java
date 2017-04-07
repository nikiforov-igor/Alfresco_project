package ru.it.lecm.ord.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.ord.api.ORDModel;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 07.04.2017
 * Time: 9:18
 */
public class ORDErrandsPolicy {
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentMembersService documentMembersService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS,
                new JavaBehaviour(this, "onUpdateProperties"));
    }

    public void onUpdateProperties(NodeRef errand, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String beforeStatus = (String) before.get(StatemachineModel.PROP_STATUS);
        String afterStatus = (String) after.get(StatemachineModel.PROP_STATUS);

        if ("Ожидает исполнения".equals(afterStatus) && !afterStatus.equals(beforeStatus)) {
            List<AssociationRef> parentDocAssoc = nodeService.getTargetAssocs(errand, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
            if (parentDocAssoc != null && parentDocAssoc.size() > 0 && ORDModel.TYPE_ORD.equals(nodeService.getType(parentDocAssoc.get(0).getTargetRef()))) {
                List<AssociationRef> ordControllerAssoc = nodeService.getTargetAssocs(parentDocAssoc.get(0).getTargetRef(), ORDModel.ASSOC_ORD_CONTROLLER);
                if (ordControllerAssoc != null) {
                    for (AssociationRef assoc : ordControllerAssoc) {
                        documentMembersService.addMemberWithoutCheckPermission(errand, assoc.getTargetRef(), new HashMap<QName, Serializable>(), true);
                    }
                }
            }
        }
    }
}
