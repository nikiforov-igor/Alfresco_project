package ru.it.lecm.businessjournal.policies;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.schedule.BusinessJournalArchiveSchedule;

/**
 * @author dbashmakov
 *         Date: 24.01.13
 *         Time: 10:47
 */
public class BusinessJournalSettingsUpdatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy{

	private final static Logger logger = LoggerFactory.getLogger(BusinessJournalSettingsUpdatePolicy.class);

	private PolicyComponent policyComponent;
	private BusinessJournalArchiveSchedule archiveSchedule;

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "archiveSchedule", archiveSchedule);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				BusinessJournalService.TYPE_ARCHIVER_SETTINGS, new JavaBehaviour(this, "onUpdateProperties"));
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setArchiveSchedule(BusinessJournalArchiveSchedule archiveSchedule) {
		this.archiveSchedule = archiveSchedule;
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		Integer prevDeepValue = (Integer) before.get(BusinessJournalService.PROP_ARCHIVER_DEEP);
		Integer curDeepValue = (Integer) after.get(BusinessJournalService.PROP_ARCHIVER_DEEP);
		if (!curDeepValue.equals(prevDeepValue)) {
			archiveSchedule.getArchiverSettings().setDeep(curDeepValue.toString());
		}
		Integer prevPeriodValue = (Integer) before.get(BusinessJournalService.PROP_ARCHIVER_PERIOD);
		Integer curPeriodValue = (Integer) after.get(BusinessJournalService.PROP_ARCHIVER_PERIOD);
		if (!curPeriodValue.equals(prevPeriodValue)) {
			archiveSchedule.getArchiverSettings().setPeriod(curPeriodValue.toString());
		}
		// update trigger and job
		CronTrigger trigger = (CronTrigger)archiveSchedule.getTrigger();
		try {
			trigger.setCronExpression(archiveSchedule.getArchiverSettings().getCronExpression());
			archiveSchedule.getScheduler().rescheduleJob(archiveSchedule.getTriggerName(), archiveSchedule.getTriggerGroup(), trigger);
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid chron expression: n" + archiveSchedule.getArchiverSettings().getCronExpression());
		} catch (SchedulerException e) {
			logger.error("Не удалось обновить параметры автоматического архивирования", e);
		}
	}
}
