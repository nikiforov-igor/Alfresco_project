package ru.it.lecm.reports.database;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 03.12.2014
 * Time: 9:47
 */
public class SQLReportingBootstrap extends BaseBean {
    private static final transient Logger logger = LoggerFactory.getLogger(SQLReportingBootstrap.class);

    private List<String> sqlFiles;
    private boolean bootstrapOnStart;
    private DataBaseHelper dataBaseHelper;

    public void setSqlFiles(List<String> sqlFiles) {
        this.sqlFiles = sqlFiles;
    }

    public void setBootstrapOnStart(boolean bootstrapOnStart) {
        this.bootstrapOnStart = bootstrapOnStart;
    }

    public void setDataBaseHelper(DataBaseHelper dataBaseHelper) {
        this.dataBaseHelper = dataBaseHelper;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
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
                                logger.debug("Execute SQL: {}", sql);
                                final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(sql);
                                InputStreamReader reader = new InputStreamReader(inputStream);
                                try {
                                    ScriptRunner runner = new ScriptRunner(dataBaseHelper.getConnection());
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
