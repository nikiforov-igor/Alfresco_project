package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.GUID;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SearchQueryProcessorService;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.documents.constraints.AuthorPropertyConstraint;
import ru.it.lecm.documents.constraints.DocumentUrlConstraint;
import ru.it.lecm.documents.expression.Expression;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: AIvkin Date: 28.02.13 Time: 16:28
 */
public class DocumentServiceImpl extends BaseBean implements DocumentService, ApplicationContextAware {

    private static final transient Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private OrgstructureBean orgstructureService;
    private BusinessJournalService businessJournalService;
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private LecmPermissionService lecmPermissionService;
    private DocumentMembersService documentMembersService;
    private SearchService searchService;
    private DocumentAttachmentsService documentAttachmentsService;
    private CopyService copyService;
    private ApplicationContext applicationContext;
    private SearchQueryProcessorService processorService;
    private PreferenceService preferenceService;
    private DocumentTableService documentTableService;
    private DocumentFrequencyAnalysisService frequencyAnalysisService;

    public DocumentTableService getDocumentTableService() {
        return documentTableService;
    }

    public void setFrequencyAnalysisService(DocumentFrequencyAnalysisService frequencyAnalysisService) {
        this.frequencyAnalysisService = frequencyAnalysisService;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    public void setProcessorService(SearchQueryProcessorService processorService) {
        this.processorService = processorService;
    }

    @Override
    public String getRating(NodeRef documentNodeRef) {
        return (String) nodeService.getProperty(documentNodeRef, DocumentService.PROP_RATING);
    }

    @Override
    public Integer getRatedPersonCount(NodeRef documentNodeRef) {
        return (Integer) nodeService.getProperty(documentNodeRef, DocumentService.PROP_RATED_PERSONS_COUNT);
    }

    @Override
    public Integer getMyRating(NodeRef documentNodeRef) {
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

        if (currentEmployee != null) {
            for (int i = 1; i < 6; i++) {
                List<NodeRef> rated = (List<NodeRef>) nodeService.getProperty(documentNodeRef, QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + i));

                if (rated != null && rated.contains(currentEmployee)) {
                    return i;
                }
            }
        }

        return null;
    }

