package ru.it.lecm.forms.jasperforms;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.alfresco.service.ServiceRegistry;
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
 */
public class JasperFormProducer extends AbstractWebScript {

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
		Map<String, String> templateParams = webScriptRequest.getServiceMatch().getTemplateVars();
		String reportName = templateParams.get("report");
		final URL reportDefinitionURL = JRLoader.getResource("/reportdefinitions/" + reportName + ".jasper");
		OutputStream outputStream = null;
		try {
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportDefinitionURL);
			outputStream = webScriptResponse.getOutputStream();
			String dataSourceClass = jasperReport.getProperty("dataSource");
			AbstractDataSourceProvider dsProvider = null;
			try {
				dsProvider = (AbstractDataSourceProvider) Class.forName(dataSourceClass)
						.getConstructor(Map.class, ServiceRegistry.class).newInstance(templateParams, serviceRegistry);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (NoSuchMethodException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (InvocationTargetException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (InstantiationException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (IllegalAccessException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			if (dsProvider == null) {
				//use FilesDataSourceProvider as default datasource
				dsProvider = new FilesDataSourceProvider(templateParams, serviceRegistry);
			}
			generateReport(outputStream, jasperReport, dsProvider);
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

	private void generateReport(OutputStream outputStream, JasperReport report, AbstractDataSourceProvider dataSourceProvider)
			throws IllegalArgumentException, JRException {
		if (outputStream == null) {
			throw new IllegalArgumentException("The output stream was not specified");
		}

		JRDataSource dataSource = dataSourceProvider.create(report);

		final Map<String, String> templateParams = dataSourceProvider.getTemplateParams();

		JasperFillManager fillManager = JasperFillManager.getInstance(DefaultJasperReportsContext.getInstance());

		Map<String, Object> reportParameters = new HashMap<String, Object>();
		reportParameters.putAll(templateParams);
		JasperPrint jPrint = fillManager.fill(report, reportParameters, dataSource);
		JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
	}
}
