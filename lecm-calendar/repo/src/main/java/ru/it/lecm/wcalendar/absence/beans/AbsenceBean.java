package ru.it.lecm.wcalendar.absence.beans;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.CalendarCategory;
import ru.it.lecm.wcalendar.ICommonWCalendar;
import ru.it.lecm.wcalendar.absence.IAbsence;
import ru.it.lecm.wcalendar.beans.AbstractCommonWCalendarBean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author vlevin
 */
public class AbsenceBean extends AbstractCommonWCalendarBean implements IAbsence {
	// Получить логгер, чтобы писать, что с нами происходит.

	private IDelegation delegationService;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceBean.class);

	@Override
	public ICommonWCalendar getWCalendarDescriptor() {
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
	public final void init() {
            PropertyCheck.mandatory(this, "repository", repository);
            PropertyCheck.mandatory(this, "nodeService", nodeService);
            PropertyCheck.mandatory(this, "transactionService", transactionService);
            PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
            PropertyCheck.mandatory(this, "delegationService", delegationService);
//TODO: Сделать инициализацию переменной в onBootstrap и потом уже не искать по репозиторию

//            // Создание контейнера (если не существует).
//            // TODO: DONE Ввиду "устранения" транзакции в AbstractCommonWCalendarBean 
//            // init метод. Транзакции нет. Создаём.
//            // RunAsSystem есть в createWCalendarContainer();
//            if (getWCalendarContainer() == null) {
//                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
//                    @Override
//                    public Object execute() throws Throwable {
//                        createWCalendarContainer();
//                        return null;
//                    }
//                }, false, true);
//            }
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
		return absences;
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
		List<NodeRef> employeeAbsence = getAbsenceByEmployee(nodeRef);
		return isEmployeeAbsent(date, employeeAbsence);
	}

	@Override
	public boolean isEmployeeAbsent(Date date, List<NodeRef> employeeAbsences) {
		boolean result = false;
		if (employeeAbsences != null && !employeeAbsences.isEmpty()) {
			for (NodeRef absence : employeeAbsences) {
				Date absenceBegin = getAbsenceStartDate(absence);
				Date absenceEnd = getAbsenceEndDate(absence);
				if (!date.before(absenceBegin) && !date.after(absenceEnd)) {
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
			addBusinessJournalRecord(node, CalendarCategory.START_NOT_IN_OFFICE);
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
			addBusinessJournalRecord(node, CalendarCategory.STOP_NOT_IN_OFFICE);
			delegationService.stopDelegation(employee);
			List<NodeRef> delegatedTasksForAssumedExecutor = delegationService.getDelegatedTasksForAssumedExecutor(employee, true);
			for (NodeRef delegatedTask: delegatedTasksForAssumedExecutor) {
				delegationService.reassignTaskBackToAssumedExecutor(delegatedTask);
			}
			logger.debug(String.format("Absence [%s] ended.", node.toString()));
		} else {
			logger.error(String.format("Somehow absence %s has no employee!", node.toString()));
		}
	}

	@Override
	public NodeRef getContainer() {
		NodeRef container = this.getWCalendarContainer();
		if (container==null) {
			container = this.createWCalendarContainer();
		}
		return container;
	}

    @Override
    public List<Pair<Date, Date>> getAbsencesDatesByEmployee(NodeRef node) {
        List<NodeRef> absenceByEmployee = getAbsenceByEmployee(node);
        List<Pair<Date, Date>> result = new ArrayList<>();
        for (NodeRef absenceRef : absenceByEmployee) {
            result.add(new Pair<>((Date) nodeService.getProperty(absenceRef, PROP_ABSENCE_BEGIN),
                    (Date) nodeService.getProperty(absenceRef, PROP_ABSENCE_END)));
        }
        return result;
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

		if (CalendarCategory.ADD_ABSENCE.equals(category)) {
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_ADD;
		} else if (CalendarCategory.DELETE_ABSENCE.equals(category)) {
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_DELETE;
		} else if (EventCategory.EDIT.equals(category)) {
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_PROLONG;
		} else if (CalendarCategory.START_NOT_IN_OFFICE.equals(category)) {
			Date absenceEnd = getAbsenceEndDate(node);
			objects.add(dateFormat.format(absenceEnd));
			messageTemplate = BUSINESS_JOURNAL_ABSENCE_START;
		} else if (CalendarCategory.STOP_NOT_IN_OFFICE.equals(category)) {
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

	@Override
	public NodeRef getNextEmployeeAbsence(NodeRef employee, Date date) {
		NodeRef result = null;

		Date minDate = null;
		List<NodeRef> absenceByEmployee = getAbsenceByEmployee(employee);
		for (NodeRef absence : absenceByEmployee) {
			Date absenceStartDate = getAbsenceStartDate(absence);
			if (!absenceStartDate.before(date)) {
				if (minDate == null) {
					minDate = absenceStartDate;
					result = absence;
				} else {
					if (absenceStartDate.before(minDate)) {
						minDate = absenceStartDate;
						result = absence;
					}
				}
			}
		}
		return result;
	}

	@Override
	public NodeRef getNextEmployeeAbsence(NodeRef employee) {
		return getNextEmployeeAbsence(employee, new Date());
	}

	@Override
	public boolean isAutoAnswerUsed(NodeRef absenceNode) {
        Serializable isAutoAnswer = nodeService.getProperty(absenceNode, PROP_ABSENCE_AUTO_ANSWER_ACTIVATED);
        return isAutoAnswer == null ? false : (Boolean) isAutoAnswer;
	}

	@Override
	public String getAutoAnswerText(NodeRef absenceNode) {
		String autoAnswerText = (String) nodeService.getProperty(absenceNode, PROP_ABSENCE_AUTO_ANSWER_TEXT);
		if (autoAnswerText == null) {
			return "";
		} else {
			return autoAnswerText;
		}
	}

	@Override
	public NodeRef getLastEmployeeAbsence(NodeRef employee) {
		return getLastEmployeeAbsence(employee, new Date());
	}

	@Override
	public NodeRef getLastEmployeeAbsence(NodeRef employee, Date date) {
		NodeRef result = null;

		Date maxDate = null;
		List<NodeRef> absenceByEmployee = getAbsenceByEmployee(employee);
		for (NodeRef absence : absenceByEmployee) {
			Date absenceStartDate = getAbsenceStartDate(absence);
			if (!absenceStartDate.after(date)) {
				if (maxDate == null) {
					maxDate = absenceStartDate;
					result = absence;
				} else {
					if (absenceStartDate.after(maxDate)) {
						maxDate = absenceStartDate;
						result = absence;
					}
				}
			}
		}
		return result;
	}

	@Override
	public NodeRef getLastCurrentEmployeeAbsence() {
		return getLastEmployeeAbsence(orgstructureService.getCurrentEmployee());
	}

	@Override
	public void setAbsenceBegin(final NodeRef absenceRef, final Date date) {
		nodeService.setProperty(absenceRef, PROP_ABSENCE_BEGIN, date);
	}
}
