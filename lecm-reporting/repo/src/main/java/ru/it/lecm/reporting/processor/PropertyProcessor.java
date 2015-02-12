package ru.it.lecm.reporting.processor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportLine;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.db.DatabaseHelperBean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

abstract class PropertyProcessor {

   protected ServiceRegistry serviceRegistry;
   private SearchService searchService;
   private AuthorityService authorityService;
   private AuthenticationService authenticationService;
   protected VersionService versionService;
   protected NodeService nodeService;
   protected NamespaceService namespaceService;
   protected DictionaryService dictionaryService;
   protected FileFolderService fileFolderService;
   protected DatabaseHelperBean dbhb;
   protected ReportingHelper reportingHelper;
   protected String method = "SINGLE_INSTANCE";
   private static Log logger = LogFactory.getLog(PropertyProcessor.class);
   protected List queue = new ArrayList();
   protected int maxErrorsPerRun = 100;
   Properties dataDictionary;
   Properties replacementDataTypes;
   Properties globalProperties;
   Properties namespaces;
   String blacklist;


   public void setQueue(List theQueue) {
      this.queue = theQueue;
   }

   public void resetQueue() {
      this.queue = new ArrayList();
   }

   public SearchService getSearchService() {
      return this.searchService;
   }

   public void setSearchService(SearchService searchService) {
      this.searchService = searchService;
   }

   public AuthorityService getAuthorityService() {
      return this.authorityService;
   }

   public void setAuthorityService(AuthorityService authorityService) {
      this.authorityService = authorityService;
   }

   public AuthenticationService getAuthenticationService() {
      return this.authenticationService;
   }

   public void setAuthenticationService(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   public ServiceRegistry getServiceRegistry() {
      return this.serviceRegistry;
   }

   public void setServiceRegistry(ServiceRegistry serviceRegistry) {
      this.serviceRegistry = serviceRegistry;
   }

   public NodeService getNodeService() {
      return this.nodeService;
   }

   public void setNodeService(NodeService nodeService) {
      this.nodeService = nodeService;
   }

   public DictionaryService getDictionaryService() {
      return this.dictionaryService;
   }

   public void setDictionaryService(DictionaryService dictionaryService) {
      this.dictionaryService = dictionaryService;
   }

   public FileFolderService getFileFolderService() {
      return this.fileFolderService;
   }

   public void setFileFolderService(FileFolderService fileFolderService) {
      this.fileFolderService = fileFolderService;
   }

   public DatabaseHelperBean getDbhb() {
      return this.dbhb;
   }

   public void setDbhb(DatabaseHelperBean dbhb) {
      this.dbhb = dbhb;
   }

   public ReportingHelper getReportingHelper() {
      return this.reportingHelper;
   }

   public void setReportingHelper(ReportingHelper reportingHelper) {
      this.reportingHelper = reportingHelper;
   }

   public void setMaxErrorsPerRun(int theMaxErrorsPerRun) {
      try {
         Math.abs(theMaxErrorsPerRun);
         this.maxErrorsPerRun = theMaxErrorsPerRun;
      } catch (Exception var3) {
         ;
      }

   }

   public int getMaxErrorsPerRun() {
      return this.maxErrorsPerRun;
   }

   public Properties getGlobalProperties() {
      return this.globalProperties;
   }

   public void setGlobalProperties(Properties globalProperties) {
      this.globalProperties = globalProperties;
   }

   public Properties getNamespaces() {
      return this.namespaces;
   }

   public void setNamespaces(Properties namespaces) {
      this.namespaces = namespaces;
   }

   public Properties getReplacementDataTypes() {
      return this.replacementDataTypes;
   }

   public void setBlacklist(String blacklist) {
      this.blacklist = blacklist;
   }

   public List getQueue() {
      return this.queue;
   }

   public Properties getClassToColumnType() {
      return this.dataDictionary;
   }

   void setClassToColumnType(Properties dataDictionary) {
      this.dataDictionary = dataDictionary;
   }

   public Properties getReplacementDataType() {
      return this.replacementDataTypes;
   }

   public String getBlacklist() {
      return this.blacklist;
   }

   public void setReplacementDataTypes(Properties p) {
      this.replacementDataTypes = p;
   }

   private void createEmtpyQueue(String table) {
      this.queue = new ArrayList();
   }

   public void addToQueue(Object queueItem) {
      this.queue.add(queueItem);
   }

   public SimpleDateFormat getSimpleDateFormat() {
      return this.reportingHelper.getSimpleDateFormat();
   }

   public String replaceNameSpaces(String namespace) {
      Properties p = this.getNamespaces();
      String returnSpace = namespace;

      String into;
      String from;
      for(Enumeration keys = p.keys(); keys.hasMoreElements(); returnSpace = returnSpace.replace(from, into)) {
         into = (String)keys.nextElement();
         from = p.getProperty(into);
      }

      returnSpace = returnSpace.replace("-", "_");
      return returnSpace;
   }

   public static void propertyLogger(String description, Properties p) {
      if(logger.isDebugEnabled()) {
         logger.debug("PropertyLogger: " + description);
      }

      Enumeration keys = p.keys();

      while(keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         String value = p.getProperty(key, "-");
         if(logger.isDebugEnabled()) {
            logger.debug("  entry: " + key + "=" + value);
         }
      }

   }

   public void setTableDefinition(String tableName, Properties props) throws Exception {
      if(logger.isDebugEnabled()) {
         logger.debug("Enter setTableDefinition tableName=" + tableName + " with props=" + props);
      }

      try {
         Properties e = this.dbhb.getTableDescription(tableName);
         if(logger.isDebugEnabled()) {
            propertyLogger("## Object properties", props);
            propertyLogger("## Table description", e);
         }

         Enumeration keys = props.keys();

         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String type = props.getProperty(key, "-");
            if(logger.isDebugEnabled()) {
               logger.debug("## COMPARE: key=" + key);
            }

            if(!e.containsKey(key) && !e.containsKey(key.toUpperCase()) && !e.containsKey(key.toLowerCase())) {
               if(!"-".equals(type) && !"".equals(type)) {
                  if(logger.isDebugEnabled()) {
                     logger.debug("DEFINITION Adding column: " + key + "=" + type);
                  }

                  this.dbhb.extendTable(tableName, key, type);
               } else if(logger.isDebugEnabled()) {
                  logger.debug("DEFINITION Column " + key + " is empty. Type=" + type);
               }
            } else if(logger.isDebugEnabled()) {
               logger.debug("DEFINITION Column " + key + " already exists.");
            }
         }
      } catch (Exception var7) {
         logger.fatal("Exception setTableDefinition: " + var7.getMessage());
         throw new Exception(var7);
      }

      logger.debug("Exit setTableDefinition");
   }

