package ru.it.lecm.subscriptions.schedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.subscriptions.beans.SubscriptionsBean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 17.01.13
 * Time: 11:09
 */
public class BusinessJournalSchedule extends AbstractScheduledAction {

	private final static Logger logger = LoggerFactory.getLogger(BusinessJournalSchedule.class);
	/*
	 * The search service.
	 */
	private SearchService searchService;
	private NodeService nodeService;

	private NamespaceService namespaceService;

	/*
	 * The cron expression
	 */
	private String cronExpression;

	/*
	 * The name of the job
	 */
	private String jobName = "business-journal-receiver";

	/*
	 * The job group
	 */
	private String jobGroup = "subscriptions";

	/*
	 * The name of the trigger
	 */
	private String triggerName = "business-journal-receiver-triger";

	/*
	 * The name of the trigger group
	 */
	private String triggerGroup = "subscriptions-triger";

	/*
	 * The scheduler
	 */
	private Scheduler scheduler;
	private BusinessJournalService businessJournalService;

	/**
	 * Default constructore
	 */
	public BusinessJournalSchedule() {
		super();
	}

	/**
	 * Set the search service.
	 *
	 * @param searchService
	 */
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	/**
	 * Set the business journal service.
	 *
	 * @param businessJournalService
	 */

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	/**
	 * Set the node service.
	 *
	 * @param nodeService
	 */
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Set the namespaceService service.
	 *
	 * @param namespaceService
	 */
	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}


	/**
	 * Get the scheduler.
	 *
	 * @return - the scheduler.
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Set the scheduler.
	 *
	 * @param scheduler
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}



	/* (non-Javadoc)
	 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getTrigger()
	 */
	@Override
	public Trigger getTrigger() {
		try {
			return new CronTrigger(getTriggerName(), getTriggerGroup(), getCronExpression());
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid chron expression: n" + getCronExpression());
		}
	}

	/* (non-Javadoc)
	 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getNodes()
	 */

	/**
	 * Выборка записей из бизнес журнала записей по которым еще не проводилась рассылка оповещенний
	 * @return
	 */
	@Override
	public List<NodeRef> getNodes() {
		NodeRef businessJournalRoot = businessJournalService.getBusinessJournalDirectory();
		String path = nodeService.getPath(businessJournalRoot).toPrefixString(namespaceService);
		String type = BusinessJournalService.TYPE_BR_RECORD.toPrefixString(namespaceService);
		String aspect = SubscriptionsBean.ASPECT_SUBSCRIBED.toPrefixString(namespaceService);

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery(" +PATH:\"" + path + "//*\" AND TYPE:\"" + type +"\" AND NOT EXACTASPECT:\"" + aspect + "\"");
		ArrayList<NodeRef> result = new ArrayList<NodeRef>();
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(parameters);
			for (ResultSetRow row : resultSet) {
				NodeRef node = row.getNodeRef();
				if (!nodeService.hasAspect(node, SubscriptionsBean.ASPECT_SUBSCRIBED)) {
					result.add(node);
				}
			}
		} catch (LuceneQueryParserException e) {
		} catch (Exception e1) {
			logger.error("Error while getting business journal's records without sending notification for subscribe", e1);
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
		// Use the template to build its action
		return getActionService().createAction("businessJournalScheduleExecutor");
	}

	/**
	 * Set the cron expression - see the wiki for examples.
	 *
	 * @param cronExpression
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}


	/**
	 * Get the cron expression.
	 *
	 * @return - the cron expression.
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * Set the job name.
	 *
	 * @param jobName
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Get the job name
	 *
	 * @return - the job name.
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Set the job group.
	 *
	 * @param jobGroup
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	/**
	 * Get the job group.
	 *
	 * @return - the job group.
	 */
	public String getJobGroup() {
		return jobGroup;
	}

	/**
	 * Set the trigger name.
	 *
	 * @param triggerName
	 */
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	/**
	 * Get the trigger name
	 *
	 * @return - the trigger name.
	 */
	public String getTriggerName() {
		return triggerName;
	}

	/**
	 * Set the trigger group.
	 *
	 * @param triggerGroup
	 */
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	/**
	 * Get the name of the trigger group.
	 *
	 * @return - the trigger group.
	 */
	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	/**
	 * Register with the scheduler.
	 *
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		register(getScheduler());
	}

}
