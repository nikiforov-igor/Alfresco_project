package ru.it.lecm.contracts;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.base.beans.SubstitudeBean;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * User: mshafeev
 * Date: 22.05.13
 * Time: 11:21
 */
public class AssociationSubstituteString extends AbstractWebScript{

    private SubstitudeBean substituteService;

    public void setSubstituteService(SubstitudeBean substituteService) {
        this.substituteService = substituteService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        JSONObject wf = new JSONObject();

        final String nodeRef = req.getParameter("nodeRef");
        String formatString = req.getParameter("nameSubstituteString");

        if (formatString == null || formatString.equals("")) {
            formatString = "{cm:name}";
        }

        try {
            final String finalFormatString = formatString;
            final AuthenticationUtil.RunAsWork<String> substitudeString = new AuthenticationUtil.RunAsWork<String>() {
                @Override
                public String doWork() throws Exception {
                    if (nodeRef.equals("")) {
                        return "";
                    }
                    NodeRef node = new NodeRef(nodeRef);
                    return substituteService.formatNodeTitle(node, finalFormatString);
                }
            };
            wf.put("substituteString", AuthenticationUtil.runAsSystem(substitudeString));
            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(wf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
