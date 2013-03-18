package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.events.INodeACLBuilder;
import ru.it.lecm.security.events.INodeACLBuilder.StdPermission;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 12.03.13
 * Time: 16:54
 */
public class DocumentMembersPolicy implements NodeServicePolicies.OnCreateAssociationPolicy,
        NodeServicePolicies.OnUpdatePropertiesPolicy,
        NodeServicePolicies.OnDeleteAssociationPolicy,
        NodeServicePolicies.OnCreateNodePolicy {

    final protected Logger logger = LoggerFactory.getLogger(DocumentMembersPolicy.class);

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DocumentMembersService documentMembersService;
    private BusinessJournalService businessJournalService;
    private NotificationChannelBeanBase notificationActiveChannel;
    private AuthenticationService authService;
    private OrgstructureBean orgstructureService;

    final public StdPermission DEFAULT_ACCESS = StdPermission.readonly;

    private String grantDynaRoleCode = "BR_MEMBER";
    private StdPermission grantAccess = DEFAULT_ACCESS;
    private INodeACLBuilder lecmAclBuilder;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
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

    public String getGrantDynaRoleCode() {
        return grantDynaRoleCode;
    }

    public void setGrantDynaRoleCode(String grantDynaRoleCode) {
        this.grantDynaRoleCode = grantDynaRoleCode;
    }

    public StdPermission getGrantAccess() {
        return grantAccess;
    }

    public void setGrantAccessTag(String value) {
        setGrantAccess(StdPermission.findPermission(value));
    }

    public void setGrantAccess(StdPermission value) {
        this.grantAccess = (value != null) ? value : DEFAULT_ACCESS;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, new JavaBehaviour(this, "onCreateNode"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
                new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
                new JavaBehaviour(this, "onDeleteAssociation"));

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
        NodeRef folder = childAssocRef.getParentRef();
        NodeRef document = nodeService.getPrimaryParent(folder).getParentRef();
        nodeService.createAssociation(document, member, DocumentService.ASSOC_DOC_MEMBERS);
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        // Обновляем имя ноды
        String newName = documentMembersService.generateMemberNodeName(nodeAssocRef.getSourceRef());
        nodeService.setProperty(nodeAssocRef.getSourceRef(), ContentModel.PROP_NAME, newName);

        NodeRef docRef = null;
        try {
            NodeRef member = nodeAssocRef.getSourceRef();
            NodeRef folder = nodeService.getPrimaryParent(member).getParentRef();
            docRef = nodeService.getPrimaryParent(folder).getParentRef();

            if (this.getGrantDynaRoleCode() == null) {
                logger.warn(String.format("Dynamic role configured as NULL -> nothing performed (document {%s})", docRef));
                return;
            }

            logger.debug(String.format("Assigning dynamic role <%s> in document {%s}", this.getGrantDynaRoleCode(), docRef));

            final String authorLogin = authService.getCurrentUserName();
            final NodeRef employee = orgstructureService.getEmployeeByPerson(authorLogin);
            if (employee == null) {
                logger.debug(String.format("Fail assigning dynamic role <%s> in document {%s}: employee is NULL", this.getGrantDynaRoleCode(), docRef));
                return;
            }

			/*
			 * нарезка прав на Документ
			 * (!) Если реально Динамическая роль явно не была ранее выдана Сотруднику,
			 * такая нарезка ничего не выполнит.
			 */
            lecmAclBuilder.grantDynamicRole(this.getGrantDynaRoleCode(), docRef, employee.getId(), this.getGrantAccess());

            logger.info(String.format("Dynamic role <%s> assigned\n\t for user '%s'/employee {%s}\n\t in document {%s}", this.getGrantDynaRoleCode(), authorLogin, employee, docRef));

        } catch (Throwable ex) { // (!, RuSA, 2013/02/22) в политиках исключения поднимать наружу не предсказуемо может изменять поведение Alfresco
            logger.error(String.format("Exception inside document policy handler for doc {%s}:\n\t%s", docRef, ex.getMessage()), ex);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        NodeRef docRef = null;
        try {
            NodeRef member = nodeAssocRef.getSourceRef();
            NodeRef folder = nodeService.getPrimaryParent(member).getParentRef();
            docRef = nodeService.getPrimaryParent(folder).getParentRef();

            logger.debug(String.format("Revoke dynamic role <%s> in document {%s}", this.getGrantDynaRoleCode(), docRef));

            final String authorLogin = authService.getCurrentUserName();
            final NodeRef employee = orgstructureService.getEmployeeByPerson(authorLogin);
            if (employee == null) {
                logger.debug(String.format("Fail revoke dynamic role <%s> in document {%s}: employee is NULL", this.getGrantDynaRoleCode(), docRef));
                return;
            }

            lecmAclBuilder.revokeDynamicRole(this.getGrantDynaRoleCode(), docRef, employee.getId());

            logger.info(String.format("Dynamic role revoked\n\t for user '%s'/employee {%s}\n\t in document {%s}", authorLogin, employee, docRef));

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
            notification.setDescription("Вы приглашены как новый участник в документ " + nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING));
            notificationActiveChannel.sendNotification(notification);
        }
    }

    public void onCreateDocument(ChildAssociationRef childAssocRef) {
        // добаваление сотрудника, создавшего документ в участники
        NodeRef document = childAssocRef.getChildRef();
        String userName = (String) nodeService.getProperty(document, ContentModel.PROP_CREATOR);
        documentMembersService.addMember(document, orgstructureService.getEmployeeByPerson(userName), null);
    }

    public void onUpdateDocument(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        // добаваление сотрудника, изменившего документ в участники
        if (before.size() == after.size()) {
            String userName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIER);
            documentMembersService.addMember(nodeRef, orgstructureService.getEmployeeByPerson(userName), null);
        }
    }

    public void setLecmAclBuilder(INodeACLBuilder lecmAclBuilder) {
        this.lecmAclBuilder = lecmAclBuilder;
    }
}
