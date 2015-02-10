package org.alfresco.reporting.execution;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import org.alfresco.model.ContentModel;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.reporting.execution.Reportable;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JasperReporting implements Reportable {

   private NodeRef inputNodeRef;
   private NodeRef outputNodeRef;
   private String format = "";
   private Connection conn;
   private ServiceRegistry serviceRegistry;
   private String dataType = "JDBC";
   private String user = "";
   private String pass = "";
   private String url = "";
   private String driver = "";
   private String jndiName = "";
   private Properties globalProperties;
   private static Log logger = LogFactory.getLog(JasperReporting.class);


   public void setDataSourceType(String dataType) {
      this.dataType = dataType;
   }

   public void setUsername(String user) {
      this.user = user;
   }

   public void setPassword(String pass) {
      this.pass = pass;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setMimetype(String ext) {
      if("pdf".equals(ext.toLowerCase())) {
         ;
      }

      if(!"xls".equals(ext.toLowerCase()) && "xlsx".equals(ext.toLowerCase())) {
         ;
      }

   }

   public void setDriver(String driver) {
      this.driver = driver;
   }

   public void setJndiName(String name) {
      this.jndiName = name;
   }

   public void setReportDefinition(NodeRef input) {
      this.inputNodeRef = input;
   }

   public void setResultObject(NodeRef output) {
      this.outputNodeRef = output;
   }

   public void setOutputFormat(String format) {
      this.format = format;
   }

   public void setParameter(String key, String value) {}

   public void setServiceRegistry(ServiceRegistry serviceRegistry) {
      this.serviceRegistry = serviceRegistry;
   }

   public void processReport() {
      HashMap parameters = new HashMap();
      OutputStream reportOS = null;
      File tempFile = null;

      try {
         ContentReader e = this.serviceRegistry.getContentService().getReader(this.inputNodeRef, ContentModel.PROP_CONTENT);
         String name = this.serviceRegistry.getNodeService().getProperty(this.inputNodeRef, QName.createQName("http://www.alfresco.org/model/content/1.0", "name")).toString();
         tempFile = File.createTempFile("reporting", "");
         logger.debug("Prepping tempFile: " + tempFile.getAbsolutePath());
         e.getContent(tempFile);
         logger.debug("Stored tempfile all right");
         Object exporter = null;
         if(this.format.equalsIgnoreCase("pdf")) {
            exporter = new JRPdfExporter();
         }

         if(this.format.equalsIgnoreCase("html")) {
            exporter = new JRHtmlExporter();
         }

         if(this.format.equalsIgnoreCase("xls")) {
            exporter = new JRXlsExporter();
         }

         if(this.format.equalsIgnoreCase("doc")) {
            exporter = new JRDocxExporter();
         }

         logger.debug("The exporter has a value.");
         JasperPrint jasperPrint = null;
         if(name.endsWith(".jrxml")) {
            logger.debug("It is a .jrxml");
            JasperReport contentWriter = JasperCompileManager.compileReport(tempFile.getAbsolutePath());
            logger.debug("Just compiled the Report " + contentWriter);
            jasperPrint = JasperFillManager.fillReport(contentWriter, parameters, this.conn);
         } else {
            logger.debug("It is a .jasper");
            jasperPrint = JasperFillManager.fillReport(tempFile.getAbsolutePath(), parameters, this.conn);
         }

         logger.debug("Just filled the report");
         ContentWriter contentWriter1 = this.serviceRegistry.getContentService().getWriter(this.outputNodeRef, ContentModel.PROP_CONTENT, true);
         logger.debug("got the contentWriter");
         reportOS = contentWriter1.getContentOutputStream();
         logger.debug("got the outputStream");
         ((JRExporter)exporter).setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
         ((JRExporter)exporter).setParameter(JRExporterParameter.OUTPUT_STREAM, reportOS);
         logger.debug("Just before exportReport");
         ((JRExporter)exporter).exportReport();
      } catch (JRException var19) {
         logger.error("Error occurred in generating report " + this.inputNodeRef + " and storing into " + this.outputNodeRef);
         var19.printStackTrace();
      } catch (Exception var20) {
         var20.printStackTrace();
      } finally {
         try {
            logger.debug("closing the stream");
            reportOS.close();
         } catch (Exception var18) {
            logger.error("Cannot close connection after generating report...");
         }

         if(tempFile != null && tempFile.canWrite()) {
            tempFile.delete();
         } else {
            tempFile.deleteOnExit();
         }

      }

   }

   public void setGlobalProperties(Properties properties) {
      this.globalProperties = properties;
   }

   public void setDatabaseHelper(DatabaseHelperBean dbhb) {}

}
