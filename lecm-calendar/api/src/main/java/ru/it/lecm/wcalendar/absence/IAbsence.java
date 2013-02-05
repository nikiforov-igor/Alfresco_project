package ru.it.lecm.wcalendar.absence;

import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.wcalendar.IWCalendar;

/**
 *
 * @author vlevin
 */
public interface IAbsence {

	/**
	 * Имя для контейнера, в котором хранятся отсутствия
	 */
	String CONTAINER_NAME = "AbsenceContainer";
	/**
	 * Ассоцияация между отсутствием и сотрудником,
	 * lecm-absence:abscent-employee-assoc
	 */
	QName ASSOC_ABSENCE_EMPLOYEE = QName.createQName(IWCalendar.ABSENCE_NAMESPACE, "abscent-employee-assoc");
	/**
	 * Дата и время начала отсутствия, lecm-absence:begin
	 */
	QName PROP_ABSENCE_BEGIN = QName.createQName(IWCalendar.ABSENCE_NAMESPACE, "begin");
	/**
	 * Дата и время окончания отсутствия, lecm-absence:end
	 */
	QName PROP_ABSENCE_END = QName.createQName(IWCalendar.ABSENCE_NAMESPACE, "end");
	/**
	 * Тип объекта для Отсутствий, lecm-absence:absence
	 */
	QName TYPE_ABSENCE = QName.createQName(IWCalendar.ABSENCE_NAMESPACE, "absence");
	/**
	 * Корневой контейнер для отсутствий, lecm-wcal:absence-container
	 */
	QName TYPE_ABSENCE_CONTAINER = QName.createQName(IWCalendar.WCAL_NAMESPACE, "absence-container");

	/**
	 * Получить список отсутствий по NodeRef-у сотрудника.
	 *
	 * @param node NodeRef на объект типа employee
	 * @return список NodeRef-ов на объекты типа absence. Если к сотруднику не
	 * привязаны отсутствия, возвращает null
	 */
	List<NodeRef> getAbsenceByEmployee(NodeRef node);

	/**
	 * Проверить, привязаны ли к сотруднику отсутствия.
	 *
	 * @param node NodeRef на объект типа employee
	 * @return Расписания привязаны - true. Нет - false.
	 */
	boolean isAbsenceAssociated(NodeRef node);

	/**
	 * Проверить, можно ли создать отсутствие для указанного сотрудника в
	 * указанном промежутке времени. В одном промежутке времени не может быть
	 * два отсутствия, так что перед созданием нового отсутствия нужно
	 * проверить, не запланировал ли сотрудник отлучиться на это время
	 *
	 * @param nodeRef NodeRef сотрудника
	 * @param begin дата (и время) начала искомого промежутка
	 * @param end дата (и время) окончания искомого промежутка
	 * @return true - промежуток свободен, создать отсутствие можно, false - на
	 * данный промежуток отсутствие уже запланировано.
	 */
	boolean isIntervalSuitableForAbsence(NodeRef nodeRef, Date begin, Date end);
}
