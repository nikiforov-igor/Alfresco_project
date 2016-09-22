package ru.it.lecm.statemachine;

public class WorkflowDescriptor {

	private String executionId;
	private String statemachineExecutionId;
	private String workflowId;
	private String startTaskId;
	private String actionName;
	private String actionId;
	private String eventName;

	public WorkflowDescriptor(String executionId, String statemachineExecutionId, String workflowId, String startTaskId, String actionName, String actionId, String eventName) {
        this.executionId = executionId;
        this.statemachineExecutionId = statemachineExecutionId;
        this.workflowId = workflowId;
		this.startTaskId = startTaskId;
		this.actionName = actionName;
		this.actionId = actionId;
		this.eventName = eventName;
	}

    public String getExecutionId() {
        return executionId;
    }

    public String getStatemachineExecutionId() {
		return statemachineExecutionId;
	}

    public String getWorkflowId() {
        return workflowId;
    }

    public String getStartTaskId() {
		return startTaskId;
	}

	public String getActionName() {
		return actionName;
	}

	public String getActionId() {
		return actionId;
	}

	public String getEventName() {
		return eventName;
	}
	
	@Override
    public String toString() {
		return "{executionId: "+executionId+", statemachineExecutionId: "+statemachineExecutionId+", workflowId: "+workflowId+", startTaskId: "+startTaskId+", actionName: "+actionName+", actionId: "+actionId+", eventName: "+eventName+"}";
	}
}