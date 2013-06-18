package ru.it.lecm.forms.jasperforms;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.alfresco.service.ServiceRegistry;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.jasper.ArgsHelper;
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

	private ServiceRegistry serviceRegistry;
	private SubstitudeBean substitudeService;
	private OrgstructureBean orgstructureService;
	private DocumentService documentService;
	private DocumentConnectionService documentConnectionService;

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public SubstitudeBean getSubstitudeService() {
		return substitudeService;
	}

	public void setSubstitudeService(SubstitudeBean substitudeService) {
		this.substitudeService = substitudeService;
	}

	public OrgstructureBean getOrgstructureService() {
		return this.orgstructureService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public DocumentConnectionService getDocumentConnectionService() {
		return documentConnectionService;
	}

	public void setDocumentConnectionService(
			DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
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

	/**
	 * Целевой формат отчёта по-умолчанию 
	 */
	private static final TargetFileType DEFAULT_TARGET = TargetFileType.PDF;

	/**
	 * Целевой тип файла
	 */
	enum TargetFileType {
		PDF( "application/pdf", ".pdf")
		, RTF( "application/rtf", ".rtf")
		, DOCX( "application/msword", ".docx")
		, XML( "text/xml", ".xml")
		;

		final private String mimeType, extension;

		private TargetFileType(String mimeType, String extension) {
			this.mimeType = mimeType;
			this.extension = extension;
		}

		public String getMimeType() {
			return mimeType;
		}

		public String getExtension() {
			return extension;
		}

		@Override
		public String toString() {
			return super.name()
					+ "["
						+ "mimeType=" + mimeType
						+ ", extension=" + extension
					+ "]";
		}

		/**
		 * Получить по названию константу перечисления. Регистр символов и незначащие пробелы игнорируются.
		 * @param aname название для преобразования
		 * @param forDefault значение по-умолчанию
		 * @return константу перечисление, если подходящая имеется, или forDefault иначе (в том числе, когда aname = null)
		 */
		static public TargetFileType findByName(String aname, TargetFileType forDefault) {
			if (aname != null) {
				aname = aname.trim();
				for(TargetFileType v: values()) {
					if ( aname.equalsIgnoreCase(v.name()) )
						return v;
				}
			}
			return forDefault; // using default value
		}
	}

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

		// webScriptRequest.getParameterNames();
		final String reportFileName = "/reportdefinitions/" + reportName + ".jasper";
		final URL reportDefinitionURL = JRLoader.getResource(reportFileName);
		if (reportDefinitionURL == null)
			throw new IOException( String.format("Report is missed - file not found at '%s'", reportFileName));

		// TODO: параметризовать выходной формат
		final TargetFileType target = findTargetArg(requestParameters);

		OutputStream outputStream = null;
		try {
			final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportDefinitionURL);// catch message NUllPoiterException for ...
			webScriptResponse.setContentType( String.format("%s;charset=UTF-8;filename=%s", target.getMimeType(), generateFileName( reportName, target.getExtension()) ));
			outputStream = webScriptResponse.getOutputStream();

			final String dataSourceClass = jasperReport.getProperty("dataSource");
			// AbstractDataSourceProvider dsProvider = null;
			JRDataSourceProvider dsProvider = null;
			try {
				
				try {
					final Constructor<?> cons = Class.forName(dataSourceClass).getConstructor(ServiceRegistry.class); 
					dsProvider = (JRDataSourceProvider) cons.newInstance(serviceRegistry);
				} catch (NoSuchMethodException e) {
					// если нет спец конструктора - пробуем обычный ...
					dsProvider = (JRDataSourceProvider) Class.forName(dataSourceClass).getConstructor().newInstance();
				}

				// "своих" особо облагородим ...
				if (dsProvider instanceof AbstractDataSourceProvider) {
					final AbstractDataSourceProvider adsp = (AbstractDataSourceProvider) dsProvider;

					adsp.setServiceRegistry( this.getServiceRegistry());
					adsp.setSubstitudeService( this.getSubstitudeService());
					adsp.setOrgstructureService(this.getOrgstructureService());

					adsp.setDocumentService(this.getDocumentService());
					adsp.setDocumentConnectionService(this.getDocumentConnectionService());
				}

				BeanUtils.populate(dsProvider, requestParameters);

			} catch (ClassNotFoundException e) {
				throw new IOException("Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">. Class not found");
			} catch (NoSuchMethodException e) {
				throw new IOException("Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">. Constructor not defined or has incorrect parrameters");
			} catch (InvocationTargetException e) {
				throw new IOException("Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">", e);
			} catch (InstantiationException e) {
				throw new IOException("Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">", e);
			} catch (IllegalAccessException e) {
				throw new IOException("Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">", e);
			}

			/* построение отчёта */ 
			generateReport(target, outputStream, jasperReport, dsProvider, requestParameters);

		} catch (JRException e) {
			log.error( "Fail to execute report at path "+ reportDefinitionURL , e);
			throw new IOException("Can not fill report", e);
		} finally {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
		}
	}

	private TargetFileType findTargetArg( final Map<String, String[]> requestParameters) 
	{
		final String argname = "targetFormat";
		final String value = ArgsHelper.findArg(requestParameters, argname, null);
		log.info( String.format( "argument %s is %s", argname, Utils.coalesce(value, "default: "+ Utils.coalesce( DEFAULT_TARGET, "empty"))));
		return TargetFileType.findByName( value, DEFAULT_TARGET);
	}

	static final String DEFAULT_FILENAME_DATE_SUFFIX = "dd-MM-yy-HH-mm-ss";


	/**
	 * Сгенерировать имя файла.
	 * @param name имя файла (без расширения и пути): "contracts"
	 * @param extension расширения файла (с точкой): ".rtf"
	 * @return уникальной имя файла (добавляется дата и время)
	 */
	static Object generateFileName(String name, String extension) {
		return String.format( "%s-%s%s", name, new SimpleDateFormat(DEFAULT_FILENAME_DATE_SUFFIX).format(new Date()), extension);
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

	private void generateReport(TargetFileType target, OutputStream outputStream, JasperReport report
			, JRDataSourceProvider dataSourceProvider
			, Map<String, String[]> requestParameters)
			throws IllegalArgumentException, JRException 
	{
		log.info("Generating report " + report.getName() + " ...");

		if (outputStream == null) {
			throw new IllegalArgumentException("The output stream was not specified");
		}

		final JRDataSource dataSource = dataSourceProvider.create(report);

		final JasperFillManager fillManager = JasperFillManager.getInstance(DefaultJasperReportsContext.getInstance());

		final Map<String, Object> reportParameters = new HashMap<String, Object>();
		reportParameters.putAll(requestParameters);

		final JasperPrint jPrint = fillManager.fill(report, reportParameters, dataSource);

		/* формирование результата в нужном формате */
		log.info("Exporting report " + report.getName() + " ...");
		switch (target) {
		case PDF:
			JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
			break;
		case XML:
			JasperExportManager.exportReportToXmlStream(jPrint, outputStream);
			break;
		case RTF:
			exportReportToStream(new JRRtfExporter(), jPrint, outputStream);
			break;
		case DOCX:
			exportReportToStream(new JRDocxExporter(), jPrint, outputStream);
			break;
		default:
			final String msg = String.format( "Unknown report target '%s'", target);
			log.error( msg);
			throw new RuntimeException(msg);
		}

		log.info("Report " + report.getName() + " generated succefully");
	}

	private void exportReportToStream( final JRAbstractExporter exporter 
			, final JasperPrint jPrint, OutputStream outputStream)
			throws JRException 
	{
		exporter.setParameter( JRExporterParameter.JASPER_PRINT, jPrint);
		// exporter.setParameter( JRExporterParameter.OUTPUT_FILE_NAME, REPORT_DIRECTORY + "/" + reportName + ".rtf");
		exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.exportReport();
	}
}
