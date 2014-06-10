package ru.it.lecm.workflow.utils;

import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;

/**
 *
 * @author vmalygin
 */
public final class WorkflowVariablesHelper {

	private final static String DOCUMENT_REF = "documentRef";
	private final static String ROUTE_REF = "routeRef";
	private final static String WORKFLOW_DEFINITION = "workflowDefinition";
	private final static String RUN_AS = "runAs";
	private final static String RUN_AS_EMPLOYEE = "runAsEmployee";

	public static NodeRef getDocumentRef(final Map<String, Object> variables) {
		Object documentRef = variables.get(DOCUMENT_REF);
		ParameterCheck.mandatory(DOCUMENT_REF, documentRef);
		return new NodeRef(documentRef.toString());
	}

	public static NodeRef getRouteRef(final Map<String, Object> variables) {
		Object routeRef = variables.get(ROUTE_REF);
		ParameterCheck.mandatory(ROUTE_REF, routeRef);
		return new NodeRef(routeRef.toString());
	}

	public static String getWorkflowDefinition(final Map<String, Object> variables) {
		Object workflowDefinition = variables.get(WORKFLOW_DEFINITION);
		ParameterCheck.mandatory(WORKFLOW_DEFINITION, workflowDefinition);
		return workflowDefinition.toString();
	}

	public static String getRunAs(final Map<String, Object> variables) {
		Object runAs = variables.get(RUN_AS);
		return runAs != null ? runAs.toString() : null;
	}

	public static NodeRef getRunAsEmployee(final Map<String, Object> variables) {
		Object runAsEmployee = variables.get(RUN_AS_EMPLOYEE);
		return runAsEmployee != null ? new NodeRef(runAsEmployee.toString()) : null;
	}

	private WorkflowVariablesHelper() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of WorkflowVariablesHelper class.");
	}
}
