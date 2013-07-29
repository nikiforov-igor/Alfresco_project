package ru.it.lecm.approval.extensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.Utils;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

public class ApprovalJavascriptExtension extends BaseScopableProcessorExtension {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalJavascriptExtension.class);
	private PersonService personService;
	private NodeService nodeService;
	private ServiceRegistry serviceRegistry;
	private OrgstructureBean orgstructureService;
	private ApprovalListService approvalListService;
	private StateMachineServiceBean stateMachineHelper;

	public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
		this.stateMachineHelper = stateMachineHelper;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setApprovalListService(ApprovalListService approvalListService) {
		this.approvalListService = approvalListService;
	}

    public ActivitiScriptNode createApprovalList(ActivitiScriptNode bpmPackage, String documentAttachmentCategoryName, String approvalType) {
        return createApprovalList(bpmPackage, documentAttachmentCategoryName, approvalType, new ActivitiScriptNodeList());
    }

	/**
	 * формирование нового листа согласования, для текущей версии регламента
	 *
	 * @param employeeList список сотрудников, ака согласущие лица
	 * @param bpmPackage ссылка на Workflow Package Folder, хранилище всех
	 * item-ов workflow
	 * @param approvalType тип согласования: APPROVAL, SEQUENTIAL, CUSTOM
	 * @param documentAttachmentCategoryName название категории вложений, в
	 * которой хранится файл документа
	 * @return ссылку на новый лист согласования
	 */
	public ActivitiScriptNode createApprovalList(final ActivitiScriptNode bpmPackage, final String documentAttachmentCategoryName, final String approvalType, ActivitiScriptNodeList assigneesList) {
		NodeRef approvalListRef = approvalListService.createApprovalList(bpmPackage.getNodeRef(), documentAttachmentCategoryName, approvalType, assigneesList);
		return new ActivitiScriptNode(approvalListRef, serviceRegistry);
	}

	public String getFinalDecision(final Map<String, String> decisionMap) {
		String finalDecision = "NO_DECISION";
		if (decisionMap.containsValue("REJECTED")) {
			finalDecision = "REJECTED";
		} else if (decisionMap.containsValue("APPROVED_WITH_REMARK")) {
			finalDecision = "APPROVED_WITH_REMARK";
		} else if (decisionMap.containsValue("APPROVED")) {
			finalDecision = "APPROVED";
		}
		return finalDecision;
	}

	public boolean isApproved(final String finalDecision) {
		return "APPROVED_WITH_REMARK".equals(finalDecision) || "APPROVED".equals(finalDecision) || "APPROVED_FORCE".equals(finalDecision);
	}

	public void logFinalDecision(final ActivitiScriptNode approvalListRef, final String finalDecision) {
		approvalListService.logFinalDecision(approvalListRef.getNodeRef(), finalDecision);
	}

	public void grantReviewerPermissions(final ActivitiScriptNode assigneeRef, final ActivitiScriptNode bpmPackage) {
		NodeRef candidate = assigneeRef.getNodeRef();
		QName candidateType = nodeService.getType(candidate);
		if (ContentModel.TYPE_PERSON.isMatch(candidateType)) {
			NodeRef employeeRef = orgstructureService.getEmployeeByPerson(candidate);
			approvalListService.grantReviewerPermissions(employeeRef, bpmPackage.getNodeRef());
		} else if (ApprovalListService.TYPE_ASSIGNEES_ITEM.isMatch(candidateType)) {
			NodeRef employeeRef = approvalListService.getEmployeeForAssignee(candidate);
			approvalListService.grantReviewerPermissions(employeeRef, bpmPackage.getNodeRef());
		}
	}

	public void grantPermissionsForReviewer(String userName, ActivitiScriptNode bpmPackage) {
		if (userName == null || bpmPackage == null) {
			return;
		}

		NodeRef personRef = personService.getPerson(userName);
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(personRef);
		approvalListService.grantReviewerPermissions(employeeRef, bpmPackage.getNodeRef());
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
			approvalListService.notifyApprovalStarted(employeeRef, dueDate, bpmPackage.getNodeRef());
		} else if (ApprovalListService.TYPE_ASSIGNEES_ITEM.isMatch(candidateType)) {
			NodeRef employeeRef = approvalListService.getEmployeeForAssignee(candidate);
			approvalListService.notifyApprovalStarted(employeeRef, dueDate, bpmPackage.getNodeRef());
		}
	}

	public void notifyCustomApprovalStarted(String userName, Date dueDate, final ActivitiScriptNode bpmPackage) {
		if (userName == null || bpmPackage == null) {
			return;
		}

		NodeRef personRef = personService.getPerson(userName);
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(personRef);
		approvalListService.notifyApprovalStarted(employeeRef, dueDate, bpmPackage.getNodeRef());
	}

	public void notifyFinalDecision(final String decision, final ActivitiScriptNode bpmPackage) {
		approvalListService.notifyFinalDecision(decision, bpmPackage.getNodeRef());
	}

	public void terminateApproval(ActivitiScriptNode bpmPackage, String variable, Object value) {
		NodeRef document = Utils.getDocumentFromBpmPackage(bpmPackage.getNodeRef());
		ArrayList<String> definitions = new ArrayList<String>();
		definitions.add("lecmApproval");
		definitions.add("lecmCustomApproval");
		stateMachineHelper.terminateWorkflowsByDefinitionId(document, definitions, variable, value);
	}

	/**
	 * если до наступления срока согласования остались сутки или меньше,
	 * то уведомить исполнителей и инициатора согласования о том,
	 * что необходимо принять решение
	 *
	 * @param processInstanceId ИД работающего процесса согласования
	 */
	public void notifyDeadlineTasks(final String processInstanceId, final ActivitiScriptNode bpmPackage, final VariableScope variableScope) {
		approvalListService.notifyAssigneesDeadline(processInstanceId, bpmPackage.getNodeRef());
		approvalListService.notifyInitiatorDeadline(processInstanceId, bpmPackage.getNodeRef(), variableScope);
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
		approvalListService.assignTask(assignee.getNodeRef(), task);
	}

	public void completeTask(ActivitiScriptNode assignee, DelegateTask task) {
		approvalListService.completeTask(assignee.getNodeRef(), task);
	}

	public ActivitiScriptNodeList createAssigneesList(ActivitiScriptNode assigneesListNode, DelegateExecution execution) {
		List<NodeRef> assigneesList = approvalListService.createAssigneesList(assigneesListNode.getNodeRef(), execution);
		ActivitiScriptNodeList assigneesActivitiList = new ActivitiScriptNodeList();
		for (NodeRef assigneeNode: assigneesList) {
			assigneesActivitiList.add(new ActivitiScriptNode(assigneeNode, serviceRegistry));
		}
		return assigneesActivitiList;
	}

	public void deleteTempAssigneesList(DelegateExecution execution) {
		approvalListService.deleteTempAssigneesList(execution);
	}
}
