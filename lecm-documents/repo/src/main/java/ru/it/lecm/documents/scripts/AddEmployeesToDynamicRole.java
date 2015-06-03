package ru.it.lecm.documents.scripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.IOException;

/**
 * User: AIvkin
 * Date: 23.04.2014
 * Time: 11:38
 */
public class AddEmployeesToDynamicRole extends AbstractWebScript {
    private StateMachineServiceBean stateMachineService;
    private NodeService nodeService;

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        Content c = req.getContent();
        try {
            JSONObject json = new JSONObject(c.getContent());
            String employees = (String) json.get("assoc_lecm-orgstr_business-role-employee-assoc");
            String roleId = (String) json.get("roleId");
            String document = (String) json.get("document");
            if (employees != null && roleId != null && document != null) {
                NodeRef documentRef = new NodeRef(document);
                if (nodeService.exists(documentRef)) {
                    String[] employeesArray = employees.split(",");
                    for (String anEmployeesArray : employeesArray) {
                        NodeRef employeeRef = new NodeRef(anEmployeesArray);
                        if (nodeService.exists(employeeRef)) {
                            stateMachineService.grandDynamicRoleForEmployee(documentRef, employeeRef, roleId);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Unable to parse JSON POST body: " + e.getMessage());
        }
    }
}
