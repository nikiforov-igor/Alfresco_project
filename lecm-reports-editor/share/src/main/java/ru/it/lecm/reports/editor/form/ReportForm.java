package ru.it.lecm.reports.editor.form;

import org.alfresco.web.config.forms.*;
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
import ru.it.lecm.base.forms.LecmFormGet;
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
public class ReportForm extends LecmFormGet {

    protected enum NumericTypes {
        d_int,
        d_float,
        d_long,
        d_double
    }

    public static final String TEMPLATE_CODE = "templateCode";
    public static final String TEMPLATES = "TEMPLATES";
    public static final String TEMPLATES_COLUMN_NAME = "Шаблон представления";
    private ReportManagerApi reportManager;

    final protected DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private final static Log logger = LogFactory.getLog(ReportForm.class);

    public void setReportManager(ReportManagerApi reportManager) {
        this.reportManager = reportManager;
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
            ColumnDescriptor templateParam = new ColumnDescriptor(TEMPLATE_CODE);
            templateParam.setAlfrescoType(TEMPLATES);

            L18Value name = new L18Value();
            name.regItem("ru", TEMPLATES_COLUMN_NAME);
            templateParam.setL18Name(name);

            templateParam.setParameterValue(new ParameterTypedValueImpl(ParameterTypedValue.Type.VALUE.getMnemonic()));

            params.add(templateParam);
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

                if (!ParameterTypedValue.Type.RANGE.equals(param.getParameterValue().getType()) && isNumber(param.getAlfrescoType())) {
                    Constraint constraint;
                    try {
                        constraint = generateConstraintModel(field, "LECM_NUMBER");
                        if (constraint != null) {
                            constraints.add(constraint);
                        }
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                }

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
                            m.put("minLength", "" + minLength);
                            m.put("maxLength", "" + maxLength);
                            constraint.setJSONParams(new JSONObject(m));
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
                field.setConfigName(colCode);
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
			    for (int i = 0; i < allowedValues.length; i++)
                                allowedValues[i] = allowedValues[i].trim();
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
                    StringBuilder resultedValueBuilder = new StringBuilder();
                    String strResultedValue = "";
                    if (desc.getFlags().getSupportedNodeTypes() != null) {
                        for (String type : desc.getFlags().getSupportedNodeTypes()) {
                            resultedValueBuilder.append(type).append(",");
                        }
                        strResultedValue = resultedValueBuilder.substring(0, resultedValueBuilder.length() - 1);
                    }

                    control.getParams().put("docType", strResultedValue);
                    control.getParams().put("multiply", String.valueOf(column.getParameterValue().getType().equals(ParameterType.Type.LIST)));
                }

                // прописываем кастомные параметры
                Map<String, String> customParams = column.getControlParams();
                for (String paramKey : customParams.keySet()) {
                    control.getParams().put(paramKey, customParams.get(paramKey));
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

    private ReportDescriptor getReportDescriptor(String reportCode) {
        InputStream xmlStream = null;
        try {
            xmlStream = reportManager.getDsXmlBytes(reportCode);
            return DSXMLProducer.parseDSXML(xmlStream, reportCode);
        } finally {
            IOUtils.closeQuietly(xmlStream);
        }
    }

    private boolean isNumber(String value) {
        return enumHasValue(NumericTypes.class, value != null ? value.replace("d:", "d_") : "not_number");
    }
}
