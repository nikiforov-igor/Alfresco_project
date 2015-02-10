package org.alfresco.reporting.action.executer;

import java.util.List;
import java.util.Properties;
import javax.transaction.SystemException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.reporting.execution.JasperReporting;
import org.alfresco.reporting.execution.Reportable;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReportExecuter extends ActionExecuterAbstractBase {

   public static final String NAME = "report-executer";
   public static final String PARAM_FREQUENCY = "frequency";
   public static final String TARGET_DOCUMENT = "targetDocument";
   public static final String OUTPUT_TYPE = "outputType";
   public static final String SEPARATOR = "seperator";
   public static final String PARAM_1 = "param1";
   public static final String PARAM_2 = "param2";
   public static final String PARAM_3 = "param3";
   public static final String PARAM_4 = "param4";
   private Properties globalProperties;
   private ServiceRegistry serviceRegistry;
   private DatabaseHelperBean dbhb = null;
   private String jndiName;
   private static Log logger = LogFactory.getLog(ReportExecuter.class);


   protected void executeImpl(Action action, NodeRef reportDefNodeRef) {
      String reportName;
      try {
         NodeRef e = (NodeRef)action.getParameterValue("targetDocument");
         reportName = (String)action.getParameterValue("outputType");
         this.processReport(reportDefNodeRef, e, reportName, action);
      } catch (Exception var5) {
         reportName = this.serviceRegistry.getNodeService().getProperty(reportDefNodeRef, ContentModel.PROP_NAME).toString();
         logger.fatal("Report execution failed! ReportDef=" + reportName + " Noderef=" + reportDefNodeRef);
         var5.printStackTrace();
      }

   }

   protected void addParameterDefinitions(List paramList) {
      paramList.add(new ParameterDefinitionImpl("targetDocument", DataTypeDefinition.NODE_REF, true, this.getParamDisplayLabel("targetDocument")));
      paramList.add(new ParameterDefinitionImpl("outputType", DataTypeDefinition.TEXT, true, this.getParamDisplayLabel("outputType")));
      paramList.add(new ParameterDefinitionImpl("seperator", DataTypeDefinition.TEXT, false, this.getParamDisplayLabel("seperator")));
      paramList.add(new ParameterDefinitionImpl("param1", DataTypeDefinition.TEXT, false, this.getParamDisplayLabel("param1")));
      paramList.add(new ParameterDefinitionImpl("param2", DataTypeDefinition.TEXT, false, this.getParamDisplayLabel("param2")));
      paramList.add(new ParameterDefinitionImpl("param3", DataTypeDefinition.TEXT, false, this.getParamDisplayLabel("param3")));
      paramList.add(new ParameterDefinitionImpl("param4", DataTypeDefinition.TEXT, false, this.getParamDisplayLabel("param4")));
   }

   public void setProperties(Properties properties) {
      this.globalProperties = properties;
      this.jndiName = properties.getProperty("reporting.db.jndiName");
   }

   public void setServiceRegistry(ServiceRegistry serviceRegistry) {
      this.serviceRegistry = serviceRegistry;
   }

   public void setDatabaseHelperBean(DatabaseHelperBean databaseHelperBean) {
      this.dbhb = databaseHelperBean;
   }

   private boolean isExecutionEnabled() {
      boolean enabled = true;

      try {
         enabled = this.globalProperties.getProperty("reporting.execution.enabled", "true").equalsIgnoreCase("true");
      } catch (Exception var3) {
         logger.debug("isExecutionEnabled() returning exception. Thus returning true;");
         logger.debug(var3);
         enabled = true;
      }

      return enabled;
   }

   public void processReport(NodeRef inputNodeRef, NodeRef outputNodeRef, String outputType, Action action) {
      logger.debug("starting ProcessReport generating a " + outputType);

      try {
         this.jndiName = this.globalProperties.getProperty("reporting.db.jndiName", "");
         if(this.isExecutionEnabled()) {
            String se = this.serviceRegistry.getNodeService().getProperty(inputNodeRef, ContentModel.PROP_NAME).toString();
            Object reportable = null;
            if(se.toLowerCase().endsWith(".jrxml") || se.toLowerCase().endsWith(".jasper")) {
               logger.debug("It is a Jasper thingy!");
               reportable = new JasperReporting();
            }


            if(reportable != null) {
               ((Reportable)reportable).setGlobalProperties(this.globalProperties);
               ((Reportable)reportable).setDatabaseHelper(this.dbhb);
               ((Reportable)reportable).setServiceRegistry(this.serviceRegistry);
               ((Reportable)reportable).setReportDefinition(inputNodeRef);
               ((Reportable)reportable).setOutputFormat(outputType);
               ((Reportable)reportable).setResultObject(outputNodeRef);
               ((Reportable)reportable).setMimetype(outputType);
               String separator = (String)action.getParameterValue("seperator");
               if(separator != null && !separator.equals("")) {
                  String param1 = (String)action.getParameterValue("param1");
                  String param2 = (String)action.getParameterValue("param2");
                  String param3 = (String)action.getParameterValue("param3");
                  String param4 = (String)action.getParameterValue("param4");
                  String key = "";
                  String value = "";
                  if(param1 != null && !param1.equals("") && param1.indexOf(separator) > 1) {
                     key = param1.split(separator)[0];
                     value = param1.split(separator)[1];
                     ((Reportable)reportable).setParameter(key, value);
                     logger.debug("1Setting: " + key + "=" + value);
                  }

                  if(param2 != null && !param2.equals("") && param2.indexOf(separator) > 1) {
                     key = param2.split(separator)[0];
                     value = param2.split(separator)[1];
                     ((Reportable)reportable).setParameter(key, value);
                     logger.debug("2Setting: " + key + "=" + value);
                  }

                  if(param3 != null && !param3.equals("") && param3.indexOf(separator) > 1) {
                     key = param3.split(separator)[0];
                     value = param3.split(separator)[1];
                     ((Reportable)reportable).setParameter(key, value);
                     logger.debug("3Setting: " + key + "=" + value);
                  }

                  if(param4 != null && !param4.equals("") && param4.indexOf(separator) > 1) {
                     key = param4.split(separator)[0];
                     value = param4.split(separator)[1];
                     ((Reportable)reportable).setParameter(key, value);
                     logger.debug("4Setting: " + key + "=" + value);
                  }
               }

               logger.debug("Lets go processReport!");
               ((Reportable)reportable).processReport();
            } else {
               logger.error(se + " is not a valid report definition");
            }
         } else {
            logger.warn("Alfresco Business Reporting is NOT enabled...");
         }
      } catch (SystemException var14) {
         var14.printStackTrace();
      }

   }

}
