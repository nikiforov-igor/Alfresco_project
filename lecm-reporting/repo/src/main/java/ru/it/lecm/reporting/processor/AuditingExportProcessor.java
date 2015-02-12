package ru.it.lecm.reporting.processor;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.Constants;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.ReportingModel;
import ru.it.lecm.reporting.db.DatabaseHelperBean;
import ru.it.lecm.reporting.script.EntryIdCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuditingExportProcessor extends PropertyProcessor {

   protected AuditService auditService;
   private String vendor;
   private int actualAmount = 0;
   private long lastFromTime = 0L;
   private static Log logger = LogFactory.getLog(AuditingExportProcessor.class);


   public AuditingExportProcessor(DatabaseHelperBean dbhb, ReportingHelper reportingHelper, ServiceRegistry serviceRegistry) throws Exception {
      this.auditService = serviceRegistry.getAuditService();
      this.setNodeService(serviceRegistry.getNodeService());
      this.setDictionaryService(serviceRegistry.getDictionaryService());
      this.setFileFolderService(serviceRegistry.getFileFolderService());
      this.setSearchService(serviceRegistry.getSearchService());
      this.setReportingHelper(reportingHelper);
      this.setDbhb(dbhb);
      this.setClassToColumnType(reportingHelper.getClassToColumnType());
      this.setReplacementDataTypes(reportingHelper.getReplacementDataType());
      this.setGlobalProperties(reportingHelper.getGlobalProperties());
      this.setNamespaces(reportingHelper.getNameSpaces());
      this.setBlacklist(reportingHelper.getBlacklist());
      this.vendor = reportingHelper.getDatabaseProvider();
      if(logger.isDebugEnabled()) {
         logger.debug("##this.dataDictionary       =" + this.dataDictionary);
         logger.debug("##this.replacementDataTypes =" + this.replacementDataTypes);
         logger.debug("##this.namespaces           =" + this.namespaces);
      }

   }

   protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
      return null;
   }

   Properties processQueueDefinition(String table) {
      return null;
   }

   void processQueueValues(String table) throws Exception {}

   public void havestNodes(NodeRef harvestDefinition) {
      this.dbhb.openReportingConnection();

      try {
         String e = (String)this.getNodeService().getProperty(harvestDefinition, ReportingModel.PROP_REPORTING_AUDIT_QUERIES);
         if(e != null) {
            String[] tableNamesArray = e.split(",");
            String[] arr$ = tableNamesArray;
            int len$ = tableNamesArray.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               String tableName = arr$[i$];
               String fullTableName = this.dbhb.fixTableColumnName(tableName);
               if(logger.isDebugEnabled()) {
                  logger.debug("processing table " + fullTableName);
               }

               if(!this.dbhb.tableIsRunning(fullTableName)) {
                  Date theDate = new Date((new Date()).getTime() - 5000L);
                  String nowFormattedDate = this.reportingHelper.getSimpleDateFormat().format(theDate);
                  this.dbhb.setLastTimestampStatusRunning(fullTableName);
                  this.dbhb.createEmptyTables(fullTableName);
                  this.processAuditingExport(tableName.trim(), fullTableName);
                  this.dbhb.setLastTimestampAndStatusDone(fullTableName, nowFormattedDate);
               }
            }
         }
      } catch (Exception var14) {
         logger.fatal("Exception havestNodes: " + var14.getMessage());
         throw new AlfrescoRuntimeException(var14.getMessage());
      } finally {
         this.dbhb.closeReportingConnection();
      }

   }

   public void processAuditingExport(final String auditFeed, final String tableName) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter processAuditingExport " + auditFeed);
      }

      if(auditFeed != null && !"".equals(auditFeed.trim())) {
         String timestamp = this.dbhb.getLastTimestamp(tableName);
         if(timestamp != null && !"".equals(timestamp)) {
            timestamp = timestamp.replaceAll("T", " ").trim();
         }

         SimpleDateFormat format = Constants.getAuditDateFormat();
         Date startDate = new Date();

         try {
            startDate = format.parse(timestamp);
         } catch (ParseException var20) {
            var20.printStackTrace();
         }

         Long fromTime = Long.valueOf(startDate.getTime() + 1L);
         final ReportLine rl = new ReportLine(tableName, this.getSimpleDateFormat(), this.reportingHelper);
         final Properties replacementTypes = this.reportingHelper.getReplacementDataType();
         boolean letsContinue = true;

         try {
            if(logger.isDebugEnabled()) {
               logger.debug("processAuditingExport: Prepping table columns");
            }

            int e = Math.min(Integer.parseInt(this.globalProperties.getProperty("reporting.harvest.audit.maxResults", "50000")), Integer.MAX_VALUE);
            Properties definition = new Properties();
            definition.setProperty("sys_store_protocol", this.getClassToColumnType().getProperty("sys_store_protocol", "-"));
            definition.setProperty("event_timestamp", this.reportingHelper.getClassToColumnType().getProperty("datetime", "-"));
            definition.setProperty("username", this.reportingHelper.getClassToColumnType().getProperty("name", "-"));
            this.setTableDefinition(tableName, definition);

            for(EntryIdCallback changeLogCollectingCallback = new EntryIdCallback(true, rl, replacementTypes, tableName, auditFeed) {
               private String validateColumnName(String tablename) {
                  if(AuditingExportProcessor.logger.isDebugEnabled()) {
                     AuditingExportProcessor.logger.debug("enter validateColumnName: " + tablename);
                  }

                  String origTablename = tablename;
                  if(this.getCache().containsKey(tablename)) {
                     return this.getCache().getProperty(tablename);
                  } else {
                     String replaceChars = "/-:;\'.,;";
                     byte index = 10;

                     try {
                        for(int e = 0; e < replaceChars.length(); ++e) {
                           while(tablename.indexOf(replaceChars.charAt(e)) > -1) {
                              int var7 = tablename.indexOf(replaceChars.charAt(e));
                              if(var7 == 0) {
                                 tablename = tablename.substring(1, tablename.length());
                              } else if(var7 == tablename.length() - 1) {
                                 tablename = tablename.substring(0, tablename.length() - 2);
                              } else if(var7 < tablename.length() - 1 && var7 > -1) {
                                 tablename = tablename.substring(0, var7) + "_" + tablename.substring(var7 + 1, tablename.length());
                              }
                           }
                        }

                        if("Oracle".equalsIgnoreCase(AuditingExportProcessor.this.vendor) && tablename.length() > 30) {
                           tablename = tablename.substring(0, 30);
                        }

                        if(!this.getCache().containsKey(tablename)) {
                           this.addToCache(origTablename, tablename);
                        }
                     } catch (Exception var6) {
                        AuditingExportProcessor.logger.fatal("That\'s weird: index=" + index + " and length=" + tablename.length() + " " + tablename);
                        var6.getMessage();
                     }

                     if(AuditingExportProcessor.logger.isDebugEnabled()) {
                        AuditingExportProcessor.logger.debug("exit validateColumnName: " + tablename.toLowerCase());
                     }

                     return tablename.toLowerCase();
                  }
               }
               public boolean handleAuditEntry(Long entryId, String user, long time, Map values) {
                  SimpleDateFormat format = Constants.getAuditDateFormat();
                  Date theDate = new Date(time);
                  AuditingExportProcessor.this.lastFromTime = time;
                  Set theValues = null;
                  Properties numberOfRows;
                  if(values != null) {
                     theValues = values.keySet();
                     numberOfRows = new Properties();
                     Iterator e = theValues.iterator();

                     while(e.hasNext()) {
                        String i$ = (String)e.next();

                        try {
                           numberOfRows.setProperty(this.validateColumnName(i$), AuditingExportProcessor.this.reportingHelper.getClassToColumnType().getProperty("noderef", "-"));
                           AuditingExportProcessor.this.setTableDefinition(this.getTableName(), numberOfRows);
                        } catch (Exception var15) {
                           AuditingExportProcessor.logger.fatal("handleAuditEntry: UNABLE to process property from Values Map object");
                        }
                     }
                  }

                  try {
                     this.getRl().reset();
                     numberOfRows = AuditingExportProcessor.this.reportingHelper.getReplacementDataType();
                     Properties e1 = AuditingExportProcessor.this.reportingHelper.getClassToColumnType();
                     this.getRl().setLine("sys_node_uuid", AuditingExportProcessor.this.reportingHelper.getClassToColumnType().getProperty("noderef"), entryId.toString(), numberOfRows);
                     this.getRl().setLine("event_timestamp", e1.getProperty("datetime"), format.format(theDate).replaceAll(" ", "T"), numberOfRows);
                     this.getRl().setLine("username", e1.getProperty("name"), user, numberOfRows);
                     if(values != null) {
                        Iterator i$1 = theValues.iterator();

                        while(i$1.hasNext()) {
                           String value = (String)i$1.next();
                           AuditingExportProcessor.logger.debug("Setting value=" + value);
                           this.getRl().setLine(this.validateColumnName(value), e1.getProperty("noderef"), String.valueOf(values.get(value)), numberOfRows);
                        }
                     }
                  } catch (Exception var16) {
                     AuditingExportProcessor.logger.fatal("Setting values in ResultLine object failed...");
                     var16.printStackTrace();
                  }

                  boolean numberOfRows1 = false;

                  try {
                     int numberOfRows2;
                     if(AuditingExportProcessor.this.dbhb.rowExists(this.getRl())) {
                        numberOfRows2 = AuditingExportProcessor.this.dbhb.updateIntoTable(this.getRl());
                     } else {
                        numberOfRows2 = AuditingExportProcessor.this.dbhb.insertIntoTable(this.getRl());
                     }
                  } catch (SQLException var13) {
                     var13.printStackTrace();
                  } catch (Exception var14) {
                     var14.printStackTrace();
                  }

                  AuditingExportProcessor.access$308(AuditingExportProcessor.this);
                  return super.handleAuditEntry(entryId, user, time, values);
               }
            }; letsContinue; letsContinue = this.actualAmount == e) {
               this.actualAmount = 0;
               AuditQueryParameters params = new AuditQueryParameters();
               params.setApplicationName(auditFeed);
               params.setForward(true);
               params.setFromTime(fromTime);
               this.auditService.auditQuery(changeLogCollectingCallback, params, e);
               fromTime = Long.valueOf(this.lastFromTime);
               if(logger.isDebugEnabled()) {
                  logger.debug("After auditQuery actual=" + this.actualAmount + " max=" + e);
               }
            }
         } catch (org.json.simple.parser.ParseException var21) {
            var21.printStackTrace();
            throw new RuntimeException(var21);
         } catch (IOException var22) {
            var22.printStackTrace();
         } catch (Exception var23) {
            var23.printStackTrace();
         } finally {
            rl.reset();
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("exit processAuditingExport");
      }

   }

   // $FF: synthetic method
   static int access$308(AuditingExportProcessor x0) {
      return x0.actualAmount++;
   }

}
