package ru.it.lecm.orgstructure.exportimport.beans;

import java.io.InputStream;
import ru.it.lecm.orgstructure.exportimport.entity.BusinessRoles;
import ru.it.lecm.orgstructure.exportimport.entity.CreatedItems;
import ru.it.lecm.orgstructure.exportimport.entity.Departments;
import ru.it.lecm.orgstructure.exportimport.entity.Employees;
import ru.it.lecm.orgstructure.exportimport.entity.Positions;
import ru.it.lecm.orgstructure.exportimport.entity.StaffList;

/**
 *
 * @author vlevin
 */
public interface OrgstructureImportService {

	void importDepartments(Departments departments, CreatedItems createdItems);

	void importEmployees(Employees employees, CreatedItems createdItems);

	void importPositions(Positions positions, CreatedItems createdItems);

	void importStaff(StaffList staffList, CreatedItems createdItems);

	void importBusinessRoles(BusinessRoles businessRoles, CreatedItems createdItems);

	Departments parseDepartmentsXML(InputStream input);

	Employees parseEmployeesXML(InputStream input);

	Positions parsePositionsXML(InputStream input);

	StaffList parseStaffXML(InputStream input);

	BusinessRoles parseBusinessRolesXML(InputStream input);

}
