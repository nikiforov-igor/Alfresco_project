package ru.it.lecm.wcalendar.absence.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWCalCommon;
import ru.it.lecm.wcalendar.beans.AbstractWCalCommonBean;

/**
 *
 * @author vlevin
 */
public class AbsenceBean extends AbstractWCalCommonBean {

	public final static QName TYPE_ABSENCE = QName.createQName(ABSENCE_NAMESPACE, "absence");
	public final static String CONTAINER_NAME = "AbsenceContainer";
	public final static QName TYPE_ABSENCE_CONTAINER = QName.createQName(WCAL_NAMESPACE, "absence-container");
	public final static QName ASSOC_ABSENCE_EMPLOYEE = QName.createQName(ABSENCE_NAMESPACE, "abscent-employee-assoc");
	public final static QName PROP_ABSENCE_BEGIN = QName.createQName(ABSENCE_NAMESPACE, "begin");
	public final static QName PROP_ABSENCE_END = QName.createQName(ABSENCE_NAMESPACE, "end");
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(AbsenceBean.class);

	@Override
	public IWCalCommon getWCalendarDescriptor() {
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
//		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);

		// Создание контейнера (если не существует).
		AuthenticationUtil.runAsSystem(this);
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
	public List<NodeRef> getAbsenceByEmployee(NodeRef node) {
		if (!isAbsenceAssociated(node)) {
			return null;
		}
		List<NodeRef> absences = new ArrayList<NodeRef>();
		List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(node, ASSOC_ABSENCE_EMPLOYEE);
		for (AssociationRef sourceAssoc : sourceAssocs) {
			absences.add(sourceAssoc.getSourceRef());
		}
		return absences;
	}

	/**
	 * Проверить, привязаны ли к сотруднику отсутствия.
	 *
	 * @param nodeRefStr - NodeRef на объект типа employee
	 * @return Расписания привязаны - true. Нет - false.
	 */
	public boolean isAbsenceAssociated(NodeRef node) {
		List<AssociationRef> sourceAssocs = nodeService.getSourceAssocs(node, ASSOC_ABSENCE_EMPLOYEE);
		if (sourceAssocs == null || sourceAssocs.isEmpty()) {
			return false;
		} else {
			return true;
		}
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
	public boolean isIntervalSuitableForAbsence(NodeRef nodeRef, Date begin, Date end) {
		boolean suitable = true;

		List<NodeRef> employeeAbsence = getAbsenceByEmployee(nodeRef);
		if (employeeAbsence != null || !employeeAbsence.isEmpty()) {
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
}
