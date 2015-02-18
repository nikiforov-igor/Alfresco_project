package ru.it.lecm.reporting;

import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import ru.it.lecm.reporting.execution.ReportTemplate;
import ru.it.lecm.reporting.execution.ReportingContainer;
import ru.it.lecm.reporting.execution.ReportingRoot;
import ru.it.lecm.reporting.util.resource.HierarchicalResourceLoader;
import ru.it.lecm.reports.dao.RepositoryReportContentDAOBean;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportingHelper {
    private static Log logger = LogFactory.getLog(ReportingHelper.class);

    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;
    private ContentService contentService;
    private NamespaceService namespaceService;

    private String blacklist = ",";
    private String invalidTableName = "";
    private HierarchicalResourceLoader hierarchicalResourceLoader;
    private String vendor;

    private Properties namespacesShortToLong = null;
    private Properties classToColumn;
    private Properties replacementTypes;
    private Properties namespaces;
    private Properties globalProperties;
    private Properties tableNameCache = new Properties();

    private HttpClientFactory httpClientFactory;

    private RepositoryReportContentDAOBean reportsService;

    private SimpleDateFormat simpleFormat;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setProperties(Properties properties) {
        this.globalProperties = properties;
    }

    public void setHierarchicalResourceLoader(HierarchicalResourceLoader hierarchicalResourceLoader) {
        this.hierarchicalResourceLoader = hierarchicalResourceLoader;
    }

    public RepositoryReportContentDAOBean getReportsService() {
        return reportsService;
    }

    public void setReportsService(RepositoryReportContentDAOBean reportsService) {
        this.reportsService = reportsService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setBlacklist(String list) {
        this.blacklist = list;
    }

    public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    public Properties getTableNameCache() {
        return this.tableNameCache;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return this.simpleFormat;
    }

    public void init() {
        if (this.replacementTypes == null) {
            try {
                ClassLoader e = this.getClass().getClassLoader();
                InputStream is = e.getResourceAsStream("alfresco/extension/reporting-custom-model.properties");
                Properties p = new Properties();
                p.load(is);
                replacementTypes = p;
            } catch (Exception var4) {
                replacementTypes = new Properties();
            }
        }

        if (this.classToColumn == null) {
            try {
                ClassLoader cl = this.getClass().getClassLoader();
                String url = this.hierarchicalResourceLoader.getResourcePath();
                if (logger.isDebugEnabled()) {
                    logger.debug("MyBatis resource path: " + url);
                }

                url = url.substring(0, url.lastIndexOf("/") + 1);
                url = url + "reporting-model.properties";
                url = "/alfresco/module/reporting-repo" + url.split("/reporting-repo")[1];
                if (logger.isDebugEnabled()) {
                    logger.debug("Vendor specific mapping path: " + url);
                }

                InputStream is = cl.getResourceAsStream(url);
                Properties p = new Properties();
                p.load(is);
                this.classToColumn = p;
            } catch (IOException e) {
                this.classToColumn = new Properties();
                logger.error("MyBatis resource path: " + e.getMessage());
            }
            System.out.println("classToColumn Loaded!");
        }

        if (this.vendor == null) {
            this.vendor = this.hierarchicalResourceLoader.getDatabaseVendor();
        }
        if (this.simpleFormat == null) {
            String dateformat = Constants.DATE_FORMAT_AUDIT;
            if ("mysql".equalsIgnoreCase(this.vendor)) {
                dateformat = "yyyy-MM-dd HH:mm:ss.SSS";
            } else if ("postgresql".equalsIgnoreCase(this.vendor)) {
                dateformat = "yyyy-MM-dd HH:mm:ss.SSS";
            } else if ("oracle".equalsIgnoreCase(this.vendor)) {
                dateformat = "yyyy-MM-dd HH:mm:ss";
            } else if ("sqlserver".equalsIgnoreCase(this.vendor)) {
                dateformat = "yyyy-MM-dd HH:mm:ss.SSS";
            }

            SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getDefault());
            this.simpleFormat = sdf;
        }
    }

    public void addTableNameCache(String tablename, String tableNameFixed) {
        this.tableNameCache.setProperty(tablename, tableNameFixed);
    }

    public NodeRef getReportingRoot(NodeRef currentNode) {
        return this.getParentByType(currentNode, ReportingModel.TYPE_REPORTING_ROOT);
    }

    public NodeRef getReportingContainer(NodeRef currentNode) {
        return this.getParentByType(currentNode, ReportingModel.TYPE_REPORTING_CONTAINER);
    }

    public String getSearchLanguage(String objectLanguage) {
        return "fts-alfresco";
    }

    public void initializeReportingRoot(ReportingRoot reportingRoot) {
        reportingRoot.setGlobalExecutionEnabled((Boolean) this.nodeService.getProperty(reportingRoot.getNodeRef(),
                ReportingModel.PROP_REPORTING_GLOBAL_EXECUTION_ENABLED));
        reportingRoot.setHarvestEnabled((Boolean) this.nodeService.getProperty(reportingRoot.getNodeRef(),
                ReportingModel.PROP_REPORTING_HARVEST_ENABLED));
        reportingRoot.setRootQueryLanguage((String) this.nodeService.getProperty(reportingRoot.getNodeRef(),
                ReportingModel.PROP_REPORTING_ROOT_QUERY_LANGUAGE));
        reportingRoot.setOutputExtensionExcel((String) this.nodeService.getProperty(reportingRoot.getNodeRef(),
                ReportingModel.PROP_REPORTING_OUTPUTEXTENSION_EXCEL));
        reportingRoot.setOutputExtensionPdf((String) this.nodeService.getProperty(reportingRoot.getNodeRef(),
                ReportingModel.PROP_REPORTING_OUTPUTEXTENSION_PDF));
        reportingRoot.setOutputExtensionCsv((String) this.nodeService.getProperty(reportingRoot.getNodeRef(),
                ReportingModel.PROP_REPORTING_OUTPUTEXTENSION_CSV));
        reportingRoot.setTargetQueries((String) this.nodeService.getProperty(reportingRoot.getNodeRef(),
                ReportingModel.PROP_REPORTING_TARGET_QUERIES));
        reportingRoot.setName((String) this.nodeService.getProperty(reportingRoot.getNodeRef(), ContentModel.PROP_NAME));
    }

    public void initializeReportingContainer(ReportingContainer reportingContainer) {
        reportingContainer.setExecutionEnabled((Boolean) this.nodeService.getProperty(reportingContainer.getNodeRef(),
                ReportingModel.PROP_REPORTING_EXECUTION_ENABLED));
        reportingContainer.setExecutionFrequency((String) this.nodeService.getProperty(reportingContainer.getNodeRef(),
                ReportingModel.PROP_REPORTING_EXECUTION_FREQUENCY));
        reportingContainer.setName((String) this.nodeService.getProperty(reportingContainer.getNodeRef(), ContentModel.PROP_NAME));
        if (logger.isDebugEnabled()) {
            logger.debug("initializeReportingContainer:");
            logger.debug("  Setting execution enabled = " + this.nodeService.getProperty(reportingContainer.getNodeRef(), ReportingModel.PROP_REPORTING_EXECUTION_ENABLED));
            logger.debug("  Execution frequency: " + this.nodeService.getProperty(reportingContainer.getNodeRef(), ReportingModel.PROP_REPORTING_EXECUTION_FREQUENCY));
            logger.debug("  Name: " + this.nodeService.getProperty(reportingContainer.getNodeRef(), ContentModel.PROP_NAME));
        }
    }

    public void initializeReport(ReportTemplate report) {
        report.setName((String) this.nodeService.getProperty(report.getNodeRef(), ContentModel.PROP_NAME));
        report.setOutputFormat((String) this.nodeService.getProperty(report.getNodeRef(), ReportingModel.PROP_REPORTING_REPORTING_FORMAT));
        report.setOutputVersioned((Boolean) this.nodeService.getProperty(report.getNodeRef(), ReportingModel.PROP_REPORTING_REPORTING_VERSIONED));
        report.setReportingDocument(this.nodeService.hasAspect(report.getNodeRef(), ReportingModel.ASPECT_REPORTING_REPORTABLE));
        report.setTargetPath((String) this.nodeService.getProperty(report.getNodeRef(), ReportingModel.PROP_REPORTING_TARGET_PATH));
        report.setSubstitution((String) this.nodeService.getProperty(report.getNodeRef(), ReportingModel.PROP_REPORTING_SUBSTITUTION));
        List ar = this.nodeService.getTargetAssocs(report.getNodeRef(), ReportingModel.ASSOC_REPORTING_TARGET_NODE);
        if (!ar.isEmpty()) {
            report.setTargetNode(((AssociationRef) ar.get(0)).getTargetRef());
        }
    }

    public Properties getNameSpacesShortToLong() {
        if (this.namespacesShortToLong == null) {
            this.namespacesShortToLong = new Properties();
            Collection<String> keys = this.namespaceService.getPrefixes();

            for (String shortValue : keys) {
                String longValue = this.namespaceService.getNamespaceURI(shortValue);
                this.namespacesShortToLong.setProperty(shortValue, longValue);
                logger.debug("replaceShortQNameIntoLong: Replacing short value: " + shortValue + " into long value: " + longValue);
            }
        }

        return this.namespacesShortToLong;
    }

    public QName replaceShortQNameIntoLong(String inString) {
        Properties namespaces = this.getNameSpacesShortToLong();
        String namespace = inString.split(":")[0];
        String property = inString.split(":")[1];
        String longName = namespaces.getProperty(namespace);
        logger.debug("replaceShortQNameIntoLong: Creating long QName: " + longName);
        return QName.createQName(longName, property);
    }

    public String getTableColumnNameTruncated(String originalName) {
        String vendor = this.getDatabaseProvider();
        String modifiedName = originalName;
        if ("Oracle".equals(vendor) && originalName.length() > 30) {
            modifiedName = originalName.substring(0, 30);
        } else if ("MySQL".equals(vendor) && originalName.length() > 64) {
            modifiedName = originalName.substring(0, 64);
        } else if ("PostgreSQL".equals(vendor) && originalName.length() > 64) {
            modifiedName = originalName.substring(0, 64);
        }

        return modifiedName;
    }

    public Properties getClassToColumnType() {
        return this.classToColumn;
    }

    public Properties getReplacementDataType() {
        return this.replacementTypes;
    }

    public Properties getNameSpaces() {
        //if (this.namespaces == null) {
            this.namespaces = new Properties();
            Collection<String> keys = this.serviceRegistry.getNamespaceService().getPrefixes();

            for (String key : keys) {
                String value = this.serviceRegistry.getNamespaceService().getNamespaceURI(key);
                String into = key + "_";
                String from = "{" + value + "}";
                this.namespaces.setProperty(into, from);
                logger.debug("getNameSpaces: Replacing: " + from + " into: " + into);
            }
        //}
        return this.namespaces;
    }

    public String getDatabaseProvider() {
        return this.vendor;
    }

    public Properties getGlobalProperties() {
        if (this.globalProperties == null) {
            logger.fatal("Whoot! globalProperties object is null!!");
        }

        return this.globalProperties;
    }

    public String getBlacklist() {
        String keys;
        if (",".equals(this.blacklist)) {
            keys = this.getGlobalProperties().getProperty("reporting.harvest.blockkeys", "-") + ",";
            keys = keys + this.getGlobalProperties().getProperty("reporting.harvest.blacklist", "") + ",";
            keys = keys.replaceAll("-", "_");
            keys = keys.replaceAll(":", "_");
            setBlacklist(keys);
        } else {
            keys = this.blacklist;
        }

        return keys;
    }

    public String getValidTableName(String tableName) {
        if ("".equals(this.invalidTableName)) {
            this.invalidTableName = "," + this.globalProperties.getProperty("reporting.harvest.invalidTableNames", "select,from,where,group,order by,order,by,distinct") + ",";
        }

        String tableNameFixed;
        if (!this.getTableNameCache().containsKey(tableName)) {
            logger.debug("getValidTableName in--tableName=" + tableName);
            String replaceString = this.globalProperties.getProperty("reporting.harvest.invalidTableChars", "- `\'");
            tableNameFixed = tableName.toLowerCase().trim();

            for (int i = 0; i < replaceString.length(); ++i) {
                String character = String.valueOf(replaceString.charAt(i));
                logger.debug("getValidTableName character=" + character);

                while (tableNameFixed.contains(character)) {
                    tableNameFixed = tableNameFixed.substring(0, tableNameFixed.indexOf(character)) + "_" + tableNameFixed.substring(tableNameFixed.indexOf(character) + 1);
                }
            }

            logger.debug("getValidTableName out-tableName=" + tableNameFixed);
            if (this.invalidTableName.toLowerCase().contains("," + tableNameFixed + ",")) {
                tableNameFixed = "_" + tableNameFixed;
            }

            if (tableNameFixed.length() > 60) {
                tableNameFixed = tableNameFixed.substring(0, 60);
            }

            tableNameFixed = tableNameFixed.toLowerCase();
            logger.debug("getValidTableName after reserved words=" + tableNameFixed);
            this.addTableNameCache(tableName, tableNameFixed);
        } else {
            tableNameFixed = this.getTableNameCache().getProperty(tableName);
            logger.debug("getValidTableName cache hit=" + tableNameFixed);
        }

        tableNameFixed = this.getTableColumnNameTruncated(tableNameFixed);
        return tableNameFixed;
    }

    private String getSolrURL() {
        boolean notSecure = "none".equals(this.globalProperties.getProperty("solr.secureComms", "null"));
        return (notSecure ? "http://" : "https://")
                + this.globalProperties.getProperty("solr.host", "localhost")
                + ":" + (notSecure ? this.globalProperties.get("solr.port") : this.globalProperties.get("solr.port.ssl")) + "/solr/admin/cores?action=SUMMARY&wt=json";
    }

    public long getSolrLastTimestamp() {
        HttpClient httpClient = httpClientFactory.getHttpClient();
        GetMethod httpGet = null;
        try {
            httpGet = new GetMethod(getSolrURL());
            int requestStatus = httpClient.executeMethod(httpGet);
            if (requestStatus == 200) {
                String jsonResponse = httpGet.getResponseBodyAsString();
                if (jsonResponse != null) {
                    JSONObject response = new JSONObject(jsonResponse);
                    if (response.has("Summary")) {
                        JSONObject report = response.getJSONObject("Summary");
                        if (report != null && report.has("alfresco")) {
                            JSONObject alfrescoStat = report.getJSONObject("alfresco");
                            if (alfrescoStat != null && alfrescoStat.has("Last Index TX Commit Time")) {
                                return alfrescoStat.getLong("Last Index TX Commit Time");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable connect to URL", e);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return -1;
    }


    public Properties getTableQueries() {
        Properties allConfigs = new Properties();
        if (logger.isDebugEnabled()) {
            logger.debug("getTableQueries");
        }

        final NodeRef configsRoot = getReportsService().getServiceRootConfigFolder();
        if (configsRoot != null) {
            Set<QName> types = new HashSet<>();
            types.add(ContentModel.TYPE_CONTENT);

            List<ChildAssociationRef> childs = nodeService.getChildAssocs(configsRoot, types);
            for (ChildAssociationRef child : childs) {
                String configName = (String) nodeService.getProperty(child.getChildRef(), ContentModel.PROP_NAME);
                if (configName.toLowerCase().endsWith(".properties")) {
                    try {
                        ContentReader contentReader = contentService.getReader(child.getChildRef(), ContentModel.PROP_CONTENT);
                        Properties props = new Properties();
                        props.load(contentReader.getContentInputStream());
                        for (Object key : props.keySet()) {
                            allConfigs.put(key, props.getProperty(key.toString()));
                        }
                    } catch (ContentIOException var5) {
                        logger.error(var5.getMessage());
                    } catch (IOException var6) {
                        var6.printStackTrace();
                        logger.error(var6.getMessage());
                    }
                }
            }
        }
        return allConfigs;
    }

    private NodeRef getParentByType(final NodeRef currentNode, final QName targetType) {
        logger.debug("Enter getParentByType");
        NodeRef returnNode = null;
        if (currentNode != null) {
            returnNode = (NodeRef) AuthenticationUtil.runAs(new RunAsWork() {
                public NodeRef doWork() throws Exception {
                    NodeRef rootNode = ReportingHelper.this.nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                    logger.debug("getParentByType: rootNode=" + rootNode);
                    logger.debug("getParentByType: nodeRef=" + currentNode);

                    NodeRef returnNode = null;
                    NodeRef loopRef = currentNode;
                    boolean siteTypeFound = false;

                    while (!loopRef.equals(rootNode) && !siteTypeFound) {
                        loopRef = ReportingHelper.this.nodeService.getPrimaryParent(loopRef).getParentRef();
                        siteTypeFound = ReportingHelper.this.nodeService.getType(loopRef).equals(targetType);
                        if (siteTypeFound) {
                            returnNode = loopRef;
                            ReportingHelper.logger.debug("getParentByType: Found QName node!");
                        }
                    }

                    return returnNode;
                }
            }, AuthenticationUtil.getSystemUserName());
        }

        logger.debug("Exit getParentByType: " + returnNode);
        return returnNode;
    }
}
