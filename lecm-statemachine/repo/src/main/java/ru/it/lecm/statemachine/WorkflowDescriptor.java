package ru.it.lecm.statemachine;

public class WorkflowDescriptor {

	private String statemachineExecutionId;
	private String startTaskId;
	private String actionName;
	private String actionId;
	private String eventName;

	public WorkflowDescriptor(String statemachineExecutionId, String startTaskId, String actionName, String actionId, String eventName) {
		this.statemachineExecutionId = statemachineExecutionId;
		this.startTaskId = startTaskId;
		this.actionName = actionName;
		this.actionId = actionId;
		this.eventName = eventName;
	}

	public String getStatemachineExecutionId() {
		return statemachineExecutionId;
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
}