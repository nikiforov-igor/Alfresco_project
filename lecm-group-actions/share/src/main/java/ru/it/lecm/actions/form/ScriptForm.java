package ru.it.lecm.actions.form;

import org.alfresco.web.config.forms.*;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import ru.it.lecm.base.forms.LecmFormGet;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 03.07.13
 * Time: 15:28
 */
public class ScriptForm extends LecmFormGet {

    protected enum AlfrescoTypes {
        d_text,
        d_mltext,
        d_int,
        d_float,
        d_long,
        d_boolean,
        d_date,
        d_datetime,
        d_any,
        d_double,
        STATUS,
        TEMPLATES
    }

    private static ScriptRemote scriptRemote;
    private final static Log logger = LogFactory.getLog(ScriptForm.class);

    public void setScriptRemote(ScriptRemote scriptRemote) {
        ScriptForm.scriptRemote = scriptRemote;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        return super.executeImpl(req, status, cache);
    }

    @Override
    protected Map<String, Object> generateModel(String itemKind, String itemId, WebScriptRequest request, Status status, Cache cache) {
        HashMap<String, Object> model = new HashMap<String, Object>();
        HashMap<String, Object> form = new HashMap<String, Object>();
        model.put(MODEL_FORM, form);

        ArrayList<Object> constraints = new ArrayList<Object>();
        form.put(MODEL_CONSTRAINTS, constraints);

        ArrayList<Set> sets = new ArrayList<Set>();
        form.put(MODEL_STRUCTURE, sets);
        Set set = new Set("", "Набор параметров");
        sets.add(set);

        HashMap<String, Object> fields = new HashMap<String, Object>();
        form.put(MODEL_FIELDS, fields);

        String actionId = request.getParameter("itemId");
        List<FieldDescriptor> descriptors = new ArrayList<FieldDescriptor>();
        try {
            String url = "/lecm/groupActions/fields?actionId=" + URLEncoder.encode(actionId, "UTF-8");
            Response response = scriptRemote.connect("alfresco").get(url);
            if (response.getStatus().getCode() == ResponseStatus.STATUS_OK) {
                JSONArray jsonFields = new JSONArray(response.getResponse());
                for (int i = 0; i < jsonFields.length(); i++) {
                    JSONObject jsonField = jsonFields.getJSONObject(i);
                    String name = jsonField.getString("name");
                    String id = jsonField.getString("id");
                    String type = jsonField.getString("type");
                    String value = jsonField.getString("value");
                    boolean mandatory = jsonField.getBoolean("mandatory");
                    if (value != null) {
                        value = URLDecoder.decode(value);
                    }
                    FieldDescriptor descriptor = new FieldDescriptor(name, id, type, value, mandatory);
                    descriptors.add(descriptor);
                }

            } else {
                logger.warn("Cannot get fields list from server");
            }
        } catch (Exception e) {
            logger.warn("Cannot get fields list from server", e);
        }

        int colnum = 0;
        for (FieldDescriptor descriptor : descriptors) {
            colnum++;
            Field field = generateFieldModel(descriptor, colnum);
            if (field != null) {
                fields.put(descriptor.getId(), field);
                FieldPointer fieldPointer = new FieldPointer(field.getId());
                set.addChild(fieldPointer);
                if (field.isMandatory()) {
                    Constraint constraint;
                    try {
                        constraint = generateConstraintModel(field, CONSTRAINT_MANDATORY);
                        if (constraint != null) {
                            constraints.add(constraint);
                        }
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }
        }

        FormUIGet.Field actionIdField = generateTransientFieldModel("actionId", "/org/alfresco/components/form/controls/hidden.ftl");
        actionIdField.setValue(actionId);
        actionIdField.setType("property");
        actionIdField.setDataType("text");
        actionIdField.setDataKeyName(actionIdField.getId());
        fields.put(actionIdField.getId(), actionIdField);
        FieldPointer fieldPointer = new FieldPointer(actionIdField.getId());
        set.addChild(fieldPointer);

        FormUIGet.Field itemsField = generateTransientFieldModel("items", "/org/alfresco/components/form/controls/hidden.ftl");
        itemsField.setValue(request.getParameter("items"));
        itemsField.setType("property");
        itemsField.setDataType("text");
        itemsField.setDataKeyName(itemsField.getId());
        fields.put(itemsField.getId(), itemsField);
        fieldPointer = new FieldPointer(itemsField.getId());
        set.addChild(fieldPointer);

        Map<String, Object> arguments = new HashMap<String, Object>();
        String[] parameters = request.getParameterNames();
        for (String parameter : parameters) {
            arguments.put(parameter, request.getParameter(parameter));
        }

        form.put(MODEL_ARGUMENTS, arguments);

        form.put(MODEL_MODE, Mode.CREATE);
        form.put(MODEL_METHOD, "GET");
        form.put(MODEL_ENCTYPE, ENCTYPE_JSON);
        form.put(MODEL_SUBMISSION_URL, "proxy/alfresco/lecm/groupActions/exec");
        form.put(MODEL_SHOW_CAPTION, false);
        form.put(MODEL_SHOW_CANCEL_BUTTON, true);
        form.put(MODEL_SHOW_RESET_BUTTON, false);
        form.put(MODEL_SHOW_SUBMIT_BUTTON, true);
        return model;
    }

    protected Field generateFieldModel(FieldDescriptor column, int colnum) {
        Field field = null;
        try {
            if (column != null) {
                // create the initial field model
                field = new Field();

                // т.к. пустые метки могут уронить диалог, сделаем их всегда заполненными ...
                final String colMnem = nonBlank(column.getId(), String.format("Column_%d", colnum) );
                final String colCaption = nonBlank(column.getName(), colMnem);

                field.setId(colMnem);
                field.setName(colMnem);
                field.setConfigName(colMnem);
                field.setLabel(colCaption);
                field.setDescription(colCaption);
                field.setMandatory(column.isMandatory());

                String fieldValue = column.getValue();

                Map<String, String> paramsMap = new HashMap<String, String>();

                if (!fieldValue.contains("=")) {
                    field.setValue(column.getValue());
                } else {
                    paramsMap = getQueryMap(fieldValue);
                }

                field.setDataKeyName(column.getId());

                String dataType = column.getType();
                dataType = dataType.startsWith("d:") ? dataType.replace("d:", "") : dataType;
                field.setDataType(dataType);

                processFieldControl(field, column, paramsMap);
            }
        } catch (JSONException je) {
            field = null;
        }

        return field;
    }

    protected void processFieldControl(Field field, FieldDescriptor descriptor, Map<String,String> controlParams) throws JSONException {
        FieldControl control = null;

        DefaultControlsConfigElement defaultControls = null;
        FormsConfigElement formsGlobalConfig =
                (FormsConfigElement) this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
        if (formsGlobalConfig != null) {
            defaultControls = formsGlobalConfig.getDefaultControls();
        }

        if (defaultControls == null) {
            throw new WebScriptException("Failed to locate default controls configuration");
        }

        String alfrescoType = descriptor.getType();
        if (alfrescoType != null) {
            if (alfrescoType.isEmpty()) {
                return;
            }

            Control defaultControlConfig;

            boolean isPropertyField = isNotAssoc(alfrescoType);
            if (isPropertyField) {
                field.setType(PROPERTY);

                defaultControlConfig = defaultControls.getItems().get(alfrescoType);
                if (defaultControlConfig == null) { // попытка получить дефолтный контрол по старой альфресовской схеме
                    defaultControlConfig = defaultControls.getItems().get(alfrescoType.replace(OLD_DATA_TYPE_PREFIX, ""));
                    if (defaultControlConfig == null) {
                        defaultControlConfig = defaultControls.getItems().get(DEFAULT_FIELD_TYPE);
                    }
                }
            } else {
                field.setType(ASSOCIATION);
                field.setEndpointDirection("TARGET");

                defaultControlConfig = defaultControls.getItems().get(ASSOCIATION + ":" + alfrescoType);
                if (defaultControlConfig == null) {
                    defaultControlConfig = defaultControls.getItems().get(ASSOCIATION);
                }
            }

            if (defaultControlConfig != null) {
                control = new FieldControl(defaultControlConfig.getTemplate());
                List<ControlParam> paramsConfig = defaultControlConfig.getParamsAsList();
                for (ControlParam param : paramsConfig) {
                    control.getParams().put(param.getName(), param.getValue());
                }

                if (controlParams != null && !controlParams.isEmpty())  {
                    for (String paramKey : controlParams.keySet()) {
                        control.getParams().put(paramKey, controlParams.get(paramKey));
                    }
                }
            }

            field.setControl(control);
        }
    }

    protected Constraint generateConstraintModel(Field field, String constraintId) throws JSONException {
        Constraint constraint = null;

        // retrieve the default constraints configuration
        ConstraintHandlersConfigElement defaultConstraintHandlers = null;
        FormsConfigElement formsGlobalConfig =
                (FormsConfigElement) this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
        if (formsGlobalConfig != null) {
            defaultConstraintHandlers = formsGlobalConfig.getConstraintHandlers();
        }

        if (defaultConstraintHandlers == null) {
            throw new WebScriptException("Failed to locate default constraint handlers configurarion");
        }

        // get the default handler for the constraint
        ConstraintHandlerDefinition defaultConstraintConfig =
                defaultConstraintHandlers.getItems().get(constraintId);

        if (defaultConstraintConfig != null) {
            // generate and process the constraint model
            constraint = generateConstraintModel(field, constraintId, new JSONObject(), defaultConstraintConfig);
        }

        return constraint;
    }

    private Constraint generateConstraintModel(Field field, String constraintId, JSONObject constraintParams, ConstraintHandlerDefinition defaultConstraintConfig) {
        // get the validation handler from the config
        String validationHandler = defaultConstraintConfig.getValidationHandler();

        Constraint constraint = new Constraint(field.getId(), constraintId, validationHandler, constraintParams);

        if (defaultConstraintConfig.getEvent() != null) {
            constraint.setEvent(defaultConstraintConfig.getEvent());
        } else {
            constraint.setEvent(DEFAULT_CONSTRAINT_EVENT);
        }

        // look for an overridden message in the field's constraint config,
        // if none found look in the default constraint config
        String constraintMsg = null;
        if (defaultConstraintConfig.getMessageId() != null) {
            constraintMsg = retrieveMessage(defaultConstraintConfig.getMessageId());
        } else if (defaultConstraintConfig.getMessage() != null) {
            constraintMsg = defaultConstraintConfig.getMessage();
        }
        if (constraintMsg == null) {
            constraintMsg = retrieveMessage(validationHandler + ".message");
        }

        // add the message if there is one
        if (constraintMsg != null) {
            constraint.setMessage(constraintMsg);
        }

        return constraint;
    }

    @Override
    protected boolean isNotAssoc(String typeKey) {
        return typeKey != null && enumHasValue(AlfrescoTypes.class, typeKey.replaceAll(":", "_"));
    }

    private Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<String, String>();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
        }
        return map;
    }

}
