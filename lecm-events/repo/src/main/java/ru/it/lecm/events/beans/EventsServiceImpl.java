package ru.it.lecm.events.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin
 * Date: 25.03.2015
 * Time: 14:44
 */
public class EventsServiceImpl extends BaseBean implements EventsService {
    private final static Logger logger = LoggerFactory.getLogger(EventsServiceImpl.class);

    private DictionaryBean dictionaryBean;
    private OrgstructureBean orgstructureBean;
    private SearchService searchService;
    private IWorkCalendar workCalendarService;
    private DocumentTableService documentTableService;
    private LecmPermissionService lecmPermissionService;
    private NotificationsService notificationsService;

    private TemplateService templateService;
    private JavaMailSender mailService;
    private ContentService contentService;
    private String defaultFromEmail;

    final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private static final String INVITED_MEMBERS_UPDATE_EVENT_MESSAGE_TEMPLATE = "/alfresco/templates/webscripts/ru/it/lecm/events/invited-members-update-event-message-content.ftl";

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    public void setMailService(JavaMailSender mailService) {
        this.mailService = mailService;
    }

    public void setDefaultFromEmail(String defaultFromEmail) {
        this.defaultFromEmail = defaultFromEmail;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(EVENTS_ROOT_ID);
    }

    @Override
    public NodeRef getEventLocation(NodeRef event) {
        return findNodeByAssociationRef(event, ASSOC_EVENT_LOCATION, TYPE_EVENT_LOCATION, ASSOCIATION_TYPE.TARGET);
    }

