package ru.it.lecm.documents.scripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.security.LecmPermissionService;

public class DocumentAccessWebscript extends DeclarativeWebScript {

	private NodeService nodeService;
	private BusinessJournalService businessJournalService;
	private LecmPermissionService lecmPermissionService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String user = req.getParameter("user");
		String nodeRefStr = req.getParameter("nodeRef");
		NodeRef nodeRef = new NodeRef(nodeRefStr);
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("nodeRef", nodeRefStr);
		json.put("user", user);
		//проверить а есть ли nodeRef вообще
		boolean exists = nodeService.exists(nodeRef);
		json.put("exists", exists);
		if (exists) {
			//проверить у чувака который вызвал скрипт права
			boolean hasReadAccess = lecmPermissionService.hasReadAccess(nodeRef, user);
			json.put("hasReadAccess", hasReadAccess);
		} else {
			//поискать в БЖ запись об удалении
			List<BusinessJournalRecord> records = businessJournalService.getHistory(nodeRef, null, true, false, false);
			if (records != null) {
				boolean removed = false;
				for (BusinessJournalRecord record : records) {
					String eventCategory = record.getEventCategoryText();
					if (EventCategory.DELETE.equals(eventCategory)) {
						removed = true;
						break;
					}
					if (EventCategory.DELETE_DOCUMENT_ATTACHMENT.equals(eventCategory)) {
						removed = true;
						break;
					}
				}
				json.put("removed", removed);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", new JSONObject(json));
		return result;
	}
}