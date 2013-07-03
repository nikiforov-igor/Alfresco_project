package ru.it.lecm.wcalendar.schedule.policy;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.wcalendar.CalendarCategory;
import ru.it.lecm.wcalendar.schedule.ISchedule;

/**
 * Полиси, срабатывающее на изменение проперти у объекта типа schedule. Если
 * lecm-dic:active поменялось на false, график считается удаленным и у него
 * удаляется ассоциация с сотрудником, чтобы избежать проблем, связанных с типом
 * ассоциации. Добавить запись в бизнес-журнал.
 *
 * @author vlevin
 */
public class ScheduleDeletePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private ISchedule scheduleService;
	private PolicyComponent policyComponent;
	private final static Logger logger = LoggerFactory.getLogger(ScheduleDeletePolicy.class);

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "scheduleService", scheduleService);

		logger.info("Initializing ScheduleDeletePolicy");
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ISchedule.TYPE_SCHEDULE, new JavaBehaviour(this, "onUpdateProperties"));

	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setScheduleService(ISchedule scheduleService) {
		this.scheduleService = scheduleService;
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);

		if (curActive != null && curActive == false && curActive != prevActive) {
			scheduleService.addBusinessJournalRecord(nodeRef, CalendarCategory.DELETE_SHEDULE);
			scheduleService.unlinkSchedule(nodeRef);
		}
		logger.debug(String.format("Policy ScheduleDeletePolicy invoked on %s", nodeRef.toString()));
	}
}
