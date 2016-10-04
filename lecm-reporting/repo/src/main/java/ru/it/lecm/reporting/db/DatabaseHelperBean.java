package ru.it.lecm.reporting.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.mybatis.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseHelperBean {

    private static Log logger = LogFactory.getLog(DatabaseHelperBean.class);
    private Properties globalProperties;
    private ReportingDAO reportingDAO;
    private WorkflowDAO workflowDAO;
    private ReportingHelper reportingHelper;

    private ReportingHelper getReportingHelper() {
        return this.reportingHelper;
    }

    public void setReportingHelper(ReportingHelper reportingHelper) {
        this.reportingHelper = reportingHelper;
    }

    public void setWorkflowDAOImpl(WorkflowDAO workflowDAO) {
        this.workflowDAO = workflowDAO;
    }

    public void setReportingDAOImpl(ReportingDAO reportingDAO) {
        this.reportingDAO = reportingDAO;
    }

    public void setProperties(Properties properties) {
        this.globalProperties = properties;
    }

    public ReportingDAO getReportingDAO() {
        return reportingDAO;
    }


    public String fixTableColumnName(String inName) {
        return this.reportingHelper.getValidTableName(inName);
    }

    public Map getShowTables() {
        Map<String, String> sm = new HashMap<>();
        List tables = this.reportingDAO.getShowTables();
        logger.debug("Found show tables results: " + tables.size());

        for (Object table : tables) {
            String tablename = (String) table;
            logger.debug("Processing table " + tablename);
            int amount = this.reportingDAO.getNumberOfRowsForTable(tablename);
            logger.debug("Returned " + amount);
            sm.put(tablename, String.valueOf(amount));
        }

        return sm;
    }

    public Map getShowTablesDetails() {
        HashMap sm = new HashMap();
        List tableList = this.reportingDAO.getShowTables();

        for (Object aTableList : tableList) {
            String tablename = (String) aTableList;
            if (tablename != null &&
                    !"associations".equalsIgnoreCase(tablename) &&
                    !"lastsuccessfulrun".equalsIgnoreCase(tablename) &&
                    !"deleted".equalsIgnoreCase(tablename) &&
                    !"type_tables".equalsIgnoreCase(tablename)) {
                tablename = tablename.trim();
                String status = this.reportingDAO.reportingSelectStatusFromLastsuccessfulrun(tablename);
                String timestamp = this.getLastTimestamp(tablename);
                logger.debug("Current Status for " + tablename + "=" + status);
                if (logger.isDebugEnabled()) {
                    logger.debug("Processing table >" + tablename + "<");
                }

                String totaal = "";
                String isLatest = "0";
                String isNonLatest = "0";
                String isWorkSpace = "0";
                String isArchive = "0";
                totaal = String.valueOf(this.reportingDAO.getNumberOfRowsForTable(tablename));
                logger.debug("Rows for table " + tablename + ": " + totaal);
                if (!"Oracle".equalsIgnoreCase(this.reportingHelper.getDatabaseProvider()) && !"0".equals(totaal)) {
                    logger.debug("Lets prep for reportingSelectIsLatestPerTable");
                    List rowList = this.reportingDAO.reportingSelectIsLatestPerTable(tablename);
                    logger.debug("Done reportingSelectIsLatestPerTable: " + rowList.size());
                    Iterator rowIterator = rowList.iterator();

                    Map e;
                    Iterator columnIterator;
                    boolean isArchiveKey;
                    boolean isWorkspaceKey;
                    boolean isArchiveSet;
                    boolean isWorkspaceSet;
                    String keyString;
                    while (rowIterator.hasNext()) {
                        e = (Map) rowIterator.next();
                        columnIterator = e.keySet().iterator();
                        isArchiveKey = false;
                        isWorkspaceKey = false;
                        isArchiveSet = false;
                        isWorkspaceSet = false;

                        while (columnIterator.hasNext()) {
                            keyString = (String) columnIterator.next();
                            logger.debug("isLatest key=" + keyString + " value=" + e.get(keyString));
                            if ("islatest".equalsIgnoreCase(keyString) && e.get(keyString).toString().equals("1")) {
                                isArchiveKey = true;
                            }

                            if ("islatest".equalsIgnoreCase(keyString) && e.get(keyString).toString().equals("0")) {
                                isWorkspaceKey = true;
                            }

                            if (keyString.toLowerCase().startsWith("count")) {
                                if (isArchiveKey && !isArchiveSet) {
                                    isLatest = String.valueOf(e.get(keyString));
                                    logger.debug("Setting isLatest=" + e.get(keyString));
                                    isArchiveKey = false;
                                    isArchiveSet = true;
                                }

                                if (isWorkspaceKey && !isWorkspaceSet) {
                                    isNonLatest = String.valueOf(e.get(keyString));
                                    logger.debug("Setting isNonLatest=" + e.get(keyString));
                                    isWorkspaceKey = false;
                                    isWorkspaceSet = true;
                                }
                            }
                        }
                    }

                    logger.debug("isLatest=" + isLatest + " isNonLatest=" + isNonLatest);

                    try {
                        rowList = this.reportingDAO.reportingSelectStoreProtocolPerTable(tablename);
                        logger.debug("Done reportingSelectStoreProtocolPerTable: " + rowList.size());
                        rowIterator = rowList.iterator();

                        while (rowIterator.hasNext()) {
                            try {
                                e = (Map) rowIterator.next();
                                columnIterator = e.keySet().iterator();
                                isArchiveKey = false;
                                isWorkspaceKey = false;
                                isArchiveSet = false;
                                isWorkspaceSet = false;

                                while (columnIterator.hasNext()) {
                                    keyString = (String) columnIterator.next();
                                    logger.debug("SpaceStore key=" + keyString + " value=" + e.get(keyString));
                                    if ("sys_store_protocol".equalsIgnoreCase(keyString) && "archive".equalsIgnoreCase(e.get(keyString).toString())) {
                                        isArchiveKey = true;
                                    }

                                    if ("sys_store_protocol".equalsIgnoreCase(keyString) && "workspace".equalsIgnoreCase(e.get(keyString).toString())) {
                                        isWorkspaceKey = true;
                                    }

                                    if (keyString.toLowerCase().startsWith("count")) {
                                        if (isArchiveKey && !isArchiveSet) {
                                            isArchive = String.valueOf(e.get(keyString));
                                            logger.debug("Setting archive=" + e.get(keyString));
                                            isArchiveKey = false;
                                            isArchiveSet = true;
                                        }

                                        if (isWorkspaceKey && !isWorkspaceSet) {
                                            isWorkSpace = String.valueOf(e.get(keyString));
                                            logger.debug("Setting worksace=" + e.get(keyString));
                                            isWorkspaceKey = false;
                                            isWorkspaceSet = true;
                                        }
                                    }
                                }

                                logger.debug("setting isWorkspace=" + isWorkSpace + " isArchive=" + isArchive);
                            } catch (Exception ignored) {
                                logger.error(ignored.getMessage(), ignored);
                            }
                        }
                    } catch (Exception var24) {
                        logger.info("Trying to process spacestore column:  " + var24.toString());
                    }
                }

                logger.debug("Through with a single loop");
                sm.put(tablename, timestamp + "," + status + "," + totaal + "," + isLatest + "," + isNonLatest + "," + isWorkSpace + "," + isArchive);
            }
        }

        return sm;
    }

    public void init() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting init - " + this.reportingHelper.getDatabaseProvider());
        }

        try {
            Map e = this.getShowTables();
            TreeSet ss = new TreeSet(e.keySet());
            Iterator keys = ss.iterator();
            if (ss.size() == 0) {
                logger.info("  No reporting tables to display...");
            } else {
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    logger.info("  " + key + " (" + e.get(key) + ")");
                }
            }
        } catch (Exception var5) {
            logger.warn("Reporting table information could not be retrieved!!");
            logger.fatal("Exception was: " + var5.getMessage());
        }

    }

    public List getCreatedTasks(String fromDate) {
        return this.workflowDAO.getCreatedTasks(fromDate);
    }

    public List getDeletedTasks(String fromDate) {
        return this.workflowDAO.getDeletedTasks(fromDate);
    }

    public HashMap getPropertiesForWorkflowTask(String id) {
        return this.workflowDAO.getPropertiesForWorkflowTask(id);
    }

    public List getCreatedProcesses(String fromDate) {
        return this.workflowDAO.getCreatedProcesses(fromDate);
    }

    public List getCompletedProcesses(String fromDate) {
        return this.workflowDAO.getCompletedProcesses(fromDate);
    }

    public boolean isEnabled() {
        return !this.globalProperties.getProperty("reporting.enabled", "true").equalsIgnoreCase("false");
    }

    public void extendTable(String table, String column, String type) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("enter extendTable colum=" + column + " type=" + type);
        }

        try {
            table = this.fixTableColumnName(table);
            ReportingColumnDefinition e = new ReportingColumnDefinition(table, column, type);
            if (logger.isDebugEnabled()) {
                logger.debug("extendTable: prep starting reportinggDAO");
            }

            this.reportingDAO.extendTableDefinition(e);
        } catch (Exception var5) {
            logger.fatal("@@@@ Exception: extendTable: " + table + " | " + column + " | " + type);
            logger.fatal("@@@@ " + var5.getMessage());
            throw new Exception(var5);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("exit extendTable");
        }
    }

    public String selectFromWhere(String select, String from, String where) throws Exception {
        String returnString;

        try {
            returnString = this.reportingDAO.reportingSelectFromWhere(select, from.toLowerCase(), where);
            return returnString;
        } catch (Exception var6) {
            logger.fatal("Exception selectFromWhere: select=" + select + " from=" + from + " where=" + where);
            logger.fatal(var6.getMessage());
            throw new Exception(var6);
        }
    }

    public boolean rowExists(ReportLine rl) throws Exception {
        boolean returnValue;

        try {
            SelectFromWhere e = new SelectFromWhere(null, rl.getTable().toLowerCase(), rl.getValue("sys_node_uuid"));
            returnValue = this.reportingDAO.reportingRowExists(e);
            if (logger.isDebugEnabled()) {
                logger.debug("rowExists returning " + returnValue);
            }

            return returnValue;
        } catch (Exception var4) {
            logger.fatal("Exception rowExists: " + var4.getMessage());
            throw new Exception(var4);
        }
    }

    private boolean rowEqualsModifiedDate(ReportLine rl, String lastModified) throws Exception {
        boolean returnValue;
        logger.debug("rowEqualsModifiedDate: lastModified=" + lastModified + " vs. " + rl.getValue("cm_modified"));

        try {
            SelectFromWhere e = new SelectFromWhere(null, rl.getTable().toLowerCase(), rl.getValue("sys_node_uuid"));
            e.setAndwhere(lastModified);
            if (rl.hasValue("cm_versionlabel")) {
                e.setAndandwhere(rl.getValue("cm_versionlabel").toLowerCase());
                returnValue = this.reportingDAO.reportingRowVersionedEqualsModifiedDate(e);
                if (logger.isDebugEnabled()) {
                    logger.debug("rowEqualsModifiedDate: Versioned! VersionLabel=" + rl.getValue("cm_versionlabel") + " returns: " + returnValue);
                }
            } else {
                returnValue = this.reportingDAO.reportingRowEqualsModifiedDate(e);
                if (logger.isDebugEnabled()) {
                    logger.debug("rowEqualsModifiedDate: returns: " + returnValue);
                }
            }
        } catch (Exception var5) {
            logger.fatal("Exception rowEqualsModifiedDate: " + var5.getMessage());
            throw new Exception(var5);
        }

        logger.debug("exit rowEqualsModifiedDate: " + returnValue);
        return returnValue;
    }

    public int updateVersionedIntoTable(ReportLine rl) throws Exception {
        logger.debug("enter updateVersionedIntoTable modified=" + rl.getValue("cm_modified") + " vs. " + rl.getValue("sys_archiveddate"));

        try {
            if (!this.rowEqualsModifiedDate(rl, rl.getValue("cm_modified"))) {
                try {
                    logger.debug("updateVersionedIntoTable table=" + rl.getTable());
                    logger.debug("updateVersionedIntoTable vaidUntil=" + rl.getValue("cm_modified"));
                    logger.debug("updateVersionedIntoTable sys_ode_uuid=" + rl.getValue("sys_node_uuid"));
                    String e1 = "\'" + rl.getValue("cm_modified") + "\'";
                    if ("Oracle".equalsIgnoreCase(this.reportingHelper.getDatabaseProvider())) {
                        e1 = "TO_DATE(" + e1 + ",\'yyyy-MM-dd HH24:MI:SS\')";
                    }

                    UpdateWhere updateWhere = new UpdateWhere(rl.getTable(), "validUntil=" + e1 + "", "sys_node_uuid LIKE \'" + rl.getValue("sys_node_uuid") + "\'");
                    this.reportingDAO.reportingUpdateVersionedIntoTable(updateWhere);
                    logger.debug("exit updateVersionedIntoTable");
                    return this.insertIntoTable(rl);
                } catch (Exception var5) {
                    logger.fatal("Exception updateVersionedIntoTable1: " + var5.getMessage());
                    throw new Exception(var5);
                }
            } else {
                logger.debug("exit updateVersionedIntoTable 0");
                return 0;
            }
        } catch (Exception var6) {
            logger.fatal("Exception updateVersionedIntoTable2: " + var6.getMessage());
            throw new Exception(var6);
        }
    }

    public int updateIntoTable(ReportLine rl) throws Exception {
        logger.debug("enter updateIntoTable");

        try {
            UpdateWhere e = new UpdateWhere(rl.getTable(), rl.getUpdateSet(), "sys_node_uuid LIKE \'" + rl.getValue("sys_node_uuid") + "\'");
            int myInt1 = this.reportingDAO.reportingUpdateIntoTable(e);
            logger.debug("exit updateIntoTable " + myInt1);
            return myInt1;
        } catch (Exception var4) {
            logger.fatal("Exception updateIntoTable: " + var4.getMessage());
            throw new Exception(var4);
        }
    }

    public int insertIntoTable(ReportLine rl) throws Exception {
        logger.debug("enter insertIntoTable");

        int myInt1;
        try {
            logger.debug("### sys_store_protocol=" + rl.getValue("sys_store_protocol"));
            Properties e = this.getReportingHelper().getClassToColumnType();
            Properties replacementTypes = this.getReportingHelper().getReplacementDataType();
            String insertInto;
            String validUntil;
            if ("archive".equals(rl.getValue("sys_store_protocol"))) {
                insertInto = rl.getValue("cm_created");
                validUntil = rl.getValue("sys_archiveddate");
                rl.setLine("isLatest", e.getProperty("boolean", "-"), "false", replacementTypes);
                rl.setLine("validFrom", e.getProperty("datetime", "-"), insertInto, replacementTypes);
                rl.setLine("validUntil", e.getProperty("datetime", "-"), validUntil, replacementTypes);
            } else if (rl.getValue("noderef") != null && rl.getValue("noderef").startsWith("version")) {
                insertInto = rl.getValue("cm_created");
                validUntil = rl.getValue("cm_modified");
                rl.setLine("isLatest", e.getProperty("boolean", "-"), "false", replacementTypes);
                rl.setLine("validFrom", e.getProperty("datetime", "-"), insertInto, replacementTypes);
                rl.setLine("validUntil", e.getProperty("datetime", "-"), validUntil, replacementTypes);
            } else {
                insertInto = rl.getValue("cm_modified");
                rl.setLine("validFrom", e.getProperty("datetime", "-"), insertInto, replacementTypes);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("insertIntoTable table=" + rl.getTable());
                logger.debug("insertIntoTable keys=" + rl.getInsertListOfKeys());
                logger.debug("insertIntoTable values=" + rl.getInsertListOfValues());
            }

            InsertInto insertInto1 = new InsertInto(rl.getTable(), rl.getInsertListOfKeys(), rl.getInsertListOfValues());
            myInt1 = this.reportingDAO.reportingInsertIntoTable(insertInto1);
        } catch (Exception var7) {
            logger.fatal("Exception insertIntoTable: " + var7.getMessage());
            throw new Exception(var7);
        }

        logger.debug("exit insertIntoTable " + myInt1);
        return myInt1;
    }

    public void dropTables(String tablesToDrop) throws Exception {
        logger.debug("Enter dropTables: " + tablesToDrop);

        try {
            String[] e = tablesToDrop.toLowerCase().split(",");
            for (String anE : e) {
                String table = anE;
                table = this.fixTableColumnName(table.trim());
                logger.debug("dropTables: before: " + table);
                this.reportingDAO.dropTable(table);
                logger.debug("dropTables: after: " + table);
            }
        } catch (Exception var6) {
            logger.fatal("Exception dropTables: " + var6.getMessage());
            throw new Exception(var6);
        }

        logger.debug("Exit dropTables: " + tablesToDrop);
    }

    public void createEmptyTables(String tablesToCreate) throws Exception {
        logger.debug("Starting createEmptyTables: " + tablesToCreate);

        try {
            String[] e = tablesToCreate.toLowerCase().split(",");

            for (String anE : e) {
                String table = anE;
                table = this.fixTableColumnName(table);
                if (logger.isDebugEnabled()) {
                    logger.debug("createEmptyTables: now table " + table.trim());
                }

                this.reportingDAO.createEmtpyTable(table.trim());
            }
        } catch (Exception var6) {
            logger.fatal("Exception createEmptyTables: " + var6.getMessage());
            throw new Exception(var6);
        }
    }

    public void createEmptyTypeTablesTable() throws Exception {
        logger.debug("Starting createEmptyTypeTablesTable");

        try {
            this.reportingDAO.createTypeTablesTable();
        } catch (Exception var6) {
            logger.fatal("Exception createEmptyTypeTablesTable: " + var6.getMessage());
            throw new Exception(var6);
        }
    }

    public void createLastTypesTableRow(String tableName, String typeName) {
        tableName = this.fixTableColumnName(tableName).toLowerCase();
        if (logger.isDebugEnabled()) {
            logger.debug("enter createLastTypesTableRow table=" + tableName + ", type=" + typeName);
        }
        this.reportingDAO.createTypeTablesRow(tableName, typeName);
        if (logger.isDebugEnabled()) {
            logger.debug("enter createLastTypesTableRow table=" + tableName + ", type=" + typeName);
        }
    }

    public List getTypesPerTable(String tableName) throws Exception {
        return this.reportingDAO.reportingSelectTypesPerTable(tableName);
    }

    public List getTablesPerType(String typeName) throws Exception {
        return this.reportingDAO.reportingSelectTablesPerType(typeName);
    }

    public Properties getTableDescription(String table) throws Exception {
        new Properties();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Starting getTableDescription");
            }

            table = this.fixTableColumnName(table);
            return this.reportingDAO.getDescTable(table);
        } catch (Exception var4) {
            logger.fatal("Exception getTableDescription: " + var4.getMessage());
            throw new Exception(var4);
        }
    }

    public void setAllStatusesDoneForTable() {
        this.reportingDAO.setAllStatusesDoneForTable();
    }

    public void resetLastTimestampTable(String tableName) {
        tableName = this.fixTableColumnName(tableName).toLowerCase();
        logger.debug("enter resetLastTimestampTable table=" + tableName);
        this.reportingDAO.updateLastSuccessfulRunStatusForTable(tableName, "Done");
    }

    public void clearLastTimestampTable(String tableName) {
        tableName = this.fixTableColumnName(tableName).toLowerCase();
        logger.debug("enter clearLastTimestampTable table=" + tableName);
        this.reportingDAO.clearLastRunTimestamp(tableName);
    }

    public String getLastTimestampStatus(String tablename) {
        tablename = this.fixTableColumnName(tablename);
        logger.debug("enter setLastTimestampStatus table=" + tablename);
        String returnString = this.reportingDAO.getLastSuccessfulRunDateForTable(tablename);
        logger.debug("exit getLastTimestampStatus returning: " + returnString);
        return returnString;
    }

    public String getLastTimestamp(String tableName) {
        tableName = this.fixTableColumnName(tableName).toLowerCase();
        logger.debug("enter getLastTimestamp table=" + tableName);
        String returnString = this.reportingDAO.getLastSuccessfulRunDateForTable(tableName);
        logger.debug("getLastTimestamp (" + tableName + ") returns " + returnString);
        if (returnString == null || returnString.trim().equals("")) {
            SimpleDateFormat format = this.reportingHelper.getSimpleDateFormat();
            Date myDate = new Date(1L);
            returnString = format.format(myDate);
            returnString = returnString.replaceAll(" ", "T").trim();
        }

        logger.debug("exit getLastTimestamp returning " + returnString);
        return returnString;
    }

    public boolean tableIsRunning(String tableName) {
        boolean returnBoolean = this.reportingDAO.lastRunTableIsRunning();
        logger.debug("exit tableIsRunning (" + tableName + ") returning " + returnBoolean);
        return returnBoolean;
    }

    public void setLastTimestampAndStatusDone(String tableName, String timestamp) {
        tableName = this.fixTableColumnName(tableName).toLowerCase();
        logger.debug("enter setLastTimestamp for table " + tableName + " timestamp=" + timestamp);
        this.reportingDAO.updateLastSuccessfulRunDateForTable(tableName, "Done", timestamp);
    }

    public void setLastTimestampStatusRunning(String tableName) {
        tableName = this.fixTableColumnName(tableName);
        logger.debug("enter setLastTimestampStatusRunning: for table=" + tableName);
        this.reportingDAO.updateLastSuccessfulRunStatusForTable(tableName, "Running");
        logger.debug("exit setLastTimestampStatusRunning");
    }

    public void createLastTimestampTableRow(String tableName) {
        tableName = this.fixTableColumnName(tableName).toLowerCase();
        logger.debug("enter createLastTimestampTableRow table=" + tableName);
        this.reportingDAO.createLastTimestampTableRow(tableName);
        logger.debug("exit createLastTimestampTableRow");
    }

    public void dropLastTimestampTable() {
        logger.debug("enter dropLastTimestampTable table=lastsuccessfulrun");
        this.reportingDAO.dropTable("lastsuccessfulrun");
    }

    public void setLastTimestampAndStatus(String tableName, String status, String timestamp) {
        tableName = this.fixTableColumnName(tableName).toLowerCase();
        logger.debug("enter setLastTimestamp for table " + tableName + " timestamp=" + timestamp);
        this.reportingDAO.updateLastSuccessfulRunStatusAndDateForTable(tableName, status, timestamp);
    }

    public void createEmptyAssocsTable() throws Exception {
        logger.debug("Starting createEmptyAssocsTable");

        try {
            this.reportingDAO.createAssocsTable();
        } catch (Exception var6) {
            logger.fatal("Exception createEmptyAssocsTable: " + var6.getMessage());
            throw new Exception(var6);
        }
    }
}
