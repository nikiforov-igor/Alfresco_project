package ru.it.lecm.wcalendar;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * API для получения информации о рабочих и выходных днях сотрудника.
 *
 * @see ru.it.lecm.wcalendar.absence.IAbsence
 * @see ru.it.lecm.wcalendar.schedule.ISchedule
 * @see ru.it.lecm.wcalendar.calendar.ICalendar
 * @author vlevin
 */
public interface IWorkCalendar {

	/**
	 * Узнать, работает ли сотрудник в указанный день.
	 * Учитывается график работы сотрудника, выходные и рабочие дни
	 * производственного календаря, запланированные отсутствия сотрудника.
	 * Если не настроены графики работы для сотрудника и его вышестоящих
	 * подразделений или отсутствует производственный календарь на интересующую
	 * дату, то генерируется исключение IllegalArgumentException
	 *
	 * @param node NodeRef на сотрудника.
	 * @param day интересующая дата.
	 * @return сотрудник работает в указанную дату - true
	 */
	boolean getEmployeeAvailability(NodeRef node, Date day);

	/**
	 * Проверить, является ли указанный день рабочим для сотрудника.
	 * Учитывается график работы сотрудника, выходные и рабочие дни
	 * производственного календаря. Запланированные отсутствия не берутся в расчет!
	 *
	 * @param employeeNode NodeRef сотрудника.
	 * @param day интересующая дата.
	 * @return рабочий ли день для сотрудника.
	 */
	boolean isWorkingDayForEmployee(NodeRef employeeNode, Date day);

	/**
	 * Получить список рабочих дней сотрудника в указанный период времени.
	 * Учитывается график работы сотрудника, выходные и рабочие дни
	 * производственного календаря, запланированные отсутствия сотрудника.
	 * Если не настроены графики работы для сотрудника и его вышестоящих
	 * подразделений или отсутствует производственный календарь на
	 * интересующую дату, то генерируется исключение IllegalArgumentException
	 *
	 * @param node NodeRef на сотрудника.
	 * @param start начало периода, на который надо получить рабочие дни.
	 * @param end конец периода, на который надо получить рабочие дни.
	 * @return список дат рабочих дней сотрудника.
	 */
	List<Date> getEmployeeWorkindDays(NodeRef node, Date start, Date end);

    Map<NodeRef,List<Date>> getEmployeesWorkingDaysMap(List<NodeRef> employeesRefs, Date start, Date end);

	/**
	 * Получить список выходных дней сотрудника в указанный период времени.
	 * Учитывается график работы сотрудника, выходные и рабочие дни
	 * производственного календаря, запланированные отсутствия сотрудника.
	 * Если не настроены графики работы для сотрудника и его вышестоящих
	 * подразделений или отсутствует производственный календарь на интересующую
	 * дату, то генерируется исключение IllegalArgumentException
	 *
	 * @param node NodeRef на сотрудника.
	 * @param start начало периода, на который надо получить выходные дни.
	 * @param end конец периода, на который надо получить выходные дни.
	 * @return список дат выходных дней сотрудника.
	 */
	List<Date> getEmployeeNonWorkindDays(NodeRef node, Date start, Date end);

	/**
	 * Получить количество рабочих дней сотрудника в указанный период времени.
	 * Учитывается график работы сотрудника, выходные и рабочие дни
	 * производственного календаря, запланированные отсутствия сотрудника.
	 * Если не настроены графики работы для сотрудника и его вышестоящих
	 * подразделений или отсутствует производственный календарь на интересующую
	 * дату, то генерируется исключение IllegalArgumentException
	 *
	 * @param node NodeRef на сотрудника.
	 * @param start начало периода, на который надо получить рабочие дни.
	 * @param end конец периода, на который надо получить рабочие дни.
	 * @return количество рабочих дней сотрудника.
	 */
	int getEmployeeWorkingDaysNumber(NodeRef node, Date start, Date end);

	/**
	 * Получить плановую дату выполнения сотрудником задачи.
	 * Учитывается график работы сотрудника, выходные и рабочие дни
	 * производственного календаря, запланированные отсутствия сотрудника.
	 * Если не настроены графики работы для сотрудника и его вышестоящих
	 * подразделений или отсутствует производственный календарь на интересующую
	 * дату, то генерируется исключение IllegalArgumentException
	 *
	 * @param node NodeRef на сотрудника.
	 * @param start начало выполнения задачи.
	 * @param workingDaysRequired количество рабочих дней, необходимых для
	 * выполнения задачи.
	 * @return плановая дата выполнения задачи.
	 */
	Date getPlannedJobFinish(NodeRef node, Date start, int workingDaysRequired);

