/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.DeclarativeWebScriptHelper;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author rjagudin
 */
public class GetHashesWebscript extends DeclarativeWebScript {
    private SignedDocflow signedDocflowService;

    public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

    private final static Logger logger = LoggerFactory.getLogger(GetHashesWebscript.class);

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> result = new HashMap<>();
        JSONArray jsonResults = new JSONArray();

        final Content content = req.getContent();
		if (content == null) {
			throw new WebScriptException("Empty JSON content. Sorry.");
		}


        JSONArray data = DeclarativeWebScriptHelper.getJsonArrayContent(content);
        List<NodeRef> refsToSignList = new ArrayList<>();

        try {
            for(int i = 0; i < data.length(); i++){
                refsToSignList.add(new NodeRef(data.getString(i)));
            }
        } catch (Exception ex) {
            throw new WebScriptException(null, ex);
        }

        Map<String, Object> javaResults = null;
        try{
            javaResults = signedDocflowService.getHashes(refsToSignList);
        } catch(Exception ex) {
            throw new WebScriptException(null, ex);
        }

        for(Entry<String, Object> javaResult : javaResults.entrySet()) {
            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("hashNodeRef", javaResult.getValue());
            resultObject.put("hash", javaResult.getKey());
            jsonResults.put(resultObject);
        }

        result.put("result", jsonResults);
        return result;
    }
}
