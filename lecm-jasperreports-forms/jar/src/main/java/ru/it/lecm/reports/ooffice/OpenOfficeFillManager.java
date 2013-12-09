package ru.it.lecm.reports.ooffice;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.generators.OpenOfficeTemplateGenerator;
import ru.it.lecm.reports.generators.SubreportBuilder;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenOfficeFillManager {
    private static final transient Logger logger = LoggerFactory.getLogger(OpenOfficeFillManager.class);

    final private OpenOfficeConnection connection;
    final private BasicDataSource basicDataSource;

    public OpenOfficeFillManager(OpenOfficeConnection connection, BasicDataSource basicDataSource) {
        this.connection = connection;
        this.basicDataSource = basicDataSource;
    }

    public OpenOfficeConnection getConnection() {
        return connection;
    }

    /**
     * Выполнить заполнение данными указанного отчёта файла openOffice
     *
     * @param report     отчёт
     * @param parameters параметры
     * @param dataSource набор данных
     * @param urlSrc     исходный файл openOffice (".odt")
     * @param urlSaveAs  целевой файл (может иметь другой формат, например, ".rtf")
     * @throws JRException
     */
    public void fill(ReportDescriptor report, Map<String, Object> parameters, JRDataSource dataSource, String urlSrc, String urlSaveAs) throws JRException {
        PropertyCheck.mandatory(this, "connection", connection);

        // атрибуты одной строки НД, которые надо будет присвоить параметрам документа
        final Map<String, Object> props = new HashMap<String, Object>();

        // формируем значения
        if (!report.isSQLDataSource()) {
            // по умолчанию - expressions
            for (ColumnDescriptor colDesc : report.getDsDescriptor().getColumns()) {
                props.put(colDesc.getColumnName(), colDesc.getExpression());
            }
            // реальные значения из источника
            if (dataSource.next()) {
                /* получение данных из текущей строки ... */
                for (ColumnDescriptor colDesc : report.getDsDescriptor().getColumns()) {
                    Object value = dataSource.getFieldValue(DataFieldColumn.createDataField(colDesc));
                    if (value == null && colDesc.getExpression().matches(SubreportBuilder.REGEXP_SUBREPORTLINK)) {
                        // пустой подотчет - вместо null подсовываем пустой список
                        value = new ArrayList();
                    }
                    props.put(colDesc.getColumnName(), value);
                }
            }
        } else {
            Connection sqlConnection = null;
            ResultSet resultSet = null;
            PreparedStatement statement = null;

            try {
                sqlConnection = basicDataSource.getConnection();
                String query = report.getFlags().getText();

                statement = sqlConnection.prepareStatement(query);
                statement.setMaxRows(1);

                resultSet = statement.executeQuery();

                // по умолчанию - названия столбцов из запроса
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);

                    String value = SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + columnName + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL;
                    props.put(columnName, value);
                }
                /* + из колонок берем подотчеты... */
                for (ColumnDescriptor colDesc : report.getDsDescriptor().getColumns()) {
                    if (colDesc.getExpression() != null && colDesc.getExpression().matches(SubreportBuilder.REGEXP_SUBREPORTLINK)) {
                        props.put(colDesc.getColumnName(), new ArrayList<Map>());
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (sqlConnection != null) {
                    try {
                        sqlConnection.close();
                    } catch (SQLException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }

        //в props для обычного провайдера - список заполненных значений, для SQL - дефолтных
        final OpenOfficeTemplateGenerator ooGen = new OpenOfficeTemplateGenerator(connection, basicDataSource);
        ooGen.odtSetColumnsAsDocCustomProps(props, parameters, report, urlSrc, urlSaveAs, null);
    }
}
