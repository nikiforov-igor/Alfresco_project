package ru.it.lecm.wcalendar.absence.policy;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.wcalendar.CalendarCategory;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Полиси, срабатывающее на изменение проперти у объекта типа absence. Если
 * lecm-dic:active поменялось на false, отсутствие считается удаленным. Следует
 * проверить, не было ли оно назначено на текущий момент и, если надо,
 * остановить. Если поменялось время конца полиси, то проверить, должно ли
 * отсутствие быть активно сейчас и поменялась ли дата конца на сегодняшнюю.
 * Если да, то отсутствие отменили и его надо остановить.
 *
 * @author vlevin
 */
public class AbsenceEndPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private IAbsence absenceService;
	private PolicyComponent policyComponent;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceEndPolicy.class);

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "absenceService", absenceService);

		logger.info("Initializing AbsenceEndPolicy");

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, IAbsence.TYPE_ABSENCE, new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final Date prevEnd = (Date) before.get(IAbsence.PROP_ABSENCE_END);
		final Date prevStart = (Date) before.get(IAbsence.PROP_ABSENCE_BEGIN);
		final Date curEnd = (Date) after.get(IAbsence.PROP_ABSENCE_END);
		final Date curStart = (Date) after.get(IAbsence.PROP_ABSENCE_BEGIN);
		final Date today = new Date();
		final NodeRef employee = absenceService.getEmployeeByAbsence(nodeRef);
		boolean deleted = false;

		// если: lecm-dic:active присутствует И нода отправилась в архив И нода раньше не была в архиве
		if (curActive != null && curActive == false && curActive != prevActive) {
			deleted = true;
			absenceService.addBusinessJournalRecord(nodeRef, CalendarCategory.DELETE_ABSENCE);
		}
		// если: нода была удалена И сегодняшняя дата позже начала отсутствия И раньше его конца
		if (deleted && today.after(curStart) && today.before(curEnd)) {
			absenceService.endAbsence(nodeRef);
			logger.debug(String.format("Policy AbsenceEndPolicy invoked on %s for employee %s", nodeRef.toString(), employee.toString()));
		}
		/*
			ALF-4912
			это условие не сработает для свежесозданного отсутствия, потому-что prevEnd==null так как нода отсутствия новая и никаких предыдущих значений у нее нет
			для отсутствий на заданный период это условие также не сработает так-как даты отсутствий не меняются после создания
			отсутствия на заданный период отключаются или по расписанию или вручную
			для бессрочных отсутствий возможна ситуация отсутствие будет деактивировано после его пролонгации ночью,
			так как сработает проверка resetTime(today).equals(resetTime(curEnd)), где today это 01:00 какого-то дня, curEnd это 23:59 этого же дня
			бессрочные отсутствия отключаются только вручную
		*/
		else if (prevEnd != null && !prevEnd.equals(curEnd) && today.after(prevStart) && today.before(prevEnd) && resetTime(today).equals(resetTime(curEnd))) {
			// если: бывшее время окончания присутствует И окончание отстутствия изменилось И сегодняшняя дата позже бывшего начала отсутствия
			// И раньше его бывшего конца И нынешний конец отсутствия - сегодня
			absenceService.endAbsence(nodeRef);
			logger.debug(String.format("Policy AbsenceEndPolicy invoked on %s for employee %s", nodeRef.toString(), employee.toString()));
		}
//		dumpNodeContent(nodeRef, "onUpdateProperties");

	}

	/**
	 * Устанавливает часы, минуты, секунды и миллисекунды в 00:00:00.000
	 *
	 * @param day Дата, у которой надо сбросить поля времени.
	 * @return Дата с обнуленными полями времени.
	 */
	private Date resetTime(final Date day) {
		Date resetDay = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		resetDay.setTime(cal.getTimeInMillis());
		return resetDay;
	}
}