	/**
	 * Получить дату, отстоящую от заданной на указанное количество дней, часов или минут.
	 * Учитывается только производственный календарь.
	 *
	 * @param start дата начала отсчета.
	 * @param offset сколько рабочих дней/часов/минут должно пройти.
	 * @param timeUnit в каких единицах отсчитываем время.
	 * Принимает значения:
	 * java.util.Calendar.DAY_OF_MONTH
	 * java.util.Calendar.HOUR_OF_DAY
	 * java.util.Calendar.MINUTE
	 * @return Дата, отстоящая от заданной на заданное количество дней/часов/минут.
	 * Если отсутствует производственный календарь, то null.
	 */
	Date getNextWorkingDate(Date start, int offset, int timeUnit);

	/**
	 * Получить дату, отстоящую от заданной на указанное количество дней, часов или минут.
	 * Учитывается только производственный календарь.
	 *
	 * @param start дата начала отсчета.
	 * @param offset сколько рабочих дней/часов/минут должно пройти.
	 * Строка должна иметь вид "Nd", "Nh", "Nm" или "N",
	 * где N - число единиц времени, d обозначает дни, h - часы, m - * минуты.
	 * Если единицы времени не указаны, то по умолчанию считаются дни.
	 * @return Дата, отстоящая от заданной на заданное количество дней/часов/минут.
	 * Если отсутствует производственный календарь, то null.
	 */
	Date getNextWorkingDate(Date start, String offset);

	/**
	 * Получить дату, отстоящую от заданной на указанное количество рабочих
	 * дней. Учитывается только производственный календарь.
	 *
	 * @param start дата начала отсчета.
	 * @param workingDaysNumber сколько рабочих дней должно пройти.
	 * @return Дата, отстоящая от заданной на заданное количество дней. Если
	 * отсутствует производственный календарь, то null.
	 */
	Date getNextWorkingDateByDays(Date start, int workingDaysNumber);

	/**
	 * Получить дату, отстоящую от заданной на указанное количество часов.
	 * Учитывается производственный календарь и общесистемный график работы.
	 *
	 * @param start дата начала отсчета.
	 * @param hoursAmount сколько часов должно пройти.
	 * @return Дата, отстоящая от заданной на заданное количество часов. Если
	 * отсутствует производственный календарь, то null.
	 */
	Date getNextWorkingDateByHours(Date start, int hoursAmount);

	/**
	 * Получить дату, отстоящую от заданной на указанное количество минут.
	 * Учитывается производственный календарь и общесистемный график работы.
	 *
	 * @param start дата начала отсчета.
	 * @param minutesAmount сколько минут должно пройти.
	 * @return Дата, отстоящая от заданной на заданное количество минут. Если
	 * отсутствует производственный календарь, то null.
	 */
	Date getNextWorkingDateByMinutes(Date start, int minutesAmount);

	/**
	 * Получить рабочий день указанного сотрудника, отстоящий от указанной даты
	 * на заданное количество дней. Если выпавший день будет выходным, то вернет
	 * следующий рабочий день за выходными. Учитываются графики работы и
	 * отсутствия.
	 *
	 * @param node интересующий сотрудник
	 * @param initialDate дата начала отсчета
	 * @param offset смещение в днях. Может быть отрицательным или нулевым
	 * @return дата рабочего дня сотрудника
	 */
	Date getEmployeeNextWorkingDay(NodeRef node, Date initialDate, int offset);

	/**
	 * Получить рабочий день указанного сотрудника, отстоящий от указанной даты
	 * на заданное количество дней. Если выпавший день будет выходным, то вернет
	 * первый рабочий день до выходных. Учитываются графики работы и
	 * отсутствия.
	 *
	 * @param node интересующий сотрудник
	 * @param initialDate дата начала отсчета
	 * @param offset смещение в днях. Может быть отрицательным или нулевым
	 * @return дата рабочего дня сотрудника
	 */
	Date getEmployeePreviousWorkingDay(NodeRef node, Date initialDate, int offset);
}
