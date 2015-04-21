package ru.it.lecm.events.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.events.beans.EventsServiceImpl;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 21.01.14
 * Time: 13:18
 */
public class EventNotificationExecutor extends ActionExecuterAbstractBase {

    private final static Logger logger = LoggerFactory.getLogger(EventNotificationExecutor.class);

    private NotificationsService notificationsService;
    private NodeService nodeService;
    private IWorkCalendar calendarBean;
    private OrgstructureBean orgstructureBean;
    private JavaMailSender mailService;
    private EventsServiceImpl eventService;
    private static SimpleDateFormat notificationDateFormat = new SimpleDateFormat("dd.MM.yyyy, в HH:mm");
    private String defaultFromEmail;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        Date fromDate = (Date) nodeService.getProperty(nodeRef, EventsService.PROP_EVENT_FROM_DATE);
        if (fromDate != null) {
            Notification notification = new Notification();
            String notificationDescription = "Напоминание: Вы приглашены на мероприятие " + eventService.wrapAsEventLink(nodeRef) + ". Начало: " + notificationDateFormat.format(fromDate);
            List<NodeRef> recipients = eventService.getEventMembers(nodeRef);
            notification.setAuthor(AuthenticationUtil.getSystemUserName());
            notification.setRecipientEmployeeRefs(recipients);
            notification.setDescription(notificationDescription);
            notification.setObjectRef(nodeRef);
            notificationsService.sendNotification(notification);

            List<NodeRef> invitedMembers = eventService.getEventInvitedMembers(nodeRef);
            for (NodeRef invitedMember : invitedMembers) {
                String email = (String) nodeService.getProperty(invitedMember, Contractors.PROP_REPRESENTATIVE_EMAIL);
                if (StringUtils.isNotEmpty(email)) {
                    try {
                        MimeMessage message = mailService.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
                        helper.setTo(email);
                        helper.setFrom(defaultFromEmail);
                        helper.setSubject("Напоминание о мероприятии");
                        helper.setText(notificationDescription);
                        mailService.send(message);
                    } catch (Exception e) {
                        logger.error("Error send mail", e);
                    }
                }
            }

        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    public void setMailService(JavaMailSender mailService) {
        this.mailService = mailService;
    }

    public void setEventService(EventsServiceImpl eventService) {
        this.eventService = eventService;
    }

    public void setDefaultFromEmail(String defaultFromEmail) {
        this.defaultFromEmail = defaultFromEmail;
    }
}
