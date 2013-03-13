package ru.it.lecm.documents.form;

import org.alfresco.web.config.forms.Mode;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pmelnikov
 * Date: 13.03.13
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class Form extends FormUIGet {

    private static ScriptRemote scriptRemote;

    public void setScriptRemote(ScriptRemote scriptRemote) {
        Form.scriptRemote = scriptRemote;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        return super.executeImpl(req, status, cache);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void processFields(ModelContext context, Map<String, Object> formUIModel) {
        List<String> visibleFields = getVisibleFields(context.getMode(), context.getFormConfig());
        if (context.getFormConfig() != null && visibleFields != null && visibleFields.size() > 0) {
            processVisibleFields(context);
        } else {
            processServerFields(context);
        }
        //дополнительная обработка
        Mode mode = context.getMode();
        if (!mode.equals(Mode.CREATE)) { // ничего не выполняем при первом сохранении объекта
            updateFilelds(context);
        }
        formUIModel.put(MODEL_FIELDS, context.getFields());
        formUIModel.put(MODEL_STRUCTURE, context.getStructure());
        formUIModel.put(MODEL_CONSTRAINTS, context.getConstraints());
    }

    private void updateFilelds(ModelContext context) {
        String url = "/lecm/statemachine/statefields?documentNodeRef=" + context.getRequest().getParameter(PARAM_ITEM_ID);
        Response response = scriptRemote.connect("alfresco").get(url);
        HashSet<String> editableFields = new HashSet<String>();
        try {
            if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
                JSONArray editableFieldsJSON = new JSONArray(response.getResponse());
                for (int i = 0; i < editableFieldsJSON.length(); i++) {
                    JSONObject fieldJSON = editableFieldsJSON.getJSONObject(i);
                    if(fieldJSON.getBoolean("editable")) {
                        editableFields.add(fieldJSON.getString("name"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Map<String, Field> fields = context.getFields();
        HashSet<String> fieldsForRemove = new HashSet<String>();
        for (String prop : fields.keySet()) {
            Field field = fields.get(prop);
            if (editableFields.contains(field.getConfigName())) {
                field.setDisabled(false);
            } else {
                field.setDisabled(true);
            }
        }
    }

}
