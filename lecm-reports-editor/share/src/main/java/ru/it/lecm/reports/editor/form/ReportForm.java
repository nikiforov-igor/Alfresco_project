package ru.it.lecm.reports.editor.form;

import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.web.config.forms.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
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

import javax.servlet.http.HttpSession;
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
    public static final String SAVED_PREFERENCES = "savedPreferences";
    public static final String TEMPLATES = "TEMPLATES";
    public static final String REPORT_PREFERENCES = "REPORT_PREFERENCES";
    public static final String TEMPLATES_COLUMN_NAME = "Шаблон представления";
    private ReportManagerApi reportManager;
    private ConnectorService connectorService;

    protected final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    protected final String CURRENT_DATE_ARG = "current-date";

    private final static Log logger = LogFactory.getLog(ReportForm.class);

    public void setReportManager(ReportManagerApi reportManager) {
        this.reportManager = reportManager;
    }

    public void setConnectorService(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    @Override
    protected Map<String, Object> generateModel(String itemKind, String itemId, WebScriptRequest request, Status status, Cache cache) {
        ReportDescriptor descriptor = getReportDescriptor(itemId);

        final HashMap<String, Object> model = new HashMap<>();
        final HashMap<String, Object> form = new HashMap<>();
        model.put(MODEL_FORM, form);

        final ArrayList<Constraint> constraints = new ArrayList<>();
        form.put(MODEL_CONSTRAINTS, constraints);

        final ArrayList<Set> sets = new ArrayList<>();
        form.put(MODEL_STRUCTURE, sets);
        Set set = new Set("", "Набор параметров");
        sets.add(set);

        final HashMap<String, Field> fields = new HashMap<>();
        form.put(MODEL_FIELDS, fields);

        List<ColumnDescriptor> columns = descriptor.getDsDescriptor().getColumns();
        List<ColumnDescriptor> params = new ArrayList<>();
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

        final Map<String, Object> arguments = new HashMap<>();

        if (!params.isEmpty()) {
            JSONArray preferences = getUserPreferencesForReport(itemId);

            List<JSONObject> jsonValues = new ArrayList<>();
            for (int i = 0; i < preferences.length(); i++) {
                try {
                    jsonValues.add(preferences.getJSONObject(i));
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                final String KEY_CREATED = "created";

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    Date valA = null;
                    Date valB = null;

                    try {
                        valA = ISO8601DateFormat.parse(String.valueOf(a.get(KEY_CREATED)));
                        valB = ISO8601DateFormat.parse(String.valueOf(b.get(KEY_CREATED)));
                    } catch (JSONException e) {
                        //do something
                    }
                    if (valA == null && valB != null) {
                        return 1;
                    } else if (valA != null && valB == null) {
                        return -1;
                    } else if (valA != null) {
                        return -valA.compareTo(valB);
                    }
                    return 0;
                }
            });

            String lastPreferenceName = null;
            if (!jsonValues.isEmpty()) {
                try {
                    JSONObject savedArgs = jsonValues.get(0).getJSONObject("args");
                    lastPreferenceName = jsonValues.get(0).getString("name");

                    Iterator keys = savedArgs.keys();
                    while (keys.hasNext()) {
                        String next = (String) keys.next();

                        Object value = savedArgs.get(next);
                        arguments.put(next, value);
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            ColumnDescriptor templateParam = new ColumnDescriptor(SAVED_PREFERENCES);
            templateParam.setAlfrescoType(REPORT_PREFERENCES);

            L18Value name = new L18Value();
            name.regItem("ru", retrieveMessage("report.param.preferences.label"));
            templateParam.setL18Name(name);

            templateParam.setParameterValue(new ParameterTypedValueImpl(ParameterTypedValue.Type.VALUE.getMnemonic()));

            Field field = generateFieldModel(templateParam, 0, descriptor);
            if (field != null) {
                field.getControl().getParams().put("preferencesValue", preferences.toString());
                if (lastPreferenceName != null) {
                    field.getControl().getParams().put("currentValue", lastPreferenceName);
                }
                fields.put(templateParam.getColumnName(), field);
                FieldPointer fieldPointer = new FieldPointer(field.getId());
                set.addChild(fieldPointer);
            }
        }

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



        String[] parameters = request.getParameterNames();
        for (String parameter : parameters) {
            arguments.put(parameter, request.getParameter(parameter));
        }

        String args = request.getParameter("args");
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
        arguments.put(CURRENT_DATE_ARG, DateFormatISO8601.format(new Date()));

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

    private JSONArray getUserPreferencesForReport(String reportCode) {
        final String url = "/lecm/user-settings/get?key=reports." + reportCode + ".saved-preferences";
        try {
            RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
            String currentUserId = requestContext.getUserId();
            HttpSession currentSession = ServletUtil.getSession(true);
            Connector connector = connectorService.getConnector("alfresco", currentUserId, currentSession);
            Response response = connector.call(url);
            if (ResponseStatus.STATUS_OK == response.getStatus().getCode()) {
                JSONObject json = new JSONObject(response.getResponse());
                return new JSONArray(json.getString("value"));
            } else {
                logger.error("Cannot get response for " + url);
            }
        } catch (ConnectorServiceException ex) {
            logger.error("Cannot get connector for " + url, ex);
        } catch (JSONException ex) {
            logger.error("Cannot parse json response for " + url, ex);
        }

        return new JSONArray();
    }
}
