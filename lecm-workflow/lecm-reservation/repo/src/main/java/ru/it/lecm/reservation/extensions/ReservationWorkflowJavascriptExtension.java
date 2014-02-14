package ru.it.lecm.reservation.extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 *
 * @author snovikov
 */
public class ReservationWorkflowJavascriptExtension extends BaseWebScript {

	private static final Logger logger = LoggerFactory.getLogger(ReservationWorkflowJavascriptExtension.class);

	private OrgstructureBean orgstructureService;
	private NodeService nodeService;
	private RegNumbersService regNumbersService;
	private NotificationsService notificationsService;
	private BusinessJournalService businessJournalService;
	private EDSGlobalSettingsService edsGlobalSettingsService;
	private StateMachineServiceBean stateMachineService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public ActivitiScriptNodeList getReservationDocumentRegistrators(ScriptNode documentNode) {
		NodeRef document = documentNode.getNodeRef();
		List<NodeRef> registrars = new ArrayList<NodeRef>();
		//получаем текущего пользователя
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		//получаем его основную должностную позицию
		NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(currentEmployee);
		if (primaryStaff != null) {
			NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
			//получение списка регистраторов в засисимости от центролизованной/нецентрализованной регистрации
			Collection<NodeRef> potentialWorkers = edsGlobalSettingsService.getPotentialWorkers("OUTGOING_REGISTRAR", unit);
			registrars.addAll(potentialWorkers);
		}
		ActivitiScriptNodeList registrarsScriptList = new ActivitiScriptNodeList();
		for (NodeRef registrar : registrars) {
			stateMachineService.grandDynamicRoleForEmployee(document, registrar, "OUTGOING_REGISTRAR_DYNAMIC");
			NodeRef userRef = orgstructureService.getPersonForEmployee(registrar);
			ActivitiScriptNode userScriptNode = new ActivitiScriptNode(userRef, serviceRegistry);
			registrarsScriptList.add(userScriptNode);
		}
		notifyRegistrarsAboutStartReservate(document, registrars);
		return registrarsScriptList;
	}

	public NodeRef getCurrentEmployee() {
		return orgstructureService.getCurrentEmployee();
	}

	public void regnumReservate(ScriptNode documentNode, String isReservate, NodeRef reservateInitiator, String comment) throws TemplateParseException, TemplateRunException {
		NodeRef document = documentNode.getNodeRef();
		if ("RESERVED".equals(isReservate)) {
			regNumbersService.registerDocument(document, "OUTGOING_DOC_NUMBER", true);
			notifyReservateInitiatorAboutSuccess(document, reservateInitiator);
		} else {
			notifyReservateInitiatorAboutReject(document, reservateInitiator, comment);
		}
	}

	public void regnumReservateEmpty(ScriptNode documentNode, NodeRef reservateInitiator) throws TemplateParseException, TemplateRunException {
		NodeRef document = documentNode.getNodeRef();
		regNumbersService.registerDocument(document, "OUTGOING_DOC_NUMBER", true);
		List<AssociationRef> regDocDataList = nodeService.getTargetAssocs(document, DocumentService.ASSOC_REG_DOCUMENT_DATA);
		if (null != regDocDataList && regDocDataList.size() > 0) {
			NodeRef regDocData = regDocDataList.get(0).getTargetRef();
			nodeService.setProperty(regDocData, DocumentService.PROP_REG_DATA_NUMBER, "\"нет доступных регистраторов\"");
		}
		notifyReserveInitiatorAboutEmptyRegistrars(document, reservateInitiator);

		String documentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		logger.warn("Нет доступных регистраторов для документа {}", documentString);
	}

	private void notifyRegistrarsAboutStartReservate(NodeRef document, List<NodeRef> registrars) {
		NodeRef currentEmp = orgstructureService.getCurrentEmployee();
		String employeeName = (String) nodeService.getProperty(currentEmp, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
		String documentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		String template = "%s запросил резервирование номера для документа %s";
		String employeeUrl = wrapperLink(currentEmp.toString(), employeeName);
		String documentUrl = wrapperLink(document.toString(), documentString, BaseBean.DOCUMENT_LINK_URL);
		String notificationMessage = String.format(template, employeeUrl, documentUrl);

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(registrars);
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(document);
		notificationsService.sendNotification(notification);
	}

	private void notifyReservateInitiatorAboutSuccess(NodeRef document, NodeRef initiator) {
		String template = "Ваш запрос на резервирование регистрационного номера для документа %s выполнен";
		String documentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		String documentUrl = wrapperLink(document.toString(), documentString, BaseBean.DOCUMENT_LINK_URL);
		String notificationMessage = String.format(template, documentUrl);

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(Arrays.asList(initiator));
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(document);
		notificationsService.sendNotification(notification);
	}

	private void notifyReservateInitiatorAboutReject(NodeRef document, NodeRef initiator, String comment) {
		String template = "Ваш запрос на резервирование регистрационного номера для документа %s %s";
		String documentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		String documentUrl = wrapperLink(document.toString(), documentString, BaseBean.DOCUMENT_LINK_URL);
		String commentUrl = String.format("<a href='#' title='%s'>отклонен</a>", comment);
		String notificationMessage = String.format(template, documentUrl, commentUrl);

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(Arrays.asList(initiator));
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(document);
		notificationsService.sendNotification(notification);

		String bjMessage = "#initiator <a href=\"#\" title=\"" + comment + "\">отклонил запрос</a> в резервировании номера документа #mainobject";
		String registrarLogin = orgstructureService.getEmployeeLogin(orgstructureService.getCurrentEmployee());
		businessJournalService.log(registrarLogin, document, "RESERVATION", bjMessage, null);
	}

	private void notifyReserveInitiatorAboutEmptyRegistrars(NodeRef document, NodeRef initiator) {
		String template = "Нет доступных регистраторов для регистрации документа %s";
		String documentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		String documentUrl = wrapperLink(document.toString(), documentString, BaseBean.DOCUMENT_LINK_URL);
		String notificationMessage = String.format(template, documentUrl);

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(Arrays.asList(initiator));
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(document);
		notificationsService.sendNotification(notification);
	}
}
