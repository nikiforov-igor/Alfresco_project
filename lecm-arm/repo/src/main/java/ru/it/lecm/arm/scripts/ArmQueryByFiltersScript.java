package ru.it.lecm.arm.scripts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;
import ru.it.lecm.arm.beans.filters.ArmDocumenstFilter;
import ru.it.lecm.arm.filters.BaseQueryArmFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    if (filterId != null) {
                        ArmDocumenstFilter filterBean = null;
                        try {
                            filterBean = (ArmDocumenstFilter) getApplicationContext().getBean(filterId);
                        } catch (BeansException ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                        if (filterBean != null) {
                            List<String> values = new ArrayList<String>();

                            if (filterBean instanceof BaseQueryArmFilter) {
                                values.add(filter.has("query") ? (String) filter.get("query") : null);
                            }

                            if (Boolean.valueOf(String.valueOf(filter.get("multiple"))))  {
                                Object curValues = filter.get("curValue");
                                if (curValues instanceof JSONArray) {
                                    JSONArray currentValueArray = (JSONArray) filter.get("curValue");
                                    for (int j = 0; j < currentValueArray.length(); j++) {
                                        String v = (String) currentValueArray.get(j);
                                        values.add(v);
                                    }
                                } else {
                                    values.addAll(Arrays.asList(((String) curValues).split(",")));
                                }

                            } else {
                                String currentValueStr = (String) filter.get("curValue");
                                values.addAll(Arrays.asList(currentValueStr.split(",")));
                            }

                            String query = filterBean.getQuery(armNode, values);
                            if (!query.isEmpty()) {
                                builder.append("(").append(query).append(")").append(" AND");
                            }
                        }
                    } /*else {
                        BaseQueryArmFilter filterBean = (BaseQueryArmFilter) getApplicationContext().getBean("baseQueryArmFilter");
                        String currentValueStr = (String) filter.get("curValue");

                        List<String> values = new ArrayList<String>();
                        values.add(filter.has("query") ? (String) filter.get("query") : null);
                        values.addAll(Arrays.asList(currentValueStr.split(",")));

                        String query = filterBean.getQuery(armNode, values);
                        if (!query.isEmpty()) {
                            builder.append("(").append(query).append(")").append(" AND");
                        }
                    }*/
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
