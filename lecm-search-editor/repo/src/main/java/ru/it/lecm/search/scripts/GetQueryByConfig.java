package ru.it.lecm.search.scripts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.search.beans.SearchEditorService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: DBashmakov
 * Date: 07.05.2015
 * Time: 14:06
 */
public class GetQueryByConfig extends DeclarativeWebScript {

    private SearchEditorService searchEditorService;

    private final static Log logger = LogFactory.getLog(GetQueryByConfig.class);

    public void setSearchEditorService(SearchEditorService searchEditorService) {
        this.searchEditorService = searchEditorService;
    }

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();
        String config = req.getParameter("config");
        if (config == null || config.length() == 0){
            config = "{}";
        }
        try {
            JSONObject configObj = new JSONObject(config);
            model.put("query", searchEditorService.buildQuery(configObj));
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return model;
    }
}
