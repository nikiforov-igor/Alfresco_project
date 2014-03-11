package ru.it.lecm.workflow.approval.extensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.workflow.approval.Utils;
import ru.it.lecm.workflow.approval.api.ApprovalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.LecmWorkflowModel;
import ru.it.lecm.workflow.api.RouteAspecsModel;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;
import ru.it.lecm.workflow.approval.DecisionResult;

public class ApprovalJavascriptExtension extends BaseWebScript {

	/**
	 * переменная регламента в которой хранится идентификатор
	 * бизнес роли, через которую будут находится исполнители регламента с учетом делегирования
	 */
	private final static String WORKFLOW_ROLE = "workflowRole";

	private final static Logger logger = LoggerFactory.getLogger(ApprovalJavascriptExtension.class);
	private NodeService nodeService;
	private OrgstructureBean orgstructureService;
	private ApprovalService approvalService;
	private StateMachineServiceBean stateMachineHelper;
	private WorkflowAssigneesListService workflowAssigneesListService;

	public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
		this.stateMachineHelper = stateMachineHelper;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setApprovalService(ApprovalService approvalListService) {
		this.approvalService = approvalListService;
	}

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	public ActivitiScriptNode createApprovalList(ActivitiScriptNode bpmPackage, String documentAttachmentCategoryName, String approvalType) {
		return createApprovalList(bpmPackage, documentAttachmentCategoryName, approvalType, new ActivitiScriptNodeList());
	}

	/**
	 * формирование нового листа согласования, для текущей версии регламента
	 *
	 * @param assigneesList список сотрудников, ака согласущие лица
	 * @param bpmPackage ссылка на Workflow Package Folder, хранилище всех
	 * item-ов workflow
	 * @param approvalType тип согласования:
	 * @param documentAttachmentCategoryName название категории вложений, в
	 * которой хранится файл документа
	 * @return ссылку на новый лист согласования
	 */
	public ActivitiScriptNode createApprovalList(final ActivitiScriptNode bpmPackage, final String documentAttachmentCategoryName, final String approvalType, ActivitiScriptNodeList assigneesList) {
		NodeRef approvalListRef = approvalService.createApprovalList(bpmPackage.getNodeRef(), documentAttachmentCategoryName, approvalType, assigneesList.getNodeReferences());
		return new ActivitiScriptNode(approvalListRef, serviceRegistry);
	}

	public String getFinalDecision(final Map<String, String> decisionMap) {
		DecisionResult finalDecision = DecisionResult.NO_DECISION;

		if (decisionMap.containsValue(DecisionResult.REJECTED.name())) {
			finalDecision = DecisionResult.REJECTED;
		} else if (decisionMap.containsValue(DecisionResult.APPROVED_WITH_REMARK.name())) {
			finalDecision = DecisionResult.APPROVED_WITH_REMARK;
		} else if (decisionMap.containsValue(DecisionResult.APPROVED.name())) {
			finalDecision = DecisionResult.APPROVED;
		}
		return finalDecision.name();
	}

	public boolean isApproved(final String finalDecision) {
		return DecisionResult.APPROVED_WITH_REMARK.name().equals(finalDecision) ||
				DecisionResult.APPROVED.name().equals(finalDecision) ||
				DecisionResult.APPROVED_FORCE.name().equals(finalDecision);
	}

	public void logFinalDecision(final ActivitiScriptNode approvalListRef, final String finalDecision) {
		approvalService.logFinalDecision(approvalListRef.getNodeRef(), finalDecision);
	}

	/**
	 * прислать сотруднику уведомление о том, что начато согласование по
	 * документу
	 *
	 * @param assigneeRef cm:person согласующий по документу
	 * @param dueDate индивидуальный срок согласования
	 * @param bpmPackage ссылка на Workflow Package Folder, хранилище всех
	 * item-ов workflow
	 */
	public void notifyApprovalStarted(final ActivitiScriptNode assigneeRef, final Date dueDate, final ActivitiScriptNode bpmPackage) {
		NodeRef candidate = assigneeRef.getNodeRef();
		QName candidateType = nodeService.getType(candidate);
		if (ContentModel.TYPE_PERSON.isMatch(candidateType)) {
			NodeRef employeeRef = orgstructureService.getEmployeeByPerson(candidate);
			approvalService.notifyWorkflowStarted(employeeRef, dueDate, bpmPackage.getNodeRef());
		} else if (LecmWorkflowModel.TYPE_ASSIGNEE.isMatch(candidateType)) {
			NodeRef employeeRef = approvalService.getEmployeeForAssignee(candidate);
			approvalService.notifyWorkflowStarted(employeeRef, dueDate, bpmPackage.getNodeRef());
		}
	}

	public void notifyFinalDecision(final String decision, final ActivitiScriptNode bpmPackage) {
		approvalService.notifyWorkflowFinished(decision, bpmPackage.getNodeRef());
	}

