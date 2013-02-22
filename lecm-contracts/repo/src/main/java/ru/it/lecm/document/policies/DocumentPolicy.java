package ru.it.lecm.document.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.events.INodeACLBuilder;
import ru.it.lecm.security.events.IOrgStructureNotifiers;
//import ru.it.lecm.businessjournal.beans.BusinessJournalService;

public class DocumentPolicy
		extends BaseBean
		implements NodeServicePolicies.OnCreateNodePolicy
{

	final static protected Logger logger = LoggerFactory.getLogger (DocumentPolicy.class);

	// "lecm-contract:document"
	final public static String DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";
	final public QName TYPE_DOCUMENT = QName.createQName(DOCUMENT_NAMESPACE_URI, "document");

	// private fields
	private PolicyComponent policyComponent;
	private INodeACLBuilder lecmAclBuilder;

	private AuthenticationService authenticationService;
	private OrgstructureBean orgstructureService;
	private IOrgStructureNotifiers sgNotifier;
//	private BusinessJournalService businessJournalService;

	private String grantDynaRoleCode = "BR_INITIATOR";


	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public INodeACLBuilder getLecmAclBuilder() {
		return lecmAclBuilder;
	}

	public void setLecmAclBuilder(INodeACLBuilder value) {
		this.lecmAclBuilder = value;
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public IOrgStructureNotifiers getSgNotifier() {
		return this.sgNotifier;
	}

	public void setSgNotifier(IOrgStructureNotifiers value) {
		this.sgNotifier = value;
	}

//	public BusinessJournalService getBusinessJournalService() {
//		return businessJournalService;
//	}
//
//	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
//		this.businessJournalService = businessJournalService;
//	}

	/**
	 * @return код динамической роли, котрую надо автоматически присвоить 
	 * пользователю, создающему документ
	 */
	public String getGrantDynaRoleCode() {
		return grantDynaRoleCode;
	}

	/**
	 * @param grantDynaRoleCode код динамической роли, котрую надо автоматически присвоить 
	 * пользователю, создающему документ
	 */
	public void setGrantDynaRoleCode(String grantDynaRoleCode) {
		this.grantDynaRoleCode = grantDynaRoleCode;
	}


	/**
	 * bean-init-method
	 */
	final public void init() {
		logger.debug( "Installing policy ...");

		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "lecmAclBuilder", lecmAclBuilder);

		PropertyCheck.mandatory(this, "authenticationService", authenticationService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

		// PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);

		policyComponent.bindClassBehaviour( NodeServicePolicies.OnCreateNodePolicy.QNAME,
				TYPE_DOCUMENT, new JavaBehaviour(this, "onCreateNode"));

		logger.info( "Policy installed");
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef docRef = null;
		try {
			docRef = childAssocRef.getChildRef();

			if (this.getGrantDynaRoleCode() == null) {
				logger.warn( String.format("Dynamic role configeured as NULL -> nothing performed (document {%s}), docRef"));
				return;
			}

			logger.debug( String.format("Assigning dynamic role <%s> in document {%s}", this.getGrantDynaRoleCode(), docRef));

			final String authorLogin = authenticationService.getCurrentUserName();
			final NodeRef employee = orgstructureService.getEmployeeByPerson(authorLogin);
			if (employee == null) {
				logger.debug( String.format("Fail assigning dynamic role <%s> in document {%s}: employee is NULL", this.getGrantDynaRoleCode(), docRef));
				return;
			}

			// вызов обоновления личной sg-группы для пользователя ...
			// final Types.SGPrivateMeOfUser employeePos = (Types.SGPrivateMeOfUser) Types.SGKind.SG_ME.getSGPos( employee.getId(), authorLogin);
			// sgNotifier.orgBRAssigned( this.grantDynaRoleCode, employeePos);

			// нарезка прав на документ
			lecmAclBuilder.grantDynamicRole(this.grantDynaRoleCode, docRef, employee.getId());

			logger.info( String.format("Dynamic role <%s> assigned\n\t for user '%s'/employee {%s}\n\t in document {%s}", this.getGrantDynaRoleCode(), authorLogin, employee, docRef));

		} catch(Throwable ex) { // (!, RuSA, 2013/02/22) в политиках исключения поднимать наружу не предсказуемо может изменять поведение Alfresco
			logger.error( String.format( "Exception inside document policy handler for doc={%s}:\n\t%s", docRef, ex.getMessage() ), ex);
		}
	}

}

