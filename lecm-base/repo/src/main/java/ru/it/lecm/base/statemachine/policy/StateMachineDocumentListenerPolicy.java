package ru.it.lecm.base.statemachine.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.statemachine.StateMachineHelper;
import ru.it.lecm.base.statemachine.StateMachineModel;

import java.io.Serializable;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 05.10.12
 * Time: 11:30
 */
public class StateMachineDocumentListenerPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

/*
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));
*/
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (nodeService.hasAspect(nodeRef, StateMachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
            new StateMachineHelper().stopDocumentProcessing((String) nodeService.getProperty(nodeRef, StateMachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS));
        }
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

}