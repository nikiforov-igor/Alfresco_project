package ru.it.lecm.forms.jasperforms;

import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;
import org.alfresco.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AZinovin
 * Date: 14.09.12
 * Time: 17:22
 * <p/>
 * Базовый класс для создания JRDataSourceProvider`s
 */
public abstract class AbstractDataSourceProvider implements JRDataSourceProvider {
	ServiceRegistry serviceRegistry;

	private List<JRField> fieldsList;

	public AbstractDataSourceProvider() {
		initFields();
	}

	/**
	 * Основной конструктор класса
	 *
	 * @param serviceRegistry точка доступа к сервисам - каждый провайдер может получить отсюда требующиеся для работы сервисы
	 */
	public AbstractDataSourceProvider(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		initFields();
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
	protected void addField(String name, Class valueClass) {
		JRDesignField field = new JRDesignField();
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
	public JRField[] getFields(JasperReport report) throws JRException, UnsupportedOperationException {
		return getFieldsList().toArray(new JRField[getFieldsList().size()]);
	}
}
