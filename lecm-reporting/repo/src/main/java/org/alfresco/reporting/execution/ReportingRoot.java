package org.alfresco.reporting.execution;

import java.util.Properties;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReportingRoot {

   private NodeRef nodeRef;
   private boolean globalExecutionEnabled;
   private boolean harvestEnabled;
   private Properties targetQueries;
   private String rootQueryLanguage;
   private String outputExtensionExcel;
   private String outputExtensionPdf;
   private String outputExtensionCsv;
   private String name;
   private static Log logger = LogFactory.getLog(ReportingRoot.class);


   public ReportingRoot(NodeRef reportingRootRef) {
      this.nodeRef = reportingRootRef;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public NodeRef getNodeRef() {
      return this.nodeRef;
   }

   public void setGlobalExecutionEnabled(boolean executionEnabled) {
      this.globalExecutionEnabled = executionEnabled;
   }

   public boolean isGlobalExecutionEnabled() {
      return this.globalExecutionEnabled;
   }

   public void setHarvestEnabled(boolean harvestEnabled) {
      this.harvestEnabled = harvestEnabled;
   }

   public boolean isHarvestEnabled() {
      return this.harvestEnabled;
   }

   public Properties getTargetQueries() {
      return this.targetQueries;
   }

   public void setTargetQueries(String queries) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter getAllTargetQueries");
      }

      Properties returnProps = new Properties();
      String[] lines = queries.split("\\n");
      String[] arr$ = lines;
      int len$ = lines.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String line = arr$[i$];
         line = line.trim();
         if(!line.trim().startsWith("#") && line.indexOf("=") > 1) {
            int i = line.indexOf("=");
            String key = line.substring(0, i);
            String value = line.substring(i + 1);
            returnProps.put(key, value);
            if(logger.isDebugEnabled()) {
               logger.debug("getAllTargetQueries: Storing " + key + "=" + value);
            }
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("exit getTargetQueries, size=" + returnProps.size());
      }

      this.targetQueries = returnProps;
   }

   public void setRootQueryLanguage(String language) {
      this.rootQueryLanguage = language;
   }

   public String getRootQueryLanguage() {
      return this.rootQueryLanguage;
   }

   public void setOutputExtensionExcel(String extension) {
      if(extension == null) {
         this.outputExtensionExcel = "xls";
      } else {
         this.outputExtensionExcel = extension;
      }

   }

   public String getOutputExtensionExcel() {
      return this.outputExtensionExcel;
   }

   public void setOutputExtensionPdf(String extension) {
      if(extension == null) {
         this.outputExtensionPdf = "pdf";
      } else {
         this.outputExtensionPdf = extension;
      }

   }

   public String getOutputExtensionPdf() {
      return this.outputExtensionPdf;
   }

   public void setOutputExtensionCsv(String extension) {
      if(extension == null) {
         this.outputExtensionCsv = "csv";
      } else {
         this.outputExtensionCsv = extension;
      }

   }

   public String getOutputExtensionCsv() {
      return this.outputExtensionCsv;
   }

}
