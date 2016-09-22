package ru.it.lecm.statemachine.action.script;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger logger = LoggerFactory.getLogger(StartWorkflowScript.class);

	private static ServiceRegistry serviceRegistry;
    private static DocumentFrequencyAnalysisService frequencyAnalysisService;
    private static OrgstructureBean orgstructureService;
    private TransactionService transactionService;
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
    
    public void setTransactionService(TransactionService transactionService) {
    	this.transactionService = transactionService;
    }

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> result = new HashMap<String, Object>();
		final String taskId = req.getParameter("taskId");
		String persistedResponse = req.getParameter("formResponse");
		String actionId = req.getParameter("actionId");
		String actionType = req.getParameter("actionType");

		//Если есть actionId обрабатываем transitionAction
		if ("trans".equals(actionType)) {
			logger.debug("!!!!!!!!!! executeImpl 1");
//			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//			NodeRef document = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//                @Override
//                public NodeRef execute() throws Throwable {
                	logger.debug("!!!!!!!!!! executeImpl 2");
		            String executionId = stateMachineHelper.getCurrentExecutionId(taskId);
		            logger.debug("!!!!!!!!!! executeImpl 3");
		            NodeRef document = stateMachineHelper.getStatemachineDocument(executionId);
		            logger.debug("!!!!!!!!!! executeImpl 4");
//		            return document;
//                }
//            }, false, true);
            TransitionResponse transitionResponse = stateMachineHelper.executeUserAction(document, taskId, actionId, FinishStateWithTransitionAction.class, persistedResponse);
            logger.debug("!!!!!!!!!! executeImpl 5");
            //если небыло ошибок, то действие логируем
            if (transitionResponse.getErrors().size() == 0) {
//				updateActionCount в своих недрах дёргает updateFrequencyCount,
//				который дёргает getOrCreateFrequencyUnit. Теперь метод разделён,
//				нужно гарантированно получить все необходимые папки.
//				Метод frequencyAnalysisService.getFrequencyUnit использует метод
//				getWorkDirectory, который ранее был getOrCreate, поэтому выполним
//				проверку и создадим папку при необходимости
            	
//				NodeService nodeService = serviceRegistry.getNodeService();
//				QName type = nodeService.getType(document);
//				String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());
//				NodeRef employee = orgstructureService.getCurrentEmployee();
//
//				if(frequencyAnalysisService.getWorkDirectory(employee) == null){
//					try {
//						frequencyAnalysisService.createWorkDirectory(employee, shortTypeName);
//					} catch (WriteTransactionNeededException ex) {
//						throw new RuntimeException("Can't create work directory", ex);
//					}
//				}
//
//				if(frequencyAnalysisService.getFrequencyUnit(employee, shortTypeName, actionId) == null) {
//					try {
//						frequencyAnalysisService.createFrequencyUnit(employee, shortTypeName, actionId);
//					} catch (WriteTransactionNeededException ex) {
//						throw new RuntimeException("Can't create FrequencyUnit");
//					}
//				}
//                updateActionCount(document, actionId);
                String newWorkflowId = parseExecutionId(persistedResponse);
                if (newWorkflowId != null) {
                    stateMachineHelper.logStartWorkflowEvent(document, newWorkflowId);
                }
                if (transitionResponse.getRedirect() != null) {
                    result.put("redirect", transitionResponse.getRedirect());
                }
                logger.debug("!!!!!!!!!! executeImpl 6");
            }
		} else if ("user".equals(actionType)){
			logger.debug("!!!!!!!!!! executeImpl 7");
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
        } else if ("signal".equals(actionType)) {
        	logger.debug("!!!!!!!!!! executeImpl 8");
            String executionId = parseExecutionId(persistedResponse);
            stateMachineHelper.sendSignal(executionId);
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
