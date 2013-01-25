package ru.it.lecm.notifications.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 11:57
 */
public class NotificationsWebScriptBean extends BaseScopableProcessorExtension {
	final private static Logger logger = LoggerFactory.getLogger(NotificationsWebScriptBean.class);

	NotificationsServiceImpl service;

	/**
	 * Service registry
	 */
	protected ServiceRegistry services;

	public void setService(NotificationsServiceImpl service) {
		this.service = service;
	}

	/**
	 * Set the service registry
	 *
	 * @param services the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) {
		this.services = services;
	}

	/**
	 * Отправка уведомлений
	 *
	 * @param json - JSON с параметрами уведомления
	 * Формат: dataObj: {
					recipients: ["nodeRef1", "nodeRef2"],
					types: ["nodeRef1", "nodeRef2"],
					object: "nodeRef",
					description: "description",
					formingDate: "date(format yyyy-MM-dd HH:mm:ss)",
					initiator: "initiator"
				},
	 *
	 * @return true - при успешной отправке иначе false
	 */
	public boolean sendNotification(JSONObject json) {
		Notification notf = new Notification();
		notf.setAutor("WebScript");
		try {
			notf.setAutor(json.getString("initiator"));

			notf.setDescription(json.getString("description"));
			notf.setFormingDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getString("formingDate")));

			JSONArray recipientsArray = json.getJSONArray("recipients");
			if (recipientsArray != null) {
				List<NodeRef> recipientRefsList = new ArrayList<NodeRef>();

				for (int i = 0; i < recipientsArray.length(); ++i) {
					NodeRef nodeRef =  new NodeRef ((String) recipientsArray.get(i));
					if (nodeRef != null && services.getNodeService().exists(nodeRef) && service.getOrgstructureService().isEmployee(nodeRef)) {
						recipientRefsList.add(nodeRef);
					}
				}
				notf.setRecipientEmployeeRefs(recipientRefsList);
			}

			String objectRef = json.getString("object");
			if (objectRef != null) {
				NodeRef nodeRef =  new NodeRef (objectRef);
				if (nodeRef != null && services.getNodeService().exists(nodeRef)) {
					notf.setObjectRef(nodeRef);
				}
			}

			JSONArray typesArray = json.getJSONArray("types");
			if (typesArray != null) {
				List<NodeRef> typesRefsList = new ArrayList<NodeRef>();

				for (int i = 0; i < typesArray.length(); ++i) {
					NodeRef nodeRef =  new NodeRef ((String) typesArray.get(i));
					if (nodeRef != null && services.getNodeService().exists(nodeRef) && service.isNotificationType(nodeRef)) {
						typesRefsList.add(nodeRef);
					}
				}
				notf.setTypeRefs(typesRefsList);
			}
		} catch (JSONException e) {
			logger.error("Error read JSON", e);
		} catch (ParseException e) {
			logger.error("Error read forming date", e);
		}
		return service.sendNotification(notf);
	}
}
