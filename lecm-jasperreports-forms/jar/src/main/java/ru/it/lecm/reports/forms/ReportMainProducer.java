package ru.it.lecm.reports.forms;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
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

    static final transient Logger log = LoggerFactory.getLogger(ReportMainProducer.class);

    final static String PARAM_EXEC = "exec";
    final static String CONTENT_TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=UTF-8";

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
        for (String paramName : webScriptRequest.getParameterNames()) {
            String value = webScriptRequest.getParameter(paramName);
            result.put(paramName, value);
        }
        return result;
    }

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        // проверка надо ли выполнять запрос или только сформировать в ответ URL ...
        if (!FLAG_EXEC.equals(webScriptRequest.getParameter(PARAM_EXEC))) {
            prepareExecURL(webScriptRequest, webScriptResponse);
            return;
        }

        PropertyCheck.mandatory(this, "reportsManager", getReportsManager());
        PropertyCheck.mandatory(this, "reportGenerators", getReportsManager().getReportGenerators());

        final Map<String, String> templateParams = webScriptRequest.getServiceMatch().getTemplateVars();
        final String reportName = Utils.coalesce(templateParams.get("report"), templateParams.get("reportCode"));
        final String templateCode = webScriptRequest.getParameter("templateCode");
        final Map<String, String> requestParameters = getRequestParameters(webScriptRequest);

		/* Вариант "права побоку": построение от имени системы */
        //TODO дыра в системе. По какой-то причине было сделано, чтобы документы попадали в отчет без проверки прав
        final ReportFileData result = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<ReportFileData>() {
            @Override
            public ReportFileData doWork() throws Exception {
                return getReportsManager().generateReport(reportName, templateCode, requestParameters);
            }
        });

        if (result != null) {
            webScriptResponse.setContentType(
                    String.format("%s;charset=%s;filename=%s"
                            , result.getMimeType()
                            , result.getEncoding()
                            , result.getFilename()
                    ));
            if (result.getData() != null) {
                final OutputStream out = webScriptResponse.getOutputStream();
                out.write(result.getData());
                out.flush();
                out.close();
            }
        }
    }

    /**
     * Отправить в ответе текст с URL, по-которому будет формироваться отчёт.
     *
     * @param request WebScriptRequest
     * @param response WebScriptResponse
     * @throws IOException
     */
    private void prepareExecURL(WebScriptRequest request, WebScriptResponse response) throws IOException {
        // добавление аргумента "exec=1"
        final String answerURL = request.getURL() + "&" + PARAM_EXEC + "=" + FLAG_EXEC;
        response.setContentType(CONTENT_TEXT_HTML_CHARSET_UTF_8);
        response.getWriter().write(answerURL);
    }
}