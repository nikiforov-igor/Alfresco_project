package ru.it.lecm.orgstructure.exportimport.webscripts;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.lang.StringUtils;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.orgstructure.exportimport.beans.OrgstructureExportService;

/**
 *
 * @author vlevin
 */
public class ExportOrgstructureXML extends AbstractWebScript {

	private OrgstructureExportService orgstructureExportService;

	public void setOrgstructureExportService(OrgstructureExportService orgstructureExportService) {
		this.orgstructureExportService = orgstructureExportService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		final String requestPath = req.getPathInfo();
		final String invocationMode = StringUtils.substringAfterLast(requestPath, "/");

		res.setContentEncoding("UTF-8");
		res.setContentType("text/xml");
		res.addHeader("Content-Disposition", "attachment; filename=orgstructure-export-" + invocationMode + ".xml");

		OutputStream outputStream = res.getOutputStream();

		switch (invocationMode) {
			case "positions":
				orgstructureExportService.getPositionsXML(orgstructureExportService.getPositions(), outputStream);
				break;
			case "employees":
				orgstructureExportService.getEmployeesXML(orgstructureExportService.getEmployees(), outputStream);
				break;
			case "departments":
				orgstructureExportService.getDepartmentsXML(orgstructureExportService.getDepartments(), outputStream);
				break;
			case "staffList":
				orgstructureExportService.getStaffListXML(orgstructureExportService.getStaffList(), outputStream);
				break;
			case "businessRoles":
				orgstructureExportService.getBusinessRolesXML(orgstructureExportService.getBusinessRoles(), outputStream);
				break;
			default:
				throw new WebScriptException("Unknown export orgstructure webscript invocation mode: " + invocationMode);
		}
	}

}
