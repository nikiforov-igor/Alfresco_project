package ru.it.lecm.reporting.execution;

import org.alfresco.service.cmr.repository.NodeRef;

public class ReportingContainer {

   private NodeRef nodeRef;
   private boolean executionEnabled;
   private String executionFrequency;
   private String name;


   public ReportingContainer(NodeRef reportingContainerRef) {
      this.nodeRef = reportingContainerRef;
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

   public void setExecutionEnabled(boolean execEnabled) {
      this.executionEnabled = execEnabled;
   }

   public boolean isExecutionEnabled() {
      return this.executionEnabled;
   }

   public void setExecutionFrequency(String frequency) {
      this.executionFrequency = frequency;
   }

   public String getExecutionFrequency() {
      return this.executionFrequency;
   }
}
