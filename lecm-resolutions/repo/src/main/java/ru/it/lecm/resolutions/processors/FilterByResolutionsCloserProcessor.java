package ru.it.lecm.resolutions.processors;

import org.json.JSONArray;
import org.json.JSONException;
import ru.it.lecm.base.beans.SearchQueryProcessor;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ALoginov on 13.03.2017.
 */
public class FilterByResolutionsCloserProcessor extends SearchQueryProcessor {

    /*
     * Usage example: {{FILTER_BY_RESOLUTIONS_CLOSER({signers:[#value]})}}
	 */

    @Override
    public String getQuery(Map<String, Object> params){
        String closer="";
        StringBuilder sbQuery = new StringBuilder();
        JSONArray signers = params != null ? (JSONArray)params.get("signers") : null;
        if (signers != null) {
            for (int i=0;i<signers.length();i++) {
                try {
                    closer=(String) signers.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sbQuery.append("((@lecm\\-resolutions\\:closers:\"AUTHOR_AND_CONTROLLER\" OR @lecm\\-resolutions\\:closers:\"AUTHOR\") " +
                        "AND @lecm\\-resolutions\\:author\\-assoc\\-ref:\"");
                sbQuery.append(closer);
                sbQuery.append("\") OR ((@lecm\\-resolutions\\:closers:\"AUTHOR_AND_CONTROLLER\" OR @lecm\\-resolutions\\:closers:\"CONTROLLER\") " +
                        "AND @lecm\\-resolutions\\:controller\\-assoc\\-ref:\"");
                sbQuery.append(closer);
                sbQuery.append("\") OR ");
            }
        } else {
            sbQuery.append("\"NOT_REF\"");
        }
        sbQuery.delete(sbQuery.length() - 3, sbQuery.length());
        return sbQuery.toString();
    }
}
