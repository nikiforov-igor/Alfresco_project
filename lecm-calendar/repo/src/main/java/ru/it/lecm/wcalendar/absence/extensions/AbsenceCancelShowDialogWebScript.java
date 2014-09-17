package ru.it.lecm.wcalendar.absence.extensions;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.absence.IAbsence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vlevin
 */
public class AbsenceCancelShowDialogWebScript extends DeclarativeWebScript {

	private IAbsence absenceService;
	private OrgstructureBean orgstructureService;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceCancelShowDialogWebScript.class);

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> model = new HashMap<String, Object>();
		boolean result;
		HttpServletRequest request = WebScriptServletRuntime.getHttpServletRequest(req);
		HttpSession session = request.getSession();
		Boolean cancelDenied = (Boolean) session.getAttribute("absence_cancel_denied");

		if (cancelDenied != null && cancelDenied) {
			result = false;
		} else {
			NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
            if (currentEmployee == null) {
                result = false;
            } else {
			    result = absenceService.isEmployeeAbsentToday(currentEmployee);
            }
			session.setAttribute("absence_cancel_denied", true);
		}

		model.put("showCancelAbsenceDialog", result);
		return model;
	}
}
