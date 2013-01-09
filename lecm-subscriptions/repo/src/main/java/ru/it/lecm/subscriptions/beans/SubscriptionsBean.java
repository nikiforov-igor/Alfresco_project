package ru.it.lecm.subscriptions.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: mShafeev
 * Date: 24.12.12
 * Time: 17:09
 */
public class SubscriptionsBean extends BaseBean {

	/**
	 *
	 */
	public static final String SUBSCRIPTIONS_ROOT_NAME = "Подписки";
	public static final String DICTIONARY_ROOT_NAME = "Dictionary";
	public static final String DICTIONARY_ROOT_NAME_EVENT_CATEGORY = "Категория события";
	public static final String DICTIONARY_ROOT_NAME_TYPE_OBJECT = "Тип объекта";
	public static final String DICTIONARY_ROOT_NAME_TYPE_TEMPLATE_MESSAGE = "Шаблон сообщения";
	public static final String SUBSCRIPTIONS_NAMESPACE_URI = "http://www.it.ru/lecm/subscriptions/1.0";
	QName TYPE_SUBSCRIPTION_TO_OBJECT = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "subscription-to-object");
	QName TYPE_SUBSCRIPTION_TO_TYPE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "subscription-to-type");
	QName ASSOC_NOTIFICATION_TYPE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "notification-type-assoc");
	QName ASSOC_DESTINATION_EMPLOYEE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-employee-assoc");
	QName ASSOC_SUBSCRIPTION_OBJECT = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "subscription-object-assoc");
	QName ASSOC_DESTINATION_POSITION = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-position-assoc");
	QName ASSOC_DESTINATION_ORGANIZATION_UNIT = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-organization-unit-assoc");
	QName ASSOC_DESTINATION_WORK_GROUP = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "destination-work-group-assoc");
	QName ASSOC_OBJECT_TYPE = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "object-type-assoc");
	QName ASSOC_EVENT_CATEGORY = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, "event-category-assoc");
	public static final String BUSJOURNAL_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";
	public static final String TYPE_SUBSCRIPTION = "subscription";

	QName ASSOC_BUSJOURNAL_LINK_EMPLOYEE = QName.createQName(BUSJOURNAL_NAMESPACE_URI, "lecm-busjournal");
	QName ASSOC_BUSJOURNAL_EVENT_CATEGORY = QName.createQName(BUSJOURNAL_NAMESPACE_URI, "messageTemplate-evCategory-assoc");
	QName ASSOC_BUSJOURNAL_OBJECT_TYPE = QName.createQName(BUSJOURNAL_NAMESPACE_URI, "messageTemplate-objType-assoc");

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private OrgstructureBean orgstructureService;



	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	private final Object lock = new Object();

	/**
	 * Получение директории подписки.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 */
	public NodeRef getSubscriptionRootRef() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, SUBSCRIPTIONS_ROOT_NAME);
	}

	/**
	 * Получение узла подписки, в котором хрянится информация об подписках.
	 * Если такой узел отсутствует - он создаётся автоматически (внутри /CompanyHome)
	 * @return
	 */
	public NodeRef ensureSubscriptionsRootRef() {
		final String rootName = SUBSCRIPTIONS_ROOT_NAME;
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef subscriptionsRef;
						synchronized (lock) {
							// еще раз пытаемся получить директорию (на случай если она уже была создана другим потоком
							subscriptionsRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
									rootName);
							if (subscriptionsRef == null) {
								QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
								QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
								QName nodeTypeQName = ContentModel.TYPE_FOLDER;

								Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, rootName);
								ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
								subscriptionsRef = associationRef.getChildRef();
							}
						}
						return subscriptionsRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	/**
	 * проверяет что объект является подпиской на объект
	 */
	public boolean isSubscriptionToObject(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_SUBSCRIPTION_TO_OBJECT);
		return isProperType(ref, types);
	}

	/**
	 * Получение списка подписок сотрудника
	 *
	 * @param employeeRef Ссылка на сотрудника
	 * @return Список ссылок на подписки
	 */
	public List<NodeRef> getEmployeeSubscriptionsToObject(NodeRef employeeRef) {
		List<NodeRef> subscriptions = new ArrayList<NodeRef>();
		if (orgstructureService.isEmployee(employeeRef)) {
			List<AssociationRef> lRefs = nodeService.getSourceAssocs(employeeRef, ASSOC_DESTINATION_EMPLOYEE);
			for (AssociationRef lRef : lRefs) {
				if (!isArchive(lRef.getSourceRef())) {
					subscriptions.add(lRef.getSourceRef());
				}
			}
		}
		return subscriptions;
	}

	/**
	 * Получения подписки сотрудника на объект
	 *
	 * @param employeeRef Ссылка на сотрудника
	 * @param objectNodeRef Ссылка на объект
	 * @return Ссылка на подписку
	 */
	public NodeRef getEmployeeSubscriptionToObject(NodeRef employeeRef, NodeRef objectNodeRef) {
		NodeRef result = null;
		List<NodeRef> subscriptions = getEmployeeSubscriptionsToObject(employeeRef);
		for (NodeRef subscriptionRef: subscriptions) {
			List<AssociationRef> lRefs = nodeService.getTargetAssocs(subscriptionRef, ASSOC_SUBSCRIPTION_OBJECT);
			for (AssociationRef lRef : lRefs) {
				if (!isArchive(lRef.getTargetRef()) && lRef.getTargetRef().equals(objectNodeRef)) {
					return subscriptionRef;
				}
			}
		}
		return result;
	}

	/**
	 * Получение директории справочника "Категории событий".
	 */
	public NodeRef getDictionaryEventCategory() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		final NodeRef dictionary = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
				DICTIONARY_ROOT_NAME);
		return nodeService.getChildByName(dictionary, ContentModel.ASSOC_CONTAINS,
				DICTIONARY_ROOT_NAME_EVENT_CATEGORY);
	}

	/**
	 * Получение директории справочника "Тип объекта".
	 */
	public NodeRef getDictionaryTypeObject() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		final NodeRef dictionary = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
				DICTIONARY_ROOT_NAME);
		return nodeService.getChildByName(dictionary, ContentModel.ASSOC_CONTAINS, DICTIONARY_ROOT_NAME_TYPE_OBJECT);
	}

	/**
	 * Получение директории справочника "Тип объекта".
	 */
	public NodeRef getDictionaryTypeObject(NodeRef nodeRefChild) {
		return nodeService.getPrimaryParent(nodeRefChild).getParentRef();
	}

	/**
	 * Получение директории справочника "Шаблон сообщения".
	 */
	public NodeRef getDictionaryTemplateMessage() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		final NodeRef dictionary = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
				DICTIONARY_ROOT_NAME);
		return nodeService.getChildByName(dictionary, ContentModel.ASSOC_CONTAINS, DICTIONARY_ROOT_NAME_TYPE_TEMPLATE_MESSAGE);
	}


	/**
	 * Получение списка категорий событий по выбранному типу объекта в сравочнике "Шаблон сообщения"
	 */
	public List<NodeRef> findEventCategoryList(NodeRef nodeRef) {
		List<NodeRef> eventCategory = new ArrayList<NodeRef>();
		// Находим элементы в справочнике Шаблон сообщения по выбранной ноде и ассоциации
		List<AssociationRef> tmRefs = nodeService.getSourceAssocs(nodeRef,ASSOC_BUSJOURNAL_OBJECT_TYPE);
		for (AssociationRef tmRef : tmRefs) {
			if (!isArchive(tmRef.getSourceRef())) {
				List<AssociationRef> ecRefs = nodeService.getTargetAssocs(tmRef.getSourceRef(), ASSOC_BUSJOURNAL_EVENT_CATEGORY);
				for (AssociationRef ecRef : ecRefs) {
					if (!isArchive(ecRef.getSourceRef())) {
						if (!isArchive(ecRef.getTargetRef())){
							eventCategory.add(ecRef.getTargetRef());
						}
					}
				}
			}
		}
		// удалаяем дубликаты
		List<NodeRef> result = new ArrayList<NodeRef>(new HashSet<NodeRef>(eventCategory));
		return result;
	}

	/**
	 * Удаление подписки на объект
	 *
	 * @param nodeRef Ссылка на подписку
	 */
	public void unsubscribeObject(NodeRef nodeRef) {
		nodeService.deleteNode(nodeRef);
	}
}
