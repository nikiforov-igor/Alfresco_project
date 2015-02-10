package org.alfresco.reporting.execution;

import java.util.Properties;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReportTemplate {

   private NodeRef nodeRef;
   private String outputFormat;
   private boolean outputVersioned;
   private boolean reportingDocument;
   private NodeRef targetNode;
   private String targetPath;
   private String substitution;
   private String name;
   private static Log logger = LogFactory.getLog(ReportTemplate.class);


   public ReportTemplate(NodeRef reportRef) {
      this.nodeRef = reportRef;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void setReportingDocument(boolean reportingDoc) {
      this.reportingDocument = reportingDoc;
   }

   public boolean isReportingDocument() {
      return this.reportingDocument;
   }

   public void setTargetNode(NodeRef target) {
      this.targetNode = target;
   }

   public NodeRef getTargetNode() {
      return this.targetNode;
   }

   public String getTargetPath() {
      return this.targetPath;
   }

   public void setTargetPath(String rawTargetPath) {
      while(true) {
         try {
            if(rawTargetPath.indexOf("\\") > -1) {
               rawTargetPath = rawTargetPath.substring(0, rawTargetPath.indexOf("\\")) + "/" + rawTargetPath.substring(rawTargetPath.indexOf("\\") + 1);
               continue;
            }
         } catch (Exception var3) {
            ;
         }

         this.targetPath = rawTargetPath;
         return;
      }
   }

   public void setSubstitution(String substitution) {
      this.substitution = substitution;
   }

   public Properties getSubstitution() {
      Properties keyValues = new Properties();
      if(this.substitution != null && this.substitution.length() > 0) {
         String[] singleKeyValue = this.substitution.split(",");
         String[] arr$ = singleKeyValue;
         int len$ = singleKeyValue.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String line = arr$[i$];
            logger.debug("getSubstitution: Processing " + line);
            if(line.contains("=")) {
               try {
                  String key = line.split("=")[0];
                  keyValues.setProperty(key, line.split("=")[1]);
               } catch (Exception var9) {
                  logger.error("HELP! Processing parameter " + line + " failed!");
               }
            }
         }
      }

      return keyValues;
   }

   public NodeRef getNodeRef() {
      return this.nodeRef;
   }

   public void setOutputFormat(String outputFormat) {
      this.outputFormat = outputFormat;
   }

   public String getOutputFormat() {
      return this.outputFormat;
   }

   public void setOutputVersioned(boolean versioned) {
      this.outputVersioned = versioned;
   }

   public boolean isOutputVersioned() {
      return this.outputVersioned;
   }

}
