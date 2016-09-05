/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.webscripts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author rjagudin
 */
public class GetSTSAAddressWebscript extends DeclarativeWebScript {
    private SignedDocflow signedDocflowService;

    public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

    private final static Logger logger = LoggerFactory.getLogger(GetHashesWebscript.class);

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> result = new HashMap<>();
        JSONArray jsonResults = new JSONArray();
        Map<String, Object> resultObject = new HashMap<>();
        resultObject.put("sTSAAddress", signedDocflowService.getSTSAAddress());
        jsonResults.put(resultObject);
        result.put("result", jsonResults);
        return result;
    }
}
