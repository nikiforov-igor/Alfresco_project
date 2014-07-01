package ru.it.lecm.reports.editor.form;

import org.alfresco.web.config.forms.*;
import org.alfresco.web.scripts.forms.FormUIGet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.StringUtils;
import ru.it.lecm.reports.api.model.ParameterType;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.manager.ReportManagerApi;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.L18Value;
import ru.it.lecm.reports.model.impl.ParameterTypedValueImpl;
import ru.it.lecm.reports.xml.DSXMLProducer;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: dbashmakov
 * Date: 03.07.13
 * Time: 15:28
 */
public class ReportForm extends FormUIGet {
    public static final String TEMPLATE_CODE = "templateCode";
    public static final String TEMPLATES = "TEMPLATES";
    public static final String TEMPLATES_COLUMN_NAME = "Шаблон представления";
    private ReportManagerApi reportManager;

    final protected DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private final static Log logger = LogFactory.getLog(ReportForm.class);

    private enum CustomTypes {
        STATUS,
        TEMPLATES
    }

    private enum RangeableTypes {
        d_int,
        d_float,
        d_long,
        d_date,
        d_datetime,
        d_double
    }

    public void setReportManager(ReportManagerApi reportManager) {
        this.reportManager = reportManager;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        return super.executeImpl(req, status, cache);
    }

