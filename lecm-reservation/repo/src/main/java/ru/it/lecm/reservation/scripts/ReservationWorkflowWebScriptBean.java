package ru.it.lecm.reservation.scripts;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNodeList;
import org.alfresco.repo.workflow.jscript.JscriptWorkflowInstance;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.notification.NotificationService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

/**
 *
 * @author snovikov
 */
public class ReservationWorkflowWebScriptBean extends BaseWebScript {
	private static final Logger logger = LoggerFactory.getLogger(ReservationWorkflowWebScriptBean.class);

	private OrgstructureBean orgstructureService;
	private DocumentService documentService;
	private DictionaryService dictionaryService;
	private NodeService nodeService;
	private RegNumbersService regNumbersService;
	private NotificationsService notificationsService;
	private BusinessJournalService businessJournalService;


	public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

	public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
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

	public ActivitiScriptNodeList getReservationDocumentRegistrators(ScriptNode documentNode){
		NodeRef document = documentNode.getNodeRef();
		List<NodeRef> registrars = orgstructureService.getEmployeesByBusinessRole("OUTGOING_REGISTRAR", true);
		ActivitiScriptNodeList registrarsScriptList = new ActivitiScriptNodeList();
		for (NodeRef registrar:registrars){
			NodeRef userRef = orgstructureService.getPersonForEmployee(registrar);
			ActivitiScriptNode userScriptNode = new ActivitiScriptNode(userRef, serviceRegistry);
			registrarsScriptList.add(userScriptNode);
		}
		notifyRegistrarsAboutStartReservate(document,registrars);
		return registrarsScriptList;
	}

	public NodeRef getCurrentEmployee(){
		return orgstructureService.getCurrentEmployee();
	}

	public void regnumReservate(ScriptNode documentNode, String isReservate, NodeRef reservateInitiator, String comment) throws TemplateParseException, TemplateRunException{
		NodeRef document  = documentNode.getNodeRef();
		if (isReservate.equals("Зарезервировать")){
			regNumbersService.registerDocument(document, "OUTGOING_DOC_NUMBER", true);
			notifyReservateInitiatorAboutSuccess(document,reservateInitiator);
		}else{
			notifyReservateInitiatorAboutReject(document,reservateInitiator,comment);
		}
	}

	private void notifyRegistrarsAboutStartReservate(NodeRef document, List<NodeRef> registrars){
		NodeRef currentEmp = orgstructureService.getCurrentEmployee();
		String employeeName = java.lang.String.format(
					"%s %s %s",
					nodeService.getProperty(currentEmp, orgstructureService.PROP_EMPLOYEE_LAST_NAME).toString(),
					nodeService.getProperty(currentEmp, orgstructureService.PROP_EMPLOYEE_FIRST_NAME).toString(),
					nodeService.getProperty(currentEmp, orgstructureService.PROP_EMPLOYEE_MIDDLE_NAME).toString());
		String doumentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		String notificationMessage = java.lang.String.format(
					"%s запросил резервирование номера для документа %s",
					this.wrapperLink(currentEmp.toString(), employeeName),
					this.wrapperLink(document.toString(), doumentString, "/share/page/document"));

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(registrars);
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(document);
		notification.setInitiatorRef(null);
		notificationsService.sendNotification(notification);
	}

	private void notifyReservateInitiatorAboutSuccess(NodeRef document, NodeRef initiator){
		String doumentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		String notificationMessage = java.lang.String.format(
			"Ваш запрос на резервирование регистрационного номера для документа %s выполнен",
			this.wrapperLink(document.toString(), doumentString, "/share/page/document"));

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(Arrays.asList(initiator));
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(document);
		notification.setInitiatorRef(null);
		notificationsService.sendNotification(notification);
	}

	private void notifyReservateInitiatorAboutReject(NodeRef document, NodeRef initiator, String comment){
		String doumentString = (String) nodeService.getProperty(document, DocumentService.PROP_PRESENT_STRING);
		String notificationMessage = java.lang.String.format(
			"Ваш запрос на резервирование регистрационного номера для документа %s <a href=\"#\" title=\""+comment+"\">отклонен</a>",
			this.wrapperLink(document.toString(), doumentString, "/share/page/document"));

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(Arrays.asList(initiator));
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(notificationMessage);
		notification.setObjectRef(document);
		notification.setInitiatorRef(null);
		notificationsService.sendNotification(notification);

		String bjMessage = "#initiator <a href=\"#\" title=\""+comment+"\">отклонил запрос</a> в резервировании номера документа #mainobject";
		String registrarLogin = orgstructureService.getEmployeeLogin(orgstructureService.getCurrentEmployee());
		businessJournalService.log(registrarLogin, document, "RESERVATION", bjMessage, null);
	}

}
