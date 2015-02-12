package org.alfresco.reporting.mybatis.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.reporting.mybatis.*;
import org.apache.commons.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

public class ReportingDAOImpl implements ReportingDAO {

   private String LASTSCCESSFULRUN = "lastsuccessfulrun";
   private String TYPE_TABLES = "type_tables";
   private static Log logger = org.apache.commons.logging.LogFactory.getLog(ReportingDAOImpl.class);
   private SqlSession template = null;


   public void init() {
      try {
         LogFactory.useLog4JLogging();
         this.template.getConnection().setAutoCommit(true);
      } catch (SQLException var2) {
         throw new AlfrescoRuntimeException(var2.getMessage());
      }
   }

   public void openConnection() throws SQLException {
      if(this.template.getConnection().isClosed()) {
         this.template.getConnection().getTransactionIsolation();
      }

   }

   public void closeConnection() throws SQLException {}

   public void createLastTimestampTable() {
      try {
         this.template.getConnection().setAutoCommit(true);
         SelectFromWhere eee = new SelectFromWhere((String)null, this.LASTSCCESSFULRUN.toLowerCase(), (String)null);
         String url = this.template.getConnection().getMetaData().getURL();
         String database = url.substring(url.lastIndexOf("/") + 1, url.length());
         eee.setDatabase(database);
         if(((Integer)this.template.selectOne("table-exists", eee)).intValue() == 0) {
            this.template.insert("lastrun-create-empty-table");
         }
      } catch (Exception var4) {
         logger.fatal("@@@@ createLastTimestampTable Exception!: " + var4.getMessage());
      }

   }

    @Override
    public void createTypeTablesTable() {
        try {
            this.template.getConnection().setAutoCommit(true);
            SelectFromWhere eee = new SelectFromWhere(null, this.TYPE_TABLES.toLowerCase(), null);
            String url = this.template.getConnection().getMetaData().getURL();
            String database = url.substring(url.lastIndexOf("/") + 1, url.length());
            eee.setDatabase(database);
            if((Integer) this.template.selectOne("table-exists", eee) == 0) {
                this.template.insert("type-tables-create-empty-table");
            }
        } catch (Exception var4) {
            logger.fatal("@@@@ createTypeTablesTable Exception!: " + var4.getMessage());
        }
    }

