package ru.it.lecm.reports.generators;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.model.impl.JavaDataType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 28.11.13
 * Time: 11:59
 */
public class SQLProvider implements JRDataSourceProvider, ReportProviderExt {

    private static final Logger logger = LoggerFactory.getLogger(SQLProvider.class);

    private ReportDescriptor reportDescriptor;
    private DataSource basicDataSource;

    @Override
    public boolean supportsGetFieldsOperation() {
        return true;
    }

    @Override
    public JRField[] getFields(JasperReport report) throws JRException, UnsupportedOperationException {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            connection = basicDataSource.getConnection();
            String query = reportDescriptor.getFlags().getText() + " LIMIT 1";
            statement = connection.prepareStatement(query);

            resultSet = statement.executeQuery();

            int columnCount = resultSet.getMetaData().getColumnCount();

            List<JRField> fields = new ArrayList<JRField>(columnCount);

            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                int columnType = resultSet.getMetaData().getColumnType(i);

                JRDesignField field = new JRDesignField();
                field.setValueClassName(JavaDataType.SupportedTypes.findTypeBySQL(columnType).javaDataType().getClassName());
                field.setName(columnName);

                fields.add(field);
            }
            return fields.toArray(new JRField[fields.size()]);
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
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return new JRField[0];
    }

    @Override
    public JRDataSource create(JasperReport report) throws JRException {
        return null;
    }

    @Override
    public void dispose(JRDataSource dataSource) throws JRException {
        logger.debug(String.format("Disposing dataSource: %s", (dataSource == null ? "null" : dataSource.getClass().getName())));
    }

    @Override
    public void setReportDescriptor(ReportDescriptor reportDescriptor) {
        this.reportDescriptor = reportDescriptor;
    }

    @Override
    public void initializeFromGenerator(ReportGeneratorBase baseGenerator) {
        this.basicDataSource = baseGenerator.getTargetDataSource();
    }
}
