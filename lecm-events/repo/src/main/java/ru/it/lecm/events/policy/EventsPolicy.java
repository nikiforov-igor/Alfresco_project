package ru.it.lecm.events.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin
 * Date: 01.04.2015
 * Time: 9:50
 */
public class EventsPolicy extends BaseBean {
    private PolicyComponent policyComponent;
    private DocumentTableService documentTableService;
    private LecmPermissionService lecmPermissionService;
    private EventsService eventService;
    private NotificationsService notificationsService;
    private DocumentService documentService;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setEventService(EventsService eventService) {
        this.eventService = eventService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public final void init() {
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_MEMBERS,
                new JavaBehaviour(this, "onCreateAddMembers", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_RESOURCES,
                new JavaBehaviour(this, "onCreateAddResources", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                EventsService.TYPE_EVENT,
                new JavaBehaviour(this, "onCreateEvent", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    public void onCreateAddMembers(AssociationRef nodeAssocRef) {
        //Мероприятие
        NodeRef event = nodeAssocRef.getSourceRef();
        //Участник
        NodeRef member = nodeAssocRef.getTargetRef();

        lecmPermissionService.grantDynamicRole("EVENTS_MEMBER_DYN", event, member.getId(), lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader));
        //Отправка уведомления
        NodeRef initiator = eventService.getEventInitiator(event);
        if (initiator != null) {
            String author = AuthenticationUtil.getSystemUserName();
            String employeeName = (String) nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
            Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
            String text = employeeName + " приглашает на мероприятие " + documentService.wrapAsDocumentLink(event) + ". Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
            List<NodeRef> recipients = new ArrayList<>();
            recipients.add(member);
            notificationsService.sendNotification(author, event, text, recipients, null);
        }

        NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
        if (tableDataRootFolder != null) {
            Set<QName> typeSet = new HashSet<>(1);
            typeSet.add(EventsService.TYPE_EVENT_MEMBERS_TABLE);
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
            if (childAssocs != null && childAssocs.size() == 1) {
                NodeRef table = childAssocs.get(0).getChildRef();
                if (table != null) {
                    // создаем строку
                    try {
                        NodeRef createdNode = createNode(table, EventsService.TYPE_EVENT_MEMBERS_TABLE_ROW, null, null);
                        if (createdNode != null) {
                            nodeService.createAssociation(createdNode, member, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE);
                        }
                    } catch (WriteTransactionNeededException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    public void onCreateAddResources(AssociationRef nodeAssocRef) {
        //Мероприятие
        NodeRef event = nodeAssocRef.getSourceRef();
        //Участник
        NodeRef resource = nodeAssocRef.getTargetRef();

        List<NodeRef> responsible = eventService.getResourceResponsible(resource);
        if (responsible != null) {
            for (NodeRef employee: responsible) {
                lecmPermissionService.grantDynamicRole("EVENTS_RESPONSIBLE_FOR_RESOURCES_DYN", event, employee.getId(), lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader));

                Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);

                //Отправка уведомления
                String author = AuthenticationUtil.getSystemUserName();
                String text = "Запланированное " + documentService.wrapAsDocumentLink(event) + " требует привлечения ресурсов за которые вы ответственны. Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
                List<NodeRef> recipients = new ArrayList<>();
                recipients.add(employee);
                notificationsService.sendNotification(author, event, text, recipients, null);
            }
        }

        NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
        if (tableDataRootFolder != null) {
            Set<QName> typeSet = new HashSet<>(1);
            typeSet.add(EventsService.TYPE_EVENT_RESOURCES_TABLE);
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
            if (childAssocs != null && childAssocs.size() == 1) {
                NodeRef table = childAssocs.get(0).getChildRef();
                if (table != null) {
                    // создаем строку
                    try {
                        NodeRef createdNode = createNode(table, EventsService.TYPE_EVENT_RESOURCES_TABLE_ROW, null, null);
                        if (createdNode != null) {
                            nodeService.createAssociation(createdNode, resource, EventsService.ASSOC_EVENT_RESOURCES_TABLE_RESOURCE);
                        }
                    } catch (WriteTransactionNeededException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    public void onCreateEvent(ChildAssociationRef childAssocRef) {

    }
}
