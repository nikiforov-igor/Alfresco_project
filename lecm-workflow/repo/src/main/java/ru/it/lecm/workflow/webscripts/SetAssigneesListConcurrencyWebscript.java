package ru.it.lecm.workflow.webscripts;

import java.io.IOException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.workflow.api.WorkflowAssigneesListService;

/**
 *
 * @author vlevin
 */
public class SetAssigneesListConcurrencyWebscript extends AbstractWebScript {
	private WorkflowAssigneesListService workflowAssigneesListService;

	public void setWorkflowAssigneesListService(WorkflowAssigneesListService workflowAssigneesListService) {
		this.workflowAssigneesListService = workflowAssigneesListService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		final JSONObject jsonRequest;
		final Content content = req.getContent();
		final String concurrencyStr, nodeRefStr;

		if (content == null) {
			throw new WebScriptException("Empty JSON content. Sorry.");
		}
		try {
			jsonRequest = new JSONObject(content.getContent());
			concurrencyStr = jsonRequest.getString("concurrency");
			nodeRefStr = jsonRequest.getString("nodeRef");
		} catch (IOException ex) {
			throw new WebScriptException("Can't read request content as json string", ex);
		} catch (JSONException ex) {
			throw new WebScriptException("Can't marshall request content as json object", ex);
		}

		workflowAssigneesListService.setAssigneesListConcurrency(new NodeRef(nodeRefStr), concurrencyStr);

	}



}
