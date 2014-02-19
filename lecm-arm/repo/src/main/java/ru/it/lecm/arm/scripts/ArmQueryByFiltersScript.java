package ru.it.lecm.arm.scripts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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

        Content c = req.getContent();
        if (c == null) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Missing POST body.");
        }

        StringBuilder builder = new StringBuilder();
        try {
            JSONObject jsonFiltersObj = new JSONObject(c.getContent());
            String filtersStr = (String) jsonFiltersObj.get("filters");
            JSONArray filtersArray = new JSONArray(filtersStr);
            for (int i = 0; i < filtersArray.length(); i++) {
                JSONObject filter = filtersArray.getJSONObject(i);
            }
            filtersMeta.put("query", builder.toString());
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
