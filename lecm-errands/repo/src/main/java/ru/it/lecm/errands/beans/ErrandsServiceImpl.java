package ru.it.lecm.errands.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.Pair;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmObjectsService;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.resolutions.api.ResolutionsService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:43
 */
public class ErrandsServiceImpl extends BaseBean implements ErrandsService {

    final protected Logger logger = LoggerFactory.getLogger(ErrandsServiceImpl.class);

    private EnumMap<ATTACHMENT_CATEGORIES, String> attachmentCategoriesMap;
    private EnumMap<ERRANDS_STATUS, String> errandStatusesMap;

    private static enum FilterEnum {
        ALL,
        ACTIVE,
        COMPLETE
    }

    public static final int MAX_ITEMS = 1000;

    private DocumentService documentService;
    private DocumentConnectionService documentConnectionService;
    private OrgstructureBean orgstructureService;
    private StateMachineServiceBean stateMachineService;
    private LecmObjectsService lecmObjectsService;
    private NamespaceService namespaceService;
    private BusinessJournalService businessJournalService;
	private LecmPermissionService lecmPermissionService;
	private NodeRef settingsNode;
	private NodeRef dashletSettingsNode;
    private EDSDocumentService edsDocumentService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setLecmObjectsService(LecmObjectsService lecmObjectsService) {
        this.lecmObjectsService = lecmObjectsService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
            this.lecmPermissionService = lecmPermissionService;
    }

