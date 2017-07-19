package ru.it.lecm.workflow.reservation;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;
import ru.it.lecm.workflow.DocumentInfo;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author vmalygin
 */
public class ReservationWorkflowServiceImpl extends WorkflowServiceAbstract implements ReservationWorkflowService {

	private final static Logger logger = LoggerFactory.getLogger(ReservationWorkflowServiceImpl.class);
	private static final String POSITIVE_DECISION = "выполнен";

	private EDSGlobalSettingsService edsGlobalSettingsService;
	private RegNumbersService regNumbersService;
	private BusinessJournalService businessJournalService;

	public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	protected String getWorkflowStartedMessage(final String documentLink, final Date dueDate) {
		return null;
	}

	@Override
	protected String getWorkflowFinishedMessage(final String documentLink, final String decision) {
		return null;
	}

	@Override
	public void notifyWorkflowStarted(NodeRef employeeRef, Date dueDate, NodeRef bpmPackage) {
		NodeRef docRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		NodeRef currentEmp = orgstructureService.getCurrentEmployee();

		HashMap<String, Object> templateObjects = new HashMap<>();
		templateObjects.put("employee", currentEmp);
		notificationsService.sendNotificationByTemplate(authService.getCurrentUserName(), docRef, Collections.singletonList(employeeRef), "RESERVATION_REQUEST_STARTED", templateObjects);
	}

	@Override
	public void notifyWorkflowFinished(NodeRef employeeRef, String decision, NodeRef bpmPackage) {
		NodeRef docRef = Utils.getDocumentFromBpmPackage(bpmPackage);

		String templateCode;
		if (decision.equals(POSITIVE_DECISION)) {
			templateCode = "RESERVATION_REQUEST_FINISHED_APPROVED";
		} else {
			templateCode = "RESERVATION_REQUEST_FINISHED_REJECTED";
		}
		notificationsService.sendNotificationByTemplate(authService.getCurrentUserName(), docRef, Collections.singletonList(employeeRef), templateCode);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public void assignTask(final NodeRef assignee, final DelegateTask task) {
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantDynamicRole(employeeRef, bpmPackage, (String) task.getVariable("registrarDynamicRole"));
		notifyWorkflowStarted(employeeRef, null, bpmPackage);
	}

	public void reassignTask(NodeRef assignee, DelegateTask task) {
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantDynamicRole(employeeRef, bpmPackage, (String) task.getVariable("registrarDynamicRole"));
	}

	@Override
	public WorkflowTaskDecision completeTask(final NodeRef assignee, final DelegateTask task) {
		String comment = (String) task.getVariableLocal("lecmRegnumRes_rejectReason");
		String decision = (String) task.getVariableLocal("reservationWf_isReservate");
		String regnumTemplateId = (String) task.getVariable("regnumTemplateId");
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage);

		WorkflowTaskDecision taskDecision = new WorkflowTaskDecision();
		taskDecision.setUserName(task.getAssignee());
		taskDecision.setDecision(decision);
		taskDecision.setStartDate(task.getCreateTime());
		taskDecision.setPreviousUserName((String) nodeService.getProperty(assignee, ContentModel.PROP_USERNAME));
		task.setVariable("taskDecision", decision);

		if (DecisionResult.RESERVED.name().equals(decision)) {
			try {
				regNumbersService.registerDocument(documentRef, regnumTemplateId, true);
			} catch (TemplateParseException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (TemplateRunException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} else if (DecisionResult.REJECTED.name().equals(decision)) {
			//запись в бизнес журнал если решение плохое
			String commentLink = String.format("<a href='#' title='%s'>отклонил</a>", comment);
			String bjMessage = String.format("#initiator %s запрос в резервировании номера документа #mainobject", commentLink);
			String registrarLogin = orgstructureService.getEmployeeLogin(orgstructureService.getCurrentEmployee());
			businessJournalService.log(registrarLogin, documentRef, "RESERVATION", bjMessage, null);
		}

		return taskDecision;
	}

	@Override
	public void setReservationActive(final NodeRef bpmPackage, final boolean isActive) {
		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		boolean hasReservationAspect = nodeService.hasAspect(documentRef, ReservationAspectsModel.ASPECT_IS_RESERVATION_RUNNING);
		if (hasReservationAspect) {
			nodeService.setProperty(documentRef, ReservationAspectsModel.PROP_IS_RESERVATION_RUNNING, isActive);
		} else {
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(ReservationAspectsModel.PROP_IS_RESERVATION_RUNNING, isActive);
			nodeService.addAspect(documentRef, ReservationAspectsModel.ASPECT_IS_RESERVATION_RUNNING, properties);
		}
		if (!isActive) {
			nodeService.removeAspect(documentRef, ReservationAspectsModel.ASPECT_IS_RESERVATION_RUNNING);
		}
	}

	@Override
	public List<NodeRef> getRegistrars(final NodeRef bpmPackage, final String registrarRole) {
		List<NodeRef> registrars;
		//получаем текущего пользователя
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		// централизованная ли регистрация
		Boolean registrationCenralized = edsGlobalSettingsService.isRegistrationCenralized();

		if (registrationCenralized) {
			registrars =  orgstructureService.getEmployeesByBusinessRole(registrarRole);
		} else {
			registrars = new ArrayList<NodeRef>();
			//получаем основную должностную позицию
			NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(currentEmployee);
			if (primaryStaff != null) {
				NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
				registrars.addAll(edsGlobalSettingsService.getPotentialWorkers(registrarRole, unit));
			}
		}

		return registrars;
	}

	@Override
	public void setEmptyRegnum(final NodeRef bpmPackage) {
		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		nodeService.setProperty(documentRef, DocumentService.PROP_DOCUMENT_REGNUM, "\"нет доступных регистраторов\"");
		String documentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);
		logger.warn("Нет доступных регистраторов для документа {}", documentString);
	}

	@Override
	public Notification prepareNotificationAboutEmptyRegistrars(final NodeRef bpmPackage, final NodeRef reservateInitiator) {
		String template = "Нет доступных регистраторов для регистрации документа %s";
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, documentService, nodeService, serviceRegistry);

		String notificationMessage = String.format(template, docInfo.getDocumentLink());

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(Arrays.asList(reservateInitiator));
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(docInfo.getDocumentRef());
		return notification;
	}

	@Override
	public boolean isReservationRunning(NodeRef document) {
		ParameterCheck.mandatory("document", document);
		if (nodeService.exists(document)) {
			Boolean isReservationRunning = (Boolean) nodeService.getProperty(document, PROP_IS_RESERVATION_RUNNING);
			return isReservationRunning != null ? isReservationRunning : false;
		}
		return false;
	}

	protected void actualizeReservationTask(NodeRef assignee, DelegateTask task) {
		String workflowRole = (String)task.getVariable("registrarDynamicRole");
		boolean delegateAll = workflowRole == null; //если роль не указана, то ориентируемся на делегирование всего
		NodeRef employee = orgstructureService.getEmployeeByPerson(assignee);
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
				String effectiveUserName = orgstructureService.getEmployeeLogin(effectiveEmployee);
				if (StringUtils.isNotEmpty(effectiveUserName)) {
					task.setAssignee(effectiveUserName);
					task.setOwner(effectiveUserName); //???
				}

				task.setVariable("assumeExecutor", employee);
			}
		}
	}

	@Override
	public void notifyAssigneesDeadline(final String processInstanceId, final NodeRef bpmPackage) {
		//nop
	}

	@Override
	public void notifyInitiatorDeadline(final String processInstanceId, final NodeRef bpmPackage, final VariableScope variableScope) {
		//nop
	}
}
