package ru.it.lecm.forms.pentahoforms;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 06.09.12
 * Time: 11:05
 */
public class FormsReportGenerator extends AbstractReportGenerator {
	private String[][] data;

	public FormsReportGenerator(String[][] data) {
		this.data = data;
	}

	@Override
	public MasterReport getReportDefinition() {
		try
		{
			// Using the classloader, get the URL to the reportDefinition file
			final ClassLoader classloader = this.getClass().getClassLoader();
			final URL reportDefinitionURL = classloader.getResource("/alfresco/module/pentahoforms-repo/reportdefinitions/Simple.prpt");

			// Parse the report file
			final ResourceManager resourceManager = new ResourceManager();
			resourceManager.registerDefaults();
			final Resource directly = resourceManager.createDirectly(reportDefinitionURL, MasterReport.class);
			return (MasterReport) directly.getResource();
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DataFactory getDataFactory() {
		TableModel tableModel = new DefaultTableModel(data, new String[]{"key","value"});
		DataFactory dataFactory = new TableDataFactory("default", tableModel);
		return dataFactory;
	}

	@Override
	public Map<String, Object> getReportParameters() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}


}
