package ru.it.lecm.forms.jasperforms;


import net.sf.jasperreports.engine.*;

import java.io.OutputStream;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 06.09.12
 * Time: 9:56
 * <p/>
 * This is the base class used with the report generation examples. It contains the actual <code>embedding</code>
 * of the reporting engine and report generation. All example embedded implementations will need to extend this class
 * and perform the following:
 * <ol>
 * <li>Implement the <code>getReportDefinition()</code> method and return the report definition (how the report
 * definition is generated is up to the implementing class).
 * <li>Implement the <code>getTableDataFactory()</code> method and return the data factory to be used (how
 * this is created is up to the implementing class).
 * <li>Implement the <code>getReportParameters()</code> method and return the set of report parameters to be used.
 * If no report parameters are required, then this method can simply return <code>null</code>
 * </ol>
 */
public abstract class AbstractReportGenerator {

	public abstract JasperReport getReportDefinition() throws JRException;

	public abstract JRDataSource getDataSource();

	public abstract Map<String, Object> getReportParameters();

	public void generateReport(OutputStream outputStream)
			throws IllegalArgumentException, JRException {
		if (outputStream == null) {
			throw new IllegalArgumentException("The output stream was not specified");
		}

		// Get the report and data factory

		final JasperReport report = getReportDefinition();
		final JRDataSource dataSource = getDataSource();

		final Map<String, Object> reportParameters = getReportParameters();

		JasperFillManager fillManager = JasperFillManager.getInstance(DefaultJasperReportsContext.getInstance());

		JasperPrint jPrint = fillManager.fill(report, reportParameters, dataSource);
		JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
	}
}
