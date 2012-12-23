package ru.it.lecm.base.statemachine.action;

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
            String label = attribute.attribute(PROP_LABEL);
            String workflowId = attribute.attribute(PROP_WORKFLOW_ID);
            String assignee = attribute.attribute(PROP_ASSIGNEE);
            entities.add(new UserWorkflowEntity(label, workflowId, assignee));
        }
    }

    public List<UserWorkflowEntity> getUserWorkflows() {
        return  entities;
    }

    public class UserWorkflowEntity {

        private String label;
        private String workflowId;
        private String assignee;

        UserWorkflowEntity(String label, String workflowId, String assignee) {
            this.label = label;
            this.workflowId = workflowId;
            this.assignee = assignee;
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
    }
}
