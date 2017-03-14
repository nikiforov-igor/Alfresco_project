package ru.it.lecm.resolutions.processors;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessor;

import java.util.Map;

/**
 * Created by ALoginov on 13.03.2017.
 */
public class FilterByResolutionsCloserProcessor extends SearchQueryProcessor {
    private final static Logger logger = LoggerFactory.getLogger(FilterByResolutionsCloserProcessor.class);

    /*
     * Usage example: {{FILTER_BY_RESOLUTIONS_CLOSER({closers:[#value]})}}
	 */

    @Override
    public String getQuery(Map<String, Object> params) {
        String closer = "";
        StringBuilder sbQuery = new StringBuilder();
        JSONArray closers = params != null ? (JSONArray) params.get("closers") : null;
        if (closers != null) {
            for (int i = 0; i < closers.length(); i++) {
                try {
                    closer = (String) closers.get(i);
                } catch (JSONException e) {
                    logger.error("JSONException:", e);
                }
                sbQuery.append("((@lecm\\-resolutions\\:closers:\"AUTHOR_AND_CONTROLLER\" OR @lecm\\-resolutions\\:closers:\"AUTHOR\") AND @lecm\\-resolutions\\:author\\-assoc\\-ref:\"");
                sbQuery.append(closer);
                sbQuery.append("\") OR ((@lecm\\-resolutions\\:closers:\"AUTHOR_AND_CONTROLLER\" OR @lecm\\-resolutions\\:closers:\"CONTROLLER\") AND @lecm\\-resolutions\\:controller\\-assoc\\-ref:\"");
                sbQuery.append(closer);
                sbQuery.append("\")");
                if (i != closers.length() - 1) {
                    sbQuery.append(" OR ");
                }
            }
        } else {
            sbQuery.append("\"NOT_REF\"");
        }
        return sbQuery.toString();
    }
}
