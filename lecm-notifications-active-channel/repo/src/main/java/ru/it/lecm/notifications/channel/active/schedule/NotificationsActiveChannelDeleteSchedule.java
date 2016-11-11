package ru.it.lecm.notifications.channel.active.schedule;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
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
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
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
	private String cronExpression;

	private String firstStartExpression = "0 */15 * * * ?";

	private boolean onServerStart = false;

	/*
	 * The name of the job
	 */
	private String jobName = "notificationa-active-channel-cleaner";

	/*
	 * The job group
	 */
	private String jobGroup = "notifications-active-channel";

	/*
	 * The name of the trigger
	 */
	private String triggerName = "notifications-active-channel-delete-trigger";

	/*
	 * The name of the trigger group
	 */
	private String triggerGroup = "notifications-active-channel-trigger";

	/*
	 * The scheduler
	 */
	private Scheduler scheduler;

	private SearchService searchService;
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private NotificationsActiveChannel notificationsActiveChannel;
	private int deleteUnreadOlderThan = -1;

	public NotificationsActiveChannelDeleteSchedule() {
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setNotificationsActiveChannel(NotificationsActiveChannel notificationsActiveChannel) {
		this.notificationsActiveChannel = notificationsActiveChannel;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}


	public String getCronExpression() {
		return cronExpression;
	}

	public void setFirstStartExpression(String firstStartExpression) {
		this.firstStartExpression = firstStartExpression;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	public void setDeleteUnreadOlderThan(int deleteUnreadOlderThan) {
		this.deleteUnreadOlderThan = deleteUnreadOlderThan;
	}

	public void afterPropertiesSet() throws Exception {
		register(getScheduler());
	}

	public void setOnServerStart(boolean onServerStart) {
		this.onServerStart = onServerStart;
	}

	/* (non-Javadoc)
 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getTrigger()
 */
	@Override
	public Trigger getTrigger() {
		try {
			CronTrigger trigger = new CronTrigger(getTriggerName(), getTriggerGroup(), onServerStart ? firstStartExpression : cronExpression);
			trigger.setJobName(getJobName());
			trigger.setJobGroup(getJobGroup());
			return trigger;
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid chron expression: n" + getCronExpression());
		}
	}

	/**
	 * Выборка уведомлений активного канала, которые нужно удалить
	 * @return список ссылок на элементы для удаления
	 */
	@Override
	public List<NodeRef> getNodesInTx() {
		if (onServerStart) { // если был запуск на старте - подменяем триггер на основной
			CronTrigger trigger = (CronTrigger) getTrigger();
			try {
				trigger.setCronExpression(cronExpression);
				getScheduler().rescheduleJob(getTriggerName(), getTriggerGroup(), trigger);
				onServerStart = false; // включаем основной триггер
			} catch (final ParseException | SchedulerException ex) {
				logger.error("Error rescheduleJob" + ex);
			}
		}

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

	/* (non-Javadoc)                            ``
 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getAction(org.alfresco.service.cmr.repository.NodeRef)
 */
	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("deleteAction");
	}
}
