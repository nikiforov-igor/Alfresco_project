package ru.it.lecm.workflow.approval;

/**
 *
 * @author vlevin
 */
public enum DecisionResult {

	APPROVED,
	APPROVED_WITH_REMARK,
	REJECTED,
	APPROVED_FORCE,
	REJECTED_FORCE,
	NO_DECISION,
	REASSIGNED
}