package ru.it.lecm.events.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin
 * Date: 01.04.2015
 * Time: 9:50
 */
public class EventsPolicy extends BaseBean {
    private final static Logger logger = LoggerFactory.getLogger(EventsPolicy.class);

    private PolicyComponent policyComponent;
    private DocumentTableService documentTableService;
    private LecmPermissionService lecmPermissionService;
    private EventsService eventService;
    private NotificationsService notificationsService;
    private DocumentService documentService;
    private TemplateService templateService;
    private JavaMailSender mailService;
    private OrgstructureBean orgstructureBean;
    private DocumentAttachmentsService documentAttachmentsService;
    private ContentService contentService;
    private String defaultFromEmail;

    private static final String INVITED_MEMBERS_MESSAGE_TEMPLATE = "/alfresco/templates/webscripts/ru/it/lecm/events/invited-members-message-content.ftl";

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

    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    public void setMailService(JavaMailSender mailService) {
        this.mailService = mailService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setDefaultFromEmail(String defaultFromEmail) {
        this.defaultFromEmail = defaultFromEmail;
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

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_INVITED_MEMBERS,
                new JavaBehaviour(this, "onCreateInvitedMember", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

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

    public void onCreateInvitedMember(AssociationRef nodeAssocRef) {
        //Мероприятие
        NodeRef event = nodeAssocRef.getSourceRef();
        //Участник
        NodeRef representative = nodeAssocRef.getTargetRef();

        String email = (String) nodeService.getProperty(representative, Contractors.PROP_REPRESENTATIVE_EMAIL);
        if (email != null && email.length() > 0) {

            try {
                Map<String, Object> mailTemplateModel = new HashMap<>();

                mailTemplateModel.put("title", nodeService.getProperty(event, EventsService.PROP_EVENT_TITLE));
                mailTemplateModel.put("description", nodeService.getProperty(event, EventsService.PROP_EVENT_DESCRIPTION));
                NodeRef initiator = eventService.getEventInitiator(event);
                if (initiator != null) {
                    mailTemplateModel.put("initiator", nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME));

                    NodeRef organization = orgstructureBean.getEmployeeOrganization(initiator);
                    if (organization != null) {
                        mailTemplateModel.put("organization", nodeService.getProperty(organization, Contractors.PROP_CONTRACTOR_FULLNAME));
                    }
                }

                Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
                Date toDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_TO_DATE);
                if (fromDate != null && toDate != null) {
                    String fromDateString = dateFormat.format(fromDate);
                    String toDateString = dateFormat.format(toDate);
                    if (fromDateString.equals(toDateString)) {
                        mailTemplateModel.put("date", fromDateString);
                    } else {
                        mailTemplateModel.put("date", " с " + fromDateString + " по " + toDateString);
                    }
                }

                NodeRef location = eventService.getEventLocation(event);
                if (location != null) {
                    mailTemplateModel.put("location", nodeService.getProperty(location, EventsService.PROP_EVENT_LOCATION_ADDRESS));
                }

                String mailText = templateService.processTemplate(INVITED_MEMBERS_MESSAGE_TEMPLATE, mailTemplateModel);

                MimeMessage message = mailService.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(email);
                helper.setFrom(defaultFromEmail);
                helper.setSubject("");
                helper.setText(mailText, true);

                List<NodeRef> attachments = new ArrayList<>();
                List<AssociationRef> attachmentsAssocs = nodeService.getTargetAssocs(event, DocumentService.ASSOC_TEMP_ATTACHMENTS);
                if (attachmentsAssocs != null) {
                    for (AssociationRef attachment: attachmentsAssocs) {
                        attachments.add(attachment.getTargetRef());
                    }
                }
                for (final NodeRef attachment: attachments) {
                    String attachmentName = MimeUtility.encodeText((String) nodeService.getProperty(attachment, ContentModel.PROP_NAME), "UTF-8", null);
                    helper.addAttachment(attachmentName, new DataSource() {
                        public InputStream getInputStream() throws IOException {
                            ContentReader reader = contentService.getReader(attachment, ContentModel.PROP_CONTENT);
                            return reader.getContentInputStream();
                        }
                        public OutputStream getOutputStream() throws IOException {
                            throw new IOException("Read-only data");
                        }
                        public String getContentType() {
                            return contentService.getReader(attachment, ContentModel.PROP_CONTENT).getMimetype();
                        }
                        public String getName() {
                            return nodeService.getProperty(attachment, ContentModel.PROP_NAME).toString();
                        }
                    });
                }

                mailService.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                logger.error("Error send mail", e);
            }
        }
    }

    public void onCreateEvent(ChildAssociationRef childAssocRef) {

    }
}
