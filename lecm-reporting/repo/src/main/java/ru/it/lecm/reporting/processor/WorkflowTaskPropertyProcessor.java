package ru.it.lecm.reporting.processor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.db.DatabaseHelperBean;

import java.io.Serializable;
import java.util.*;

public class WorkflowTaskPropertyProcessor extends PropertyProcessor {

   protected WorkflowService workflowService;
   private static Log logger = LogFactory.getLog(WorkflowTaskPropertyProcessor.class);
   private String DELETE_REASON_DB_COLUMN_TYPE = "VARCHAR(50)";
   private String PROCESS_ID_DB_COLUMN_TYPE = "VARCHAR(20)";
   private String TASK_DEF_KEY_DB_COLUMN_TYPE = "VARCHAR(100)";
   private String FILES_ATTACHED_COLUMN_TYPE = "noderefs";
   private String DURATION_COLUMN_TYPE = "int";
   private String NUMBER_OF_FILES_COLUMN_TYPE = "int";
   private String MODIFIED_COLUMN_TYPE = "datetime";


   public WorkflowTaskPropertyProcessor(ServiceRegistry serviceRegistry, DatabaseHelperBean dbhb, ReportingHelper reportingHelper) throws Exception {
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
      logger.debug("Enter getPropertyValue");
      String returnValue = "";
      if(multiValued && !"category".equals(dtype)) {
         ArrayList var9 = (ArrayList)s;
         if(var9 != null && !var9.isEmpty() && var9.size() > 0) {
            int i$;
            if(dtype.equals("date") || dtype.equals("datetime")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + this.getSimpleDateFormat().format((Date)var9.get(i$)) + ",";
               }
            }

            if(dtype.equals("id") || dtype.equals("long")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + Long.toString(((Long)var9.get(i$)).longValue()) + ",";
               }
            }

            if(dtype.equals("int")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + Integer.toString(((Integer)var9.get(i$)).intValue()) + ",";
               }
            }

