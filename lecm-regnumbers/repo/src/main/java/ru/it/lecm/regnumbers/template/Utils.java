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
 *
 * @author vlevin
 */
public final class Utils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	private static OrgstructureBean orgstructureService = null;
	private static NodeService nodeService = null;


	private final static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static Method getDeclaredMethod (String name, Class<?>... parameterTypes) {
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

	public static String formatDate(String format, Date date) {
		DateFormat dateFormatter = new SimpleDateFormat(format);
		return dateFormatter.format(date);
	}

	public static String formatCurrentDate(String format) {
		return formatDate(format, new Date());
	}

	public static String formatNumber(String format, Long number) {
		DecimalFormat decimalFormatter = new DecimalFormat(format);
		return decimalFormatter.format(number);
	}

	public static String employeeOrgUnitCode(NodeRef employeeNode) {
		initServices();
		NodeRef employeeUnit = orgstructureService.getUnitByStaff(orgstructureService.getEmployeePrimaryStaff(employeeNode));
		return (String) nodeService.getProperty(employeeUnit, OrgstructureBean.PROP_UNIT_CODE);
	}

	public static String employeeInitials(NodeRef employeeNode) {
		initServices();
		String lastName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
		String firstName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
		String middleName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);

		return Character.toString(lastName.charAt(0)) + Character.toString(firstName.charAt(0)) + Character.toString(middleName.charAt(0));
	}

	public static String employeeNumber(NodeRef employeeNode) {
		initServices();
		return (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_NUMBER);
	}

	private static void initServices() {
		if (orgstructureService == null) {
			orgstructureService = applicationContext.getBean("serviceOrgstructure", OrgstructureBean.class);
		}
		if (nodeService == null) {
			nodeService = applicationContext.getBean("nodeService", NodeService.class);
		}
	}

	private Utils() {
	}
}
