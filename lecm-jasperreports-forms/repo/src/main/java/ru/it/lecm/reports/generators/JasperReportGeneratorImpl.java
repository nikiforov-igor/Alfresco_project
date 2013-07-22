package ru.it.lecm.reports.generators;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.forms.jasperforms.AbstractDataSourceProvider;
import ru.it.lecm.reports.api.JasperReportTargetFileType;
import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.JRXMLProducer;

/**
 * Генератор Jasper-отчётов.
 *  
 * @author rabdullin
 *
 */
public class JasperReportGeneratorImpl implements ReportGenerator {

	private static final transient Logger log = LoggerFactory.getLogger(JasperReportGeneratorImpl.class);

	private WKServiceKeeper services; 
	private ReportsManager reportsManager;

	public JasperReportGeneratorImpl() {
	}

	public WKServiceKeeper getServices() {
		return services;
	}

	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	@Override
	public void produceReport(
			WebScriptResponse webScriptResponse
			, String reportName
			, Map<String, String[]> parameters
			, ReportDescriptor reportDesc
			) throws IOException 
	{
		PropertyCheck.mandatory (this, "services", services);
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		// TODO: debug
		{
			final URL url = this.reportsManager.getDsXmlResourceUrl(reportName);
			log.info( String.format( "ds file at url: '%s'", url));
		}

		// "/reportdefinitions/" + reportName + ".jasper"
		final String reportFileName = String.format( "%s/%s.jasper" 
				, this.reportsManager.getReportTemplateFileDir(reportDesc.getReportType())
				, reportName
				);

		final URL reportDefinitionURL = JRLoader.getResource(reportFileName);
		if (reportDefinitionURL == null)
			throw new IOException( String.format("Report is missed - file not found at '%s'", reportFileName));

		// DONE: параметризовать выходной формат
		final JasperReportTargetFileType target = findTargetArg(parameters);

		OutputStream outputStream = null;
		try {
			final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportDefinitionURL);// catch message NullPoiterException for ...
			webScriptResponse.setContentType( String.format("%s;charset=UTF-8;filename=%s", target.getMimeType(), generateFileName( reportName, target.getExtension()) ));
			outputStream = webScriptResponse.getOutputStream();

			final String dataSourceClass = jasperReport.getProperty("dataSource");
			final String failMsg = "Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">";
			// AbstractDataSourceProvider dsProvider = null;
			JRDataSourceProvider dsProvider = null;
			try {
				
				try {
					final Constructor<?> cons = Class.forName(dataSourceClass).getConstructor(ServiceRegistry.class); 
					dsProvider = (JRDataSourceProvider) cons.newInstance(getServices().getServiceRegistry());
				} catch (NoSuchMethodException e) {
					// если нет спец конструктора - пробуем обычный ...
					dsProvider = (JRDataSourceProvider) Class.forName(dataSourceClass).getConstructor().newInstance();
				}

				// "своих" особо облагородим ...
				if (dsProvider instanceof AbstractDataSourceProvider) {
					final AbstractDataSourceProvider adsp = (AbstractDataSourceProvider) dsProvider;

					adsp.setServices(this.getServices());
					adsp.setReportDescriptor(reportDesc);
				} else if (dsProvider instanceof GenericDSProviderBase ){
                    ((GenericDSProviderBase) dsProvider).setServices(this.getServices());
                    ((GenericDSProviderBase) dsProvider).setReportDescriptor(reportDesc);
                }

				BeanUtils.populate(dsProvider, parameters);

			} catch (ClassNotFoundException e) {
				throw new IOException(failMsg + ". Class not found");
			} catch (NoSuchMethodException e) {
				throw new IOException(failMsg + ". Constructor not defined or has incorrect parameters");
			} catch (InvocationTargetException e) {
				throw new IOException(failMsg, e);
			} catch (InstantiationException e) {
				throw new IOException(failMsg, e);
			} catch (IllegalAccessException e) {
				throw new IOException(failMsg, e);
			}

			/* построение отчёта */ 
			generateReport(target, outputStream, jasperReport, dsProvider, parameters);

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

	/**
	 * Целевой формат отчёта по-умолчанию 
	 */
	private static final JasperReportTargetFileType DEFAULT_TARGET = JasperReportTargetFileType.PDF;

	private JasperReportTargetFileType findTargetArg( final Map<String, String[]> requestParameters) 
	{
		final String argname = "targetFormat";
		final String value = ArgsHelper.findArg(requestParameters, argname, null);
		log.info( String.format( "argument %s is %s", argname, Utils.coalesce(value, "default: "+ Utils.coalesce( DEFAULT_TARGET, "empty"))));
		return JasperReportTargetFileType.findByName( value, DEFAULT_TARGET);
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

	private void generateReport(JasperReportTargetFileType target, OutputStream outputStream, JasperReport report
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

	@Override
	public void onRegister(String templateFileFullName, ReportDescriptor desc) {
		saveJrxmlTemplate(templateFileFullName, desc);
		compile( templateFileFullName, desc);
	}

	private boolean saveJrxmlTemplate(String outFullFileName, ReportDescriptor desc) {
		if (desc.getReportTemplate() == null) {
			log.warn(String.format( "Report '%s' has no template", desc.getMnem()));
			return false;
		}
		if (desc.getReportTemplate().getData() == null) {
			log.warn(String.format( "Report '%s' has no template data", desc.getMnem()));
			return false;
		}
		/*
		if (desc.getReportTemplate().getFileName() == null) {
			log.warn(String.format( "Report '%s' has empty template FileName", desc.getMnem()));
			return false;
		}
		 */

		// сохранение шаблона как файла ...
		try {
			final FileOutputStream wout = new FileOutputStream(outFullFileName);
			try {
				// TODO: NORMAL IOUtils.copy(desc.getReportTemplate().getData(), wout);

				// в шаблоне пишем dataSource и задаём состав полей ...
				// JRXMLProducer.patchJrxml(inJrxmlFileName, outFullName, desc);
				final String streamTag = desc.getReportTemplate().getFileName(); 
				final ByteArrayOutputStream updated = JRXMLProducer.updateJRXML( 
						desc.getReportTemplate().getData(), streamTag, desc);
				updated.writeTo(wout);

			} finally {
				IOUtils.closeQuietly(wout);
			}
			log.info(String.format( "Report '%s': template file created as '%s'"
					, desc.getMnem(), outFullFileName));

		} catch (IOException ex) {
			final String msg = String.format( "Report '%s': error creating template file '%s'\n%s"
					, desc.getMnem(), outFullFileName, ex.getMessage());
			log.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}

		return true;
	}

	private void compile(String templateFileFullName, ReportDescriptor desc) {
		log.info( String.format( "compiling '%s' ...", templateFileFullName));
		final String destJasperName = FilenameUtils.removeExtension(templateFileFullName)+".jasper";
		try {
			JasperCompileManager.compileReportToFile(templateFileFullName, destJasperName); // context.getRealPath("/reports/WebappReport.jrxml"));
		}
		catch (JRException ex)
		{
			final String msg = String.format( "Error compiling '%s': %s", templateFileFullName, ex.getMessage());
			log.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}
		log.info( String.format( "compiled SUCCESSFULLY from '%s' into '%s'", templateFileFullName, destJasperName));
	}

}
