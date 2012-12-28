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
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.subscriptions.beans.SubscriptionsBean;

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

			subscriptionsService.unsubscribeObject(subscriptionRef);
			return true;
		}
		return false;
	}
}
