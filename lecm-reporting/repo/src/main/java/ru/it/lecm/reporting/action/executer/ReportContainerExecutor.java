//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.it.lecm.reporting.action.executer;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.reporting.ReportingException;
import ru.it.lecm.reporting.ReportingHelper;
import ru.it.lecm.reporting.ReportingModel;
import ru.it.lecm.reporting.execution.ReportTemplate;
import ru.it.lecm.reporting.execution.ReportingContainer;
import ru.it.lecm.reporting.execution.ReportingRoot;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReportContainerExecutor extends ActionExecuterAbstractBase {
    private ActionService actionService;
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private SearchService searchService;
    private ReportingHelper reportingHelper;
    public static final String REPORTING_CONTAINER_NODEREF = "reportingContainerRef";
    public static final String REPORTING_ROOT_NODEREF = "reportingRootRef";
    public static final String NAME = "report-container-executer";
    private static Log logger = LogFactory.getLog(ReportContainerExecutor.class);

    public ReportContainerExecutor() {
    }

    protected void executeImpl(Action action, NodeRef reportingContainerRef) {
        NodeRef specificReportRef = null;
        if(logger.isDebugEnabled()) {
            logger.debug("enter executeImpl, getting " + this.nodeService.getType(reportingContainerRef) + " - " + this.nodeService.getProperty(reportingContainerRef, ContentModel.PROP_NAME));
        }

        if(reportingContainerRef == null) {
            throw new AlfrescoRuntimeException("not a valid NodeRef");
        } else {
            if(this.nodeService.hasAspect(reportingContainerRef, ReportingModel.ASPECT_REPORTING_CONTAINERABLE) || this.nodeService.hasAspect(reportingContainerRef, ReportingModel.ASPECT_REPORTING_REPORTABLE)) {
                if(this.nodeService.hasAspect(reportingContainerRef, ReportingModel.ASPECT_REPORTING_REPORTABLE)) {
                    specificReportRef = reportingContainerRef;
                    reportingContainerRef = this.reportingHelper.getReportingContainer(reportingContainerRef);
                }

                ReportingContainer reportingContainer = new ReportingContainer(reportingContainerRef);
                this.reportingHelper.initializeReportingContainer(reportingContainer);
                ReportingRoot reportingRoot = new ReportingRoot(this.reportingHelper.getReportingRoot(reportingContainer.getNodeRef()));
                this.reportingHelper.initializeReportingRoot(reportingRoot);
                List childList = this.nodeService.getChildAssocs(reportingContainerRef);
                Iterator childIterator = childList.iterator();

                while(childIterator.hasNext()) {
                    try {
                        ChildAssociationRef e = (ChildAssociationRef)childIterator.next();
                        NodeRef reportRef = e.getChildRef();
                        ReportTemplate report = new ReportTemplate(reportRef);
                        this.reportingHelper.initializeReport(report);
                        if(report.isReportingDocument()) {
                            logger.debug("executeImpl: Report name = " + report.getName());
                            if(specificReportRef != null) {
                                if(report.getNodeRef().equals(specificReportRef)) {
                                    this.processReport(report, reportingContainer, reportingRoot);
                                }
                            } else {
                                this.processReport(report, reportingContainer, reportingRoot);
                            }
                        }
                    } catch (Exception var11) {
                        logger.error("executeImpl: while (preparing) exeuting processing a report..." + var11.getMessage());
                        logger.error(var11.getCause());
                    }
                }
            }

            if(logger.isDebugEnabled()) {
                logger.debug("exit executeImpl");
            }

        }
    }

    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    private String processDateElementsInPath(String folderPath) {
        if(folderPath != null) {
            if(logger.isDebugEnabled()) {
                logger.debug("processDateElementsInFolderPath index=" + folderPath.indexOf("${"));
            }

            while(folderPath.indexOf("${") > -1) {
                String folderStart = folderPath.substring(0, folderPath.indexOf("${"));
                String datemask = folderPath.substring(folderPath.indexOf("${") + 2, folderPath.indexOf("}"));
                String folderEnd = folderPath.substring(folderPath.indexOf("}") + 1);
                if(logger.isDebugEnabled()) {
                    logger.debug("processDateElementsInFolderPath datemask=" + datemask);
                }

                SimpleDateFormat sdf = new SimpleDateFormat(datemask);
                folderPath = folderStart + sdf.format(new Date()) + folderEnd;
            }
        }

        if(logger.isDebugEnabled()) {
            logger.debug("processDateElementsInFolderPath returning " + folderPath);
        }

        return folderPath;
    }

    private void processReport(ReportTemplate report, ReportingContainer reportingContainer, ReportingRoot reportingRoot) {
        new Properties();
        if(logger.isDebugEnabled()) {
            logger.debug("enter processReport, report=" + report.getName());
        }

        String targetPath = report.getTargetPath();
        Properties targetQueries = reportingRoot.getTargetQueries();
        Properties keyValues;
        String newTarget;
        String keys;
        String key;
        String value;
        if(targetPath != null) {
            targetPath = targetPath.trim();
            if(targetPath.indexOf("${") == 0 && targetPath.indexOf("}") > -1) {
                if(logger.isDebugEnabled()) {
                    logger.debug("processReportable: it is a distribution by container");
                }

                newTarget = targetPath.substring(2, targetPath.indexOf("}"));
                keys = targetPath.substring(targetPath.indexOf("}") + 1);
                keys = this.processDateElementsInPath(keys);
                if(logger.isDebugEnabled()) {
                    logger.debug("  placeholder:   " + newTarget);
                    logger.debug("  relative path: " + keys);
                }

                if(newTarget != null) {
                    if(targetQueries.containsKey(newTarget)) {
                        if(logger.isDebugEnabled()) {
                            logger.debug("processReport: Processing with placeholder: " + newTarget);
                        }

                        key = targetQueries.getProperty(newTarget);
                        value = this.reportingHelper.getSearchLanguage();
                        if(logger.isDebugEnabled()) {
                            logger.debug("processReport: query2=" + key + "(" + value + ")");
                        }

                        ResultSet placeHolderResults = this.searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, value, key);

                        NodeRef targetRef1;
                        for(Iterator i$ = placeHolderResults.iterator(); i$.hasNext(); this.createExecuteReport(targetRef1, report, keyValues)) {
                            ResultSetRow placeHolderRow = (ResultSetRow)i$.next();
                            NodeRef targetRootRef = placeHolderRow.getChildAssocRef().getChildRef();
                            if(logger.isDebugEnabled()) {
                                logger.debug("Found targetRoot: " + this.nodeService.getProperty(targetRootRef, ContentModel.PROP_NAME));
                            }

                            NodeRef storageNodeRef = targetRootRef;
                            if(logger.isDebugEnabled()) {
                                logger.debug("processReport: storageNodeRef before=" + targetRootRef);
                            }

                            try {
                                if(this.nodeService.getType(targetRootRef).equals(ContentModel.TYPE_PERSON)) {
                                    if(logger.isDebugEnabled()) {
                                        logger.debug("processReport: The value=" + this.nodeService.getProperty(targetRootRef, ContentModel.PROP_HOMEFOLDER));
                                    }

                                    if(this.nodeService.getProperty(targetRootRef, ContentModel.PROP_HOMEFOLDER) == null) {
                                        if(logger.isDebugEnabled()) {
                                            logger.debug("processReport: createGetRepositoryPath: ow boy, no UserHome available for user " + this.nodeService.getProperty(targetRootRef, ContentModel.PROP_USERNAME));
                                        }

                                        throw new ReportingException("No UserHome found for user " + this.nodeService.getProperty(targetRootRef, ContentModel.PROP_USERNAME));
                                    }

                                    if(logger.isDebugEnabled()) {
                                        logger.debug("processReport: createGetRepositoryPath: SWAPPING Person for UserHome");
                                    }

                                    storageNodeRef = (NodeRef)this.nodeService.getProperty(targetRootRef, ContentModel.PROP_HOMEFOLDER);
                                    if(logger.isDebugEnabled()) {
                                        logger.debug("processReport: createGetRepositoryPath: storageNodeRef after swap=" + storageNodeRef);
                                    }
                                } else if(logger.isDebugEnabled()) {
                                    logger.debug("createGetRepositoryPath: no SWAPPING");
                                }
                            } catch (ReportingException var21) {
                                logger.fatal("processReport: User without a UserHome... Silent ignore");
                            }

                            if(logger.isDebugEnabled()) {
                                logger.debug("processReport: processReportstorageNodeRef fully after=" + storageNodeRef);
                            }

                            keyValues = report.getSubstitution();
                            if(logger.isDebugEnabled()) {
                                logger.debug("processReport: initial keyValues = " + keyValues);
                            }

                            Enumeration keys1 = keyValues.keys();

                            while(keys1.hasMoreElements()) {
                                String targetRef = (String)keys1.nextElement();
                                String value1 = keyValues.getProperty(targetRef, "");
                                if(logger.isDebugEnabled()) {
                                    logger.debug("Initial key=value; " + targetRef + "=" + value1);
                                }

                                if(value1.contains(":") && !"".equals(value1)) {
                                    QName property = this.reportingHelper.replaceShortQNameIntoLong(value1);
                                    if(logger.isDebugEnabled()) {
                                        logger.debug("processReport: QName=" + property);
                                        logger.debug("processReport: key=" + targetRef + " value=" + this.nodeService.getProperty(targetRootRef, property));
                                    }

                                    String propertyValue = this.nodeService.getProperty(targetRootRef, property).toString();
                                    if(logger.isDebugEnabled()) {
                                        logger.debug("processReport: propertyValue=" + propertyValue);
                                    }

                                    keyValues.setProperty(targetRef, propertyValue);
                                } else {
                                    keyValues.setProperty(targetRef, value1);
                                    if(logger.isDebugEnabled()) {
                                        logger.debug("processReport: key=" + targetRef + " value=" + value1 + " targetRootRef=" + targetRootRef);
                                    }
                                }
                            }

                            if(logger.isDebugEnabled()) {
                                logger.debug("processReport: final keyValues = " + keyValues);
                            }

                            targetRef1 = this.createGetRepositoryPath(storageNodeRef, keys);
                            if(logger.isDebugEnabled()) {
                                logger.debug("processReport: Found full path: " + this.nodeService.getPath(targetRef1));
                                logger.debug("processReport: keyValues = " + keyValues);
                            }
                        }
                    } else {
                        logger.warn("Cannot deal with placeholder: " + newTarget);
                    }
                }
            }
        }

        if(report.getTargetNode() != null && !"".equals(report.getTargetNode())) {
            newTarget = null;
            NodeRef newTarget1;
            if(report.getTargetPath() != null && !"".equals(report.getTargetPath())) {
                keys = this.processDateElementsInPath(report.getTargetPath());
                newTarget1 = this.createGetRepositoryPath(report.getTargetNode(), keys);
            } else {
                newTarget1 = report.getTargetNode();
            }

            if(logger.isDebugEnabled()) {
                logger.debug("executing fixed output path");
                logger.debug("  report    : " + report.getName());
                logger.debug("  targetNode: " + newTarget1);
            }

            keyValues = report.getSubstitution();
            logger.debug("processReport: initial keyValues = " + keyValues);
            Enumeration keys2 = keyValues.keys();

            while(keys2.hasMoreElements()) {
                key = (String)keys2.nextElement();
                value = keyValues.getProperty(key, "");
                keyValues.setProperty(key, value);
                logger.debug("processReport: key=" + key + " value=" + value);
            }

            logger.debug("processReport: final keyValues = " + keyValues);
            this.createExecuteReport(newTarget1, report, keyValues);
        }

        if(logger.isDebugEnabled()) {
            logger.debug("exit processReportable");
        }

    }

    private void createExecuteReport(NodeRef parentRef, ReportTemplate report, Properties keyValues) {
        if(logger.isDebugEnabled()) {
            logger.debug("enter createExecuteReport");
        }

        String filename = this.getOutputFilename(report);
        filename = this.processDateElementsInPath(filename);
        if(logger.isDebugEnabled()) {
            logger.debug("createExecuteReport: just before createGetFilename, parentRef=" + parentRef + " filename=" + filename);
        }

        NodeRef targetDocumentRef = this.createGetFilename(parentRef, filename, report.isOutputVersioned());
        this.nodeService.addAspect(targetDocumentRef, ReportingModel.ASPECT_REPORTING_EXECUTIONRESULT, (Map)null);
        if(logger.isDebugEnabled()) {
            logger.debug("createExecuteReport: just before executeReportExecuter, ");
            logger.debug("  targetDocumentRef = " + targetDocumentRef);
            logger.debug("  keyValues         = " + keyValues);
        }

        this.executeReportExecuter(report, targetDocumentRef, keyValues);
        if(logger.isDebugEnabled()) {
            logger.debug("exit createExecuteReport");
        }

    }

    private String getOutputFilename(ReportTemplate report) {
        String outputType = report.getOutputFormat();
        String filename = report.getName();
        filename = filename.substring(0, filename.lastIndexOf(".") + 1);
        NodeRef reportingRootRef = this.reportingHelper.getReportingRoot(report.getNodeRef());
        ReportingRoot reportingRoot = new ReportingRoot(reportingRootRef);
        this.reportingHelper.initializeReportingRoot(reportingRoot);
        if(logger.isDebugEnabled()) {
            logger.debug("RootRef name=" + reportingRoot.getName());
        }

        String ext = "";
        ext = reportingRoot.getOutputExtensionPdf();
        if(outputType.equalsIgnoreCase("excel")) {
            ext = reportingRoot.getOutputExtensionExcel();
        }

        if(outputType.equalsIgnoreCase("csv")) {
            ext = reportingRoot.getOutputExtensionCsv();
        }

        filename = filename + ext;
        return filename;
    }

    private NodeRef createGetFilename(NodeRef parentRef, String filename, boolean versioned) {
        if(logger.isDebugEnabled()) {
            logger.debug("enter createGetFilename: Filename=" + filename);
        }

        NodeRef childRef = this.getChildDocument(parentRef, filename);
        if(childRef == null) {
            childRef = this.fileFolderService.create(parentRef, filename, ContentModel.TYPE_CONTENT).getNodeRef();
            if(versioned && !this.nodeService.hasAspect(childRef, ContentModel.ASPECT_VERSIONABLE)) {
                this.nodeService.addAspect(childRef, ContentModel.ASPECT_VERSIONABLE, (Map)null);
            }

            if(logger.isDebugEnabled()) {
                logger.debug("  created document   " + filename);
            }
        } else if(logger.isDebugEnabled()) {
            logger.debug("  retrieved document " + filename);
        }

        if(logger.isDebugEnabled()) {
            logger.debug("exit createGetFilename");
        }

        return childRef;
    }

    private NodeRef getChildDocument(NodeRef nodeRef, String filename) {
        NodeRef returnRef = null;
        List fileInfoList = this.fileFolderService.listFiles(nodeRef);
        Iterator i$ = fileInfoList.iterator();

        while(i$.hasNext()) {
            FileInfo fileInfo = (FileInfo)i$.next();
            if(fileInfo.getName().equals(filename)) {
                returnRef = fileInfo.getNodeRef();
                break;
            }
        }

        return returnRef;
    }

    public NodeRef createGetRepositoryPath(final NodeRef targetRootRef, final String relativePath) {
        if(logger.isDebugEnabled()) {
            logger.debug("enter createGetRepositoryPath: relativePath=" + relativePath);
        }

        NodeRef returnRef = (NodeRef)AuthenticationUtil.runAs(new RunAsWork() {
            public NodeRef doWork() throws Exception {
                NodeRef returnRef = targetRootRef;
                String[] path = relativePath.split("/");
                if(ReportContainerExecutor.logger.isDebugEnabled()) {
                    ReportContainerExecutor.logger.debug("createGetRepositoryPath path elements=" + path.length);
                }

                String[] arr$ = path;
                int len$ = path.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    String element = arr$[i$];
                    if(element != null && element.length() > 0) {
                        if(ReportContainerExecutor.logger.isDebugEnabled()) {
                            ReportContainerExecutor.logger.debug("createGetRepositoryPath current element=" + element);
                        }

                        NodeRef childRef = ReportContainerExecutor.this.getChildFolder(returnRef, element);
                        if(childRef == null) {
                            childRef = ReportContainerExecutor.this.fileFolderService.create(returnRef, element, ContentModel.TYPE_FOLDER).getNodeRef();
                            if(ReportContainerExecutor.logger.isDebugEnabled()) {
                                ReportContainerExecutor.logger.debug("  created folder   " + element);
                            }
                        } else if(ReportContainerExecutor.logger.isDebugEnabled()) {
                            ReportContainerExecutor.logger.debug("  retrieved folder " + element);
                        }

                        returnRef = childRef;
                    }
                }

                return returnRef;
            }
        }, AuthenticationUtil.getSystemUserName());
        if(logger.isDebugEnabled()) {
            logger.debug("exit createGetRepositoryPath: returning: " + returnRef);
        }

        return returnRef;
    }

    private NodeRef getChildFolder(NodeRef nodeRef, String childName) {
        NodeRef returnRef = null;
        if(logger.isDebugEnabled()) {
            logger.debug("enter getChildFolder with nodeRef=" + nodeRef + " childName=" + childName);
        }

        List fileInfoList = this.fileFolderService.listFolders(nodeRef);
        Iterator i$ = fileInfoList.iterator();

        while(i$.hasNext()) {
            FileInfo fileInfo = (FileInfo)i$.next();
            if(fileInfo.getName().equalsIgnoreCase(childName)) {
                returnRef = fileInfo.getNodeRef();
                if(logger.isDebugEnabled()) {
                    logger.debug("getChildFolder: We are in the break!");
                }
                break;
            }
        }

        return returnRef;
    }

    private void executeReportExecuter(ReportTemplate report, NodeRef targetDocumentRef, Properties keyValues) {
        if(logger.isDebugEnabled()) {
            logger.debug("enter executeReportExecuter");
            logger.debug("  reportRef        : " + report.getNodeRef());
            logger.debug("  reportName       : " + report.getName());
            logger.debug("  targetDocumentRef: " + targetDocumentRef);
            logger.debug("  outputType       : " + report.getOutputFormat());
            logger.debug("  parameters       : " + keyValues);
        }

        Action action = this.actionService.createAction("report-executer");
        action.setParameterValue("outputType", report.getOutputFormat());
        action.setParameterValue("targetDocument", targetDocumentRef);
        if(keyValues.size() > 0) {
            action.setParameterValue("seperator", "~");
            Enumeration keys = keyValues.keys();
            int i = 0;

            while(keys.hasMoreElements()) {
                ++i;
                String key = (String)keys.nextElement();
                String pushString = key + "~" + keyValues.getProperty(key, "");
                if(logger.isDebugEnabled()) {
                    logger.debug("Setting report parameter " + key + " = " + keyValues.getProperty(key, ""));
                }

                if(i == 1) {
                    action.setParameterValue("param1", pushString);
                }

                if(i == 2) {
                    action.setParameterValue("param2", pushString);
                }

                if(i == 3) {
                    action.setParameterValue("param3", pushString);
                }

                if(i == 4) {
                    action.setParameterValue("param4", pushString);
                }
            }
        }

        this.actionService.executeAction(action, report.getNodeRef());
        if(logger.isDebugEnabled()) {
            logger.debug("Exit executeReportExecuter");
        }

    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public void setReportingHelper(ReportingHelper reportingHelper) {
        this.reportingHelper = reportingHelper;
    }
}
