package ru.it.lecm.notifications.dashlet.beans;

import org.alfresco.model.ContentModel;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: AIvkin Date: 17.01.13 Time: 15:19
 *
 * Сервис канала уведомления для дашлета
 */
public class NotificationsDashletChannel extends NotificationChannelBeanBase {

    final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static final String NOTIFICATIONS_DASHLET_ROOT_NAME = "Дашлет";
    public static final String NOTIFICATIONS_DASHLET_ROOT_ID = "NOTIFICATIONS_DASHLET_ROOT_ID";
    public static final String NOTIFICATIONS_DASHLET_ASSOC_QNAME = "dashlet";

    public static final String NOTIFICATIONS_DASHLET_NAMESPACE_URI = "http://www.it.ru/lecm/notifications/dashlet/1.0";
    public static final QName TYPE_NOTIFICATION_DASHLET = QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, "notification");

    private final static Logger logger = LoggerFactory.getLogger(NotificationsDashletChannel.class);


    protected NotificationsService notificationsService;
    private NamespaceService namespaceService;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public NodeRef getRootRef() {
        return getFolder(NOTIFICATIONS_DASHLET_ROOT_ID);
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    /**
     * Метод инициализвции сервиса Создает рабочую директорию - если она еще не
     * создана.
     */
    public void init() {
        //создание каталога вынесено в service-context
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
     * Создание уведомления для дашлета
     *
     * @param notification Атомарное уведомление
     * @return Ссылка на уведомление для дашлета
     */
    //TODO DONE Refactoring in progress
    private NodeRef createNotification(NotificationUnit notification) throws WriteTransactionNeededException {
        String employeeName = (String) nodeService.getProperty(notification.getRecipientRef(), ContentModel.PROP_NAME);
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);

        properties.put(NotificationsService.PROP_AUTOR, notification.getAutor());
        properties.put(NotificationsService.PROP_DESCRIPTION, notification.getDescription());
        properties.put(NotificationsService.PROP_FORMING_DATE, notification.getFormingDate());

        NodeRef rootRef = getRootRef();
        List<String> directoryPath = getDirectoryPath(employeeName, null);
        NodeRef saveDirectoryRef = getFolder(rootRef, directoryPath);
        //Судя по тому, что нода создаётся, транзакция должна быть.
        if (null == saveDirectoryRef) {
            saveDirectoryRef = createPath(rootRef, directoryPath);
        }
        
        ChildAssociationRef associationRef = nodeService.createNode(saveDirectoryRef, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NOTIFICATIONS_DASHLET_NAMESPACE_URI, GUID.generate()),
                TYPE_NOTIFICATION_DASHLET, properties);

        NodeRef result = associationRef.getChildRef();

        // создаем ассоциации
        nodeService.createAssociation(result, notification.getRecipientRef(), NotificationsService.ASSOC_RECIPIENT);
        return result;
    }

    /**
     * Метод, возвращающий список ссылок на уведомления, сформированные за
     * заданный период
     *
     * @param begin - начальная дата
     * @param end - конечная дата
     * @return список ссылок
     */
    public List<NodeRef> getRecordsByInterval(Date begin, Date end) {
        List<NodeRef> records = new ArrayList<NodeRef>(10);
        NodeRef employeeDirectoryRef = getCurrentEmployeeFolder(getRootRef());

        if (employeeDirectoryRef != null) {
            String path = nodeService.getPath(employeeDirectoryRef).toPrefixString(namespaceService);
            String type = TYPE_NOTIFICATION_DASHLET.toPrefixString(namespaceService);
            final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
            final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";
            ResultSet results = null;
            String query;
            SearchParameters sp = new SearchParameters();

            sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            sp.setLanguage(SearchService.LANGUAGE_LUCENE);
            query = " +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\" AND @lecm\\-notf\\:forming\\-date:[" + MIN + " TO " + MAX + "]";
            sp.addSort("@" + NotificationsService.PROP_FORMING_DATE, false);
            sp.setQuery(query);
            try {
                results = serviceRegistry.getSearchService().query(sp);
                for (ResultSetRow row : results) {
                    NodeRef currentNodeRef = row.getNodeRef();
                    if (!isArchive(currentNodeRef)) {
                        records.add(currentNodeRef);
                    }
                }
            } catch (Exception e) {
                logger.error("Error while getting notifications records", e);
            } finally {
                if (results != null) {
                    results.close();
                }
            }
        }
        return records;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getRootRef();
    }
}
