package ru.it.lecm.workflow.approval.deprecated;

/**
 *
 * @author vlevin
 */
@Deprecated
public enum DecisionResult {

	APPROVED,
	APPROVED_WITH_REMARK,
	REJECTED,
	APPROVED_FORCE,
	REJECTED_FORCE,
	NO_DECISION,
	REASSIGNED
}