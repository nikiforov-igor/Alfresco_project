package ru.it.lecm.statemachine.action.document;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.StatemachineActionConstants;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.action.PostponedAction;
import ru.it.lecm.statemachine.action.StateMachineAction;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 23.10.12
 * Time: 8:47
 */
public class WaitForDocumentChangeAction extends StateMachineAction implements PostponedAction {

	private List<Expression> expressions = new ArrayList<Expression>();

	@Override
	public void init(Element element, String processId) {
		Element expressions = element.element(TAG_EXPRESSIONS);

		String outputVariable = expressions.attribute(PROP_OUTPUT_VARIABLE);

		for (Element expressionElement : expressions.elements(TAG_EXPRESSION)) {
			String expression = expressionElement.attribute(PROP_EXPRESSION);
			String outputValue = expressionElement.attribute(PROP_OUTPUT_VALUE);
			boolean stopSubWorkflows = Boolean.parseBoolean(expressionElement.attribute(StatemachineActionConstants.PROP_STOP_SUBWORKFLOWS));
            String script = expressionElement.getText();
			this.expressions.add(new Expression(expression, outputVariable, outputValue, stopSubWorkflows, script));
		}
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	@Override
	public void execute(DelegateExecution execution) {
        for (Expression expression : expressions) {
            execution.setVariable(expression.getOutputVariable(), "");
        }
	}

    @Override
    public void postponedExecution(final String taskId, StateMachineHelper helper) {
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

            @Override
            public Object doWork() throws Exception {
                NodeService nodeService = getServiceRegistry().getNodeService();

                WorkflowService workflowService = getServiceRegistry().getWorkflowService();
                WorkflowTask task = workflowService.getTaskById(StateMachineHelper.ACTIVITI_PREFIX + taskId.replace(StateMachineHelper.ACTIVITI_PREFIX, ""));

                DictionaryService dictionaryService = getServiceRegistry().getDictionaryService();

                NodeRef wfPackage = (NodeRef) task.getProperties().get(WorkflowModel.ASSOC_PACKAGE);

                List<ChildAssociationRef> documents = nodeService.getChildAssocs(wfPackage);
                for (ChildAssociationRef item : documents) {
                    NodeRef itemRef = item.getChildRef();
                    QName itemType = nodeService.getType(itemRef);
                    if (dictionaryService.isSubClass(itemType, DocumentService.TYPE_BASE_DOCUMENT)) {
                        if (!nodeService.hasAspect(itemRef, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
                            nodeService.addAspect(itemRef, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK, null);
                        }
                        nodeService.setProperty(itemRef, StatemachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS, taskId);
                    }
                }

                return null;
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }

    public class Expression {

		private String expression = null;
		private String outputVariable = null;
		private String outputValue = null;
        private boolean stopSubWorkflows = false;
        private String script = null;

        public Expression(String expression, String outputVariable, String outputValue, boolean stopSubWorkflows, String script) {
			this.expression = expression;
			this.outputVariable = outputVariable;
			this.outputValue = outputValue;
            this.stopSubWorkflows = stopSubWorkflows;
            this.script = script;
        }

		public String getExpression() {
			return expression;
		}

		public String getOutputVariable() {
			return outputVariable;
		}

		public String getOutputValue() {
			return outputValue;
		}

        public boolean isStopSubWorkflows() {
            return stopSubWorkflows;
        }

        public String getScript() {
            return script;
        }
    }
}
