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
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.TransitionResponse;
import ru.it.lecm.statemachine.action.UserWorkflow;
import ru.it.lecm.statemachine.action.finishstate.FinishStateWithTransitionAction;

import java.util.HashMap;
import java.util.Map;

import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 16:18
 */
public class StartWorkflowScript extends DeclarativeWebScript {

	private static ServiceRegistry serviceRegistry;
    private static DocumentFrequencyAnalysisService frequencyAnalysisService;
    private static OrgstructureBean orgstructureService;
    private LifecycleStateMachineHelper stateMachineHelper;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        StartWorkflowScript.orgstructureService = orgstructureService;
    }

    public void setFrequencyAnalysisService(DocumentFrequencyAnalysisService frequencyAnalysisService) {
        StartWorkflowScript.frequencyAnalysisService = frequencyAnalysisService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StartWorkflowScript.serviceRegistry = serviceRegistry;
	}

    public void setStateMachineHelper(LifecycleStateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
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
            String executionId = stateMachineHelper.getCurrentExecutionId(taskId);
            NodeRef document = stateMachineHelper.getStatemachineDocument(executionId);
            TransitionResponse transitionResponse = stateMachineHelper.executeUserAction(document, taskId, actionId, FinishStateWithTransitionAction.class, persistedResponse);
            //если небыло ошибок, то действие логируем
            if (transitionResponse.getErrors().size() == 0) {
//				updateActionCount в своих недрах дёргает updateFrequencyCount,
//				который дёргает getOrCreateFrequencyUnit. Теперь метод разделён,
//				нужно гарантированно получить все необходимые папки.
//				Метод frequencyAnalysisService.getFrequencyUnit использует метод
//				getWorkDirectory, который ранее был getOrCreate, поэтому выполним
//				проверку и создадим папку при необходимости
				NodeService nodeService = serviceRegistry.getNodeService();
				QName type = nodeService.getType(document);
				String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());
				NodeRef employee = orgstructureService.getCurrentEmployee();

				if(frequencyAnalysisService.getWorkDirectory(employee) == null){
					try {
						frequencyAnalysisService.createWorkDirectory(employee, shortTypeName);
					} catch (WriteTransactionNeededException ex) {
						throw new RuntimeException("Can't create work directory", ex);
					}
				}

				if(frequencyAnalysisService.getFrequencyUnit(employee, shortTypeName, actionId) == null) {
					try {
						frequencyAnalysisService.createFrequencyUnit(employee, shortTypeName, actionId);
					} catch (WriteTransactionNeededException ex) {
						throw new RuntimeException("Can't create FrequencyUnit");
					}
				}
                updateActionCount(document, actionId);
                String newWorkflowId = parseExecutionId(persistedResponse);
                if (newWorkflowId != null) {
                    stateMachineHelper.logStartWorkflowEvent(document, newWorkflowId);
                }
                if (transitionResponse.getRedirect() != null) {
                    result.put("redirect", transitionResponse.getRedirect());
                }
            }
		} else if ("user".equals(actionType)){
            String executionId = parseExecutionId(persistedResponse);

            NodeRef document = stateMachineHelper.getStatemachineDocument(executionId);
            TransitionResponse transitionResponse = stateMachineHelper.executeUserAction(document, taskId, actionId, UserWorkflow.class, persistedResponse);
            //если небыло ошибок, то действие логируем
            if (transitionResponse.getErrors().size() == 0) {
                updateActionCount(document, actionId);
                String newWorkflowId = parseExecutionId(persistedResponse);
                stateMachineHelper.logStartWorkflowEvent(document, newWorkflowId);
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

    private String /*Используется только в StartWorkflowScript*/parseExecutionId(String persistedResponse) {
        if (persistedResponse == null || "null".equals(persistedResponse)) {
            return null;
        }

        int start = persistedResponse.indexOf("=") + 1;
        int end = persistedResponse.indexOf(",");

        try {
            return persistedResponse.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

}
