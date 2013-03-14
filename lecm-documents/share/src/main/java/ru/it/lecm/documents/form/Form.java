package ru.it.lecm.documents.form;

import org.alfresco.web.config.forms.Mode;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * User: pmelnikov
 * Date: 13.03.13
 * Time: 12:57
 */
public class Form extends FormUIGet {

    private static ScriptRemote scriptRemote;
    private final static Log logger = LogFactory.getLog(Form.class);

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
        //Отключаем поля в форме, которые не доступны на данном шаге
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
            } else {
                logger.warn("Cannot get editable fields list from server");
            }
        } catch (JSONException e) {
            logger.warn("Cannot get editable fields list from server", e);
        }

        //формируем подсветку для полей
        HashSet<String> highlightedFields = new HashSet<String>();
        try {
            JSONArray highlightedFieldsJSON = new JSONArray(context.getRequest().getParameter("fields"));
            for (int i = 0; i < highlightedFieldsJSON.length(); i++) {
                highlightedFields.add(highlightedFieldsJSON.getString(i));
            }
        } catch (JSONException e) {
            logger.warn("Highlighted fields is wrong format", e);
        }

        JSONArray highlightResultFields = new JSONArray();
        Map<String, Field> fields = context.getFields();
        for (String prop : fields.keySet()) {
            Field field = fields.get(prop);
            if (editableFields.contains(field.getConfigName())) {
                field.setDisabled(false);
            } else {
                field.setDisabled(true);
            }
            if (highlightedFields.contains(field.getConfigName())) {
                highlightResultFields.put(field.getId());
            }
        }

        Field highlight = generateTransientFieldModel("lecm-document:highlight", "/ru/it/lecm/documents/form/controls/highlight.ftl");
        highlight.setValue(highlightResultFields.toString());
        highlight.setType("property");
        highlight.setDataType("text");
        highlight.setDataKeyName(highlight.getId());
        context.getFields().put(highlight.getId(), highlight);
        FieldPointer pointer = new FieldPointer(highlight.getId());
        context.getStructure().add(pointer);
    }

    public class FieldPointer extends Element
    {
        FieldPointer(String id)
        {
            this.kind = FIELD;
            this.id = id;
        }
    }

}
