package org.alfresco.reporting.action.executer;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.reporting.ReportingHelper;
import org.alfresco.reporting.ReportingModel;
import org.alfresco.reporting.db.DatabaseHelperBean;
import org.alfresco.reporting.processor.*;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class HarvestingExecuter extends ActionExecuterAbstractBase {
    private Properties globalProperties;
    private ServiceRegistry serviceRegistry;
    private SearchService searchService;
    private DatabaseHelperBean dbhb;
    private NodeService nodeService;
    private ReportingHelper reportingHelper;
    private static Log logger = LogFactory.getLog(HarvestingExecuter.class);

    private ActiveMQConnectionFactory connectionFactory;
    private JmsTemplate templateProducer;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDatabaseHelperBean(DatabaseHelperBean databaseHelperBean) {
        this.dbhb = databaseHelperBean;
    }

    public void setReportingHelper(ReportingHelper reportingHelper) {
        this.reportingHelper = reportingHelper;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setGlobalProperties(Properties globalProperties) {
        this.globalProperties = globalProperties;
    }

    public void setTemplateProducer(JmsTemplate templateProducer) {
        this.templateProducer = templateProducer;
    }

    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    protected void executeImpl(Action action, NodeRef harvestDefNodeRef) {
        String frequency = (String) action.getParameterValue("frequency");
        String fullQuery = this.getHarvestingDefinitionQuery(frequency);
        Iterator harvestIteration;
        NodeRef childRef;
        if (logger.isDebugEnabled()) {
            logger.debug("executeImpl: fullQuery=" + fullQuery);
        }
        if (fullQuery != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("executeImpl: frequency=" + frequency);
            }

            SearchParameters parentRef = new SearchParameters();
            parentRef.setLanguage("lucene");
            parentRef.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            parentRef.setQuery(fullQuery);
            ResultSet listRef = this.searchService.query(parentRef);
            if (logger.isDebugEnabled()) {
                logger.debug("executeImpl: Found results: " + listRef.length());
            }

            harvestIteration = listRef.iterator();

            while (harvestIteration.hasNext()) {
                childRef = ((ResultSetRow) harvestIteration.next()).getNodeRef();
                NodeRef parentRef1 = this.nodeService.getPrimaryParent(childRef).getParentRef();
                if (ReportingModel.TYPE_REPORTING_HARVEST_DEFINITION.equals(this.nodeService.getType(childRef)) && this.isHarvestingEnabledByProperties() && null != this.nodeService.getProperty(parentRef1, ReportingModel.PROP_REPORTING_HARVEST_ENABLED) && ((Boolean) this.nodeService.getProperty(parentRef1, ReportingModel.PROP_REPORTING_HARVEST_ENABLED)).booleanValue()) {
                    this.processHarvestDefinition(childRef);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("No harvesting run");
                    logger.debug("Properties: " + this.isHarvestingEnabledByProperties());
                    logger.debug("Object swich: " + this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_HARVEST_ENABLED));
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("executeImpl frequency=null -> it must be a HarvestingDefintion or ReportingRoot");
            }

            NodeRef parentRef2 = this.nodeService.getPrimaryParent(harvestDefNodeRef).getParentRef();
            if (ReportingModel.TYPE_REPORTING_HARVEST_DEFINITION.equals(this.nodeService.getType(harvestDefNodeRef)) && this.isHarvestingEnabledByProperties() && null != this.nodeService.getProperty(parentRef2, ReportingModel.PROP_REPORTING_HARVEST_ENABLED) && ((Boolean) this.nodeService.getProperty(parentRef2, ReportingModel.PROP_REPORTING_HARVEST_ENABLED)).booleanValue()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("executeImpl frequency=null -> it is a HarvestingDefintion");
                }

                this.processHarvestDefinition(harvestDefNodeRef);
            } else if (ReportingModel.TYPE_REPORTING_ROOT.equals(this.nodeService.getType(harvestDefNodeRef)) && this.isHarvestingEnabledByProperties()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("executeImpl frequency=null -> it is a ReportingRoot");
                }

                List listRef1 = this.nodeService.getChildAssocs(harvestDefNodeRef);
                harvestIteration = listRef1.iterator();

                while (harvestIteration.hasNext()) {
                    childRef = ((ChildAssociationRef) harvestIteration.next()).getChildRef();
                    if (ReportingModel.TYPE_REPORTING_HARVEST_DEFINITION.equals(this.nodeService.getType(childRef))) {
                        this.processHarvestDefinition(childRef);
                    }
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("executeImpl frequency=null -> it is nothing... (To)Do all");
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("No harvesting run");
                    logger.debug("Properties: " + this.isHarvestingEnabledByProperties());
                    logger.debug("Object swich: " + this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_HARVEST_ENABLED));
                }
            }
        }
    }

    private void processHarvestDefinition(NodeRef harvestDefNodeRef) {
        if (null != this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_TARGET_QUERIES_ENABLED) && (Boolean) this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_TARGET_QUERIES_ENABLED)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Kicking off QueryTables");
            }

            this.processTargetQueries(harvestDefNodeRef);
        }

        if (null != this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_USERGROUPS_ENABLED) && (Boolean) this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_USERGROUPS_ENABLED)) {
            logger.debug("Kicking off UserGroups");
            this.processUsersAndGroups();
        }

        if (null != this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_CATEGORIES_ENABLED) && (Boolean) this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_CATEGORIES_ENABLED)) {
            logger.debug("Kicking off Categories");
            this.processCategories(harvestDefNodeRef);
        }

        if (null != this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_AUDIT_ENABLED) && (Boolean) this.nodeService.getProperty(harvestDefNodeRef, ReportingModel.PROP_REPORTING_AUDIT_ENABLED)) {
            logger.debug("Kicking off AuditFramework");
            this.processAuditFramework(harvestDefNodeRef);
        }

    }

    protected void addParameterDefinitions(List paramList) {
        if (paramList != null) {
            paramList.add(new ParameterDefinitionImpl("frequency", DataTypeDefinition.TEXT, false, this.getParamDisplayLabel("frequency")));
        }
    }

    private boolean isHarvestingEnabledByProperties() {
        boolean enabled;

        try {
            enabled = this.globalProperties.getProperty("reporting.harvest.enabled", "true").equalsIgnoreCase("true");
        } catch (Exception var3) {
            logger.debug("isExecutionEnabled() returning exception. Thus returning true;");
            logger.debug(var3.getMessage());
            enabled = true;
        }

        return enabled;
    }

    private void processCategories(NodeRef harvestDefNodeRef) {
        try {
            CategoryProcessor e = new CategoryProcessor(this.dbhb, this.reportingHelper, this.serviceRegistry);
            e.havestNodes(harvestDefNodeRef);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private void processAuditFramework(NodeRef harvestDefinition) {
        try {
            AuditingExportProcessor e = new AuditingExportProcessor(this.dbhb, this.reportingHelper, this.serviceRegistry);
            e.havestNodes(harvestDefinition);
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    private void processTargetQueries(NodeRef harvestDefinition) {
        try {
            NodeRefBasedPropertyProcessor e = new NodeRefBasedPropertyProcessor(this.serviceRegistry, this.reportingHelper);
            e.setDbhb(this.dbhb);
            e.setConnectionFactory(this.connectionFactory);
            e.setTemplateProducer(this.templateProducer);
            e.havestNodes(harvestDefinition);
        } catch (Exception var3) {
            logger.error(var3.getMessage(), var3);
        }
    }

    private void processUsersAndGroups() {
        String e;
        try {
            e = this.dbhb.fixTableColumnName("person");
            if (!this.dbhb.tableIsRunning(e)) {
                Date nowFormattedDate = new Date((new Date()).getTime() - 5000L);
                String sitePersonProcessor = this.reportingHelper.getSimpleDateFormat().format(nowFormattedDate);
                this.dbhb.setLastTimestampStatusRunning(e);
                this.dbhb.dropTables(e);
                this.dbhb.createEmptyTables(e);
                PersonProcessor personProcessor = new PersonProcessor(this.serviceRegistry, this.reportingHelper, this.dbhb);
                personProcessor.processPersons(e);
                this.dbhb.setLastTimestampAndStatusDone(e, sitePersonProcessor);
            } else {
                logger.fatal("Table " + e + " is already running! (or another table)");
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        String nowFormattedDate1;
        try {
            e = this.dbhb.fixTableColumnName("groups");
            if (!this.dbhb.tableIsRunning(e)) {
                nowFormattedDate1 = this.reportingHelper.getSimpleDateFormat().format(new Date());
                this.dbhb.setLastTimestampStatusRunning(e);
                this.dbhb.dropTables(e);
                this.dbhb.createEmptyTables(e);
                GroupProcessor sitePersonProcessor1 = new GroupProcessor(this.serviceRegistry, this.reportingHelper, this.dbhb);
                sitePersonProcessor1.processGroups(e);
                this.dbhb.setLastTimestampAndStatusDone(e, nowFormattedDate1);
            } else {
                logger.fatal("Table " + e + " is already running! (or another table)");
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        try {
            e = this.dbhb.fixTableColumnName("siteperson");
            if (!this.dbhb.tableIsRunning(e)) {
                nowFormattedDate1 = this.reportingHelper.getSimpleDateFormat().format(new Date());
                this.dbhb.setLastTimestampStatusRunning(e);
                this.dbhb.dropTables(e);
                this.dbhb.createEmptyTables(e);
                SitePersonProcessor sitePersonProcessor2 = new SitePersonProcessor(this.serviceRegistry, this.reportingHelper, this.dbhb);
                sitePersonProcessor2.processSitePerson(e);
                this.dbhb.setLastTimestampAndStatusDone(e, nowFormattedDate1);
            } else {
                logger.fatal("Table " + e + " is already running! (or another table)");
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    private String getHarvestingDefinitionQuery(String frequency) {
        if (logger.isDebugEnabled()) {
            logger.debug("Enter getHarvestingDefinitionQuery frequency=" + frequency);
        }

        String query = "TYPE:\"reporting:harvestDefinition\"";
        if (frequency != null && !"".equals(frequency.trim())) {
            if (!"all".equalsIgnoreCase(frequency)) {
                query = query + " AND @reporting\\:harvestFrequency:\"" + frequency + "\"";
            }
        } else {
            query = null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Exit getHarvestingDefinitionQuery query=" + query);
        }

        return query;
    }
}
