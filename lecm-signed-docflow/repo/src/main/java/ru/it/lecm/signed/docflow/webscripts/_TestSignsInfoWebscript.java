package ru.it.lecm.signed.docflow.webscripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dgonchar
 * Date: 02.08.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class _TestSignsInfoWebscript extends DeclarativeWebScript {
    private SignedDocflow signedDocflowService;

    public void setSignedDocflowService(SignedDocflow signedDocflowService) {
        this.signedDocflowService = signedDocflowService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> result = new HashMap<String, Object>();

        String contentToSignRef = req.getParameter("contentToSignRef");
        signedDocflowService.generateTestSigns(new NodeRef(contentToSignRef));

        JSONObject json = new JSONObject();
        try {
            json.put("ok", true);
        } catch (JSONException ex) {
            throw new WebScriptException("Error forming JSONObject", ex);
        }
        result.put("result", json);

        return result;
    }
}
