package ru.it.lecm.notifications.active_channel.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.notifications.active_channel.beans.NotificationsActiveChannel;

import java.util.List;

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

	public Scriptable getNotifications() {
		List<NodeRef> notfs = service.getNotifications();
		return createScriptable(notfs);
	}

	/**
	 * Возвращает массив, пригодный для использования в веб-скриптах
	 *
	 * @return Scriptable
	 */
	private Scriptable createScriptable(List<NodeRef> refs) {
		Object[] results = new Object[refs.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = new ScriptNode(refs.get(i), service.getServiceRegistry(), getScope());
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}
}
