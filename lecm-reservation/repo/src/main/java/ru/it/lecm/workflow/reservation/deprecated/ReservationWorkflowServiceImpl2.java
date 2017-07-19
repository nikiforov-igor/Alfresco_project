package ru.it.lecm.workflow.reservation.deprecated;

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
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;
import ru.it.lecm.workflow.DocumentInfo;
import ru.it.lecm.workflow.Utils;
import ru.it.lecm.workflow.WorkflowTaskDecision;
import ru.it.lecm.workflow.beans.WorkflowServiceAbstract;
import ru.it.lecm.workflow.reservation.DecisionResult;
import ru.it.lecm.workflow.reservation.ReservationAspectsModel;
import ru.it.lecm.workflow.reservation.ReservationWorkflowService;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author vmalygin/apalm
 */

@Deprecated
public class ReservationWorkflowServiceImpl2 extends WorkflowServiceAbstract implements ReservationWorkflowService {

	private final static Logger logger = LoggerFactory.getLogger(ReservationWorkflowServiceImpl2.class);

	private EDSGlobalSettingsService edsGlobalSettingsService;
	private RegNumbersService regNumbersService;
	private BusinessJournalService businessJournalService;
	protected NotificationsService notificationsService;
	
	private static final String POSITIVE_DECISION = "выполнен";
	
	private static final String OUTGOING_DOC_NUMBER = "OUTGOING_DOC_NUMBER";
	private static final String INTERNAL_DOC_NUMBER = "INTERNAL_DOC_NUMBER";
	private static final String ND_DOC_NUMBER = "ND_NUMBER";
	private static final String ORD_DOC_NUMBER = "ORD_NUMBER";
	private static final String CONTRACT_DOC_NUMBER = "CONTRACT_REGNUM";

