package ru.it.lecm.base.statemachine.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import ru.it.lecm.base.statemachine.StateMachineHelper;
import ru.it.lecm.base.statemachine.StateMachineModel;
import ru.it.lecm.base.statemachine.action.StartDocumentProcessingAction;
import ru.it.lecm.base.statemachine.action.StateMachineAction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 05.10.12
 * Time: 11:30
 */
public class StateMachineDocumentListenerPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                StateMachineModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (nodeService.hasAspect(nodeRef, StateMachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext();
            for (QName key : after.keySet()) {
                List<String> prefixes = (List<String>) serviceRegistry.getNamespaceService().getPrefixes(key.getNamespaceURI());
                String textKey = (prefixes.get(0) + "_" + key.getLocalName()).replace("-", "_");
                context.setVariable(textKey, after.get(key));
            }
            String taskId = (String) nodeService.getProperty(nodeRef, StateMachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS);
            StateMachineHelper helper = new StateMachineHelper();
            List<StateMachineAction> actions = helper.getTaskActionsByName(taskId, "StartDocumentProcessing", "start");
            Boolean result = Boolean.TRUE;
            for (StateMachineAction action : actions) {
                StartDocumentProcessingAction documentProcessingAction = (StartDocumentProcessingAction) action;
                String expression = documentProcessingAction.getExpression();
                result = result && parser.parseExpression(expression).getValue(context, Boolean.class);
            }

            if (result) {
                new StateMachineHelper().stopDocumentProcessing((String) nodeService.getProperty(nodeRef, StateMachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS));
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
}