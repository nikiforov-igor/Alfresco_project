package ru.it.lecm.workflow.api;

import java.util.Date;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface WorkflowService {

	/**
	 * формирование нового листа согласования, для текущей версии регламента
	 *
	 * @return ссылка на новый лист согласования
	 */
	NodeRef createResultList(NodeRef bpmPackage, final String documentAttachmentCategoryName, final String approvalType, ActivitiScriptNodeList assigneesList);

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

	void notifyWorkflowStarted(final NodeRef employeeRef, final Date dueDate, final NodeRef bpmPackage);

	/**
	 * уведомить инициатора о том что решение о согласовании принято
	 *
	 * @param decision решение
	 * @param bpmPackage
	 */
	void notifyWorkflowFinished(final String decision, final NodeRef bpmPackage);

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

	List<NodeRef> createAssigneesList(NodeRef assigneesListNode, DelegateExecution execution);

	void deleteTempAssigneesList(DelegateExecution execution);

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
	NodeRef getWorkflowFolder();
	NodeRef getResultFolder();
	void assignTask(NodeRef assignee, DelegateTask task);
	NodeRef createResultItem(NodeRef approvalListRef, NodeRef employeeRef, String itemTitle, Date dueDate, QName resultItemType);
}
