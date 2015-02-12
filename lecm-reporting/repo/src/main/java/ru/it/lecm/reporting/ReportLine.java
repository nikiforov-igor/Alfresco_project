package ru.it.lecm.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class ReportLine {

   private Properties types = new Properties();
   private Properties values = new Properties();
   private String table = "";
   private String vendor;
   private ReportingHelper reportingHelper;
   private final SimpleDateFormat sdf;
   private static Log logger = LogFactory.getLog(ReportLine.class);


   public ReportLine(String table, SimpleDateFormat sdf, ReportingHelper reportingHelper) {
      this.vendor = reportingHelper.getDatabaseProvider();
      this.reportingHelper = reportingHelper;
      String myTable = table.trim();
      if("Oracle".equalsIgnoreCase(this.vendor) && myTable.length() > 30) {
         myTable = myTable.substring(0, 30);
      }

      this.setTable(table);
      this.sdf = sdf;
   }

   public void reset() {
      this.types = new Properties();
      this.values = new Properties();
   }

   public void setLine(String key, String sqltype, String value, Properties replacementTypes) {
      if(value != null && !value.equalsIgnoreCase("null") && !value.equalsIgnoreCase("~~null~~") && !key.equals("isLatest")) {
         if(key != null && sqltype != null) {
            String mySqltype = sqltype.trim();
            value = value.trim();
            String myKey = this.reportingHelper.getTableColumnNameTruncated(key);
            if("Oracle".equals(this.vendor) && myKey.equalsIgnoreCase("size")) {
               myKey = "docsize".toLowerCase();
            }

            if(replacementTypes.containsKey(myKey)) {
               mySqltype = replacementTypes.getProperty(myKey, "-").trim();
            }

            if(mySqltype.toUpperCase().startsWith("VARCHAR")) {
               try {
                  if(logger.isDebugEnabled()) {
                     ;
                  }

                  int e = Integer.parseInt(mySqltype.substring(mySqltype.indexOf("(") + 1, mySqltype.length() - 1));
                  if(value.length() > e) {
                     value = value.substring(0, e - 2);
                  }
               } catch (Exception var8) {
                  var8.printStackTrace();
                  logger.fatal("Error in processing VARCHAR!!");
                  logger.fatal(var8.getMessage());
               }
            }

            this.types.setProperty(myKey, mySqltype);
            this.values.setProperty(myKey, value);
         }

      } else {
         logger.debug("setLine *************** NULL ****************");
      }
   }

   public void setTable(String table) {
      this.table = table.toLowerCase().replaceAll("-", "_").replaceAll(" ", "_");
   }

   public String getType(String key) {
      String type = key;
      if(key.equalsIgnoreCase("size") && this.vendor.equalsIgnoreCase("Oracle")) {
         type = "docsize";
      }

      return this.types.getProperty(type);
   }

   public boolean hasValue(String key) {
      String returnString = this.values.getProperty(key);
      return returnString != null;
   }

   public String getValue(String key) {
      String returnString;
      if(key.equalsIgnoreCase("cm_modified") && this.values.getProperty("sys_archiveddate") != null) {
         returnString = this.values.getProperty("sys_archiveddate");
      } else {
         returnString = this.values.getProperty(key);
      }

      if(returnString != null) {
         returnString = returnString.replaceAll("\'", "_").replaceAll("\"", "_");
      }

      return returnString;
   }

   public int size() {
      return this.types.size();
   }

   public Enumeration getKeys() {
      return this.types.keys();
   }

   public String getTable() {
      return this.table;
   }

   public String getUpdateSet() {
      String returnString = "";
      String ignoreKey = "sys_node_uuid";
      Enumeration keys = this.getKeys();

      while(keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         if(ignoreKey.indexOf(key) < 0) {
            if(returnString != "") {
               returnString = returnString + ", ";
            }

            String value = this.getValue(key);
            String type = this.getType(key);
            if("Oracle".equalsIgnoreCase(this.vendor)) {
               returnString = returnString + key.toLowerCase() + "=" + this.formatValue(type, value);
            } else {
               returnString = returnString + key + "=" + this.formatValue(type, value);
            }
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("SQL-update:" + returnString);
      }

      return returnString;
   }

   private String formatValue(String type, String value) {
      String returnString = "\'\'";
      boolean wasBoolean = false;
      if(!"~~NULL~~".equals(value) && !"-".equals(value)) {
         logger.debug("formatValue: type=" + type + " -was- " + value + " & vendor=" + this.vendor);
         if("Oracle".equalsIgnoreCase(this.vendor) && "NUMBER(1)".equalsIgnoreCase(type)) {
            if("true".equalsIgnoreCase(value)) {
               returnString = "1";
            } else {
               returnString = "0";
            }

            return returnString;
         }

         if("PostgreSQL".equalsIgnoreCase(this.vendor) && "SMALLINT".equalsIgnoreCase(type)) {
            if("true".equalsIgnoreCase(value)) {
               returnString = "1";
            } else {
               returnString = "0";
            }

            return returnString;
         }

         if("MySQL".equalsIgnoreCase(this.vendor) && "TINYINT".equalsIgnoreCase(type)) {
            if("true".equalsIgnoreCase(value)) {
               returnString = "1";
            } else {
               returnString = "0";
            }

            return returnString;
         }

         String compareType = "";
         if(type.indexOf("(") > 0) {
            compareType = type.substring(0, type.indexOf("("));
         } else {
            compareType = type;
         }

         if(",BIGINT,BOOLEAN,NUMBER(,INTEGER,DOUBLE PRECISION,BINARY_DOUBLE,BINARY_FLOAT,PLS_INTEGER,LONG".indexOf(compareType.toUpperCase()) > -1 && !wasBoolean) {
            return !"".equals(value) ? value : "NULL";
         }

         if(!"DATETIME".equalsIgnoreCase(type) && !"DATE".equalsIgnoreCase(type) && !"TIMESTAMP".equalsIgnoreCase(type)) {
            if(!wasBoolean) {
               logger.debug("It is a String/VARCHAR");
               return "\'" + value + "\'";
            }
         } else {
            if("Oracle".equalsIgnoreCase(this.vendor)) {
               value = value.replaceAll("T", " ");
               return "TO_DATE(\'" + value + "\',\'YYYY-MM-DD HH24:MI:SS\')";
            }

            try {
               Date e = new Date(Long.parseLong(value));
               returnString = "\'" + this.sdf.format(e).replace(" ", "T") + "\'";
            } catch (Exception var7) {
               returnString = "\'" + value + "\'";
            }
         }
      } else {
         returnString = "NULL";
      }

      if(!wasBoolean && !type.contains("VARCHAR") && "".equals(value)) {
         returnString = "NULL";
      }

      return returnString;
   }

   public String getInsertListOfKeys() {
      String returnString = "";
      String key = "";
      Enumeration keys = this.getKeys();

      while(keys.hasMoreElements()) {
         if(returnString != "") {
            returnString = returnString + ", ";
         }

         key = (String)keys.nextElement();
         if("Oracle".equalsIgnoreCase(this.vendor)) {
            returnString = returnString + key.toLowerCase();
         } else {
            returnString = returnString + key;
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("SQL-keys :" + returnString);
      }

      return returnString;
   }

   public String getInsertListOfValues() {
      String returnString = "";

      String value;
      String type;
      for(Enumeration keys = this.getKeys(); keys.hasMoreElements(); returnString = returnString + this.formatValue(type, value)) {
         String key = (String)keys.nextElement();
         if(returnString != "") {
            returnString = returnString + ", ";
         }

         value = this.getValue(key);
         type = this.getType(key);
      }

      if(logger.isDebugEnabled()) {
         logger.debug("SQL-values:" + returnString);
      }

      return returnString;
   }

}
