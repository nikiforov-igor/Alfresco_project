package ru.it.lecm.workflow.approval.webscript.deprecated;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author akatamanov
 */
@Deprecated
public class OverrideReassign extends DeclarativeWebScript {

    private final static Logger logger = LoggerFactory.getLogger(OverrideReassign.class);

    private OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        Map<String, Object> result = new HashMap<String, Object>();
        String taskID = req.getParameter("taskID");
        String employeeNodeRefStr = req.getParameter("employeeNodeRef");

        if (taskID == null || taskID.isEmpty()){
            logger.error("No taskID presented");
            throw new WebScriptException("No taskID presented");
        }

        if (employeeNodeRefStr == null || employeeNodeRefStr.isEmpty() || !NodeRef.isNodeRef(employeeNodeRefStr)){
            logger.error("No employeeNodeRef presented");
            throw new WebScriptException("No employeeNodeRef presented");
        }

        final NodeRef employeeNodeRef = new NodeRef(employeeNodeRefStr);
        if (!orgstructureService.isEmployee(employeeNodeRef))
        {
            logger.error("The type of employeeNodeRef is not a TYPE_EMPLOYEE "  + employeeNodeRefStr);
            throw new WebScriptException("The type of employeeNodeRef is not a TYPE_EMPLOYEE "  + employeeNodeRefStr);
        }

        String personLogin = orgstructureService.getEmployeeLogin(employeeNodeRef);
        if (personLogin == null || personLogin.isEmpty()){
            logger.error("The employee has no associated person " + employeeNodeRefStr);
            throw new WebScriptException("The employee has no associated person " + employeeNodeRefStr);
        }

        result.put("personLogin", personLogin);

        return result;
    }
}
