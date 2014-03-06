package ru.it.lecm.actions.form;

import org.alfresco.web.config.forms.*;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

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
public class ScriptForm extends FormUIGet {

    private static ScriptRemote scriptRemote;
    private final static Log logger = LogFactory.getLog(ScriptForm.class);

    private enum AlfrescoTypes {
        d_text,
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

        form.put(MODEL_CONSTRAINTS, new ArrayList<Object>());

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
                    FieldDescriptor descriptor = new FieldDescriptor(name, id, type, value);
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

    static String nonBlank(String s, String sDefault) {
    	return (s != null && s.trim().length() > 0) ? s : sDefault;
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
                field.setLabel(colCaption);
                field.setDescription(colCaption);
                field.setValue(column.getValue());

                field.setDataKeyName(column.getId());

                String dataType = column.getType();
                dataType = dataType.startsWith("d:") ? dataType.replace("d:", "") : dataType;
                field.setDataType(dataType);

                processFieldControl(field, column);
            }
        } catch (JSONException je) {
            field = null;
        }

        return field;
    }

    protected void processFieldControl(Field field, FieldDescriptor descriptor) throws JSONException {
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
            String[] allowedValues = null;

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
            }

            field.setControl(control);
        }
    }

    private boolean isNotAssoc(String typeKey) {
        return typeKey != null && enumHasValue(AlfrescoTypes.class, typeKey.replaceAll(":", "_"));
    }

    static public boolean enumHasValue(Class enumClass, String name) {
        boolean inEnum = false;
        try {
            inEnum = Enum.valueOf(enumClass, name) != null;
        } catch (Exception ex){
        }
        return inEnum;
    }

    public class FieldDescriptor {

        private String name;
        private String id;
        private String type;
        private String value;

        public FieldDescriptor(String name, String id, String type, String value) {
            this.name = name;
            this.id = id;
            this.type = type;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    public class Field extends Element {
        protected String name;
        protected String configName;
        protected String label;
        protected String description;
        protected String help;
        protected FieldControl control;
        protected String dataKeyName;
        protected String dataType;
        protected String type;
        protected String content;
        protected String endpointDirection;
        protected Object value;
        protected boolean disabled = false;
        protected boolean mandatory = false;
        protected boolean transitory = false;
        protected boolean repeating = false;

        Field() {
            this.kind = FIELD;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getConfigName() {
            return this.configName;
        }

        public void setConfigName(String configName) {
            this.configName = configName;
        }

        public FieldControl getControl() {
            return this.control;
        }

        public void setControl(FieldControl control) {
            this.control = control;
        }

        public String getDataKeyName() {
            return this.dataKeyName;
        }

        public void setDataKeyName(String dataKeyName) {
            this.dataKeyName = dataKeyName;
        }

        public String getDataType() {
            return this.dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public String getLabel() {
            return this.label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public boolean isMandatory() {
            return this.mandatory;
        }

        public void setMandatory(boolean mandatory) {
            this.mandatory = mandatory;
        }

        public boolean isTransitory() {
            return this.transitory;
        }

        public void setTransitory(boolean transitory) {
            this.transitory = transitory;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isRepeating() {
            return this.repeating;
        }

        public void setRepeating(boolean repeating) {
            this.repeating = repeating;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getHelp() {
            return this.help;
        }

        public void setHelp(String help) {
            this.help = help;
        }

        public String getEndpointDirection() {
            return this.endpointDirection;
        }

        public void setEndpointDirection(String endpointDirection) {
            this.endpointDirection = endpointDirection;
        }

        public String getEndpointType() {
            return getDataType();
        }

        public boolean isEndpointMandatory() {
            return this.mandatory;
        }

        public boolean isEndpointMany() {
            return this.repeating;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(this.kind);
            buffer.append("(id=").append(this.id);
            buffer.append(" name=").append(this.name);
            buffer.append(" configName=").append(this.configName);
            buffer.append(" type=").append(this.type);
            buffer.append(" value=").append(this.value);
            buffer.append(" label=").append(this.label);
            buffer.append(" description=").append(this.description);
            buffer.append(" help=").append(this.help);
            buffer.append(" dataKeyName=").append(this.dataKeyName);
            buffer.append(" dataType=").append(this.dataType);
            buffer.append(" endpointDirection=").append(this.endpointDirection);
            buffer.append(" disabled=").append(this.disabled);
            buffer.append(" mandatory=").append(this.mandatory);
            buffer.append(" repeating=").append(this.repeating);
            buffer.append(" transitory=").append(this.transitory);
            buffer.append(" ").append(this.control);
            buffer.append(")");
            return buffer.toString();
        }
    }

    /**
     * Represents the control used by a form field.
     */
    public class FieldControl {
        protected String template;
        protected Map<String, String> params;

        FieldControl(String template) {
            this.template = template;
            this.params = new HashMap<String, String>(4);
        }

        public String getTemplate() {
            return this.template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public Map<String, String> getParams() {
            return this.params;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("control(template=").append(this.template);
            buffer.append(" params=").append(this.params);
            buffer.append(")");
            return buffer.toString();
        }
    }

    /**
     * Represents a field constraint.
     */
    public class Constraint {
        private String fieldId;
        private String id;
        private String validationHandler;
        private JSONObject params;
        private String message;
        private String event;

        Constraint(String fieldId, String id, String handler, JSONObject params) {
            this.fieldId = fieldId;
            this.id = id;
            this.validationHandler = handler;
            this.params = params;
        }

        public String getFieldId() {
            return this.fieldId;
        }

        public String getId() {
            return this.id;
        }

        public String getValidationHandler() {
            return this.validationHandler;
        }

        public void setValidationHandler(String validationHandler) {
            this.validationHandler = validationHandler;
        }

        /**
         * Returns the parameters formatted as a JSON string.
         *
         * @return
         */
        public String getParams() {
            if (this.params == null) {
                this.params = new JSONObject();
            }

            return this.params.toString();
        }

        public JSONObject getJSONParams() {
            return this.params;
        }

        public void setJSONParams(JSONObject params) {
            this.params = params;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getEvent() {
            return this.event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("constraint(fieldId=").append(this.fieldId);
            buffer.append(" id=").append(this.id);
            buffer.append(" validationHandler=").append(this.validationHandler);
            buffer.append(" event=").append(this.event);
            buffer.append(" message=").append(this.message);
            buffer.append(")");
            return buffer.toString();
        }
    }

    public abstract class Element {
        protected String kind;
        protected String id;

        public String getKind() {
            return this.kind;
        }

        public String getId() {
            return this.id;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.kind);
            builder.append("(");
            builder.append(this.id);
            builder.append(")");
            return builder.toString();
        }
    }

    /**
     * Represents a pointer to a field, used in the form UI model.
     */
    public class FieldPointer extends Element {
        FieldPointer(String id) {
            this.kind = FIELD;
            this.id = id;
        }
    }

    /**
     * Represents a set of fields and/or nested sets.
     */
    public class Set extends Element {
        protected String appearance;
        protected String template;
        protected String label;
        protected List<Element> children;

        Set(FormSet setConfig) {
            this.kind = SET;
            this.id = setConfig.getSetId();
            this.appearance = setConfig.getAppearance();
            this.template = setConfig.getTemplate();
            this.label = discoverSetLabel(setConfig);
            this.children = new ArrayList<Element>(4);
        }

        Set(String id, String label) {
            this.kind = SET;
            this.id = id;
            this.label = label;
            this.children = new ArrayList<Element>(1);
        }

        public void addChild(Element child) {
            this.children.add(child);
        }

        public String getAppearance() {
            return this.appearance;
        }

        public String getTemplate() {
            return this.template;
        }

        public String getLabel() {
            return this.label;
        }

        public List<Element> getChildren() {
            return this.children;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(this.kind);
            buffer.append("(id=").append(this.id);
            buffer.append(" appearance=").append(this.appearance);
            buffer.append(" label=").append(this.label);
            buffer.append(" template=").append(this.template);
            buffer.append(" children=[");
            boolean first = true;
            for (Element child : this.children) {
                if (first)
                    first = false;
                else
                    buffer.append(", ");

                buffer.append(child);
            }
            buffer.append("])");
            return buffer.toString();
        }
    }
}
