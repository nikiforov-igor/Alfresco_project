package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.statemachine.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 23.07.13
 * Time: 9:37
 */
public class ChooseStartPathAction extends StateMachineAction {

    private List<StartExpression> expressions = new ArrayList<StartExpression>();

    private static final String DIRECTION_VARIABLE = "lecm_start_direction";

    private static final transient Logger logger = LoggerFactory.getLogger(ChooseStartPathAction.class);


    @Override
    public void execute(DelegateExecution execution) {
        NodeRef nodeRef = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
        List<ChildAssociationRef> documents = getServiceRegistry().getNodeService().getChildAssocs(nodeRef);
        NodeRef document = null;
        if (documents.size() > 0) {
            document = documents.get(0).getChildRef();
        } else {
            return;
        }

        Expression expression = new Expression(document, getServiceRegistry(), getOrgstructureBean());
        execution.setVariable(DIRECTION_VARIABLE, "");
        for (StartExpression startExpression : expressions) {
            if (expression.execute(startExpression.getExpression())) {
                execution.setVariable(DIRECTION_VARIABLE, startExpression.getValue());
                break;
            }
        }
    }

    @Override
    public void init(Element actionElement, String processId) {
        List<Element> attributes = actionElement.elements("attribute");
        for (Element attribute : attributes) {
            String expression = attribute.attribute("expression");
            String value = attribute.attribute("value");
            StartExpression startExpression = new StartExpression(expression, value);
            expressions.add(startExpression);
        }
    }

    private class StartExpression {

        private String expression;
        private String value;

        private StartExpression(String expression, String value) {
            this.expression = expression;
            this.value = value;
        }

        private String getExpression() {
            return expression;
        }

        private String getValue() {
            return value;
        }

    }

}
