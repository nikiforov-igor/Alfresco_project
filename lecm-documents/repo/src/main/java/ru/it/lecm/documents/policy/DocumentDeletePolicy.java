package ru.it.lecm.documents.policy;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 * User: pmelnikov
 * Date: 03.04.13
 * Time: 16:54
 */
public class DocumentDeletePolicy implements NodeServicePolicies.BeforeDeleteNodePolicy {

	final protected Logger logger = LoggerFactory.getLogger(DocumentDeletePolicy.class);

	private PolicyComponent policyComponent;
    private StateMachineServiceBean stateMachineServiceBean;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setStateMachineServiceBean(StateMachineServiceBean stateMachineServiceBean) {
        this.stateMachineServiceBean = stateMachineServiceBean;
    }

    public final void init() {
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "beforeDeleteNode"));

	}

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        if (stateMachineServiceBean.hasActiveStatemachine(nodeRef) && !stateMachineServiceBean.isDraft(nodeRef)) {
            throw new AlfrescoRuntimeException("Cannot delete document " + nodeRef + ". Is not draft.");
        }
    }
}
