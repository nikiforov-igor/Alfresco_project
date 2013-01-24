package ru.it.lecm.notifications.channel.active.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 17.01.13
 * Time: 15:19
 * <p/>
 * Сервис активного канала уведомлений
 */
public class NotificationsActiveChannel extends NotificationChannelBeanBase {
	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_NAME = "Активный канал";
	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ASSOC_QNAME = "active_channel";

	public static final String NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/channel/active/1.0";
	public static final QName TYPE_NOTIFICATION_ACTIVE_CHANNEL = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "notification");
	public final QName PROP_READ_DATE = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "read-date");

	private final static Logger logger = LoggerFactory.getLogger(NotificationsActiveChannel.class);

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	protected NotificationsService notificationsService;
	private OrgstructureBean orgstructureService;
	private SearchService searchService;
	private NamespaceService namespaceService;
	private NodeRef rootRef;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public NodeRef getRootRef() {
		return rootRef;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 */
	public void init() {
		final String rootName = NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_NAME;
		repositoryHelper.init();
		nodeService = serviceRegistry.getNodeService();
		transactionService = serviceRegistry.getTransactionService();

		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef rootRef = nodeService.getChildByName(notificationsService.getNotificationsRootRef(), ContentModel.ASSOC_CONTAINS, rootName);
						if (rootRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, NOTIFICATIONS_ACTIVE_CHANNEL_ASSOC_QNAME);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, rootName);
							ChildAssociationRef associationRef = nodeService.createNode(notificationsService.getNotificationsRootRef(), assocTypeQName, assocQName, nodeTypeQName, properties);
							rootRef = associationRef.getChildRef();
						}
						return rootRef;
					}
				});
			}
		};
		this.rootRef = AuthenticationUtil.runAsSystem(raw);
	}

	@Override
	public boolean sendNotification(NotificationUnit notification) {
		return createNotification(notification) != null;
	}

	/**
	 * Создание уведомления активного канала
	 *
	 * @param notification Атомарное уведомление
	 * @return Ссылка на уведомление активного канала
	 */
	private NodeRef createNotification(NotificationUnit notification) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
		String employeeName = (String) nodeService.getProperty(notification.getRecipientRef(), ContentModel.PROP_NAME);
		properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, notification.getFormingDate());

		final NodeRef saveDirectoryRef = getFolder(transactionService, NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, this.rootRef, employeeName, notification.getFormingDate());

		ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, GUID.generate()),
				TYPE_NOTIFICATION_ACTIVE_CHANNEL, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}

	/**
	 * Метод, возвращающий ссылку на директорию пользователя в директории "Уведомления/Активный канал" согласно заданным параметрам
	 * Если такой директории нет, то она НЕ создаётся
	 *
	 * @return ссылка на директорию
	 */
	private NodeRef getCurrentEmployeeFolder() {
		NodeRef currentEmployeeNodeRef = orgstructureService.getCurrentEmployee();
		if (currentEmployeeNodeRef != null) {
			String employeeName = (String) nodeService.getProperty(currentEmployeeNodeRef, ContentModel.PROP_NAME);
			if (employeeName != null) {
				return nodeService.getChildByName(this.rootRef, ContentModel.ASSOC_CONTAINS, employeeName);
			}
		}
		return null;

	}

	/**
	 * Проверяет, что NodeRef является ссылкой на уведомление активного канала
	 * @param ref ссылка на элемент
	 * @return    true если NodeRef является ссылкой на уведомление активного канала иначе false
	 */
	public boolean isActiveChannelNotification(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_NOTIFICATION_ACTIVE_CHANNEL);
		return isProperType(ref, types);
	}

	/**
	 * Проверка, что уведомление является новым
	 * @param ref ссылка на уведомление
	 * @return    true если дата прочтения равна null иначе false
	 */
	public boolean isNewNotification(NodeRef ref) {
		return ref != null && isActiveChannelNotification(ref) && !isArchive(ref) &&
				nodeService.getProperty(ref, PROP_READ_DATE) == null;
	}

	/**
	 * Получение количества новых уведомлений
	 * @return количество новых уведомлений
	 */
	public int getNewNotificationsCount() {
		int result = 0;
		NodeRef currentEmloyeeNodeRef = orgstructureService.getCurrentEmployee();
		if (currentEmloyeeNodeRef != null) {
			List<AssociationRef> lRefs = nodeService.getSourceAssocs(currentEmloyeeNodeRef, NotificationsService.ASSOC_RECIPIENT);
			for (AssociationRef ref : lRefs) {
				if (isNewNotification(ref.getSourceRef())) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * Получение уведомлений
	 * @param skipCount количество пропущенных записей
	 * @param maxItems  максимальное количество возвращаемых элементов
	 * @return          список ссылок на уведомления
	 */
	public List<NodeRef> getNotifications(int skipCount, int maxItems) {
		List<NodeRef> result = new ArrayList<NodeRef>();

		NodeRef employeeDirectoryRef = getCurrentEmployeeFolder();
		if (employeeDirectoryRef != null) {
			String path = nodeService.getPath(employeeDirectoryRef).toPrefixString(namespaceService);
			String type = TYPE_NOTIFICATION_ACTIVE_CHANNEL.toPrefixString(namespaceService);

			SearchParameters parameters = new SearchParameters();
			parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
			parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			parameters.addSort("@" + NotificationsService.PROP_FORMING_DATE, false);
			parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\"");
			parameters.setSkipCount(skipCount);
			parameters.setMaxItems(maxItems);
			ResultSet resultSet = null;
			try {
				resultSet = searchService.query(parameters);
				for (ResultSetRow row : resultSet) {
					NodeRef node = row.getNodeRef();
					result.add(node);
				}
			} catch (LuceneQueryParserException e) {
				logger.error("Error while getting notifications records", e);
			} catch (Exception e) {
				logger.error("Error while getting notifications records", e);
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
		}

		return result;
	}

	/**
	 * Выставление времени прочтения уведомления
	 * @param nodeRefs список ссылок на уведомления
	 */
	public void setReadNotifications(List<NodeRef> nodeRefs) {
		if (nodeRefs != null) {
			for (NodeRef ref: nodeRefs) {
				nodeService.setProperty(ref, PROP_READ_DATE, new Date());
			}
		}
	}
}
