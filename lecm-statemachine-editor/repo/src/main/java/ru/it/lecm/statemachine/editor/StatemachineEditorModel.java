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
	public final static QName TYPE_USER_TRANSITION = QName.createQName(STATEMACHINE_EDITOR_URI,"userTransition");
	public final static QName TYPE_FORM_TRANSITION = QName.createQName(STATEMACHINE_EDITOR_URI,"transitionForm");
	public final static QName TYPE_FORMTRANS_TRANSITION = QName.createQName(STATEMACHINE_EDITOR_URI,"transitionFormTrans");
	public final static QName TYPE_USER_WORKFLOW_TRANSITION = QName.createQName(STATEMACHINE_EDITOR_URI,"UserWorkflowEntity");
	public final static QName TYPE_START_WORKFLOW_TRANSITION = QName.createQName(STATEMACHINE_EDITOR_URI,"StartWorkflowEntity");
	public final static QName TYPE_SCRIPT_ACTION_TRANSITION = QName.createQName(STATEMACHINE_EDITOR_URI,"ScriptActionEntity");
	public final static QName TYPE_OUTPUT_VARIABLE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputVariable");
	public final static QName TYPE_INPUT_VARIABLE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputVariable");
	public final static QName TYPE_INPUT_FORM_VARIABLE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputFormVariable");
	public final static QName TYPE_DYNAMIC_ROLE = QName.createQName(STATEMACHINE_EDITOR_URI, "dynamic-role");
	public final static QName TYPE_CONDITION_ACCESS = QName.createQName(STATEMACHINE_EDITOR_URI, "conditionAccess");
	public final static QName TYPE_VERSION_CONTAINER = QName.createQName(STATEMACHINE_EDITOR_URI, "versionContainer");
	public final static QName TYPE_VERSIONS = QName.createQName(STATEMACHINE_EDITOR_URI, "versions");

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
	public final static QName PROP_TRANSITION_FORM_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionFormType");
	public final static QName PROP_TRANSITION_FORM_FOLDER = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionFormFolder");
	public final static QName PROP_TRANSITION_FORM_CONNECTION = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionFormConnection");
	public final static QName PROP_TRANSITION_IS_SYSTEM_FORM_CONNECTION = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionIsSystemFormConnection");
	public final static QName PROP_DOCUMENT_AUTOFILL = QName.createQName(STATEMACHINE_EDITOR_URI, "document-autofill-enabled");
	public final static QName PROP_WORKFLOW_ID = QName.createQName(STATEMACHINE_EDITOR_URI, "workflowId");
	public final static QName PROP_ASSIGNEE = QName.createQName(STATEMACHINE_EDITOR_URI, "assignee");
	public final static QName PROP_INPUT_TO_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputToType");
	public final static QName PROP_INPUT_TO_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputToValue");
	public final static QName PROP_INPUT_FROM_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputFromType");
	public final static QName PROP_INPUT_FROM_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "inputFromValue");
    public final static QName PROP_FORM_INPUT_TO_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "formInputToValue");
    public final static QName PROP_FORM_INPUT_FROM_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "formInputFromType");
    public final static QName PROP_FORM_INPUT_FROM_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "formInputFromValue");
	public final static QName PROP_OUTPUT_TO_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputToType");
	public final static QName PROP_OUTPUT_TO_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputToValue");
	public final static QName PROP_OUTPUT_FROM_TYPE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputFromType");
	public final static QName PROP_OUTPUT_FROM_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "outputFromValue");
	public final static QName PROP_TRANSITION_EXPRESSION = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionExpression");
	public final static QName PROP_ACTION_SCRIPT = QName.createQName(STATEMACHINE_EDITOR_URI, "actionScript");
	public final static QName PROP_WORKFLOW_LABEL = QName.createQName(STATEMACHINE_EDITOR_URI, "workflowLabel");
	public final static QName PROP_ARCHIVE_FOLDER = QName.createQName(STATEMACHINE_EDITOR_URI, "archiveFolder");
	public final static QName PROP_ARCHIVE_FOLDER_ADDITIONAL = QName.createQName(STATEMACHINE_EDITOR_URI, "archiveFolderAdditional");
	public final static QName PROP_CREATION_DOCUMENT = QName.createQName(STATEMACHINE_EDITOR_URI, "isCreator");
	public final static QName PROP_PERMISSION_TYPE_VALUE = QName.createQName(STATEMACHINE_EDITOR_URI, "permissionTypeValue");
	public final static QName PROP_EDITABLE_FIELD = QName.createQName(STATEMACHINE_EDITOR_URI, "editableField");
    public final static QName PROP_CONDITION = QName.createQName(STATEMACHINE_EDITOR_URI, "condition");
    public final static QName PROP_CONDITION_ERROR_MESSAGE = QName.createQName(STATEMACHINE_EDITOR_URI, "conditionErrorMessage");
    public final static QName PROP_CONDITION_HIDE_ACTION = QName.createQName(STATEMACHINE_EDITOR_URI, "hideAction");
	public final static QName PROP_STATIC_ROLES_FOLDER = QName.createQName(STATEMACHINE_EDITOR_URI, "staticRolesList");
	public final static QName PROP_DYNAMIC_ROLES_FOLDER = QName.createQName(STATEMACHINE_EDITOR_URI, "dynamicRolesList");
	public final static QName PROP_LAST_VERSION = QName.createQName(STATEMACHINE_EDITOR_URI, "last_version");
	public final static QName PROP_SIMPLE_DOCUMENT_LAST_VERSION = QName.createQName(STATEMACHINE_EDITOR_URI, "simple-document-last-version");
	public final static QName PROP_VERSION = QName.createQName(STATEMACHINE_EDITOR_URI, "version");
	public final static QName PROP_VERSION_IS_SIMPLE_DOCUMENT = QName.createQName(STATEMACHINE_EDITOR_URI, "version-is-simple-document");
	public final static QName PROP_PUBLISH_DATE = QName.createQName(STATEMACHINE_EDITOR_URI, "publishDate");
	public final static QName PROP_PUBLISH_COMMENT = QName.createQName(STATEMACHINE_EDITOR_URI, "publishComment");
    public final static QName PROP_ALTERNATIVE_EXPRESSION = QName.createQName(STATEMACHINE_EDITOR_URI, "alternativeExpression");
    public final static QName PROP_ALTERNATIVES_FOLDER = QName.createQName(STATEMACHINE_EDITOR_URI, "alternativesFolder");
    public final static QName PROP_TRANSITION_SCRIPT = QName.createQName(STATEMACHINE_EDITOR_URI, "transition-script");
    public final static QName PROP_TRANSITION_DOCUMENT_CHANGE_SCRIPT = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionDocumentChangeScript");
    public final static QName PROP_SIMPLE_DOCUMENT = QName.createQName(STATEMACHINE_EDITOR_URI, "simple-document");
    public final static QName PROP_NOT_ARM_CREATED = QName.createQName(STATEMACHINE_EDITOR_URI, "notArmCreate");
    public final static QName PROP_STATIC_ROLE_PRIVILEGE = QName.createQName(STATEMACHINE_EDITOR_URI, "static-role-privilege");

	public final static QName ASSOC_TRANSITION_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionStatus");
    public final static QName ASSOC_ALTERNATIVE_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "alternativeStatus");
	public final static QName ASSOC_ROLE = QName.createQName(STATEMACHINE_EDITOR_URI, "role-assoc");

    public final static QName ASPECT_TRANSITION_STATUS = QName.createQName(STATEMACHINE_EDITOR_URI, "transitionStatusAspect");

    public static final String STATEMACHINES = "statemachines";
    public static final String FOLDER_VERSIONS = "versions";
    public static final String STATUSES = "statuses";
    public static final String ACTIONS = "actions";
    public static final String ROLES = "roles";
    public static final String ROLES_LIST = "roles-list";
    public static final String ALTERNATIVES = "alternatives";
    public static final String STATIC_ROLES = "static";
    public static final String DYNAMIC_ROLES = "dynamic";
    public static final String FIELDS = "fields";
    public static final String CATEGORIES = "categories";

    public final static String ACTION_FINISH_STATE_WITH_TRANSITION = "FinishStateWithTransition";
	public final static String ACTION_SCRIPT_ACTION = "ScriptAction";
	public final static String ACTION_START_WORKFLOW = "StartWorkflow";
	public final static String ACTION_WAIT_FOR_DOCUMENT_CHANGE = "WaitForDocumentChange";
	public final static String ACTION_USER_WORKFLOW = "UserWorkflow";
	public final static String ACTION_TRANSITION_ACTION = "TransitionAction";
	public final static String ACTION_TIMER_ACTION = "TimerAction";

}
