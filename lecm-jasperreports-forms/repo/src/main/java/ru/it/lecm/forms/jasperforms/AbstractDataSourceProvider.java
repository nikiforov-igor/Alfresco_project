package ru.it.lecm.forms.jasperforms;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.LinksResolver;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.beans.WKServiceKeeper;

/**
 * User: AZinovin
 * Date: 14.09.12
 * Time: 17:22
 * <p/>
 * Базовый класс для создания JRDataSourceProvider`s
 * Имеет список jasper-полей и реестр alfresco-служб.
 */
public abstract class AbstractDataSourceProvider
		implements JRDataSourceProvider, ReportProviderExt
{

	protected WKServiceKeeper services;
	protected LinksResolver resolver;
	private List<JRField> fieldsList;
	private ReportDescriptor reportDescriptor;
	private ReportsManager reportManager;

	public AbstractDataSourceProvider() {
		initFields();
	}

	/**
	 * Основной конструктор класса
	 *
	 * @param serviceRegistry точка доступа к сервисам - каждый провайдер может получить отсюда требующиеся для работы сервисы
	 */
	public AbstractDataSourceProvider(WKServiceKeeper wksServices) {
		this.services = wksServices;
		initFields();
	}

	public WKServiceKeeper getServices() {
		return services;
	}

	@Override
	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	public ReportDescriptor getReportDescriptor() {
		return reportDescriptor;
	}

	@Override
	public void setReportDescriptor(ReportDescriptor reportDesc) {
		this.reportDescriptor = reportDesc;
	}

	public ReportsManager getReportManager() {
		return reportManager;
	}

	@Override
	public void setReportManager(ReportsManager reportManager) {
		this.reportManager = reportManager;
	}

	@Override
	public void setResolver(LinksResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * инициализирует список колонок источника данных
	 *
	 * требуется для редактора iReport при указании провайдера, чтобы получить список доступных полей
	 * достаточно вызвать внутри метод addField(..) для каждой колонки
	 */
	protected abstract void initFields();

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
		return getFieldsList().toArray(new JRField[getFieldsList().size()]);
	}

}
