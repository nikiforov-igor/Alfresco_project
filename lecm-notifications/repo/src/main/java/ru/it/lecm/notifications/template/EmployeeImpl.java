package ru.it.lecm.notifications.template;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vkuprin
 */
public class EmployeeImpl extends CMObjectImpl implements Employee {

	private final OrgstructureBean orgstructureService;
	
	public EmployeeImpl(NodeRef ref, ApplicationContext applicationContext) {
		super(ref, applicationContext);
		orgstructureService = applicationContext.getBean("serviceOrgstructure", OrgstructureBean.class);
	}
	
	/**
	 * Получить инициалы сотрудника: Иванов Андрей Петрович -> ИАП
	 * @return 
	 */
	@Override
	public String getEmployeeInitials() {
		String lastName = (String) nodeService.getProperty(nodeRef, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
		String firstName = (String) nodeService.getProperty(nodeRef, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
		String middleName = (String) nodeService.getProperty(nodeRef, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
		return Character.toString(lastName.charAt(0)) + Character.toString(firstName.charAt(0)) + Character.toString(middleName.charAt(0));
	}

	/**
	 * Получить код подразделения, в котором занимает основную должность
	 * сотрудник. Если код не указан или сотрудник
	 * не занимает должностей, то результатом будет "NA".
	 * @return 
	 */
	
	@Override
	public String getEmployeeOrgUnitCode() {
		String result = "";
		NodeRef employeeUnit = orgstructureService.getUnitByStaff(orgstructureService.getEmployeePrimaryStaff(nodeRef));
		if (employeeUnit != null) {
			result = (String) nodeService.getProperty(employeeUnit, OrgstructureBean.PROP_UNIT_CODE);
		}
		return result.isEmpty() ? "NA" : result;
	}
	
	/**
	 * Получить подразделение, в котором сотрудник занимает 
	 * основную должность. Если сотрудник
	 * не занимает должностей, то результатом будет null.
	 * @return 
	 */
	@Override
	public CMObject getEmployeeOrgUnit() {
		CMObject result = null;
		NodeRef employeeUnit = orgstructureService.getUnitByStaff(orgstructureService.getEmployeePrimaryStaff(nodeRef));
		if (employeeUnit != null) {
			result = new CMObjectImpl(employeeUnit, applicationContext);
		}
		return result;
	}
	
	/**
	 * Получить табельный номер сотрудника. Если номер не указан, то
	 * строка "NA".
	 * @return 
	 */
	@Override
	public String getEmployeeNumber() {
		Long employeeCode = (Long) nodeService.getProperty(nodeRef, OrgstructureBean.PROP_EMPLOYEE_NUMBER);
		return employeeCode != null ? String.valueOf(employeeCode) : "NA";
	}

	@Override
	public String getPresentString() {
		return (String) nodeService.getProperty(nodeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
	}

}
