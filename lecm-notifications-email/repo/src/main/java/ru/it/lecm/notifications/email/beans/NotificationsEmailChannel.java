package ru.it.lecm.notifications.email.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: AIvkin
 * Date: 17.01.13
 * Time: 11:19
 *
 * Сервис канала уведомлений для электронной почты
 */
public class NotificationsEmailChannel extends NotificationChannelBeanBase {
	private static final transient Logger logger = LoggerFactory.getLogger(NotificationsEmailChannel.class);
	public static final String NOTIFICATIONS_EMAIL_ROOT_NAME = "Email";
	public static final String NOTIFICATIONS_EMAIL_ROOT_ID = "NOTIFICATIONS_EMAIL_ROOT_ID";

	public static final String NOTIFICATIONS_EMAIL_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/email/1.0";
	public final QName TYPE_NOTIFICATION_EMAIL = QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, "notification");
	public final QName PROP_EMAIL = QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, "email");

	protected NotificationsService notificationsService;
	private ActionService actionService;

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 */
	public void init() {
	}

	@Override
	public boolean sendNotification(NotificationUnit notification) {
		String email = (String) nodeService.getProperty(notification.getRecipientRef(), OrgstructureBean.PROP_EMPLOYEE_EMAIL);
		if (email != null) {
			try {
				String body = notification.getBody();
				String subject = notification.getSubject();
				if (StringUtils.isNotEmpty(body)) {
					notification.setDescription(body);
				}
				createNotification(notification, email);

				if (StringUtils.isEmpty(subject)) {
					subject = I18NUtil.getMessage("notifications.email.subject", I18NUtil.getLocale());
				}
				if (StringUtils.isEmpty(subject)) {
					subject = "New notification";
				}
				sendEmail(subject, notification.getDescription(), email);
				return true;
			} catch (WriteTransactionNeededException ex) {
				logger.debug("Can't create notification.", ex);
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Создание уведомления для email
	 *
	 * @param notification Атомарное уведомление
	 * @param email Адрес электронной почты
	 * @return Ссылка на уведомление для email
	 */
        //TODO DONE Refactoring in progress
	private NodeRef createNotification(NotificationUnit notification, String email) throws WriteTransactionNeededException {
		String employeeName = (String) nodeService.getProperty(notification.getRecipientRef(), ContentModel.PROP_NAME);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(4);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, notification.getFormingDate());
		properties.put(PROP_EMAIL, email);

                NodeRef rootRef = getServiceRootFolder();
                List<String> directoryPath = getDirectoryPath(employeeName, notification.getFormingDate());
                NodeRef saveDirectoryRef = getFolder(rootRef, directoryPath);
                //Судя по тому, что нода создаётся, транзакция должна быть.
                if (null == saveDirectoryRef) {
                    saveDirectoryRef = createPath(rootRef, directoryPath);
                }

		ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, GUID.generate()),
				TYPE_NOTIFICATION_EMAIL, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}

	public void sendEmail(String subject, String message, String email) {
		logger.debug("Sending email to: {}", email);
		Action mail = actionService.createAction(MailActionExecuterWithAttachment.NAME);

		Pattern pattern = Pattern.compile("<img\\s+src\\s*=\\s*(\"|')([^\"']+)(\"|')/>");
		Matcher matcher = pattern.matcher(message);

		ArrayList<String> attachments = new ArrayList<>();
		while (matcher.find()) {
			attachments.add(matcher.group(2));
		}
		if (!attachments.isEmpty()) {
			message = matcher.replaceAll("<img src=\"cid:$2\"");
			mail.setParameterValue(MailActionExecuterWithAttachment.ATTACHMENTS, attachments);
		}

		mail.setParameterValue(MailActionExecuter.PARAM_TO, email);
		mail.setParameterValue(MailActionExecuter.PARAM_SUBJECT, subject);
		mail.setParameterValue(MailActionExecuter.PARAM_HTML, message);
		mail.setExecuteAsynchronously(true);
		actionService.executeAction(mail, null);
	}

	@Override
	public NodeRef getServiceRootFolder() {
            return getFolder(NOTIFICATIONS_EMAIL_ROOT_ID);
	}
}
