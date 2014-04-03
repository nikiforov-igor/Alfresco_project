package ru.it.lecm.workflow.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class WorkflowResultModel {

	public final static String WORKFLOW_RESULT_PREFIX = "lecm-workflow-result";
	public final static String WORKFLOW_RESULT_NAMESPACE = "http://www.it.ru/logicECM/model/workflow-result/1.0";

	/**
	 * &lt;type name="lecm-workflow-result:workflow-result-list"&gt;
	 */
	public final static QName TYPE_WORKFLOW_RESULT_LIST = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-list");

	/**
	 * &lt;property name="lecm-workflow-result:workflow-result-list-complete-date"&gt;
	 */
	public final static QName PROP_WORKFLOW_RESULT_LIST_COMPLETE_DATE = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-list-complete-date");

	/**
	 * &lt;property name="lecm-workflow-result:workflow-result-list-start-date"&gt;
	 */
	public final static QName PROP_WORKFLOW_RESULT_LIST_START_DATE = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-list-start-date");

	/**
	 * &lt;property name="lecm-workflow-result:workflow-result-list-document-version"&gt;
	 */
	public final static QName PROP_WORKFLOW_RESULT_LIST_DOCUMENT_VERSION = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-list-document-version");

	/**
	 * &lt;child-association name="lecm-workflow-result:workflow-result-list-workflow-result-item-assoc"&gt;
	 */
	public final static QName ASSOC_WORKFLOW_RESULT_LIST_RESULT_ITEM = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-list-workflow-result-item-assoc");

	/**
	 * &lt;type name="lecm-workflow-result:workflow-result-item"&gt;
	 */
	public final static QName TYPE_WORKFLOW_RESULT_ITEM = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-item");

	/**
	 * &lt;property name="lecm-workflow-result:workflow-result-item-start-date"&gt;
	 */
	public final static QName PROP_WORKFLOW_RESULT_ITEM_START_DATE = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-item-start-date");

	/**
	 * &lt;property name="lecm-workflow-result:workflow-result-item-due-date"&gt;
	 */
	public final static QName PROP_WORKFLOW_RESULT_ITEM_DUE_DATE = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-item-due-date");

	/**
	 * &lt;property name="lecm-workflow-result:workflow-result-item-finish-date"&gt;
	 */
	public final static QName PROP_WORKFLOW_RESULT_ITEM_FINISH_DATE = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-item-finish-date");

	/**
	 * &lt;association name="lecm-workflow-result:workflow-result-item-employee-assoc"&gt;
	 */
	public final static QName ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-item-employee-assoc");

	/**
	 * &lt;association name="lecm-workflow-result:workflow-result-list-initiator-assoc"&gt;
	 *
	 */
	public final static QName ASSOCWORKFLOW_RESULT_LIST_INITIATOR = QName.createQName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-list-initiator-assoc");

	/**
	 * &lt;property name="lecm-workflow-result:workflow-result-item-task-id"&gt;
	 *
	 */
	public final static QName PROP_WORKFLOW_RESULT_ITEM_TASK_ID = QName.createQNameWithValidLocalName(WORKFLOW_RESULT_NAMESPACE, "workflow-result-item-task-id");

	private WorkflowResultModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of WorkflowResultModel class.");
	}

}
