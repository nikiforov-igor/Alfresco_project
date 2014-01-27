package ru.it.lecm.errands.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.Pair;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmObjectsService;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:43
 */
public class ErrandsServiceImpl extends BaseBean implements ErrandsService {


    private static enum ModeChoosingExecutors {
        ORGANIZATION,
        UNIT
    }

    private static enum FilterEnum {
        ALL,
        ACTIVE,
        COMPLETE
    }

    public static final int MAX_ITEMS = 1000;

    private DocumentService documentService;
    private OrgstructureBean orgstructureService;
    private StateMachineServiceBean stateMachineBean;
    private LecmObjectsService lecmObjectsService;
    private NamespaceService namespaceService;
    private BusinessJournalService businessJournalService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setStateMachineBean(StateMachineServiceBean stateMachineBean) {
        this.stateMachineBean = stateMachineBean;
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

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(ERRANDS_ROOT_ID);
    }

    public NodeRef getDraftRoot() {
        return documentService.getDraftRootByType(TYPE_ERRANDS);
    }

    public NodeRef getSettingsNode() {
        final NodeRef rootFolder = this.getServiceRootFolder();

        NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, ERRANDS_SETTINGS_NODE_NAME);
        if (settings != null) {
            return settings;
        } else {
            AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
                @Override
                public NodeRef doWork() throws Exception {
                    return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                        @Override
                        public NodeRef execute() throws Throwable {
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
                    });
                }
            };
            return AuthenticationUtil.runAsSystem(raw);
        }
    }

    public ModeChoosingExecutors getModeChoosingExecutors() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            String modeChoosingExecutors = (String) nodeService.getProperty(settings, SETTINGS_PROP_MODE_CHOOSING_EXECUTORS);
            if (modeChoosingExecutors.equals(SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION)) {
                return ModeChoosingExecutors.ORGANIZATION;
            }
        }
        return ModeChoosingExecutors.UNIT;
    }

	public boolean isTransferRightToBaseDocument() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            return (Boolean) nodeService.getProperty(settings, SETTINGS_PROP_TRANSFER_RIGHT);
        }
        return false;
    }

	public NodeRef getCurrentUserSettingsNode(boolean createNewIfNotExist) {
        final NodeRef rootFolder = this.getServiceRootFolder();
        final String settingsObjectName = authService.getCurrentUserName() + "_" + ERRANDS_SETTINGS_NODE_NAME;

        NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
		if (settings != null || !createNewIfNotExist) {
            return settings;
        } else {
            AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
                @Override
                public NodeRef doWork() throws Exception {
                    return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                        @Override
                        public NodeRef execute() throws Throwable {
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
                    });
                }
            };
            return AuthenticationUtil.runAsSystem(raw);
        }
    }

    public boolean isDefaultWithoutInitiatorApproval() {
		NodeRef settings = getCurrentUserSettingsNode(false);
        if (settings != null) {
            return (Boolean) nodeService.getProperty(settings, USER_SETTINGS_PROP_WITHOUT_INITIATOR_APPROVAL);
        }
        return false;
    }

    public NodeRef getDefaultInitiator() {
        NodeRef result = orgstructureService.getCurrentEmployee();
        if (orgstructureService.isCurrentEmployeeHasBusinessRole(BUSINESS_ROLE_ERRANDS_CHOOSING_INITIATOR)) {
			NodeRef settings = getCurrentUserSettingsNode(false);
            if (settings != null) {
                List<AssociationRef> defaultInitiatorAssocs = nodeService.getTargetAssocs(settings, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR);
                if (defaultInitiatorAssocs.size() > 0) {
                    result = defaultInitiatorAssocs.get(0).getTargetRef();
                }
            }
        }
        return result;
    }

    public NodeRef getDefaultSubject() {
		NodeRef settings = getCurrentUserSettingsNode(false);
        if (settings != null) {
            List<AssociationRef> defaultSubjectAssocs = nodeService.getTargetAssocs(settings, USER_SETTINGS_ASSOC_DEFAULT_SUBJECT);
            if (defaultSubjectAssocs.size() > 0) {
                return defaultSubjectAssocs.get(0).getTargetRef();
            }
        }
        return null;
    }

    public List<NodeRef> getAvailableExecutors() {
        if (getModeChoosingExecutors() == ModeChoosingExecutors.ORGANIZATION) {
            return null;
        } else {
            NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
            List<NodeRef> subordinates = orgstructureService.getBossSubordinate(currentEmployee, true);

            List<NodeRef> result = new ArrayList<NodeRef>();
            result.add(currentEmployee);
            result.addAll(subordinates);

            return result;
        }
    }

    private List<NodeRef> getDocumentErrands(NodeRef document, Boolean active, List<QName> roles) {
        if (document == null) {
            return new ArrayList<NodeRef>();
        }


        List<NodeRef> result = new ArrayList<NodeRef>();
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

        List<AssociationRef> documentErrandsAssocs = nodeService.getSourceAssocs(document, ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
        for (AssociationRef documentErrandsAssoc : documentErrandsAssocs) {
            NodeRef errand = documentErrandsAssoc.getSourceRef();

            if (active != null) {
                if (active) {
                    if (stateMachineBean.hasActiveStatemachine(errand)) {
                        if (stateMachineBean.isDraft(errand)) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    if (!stateMachineBean.isFinal(errand)) {
                        continue;
                    }
                }
            }

            for (QName role : roles) {
                if (currentEmployee.equals(findNodeByAssociationRef(errand, role, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))) {
                    result.add(errand);
                    break;
                }
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

    public List<NodeRef> getErrandsDocuments(List<String> paths, int skipCount, int maxItems) {
        List<QName> types = new ArrayList<QName>();
        types.add(TYPE_ERRANDS);

        List<SortDefinition> sort = new ArrayList<SortDefinition>();
        List<NodeRef> sortingErrands = new ArrayList<NodeRef>();
        List<NodeRef> result = new ArrayList<NodeRef>();
        List<String> status = stateMachineBean.getStatuses("lecm-errands:document", true, false);

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        // сортируем по важности поручения и по сроку исполнения
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_IS_IMPORTANT.toString(), false));
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_LIMITATION_DATE.toString(), false));

        for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, paths, status, null, sort)) {
            if (stateMachineBean.isDraft(nodeRef)) {
                continue;
            }
            if (stateMachineBean.isFinal(nodeRef)) {
                continue;
            }
            if (currentEmployee.equals(findNodeByAssociationRef(nodeRef, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))) {
                sortingErrands.add(nodeRef);
            }
        }

        int endIndex = (skipCount + maxItems) < sortingErrands.size() ? (skipCount + maxItems) : sortingErrands.size();

        for (int i = skipCount; i < endIndex; i++) {
            result.add(sortingErrands.get(i));
        }
        return result;
    }

    public List<NodeRef> getActiveErrands(List<String> paths, int skipCount, int maxItems) {
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        List<QName> types = new ArrayList<QName>();
        types.add(TYPE_ERRANDS);

        List<NodeRef> employees = orgstructureService.getBossSubordinate(currentEmployee, true);

        List<SortDefinition> sort = new ArrayList<SortDefinition>();
        List<NodeRef> sortingErrands = new ArrayList<NodeRef>();
        List<NodeRef> result = new ArrayList<NodeRef>();
        List<String> status = stateMachineBean.getStatuses("lecm-errands:document", true, false);

        // сортируем по наименованию поручения и по сроку исполнения
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_NUMBER.toString(), false));
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD, "@" + PROP_ERRANDS_LIMITATION_DATE.toString(), false));

        for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, paths, status, null, sort)) {
            if (stateMachineBean.isDraft(nodeRef)) {
                continue;
            }
            if (stateMachineBean.isFinal(nodeRef)) {
                continue;
            }
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
        return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        NodeRef linkFolder = nodeService.getChildByName(document, ContentModel.ASSOC_CONTAINS, ERRANDS_LINK_FOLDER_NAME);
                        if (linkFolder == null) {
                            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ERRANDS_LINK_FOLDER_NAME);
                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
                            properties.put(ContentModel.PROP_NAME, ERRANDS_LINK_FOLDER_NAME);
                            ChildAssociationRef childAssoc = nodeService.createNode(document, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
                            return childAssoc.getChildRef();
                        } else {
                            return linkFolder;
                        }
                    }
                });
            }
        });
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
        try {
            pageOfNodeInfos = lecmObjectsService.list(getLinksFolderRef(document), TYPE_BASE_LINK, new ArrayList<FilterProp>(), sortProps, pageRequest);
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
            businessJournalService.log(document, DocumentEventCategory.LINK_ADDED, "#initiator добавил(а) ссылку #object1 к документу \"#mainobject\"", Arrays.asList(childAssoc.getChildRef().toString()));
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
}
