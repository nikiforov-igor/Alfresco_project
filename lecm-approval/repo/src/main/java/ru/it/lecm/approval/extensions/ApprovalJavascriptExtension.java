package ru.it.lecm.approval.extensions;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

public class ApprovalJavascriptExtension extends BaseScopableProcessorExtension {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalJavascriptExtension.class);
	private AuthenticationService authenticationService;
	private PersonService personService;
	private NodeService nodeService;
	private ServiceRegistry serviceRegistry;
	private OrgstructureBean orgstructureService;
	private ApprovalListService approvalListService;

	public ApprovalJavascriptExtension() {
		super();
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
	 * @return ссылку на новый лист согласования
	 */
	public ActivitiScriptNode createApprovalList(final ActivitiScriptNodeList employeeList, final ActivitiScriptNode bpmPackage) {
		NodeRef approvalListRef = approvalListService.createApprovalList(employeeList.getNodeReferences(), bpmPackage.getNodeRef());

		ActivitiScriptNode activitiApprovalListRef = new ActivitiScriptNode(approvalListRef, serviceRegistry);
		return activitiApprovalListRef;
	}

	/**
	 * добавление решения о согласовании от текущего исполнителя
	 * @param decisionMap карта с решениями
	 * @param userName имя пользователя, являющегося исполнителем
	 * @param decision решение принятое пользователем
	 * @return карта, дополненная новым решением
	 */
	public Map<String, String> addDecision(final Map<String, String> decisionMap, final String userName, final String decision) {
		Map<String, String> currentDecisionMap = (decisionMap == null) ? new HashMap<String, String>() : decisionMap;
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
	public void logDecision(ActivitiScriptNode approvalListRef, final String userName, final String decision) {
		
	}
}