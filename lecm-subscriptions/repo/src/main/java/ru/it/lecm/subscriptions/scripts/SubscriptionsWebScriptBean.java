package ru.it.lecm.subscriptions.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.subscriptions.beans.SubscriptionsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mShafeev
 * Date: 24.12.12
 * Time: 17:47
 */
public class SubscriptionsWebScriptBean extends BaseScopableProcessorExtension {
	public static final String NODE_REF = "nodeRef";
	public static final String PAGE = "page";
	public static final String ITEM_TYPE = "itemType";
	public static final String TITLE = "title";
	public static final String LABEL = "label";
	public static final String IS_LEAF = "isLeaf";
	public static final String NAME_PATTERN = "namePattern";
	public static final String DELETE_NODE = "deleteNode";

	public static final String PAGE_SUBSCRIPT_PROFILE = "subscr-positions";
	public static final String PAGE_SUBSCRIPT_OBJECT = "subscriptions-to-object";
	public static final String PAGE_SUBSCRIPT_TYPE = "subscriptions-to-type";
	public static final String TYPE_SUBSCRIPT_OBJECT = "subscription-to-object";
	public static final String TYPE_SUBSCRIPT_TYPE = "subscription-to-type";


	private static Log logger = LogFactory.getLog(SubscriptionsWebScriptBean.class);
	/**
	 * Service registry
	 */
	protected ServiceRegistry services;

	/**
	 * Repository helper
	 */
	protected Repository repository;

	private SubscriptionsBean subscriptionsService;

	/**
	 * Set the service registry
	 *
	 * @param services the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) {
		this.services = services;
	}

	/**
	 * Set the repository helper
	 *
	 * @param repository the repository helper
	 */
	public void setRepositoryHelper(Repository repository) {
		this.repository = repository;
	}

	public void setSubscriptionsService(SubscriptionsBean subscriptionsService) {
		this.subscriptionsService = subscriptionsService;
	}

	/**
	 * Возвращает корневой узел подписчиков
	 *
	 * @return Созданный корневой узел подписчиков или Null, если произошла ошибка
	 */
	public ScriptNode getSubscriptions() {
		NodeRef subscribtions = subscriptionsService.ensureSubscriptionsRootRef();
		return new ScriptNode(subscribtions, services, getScope());
	}

	/**
	 * Получаем список "корневых" объектов для меню в подписках
	 *
	 * @return Текстовое представление JSONArrray c объектами
	 */
	public String getRoots() {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = services.getNodeService();
		repository.init();
		JSONObject root;
		NodeRef subscriptionRef = subscriptionsService.getSubscriptionRootRef();
		try {
			// Добавить подписки
			root = new JSONObject();
			root.put(NODE_REF, "NOT_LOAD");
			root.put(PAGE, PAGE_SUBSCRIPT_PROFILE);
			nodes.put(root);

			root = new JSONObject();
			root.put(NODE_REF, subscriptionRef.toString());
			root.put(ITEM_TYPE, TYPE_SUBSCRIPT_OBJECT);
			root.put(PAGE, PAGE_SUBSCRIPT_OBJECT);
			root.put(DELETE_NODE, true);
			nodes.put(root);

			root = new JSONObject();
			root.put(NODE_REF, subscriptionRef.toString());
			root.put(ITEM_TYPE, TYPE_SUBSCRIPT_TYPE);
			root.put(PAGE, PAGE_SUBSCRIPT_TYPE);
			root.put(DELETE_NODE, true);
			nodes.put(root);

		} catch (JSONException e) {
			logger.error(e);
		}
		return nodes.toString();
	}

	/**
	 * Получения подписки сотрудника на объект
	 *
	 * @param employeeRefStr Ссылка на сотрудника
	 * @param objectRefStr   Ссылка на объект
	 * @return Подписка
	 */
	public ScriptNode getEmployeeSubscriptionToObject(String employeeRefStr, String objectRefStr) {
		ParameterCheck.mandatory("employeeRefStr", employeeRefStr);
		ParameterCheck.mandatory("objectRefStr", objectRefStr);
		NodeRef employeeRef = new NodeRef(employeeRefStr);
		NodeRef objectRef = new NodeRef(objectRefStr);
		if (this.services.getNodeService().exists(employeeRef) && this.services.getNodeService().exists(objectRef)) {
			if (subscriptionsService.getOrgstructureService().isEmployee(employeeRef)) {
				NodeRef subscriptionRef = subscriptionsService.getEmployeeSubscriptionToObject(employeeRef, objectRef);
				if (subscriptionRef != null && subscriptionsService.isSubscriptionToObject(subscriptionRef)) {
					return new ScriptNode(subscriptionRef, this.services, getScope());
				}
			}
		}
		return null;
	}

