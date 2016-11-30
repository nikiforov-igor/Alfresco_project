package ru.it.lecm.notifications.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
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
import java.util.Map;
import java.util.Set;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.LecmTransactionHelper;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 11:57
 */
public class NotificationsWebScriptBean extends BaseWebScript {
	final private static Logger logger = LoggerFactory.getLogger(NotificationsWebScriptBean.class);

	NotificationsService service;
	private OrgstructureBean orgstructureService;
	protected AuthenticationService authService;
	private TransactionService transactionService;
        private LecmTransactionHelper lecmTransactionHelper;

        public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
            this.lecmTransactionHelper = lecmTransactionHelper;
        }

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

    public void setService(NotificationsService service) {
		this.service = service;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	/**
	 * отправка заранее подготовленного уведомления
	 * @param notification - объект уведомления, который был где-то заранее создан и готов к отправке
	 */
	public void sendNotification(final Notification notification) {
		service.sendNotification(notification);
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
	 */
	public void sendNotification(JSONObject json) {
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
		service.sendNotification(notf);
	}

    /**
     * Отправка уведомлений
     * @param author автор уведомления
     * @param employee список ссылок на получателей (пользователей).
     * @param textFormatString форматная строка для текста сообщения
     * @param channels перечень каналов
     * @param object основной объект уведомления
     * @param initiator инициатор
     */
    public void sendNotification(String author, Scriptable employee, String textFormatString, Scriptable channels, ScriptNode object, NodeRef initiator) {
        sendNotification(author, employee, textFormatString, channels, object, initiator, false);
    }
	private List<NodeRef> getRecipientsList(Scriptable recipients) {
        ArrayList<String> recipientsArray = getArraysList(Context.getCurrentContext().getElements(recipients));

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
		return employees;
	}
    /**
     * Отправка уведомлений
     * @param author автор уведомления
     * @param employee Список ссылок на получателей (пользователей)
     * @param textFormatString форматная строка для текста сообщения
     * @param channels перечень каналов
     * @param object Основной объект уведомления
     * @param initiator инициатор
     * @param dontCheckAccessToObject не проверять доступность объекта получателю
     */
    public void sendNotification(String author, Scriptable employee, String textFormatString, Scriptable channels, ScriptNode object, NodeRef initiator, boolean dontCheckAccessToObject) {
		List<NodeRef> employees = getRecipientsList(employee);
	    ArrayList<String> channelsArray = null;
	    if (channels != null) {
		    channelsArray = getArraysList(Context.getCurrentContext().getElements(channels));
	    }

	    service.sendNotification(author, object.getNodeRef(), textFormatString, employees, channelsArray, initiator, dontCheckAccessToObject);
    }

	/**
	 * Отправка уведомлений
	 * @param employee список nodeRef-ов на получателей (пользователей).
	 * @param textFormatString форматная строка для текста сообщения
	 * @param channels перечень каналов
	 * @param object основной объект уведомления
	 */
	public void sendNotification(Scriptable employee, String textFormatString, Scriptable channels, ScriptNode object) {
        sendNotification(employee, textFormatString, channels, object, false);
    }

	/**
	 * Отправка уведомлений
	 * @param employee список nodeRef-ов на получателей (пользователей).
	 * @param textFormatString форматная строка для текста сообщения
	 * @param channels перечень каналов
	 * @param object основной объект уведомления
	 * @param dontCheckAccessToObject не проверять доступность объекта получателю
	 */
	public void sendNotification(Scriptable employee, String textFormatString, Scriptable channels, ScriptNode object, boolean dontCheckAccessToObject) {
		sendNotification("WebScript", employee, textFormatString, channels, object, null, dontCheckAccessToObject);
	}

	/**
	 * Отправка уведомлений в каналы по умолчанию
	 * @param employee Список ссылок на получателей (пользователей).
	 * @param textFormatString форматная строка для текста сообщения
	 * @param object Основной объект уведомления
	 */
	public void sendNotification(Scriptable employee, String textFormatString, ScriptNode object) {
        sendNotification(employee, textFormatString, object, false);
    }
	/**
	 * Отправка уведомлений в каналы по умолчанию
	 * @param employee Список ссылок на получателей (пользователей).
	 * @param textFormatString форматная строка для текста сообщения
	 * @param object Основной объект уведомления
     * @param dontCheckAccessToObject не проверять доступность объекта получателю
	 */
	public void sendNotification(Scriptable employee, String textFormatString, ScriptNode object, boolean dontCheckAccessToObject) {
		sendNotification(employee, textFormatString, null, object, dontCheckAccessToObject);
	}

	/**
	 * Отправка уведомлений от текущего пользователя
	 * @param employee Список ссылок на получателей (пользователей).
	 * @param textFormatString форматная строка для текста сообщения
	 * @param object Основной объект уведомления
	 */
	public void sendNotificationFromCurrentUser(Scriptable employee, String textFormatString, ScriptNode object) {
        sendNotificationFromCurrentUser(employee, textFormatString, object, false);
    }

	/**
	 * Отправка уведомлений от текущего пользователя
	 * @param employee Список ссылок на получателей (пользователей).
	 * @param textFormatString форматная строка для текста сообщения
	 * @param object Основной объект уведомления
	 * @param dontCheckAccessToObject не проверять доступность объекта получателю
	 */
	public void sendNotificationFromCurrentUser(Scriptable employee, String textFormatString, ScriptNode object, boolean dontCheckAccessToObject) {
		sendNotification(authService.getCurrentUserName(), employee, textFormatString, null, object, orgstructureService.getCurrentEmployee(), dontCheckAccessToObject);
	}

	/**
	 * Отправка уведомлений от текущего пользователя
	 * @param employee Список ссылок на получателей (пользователей).
	 * @param templateCode Код шаблона уведомления
	 * @param map Набор объектов. Обязательно должен приисутствовать как минимум один объект с идентификатором "mainObject"
	 * @param dontCheckAccessToObject не проверять доступность объекта получателю
	 */
//	public void sendNotificationFromCurrentUser(Scriptable employee, String templateCode, Scriptable map, boolean dontCheckAccessToObject) throws TemplateRunException, TemplateParseException {
//		Map<String,NodeRef> objects = getMap(map);
//		String author = authService.getCurrentUserName();
//		List<NodeRef> recipientsArray = getRecipientsList(employee);
//
//		service.sendNotification(author, objects, templateCode, recipientsArray, orgstructureService.getCurrentEmployee(), dontCheckAccessToObject);
//	}

	/**
	 * Отправка уведомлений по заданной конфигурации
	 * @param oParams native json object параметры отправлямого уведомления
	 * формат
	 * {
	 *   "author": "string", //значение по умолчанию WebScript
	 *   "recipients": [scriptNode1, scriptNode1, ...], //scriptNode-ы получателей
	 *   "templateCode": "string" //строковый код шаблона уведомления
	 *   "templateConfig": { //конфигурация с переменными передаваемыми в шаблон
	 *       "var1": "value1",
	 *       "var1": "value2",
	 *   },
	 *   "initiator": scriptNode, //ScriptNode на сотрудника, являющегося инициатором уведомления, может быть null
	 *   "dontCheckAccessToObject": boolean, //значение по умолчанию false, не проверять доступность объекта получателю
	 * }
	 */
	public void sendNotification(Scriptable oParams) {
		sendNotification((Map<String, Object>)getValueConverter().convertValueForJava(oParams));
	}

	public void sendNotificationFromCurrentUser(Scriptable oParams) {
		Map<String, Object> params = (Map<String, Object>)getValueConverter().convertValueForJava(oParams);
		params.put("author", authService.getCurrentUserName());
		params.put("initiator", orgstructureService.getCurrentEmployee());
		sendNotification(params);
	}

	private void sendNotification(Map<String, Object> params) {
		String author = "WebScript";
		List<NodeRef> recipients = (List<NodeRef>)params.get("recipients");
		String templateCode = (String)params.get("templateCode");
		Map<String, Object> templateConfig = (Map<String, Object>)params.get("templateConfig");
		NodeRef initiator = (NodeRef)params.get("initiator");
		boolean dontCheckAccessToObject = false;

		ParameterCheck.mandatory("recipients", recipients);
		ParameterCheck.mandatory("templateCode", templateCode);
		ParameterCheck.mandatory("templateConfig", templateConfig);
		ParameterCheck.mandatory("templateConfig.mainObject", templateConfig.get("mainObject"));

		if (StringUtils.isNotBlank((String)params.get("author"))) {
			author = (String)params.get("author");
		}

		if (params.get("dontCheckAccessToObject") != null) {
			dontCheckAccessToObject = (Boolean)params.get("dontCheckAccessToObject");
		}

		service.sendNotification(author, initiator, recipients, templateCode, templateConfig, dontCheckAccessToObject);
	}

//	private Map<String, NodeRef> getMap(Scriptable object) {
//		Map<String, NodeRef> result = new HashMap<>();
//		if (null != object) {
//			List<String> ids = getArraysList(object.getIds());
//			for (String id : ids) {
//				Object obj = object.get(id, getScope());
//				if (obj instanceof NativeJavaObject) {
//					obj=((NativeJavaObject)obj).unwrap();
//				}
//				if (obj instanceof ScriptNode) {
//					result.put(id, ((ScriptNode) obj).getNodeRef());
//				} else if (obj instanceof String) {
//					if (NodeRef.isNodeRef(obj.toString())) {
//						NodeRef ref = new NodeRef(obj.toString());
//						result.put(id, ref);
//					} else {
//						logger.error("Skipping invalid string in map:" + id +"='"+ obj.toString()+"'");
//					}
//				}
//
//			}
//		}
//		return result;
//	}

    private ArrayList<String> getArraysList(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj instanceof String){
                arrayList.add(obj.toString());
            } else if (obj instanceof ScriptNode){
	            ScriptNode element = (ScriptNode) obj;
                arrayList.add(element.getNodeRef().toString());
            }
        }
        return arrayList;
    }

	/**
	 * Получение настроек уведомлений текущего пользователя
	 * @return узел с настройками уведомлений
	 */
	public ScriptNode getCurrentUserSettingsNode() {
		NodeRef settings = service.getCurrentUserSettingsNode();
		if(settings == null) {
			logger.debug("Notifications user settings not found. Try to create.");
			settings = lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>(){
				@Override
				public NodeRef execute() throws Throwable {
					if(service.getCurrentUserSettingsNode() == null) {									
						 return service.createCurrentUserSettingsNode();
					}
					return null;
				}
			});
			logger.debug("Notification user settings created. NodeRef = " + settings);
		}
		return new ScriptNode(settings, serviceRegistry, getScope());
	}

	/**
	 * Получение каналов уведомлений по умолчанию для текущего пользователя (из настроек)
	 * @return список каналов уведомлений
	 */
	public List<NodeRef> getCurrentUserDefaultNotificationTypes() {
//		TODO: getCurrentUserDefaultNotificationTypes глубоко в недрах в итоге использует метод
//		service.getCurrentUserSettingsNode(). Может так получится, что папка ещё не создана, поэтому
//		подёргаем перед выполнением.
		getCurrentUserSettingsNode();
		return service.getCurrentUserDefaultNotificationTypes();
	}

	/**
	 * Получение глобальный настроек уведомлений
	 * @return узел с глобальными настройками уведомлений
	 */
	public ScriptNode getGlobalSettingsNode() {
        //globalSettingsNode создаётся при инициализации сервиса.
		NodeRef settings = service.getGlobalSettingsNode();
		if(settings == null) {
			settings = service.createGlobalSettingsNode(); 
		}
		if(settings != null) {
            return new ScriptNode(settings, serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * Получение количества рабочих дней за которое должно высылаться уведомление
	 * @return количество рабочих дней за которое должно высылаться уведомление
	 */
    public int getSettingsNDays() {
        return service.getSettingsNDays();
    }
}
