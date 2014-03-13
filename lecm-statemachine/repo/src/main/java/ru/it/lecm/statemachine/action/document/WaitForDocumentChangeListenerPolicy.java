package ru.it.lecm.statemachine.action.document;

import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 05.10.12
 * Time: 11:30
 */
public class WaitForDocumentChangeListenerPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;
    private DocumentService documentService;

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                StatemachineModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (nodeService.hasAspect(nodeRef, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
            final String taskId = (String) nodeService.getProperty(nodeRef, StatemachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS);
            final StateMachineHelper helper = new StateMachineHelper();
            List<StateMachineAction> actions = helper.getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(WaitForDocumentChangeAction.class), ExecutionListener.EVENTNAME_START);

            WaitForDocumentChangeAction.Expression result = null;
            for (StateMachineAction action : actions) {
                WaitForDocumentChangeAction documentChangeAction = (WaitForDocumentChangeAction) action;
                List<WaitForDocumentChangeAction.Expression> expressions = documentChangeAction.getExpressions();
                for (WaitForDocumentChangeAction.Expression expression : expressions) {
                    if (documentService.execExpression(nodeRef, expression.getExpression())) {
                        result = expression;
                        break;
                    }
                }
                if (result != null) {
                    break;
                }
            }

            if (result != null) {
                if (result.getScript() != null && !"".equals(result.getScript())) {
                    final String script = result.getScript();
                    AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                        @Override
                        public Object doWork() throws Exception {
                            helper.executeScript(script, helper.getCurrentExecutionId(taskId));
                            return null;
                        }
                    });
                }
                if (result.getOutputValue() != null && !"".equals(result.getOutputValue())) {
                    HashMap<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put(result.getOutputVariable(), result.getOutputValue());
                    helper.setExecutionParamentersByTaskId(taskId, parameters);
                    if (result.isStopSubWorkflows()) {
                        String statemachineId = helper.getCurrentExecutionId(taskId);
                        helper.stopDocumentSubWorkflows(statemachineId);
                    }
                    helper.stopDocumentProcessing(taskId);
                }
            }
        }
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}