package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
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
        NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.BeforeDeleteNodePolicy {

    final protected Logger logger = LoggerFactory.getLogger(DocumentMembersPolicy.class);

    private PolicyComponent policyComponent;
    private DocumentMembersService documentMembersService;
    private BusinessJournalService businessJournalService;
    private NotificationsService notificationService;
    private OrgstructureBean orgstructureService;
    private StateMachineServiceBean stateMachineService;
    private NamespaceService namespaceService;
    private DocumentService documentService;

    final public String DEFAULT_ACCESS = LecmPermissionGroup.PGROLE_Reader;

    private String grantAccess = DEFAULT_ACCESS; // must have legal corresponding LecmPermissionGroup

    private LecmPermissionService lecmPermissionService;
    final private QName[] AFFECTED_NOT_ADD_MEMBER_PROPERTIES_ON_FINAL_STATE = {ForumModel.PROP_COMMENT_COUNT, DocumentService.PROP_RATING, DocumentService.PROP_RATED_PERSONS_COUNT};
    final private QName[] AFFECTED_NOT_ADD_MEMBER_PROPERTIES_EVER = {DocumentService.PROP_SYS_WORKFLOWS};

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
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

    public String getGrantAccess() {
        return grantAccess;
    }

    public void setGrantAccessTag(String value) {
		setGrantAccess(value); // StdPermission.findPermission(value));
    }

    public void setGrantAccess(String value) {
        this.grantAccess = (value != null) ? value : DEFAULT_ACCESS;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, new JavaBehaviour(this, "onCreateNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, new JavaBehaviour(this, "beforeDeleteNode"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
                new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                DocumentMembersService.TYPE_DOC_MEMBER, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE,
                new JavaBehaviour(this, "onDeleteAssociation",Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

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
    	logger.debug("ДОКУМЕНТ. onCreateNode");
        // создание ассоциации документ -> участник
        NodeRef member = childAssocRef.getChildRef();
        NodeRef document = nodeService.getPrimaryParent(childAssocRef.getParentRef()).getParentRef();
        nodeService.createAssociation(document, member, DocumentMembersService.ASSOC_DOC_MEMBERS);
        hideNode(member, true);
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
    	logger.debug("ДОКУМЕНТ. onCreateAssociation");
        NodeRef member = nodeAssocRef.getSourceRef();
        if (nodeService.exists(member)) {
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
//		TODO: Метод addMemberToUnit дёрагал метод getOrCreateDocMemberUnit,
//		который был благополучно разделён. Поэтому сделаем проверку на существование
            String type = nodeService.getType(docRef).toPrefixString(namespaceService).replaceAll(":", "_");
            if (documentMembersService.getDocMembersUnit(type) == null) {
                try {
                    documentMembersService.createDocMemberUnit(type);
                } catch (WriteTransactionNeededException ex) {
                    throw new RuntimeException("Can't create DocMemberUnit");
                }
            }
            documentMembersService.addMemberToUnit(employee, docRef);

            // уведомление
            Boolean silent = (Boolean) nodeService.getProperty(member, DocumentMembersService.PROP_SILENT);
            if (silent == null || !silent) {
                HashMap<String, Object> templateObjects = new HashMap<>();
                templateObjects.put("mainObject", docRef);
                Notification notification = new Notification(templateObjects);
                notification.setRecipientEmployeeRefs(Collections.singletonList(employee));
                notification.setAuthor(authService.getCurrentUserName());
                notification.setTemplateCode("DOCUMENT_INVITATION");
                notification.setObjectRef(docRef);
                notification.setInitiatorRef(orgstructureService.getCurrentEmployee());
                notificationService.sendNotification(notification);
            }

            // Обновляем имя ноды
			String newName = generateMemberNodeName(/*member*/nodeAssocRef.getSourceRef());
			nodeService.setProperty(/*member*/nodeAssocRef.getSourceRef(), ContentModel.PROP_NAME, newName);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
    	logger.debug("ДОКУМЕНТ. onDeleteAssociation");
        NodeRef docRef = null;
        try {
            NodeRef member = nodeAssocRef.getSourceRef();
            if (nodeService.exists(member) && !nodeService.hasAspect(member, ContentModel.ASPECT_PENDING_DELETE)) {
                NodeRef folder = nodeService.getPrimaryParent(member).getParentRef();
                docRef = nodeService.getPrimaryParent(folder).getParentRef();
                NodeRef employee = nodeAssocRef.getTargetRef();

                revokePermission(member, docRef, employee);
            }
        } catch (Throwable ex) { // (!, RuSA, 2013/02/22) в политиках исключения поднимать наружу не предсказуемо может изменять поведение Alfresco
            logger.error(String.format("Exception inside document policy handler for doc {%s}:\n\t%s", docRef, ex.getMessage()), ex);
        }
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
    	logger.debug("ДОКУМЕНТ. onUpdateProperties");
        Object prevGroup = before.get(DocumentMembersService.PROP_MEMBER_GROUP);
        Object curGroup = after.get(DocumentMembersService.PROP_MEMBER_GROUP);
        if (before.size() == after.size() && curGroup != prevGroup) {
            // изменили группу привилегий - меняем имя ноды
            String newName = generateMemberNodeName(nodeRef);
            nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, newName);
        }
    }

    public void onCreateNodeLog(ChildAssociationRef childAssocRef) {
    	logger.debug("ДОКУМЕНТ. onCreateNodeLog");
        NodeRef member = childAssocRef.getChildRef();
        NodeRef folder = childAssocRef.getParentRef();

        if (nodeService.exists(folder)) {
            NodeRef document = nodeService.getPrimaryParent(folder).getParentRef();

            NodeRef employee = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE).get(0).getTargetRef();
            final List<String> objects = Arrays.asList(employee.toString());

            // запись в БЖ
            final String initiator = authService.getCurrentUserName();
            if (!initiator.equals(orgstructureService.getEmployeeLogin(employee))) { // не создавать запись и уведомление для текущего пользователя
                businessJournalService.log(initiator, document, DocumentEventCategory.INVITE_DOCUMENT_MEMBER, "#initiator пригласил(а) сотрудника #object1 в документ #mainobject", objects);
            }
        }
    }

    public void onCreateDocument(ChildAssociationRef childAssocRef) {
    	logger.debug("ДОКУМЕНТ. onCreateDocument");
        // добаваление сотрудника, создавшего документ в участники
        final NodeRef docRef = childAssocRef.getChildRef();
        final String userName = (String) nodeService.getProperty(docRef, ContentModel.PROP_CREATOR);
        if (!AuthenticationUtil.getSystemUserName().equals(userName)) {
            final LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(getGrantAccess());
            Map<QName, Serializable> props = new HashMap<>();
            props.put(DocumentMembersService.PROP_MEMBER_GROUP, pgGranting.getName());
            //полиси onCreate. транзакция должна быть                        
            try {
                documentMembersService.addMemberWithoutCheckPermission(docRef, orgstructureService.getEmployeeByPerson(userName), props);
            } catch (WriteTransactionNeededException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void onUpdateDocument(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
    	logger.debug("ДОКУМЕНТ. onUpdateDocument");
        /* не добавлять сотрудника как участника
            1) если изменения выполняет система
            2) если сотрудник добавляет комментарий, ставит рейтинг и документ находится на финальном статусе
            3) если меняются бизнес-процессы, прицепленные к документу (бизнес-процесс сам решит, включать ли сотрудника в участники)
        */
        if (nodeService.exists(nodeRef) && !AuthenticationUtil.isRunAsUserTheSystemUser() &&
                !(hasDoNotAddMemberUpdatedProperties(before, after, AFFECTED_NOT_ADD_MEMBER_PROPERTIES_ON_FINAL_STATE) && stateMachineService.isFinal(nodeRef)) &&
                !hasDoNotAddMemberUpdatedProperties(before, after, AFFECTED_NOT_ADD_MEMBER_PROPERTIES_EVER)) {
            // добаваление сотрудника, изменившего документ в участники
            final String userName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIER);
            final LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(getGrantAccess());

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DocumentMembersService.PROP_MEMBER_GROUP, pgGranting.getName());
//			TODO: Метод addMemberWithoutCheckPermission использует метод getMembersFolderRef,
//			который был разделён, поэтому выполним проверку и создадим папку, если надо
//                      думаю, эта папка должна создаваться в addMemberWithoutCheckPermission, т.к. это метод на запись, и папка нужна ему.
//                      а то, и вовсе, при создании документа. более того, она создавалась в onCreate. но пока сделал в addMemberWithoutCheckPermission
//                      он в onCreate вызывается, и всё равно требует RW транзакцию
//			if(documentMembersService.getMembersFolderRef(nodeRef) == null) {
//				logger.warn("Members folder not found, creating...");
//				try {
//					documentMembersService.createMembersFolderRef(nodeRef);
//				} catch (WriteTransactionNeededException ex) {
//					logger.error("Can't crate members folder for document ", nodeRef);
//					throw new RuntimeException(ex);
//				}
//			}
            try {
                //Транзакция должна быть, т.к. onUpdatePolicy
                documentMembersService.addMemberWithoutCheckPermission(nodeRef, orgstructureService.getEmployeeByPerson(userName), props);
            } catch (WriteTransactionNeededException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    private boolean hasDoNotAddMemberUpdatedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after, QName[] notAffectedProperties) {
    	logger.debug("ДОКУМЕНТ. hasDoNotAddMemberUpdatedProperties");
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
    	logger.debug("ДОКУМЕНТ. generateMemberNodeName");
        Object propGroup = nodeService.getProperty(member, DocumentMembersService.PROP_MEMBER_GROUP);
        String groupName = propGroup != null ? (String) propGroup : "";

        NodeRef employee = findNodeByAssociationRef(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
        String propName = employee != null ? (String) nodeService.getProperty(employee, ContentModel.PROP_NAME) : "unnamed";

        return (propName + " " + groupName).trim();
    }

    @Override
    public void beforeDeleteNode(NodeRef member) {
    	logger.debug("ДОКУМЕНТ. beforeDeleteNode");
        try {
            if (nodeService.exists(member)) {
                NodeRef folder = nodeService.getPrimaryParent(member).getParentRef();
                NodeRef docRef = nodeService.getPrimaryParent(folder).getParentRef();

                NodeRef employee = findNodeByAssociationRef(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);

                revokePermission(member, docRef, employee);
            }
        } catch (Throwable ex) {
            logger.error("Ошибка при удалении сотрудника! Права отобраны не были!", ex);
        }
    }

    private void revokePermission(NodeRef member, NodeRef docRef, NodeRef employee) {
    	logger.debug("ДОКУМЕНТ. revokePermission");
        LecmPermissionGroup pgRevoking = null;
        String permGroup = (String) nodeService.getProperty(member, DocumentMembersService.PROP_MEMBER_GROUP);
        if (permGroup != null && !permGroup.isEmpty()) {
            pgRevoking = lecmPermissionService.findPermissionGroup(permGroup);
        }
        if (pgRevoking == null) {
            pgRevoking = lecmPermissionService.findPermissionGroup(this.getGrantAccess());
        }
        lecmPermissionService.revokeAccess(pgRevoking, docRef, employee);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
