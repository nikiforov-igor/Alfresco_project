package ru.it.lecm.forms.jasperforms;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

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
	public JasperReport getReportDefinition() throws JRException {
		final URL reportDefinitionURL = JRLoader.getResource("/alfresco/module/jasperforms-repo/reportdefinitions/Simple.jasper");
		return (JasperReport) JRLoader.loadObject(reportDefinitionURL);
	}

	@Override
	public JRDataSource getDataSource() {
		TableModel tableModel = new DefaultTableModel(data, new String[]{"key","value"});
		return new JRTableModelDataSource(tableModel);
	}

	@Override
	public Map<String, Object> getReportParameters() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
