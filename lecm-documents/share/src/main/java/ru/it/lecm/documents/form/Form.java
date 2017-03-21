package ru.it.lecm.documents.form;

import org.alfresco.web.config.forms.FormConfigElement;
import org.alfresco.web.config.forms.FormField;
import org.alfresco.web.config.forms.Mode;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.connector.*;

import javax.servlet.http.HttpSession;
import java.util.*;

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
        Map<String, Object> model = executeImplFromAdmin(req, status, cache);
	    Map arguments = (Map) ((Map) model.get("form")).get("arguments");

        String args = req.getParameter("args");
        if (args != null) {
            try {
                JSONObject argsObject = new JSONObject(args);
                Iterator<String> it = argsObject.keys();
                while(it.hasNext()) {
                    String key = it.next();
                    String value = argsObject.getString(key);
                    arguments.put(key, value);
                }
            } catch (JSONException e) {
                logger.warn("Cannot parse input arguments");
            }
        }

        String fields = req.getParameter("initFields");
        if (fields != null) {
            try {
                JSONObject argsObject = new JSONObject(fields);
                HashMap<String, String> fieldsValues = new HashMap<String, String>();
                Iterator<String> it = argsObject.keys();
                while(it.hasNext()) {
                    String key = it.next();
                    String value = argsObject.getString(key);
                    fieldsValues.put(key, value);
                }
                Map<String, Field> formFields =  (Map) ((Map) model.get("form")).get("fields");
                for (Field field : formFields.values()) {
                    if (fieldsValues.containsKey(field.getConfigName())) {
                        field.setValue(fieldsValues.get(field.getConfigName()));
                    }
                }
            } catch (JSONException e) {
                logger.warn("Cannot parse input arguments");
            }
        }
	    arguments.put("documentNodeRef", req.getParameter("nodeRef"));
        processInaccessableNode(model, super.executeImpl(req, status, cache));
        return model;
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
        HashSet<String> editableFields = new HashSet<String>();
        boolean hasStatemachine = false;
        try {
            String url = "/lecm/statemachine/statefields?documentNodeRef=" + context.getRequest().getParameter(PARAM_ITEM_ID);
            ConnectorService connService = FrameworkUtil.getConnectorService();
            RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
            String currentUserId = requestContext.getUserId();
            HttpSession currentSession = ServletUtil.getSession(true);
            Connector connector = connService.getConnector(ENDPOINT_ID, currentUserId, currentSession);
            ConnectorContext connectorContext = new ConnectorContext(HttpMethod.GET, null, null);
            connectorContext.setContentType("application/json");

            // call the form service
            Response response = connector.call(url, connectorContext);
            if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
                JSONObject stateFields = new JSONObject(response.getResponse());
                hasStatemachine = stateFields.getBoolean("hasStatemachine");
                if (hasStatemachine) {
                    JSONArray editableFieldsJSON = stateFields.getJSONArray("fields");
                    for (int i = 0; i < editableFieldsJSON.length(); i++) {
                        JSONObject fieldJSON = editableFieldsJSON.getJSONObject(i);
                        if(fieldJSON.getBoolean("editable")) {
                            editableFields.add(fieldJSON.getString("name"));
                        }
                    }
                }
            } else {
                logger.warn("Cannot get editable fields list from server");
            }
        } catch (Exception e) {
            logger.warn("Cannot get editable fields list from server", e);
        }

        HashSet<String> dynamicRoles = new HashSet<String>();
        try {
            String url = "/lecm/documents/dynamicRoles?nodeRef=" + context.getRequest().getParameter(PARAM_ITEM_ID);
            ConnectorService connService = FrameworkUtil.getConnectorService();
            RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
            String currentUserId = requestContext.getUserId();
            HttpSession currentSession = ServletUtil.getSession(true);
            Connector connector = connService.getConnector(ENDPOINT_ID, currentUserId, currentSession);
            ConnectorContext connectorContext = new ConnectorContext(HttpMethod.GET, null, null);
            connectorContext.setContentType("application/json");

            // call the form service
            Response response = connector.call(url, connectorContext);
            if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
                JSONArray roles = new JSONArray(response.getResponse());
                for (int i = 0; i < roles.length(); i++) {
                    String role = roles.getString(i);
                    dynamicRoles.add(role);
                }
            } else {
                logger.warn("Cannot get dynamic roles from server");
            }
        } catch (Exception e) {
            logger.warn("Cannot get dynamic roles from server", e);
        }

        //формируем подсветку для полей
        HashSet<String> highlightedFields = new HashSet<String>();
        if (context.getRequest().getParameter("fields") != null) {
            try {
                JSONArray highlightedFieldsJSON = new JSONArray(context.getRequest().getParameter("fields"));
                for (int i = 0; i < highlightedFieldsJSON.length(); i++) {
                    highlightedFields.add(highlightedFieldsJSON.getString(i));
                }
            } catch (JSONException e) {
                logger.warn("Highlighted fields is wrong format", e);
            }
        }

        String document = null;
        try {
            document = context.getFormDefinition().getJSONObject("data").getString("type");
        } catch (JSONException e) {
            logger.warn("Error while getting document type", e);
        }
        FormConfigElement formConfig = getFormConfig(document, "statemachine-editable-fields");
        JSONArray highlightResultFields = new JSONArray();
        Map<String, Field> fields = context.getFields();
        for (String prop : fields.keySet()) {
            Field field = fields.get(prop);
            if (!field.isDisabled()) {
                if (hasStatemachine) {
                    if (editableFields.contains(field.getConfigName().replace(":", "_"))) {
                        FormField formField = formConfig.getFields().get(field.getConfigName());
                        boolean allowed = true;
                        if (formField != null) {
                            String roles = formField.getAttributes().get("roles");
                            if (roles != null && !"".equals(roles)) {
                                allowed = false;
                                StringTokenizer st = new StringTokenizer(roles, ",");
                                while (st.hasMoreTokens()) {
                                    String role = st.nextToken().trim();
                                    allowed = allowed || dynamicRoles.contains(role);
                                }
                            }
                        }
                        if (allowed) {
                            field.setDisabled(false);
                        } else {
                            field.setDisabled(true);
                        }
                    } else {
                        field.setDisabled(true);
                    }
                }
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

    private void processInaccessableNode(Map<String, Object> fromAdminModel, Map<String, Object> fromUserModel) {
        Map<String, Field> adminFormFields =  (Map) ((Map) fromAdminModel.get("form")).get("fields");
        Map<String, Field> userFormFields =  (Map) ((Map) fromUserModel.get("form")).get("fields");
        for (Field adminField : adminFormFields.values()) {
            for (Field userField : userFormFields.values()) {
                if (Objects.equals(adminField.getConfigName(), userField.getConfigName())
                        && !Objects.equals(adminField.getValue(), userField.getValue())) {
                    adminField.setDisabled(true);
                }
            }
        }
    }

    private Map<String, Object> executeImplFromAdmin(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model;

        String itemKind = getParameter(req, PARAM_ITEM_KIND);
        String itemId = getParameter(req, PARAM_ITEM_ID);

        if (logger.isDebugEnabled()) {
            logger.debug(PARAM_ITEM_KIND + " = " + itemKind);
            logger.debug(PARAM_ITEM_ID + " = " + itemId);
        }

        if (itemKind != null && itemId != null && itemKind.length() > 0 && itemId.length() > 0) {
            model = generateModelFromAdmin(itemKind, itemId, req, status, cache);
        } else {
            // an item kind and id have not been provided so return a model
            // with a 'form' entry but set to null, this prevents FreeMarker
            // adding a default 'form' taglib object to the model.
            model = new HashMap<>(1);
            model.put(MODEL_FORM, null);
        }

        return model;
    }

    private Map<String, Object> generateModelFromAdmin(String itemKind, String itemId,
                                                       WebScriptRequest request, Status status, Cache cache) {
        Map<String, Object> model = null;

        // get mode and optional formId
        String modeParam = getParameter(request, MODEL_MODE, DEFAULT_MODE);
        String formId = getParameter(request, PARAM_FORM_ID);
        Mode mode = Mode.modeFromString(modeParam);

        if (logger.isDebugEnabled()) {
            logger.debug("Showing " + mode + " form (id=" + formId + ") for item: [" + itemKind + "]" + itemId);
        }

        // get the form configuration and list of fields that are visible (if any)
        FormConfigElement formConfig = getFormConfig(itemId, formId);
        List<String> visibleFields = getVisibleFields(mode, formConfig);

        // get the form definition from the form service
        Response formSvcResponse = retrieveFormDefinitionFromAdmin(itemKind, itemId, visibleFields, formConfig);
        if (formSvcResponse.getStatus().getCode() == Status.STATUS_OK) {
            model = generateFormModel(request, mode, formSvcResponse, formConfig);
        } else if (formSvcResponse.getStatus().getCode() == Status.STATUS_UNAUTHORIZED) {
            // set status to 401 and return null model
            status.setCode(Status.STATUS_UNAUTHORIZED);
            status.setRedirect(true);
        } else {
            String errorKey = getParameter(request, PARAM_ERROR_KEY);
            model = generateErrorModel(formSvcResponse, errorKey);
        }

        return model;
    }

    private Response retrieveFormDefinitionFromAdmin(String itemKind, String itemId,
                                                     List<String> visibleFields, FormConfigElement formConfig) {
        Response response = null;

        try {
            // setup the connection
            ConnectorService connService = FrameworkUtil.getConnectorService();
            RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
            String currentUserId = requestContext.getUserId();
            HttpSession currentSession = ServletUtil.getSession(true);
            Connector connector = connService.getConnector(ENDPOINT_ID, currentUserId, currentSession);
            ConnectorContext context = new ConnectorContext(HttpMethod.POST, null, buildDefaultHeaders());
            context.setContentType("application/json");

            // call the form service
            response = connector.call("/api/formdefinitions/admin", context, generateFormDefPostBody(itemKind,
                    itemId, visibleFields, formConfig));

            if (logger.isDebugEnabled()) {
                logger.debug("Response status: " + response.getStatus().getCode());
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled())
                logger.error("Failed to get form definition: ", e);
        }

        return response;
    }

    private static Map<String, String> buildDefaultHeaders() {
        HashMap headers = new HashMap(1, 1.0F);
        headers.put("Accept-Language", I18NUtil.getLocale().toString().replace('_', '-'));
        return headers;
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
