package ru.it.lecm.approval.webscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;
import ru.it.lecm.documents.beans.DocumentService;

/**
 *
 * @author vlevin
 */
public class GetDocumentDataByTaskId extends DeclarativeWebScript {

	private final static String CONTRACT_NAMESPACE = "http://www.it.ru/logicECM/contract/1.0";
	private final static String CONTRACT_FAKE_NAMESPACE = "http://www.it.ru/logicECM/contract/fake/1.0";
	private final static QName TYPE_CONTRACT_DOCUMENT = QName.createQName(CONTRACT_NAMESPACE, "document");
	private final static QName TYPE_CONTRACT_FAKE_DOCUMENT = QName.createQName(CONTRACT_FAKE_NAMESPACE, "document");
	private final static QName ASSOC_CONTRACT_TYPE = QName.createQName(CONTRACT_NAMESPACE, "typeContract-assoc");
	private WorkflowService workflowService;
	private NodeService nodeService;

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		NodeRef documentRef = null;
		Map<String, Object> result = new HashMap<String, Object>();
		HttpServletRequest request = WebScriptServletRuntime.getHttpServletRequest(req);
		String taskID = request.getParameter("taskID");
		if (taskID == null || taskID.isEmpty()) {
			throw new WebScriptException("Task ID must be supplied");
		}

		List<NodeRef> packageContents = workflowService.getPackageContents(taskID);
		for (NodeRef node : packageContents) {
			QName nodeType = nodeService.getType(node);
			if (TYPE_CONTRACT_DOCUMENT.isMatch(nodeType)) {
				documentRef = node;

				break;
			} else if (TYPE_CONTRACT_FAKE_DOCUMENT.isMatch(nodeType)) {
				documentRef = node;

				break;
			}
		}
		if (documentRef == null) {
			throw new WebScriptException("No document attached");
		}

		String presentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);

		result.put("presentString", presentString);

		return result;
	}
}
