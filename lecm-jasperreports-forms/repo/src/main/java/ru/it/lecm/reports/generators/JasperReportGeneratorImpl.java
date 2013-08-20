package ru.it.lecm.reports.generators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.JasperReportTargetFileType;
import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.NamedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFlags;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

/**
 * Генератор Jasper-отчётов.
 *  
 * @author rabdullin
 *
 */
public class JasperReportGeneratorImpl
		implements ReportGenerator, ApplicationContextAware
{

	private static final transient Logger log = LoggerFactory.getLogger(JasperReportGeneratorImpl.class);

	private WKServiceKeeper services; 
	private ReportsManager reportsMgr;
	private String reportsManagerBeanName;
	private ApplicationContext context;

	public JasperReportGeneratorImpl() {
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.context = ctx;
	}

	public WKServiceKeeper getServices() {
		return services;
	}

	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	public String getReportsManagerBeanName() {
		return reportsManagerBeanName;
	}

	public void setReportsManagerBeanName(String beanName) {
		if ( Utils.isSafelyEquals(beanName, reportsManagerBeanName) )
			return;
		log.debug(String.format("ReportsManagerBeanName assigned: %s", beanName));
		this.reportsManagerBeanName = beanName;
		this.reportsMgr = null; // очистка
	}

	public ReportsManager getReportsManager() {
		if (this.reportsMgr == null && this.reportsManagerBeanName != null) {
			this.reportsMgr = (ReportsManager) this.context.getBean(this.reportsManagerBeanName);
		}
		return this.reportsMgr;
	}

//	public void setReportsManager(ReportsManager reportsManager) {
//		this.reportsManager = reportsManager;
//	}

	@Override
	public void produceReport( WebScriptResponse webScriptResponse
			, ReportDescriptor reportDesc
			, Map<String, String[]> parameters
			, ReportContentDAO rptContent
	) throws IOException
	{
		PropertyCheck.mandatory (this, "services", services);
		PropertyCheck.mandatory (this, "reportsManager", getReportsManager());

		// "/reportdefinitions/" + reportName + ".jasper"
		final String reportFileName = 
//				String.format( "%s/%s.jasper" 
//				, this.getReportsManager().getReportTemplateFileDir(reportDesc.getReportType())
//				, reportName
//				);
				String.format( "%s.jasper", reportDesc.getMnem());

		final ContentReader reader = rptContent.loadContent( IdRContent.createId(reportDesc, reportFileName));

		OutputStream outputStream = null;
		InputStream stm = (reader != null) ? reader.getContentInputStream() : null;
		try {
			if (reader == null)
				throw new IOException( String.format("Report is missed - file '%s' not found", reportFileName ));

			// DONE: параметризовать выходной формат
			final JasperReportTargetFileType target = findTargetArg(parameters);

			// final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportDefinitionURL);// catch message NullPoiterException for ...
			final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stm);
			IOUtils.closeQuietly(stm); stm = null; // сразу закроем поток отчёта

			webScriptResponse.setContentType( String.format("%s;charset=UTF-8;filename=%s"
							, target.getMimeType()
							, generateReportResultFileName( reportDesc.getMnem(), target.getExtension()) 
			));
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
				if (dsProvider instanceof ReportProviderExt) {
					final ReportProviderExt adsp = (ReportProviderExt) dsProvider;

					adsp.setServices( this.getServices());
					adsp.setReportDescriptor( reportDesc);
					adsp.setReportManager( this.reportsMgr);
				}

				assignProviderProps( dsProvider, parameters, reportDesc);

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

		} catch (Throwable e) { // (JRException e) {
			final String msg = String.format( "Fail to execute report '%s':\n\t%s", reportDesc.getMnem(), e);
			log.error( msg, e);
			throw new IOException(msg, e);
		} finally {
			if (stm != null) {
				IOUtils.closeQuietly(stm);
			}
		
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
		}
	}

	/**
	 * Присвоение свойств для Провайдера:
	 *    1) по совпадению названий параметров и свойств провайдера
	 *    2) по списку сконфигурированному списку алиасов для этого провайдера
	 * @param destProvider целевой Провайдер
	 * @param srcParameters список параметров
	 * @param srcReportDesc текущий описатель Отчёта, для получения из его флагов списка алиасов
	 * (в виде "property.xxx=paramName")
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void assignProviderProps(JRDataSourceProvider destProvider,
			Map<String, String[]> srcParameters, ReportDescriptor srcReportDesc
		) throws IllegalAccessException, InvocationTargetException
	{
		if (srcParameters != null && destProvider != null) {
			// присвоение сконфигурированных алиасов ...
			ArgsHelper.assignParameters( destProvider, getPropertiesAliases(srcReportDesc), srcParameters);

			// присвоение свойств с совпадающими именами с параметрами
			BeanUtils.populate(destProvider, srcParameters);
		}
	}

	/**
	 * Получить из флагов описателя отчёта список алиасов параметров для именованных свойств.
	 * Для случаев, когда название входного (web-)параметра отличается от 
	 * названия свойства провайдера, в которое это свойство должно попасть. 
	 *    ключ = название свойства провайдера,
	 *    значение = возможные синонимы в параметрах.
	 */
	public Map<String, String[]> getPropertiesAliases(ReportDescriptor reportDesc) {
		// выбираем из флагов дескриптора ...
		final Map<String, String[]> result = getPropertiesAliases( (reportDesc == null) ? null : reportDesc.getFlags());

		if (reportDesc != null && log.isDebugEnabled()) {
			log.debug(String.format( "Found parameters' aliases for provider %s:\n\t%s", reportDesc.getClass(), result));
		}

		return result;
	}

	/** префикс названия для конвертирующего свойства */
	final static String PFX_PROPERTY_ITEM = "property.".toLowerCase();

	public Map<String, String[]> getPropertiesAliases(ReportFlags reportFlags) {
		// выбираем из флагов дескриптора ...
		if (reportFlags == null || reportFlags.flags() == null)
			return null;

		final Map<String, String[]> result = new HashMap<String, String[]>( reportFlags.flags().size());

		// сканируем параметры-флаги вида "property.XXX"
		for(NamedValue item: reportFlags.flags()) {
			if (item != null && item.getMnem() != null) {
				if (item.getMnem().toLowerCase().startsWith(PFX_PROPERTY_ITEM)) {
					// это описание конвертирования ...
					final String propName = item.getMnem().substring(PFX_PROPERTY_ITEM.length()); // часть строки после префикса это имя свойства (возможно вложенного)
					final String[] aliases = (Utils.isStringEmpty(item.getValue())) ? null : item.getValue().split("[,;]");
					if (aliases != null) {
						for(int i = 0; i < aliases.length; i++) {
							if (aliases[i] != null)
								aliases[i] = aliases[i].trim();
						}
					}
					result.put( propName, aliases);
				}
			}
		}

		return (result.isEmpty()) ? null : result;
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
	static Object generateReportResultFileName(String name, String extension) {
		return String.format( "%s-%s%s"
				, name
				, new SimpleDateFormat(DEFAULT_FILENAME_DATE_SUFFIX).format(new Date())
				, extension);
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
	public void onRegister(ReportDescriptor desc, byte[] templateData, ReportContentDAO storage) {
		// final ContentReader jrxmlContent = saveJrxmlTemplate(desc, storage);
		compileJrxml(desc, templateData,  storage);
	}

	private void compileJrxml(ReportDescriptor desc, byte[] templateData
			, ReportContentDAO storage)
	{
		if (templateData == null)
			return;

		log.info( String.format( "compiling report '%s' ...", desc.getMnem()));

		final ByteArrayInputStream inData = new ByteArrayInputStream(templateData);
		final ByteArrayOutputStream outData = new ByteArrayOutputStream();

		// final String destJasperName = FilenameUtils.removeExtension(templateFileFullName)+".jasper";
		try {
			// JasperCompileManager.compileReportToFile(templateFileFullName, destJasperName); // context.getRealPath("/reports/WebappReport.jrxml"));
			JasperCompileManager.compileReportToStream(inData, outData);
			final IdRContent id = IdRContent.createId(desc, desc.getMnem() + ".jasper");
			storage.storeContent(id, new ByteArrayInputStream(outData.toByteArray()));
		} catch (JRException ex) {
			final String msg = String.format( "Error compiling report '%s':\n\t%s", desc.getMnem(), ex.getMessage());
			log.error( msg, ex);
			throw new RuntimeException( msg, ex);
		}
		log.info( String.format( "Jasper report '%s' compiled SUCCESSFULLY into %s bytes", desc.getMnem(), outData.size()));
	}

}
