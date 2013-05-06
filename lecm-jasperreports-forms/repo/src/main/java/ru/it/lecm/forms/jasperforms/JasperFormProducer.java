package ru.it.lecm.forms.jasperforms;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.alfresco.service.ServiceRegistry;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.base.beans.SubstitudeBean;
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

	private static final Log log = LogFactory.getLog(JasperFormProducer.class);

	private ServiceRegistry serviceRegistry;
	private SubstitudeBean substitudeService;

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

	@Override
	public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
		final Map<String, String> templateParams = webScriptRequest.getServiceMatch().getTemplateVars();
		final String reportName = templateParams.get("report");
		final Map<String, String[]> requestParameters = getRequestParameters(webScriptRequest, String.format("Processing report '%s' with args: \n", reportName));

		// webScriptRequest.getParameterNames();
		final String reportFileName = "/reportdefinitions/" + reportName + ".jasper";
		final URL reportDefinitionURL = JRLoader.getResource(reportFileName);
		if (reportDefinitionURL == null)
			throw new IOException( String.format("Report is missed - file not found at '%s'", reportFileName));

		OutputStream outputStream = null;
		try {
			final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportDefinitionURL);// catch message NUllPoiterException for ...
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
			generateReport(outputStream, jasperReport, dsProvider, requestParameters);
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

	private void generateReport(OutputStream outputStream, JasperReport report
			, JRDataSourceProvider dataSourceProvider
			, Map<String, String[]> requestParameters)
			throws IllegalArgumentException, JRException 
	{
		if (outputStream == null) {
			throw new IllegalArgumentException("The output stream was not specified");
		}

		JRDataSource dataSource = dataSourceProvider.create(report);

		JasperFillManager fillManager = JasperFillManager.getInstance(DefaultJasperReportsContext.getInstance());

		final Map<String, Object> reportParameters = new HashMap<String, Object>();
		reportParameters.putAll(requestParameters);

		JasperPrint jPrint = fillManager.fill(report, reportParameters, dataSource);
		JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
	}
}
