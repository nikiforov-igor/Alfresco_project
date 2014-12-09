package ru.it.lecm.orgstructure.exportimport.beans;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.exportimport.ExportImportHelper;
import ru.it.lecm.orgstructure.exportimport.entity.BusinessRole;
import ru.it.lecm.orgstructure.exportimport.entity.BusinessRoles;
import ru.it.lecm.orgstructure.exportimport.entity.Department;
import ru.it.lecm.orgstructure.exportimport.entity.Departments;
import ru.it.lecm.orgstructure.exportimport.entity.Employee;
import ru.it.lecm.orgstructure.exportimport.entity.Employees;
import ru.it.lecm.orgstructure.exportimport.entity.Position;
import ru.it.lecm.orgstructure.exportimport.entity.Positions;
import ru.it.lecm.orgstructure.exportimport.entity.Staff;
import ru.it.lecm.orgstructure.exportimport.entity.StaffList;

/**
 *
 * @author vlevin
 */
public class OrgstructureExportServiceImpl extends BaseBean implements OrgstructureExportService {

	private final static Logger logger = LoggerFactory.getLogger(OrgstructureExportServiceImpl.class);

	private NamespaceService namespaceService;
	private SearchService searchService;
	private OrgstructureBean orgstructureService;

	private ExportImportHelper helper;

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
//		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
//		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
//		PropertyCheck.mandatory(this, "personService", personService);
//		PropertyCheck.mandatory(this, "authService", authService);
//		PropertyCheck.mandatory(this, "behaviourFilter", behaviourFilter);
		PropertyCheck.mandatory(this, "searchService", searchService);
		PropertyCheck.mandatory(this, "namespaceService", namespaceService);

		helper = new ExportImportHelper(nodeService, namespaceService, searchService, orgstructureService);
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public Positions getPositions() {
		Positions positions = new Positions();

		List<Position> positionList = positions.getPosition();
		for (NodeRef positionNode : orgstructureService.getStaffPositions(true)) {
			Position position = new Position();

			position.setId(helper.getNodeRefID(positionNode));

			Map<QName, Serializable> properties = nodeService.getProperties(positionNode);
			position.setCode((String) properties.get(OrgstructureBean.PROP_STAFF_POSITION_CODE));
			position.setName((String) properties.get(ContentModel.PROP_NAME));
			position.setNameDative((String) properties.get(OrgstructureBean.PROP_STAFF_POSITION_NAME_D));
			position.setNameGenitive((String) properties.get(OrgstructureBean.PROP_STAFF_POSITION_NAME_G));

			positionList.add(position);
		}

		return positions;
	}

	@Override
	public Employees getEmployees() {
		Employees employees = new Employees();

		List<Employee> employeeList = employees.getEmployee();
		for (NodeRef employeeNode : helper.getAllEmployees()) {

			Employee employee = new Employee();

			employee.setId(helper.getNodeRefID(employeeNode));
			Map<QName, Serializable> properties = nodeService.getProperties(employeeNode);

			employee.setFirstname((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME));
			employee.setMiddlename((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME));
			employee.setLastname((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME));
			employee.setEmail((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_EMAIL));
			employee.setNameDative((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_FIO_D));
			employee.setNameGenitive((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_FIO_G));
			employee.setNumber((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_NUMBER));
			employee.setPhone((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_PHONE));
			employee.setSex((String) properties.get(OrgstructureBean.PROP_EMPLOYEE_SEX));

			String employeeLogin = orgstructureService.getEmployeeLogin(employeeNode);
			employee.setLogin(employeeLogin);

			employeeList.add(employee);
		}

		return employees;
	}

	@Override
	public Departments getDepartments() {
		Departments departments = new Departments();

		List<Department> departmentList = departments.getDepartment();
		NodeRef rootUnit = orgstructureService.getRootUnit();

		for (NodeRef departmentNode : helper.getAllOrgUnits()) {
			if (departmentNode.equals(rootUnit)) {
				// нам попался корневой элемент оргструкттуры
				continue;
			}

			String pid;
			NodeRef departmentParent = nodeService.getPrimaryParent(departmentNode).getParentRef();
			if (departmentParent.equals(rootUnit)) {
				// организация
				pid = "0";
			} else {
				pid = helper.getNodeRefID(departmentParent);
			}

			Map<QName, Serializable> properties = nodeService.getProperties(departmentNode);

			Department department = new Department();
			department.setId(helper.getNodeRefID(departmentNode));
			department.setPid(pid);
			department.setNameFull((String) properties.get(OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME));
			department.setNameShort((String) properties.get(OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME));
			department.setCode((String) properties.get(OrgstructureBean.PROP_UNIT_CODE));
			department.setType((String) properties.get(OrgstructureBean.PROP_UNIT_TYPE));

			departmentList.add(department);
		}

		return departments;
	}

