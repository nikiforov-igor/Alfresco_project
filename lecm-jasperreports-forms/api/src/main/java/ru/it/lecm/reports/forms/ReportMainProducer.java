package ru.it.lecm.reports.forms;

import org.alfresco.util.PropertyCheck;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.utils.Utils;

import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: AZinovin
 * Date: 05.09.12
 * Time: 16:25
 * <p/>
 * Веб скрипт для формирования печатных форм
 * точка входа - /lecm/report/{report} см. /alfresco/templates/webscripts/ru/it/lecm/forms/jasper/form.get.desc.xml
 * Параметры:
 * <code>report</code> - путь к файлу отчета относительно <code>classes/reportdefinitions</code> без расширения <code>.jasper</code>
 * в файле отчета должно быть задано свойство <code><property name="dataSource" value="ru.it.lecm.forms.jasperforms.AbstractDataSourceProvider"/></code>
 * значение - полное имя класса провайдера источника данных, который будет использоваться для генерации отчета
 */
public class ReportMainProducer extends AbstractWebScript {
    public static final String FLAG_EXEC = "1";

    final static String PARAM_EXEC = "exec";
    final static String CONTENT_TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=UTF-8";
    private final static String JSON_PARAMETERS_KEY = "json_form_parameters";
    private static final transient Logger logger = LoggerFactory.getLogger(ReportMainProducer.class);

    private ReportsManager reportsManager;

    public ReportsManager getReportsManager() {
        return reportsManager;
    }

    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }

    /**
     * Формирование карты параметров webScriptRequest в виде:
     * ключ - название параметра,
     * значение - его строковое значение.
     *
     * @param webScriptRequest WebScriptRequest
     * @return  Map
     */
    public static Map<String, String> getRequestParameters(WebScriptRequest webScriptRequest) {
        final Map<String, String> result = new HashMap<String, String>();
        //Get json params
        String json = webScriptRequest.getParameter(JSON_PARAMETERS_KEY);
        if (json != null) {
            try {
                JSONObject jsonParameters = new JSONObject(json);
                Iterator<String> keys = jsonParameters.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonParameters.getString(key);
                    result.put(key, value);
                }
            } catch (JSONException e) {
                logger.error("Error while parsing JSON parameters", e);
            }
        }

        //Get query parameters
        for (String paramName : webScriptRequest.getParameterNames()) {
            if (!JSON_PARAMETERS_KEY.equals(paramName)) {
                String value = webScriptRequest.getParameter(paramName);
                result.put(paramName, value);
            }
        }
        return result;
    }

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());
        PropertyCheck.mandatory(this, "reportGenerators", getReportsManager().getReportGenerators());

        final Map<String, String> requestParameters = getRequestParameters(webScriptRequest);
        final Map<String, String> templateParams = webScriptRequest.getServiceMatch().getTemplateVars();
        final String reportName = Utils.coalesce(templateParams.get("report"), templateParams.get("reportCode"));
        final String templateCode = requestParameters.get("templateCode");

        final ReportFileData result = getReportsManager().generateReport(reportName, templateCode, requestParameters);

        if (result != null) {
            webScriptResponse.setContentType(result.getMimeType());
            webScriptResponse.setContentEncoding(result.getEncoding());
            webScriptResponse.addHeader("Content-Disposition", "filename=\"" + MimeUtility.encodeWord(result.getFilename(), "utf-8", "Q") + "\"");

            if (result.getData() != null) {
                final OutputStream out = webScriptResponse.getOutputStream();
                out.write(result.getData());
                out.flush();
                out.close();
            }
        }
    }
}