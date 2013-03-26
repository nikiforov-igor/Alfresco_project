package ru.it.lecm.regnumbers.template;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * Утилитарные функции, которые генератор рег. номеров будет использовать в
 * своей работе. Методы этого класса будут зарегистрированы в контексте SpEL'а
 * как встроенные функции (#function()). В этот класс следует добавлять только
 * функции, не связанные напрямую с экземпляром документа. Функции документа
 * должны находиться в ru.it.lecm.regnumbers.template.DocumentImpl
 *
 * @author vlevin
 */
public final class Utils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	private final static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static Method getDeclaredMethod(String name, Class<?>... parameterTypes) {
		Method method = null;
		try {
			method = Utils.class.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException ex) {
			logger.error("Can't get declared method named " + name, ex);
		}
		return method;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Utils.applicationContext = applicationContext;
	}

	/**
	 * Форматировать дату по правилам DateFormat/
	 */
	public static String formatDate(String format, Date date) {
		DateFormat dateFormatter = new SimpleDateFormat(format);
		return dateFormatter.format(date);
	}

	/**
	 * Форматировать текущую дату по правилам DateFormat/
	 */
	public static String formatDate(String format) {
		return formatDate(format, new Date());
	}

	/**
	 * Форматировать целочисленное значение по правилам DecimalFormat
	 */
	public static String formatNumber(String format, Long number) {
		DecimalFormat decimalFormatter = new DecimalFormat(format);
		return decimalFormatter.format(number);
	}

	/**
	 * Получить код подразделения, в котором занимает основную должность
	 * указанный сотрудник. Если код не указан, то пустая строка. Если сотрудник
	 * не занимает должностей, то генерируется исключение.
	 */
	public static String employeeOrgUnitCode(NodeRef employeeNode) {
		OrgstructureBean orgstructureService = applicationContext.getBean("serviceOrgstructure", OrgstructureBean.class);
		NodeService nodeService = applicationContext.getBean("nodeService", NodeService.class);
		NodeRef employeeUnit = orgstructureService.getUnitByStaff(orgstructureService.getEmployeePrimaryStaff(employeeNode));
		return (String) nodeService.getProperty(employeeUnit, OrgstructureBean.PROP_UNIT_CODE);
	}

	/**
	 * Получить инициалы указанного сотрудника: Иванов Андрей Петрович -> ИАП
	 */
	public static String employeeInitials(NodeRef employeeNode) {
		NodeService nodeService = applicationContext.getBean("nodeService", NodeService.class);
		String lastName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
		String firstName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
		String middleName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);

		return Character.toString(lastName.charAt(0)) + Character.toString(firstName.charAt(0)) + Character.toString(middleName.charAt(0));
	}

	/**
	 * Получить табельный номер указанного сотрудника. Если номер не указан, то
	 * пустая строка.
	 */
	public static String employeeNumber(NodeRef employeeNode) {
		NodeService nodeService = applicationContext.getBean("nodeService", NodeService.class);
		Long employeeCode = (Long) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_NUMBER);
		return employeeCode != null ? String.valueOf(employeeCode) : "";
	}

	private Utils() {
	}
}
