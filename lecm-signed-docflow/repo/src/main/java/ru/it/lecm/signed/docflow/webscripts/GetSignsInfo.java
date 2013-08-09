package ru.it.lecm.signed.docflow.webscripts;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
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
		JSONArray jsonArrayResult;
		try {
			String signedContentRefString = req.getParameter("signedContentRef");
			List<String> signedContentStringList = Arrays.asList(signedContentRefString.split("!!!"));
			List<NodeRef> signedContentList = new ArrayList<NodeRef>();
			for (String string : signedContentStringList) {
				signedContentList.add(new NodeRef(string));
			}

			jsonArrayResult = new JSONArray();

			Map<NodeRef, List<Signature>> signatures = signedDocflowService.getSignaturesInfo(signedContentList);
			
			for (Map.Entry<NodeRef, List<Signature>> entry : signatures.entrySet()) {
				JSONArray signaturesJsonArray = new JSONArray();
				jsonResult = new JSONObject();
				NodeRef nodeRef = entry.getKey();
				List<Signature> signaturesList = entry.getValue();
				jsonResult.put("signedContentNodeRef", nodeRef);
				jsonResult.put("signedContentName", nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));

				for (Signature signature : signaturesList) {
					signaturesJsonArray.put(new JSONObject(signature));
				}
				jsonResult.put("signatures", signaturesJsonArray);
				jsonArrayResult.put(jsonResult);
			}

			
		} catch (JSONException e) {
			throw new WebScriptException(">>> >>> GetSignsInfo <<< <<<");
		}

		result.put("result", jsonArrayResult);
		return result;
	}
}