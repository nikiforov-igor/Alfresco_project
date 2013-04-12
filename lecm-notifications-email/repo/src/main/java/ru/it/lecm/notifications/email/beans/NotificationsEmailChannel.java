package ru.it.lecm.notifications.email.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	protected NotificationsService notificationsService;
	private ActionService actionService;
	private NodeRef rootRef;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

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
		this.rootRef = getFolder(NOTIFICATIONS_EMAIL_ROOT_ID);
	}

	@Override
	public boolean sendNotification(NotificationUnit notification) {
		String email = (String) nodeService.getProperty(notification.getRecipientRef(), OrgstructureBean.PROP_EMPLOYEE_EMAIL);
		if (email != null) {
			createNotification(notification, email);
			sendEmail(notification, email);
			return true;
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
	private NodeRef createNotification(NotificationUnit notification, String email) {
		String employeeName = (String) nodeService.getProperty(notification.getRecipientRef(), ContentModel.PROP_NAME);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(4);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, notification.getFormingDate());
		properties.put(PROP_EMAIL, email);

		final NodeRef saveDirectoryRef = getFolder(this.rootRef, employeeName, notification.getFormingDate());

		ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_EMAIL_NAMESPACE_URI, GUID.generate()),
				TYPE_NOTIFICATION_EMAIL, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}

	/**
	 * Отправка письма с уведомлением на почту сотрудника
	 *
	 * @param notification Атомарное уведомление
	 * @param email Адрес электронной почты
	 */
	private void sendEmail(NotificationUnit notification, String email) {
		logger.debug("Sending email to: {}", email);
		Action mail = actionService.createAction(MailActionExecuter.NAME);

		mail.setParameterValue(MailActionExecuter.PARAM_TO, email);
		String message = I18NUtil.getMessage("notifications.email.subject", I18NUtil.getLocale());
		mail.setParameterValue(MailActionExecuter.PARAM_SUBJECT, message != null ? message : "New notification");
		mail.setParameterValue(MailActionExecuter.PARAM_HTML, notification.getDescription());
		mail.setExecuteAsynchronously(true);
		actionService.executeAction(mail, null);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return rootRef;
	}
}
