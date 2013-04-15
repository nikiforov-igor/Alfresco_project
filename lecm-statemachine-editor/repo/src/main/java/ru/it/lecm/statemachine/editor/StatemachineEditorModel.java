package ru.it.lecm.statemachine.editor;

import org.alfresco.service.namespace.QName;

/**
 * User: PMelnikov
 * Date: 21.02.13
 * Time: 10:24
 */
public class StatemachineEditorModel {


	public final static String STATEMACHINE_EDITOR_URI = "http://www.it.ru/logicECM/statemachine/editor/1.0";

    public final static QName TYPE_STATEMACHINE = QName.createQName(STATEMACHINE_EDITOR_URI, "statemachine");

    public final static QName TYPE_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "status");
    public final static QName TYPE_TASK_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "taskStatus");
    public final static QName TYPE_STATUSES = QName.createQName(STATEMACHINE_EDITOR_URI, "statuses");
	public final static QName TYPE_ROLES = QName.createQName(STATEMACHINE_EDITOR_URI, "roles");
	public final static QName TYPE_ACTIONS = QName.createQName(STATEMACHINE_EDITOR_URI, "actions");
	public final static QName TYPE_END_EVENT = QName.createQName(STATEMACHINE_EDITOR_URI, "endEvent");
	public final static QName TYPE_WORKFLOW_TRANSITION = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionWorkflow");
	public final static QName TYPE_OUTPUT_VARIABLE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputVariable");
	public final static QName TYPE_INPUT_VARIABLE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputVariable");
	public final static QName TYPE_DYNAMIC_ROLE = QName.createQName(STATEMACHINE_EDITOR_URI, "dynamic-role");
	public final static QName TYPE_CONDITION_ACCESS = QName.createQName(STATEMACHINE_EDITOR_URI, "conditionAccess");

	public final static QName PROP_ACTION_ID = QName.createQName(STATEMACHINE_EDITOR_URI, "actionId");
	public final static QName PROP_ACTION_EXECUTION = QName.createQName(STATEMACHINE_EDITOR_URI, "actionExecution");
	public final static QName PROP_STATUS_UUID = QName.createQName(STATEMACHINE_EDITOR_URI, "statusUUID");
	public final static QName PROP_STATIC_ROLES = QName.createQName(STATEMACHINE_EDITOR_URI, "staticRoles");
	public final static QName PROP_DYNAMIC_ROLES = QName.createQName(STATEMACHINE_EDITOR_URI, "dynamicRoles");
	public final static QName PROP_START_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "startStatus");
	public final static QName PROP_FOR_DRAFT = QName.createQName(STATEMACHINE_EDITOR_URI, "forDraft");
	public final static QName PROP_TIMER_DURATION = QName.createQName(STATEMACHINE_EDITOR_URI, "timerDuration");
	public final static QName PROP_STOP_SUB_WORKFLOWS = QName.createQName(STATEMACHINE_EDITOR_URI, "stopSubWorkflows");
	public final static QName PROP_TRANSITION_LABEL = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionLabel");
	public final static QName PROP_WORKFLOW_ID = QName.createQName(STATEMACHINE_EDITOR_URI, "workflowId");
	public final static QName PROP_ASSIGNEE = QName.createQName(STATEMACHINE_EDITOR_URI, "assignee");
	public final static QName PROP_INPUT_TO_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputToType");
	public final static QName PROP_INPUT_TO_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputToValue");
	public final static QName PROP_INPUT_FROM_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputFromType");
	public final static QName PROP_INPUT_FROM_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputFromValue");
	public final static QName PROP_OUTPUT_TO_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputToType");
	public final static QName PROP_OUTPUT_TO_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputToValue");
	public final static QName PROP_OUTPUT_FROM_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputFromType");
	public final static QName PROP_OUTPUT_FROM_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputFromValue");
	public final static QName PROP_TRANSITION_EXPRESSION = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionExpression");
	public final static QName PROP_ACTION_SCRIPT = QName.createQName(STATEMACHINE_EDITOR_URI, "actionScript");
	public final static QName PROP_WORKFLOW_LABEL = QName.createQName(STATEMACHINE_EDITOR_URI, "workflowLabel");
	public final static QName PROP_ARCHIVE_FOLDER = QName.createQName(STATEMACHINE_EDITOR_URI, "archiveFolder");
	public final static QName PROP_CREATION_DOCUMENT = QName.createQName(STATEMACHINE_EDITOR_URI, "creationDocument");
	public final static QName PROP_PERMISSION_TYPE_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "permissionTypeValue");
	public final static QName PROP_EDITABLE_FIELD = QName.createQName(STATEMACHINE_EDITOR_URI, "editableField");
    public final static QName PROP_CONDITION = QName.createQName(STATEMACHINE_EDITOR_URI, "condition");
    public final static QName PROP_CONDITION_ERROR_MESSAGE = QName.createQName(STATEMACHINE_EDITOR_URI, "conditionErrorMessage");
	public final static QName PROP_ROLES_FOLDER = QName.createQName(STATEMACHINE_EDITOR_URI, "rolesFolder");

	public final static QName ASSOC_TRANSITION_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionStatus");
	public final static QName ASSOC_ROLE = QName.createQName(STATEMACHINE_EDITOR_URI, "role-assoc");

    public final static QName ASPECT_TRANSITION_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionStatusAspect");

    public static final String STATEMACHINES = "statemachines";
    public static final String STATUSES = "statuses";
    public static final String ROLES = "roles";

    public final static String ACTION_FINISH_STATE_WITH_TRANSITION = "FinishStateWithTransition";
	public final static String ACTION_SCRIPT_ACTION = "ScriptAction";
	public final static String ACTION_START_WORKFLOW = "StartWorkflow";
	public final static String ACTION_WAIT_FOR_DOCUMENT_CHANGE = "WaitForDocumentChange";
	public final static String ACTION_USER_WORKFLOW = "UserWorkflow";
	public final static String ACTION_TRANSITION_ACTION = "TransitionAction";
	public final static String ACTION_TIMER_ACTION = "TimerAction";

}
