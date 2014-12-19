package ru.it.lecm.orgstructure.exportimport.webscripts;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.exportimport.beans.OrgstructureImportService;
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
public class ImportOrgstructureXMLRunner implements AuthenticationUtil.RunAsWork<Object>, Runnable {

	private final OrgstructureImportService orgstructureImportService;

	private final static Logger logger = LoggerFactory.getLogger(ImportOrgstructureXMLRunner.class);

	private final Employees employees;
	private final Departments departments;
	private final Positions positions;
	private final StaffList staffList;
	private final BusinessRoles businessRoles;
	private final CreatedItems createdItems = new CreatedItems();

	public ImportOrgstructureXMLRunner(OrgstructureImportService orgstructureImportService, Employees employees, Departments departments,
			Positions positions, StaffList staffList, BusinessRoles businessRoles) {
		this.orgstructureImportService = orgstructureImportService;
		this.employees = employees;
		this.departments = departments;
		this.positions = positions;
		this.staffList = staffList;
		this.businessRoles = businessRoles;
	}

	@Override
	public Object doWork() throws Exception {
		try {
			if (positions != null) {
				orgstructureImportService.importPositions(positions, createdItems);
			}

			if (employees != null) {
				orgstructureImportService.importEmployees(employees, createdItems);
			}

			if (departments != null) {
				orgstructureImportService.importDepartments(departments, createdItems);
			}

			if (staffList != null) {
				orgstructureImportService.importStaff(staffList, createdItems);
			}

			if (businessRoles != null) {
				orgstructureImportService.importBusinessRoles(businessRoles, createdItems);
			}
		} catch (Exception ex) {
			logger.error("Error!", ex);
		}

		return null;
	}

	@Override
	public void run() {
		AuthenticationUtil.runAsSystem(this);
	}

}
