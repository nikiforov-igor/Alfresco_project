package ru.it.lecm.approval.api;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.Date;
import java.util.List;

/**
 *
 * @author vlevin
 */
public interface ApprovalListService {

	String APPROVAL_LIST_NAMESPACE = "http://www.it.ru/logicECM/model/approval-list/1.0";
	String APPROVAL_LIST_PREFIX = "lecm-al";
	QName TYPE_APPROVAL_ITEM = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item");
	QName TYPE_APPROVAL_LIST = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list");
	QName TYPE_ASSIGNEES_ITEM = QName.createQName(APPROVAL_LIST_NAMESPACE, "assignees-item");
	QName TYPE_ASSIGNEES_LIST = QName.createQName(APPROVAL_LIST_NAMESPACE, "assignees-list");
	QName PROP_APPROVAL_ITEM_DECISION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-decision");
	QName PROP_APPROVAL_ITEM_START_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-start-date");
	QName PROP_APPROVAL_ITEM_DUE_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-due-date");
	QName PROP_APPROVAL_ITEM_APPROVE_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-approve-date");
	QName PROP_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-comment");
	QName PROP_APPROVAL_LIST_DECISION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-decision");
	QName PROP_APPROVAL_LIST_APPROVE_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-approve-date");
	QName PROP_APPROVAL_LIST_APPROVE_START = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-approve-start");
	QName PROP_APPROVAL_LIST_DOCUMENT_VERSION = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-document-version");
	QName PROP_ASSIGNEES_ITEM_ORDER = QName.createQName(APPROVAL_LIST_NAMESPACE, "assignees-item-order");
	QName PROP_ASSIGNEES_ITEM_DUE_DATE = QName.createQName(APPROVAL_LIST_NAMESPACE, "assignees-item-due-date");
	QName PROP_ASSIGNEES_ITEM_USERNAME = QName.createQName(APPROVAL_LIST_NAMESPACE, "userName");
	QName ASSOC_APPROVAL_ITEM_EMPLOYEE = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-employee-assoc");
	QName ASSOC_APPROVAL_ITEM_COMMENT = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-item-comment-assoc");
	QName ASSOC_APPROVAL_LIST_CONTAINS_APPROVAL_ITEM = QName.createQName(APPROVAL_LIST_NAMESPACE, "approval-list-contains-approval-item");
	QName ASSOC_ASSIGNEES_LIST_CONTAINS_ASSIGNEES_ITEM = QName.createQName(APPROVAL_LIST_NAMESPACE, "assignees-list-contains-assignees-item");
	QName ASSOC_ASSIGNEES_ITEM_EMPLOYEE_ASSOC = QName.createQName(APPROVAL_LIST_NAMESPACE, "assignees-item-employee-assoc");
	String ASSIGNEES_LISTS_FOLDER_NAME = "Списки согласования";
	String ASSIGNEES_DEFAULT_LIST_FOLDER_NAME = "Без списка (список не выбран)";
	String ASSIGNEES_LISTS_PARALLEL_FOLDER_NAME = "Параллельное согласование";
	String ASSIGNEES_LISTS_SEQUENTIAL_FOLDER_NAME = "Последовательное согласование";
	String CUSTOM_APPROVAL_FOLDER_NAME = "Специальное согласование";
	String PARLLEL_APPROVAL_FOLDER_NAME = "Параллельное согласование";
	String SEQUENTIAL_APPROVAL_FOLDER_NAME = "Последовательное согласование";
	String APPROVAL_TYPE_SEQUENTIAL = "SEQUENTIAL";
	String APPROVAL_TYPE_PARALLEL = "PARALLEL";
	String APPROVAL_TYPE_CUSTOM = "CUSTOM";
	String ASSEGNEE_ITEM_FORMAT = "Согласующий %s";

	/**
	 * формирование нового листа согласования, для текущей версии регламента
	 *
	 * @return ссылка на новый лист согласования
	 */
	NodeRef createApprovalList(NodeRef bpmPackage, final String documentAttachmentCategoryName, final String approvalType, ActivitiScriptNodeList assigneesList);

	void logFinalDecision(final NodeRef approvalListRef, final String finalDecision);

	/**
	 * раздать всем участникам процесса согласования права
	 * LECM_BASIC_PG_Reviewer
	 *
	 * @param employeeRef
	 * @param bpmPackage
	 */
	void grantReviewerPermissions(final NodeRef employeeRef, final NodeRef bpmPackage);

	/**
	 * раздать всем участникам процесса согласования права
	 * LECM_BASIC_PG_Reviewer
	 *
	 * @param employeeRef
	 * @param documentRef
	 */
	void grantReviewerPermissionsInternal(final NodeRef employeeRef, final NodeRef documentRef);

	void notifyApprovalStarted(final NodeRef employeeRef, final Date dueDate, final NodeRef bpmPackage);

	/**
	 * уведомить инициатора о том что решение о согласовании принято
	 *
	 * @param decision решение
	 * @param bpmPackage
	 */
	void notifyFinalDecision(final String decision, final NodeRef bpmPackage);

	/**
	 * уведомить согласующих о том что подходит срок согласования
	 * или о том что согласование просрочено
	 *
	 * @param processInstanceId
	 */
	void notifyAssigneesDeadline(final String processInstanceId, final NodeRef bpmPackage);

	/**
	 * уведомить исполнителя договора о том что подходит срок согласования
	 * или о том что согласование просрочено
	 *
	 * @param processInstanceId
	 */
	void notifyInitiatorDeadline(final String processInstanceId, final NodeRef bpmPackage, final VariableScope variableScope);

	NodeRef getEmployeeForAssignee(final NodeRef assigneeRef);

	NodeRef getApprovalFolder();

	void assignTask(NodeRef assignee, DelegateTask task);

	void completeTask(NodeRef assignee, DelegateTask task);

	List<NodeRef> createAssigneesList(NodeRef assigneesListNode, DelegateExecution execution);

	void deleteTempAssigneesList(DelegateExecution execution);

	void completeTask(NodeRef assignee, DelegateTask task, String decision, NodeRef commentRef, Date dueDate);

	/**
	 * Отобрать права LECM_BASIC_PG_Reviewer после завершения задачи
	 *
	 * @param employeeRef
	 * @param bpmPackage
	 */
	void revokeReviewerPermissions(final NodeRef employeeRef, final NodeRef bpmPackage);
	/**
	 * раздать всем участникам процесса согласования права
	 * LECM_BASIC_PG_Reader
	 *
	 * @param employeeRef
	 * @param bpmPackage
	 */
	void grantReaderPermissions(final NodeRef employeeRef, final NodeRef bpmPackage);

	/**
	 * актуализация исполнителей бизнес процесса с использованием делегирования
	 * для каждого участника бизнес процесса проверяется наличие активного делегирования
	 * если делегирование активно, то вычисляется актуальный исполнитель задачи бизнес процесса
	 * если определена бизнес роль workflowRole, то исполнитель определяется с ее помощью
	 * в противном случае актуальный исполнитель вычисляется по отвественному лицу согласно настройкам делегирования
	 * @param assigneesList список исполнителей бизнес процесса
	 * @param workflowRole идентификатор бизнес роли по которой получаем актуальных исполнителей
	 * @return  список исполнителей у которого сотрудники и имена указывают на исполнителей с учетом делегирования
	 */
	List<NodeRef> actualizeAssigneesUsingDelegation(final List<NodeRef> assigneesList, final String workflowRole);
}
