package ru.it.lecm.workflow.api;

import java.util.Date;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.workflow.WorkflowTaskDecision;

/**
 *
 * @author vlevin
 */
public interface LecmWorkflowService {

	void assignTask(final NodeRef assignee, final DelegateTask task);

	WorkflowTaskDecision completeTask(NodeRef assignee, DelegateTask task);

	/**
	 * раздать всем участникам процесса согласования права
	 * LECM_BASIC_PG_Reviewer
	 *
	 * @param employeeRef сотрудник
	 * @param bpmPackage bpmPackage с документом
	 * @param addEmployeeAsMember добавлять ли сотрудника в участники документа
	 */
	void grantReviewerPermissions(final NodeRef employeeRef, final NodeRef bpmPackage, final boolean addEmployeeAsMember);

	/**
	 * раздать всем участникам процесса согласования права
	 * LECM_BASIC_PG_Reviewer
	 *
	 * @param employeeRef сотрудник
	 * @param documentRef документ
	 * @param addEmployeeAsMember добавлять ли сотрудника в участники документа
	 */
	void grantReviewerPermissionsInternal(final NodeRef employeeRef, final NodeRef documentRef, final boolean addEmployeeAsMember);

	/**
	 * раздать всем участникам процесса согласования права
	 * LECM_BASIC_PG_Reader
	 *
	 * @param employeeRef сотрудник
	 * @param bpmPackage bpmPackage с документом
	 * @param addEmployeeAsMember добавлять ли сотрудника в участники документа
	 */
	void grantReaderPermissions(final NodeRef employeeRef, final NodeRef bpmPackage, final boolean addEmployeeAsMember);

	/**
	 * Отобрать права LECM_BASIC_PG_Reviewer после завершения задачи
	 *
	 * @param employeeRef сотрудник
	 * @param bpmPackage bpmPackage с документом
	 */
	void revokeReviewerPermissions(final NodeRef employeeRef, final NodeRef bpmPackage);

	void notifyWorkflowStarted(final NodeRef employeeRef, final Date dueDate, final NodeRef bpmPackage);

	/**
	 * уведомить инициатора о том что решение о согласовании принято
	 *
	 * @param decision решение
	 * @param bpmPackage
	 */
	void notifyWorkflowFinished(final String decision, final NodeRef bpmPackage);

	void notifyWorkflowFinished(final NodeRef employeeRef, final String decision, final NodeRef bpmPackage);

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

}
