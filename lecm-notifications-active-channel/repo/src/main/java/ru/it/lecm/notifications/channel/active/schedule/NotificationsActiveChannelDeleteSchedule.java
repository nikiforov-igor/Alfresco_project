package ru.it.lecm.notifications.channel.active.schedule;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
import ru.it.lecm.notifications.channel.active.beans.NotificationsActiveChannel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: AIvkin
 * Date: 24.01.13
 * Time: 15:01
 */
public class NotificationsActiveChannelDeleteSchedule extends BaseTransactionalSchedule {
	private final static Logger logger = LoggerFactory.getLogger(NotificationsActiveChannelDeleteSchedule.class);

	private static final int MAX_COUNT_RECORDS = 50;

	/*
 * The cron expression
 */
	private NamespaceService namespaceService;
	private NotificationsActiveChannel notificationsActiveChannel;
	private int deleteUnreadOlderThan = -1;

	public NotificationsActiveChannelDeleteSchedule() {
	}

	public void setNotificationsActiveChannel(NotificationsActiveChannel notificationsActiveChannel) {
		this.notificationsActiveChannel = notificationsActiveChannel;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDeleteUnreadOlderThan(int deleteUnreadOlderThan) {
		this.deleteUnreadOlderThan = deleteUnreadOlderThan;
	}

	/**
	 * Выборка уведомлений активного канала, которые нужно удалить
	 * @return список ссылок на элементы для удаления
	 */
	@Override
	public List<NodeRef> getNodesInTx() {
		Set<NodeRef> nodes = new HashSet<>();
		nodes.addAll(getOldNotifications());
		nodes.addAll(getOldUnreadedNotifications());

		Set<QName> typeSet = new HashSet<>();
		typeSet.add(ContentModel.TYPE_FOLDER);
		List<ChildAssociationRef> employeeFolders = nodeService.getChildAssocs(notificationsActiveChannel.getRootRef(), typeSet);
		if (employeeFolders != null) {
			for (ChildAssociationRef folderAssocRef: employeeFolders) {
				NodeRef folderRef = folderAssocRef.getChildRef();
				nodes.addAll(getGreaterMaxNotifications(folderRef));
			}
		}

		ArrayList<NodeRef> result = new ArrayList<>(nodes);
		return result;
	}

	/**
	 * Получение непрочитанных уведомлений старше месяца
	 * @return список ссылок на элементы для удаления
	 */
	public List<NodeRef> getOldNotifications() {
		List<NodeRef> result = new ArrayList<>();
		String path = nodeService.getPath(notificationsActiveChannel.getRootRef()).toPrefixString(namespaceService);
		String type = NotificationsActiveChannel.TYPE_NOTIFICATION_ACTIVE_CHANNEL.toPrefixString(namespaceService);
		String isReadField = "@" + NotificationsActiveChannel.PROP_IS_READ.toPrefixString(namespaceService).replace(":", "\\:");
		String formingDateField = "@" + NotificationsService.PROP_FORMING_DATE.toPrefixString(namespaceService).replace(":", "\\:");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		String maxDate = new SimpleDateFormat("yyyy\\-MM\\-dd").format(cal.getTime());

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.addSort("@" + NotificationsService.PROP_FORMING_DATE, false);
		parameters.setMaxItems(Integer.MAX_VALUE);
		parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\" AND " + isReadField + ":true" +
				" AND " + formingDateField + ":[MIN TO " + maxDate + "]");
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
		String type = NotificationsActiveChannel.TYPE_NOTIFICATION_ACTIVE_CHANNEL.toPrefixString(namespaceService);
		String isReadField = "@" + NotificationsActiveChannel.PROP_IS_READ.toPrefixString(namespaceService).replace(":", "\\:");

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.addSort("@" + NotificationsService.PROP_FORMING_DATE, false);
		parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\" AND " + isReadField + ":true");
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

	/**
	 * Получение непрочитанных уведомлений старше месяца
	 * @return список ссылок на элементы для удаления
	 */
	public List<NodeRef> getOldUnreadedNotifications() {
		List<NodeRef> result = new ArrayList<>();

		if (deleteUnreadOlderThan > 0) {
			String path = nodeService.getPath(notificationsActiveChannel.getRootRef()).toPrefixString(namespaceService);
			String type = NotificationsActiveChannel.TYPE_NOTIFICATION_ACTIVE_CHANNEL.toPrefixString(namespaceService);
			String isReadField = "@" + NotificationsActiveChannel.PROP_IS_READ.toPrefixString(namespaceService).replace(":", "\\:");
			String formingDateField = "@" + NotificationsService.PROP_FORMING_DATE.toPrefixString(namespaceService).replace(":", "\\:");

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, -deleteUnreadOlderThan);
			String maxDate = new SimpleDateFormat("yyyy\\-MM\\-dd").format(cal.getTime());

			SearchParameters parameters = new SearchParameters();
			parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
			parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			parameters.addSort("@" + NotificationsService.PROP_FORMING_DATE, false);
			parameters.setMaxItems(Integer.MAX_VALUE);
			parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\" AND " + isReadField + ":false" +
					" AND " + formingDateField + ":[MIN TO " + maxDate + "]");
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
		}
		return result;
	}
}
