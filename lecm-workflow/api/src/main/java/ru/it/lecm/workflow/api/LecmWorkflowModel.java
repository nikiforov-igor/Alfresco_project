package ru.it.lecm.workflow.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public final class LecmWorkflowModel {

	public final static String WORKFLOW_PREFIX = "lecm-workflow";
	public final static String WORKFLOW_NAMESPACE = "http://www.it.ru/logicECM/model/workflow/1.0";

	/**
	 * &lt;type name="lecm-workflow:workflow-assignees-list"&gt;
	 */
	public final static QName TYPE_WORKFLOW_ASSIGNEES_LIST = QName.createQName(WORKFLOW_NAMESPACE, "workflow-assignees-list");

	/**
	 * &lt;association name="lecm-workflow:workflow-assignees-list-owner-assoc"&gt;
	 */
	public final static QName ASSOC_WORKFLOW_ASSIGNEES_LIST_OWNER = QName.createQName(WORKFLOW_NAMESPACE, "workflow-assignees-list-owner-assoc");

	/**
	 * &lt;child-association name="lecm-workflow:workflow-assignees-list-contains-assignee"&gt;
	 */
	public final static QName ASSOC_WORKFLOW_ASSIGNEES_LIST_CONTAINS_ASSIGNEE = QName.createQName(WORKFLOW_NAMESPACE, "workflow-assignees-list-contains-assignee");

	/**
	 * &lt;type name="lecm-workflow:assignee"&gt;
	 */
	public final static QName TYPE_ASSIGNEE = QName.createQName(WORKFLOW_NAMESPACE, "assignee");

	/**
	 * &lt;property name="lecm-workflow:assignee-due-date"&gt;
	 */
	public final static QName PROP_ASSIGNEE_DUE_DATE = QName.createQName(WORKFLOW_NAMESPACE, "assignee-due-date");

	/**
	 * &lt;property name="lecm-workflow:userName"&gt;
	 */
	public final static QName PROP_ASSIGNEE_USERNAME = QName.createQName(WORKFLOW_NAMESPACE, "userName");

	/**
	 * &lt;association name="lecm-workflow:assignee-employee-assoc"&gt;
	 */
	public final static QName ASSOC_ASSIGNEE_EMPLOYEE = QName.createQName(WORKFLOW_NAMESPACE, "assignee-employee-assoc");

	/**
	 * &lt;association name="lecm-workflow:assignee-organization-element-member-assoc"&gt;
	 */
	public final static QName ASSOC_ASSIGNEE_ORG_ELEMENT_MEMBER = QName.createQName(WORKFLOW_NAMESPACE, "assignee-organization-element-member-assoc");

	/**
	 * &lt;aspect name="lecm-workflow:workflow-route-aspect"&gt;
	 */
	public final static QName ASPECT_WORKFLOW_ROUTE = QName.createQName(WORKFLOW_NAMESPACE, "workflow-route-aspect");

	/**
	 * &lt;aspect name="lecm-workflow:temp"&gt;
	 */
	public final static QName ASPECT_TEMP = QName.createQName(WORKFLOW_NAMESPACE, "temp");

	/**
	 * &lt;aspect name="lecm-workflow:workflow-type-aspect"&gt;
	 */
	public final static QName ASPECT_WORKFLOW_TYPE = QName.createQName(WORKFLOW_NAMESPACE, "workflow-type-aspect");

	/**
	 * &lt;property name="lecm-workflow:workflow-type"&gt;
	 */
	public final static QName PROP_WORKFLOW_TYPE = QName.createQName(WORKFLOW_NAMESPACE, "workflow-type");

	/**
	 * &lt;aspect name="lecm-workflow:assignee-order-aspect"&gt;
	 */
	public final static QName ASPECT_ASSIGNEE_ORDER = QName.createQName(WORKFLOW_NAMESPACE, "assignee-order-aspect");

	/**
	 * &lt;property name="lecm-workflow:assignee-order"&gt;
	 */
	public final static QName PROP_ASSIGNEE_ORDER = QName.createQName(WORKFLOW_NAMESPACE, "assignee-order");

	/**
	 * &lt;aspect name="lecm-workflow:assignee-days-to-complete-aspect"&gt;
	 */
	public final static QName ASPECT_ASSIGNEE_DAYS_TO_COMPLETE = QName.createQName(WORKFLOW_NAMESPACE, "assignee-days-to-complete-aspect");

	/**
	 * &lt;property name="lecm-workflow:assignee-days-to-complete"&gt;
	 */
	public final static QName PROP_ASSIGNEE_DAYS_TO_COMPLETE = QName.createQName(WORKFLOW_NAMESPACE, "assignee-days-to-complete");

	/**
	 * &lt;constraint name="lecm-workflow:workflow-concurrency-constraint" type="LIST"&gt;
	 * SEQUENTIAL - последовательный процесс
	 */
	public final static String CONCURRENCY_SEQ = "SEQUENTIAL";

	/**
	 * &lt;constraint name="lecm-workflow:workflow-concurrency-constraint" type="LIST"&gt;
	 * PARALLEL - параллельный процесс
	 */
	public final static String CONCURRENCY_PAR = "PARALLEL";

	/**
	 * &lt;type name="lecm-workflow:route"&gt;
	 */
	public final static QName TYPE_ROUTE = QName.createQName(WORKFLOW_NAMESPACE, "route");

	/**
	 * &lt;aspect name="lecm-workflow:workflow-route-aspect"&gt;
	 */
	public final static QName ASPECT_ROUTE = QName.createQName(WORKFLOW_NAMESPACE, "workflow-route-aspect");

	/**
	 * &lt;property name="lecm-workflow:is-register-after-signed"&gt;
	 */
	public final static QName PROP_IS_REGISTER_AFTER_SIGNED = QName.createQName(WORKFLOW_NAMESPACE, "is-register-after-signed");

	/**
	 * &lt;child-association name="lecm-workflow:route-contains-workflow-assignees-list"&gt;
	 */
	public final static QName ASSOC_ROUTE_CONTAINS_WORKFLOW_ASSIGNEES_LIST = QName.createQName(WORKFLOW_NAMESPACE, "route-contains-workflow-assignees-list");

	/**
	 * &lt;association name="lecm-workflow:assigneesListAssoc"&gt;
	 */
	public final static QName ASSOC_WORKFLOW_ASSIGNEES_LIST = QName.createQName(WORKFLOW_NAMESPACE, "assigneesListAssoc");

	/**
	 * &lt;aspect name="lecm-workflow:workflow-concurrency-aspect"&gt;
	 */
	public final static QName ASPECT_WORKFLOW_CONCURRENCY = QName.createQName(WORKFLOW_NAMESPACE, "workflow-concurrency-aspect");

	/**
	 * &lt;property name="lecm-workflow:workflowConcurrency"&gt;
	 */
	public final static QName PROP_WORKFLOW_CONCURRENCY = QName.createQName(WORKFLOW_NAMESPACE, "workflowConcurrency");

	private LecmWorkflowModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of LecmWorkflowModel class.");
	}
}
