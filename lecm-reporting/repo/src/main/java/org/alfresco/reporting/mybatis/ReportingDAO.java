package org.alfresco.reporting.mybatis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public interface ReportingDAO {

    void init();

    void openConnection() throws SQLException;

    void closeConnection() throws SQLException;

    void pushPreliminaryFunctions();

    boolean lastRunTableIsRunning();

    void updateLastSuccessfulRunDateForTable(String var1, String var2, String var3);

    void updateLastSuccessfulRunStatusForTable(String var1, String var2);

    String getLastSuccessfulRunDateForTable(String var1);

    String getLastSuccessfulRunStatusForTable(String var1);

    int getNumberOfRowsForTable(String var1);

    void createLastTimestampTable();

    void createTypeTablesTable();

    void createLastTimestampTableRow(String var1);

    void createTypeTablesRow(String var1, String var2);

    String selectLastRunForTable(String var1);

    void setAllStatusesDoneForTable();

    List getShowTables();

    Properties getDescTable(String var1);

    void dropTable(String var1);

    void clearLastRunTimestamp(String var1);

    void createEmtpyTable(String var1);

    void extendTableDefinition(ReportingColumnDefinition var1);

    boolean reportingRowExists(SelectFromWhere var1);

    boolean reportingRowEqualsModifiedDate(SelectFromWhere var1);

    boolean reportingRowVersionedEqualsModifiedDate(SelectFromWhere var1);

    int reportingUpdateIntoTable(UpdateWhere var1);

    int reportingUpdateVersionedIntoTable(UpdateWhere var1);

    int deleteFromTable(UpdateWhere var1);

    int reportingInsertIntoTable(InsertInto var1);

    String reportingSelectFromWhere(String var1, String var2, String var3);

    List reportingSelectStoreProtocolPerTable(String var1);

    String reportingSelectStatusFromLastsuccessfulrun(String var1);

    List reportingSelectIsLatestPerTable(String var1);

    List reportingSelectTypesPerTable(String tablename);

    List reportingSelectTablesPerType(String typename);

    Connection getConnection();
}
