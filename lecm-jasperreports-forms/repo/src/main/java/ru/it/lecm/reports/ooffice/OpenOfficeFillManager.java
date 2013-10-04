package ru.it.lecm.reports.ooffice;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;

import org.alfresco.util.PropertyCheck;

import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.generators.OpenOfficeTemplateGenerator;

public class OpenOfficeFillManager {

	final private OpenOfficeConnection connection;

	public OpenOfficeFillManager(OpenOfficeConnection connection) {
		this.connection = connection;
	}

	public OpenOfficeConnection getConnection() {
		return connection;
	}

	/**
	 * Выполнить заполнение данными указанного отчёта файла openOffice
	 * @param report отчёт
	 * @param parameters параметры
	 * @param dataSource набор данных
	 * @param urlSrc исходный файл openOffice (".odt")
	 * @param urlSaveAs целевой файл (может иметь другой формат, например, ".rtf")
	 * @return
	 * @throws JRException 
	 */
	public void fill(
			ReportDescriptor report
			, Map<String, String[]> parameters
			, JRDataSource dataSource
			, String urlSrc
			, String urlSaveAs
	) throws JRException
	{
		PropertyCheck.mandatory(this, "connection", getConnection());
		// PropertyCheck.mandatory(this, "dataSource", dataSource);

		// атрибуты одной строки НД, которые надо будет присвоить параметрам документа
		final Map<String, Object> props = new HashMap<String, Object>();

		// формируем значения по-умолчанию
		for (ColumnDescriptor colDesc: report.getDsDescriptor().getColumns()) {
			props.put( colDesc.getColumnName(), colDesc.getExpression());
		}

		if (dataSource != null) {
			// while (dataSource.next()) { ...dataRow process ... } // while

			if (dataSource.next()) {
				/* получение данных из текущей строки ... */
				// for (Map.Entry<String, Object> entry: props.entrySet())
				for (ColumnDescriptor colDesc: report.getDsDescriptor().getColumns()) {
					final Object value = dataSource.getFieldValue( DataFieldColumn.createDataField(colDesc));
					props.put( colDesc.getColumnName(), value);
				}
			}
		}

		final String author = null;

		final OpenOfficeTemplateGenerator ooGen = new OpenOfficeTemplateGenerator();
		ooGen.odtSetColumnsAsDocCustomProps(props, getConnection(), report, urlSrc, urlSaveAs, author);
	}

}
