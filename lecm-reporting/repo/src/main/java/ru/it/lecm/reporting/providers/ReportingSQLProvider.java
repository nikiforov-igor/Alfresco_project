package ru.it.lecm.reporting.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.generators.ReportGeneratorBase;
import ru.it.lecm.reports.generators.SQLProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: dbashmakov
 * Date: 10.02.2015
 * Time: 12:59
 */
public class ReportingSQLProvider extends SQLProvider {
    private static Logger log = LoggerFactory.getLogger(ReportingSQLProvider.class);

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void initializeFromGenerator(ReportGeneratorBase baseGenerator) {}

    @Override
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = getDataSource().getConnection();
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
        }

        return conn;
    }
}
