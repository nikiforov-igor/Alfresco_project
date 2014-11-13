package ru.it.lecm.reports.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User: dbashmakov
 * Date: 07.11.2014
 * Time: 9:39
 */
public class DataBaseHelper {
    private static Logger log = LoggerFactory.getLogger(DataBaseHelper.class);

    private DataSource basicDataSource;

    private Connection connection;

    private String jdbcdriver;
    private String username;
    private String password;
    private String url;

    public DataSource getBasicDataSource() {
        return basicDataSource;
    }

    public void setBasicDataSource(DataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
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

    public void init() {
        this.connection = getConnection();
    }

    public Connection getConnection() {
        try {
            boolean useDefault = true;

            if ((this.connection == null) || connection.isClosed()) {
                if (url != null && !url.contains("${")) {
                    if (jdbcdriver != null && !jdbcdriver.contains("${")) {
                        Class.forName(jdbcdriver);

                        if (username != null && !username.contains("${") && password != null && !password.contains("${")) {
                            this.connection = DriverManager.getConnection(url, username, password);
                            useDefault = false;
                        }
                    }
                }

                if (useDefault) {
                    try {
                        this.connection = getBasicDataSource().getConnection();
                    } catch (SQLException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException se) {
            log.error(se.getMessage(), se);
        }

        return connection;
    }
}
