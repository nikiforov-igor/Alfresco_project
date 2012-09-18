package ru.it.lecm.forms.jasperforms;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignField;
import org.alfresco.service.ServiceRegistry;

import java.util.List;
import java.util.Map;

/**
 * User: AZinovin
 * Date: 14.09.12
 * Time: 17:22
 */
public abstract class AbstractDataSourceProvider implements JRDataSourceProvider {
	ServiceRegistry serviceRegistry;
	Map<String, String> templateParams;

	public AbstractDataSourceProvider() {
		initFields();
	}

	public AbstractDataSourceProvider(Map<String, String> templateParams, ServiceRegistry serviceRegistry) {
		this.templateParams = templateParams;
		this.serviceRegistry = serviceRegistry;
		initFields();
	}

	protected abstract void initFields();

	@Override
	public boolean supportsGetFieldsOperation() {
		return true;
	}

	protected void addField(String name, Class valueClass) {
		JRDesignField field = new JRDesignField();
		field.setName(name);
		field.setValueClass(valueClass);
		getFieldsList().add(field);
	}

	protected abstract List<JRField> getFieldsList();

	@Override
	public JRField[] getFields(JasperReport report) throws JRException, UnsupportedOperationException {
		return getFieldsList().toArray(new JRField[getFieldsList().size()]);
	}

	public Map<String, String> getTemplateParams() {
		return templateParams;
	}

	@Override
	public void dispose(JRDataSource dataSource) throws JRException {

	}
}
