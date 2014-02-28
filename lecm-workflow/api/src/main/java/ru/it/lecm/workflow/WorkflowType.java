package ru.it.lecm.workflow;


/**
 *
 * @author vmalygin
 */
public enum WorkflowType {

	APROVAL("lecmApprovalWorkflow"),
	SIGNING("lecmSign"),
	RESERVATION("regnumReservation"),
	REVIEW("lecmReview");

	private final String workflowDefinitionId;

	private WorkflowType(final String workflowDefinitionId) {
		this.workflowDefinitionId = workflowDefinitionId;
	}

	public static WorkflowType get(final String type) {
		for (WorkflowType workflowType : WorkflowType.values()) {
			if (workflowType.toString().equalsIgnoreCase(type)) {
				return workflowType;
			}
		}
		throw new IllegalArgumentException(String.format("'%s' type is invalid. Appropriate WorkflowType not found!", type));
	}

	public static WorkflowType getById(final String workflowDefinitionId) {
		for (WorkflowType workflowType : WorkflowType.values()) {
			if (workflowType.workflowDefinitionId.equals(workflowDefinitionId)) {
				return workflowType;
			}
		}
		throw new IllegalArgumentException(String.format("'%s' is invalid. Appropriate WorkflowType not found!", workflowDefinitionId));
	}
}