            if(dtype.equals("float") || dtype.equals("double")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + Double.toString(((Double)var9.get(i$)).doubleValue()) + ",";
               }
            }

            if(dtype.equals("boolean")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + Boolean.toString(((Boolean)var9.get(i$)).booleanValue()) + ",";
               }
            }

            if(dtype.equals("text")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + (String)var9.get(i$) + ",";
               }
            }

            if(dtype.equals("noderef")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + var9.get(i$).toString() + ",";
               }
            }

            if(returnValue.equals("")) {
               for(i$ = 0; i$ < var9.size(); ++i$) {
                  returnValue = returnValue + (String)var9.get(i$) + ",";
               }
            }
         }
      } else if(s != null && !"category".equals(dtype)) {
         if(dtype.equals("date") || dtype.equals("datetime")) {
            Calendar categories = Calendar.getInstance();
            categories.setTimeInMillis(((Date)s).getTime());
            returnValue = this.getSimpleDateFormat().format((Date)s);
         }

         if(dtype.equals("id") || dtype.equals("long")) {
            returnValue = Long.toString(((Long)s).longValue());
         }

         if(dtype.equals("int")) {
            returnValue = Integer.toString(((Integer)s).intValue());
         }

         if(dtype.equals("float") || dtype.equals("double")) {
            returnValue = Double.toString(((Double)s).doubleValue());
         }

         if(dtype.equals("boolean")) {
            returnValue = Boolean.toString(((Boolean)s).booleanValue());
         }

         if(dtype.equals("text")) {
            returnValue = s.toString();
         }

         if(dtype.equals("noderef")) {
            returnValue = s.toString();
         }

         if(returnValue.equals("")) {
            returnValue = s.toString();
         }
      }

      if(dtype.equals("category")) {
         logger.debug("Found a category!");
         List var10 = (List)s;
         String catName;
         if(var10 != null) {
            for(Iterator var11 = var10.iterator(); var11.hasNext(); returnValue = returnValue + catName) {
               NodeRef cat = (NodeRef)var11.next();
               catName = this.getNodeService().getProperty(cat, ContentModel.PROP_NAME).toString();
               if(returnValue.length() > 0) {
                  returnValue = returnValue + ",";
               }
            }
         }
      }

      logger.debug("Exit getPropertyValue, returning: " + returnValue);
      return returnValue;
   }

   protected ReportLine processNodeToMap(String identifier, String table, ReportLine rl) {
      this.dbhb.fixTableColumnName(table);

      try {
         logger.debug("Enter processNodeToMap");
         rl.setLine("sys_node_uuid", "VARCHAR(50)", identifier, this.getReplacementDataType());
         WorkflowTask e = this.workflowService.getTaskById(identifier);
         Map map = e.getProperties();
         Iterator keys = map.keySet().iterator();

         String taskId;
         String task_def_key;
         while(keys.hasNext()) {
            String bpm_package = "";
            String bpmi = "";

            try {
               QName bpmString = (QName)keys.next();
               bpm_package = bpmString.toString();
               bpm_package = this.replaceNameSpaces(bpm_package);
               bpmi = this.dictionaryService.getProperty(bpmString).getDataType().toString();
               bpmi = bpmi.substring(bpmi.indexOf("}") + 1, bpmi.length()).trim();
               taskId = this.getClassToColumnType().getProperty(bpmi, "-");
               String results = taskId.toString();
               boolean proc_ref_id = false;
               proc_ref_id = this.dictionaryService.getProperty(bpmString).isMultiValued();
               logger.debug("processNodeToMap: EVAL: key=" + bpm_package + ", type=" + results + ", dtype=" + bpmi + " multi=" + proc_ref_id);
               if(!this.blacklist.contains("," + bpm_package + ",") && !results.equals("-")) {
                  task_def_key = this.getPropertyValue((Serializable)map.get(bpmString), bpmi, proc_ref_id);
                  rl.setLine(bpm_package, results, task_def_key, this.getReplacementDataType());
                  logger.debug("processNodeToMap: Found " + bpm_package + "=" + task_def_key);
               }
            } catch (Exception var17) {
               logger.info("processNodeToMap: Error in object, property " + bpm_package + " not found! (" + bpmi + ")");
            }
         }

         List bpm_package1 = this.workflowService.getPackageContents(identifier);
         rl.setLine("number_of_files_attached", this.getClassToColumnType().getProperty(this.NUMBER_OF_FILES_COLUMN_TYPE), String.valueOf(bpm_package1.size()), this.getReplacementDataType());
         Iterator bpmi1 = bpm_package1.iterator();

         String bpmString1;
         NodeRef taskId1;
         for(bpmString1 = ""; bpmi1.hasNext(); bpmString1 = bpmString1 + taskId1.toString()) {
            taskId1 = (NodeRef)bpmi1.next();
            if(bpmString1.length() > 0) {
               bpmString1 = bpmString1 + ",";
            }
         }

         rl.setLine("files_attached", this.getClassToColumnType().getProperty(this.FILES_ATTACHED_COLUMN_TYPE), bpmString1, this.getReplacementDataType());
         taskId = "";
         if(identifier.contains("$")) {
            taskId = identifier.substring(identifier.indexOf("$") + 1, identifier.length());
         }

         logger.debug("Just before, trying with id=" + taskId);
         HashMap results1 = this.dbhb.getPropertiesForWorkflowTask(taskId);
         String proc_ref_id1 = (String)results1.get("proc_ref_id_");
         if(proc_ref_id1 != null) {
            rl.setLine("process_id", this.PROCESS_ID_DB_COLUMN_TYPE, proc_ref_id1, this.getReplacementDataType());
         }

         task_def_key = (String)results1.get("task_def_key_");
         if(task_def_key != null) {
            rl.setLine("task_def_key", this.TASK_DEF_KEY_DB_COLUMN_TYPE, task_def_key, this.getReplacementDataType());
         }

         String delete_reason = (String)results1.get("delete_reason_");
         if(delete_reason != null) {
            rl.setLine("delete_reason", this.DELETE_REASON_DB_COLUMN_TYPE, delete_reason, this.getReplacementDataType());
         }

         String duration = "";
         if(null != results1.get("duration_")) {
            duration = String.valueOf(results1.get("duration_"));
            rl.setLine("duration", this.getClassToColumnType().getProperty(this.DURATION_COLUMN_TYPE), duration, this.getReplacementDataType());
         }

         String now = this.getSimpleDateFormat().format(new Date());
         now = now.replaceAll(" ", "T").trim();
         rl.setLine("cm_modified", this.getClassToColumnType().getProperty(this.MODIFIED_COLUMN_TYPE), now, this.getReplacementDataType());
      } catch (Exception var18) {
         var18.printStackTrace();
      }

      return rl;
   }

   private void storeTaskProperties(ReportLine rl) {
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

   protected Properties processTaskNodeDefinition(String identifier, Properties definition) {
      try {
         WorkflowTask e = this.workflowService.getTaskById(identifier);
         Map map = e.getProperties();
         Iterator keys = map.keySet().iterator();

         while(keys.hasNext()) {
            String key = "";
            String dtype = "";

            try {
               QName e1 = (QName)keys.next();
               key = e1.toString();
               key = this.replaceNameSpaces(key);
               dtype = this.dictionaryService.getProperty(e1).getDataType().toString();
               dtype = dtype.substring(dtype.indexOf("}") + 1, dtype.length()).trim();
               String theObject = this.getClassToColumnType().getProperty(dtype, "-");
               String type = theObject.toString();
               boolean multiValued = false;
               multiValued = this.dictionaryService.getProperty(e1).isMultiValued();
               if(logger.isDebugEnabled()) {
                  logger.debug("processTaskNodeDefinition: EVAL: key=" + key + ", type=" + type + ", dtype=" + dtype + " multi=" + multiValued);
               }

               if(!this.blacklist.contains("," + key + ",") && !type.equals("-")) {
                  definition.setProperty(key, type);
                  if(logger.isDebugEnabled()) {
                     logger.debug("processTaskNodeDefinition: Found " + key + "=" + type);
                  }
               }
            } catch (Exception var12) {
               logger.error("processTaskNodeDefinition: Error in object, property " + key + " not found! (" + dtype + ")");
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

      return definition;
   }

   public Properties processQueueDefinition(String taskTable) {
      this.dbhb.fixTableColumnName(taskTable);
      Properties definitions = new Properties();
      if(logger.isDebugEnabled()) {
         logger.debug("processQueueDefinition: pocessing " + this.getQueue().size() + " entries");
      }

      String taskId;
      for(Iterator queueIterator = this.getQueue().iterator(); queueIterator.hasNext(); definitions = this.processTaskNodeDefinition(taskId, definitions)) {
         taskId = (String)queueIterator.next();
         if(logger.isDebugEnabled()) {
            logger.debug("processQueueDefinition: pocessing " + taskId);
         }
      }

      if(definitions == null) {
         definitions = new Properties();
      }

      definitions.setProperty("number_of_files_attached", this.getClassToColumnType().getProperty(this.NUMBER_OF_FILES_COLUMN_TYPE));
      definitions.setProperty("files_attached", this.getClassToColumnType().getProperty(this.FILES_ATTACHED_COLUMN_TYPE));
      definitions.setProperty("process_id", this.PROCESS_ID_DB_COLUMN_TYPE);
      definitions.setProperty("task_def_key", this.TASK_DEF_KEY_DB_COLUMN_TYPE);
      definitions.setProperty("delete_reason", this.DELETE_REASON_DB_COLUMN_TYPE);
      definitions.setProperty("duration", this.getClassToColumnType().getProperty(this.DURATION_COLUMN_TYPE));
      definitions.setProperty("cm_modified", this.getClassToColumnType().getProperty(this.MODIFIED_COLUMN_TYPE));
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
         this.storeTaskProperties(rl);
      }

   }

   public void havestNodes() {
      if(this.getGlobalProperties().getProperty("system.workflow.engine.activiti.enabled", "true").toLowerCase().equals("true")) {
         if(logger.isDebugEnabled()) {
            logger.debug("Harvesting Activiti workflowTasks");
         }

         this.dbhb.openReportingConnection();
         String nowFormattedDate = this.reportingHelper.getSimpleDateFormat().format(new Date());
         String formattedDate = this.dbhb.getLastTimestamp("workflowtask");
         List myTaskList = this.dbhb.getCreatedTasks(formattedDate);
         if(logger.isDebugEnabled()) {
            logger.debug("Found " + myTaskList.size() + " started workflow tasks...");
         }

         Iterator myTaskListIterator = myTaskList.iterator();

         while(myTaskListIterator.hasNext()) {
            this.addToQueue("activiti$" + (String)myTaskListIterator.next());
         }

         myTaskList = this.dbhb.getDeletedTasks(formattedDate);
         if(logger.isDebugEnabled()) {
            logger.debug("Found " + myTaskList.size() + " deleted workflow tasks...");
         }

         myTaskListIterator = myTaskList.iterator();

         while(myTaskListIterator.hasNext()) {
            this.addToQueue("activiti$" + (String)myTaskListIterator.next());
         }

         if(logger.isDebugEnabled()) {
            logger.debug("Found total of " + this.getQueue().size() + " workflow tasks...");
         }

         this.dbhb.closeReportingConnection();
      }

   }

   void havestNodes(NodeRef harvestDefinition) {}

}
