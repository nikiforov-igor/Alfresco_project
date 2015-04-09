package ru.it.lecm.events.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentConnectionService;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

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
    private DocumentConnectionService documentConnectionService;
    private TemplateService templateService;
    private JavaMailSender mailService;
    private OrgstructureBean orgstructureBean;
    private ContentService contentService;
    private ThreadPoolExecutor threadPoolExecutor;
    private String defaultFromEmail;
    private TransactionListener transactionListener;

    private static final String EVENTS_TRANSACTION_LISTENER = "events_transaction_listaner";

    private static final String INVITED_MEMBERS_MESSAGE_TEMPLATE = "/alfresco/templates/webscripts/ru/it/lecm/events/invited-members-message-content.ftl";

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private final static String WEEK_DAYS = "week-days";
    private final static String MONTH_DAYS = "month-days";

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

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void setDefaultFromEmail(String defaultFromEmail) {
        this.defaultFromEmail = defaultFromEmail;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public final void init() {
        transactionListener = new EventPolicyTransactionListener();

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

        lecmPermissionService.grantDynamicRole("EVENTS_MEMBER_DYN", event, member.getId(), lecmPermissionService.findPermissionGroup("LECM_BASIC_PG_ActionPerformer"));
        //Отправка уведомления
        NodeRef initiator = eventService.getEventInitiator(event);
        if (initiator != null) {
            String author = AuthenticationUtil.getSystemUserName();
            String employeeName = (String) nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
            Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
            String text = employeeName + " приглашает на мероприятие " + eventService.wrapAsEventLink(event) + ". Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
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
            for (NodeRef employee : responsible) {
                lecmPermissionService.grantDynamicRole("EVENTS_RESPONSIBLE_FOR_RESOURCES_DYN", event, employee.getId(), lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader));

                Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);

                //Отправка уведомления
                String author = AuthenticationUtil.getSystemUserName();
                String text = "Запланированное " + eventService.wrapAsEventLink(event) + " требует привлечения ресурсов за которые вы ответственны. Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
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
                    for (AssociationRef attachment : attachmentsAssocs) {
                        attachments.add(attachment.getTargetRef());
                    }
                }
                for (final NodeRef attachment : attachments) {
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
        NodeRef event = childAssocRef.getChildRef();

        AlfrescoTransactionSupport.bindListener(this.transactionListener);

        List<NodeRef> pendingActions = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
        if (pendingActions == null) {
            pendingActions = new ArrayList<>();
            AlfrescoTransactionSupport.bindResource(EVENTS_TRANSACTION_LISTENER, pendingActions);
        }

        // Check that action has only been added to the list once
        Boolean repeatable = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE);
        if (repeatable != null && repeatable && !pendingActions.contains(event)) {
            pendingActions.add(event);
        }
    }

    private class EventPolicyTransactionListener implements TransactionListener {

        @Override
        public void flush() {

        }

        @Override
        public void beforeCommit(boolean readOnly) {

        }

        @Override
        public void beforeCompletion() {

        }

        @Override
        public void afterCommit() {
            List<NodeRef> pendingDocs = AlfrescoTransactionSupport.getResource(EVENTS_TRANSACTION_LISTENER);
            if (pendingDocs != null) {
                while (!pendingDocs.isEmpty()) {
                    final NodeRef event = pendingDocs.remove(0);
                    final String ruleContent = (String) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE_RULE);
                    final Date startPeriod = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE_START_PERIOD);
                    final Date endPeriod = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE_END_PERIOD);

                    if (ruleContent != null && startPeriod != null && endPeriod != null) {
                        try {
                            JSONObject rule = new JSONObject(ruleContent);
                            String type = rule.getString("type");
                            JSONArray data = rule.getJSONArray("data");

                            List<Integer> weekDays = new ArrayList<>();
                            List<Integer> monthDays = new ArrayList<>();

                            if (type.equals(WEEK_DAYS)) {
                                for (int i = 0; i < data.length(); i++) {
                                    if (data.getInt(i) == 7) {
                                        weekDays.add(Calendar.SUNDAY);
                                    } else {
                                        weekDays.add(data.getInt(i) + 1);
                                    }
                                }
                            } else if (type.equals(MONTH_DAYS)) {
                                for (int i = 0; i < data.length(); i++) {
                                    monthDays.add(data.getInt(i));
                                }
                            }

                            final Calendar calStart = Calendar.getInstance();
                            calStart.setTime(startPeriod);
                            Calendar calEnd = Calendar.getInstance();
                            calEnd.setTime(endPeriod);

                            while (calStart.before(calEnd)) {
                                int weekDay = calStart.get(Calendar.DAY_OF_WEEK);
                                int monthDay = calStart.get(Calendar.DAY_OF_MONTH);
                                if (weekDays.contains(weekDay) || monthDays.contains(monthDay)) {

                                    transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                        @Override
                                        public Void execute() throws Throwable {
                                            QName docType = nodeService.getType(event);
                                            NodeRef parentRef = nodeService.getPrimaryParent(event).getParentRef();
                                            QName assocQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
                                            Map<QName, Serializable> properties = copyProperties(event, calStart.getTime());

                                            if (properties != null) {
                                                // создаем ноду
                                                ChildAssociationRef createdNodeAssoc = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQname, docType, properties);
                                                copyAssocs(event, createdNodeAssoc.getChildRef());

                                                nodeService.createAssociation(event, createdNodeAssoc.getChildRef(), EventsService.ASSOC_EVENT_REPEATED_EVENTS);

                                                documentConnectionService.createConnection(event, createdNodeAssoc.getChildRef(), "hasRepeated", true);
                                            }

                                            return null;
                                        }
                                    }, false, true);
                                }

                                calStart.add(Calendar.DAY_OF_YEAR, 1);
                            }
                        } catch (JSONException e) {
                            logger.error("Error parse repeatable rule", e);
                        }
                    }
                }
            }
        }

        public Map<QName, Serializable> copyProperties(NodeRef event, Date newStartDate) {
            Map<QName, Serializable> oldProperties = nodeService.getProperties(event);
            Map<QName, Serializable> newProperties = new HashMap<>();

            Date dateFrom = (Date) oldProperties.get(EventsService.PROP_EVENT_FROM_DATE);
            int dayCount = daysBetween(dateFrom, newStartDate);
            if (dayCount != 0) {
                // копируем свойства
                List<QName> propertiesToCopy = new ArrayList<>();
                propertiesToCopy.add(EventsService.PROP_EVENT_TITLE);
                propertiesToCopy.add(EventsService.PROP_EVENT_ALL_DAY);
                propertiesToCopy.add(EventsService.PROP_EVENT_DESCRIPTION);

                for (QName propName : propertiesToCopy) {
                    newProperties.put(propName, oldProperties.get(propName));
                }

                Calendar calFrom = Calendar.getInstance();
                calFrom.setTime(dateFrom);
                calFrom.add(Calendar.DAY_OF_YEAR, dayCount);
                newProperties.put(EventsService.PROP_EVENT_FROM_DATE, calFrom.getTime());

                Calendar calTo = Calendar.getInstance();
                calTo.setTime((Date) oldProperties.get(EventsService.PROP_EVENT_TO_DATE));
                calTo.add(Calendar.DAY_OF_YEAR, dayCount);
                newProperties.put(EventsService.PROP_EVENT_TO_DATE, calTo.getTime());

                newProperties.put(EventsService.PROP_EVENT_IS_REPEATED, true);

                return newProperties;
            } else {
                return null;
            }
        }

        public void copyAssocs(NodeRef oldEvent, NodeRef newEvent) {
            List<QName> assocsToCopy = new ArrayList<>();
            assocsToCopy.add(EventsService.ASSOC_EVENT_INITIATOR);
            assocsToCopy.add(EventsService.ASSOC_EVENT_LOCATION);
            assocsToCopy.add(EventsService.ASSOC_EVENT_INVITED_MEMBERS);
            assocsToCopy.add(EventsService.ASSOC_EVENT_TEMP_MEMBERS);
            assocsToCopy.add(EventsService.ASSOC_EVENT_TEMP_RESOURCES);

            for (QName assocQName : assocsToCopy) {
                List<NodeRef> targets = findNodesByAssociationRef(oldEvent, assocQName, null, ASSOCIATION_TYPE.TARGET);
                nodeService.setAssociations(newEvent, assocQName, targets);
            }
        }

        private int daysBetween(Date d1, Date d2){
            return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
        }

        @Override
        public void afterRollback() {

        }
    }
}
