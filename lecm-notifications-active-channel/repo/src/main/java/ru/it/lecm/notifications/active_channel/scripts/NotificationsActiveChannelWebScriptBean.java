package ru.it.lecm.notifications.active_channel.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.notifications.active_channel.beans.NotificationsActiveChannel;

/**
 * User: AIvkin
 * Date: 22.01.13
 * Time: 9:33
 */
public class NotificationsActiveChannelWebScriptBean extends BaseScopableProcessorExtension {
	private NotificationsActiveChannel service;

	public void setService(NotificationsActiveChannel service) {
		this.service = service;
	}

	public ScriptNode getDirectory() {
		try {
			NodeRef ref = service.getRootRef();
			return new ScriptNode(ref, service.getServiceRegistry(), getScope());
		} catch (Exception e) {
			throw new ScriptException("Не удалось получить директорию с уведомлениями активного канала", e);
		}
	}

	public int getNewNotificationsCount() {
		return service.getNewNotificationsCount();
	}
}
