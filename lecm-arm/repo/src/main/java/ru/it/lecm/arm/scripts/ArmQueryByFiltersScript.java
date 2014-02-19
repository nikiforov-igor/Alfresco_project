package ru.it.lecm.arm.scripts;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * User: dbashmakov
 * Date: 19.02.14
 * Time: 11:23
 */
public class ArmQueryByFiltersScript extends AbstractWebScript {
    final private static Logger logger = LoggerFactory.getLogger(ArmTreeMenuScript.class);

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

        JSONObject filtersMeta = new JSONObject();
        try {
            filtersMeta.put("query", "");
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(filtersMeta.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
