package ru.it.lecm.reports.jasper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.jasper.utils.JRUtils;

public class XMLFieldsDSProvider implements JRDataSourceProvider {

	private List<JRField> fieldsList = new ArrayList<JRField>();

	public XMLFieldsDSProvider() {
	}

	@Override
	public boolean supportsGetFieldsOperation() {
		return true;
	}

	/**
	 * добавляет поле в список возвращаемых источником данных
	 * @param name
	 * @param valueClass
	 */
	protected void addField(String name, Class<?> valueClass) {
		final JRDesignField field = new JRDesignField();
		field.setName(name);
		field.setValueClass(valueClass);
		getFieldsList().add(field);
	}

	public List<JRField> getFieldsList() {
		if (fieldsList == null) {
			fieldsList = new ArrayList<JRField>();
		}
		return fieldsList;
	}

	@Override
	public JRField[] getFields(JasperReport report)
				throws JRException, UnsupportedOperationException {
		final List<JRField> list = getFieldsList();
		return list.toArray(new JRField[list.size()]);
	}

	@Override
	public void dispose(JRDataSource arg0) throws JRException {
	}

	@Override
	public JRDataSource create(JasperReport report) throws JRException {
		final JRDSConfigXML conf = new JRDSConfigXML();

		if (report != null) {
			// get the data source parameters from the report
			final Map<String, JRParameter> params = JRUtils.buildParamMap(report.getParameters());
			conf.setArgsByJRParams(params); // + conf.loadConfig() inside
		}

		// Create a new data source
		final JRDataSource dataSource = new SimpeJRDataSource(conf.getMetaFields());
		return dataSource;

	}

	class SimpeJRDataSource implements JRDataSource {

		final Map<String, DataFieldColumn> metaFields;
		boolean hasNext = true;

		public SimpeJRDataSource(Map<String, DataFieldColumn> metaFields) {
			this.metaFields = metaFields;
		}

		@Override
		public Object getFieldValue(JRField fld) throws JRException {
			return (fld != null && String.class.equals(fld.getValueClass()) ) ? "" : null;
		}

		@Override
		// эмулируем НД из одной строки
		public boolean next() throws JRException {
			if (hasNext) {
				hasNext = false;
				return true;
			}
			return false;
		}
		
	}
}
