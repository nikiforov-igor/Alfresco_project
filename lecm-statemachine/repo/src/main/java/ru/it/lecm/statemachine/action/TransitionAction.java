package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.bpmn.model.BaseElement;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: PMelnikov
 * Date: 28.11.12
 * Time: 13:37
 */
public class TransitionAction extends StateMachineAction {

	private static final String PROP_VARIABLE_NAME = "variableName";
	private static final String PROP_EXPRESSION = "expression";
	private final static Logger logger = LoggerFactory.getLogger(TransitionAction.class);
	
	private List<TransitionActionEntity> entities = new ArrayList<TransitionActionEntity>();

	@Override
	public void execute(DelegateExecution execution) {
//		NodeRef document = getStateMachineHelper().getStatemachineDocument(execution.getId());
//		boolean result = getDocumentService().execExpression(document, expressionValue);
//		execution.setVariable(variableName, result);
	}

	@Override
	public void init(BaseElement actionElement, String processId) {
//		List<Element> attributes = actionElement.elements("attribute");
//		for (Element attribute : attributes) {
//			String name = attribute.attribute("name");
//			String value = attribute.attribute("value");
//			if (PROP_VARIABLE_NAME.equalsIgnoreCase(name)) {
//				variableName = value;
//			} else if (PROP_EXPRESSION.equalsIgnoreCase(name)) {
//				expressionValue = value;
//			} else if (StatemachineActionConstants.PROP_STOP_SUBWORKFLOWS.equalsIgnoreCase(name)) {
//                stopSubWorkflows = Boolean.parseBoolean(value);
//			}
//		}
	}
	
	public List<TransitionActionEntity> getTransitions() {
        return  entities;
    }
    
    public void addTransition(String variableName, String expressionValue, boolean stopSubWorkflows) {
    	entities.add(new TransitionActionEntity(variableName, expressionValue, stopSubWorkflows));
    }
    
    public class TransitionActionEntity{
    	private String variableName;
    	private String expressionValue;
    	private boolean stopSubWorkflows;
    	public TransitionActionEntity() {
    		
    	}
    	public TransitionActionEntity(String variableName, String expressionValue, boolean stopSubWorkflows) {
    		this.variableName = variableName;
    		this.expressionValue = expressionValue;
    		this.stopSubWorkflows = stopSubWorkflows;
    	}
    	public String getExpression() {
    		return expressionValue;
    	}

        public String getVariableName() {
            return variableName;
        }

        public boolean isStopSubWorkflows() {
            return stopSubWorkflows;
        }
    }
}
