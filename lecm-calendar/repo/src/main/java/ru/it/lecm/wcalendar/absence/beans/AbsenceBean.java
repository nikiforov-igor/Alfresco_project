package ru.it.lecm.wcalendar.absence.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWCalendar;
import ru.it.lecm.wcalendar.absence.IAbsence;
import ru.it.lecm.wcalendar.beans.AbstractWCalendarBean;

/**
 *
 * @author vlevin
 */
public class AbsenceBean extends AbstractWCalendarBean implements IAbsence {
	// Получить логгер, чтобы писать, что с нами происходит.

	private IDelegation delegationService;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceBean.class);

	@Override
	public IWCalendar getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_ABSENCE;
	}

	/**
	 * Метод, который запускает Spring при старте Tomcat-а. Создает корневой
	 * объект для графиков отсутствия.
	 */
	public final void bootstrap() {
		PropertyCheck.mandatory(this, "repository", repository);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "delegationService", delegationService);

		// Создание контейнера (если не существует).
		AuthenticationUtil.runAsSystem(this);
	}

	public void setDelegationService(IDelegation delegationService) {
		this.delegationService = delegationService;
	}

	@Override
	protected Map<String, Object> containerParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CONTAINER_NAME", CONTAINER_NAME);
		params.put("CONTAINER_TYPE", TYPE_ABSENCE_CONTAINER);

		return params;
	}

	@Override
	public List<NodeRef> getAbsenceByEmployee(NodeRef node) {
		List<NodeRef> absences = new ArrayList<NodeRef>();
		List<NodeRef> absenceList = findNodesByAssociationRef(node, ASSOC_ABSENCE_EMPLOYEE, TYPE_ABSENCE, ASSOCIATION_TYPE.SOURCE);
		for (NodeRef absence : absenceList) {
			if (!isArchive(absence)) {
				absences.add(absence);
			}
		}
		return absences.isEmpty() ? null : absences;
	}

	@Override
	public boolean isAbsenceAssociated(NodeRef node) {
		boolean result = false;
		List<NodeRef> absenceList = findNodesByAssociationRef(node, ASSOC_ABSENCE_EMPLOYEE, TYPE_ABSENCE, ASSOCIATION_TYPE.SOURCE);
		for (NodeRef absence : absenceList) {
			if (!isArchive(absence)) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean isIntervalSuitableForAbsence(NodeRef nodeRef, Date begin, Date end) {
		boolean suitable = true;

		List<NodeRef> employeeAbsence = getAbsenceByEmployee(nodeRef);
		if (employeeAbsence != null && !employeeAbsence.isEmpty()) {
			for (NodeRef absence : employeeAbsence) {
				Date absenceBegin = getAbsenceStartDate(absence);
				Date absenceEnd = getAbsenceEndDate(absence);
				if (absenceBegin.before(end) && begin.before(absenceEnd)) {
					suitable = false;
					break;
				}
			}
		}
		return suitable;
	}

	@Override
	public boolean isEmployeeAbsent(NodeRef nodeRef, Date date) {
		boolean result = false;
		List<NodeRef> employeeAbsence = getAbsenceByEmployee(nodeRef);
		if (employeeAbsence != null && !employeeAbsence.isEmpty()) {
			for (NodeRef absence : employeeAbsence) {
				Date absenceBegin = getAbsenceStartDate(absence);
				Date absenceEnd = getAbsenceEndDate(absence);
				if (date.after(absenceBegin) && date.before(absenceEnd)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public boolean isEmployeeAbsentToday(NodeRef nodeRef) {
		return isEmployeeAbsent(nodeRef, new Date());
	}

	@Override
	public NodeRef getActiveAbsence(NodeRef nodeRef, Date date) {
		NodeRef result = null;
		List<NodeRef> employeeAbsence = getAbsenceByEmployee(nodeRef);
		if (employeeAbsence != null && !employeeAbsence.isEmpty()) {
			for (NodeRef absence : employeeAbsence) {
				Date absenceBegin = getAbsenceStartDate(absence);
				Date absenceEnd = getAbsenceEndDate(absence);
				if (date.after(absenceBegin) && date.before(absenceEnd)) {
					result = absence;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public NodeRef getActiveAbsence(NodeRef nodeRef) {
		return getActiveAbsence(nodeRef, new Date());
	}

	@Override
	public void setAbsenceEnd(NodeRef nodeRef, Date date) {
		nodeService.setProperty(nodeRef, PROP_ABSENCE_END, date);
	}

	@Override
	public void setAbsenceEnd(NodeRef nodeRef) {
		setAbsenceEnd(nodeRef, new Date());
	}

	@Override
	public NodeRef getEmployeeByAbsence(NodeRef node) {
		return findNodeByAssociationRef(node, ASSOC_ABSENCE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
	}

	@Override
	public void startAbsence(NodeRef node) {
		NodeRef employee = getEmployeeByAbsence(node);

		if (employee != null) {
			setAbsenceActivated(node, true);
			addBusinessJournalRecord(node, EventCategory.START_ABSENCE_ON_WORK);
			delegationService.startDelegation(employee);
			logger.debug(String.format("Absence [%s] started.", node.toString()));
		} else {
			logger.error(String.format("Somehow absence %s has no employee!", node.toString()));
		}
	}

	@Override
	public void endAbsence(NodeRef node) {
		NodeRef employee = getEmployeeByAbsence(node);

		if (employee != null) {
			setAbsenceActivated(node, false);
			addBusinessJournalRecord(node, EventCategory.FINISH_ABSENCE_ON_WORK);
			delegationService.stopDelegation(employee);
			logger.debug(String.format("Absence [%s] ended.", node.toString()));
		} else {
			logger.error(String.format("Somehow absence %s has no employee!", node.toString()));
		}
	}

	@Override
	public NodeRef getContainer() {
		return this.getWCalendarContainer();
	}

	@Override
	public Date getAbsenceStartDate(NodeRef node) {
		return (Date) nodeService.getProperty(node, PROP_ABSENCE_BEGIN);
	}

	@Override
	public Date getAbsenceEndDate(NodeRef node) {
		return (Date) nodeService.getProperty(node, PROP_ABSENCE_END);
	}

	@Override
	public void setAbsenceUnlimited(NodeRef nodeRef, boolean unlimited) {
		nodeService.setProperty(nodeRef, PROP_ABSENCE_UNLIMITED, unlimited);
	}

	@Override
	public void addBusinessJournalRecord(NodeRef node, String category) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
		List<String> objects = new ArrayList<String>();
		NodeRef absentEmployee = getEmployeeByAbsence(node);
		objects.add(absentEmployee.toString());
		String messageTemplate = null;

		if (EventCategory.ADD.equals(category)) {
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_ADD;
		} else if (EventCategory.DELETE.equals(category)) {
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_DELETE;
		} else if (EventCategory.EDIT.equals(category)) {
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_PROLONG;
		} else if (EventCategory.START_ABSENCE_ON_WORK.equals(category)) {
			Date absenceEnd = getAbsenceEndDate(node);
			objects.add(dateFormat.format(absenceEnd));
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_START;
		} else if (EventCategory.FINISH_ABSENCE_ON_WORK.equals(category)) {
			Date absenceStart = getAbsenceStartDate(node);
			objects.add(dateFormat.format(absenceStart));
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_END;
		}

		if (messageTemplate != null) {
			businessJournalService.log(authService.getCurrentUserName(), node, category, messageTemplate, objects);
		}
	}

	@Override
	public void setAbsenceActivated(NodeRef node, boolean activated) {
		nodeService.setProperty(node, PROP_ABSENCE_ACTIVATED, activated);
	}

	@Override
	public boolean getAbsenceActivated(NodeRef node) {
		return (Boolean) nodeService.getProperty(node, PROP_ABSENCE_ACTIVATED);
	}

	@Override
	public boolean getAbsenceUnlimited(NodeRef node) {
		return (Boolean) nodeService.getProperty(node, PROP_ABSENCE_UNLIMITED);
	}
}