	private static final String OUTGOING_DOC = QName.createQName("http://www.it.ru/logicECM/outgoing/1.0", "document").toString();
	private static final String INTERNAL_DOC = QName.createQName("http://www.it.ru/logicECM/internal/1.0", "document").toString();
	private static final String ND_DOC = QName.createQName("http://www.it.ru/lecm/ND/1.0", "document").toString();
	private static final String ORD_DOC = QName.createQName("http://www.it.ru/lecm/ORD/1.0", "document").toString();
	private static final String CONTRACT_DOC = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "document").toString();
	
	public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}
	
	// Уведомление при старте запроса на резервирование рег.номера:
	
	@Override
	public void notifyWorkflowStarted(NodeRef employeeRef, Date dueDate, NodeRef bpmPackage) {
		NodeRef docRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		NodeRef currentEmp = orgstructureService.getCurrentEmployee();
		
        HashMap<String, Object> templateObjects = new HashMap<>();
        templateObjects.put("employee", currentEmp);
		notificationsService.sendNotificationByTemplate(authService.getCurrentUserName(), docRef, Collections.singletonList(employeeRef), "RESERVATION_REQUEST_STARTED", templateObjects);
	}
		
	// Уведомление при окончании запроса на резервирование рег.номера:
	
	@Override
	public void notifyWorkflowFinished(NodeRef employeeRef, String decision, NodeRef bpmPackage) {
		NodeRef docRef = Utils.getDocumentFromBpmPackage(bpmPackage);
        HashMap<String, Object> templateObjects = new HashMap<>();

		String templateCode;
        if (decision.equals(POSITIVE_DECISION)) {
			String regNumber = (String) nodeService.getProperty(docRef, DocumentService.PROP_REG_DATA_DOC_NUMBER);
        	templateObjects.put("regNumber", regNumber);
        	Date reserveDate = (Date) nodeService.getProperty(docRef, DocumentService.PROP_REG_DATA_DOC_DATE);
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			String regDateFormatted = dateFormat.format(reserveDate);
        	if (reserveDate != null) {
        		templateCode = "RESERVATION_REQUEST_FINISHED_APPROVED_WITH_DATE";
        		templateObjects.put("reserveDate", regDateFormatted);
        	}
        	else {
        		templateCode = "RESERVATION_REQUEST_FINISHED_APPROVED_WITHOUT_DATE";
        	}
        }
        else {
        	templateCode = "RESERVATION_REQUEST_FINISHED_REJECTED";
        }

		notificationsService.sendNotificationByTemplate(authService.getCurrentUserName(), docRef, Collections.singletonList(employeeRef), templateCode, templateObjects);
	}
	
	@Override
	protected String getWorkflowFinishedMessage(final String documentLink, final String decision) {
		return null;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	protected String getWorkflowStartedMessage(final String documentLink, final Date dueDate) {
		return null;
	}
	
	@Override
	public void assignTask(final NodeRef assignee, final DelegateTask task) {
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantDynamicRole(employeeRef, bpmPackage, (String) task.getVariable("registrarDynamicRole"));
		notifyWorkflowStarted(employeeRef, null, bpmPackage);
		// Set the message for the reservation task
		setReservationTaskMessage(bpmPackage, task);
	}

	public void reassignTask(NodeRef assignee, DelegateTask task) {
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef employeeRef = orgstructureService.getEmployeeByPerson(task.getAssignee());
		grantDynamicRole(employeeRef, bpmPackage, (String) task.getVariable("registrarDynamicRole"));
	}

	@Override
	public WorkflowTaskDecision completeTask(final NodeRef assignee, final DelegateTask task) {
		String comment = (String) task.getVariableLocal("lecmRegnumRes_rejectReason");
		String decision = (String) task.getVariableLocal("lecmRegnumRes_decision");
		Date regDate = (Date) task.getVariable("lecmRegnumRes_date");
		NodeRef bpmPackage = ((ScriptNode) task.getVariable("bpm_package")).getNodeRef();
		NodeRef documentRef = Utils.getDocumentFromBpmPackage(bpmPackage);
		String regnumTemplateId = getRegnumTemplateId(documentRef);
		
		WorkflowTaskDecision taskDecision = new WorkflowTaskDecision();
		taskDecision.setUserName(task.getAssignee());
		taskDecision.setDecision(decision);
		taskDecision.setStartDate(task.getCreateTime());
		taskDecision.setPreviousUserName((String) nodeService.getProperty(assignee, ContentModel.PROP_USERNAME));
		task.setVariable("taskDecision", decision);

		if (DecisionResult.RESERVED.name().equals(decision)) {
			try {
				String presentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);
				nodeService.setProperty(documentRef, ReservationAspectsModel.PROP_PRESENT_STRING_BEFORE_RESERVATION, presentString);
				
				regNumbersService.registerDocument(documentRef, regnumTemplateId, true);
				nodeService.setProperty(documentRef, ReservationAspectsModel.PROP_IS_RESERVED, true);
				if (regDate != null) {
					nodeService.setProperty(documentRef, DocumentService.PROP_REG_DATA_DOC_DATE, regDate);
				}
				
				// Запись в бизнес журнал если решение хорошее:
				String documentReservedNumber = (String) nodeService.getProperty(documentRef, DocumentService.PROP_REG_DATA_DOC_NUMBER);	
				String regInfo = "Зарезервирован номер: " + documentReservedNumber;
				if (regDate != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
					String documentReservedDate = dateFormat.format(regDate);
					regInfo += " от " + documentReservedDate;
				}
				String bjMessage = "#initiator выполнил резервирование регистрационного номера для документа #mainobject " + regInfo;
				String registrarLogin = orgstructureService.getEmployeeLogin(orgstructureService.getCurrentEmployee());
				businessJournalService.log(registrarLogin, documentRef, "RESERVATION", bjMessage, null);

			} catch (TemplateParseException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (TemplateRunException ex) {
				logger.error(ex.getMessage(), ex);
			}
		} else if (DecisionResult.REJECTED.name().equals(decision)) {
			nodeService.setProperty(documentRef, ReservationAspectsModel.PROP_IS_RESERVED, false);
			// Запись в бизнес журнал если решение плохое:
			String commentLink = String.format("<a href='#' title='%s'>отклонил</a>", comment);
			String bjMessage = String.format("#initiator %s запрос в резервировании номера документа #mainobject", commentLink);
			String registrarLogin = orgstructureService.getEmployeeLogin(orgstructureService.getCurrentEmployee());
			businessJournalService.log(registrarLogin, documentRef, "RESERVATION", bjMessage, null);
		}

		return taskDecision;
	}

	private String getRegnumTemplateId(NodeRef documentRef) {
		QName documentType = nodeService.getType(documentRef);
		String stringDocumentType = documentType.toString();
		if (stringDocumentType.equals(OUTGOING_DOC)) {
			return OUTGOING_DOC_NUMBER;
		} else if (stringDocumentType.equals(INTERNAL_DOC)) {
			return INTERNAL_DOC_NUMBER;
		} else if (stringDocumentType.equals(ND_DOC)) {
			return ND_DOC_NUMBER;
		} else if (stringDocumentType.equals(ORD_DOC)) {
			return ORD_DOC_NUMBER;
		} else if (stringDocumentType.equals(CONTRACT_DOC)) {
			return CONTRACT_DOC_NUMBER;
		} else {
			return "";
		}
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
			//nodeService.removeAspect(documentRef, ReservationAspectsModel.ASPECT_IS_RESERVATION_RUNNING);
			nodeService.setProperty(documentRef, ReservationAspectsModel.PROP_IS_RESERVATION_RUNNING, false);
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
	
	private void setReservationTaskMessage(NodeRef bpmPackage, final DelegateTask task) {
		NodeRef documentRef = Utils.getObjectFromBpmPackage(bpmPackage);
		
		NodeRef currentEmp = orgstructureService.getCurrentEmployee();
		String employeeName = (String) nodeService.getProperty(currentEmp, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
		
		Date resDate = (Date) task.getVariable("lecmRegnumRes_date");
		boolean isReservationDatePresent = (resDate == null) ? false : true;
				
		String message = "";

		if (isReservationDatePresent) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			String reservationDate = dateFormat.format(resDate);
			message = "Сотрудник " + employeeName + " запросил резервирование регистрационного номера на дату " + reservationDate;
		}
		else {
			message = "Сотрудник " + employeeName + " запросил резервирование регистрационного номера без указания желаемой даты регистрации.";
		}
		
		nodeService.setProperty(documentRef, ReservationAspectsModel.PROP_RESERVE_TASK_MESSAGE, message);
	}
	
}
