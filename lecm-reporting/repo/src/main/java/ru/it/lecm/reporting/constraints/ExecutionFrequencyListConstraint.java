package ru.it.lecm.reporting.constraints;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class ExecutionFrequencyListConstraint extends ListOfValuesConstraint {

   private static Log logger = LogFactory.getLog(ExecutionFrequencyListConstraint.class);
   private static List listOfFrequencies = null;
   private Properties globalProperties;
   final String PROPERTY_PREFIX = "reporting.execution.frequency.";


   public ExecutionFrequencyListConstraint() {
      logger.debug("exit ExecutionFrequencyListConstraint");
   }

   private List getDummyList() {
      ArrayList allowedValues = new ArrayList();
      allowedValues.add("1");
      allowedValues.add("2");
      return allowedValues;
   }

   private List getList() {
      if(logger.isDebugEnabled()) {
         logger.debug("enter getList");
      }

      ArrayList myListOfValues = new ArrayList();
      String key = "";
      if(this.getProperties() == null) {
         logger.debug("Someone killed the Properties!!");
      }

      Enumeration keys = this.getProperties().keys();
      if(logger.isDebugEnabled()) {
         logger.debug("#keys=" + this.getProperties().size());
      }

      while(keys.hasMoreElements()) {
         key = keys.nextElement().toString();

         try {
            if(key.startsWith("reporting.execution.frequency.")) {
               String e = key.substring("reporting.execution.frequency.".length(), key.length());
               if(logger.isDebugEnabled()) {
                  logger.debug("Found value: " + e);
               }

               myListOfValues.add(e);
            }
         } catch (Exception var5) {
            ;
         }
      }

      if(myListOfValues.isEmpty()) {
         myListOfValues.add("-unexpectedly empty-");
      }

      if(logger.isDebugEnabled()) {
         logger.debug("exit getList");
      }

      return myListOfValues;
   }

   public List getAllowedValues() {
      if(logger.isDebugEnabled()) {
         logger.debug("enter getAllowedValues");
      }

      new ArrayList();
      List allowedValues = this.getDummyList();
      super.setAllowedValues(allowedValues);
      logger.debug("getAllowedValues: before super: " + allowedValues.size());
      if(logger.isDebugEnabled()) {
         logger.debug("getAllowedValues returning: " + allowedValues.size());
      }

      return allowedValues;
   }

   public void setProperties(Properties properties) {
      this.globalProperties = properties;
   }

   private Properties getProperties() {
      return this.globalProperties;
   }

}
