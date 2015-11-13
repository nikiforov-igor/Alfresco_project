package ru.it.lecm.wcalendar.absence;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import ru.it.lecm.wcalendar.ICommonWCalendar;

import java.util.Date;
import java.util.List;

/**
 *
 * @author vlevin
 */
public interface IAbsence extends ICommonWCalendar {

	/**
	 * Имя для контейнера, в котором хранятся отсутствия
	 */
	String CONTAINER_NAME = "AbsenceContainer";
	/**
	 * Ассоциация между отсутствием и сотрудником,
	 * lecm-absence:abscent-employee-assoc
	 */
	QName ASSOC_ABSENCE_EMPLOYEE = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "abscent-employee-assoc");
	/**
	 * Дата и время начала отсутствия, lecm-absence:begin
	 */
	QName PROP_ABSENCE_BEGIN = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "begin");
	/**
	 * Дата и время окончания отсутствия, lecm-absence:end
	 */
	QName PROP_ABSENCE_END = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "end");
	/**
	 * Флаг, обозначающий, что отсутствие бессрочное, lecm-absence:unlimited
	 */
	QName PROP_ABSENCE_UNLIMITED = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "unlimited");
	/**
	 * Флаг, обозначающий, что отсутствие уже активировано,
	 * lecm-absence:activated
	 */
	QName PROP_ABSENCE_ACTIVATED = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "activated");
	/**
	 * Флаг, обозначающий, что в отсутствии используется автоответ,
	 * lecm-absence:auto-answer-enabled
	 */
	QName PROP_ABSENCE_AUTO_ANSWER_ACTIVATED = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "auto-answer-enabled");
	/**
	 * Текст автоответа, lecm-absence:auto-answer-text
	 */
	QName PROP_ABSENCE_AUTO_ANSWER_TEXT = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "auto-answer-text");
	/**
	 * Тип объекта для Отсутствий, lecm-absence:absence
	 */
	QName TYPE_ABSENCE = QName.createQName(ICommonWCalendar.ABSENCE_NAMESPACE, "absence");
	/**
	 * Корневой контейнер для отсутствий, lecm-wcal:absence-container
	 */
	QName TYPE_ABSENCE_CONTAINER = QName.createQName(ICommonWCalendar.WCAL_NAMESPACE, "absence-container");

	/**
	 * Получить список отсутствий по NodeRef-у сотрудника.
	 *
	 * @param node NodeRef на объект типа employee
	 * @return список NodeRef-ов на объекты типа absence. Если к сотруднику не
	 * привязаны отсутствия, возвращает пустую Map
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
	 * @param node NodeRef сотрудника
	 * @param begin дата (и время) начала искомого промежутка
	 * @param end дата (и время) окончания искомого промежутка
	 * @return true - промежуток свободен, создать отсутствие можно, false - на
	 * данный промежуток отсутствие уже запланировано.
	 */
	boolean isIntervalSuitableForAbsence(NodeRef node, Date begin, Date end);

	/**
	 * Проверить, отсутствует ли указанный сотрудника указанный день.
	 *
	 * @param node NodeRef на объект типа employee
	 * @param date интересующая нас дата отсутствия
	 * @return true - сотрудник отсутствует в указанный день. false - сотрудник
	 * не планировал отсутствия.
	 */
	boolean isEmployeeAbsent(NodeRef node, Date date);

    /**
     * Проверить, отсутствует ли сотрудник в указанный день.
     * Метод нужен для пакетной обработки данных по сотрудникам
     *
     * @param date интересующая нас дата отсутствия
     * @param employeeAbsences предварительно полученный список отсутствий сотрудника
     * @return true - сотрудник отсутствует в указанный день. false - сотрудник
     * не планировал отсутствия.
     */
    boolean isEmployeeAbsent(Date date, List<NodeRef> employeeAbsences);

    /**
	 * Проверить, отсутствует ли сегодня указанный сотрудник.
	 *
	 * @param node NodeRef на объект типа employee
	 * @return true - сотрудник сегодня отсутствует
	 */
	boolean isEmployeeAbsentToday(NodeRef node);

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * указанную дату.
	 *
	 * @param node NodeRef на объект типа employee
	 * @param date дата, на которую надо получить экземпляр отсутствия.
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим на указанную дату. Если такового нет, то null
	 */
	NodeRef getActiveAbsence(NodeRef node, Date date);

	/**
	 * Получить экземпляр отсутствия, активного для указанного сотрудника на
	 * сегодня.
	 *
	 * @param node NodeRef на объект типа employee
	 * @return NodeRef на объект типа absence, из-за которого сотрудник
	 * считается отсутствующим сегодня.
	 */
	NodeRef getActiveAbsence(NodeRef node);

	/**
	 * Установить параметр "end" у объекта типа absence в определенное значение.
	 *
	 * @param node NodeRef на объект типа absence
	 * @param date дата, в которую необходимо установить параметр "end"
	 */
	void setAbsenceEnd(NodeRef node, Date date);

	/**
	 * Установить параметр "end" у объекта типа absence в текущую дату и время.
	 *
	 * @param node NodeRef на объект типа absence
	 */
	void setAbsenceEnd(NodeRef node);

	/**
	 * Получить параметр "бессрочное" ("unlimited") объекта типа absence.
	 *
	 * @param node NodeRef на объект типа absence
	 * @return значение параметра "бессрочное" у отсутствия.
	 */
	boolean getAbsenceUnlimited(NodeRef node);

	/**
	 * Установить параметр "бессрочное" ("unlimited") у объекта типа absence в
	 * определенное значение.
	 *
	 * @param node NodeRef на объект типа absence
	 * @param unlimited значение, в которое следует установить параметр
	 * "бессрочное".
	 */
	void setAbsenceUnlimited(NodeRef node, boolean unlimited);

	/**
	 * Получить NodeRef на сотрудника, с которым ассоциирован объект отсутствия.
	 *
	 * @param node NodeRef на объект типа absence.
	 * @return NodeRef на объект типа employee. Если по какой-то причине
	 * ассоциация отсутствует (хотя такого случиться не должно), то null.
	 */
	NodeRef getEmployeeByAbsence(NodeRef node);

	/**
	 * Начать отсутствие: запустить процедуры делегирования, сделать запись в
	 * бизнес-журнал.
	 *
	 * @param node NodeRef на объект типа absence.
	 */
	void startAbsence(NodeRef node);

	/**
	 * Завершить отсутствие: отозвать делегирования, сделать запись в
	 * бизнес-журнал.
	 *
	 * @param node NodeRef на объект типа absence.
	 */
	void endAbsence(NodeRef node);

	/**
	 * Получить ссылку на контейнер, где хранятся расписания.
	 *
	 * @return NodeRef на absence-container
	 */
	NodeRef getContainer();

    List<Pair<Date, Date>> getAbsencesDatesByEmployee(NodeRef node);

    /**
	 * Получить дату (и время), на которую назначено начало отсутствия.
	 *
	 * @param node NodeRef на объект типа absence.
	 * @return дата начала отсутствия
	 */
	Date getAbsenceStartDate(NodeRef node);

	/**
	 * Получить дату (и время), на которую назначено окончание отсутствия.
	 *
	 * @param node NodeRef на объект типа absence.
	 * @return дата окончания отсутствия
	 */
	Date getAbsenceEndDate(NodeRef node);

	/**
	 * Добавить запись в бизнес-журнал об операции над отсутствиями. Пишем
	 * создание, удаление, начало и конец отсутствий.
	 *
	 * @param node NodeRef на #mainobject (объект отсутствия)
	 * @param category категория события (EventCategory)
	 */
	void addBusinessJournalRecord(NodeRef node, String category);

	/**
	 * Установить параметр "активировано" ("activated") у объекта типа absence в
	 * определенное значение.
	 *
	 * @param node NodeRef на объект типа absence
	 * @param activated значение, в которое следует установить параметр
	 * "активировано".
	 */
	void setAbsenceActivated(NodeRef node, boolean activated);

	/**
	 * Получить параметр "активировано" ("activated") объекта типа absence.
	 *
	 * @param node NodeRef на объект типа absence
	 * @return значение параметра "активировано" у отсутствия.
	 */
	boolean getAbsenceActivated(NodeRef node);

	/**
	 * Получить будущее отсутствие сотрудника, ближайшее к указаной дате.
	 *
	 * @param employee NodeRef сотрудника
	 * @param date дата, за которой должно следовать отсутствие
	 * @return NodeRef на объект типа absence. Если у сотрудника не
	 * запланированы отсутствия, то null
	 */
	NodeRef getNextEmployeeAbsence(NodeRef employee, Date date);

	/**
	 * Получить будущее отсутствие сотрудника, ближайшее к текущей дате.
	 *
	 * @param employee NodeRef сотрудника
	 * @return NodeRef на объект типа absence. Если у сотрудника не
	 * запланированы отсутствия, то null
	 */
	NodeRef getNextEmployeeAbsence(NodeRef employee);

	/**
	 * Используется ли автоответ в данном отсутствии
	 *
	 * @param absenceNode экземпляр отсутствия
	 * @return используется ли автоответ
	 */
	boolean isAutoAnswerUsed(NodeRef absenceNode);

	/**
	 * Получить текст автоответа для данного отсутствия.
	 *
	 * @param absenceNode экземпляр отсутствия
	 * @return текст автоответа. Если не задан, то пкстая строка.
	 */
	String getAutoAnswerText(NodeRef absenceNode);

	/**
	 * Получить прошедшее или еще активное отсутствие сотрудника, ближайшее к
	 * указанной дате.
	 *
	 * @param employee NodeRef сотрудника
	 * @param date дата, перед которой должно быть искомое отсутствие.
	 * @return NodeRef на объект типа absence. Если у сотрудника не
	 * было отсутствий, то null
	 */
	NodeRef getLastEmployeeAbsence(NodeRef employee, Date date);

	/**
	 * Получить прошедшее или еще активное отсутствие сотрудника, ближайшее к
	 * текущей дате.
	 *
	 * @param employee NodeRef сотрудника
	 * @return NodeRef на объект типа absence. Если у сотрудника не
	 * было отсутствий, то null
	 */
	NodeRef getLastEmployeeAbsence(NodeRef employee);

	/**
	 * Получить прошедшее или еще активное отсутствие текущего сотрудника,
	 * ближайшее к текущей дате.
	 *
	 * @return NodeRef на объект типа absence. Если у сотрудника не
	 * было отсутствий, то null
	 */
	NodeRef getLastCurrentEmployeeAbsence();

	void setAbsenceBegin(final NodeRef absenceRef, final Date date);
}
