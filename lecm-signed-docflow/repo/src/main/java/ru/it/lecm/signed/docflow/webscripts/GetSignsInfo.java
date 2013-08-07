package ru.it.lecm.signed.docflow.webscripts;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.base.DeclarativeWebScriptHelper;
import ru.it.lecm.signed.docflow.api.Signature;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSignsInfo extends DeclarativeWebScript {

	private NodeService nodeService;

	private SignedDocflow signedDocflowService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> result = new HashMap<String, Object>();

		final Content content = req.getContent();
		if (content == null) {
			throw new WebScriptException("Empty JSON content. Sorry.");
		}

		JSONObject jsonResult;

		try {
			String signedContentRefString = req.getParameter("signedContentRef");
			NodeRef signedContentRef = new NodeRef(signedContentRefString);

			jsonResult = new JSONObject();
			jsonResult.put("signedContentName", nodeService.getProperty(signedContentRef, ContentModel.PROP_NAME));

			List<Signature> signatures = signedDocflowService.getSignatures(signedContentRef);
			JSONArray signaturesJsonArray = new JSONArray();

			for(Signature signature : signatures) {
				signaturesJsonArray.put(new JSONObject(signature));
			}

			jsonResult.put("signatures", signaturesJsonArray);
		} catch (JSONException e) {
			throw new WebScriptException(">>> >>> GetSignsInfo <<< <<<");
		}

		result.put("result", jsonResult);
		return result;
	}
}