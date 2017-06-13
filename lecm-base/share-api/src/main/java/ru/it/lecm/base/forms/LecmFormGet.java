package ru.it.lecm.base.forms;

import org.alfresco.web.config.forms.*;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptException;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 29.04.2015
 * Time: 16:50
 */
public abstract class LecmFormGet extends FormUIGet {
    private final static Log logger = LogFactory.getLog(LecmFormGet.class);

    protected final String CONTROL_LECM_SELECT_MANY = "/ru/it/lecm/base-share/components/controls/selectmany.ftl";
    protected final String CONTROL_LECM_SELECT_ONE = "/ru/it/lecm/base-share/components/controls/selectone.ftl";

    protected enum CustomTypes {
        STATUS,
        TEMPLATES,
        REPORT_PREFERENCES
    }

    protected enum RangeableTypes {
        d_int,
        d_float,
        d_long,
        d_date,
        d_datetime,
        d_double
    }

    protected enum NumericTypes {
        d_int,
        d_float,
        d_long,
        d_double
    }

    protected boolean isNumber(String value) {
        return enumHasValue(NumericTypes.class, value != null ? value.replace("d:", "d_") : "not_number");
    }

    protected boolean isNotAssoc(String typeKey) {
        return typeKey != null &&
                (typeKey.startsWith("d:") ||
                        enumHasValue(CustomTypes.class, typeKey));
    }

    protected boolean enumHasValue(Class enumClass, String name) {
        boolean inEnum = true;
        try {
            Enum.valueOf(enumClass, name);
        } catch (Exception ignored) {
            inEnum = false;
        }
        return inEnum;
    }

    protected String nonBlank(String s, String sDefault) {
        return (s != null && s.trim().length() > 0) ? s : sDefault;
    }

    /**
     * Получить контрол по умолчанию из share-config по его типу
     */
    protected Control getDefaultControlFromConfig(DefaultControlsConfigElement defaultControls, String alfrescoType) {
        Control defaultControlConfig;
        boolean isPropertyField = isNotAssoc(alfrescoType);
        if (isPropertyField) {
            defaultControlConfig = defaultControls.getItems().get(alfrescoType);
            if (defaultControlConfig == null) { // попытка получить дефолтный контрол по старой альфресовской схеме
                defaultControlConfig = defaultControls.getItems().get(alfrescoType.replace(OLD_DATA_TYPE_PREFIX, ""));
                if (defaultControlConfig == null) {
                    defaultControlConfig = defaultControls.getItems().get(DEFAULT_FIELD_TYPE);
                }
            }
        } else {
            defaultControlConfig = defaultControls.getItems().get(ASSOCIATION + ":" + alfrescoType);
            if (defaultControlConfig == null) {
                defaultControlConfig = defaultControls.getItems().get(ASSOCIATION);
            }
        }
        return defaultControlConfig;
    }

