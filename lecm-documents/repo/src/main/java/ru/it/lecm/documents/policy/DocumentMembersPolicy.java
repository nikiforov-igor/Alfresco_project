package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
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
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.*;

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
	private NotificationsService notificationService;
	private AuthenticationService authService;
	private OrgstructureBean orgstructureService;
    private StateMachineServiceBean stateMachineBean;

	final public String DEFAULT_ACCESS = LecmPermissionGroup.PGROLE_Reader;
	private String grantAccess = DEFAULT_ACCESS; // must have legal corresponding LecmPermissionGroup

	private LecmPermissionService lecmPermissionService;

    final private QName[] AFFECTED_NOT_ADD_MEMBER_PROPERTIES_ON_FINAL_STATE = {ForumModel.PROP_COMMENT_COUNT, DocumentService.PROP_RATING, DocumentService.PROP_RATED_PERSONS_COUNT};
    final private QName[] AFFECTED_NOT_ADD_MEMBER_PROPERTIES_EVER = { DocumentService.PROP_SYS_WORKFLOWS };

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

	public void setNotificationService(NotificationsService notificationService) {
		this.notificationService = notificationService;
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

	public String getGrantAccess() {
		return grantAccess;
	}

	public void setGrantAccessTag(String value) {
		setGrantAccess(value); // StdPermission.findPermission(value));
	}

	public void setGrantAccess(String value) {
		this.grantAccess = (value != null) ? value : DEFAULT_ACCESS;
	}

    public void setStateMachineBean(StateMachineServiceBean stateMachineBean) {
        this.stateMachineBean = stateMachineBean;
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
		nodeService.createAssociation(document, member, DocumentMembersService.ASSOC_DOC_MEMBERS);
        hideNode(member, true);
	}

	@Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef member = nodeAssocRef.getSourceRef();
        NodeRef folder = nodeService.getPrimaryParent(member).getParentRef();
        NodeRef docRef = nodeService.getPrimaryParent(folder).getParentRef();
        NodeRef employee = nodeAssocRef.getTargetRef();

        try {
            // Выдача прав новому участнику
            LecmPermissionService.LecmPermissionGroup pgGranting = documentMembersService.getMemberPermissionGroup(member);
            lecmPermissionService.grantAccess(pgGranting, docRef, employee);
            nodeService.setProperty(member, DocumentMembersService.PROP_MEMBER_GROUP, pgGranting.toString());
        } catch (Throwable ex) { // (!, RuSA, 2013/02/22) в политиках исключения поднимать наружу не предсказуемо может изменять поведение Alfresco
            logger.error("Не удалось выдать права новому участнику!", ex);
        }
        try {
            // сохранение ссылки на сотрудника-участника в документе
            QName propertyRefQName = DocumentMembersService.PROP_DOC_MEMBERS;

            Serializable oldValue = nodeService.getProperty(docRef, propertyRefQName);
            String strOldValue = oldValue != null ? oldValue.toString() : "";
            String refValue = employee.toString();
            if (!strOldValue.contains(refValue)) {
                if (!strOldValue.isEmpty()) {
                    strOldValue += ";";
                }
                strOldValue += refValue;
            }
            nodeService.setProperty(docRef, propertyRefQName, strOldValue);
        } catch (Throwable ex) {
            logger.error("Не удалось сохранить ссылку на участника!", ex);
        }
        documentMembersService.addMemberToUnit(employee, docRef);

        // уведомление
        Boolean silent = (Boolean) nodeService.getProperty(member, DocumentMembersService.PROP_SILENT);
        if (silent == null || !silent) {
            Notification notification = new Notification();
            ArrayList<NodeRef> employeeList = new ArrayList<NodeRef>();
            employeeList.add(employee);
            notification.setRecipientEmployeeRefs(employeeList);
            notification.setAuthor(authService.getCurrentUserName());
            notification.setDescription("Вы приглашены как новый участник в документ " +
                    wrapperLink(docRef, nodeService.getProperty(docRef, DocumentService.PROP_PRESENT_STRING).toString(), DOCUMENT_LINK_URL));
            notification.setObjectRef(docRef);
            notification.setInitiatorRef(orgstructureService.getCurrentEmployee());
            notificationService.sendNotification(notification);
        }

        // Обновляем имя ноды
        String newName = generateMemberNodeName(nodeAssocRef.getSourceRef());
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

            LecmPermissionGroup pgRevoking = null;
            String permGroup = (String) nodeService.getProperty(member, DocumentMembersService.PROP_MEMBER_GROUP);
            if (permGroup != null && !permGroup.isEmpty()) {
                pgRevoking = lecmPermissionService.findPermissionGroup(permGroup);
            }
            if (pgRevoking == null) {
                pgRevoking = lecmPermissionService.findPermissionGroup(this.getGrantAccess());
            }
            lecmPermissionService.revokeAccess(pgRevoking, docRef, employee);
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
			String newName = generateMemberNodeName(nodeRef);
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
			businessJournalService.log(initiator, document, DocumentEventCategory.INVITE_DOCUMENT_MEMBER, "#initiator пригласил(а) сотрудника #object1 в документ #mainobject", objects);
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

        /* не добавлять сотрудника как участника
            1) если изменения выполняет система
            2) если сотрудник добавляет комментарий, ставит рейтинг и документ находится на финальном статусе
            3) если меняются бизнес-процессы, прицепленные к документу (бизнес-процесс сам решит, включать ли сотрудника в участники)
        */
        if (!AuthenticationUtil.getSystemUserName().equals(userName) &&
                !(hasDoNotAddMemberUpdatedProperties(before, after, AFFECTED_NOT_ADD_MEMBER_PROPERTIES_ON_FINAL_STATE) && stateMachineBean.isFinal(nodeRef)) &&
                !hasDoNotAddMemberUpdatedProperties(before, after, AFFECTED_NOT_ADD_MEMBER_PROPERTIES_EVER)) {
            documentMembersService.addMemberWithoutCheckPermission(nodeRef, orgstructureService.getEmployeeByPerson(userName), props);
        }
    }

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

    private boolean hasDoNotAddMemberUpdatedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after, QName[] notAffectedProperties) {
        for (QName affected : notAffectedProperties) {
            Object prev = before.get(affected);
            Object cur = after.get(affected);
            if (cur != null && !cur.equals(prev)) {
                return true;
            }
        }
        return false;
    }

    private String generateMemberNodeName(NodeRef member) {
        Object propGroup = nodeService.getProperty(member, DocumentMembersService.PROP_MEMBER_GROUP);
        String groupName = propGroup != null ? (String) propGroup : "";

        NodeRef employee = findNodeByAssociationRef(member,DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,null,ASSOCIATION_TYPE.TARGET);
        String propName = employee != null ? (String) nodeService.getProperty(employee, ContentModel.PROP_NAME) : "unnamed";

        return (propName + " " + groupName).trim();
    }
}