	public void terminateApproval(ActivitiScriptNode bpmPackage, String variable, Object value) {
		NodeRef document = Utils.getDocumentFromBpmPackage(bpmPackage.getNodeRef());
		ArrayList<String> definitions = new ArrayList<String>();
		definitions.add("lecmApprovalWorkflow");
		stateMachineHelper.terminateWorkflowsByDefinitionId(document, definitions, variable, value);
	}

	/**
	 * если до наступления срока согласования остались сутки или меньше,
	 * то уведомить исполнителей и инициатора согласования о том,
	 * что необходимо принять решение
	 *
	 * @param processInstanceId ИД работающего процесса согласования
	 * @param bpmPackage ссылка на Workflow Package Folder, хранилище всех
	 * item-ов workflow
	 * @param variableScope переменные бизнес-процесса
	 */
	public void notifyDeadlineTasks(final String processInstanceId, final ActivitiScriptNode bpmPackage, final VariableScope variableScope) {
		approvalService.notifyAssigneesDeadline(processInstanceId, bpmPackage.getNodeRef());
		approvalService.notifyInitiatorDeadline(processInstanceId, bpmPackage.getNodeRef(), variableScope);
	}

	public String getExecutorBoss(String executorPersonName) {
		return Utils.getExecutorBoss(executorPersonName);
	}

	public String getUnitBoss(String unitCode) {
		NodeRef unitBossRef = orgstructureService.getUnitBoss(unitCode);
		return orgstructureService.getEmployeeLogin(unitBossRef);
	}

	public String getFirstCurator() {
		List<NodeRef> curators = Utils.getCurators();
		if (curators == null || curators.isEmpty()) {
			return null;
		}

		NodeRef firstCuratorEmployee = curators.get(0);
		return orgstructureService.getEmployeeLogin(firstCuratorEmployee);
	}

	public Collection<String> getDivisionBosses(String divisionCodesStr) {
		if (divisionCodesStr == null) {
			return null;
		}

		String[] divisionCodes = divisionCodesStr.split(",");
		Collection<String> result = new ArrayList<String>();
		for (String divisionCode : divisionCodes) {
			String divisionBoss = getUnitBoss(divisionCode);
			result.add(divisionBoss);
		}

		return result;
	}

	public void assignTask(ActivitiScriptNode assignee, DelegateTask task) {
		approvalService.assignTask(assignee.getNodeRef(), task);
	}

	public WorkflowTaskDecision completeTask(ActivitiScriptNode assignee, DelegateTask task) {
		return approvalService.completeTask(assignee.getNodeRef(), task);
	}

	public ActivitiScriptNodeList createAssigneesList(ActivitiScriptNode assigneesListNode, DelegateExecution execution) {
		List<NodeRef> assigneesList = approvalService.createAssigneesList(assigneesListNode.getNodeRef(), execution);
		//у нас есть рабочая копия списка участников процесса подписания
		//нам надо пробежаться по участникам этого списка, через сервис делегирования найти актуальных исполнителей
		//актуализировать ассоциацию на сотрудника и userName
		String workflowRole = (String)execution.getVariable(WORKFLOW_ROLE);
		assigneesList = workflowAssigneesListService.actualizeAssigneesUsingDelegation(assigneesList, workflowRole);
		ActivitiScriptNodeList assigneesActivitiList = new ActivitiScriptNodeList();
		for (NodeRef assigneeNode : assigneesList) {
			assigneesActivitiList.add(new ActivitiScriptNode(assigneeNode, serviceRegistry));
		}
		return assigneesActivitiList;
	}

	public void deleteTempAssigneesList(DelegateExecution execution) {
		approvalService.deleteTempAssigneesList(execution);
	}

	public void assignCustomApprovalTask(DelegateTask task) {
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		approvalService.assignTask(employeeRef, task);
	}

	public WorkflowTaskDecision completeTask(DelegateTask task, String decision, ActivitiScriptNode commentScriptNode) {
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		NodeRef commentRef = commentScriptNode != null ? commentScriptNode.getNodeRef() : null;
		return approvalService.completeTask(employeeRef, task, decision, commentRef, task.getDueDate());
	}

	public Map<String, String> addDecision(Map<String, String> decisionMap, String userName, String decision) {
		if (decisionMap == null) {
			decisionMap = new HashMap<String, String>();
		}
		decisionMap.put(userName, decision);

		return decisionMap;
	}

	public void saveRouteApprovalResult(final ActivitiScriptNode bpmPackage, final boolean isApproved) {
		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage.getNodeRef());
		if (nodeService.hasAspect(documentRef, RouteAspecsModel.ASPECT_ROUTABLE)) {
			nodeService.setProperty(documentRef, RouteAspecsModel.PROP_IS_APPROVED, isApproved);
		}
	}
}
