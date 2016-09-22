package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.Set;

/**
 * User: dbashmakov
 * Date: 20.03.13
 * Time: 15:02
 */
public class InitiatorDocumentPolicy implements NodeServicePolicies.OnCreateNodePolicy {

	private static final String GRAND_DYNAMIC_ROLE_CODE_INITIATOR = "BR_INITIATOR";

    final static protected Logger logger = LoggerFactory.getLogger(InitiatorDocumentPolicy.class);

    private PolicyComponent policyComponent;
    private AuthenticationService authenticationService;
    private OrgstructureBean orgstructureService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineService;
	private NamespaceService namespaceService;
	private NodeService nodeService;
	private IDelegation delegationService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDelegationService(IDelegation delegationService) {
		this.delegationService = delegationService;
	}

	final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "authenticationService", authenticationService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
    	logger.debug("ДОКУМЕНТ. onCreateNode");
	    // Добавление прав инициатора
	    NodeRef docRef = childAssocRef.getChildRef();
	    //TODO Возможно здесь не текущий пользовател а кто-то еще - String authorLogin = (String) nodeService.getProperty(docRef, ContentModel.PROP_MODIFIER);
	    String authorLogin = authenticationService.getCurrentUserName();
	    NodeRef employee = orgstructureService.getEmployeeByPerson(authorLogin);

	    LecmPermissionService.LecmPermissionGroup initiatorPermissionGroup = lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Initiator);

	    lecmPermissionService.grantDynamicRole(GRAND_DYNAMIC_ROLE_CODE_INITIATOR, docRef, employee.getId(), initiatorPermissionGroup);

	    if (delegationService.getCreateDocumentDelegationSetting()) {
		    QName documentType = nodeService.getType(docRef);
		    if (documentType != null) {
			    Set<String> startRoles = stateMachineService.getStarterRoles(documentType.toPrefixString(namespaceService));
			    if (startRoles != null) {
				    Set<NodeRef> delegateOwners = delegationService.getDeletionOwnerEmployees(employee, startRoles);
				    if (delegateOwners != null ) {
					    for (NodeRef owner: delegateOwners) {
						    lecmPermissionService.grantDynamicRole(GRAND_DYNAMIC_ROLE_CODE_INITIATOR, docRef, owner.getId(), initiatorPermissionGroup);
					    }
				    }
			    }
		    }
	    }
    }

}
