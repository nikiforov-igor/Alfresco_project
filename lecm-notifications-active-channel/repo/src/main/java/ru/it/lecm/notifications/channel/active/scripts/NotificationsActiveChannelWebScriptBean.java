package ru.it.lecm.notifications.channel.active.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.notifications.channel.active.beans.NotificationsActiveChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 22.01.13
 * Time: 9:33
 */
public class NotificationsActiveChannelWebScriptBean extends BaseScopableProcessorExtension {
	private NotificationsActiveChannel service;

	private final static Logger logger = LoggerFactory.getLogger(NotificationsActiveChannelWebScriptBean.class);

	public void setService(NotificationsActiveChannel service) {
		this.service = service;
	}

	/**
	 * Получение корневой папки уведомлений активного канала
	 * @return
	 */
	public ScriptNode getDirectory() {
		try {
			NodeRef ref = this.service.getRootRef();
			return new ScriptNode(ref, this.service.getServiceRegistry(), getScope());
		} catch (Exception e) {
			throw new ScriptException("Не удалось получить директорию с уведомлениями активного канала", e);
		}
	}

	/**
	 * Получение количества новых уведомлений
	 * @return количество новых уведомлений
	 */
	public int getNewNotificationsCount() {
		return this.service.getNewNotificationsCount();
	}

	/**
	 * Получение уведомлений
	 * @param skipItemsCount количество пропущенных записей
	 * @param loadItemsCount максимальное количество возвращаемых элементов
	 * @return               список уведомлений
	 */
	public Scriptable getNotifications(String skipItemsCount, String loadItemsCount) {
		ParameterCheck.mandatory("skipItemsCount", skipItemsCount);
		ParameterCheck.mandatory("loadItemsCount", loadItemsCount);
		List<NodeRef> notfs = this.service.getNotifications(Integer.parseInt(skipItemsCount), Integer.parseInt(loadItemsCount));
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
			results[i] = new ScriptNode(refs.get(i), this.service.getServiceRegistry(), getScope());
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}

	/**
	 * Выставление времени прочтения уведомления
	 * @param nodeRefs json со списком ссылок на уведомления
	 */
	public void setReadNotifications(final JSONArray nodeRefs) {
		List<NodeRef> nodeRefsList = new ArrayList<NodeRef>();
		for (int i = 0; i < nodeRefs.length(); ++i) {
			NodeRef nodeRef = null;
			try {
				nodeRef = new NodeRef (nodeRefs.getJSONObject (i).getString ("nodeRef"));
			} catch (JSONException ex) {
				logger.error (ex.getMessage (), ex);
			}
			if (nodeRef != null && this.service.isActiveChannelNotification(nodeRef)) {
				nodeRefsList.add(nodeRef);
			}
		}
		this.service.setReadNotifications(nodeRefsList);
	}
}