    public void createLastTimestampTableRow(String tablename) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter createLastTimestampTableRow: " + tablename.toLowerCase());
      }

      LastRunDefinition lrd = new LastRunDefinition(tablename.toLowerCase(), (String)null, (String)null);

      try {
         this.template.getConnection().setAutoCommit(true);
         int e = this.template.insert("lastrun-insertTablename", lrd);
         logger.debug("createLastTimestampTableRow: inserted: lastrun-insertTablename " + e);
      } catch (Exception var6) {
         try {
            this.createLastTimestampTable();
            this.template.insert("lastrun-insertTablename", lrd);
            logger.debug("createLastTimestampTableRow: inserted: lastrun-insertTablename AND created entire table");
         } catch (Exception var5) {
            logger.fatal("@@@@ createLastTimestampTableRow Exception!: " + var5.getMessage());
         }
      }

   }

    @Override
    public void createTypeTablesRow(String tablename, String typename) {
        if(logger.isDebugEnabled()) {
            logger.debug("enter createTypeTablesRow: " + tablename.toLowerCase() + "," + typename);
        }

        TypeTableDefinition lrd = new TypeTableDefinition(tablename.toLowerCase(), typename.toLowerCase());

        try {
            this.template.getConnection().setAutoCommit(true);
            int e = this.template.insert("type-tables-insertValue", lrd);
            logger.debug("createTypeTablesRow: inserted: type-tables-insertValue " + e);
        } catch (Exception var6) {
            try {
                this.createTypeTablesTable();
                this.template.insert("type-tables-insertValue", lrd);
                logger.debug("createTypeTablesRow: inserted: type-tables-insertValue AND created entire table");
            } catch (Exception var5) {
                logger.fatal("@@@@ createTypeTablesRow Exception!: " + var5.getMessage());
            }
        }
    }

    public void clearLastRunTimestamp(String tablename) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter clearLastRunTimestamp: " + tablename.toLowerCase());
      }

      LastRunDefinition lrd = new LastRunDefinition(tablename.toLowerCase(), (String)null, "");

      try {
         this.template.getConnection().setAutoCommit(true);
         int e = this.template.insert("lastrun-cleanTimestampTablename", lrd);
         logger.debug("cleanTimestampTablename: updated: " + e);
      } catch (Exception var4) {
         logger.fatal("@@@@ cleanTimestampTablename Exception!: " + var4.getMessage());
      }

   }

   public int getNumberOfRowsForTable(String tablename) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter getNumberOfRowsForTable: " + tablename.toLowerCase());
      }

      int returnInt = 0;

      try {
         SelectFromWhere eee = new SelectFromWhere((String)null, tablename.toLowerCase(), (String)null);
         returnInt = ((Integer)this.template.selectOne("show-table-count", eee)).intValue();
         eee = null;
      } catch (Exception var4) {
         logger.fatal("@@@@ getNumberOfRowsForTable Exception!: " + var4.getMessage());
      }

      if(logger.isDebugEnabled()) {
         logger.debug("exit getNumberOfRowsForTable: " + returnInt);
      }

      return returnInt;
   }

   public String selectLastRunForTable(String tablename) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter selectLastRunForTable: " + tablename.toLowerCase());
      }

      String returnString = "";

      try {
         returnString = (String)this.template.selectOne("lastrun-selectLastRunForTable", tablename.toLowerCase());
      } catch (Exception var4) {
         logger.fatal("@@@@ selectLastRunForTable Exception!: " + var4.getMessage());
      }

      return returnString;
   }

   public boolean lastRunTableIsRunning() {
      if(logger.isDebugEnabled()) {
         logger.debug("enter lastRunTableIsRunning");
      }

      boolean returnBoolean = true;

      try {
         Object e = this.template.selectOne("lastrun-table-is-running");
         logger.debug("lastRunTableIsRunning: mybatis returning: " + e);
         returnBoolean = 0 != ((Integer)e).intValue();
      } catch (Exception var5) {
         returnBoolean = false;

         try {
            this.createLastTimestampTable();
         } catch (Exception var4) {
            logger.fatal("@@@@ lastRunTableIsRunning Exception!: " + var4.getMessage());
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("lastRunTableIsRunning returning: " + returnBoolean);
      }

      return returnBoolean;
   }

   public void updateLastSuccessfulRunDateForTable(String tableName, String status, String timestamp) {
      LastRunDefinition lrd = new LastRunDefinition(tableName.toLowerCase(), status, timestamp);
      Integer returnInt = Integer.valueOf(0);
      if(logger.isDebugEnabled()) {
         logger.debug("updateLastSuccessfulRunDateForTable enter; table=" + lrd.getTablename() + ", time: " + lrd.getLastrun() + ", status: " + lrd.getStatus());
      }

      try {
         this.template.getConnection().setAutoCommit(true);
         returnInt = Integer.valueOf(this.template.update("lastrun-updateLastSuccessfulRunDateForTable", lrd));
         logger.debug("updateLastSuccessfulRunDateForTable ### updated rows: " + returnInt);
         if(returnInt.intValue() != 1) {
            throw new AlfrescoRuntimeException("There were no rows updated, try an Insert statement instead!");
         }

         logger.debug("updateLastSuccessfulRunDateForTable updated the timestamp for " + lrd.getTablename() + " into " + lrd.getLastrun());
      } catch (Exception var9) {
         try {
            this.createLastTimestampTableRow(lrd.getTablename());
            returnInt = Integer.valueOf(this.template.update("lastrun-updateLastSuccessfulRunDateForTable", lrd));
            logger.debug("updateLastSuccessfulRunDateForTable created the row AND updated the timestamp for " + lrd.getTablename() + " into " + lrd.getLastrun());
         } catch (Exception var8) {
            logger.fatal("@@@@ updateLastSuccessfulRunDateForTable Exception!: " + var8.getMessage());
         }
      }

   }

   public void updateLastSuccessfulRunStatusForTable(String tableName, String status) {
      LastRunDefinition lrd = new LastRunDefinition(tableName.toLowerCase(), status, (String)null);
      Integer returnInt = Integer.valueOf(0);
      if(logger.isDebugEnabled()) {
         logger.debug("updateLastSuccessfulRunStatusForTable enter");
      }

      try {
         this.template.getConnection().setAutoCommit(true);
         returnInt = Integer.valueOf(this.template.update("lastrun-updateLastSuccessfulRunStatusForTable", lrd));
         logger.debug("updateLastSuccessfulRunStatusForTable ### number of updated rows: " + returnInt);
         if(returnInt.intValue() == 0) {
            throw new AlfrescoRuntimeException("There were no rows updated, try an Insert statement instead!");
         }

         logger.debug("updateLastSuccessfulRunStatusForTable updated " + lrd.getTablename() + " the status to " + lrd.getStatus());
      } catch (Exception var8) {
         try {
            this.createLastTimestampTableRow(lrd.getTablename());
            returnInt = Integer.valueOf(this.template.update("lastrun-updateLastSuccessfulRunStatusForTable", lrd));
            logger.debug("updateLastSuccessfulRunStatusForTable created the row, AND updated " + lrd.getTablename() + " the status to " + lrd.getStatus());
         } catch (Exception var7) {
            logger.fatal("@@@@ updateLastSuccessfulRunStatusForTable Exception!: " + var7.getMessage());
         }
      }

   }

   public String getLastSuccessfulRunDateForTable(String tablename) {
      String table = tablename.toLowerCase();
      if(logger.isDebugEnabled()) {
         logger.debug("getLastSuccessfulRunDateForTable enter for table " + table);
      }

      LastRunDefinition lrd = new LastRunDefinition(table, (String)null, (String)null);
      String theDate = "";

      try {
         theDate = (String)this.template.selectOne("lastrun-getLastSuccessfulRunDateForTable", lrd);
         if(theDate == null) {
            theDate = "";
         }

         logger.debug("getLastSuccessfulRunDateForTable got the lastRunDate: " + theDate);
      } catch (Exception var8) {
         try {
            this.createLastTimestampTableRow(lrd.getTablename());
            theDate = "";
            logger.debug("getLastSuccessfulRunDateForTable added the TableRow, ADN got empty string");
         } catch (Exception var7) {
            logger.fatal("@@@@ getLastSuccessfulRunDateForTable Exception!: " + var7.getMessage());
         }
      }

      lrd = null;
      return theDate;
   }

   public String getLastSuccessfulRunStatusForTable(String tablename) {
      String table = tablename.toLowerCase();
      if(logger.isDebugEnabled()) {
         logger.debug("getLastSuccessfulRunStateForTable enter for table " + table);
      }

      LastRunDefinition lrd = new LastRunDefinition(table, (String)null, (String)null);
      String status = "";

      try {
         status = (String)this.template.selectOne("lastrun-getLastSuccessfulRunStatusForTable", lrd);
         if(status == null) {
            status = "";
         }

         logger.debug("getLastSuccessfulRunStateForTable got the lastRunStatus: " + status);
      } catch (Exception var8) {
         try {
            this.createLastTimestampTableRow(lrd.getTablename());
            status = "";
            logger.debug("getLastSuccessfulRunStateForTable added the TableRow, ADN got empty string");
         } catch (Exception var7) {
            logger.fatal("@@@@ getLastSuccessfulRunStateForTable Exception!: " + var7.getMessage());
         }
      }

      return status;
   }

   public void setAllStatusesDoneForTable() {
      try {
         this.template.getConnection().setAutoCommit(true);
         int eee = Integer.valueOf(this.template.update("lastrun-updateLastSuccessfulRunStatusesDoneForTable")).intValue();
         logger.debug("setAllStatusesDoneForTable: updated " + eee + " lost statusses");
      } catch (Exception var2) {
         logger.fatal("@@@@ setAllStatusesDoneForTable Exception!: " + var2.getMessage());
      }

   }

   public void pushPreliminaryFunctions() {
      this.template.insert("push-preliminary-functions");
   }

   public void createEmtpyTable(String tablename) {
      String from = tablename.toLowerCase();
      if(logger.isDebugEnabled()) {
         logger.debug("enter createEmtpyTable: " + from);
      }

      String sequencename = from + "_seq";
      String triggername = from + "_trigger";
      SelectFromWhere sfw = new SelectFromWhere(sequencename, from, triggername);

      try {
         String e = this.template.getConnection().getMetaData().getURL();
         String database = e.substring(e.lastIndexOf("/") + 1, e.length());
         sfw.setDatabase(database);
         this.template.getConnection().setAutoCommit(true);
         if(logger.isDebugEnabled()) {
            logger.debug("enter createEmtpyTable: checking if table already exists...");
         }

         if(((Integer)this.template.selectOne("table-exists", sfw)).intValue() == 0) {
            this.template.insert("reporting-create-empty-table", sfw);
         }

         logger.debug("exit createEmtpyTable " + sfw.getFrom());
      } catch (Exception var8) {
         logger.debug("## " + var8.getMessage());
         var8.printStackTrace();
      }

   }

   public List getShowTables() {
      if(logger.isDebugEnabled()) {
         logger.debug("enter getShowTables");
      }

      List results = this.template.selectList("show-tables");
      if(logger.isDebugEnabled()) {
         for(int i = 0; i < results.size(); ++i) {
            logger.debug(" +" + (String)results.get(i));
         }

         logger.debug("exit getShowTables");
      }

      return results;
   }

   public Properties getDescTable(String tablename) {
      String from = tablename.toLowerCase();
      Properties props = new Properties();
      SelectFromWhere sfw = new SelectFromWhere((String)null, from, (String)null);

      try {
         String eee = this.template.getConnection().getMetaData().getURL();
         String database = eee.substring(eee.lastIndexOf("/") + 1, eee.length());
         if(logger.isDebugEnabled()) {
            logger.debug("$$$ getDescTable: Database appears to be: " + database);
         }

         sfw.setDatabase(database);
         if(logger.isDebugEnabled()) {
            logger.debug("getDescTable: before selectList");
         }

         List myList = this.template.selectList("describe-table", sfw);
         if(logger.isDebugEnabled()) {
            logger.debug("getDescTable after selectList");
         }

         String key = "";
         Iterator listIterator = myList.iterator();

         while(listIterator.hasNext()) {
            Map map = (Map)listIterator.next();
            if(logger.isDebugEnabled()) {
               logger.debug("Map=" + map);
            }

            Iterator mapIterator = map.keySet().iterator();
            if(logger.isDebugEnabled()) {
               logger.debug("getDescTable: Map Iterator constructed");
            }

            String theKey = "";
            String theValue = "";

            while(mapIterator.hasNext()) {
               key = (String)mapIterator.next();
               if(logger.isDebugEnabled()) {
                  logger.debug("Key=" + key);
               }

               if("COLUMN_NAME".equalsIgnoreCase(key)) {
                  theKey = ((String)map.get(key)).toLowerCase();
               }

               if("COLUMN_TYPE".equalsIgnoreCase(key)) {
                  theValue = (String)map.get(key);
               }
            }

            if(logger.isDebugEnabled()) {
               logger.debug("getDescTable: processed key  =" + theKey);
               logger.debug("getDescTable: processed value=" + theValue);
            }

            props.setProperty(theKey, theValue);
            if(logger.isDebugEnabled()) {
               logger.debug("getDescTable: done processing key=value");
            }
         }
      } catch (Exception var14) {
         logger.fatal("@@@@ getDescTable Exception!: " + var14.getMessage());
      }

      if(logger.isDebugEnabled()) {
         logger.debug("exit getDescTable returning " + props);
      }

      return props;
   }

   public void extendTableDefinition(ReportingColumnDefinition rcd) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter extendTableDefinition");
      }

      rcd.setTablename(rcd.getTablename().toLowerCase());

      try {
         this.template.getConnection().setAutoCommit(true);
         this.template.update("reporting-extendTableDefinition", rcd);
      } catch (Exception var3) {
         logger.fatal("@@@@ extendTableDefinition Exception!: " + var3.getMessage());
      }

      if(logger.isDebugEnabled()) {
         logger.debug("enter extendTableDefinition");
      }

   }

   public boolean reportingRowExists(SelectFromWhere sfw) {
      sfw.setFrom(sfw.getFrom().toLowerCase());
      int i = ((Integer)this.template.selectOne("reporting-row-exists", sfw)).intValue();
      return i > 0;
   }

   public boolean reportingRowEqualsModifiedDate(SelectFromWhere sfw) {
      sfw.setFrom(sfw.getFrom().toLowerCase());
      int i = ((Integer)this.template.selectOne("reporting-row-equals-modified-date", sfw)).intValue();
      return i > 0;
   }

   public boolean reportingRowVersionedEqualsModifiedDate(SelectFromWhere sfw) {
      sfw.setFrom(sfw.getFrom().toLowerCase());
      int i = ((Integer)this.template.selectOne("reporting-row-versioned-equals-modified-date", sfw)).intValue();
      return i > 0;
   }

   public int reportingInsertIntoTable(InsertInto insertInto) {
      insertInto.setTablename(insertInto.getTablename().toLowerCase());
      if(logger.isDebugEnabled()) {
         logger.debug("insert into : " + insertInto.getTablename());
         logger.debug("insert keys : " + insertInto.getKeys());
         logger.debug("insert value: " + insertInto.getValues());
      }

      int i = 0;

      try {
         this.template.getConnection().setAutoCommit(true);
         i = this.template.insert("reporting-insert-into-table", insertInto);
      } catch (Exception var4) {
         logger.fatal("@@@@ reportingInsertIntoTable Exception!: " + var4.getMessage());
      }

      return i;
   }

   public int reportingUpdateIntoTable(UpdateWhere updateWhere) {
      int i = 0;

      try {
         updateWhere.setTablename(updateWhere.getTablename().toLowerCase());
         if(logger.isDebugEnabled()) {
            logger.debug("update into   : " + updateWhere.getTablename());
            logger.debug("update update : " + updateWhere.getUpdateClause());
            logger.debug("update where  : " + updateWhere.getWhereClause());
         }

         this.template.getConnection().setAutoCommit(true);
         i = this.template.update("reporting-update-into-table", updateWhere);
      } catch (Exception var4) {
         logger.fatal("@@@@ reportingUpdateIntoTable Exception!: " + var4.getMessage());
      }

      return i;
   }

   public int reportingUpdateVersionedIntoTable(UpdateWhere updateWhere) {
      updateWhere.setTablename(updateWhere.getTablename().toLowerCase());
      if(logger.isDebugEnabled()) {
         logger.debug(" update into   : " + updateWhere.getTablename());
         logger.debug(" update update : " + updateWhere.getUpdateClause());
         logger.debug(" update where  : " + updateWhere.getWhereClause());
      }

      int i = 0;

      try {
         this.template.getConnection().setAutoCommit(true);
         i = this.template.update("reporting-update-versioned-into-table-reset-islatest", updateWhere);
      } catch (Exception var4) {
         logger.fatal("@@@@ reportingUpdateVersionedIntoTable Exception!: " + var4.getMessage());
      }

      return i;
   }

    @Override
    public int deleteFromTable(UpdateWhere updateWhere) {
        updateWhere.setTablename(updateWhere.getTablename().toLowerCase());
        if(logger.isDebugEnabled()) {
            logger.debug(" update into   : " + updateWhere.getTablename());
            logger.debug(" update update : " + updateWhere.getUpdateClause());
            logger.debug(" update where  : " + updateWhere.getWhereClause());
        }

        int i = 0;

        try {
            this.template.getConnection().setAutoCommit(true);
            i = this.template.delete("reporting-delete-from-table", updateWhere);
        } catch (Exception var4) {
            logger.fatal("@@@@ reportingUpdateVersionedIntoTable Exception!: " + var4.getMessage());
        }

        return i;
    }

    public List reportingSelectIsLatestPerTable(String tablename) {
      String from = tablename.toLowerCase();
      SelectFromWhere sfw = new SelectFromWhere((String)null, from, (String)null);
      List myTable = this.template.selectList("reporting-select-islatest-per-table", sfw);
      sfw = null;
      return myTable;
   }

    @Override
    public List reportingSelectTypesPerTable(String tablename) {
        TypeTableDefinition sfw = new TypeTableDefinition(tablename.toLowerCase(), null);
        return this.template.selectList("type-tables-types-per-table", sfw);
    }

    @Override
    public List reportingSelectTablesPerType(String typename) {
        TypeTableDefinition sfw = new TypeTableDefinition(null, typename.toLowerCase());
        return this.template.selectList("type-tables-tables-per-type", sfw);
    }

    @Override
    public Connection getConnection() {
        return this.template.getConnection();
    }

    public String reportingSelectFromWhere(String select, String tablename, String where) {
      String from = tablename.toLowerCase();

      try {
         SelectFromWhere e = new SelectFromWhere(select, from, where);
         String url = this.template.getConnection().getMetaData().getURL();
         String database = url.substring(url.lastIndexOf("/") + 1, url.length());
         e.setDatabase(database);
         int tableCols = ((Integer)this.template.selectOne("table-exists", e)).intValue();
         return ((Integer)this.template.selectOne("table-exists", e)).intValue() > 0?(String)this.template.selectOne("reporting-select-from-where", e):null;
      } catch (SQLException var9) {
         logger.fatal("@@@@ reportingSelectFromWhere Exception!: " + var9.getMessage());
         return null;
      }
   }

   public String reportingSelectStatusFromLastsuccessfulrun(String tablename) {
      String from = tablename.toLowerCase();
      String status = null;
      SelectFromWhere sfw = new SelectFromWhere((String)null, from, (String)null);
      status = (String)this.template.selectOne("lastrun-select-status-for-table", sfw);
      sfw = null;
      return status;
   }

   public List reportingSelectStoreProtocolPerTable(String tablename) {
      String from = tablename.toLowerCase();
      SelectFromWhere sfw = new SelectFromWhere((String)null, from, (String)null);
      List myTable = this.template.selectList("reporting-select-store-protocol-per-table", sfw);
      return myTable;
   }

   public void dropTable(String tablename) {
      try {
         String eee = tablename.toLowerCase();
         this.template.getConnection().setAutoCommit(true);
         String sequencename = tablename + "_seq";
         String triggername = tablename + "_trigger";
         SelectFromWhere sfw = new SelectFromWhere(sequencename, eee, triggername);
         String url = this.template.getConnection().getMetaData().getURL();
         String database = url.substring(url.lastIndexOf("/") + 1, url.length());
         sfw.setDatabase(database);
         int numberOfColumns = ((Integer)this.template.selectOne("table-exists", sfw)).intValue();
         if(numberOfColumns > 0) {
            this.template.delete("dropTable", sfw);
         }

         sfw = null;
      } catch (Exception var9) {
         logger.fatal("@@@@ dropTable Exception!: " + var9.getMessage());
      }

   }

   public void setReportingTemplate(SqlSessionTemplate template) {
      this.template = template;
   }

}
