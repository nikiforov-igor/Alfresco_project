package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

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

	final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "authenticationService", authenticationService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateNode"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
	    // Добавление прав инициатора
	    NodeRef docRef = childAssocRef.getChildRef();
	    String authorLogin = authenticationService.getCurrentUserName();
	    NodeRef employee = orgstructureService.getEmployeeByPerson(authorLogin);
	    lecmPermissionService.grantDynamicRole(GRAND_DYNAMIC_ROLE_CODE_INITIATOR, docRef, employee.getId(), lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Initiator) );
    }

}