   public Properties processPropertyDefinitions(Properties definition, NodeRef nodeRef) {
      if(logger.isDebugEnabled()) {
         logger.debug("enter processPropertyDefinitions #props=" + definition.size() + " and nodeRef " + nodeRef);
      }

      try {
         Map e = this.nodeService.getProperties(nodeRef);
         if(logger.isDebugEnabled()) {
            logger.debug("processPropertyDefinitions: Size of map=" + e.size());
         }

         Iterator keys = e.keySet().iterator();

         while(keys.hasNext()) {
            String key = "";
            String type = "";

            try {
               QName e1 = (QName)keys.next();
               if(e1 != null) {
                  key = e1.toString();
                  key = this.replaceNameSpaces(key);
                  if(logger.isDebugEnabled()) {
                     logger.debug("processPropertyDefinitions: Processing key " + key);
                  }

                  if(!key.startsWith("{urn:schemas_microsoft_com:}") && !definition.containsKey(key)) {
                     type = "";
                     if(this.getReplacementDataType().containsKey(key)) {
                        type = this.getReplacementDataType().getProperty(key, "-").trim();
                     } else {
                        type = "-";

                        try {
                           type = this.dictionaryService.getProperty(e1).getDataType().toString().trim();
                           type = type.substring(type.indexOf("}") + 1, type.length());
                           type = this.getClassToColumnType().getProperty(type, "-");
                        } catch (NullPointerException var9) {
                           logger.info("Silent drop of NullPointerException against " + key);
                        }
                     }

                     if(type != null && !type.equals("-") && !type.equals("") && key != null && !key.equals("") && !this.getBlacklist().toLowerCase().contains("," + key.toLowerCase() + ",")) {
                        definition.setProperty(key, type);
                        if(logger.isDebugEnabled()) {
                           logger.debug("processPropertyDefinitions: Adding column " + key + "=" + type);
                        }
                     } else if(logger.isDebugEnabled()) {
                        logger.debug("Ignoring column " + key + "=" + type);
                     }
                  }
               }
            } catch (Exception var10) {
               logger.error("processPropertyDefinitions: Property not found! Property below...");
               logger.error("processPropertyDefinitions: type=" + type + ", key=" + key);
               var10.printStackTrace();
            }

            if(logger.isDebugEnabled()) {
               logger.debug("processPropertyDefinitions: end while");
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
         logger.error("processPropertyDefinitions: Finally an EXCEPTION " + var11.getMessage());
      }

      return definition;
   }

   private String getPropertyValue(Serializable s, String dtype, boolean multiValued) {
      if(logger.isDebugEnabled()) {
         logger.debug("Enter getPropertyValue (3 params)");
      }

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
         if(logger.isDebugEnabled()) {
            logger.debug("Found a category!");
         }

         List var10 = (List)s;
         String catName;
         if(var10 != null) {
            for(Iterator var11 = var10.iterator(); var11.hasNext(); returnValue = returnValue + catName) {
               NodeRef cat = (NodeRef)var11.next();
               catName = this.nodeService.getProperty(cat, ContentModel.PROP_NAME).toString();
               if(returnValue.length() > 0) {
                  returnValue = returnValue + ",";
               }
            }
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("Exit getPropertyValue, returning: " + returnValue);
      }

      return returnValue;
   }

   private String getCategoryDisplayPath(NodeRef category) {
      String returnString = (String)this.getNodeService().getProperty(category, ContentModel.PROP_NAME);
      NodeRef parent = this.getNodeService().getPrimaryParent(category).getParentRef();
      NodeRef parentsParent = this.getNodeService().getPrimaryParent(parent).getParentRef();

      for(int counter = 0; !ContentModel.TYPE_CATEGORYROOT.equals(this.getNodeService().getType(parentsParent)) && counter < 20; ++counter) {
         returnString = (String)this.getNodeService().getProperty(parent, ContentModel.PROP_NAME) + "/" + returnString;
         parent = this.getNodeService().getPrimaryParent(parent).getParentRef();
         parentsParent = this.getNodeService().getPrimaryParent(parent).getParentRef();
      }

      return returnString;
   }

   public String getPropertyValue(NodeRef nodeRef, QName qname, String dtype, boolean multiValued) {
      if(logger.isDebugEnabled()) {
         logger.debug("Enter getPropertyValue (4 params), qname=" + qname + ", noderef=" + nodeRef + ", dtype=" + dtype);
      }

      String returnValue = "";
      Serializable s = this.getNodeService().getProperty(nodeRef, qname);
      if(logger.isDebugEnabled()) {
         logger.debug("getPropertyType Serialized=" + s);
      }

      if(multiValued && !"category".equals(dtype)) {
         new ArrayList();
         ArrayList var11 = (ArrayList)this.getNodeService().getProperty(nodeRef, qname);
         if(var11 != null && !var11.isEmpty() && var11.size() > 0) {
            if(dtype.equals("date") || dtype.equals("datetime")) {
               SimpleDateFormat var13 = this.getSimpleDateFormat();

               for(int cat = 0; cat < var11.size(); ++cat) {
                  returnValue = returnValue + var13.format((Date)var11.get(cat)) + ",";
               }
            }

            int var14;
            if(dtype.equals("id") || dtype.equals("long")) {
               for(var14 = 0; var14 < var11.size(); ++var14) {
                  returnValue = returnValue + Long.toString(((Long)var11.get(var14)).longValue()) + ",";
               }
            }

            if(dtype.equals("int")) {
               for(var14 = 0; var14 < var11.size(); ++var14) {
                  returnValue = returnValue + Integer.toString(((Integer)var11.get(var14)).intValue()) + ",";
               }
            }

            if(dtype.equals("float") || dtype.equals("double")) {
               for(var14 = 0; var14 < var11.size(); ++var14) {
                  returnValue = returnValue + Double.toString(((Double)var11.get(var14)).doubleValue()) + ",";
               }
            }

            if(dtype.equals("boolean")) {
               for(var14 = 0; var14 < var11.size(); ++var14) {
                  returnValue = returnValue + Boolean.toString(((Boolean)var11.get(var14)).booleanValue()) + ",";
               }
            }

            if(dtype.equals("text")) {
               for(var14 = 0; var14 < var11.size(); ++var14) {
                  returnValue = returnValue + (String)var11.get(var14) + ",";
               }
            }

            if(dtype.equals("noderef")) {
               for(var14 = 0; var14 < var11.size(); ++var14) {
                  returnValue = returnValue + var11.get(var14).toString() + ",";
               }
            }

            if(returnValue.equals("")) {
               for(var14 = 0; var14 < var11.size(); ++var14) {
                  returnValue = returnValue + (String)var11.get(var14) + ",";
               }
            }
         }
      } else if(s != null && !"category".equals(dtype)) {
         if(dtype.equals("date") || dtype.equals("datetime")) {
            SimpleDateFormat categories = this.getSimpleDateFormat();
            Calendar i$ = Calendar.getInstance();
            i$.setTimeInMillis(((Date)s).getTime());
            returnValue = categories.format((Date)s);
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
            returnValue = String.valueOf(s);
         }
      }

      if(dtype.equals("category")) {
         if(logger.isDebugEnabled()) {
            logger.debug("I am a category!");
         }

         List var12 = (List)this.nodeService.getProperty(nodeRef, qname);
         String catName;
         if(var12 != null) {
            for(Iterator var16 = var12.iterator(); var16.hasNext(); returnValue = returnValue + catName) {
               NodeRef var15 = (NodeRef)var16.next();
               catName = this.nodeService.getProperty(var15, ContentModel.PROP_NAME).toString();
               catName = this.getCategoryDisplayPath(var15);
               if(returnValue.length() > 0) {
                  returnValue = returnValue + ",";
               }
            }
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("Exit getPropertyValue, returning: " + returnValue);
      }

      return returnValue;
   }

   public ReportLine processPropertyValues(ReportLine rl, NodeRef nodeRef) {
      Map map = this.nodeService.getProperties(nodeRef);
      if(logger.isDebugEnabled()) {
         logger.debug("processPropertyValues enter " + nodeRef);
      }

      if(this.dictionaryService.isSubClass(this.nodeService.getType(nodeRef), ContentModel.TYPE_CONTENT)) {
         try {
            rl.setLine("cm_workingcopylink", this.getClassToColumnType().getProperty("noderef", ""), (String)null, this.getReplacementDataType());
            rl.setLine("cm_lockOwner", this.getClassToColumnType().getProperty("noderef", ""), (String)null, this.getReplacementDataType());
            rl.setLine("cm_lockType", this.getClassToColumnType().getProperty("noderef", ""), (String)null, this.getReplacementDataType());
            rl.setLine("cm_expiryDate", this.getClassToColumnType().getProperty("datetime", ""), (String)null, this.getReplacementDataType());
         } catch (Exception var13) {
            logger.error("processPropertyValues Exception " + var13.getMessage());
            var13.printStackTrace();
         }
      }

      Iterator keys = map.keySet().iterator();

      while(keys.hasNext()) {
         String key = "";
         String dtype = "";

         try {
            QName e = (QName)keys.next();
            key = e.toString();
            if(!key.startsWith("{urn:schemas_microsoft_com:}")) {
               key = this.replaceNameSpaces(key);
               dtype = this.dictionaryService.getProperty(e).getDataType().toString();
               dtype = dtype.substring(dtype.indexOf("}") + 1, dtype.length()).trim();
               String theObject = this.getClassToColumnType().getProperty(dtype, "-");
               String type = theObject.toString();
               boolean multiValued = false;
               multiValued = this.dictionaryService.getProperty(e).isMultiValued();
               if(!this.blacklist.toLowerCase().contains("," + key.toLowerCase() + ",") && !type.equals("-")) {
                  String value = this.getPropertyValue(nodeRef, e, dtype, multiValued);
                  rl.setLine(key, type, value, this.getReplacementDataType());
               }
            }
         } catch (Exception var12) {
            logger.debug("processPropertyValues: Error in object, property " + key + " not found! (" + dtype + ")");
         }
      }

      return rl;
   }

   protected abstract ReportLine processNodeToMap(String var1, String var2, ReportLine var3);

   abstract Properties processQueueDefinition(String var1);

   abstract void processQueueValues(String var1) throws Exception;

   abstract void havestNodes(NodeRef var1);

    public NamespaceService getNamespaceService() {
        return namespaceService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
}
