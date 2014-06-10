package ru.it.lecm.statemachine.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 11:08
 */
public class StateMachineDeleteDocumentPolicy implements NodeServicePolicies.BeforeDeleteNodePolicy {

	private PolicyComponent policyComponent;

	final static Logger logger = LoggerFactory.getLogger(StateMachineDeleteDocumentPolicy.class);
    private StateMachineHelper stateMachineHelper;

    public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

    public void setStateMachineHelper(StateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

	public final void init() {
		logger.debug( "Installing Policy ...");

		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME, StatemachineModel.TYPE_CONTENT, new JavaBehaviour(this, "beforeDeleteNode"));
	}

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        if (stateMachineHelper.hasActiveStatemachine(nodeRef)) {
            String processId = stateMachineHelper.getStatemachineId(nodeRef);
            stateMachineHelper.terminateProcess(processId);
        }
    }


}
