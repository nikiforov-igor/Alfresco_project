package ru.it.lecm.wcalendar.schedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.wcalendar.ICommonWCalendar;

import java.util.Date;
import java.util.List;

/**
 *
 * @author vlevin
 */
public interface ISchedule extends ICommonWCalendar {

	/**
	 * Имя для контейнера, в котором хранятся расписания
	 */
	String CONTAINER_NAME = "ScheduleContainer";
	/**
	 * Ассоцияация между расписанием и сотрудником,
	 * lecm-sched:sched-employee-link-assoc
	 */
	QName ASSOC_SCHEDULE_EMPLOYEE_LINK = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "sched-employee-link-assoc");
	/**
	 * Элемент графика: дата начала рабочей смены, lecm-sched:begin
	 */
	QName PROP_SCHEDULE_ELEMENT_BEGIN = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "begin");
	/**
	 * Элемент графика: дата конца рабочей смены, lecm-sched:end
	 */
	QName PROP_SCHEDULE_ELEMENT_END = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "end");
	/**
	 * Элемент графика: комментарий, lecm-sched:comment
	 */
	QName PROP_SCHEDULE_ELEMENT_COMMENT = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "comment");
	/**
	 * Время началя рабочего дня, lecm-sched:std-begin
	 */
	QName PROP_SCHEDULE_STD_BEGIN = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "std-begin");
	/**
	 * Время окончания рабочего дня, lecm-sched:std-end
	 */
	QName PROP_SCHEDULE_STD_END = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "std-end");
	/**
	 * Дата начала действия особого графика работы, lecm-sched:time-limit-start
	 */
	QName PROP_SCHEDULE_TIME_LIMIT_START = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "time-limit-start");
	/**
	 * Дата окончания действия особого графика работы, lecm-sched:time-limit-end
	 */
	QName PROP_SCHEDULE_TIME_LIMIT_END = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "time-limit-end");
	/**
	 * Тип графика: COMMON - обычный, SPECIAL - особый, lecm-sched:type
	 */
	QName PROP_SCHEDULE_TYPE = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "type");
	/**
	 * Тип для объекта График работы, lecm-sched:schedule
	 */
	QName TYPE_SCHEDULE = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "schedule");
	/**
	 * Тип для объекта Элемент особого графика работы,
	 * lecm-sched:special-sched-element
	 */
	QName TYPE_SCHEDULE_ELEMENT = QName.createQName(ICommonWCalendar.SCHEDULE_NAMESPACE, "special-sched-element");
	/**
	 * Корневой контейнер для графиков, lecm-wcal:schedule-container
	 */
	QName TYPE_SCHEDULE_CONTAINER = QName.createQName(ICommonWCalendar.WCAL_NAMESPACE, "schedule-container");
	String SCHEDULE_TYPE_COMMON = "COMMON";
	String SCHEDULE_TYPE_SPECIAL = "SPECIAL";

	/**
	 * Создает новое особое расписание.
	 *
	 * @param scheduleRawData объект с правилами повторения расписания.
	 * @param scheduleEmployeeAssoc NodeRef на сотрудника или орг. единицу, к
	 * которому надо привязать расписание.
	 * @param scheduleContainer NodeRef на каталог, в котором будет создано
	 * расписание.
	 * @return - NodeRef на созданное расписание.
	 */
	NodeRef createNewSpecialSchedule(final ISpecialScheduleRaw scheduleRawData, final NodeRef scheduleEmployeeAssoc, final NodeRef scheduleContainer);

	/**
	 * Если node - сотрудник, то возвращает ссылку на расписание подразделения,
	 * в котором сотрудник занимает основную позицию (или вышестоящего
	 * подразделения). Если node - подразделение, то возвращает ссылку на
	 * расписание вышестоящего подразделения. Если расписание к node не
	 * привязано, то возвращает null.
	 *
	 * @param node NodeRef на сотрудника или орг. единицу.
	 * @return NodeRef на расписание.
	 */
	NodeRef getParentSchedule(NodeRef node);

	/**
	 * Возвращает время начала работы у данного графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return Время начала работы.
	 */
	String getScheduleBeginTime(NodeRef node);

	/**
	 * Возвращает время конца работы у данного графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return Время начала работы.
	 */
	String getScheduleEndTime(NodeRef node);

	/**
	 * Получить расписание, привзянное к сотруднику или орг. единице.
	 *
	 * @param node NodeRef сотрудника/орг. единицы.
	 * @return NodeRef расписания, привязанного к node. Если таковое
	 * отсутствует, то null.
	 */
	NodeRef getScheduleByOrgSubject(NodeRef node);

    /**
     * Получить расписание, привзянное к сотруднику или орг. единице.
     *
     * @param node NodeRef сотрудника/орг. единицы.
     * @return NodeRef расписания, привязанного к node. Если таковое
     * отсутствует, то null.
     */
    NodeRef getScheduleByOrgSubject(NodeRef node, boolean excludeDefault);

    /**
	 * Проверяет, привязано ли какое-нибудь расписание к node.
	 *
	 * @param node NodeRef на сотрудника или орг. единицу.
	 * @return true - привязано, false - не привязано.
	 */
	boolean isScheduleAssociated(NodeRef node);

	/**
	 * Удалить ассоциацию графика работы с сотрудником или орг. единицей
	 * (sched-employee-link-assoc).
	 *
	 * @param node NodeRef на график работы (schedule)
	 */
	void unlinkSchedule(NodeRef node);

	/**
	 * Получить ссылку на сотрудника или огр. единицу, с которым/-ой
	 * ассоциирован данный график работы.
	 *
	 * @param node NodeRef на график.
	 * @return NodeRef на сотрудника или огр. единицу
	 */
	NodeRef getOrgSubjectBySchedule(NodeRef node);

	/**
	 * Получить тип графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return COMMON - обычный график. SPECIAL - особый.
	 */
	String getScheduleType(NodeRef node);

	/**
	 * Добавить запись в бизнес-журнал об операции над графиками работы. Пишем
	 * создание и удаление графиков.
	 *
	 * @param node NodeRef на #mainobject (объект графика)
	 * @param category категория события (EventCategory)
	 */
	void addBusinessJournalRecord(NodeRef node, String category);

	/**
	 * Проверяет, является ли день рабочим для данного ОСОБОГО расписания.
	 *
	 * @param node NodeRef на объект "расписание".
	 * @param day Дата, которую следует проверить.
	 *
	 * @return true - день рабочий. false - не рабочий. null - если тип
	 * расписания не "особое".
	 */
	Boolean isWorkingDay(NodeRef node, Date day);

	/**
	 * Получить все элементы особого расписания.
	 *
	 * @param node NodeRef на объект "расписание".
	 * @return список NodeRef'ов на объекты "schedule-element". Если node - не
	 * особое расписание, то null.
	 */
	List<NodeRef> getScheduleElements(NodeRef node);

	/**
	 * Получить дату первого рабочего дня в серии ("schedule-element").
	 *
	 * @param node NodeRef'ов на объект "schedule-element".
	 * @return дата первого рабочего дня.
	 */
	Date getScheduleElementStart(NodeRef node);

	/**
	 * Получить дату последнего рабочего дня в серии ("schedule-element").
	 *
	 * @param node NodeRef'ов на объект "schedule-element".
	 * @return дата последнего рабочего дня.
	 */
	Date getScheduleElementEnd(NodeRef node);

	/**
	 * Получить рабочий график по умолчанию для всей организации.
	 *
	 * @return график по умолчанию
	 */
	NodeRef getDefaultSystemSchedule();

	/**
	 * Получить длительность рабочего дня в минутах согласно рабочему графику.
	 *
	 * @param schedule график работы
	 * @return длительность рабочего дня
	 */
	int getWorkDayDurationInMinutes(NodeRef schedule);
}
