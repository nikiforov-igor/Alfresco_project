package ru.it.lecm.reports.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User: DBashmakov
 * Date: 27.05.2015
 * Time: 13:48
 */
public class BaseSQLProvider extends SQLProvider {
    private static Logger log = LoggerFactory.getLogger(BaseSQLProvider.class);

    private DataSource basicDataSource;

    private String jdbcdriver;
    private String username;
    private String password;
    private String url;

    @Override
    public void initializeFromGenerator(ReportGeneratorBase baseGenerator) {

    }

    @Override
    public Connection getConnection() {
        Connection conn = null;
        try {
            boolean useDefault = true;
            if (url != null && !url.contains("${")) {
                if (jdbcdriver != null && !jdbcdriver.contains("${")) {
                    Class.forName(jdbcdriver);

                    if (username != null && !username.contains("${") && password != null && !password.contains("${")) {
                        conn = DriverManager.getConnection(url, username, password);
                        useDefault = false;
                    }
                }
            }
            if (useDefault) {
                conn = basicDataSource.getConnection();
            }
        } catch (SQLException | ClassNotFoundException se) {
            log.error(se.getMessage(), se);
        }

        return conn;
    }

    public void setJdbcdriver(String jdbcdriver) {
        this.jdbcdriver = jdbcdriver;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBasicDataSource(DataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }
}