    public List<NodeRef> getEventMembers(NodeRef event) {
        List<NodeRef> results = new ArrayList<>();

        NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
        if (tableDataRootFolder != null) {
            Set<QName> typeSet = new HashSet<>(1);
            typeSet.add(EventsService.TYPE_EVENT_MEMBERS_TABLE);
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
            if (childAssocs != null && childAssocs.size() == 1) {
                NodeRef table = childAssocs.get(0).getChildRef();
                if (table != null) {
                    List<NodeRef> rows = documentTableService.getTableDataRows(table);
                    if (rows != null) {
                        for (NodeRef row : rows) {
                            NodeRef rowEmployee = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
                            if (rowEmployee != null) {
                                results.add(rowEmployee);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    public List<NodeRef> getEventResources(NodeRef event) {
        List<NodeRef> results = new ArrayList<>();

        NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
        if (tableDataRootFolder != null) {
            Set<QName> typeSet = new HashSet<>(1);
            typeSet.add(EventsService.TYPE_EVENT_RESOURCES_TABLE);
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
            if (childAssocs != null && childAssocs.size() == 1) {
                NodeRef table = childAssocs.get(0).getChildRef();
                if (table != null) {
                    List<NodeRef> rows = documentTableService.getTableDataRows(table);
                    if (rows != null) {
                        for (NodeRef row : rows) {
                            NodeRef rowResource = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_RESOURCES_TABLE_RESOURCE, null, ASSOCIATION_TYPE.TARGET);
                            if (rowResource != null) {
                                results.add(rowResource);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    public List<NodeRef> getEventInvitedMembers(NodeRef event) {
        return findNodesByAssociationRef(event, ASSOC_EVENT_INVITED_MEMBERS, null, ASSOCIATION_TYPE.TARGET);
    }

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setWorkCalendarService(IWorkCalendar workCalendarService) {
        this.workCalendarService = workCalendarService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    public List<NodeRef> getEvents(String fromDate, String toDate) {
        return getEvents(fromDate, toDate, "");
    }

    public List<NodeRef> getEvents(String fromDate, String toDate, String additionalFilter) {
        List<NodeRef> results = new ArrayList<>();

        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
//        sp.addSort("@" + ContentModel.PROP_NAME, true);
        String query = "TYPE:\"lecm-events:document\" AND @lecm\\-events\\:from\\-date:[MIN TO " + toDate + "] AND @lecm\\-events\\:to\\-date:[" + fromDate + " TO MAX] AND @lecm\\-events\\:removed: false " + additionalFilter;
        sp.setQuery(query);

        ResultSet searchResult = null;
        try {
            searchResult = searchService.query(sp);
            for (ResultSetRow row : searchResult) {
                results.add(row.getNodeRef());
            }
        } finally {
            if (searchResult != null) {
                searchResult.close();
            }
        }

        return results;
    }

    @Override
    public List<NodeRef> getAvailableUserLocations() {
        List<NodeRef> results = null;
        NodeRef locationsDic = dictionaryBean.getDictionaryByName("Места проведения мероприятий");
        if (locationsDic != null) {
            results = new ArrayList<>();

            List<NodeRef> locations = dictionaryBean.getChildren(locationsDic);
            NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();
            int currentUserLocationPrivilegeLevel = getCurrentUserLocationPrivilegeLevel();

            for (NodeRef location : locations) {
                NodeRef locationOrganization = findNodeByAssociationRef(location, ASSOC_EVENT_LOCATION_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
                Integer locationPrivilegeLevel = (Integer) nodeService.getProperty(location, PROP_EVENT_LOCATION_PRIVILEGE_LEVEL);

                if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(locationOrganization) &&
                        (locationPrivilegeLevel == 0 || currentUserLocationPrivilegeLevel >= locationPrivilegeLevel)) {
                    results.add(location);
                }
            }
        }
        return results;
    }

    @Override
    public List<NodeRef> getAvailableUserResources() {
        List<NodeRef> results = null;
        NodeRef resourcesDic = dictionaryBean.getDictionaryByName("Ресурсы");
        if (resourcesDic != null) {
            results = new ArrayList<>();

            List<NodeRef> resources = dictionaryBean.getChildren(resourcesDic);
            NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();
            int currentUserResourcesPrivilegeLevel = getCurrentUserResourcesPrivilegeLevel();

            for (NodeRef resource : resources) {
                if ((Boolean) nodeService.getProperty(resource, PROP_EVENT_RESOURCE_AVAILABLE)) {
                    NodeRef resourceOrganization = findNodeByAssociationRef(resource, ASSOC_EVENT_RESOURCE_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
                    Integer resourcePrivilegeLevel = (Integer) nodeService.getProperty(resource, PROP_EVENT_RESOURCE_PRIVILEGE_LEVEL);

                    if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(resourceOrganization) &&
                            (resourcePrivilegeLevel == 0 || currentUserResourcesPrivilegeLevel >= resourcePrivilegeLevel)) {
                        results.add(resource);
                    }
                }
            }
        }
        return results;
    }

    private NodeRef getCurrentEmployeeOrganization() {
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        if (currentEmployee != null) {
            return orgstructureBean.getEmployeeOrganization(currentEmployee);
        }
        return null;
    }

    private int getCurrentUserLocationPrivilegeLevel() {
        return getCurrentUserDicPrivilegeLevel("Уровни привилегий для выбора мест проведения", ASSOC_EVENT_LOCATION_PL_ROLE, PROP_EVENT_LOCATION_PL_LEVEL);
    }

    private int getCurrentUserResourcesPrivilegeLevel() {
        return getCurrentUserDicPrivilegeLevel("Уровни привилегий для выбора ресурсов", ASSOC_EVENT_RESOURCES_PL_ROLE, PROP_EVENT_RESOURCES_PL_LEVEL);
    }

    private int getCurrentUserDicPrivilegeLevel(String dicName, QName dicRoleAssoc, QName dicLevelProp) {
        int result = 0;
        NodeRef privilegeLevelDic = dictionaryBean.getDictionaryByName(dicName);
        if (privilegeLevelDic != null) {
            List<NodeRef> privilegeLevels = dictionaryBean.getChildren(privilegeLevelDic);
            for (NodeRef privilegeLevel : privilegeLevels) {
                NodeRef role = findNodeByAssociationRef(privilegeLevel, dicRoleAssoc, null, ASSOCIATION_TYPE.TARGET);
                if (role != null) {
                    String roleId = orgstructureBean.getBusinessRoleIdentifier(role);
                    if (roleId != null && orgstructureBean.isCurrentEmployeeHasBusinessRole(roleId)) {
                        Integer level = (Integer) nodeService.getProperty(privilegeLevel, dicLevelProp);
                        if (level != null && level > result) {
                            result = level;
                        }
                    }
                }
            }
        }
        return result;
    }

    public boolean checkLocationAvailable(NodeRef location, Date fromDate, Date toDate, boolean allDay) {
        return checkLocationAvailable(location, null, fromDate, toDate, allDay);
    }

    public boolean checkLocationAvailable(NodeRef location, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay) {
        if (allDay) {
            Calendar fromDateCal = Calendar.getInstance();
            fromDateCal.setTime(fromDate);
            fromDateCal.set(Calendar.HOUR_OF_DAY, 0);
            fromDateCal.set(Calendar.MINUTE, 1);
            fromDateCal.set(Calendar.SECOND, 0);
            fromDateCal.set(Calendar.MILLISECOND, 0);
            fromDate = fromDateCal.getTime();

            Calendar toDateCal = Calendar.getInstance();
            toDateCal.setTime(toDate);
            toDateCal.set(Calendar.HOUR_OF_DAY, 23);
            toDateCal.set(Calendar.MINUTE, 59);
            toDateCal.set(Calendar.SECOND, 0);
            toDateCal.set(Calendar.MILLISECOND, 0);
            toDate = toDateCal.getTime();
        }

        String additionalFilter = " AND @lecm\\-events\\:location\\-assoc\\-ref:\"" + location.toString() + "\"";
        if (ignoreNode != null) {
            additionalFilter += " AND NOT ID:\"" + ignoreNode.toString() + "\"";
        }

        List<NodeRef> events = getEvents(DateFormatISO8601.format(fromDate), DateFormatISO8601.format(toDate), additionalFilter);

        return events == null || events.size() == 0;
    }

    public boolean checkMemberAvailable(NodeRef member, Date fromDate, Date toDate, boolean allDay) {
        return checkMemberAvailable(member, null, fromDate, toDate, allDay);
    }

    public boolean checkMemberAvailable(NodeRef member, NodeRef ignoreNode, Date fromDate, Date toDate, boolean allDay) {
        List<Date> employeeWorkindDays = workCalendarService.getEmployeeWorkindDays(member, fromDate, toDate);
        if (employeeWorkindDays.size() == 0) {
            return false;
        } else {
            if (allDay) {
                Calendar fromDateCal = Calendar.getInstance();
                fromDateCal.setTime(fromDate);
                fromDateCal.set(Calendar.HOUR_OF_DAY, 0);
                fromDateCal.set(Calendar.MINUTE, 1);
                fromDateCal.set(Calendar.SECOND, 0);
                fromDateCal.set(Calendar.MILLISECOND, 0);
                fromDate = fromDateCal.getTime();

                Calendar toDateCal = Calendar.getInstance();
                toDateCal.setTime(toDate);
                toDateCal.set(Calendar.HOUR_OF_DAY, 23);
                toDateCal.set(Calendar.MINUTE, 59);
                toDateCal.set(Calendar.SECOND, 0);
                toDateCal.set(Calendar.MILLISECOND, 0);
                toDate = toDateCal.getTime();
            }

            String additionalFilter = " AND @lecm\\-events\\:temp\\-members\\-assoc\\-ref:\"" + member.toString() + "\"";
            if (ignoreNode != null) {
                additionalFilter += " AND NOT ID:\"" + ignoreNode.toString() + "\"";
            }

            List<NodeRef> events = getEvents(DateFormatISO8601.format(fromDate), DateFormatISO8601.format(toDate), additionalFilter);

            return events == null || events.size() == 0;
        }
    }

    public List<NodeRef> getResourceResponsible(NodeRef resource) {
        return findNodesByAssociationRef(resource, ASSOC_EVENT_RESOURCE_RESPONSIBLE, null, ASSOCIATION_TYPE.TARGET);
    }

    public NodeRef getEventInitiator(NodeRef event) {
        return findNodeByAssociationRef(event, ASSOC_EVENT_INITIATOR, null, ASSOCIATION_TYPE.TARGET);
    }

    public NodeRef getResourcesTable(NodeRef event) {
        return documentTableService.getTable(event, TYPE_EVENT_RESOURCES_TABLE);
    }

    public NodeRef getMemberTable(NodeRef event) {
        return documentTableService.getTable(event, TYPE_EVENT_MEMBERS_TABLE);
    }

    public NodeRef getResourceTableRow(NodeRef event, NodeRef employee) {
        NodeRef table = getResourcesTable(event);
        if (table != null) {
            List<NodeRef> rows = documentTableService.getTableDataRows(table);
            if (rows != null) {
                for (NodeRef row : rows) {
                    NodeRef rowEmployee = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_RESOURCES_TABLE_RESOURCE, null, ASSOCIATION_TYPE.TARGET);
                    if (rowEmployee.equals(employee)) {
                        return row;
                    }
                }
            }
        }
        return null;
    }

    public NodeRef getMemberTableRow(NodeRef event, NodeRef employee) {
        NodeRef table = getMemberTable(event);
        if (table != null) {
            List<NodeRef> rows = documentTableService.getTableDataRows(table);
            if (rows != null) {
                for (NodeRef row : rows) {
                    NodeRef rowEmployee = findNodeByAssociationRef(row, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
                    if (rowEmployee.equals(employee)) {
                        return row;
                    }
                }
            }
        }
        return null;
    }

    public String getCurrentEmployeeMemberStatus(NodeRef event) {
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        if (currentEmployee != null) {
            NodeRef memberTableRow = getMemberTableRow(event, currentEmployee);
            if (memberTableRow != null) {
                return (String) nodeService.getProperty(memberTableRow, PROP_EVENT_MEMBERS_STATUS);
            }
        }
        return null;
    }

    public String wrapAsEventLink(NodeRef documentRef) {
        return wrapperLink(documentRef, (String) nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING), EVENT_LINK_URL);
    }

    public void onAfterUpdate(NodeRef event, String updateRepeated) {
        updateMembers(event);
        sendNotificationsToInvitedMembers(event);

        Boolean repeatable = (Boolean) nodeService.getProperty(event, EventsService.PROP_EVENT_REPEATABLE);
        if (repeatable != null && repeatable && updateRepeated != null) {
            updateRepeatedEvents(event, updateRepeated);
        }
    }

    private void updateMembers(NodeRef event) {
        List<NodeRef> newMembers = getEventMembers(event);
        List<NodeRef> oldMembers = findNodesByAssociationRef(event, ASSOC_EVENT_OLD_MEMBERS, null, ASSOCIATION_TYPE.TARGET);

        List<NodeRef> oldAndNewMembers = new ArrayList<>();
        List<NodeRef> onlyOldMembers = new ArrayList<>();
        List<NodeRef> onlyNewMembers;
        for (NodeRef oldMember : oldMembers) {
            if (newMembers.contains(oldMember)) {
                oldAndNewMembers.add(oldMember);
            } else {
                onlyOldMembers.add(oldMember);
            }
        }

        newMembers.removeAll(oldAndNewMembers);
        onlyNewMembers = newMembers;

        NodeRef initiator = getEventInitiator(event);
        Date fromDate = (Date) nodeService.getProperty(event, EventsService.PROP_EVENT_FROM_DATE);
        if (initiator != null && fromDate != null) {
            String author = AuthenticationUtil.getSystemUserName();
            String employeeName = (String) nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);

            for (NodeRef member : oldAndNewMembers) {
                String text = employeeName + " обновил информацию по мероприятию " + wrapAsEventLink(event) + ". Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
                List<NodeRef> recipients = new ArrayList<>();
                recipients.add(member);
                notificationsService.sendNotification(author, event, text, recipients, null);
            }

            for (NodeRef member : onlyNewMembers) {
                lecmPermissionService.grantDynamicRole("EVENTS_MEMBER_DYN", event, member.getId(), lecmPermissionService.findPermissionGroup("LECM_BASIC_PG_ActionPerformer"));

                String text = employeeName + " пригласил вас на мероприятие " + wrapAsEventLink(event) + ". Начало: " + dateFormat.format(fromDate) + ", в " + timeFormat.format(fromDate);
                List<NodeRef> recipients = new ArrayList<>();
                recipients.add(member);
                notificationsService.sendNotification(author, event, text, recipients, null);
            }

            for (NodeRef member : onlyOldMembers) {
                lecmPermissionService.revokeDynamicRole("EVENTS_MEMBER_DYN", event, member.getId());
                lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader), event, member);

                String text = "Вам не требуется присутствовать на мероприятии " + wrapAsEventLink(event);
                List<NodeRef> recipients = new ArrayList<>();
                recipients.add(member);
                notificationsService.sendNotification(author, event, text, recipients, null, true);
            }
        }


        nodeService.setAssociations(event, EventsService.ASSOC_EVENT_OLD_MEMBERS, getEventMembers(event));
    }

    private void sendNotificationsToInvitedMembers(NodeRef event) {
        List<NodeRef> invitedMembers = getEventInvitedMembers(event);
        for (NodeRef representative : invitedMembers) {
            String email = (String) nodeService.getProperty(representative, Contractors.PROP_REPRESENTATIVE_EMAIL);
            if (email != null && email.length() > 0) {

                try {
                    Map<String, Object> mailTemplateModel = new HashMap<>();

                    mailTemplateModel.put("title", nodeService.getProperty(event, EventsService.PROP_EVENT_TITLE));
                    mailTemplateModel.put("description", nodeService.getProperty(event, EventsService.PROP_EVENT_DESCRIPTION));
                    NodeRef initiator = getEventInitiator(event);
                    if (initiator != null) {
                        mailTemplateModel.put("initiator", nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME));
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

                    NodeRef location = getEventLocation(event);
                    if (location != null) {
                        mailTemplateModel.put("location", nodeService.getProperty(location, EventsService.PROP_EVENT_LOCATION_ADDRESS));
                    }

                    String mailText = templateService.processTemplate(INVITED_MEMBERS_UPDATE_EVENT_MESSAGE_TEMPLATE, mailTemplateModel);

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
                } catch (Exception e) {
                    logger.error("Error send mail", e);
                }
            }
        }
    }

    public List<NodeRef> getNextRepeatedEvents(NodeRef event) {
        List<NodeRef> results = new ArrayList<>();
        NodeRef nextEvent = findNodeByAssociationRef(event, ASSOC_NEXT_REPEATED_EVENT, TYPE_EVENT, ASSOCIATION_TYPE.TARGET);
        while (nextEvent != null && !nextEvent.equals(event)) {
            results.add(nextEvent);
            nextEvent = findNodeByAssociationRef(nextEvent, ASSOC_NEXT_REPEATED_EVENT, TYPE_EVENT, ASSOCIATION_TYPE.TARGET);
        }
        return results;
    }

    public List<NodeRef> getPrevRepeatedEvents(NodeRef event) {
        List<NodeRef> results = new ArrayList<>();
        NodeRef nextEvent = findNodeByAssociationRef(event, ASSOC_NEXT_REPEATED_EVENT, TYPE_EVENT, ASSOCIATION_TYPE.SOURCE);
        while (nextEvent != null && !nextEvent.equals(event)) {
            results.add(nextEvent);
            nextEvent = findNodeByAssociationRef(nextEvent, ASSOC_NEXT_REPEATED_EVENT, TYPE_EVENT, ASSOCIATION_TYPE.SOURCE);
        }
        return results;
    }

    public List<NodeRef> getAllRepeatedEvents(NodeRef event) {
        List<NodeRef> results = new ArrayList<>();
        results.addAll(getNextRepeatedEvents(event));
        results.addAll(getPrevRepeatedEvents(event));
        return results;
    }

    private void updateRepeatedEvents(NodeRef event, String updateRepeated) {
        List<NodeRef> updateEvents = new ArrayList<>();
        if ("ALL".equals(updateRepeated)) {
            updateEvents = getAllRepeatedEvents(event);
        } else if ("ALL_NEXT".equals(updateRepeated)) {
            updateEvents = getNextRepeatedEvents(event);
        } else if ("ALL_PREV".equals(updateRepeated)) {
            updateEvents = getPrevRepeatedEvents(event);
        }
        for (NodeRef repeatedEvent: updateEvents) {
            updateRepeatedEvent(event, repeatedEvent);
        }
    }

    private void updateRepeatedEvent(NodeRef event, NodeRef repeatedEvent) {
        // копируем свойства
        Map<QName, Serializable> oldProperties = nodeService.getProperties(event);
        Map<QName, Serializable> newProperties = nodeService.getProperties(repeatedEvent);
        List<QName> propertiesToCopy = new ArrayList<>();

        propertiesToCopy.add(EventsService.PROP_EVENT_TITLE);
        propertiesToCopy.add(EventsService.PROP_EVENT_DESCRIPTION);

        propertiesToCopy.add(PROP_EVENT_ALL_DAY);

        Calendar calOldFrom = Calendar.getInstance();
        calOldFrom.setTime((Date) oldProperties.get(PROP_EVENT_FROM_DATE));
        Calendar calNewFrom = Calendar.getInstance();
        calNewFrom.setTime((Date) newProperties.get(PROP_EVENT_FROM_DATE));
        calNewFrom.set(Calendar.HOUR_OF_DAY, calOldFrom.get(Calendar.HOUR_OF_DAY));
        calNewFrom.set(Calendar.MINUTE, calOldFrom.get(Calendar.MINUTE));
        newProperties.put(PROP_EVENT_FROM_DATE, calNewFrom.getTime());

        Calendar calOldTo = Calendar.getInstance();
        calOldTo.setTime((Date) oldProperties.get(PROP_EVENT_TO_DATE));
        Calendar calNewTo = Calendar.getInstance();
        calNewTo.setTime((Date) newProperties.get(PROP_EVENT_TO_DATE));
        calNewTo.set(Calendar.HOUR_OF_DAY, calOldTo.get(Calendar.HOUR_OF_DAY));
        calNewTo.set(Calendar.MINUTE, calOldTo.get(Calendar.MINUTE));
        newProperties.put(PROP_EVENT_TO_DATE, calNewTo.getTime());

        for (QName propName : propertiesToCopy) {
            newProperties.put(propName, oldProperties.get(propName));
        }
        nodeService.setProperties(repeatedEvent, newProperties);

        //Копируем ассоциации
        List<QName> assocsToCopy = new ArrayList<>();
        assocsToCopy.add(EventsService.ASSOC_EVENT_LOCATION);
        assocsToCopy.add(EventsService.ASSOC_EVENT_INITIATOR);
        assocsToCopy.add(EventsService.ASSOC_EVENT_INVITED_MEMBERS);
        for (QName assocQName : assocsToCopy) {
            List<NodeRef> targets = findNodesByAssociationRef(event, assocQName, null, ASSOCIATION_TYPE.TARGET);
            nodeService.setAssociations(repeatedEvent, assocQName, targets);
        }

        //Обновляем участников
        List<NodeRef> eventMembers = getEventMembers(event);
        List<NodeRef> repeatedEventMembers = getEventMembers(repeatedEvent);
        for(NodeRef member: eventMembers) {
            if (!repeatedEventMembers.contains(member)) {
                nodeService.createAssociation(repeatedEvent, member, ASSOC_EVENT_TEMP_MEMBERS);
            }
        }
        NodeRef membersTable = getMemberTable(repeatedEvent);
        if (membersTable != null) {
            for(NodeRef member: repeatedEventMembers) {
                if (!eventMembers.contains(member)) {
                    NodeRef memberTableRow = getMemberTableRow(repeatedEvent, member);
                    if (memberTableRow != null) {
                        nodeService.removeChild(membersTable, memberTableRow);
                    }
                }
            }
        }

        //Обновляем ресурсы
        List<NodeRef> eventResources = getEventResources(event);
        List<NodeRef> repeatedEventResources = getEventResources(repeatedEvent);
        for(NodeRef resource: eventResources) {
            if (!repeatedEventResources.contains(resource)) {
                nodeService.createAssociation(repeatedEvent, resource, ASSOC_EVENT_TEMP_RESOURCES);
            }
        }
        NodeRef resourcesTable = getResourcesTable(repeatedEvent);
        if (resourcesTable != null) {
            for(NodeRef resource: repeatedEventResources) {
                if (!eventResources.contains(resource)) {
                    NodeRef resourceTableRow = getResourceTableRow(repeatedEvent, resource);
                    if (resourceTableRow != null) {
                        nodeService.removeChild(resourcesTable, resourceTableRow);
                    }
                }
            }
        }

        onAfterUpdate(repeatedEvent, null);
    }
}
