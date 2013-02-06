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
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: mShafeev
 * Date: 24.12.12
 * Time: 17:09
 */
public class SubscriptionsServiceImpl extends BaseBean implements SubscriptionsService {
	final protected Logger logger = LoggerFactory.getLogger(SubscriptionsServiceImpl.class);

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private OrgstructureBean orgstructureService;
	private static BusinessJournalService businessJournalService;


	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	private final Object lock = new Object();

	@Override
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
	public NodeRef init() {
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

	@Override
	public boolean isSubscriptionToObject(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_SUBSCRIPTION_TO_OBJECT);
		return isProperType(ref, types);
	}

	@Override
	public boolean isSubscriptionToType(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_SUBSCRIPTION_TO_TYPE);
		return isProperType(ref, types);
	}

	@Override
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

	@Override
	public List<NodeRef> getSubscriptionsToObject(NodeRef objectRef) {
		List<NodeRef> subscriptions = new ArrayList<NodeRef>();
		List<AssociationRef> lRefs = nodeService.getSourceAssocs(objectRef, ASSOC_SUBSCRIPTION_OBJECT);
		for (AssociationRef lRef : lRefs) {
			if (!isArchive(lRef.getSourceRef())) {
				subscriptions.add(lRef.getSourceRef());
			}
		}
		return subscriptions;
	}

	@Override
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
	 * Создание асоциации
	 * @param sourceRef узел в котором создается ассоциация
	 * @param list список ссылок на узел
	 * @param assocTypeQName тип ассоциации
	 */
	private void createAssociation(NodeRef sourceRef, List<NodeRef> list, QName assocTypeQName){
		if ((list != null) && (list.size() > 0)) {
			for (NodeRef targetRef  : list) {
				nodeService.createAssociation(sourceRef, targetRef, assocTypeQName);
			}
		}
	}

	@Override
	public NodeRef createSubscriptionToObject(String name, NodeRef objectRef, String description,
	                                          List<NodeRef> notificationType,
	                                          List<NodeRef> employee) {
		NodeRef subscriptionRootRef = getSubscriptionRootRef();

		String subscribeName;
		if ((name == null) || name.equals("")) {
			subscribeName = GUID.generate();
		} else {
			subscribeName = name;
		}

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(0);
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		QName assocQName = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, subscribeName);

		if (description != null && description.equals("")) {
			properties.put(PROP_DESCRIPTION, description);
		}
		ChildAssociationRef subscriptionsRef = nodeService.createNode(subscriptionRootRef, assocTypeQName, assocQName,
				TYPE_SUBSCRIPTION_TO_OBJECT, properties);

		// Создаем ассоциацию подписки на объект
		nodeService.createAssociation(subscriptionsRef.getChildRef(), objectRef, ASSOC_SUBSCRIPTION_OBJECT);
		// Создаем ассоциацию подписки на тип доставки
		createAssociation(subscriptionsRef.getChildRef(), notificationType, ASSOC_NOTIFICATION_TYPE);
		// Создаем ассоциацию подписки на сотрудников
		createAssociation(subscriptionsRef.getChildRef(), employee, ASSOC_DESTINATION_EMPLOYEE);

		return subscriptionsRef.getChildRef();
	}

	@Override
	public NodeRef createSubscriptionToType(String name, String description, NodeRef objectType,
	                                        NodeRef eventCategory, List<NodeRef> notificationType,
	                                        List<NodeRef> employee, List<NodeRef> workGroup,
	                                        List<NodeRef> organizationUnit, List<NodeRef> position) {

		NodeRef subscriptionRootRef = getSubscriptionRootRef();

		String subscribeName;
		if ((name == null) || name.equals("")) {
			subscribeName = GUID.generate();
		} else {
			subscribeName = name;
		}
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(0);
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		QName assocQName = QName.createQName(SUBSCRIPTIONS_NAMESPACE_URI, subscribeName);

		if (description != null && description.equals("")) {
			properties.put(PROP_DESCRIPTION, description);
		}
		ChildAssociationRef subscriptionsRef = nodeService.createNode(subscriptionRootRef, assocTypeQName, assocQName,
				TYPE_SUBSCRIPTION_TO_TYPE, properties);
		// Создаем ассоциацию подписки на тип объекта
		if (objectType != null){
			nodeService.createAssociation(subscriptionsRef.getChildRef(), objectType, ASSOC_OBJECT_TYPE);
		}
		// Создаем ассоциацию подписки на категорию события
		if (eventCategory != null){
			nodeService.createAssociation(subscriptionsRef.getChildRef(), eventCategory, ASSOC_EVENT_CATEGORY);
		}
		// Создаем ассоциацию подписки на тип доставки
		createAssociation(subscriptionsRef.getChildRef(), notificationType, ASSOC_NOTIFICATION_TYPE);
		// Создаем ассоциацию подписки на сотрудников
		createAssociation(subscriptionsRef.getChildRef(), employee, ASSOC_DESTINATION_EMPLOYEE);
		// Создаем ассоциацию подписки на рабочие группы
		createAssociation(subscriptionsRef.getChildRef(), workGroup, ASSOC_DESTINATION_WORK_GROUP);
		// Создаем ассоциацию подписки на подразделения
		createAssociation(subscriptionsRef.getChildRef(), organizationUnit, ASSOC_DESTINATION_ORGANIZATION_UNIT);
		// Создаем ассоциацию подписки на должностные позиции
		createAssociation(subscriptionsRef.getChildRef(), position, ASSOC_DESTINATION_POSITION);

		return subscriptionsRef.getChildRef();
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

	@Override
	public void unsubscribe(NodeRef nodeRef) {
		Set<QName> types = new HashSet<QName>();
		types.add(SubscriptionsService.ASSOC_SUBSCRIPTION_OBJECT);
		NodeRef objectRef = null;
		List<String> objectsList = new ArrayList<String>();
		AssociationRef objectAssocRef = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_SUBSCRIPTION_OBJECT).get(0);
		if (objectAssocRef != null)  {
			objectRef = objectAssocRef.getTargetRef();
			objectsList.add(objectRef.toString());

			try {
				if (orgstructureService.isEmployee(objectRef)) {
					businessJournalService.log(nodeRef, EventCategory.DELETE, "Сотрудник #initiator отменил подписку на действия сотрудника  #object1", objectsList);
				} else if (orgstructureService.isWorkGroup(objectRef)) {
					businessJournalService.log(nodeRef, EventCategory.DELETE, "Сотрудник #initiator отменил подписку на действия рабочей группы  #object1", objectsList);
				} else {
					businessJournalService.log(nodeRef, EventCategory.DELETE, "Сотрудник #initiator отменил подписку на #object1", objectsList);
				}
			} catch (Exception e) {
				logger.error("Could not create the record business-journal", e);
			}
		}

		nodeService.deleteNode(nodeRef);
	}

	private boolean isBJEngineer() {
		return orgstructureService.isCurrentEmployeeHasBusinessRole(SubscriptionsService.BUSINESS_ROLE_SUBSCRIPTIONS_ENGINEER);
	}
}
