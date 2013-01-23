package ru.it.lecm.notifications.dashlet.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 17.01.13
 * Time: 15:19
 *
 * Сервис канала уведомления для дашлета
 */
public class NotificationsDashletChannel implements NotificationChannelBeanBase {
	public static final String NOTIFICATIONS_DASHLET_ROOT_NAME = "Дашлет";
	public static final String NOTIFICATIONS_DASHLET_ASSOC_QNAME = "dashlet";

	public static final String NOTIFICATIONS_DASHLET_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/dashlet/1.0";
	public final QName TYPE_NOTIFICATION_DASHLET = QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, "notification");

	private ServiceRegistry serviceRegistry;
	protected NodeService nodeService;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	protected NotificationsService notificationsService;
	private NodeRef rootRef;

    private final Object lock = new Object();

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 */
	public void init() {
		final String rootName = NOTIFICATIONS_DASHLET_ROOT_NAME;
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
							QName assocQName = QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, NOTIFICATIONS_DASHLET_ASSOC_QNAME);
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
	 * Создание уведомления для дашлета
	 *
	 * @param notification Атомарное уведомление
	 * @return Ссылка на уведомление для дашлета
	 */
	private NodeRef createNotification(NotificationUnit notification) {
        Date date = new Date();
        String employeeName = (String) nodeService.getProperty(notification.getRecipientRef(), ContentModel.PROP_NAME);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);

        properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
		properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
		properties.put(NotificationsService.PROP_FORMING_DATE, date);

        final NodeRef directoryRef = getFolder(this.rootRef, employeeName, date);

        ChildAssociationRef associationRef = nodeService.createNode(directoryRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, GUID.generate()),
				TYPE_NOTIFICATION_DASHLET, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
		return result;
	}

    /**
     * Метод, возвращающий ссылку на директорию в директории "Уведомления" согласно заданным параметрам
     *
     * @param date - текущая дата
     * @param employeeName - имя сотрудника
     * @param root - корень, относительно которого строится путь
     * @return ссылка на директорию
     */
    private NodeRef getFolder(final NodeRef root, final String employeeName, final Date date) {
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                    // имя директории "Корень/Имя пользователя/ГГГГ/ММ/ДД"
                        NodeRef directoryRef;
                        synchronized (lock) {
                            List<String> directoryPaths = new ArrayList<String>(3);
                            if (employeeName != null) {
                                directoryPaths.add(employeeName);
                            }
                            directoryPaths.add(FolderNameFormatYear.format(date));
                            directoryPaths.add(FolderNameFormatMonth.format(date));
                            directoryPaths.add(FolderNameFormatDay.format(date));

                            directoryRef = root;
                            for (String pathString : directoryPaths) {
                                NodeRef pathDir = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
                                if (pathDir == null) {
                                    QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                    QName assocQName = QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, pathString);
                                    QName nodeTypeQName = ContentModel.TYPE_FOLDER;
                                    Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                    properties.put(ContentModel.PROP_NAME, pathString);
                                    ChildAssociationRef result = nodeService.createNode(directoryRef, assocTypeQName, assocQName, nodeTypeQName, properties);
                                    directoryRef = result.getChildRef();
                                } else {
                                    directoryRef = pathDir;
                                }
                            }
                        }
                        return directoryRef;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }
}
