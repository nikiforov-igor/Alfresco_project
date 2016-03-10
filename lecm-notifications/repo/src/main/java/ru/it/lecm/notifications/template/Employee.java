package ru.it.lecm.notifications.template;

/**
 *
 * @author vkuprin
 */
public interface Employee {

	/**
	 * Получить инициалы сотрудника: Иванов Андрей Петрович -> ИАП
	 * @return
	 */
	String getEmployeeInitials();

	/**
	 * Получить код подразделения, в котором занимает основную должность
	 * сотрудник. Если код не указан или сотрудник
	 * не занимает должностей, то результатом будет "NA".
	 * @return
	 */
	String getEmployeeOrgUnitCode();

	/**
	 * Получить табельный номер сотрудника. Если номер не указан, то
	 * строка "NA".
	 * @return
	 */
	String getEmployeeNumber();
	
	/**
	 * Получить подразделение, в котором занимает основную должность
	 * сотрудник. Если код не указан или сотрудник
	 * не занимает должностей, то результатом будет "NA".
	 * @return
	 */
	CMObject getEmployeeOrgUnit();
	
}
