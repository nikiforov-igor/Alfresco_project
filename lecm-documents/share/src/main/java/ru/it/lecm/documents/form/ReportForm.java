package ru.it.lecm.documents.form;

import org.alfresco.web.config.forms.FormSet;
import org.alfresco.web.config.forms.Mode;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 21.06.13
 * Time: 13:46
 */
public class ReportForm extends FormUIGet {

    private static ScriptRemote scriptRemote;
    private final static Log logger = LogFactory.getLog(ReportForm.class);

    public void setScriptRemote(ScriptRemote scriptRemote) {
        ReportForm.scriptRemote = scriptRemote;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        return super.executeImpl(req, status, cache);
    }

    @Override
    protected Map<String, Object> generateModel(String itemKind, String itemId, WebScriptRequest request, Status status, Cache cache) {
        //Map<String, Object> superModel = super.generateModel(itemKind, itemId, request, status, cache);
        HashMap<String, Object> model = new HashMap<String, Object>();
        HashMap<String, Object> form = new HashMap<String, Object>();
        model.put(MODEL_FORM, form);

        form.put(MODEL_MODE, Mode.CREATE);
        form.put(MODEL_METHOD, "post");
        form.put(MODEL_ENCTYPE, ENCTYPE_JSON);
        form.put(MODEL_SUBMISSION_URL, "sendto");
        form.put(MODEL_ARGUMENTS, "");
        form.put(MODEL_DATA, new HashMap<Object, Object>());
        form.put(MODEL_SHOW_CAPTION, true);
        form.put(MODEL_SHOW_CANCEL_BUTTON, false);
        form.put(MODEL_SHOW_RESET_BUTTON, true);
        form.put(MODEL_SHOW_SUBMIT_BUTTON, true);
        form.put(MODEL_CONSTRAINTS, new ArrayList<Object>());

        ArrayList<Set> sets = new ArrayList<Set>();
        form.put(MODEL_STRUCTURE, sets);
        Set set = new Set("", "По умолчанию");
        FieldPointer fieldPointer = new FieldPointer("test_prop");
        set.addChild(fieldPointer);
        sets.add(set);


        //fields
        HashMap<String, Field> fields = new HashMap<String, Field>();
        form.put(MODEL_FIELDS, fields);
        Field field = new Field();
        field.setId("test_prop");
        field.setName("test_prop");
        field.setLabel("Тестовое поле");
        field.setDescription("Описание поля");
        FieldControl control = new FieldControl("/org/alfresco/components/form/controls/textfield.ftl");
        field.setControl(control);
        field.setDataKeyName("test_prop");
        field.setDataType("text");
        field.setType("property");
        field.setValue("test_value");
        fields.put("test_prop", field);

        return model;
    }

    public class Field extends Element
    {
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

