package ru.it.lecm.statemachine.action.script;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.documents.beans.DocumentFrequencyAnalysisService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.TransitionResponse;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;

import java.util.HashMap;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:18
 */
public class StartWorkflowScript extends DeclarativeWebScript {

	private static ServiceRegistry serviceRegistry;
    private static DocumentFrequencyAnalysisService frequencyAnalysisService;
    private static OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        StartWorkflowScript.orgstructureService = orgstructureService;
    }

    public void setFrequencyAnalysisService(DocumentFrequencyAnalysisService frequencyAnalysisService) {
        StartWorkflowScript.frequencyAnalysisService = frequencyAnalysisService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StartWorkflowScript.serviceRegistry = serviceRegistry;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> result = new HashMap<String, Object>();
		String taskId = req.getParameter("taskId");
		String persistedResponse = req.getParameter("formResponse");
		String actionId = req.getParameter("actionId");
		String actionType = req.getParameter("actionType");

		//Если есть actionId обрабатываем transitionAction
		if ("trans".equals(actionType)) {
        StateMachineHelper helper = new StateMachineHelper();
        String executionId = helper.getCurrentExecutionId(taskId);
        NodeRef document = helper.getStatemachineDocument(executionId);
            TransitionResponse transitionResponse = helper.executeUserAction(document, taskId, actionId, FinishStateWithTransitionAction.class, persistedResponse);
            //если небыло ошибок, то действие логируем
            if (transitionResponse.getErrors().size() == 0) {
                updateActionCount(document, actionId);
                String newWorkflowId = helper.parseExecutionId(persistedResponse);
                if (newWorkflowId != null) {
                    helper.logStartWorkflowEvent(document, newWorkflowId);
                }
                if (transitionResponse.getRedirect() != null) {
                    result.put("redirect", transitionResponse.getRedirect());
                }
            }
		} else if ("user".equals(actionType)){
            StateMachineHelper helper = new StateMachineHelper();
            String executionId = helper.parseExecutionId(persistedResponse);

            NodeRef document = helper.getStatemachineDocument(executionId);
            TransitionResponse transitionResponse = helper.executeUserAction(document, taskId, actionId, UserWorkflow.class, persistedResponse);
            //если небыло ошибок, то действие логируем
            if (transitionResponse.getErrors().size() == 0) {
                updateActionCount(document, actionId);
                String newWorkflowId = helper.parseExecutionId(persistedResponse);
                helper.logStartWorkflowEvent(document, newWorkflowId);
                if (transitionResponse.getRedirect() != null) {
                    result.put("redirect", transitionResponse.getRedirect());
                }
            }
        }

		return result;
	}

    private void updateActionCount(NodeRef document, String actionId) {
        NodeService nodeService = serviceRegistry.getNodeService();
        QName type = nodeService.getType(document);
        String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());

        NodeRef employee = orgstructureService.getCurrentEmployee();
        if (employee != null) {
            frequencyAnalysisService.updateFrequencyCount(employee, shortTypeName, actionId);
        }
    }

}
