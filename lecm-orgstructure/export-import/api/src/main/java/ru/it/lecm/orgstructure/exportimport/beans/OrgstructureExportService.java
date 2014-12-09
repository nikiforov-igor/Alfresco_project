package ru.it.lecm.orgstructure.exportimport.beans;

import java.io.OutputStream;
import ru.it.lecm.orgstructure.exportimport.entity.BusinessRoles;
import ru.it.lecm.orgstructure.exportimport.entity.Departments;
import ru.it.lecm.orgstructure.exportimport.entity.Employees;
import ru.it.lecm.orgstructure.exportimport.entity.Positions;
import ru.it.lecm.orgstructure.exportimport.entity.StaffList;

/**
 *
 * @author vlevin
 */
public interface OrgstructureExportService {

	BusinessRoles getBusinessRoles();

	Departments getDepartments();

	Employees getEmployees();

	Positions getPositions();

	StaffList getStaffList();

	void getBusinessRolesXML(BusinessRoles businessRoles, OutputStream output);

	void getDepartmentsXML(Departments departments, OutputStream output);

	void getEmployeesXML(Employees employees, OutputStream output);

	void getPositionsXML(Positions positions, OutputStream output);

	void getStaffListXML(StaffList staffList, OutputStream output);

}
