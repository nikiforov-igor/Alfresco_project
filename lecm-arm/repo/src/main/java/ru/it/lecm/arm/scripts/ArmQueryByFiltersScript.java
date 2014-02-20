package ru.it.lecm.arm.scripts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.arm.beans.filters.DocumentFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * User: dbashmakov
 * Date: 19.02.14
 * Time: 11:23
 */
public class ArmQueryByFiltersScript extends AbstractWebScript implements ApplicationContextAware {
    final private static Logger logger = LoggerFactory.getLogger(ArmTreeMenuScript.class);

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        ctx = appContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

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
            String nodeStr = (String) jsonFiltersObj.get("armNode");

            JSONArray filtersArray = null;
            if (filtersStr != null && !filtersStr.isEmpty()) {
                filtersArray = new JSONArray(filtersStr);
            }

            JSONObject armNode = null;
            if (nodeStr != null && !nodeStr.isEmpty()) {
                armNode = new JSONObject(nodeStr);
            }

            if (filtersArray != null) {
                for (int i = 0; i < filtersArray.length(); i++) {
                    JSONObject filter = filtersArray.getJSONObject(i);
                    String filterId = (String) filter.get("code");
                    DocumentFilter filterBean = (DocumentFilter) getApplicationContext().getBean(filterId);
                    if (filterBean == null) {
                        throw new Exception("Cannot find bean with ID = " + filterId);
                    }
                    String currentValueStr = (String) filter.get("curValue");

                    String query = filterBean.getQuery(armNode, Arrays.asList(currentValueStr.split(",")));
                    if (!query.isEmpty()) {
                        builder.append("(").append(query).append(")").append(" AND");
                    }
                }
                if (builder.length() > 0) {
                    builder.delete(builder.length() - 4, builder.length());
                }
            }
            filtersMeta.put("query", builder.toString());
        } catch (Exception e) {
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
