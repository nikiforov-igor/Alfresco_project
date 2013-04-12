package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 12.03.13
 * Time: 16:54
 */
public class DocumentMembersPolicy extends BaseBean implements NodeServicePolicies.OnCreateAssociationPolicy,
		NodeServicePolicies.OnUpdatePropertiesPolicy,
		NodeServicePolicies.OnDeleteAssociationPolicy,
		NodeServicePolicies.OnCreateNodePolicy
{

	final protected Logger logger = LoggerFactory.getLogger(DocumentMembersPolicy.class);

	private PolicyComponent policyComponent;
	private DocumentMembersService documentMembersService;
	private BusinessJournalService businessJournalService;
	private NotificationChannelBeanBase notificationActiveChannel;
	private AuthenticationService authService;
	private OrgstructureBean orgstructureService;

	final public String DEFAULT_ACCESS = LecmPermissionGroup.PGROLE_Reader;
	private String grantAccess = DEFAULT_ACCESS; // must have legal corresponding LecmPermissionGroup

	private String grantDynaRoleCode = "BR_MEMBER";
	private LecmPermissionService lecmPermissionService;
	private final String DOC_LINK = "/share/page/document";

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setNotificationActiveChannel(NotificationChannelBeanBase notificationActiveChannel) {
		this.notificationActiveChannel = notificationActiveChannel;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public String getGrantDynaRoleCode() {
		return grantDynaRoleCode;
	}

	public void setGrantDynaRoleCode(String grantDynaRoleCode) {
		this.grantDynaRoleCode = grantDynaRoleCode;
	}

	public String getGrantAccess() {
		return grantAccess;
	}

	public void setGrantAccessTag(String value) {
		setGrantAccess(value); // StdPermission.findPermission(value));
	}

	public void setGrantAccess(String value) {
		this.grantAccess = (value != null) ? value : DEFAULT_ACCESS;
	}

	public final void init() {
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentMembersService.TYPE_DOC_MEMBER, new JavaBehaviour(this, "onCreateNode"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
				new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
				new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DocumentMembersService.TYPE_DOC_MEMBER, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentMembersService.TYPE_DOC_MEMBER, new JavaBehaviour(this, "onCreateNodeLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateDocument", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onUpdateDocument", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		// создание ассоциации документ -> участник
		NodeRef member = childAssocRef.getChildRef();
		NodeRef document = nodeService.getPrimaryParent(childAssocRef.getParentRef()).getParentRef();
		nodeService.createAssociation(document, member, DocumentService.ASSOC_DOC_MEMBERS);
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef docRef = null;
        try {
            NodeRef member = nodeAssocRef.getSourceRef();
            NodeRef folder = nodeService.getPrimaryParent(member).getParentRef();
            docRef = nodeService.getPrimaryParent(folder).getParentRef();
            NodeRef employee = nodeAssocRef.getTargetRef();
            if (this.getGrantDynaRoleCode() == null) {
                logger.warn("Dynamic role configured as NULL -> nothing performed (document {" + docRef + "})");
                return;
            }

            LecmPermissionGroup pgGranting = getLecmPermissionGroup(member);
            lecmPermissionService.grantAccess(pgGranting, docRef, employee.getId());
            nodeService.setProperty(nodeAssocRef.getSourceRef(),DocumentMembersService.PROP_MEMBER_GROUP, pgGranting.toString());
        } catch (Throwable ex) { // (!, RuSA, 2013/02/22) в политиках исключения поднимать наружу не предсказуемо может изменять поведение Alfresco
            logger.error(String.format("Exception inside document policy handler for doc {%s}:\n\t%s", docRef, ex.getMessage()), ex);
        }

        // Обновляем имя ноды
        String newName = documentMembersService.generateMemberNodeName(nodeAssocRef.getSourceRef());
        nodeService.setProperty(nodeAssocRef.getSourceRef(), ContentModel.PROP_NAME, newName);
	}

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        NodeRef docRef = null;
        try {
            NodeRef member = nodeAssocRef.getSourceRef();
            NodeRef folder = nodeService.getPrimaryParent(member).getParentRef();
            docRef = nodeService.getPrimaryParent(folder).getParentRef();
            NodeRef employee = nodeAssocRef.getTargetRef();

            LecmPermissionGroup pgRevoking = getLecmPermissionGroup(docRef);
            lecmPermissionService.revokeAccess(pgRevoking, docRef, employee.getId());
        } catch (Throwable ex) { // (!, RuSA, 2013/02/22) в политиках исключения поднимать наружу не предсказуемо может изменять поведение Alfresco
            logger.error(String.format("Exception inside document policy handler for doc {%s}:\n\t%s", docRef, ex.getMessage()), ex);
        }
    }

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		Object prevGroup = before.get(DocumentMembersService.PROP_MEMBER_GROUP);
		Object curGroup = after.get(DocumentMembersService.PROP_MEMBER_GROUP);
		if (before.size() == after.size() && curGroup != prevGroup) {
			// изменили группу привилегий - меняем имя ноды
			String newName = documentMembersService.generateMemberNodeName(nodeRef);
			nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, newName);
		}
	}

	public void onCreateNodeLog(ChildAssociationRef childAssocRef) {
		NodeRef member = childAssocRef.getChildRef();
		NodeRef folder = childAssocRef.getParentRef();
		NodeRef document = nodeService.getPrimaryParent(folder).getParentRef();

		NodeRef employee = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE).get(0).getTargetRef();
		final List<String> objects = Arrays.asList(employee.toString());

		// запись в БЖ
		final String initiator = authService.getCurrentUserName();
		if (!initiator.equals(orgstructureService.getEmployeeLogin(employee))) { // не создавать запись и уведомление для текущего пользователя
			businessJournalService.log(initiator, document, DocumentEventCategory.INVITE_DOCUMENT_MEMBER, "Сотрудник #initiator пригласил сотрудника #object1 в документ #mainobject", objects);

			// уведомление
			NotificationUnit notification = new NotificationUnit();
			notification.setRecipientRef(employee);
			notification.setAutor(authService.getCurrentUserName());
			notification.setDescription("Вы приглашены как новый участник в документ " +
					wrapperLink(document, nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING).toString(), DOC_LINK));
			notificationActiveChannel.sendNotification(notification);
		}
	}

    public void onCreateDocument(ChildAssociationRef childAssocRef) {
        // добаваление сотрудника, создавшего документ в участники
        final NodeRef docRef = childAssocRef.getChildRef();
        final String userName = (String) nodeService.getProperty(docRef, ContentModel.PROP_CREATOR);
        final LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(getGrantAccess());

        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(DocumentMembersService.PROP_MEMBER_GROUP, pgGranting.getName());
        if (!AuthenticationUtil.getSystemUserName().equals(userName)) {
            documentMembersService.addMemberWithoutCheckPermission(docRef, orgstructureService.getEmployeeByPerson(userName), props);
        }
    }

	public void onUpdateDocument(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		// добаваление сотрудника, изменившего документ в участники
        final String userName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIER);
        final LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(getGrantAccess());

        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(DocumentMembersService.PROP_MEMBER_GROUP, pgGranting.getName());
        if (!AuthenticationUtil.getSystemUserName().equals(userName)) {
            documentMembersService.addMemberWithoutCheckPermission(nodeRef, orgstructureService.getEmployeeByPerson(userName), props);
        }
    }

    private LecmPermissionGroup getLecmPermissionGroup(NodeRef memberRef) {
        LecmPermissionGroup pgGranting = null;
        String permGroup = (String) nodeService.getProperty(memberRef, DocumentMembersService.PROP_MEMBER_GROUP);
        if (permGroup != null && !permGroup.isEmpty()) {
            pgGranting = lecmPermissionService.findPermissionGroup(permGroup);
        }
        if (pgGranting == null) {
            pgGranting = lecmPermissionService.findPermissionGroup(this.getGrantAccess());
        }
        return pgGranting;
    }

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
