package ru.it.lecm.document.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
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
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.events.INodeACLBuilder;
import ru.it.lecm.security.events.INodeACLBuilder.StdPermission;
import ru.it.lecm.security.events.IOrgStructureNotifiers;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.Map;
//import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * Политика отслеживания создания документов с типом "lecm-contract:document"
 * для автоматической выдачи роли Инициатор для автора операции.
 * 
 * @author rabdullin
 */
public class DocumentPolicy
		extends BaseBean
		implements NodeServicePolicies.OnCreateNodePolicy
{

	final static protected Logger logger = LoggerFactory.getLogger (DocumentPolicy.class);

	// "lecm-contract:document"
	final public static String DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";
	final public QName TYPE_DOCUMENT = QName.createQName(DOCUMENT_NAMESPACE_URI, "document");
	final public StdPermission DEFAULT_ACCESS = StdPermission.full;

    final private QName[] IGNORED_PROPERTIES = {DocumentService.PROP_RATING, DocumentService.PROP_RATED_PERSONS_COUNT, StatemachineModel.PROP_STATUS};
	// private fields
	private PolicyComponent policyComponent;
	private INodeACLBuilder lecmAclBuilder;

	private AuthenticationService authenticationService;
	private OrgstructureBean orgstructureService;
	private IOrgStructureNotifiers sgNotifier;
	private BusinessJournalService businessJournalService;

	private String grantDynaRoleCode = "BR_INITIATOR";
	private StdPermission grantAccess = DEFAULT_ACCESS;

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

	public BusinessJournalService getBusinessJournalService() {
		return businessJournalService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

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
	 * @return предоставляемый доступ для роли grantDynaRoleCode
	 */
	public StdPermission getGrantAccess() {
		return grantAccess;
	}

	/**
	 * @param value предоставляемый доступ для роли grantDynaRoleCode
	 */
	public void setGrantAccess(StdPermission value) {
		this.grantAccess = (value != null) ? value : DEFAULT_ACCESS;
	}

	public void setGrantAccessTag(String value) {
		setGrantAccess(StdPermission.findPermission(value));
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
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                TYPE_DOCUMENT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

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

			/* 
			 * автоматическое присвоение Динамической бизнес-роли для Cотрудника:
			 * вызов обоновления личной sg-группы пользователя ...
			 */
			// final Types.SGPrivateMeOfUser employeePos = (Types.SGPrivateMeOfUser) Types.SGKind.SG_ME.getSGPos( employee.getId(), authorLogin);
			// sgNotifier.orgBRAssigned( this.grantDynaRoleCode, employeePos);

			/*
			 * нарезка прав на Документ
			 * (!) Если реально Динамическая роль явно не была ранее выдана Сотруднику,
			 * такая нарезка ничего не выполнит.
			 */
			lecmAclBuilder.grantDynamicRole(this.getGrantDynaRoleCode(), docRef, employee.getId(), this.getGrantAccess());

			logger.info( String.format("Dynamic role <%s> assigned\n\t for user '%s'/employee {%s}\n\t in document {%s}", this.getGrantDynaRoleCode(), authorLogin, employee, docRef));

		} catch(Throwable ex) { // (!, RuSA, 2013/02/22) в политиках исключения поднимать наружу не предсказуемо может изменять поведение Alfresco
			logger.error( String.format( "Exception inside document policy handler for doc {%s}:\n\t%s", docRef, ex.getMessage() ), ex);
		}
	}

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (!changeIgnoredProperties(before, after)) {
            if (before.size() == after.size()) { // только при изменении свойств
                businessJournalService.log(nodeRef, EventCategory.EDIT, "Сотрудник #initiator внес изменения в договор \"#mainobject\"");
            }
        }
    }

    private boolean changeIgnoredProperties(Map<QName, Serializable> before, Map<QName, Serializable> after) {
        for (QName ignored : IGNORED_PROPERTIES) {
            Object prev = before.get(ignored);
            Object cur = after.get(ignored);
            if (cur != null && !cur.equals(prev)) {
                return true;
            }
        }
        return false;
    }
}

