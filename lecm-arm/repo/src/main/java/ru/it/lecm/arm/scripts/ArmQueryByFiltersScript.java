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
    public static final String CLASS = "class";
    public static final String QUERY = "query";
    public static final String CUR_VALUE = "curValue";
    public static final String MULTIPLE = "multiple";
    public static final String FILTERS = "filters";
    public static final String ARM_NODE = "armNode";
    public static final String BASE_QUERY_ARM_FILTER = "baseQueryArmFilter";

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

            String filtersStr = (String) (jsonFiltersObj.has(FILTERS) ? jsonFiltersObj.get(FILTERS) : "");
            String nodeStr = (String) (jsonFiltersObj.has(ARM_NODE) ? jsonFiltersObj.get(ARM_NODE) : "");

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
                    String filterId = (String) filter.get(CLASS);

                    ArmDocumenstFilter filterBean = null;
                    List<String> values = new ArrayList<String>();

                    if (filterId != null) {
                        try {
                            filterBean = (ArmDocumenstFilter) getApplicationContext().getBean(filterId);
                        } catch (BeansException ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    } else { // не задано - берем по умолчанию
                        filterBean = (BaseQueryArmFilter) getApplicationContext().getBean(BASE_QUERY_ARM_FILTER);
                    }

                    if (filterBean != null) {
                        if (Boolean.valueOf(String.valueOf(filter.get(MULTIPLE)))) {
                            Object curValues = filter.get(CUR_VALUE);
                            if (curValues instanceof JSONArray) {
                                JSONArray currentValueArray = (JSONArray) filter.get(CUR_VALUE);
                                for (int j = 0; j < currentValueArray.length(); j++) {
                                    String v = (String) currentValueArray.get(j);
                                    values.add(v);
                                }
                            } else {
                                values.addAll(Arrays.asList(((String) curValues).split(",")));
                            }

                        } else {
                            String currentValueStr = (String) filter.get(CUR_VALUE);
                            values.addAll(Arrays.asList(currentValueStr.split(",")));
                        }

	                    String params = filter.has(QUERY) ? (String) filter.get(QUERY) : null;

                        String query = filterBean.getQuery(armNode, params, values);
                        if (!query.isEmpty()) {
                            builder.append("(").append(query).append(")").append(" AND");
                        }
                    }
                }

                if (builder.length() > 0) {
                    builder.delete(builder.length() - 4, builder.length());
                }
            }
            filtersMeta.put(QUERY, builder.toString());
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
