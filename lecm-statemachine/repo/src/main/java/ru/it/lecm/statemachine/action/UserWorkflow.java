package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 28.11.12
 * Time: 9:09
 */
public class UserWorkflow extends StateMachineAction {

    private final static String PROP_ID = "id";
    private final static String PROP_WORKFLOW_ID = "workflowId";
    private final static String PROP_ASSIGNEE = "assignee";
    private final static String PROP_LABEL = "label";

    private ArrayList<UserWorkflowEntity> entities = new ArrayList<UserWorkflowEntity>();

	@Override
	public void execute(DelegateExecution execution) {
	}

	@Override
	public void init(Element actionElement, String processId) {
        List<Element> attributes = actionElement.elements("attribute");
        for (Element attribute : attributes) {
            String id = attribute.attribute(PROP_ID);
            String label = attribute.attribute(PROP_LABEL);
            String workflowId = attribute.attribute(PROP_WORKFLOW_ID);
            String assignee = attribute.attribute(PROP_ASSIGNEE);
			WorkflowVariables variables = new WorkflowVariables(attribute.element("workflowVariables"));
            entities.add(new UserWorkflowEntity(id, label, workflowId, assignee, variables));
        }
    }

    public List<UserWorkflowEntity> getUserWorkflows() {
        return  entities;
    }

    public class UserWorkflowEntity {

        private String id;
        private String label;
        private String workflowId;
        private String assignee;
        private WorkflowVariables variables;

        UserWorkflowEntity(String id, String label, String workflowId, String assignee, WorkflowVariables variables) {
            this.id = id;
			this.label = label;
            this.workflowId = workflowId;
            this.assignee = assignee;
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

        public String getAssignee() {
            return assignee;
        }

		public WorkflowVariables getVariables() {
			return variables;
		}
	}
}
