package ru.it.lecm.orgstructure.exportimport.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.lang.StringUtils;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.http.MediaType;
import ru.it.lecm.orgstructure.exportimport.beans.OrgstructureImportService;
import ru.it.lecm.orgstructure.exportimport.entity.BusinessRoles;
import ru.it.lecm.orgstructure.exportimport.entity.Departments;
import ru.it.lecm.orgstructure.exportimport.entity.Employees;
import ru.it.lecm.orgstructure.exportimport.entity.Positions;
import ru.it.lecm.orgstructure.exportimport.entity.StaffList;

/**
 *
 * @author vlevin
 */
public class ImportOrgstructureXML extends AbstractWebScript {

	private OrgstructureImportService orgstructureImportService;
	private ThreadPoolExecutor threadPoolExecutor;

	public void setOrgstructureImportService(OrgstructureImportService orgstructureImportService) {
		this.orgstructureImportService = orgstructureImportService;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		FormData formData = (FormData) req.parseContent();
		FormData.FormField[] fields = formData.getFields();

		Employees employees = null;
		Departments departments = null;
		Positions positions = null;
		StaffList staffList = null;
		BusinessRoles businessRoles = null;

		StringBuilder importStatusBuilder = new StringBuilder("Импорт начат. Дождитесь его окончания!\nСм. orgstructureExportImport.log для получения подробностей о выполнении импорта.\nИмпортируются следующие файлы: ");
		for (FormData.FormField field : fields) {
			String fileName = field.getValue();
			InputStream input = field.getInputStream();

			final boolean fileNameIsNotEmpty = !StringUtils.isEmpty(fileName);

			if (fileNameIsNotEmpty) {
				importStatusBuilder.append(fileName).append(" ");
			}

			switch (field.getName()) {
				case "employeesFile":
					employees = fileNameIsNotEmpty ? orgstructureImportService.parseEmployeesXML(input) : null;
					break;
				case "departmentsFile":
					departments = fileNameIsNotEmpty ? orgstructureImportService.parseDepartmentsXML(input) : null;
					break;
				case "positionsFile":
					positions = fileNameIsNotEmpty ? orgstructureImportService.parsePositionsXML(input) : null;
					break;
				case "staffListFile":
					staffList = fileNameIsNotEmpty ? orgstructureImportService.parseStaffXML(input) : null;
					break;
				case "businessRolesFile":
					businessRoles = fileNameIsNotEmpty ? orgstructureImportService.parseBusinessRolesXML(input) : null;
					break;
				default:
					throw new WebScriptException("Unknown field: " + field.getName());
			}
		}

		ImportOrgstructureXMLRunner runner = new ImportOrgstructureXMLRunner(orgstructureImportService, employees, departments, positions, staffList, businessRoles);
		threadPoolExecutor.execute(runner);

		res.setContentType(MediaType.TEXT_PLAIN.toString());
		res.setContentEncoding("UTF-8");

		res.getWriter().append(importStatusBuilder).close();

	}

}
