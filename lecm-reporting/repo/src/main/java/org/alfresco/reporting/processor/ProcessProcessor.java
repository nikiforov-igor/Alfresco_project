package org.alfresco.reporting.processor;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.alfresco.reporting.ReportLine;
import org.alfresco.reporting.ReportingHelper;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.reporting.processor.PropertyProcessor;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProcessProcessor extends PropertyProcessor {

   private String START_DATE_TYPE = "datetime";
   private String END_DATE_TYPE = "datetime";
   private String DUE_DATE_TYPE = "datetime";
   private String PROCESS_DEFINITION_TYPE = "noderef";
   private String DESCRIPTION_TYPE = "text";
   private String PRIORITY_TYPE = "long";
   private String IS_ACTIVE_TYPE = "boolean";
   private String START_DATE_KEY = "start_date";
   private String END_DATE_KEY = "end_date";
   private String DUE_DATE_KEY = "due_date";
   private String PROCESS_DEFINITION_KEY = "process_definition";
   private String DESCRITION_KEY = "description";
   private String PRIORITY_KEY = "priority";
   private String IS_ACTIVE_KEY = "is_active";
   private WorkflowService workflowService;
   private static Log logger = LogFactory.getLog(ProcessProcessor.class);


   public ProcessProcessor(ServiceRegistry serviceRegistry, DatabaseHelperBean dbhb, ReportingHelper reportingHelper) throws Exception {
      this.workflowService = serviceRegistry.getWorkflowService();
      this.setNodeService(serviceRegistry.getNodeService());
      this.setDictionaryService(serviceRegistry.getDictionaryService());
      this.setFileFolderService(serviceRegistry.getFileFolderService());
      this.setReportingHelper(reportingHelper);
      this.setDbhb(dbhb);
      this.setClassToColumnType(reportingHelper.getClassToColumnType());
      this.setReplacementDataTypes(reportingHelper.getReplacementDataType());
      this.setGlobalProperties(reportingHelper.getGlobalProperties());
      this.setNamespaces(reportingHelper.getNameSpaces());
      this.setBlacklist(reportingHelper.getBlacklist());
   }

   private String getPropertyValue(Serializable s, String dtype, boolean multiValued) {
      return "";
   }

   private ReportLine processToReportingLine(ReportLine rl, String key, String type, String value) {
      if(!this.blacklist.contains("," + key + ",") && !type.equals("-")) {
         rl.setLine(key, this.getClassToColumnType().getProperty(type, "-"), value, this.getReplacementDataType());
         logger.debug("processNodeToMap: Found " + key + "=" + value + " (" + this.getClassToColumnType().getProperty(type, "-") + ")");
      }

      return rl;
   }

   protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
      this.dbhb.fixTableColumnName(table);

      try {
         logger.debug("Enter processNodeToMap");
         rl.setLine("sys_node_uuid", "VARCHAR(50)", identifier, this.getReplacementDataType());
         WorkflowInstance wfi = this.workflowService.getWorkflowById(identifier);
         String processDefinition = wfi.getDefinition().getId();
         String description = wfi.getDescription();
         Date dueDate = wfi.getDueDate();
         Date endDate = wfi.getEndDate();
         int priority = wfi.getPriority().intValue();
         Date startDate = wfi.getStartDate();
         rl = this.processToReportingLine(rl, this.START_DATE_KEY, this.START_DATE_TYPE, this.getSimpleDateFormat().format(startDate));
         rl = this.processToReportingLine(rl, this.END_DATE_KEY, this.END_DATE_TYPE, this.getSimpleDateFormat().format(endDate));
         rl = this.processToReportingLine(rl, this.DUE_DATE_KEY, this.DUE_DATE_TYPE, this.getSimpleDateFormat().format(dueDate));
         rl = this.processToReportingLine(rl, this.PROCESS_DEFINITION_KEY, this.PROCESS_DEFINITION_TYPE, this.getSimpleDateFormat().format(processDefinition));
         rl = this.processToReportingLine(rl, this.DESCRITION_KEY, this.DESCRIPTION_TYPE, this.getSimpleDateFormat().format(description));
         rl = this.processToReportingLine(rl, this.PRIORITY_KEY, this.PRIORITY_TYPE, String.valueOf(priority));
         rl = this.processToReportingLine(rl, this.IS_ACTIVE_KEY, this.IS_ACTIVE_TYPE, String.valueOf(priority));
      } catch (Exception var14) {
         var14.printStackTrace();
      }

      return rl;
   }

   private void storeProcessProperties(ReportLine rl) {
      logger.debug("Current method=" + this.method);

      try {
         if(rl.size() > 0) {
            if(this.method.equals("INSERT_ONLY")) {
               this.dbhb.insertIntoTable(rl);
            }

            if(this.method.equals("SINGLE_INSTANCE")) {
               if(this.dbhb.rowExists(rl)) {
                  this.dbhb.updateIntoTable(rl);
               } else {
                  this.dbhb.insertIntoTable(rl);
               }
            }

            if(this.method.equals("UPDATE_VERSIONED")) {
               if(logger.isDebugEnabled()) {
                  logger.debug("Going UPDATE_VERSIONED");
               }

               if(this.dbhb.rowExists(rl)) {
                  this.dbhb.updateVersionedIntoTable(rl);
               } else {
                  this.dbhb.insertIntoTable(rl);
               }
            }
         }
      } catch (Exception var7) {
         logger.fatal(var7);
         var7.printStackTrace();
      } finally {
         rl.reset();
      }

   }

   public Properties processQueueDefinition(String taskTable) {
      Properties definitions = new Properties();
      definitions.setProperty(this.START_DATE_KEY, this.getClassToColumnType().getProperty(this.START_DATE_TYPE, "-"));
      definitions.setProperty(this.END_DATE_KEY, this.getClassToColumnType().getProperty(this.END_DATE_TYPE, "-"));
      definitions.setProperty(this.DUE_DATE_KEY, this.getClassToColumnType().getProperty(this.DUE_DATE_TYPE, "-"));
      definitions.setProperty(this.DESCRITION_KEY, this.getClassToColumnType().getProperty(this.DESCRIPTION_TYPE, "-"));
      definitions.setProperty(this.PRIORITY_KEY, this.getClassToColumnType().getProperty(this.PRIORITY_TYPE, "-"));
      definitions.setProperty(this.PROCESS_DEFINITION_KEY, this.getClassToColumnType().getProperty(this.PROCESS_DEFINITION_TYPE, "-"));
      return definitions;
   }

   public void processQueueValues(String table) throws Exception {
      table = this.dbhb.fixTableColumnName(table);
      logger.debug("processQueueValues: pocessing " + this.getQueue().size() + " entries");
      ReportLine rl = new ReportLine(table, this.getSimpleDateFormat(), this.reportingHelper);
      Iterator queueIterator = this.getQueue().iterator();

      while(queueIterator.hasNext()) {
         String taskId = (String)queueIterator.next();
         if(logger.isDebugEnabled()) {
            logger.debug("processQueueValues: pocessing " + taskId);
         }

         rl = this.processNodeToMap(taskId, table, rl);
         this.storeProcessProperties(rl);
      }

   }

   public void havestNodes() {
      this.dbhb.openReportingConnection();
      if(this.getGlobalProperties().getProperty("system.workflow.engine.activiti.enabled", "true").toLowerCase().equals("true")) {
         if(logger.isDebugEnabled()) {
            logger.debug("Harvesting Activiti workflowTasks");
         }

         Object formattedDate = null;
         List myProcessList = this.dbhb.getCreatedProcesses((String)formattedDate);
         if(logger.isDebugEnabled()) {
            logger.debug("Found " + myProcessList.size() + " started workflow istancess...");
         }

         Iterator myProcessListIterator = myProcessList.iterator();

         while(myProcessListIterator.hasNext()) {
            this.addToQueue("activiti$" + (String)myProcessListIterator.next());
         }

         myProcessList = this.dbhb.getCompletedProcesses((String)formattedDate);
         if(logger.isDebugEnabled()) {
            logger.debug("Found " + myProcessList.size() + " deleted workflow instancess...");
         }

         myProcessListIterator = myProcessList.iterator();

         while(myProcessListIterator.hasNext()) {
            this.addToQueue("activiti$" + (String)myProcessListIterator.next());
         }

         if(logger.isDebugEnabled()) {
            logger.debug("Found total of " + this.getQueue().size() + " workflow instancess...");
         }
      }

      this.dbhb.closeReportingConnection();
   }

   void havestNodes(NodeRef harvestDefinition) {}

}