    @Override
    public Integer setMyRating(NodeRef documentNodeRef, Integer rating) {
        lecmPermissionService.checkPermission(LecmPermissionService.PERM_SET_RATE, documentNodeRef);
        if (rating > 0 && rating < 6) {
            NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

            if (currentEmployee != null) {
                QName thisRatedList;
                List<NodeRef> ratedList;
                Integer myRating = getMyRating(documentNodeRef);

                if (myRating != null) {
                    thisRatedList = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + myRating);
                    ratedList = (List<NodeRef>) nodeService.getProperty(documentNodeRef, thisRatedList);

                    if (ratedList != null && !ratedList.isEmpty()) {
                        ratedList.remove(currentEmployee);
                    }
                    nodeService.setProperty(documentNodeRef, thisRatedList, (Serializable) ratedList);
                }
                thisRatedList = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + rating);
                ratedList = (List<NodeRef>) nodeService.getProperty(documentNodeRef, thisRatedList);
                if (ratedList == null) {
                    ratedList = new ArrayList<NodeRef>();
                }
                ratedList.add(currentEmployee);
                nodeService.setProperty(documentNodeRef, thisRatedList, (Serializable) ratedList);

                //обновить хранимые значения (среднего рейтинга и количества проголосовавших)
                refreshValues(documentNodeRef);

                //логировать изменения в журнал
                List<String> ratingList = new ArrayList<String>();
                ratingList.add(rating.toString());
                businessJournalService.log(documentNodeRef, DocumentEventCategory.SET_RATING, "#initiator присвоил(а) рейтинг #object1 документу \"#mainobject\"", ratingList);

                return getMyRating(documentNodeRef);
            }
        }
        return null;
    }

    @Override
    public Map<QName, Serializable> getProperties(NodeRef documentRef) {
        lecmPermissionService.checkPermission(LecmPermissionService.PERM_ATTR_LIST, documentRef);
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

        for (Map.Entry<QName, Serializable> e : nodeService.getProperties(documentRef).entrySet()) {
            if (!(namespaceService.getPrefixes(e.getKey().getNamespaceURI()).contains("cm")
                    || namespaceService.getPrefixes(e.getKey().getNamespaceURI()).contains("sys"))) {
                properties.put(e.getKey(), e.getValue());
            }
        }
        return properties;
    }

    /**
     * Создание документа
     *
     * @param type тип документа lecm-contract:document
     * @param property свойства документа
     * @return
     */
    @Override
    public NodeRef createDocument(String type, Map<String, String> property, final Map<String, String> association) {
        // получение папки черновиков для документа
        NodeRef draftRef;
        if (getDraftRootLabel(type) != null) {
            QName typeQName = QName.createQName(type, namespaceService);
            draftRef = getDraftRootByType(typeQName);
            if (draftRef == null) {
                try {
                    draftRef = createDraftRoot(typeQName);
                } catch (WriteTransactionNeededException ex) {
                    logger.debug(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }
            }
        } else {
            draftRef = getDraftRoot();
        }

        final QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
        final QName assocQName = ContentModel.ASSOC_CONTAINS;
        final QName nodeTypeQName = QName.createQName(type, namespaceService);

        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        for (Map.Entry<String, String> e : property.entrySet()) {
            properties.put(QName.createQName(e.getKey(), namespaceService), e.getValue());
        }

        ChildAssociationRef associationRef = nodeService.createNode(draftRef, assocTypeQName, assocQName, nodeTypeQName, properties);

        for (Map.Entry<String, String> assoc : association.entrySet()) {
            nodeService.createAssociation(associationRef.getChildRef(), new NodeRef(assoc.getValue()), QName.createQName(assoc.getKey(), namespaceService));
        }

        return associationRef.getChildRef();
    }

    /**
     * Изменение свойств документа
     *
     * @param nodeRef
     * @param property
     * @return ссылка на на документ
     */
    @Override
    public NodeRef editDocument(NodeRef nodeRef, Map<String, String> property) {
        lecmPermissionService.checkPermission(LecmPermissionService.PERM_ATTR_EDIT, nodeRef);

        Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
        for (Map.Entry<String, String> e : property.entrySet()) {
            properties.put(QName.createQName(e.getKey(), namespaceService), e.getValue());
        }

        nodeService.setProperties(nodeRef, properties);
        return nodeRef;
    }

    @Override
    public boolean isDocument(NodeRef ref) {
        QName refType = nodeService.getType(ref);
        return refType != null && dictionaryService.isSubClass(refType, DocumentService.TYPE_BASE_DOCUMENT);
    }

    public String getDraftPath() {
        NodeRef draftRef = getDraftRoot();
        return nodeService.getPath(draftRef).toPrefixString(namespaceService);
    }

    @Override
    public String getDraftPathByType(QName docType) {
        NodeRef draftRef = getDraftRootByType(docType);
        return nodeService.getPath(draftRef).toPrefixString(namespaceService);
    }

    @Override
    public NodeRef getDraftRoot() {
        String fullyAuthenticatedUser = AuthenticationUtil.getFullyAuthenticatedUser();
        try {
            return repositoryStructureHelper.getDraftsRef(fullyAuthenticatedUser);
        } catch (WriteTransactionNeededException e) {
            logger.debug("Can't get folder.", e);
            return null;
        }
    }

    @Override
    public NodeRef getDraftRootByType(QName docType) {
        final NodeRef draftRef = getDraftRoot();
        String rootName = !DocumentService.TYPE_BASE_DOCUMENT.equals(docType) ? getDraftRootLabel(docType) : (String) nodeService.getProperty(draftRef, ContentModel.PROP_NAME);
	    rootName = FileNameValidator.getValidFileName(rootName);
        NodeRef nodeRef = nodeService.getChildByName(draftRef, ContentModel.ASSOC_CONTAINS, rootName);

//		TODO: DONE Разделение метода
//        if (nodeRef == null) {
//	        RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
//	        return transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//		        @Override
//		        public NodeRef execute () throws Throwable {
//			        QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
//			        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
//			        QName nodeTypeQName = ContentModel.TYPE_FOLDER;
//
//			        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
//			        properties.put(ContentModel.PROP_NAME, rootName);
//			        ChildAssociationRef childAssoc = nodeService.createNode(draftRef, assocTypeQName, assocQName, nodeTypeQName, properties);
//			        return childAssoc.getChildRef ();
//		        }
//	        }, false, true);
//        }
        return nodeRef;
    }

    @Override
    //TODO DONE Refactoring in progress...
    public NodeRef createDraftRoot(QName docType) throws WriteTransactionNeededException {
        final NodeRef draftRef = getDraftRoot();
        String rootName = !DocumentService.TYPE_BASE_DOCUMENT.equals(docType) ? getDraftRootLabel(docType) : (String) nodeService.getProperty(draftRef, ContentModel.PROP_NAME);
	    final String rootNameFinal = FileNameValidator.getValidFileName(rootName);

        try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create Draft Root for DocType " + rootName);
        }

        QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootNameFinal);
        QName nodeTypeQName = ContentModel.TYPE_FOLDER;

        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, rootNameFinal);
        ChildAssociationRef childAssoc = nodeService.createNode(draftRef, assocTypeQName, assocQName, nodeTypeQName, properties);
        return childAssoc.getChildRef();
    }

    @Override
    public String getDraftRootLabel(QName docType) {
        return getDraftRootLabel(docType.toPrefixString(namespaceService));
    }

    @Override
    public String getDocumentsFolderPath() {
        NodeRef nodeRef = repositoryStructureHelper.getDocumentsRef();
        return nodeService.getPath(nodeRef).toPrefixString(namespaceService);
    }

    // в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public List<NodeRef> getMembers(QName docType) {
        NodeRef membersUnit = documentMembersService.getMembersUnit(docType);
        List<NodeRef> allMembers = findNodesByAssociationRef(membersUnit, DocumentMembersService.ASSOC_UNIT_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
        List<NodeRef> result = new ArrayList<NodeRef>();
        for (NodeRef allMember : allMembers) {
            if (!isArchive(allMember)) {
                result.add(allMember);
            }
        }
        return result;
    }

    @Override
    public Long getAmountDocumentsByFilter(List<QName> docTypes, List<String> paths, List<String> statuses, String filterQuery, List<SortDefinition> sortDefinition) {
        SearchParameters sp = buildDocumentsSearcParametersByFilter(docTypes, paths, statuses, filterQuery, sortDefinition);

        ResultSet results = null;
        try {
            sp.setMaxItems(0);
            results = searchService.query(sp);
            if (results instanceof SolrJSONResultSet) {
                return ((SolrJSONResultSet) results).getNumberFound();
            } else {
                //если вдруг в бин был подложен другой SearchComponent - выполнил запрос без ограничений
                sp.setMaxItems(-1);
                return (long) searchService.query(sp).length();
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
    }

    @Override
    public List<NodeRef> getDocumentsByFilter(List<QName> docTypes, List<String> paths, List<String> statuses, String filterQuery, List<SortDefinition> sortDefinition) {
        SearchParameters sp = buildDocumentsSearcParametersByFilter(docTypes, paths, statuses, filterQuery, sortDefinition);
        ResultSet results = null;
        List<NodeRef> records = new ArrayList<NodeRef>();
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return records;
    }

    private SearchParameters buildDocumentsSearcParametersByFilter(List<QName> docTypes, List<String> paths, List<String> statuses, String filterQuery, List<SortDefinition> sortDefinition) {
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

        String query = "";
        if (docTypes != null && !docTypes.isEmpty()) {
            boolean addOR = false;
            StringBuilder typesFilter = new StringBuilder();
            for (QName type : docTypes) {
                typesFilter.append(addOR ? " OR " : "").append(" TYPE:\"").append(type).append("\"");
                addOR = true;
            }
            query += "(" + typesFilter.toString() + ")";
        }

        // пути
        if (paths != null && !paths.isEmpty()) {
            boolean addOR = false;
            StringBuilder pathsFilter = new StringBuilder();
            for (String path : paths) {
                pathsFilter.append(addOR ? " OR " : "").append("PATH:\"").append(path).append("//*\"");
                addOR = true;
            }
            query += (query.length() > 0 ? " AND (" : "(") + pathsFilter.toString() + ")";
        }

        // фильтр по статусам
        if (statuses != null && !statuses.isEmpty()) {
            StringBuilder statusesFilter = new StringBuilder();
            StringBuilder statusesNotFilter = new StringBuilder();
            for (String status : statuses) {
                if (!status.trim().startsWith("!")) {
                    statusesFilter.append(" @lecm\\-statemachine\\:status:\"").append(status.replace("!", "").trim()).append("\"");
                } else {
                    statusesNotFilter.append(" @lecm\\-statemachine\\:status:\"").append(status.replace("!", "").trim()).append("\"");
                }
            }
            query += (statusesFilter.length() > 0 ? (query.length() > 0 ? " AND (" : "(") + ("" + statusesFilter.toString() + ")") : "")
                    + (statusesNotFilter.length() > 0 ? (" AND NOT (" + statusesNotFilter.toString() + ")") : "");
        }

        query += (query.length() > 0 ? " AND " : "") + processorService.processQuery("{{IN_SAME_ORGANIZATION}}");

        if (filterQuery != null && filterQuery.length() > 0) {
            query += (query.length() > 0 ? " AND (" : "(") + filterQuery + ") ";
        }



        if (sortDefinition != null && !sortDefinition.isEmpty()) {
            for (SortDefinition sort : sortDefinition) {
                sp.addSort(sort);
            }
        }

        sp.setQuery(processorService.processQuery(query));
        return sp;
    }

    public String getAuthorProperty(QName docType) {
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(docType.getNamespaceURI(), DocumentService.CONSTRAINT_AUTHOR_PROPERTY));
        if (constraint != null && constraint.getConstraint() != null && (constraint.getConstraint() instanceof AuthorPropertyConstraint)) {
            AuthorPropertyConstraint auConstraint = (AuthorPropertyConstraint) constraint.getConstraint();
            if (auConstraint.getAuthorProperty() != null) {
                return auConstraint.getAuthorProperty();
            }
        }
        return DocumentService.PROP_DOCUMENT_CREATOR_REF.toPrefixString(namespaceService);
    }

    @Override
    public NodeRef duplicateDocument(NodeRef document) {
        if (isDocument(document)) {
            QName docType = nodeService.getType(document);
            DocumentCopySettings settings = DocumentCopySettingsBean.getSettingsForDocType(docType.toPrefixString(namespaceService));

            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
            if (settings != null) {
                // копируем свойства
                List<String> propertiesToCopy = settings.getPropsToCopy();
                if (null != propertiesToCopy) {
                    Map<QName, Serializable> originalProperties = nodeService.getProperties(document);
                    for (String propName : propertiesToCopy) {
                        try {
                            QName propQName = QName.createQName(propName, namespaceService);
                            if (propQName != null) {
                                properties.put(propQName, originalProperties.get(propQName));
                            }
                        } catch (InvalidQNameException invalid) {
                            logger.warn("Invalid QName for document property:" + propName);
                        }
                    }
                }
            }
            // создаем ноду
            NodeRef draftRoot = getDraftRootByType(docType);
//			TODO: DONE Ввиду разделения метода проверка
            if (draftRoot == null) {
                try {
                    draftRoot = createDraftRoot(docType);
                } catch (WriteTransactionNeededException ex) {
                    logger.debug(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }
            }
            ChildAssociationRef createdNodeAssoc = nodeService.createNode(draftRoot,
                    ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                            GUID.generate()), docType, properties);

            if (createdNodeAssoc != null && createdNodeAssoc.getChildRef() != null) {
                NodeRef createdNode = createdNodeAssoc.getChildRef();
                if (settings != null) {
                    // копируем ассоциации
                    List<String> assocsToCopy = settings.getAssocsToCopy();
                    if (null != assocsToCopy) {
                        for (String assocName : assocsToCopy) {
                            try {
                                QName assocQName = QName.createQName(assocName, namespaceService);
                                if (assocQName != null) {
                                    List<NodeRef> targets = findNodesByAssociationRef(document, assocQName, null, ASSOCIATION_TYPE.TARGET);
                                    nodeService.setAssociations(createdNode, assocQName, targets);

                                }
                            } catch (InvalidQNameException invalid) {
                                logger.warn("Invalid QName for document assoc:" + assocName);
                            }
                        }
                    }
                }

                if (settings != null) {
                    // копируем категории
                    List<String> categories = settings.getCategoriesToCopy();
                    if (categories != null) {
                        for (String category : categories) {
                            NodeRef categoryRef = documentAttachmentsService.getCategory(category, document);
                            if (categoryRef != null) {
 	                            documentAttachmentsService.getCategories(createdNode);
	                            NodeRef errandCategoryFolder = documentAttachmentsService.getCategory(category, createdNode);
                                // копируем вложения для категории
                                List<ChildAssociationRef> childs = nodeService.getChildAssocs(categoryRef);
                                for (ChildAssociationRef child : childs) {
                                    NodeRef childRef = child.getChildRef();
                                    List<AssociationRef> parentDocAssocs = nodeService.getTargetAssocs(childRef, DocumentService.ASSOC_PARENT_DOCUMENT);
                                    for (AssociationRef parentDocAssoc : parentDocAssocs) {
                                        //для всех вложений получаем ссылку на родительский документ
                                        NodeRef parentDoc = parentDocAssoc.getTargetRef();
                                        // временно удаляем ассоциацию
                                        nodeService.removeAssociation(childRef, parentDoc, DocumentService.ASSOC_PARENT_DOCUMENT);
                                        try {
                                            copyService.copyAndRename(childRef, errandCategoryFolder, ContentModel.ASSOC_CONTAINS,
                                                    QName.createQName(ContentModel.PROP_CONTENT.getNamespaceURI(), nodeService.getProperty(childRef, ContentModel.PROP_NAME).toString()),
                                                    false);

                                        } finally {
                                            // возвращаем удаленную ассоциацию
                                            try {
                                                nodeService.createAssociation(childRef, parentDoc, DocumentService.ASSOC_PARENT_DOCUMENT);
                                            } catch (AssociationExistsException ignored) {
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                //копируем табличные данные
                if (null != settings) {
                    List<String> tableDataToCopy = settings.getTableDataToCopy();
                    if (null != tableDataToCopy) {
                        for (String table : tableDataToCopy) {
                            QName tableType = QName.createQName(table, namespaceService);
                            List<NodeRef> targets = findNodesByAssociationRef(document, RegexQNamePattern.MATCH_ALL, tableType, ASSOCIATION_TYPE.TARGET);
                            documentTableService.copyTableData(createdNode, targets.get(0));
                        }
                    }

                }

                return createdNode;
            }
        }
        return null;
    }
    @Override
    public boolean canCopyDocument(NodeRef document){
        boolean canCopy = false;
        if (document != null && nodeService.exists(document)) {
            final QName docType = nodeService.getType(document);
            final String prefixedType = docType.toPrefixString(namespaceService);

            DocumentCopySettings settings = DocumentCopySettingsBean.getSettingsForDocType(prefixedType);
            canCopy = settings != null && !settings.isEmpty();
        }
        return canCopy;
    }

    @Override
    public String getDocumentCopyURL(NodeRef document) {
        StringBuilder urlBuilder = new StringBuilder();

        if (document != null && nodeService.exists(document)) {
            final QName docType = nodeService.getType(document);
            final String prefixedType = docType.toPrefixString(namespaceService);

            DocumentCopySettings settings = DocumentCopySettingsBean.getSettingsForDocType(prefixedType);
            boolean isHasSettings = settings != null && !settings.isEmpty();

            if (isHasSettings) {
                urlBuilder.append(settings.getBaseURL()).append("?documentType=").append(prefixedType);

                StringBuilder paramsBuilder = new StringBuilder();
                paramsBuilder.append("documentType=").append(prefixedType);

                // копируем свойства
                final List<String> propertiesToCopy = settings.getPropsToCopy();
                Map<QName, Serializable> originalProperties = nodeService.getProperties(document);
                for (String propName : propertiesToCopy) {
                    try {
                        QName propQName = QName.createQName(propName, namespaceService);
                        if (null != propQName) {
                            Object propValue = originalProperties.get(propQName);
                            if (null != propValue) {
                                if (propValue instanceof Date) {
                                    propValue = BaseBean.DateFormatISO8601.format(propValue);
                                }
                                paramsBuilder.append("&prop_")
                                        .append(propQName.toPrefixString(namespaceService).replace(":", "_"))
                                        .append("=")
                                        .append(propValue.toString());
                            }
                        }
                    } catch (InvalidQNameException invalid) {
                        logger.warn("Invalid QName for document property:" + propName);
                    }
                }
                // Формируем список вложений для копирования. Копирование происходит в полиси создания ассоциации temp-attachments
                final List<String> categories = settings.getCategoriesToCopy();
                if (categories != null) {

                    List<NodeRef> attachmentsToMove = new ArrayList<>();
                    NodeRef userTemp = null;
                    try {
                        userTemp = repositoryStructureHelper.getUserTemp(false);
                        if (userTemp == null) {
                            userTemp = repositoryStructureHelper.createUserTemp();
                        }
                    } catch (WriteTransactionNeededException e) {
                        logger.error(e.toString());
                    }
                    for (String category : categories) {
                        NodeRef categoryRef = documentAttachmentsService.getCategory(category, document);
                        if (categoryRef != null) {
                            // Проходим по вложениям, копируя их в userTemp
                            List<NodeRef> attachmentsFromCategory = documentAttachmentsService.getAttachmentsByCategory(categoryRef);
                            for (NodeRef attachment : attachmentsFromCategory) {
                                if (userTemp != null) {
                                    List<AssociationRef> parentDocAssocs = nodeService.getTargetAssocs(attachment, DocumentService.ASSOC_PARENT_DOCUMENT);
                                    for (AssociationRef parentDocAssoc : parentDocAssocs) {
                                        // Для всех вложений получаем ссылку на родительский документ
                                        NodeRef parentDoc = parentDocAssoc.getTargetRef();
                                        // Временно удаляем ассоциацию
                                        nodeService.removeAssociation(attachment, parentDoc, DocumentService.ASSOC_PARENT_DOCUMENT);
                                        // Копируем
                                        NodeRef attachmentCopy = copyService.copyAndRename(attachment, userTemp, ContentModel.ASSOC_CONTAINS,
                                                QName.createQName(ContentModel.PROP_CONTENT.getNamespaceURI(), nodeService.getProperty(attachment, ContentModel.PROP_NAME).toString()),
                                                false);
                                        attachmentsToMove.add(attachmentCopy);
                                        // Возвращаем ассоциацию
                                        nodeService.createAssociation(attachment, parentDoc, DocumentService.ASSOC_PARENT_DOCUMENT);

                                    }
                                }
                            }
                        }
                    }

                    if (attachmentsToMove.size() != 0) {
                        paramsBuilder.append("&assoc_")
                                .append(DocumentService.ASSOC_TEMP_ATTACHMENTS.toPrefixString(namespaceService).replace(":", "_"))
                                .append("=")
                                .append(StringUtils.collectionToDelimitedString(attachmentsToMove, ","));
                    }
                }

                final List<String> assocsToCopy = settings.getAssocsToCopy();
                for (String assocName : assocsToCopy) {
                    try {
                        QName assocQName = QName.createQName(assocName, namespaceService);
                        if (null != assocQName) {
                            List<NodeRef> targets = findNodesByAssociationRef(document, assocQName, null, ASSOCIATION_TYPE.TARGET);
                            if (null != targets) {
                                paramsBuilder.append("&assoc_")
                                        .append(assocQName.toPrefixString(namespaceService).replace(":", "_"))
                                        .append("=")
                                        .append(StringUtils.collectionToDelimitedString(targets, ","));
                            }
                        }
                    } catch (InvalidQNameException invalid) {
                        logger.warn("Invalid QName for document assoc:" + assocName);
                    }
                }

                String encodedParams = Base64.encodeBase64String(paramsBuilder.toString().getBytes());
                int encodedParamsHash = paramsBuilder.toString().hashCode();

                String encodedURIParams = URLEncoder.encodeUriComponent(encodedParams);

                urlBuilder.append("&p1=").append(encodedURIParams);
                urlBuilder.append("&p2=").append(encodedParamsHash);
            }
        }

        return urlBuilder.toString();
    }

    private String getDraftRootLabel(String docType) {
        return getDocumentTypeLabel(docType);
    }

    private void refreshValues(NodeRef documentNodeRef) {
        int personsCount = 0;
        int summaryRating = 0;
        int size;

        for (int i = 1; i < 6; i++) {
            List<NodeRef> rated = (List<NodeRef>) nodeService.getProperty(documentNodeRef, QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-" + i));

            if (rated != null && !rated.isEmpty()) {
                size = rated.size();
                personsCount += size;
                summaryRating += (size * i);
            }
        }
        BigDecimal rating = (new BigDecimal((float) summaryRating / personsCount)).setScale(1, BigDecimal.ROUND_HALF_UP);
        //TODO DONE замена нескольких setProperty на setProperties.
        Map<QName, Serializable> properties = nodeService.getProperties(documentNodeRef);
        properties.put(DocumentService.PROP_RATED_PERSONS_COUNT, personsCount);
        properties.put(DocumentService.PROP_RATING, rating.toString());
        nodeService.setProperties(documentNodeRef, properties);
    }

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setCopyService(CopyService copyService) {
        this.copyService = copyService;
    }

    @Override
    public List<String> getRegNumbersValues(NodeRef document) {
        List<String> resultValues = new ArrayList<String>();
        String regProjectNumber = getProjectRegNumber(document);
        if (regProjectNumber != null) {
            resultValues.add(regProjectNumber);
        }
        String regDocNumber = getDocumentRegNumber(document);
        if (regDocNumber != null) {
            resultValues.add(regDocNumber);
        }
        return resultValues;
    }

    @Override
    public Date getProjectRegDate(NodeRef document) {
        return getRegDate(document, true);
    }

    @Override
    public Date getDocumentRegDate(NodeRef document) {
        return getRegDate(document, false);
    }

    @Override
    public String getProjectRegNumber(NodeRef document) {
        return getRegNumber(document, true);
    }

    @Override
    public String getDocumentRegNumber(NodeRef document) {
        return getRegNumber(document, false);
    }

    @Override
    public String getDocumentActualNumber(NodeRef document) {
        Serializable number = nodeService.getProperty(document, DocumentService.PROP_DOCUMENT_REGNUM);
        if (number != null) {
            return number.toString();
        } else {
            return "Не присвоено";
        }
    }

    @Override
    public Date getDocumentActualDate(NodeRef document) {
        Serializable regDate = nodeService.getProperty(document, DocumentService.PROP_DOCUMENT_DATE);
        if (regDate != null) {
            return (Date) regDate;
        }
        return null;
    }

    @Override
    public void setDocumentActualNumber(NodeRef document, String number) {
        nodeService.setProperty(document, DocumentService.PROP_DOCUMENT_REGNUM, number);
    }

    @Override
    public void setDocumentActualDate(NodeRef document, Date date) {
        nodeService.setProperty(document, DocumentService.PROP_DOCUMENT_DATE, date);
    }

    private String getRegNumber(NodeRef document, boolean getProjectNumber) {
        QName regAspectName = getProjectNumber ? DocumentService.ASPECT_HAS_REG_PROJECT_DATA : DocumentService.ASPECT_HAS_REG_DOCUMENT_DATA;
        QName propNumber = getProjectNumber ? DocumentService.PROP_REG_DATA_PROJECT_NUMBER : DocumentService.PROP_REG_DATA_DOC_NUMBER;
        if (nodeService.hasAspect(document, regAspectName)) {
            Serializable number = nodeService.getProperty(document, propNumber);
            if (number != null) {
                return number.toString();
            }
        }
        return null;
    }

    private Date getRegDate(NodeRef document, boolean getProjectNumber) {
        QName regAspectName = getProjectNumber ? DocumentService.ASPECT_HAS_REG_PROJECT_DATA : DocumentService.ASPECT_HAS_REG_DOCUMENT_DATA;
        QName propDate = getProjectNumber ? DocumentService.PROP_REG_DATA_PROJECT_DATE : DocumentService.PROP_REG_DATA_DOC_DATE;
        if (nodeService.hasAspect(document, regAspectName)) {
            Serializable regDate = nodeService.getProperty(document, propDate);
            if (regDate != null) {
                return (Date) regDate;
            }
        }
        return null;
    }

    @Override
    public NodeRef getDocumentRegistrator(NodeRef document) {
        if (nodeService.hasAspect(document, DocumentService.ASPECT_HAS_REG_DOCUMENT_DATA)) {
            List<AssociationRef> refs = nodeService.getTargetAssocs(document, DocumentService.ASSOC_REG_DATA_DOC_REGISTRATOR);
            if (refs != null && !refs.isEmpty()) {
                return refs.get(0).getTargetRef();
            }
        }
        return null;
    }

    @Override
    public String getPresentString(final NodeRef document) {
        return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<String>() {
            @Override
            public String doWork() throws Exception {
                Serializable presentString = nodeService.getProperty(document, PROP_PRESENT_STRING);
                return presentString != null ? presentString.toString() : null;
            }
        });
    }

    @Override
    public NodeRef getDocumentAuthor(NodeRef document) {
        QName docType = nodeService.getType(document);
        String docAuthorProperty = getAuthorProperty(docType);
        Object authorStr = nodeService.getProperty(document, QName.createQName(docAuthorProperty, namespaceService));
        if (authorStr != null && NodeRef.isNodeRef((String) authorStr)) {
            return new NodeRef((String) authorStr);
        } else {
            return null;
        }
    }

    @Override
    public Collection<QName> getDocumentSubTypes() {
        Collection<QName> subTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);
        subTypes.remove(DocumentService.TYPE_BASE_DOCUMENT);
        return subTypes;
    }

    @Override
    public boolean execExpression(NodeRef document, String expression) {
        Expression evaluator = new Expression(document, serviceRegistry, applicationContext);
        return evaluator.executeAsBoolean(expression);
    }

	@Override
	public String execStringExpression(NodeRef document, String expression) {
		Expression evaluator = new Expression(document, serviceRegistry, applicationContext);
		return evaluator.executeAsString(expression);
	}

    @Override
    public void finalizeToUnit(NodeRef document, Boolean sharedFolder, NodeRef primaryUnit, List<NodeRef> additionalUnits) {
        if (!nodeService.hasAspect(document, DocumentService.ASPECT_FINALIZE_TO_UNIT)) {
            nodeService.addAspect(document, DocumentService.ASPECT_FINALIZE_TO_UNIT, null);
        }

        if (sharedFolder != null) {
            nodeService.setProperty(document, DocumentService.PROP_IS_SHARED_FOLDER, sharedFolder);
        }

        if (primaryUnit != null) {
            List<NodeRef> units = new ArrayList<NodeRef>(1);
            units.add(primaryUnit);
            nodeService.setAssociations(document, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, units);
        }

        if (additionalUnits != null) {
            HashSet<NodeRef> unique = new HashSet<NodeRef>(additionalUnits);
            nodeService.setAssociations(document, DocumentService.ASSOC_ADDITIONAL_ORGANIZATION_UNIT_ASSOC, new ArrayList<NodeRef>(unique));
        }
    }

    @Override
    public void finalizeToUnit(NodeRef document, Boolean sharedFolder, NodeRef primaryUnit) {
        finalizeToUnit(document, sharedFolder, primaryUnit, null);
    }

    @Override
    public void finalizeToUnit(NodeRef document, NodeRef primaryUnit, List<NodeRef> additionalUnits) {
        finalizeToUnit(document, null, primaryUnit, additionalUnits);
    }

    @Override
    public void finalizeToUnit(NodeRef document, NodeRef primaryUnit) {
        finalizeToUnit(document, null, primaryUnit, null);
    }

    @Override
    public List<NodeRef> getDocumentsByQuery(String query, List<SortDefinition> sort, int skipCount, int loadCount) {
        //List<String> paths = Arrays.asList(getDraftPath(), getDocumentsFolderPath());

        SearchParameters sp = buildDocumentsSearcParametersByFilter(null, null, null, query, sort);

        sp.setSkipCount(skipCount);
        sp.setMaxItems(loadCount);

        ResultSet results = null;
        List<NodeRef> records = new ArrayList<NodeRef>();
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return records;
    }

    public String wrapAsDocumentLink(NodeRef documentRef) {
        return wrapperLink(documentRef, (String) nodeService.getProperty(documentRef, PROP_EXT_PRESENT_STRING), getDocumentUrl(documentRef));
    }

    @Override
    public void addToFavourites(NodeRef document) {
        String favourites = "org.alfresco.share.documents.favourites";
        String currentUser = authService.getCurrentUserName();
        Map<String, Serializable> preferences = preferenceService.getPreferences(currentUser, favourites);
        if (preferences != null) {
            String favouriteDocs = preferences.get(favourites) != null ? preferences.get(favourites).toString() : "";
            if (!favouriteDocs.contains(document.toString())) {
                favouriteDocs += (favouriteDocs.isEmpty() ? "" : ",") + document.toString();
                preferences.put(favourites, favouriteDocs);
            }
        } else {
            preferences = new HashMap<>();
            preferences.put(favourites, document.toString());
        }
        preferenceService.setPreferences(currentUser, preferences);
    }

    @Override
    public void removeFromFavourites(NodeRef document) {
        String favourites = "org.alfresco.share.documents.favourites";
        String currentUser = authService.getCurrentUserName();
        Map<String, Serializable> preferences = preferenceService.getPreferences(currentUser, favourites);
        if (preferences != null) {
            String favouriteDocs = preferences.get(favourites) != null ? preferences.get(favourites).toString() : null;
            if (favouriteDocs != null) {
                if (favouriteDocs.contains(document.toString())) {
                    favouriteDocs = favouriteDocs.replace("," + document.toString(), "")
                            .replace(document.toString(), "");
                    preferences.put(favourites, favouriteDocs);
                    preferenceService.setPreferences(currentUser, preferences);
                }
            }
        }
    }

    @Override
    public boolean hasOrganization(NodeRef document) {
        return getOrganization(document) != null;
    }

    @Override
    public NodeRef getOrganization(NodeRef document) {
        return orgstructureService.getOrganization(document);
    }

    @Override
    public String getCreateUrl(QName type) {
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_DOCUMENT_URL));
        if (constraint != null && (constraint.getConstraint() instanceof DocumentUrlConstraint)) {
            String value = ((DocumentUrlConstraint) constraint.getConstraint()).getCreateUrl();
            return value == null ? DEFAULT_CREATE_URL : value;
        } else {
            return DEFAULT_CREATE_URL;
        }
    }

    @Override
    public String getViewUrl(QName type) {
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_DOCUMENT_URL));
        if (constraint != null && (constraint.getConstraint() instanceof DocumentUrlConstraint)) {
            String value = ((DocumentUrlConstraint) constraint.getConstraint()).getViewUrl();
            return value == null ? DEFAULT_VIEW_URL : value;
        } else {
            return DEFAULT_VIEW_URL;
        }
    }

    @Override
    public String getDocumentUrl(NodeRef document) {
        QName type = nodeService.getType(document);
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String context = params.getShareContext();
        return "/" + context + "/page/" + getViewUrl(type);
    }

    @Override
    public String getDocumentTypeLabel(String docType) {
        QName typeQName = QName.createQName(docType, namespaceService);
        TypeDefinition definition = dictionaryService.getType(typeQName);
        String key = definition.getModel().getName().toPrefixString(namespaceService);
        key += ".type." + docType + ".title";
        key = StringUtils.replace(key, ":", "_");
        String label = I18NUtil.getMessage(key, I18NUtil.getLocale());
        return label != null ? label : key;
    }
}
