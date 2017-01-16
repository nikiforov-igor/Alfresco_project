package ru.it.lecm;

import org.alfresco.web.config.forms.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.base.forms.LecmFormGet;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 29.04.2015
 * Time: 13:06
 */
public class ControlForm extends LecmFormGet {
    protected static final String PARAM_FIELD_ID = "fieldId";
    protected static final String PARAM_LABEL_ID = "labelId";
    protected static final String PARAM_TEMPLATE = "template";
    protected static final String PARAM_PARAMS = "params";
    protected static final String PARAM_TYPE = "type";

    private final static Log logger = LogFactory.getLog(ControlForm.class);
    public static final String DEFAULT_VALUE = "defaultValue";

    private ConfigService configService;

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();

        final HashMap<String, Object> form = new HashMap<>();
        model.put(MODEL_FORM, form);

        form.put(MODEL_CONSTRAINTS, new ArrayList<>());

        final HashMap<String, Field> fields = new HashMap<>();
        final List<Field> fieldsSet = new ArrayList<>();
        form.put(MODEL_FIELDS, fields);
        form.put(MODEL_STRUCTURE, fieldsSet);

        String fieldId = getParameter(req, PARAM_FIELD_ID, null);
        String labelId = getParameter(req, PARAM_LABEL_ID, null);
        String template = getParameter(req, PARAM_TEMPLATE, null);
        String parameters = getParameter(req, PARAM_PARAMS, null);
        String alfrescoType = getParameter(req, PARAM_TYPE, null);

        JSONObject paramsObj = null;
        if (parameters != null) {
            // прописываем кастомные параметры
            try {
                paramsObj = new JSONObject(parameters);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        Field field = generateFieldModel(fieldId, labelId, alfrescoType);
        if (alfrescoType != null || template != null) {
            FieldControl control = generateControlModel(template, alfrescoType, paramsObj);
            if (control != null) {
                field.setControl(control);
            }
            if (paramsObj != null && paramsObj.has(DEFAULT_VALUE)) {
                try {
                    field.setValue(paramsObj.get(DEFAULT_VALUE));
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        if (field != null) {
            fields.put(field.getName(), field);
            fieldsSet.add(field);
        }

        final Map<String, Object> arguments = new HashMap<>();

        String[] pars = req.getParameterNames();
        for (String parameter : pars) {
            arguments.put(parameter, req.getParameter(parameter));
        }
        arguments.put(PARAM_ITEM_KIND, "type");
        arguments.put(PARAM_ITEM_ID, "control");

        form.put(MODEL_MODE, Mode.CREATE);
        form.put(MODEL_ARGUMENTS, arguments);
        form.put(MODEL_METHOD, "GET");
        form.put(MODEL_ENCTYPE, "application/json");
        form.put(MODEL_SUBMISSION_URL, "#");
        form.put(MODEL_SHOW_CAPTION, false);
        form.put(MODEL_SHOW_CANCEL_BUTTON, false);
        form.put(MODEL_SHOW_RESET_BUTTON, false);
        form.put(MODEL_SHOW_SUBMIT_BUTTON, false);

        return model;
    }

    protected FieldControl generateControlModel(String template, String alfrescoType, JSONObject paramsObj) {
        FieldControl control = null;

        DefaultControlsConfigElement defaultControls = null;
        FormsConfigElement formsGlobalConfig = (FormsConfigElement) this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
        if (formsGlobalConfig != null) {
            defaultControls = formsGlobalConfig.getDefaultControls();
        }
        if (defaultControls == null) {
            throw new WebScriptException("Failed to locate default controls configuration");
        }

        if (template != null && template.length() > 0) {
            control = new FieldControl(template);
        } else if (alfrescoType != null && alfrescoType.length() > 0) {
            Control defaultControlConfig = getDefaultControlFromConfig(defaultControls, alfrescoType);
            if (defaultControlConfig != null) {
                control = new FieldControl(defaultControlConfig.getTemplate());
                List<ControlParam> paramsConfig = defaultControlConfig.getParamsAsList();
                for (ControlParam param : paramsConfig) {
                    control.getParams().put(param.getName(), param.getValue());
                }
            }
        }
        if (control != null && paramsObj != null) {
            // прописываем кастомные параметры
            try {
                Iterator keys = paramsObj.keys();
                while (keys.hasNext()) {
                    String next = (String) keys.next();
                    Object value = paramsObj.get(next);
                    control.getParams().put(next, value.toString());
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return control;
    }

    protected Field generateFieldModel(String fieldId, String labelId, String dataType) {
        Field field;
        // create the initial field model
        field = new Field();

        field.setId(fieldId);
        field.setName(fieldId);
        field.setConfigName(fieldId);
        field.setLabel(labelId != null ? retrieveMessage(labelId) : fieldId);

        field.setMandatory(false);
        field.setDataKeyName(fieldId);
        if (dataType != null) {
            dataType = dataType.startsWith("d:") ? dataType.replace("d:", "") : dataType;
            field.setDataType(dataType);
        }
        field.setValue("");

        return field;
    }
}
