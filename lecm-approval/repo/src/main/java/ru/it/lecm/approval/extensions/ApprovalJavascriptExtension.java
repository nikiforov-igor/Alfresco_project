package ru.it.lecm.approval.extensions;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.VariableScope;

public class ApprovalJavascriptExtension extends BaseScopableProcessorExtension {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalJavascriptExtension.class);
	private AuthenticationService authenticationService;
	private PersonService personService;
	private NodeService nodeService;
	private ServiceRegistry serviceRegistry;
	private OrgstructureBean orgstructureService;
	private ApprovalListService approvalListService;
	private StateMachineServiceBean stateMachineHelper;


    public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
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

	public ActivitiScriptNode getCurrentAuthenticatedPerson() {
		String currentUserName = authenticationService.getCurrentUserName();
		NodeRef nodeRef = personService.getPerson(currentUserName, false);
		ActivitiScriptNode scriptNode = null;
		if (nodeRef != null && nodeService.exists(nodeRef)) {
			scriptNode = new ActivitiScriptNode(nodeRef, serviceRegistry);
			String username = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_USERNAME);
			String firstname = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_FIRSTNAME);
			String lastname = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_LASTNAME);
			logger.info("Current authenticated user is [{}] {} {}", new Object[]{username, firstname, lastname});
		}
		return scriptNode;
	}

	public ActivitiScriptNodeList getPersonListByEmployeeList(ActivitiScriptNodeList employeeList) {
		ParameterCheck.mandatory("employeeList", employeeList);
		ActivitiScriptNodeList personList = new ActivitiScriptNodeList();
		for (ActivitiScriptNode employee: employeeList) {
			NodeRef personNodeRef = orgstructureService.getPersonForEmployee(employee.getNodeRef());
			if (personNodeRef != null && nodeService.exists(personNodeRef)) {
				ActivitiScriptNode person = new ActivitiScriptNode(personNodeRef, serviceRegistry);
				personList.add(person);
			}
		}
		return personList;
	}

	/**
	 * формирование нового листа согласования, для текущей версии регламента
	 * @param employeeList список сотрудников, ака согласущие лица
	 * @param bpmPackage ссылка на Workflow Package Folder, хранилище всех item-ов workflow
	 * @param documentAttachmentCategoryName  название категории вложений, в которой хранится файл документа
	 * @return ссылку на новый лист согласования
	 */
	public ActivitiScriptNode createApprovalList(final ActivitiScriptNode bpmPackage, final String documentAttachmentCategoryName) {
		NodeRef approvalListRef = approvalListService.createApprovalList(bpmPackage.getNodeRef(), documentAttachmentCategoryName);
		return new ActivitiScriptNode(approvalListRef, serviceRegistry);
	}

	/**
	 * добавление решения о согласовании от текущего исполнителя
	 * @param decisionMap карта с решениями
	 * @param userName имя пользователя, являющегося исполнителем
	 * @param decision решение принятое пользователем
	 * @return карта, дополненная новым решением
	 */
	public Map<String, String> addDecision(final Map<String, String> decisionMap, final String taskDecision) {
		Map<String, String> currentDecisionMap = (decisionMap == null) ? new HashMap<String, String>() : decisionMap;
		String userName = null;
		String decision = null;
		try {
			JSONObject task = new JSONObject(taskDecision);
			userName = task.getString("userName");
			decision = task.getString("decision");
		} catch(JSONException ex) {
			logger.error(ex.getMessage(), ex);
		}
		currentDecisionMap.put(userName, decision);
		return currentDecisionMap;
	}

	/**
	 * запись решения о согласовании от текущего исполнителя в лист согласования
	 * @param approvalListRef ссылка на лист согласования
	 * @param userName имя пользователя, являющегося исполнителем
	 * @param decision решение принятое пользователем
	 * @return
	 */
	public void logDecision(final ActivitiScriptNode approvalListRef, final String taskDecision) {
		logger.debug(taskDecision);
		JSONObject task = null;
		try {
			task = new JSONObject(taskDecision);
		} catch (JSONException ex) {
			logger.error(ex.getMessage(), ex);
		}
		approvalListService.logDecision(approvalListRef.getNodeRef(), task);
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
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(assigneeRef.getNodeRef());
		approvalListService.grantReviewerPermissions(employeeRef, bpmPackage.getNodeRef());
	}

	/**
	 * прислать сотруднику уведомление о том, что начато согласование по документу
	 * @param person cm:person согласующий по документу
	 * @param dueDate индивидуальный срок согласования
	 * @param bpmPackage ссылка на Workflow Package Folder, хранилище всех item-ов workflow
	 */
	public void notifyApprovalStarted(final ActivitiScriptNode person, final Date dueDate, final ActivitiScriptNode bpmPackage) {
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(person.getNodeRef());
		approvalListService.notifyApprovalStarted(employeeRef, dueDate, bpmPackage.getNodeRef());
	}

	public void notifyFinalDecision(final String decision, final ActivitiScriptNode bpmPackage) {
		approvalListService.notifyFinalDecision(decision, bpmPackage.getNodeRef());
	}

    public void terminateApproval(ActivitiScriptNode bpmPackage, String variable, Object value) {
        NodeRef document = approvalListService.getDocumentFromBpmPackage(bpmPackage.getNodeRef());
        ArrayList<String> definitions = new ArrayList<String>();
        definitions.add("lecmParallelApproval");
        definitions.add("lecmSequentialApproval");
        stateMachineHelper.terminateWorkflowsByDefinitionId(document, definitions, variable, value);
    }

	/**
	 * если до наступления срока согласования остались сутки или меньше,
	 * то уведомить исполнителей и инициатора согласования о том,
	 * что необходимо принять решение
	 * @param processInstanceId ИД работающего процесса согласования
	 */
	public void notifyDeadlineTasks(final String processInstanceId, final ActivitiScriptNode bpmPackage, final VariableScope variableScope) {
		approvalListService.notifyAssigneesDeadline(processInstanceId, bpmPackage.getNodeRef());
		approvalListService.notifyInitiatorDeadline(processInstanceId, bpmPackage.getNodeRef(), variableScope);
	}
}