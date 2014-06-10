package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
//import org.activiti.engine.impl.util.xml.Element;
import org.activiti.bpmn.model.BaseElement;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: PMelnikov
 * Date: 28.11.12
 * Time: 9:09
 */
public class UserWorkflow extends StateMachineAction {

    private final static String PROP_ID = "id";
    private final static String PROP_WORKFLOW_ID = "workflowId";
    private final static String PROP_LABEL = "label";
    private final static String PROP_CONDITION_ACCESS = "conditionAccess";

    private List<UserWorkflowEntity> entities = new ArrayList<UserWorkflowEntity>();
    
    private final static Logger logger = LoggerFactory.getLogger(UserWorkflow.class);

	@Override
	public void execute(DelegateExecution execution) {
	}

	@Override
	public void init(BaseElement actionElement, String processId) {
//        List<Element> attributes = actionElement.elements("attribute");
//        for (Element attribute : attributes) {
//            String id = attribute.attribute(PROP_ID);
//            String label = attribute.attribute(PROP_LABEL);
//            String workflowId = attribute.attribute(PROP_WORKFLOW_ID);
//            String conditionAccess = attribute.attribute(PROP_CONDITION_ACCESS);
//			if (conditionAccess == null) {
//				conditionAccess = "";
//			}
//            Conditions conditions = new Conditions(attribute.element("conditions"));
//			WorkflowVariables variables = new WorkflowVariables(attribute.element("workflowVariables"));
//            entities.add(new UserWorkflowEntity(id, label, workflowId, conditions, variables));
//        }
    }

    public List<UserWorkflowEntity> getUserWorkflows() {
        return  entities;
    }
    
    public void addUserWorkflow(String id, String label, String workflowId, Conditions conditionAccess, WorkflowVariables variables) {
    	entities.add(new UserWorkflowEntity(id, label, workflowId, conditionAccess, variables));
    }

    public class UserWorkflowEntity {

        private String id;
        private String label;
        private String workflowId;
        private Conditions conditionAccess;
        private WorkflowVariables variables;

        UserWorkflowEntity(String id, String label, String workflowId, Conditions conditionAccess, WorkflowVariables variables) {
            this.id = id;
			this.label = label;
            this.workflowId = workflowId;
            this.conditionAccess = conditionAccess;
			this.variables = variables;
        }

		public String getId() {
			return id;
		}

		public String getLabel() {
            return label;
        }

        public String getWorkflowId() {
            return workflowId;
        }

		public Conditions getConditionAccess() {
			return conditionAccess;
		}

		public WorkflowVariables getVariables() {
			return variables;
		}
	}
}
