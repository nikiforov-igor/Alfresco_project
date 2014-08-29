package ru.it.lecm.notifications.dashlet.schedule;

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
import ru.it.lecm.notifications.dashlet.beans.NotificationsDashletChannel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: AIvkin
 * Date: 25.01.13
 * Time: 9:22
 */
public class NotificationsDashletDeleteSchedule extends AbstractScheduledAction {
	private final static Logger logger = LoggerFactory.getLogger(NotificationsDashletDeleteSchedule.class);

	private static final int MAX_COUNT_RECORDS = 500;

	/*
 * The cron expression
 */
	private String cronExpression;

	private String firstStartExpression = "0 */15 * * * ?";

	private boolean onServerStart = false;

	/*
	 * The name of the job
	 */
	private String jobName = "notificationa-dashlet-cleaner";

	/*
	 * The job group
	 */
	private String jobGroup = "notifications-dashlet";

	/*
	 * The name of the trigger
	 */
	private String triggerName = "notifications-dashlet-delete-trigger";

	/*
	 * The name of the trigger group
	 */
	private String triggerGroup = "notifications-dashlet-trigger";

	/*
	 * The scheduler
	 */
	private Scheduler scheduler;

	private SearchService searchService;
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private NotificationsDashletChannel notificationsDashletChannel;

	public NotificationsDashletDeleteSchedule() {
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
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

	public void setFirstStartExpression(String firstStartExpression) {
		this.firstStartExpression = firstStartExpression;
	}

	public void setOnServerStart(boolean onServerStart) {
		this.onServerStart = onServerStart;
	}

	public void setNotificationsDashletChannel(NotificationsDashletChannel notificationsDashletChannel) {
		this.notificationsDashletChannel = notificationsDashletChannel;
	}

	public String getCronExpression() {
		return cronExpression;
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

	public void afterPropertiesSet() throws Exception {
		register(getScheduler());
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
	 * Выборка уведомлений дашлета, которые нужно удалить
	 * @return список ссылок на элементы для удаления
	 */
	@Override
	public List<NodeRef> getNodes() {
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

		Set<QName> typeSet = new HashSet<>();
		typeSet.add(ContentModel.TYPE_FOLDER);
		List<ChildAssociationRef> employeeFolders = nodeService.getChildAssocs(notificationsDashletChannel.getRootRef(), typeSet);
		if (employeeFolders != null) {
			for (ChildAssociationRef folderAssocRef: employeeFolders) {
				NodeRef folderRef = folderAssocRef.getChildRef();
				nodes.addAll(getGreaterMaxNotifications(folderRef));
			}
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

	/* (non-Javadoc)
 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getAction(org.alfresco.service.cmr.repository.NodeRef)
 */
	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("deleteAction");
	}
}
