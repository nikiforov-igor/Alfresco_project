package ru.it.lecm.forms.jasperforms;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.alfresco.service.ServiceRegistry;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * User: AZinovin
 * Date: 05.09.12
 * Time: 16:25
 *
 * Веб скрипт для формирования печатных форм
 * точка входа - /lecm/jforms/form/{report}/{nodeRef} см. /alfresco/templates/webscripts/ru/it/lecm/forms/jasper/form.get.desc.xml
 * Параметры:
 * <code>report</code> - путь к файлу отчета относительно <code>classes/reportdefinitions</code> без расширения <code>.jasper</code>
 * в файле отчета должно быть задано свойство <code><property name="dataSource" value="ru.it.lecm.forms.jasperforms.AbstractDataSourceProvider"/></code>
 * значение - полное имя класса провайдера источника данных, который будет использоваться для генерации отчета
 */
public class JasperFormProducer extends AbstractWebScript {

	private static final Log log = LogFactory.getLog(JasperFormProducer.class);

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
		Map<String, String> templateParams = webScriptRequest.getServiceMatch().getTemplateVars();
		String reportName = templateParams.get("report");
		Map<String, String[]> requestParameters = new HashMap<String, String[]>();
		for (String paramName : webScriptRequest.getParameterNames()) {
			requestParameters.put(paramName, webScriptRequest.getParameterValues(paramName));
		}
		webScriptRequest.getParameterNames();
		final URL reportDefinitionURL = JRLoader.getResource("/reportdefinitions/" + reportName + ".jasper");
		OutputStream outputStream = null;
		try {
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportDefinitionURL);
			outputStream = webScriptResponse.getOutputStream();
			String dataSourceClass = jasperReport.getProperty("dataSource");
			AbstractDataSourceProvider dsProvider = null;
			try {
				dsProvider = (AbstractDataSourceProvider) Class.forName(dataSourceClass)
						.getConstructor(ServiceRegistry.class).newInstance(serviceRegistry);
				BeanUtils.populate(dsProvider,requestParameters);
			} catch (ClassNotFoundException e) {
				throw new IOException("Can not istantiate DataSourceProvider of class <" + dataSourceClass + ">. Class not found");
			} catch (NoSuchMethodException e) {
				throw new IOException("Can not istantiate DataSourceProvider of class <" + dataSourceClass + ">. Constructor not defined or has incorrect parrameters");
			} catch (InvocationTargetException e) {
				throw new IOException("Can not istantiate DataSourceProvider of class <" + dataSourceClass + ">", e);
			} catch (InstantiationException e) {
				throw new IOException("Can not istantiate DataSourceProvider of class <" + dataSourceClass + ">", e);
			} catch (IllegalAccessException e) {
				throw new IOException("Can not istantiate DataSourceProvider of class <" + dataSourceClass + ">", e);
			}
			generateReport(outputStream, jasperReport, dsProvider, requestParameters);
		} catch (JRException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			throw new IOException("Can not fill report", e);
		} finally {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
		}
	}

	private void generateReport(OutputStream outputStream, JasperReport report, AbstractDataSourceProvider dataSourceProvider, Map<String, String[]> requestParameters)
			throws IllegalArgumentException, JRException {
		if (outputStream == null) {
			throw new IllegalArgumentException("The output stream was not specified");
		}

		JRDataSource dataSource = dataSourceProvider.create(report);


		JasperFillManager fillManager = JasperFillManager.getInstance(DefaultJasperReportsContext.getInstance());

		Map<String, Object> reportParameters = new HashMap<String, Object>();

		reportParameters.putAll(requestParameters);
		JasperPrint jPrint = fillManager.fill(report, reportParameters, dataSource);
		JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
	}
}
