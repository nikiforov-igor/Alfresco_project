package ru.it.lecm.documents.policy;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 * User: pmelnikov
 * Date: 03.04.13
 * Time: 16:54
 */
public class DocumentDeletePolicy implements BeforeDeleteNodePolicy {

	final protected Logger logger = LoggerFactory.getLogger(DocumentDeletePolicy.class);

	private PolicyComponent policyComponent;
    private StateMachineServiceBean stateMachineService;
	private BusinessJournalService businessJournalService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

    public final void init() {
		policyComponent.bindClassBehaviour(BeforeDeleteNodePolicy.QNAME, DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "beforeDeleteNode"));
	}

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
    	logger.debug("ДОКУМЕНТ. beforeDeleteNode");
        if (stateMachineService.hasActiveStatemachine(nodeRef) && !stateMachineService.isDraft(nodeRef)) {
            //throw new AlfrescoRuntimeException("Cannot delete document " + nodeRef + ". Is not draft.");
        }
		String user = AuthenticationUtil.getFullyAuthenticatedUser();

		String defaultDescription = "Документ #mainobject полностью удален из системы";
		BusinessJournalRecord record = businessJournalService.createBusinessJournalRecord(user, nodeRef, EventCategory.DELETE, defaultDescription);
		businessJournalService.sendRecord(record);
    }
}
