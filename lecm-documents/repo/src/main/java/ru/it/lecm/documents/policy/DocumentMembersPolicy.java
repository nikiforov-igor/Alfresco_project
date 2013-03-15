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

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
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
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        logger.debug("!!!Here are given permission to document!!!");
        // Обновляем имя ноды
        String newName = documentMembersService.generateMemberNodeName(nodeAssocRef.getSourceRef());
        nodeService.setProperty(nodeAssocRef.getSourceRef(), ContentModel.PROP_NAME, newName);
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef member = childAssocRef.getChildRef();
        NodeRef folder = childAssocRef.getParentRef();
        NodeRef document = nodeService.getPrimaryParent(folder).getParentRef();
        nodeService.createAssociation(document, member, DocumentService.ASSOC_DOC_MEMBERS);
    }

    public void onCreateNodeLog(ChildAssociationRef childAssocRef) {
        NodeRef member = childAssocRef.getChildRef();
        NodeRef folder = childAssocRef.getParentRef();
        NodeRef document = nodeService.getPrimaryParent(folder).getParentRef();

        NodeRef employee = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE).get(0).getTargetRef();
        final List<String> objects = Arrays.asList(employee.toString());

        // запись в БЖ
        final String initiator = authService.getCurrentUserName();
        businessJournalService.log(initiator, document, DocumentEventCategory.INVITE_DOCUMENT_MEMBER, "Сотрудник #initiator пригласил сотрудника #object1 в документ #mainobject", objects);

        // уведомление
        NotificationUnit notification = new NotificationUnit();
        notification.setRecipientRef(employee);
        notification.setAutor(authService.getCurrentUserName());
        notification.setDescription("Вы приглашены как новый участник в документ " + nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING));
        notificationActiveChannel.sendNotification(notification);
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        logger.debug("!!!Here are taken permission to the document!!!");
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
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

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setNotificationActiveChannel(NotificationChannelBeanBase notificationActiveChannel) {
        this.notificationActiveChannel = notificationActiveChannel;
    }

    public void onCreateDocument(ChildAssociationRef childAssocRef) {
        NodeRef document = childAssocRef.getChildRef();
        String userName = (String) nodeService.getProperty(document, ContentModel.PROP_CREATOR);
        documentMembersService.addMember(document, orgstructureService.getEmployeeByPerson(userName) , null);
    }

    public void onUpdateDocument(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (before.size() == after.size()) {
            String userName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIER);
            documentMembersService.addMember(nodeRef, orgstructureService.getEmployeeByPerson(userName) , null);
        }
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }
}
