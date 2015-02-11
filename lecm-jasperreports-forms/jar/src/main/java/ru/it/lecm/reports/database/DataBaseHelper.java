package ru.it.lecm.reports.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: dbashmakov
 * Date: 07.11.2014
 * Time: 9:39
 */
public class DataBaseHelper {
    private static Logger log = LoggerFactory.getLogger(DataBaseHelper.class);

    private static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";

    private DataSource basicDataSource;

    private String jdbcdriver;
    private String username;
    private String password;
    private String url;

    private boolean isPostgresDB = false;
    private String alfrescoDriver;

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

    public void setAlfrescoDriver(String alfrescoDriver) {
        this.alfrescoDriver = alfrescoDriver;
    }

    public void init() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                if (isPostgresDB) {
                    initializeFunctions(conn);
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

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
                        isPostgresDB = jdbcdriver.equals(ORG_POSTGRESQL_DRIVER);
                    }
                }
            }
            if (useDefault) {
                conn = getBasicDataSource().getConnection();
                isPostgresDB = alfrescoDriver != null && alfrescoDriver.equals(ORG_POSTGRESQL_DRIVER);
            }
        } catch (SQLException | ClassNotFoundException se) {
            log.error(se.getMessage(), se);
        }

        return conn;
    }

    private void initializeFunctions(Connection conn) throws SQLException{
        String sqlQuery = "CREATE EXTENSION IF NOT EXISTS tablefunc";
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sqlQuery);
            statement.execute();
            if (!conn.getAutoCommit()){
                conn.commit();
            }
            log.debug("Extensions created:\n{}", sqlQuery);
        } catch (SQLException e) {
            log.warn("Can not create extensions:\n{}", sqlQuery);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
