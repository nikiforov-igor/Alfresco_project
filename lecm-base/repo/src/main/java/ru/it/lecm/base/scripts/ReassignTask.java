package ru.it.lecm.base.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 23.04.2014
 * Time: 11:38
 */
public class ReassignTask extends AbstractWebScript {
	final private static Logger logger = LoggerFactory.getLogger(ReassignTask.class);

	private WorkflowService workflowService;
	private OrgstructureBean orgstructureService;
	protected NotificationsService notificationsService;
	private BusinessJournalService businessJournalService;
	protected NodeService nodeService;
	protected DocumentService documentService;

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String taskId = req.getExtensionPath();

		if (taskId != null) {
			Content c = req.getContent();
			try {
				JSONObject json = new JSONObject(c.getContent());
				String ownerStr = (String) json.get("prop_cm_owner");
				if (ownerStr != null) {
					NodeRef employeeRef = new NodeRef(ownerStr);
					if (nodeService.exists(employeeRef) && orgstructureService.isEmployee(employeeRef)) {
						String userName = orgstructureService.getEmployeeLogin(employeeRef);
						if (userName != null) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
							properties.put(ContentModel.PROP_OWNER, userName);
							workflowService.updateTask(taskId, properties, null, null);

							NodeRef document = getDocumentFromTask(taskId);
							if (document != null) {
								//Отправка уведомления
								String author = AuthenticationUtil.getSystemUserName();
								String employeeName = (String) nodeService.getProperty(orgstructureService.getCurrentEmployee(), OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
								String text = "Сотрудник " + employeeName + " переназначил вам задачу по документу:  " + documentService.wrapAsDocumentLink(document);

								List<NodeRef> recipients = new ArrayList<NodeRef>();
								recipients.add(employeeRef);

								notificationsService.sendNotification(author, document, text, recipients, null);

								//Запись в бизнес-журнал
								List<String> objects = new ArrayList<String> ();
								objects.add(employeeRef.toString());
								String template = "Сотрудник #initiator переназначил задачу по документу: #mainobject сотруднику #object1";
								businessJournalService.log(document, EventCategory.EDIT, template, objects);
							}
						}
					}
				}
			} catch (JSONException e) {
				throw new WebScriptException(Status.STATUS_BAD_REQUEST,
						"Unable to parse JSON POST body: " + e.getMessage());
			}
		}
	}

	private NodeRef getDocumentFromTask(String taskId) {
		List<NodeRef> packageContents = workflowService.getPackageContents(taskId);
		if (packageContents != null) {
			for (NodeRef ref : packageContents) {
				if (documentService.isDocument(ref)) {
					return ref;
				}
			}
		} else {
			logger.error("List of bpm:package children is null");
		}
		return null;
	}
}
