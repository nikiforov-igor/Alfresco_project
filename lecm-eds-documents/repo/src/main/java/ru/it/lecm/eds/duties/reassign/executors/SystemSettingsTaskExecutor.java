package ru.it.lecm.eds.duties.reassign.executors;

import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import ru.it.lecm.duties.reassign.executors.TaskExecutor;

import java.util.List;

/**
 * $Author:  AElkin
 * 19.02.2018 13:54
 */
public class SystemSettingsTaskExecutor extends TaskExecutor {

    @Override
    public boolean execute(NodeRef nodeRef, JSONObject jsonObject) {
        return false;
    }

    @Override
    public List<JSONObject> calculateElements(NodeRef nodeRef) {
        return null;
    }

    @Override
    public List<JSONObject> calculateElements(JSONObject jsonObject) {
        return null;
    }

    @Override
    protected String getSuccessLogTemplate(JSONObject jsonObject) throws JSONException {
        return null;
    }

    @Override
    protected String getFailureLogTemplate(JSONObject jsonObject) throws JSONException {
        return null;
    }
}
