package ru.it.lecm.workflow.beans;

import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.workflow.DocumentInfo;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.api.*;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author vlevin
 */
public abstract class WorkflowServiceAbstract extends BaseBean implements LecmWorkflowService {

	private final static Logger logger = LoggerFactory.getLogger(WorkflowServiceAbstract.class);
	public final static String RESULT_ITEM_FORMAT = "Участник %s";
	protected final static String DATE_FORMAT = "dd.MM.yyyy";

	protected final static QName FAKE_PROP_COMINGSOON = QName.createQName(NamespaceService.ALFRESCO_URI, "comingSoonNotified");
	protected final static QName FAKE_PROP_OVERDUE = QName.createQName(NamespaceService.ALFRESCO_URI, "overdueNotified");

	protected OrgstructureBean orgstructureService;
	protected LecmPermissionService lecmPermissionService;
	protected DocumentService documentService;
	protected DocumentMembersService documentMembersService;
	protected NotificationsService notificationsService;
	protected WorkflowService workflowService;
	protected WorkflowFoldersService workflowFoldersService;
	protected IDelegation delegationService;
	protected WorkflowResultListService workflowResultListService;
	protected StateMachineServiceBean stateMachineService;
    protected BusinessJournalService businessJournalService;
    protected DictionaryService dictionaryService;
    protected NamespaceService namespaceService;

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setWorkflowFoldersService(WorkflowFoldersService workflowFoldersService) {
		this.workflowFoldersService = workflowFoldersService;
	}

	public void setDelegationService(IDelegation delegationService) {
		this.delegationService = delegationService;
	}

	public void setWorkflowResultListService(WorkflowResultListService workflowResultListService) {
		this.workflowResultListService = workflowResultListService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
	@Override
	public void grantDynamicRole(final NodeRef employeeRef, final NodeRef bpmPackage, final String role) {
		NodeRef documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		if (Utils.isDocument(documentRef)) {
			stateMachineService.grandDynamicRoleForEmployee(documentRef, employeeRef, role);
		}
	}

	@Override
	public void grantReaderPermissions(final NodeRef employeeRef, final NodeRef bpmPackage, final boolean addEmployeeAsMember) throws WriteTransactionNeededException {
		NodeRef documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		if (Utils.isDocument(documentRef)) {
			this.grantPermissions(employeeRef, documentRef, "LECM_BASIC_PG_Reader", addEmployeeAsMember);
		}
	}

	protected void grantPermissions(final NodeRef employeeRef, final NodeRef documentRef, final String permissionGroup, final boolean addEmployeeAsMember) throws WriteTransactionNeededException {
		if (documentRef != null) {
			NodeRef member = null;
			if (addEmployeeAsMember) {
				member = documentMembersService.addMemberWithoutCheckPermission(documentRef, employeeRef, permissionGroup);
			}
			if (!addEmployeeAsMember || member == null) { // сотрудник уже добавлен как участник - значит просто раздаем доп права
				LecmPermissionService.LecmPermissionGroup pgGranting = lecmPermissionService.findPermissionGroup(permissionGroup);
				lecmPermissionService.grantAccess(pgGranting, documentRef, employeeRef);
			}
			if (logger.isTraceEnabled()) {
				String employeeName = (String) nodeService.getProperty(employeeRef, ContentModel.PROP_NAME);
				String docName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
				logger.trace("Employee {} has been invited to the document {} with {} permission. ", new Object[]{employeeName, docName, permissionGroup});
			}
		} else {
			logger.error("There is no any lecm-contract:document in bpm:package. Permissions won't be granted");
		}
	}

	@Override
	public void notifyWorkflowStarted(NodeRef employeeRef, Date dueDate, NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employeeRef);

		String description = getWorkflowStartedMessage(docInfo.getDocumentLink(), dueDate);

		sendNotification(description, docInfo.getDocumentRef(), recipients);
	}

