package ru.it.lecm.forms.jasperforms;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.utils.ParameterMapper;
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
public class JasperFormProducer extends AbstractWebScript {

	static final transient Logger log = LoggerFactory.getLogger(JasperFormProducer.class);

	// Map<КодТипаОтчёта, Провайдер>
	private Map< /*ReportType*/String, ReportGenerator> reportGenerators;

	private ReportsManager reportsManager;

	/**
	 * @return не NULL список [ReportTypeMnemonic -> ReportGenerator]
	 */
	public Map</*ReportType*/String, ReportGenerator> getReportGenerators() {
		if (reportGenerators == null)
			reportGenerators = new HashMap<String, ReportGenerator>(1);
		return reportGenerators;
	}

	/**
	 * Задать соот-вие типов отчётов и их провайдеров
	 * @param map список [ReportTypeMnemonic -> ReportGenerator]
	 */
	public void setReportGenerators(Map<String, ReportGenerator> map) {
		this.reportGenerators = map;
	}

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	static Map<String, String[]> getRequestParameters( WebScriptRequest webScriptRequest
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
		final String reportName = templateParams.get("report");

		// TODO: ТипОтчёта (Jasper/OOffice и пр) надо брать тоже из параметров
		final String rtype = "JASPER";

		final Map<String, String[]> requestParameters = getRequestParameters(webScriptRequest, String.format("Processing report '%s' with args: \n", reportName));

		PropertyCheck.mandatory (this, "reportGenerators", getReportGenerators());
		PropertyCheck.mandatory(this, "reportsManager", getReportsManager() );

		final ReportDescriptor reportDesc = this.getReportsManager().getRegisteredReportDescriptor(reportName);
		if (reportDesc != null) {
			ParameterMapper.assignParameters( reportDesc, requestParameters);
		}

		// получение провайдера ...
		final ReportGenerator reporter = this.getReportGenerators().get(rtype);
		if (reporter == null)
			throw new RuntimeException( String.format("Unsupported report kind '%s': no privider", rtype));
		reporter.produceReport(webScriptResponse, reportName, requestParameters, reportDesc);
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