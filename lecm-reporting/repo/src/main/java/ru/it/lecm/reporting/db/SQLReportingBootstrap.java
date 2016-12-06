package ru.it.lecm.reporting.db;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reporting.mybatis.ReportingDAO;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 03.12.2014
 * Time: 9:47
 */
public class SQLReportingBootstrap {
    private static final transient Logger logger = LoggerFactory.getLogger(SQLReportingBootstrap.class);

    private List<String> sqlFiles;
    private boolean bootstrapOnStart;
    private ReportingDAO reportingDAO;

    private TransactionService transactionService;

    public void setReportingDAO(ReportingDAO reportingDAO) {
        this.reportingDAO = reportingDAO;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setSqlFiles(List<String> sqlFiles) {
        this.sqlFiles = sqlFiles;
    }

    public void setBootstrapOnStart(boolean bootstrapOnStart) {
        this.bootstrapOnStart = bootstrapOnStart;
    }

    public void bootstrap() {
        if (!bootstrapOnStart) {
            logger.warn("SQL Bootstrap disabled. Use 'lecm.reporting.bootstrapOnStart=true' in alfresco-global.properties file to enable it.");
            return; //пропускаем
        }
        AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                if (sqlFiles != null) {
                    transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                        @Override
                        public Object execute() throws Throwable {
                            for (final String sql : sqlFiles) {
                                InputStreamReader reader = null;
                                InputStream inputStream = null;
                                try {
                                    logger.debug("Execute SQL: {}", sql);
                                    inputStream = getClass().getClassLoader().getResourceAsStream(sql);
                                    reader = new InputStreamReader(inputStream);
                                    ScriptRunner runner = new ScriptRunner(reportingDAO.getConnection());
                                    runner.runScript(reader);
                                    logger.debug("{} execute finished.", sql);
                                } catch (Exception e) {
                                    logger.error("Can not execute sql: " + sql, e);
                                } finally {
                                    IOUtils.closeQuietly(reader);
                                    IOUtils.closeQuietly(inputStream);
                                }
                            }
                            return null;
                        }
                    });
                }
                return null;
            }
        };
        AuthenticationUtil.runAsSystem(raw);
    }
}
