package ru.it.lecm.notifications.dashlet.schedule;

import org.alfresco.model.ContentModel;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.notifications.dashlet.beans.NotificationsDashletChannel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.transaction.UserTransaction;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: AIvkin
 * Date: 25.01.13
 * Time: 9:22
 */
public class NotificationsDashletDeleteSchedule extends BaseTransactionalSchedule {
	private final static Logger logger = LoggerFactory.getLogger(NotificationsDashletDeleteSchedule.class);

	private static final int MAX_COUNT_RECORDS = 500;

	private NamespaceService namespaceService;
	private NotificationsDashletChannel notificationsDashletChannel;

	public NotificationsDashletDeleteSchedule() {
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNotificationsDashletChannel(NotificationsDashletChannel notificationsDashletChannel) {
		this.notificationsDashletChannel = notificationsDashletChannel;
	}

	/**
	 * Выборка уведомлений дашлета, которые нужно удалить
	 * @return список ссылок на элементы для удаления
	 */
	@Override
	public List<NodeRef> getNodesInTx() {		
		Set<NodeRef> nodes = new HashSet<>();
		
		UserTransaction userTransaction = getTransactionService().getUserTransaction();
        try
        {
            userTransaction.begin();
            nodes.addAll(getOldNotifications());

    		Set<QName> typeSet = new HashSet<>();
    		typeSet.add(ContentModel.TYPE_FOLDER);
    		List<ChildAssociationRef> employeeFolders = nodeService.getChildAssocs(notificationsDashletChannel.getRootRef(), typeSet);
    		if (employeeFolders != null) {
    			for (ChildAssociationRef folderAssocRef: employeeFolders) {
    				NodeRef folderRef = folderAssocRef.getChildRef();
    				nodes.addAll(getGreaterMaxNotifications(folderRef));
    			}
    		}
			userTransaction.commit();
        }
        catch(Throwable e)
        {
            // rollback the transaction
            try
            { 
                if (userTransaction != null) 
                {
                    userTransaction.rollback();
                }
            }
            catch (Exception ex)
            {
                // NOOP 
            }
            throw new AlfrescoRuntimeException("Service folders [notifications-dashlet] bootstrap failed", e);
        }

		return new ArrayList<>(nodes);
	}

	/**
	 * Получение непрочитанных уведомлений старше месяца
	 * @return список ссылок на элементы для удаления
	 */
	public List<NodeRef> getOldNotifications() {
		List<NodeRef> result = new ArrayList<>();
		String path = nodeService.getPath(notificationsDashletChannel.getRootRef()).toPrefixString(namespaceService);
		String type = NotificationsDashletChannel.TYPE_NOTIFICATION_DASHLET.toPrefixString(namespaceService);
		String formingDateField = "@" + NotificationsService.PROP_FORMING_DATE.toPrefixString(namespaceService).replace(":", "\\:");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		String maxDate = new SimpleDateFormat("yyyy\\-MM\\-dd").format(cal.getTime());

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.addSort("@" + NotificationsService.PROP_FORMING_DATE, false);
		parameters.setMaxItems(Integer.MAX_VALUE);
		parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\" AND " +
				formingDateField + ":[MIN TO " + maxDate + "]");
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			for (ResultSetRow row : resultSet) {
				NodeRef node = row.getNodeRef();
				result.add(node);
			}
		} catch (Exception e) {
			logger.error("Error while getting notifications records", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return result;
	}

	/**
	 * Получение самых старых уведомлений, при их количестве больше 50
	 *
	 * @param folderRef ссылка на папку уведомлений для пользователя
	 * @return  список ссылок на элементы для удаления
	 */
	public List<NodeRef> getGreaterMaxNotifications(NodeRef folderRef) {
		List<NodeRef> result = new ArrayList<>();
		String path = nodeService.getPath(folderRef).toPrefixString(namespaceService);
		String type = NotificationsDashletChannel.TYPE_NOTIFICATION_DASHLET.toPrefixString(namespaceService);

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.addSort("@" + NotificationsService.PROP_FORMING_DATE, false);
		parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\"");
		parameters.setSkipCount(MAX_COUNT_RECORDS);
		parameters.setMaxItems(10000);
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			for (ResultSetRow row : resultSet) {
				NodeRef node = row.getNodeRef();
				result.add(node);
			}
		} catch (Exception e) {
			logger.error("Error while getting notifications records", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return result;
	}
}
