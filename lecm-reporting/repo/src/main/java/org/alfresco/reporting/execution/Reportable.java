package org.alfresco.reporting.execution;

import java.util.Properties;
import javax.transaction.SystemException;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;

public interface Reportable {

   String EXTENSION = "";


   void setUsername(String var1);

   void setPassword(String var1);

   void setDataSourceType(String var1);

   void setUrl(String var1);

   void setDriver(String var1);

   void setJndiName(String var1);

   void setReportDefinition(NodeRef var1);

   void setResultObject(NodeRef var1);

   void setOutputFormat(String var1);

   void setParameter(String var1, String var2);

   void setGlobalProperties(Properties var1);

   void setDatabaseHelper(DatabaseHelperBean var1);

   void setServiceRegistry(ServiceRegistry var1);

   void setMimetype(String var1);

   void processReport() throws IllegalStateException, SecurityException, SystemException;
}
