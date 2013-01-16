package ru.it.lecm.notifications.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 10.01.13
 * Time: 16:53
 */
public class NotificationsServiceImpl extends BaseBean implements NotificationsService {
	final private static Logger logger = LoggerFactory.getLogger(NotificationsServiceImpl.class);

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;
	private OrgstructureBean orgstructureService;

	private NodeRef notificationsRootRef;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	/**
	 * Метод инициализвции сервиса
	 * Создает рабочую директорию - если она еще не создана.
	 * Записывает в свойства сервиса nodeRef директории с уведомлениями
	 */
	public void init() {
		final String rootName = NOTIFICATIONS_ROOT_NAME;
		repositoryHelper.init();
		nodeService = serviceRegistry.getNodeService();
		transactionService = serviceRegistry.getTransactionService();

		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef rootRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, rootName);
						if (rootRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(NOTIFICATIONS_NAMESPACE_URI, NOTIFICATIONS_ASSOC_QNAME);
							QName nodeTypeQName = ContentModel.TYPE_FOLDER;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, rootName);
							ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
							rootRef = associationRef.getChildRef();
						}
						return rootRef;
					}
				});
			}
		};
		notificationsRootRef = AuthenticationUtil.runAsSystem(raw);
	}

	/**
	 * Отправка уведомлений
	 *
	 * @param notification Обобщённое уведомление
	 */
	public boolean sendNotification(Notification notification) {
		if (checkNotification(notification)) {
			NodeRef generalizedNotification = createGeneralizedNotification(notification);
			return generalizedNotification != null;
		} else {
			return false;
		}
	}

	private NodeRef createGeneralizedNotification(Notification notification) {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(7);
		properties.put(PROP_GENERAL_AUTOR, notification.getAutor());
		properties.put(PROP_GENERAL_DESCRIPTION, notification.getDescription());
		properties.put(PROP_GENERAL_FORMING_DATE, new Date());

		ChildAssociationRef associationRef = nodeService.createNode(this.notificationsRootRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NOTIFICATIONS_NAMESPACE_URI, GUID.generate()), TYPE_GENERALIZED_NOTIFICATION, properties);

		NodeRef result = associationRef.getChildRef();

		// создаем ассоциации
		nodeService.createAssociation(result, notification.getObjectRef(), ASSOC_NOTIFICATION_OBJECT);
		if (notification.getTypeRefs() != null) {
			for (NodeRef ref : notification.getTypeRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_NOTIFICATION_TYPE);
			}
		}
		if (notification.getRecipientEmployeeRefs() != null) {
			for (NodeRef ref : notification.getRecipientEmployeeRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_EMPLOYEE);
			}
		}
		if (notification.getRecipientOrganizationUnitRefs() != null) {
			for (NodeRef ref : notification.getRecipientOrganizationUnitRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_ORGANIZATION_UNIT);
			}
		}
		if (notification.getRecipientWorkGroupRefs() != null) {
			for (NodeRef ref : notification.getRecipientWorkGroupRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_WORK_GROUP);
			}
		}
		if (notification.getRecipientPositionRefs() != null) {
			for (NodeRef ref : notification.getRecipientPositionRefs()) {
				nodeService.createAssociation(result, ref, ASSOC_RECIPIENT_POSITION);
			}
		}
		return result;
	}

	private boolean checkNotification(Notification notification) {
		if (notification == null) {
			logger.warn("Уведомление null");
			return false;
		}
		if (notification.getAutor() == null) {
			logger.warn("Автор уведомление null");
			return false;
		}
		if (!nodeService.exists(notification.getObjectRef())) {
			logger.warn("Ссылка на объект уведомление не существует");
			return false;
		}
		if ((notification.getRecipientEmployeeRefs() == null || notification.getRecipientEmployeeRefs().size() == 0) &&
				(notification.getRecipientOrganizationUnitRefs() == null || notification.getRecipientOrganizationUnitRefs().size() == 0) &&
				(notification.getRecipientPositionRefs() == null || notification.getRecipientPositionRefs().size() == 0) &&
				(notification.getRecipientWorkGroupRefs() == null || notification.getRecipientWorkGroupRefs().size() == 0)) {
			logger.warn("Должен быть хотя бы один получатель уведомления");
			return false;
		}
		return true;
	}
}