	@Override
	public void notifyWorkflowFinished(String decision, NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);
		NodeRef employeeRef = documentService.getDocumentAuthor(docInfo.getDocumentRef());
		notifyWorkflowFinished(employeeRef, decision, docInfo);
	}

	@Override
	public void notifyWorkflowFinished(NodeRef employeeRef, String decision, NodeRef bpmPackage) {
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);
		notifyWorkflowFinished(employeeRef, decision, docInfo);
	}

	private void notifyWorkflowFinished(NodeRef employeeRef, String decision, DocumentInfo docInfo) {
		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(employeeRef);

		String description = getWorkflowFinishedMessage(docInfo.getDocumentLink(), decision);
		sendNotification(description, docInfo.getDocumentRef(), recipients);
	}

	protected void sendNotification(String message, NodeRef documentRef, List<NodeRef> recipients) {
		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(message);
		notification.setObjectRef(documentRef);
		notification.setRecipientEmployeeRefs(recipients);
		notificationsService.sendNotification(notification, true);
	}

	protected String getIncompleteAssignees(final String processInstanceId) {
		WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
		taskQuery.setProcessId(processInstanceId);
		taskQuery.setTaskState(WorkflowTaskState.IN_PROGRESS);
		List<WorkflowTask> tasks = workflowService.queryTasks(taskQuery);
		StringBuilder builder = new StringBuilder();
		for (WorkflowTask task : tasks) {
			String owner = (String) task.getProperties().get(ContentModel.PROP_OWNER);
			NodeRef employee = orgstructureService.getEmployeeByPerson(owner);
			String name = (String) nodeService.getProperty(employee, ContentModel.PROP_NAME);
			builder.append(name).append(", ");
		}
		int length = builder.length();
		if (length > 0) {
			builder.delete(length - 2, length); //удалить последний ", "
		}
		return builder.toString();
	}

	protected Map<String, String> addDecision(final Map<String, String> decisionMap, WorkflowTaskDecision taskDecision) {
		Map<String, String> currentDecisionMap = (decisionMap == null) ? new HashMap<String, String>() : decisionMap;

		String userName = taskDecision.getUserName();
		String decision = taskDecision.getDecision();

		currentDecisionMap.put(userName, decision);
		return currentDecisionMap;
	}

	protected void actualizeTaskAssignee(NodeRef assignee, DelegateTask task) {
		//находить настоящего employee согласно логике делегирования
		//находить его usename
		//поменять ссылку на employee
		//поменять username
		String workflowRole = (String)task.getVariable("workflowDynRole");
		boolean delegateAll = workflowRole == null; //если роль не указана, то ориентируемся на делегирование всего
		NodeRef employee = findNodeByAssociationRef(assignee, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		String userName = orgstructureService.getEmployeeLogin(employee);
		//находим ResultItem
		NodeRef approvalListRef = workflowResultListService.getResultListRef(task);
		NodeRef approvalListItemRef = workflowResultListService.getResultItemByUserName(approvalListRef, userName);
		//привязали к задаче
		String taskId = String.format("activiti$%s$%s", task.getProcessInstanceId(), task.getId());
		nodeService.setProperty(approvalListItemRef, WorkflowResultModel.PROP_WORKFLOW_RESULT_ITEM_TASK_ID, taskId);
		NodeRef delegationOpts = delegationService.getDelegationOpts(employee);
		boolean isDelegationActive = delegationService.isDelegationActive(delegationOpts);
		if (isDelegationActive) {
			NodeRef effectiveEmployee;
			if (delegateAll) {
				effectiveEmployee = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			} else {
				effectiveEmployee = delegationService.getEffectiveExecutor(employee, workflowRole);
				//если эффективного исполнителя не нашли по бизнес-ролям, то поискать его через параметры делегирования
				if (employee.equals(effectiveEmployee)) {
					effectiveEmployee = findNodeByAssociationRef(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				}
			}
			if (effectiveEmployee != null) {
				nodeService.removeAssociation(assignee, employee, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE);
				nodeService.createAssociation(assignee, effectiveEmployee, LecmWorkflowModel.ASSOC_ASSIGNEE_EMPLOYEE);
				String effectiveUserName = orgstructureService.getEmployeeLogin(effectiveEmployee);
				if (StringUtils.isNotEmpty(effectiveUserName)) {
					nodeService.setProperty(assignee, LecmWorkflowModel.PROP_ASSIGNEE_USERNAME, effectiveUserName);
					task.setAssignee(effectiveUserName);
					task.setOwner(effectiveUserName); //???
				}
				//меняем ассоциацию на employee в result list item
				nodeService.removeAssociation(approvalListItemRef, employee, WorkflowResultModel.ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE);
				nodeService.createAssociation(approvalListItemRef, effectiveEmployee, WorkflowResultModel.ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE);

				task.setVariable("assumeExecutor", employee);
			}
		}
	}

	protected void completeTaskAddMembers(NodeRef employeeRef, NodeRef bpmPackage, DelegateTask task) throws WriteTransactionNeededException {
		NodeRef documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		if (Utils.isDocument(documentRef)) {
			documentMembersService.addMemberWithoutCheckPermission(documentRef, employeeRef, new HashMap<QName, Serializable>(), true);

			NodeRef assumeExecutor = (NodeRef) task.getVariable("assumeExecutor");
			if (assumeExecutor != null) {
				documentMembersService.addMemberWithoutCheckPermission(documentRef, assumeExecutor, new HashMap<QName, Serializable>(), true);
			}
		}
	}

	abstract protected String getWorkflowStartedMessage(String documentLink, Date dueDate);

	abstract protected String getWorkflowFinishedMessage(String documentLink, String decision);
}