	/**
	 * Возвращает массив, пригодный для использования в веб-скриптах
	 *
	 * @return Scriptable
	 */
	private Scriptable createScriptable(List<NodeRef> refs) {
		Object[] results = new Object[refs.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = new ScriptNode(refs.get(i), services, getScope());
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}

	/**
	 * Получение списка значений Категорий события при выбранном элементе Тип Объекта
	 */
	public Scriptable findEventCategoryList(String selectTypeNodeRef) {
		NodeRef nodeRef = new NodeRef(selectTypeNodeRef);
		List<NodeRef> evenCategoryList = subscriptionsService.findEventCategoryList(nodeRef);
		return createScriptable(evenCategoryList);
	}

	/**
	 * Добавление элементов массива в список
	 * @param object
	 * @return
	 */
	private List<NodeRef> add(Object[] object){
		List<NodeRef> list = new ArrayList<NodeRef>();
		for (Object obj : object) {
			list.add(new NodeRef(obj.toString()));
		}
		return list;
	}

	/**
	 * Создание подписки на объект
	 * @param nodeRef ссылка на объект
	 * @param description описание
	 * @param notificationType тип доставки
	 * @param employee сотрудники
	 * @return Подписка
	 */
	public ScriptNode createSubscribeObject(String name, String nodeRef, String description,
	                                        Scriptable notificationType,
	                                        Scriptable employee) {

		NodeRef objectRef = new NodeRef(nodeRef);

		List<NodeRef> notificationTypeList = add(Context.getCurrentContext().getElements(notificationType));
		List<NodeRef> employeeList = add(Context.getCurrentContext().getElements(employee));

		if (this.services.getNodeService().exists(objectRef)) {
			NodeRef subscriptionRef = subscriptionsService.createSubscriptionToObject(name, objectRef, description,
					notificationTypeList, employeeList);
			if (subscriptionRef != null && subscriptionsService.isSubscriptionToObject(subscriptionRef)) {
				return new ScriptNode(subscriptionRef, services, getScope());
			}
		}
		return null;
	}

	/**
	 * Создание подписки на тип
	 * @param name имя узла
	 * @param description описание
	 * @param objectType тип объекта
	 * @param eventCategory категория события
	 * @param notificationType тип доставки
	 * @param employee сотрудники
	 * @param workGroup рабочие группы
	 * @param organizationUnit подразделения
	 * @param position должностная позиция
	 * @return Подписка
	 */
	public ScriptNode createSubscribeType(String name, String description, String objectType,
	                                      String eventCategory, Scriptable notificationType,
	                                      Scriptable employee, Scriptable workGroup, Scriptable organizationUnit,
	                                      Scriptable position){

		NodeRef objectTypeRef = null;
		NodeRef eventCategoryRef = null;

		if ((objectType != null) && !objectType.equals("")){
			objectTypeRef = new NodeRef(objectType);
		}

		if (eventCategory != null && !eventCategory.equals("")){
			eventCategoryRef = new NodeRef(objectType);
		}

		List<NodeRef> notificationTypeList = add(Context.getCurrentContext().getElements(notificationType));
		List<NodeRef> employeeList = add(Context.getCurrentContext().getElements(employee));
		List<NodeRef> workGroupList = add(Context.getCurrentContext().getElements(workGroup));
		List<NodeRef> organizationUnitList = add(Context.getCurrentContext().getElements(organizationUnit));
		List<NodeRef> positionList = add(Context.getCurrentContext().getElements(position));

			NodeRef subscriptionRef = subscriptionsService.createSubscriptionToType(name, description,
					objectTypeRef, eventCategoryRef, notificationTypeList, employeeList, workGroupList,
					organizationUnitList, positionList);
			if (subscriptionRef != null && subscriptionsService.isSubscriptionToType(subscriptionRef)) {
				return new ScriptNode(subscriptionRef, services, getScope());
			}
		return null;
	}

	/**
	 * Удаление подписки на объект
	 *
	 * @param nodeRef Ссылка на объект
	 * @return true если удачно удалена подписка, иначе false
	 */
	public boolean unsubscribeObject(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef subscriptionRef = new NodeRef(nodeRef);
		if (this.services.getNodeService().exists(subscriptionRef) &&
				subscriptionsService.isSubscriptionToObject(subscriptionRef)) {

			subscriptionsService.unsubscribe(subscriptionRef);
			return true;
		}
		return false;
	}

	/**
	 * Удаление подписки на тип
	 *
	 * @param nodeRef Ссылка на тип
	 * @return true если удачно удалена подписка, иначе false
	 */
	public boolean unsubscribeType(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef subscriptionRef = new NodeRef(nodeRef);
		if (this.services.getNodeService().exists(subscriptionRef) &&
				subscriptionsService.isSubscriptionToType(subscriptionRef)) {

			subscriptionsService.unsubscribe(subscriptionRef);
			return true;
		}
		return false;
	}
}