	@Override
	public StaffList getStaffList() {
		StaffList staffList = new StaffList();

		List<Staff> staffs = staffList.getStaff();

		for (NodeRef staffNode : helper.getAllStaff()) {
			Staff staff = new Staff();

			NodeRef departmentNode = orgstructureService.getUnitByStaff(staffNode);
			NodeRef positionNode = orgstructureService.getPositionByStaff(staffNode);
			NodeRef employeeNode = orgstructureService.getEmployeeByPosition(staffNode);

			boolean isLeading = (boolean) nodeService.getProperty(staffNode, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);
			NodeRef employeeLink = orgstructureService.getEmployeeLinkByPosition(staffNode);

			boolean isPrimary;
			if (employeeLink != null) {
				isPrimary = (boolean) nodeService.getProperty(employeeLink, OrgstructureBean.PROP_EMP_LINK_IS_PRIMARY);
			} else {
				isPrimary = true;
			}

			String description = (String) nodeService.getProperty(staffNode, OrgstructureBean.PROP_STAFF_LIST_DESCRIPTION);

			staff.setId(helper.getNodeRefID(staffNode));
			staff.setDepartmentId(helper.getNodeRefID(departmentNode));
			staff.setEmployeeId(helper.getNodeRefID(employeeNode));
			staff.setPositionId(helper.getNodeRefID(positionNode));
			staff.setLeading(isLeading);
			staff.setPrimary(isPrimary);
			staff.setDescription(description);

			staffs.add(staff);
		}

		return staffList;
	}

	@Override
	public BusinessRoles getBusinessRoles() {
		BusinessRoles businessRoles = new BusinessRoles();

		List<BusinessRole> businessRoleList = businessRoles.getBusinessRole();

		for (NodeRef businessRoleNode : orgstructureService.getBusinesRoles(true)) {
			BusinessRole businessRole = new BusinessRole();

			String id = (String) nodeService.getProperty(businessRoleNode, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
			businessRole.setId(id);

			String name = (String) nodeService.getProperty(businessRoleNode, ContentModel.PROP_NAME);
			businessRole.setName(name);

			String description = (String) nodeService.getProperty(businessRoleNode, OrgstructureBean.PROP_BUSINESS_ROLE_DESCRIPTION);
			businessRole.setDescription(description);

			boolean isDynamic = (boolean) nodeService.getProperty(businessRoleNode, OrgstructureBean.PROP_BUSINESS_ROLE_IS_DYNAMIC);
			businessRole.setDynamic(isDynamic);

			List<String> employeesIDs = businessRole.getEmployees().getId();
			List<String> departmentsIDs = businessRole.getDepartments().getId();
			List<String> staffIDs = businessRole.getStaffs().getId();

			List<NodeRef> employeesByBusinessRole = orgstructureService.getEmployeesByBusinessRole(businessRoleNode);
			List<NodeRef> departmentsByBusinessRole = orgstructureService.getOrganizationElementsByBusinessRole(businessRoleNode, false);
			List<NodeRef> staffsByBusinessRole = orgstructureService.getOrganizationElementMembersByBusinessRole(businessRoleNode);

			for (NodeRef node : employeesByBusinessRole) {
				employeesIDs.add(helper.getNodeRefID(node));
			}

			for (NodeRef node : departmentsByBusinessRole) {
				departmentsIDs.add(helper.getNodeRefID(node));
			}

			for (NodeRef node : staffsByBusinessRole) {
				staffIDs.add(helper.getNodeRefID(node));
			}

			businessRoleList.add(businessRole);
		}

		return businessRoles;
	}

	@Override
	public void getPositionsXML(Positions positions, OutputStream output) {
		try {
			JAXBContext context = JAXBContext.newInstance(Positions.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(positions, output);
		} catch (JAXBException ex) {
			throw new AlfrescoRuntimeException("Error marshalling Positions", ex);
		}
	}

	@Override
	public void getDepartmentsXML(Departments departments, OutputStream output) {
		try {
			JAXBContext context = JAXBContext.newInstance(Departments.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(departments, output);
		} catch (JAXBException ex) {
			throw new AlfrescoRuntimeException("Error marshalling Departments", ex);
		}
	}

	@Override
	public void getEmployeesXML(Employees employees, OutputStream output) {
		try {
			JAXBContext context = JAXBContext.newInstance(Employees.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(employees, output);
		} catch (JAXBException ex) {
			throw new AlfrescoRuntimeException("Error marshalling Employees", ex);
		}
	}

	@Override
	public void getStaffListXML(StaffList staffList, OutputStream output) {
		try {
			JAXBContext context = JAXBContext.newInstance(StaffList.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(staffList, output);
		} catch (JAXBException ex) {
			throw new AlfrescoRuntimeException("Error marshalling StaffList", ex);
		}
	}

	@Override
	public void getBusinessRolesXML(BusinessRoles businessRoles, OutputStream output) {
		try {
			JAXBContext context = JAXBContext.newInstance(BusinessRoles.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(businessRoles, output);
		} catch (JAXBException ex) {
			throw new AlfrescoRuntimeException("Error marshalling BusinessRoles", ex);
		}
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

}
