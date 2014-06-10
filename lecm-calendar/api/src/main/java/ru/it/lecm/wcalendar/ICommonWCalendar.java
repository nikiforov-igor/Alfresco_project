package ru.it.lecm.wcalendar;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface ICommonWCalendar {

	/**
	 * Namespace: lecm-absence, absence-model.xml
	 */
	String ABSENCE_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/absence/1.0";
	/**
	 * Namespace: lecm-cal, calendar-model.xml
	 */
	String CALENDAR_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/calendar/1.0";
	/**
	 * Namespace: lecm-sched, schedule-model.xml
	 */
	String SCHEDULE_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/schedule/1.0";
	/**
	 * Namespace: lecm-wcal, wcal-common.xml
	 */
	String WCAL_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/1.0";
	/**
	 * Шаблон сообщения в бизнес-журнал: внесение изменений в рабочий календарь.
	 */
	String BUSINESS_JOURNAL_CALENDAR_MODIFIED = "Сотрудник #initiator внес(ла) изменения в Производственный календарь";
	/**
	 * Шаблон сообщения в бизнес-журнал: создание типового расписания для орг.
	 * единицы.
	 */
	String BUSINESS_JOURNAL_COMMON_SCHEDULE_OU_CREATE = "Сотрудник #initiator создал(а) типовой #mainobject для подразделения #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: создание типового расписания для
	 * сотрудника.
	 */
	String BUSINESS_JOURNAL_COMMON_SCHEDULE_EMPLOYEE_CREATE = "Сотрудник #initiator создал(а) типовой #mainobject для сотрудника #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: создание индивидуального расписания для
	 * орг. единицы.
	 */
	String BUSINESS_JOURNAL_SPECIAL_SCHEDULE_OU_CREATE = "Сотрудник #initiator создал(а) индивидуальный #mainobject для подразделения #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: создание индивидуального расписания для
	 * сотрудника.
	 */
	String BUSINESS_JOURNAL_SPECIAL_SCHEDULE_EMPLOYEE_CREATE = "Сотрудник #initiator создал(а) индивидуальный #mainobject для сотрудника #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: удаление типового расписания для орг.
	 * единицы.
	 */
	String BUSINESS_JOURNAL_COMMON_SCHEDULE_OU_DELETE = "Сотрудник #initiator удалил(а) типовой #mainobject для подразделения #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: удаление типового расписания для
	 * сотрудника.
	 */
	String BUSINESS_JOURNAL_COMMON_SCHEDULE_EMPLOYEE_DELETE = "Сотрудник #initiator удалил(а) типовой #mainobject для сотрудника #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: удаление индивидуального расписания для
	 * орг. единицы.
	 */
	String BUSINESS_JOURNAL_SPECIAL_SCHEDULE_OU_DELETE = "Сотрудник #initiator удалил(а) индивидуальный #mainobject для подразделения #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: удаление индивидуального расписания для
	 * осотрудника
	 */
	String BUSINESS_JOURNAL_SPECIAL_SCHEDULE_EMPLOYEE_DELETE = "Сотрудник #initiator удалил(а) индивидуальный #mainobject для сотрудника #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: планирование отсутствия.
	 */
	String BUSINESS_JOURNAL_ABSENCE_ADD = "Сотрудник #initiator запланировал(а) #mainobject для сотрудника #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: удаление отсутствия.
	 */
	String BUSINESS_JOURNAL_ABSENCE_DELETE = "Сотрудник #initiator удалил(а) #mainobject для сотрудника #object1";
	/**
	 * Шаблон сообщения в бизнес-журнал: продление отсутствия.
	 */
	String BUSINESS_JOURNAL_ABSENCE_PROLONG = "Бессрочное #mainobject сотрудника #object1 продлено";
	/**
	 * Шаблон сообщения в бизнес-журнал: старт отсутствия.
	 */
	String BUSINESS_JOURNAL_ABSENCE_START = "Сотрудник #object1 отсутствует в офисе (#mainobject до #object2)";
	/**
	 * Шаблон сообщения в бизнес-журнал: окончание отсутствия.
	 */
	String BUSINESS_JOURNAL_ABSENCE_END = "Сотрудник #object1 вернулся в офис (#mainobject с #object2)";

	/**
	 * Получение ссылки на корневые каталоги для календарей, графиков работы и
	 * отсутствия.
	 *
	 * @return nodeRef контейнера
	 */
	NodeRef getWCalendarContainer();

	/**
	 * Создание папки для календарей, графиков работы и отсутствия.
	 * @return
	 */
	NodeRef createWCalendarContainer();
	/**
	 * Получение обекта класса, реализующего интерфейс IWCalCommon
	 * (CalendarBean, AbsenceBean, ScheduleBean).
	 *
	 * @return объект CalendarBean, AbsenceBean или ScheduleBean
	 */
	ICommonWCalendar getWCalendarDescriptor();

	/**
	 * Получение правильно оформленного типа данных для календаря, графика
	 * работы или отсутсвия.
	 *
	 * @return qualified name типа данных с namespace-ом
	 */
	QName getWCalendarItemType();
}
