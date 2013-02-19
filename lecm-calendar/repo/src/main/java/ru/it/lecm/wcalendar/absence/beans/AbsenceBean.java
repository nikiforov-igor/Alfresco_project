package ru.it.lecm.wcalendar.absence.beans;

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

	/**
	 * Получить список отсутствий по NodeRef-у сотрудника.
	 *
	 * @param nodeRefStr - NodeRef на объект типа employee
	 * @return список NodeRef-ов на объекты типа absence. Если к сотруднику не
	 * привязаны отсутствия, возвращает null
	 */
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

	/**
	 * Проверить, привязаны ли к сотруднику отсутствия.
	 *
	 * @param nodeRefStr - NodeRef на объект типа employee
	 * @return Расписания привязаны - true. Нет - false.
	 */
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

	/**
	 * Проверить, можно ли создать отсутствие для указанного сотрудника в
	 * указанном промежутке времени. В одном промежутке времени не может быть
	 * два отсутствия, так что перед созданием нового отсутствия нужно
	 * проверить, не запланировал ли сотрудник отлучиться на это время
	 *
	 * @param nodeRef - NodeRef сотрудника
	 * @param begin - дата (и время) начала искомого промежутка
	 * @param end - дата (и время) окончания искомого промежутка
	 * @return true - промежуток свободен, создать отсутствие можно, false - на
	 * данный промежуток отсутствие уже запланировано.
	 */
	@Override
	public boolean isIntervalSuitableForAbsence(NodeRef nodeRef, Date begin, Date end) {
		boolean suitable = true;

		List<NodeRef> employeeAbsence = getAbsenceByEmployee(nodeRef);
		if (employeeAbsence != null && !employeeAbsence.isEmpty()) {
			for (NodeRef absence : employeeAbsence) {
				Date absenceBegin = (Date) nodeService.getProperty(absence, PROP_ABSENCE_BEGIN);
				Date absenceEnd = (Date) nodeService.getProperty(absence, PROP_ABSENCE_END);
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
				Date absenceBegin = (Date) nodeService.getProperty(absence, PROP_ABSENCE_BEGIN);
				Date absenceEnd = (Date) nodeService.getProperty(absence, PROP_ABSENCE_END);
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
				Date absenceBegin = (Date) nodeService.getProperty(absence, PROP_ABSENCE_BEGIN);
				Date absenceEnd = (Date) nodeService.getProperty(absence, PROP_ABSENCE_END);
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
			delegationService.startDelegation(employee);

			NodeRef currentEmployee = orgstructureService.getCurrentEmployee(); 
			logger.debug("Current user is: " + currentEmployee.toString());// !!!
			// TODO: добавить записи в бизнес-журнал
			logger.debug(String.format("Absence [%s] started.", node.toString()));
		} else {
			logger.error(String.format("Somehow absence %s has no employee!", node.toString()));
		}
	}

	@Override
	public void endAbsence(NodeRef node) {
		NodeRef employee = getEmployeeByAbsence(node);

		if (employee != null) {
			delegationService.stopDelegation(employee);

			NodeRef currentEmployee = orgstructureService.getCurrentEmployee(); 
			logger.debug("Current user is: " + currentEmployee.toString());// !!!
			// TODO: добавить записи в бизнес-журнал
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
}
