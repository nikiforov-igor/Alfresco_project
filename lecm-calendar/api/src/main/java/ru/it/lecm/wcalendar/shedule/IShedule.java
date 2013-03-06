package ru.it.lecm.wcalendar.shedule;

import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.wcalendar.ICommonWCalendar;

/**
 *
 * @author vlevin
 */
public interface IShedule extends ICommonWCalendar {

	/**
	 * Имя для контейнера, в котором хранятся расписания
	 */
	String CONTAINER_NAME = "SheduleContainer";
	/**
	 * Ассоцияация между расписанием и сотрудником,
	 * lecm-shed:shed-employee-link-assoc
	 */
	QName ASSOC_SHEDULE_EMPLOYEE_LINK = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "shed-employee-link-assoc");
	/**
	 * Элемент графика: дата начала рабочей смены, lecm-shed:begin
	 */
	QName PROP_SHEDULE_ELEMENT_BEGIN = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "begin");
	/**
	 * Элемент графика: дата конца рабочей смены, lecm-shed:end
	 */
	QName PROP_SHEDULE_ELEMENT_END = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "end");
	/**
	 * Элемент графика: комментарий, lecm-shed:comment
	 */
	QName PROP_SHEDULE_ELEMENT_COMMENT = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "comment");
	/**
	 * Время началя рабочего дня, lecm-shed:std-begin
	 */
	QName PROP_SHEDULE_STD_BEGIN = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "std-begin");
	/**
	 * Время окончания рабочего дня, lecm-shed:std-end
	 */
	QName PROP_SHEDULE_STD_END = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "std-end");
	/**
	 * Дата начала действия особого графика работы, lecm-shed:time-limit-start
	 */
	QName PROP_SHEDULE_TIME_LIMIT_START = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "time-limit-start");
	/**
	 * Дата окончания действия особого графика работы, lecm-shed:time-limit-end
	 */
	QName PROP_SHEDULE_TIME_LIMIT_END = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "time-limit-end");
	/**
	 * Тип графика: COMMON - обычный, SPECIAL - особый, lecm-shed:type
	 */
	QName PROP_SHEDULE_TYPE = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "type");
	/**
	 * Тип для объекта График работы, lecm-shed:shedule
	 */
	QName TYPE_SHEDULE = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "shedule");
	/**
	 * Тип для объекта Элемент особого графика работы,
	 * lecm-shed:special-shed-element
	 */
	QName TYPE_SHEDULE_ELEMENT = QName.createQName(ICommonWCalendar.SHEDULE_NAMESPACE, "special-shed-element");
	/**
	 * Корневой контейнер для графиков, lecm-wcal:shedule-container
	 */
	QName TYPE_SHEDULE_CONTAINER = QName.createQName(ICommonWCalendar.WCAL_NAMESPACE, "shedule-container");
	String SHEDULE_TYPE_COMMON = "COMMON";
	String SHEDULE_TYPE_SPECIAL = "SPECIAL";

	/**
	 * Создает новое особое расписание.
	 *
	 * @param sheduleRawData объект с правилами повторения расписания.
	 * @param sheduleEmployeeAssoc NodeRef на сотрудника или орг. единицу, к
	 * которому надо привязать расписание.
	 * @param sheduleContainer NodeRef на каталог, в котором будет создано
	 * расписание.
	 * @return - NodeRef на созданное расписание.
	 */
	NodeRef createNewSpecialShedule(final ISpecialSheduleRaw sheduleRawData, final NodeRef sheduleEmployeeAssoc, final NodeRef sheduleContainer);

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
	NodeRef getParentShedule(NodeRef node);

	/**
	 * Возвращает время начала работы у данного графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return Время начала работы.
	 */
	String getSheduleBeginTime(NodeRef node);

	/**
	 * Возвращает время конца работы у данного графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return Время начала работы.
	 */
	String getSheduleEndTime(NodeRef node);

	/**
	 * Получить расписание, привзянное к сотруднику или орг. единице.
	 *
	 * @param node NodeRef сотрудника/орг. единицы.
	 * @return NodeRef расписания, привязанного к node. Если таковое
	 * отсутствует, то null.
	 */
	NodeRef getSheduleByOrgSubject(NodeRef node);

	/**
	 * Проверяет, привязано ли какое-нибудь расписание к node.
	 *
	 * @param node NodeRef на сотрудника или орг. единицу.
	 * @return true - привязано, false - не привязано.
	 */
	boolean isSheduleAssociated(NodeRef node);

	/**
	 * Удалить ассоциацию графика работы с сотрудником или орг. единицей
	 * (shed-employee-link-assoc).
	 *
	 * @param node NodeRef на график работы (shedule)
	 */
	void unlinkShedule(NodeRef node);

	/**
	 * Получить ссылку на сотрудника или огр. единицу, с которым/-ой
	 * ассоциирован данный график работы.
	 *
	 * @param node NodeRef на график.
	 * @return NodeRef на сотрудника или огр. единицу
	 */
	NodeRef getOrgSubjectByShedule(NodeRef node);

	/**
	 * Получить тип графика работы.
	 *
	 * @param node NodeRef на график работы.
	 * @return COMMON - обычный график. SPECIAL - особый.
	 */
	String getSheduleType(NodeRef node);

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
	 * @return список NodeRef'ов на объекты "shedule-element". Если node - не
	 * особое расписание, то null.
	 */
	List<NodeRef> getSheduleElements(NodeRef node);

	/**
	 * Получить дату первого рабочего дня в серии ("shedule-element").
	 *
	 * @param node NodeRef'ов на объект "shedule-element".
	 * @return дата первого рабочего дня.
	 */
	Date getSheduleElementStart(NodeRef node);

	/**
	 * Получить дату последнего рабочего дня в серии ("shedule-element").
	 *
	 * @param node NodeRef'ов на объект "shedule-element".
	 * @return дата последнего рабочего дня.
	 */
	Date getSheduleElementEnd(NodeRef node);
}
