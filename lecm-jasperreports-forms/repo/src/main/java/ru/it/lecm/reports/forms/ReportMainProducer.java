package ru.it.lecm.reports.forms;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.ReportFileData;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.beans.ReportBeansLocator;
import ru.it.lecm.reports.utils.Utils;

/**
 * User: AZinovin
 * Date: 05.09.12
 * Time: 16:25
 *
 * Веб скрипт для формирования печатных форм
 * точка входа - /lecm/report/{report} см. /alfresco/templates/webscripts/ru/it/lecm/forms/jasper/form.get.desc.xml
 * Параметры:
 * <code>report</code> - путь к файлу отчета относительно <code>classes/reportdefinitions</code> без расширения <code>.jasper</code>
 * в файле отчета должно быть задано свойство <code><property name="dataSource" value="ru.it.lecm.forms.jasperforms.AbstractDataSourceProvider"/></code>
 * значение - полное имя класса провайдера источника данных, который будет использоваться для генерации отчета
 */
public class ReportMainProducer extends AbstractWebScript {

	static final transient Logger log = LoggerFactory.getLogger(ReportMainProducer.class);

	private ReportsManager reportsManager;

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	/**
	 * Формирование карты параметров webScriptRequest в виде:
	 *    ключ - название параметра, 
	 *    значение - его строковое значение.
	 * @param webScriptRequest
	 * @param msg
	 * @return
	 */
	public static Map<String, String[]> getRequestParameters( WebScriptRequest webScriptRequest
			, final String msg) 
	{
		final Map<String, String[]> result = new HashMap<String, String[]>();
		final StringBuilder infosb = new StringBuilder();
		if (msg != null)
			infosb.append( msg);
		int i = 0;
		for (String paramName : webScriptRequest.getParameterNames()) {
			++i;
			final String[] value = webScriptRequest.getParameterValues(paramName);
			result.put(paramName, value);
			infosb.append(String.format( "\t[%d]\t'%s' \t'%s'\n", i, paramName, Utils.coalesce( Utils.getAsString(value), "NULL")));
		}
		if (log.isInfoEnabled()) {
			log.info( String.format("Call report maker with args count=%d:\n %s", i, infosb.toString()) );
		}
		return result;
	}

	final static String PARAM_EXEC = "exec";
	final static String ContentTypeHtml = "text/html;charset=UTF-8";
	// final static String ContentTypePdf = "application/pdf;charset=UTF-8";
	// final static String ContentTypeRtf = "application/rtf;charset=UTF-8";

	@Override
	public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {

		// проверка надо ли выполнять запрос или только сформировать в ответ URL ... 
		if (!"1".equals(webScriptRequest.getParameter(PARAM_EXEC))) {
			prepareExecURL( webScriptRequest, webScriptResponse);
			return;
		}

		final Map<String, String> templateParams = webScriptRequest.getServiceMatch().getTemplateVars();
		final String reportName = Utils.coalesce( templateParams.get("report"), templateParams.get("reportCode"));

		// DONE: если ТипОтчёта (Jasper/OOffice и пр) брать из параметров, то может быть несоотвествие дескриптору ...
		// final String rtype = Utils.coalesce( templateParams.get("reporType"), ReportsManagerImpl.DEFAULT_REPORT_TYPE);

		final Map<String, String[]> requestParameters = getRequestParameters(webScriptRequest, String.format("Processing report '%s' with args: \n", reportName));
 
		PropertyCheck.mandatory(this, "reportsManager", getReportsManager() );
		PropertyCheck.mandatory (this, "reportGenerators", getReportsManager().getReportGenerators());

		// локатору закинем текущее значение менеджера ...
		ReportBeansLocator.setReportsManager(this.getReportsManager());

		final ReportFileData result = this.getReportsManager().generateReport(reportName, requestParameters);
		if (result != null) {
			webScriptResponse.setContentType(
					String.format( "%s;charset=%s;filename=%s"
							, result.getMimeType()
							, result.getEncoding()
							, result.getFilename() 
							));
			if (result.getData() != null) {
				final OutputStream out = webScriptResponse.getOutputStream();
				out.write( result.getData());
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * Отправить в ответе текст с URL, по-которому будет формироваться отчёт. 
	 * @param webScriptRequest
	 * @param webScriptResponse
	 * @throws IOException 
	 */
	private void prepareExecURL(WebScriptRequest request, WebScriptResponse response)
			throws IOException
	{
		// добавление аргумента "exec=1" 
		// request.getServerPath() always "http://localhost:8080" 
		final String answerURL = request.getURL() + String.format( "&%s=1", PARAM_EXEC); 

		response.setContentType(ContentTypeHtml);
		// response.setContentEncoding();
		final Writer out = response.getWriter();
		out.write(answerURL);
	}

}