    public void setEdsDocumentService(EDSDocumentService edsDocumentService) {
        this.edsDocumentService = edsDocumentService;
    }

    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }
    
    @Override
	public void initServiceImpl() {
		if (null == getSettingsNode()) {
			settingsNode = createSettingsNode();
		}
		if (null == getDashletSettingsNode()) {
			dashletSettingsNode = createDashletSettingsNode();
		}

        errandStatusesMap = new EnumMap<ERRANDS_STATUS,String>(ERRANDS_STATUS.class){{
            put(ERRANDS_STATUS.REMOVED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.removed", "??????????????"));
            put(ERRANDS_STATUS.CANCELLED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.cancelled", "????????????????"));
            put(ERRANDS_STATUS.NOT_EXECUTED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.not-executed", "???? ??????????????????"));
            put(ERRANDS_STATUS.EXECUTED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.executed", "??????????????????"));
            put(ERRANDS_STATUS.WAIT_FOR_EXECUTION, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.wait-for-execution", "?????????????? ????????????????????"));
            put(ERRANDS_STATUS.ON_EXECUTION, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.on-execution", "???? ????????????????????"));
            put(ERRANDS_STATUS.REPORT_CHECK, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.report-check", "???? ???????????????? ????????????"));
            put(ERRANDS_STATUS.ON_REWORK, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.on-rework", "???? ??????????????????"));
            put(ERRANDS_STATUS.PERIODICALLY, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.statemachine-status.periodically-execution", "???? ?????????????????????????? ????????????????????"));
        }};

        attachmentCategoriesMap = new EnumMap<ATTACHMENT_CATEGORIES,String>(ATTACHMENT_CATEGORIES.class){{
            put(ATTACHMENT_CATEGORIES.ERRAND, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.document.attachment.category.ERRAND.title", "??????????????????"));
            put(ATTACHMENT_CATEGORIES.CONTROL, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.document.attachment.category.CONTROL.title", "????????????????"));
            put(ATTACHMENT_CATEGORIES.EXECUTION, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.document.attachment.category.EXECUTION.title", "????????????????????"));
            put(ATTACHMENT_CATEGORIES.COEXECUTORS_REPORTS, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.errands.document.attachment.category.EXECUTION_REPORTS.title", "???????????? ????????????????????????????"));
        }};
	}

    @Override
    public String getErrandStatusName(ERRANDS_STATUS code) {
        return errandStatusesMap != null ? errandStatusesMap.get(code) : null;
    }

    @Override
    public String getAttachmentCategoryName(ATTACHMENT_CATEGORIES code) {
        return attachmentCategoriesMap != null ? attachmentCategoriesMap.get(code) : null;
    }

    @Override
    public NodeRef getServiceRootFolder() {
	return getFolder(ERRANDS_ROOT_ID);
    }

    @Override
    public NodeRef getDraftRoot() {
        return documentService.getDraftRootByType(TYPE_ERRANDS);
    }

    @Override
    public NodeRef getSettingsNode() {
//		TODO: ?????????? ????????????????. ???????????????? ???????????????? ?? ?????????? createSettingsNode
		if (settingsNode == null) {
			settingsNode = nodeService.getChildByName(this.getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, ERRANDS_SETTINGS_NODE_NAME);
		}
		
		return settingsNode;
    }

    @Override
    public NodeRef getDashletSettingsNode() {
		if (dashletSettingsNode == null) {
			dashletSettingsNode = nodeService.getChildByName(this.getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, ERRANDS_DASHLET_SETTINGS_NODE_NAME);
		}
		return dashletSettingsNode;
    }

    @Override
    public NodeRef createSettingsNode() {
//        try {
//            lecmTransactionHelper.checkTransaction();
//        } catch (TransactionNeededException ex) {
//            throw new WriteTransactionNeededException("Can't create settings node");
//        }

        final NodeRef rootFolder = this.getServiceRootFolder();
        NodeRef settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, ERRANDS_SETTINGS_NODE_NAME);
        if (settingsRef == null) {
            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ERRANDS_SETTINGS_NODE_NAME);
            QName nodeTypeQName = TYPE_ERRANDS_SETTINGS;

            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
            properties.put(ContentModel.PROP_NAME, ERRANDS_SETTINGS_NODE_NAME);
            ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
            settingsRef = associationRef.getChildRef();
        }
        return settingsRef;
    }

    @Override
    public NodeRef createDashletSettingsNode() {
//        try {
//            lecmTransactionHelper.checkTransaction();
//        } catch (TransactionNeededException ex) {
//            throw new WriteTransactionNeededException("Can't create settings node");
//        }

        final NodeRef rootFolder = this.getServiceRootFolder();
        NodeRef settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, ERRANDS_DASHLET_SETTINGS_NODE_NAME);
        if (settingsRef == null) {
            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ERRANDS_DASHLET_SETTINGS_NODE_NAME);
            QName nodeTypeQName = TYPE_ERRANDS_DASHLET_SETTINGS;

            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
            properties.put(ContentModel.PROP_NAME, ERRANDS_DASHLET_SETTINGS_NODE_NAME);
            ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
            settingsRef = associationRef.getChildRef();
        }
        return settingsRef;
    }

    @Override
    public ModeChoosingExecutors getModeChoosingExecutors() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            String modeChoosingExecutors = (String) nodeService.getProperty(settings, SETTINGS_PROP_MODE_CHOOSING_EXECUTORS);
            if (SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION.equals(modeChoosingExecutors)) {
                return ModeChoosingExecutors.ORGANIZATION;
            }
        }
        return ModeChoosingExecutors.UNIT;
    }

    @Override
    public boolean isTransferRightToBaseDocument() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            return Boolean.TRUE.equals(nodeService.getProperty(settings, SETTINGS_PROP_TRANSFER_RIGHT));
        }
        return false;
    }

    @Override
    public boolean isHideAdditionAttributes() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            return Boolean.TRUE.equals(nodeService.getProperty(settings, SETTINGS_HIDE_ADDITIONAL_ATTRS));
        }
        return false;
    }

    @Override
    public NodeRef getCurrentUserSettingsNode() {
//		TODO: ?????????? ????????????????, ???????????????? ???????????????? ?? createCurrentUserSettingsNode
        final NodeRef rootFolder = this.getServiceRootFolder();
        final String settingsObjectName = authService.getCurrentUserName() + "_" + ERRANDS_SETTINGS_NODE_NAME;

        NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
	return settings;
    }

    @Override
    public NodeRef createCurrentUserSettingsNode() throws WriteTransactionNeededException {
//		TODO: ???????????????? ???? ?????????????????? ????????????????????, ???????? ?????? ????????????????.
        try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create settings node");
        }

        final NodeRef rootFolder = this.getServiceRootFolder();
        final String settingsObjectName = authService.getCurrentUserName() + "_" + ERRANDS_SETTINGS_NODE_NAME;

        NodeRef settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
        if (settingsRef == null) {
            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, settingsObjectName);
            QName nodeTypeQName = TYPE_ERRANDS_USER_SETTINGS;

            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
            properties.put(ContentModel.PROP_NAME, settingsObjectName);
            ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
            settingsRef = associationRef.getChildRef();
        }
        return settingsRef;
    }

    @Override
    public boolean isDefaultWithoutInitiatorApproval() {
		NodeRef settings = getCurrentUserSettingsNode();
        if (settings != null) {
            return (Boolean) nodeService.getProperty(settings, USER_SETTINGS_PROP_WITHOUT_INITIATOR_APPROVAL);
        }
        return false;
    }

    @Override
    public NodeRef getDefaultInitiator() {
        NodeRef result = orgstructureService.getCurrentEmployee();
        if (orgstructureService.isCurrentEmployeeHasBusinessRole(BUSINESS_ROLE_CHOOSING_INITIATOR)) {
            NodeRef settings = getCurrentUserSettingsNode();
            if (settings != null) {
                List<AssociationRef> defaultInitiatorAssocs = nodeService.getTargetAssocs(settings, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR);
                if (defaultInitiatorAssocs.size() > 0) {
                    result = defaultInitiatorAssocs.get(0).getTargetRef();
                }
            }
        }
        return result;
    }

    @Override
    public List<NodeRef> getDefaultSubject() {
	NodeRef settings = getCurrentUserSettingsNode();
        if (settings != null) {
            return findNodesByAssociationRef(settings, USER_SETTINGS_ASSOC_DEFAULT_SUBJECT, null, ASSOCIATION_TYPE.TARGET);
        }
        return null;
    }

    @Override
    public List<NodeRef> getAvailableExecutors() {
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		return getAvailableExecutors(currentEmployee);
    }

	@Override
    public List<NodeRef> getAvailableExecutors(NodeRef employeeRef) {
        if (getModeChoosingExecutors() == ModeChoosingExecutors.ORGANIZATION) {
            return null;
        } else {
            List<NodeRef> subordinates = orgstructureService.getBossSubordinate(employeeRef, true);
            List<NodeRef> result = new ArrayList<NodeRef>();

            result.add(employeeRef);
            result.addAll(subordinates);

            return result;
        }
    }

    private List<NodeRef> getDocumentErrands(NodeRef document, Boolean active, List<QName> roles) {
        if (document == null) {
            return new ArrayList<>();
        }


        List<NodeRef> result = new ArrayList<>();
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();


        List<NodeRef> documentErrandsDocs = documentConnectionService.getConnectedDocuments(document, "onBasis", TYPE_ERRANDS, true);
        for (NodeRef errand : documentErrandsDocs) {

			boolean hasPermission = lecmPermissionService.hasPermission("_ReadProperties", errand);
			hasPermission = hasPermission && lecmPermissionService.hasPermission("_ReadAssociations", errand);
			if (!hasPermission) {
				continue;
			}

            String status = (String) nodeService.getProperty(errand, StatemachineModel.PROP_STATUS);

            if (active != null) {
                if (active) {
                    if (stateMachineService.hasActiveStatemachine(errand)) {
                        if (stateMachineService.isDraft(errand)) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    if (ERRANDS_STATUS.REMOVED.isStatusEquals(status, this)) {
                        continue;
                    }
                } else {
                    if (!stateMachineService.isFinal(errand)) {
                        continue;
                    }
                    if (ERRANDS_STATUS.REMOVED.isStatusEquals(status, this)) {
                        continue;
                    }
                }
            } else {
//                if (stateMachineService.isDraft(errand)) {
//                    continue;
//                }
//                if ("??????????????".equals(status)) {
//                    continue;
//                }
            }

            if (roles != null) {
                for (QName role : roles) {
                    if (currentEmployee.equals(findNodeByAssociationRef(errand, role, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))) {
                        result.add(errand);
                        break;
                    }
                }
            } else {
                result.add(errand);
            }
        }

        Collections.sort(result, new Comparator<NodeRef>() {
            @Override
            public int compare(NodeRef o1, NodeRef o2) {
                Date dateCreated1 = (Date) nodeService.getProperty(o1, ContentModel.PROP_CREATED);
                Date dateCreated2 = (Date) nodeService.getProperty(o2, ContentModel.PROP_CREATED);
                return dateCreated1.compareTo(dateCreated2);
            }
        });

        return result;
    }

    @Override
    public List<NodeRef> getFilterDocumentErrands(NodeRef document, String filter, List<QName> roles) {
        if (filter != null && !filter.equals("")) {
            switch (FilterEnum.valueOf(filter.toUpperCase())) {
                case ALL: {
                    return getDocumentErrands(document, null, roles);
                }
                case ACTIVE: {
                    return getDocumentErrands(document, true, roles);
                }
                case COMPLETE: {
                    return getDocumentErrands(document, false, roles);
                }
            }
        }
        return getDocumentErrands(document, null, roles);
    }

    @Override
    public List<NodeRef> getErrandsDocuments(List<String> paths, int skipCount, int maxItems) {
        List<QName> types = new ArrayList<QName>();
        types.add(TYPE_ERRANDS);

        List<SortDefinition> sort = new ArrayList<SortDefinition>();
        List<NodeRef> sortingErrands = new ArrayList<NodeRef>();
        List<NodeRef> result = new ArrayList<NodeRef>();
        List<String> status = stateMachineService.getStatuses(ErrandsService.TYPE_ERRANDS, true, false);

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        // ?????????????????? ?????????????????? ???? ????????????????, ???????????????????????????? ?? ???? ?????????? ????????????????????
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_IS_IMPORTANT.toString(), false));
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_IS_EXPIRED.toString(), false));
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_LIMITATION_DATE.toString(), true));

        String filter = new String("(@lecm\\-errands\\:executor\\-assoc\\-ref:\"#current-user\" OR @lecm\\-errands\\:coexecutors\\-assoc\\-ref:\"#current-user\") AND NOT @lecm\\-statemachine\\-aspects\\:is\\-final:true AND NOT @lecm\\-statemachine\\-aspects\\:is\\-draft:true");

        for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, paths, status, filter, sort)) {
            sortingErrands.add(nodeRef);
        }

        int endIndex = (skipCount + maxItems) < sortingErrands.size() ? (skipCount + maxItems) : sortingErrands.size();

        for (int i = skipCount; i < endIndex; i++) {
            result.add(sortingErrands.get(i));
        }
        return result;
    }

    @Override
    public List<NodeRef> getActiveErrands(List<String> paths, int skipCount, int maxItems) {
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        List<QName> types = new ArrayList<QName>();
        types.add(TYPE_ERRANDS);

        List<NodeRef> employees = orgstructureService.getBossSubordinate(currentEmployee, true);

        List<SortDefinition> sort = new ArrayList<SortDefinition>();
        List<NodeRef> sortingErrands = new ArrayList<NodeRef>();
        List<NodeRef> result = new ArrayList<NodeRef>();
        List<String> status = stateMachineService.getStatuses(ErrandsService.TYPE_ERRANDS, true, false);

        // ?????????????????? ???? ???????????????????????? ?????????????????? ?? ???? ?????????? ????????????????????
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_NUMBER.toString(), false));
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_LIMITATION_DATE.toString(), false));

        String filter = new String("NOT @lecm\\-statemachine\\-aspects\\:-final:true AND NOT @lecm\\-statemachine\\-aspects\\:is\\-draft:true");

        for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, paths, status, filter, sort)) {
            if (employees.containsAll(findNodesByAssociationRef(nodeRef, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))) {
                sortingErrands.add(nodeRef);
            }
        }

        int endIndex = (skipCount + maxItems) < sortingErrands.size() ? (skipCount + maxItems) : sortingErrands.size();

        for (int i = skipCount; i < endIndex; i++) {
            result.add(sortingErrands.get(i));
        }
        return result;
    }

    @Override
    public NodeRef getLinksFolderRef(final NodeRef document) {
//		TODO: ?????????? ????????????????, ???????????????? ???????????????? ?? ?????????? createLinksFolderRef
        //?? ?????????? ???????????? ?????????????????? ?????? ???? ???????????????
//        return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
//            @Override
//            public NodeRef doWork() throws Exception {
				return nodeService.getChildByName(document, ContentModel.ASSOC_CONTAINS, ERRANDS_LINK_FOLDER_NAME);
//                RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//                    @Override
//                    public NodeRef execute() throws Throwable {
//                        NodeRef linkFolder = nodeService.getChildByName(document, ContentModel.ASSOC_CONTAINS, ERRANDS_LINK_FOLDER_NAME);
//                        if (linkFolder == null) {
//                            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ERRANDS_LINK_FOLDER_NAME);
//                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//                            properties.put(ContentModel.PROP_NAME, ERRANDS_LINK_FOLDER_NAME);
//                            ChildAssociationRef childAssoc = nodeService.createNode(document, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
//                            return childAssoc.getChildRef();
//                        } else {
//                            return linkFolder;
//                        }
//                    }
//                });
//            }
//        });
    }

	@Override
	public NodeRef createLinksFolderRef(final NodeRef document) throws WriteTransactionNeededException {
//		TODO: ???????????????? ???? ?????????????????? ????????????????????, ???????? ?????? ????????????????.
		try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create settings node" );
        }

		NodeRef linkFolder = nodeService.getChildByName(document, ContentModel.ASSOC_CONTAINS, ERRANDS_LINK_FOLDER_NAME);
		if (linkFolder == null) {
			QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ERRANDS_LINK_FOLDER_NAME);
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(ContentModel.PROP_NAME, ERRANDS_LINK_FOLDER_NAME);
			ChildAssociationRef childAssoc = nodeService.createNode(document, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
			return childAssoc.getChildRef();
		}
		return linkFolder;
	}

    @Override
    public List<NodeRef> getLinks(NodeRef document) {
        return getLinks(document, 0, MAX_ITEMS);
    }

    @Override
    public List<NodeRef> getLinks(NodeRef document, int skipCount, int maxItems) {
        List<NodeRef> results = new ArrayList<NodeRef>();
        List<Pair<QName, Boolean>> sortProps = new ArrayList<Pair<QName, Boolean>>(1);
        sortProps.add(new Pair<QName, Boolean>(ContentModel.PROP_MODIFIED, false));

        PagingRequest pageRequest = new PagingRequest(skipCount, maxItems, null);
        pageRequest.setRequestTotalCountMax(MAX_ITEMS);

        PagingResults<NodeRef> pageOfNodeInfos = null;
        FileFilterMode.setClient(FileFilterMode.Client.script);
        NodeRef linksFolderRef = getLinksFolderRef(document);
        if (null == linksFolderRef) {
            return results;
        }
        try {
            pageOfNodeInfos = lecmObjectsService.list(linksFolderRef, TYPE_BASE_LINK, new ArrayList<FilterProp>(), sortProps, pageRequest);
        } finally {
            FileFilterMode.clearClient();
        }

        List<NodeRef> nodeInfos = pageOfNodeInfos.getPage();
        for (NodeRef ref : nodeInfos) {
            results.add(ref);
        }
        return results;
    }

    @Override
    public List<NodeRef> getLinksByAssociation(NodeRef document, String association) {
        QName assoc = QName.createQName(association, namespaceService);

        List<NodeRef> result = findNodesByAssociationRef(document, assoc, BaseBean.TYPE_BASE_LINK, ASSOCIATION_TYPE.TARGET);

        Collections.sort(result, new Comparator<NodeRef>() {
            @Override
            public int compare(NodeRef o1, NodeRef o2) {
                String name1 = nodeService.getProperty(o1, ContentModel.PROP_NAME).toString();
                String name2 = nodeService.getProperty(o2, ContentModel.PROP_NAME).toString();
                return name1.compareTo(name2);
            }
        });

        return result;

    }

    public NodeRef createLinks(NodeRef document, String name, String url, boolean isExecute) {
        NodeRef linkFolder = getLinksFolderRef(document);

        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

        properties.put(ContentModel.PROP_NAME, name);
        properties.put(BaseBean.PROP_BASE_LINK_URL, url);

        ChildAssociationRef childAssoc = nodeService.createNode(linkFolder, ContentModel.ASSOC_CONTAINS, assocQName, BaseBean.TYPE_BASE_LINK, properties);
        if (isExecute) {
            nodeService.createAssociation(document, childAssoc.getChildRef(), ASSOC_ERRANDS_EXECUTION_LINKS);
        } else {
            nodeService.createAssociation(document, childAssoc.getChildRef(), ASSOC_ERRANDS_LINKS);
        }
        if (childAssoc != null) {
            businessJournalService.log(document, DocumentEventCategory.LINK_ADDED, EDSDocumentService.getFromMessagesOrDefaultValue("ru.it.lecm.errands.bjMessages.addLink", "#initiator ??????????????(??) ???????????? #object1 ?? ?????????????????? \"#mainobject\""), Arrays.asList(childAssoc.getChildRef().toString()));
        }

        return childAssoc.getChildRef();
    }

    public NodeRef getAdditionalDocumentNode(NodeRef errand) {
        return findNodeByAssociationRef(errand, ASSOC_ADDITIONAL_ERRANDS_DOCUMENT, null, BaseBean.ASSOCIATION_TYPE.TARGET);
    }

    public void setExecutionReport(NodeRef errandRef, String report) {
        nodeService.setProperty(errandRef, PROP_ERRANDS_EXECUTION_REPORT, report);
    }

	public NodeRef getExecutor(NodeRef errand) {
		return findNodeByAssociationRef(errand, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET);
	}

	public NodeRef getBaseDocument(NodeRef errand) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(errand, ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
		if (assocs != null && assocs.size() > 0) {
			return assocs.get(0).getTargetRef();
		}
		return null;
	}

	public NodeRef getErrandBaseDocument(NodeRef errand) {
        return findNodeByAssociationRef(errand, ASSOC_BASE_DOCUMENT, null, BaseBean.ASSOCIATION_TYPE.TARGET);
	}

	public NodeRef getInitiator(NodeRef errand) {
        return findNodeByAssociationRef(errand, ASSOC_ERRANDS_INITIATOR, null, BaseBean.ASSOCIATION_TYPE.TARGET);
	}

    @Override
    public boolean hasChildNotFinalByExecutor(NodeRef errand) {
        if (nodeService.exists(errand)) {
            String errandExecutorRefString = (String) nodeService.getProperty(errand, ErrandsService.PROP_ERRANDS_EXECUTOR_REF);
            List<NodeRef> childErrands = documentConnectionService.getConnectedDocuments(errand, DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, ErrandsService.TYPE_ERRANDS);
            for (NodeRef child : childErrands) {
                String childInitiatorRefString = (String) nodeService.getProperty(child, ErrandsService.PROP_ERRANDS_INITIATOR_REF);
                if (childInitiatorRefString != null && errandExecutorRefString != null && childInitiatorRefString.equals(errandExecutorRefString)) {
                    if (!stateMachineService.isFinal(child)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public List<NodeRef> getChildResolutions(NodeRef errand){
        List<NodeRef> childResolutions = new ArrayList<>();
        List<AssociationRef> childResolutionsAssocs = nodeService.getSourceAssocs(errand, ResolutionsService.ASSOC_BASE);
        for (AssociationRef assoc : childResolutionsAssocs) {
            childResolutions.add(assoc.getSourceRef());
        }
        return childResolutions;
    }

    @Override
    public List<NodeRef> getChildErrands(NodeRef errand) {
        List<NodeRef> childErrands = new ArrayList<>();
        List<AssociationRef> childErrandsAssocs = nodeService.getSourceAssocs(errand, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
        for (AssociationRef assoc : childErrandsAssocs) {
            childErrands.add(assoc.getSourceRef());
        }
        return childErrands;
    }

    @Override
    public void sendCancelSignal(NodeRef errand, String reason, NodeRef signalSender) {
        if (!nodeService.exists(signalSender)) {
            signalSender = orgstructureService.getCurrentEmployee();
        }
        nodeService.createAssociation(errand, signalSender, ErrandsService.ASSOC_ERRANDS_CANCELLATION_SIGNAL_SENDER);
        nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_CANCELLATION_SIGNAL, true);
        nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_CANCELLATION_SIGNAL_REASON, reason);
    }

    @Override
    public void resetCancelSignal(NodeRef errand) {
        try {
            nodeService.setProperty(errand, ErrandsService.PROP_ERRANDS_CANCELLATION_SIGNAL, false);
        } catch (ConcurrencyFailureException ex) {
            logger.warn("Reset signal at the same time", ex);
        }
    }


    @Override
    public CreateDateNotWorkingDayAction getCreateDateNotWorkingDayAction() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            String createDateNotWorkingDayAction = (String) nodeService.getProperty(settings, SETTINGS_CREATE_DATE_NOT_WORKING_DAY_ACTION);
            return EnumUtils.getEnum(CreateDateNotWorkingDayAction.class, createDateNotWorkingDayAction);
        }
        return CreateDateNotWorkingDayAction.MOVE_TO_NEXT_WORKING_DAY;
    }

    @Override
    public ControlDeadlineNotWorkingDayAction getControlDeadlineNotWorkingDayAction() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            String controlDeadlineNotWorkingDayAction = (String) nodeService.getProperty(settings, SETTINGS_CONTROL_DEADLINE_NOT_WORKING_DAY_ACTION);
            return EnumUtils.getEnum(ControlDeadlineNotWorkingDayAction.class, controlDeadlineNotWorkingDayAction);
        }
        return ControlDeadlineNotWorkingDayAction.MOVE_TO_NEXT_WORKING_DAY;
    }

    @Override
    public EmployeeNotActiveAction getExecutorNotActiveAction() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            String employeeNotActiveAction = (String) nodeService.getProperty(settings, SETTINGS_EXECUTOR_NOT_ACTIVE_ACTION);
            return EnumUtils.getEnum(EmployeeNotActiveAction.class, employeeNotActiveAction);
        }
        return EmployeeNotActiveAction.NOTIFY_ADMIN_AND_AUTHOR;
    }

    @Override
    public EmployeeNotActiveAction getCoexecutorNotActiveAction() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            String employeeNotActiveAction = (String) nodeService.getProperty(settings, SETTINGS_COEXECUTOR_NOT_ACTIVE_ACTION);
            return EnumUtils.getEnum(EmployeeNotActiveAction.class, employeeNotActiveAction);
        }
        return EmployeeNotActiveAction.NOTIFY_ADMIN_AND_AUTHOR;
    }

    @Override
    public EmployeeNotActiveAction getControllerNotActiveAction() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            String employeeNotActiveAction = (String) nodeService.getProperty(settings, SETTINGS_CONTROLLER_NOT_ACTIVE_ACTION);
            return EnumUtils.getEnum(EmployeeNotActiveAction.class, employeeNotActiveAction);
        }
        return EmployeeNotActiveAction.NOTIFY_ADMIN_AND_AUTHOR;
    }

    @Override
    public Map<String, Set<NodeRef>> getDelayedErrandsByDate() {
        NodeRef settings = getSettingsNode();
        Map<String, Set<NodeRef>> results = new HashMap<String, Set<NodeRef>>();
        if (settings != null) {
            String delayedErrandsByDateJson = (String) nodeService.getProperty(settings, SETTINGS_DELAYED_ERRAND_CREATION_BY_DATE);
            if (delayedErrandsByDateJson != null) {
                try {
                    final JSONObject json = new JSONObject(delayedErrandsByDateJson);
                    final JSONArray dates = json.names();
                    Set<NodeRef> someDateResult;
                    if (dates != null) {
                        for (int i = 0; i < dates.length(); i++) {
                            final String dateStr = dates.optString(i, null);
                            if (dateStr != null) {
                                final JSONArray errandRefsArray = json.getJSONArray(dateStr);
                                someDateResult = new HashSet<>(errandRefsArray.length());
                                for (int j = 0; j < errandRefsArray.length(); j++) {
                                    someDateResult.add(new NodeRef(errandRefsArray.getString(j)));
                                }
                                results.put(dateStr, someDateResult);
                            }
                        }
                    }
                } catch (JSONException e) {
                    logger.error("Failed to get 'delayedErrandsByDate' map from json string.", e);
                }
            }
        }
        return results;
    }

    @Override
    public void setDelayedErrandsByDate(Map<String, Set<NodeRef>> delayedErrandsByDate) {
        NodeRef settings = getSettingsNode();
        if (settings != null && delayedErrandsByDate != null) {
            try {
                final JSONObject delayedErrandsByDateJson = new JSONObject();
                for (Map.Entry<String, Set<NodeRef>> errandsByDate : delayedErrandsByDate.entrySet()) {
                    final Set<NodeRef> errandsRefs = errandsByDate.getValue();
                    final JSONArray errandsJsonArray = new JSONArray();
                    if (errandsRefs != null && !errandsRefs.isEmpty()) {
                        for (NodeRef errandRef : errandsRefs) {
                            errandsJsonArray.put(errandRef.toString());
                        }
                    }
                    delayedErrandsByDateJson.put(errandsByDate.getKey(), errandsJsonArray);
                }
                String delayedErrandsByDateJsonStr = delayedErrandsByDateJson.toString();
                nodeService.setProperty(settings, SETTINGS_DELAYED_ERRAND_CREATION_BY_DATE, delayedErrandsByDateJsonStr);
            } catch (JSONException e) {
                logger.error("Failed to save json string for 'delayedErrandsByDate' map.", e);
            }
        }
    }

    public Date calculatePeriodicalErrandControlDate(NodeRef errandRef){
        String dateRadio = (String) nodeService.getProperty(errandRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
        Date periodicalLimitDate = (Date) nodeService.getProperty(errandRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
        Date newLimitDate = null;
        if ("DATE".equals(dateRadio)) {
            if (periodicalLimitDate != null) {
                periodicalLimitDate = DateUtils.truncate(periodicalLimitDate, Calendar.DAY_OF_MONTH);
                Date now = new Date();
                now = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
                Date createdDate = (Date) nodeService.getProperty(errandRef, ContentModel.PROP_CREATED);
                createdDate = DateUtils.truncate(createdDate, Calendar.DAY_OF_MONTH);
                long dateDif = TimeUnit.DAYS.convert(periodicalLimitDate.getTime() - createdDate.getTime(), TimeUnit.MILLISECONDS);
                newLimitDate = DateUtils.addDays(now, (int) dateDif);
            }
        } else if ("DAYS".equals(dateRadio)) {
            newLimitDate =  edsDocumentService.convertComplexDate(
                    (String) nodeService.getProperty(errandRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO),
                    (Date) nodeService.getProperty(errandRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE),
                    (String) nodeService.getProperty(errandRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE),
                    (Integer) nodeService.getProperty(errandRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS));
        }
        return newLimitDate;
    }

    @Override
    public Boolean hasDelayedPeriodicalErrands(NodeRef periodicalErrand) {
        Map<String, Set<NodeRef>> allDelayedErrandsByDate = getDelayedErrandsByDate();
        Collection<Set<NodeRef>> allDelayedErrands = allDelayedErrandsByDate.values();
        return allDelayedErrands.stream().anyMatch(set -> set.contains(periodicalErrand));
    }

    @Override
    public Boolean doesRuleAllowCreation(NodeRef periodicalErrand) {
        return doesRuleAllowCreation(periodicalErrand, new Date());
    }

    @Override
    public Boolean doesRuleAllowCreation(NodeRef periodicalErrand, Date date) {
        boolean createErrand = false;
        String ruleContent = (String) nodeService.getProperty(periodicalErrand, ErrandsService.PROP_ERRANDS_PERIODICAL_RULE);
        if (ruleContent != null) {
            try {
                JSONObject rule = new JSONObject(ruleContent);
                String type = rule.getString("type");
                JSONArray data = rule.has("data") ? rule.getJSONArray("data") : new JSONArray();
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(date);
                switch (type) {
                    case DAILY:
                        createErrand = true;
                        break;
                    case WEEK_DAYS:
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek == Calendar.SUNDAY) {
                            dayOfWeek = 7;
                        } else {
                            dayOfWeek -= 1;
                        }
                        for (int i = 0; i < data.length(); i++) {
                            int value = data.getInt(i);
                            if (value == dayOfWeek) {
                                createErrand = true;
                                break;
                            }
                        }
                        break;
                    case MONTH_DAYS:
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        for (int i = 0; i < data.length(); i++) {
                            int value = data.getInt(i);
                            if (value == dayOfMonth) {
                                createErrand = true;
                                break;
                            }
                        }
                        break;
                    case QUARTERLY:
                        int currentMonthValue = calendar.get(Calendar.MONTH) + 1;
                        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        Date startDate = (Date) nodeService.getProperty(periodicalErrand, ErrandsService.PROP_ERRANDS_PERIOD_START);
                        calendar.setTime(startDate);
                        int startMonthValue = calendar.get(Calendar.MONTH) + 1;
                        if (currentMonthValue % 3 == startMonthValue % 3) {
                            for (int i = 0; i < data.length(); i++) {
                                int value = data.getInt(i);
                                if (value == currentDayOfMonth) {
                                    createErrand = true;
                                    break;
                                }
                            }
                        }
                        break;
                    default:
                        logger.warn("Error parse periodical rule. Property 'type' has not supported value");
                        break;

                }
            } catch (JSONException e) {
                logger.warn("Error parse periodical rule", e);
            }
        }
        return createErrand;
    }
}