    /**
     * Получить контрол по шаблону и параметрам
     */
    protected FieldControl generateControlModel(String template, String alfrescoType, JSONObject paramsObj) {
        JSONArray paramsArray = new JSONArray();
        if (paramsObj != null) {
            // прописываем кастомные параметры
            try {
                Iterator keys = paramsObj.keys();
                while (keys.hasNext()) {
                    String next = (String) keys.next();
                    Object value = paramsObj.get(next);

                    JSONObject param = new JSONObject();
                    param.put("name", next);
                    param.put("value", value);
                    paramsArray.put(param);
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return generateControlModel(template, alfrescoType, paramsArray);
    }

    protected FieldControl generateControlModel(final String template, final String alfrescoType, final JSONArray paramsArray) {
        FieldControl control = null;

        if (template != null && template.length() > 0) {
            control = new FieldControl(template);
        } else if (alfrescoType != null && !alfrescoType.isEmpty()) {
            DefaultControlsConfigElement defaultControls = null;
            FormsConfigElement formsGlobalConfig = (FormsConfigElement) this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
            if (formsGlobalConfig != null) {
                defaultControls = formsGlobalConfig.getDefaultControls();
            }
            if (defaultControls == null) {
                throw new WebScriptException("Failed to locate default controls configuration");
            }

            Control defaultControlConfig = getDefaultControlFromConfig(defaultControls, alfrescoType);
            if (defaultControlConfig != null) {
                control = new FieldControl(defaultControlConfig.getTemplate());
                List<ControlParam> paramsConfig = defaultControlConfig.getParamsAsList();
                for (ControlParam param : paramsConfig) {
                    control.getParams().put(param.getName(), param.getValue());
                }
            }
        }
        // прописываем кастомные параметры
        if (control != null && paramsArray != null) {
            try {
                for (int i = 0; i < paramsArray.length(); i++) {
                    JSONObject parameter = paramsArray.getJSONObject(i);
                    control.getParams().put(parameter.getString("name"), parameter.getString("value"));
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return control;
    }
    /**
     * Обновить список констрейнтов (@param constraints) на основании данных из поля (@param field)
     */
    protected void updateConstraints(ArrayList<Constraint> constraints, Field field) {
        /*MANDATORY*/
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

        /*LENGTH*/
        if (field.getControl() != null && field.getControl().getParams() != null) {
            Map<String, String> parameters = field.getControl().getParams();
            if (parameters.containsKey("constraintLENGTH")) {
                int minLength = -1;
                int maxLength = -1;
                try {
                    if (parameters.containsKey("constraintLENGTH_minLength")) {
                        minLength = Integer.valueOf(parameters.get("constraintLENGTH_minLength"));
                    }
                    if (parameters.containsKey("constraintLENGTH_maxLength")) {
                        maxLength = Integer.valueOf(parameters.get("constraintLENGTH_maxLength"));
                    }
                } catch (Exception ex) {
                    logger.error( ex.getMessage(), ex);
                }

                Constraint constraint;
                try {
                    constraint = generateConstraintModel(field, CONSTRAINT_LENGTH);
                    if (constraint != null) {
                        Map<String, String> m = new HashMap<>();
                        m.put("minLength", Integer.toString(minLength));
                        m.put("maxLength", Integer.toString(maxLength));
                        constraint.setJSONParams(new JSONObject(m));
                        constraints.add(constraint);
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Получить констрейнт по id (@param constraintId)
     */
    protected Constraint generateConstraintModel(Field field, String constraintId) throws JSONException {
        Constraint constraint = null;

        // retrieve the default constraints configuration
        ConstraintHandlersConfigElement defaultConstraintHandlers = null;
        FormsConfigElement formsGlobalConfig = (FormsConfigElement) this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
        if (formsGlobalConfig != null) {
            defaultConstraintHandlers = formsGlobalConfig.getConstraintHandlers();
        }

        if (defaultConstraintHandlers == null) {
            throw new WebScriptException("Failed to locate default constraint handlers configurarion");
        }

        // get the default handler for the constraint
        ConstraintHandlerDefinition defaultConstraintConfig = defaultConstraintHandlers.getItems().get(constraintId);
        if (defaultConstraintConfig != null) {
            // generate and process the constraint model
            // get the validation handler from the config
            String validationHandler = defaultConstraintConfig.getValidationHandler();

            constraint = new Constraint(field.getId(), constraintId, validationHandler, new JSONObject());

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
        }

        return constraint;
    }

    /**
     * Represents the control used by a form field.
     */
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

        public Field() {
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

    public class FieldControl {
        protected String template;
        protected Map<String, String> params;

        public FieldControl(String template) {
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
            return "control(template=" + this.template + " params=" + this.params + ")";
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

        public Constraint(String fieldId, String id, String handler, JSONObject params) {
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

    /**
     * Represents a pointer to a field, used in the form UI model.
     */
    public class FieldPointer extends Element {
        public FieldPointer(String id) {
            this.kind = FIELD;
            this.id = id;
        }
    }

    public class FieldDescriptor {

        private String name;
        private String id;
        private String type;
        private String value;
        private boolean mandatory;
        private JSONObject control;

        public FieldDescriptor(String name, String id, String type, String value, boolean mandatory) {
            this.name = name;
            this.id = id;
            this.type = type;
            this.value = value;
            this.mandatory = mandatory;
        }
        public FieldDescriptor(String name, String id, String type, String value, boolean mandatory, JSONObject control) {
            this.name = name;
            this.id = id;
            this.type = type;
            this.value = value;
            this.mandatory = mandatory;
            this.control = control;
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

        public boolean isMandatory() {
            return mandatory;
        }

        public JSONObject getControl() {
            return control;
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

        public Set(FormSet setConfig) {
            this.kind = SET;
            this.id = setConfig.getSetId();
            this.appearance = setConfig.getAppearance();
            this.template = setConfig.getTemplate();
            this.label = discoverSetLabel(setConfig);
            this.children = new ArrayList<Element>(4);
        }

        public Set(String id, String label) {
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
