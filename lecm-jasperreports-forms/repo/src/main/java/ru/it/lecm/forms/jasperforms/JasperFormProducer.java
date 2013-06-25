package ru.it.lecm.forms.jasperforms;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportDAO;
import ru.it.lecm.reports.jasper.utils.Utils;

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

	private static final transient Logger log = LoggerFactory.getLogger(JasperFormProducer.class);

	private ReportGenerator jasperGenerator;
	private ReportDAO reportDAO;

	/**
	 * Список зарегистрирванных отчётов
	 */
	private List<ReportDescriptor> descriptors;

	public ReportGenerator getJasperGenerator() {
		return jasperGenerator;
	}

	public void setJasperGenerator(ReportGenerator jasperGenerator) {
		this.jasperGenerator = jasperGenerator;
	}

	public ReportDAO getReportDAO() {
		return reportDAO;
	}

	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	public List<ReportDescriptor> getDescriptors() {
		if (this.descriptors == null)
			this.descriptors = new ArrayList<ReportDescriptor>();
		return this.descriptors;
	}

	public void setDescriptors(List<ReportDescriptor> list) {
		this.descriptors = list;
	}

	public void regReportDescriptor(ReportDescriptor desc) {
		if (desc != null)
			getDescriptors().add(desc);
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
		final Map<String, String[]> requestParameters = getRequestParameters(webScriptRequest, String.format("Processing report '%s' with args: \n", reportName));

		PropertyCheck.mandatory (this, "jasperGenerator", jasperGenerator);

		final ReportDescriptor reportDesc = initReportDesc( reportName);
		if (reportDesc != null) {
			assignParameters( reportDesc, requestParameters);
		}

		this.jasperGenerator.produceReport(webScriptResponse, reportName, requestParameters, reportDesc);
	}

	/**
	 * Задать параметры из списка. Подразумевается, что параметры имеют названия 
	 * совпадающие с названиями параметра колонки данных из описателя reportDesc.getDsDescriptor.
	 * @param reportDesc
	 * @param params
	 */
	private void assignParameters( ReportDescriptor reportDesc, Map<String, String[]> params) {
		if (params == null || reportDesc == null || reportDesc.getDsDescriptor() == null)
			return;
		//
		for ( ColumnDescriptor colDesc: reportDesc.getDsDescriptor().getColumns()) {
			// если колонка параметризована ...
			if ( colDesc.getParameterValue() == null) // колонка не описана как параметр ...
				continue;
			final String paramRootName = colDesc.getParameterValue().getMnem(); // colDesc.getColumnName()
			switch (colDesc.getParameterValue().getType()) {
				case VALUE:
				case LIST:
					if (params.containsKey(paramRootName)) {
						final String[] paramValue = params.get(paramRootName);
						log.debug(String.format( "assigning for column '%s' parameter '%s': value '%s'"
								, colDesc.getColumnName(), paramRootName, Arrays.toString(paramValue)) );
						if ( (colDesc.getParameterValue().getType() == Type.VALUE)
								&& (paramValue != null) && (paramValue.length > 0)
								) // для простых значений явно зададим один элемент
							colDesc.getParameterValue().setBound1(paramValue[0]);
						else
							colDesc.getParameterValue().setBound1(paramValue);
					}
					break;
				case RANGE:
					// диапазон задаётся параметрами "{mnem}_lo" и "{mnem}_hi" ...
					if (params.containsKey(paramRootName + "_lo")) {
						final String[] paramValue = params.get(paramRootName + "_lo");
						log.debug(String.format( "assigning for column '%s' low-bound value '%s'", colDesc.getColumnName(), Arrays.toString(paramValue)) );
						colDesc.getParameterValue().setBound1( (paramValue != null && paramValue.length > 0) ? paramValue[0] : null);
					}
					if (params.containsKey(paramRootName + "_hi")) {
						final String[] paramValue = params.get(paramRootName + "_hi");
						log.debug(String.format( "assigning for column '%s' high-bound value '%s'", colDesc.getColumnName(), Arrays.toString(paramValue)) );
						colDesc.getParameterValue().setBound2( (paramValue != null && paramValue.length > 0) ? paramValue[0] : null);
					}
					break;
				default:
					log.error( String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL") ));
					break;
			}

		}
	}

	/**
	 * Получить дексриптор отчёта по его мнемонике или вернуть null.
	 * Поиск ведётся по зарегистрированным отчётам в this.descriptors и в БД.
	 * @param reportName дескриптор
	 * @return описатеть отчёта или null, если не найден
	 */
	private ReportDescriptor initReportDesc(String reportName) {
		for(ReportDescriptor d: getDescriptors()) {
			if (Utils.isSafelyEquals(reportName, d.getMnem()))
				return d; // FOUND by Mnemonic
		}

		// попытка загрузить DAO-объект
		if (reportDAO != null) {
			final ReportDescriptor d = reportDAO.getReportDescriptor(reportName);
			if (d != null)
				return d; // FOUND by DAO mnemonic
		}

		log.warn(String.format( "Report '%s' has no descriptor", reportName));
		return null; // NOT FOUND
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

	public void init() {
		log.info( String.format( " initialized templates count %s\n%s",
					getDescriptors().size(), Utils.getAsString(getDescriptors()) 
		));
	}
}