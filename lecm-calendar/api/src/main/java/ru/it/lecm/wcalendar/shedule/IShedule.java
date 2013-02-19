package ru.it.lecm.wcalendar.shedule;

import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.wcalendar.IWCalendar;

/**
 *
 * @author vlevin
 */
public interface IShedule {

	/**
	 * Имя для контейнера, в котором хранятся расписания
	 */
	String CONTAINER_NAME = "SheduleContainer";
	/**
	 * Ассоцияация между расписанием и сотрудником,
	 * lecm-shed:shed-employee-link-assoc
	 */
	QName ASSOC_SHEDULE_EMPLOYEE_LINK = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "shed-employee-link-assoc");
	/**
	 * Элемент графика: дата начала рабочей смены, lecm-shed:begin
	 */
	QName PROP_SHEDULE_ELEMENT_BEGIN = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "begin");
	/**
	 * Элемент графика: дата конца рабочей смены, lecm-shed:end
	 */
	QName PROP_SHEDULE_ELEMENT_END = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "end");
	/**
	 * Элемент графика: комментарий, lecm-shed:comment
	 */
	QName PROP_SHEDULE_ELEMENT_COMMENT = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "comment");
	/**
	 * Время началя рабочего дня, lecm-shed:std-begin
	 */
	QName PROP_SHEDULE_STD_BEGIN = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "std-begin");
	/**
	 * Время окончания рабочего дня, lecm-shed:std-end
	 */
	QName PROP_SHEDULE_STD_END = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "std-end");
	/**
	 * Дата начала действия особого графика работы, lecm-shed:time-limit-start
	 */
	QName PROP_SHEDULE_TIME_LIMIT_START = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "time-limit-start");
	/**
	 * Дата окончания действия особого графика работы, lecm-shed:time-limit-end
	 */
	QName PROP_SHEDULE_TIME_LIMIT_END = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "time-limit-end");
	/**
	 * Тип графика: COMMON - обычный, SPECIAL - особый, lecm-shed:type
	 */
	QName PROP_SHEDULE_TYPE = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "type");
	/**
	 * Тип для объекта График работы, lecm-shed:shedule
	 */
	QName TYPE_SHEDULE = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "shedule");
	/**
	 * Тип для объекта Элемент особого графика работы,
	 * lecm-shed:special-shed-element
	 */
	QName TYPE_SHEDULE_ELEMENT = QName.createQName(IWCalendar.SHEDULE_NAMESPACE, "special-shed-element");
	/**
	 * Корневой контейнер для графиков, lecm-wcal:shedule-container
	 */
	QName TYPE_SHEDULE_CONTAINER = QName.createQName(IWCalendar.WCAL_NAMESPACE, "shedule-container");

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
	 * Возвращает время работы и тип родительского расписания (см.
	 * getParentShedule).
	 *
	 * @param node NodeRef на сотрудника или орг. единицу.
	 * @return Ключи map'а: "type" - тип расписания, "begin" - время начала
	 * работы, "end" - время конца работы.
	 */
	Map<String, String> getParentSheduleStdTime(NodeRef node);

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
	 * @param nodeRef NodeRef на график работы (shedule)
	 */
	public void unlinkShedule(NodeRef nodeRef);
}
