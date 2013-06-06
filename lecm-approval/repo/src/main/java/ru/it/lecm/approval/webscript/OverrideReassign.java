package ru.it.lecm.approval.webscript;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author akatamanov
 */
public class OverrideReassign extends DeclarativeWebScript {

    private final static Logger logger = LoggerFactory.getLogger(OverrideReassign.class);

    private WorkflowService workflowService;
    private NodeService nodeService;
    private ApprovalListService approvalListService;
    private DictionaryService dictionaryService;
    private OrgstructureBean orgstructureService;

    public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setApprovalListService(ApprovalListService approvalListService) {
		this.approvalListService = approvalListService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

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

        grandPermissions(taskID, employeeNodeRef);

        return result;
    }

    private void grandPermissions(String taskID, NodeRef employeeNodeRef) {
        NodeRef documentRef = null;
        List<NodeRef> packageContents = workflowService.getPackageContents(taskID);
        for (NodeRef node : packageContents) {
            if (dictionaryService.isSubClass(nodeService.getType(node), DocumentService.TYPE_BASE_DOCUMENT)) {
                documentRef = node;
                logger.trace("Found documentRef!");
                break;
            }
        }

        if (documentRef != null){
             approvalListService.grantReviewerPermissionsInternal(employeeNodeRef, documentRef );
        }else{
            logger.trace("No documentRef presented.");
        }
    }
}