        Field()
        {
            this.kind = FIELD;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getConfigName()
        {
            return this.configName;
        }

        public void setConfigName(String configName)
        {
            this.configName = configName;
        }

        public FieldControl getControl()
        {
            return this.control;
        }

        public void setControl(FieldControl control)
        {
            this.control = control;
        }

        public String getDataKeyName()
        {
            return this.dataKeyName;
        }

        public void setDataKeyName(String dataKeyName)
        {
            this.dataKeyName = dataKeyName;
        }

        public String getDataType()
        {
            return this.dataType;
        }

        public void setDataType(String dataType)
        {
            this.dataType = dataType;
        }

        public String getDescription()
        {
            return this.description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public boolean isDisabled()
        {
            return this.disabled;
        }

        public void setDisabled(boolean disabled)
        {
            this.disabled = disabled;
        }

        public String getLabel()
        {
            return this.label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public boolean isMandatory()
        {
            return this.mandatory;
        }

        public void setMandatory(boolean mandatory)
        {
            this.mandatory = mandatory;
        }

        public boolean isTransitory()
        {
            return this.transitory;
        }

        public void setTransitory(boolean transitory)
        {
            this.transitory = transitory;
        }

        public String getName()
        {
            return this.name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public boolean isRepeating()
        {
            return this.repeating;
        }

        public void setRepeating(boolean repeating)
        {
            this.repeating = repeating;
        }

        public String getType()
        {
            return this.type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public Object getValue()
        {
            return this.value;
        }

        public void setValue(Object value)
        {
            this.value = value;
        }

        public String getContent()
        {
            return this.content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public String getHelp()
        {
            return this.help;
        }

        public void setHelp(String help)
        {
            this.help = help;
        }

        public String getEndpointDirection()
        {
            return this.endpointDirection;
        }

        public void setEndpointDirection(String endpointDirection)
        {
            this.endpointDirection = endpointDirection;
        }

        public String getEndpointType()
        {
            return getDataType();
        }

        public boolean isEndpointMandatory()
        {
            return this.mandatory;
        }

        public boolean isEndpointMany()
        {
            return this.repeating;
        }

        @Override
        public String toString()
        {
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
    public class FieldControl
    {
        protected String template;
        protected Map<String, String> params;

        FieldControl(String template)
        {
            this.template = template;
            this.params = new HashMap<String, String>(4);
        }

        public String getTemplate()
        {
            return this.template;
        }

        public void setTemplate(String template)
        {
            this.template = template;
        }

        public Map<String, String> getParams()
        {
            return this.params;
        }

        @Override
        public String toString()
        {
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
    public class Constraint
    {
        private String fieldId;
        private String id;
        private String validationHandler;
        private JSONObject params;
        private String message;
        private String event;

        Constraint(String fieldId, String id, String handler, JSONObject params)
        {
            this.fieldId = fieldId;
            this.id = id;
            this.validationHandler = handler;
            this.params = params;
        }

        public String getFieldId()
        {
            return this.fieldId;
        }

        public String getId()
        {
            return this.id;
        }

        public String getValidationHandler()
        {
            return this.validationHandler;
        }

        public void setValidationHandler(String validationHandler)
        {
            this.validationHandler = validationHandler;
        }

        /**
         * Returns the parameters formatted as a JSON string.
         *
         * @return
         */
        public String getParams()
        {
            if (this.params == null)
            {
                this.params = new JSONObject();
            }

            return this.params.toString();
        }

        public JSONObject getJSONParams()
        {
            return this.params;
        }

        public void setJSONParams(JSONObject params)
        {
            this.params = params;
        }

        public String getMessage()
        {
            return this.message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        public String getEvent()
        {
            return this.event;
        }

        public void setEvent(String event)
        {
            this.event = event;
        }

        @Override
        public String toString()
        {
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

    public abstract class Element
    {
        protected String kind;
        protected String id;

        public String getKind()
        {
            return this.kind;
        }

        public String getId()
        {
            return this.id;
        }

        @Override
        public String toString()
        {
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
    public class FieldPointer extends Element
    {
        FieldPointer(String id)
        {
            this.kind = FIELD;
            this.id = id;
        }
    }

    /**
     * Represents a set of fields and/or nested sets.
     */
    public class Set extends Element
    {
        protected String appearance;
        protected String template;
        protected String label;
        protected List<Element> children;

        Set(FormSet setConfig)
        {
            this.kind = SET;
            this.id = setConfig.getSetId();
            this.appearance = setConfig.getAppearance();
            this.template = setConfig.getTemplate();
            this.label = discoverSetLabel(setConfig);
            this.children = new ArrayList<Element>(4);
        }

        Set(String id, String label)
        {
            this.kind = SET;
            this.id = id;
            this.label = label;
            this.children = new ArrayList<Element>(1);
        }

        public void addChild(Element child)
        {
            this.children.add(child);
        }

        public String getAppearance()
        {
            return this.appearance;
        }

        public String getTemplate()
        {
            return this.template;
        }

        public String getLabel()
        {
            return this.label;
        }

        public List<Element> getChildren()
        {
            return this.children;
        }

        @Override
        public String toString()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append(this.kind);
            buffer.append("(id=").append(this.id);
            buffer.append(" appearance=").append(this.appearance);
            buffer.append(" label=").append(this.label);
            buffer.append(" template=").append(this.template);
            buffer.append(" children=[");
            boolean first = true;
            for (Element child : this.children)
            {
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
