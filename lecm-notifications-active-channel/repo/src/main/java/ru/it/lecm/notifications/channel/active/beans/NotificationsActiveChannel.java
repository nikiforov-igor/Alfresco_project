package ru.it.lecm.notifications.channel.active.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.notifications.beans.NotificationChannelBeanBase;
import ru.it.lecm.notifications.beans.NotificationUnit;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: AIvkin Date: 17.01.13 Time: 15:19
 * <p/>
 * Сервис активного канала уведомлений
 */
public class NotificationsActiveChannel extends NotificationChannelBeanBase {

    public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_NAME = "Активный канал";
    public static final String NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_ID = "NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_ID";

    public static final String NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/channel/active/1.0";
    public static final QName TYPE_NOTIFICATION_ACTIVE_CHANNEL = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "notification");
    public static final QName PROP_READ_DATE = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "read-date");
    public static final QName PROP_IS_READ = QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, "is_read");

    private final static Logger logger = LoggerFactory.getLogger(NotificationsActiveChannel.class);

    protected NotificationsService notificationsService;
    private SearchService searchService;
    private NamespaceService namespaceService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public NodeRef getRootRef() {
        return getFolder(NOTIFICATIONS_ACTIVE_CHANNEL_ROOT_ID);
    }

    /**
     * Метод инициализвции сервиса Создает рабочую директорию - если она еще не
     * создана.
     */
    public void init() {
    }

    @Override
    public boolean sendNotification(NotificationUnit notification) {
        try {
            return createNotification(notification) != null;
        } catch (WriteTransactionNeededException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Создание уведомления активного канала
     *
     * @param notification Атомарное уведомление
     * @return Ссылка на уведомление активного канала
     */
    private NodeRef createNotification(NotificationUnit notification) throws WriteTransactionNeededException {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
        String employeeName = (String) nodeService.getProperty(notification.getRecipientRef(), ContentModel.PROP_NAME);
        properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
        properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
        properties.put(NotificationsService.PROP_FORMING_DATE, notification.getFormingDate());
        properties.put(PROP_IS_READ, false);

        NodeRef rootRef = getRootRef();
        List<String> directoryPath = getDirectoryPath(employeeName, null);
        NodeRef saveDirectoryRef = getFolder(rootRef, directoryPath);
        //Судя по тому, что нода создаётся, транзакция должна быть.
        if (null == saveDirectoryRef) {
            saveDirectoryRef = createPath(rootRef, directoryPath);
        }
        
        ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NOTIFICATIONS_ACTIVE_CHANNEL_NAMESPACE_URI, GUID.generate()),
                TYPE_NOTIFICATION_ACTIVE_CHANNEL, properties);

        NodeRef result = associationRef.getChildRef();

        // создаем ассоциации
        nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
        return result;
    }

    /**
     * Проверяет, что NodeRef является ссылкой на уведомление активного канала
     *
     * @param ref ссылка на элемент
     * @return true если NodeRef является ссылкой на уведомление активного
     * канала иначе false
     */
    public boolean isActiveChannelNotification(NodeRef ref) {
        Set<QName> types = new HashSet<QName>();
        types.add(TYPE_NOTIFICATION_ACTIVE_CHANNEL);
        return isProperType(ref, types);
    }

    /**
     * Проверка, что уведомление является новым
     *
     * @param ref ссылка на уведомление
     * @return true если дата прочтения равна null иначе false
     */
    public boolean isNewNotification(NodeRef ref) {
        return ref != null && nodeService.exists(ref) && !isArchive(ref) && isActiveChannelNotification(ref)
                && !((Boolean) nodeService.getProperty(ref, PROP_IS_READ));
    }

    /**
     * Получение количества новых уведомлений
     *
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
     *
     * @param skipCount количество пропущенных записей
     * @param maxItems максимальное количество возвращаемых элементов
     * @return список ссылок на уведомления
     */
    public List<NodeRef> getNotifications(int skipCount, int maxItems, List<String> ignoreNotifications) {
        List<NodeRef> result = new ArrayList<NodeRef>();

        NodeRef currentEmloyeeNodeRef = orgstructureService.getCurrentEmployee();
        if (currentEmloyeeNodeRef != null) {
            List<AssociationRef> lRefs = nodeService.getSourceAssocs(currentEmloyeeNodeRef, NotificationsService.ASSOC_RECIPIENT);
            List<NodeRef> filteredNotifications = filterNotifications(lRefs, ignoreNotifications);
            int endIndex = (skipCount + maxItems) < filteredNotifications.size() ? (skipCount + maxItems) : filteredNotifications.size();

            for (int i = skipCount; i < endIndex; i++) {
                result.add(filteredNotifications.get(i));
            }
        }

        return result;
    }

    private List<NodeRef> filterNotifications(List<AssociationRef> notifications, List<String> ignoreNotifications) {
        ArrayList<NodeRef> result = new ArrayList<NodeRef>();
        if (notifications != null) {
            for (AssociationRef ref : notifications) {
                NodeRef notificationRef = ref.getSourceRef();
                if (isActiveChannelNotification(notificationRef) && !isArchive(notificationRef) && !ignoreNotifications.contains(notificationRef.toString())) {
                    result.add(notificationRef);
                }
            }
        }
        Collections.sort(result, new Comparator<NodeRef>() {
            public int compare(NodeRef o1, NodeRef o2) {
                Boolean isRead1 = (Boolean) nodeService.getProperty(o1, PROP_IS_READ);
                Boolean isRead2 = (Boolean) nodeService.getProperty(o2, PROP_IS_READ);

                int result = isRead1.compareTo(isRead2);
                if (result == 0) {
                    Date formingDate1 = (Date) nodeService.getProperty(o1, NotificationsService.PROP_FORMING_DATE);
                    Date formingDate2 = (Date) nodeService.getProperty(o2, NotificationsService.PROP_FORMING_DATE);

                    return -formingDate1.compareTo(formingDate2);
                }
                return result;
            }
        });
        return result;
    }

    /**
     * Выставление времени прочтения уведомления
     *
     * @param nodeRefs список ссылок на уведомления
     */
    public void setReadNotifications(List<NodeRef> nodeRefs) {
        if (nodeRefs != null) {
            for (NodeRef ref : nodeRefs) {
                //TODO DONE замена нескольких setProperty на setProperties.
                Map<QName, Serializable> properties = nodeService.getProperties(ref);
                properties.put(PROP_READ_DATE, new Date());
                properties.put(PROP_IS_READ, true);
                nodeService.setProperties(ref, properties);

            }
        }
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getRootRef();
    }
}
