package ru.it.lecm.base.schedule;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.quartz.Scheduler;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.text.ParseException;
import java.util.*;

/**
 * User: AIvkin
 * Date: 19.09.13
 * Time: 11:59
 */
public class UserTempDirectoryCleanSchedule extends AbstractScheduledAction {
	private OrgstructureBean orgstructureService;
	private RepositoryStructureHelper repositoryStructureHelper;
	private NodeService nodeService;

	public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
		this.repositoryStructureHelper = repositoryStructureHelper;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/*
				* The cron expression
				*/
	private String cronExpression;

	/*
	 * The name of the job
	 */
	private String jobName = "usertemp-directory-cleaner";

	/*
	 * The job group
	 */
	private String jobGroup = "usertemp-directory";

	/*
	 * The name of the trigger
	 */
	private String triggerName = "usertemp-directory-clean-trigger";

	/*
	 * The name of the trigger group
	 */
	private String triggerGroup = "usertemp-directory-trigger";

	/*
	 * The scheduler
	 */
	private Scheduler scheduler;

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
			return new CronTrigger(getTriggerName(), getTriggerGroup(), getCronExpression());
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid chron expression: n" + getCronExpression());
		}
	}

	/* (non-Javadoc)
	* @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getNodes()
	*/
	@Override
	public List<NodeRef> getNodes() {
		List<NodeRef> searchFiles = new ArrayList<NodeRef>();

	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DAY_OF_YEAR, -1);
	    Date beforeFireTime = cal.getTime();

		List<NodeRef> allEmployees = this.orgstructureService.getAllEmployees();
		if (allEmployees != null) {
			Set<NodeRef> allPersons = new HashSet<NodeRef>(allEmployees.size());

			for (NodeRef employee: allEmployees) {
				allPersons.add(this.orgstructureService.getPersonForEmployee(employee));
			}
			for (NodeRef person: allPersons) {
				NodeRef tempDirectory = repositoryStructureHelper.getUserTemp(person, false);
				if (tempDirectory != null) {
					List<ChildAssociationRef> childs = nodeService.getChildAssocs(tempDirectory);
					if (childs != null) {
						for (ChildAssociationRef child: childs) {
							NodeRef nodeRef = child.getChildRef();

							Date createDate = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_CREATED);
							if (createDate != null && createDate.before(beforeFireTime)) {
								List<AssociationRef> source = nodeService.getSourceAssocs(tempDirectory, RegexQNamePattern.MATCH_ALL);
								if (source == null || source.size() == 0) {
									searchFiles.add(nodeRef);
								}
							}
						}
					}
				}
			}
		}
		return searchFiles;
	}

	/* (non-Javadoc)
	* @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getAction(org.alfresco.service.cmr.repository.NodeRef)
	*/
	@Override
	public Action getAction(NodeRef nodeRef) {
		return getActionService().createAction("deleteAction");
	}
}
