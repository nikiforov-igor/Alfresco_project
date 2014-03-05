package ru.it.lecm.workflow.reservation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
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

/**
 *
 * @author vmalygin
 */
public class ReservationWorkflowServiceImpl extends WorkflowServiceAbstract implements ReservationWorkflowService {

	private final static Logger logger = LoggerFactory.getLogger(ReservationWorkflowServiceImpl.class);

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
		NodeRef currentEmp = orgstructureService.getCurrentEmployee();
		String employeeName = (String) nodeService.getProperty(currentEmp, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
		String template = "%s запросил резервирование номера для документа %s";
		String employeeUrl = wrapperLink(currentEmp, employeeName, LINK_URL);
		return String.format(template, employeeUrl, documentLink);
	}

	@Override
	protected String getWorkflowFinishedMessage(final String documentLink, final String decision) {
		String template = "Ваш запрос на резервирование регистрационного номера для документа %s %s";
		return String.format(template, documentLink, decision);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public void assignTask(final NodeRef assignee, final DelegateTask task) {
		//nop
	}

	@Override
	public WorkflowTaskDecision completeTask(final NodeRef assignee, final DelegateTask task) {
		String comment = (String) task.getVariableLocal("bpm_comment");
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
		//TODO: AuthenticationUtils.runAsSystemUser
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
		DocumentInfo docInfo = new DocumentInfo(bpmPackage, orgstructureService, nodeService, serviceRegistry);

		String notificationMessage = String.format(template, docInfo.getDocumentLink());

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(Arrays.asList(reservateInitiator));
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(docInfo.getDocumentRef());
		return notification;
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