    @Override
    protected Map<String, Object> generateModel(String itemKind, String itemId, WebScriptRequest request, Status status, Cache cache) {
        ReportDescriptor descriptor = getReportDescriptor(itemId);

        final HashMap<String, Object> model = new HashMap<String, Object>();
        final HashMap<String, Object> form = new HashMap<String, Object>();
        model.put(MODEL_FORM, form);

        final ArrayList<Constraint> constraints = new ArrayList<Constraint>();
        form.put(MODEL_CONSTRAINTS, constraints);

        final ArrayList<Set> sets = new ArrayList<Set>();
        form.put(MODEL_STRUCTURE, sets);
        Set set = new Set("", "Набор параметров");
        sets.add(set);

        final HashMap<String, Field> fields = new HashMap<String, Field>();
        form.put(MODEL_FIELDS, fields);

        List<ColumnDescriptor> columns = descriptor.getDsDescriptor().getColumns();
        List<ColumnDescriptor> params = new ArrayList<ColumnDescriptor>();
        for (ColumnDescriptor column : columns) {
            ParameterTypedValue typedValue = column.getParameterValue();
            if (typedValue != null) {
                params.add(column);
            }
        }

        Collections.sort(params, new Comparator<ColumnDescriptor>() {
            public int compare(ColumnDescriptor o1, ColumnDescriptor o2) {
                return o1.compareTo(o2);
            }
        });

        if (descriptor.getReportTemplates() != null && descriptor.getReportTemplates().size() > 1) {
            addTemplateParameterColumn(params);
        }

        int colNumber = 0;
        for (ColumnDescriptor param : params) {
            colNumber++;
            Field field = generateFieldModel(param, colNumber, descriptor);
            if (field != null) {
                fields.put(param.getColumnName(), field);
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

        final Map<String, Object> arguments = new HashMap<String, Object>();

        String[] parameters = request.getParameterNames();
        for (String parameter : parameters) {
            arguments.put(parameter, request.getParameter(parameter));
        }

        arguments.put("current-date", DateFormatISO8601.format(new Date()));

        form.put(MODEL_MODE, Mode.CREATE);
        form.put(MODEL_ARGUMENTS, arguments);
        form.put(MODEL_METHOD, "GET");
        form.put(MODEL_ENCTYPE, ENCTYPE_JSON);
        form.put(MODEL_SUBMISSION_URL, "proxy/alfresco/lecm/report/" + descriptor.getMnem() + (params.isEmpty() ? "?autoSubmit=true" : ""));
        form.put(MODEL_SHOW_CAPTION, false);
        form.put(MODEL_SHOW_CANCEL_BUTTON, true);
        form.put(MODEL_SHOW_RESET_BUTTON, false);
        form.put(MODEL_SHOW_SUBMIT_BUTTON, true);
        return model;
    }

    protected Field generateFieldModel(ColumnDescriptor column, int colNum, ReportDescriptor desc) {
        Field field = null;
        try {
            if (column != null) {
                // create the initial field model
                field = new Field();

                // т.к. пустые метки могут уронить диалог, сделаем их всегда заполненными ...
                final String colCode = nonBlank(column.getColumnName(), String.format("Column_%d", colNum));
                final String colCaption = nonBlank(column.getDefault(), colCode);

                field.setId(colCode);
                field.setName(colCode);
                field.setLabel(colCaption);
                field.setDescription(colCaption);

                field.setMandatory(column.getParameterValue().isRequired());
                field.setDataKeyName(column.getColumnName());

                String dataType = column.getAlfrescoType();
                dataType = dataType.startsWith("d:") ? dataType.replace("d:", "") : dataType;
                field.setDataType(dataType);
                field.setValue("");

                processFieldControl(field, column, desc);
            }
        } catch (JSONException je) {
            logger.debug(je.getMessage(), je);
            field = null;
        }

        return field;
    }

    protected void processFieldControl(Field field, ColumnDescriptor column, ReportDescriptor desc) throws JSONException {
        FieldControl control = null;

        DefaultControlsConfigElement defaultControls = null;
        FormsConfigElement formsGlobalConfig = (FormsConfigElement) this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
        if (formsGlobalConfig != null) {
            defaultControls = formsGlobalConfig.getDefaultControls();
        }

        if (defaultControls == null) {
            throw new WebScriptException("Failed to locate default controls configuration");
        }

        String alfrescoType = column.getAlfrescoType();
        if (alfrescoType != null) {
            if (alfrescoType.isEmpty()) {
                return;
            }

            Control defaultControlConfig;
            String[] allowedValues = null;
            if (column.getParameterValue().getType().equals(ParameterType.Type.RANGE)
                    && enumHasValue(RangeableTypes.class, alfrescoType.replaceAll(":", "_"))) {

                String rangedType = alfrescoType.replace(OLD_DATA_TYPE_PREFIX, "").concat("-range");
                defaultControlConfig = defaultControls.getItems().get(rangedType);
            } else { // для списка и значения
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
                String columnExpression = column.getExpression();
                if (columnExpression != null && !columnExpression.isEmpty()) {
                    if (!columnExpression.startsWith("{") && !columnExpression.startsWith("#")) {// не вычисляемое значение, значит либо константа, либо список значений
                        if (!columnExpression.contains(",")) { // константа
                            allowedValues = new String[1];
                            allowedValues[0] = columnExpression;
                        } else {
                            allowedValues = columnExpression.split(",");
                        }
                    }
                }
            }

            if (defaultControlConfig != null) {
                control = new FieldControl(defaultControlConfig.getTemplate());
                List<ControlParam> paramsConfig = defaultControlConfig.getParamsAsList();
                for (ControlParam param : paramsConfig) {
                    control.getParams().put(param.getName(), param.getValue());
                }

                if (alfrescoType.toUpperCase().equals(CustomTypes.STATUS.name())) {
                    String resultedValue = "";
                    if (desc.getFlags().getSupportedNodeTypes() != null) {
                        for (String type : desc.getFlags().getSupportedNodeTypes()) {
                            resultedValue += (type + ",");
                        }
                        resultedValue = resultedValue.substring(0, resultedValue.length() - 1);
                    }

                    control.getParams().put("docType", resultedValue);
                    control.getParams().put("multiply", String.valueOf(column.getParameterValue().getType().equals(ParameterType.Type.LIST)));
                }
                // поддерживается ли множественный выбор? Да, если тип Параметра - Список
                field.setRepeating(column.getParameterValue().getType().equals(ParameterType.Type.LIST));
            }

            field.setControl(control);

            if (allowedValues != null) {
                if (field.isRepeating()) {
                    field.getControl().setTemplate("/ru/it/lecm/base-share/components/controls/selectmany.ftl");
                } else {
                    field.getControl().setTemplate("/ru/it/lecm/base-share/components/controls/selectone.ftl");
                }

                if (!field.getControl().getParams().containsKey(CONTROL_PARAM_OPTIONS)) {
                    List<String> optionsList = new ArrayList<String>(allowedValues.length);
                    Collections.addAll(optionsList, allowedValues);

                    field.getControl().getParams().put(CONTROL_PARAM_OPTIONS,
                            StringUtils.collectionToDelimitedString(optionsList, DELIMITER));
                    field.getControl().getParams().put(CONTROL_PARAM_OPTION_SEPARATOR, DELIMITER);
                }
            }
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

    private ReportDescriptor getReportDescriptor(String reportCode) {
        InputStream xmlStream = null;
        try {
            xmlStream = reportManager.getDsXmlBytes(reportCode);
            return DSXMLProducer.parseDSXML(xmlStream, reportCode);
        } finally {
            IOUtils.closeQuietly(xmlStream);
        }
    }

    private boolean isNotAssoc(String typeKey) {
        return typeKey != null &&
                (typeKey.startsWith("d:") ||
                        enumHasValue(CustomTypes.class, typeKey));
    }

    private boolean enumHasValue(Class enumClass, String name) {
        boolean inEnum = true;
        try {
            Enum.valueOf(enumClass, name);
        } catch (Exception ignored) {
            inEnum = false;
        }
        return inEnum;
    }

    private void addTemplateParameterColumn(List<ColumnDescriptor> params) {
        ColumnDescriptor templateParam = new ColumnDescriptor(TEMPLATE_CODE);
        templateParam.setAlfrescoType(TEMPLATES);

        L18Value name = new L18Value();
        name.regItem("ru", TEMPLATES_COLUMN_NAME);
        templateParam.setL18Name(name);

        templateParam.setParameterValue(new ParameterTypedValueImpl(ParameterTypedValue.Type.VALUE.getMnemonic()));
        params.add(templateParam);
    }

    private String nonBlank(String s, String sDefault) {
        return (s != null && s.trim().length() > 0) ? s : sDefault;
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

        protected FieldControl(String template) {
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

        protected Constraint(String fieldId, String id, String handler, JSONObject params) {
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
        protected FieldPointer(String id) {
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

        protected Set(FormSet setConfig) {
            this.kind = SET;
            this.id = setConfig.getSetId();
            this.appearance = setConfig.getAppearance();
            this.template = setConfig.getTemplate();
            this.label = discoverSetLabel(setConfig);
            this.children = new ArrayList<Element>(4);
        }

        protected Set(String id, String label) {
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
