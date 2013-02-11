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
	 * Флаг, обозначающий, что отсутствие бессрочное, lecm-absence:unlimited
	 */
	QName PROP_ABSENCE_UNLIMITED = QName.createQName(IWCalendar.ABSENCE_NAMESPACE, "unlimited");
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

	/**
	 * Проверить, отсутствует ли указанный сотрудника указанный день.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @param date интересующая нас дата отсутствия
	 * @return true - сотрудник отсутствует в указанный день. false - сотрудник
	 * не планировал отсутствия.
	 */
	boolean isEmployeeAbsent(NodeRef nodeRef, Date date);

	/**
	 * Проверить, отсутствует ли сегодня указанный сотрудник.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @return true - сотрудник сегодня отсутствует
	 */
	boolean isEmployeeAbsentToday(NodeRef nodeRef);

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * указанную дату.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @param date дата, на которую надо получить экземпляр отсутствия.
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим на указанную дату. Если такового нет, то null
	 */
	NodeRef getActiveAbsence(NodeRef nodeRef, Date date);

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * сегодня.
	 *
	 * @param nodeRef NodeRef на объект типа employee
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим сегодня.
	 */
	NodeRef getActiveAbsence(NodeRef nodeRef);

	/**
	 * Установить параметр "end" у объекта типа absence в определенное значение.
	 *
	 * @param nodeRef NodeRef на объект типа absence
	 * @param date дата, в которую необходимо установить параметр "end"
	 */
	void setAbsenceEnd(NodeRef nodeRef, Date date);

	/**
	 * Установить параметр "end" у объекта типа absence в текущую дату и время.
	 *
	 * @param nodeRef NodeRef на объект типа absence
	 */
	void setAbsenceEnd(NodeRef nodeRef);
}
