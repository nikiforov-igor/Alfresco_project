package ru.it.lecm.notifications.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 11:57
 */
public class NotificationsWebScriptBean extends BaseWebScript {
	final private static Logger logger = LoggerFactory.getLogger(NotificationsWebScriptBean.class);

	NotificationsService service;
	private OrgstructureBean orgstructureService;

    public void setService(NotificationsService service) {
		this.service = service;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
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
		notf.setAuthor("WebScript");
		try {
			notf.setAuthor(json.getString("initiator"));

			notf.setDescription(json.getString("description"));
			notf.setFormingDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getString("formingDate")));

			JSONArray recipientsArray = json.getJSONArray("recipients");
			if (recipientsArray != null) {
				List<NodeRef> recipientRefsList = new ArrayList<NodeRef>();

				for (int i = 0; i < recipientsArray.length(); ++i) {
					NodeRef nodeRef =  new NodeRef ((String) recipientsArray.get(i));
					if (serviceRegistry.getNodeService().exists(nodeRef) && orgstructureService.isEmployee(nodeRef)) {
						recipientRefsList.add(nodeRef);
					}
				}
				notf.setRecipientEmployeeRefs(recipientRefsList);
			}

			String objectRef = json.getString("object");
			if (objectRef != null) {
				NodeRef nodeRef =  new NodeRef (objectRef);
				if (serviceRegistry.getNodeService().exists(nodeRef)) {
					notf.setObjectRef(nodeRef);
				}
			}

			JSONArray typesArray = json.getJSONArray("types");
			if (typesArray != null) {
				List<NodeRef> typesRefsList = new ArrayList<NodeRef>();

				for (int i = 0; i < typesArray.length(); ++i) {
					NodeRef nodeRef =  new NodeRef ((String) typesArray.get(i));
					if (serviceRegistry.getNodeService().exists(nodeRef) && service.isNotificationType(nodeRef)) {
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

    /**
     * Отправка уведомлений
     * @param employee Список ссылок на получателей (пользователей).
     * @param textFormatString форматная строка для текста сообщения
     * @param channels перечень каналов
     * @param objectRef Основной объект уведомления
     * @return true - при успешной отправке иначе false
     */
    public boolean sendNotification(Scriptable employee, String textFormatString, Scriptable channels, String objectRef) {
        ArrayList<String> recipientsArray = getArraysList(Context.getCurrentContext().getElements(employee));

	    List<NodeRef> employees = null;
        if (recipientsArray != null) {
            Set<NodeRef> recipientRefsList = new HashSet<NodeRef>();

            for (String aRecipientsArray : recipientsArray) {
                NodeRef nodeRef = null;
                if (NodeRef.isNodeRef(aRecipientsArray)) {
                    nodeRef = new NodeRef(aRecipientsArray);
                    if (serviceRegistry.getNodeService().exists(nodeRef)) {
                        if (!orgstructureService.isEmployee(nodeRef)) {
                            nodeRef = orgstructureService.getEmployeeByPerson(nodeRef);
                        }
                    }
                } else if (orgstructureService.isEmployee(orgstructureService.getEmployeeByPerson(aRecipientsArray))) {
                    nodeRef = orgstructureService.getEmployeeByPerson(aRecipientsArray);
                }
                if (nodeRef != null) {
                    recipientRefsList.add(nodeRef);
                }
            }
	        employees = new ArrayList<NodeRef>(recipientRefsList);
        }

	    NodeRef object = null;
        if (objectRef != null) {
            NodeRef nodeRef =  new NodeRef (objectRef);
            if (serviceRegistry.getNodeService().exists(nodeRef)) {
	            object = nodeRef;
            }
        }

	    ArrayList<String> channelsArray = null;
	    if (channels != null) {
		    channelsArray = getArraysList(Context.getCurrentContext().getElements(channels));
	    }

	    return service.sendNotification("WebScript", object, textFormatString, employees, channelsArray);
    }

	/**
	 * Отправка уведомлений в каналы по умолчанию
	 * @param employee Список ссылок на получателей (пользователей).
	 * @param textFormatString форматная строка для текста сообщения
	 * @param objectRef Основной объект уведомления
	 * @return true - при успешной отправке иначе false
	 */
	public boolean sendNotification(Scriptable employee, String textFormatString, String objectRef) {
		return sendNotification(employee, textFormatString, null, objectRef);
	}

    private ArrayList<String> getArraysList(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj instanceof String){
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }

	public ScriptNode getCurrentUserSettingsNode() {
		return new ScriptNode(service.getCurrentUserSettingsNode(true), serviceRegistry, getScope());
	}

	public List<NodeRef> getCurrentUserDefaultNotificationTypes() {
		return  service.getCurrentUserDefaultNotificationTypes();
	}
}
