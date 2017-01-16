package ru.it.lecm.reports.editor.form;

import org.alfresco.web.config.forms.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
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

                updateConstraints(constraints, field);

                /*специфика для Редактора Отчетов*/
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
            if (column.getParameterValue().getType().equals(ParameterType.Type.RANGE)
                    && enumHasValue(RangeableTypes.class, alfrescoType.replaceAll(":", "_"))) {
                field.setType(PROPERTY);
                String rangedType = alfrescoType.replace(OLD_DATA_TYPE_PREFIX, "").concat("-range");
                defaultControlConfig = defaultControls.getItems().get(rangedType);
            } else { // для списка и значения
                boolean isPropertyField = isNotAssoc(alfrescoType);
                if (isPropertyField) {
                    field.setType(PROPERTY);
                } else {
                    field.setType(ASSOCIATION);
                    field.setEndpointDirection("TARGET");
                }
                defaultControlConfig = getDefaultControlFromConfig(defaultControls, alfrescoType);
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

            /*Подменяем контрол на selectone/selectmany - если требуется*/
            String allowedValuesStr = null;

            String columnExpression = column.getExpression();
            if (columnExpression != null && !columnExpression.isEmpty()
                    && !columnExpression.startsWith("{") && !columnExpression.startsWith("#")) {
                // не вычисляемое значение - либо константа, либо список значений
                allowedValuesStr = columnExpression;
            } else if (field.getControl().getParams().containsKey(CONTROL_PARAM_OPTIONS)) {
                allowedValuesStr = field.getControl().getParams().get(CONTROL_PARAM_OPTIONS);
            }

            if (allowedValuesStr != null && !allowedValuesStr.isEmpty()) {
                if (field.isRepeating()) {
                    field.getControl().setTemplate(CONTROL_LECM_SELECT_MANY);
                } else {
                    field.getControl().setTemplate(CONTROL_LECM_SELECT_ONE);
                }

                String[] allowedValues = allowedValuesStr.split(",");
                for (int i = 0; i < allowedValues.length; i++) {
                    allowedValues[i] = allowedValues[i].trim().replace("\n", "");
                }
                List<String> optionsList = new ArrayList<>(allowedValues.length);
                Collections.addAll(optionsList, allowedValues);

                field.getControl().getParams().put(CONTROL_PARAM_OPTIONS, StringUtils.collectionToDelimitedString(optionsList, DELIMITER));
                field.getControl().getParams().put(CONTROL_PARAM_OPTION_SEPARATOR, DELIMITER);
            }
        }
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
}
