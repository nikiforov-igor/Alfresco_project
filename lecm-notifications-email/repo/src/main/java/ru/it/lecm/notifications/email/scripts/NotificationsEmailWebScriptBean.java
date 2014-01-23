package ru.it.lecm.notifications.email.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.notifications.email.beans.NotificationsEmailChannel;
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
public class NotificationsEmailWebScriptBean extends BaseWebScript {

	protected NotificationsEmailChannel notificationsEmailService;

	public void setNotificationsEmailService(NotificationsEmailChannel notificationsEmailService) {
		this.notificationsEmailService = notificationsEmailService;
	}

	public void sendEmail(String subject, String message, String email) {
		notificationsEmailService.sendEmail(subject, message, email);
	}
}
