package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.DocumentEventCategory;
import ru.it.lecm.documents.constraints.AuthorPropertyConstraint;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 28.02.13
 * Time: 16:28
 */
public class DocumentServiceImpl extends BaseBean implements DocumentService {
    private OrgstructureBean orgstructureService;
    private BusinessJournalService businessJournalService;
    private Repository repositoryHelper;
    private NamespaceService namespaceService;
	private DictionaryService dictionaryService;
    private LecmPermissionService lecmPermissionService;
    private DocumentMembersService documentMembersService;
    private SearchService searchService;

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
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
            if (!(namespaceService.getPrefixes(e.getKey().getNamespaceURI()).contains("cm") ||
                    namespaceService.getPrefixes(e.getKey().getNamespaceURI()).contains("sys"))) {
                properties.put(e.getKey(), e.getValue());
            }
        }
        return properties;
    }

    /**
     * Создание документа
     * @param type тип документа lecm-contract:document
     * @param property свойства документа
     * @return
     */
    @Override
    public NodeRef createDocument(String type, Map<String, String> property, final Map<String,String> association) {
        // получение папки черновиков для документа
        final NodeRef draftRef;
        if (getDraftRootLabel(type) != null) {
            draftRef = getDraftRootByType(QName.createQName(type, namespaceService));
        } else {
            draftRef = getDraftRoot();
        }

        final QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
        final QName assocQName = ContentModel.ASSOC_CONTAINS;
        final QName nodeTypeQName =  QName.createQName(type, namespaceService);

        final Map<QName, Serializable> properties =  new HashMap<QName, Serializable>();
        for(Map.Entry<String, String> e: property.entrySet()) {
            properties.put(QName.createQName(e.getKey(),namespaceService),e.getValue());
        }

        ChildAssociationRef associationRef = nodeService.createNode(draftRef, assocTypeQName, assocQName, nodeTypeQName, properties);

        for(Map.Entry<String, String> assoc : association.entrySet()) {
           nodeService.createAssociation(associationRef.getChildRef(), new NodeRef(assoc.getValue()), QName.createQName(assoc.getKey().toString(),namespaceService));
        }

        return associationRef.getChildRef();
    }

    /**
     * Изменение свойств документа
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
        return  nodeService.getPath(draftRef).toPrefixString(namespaceService);
    }

    public NodeRef getDraftRoot() {
        NodeRef person = repositoryHelper.getPerson();
        return repositoryStructureHelper.getDraftsRef(person);
    }

    public NodeRef getDraftRootByType(QName docType) {
        final NodeRef draftRef = getDraftRoot();
        final String rootName = getDraftRootLabel(docType);
        NodeRef nodeRef = nodeService.getChildByName(draftRef, ContentModel.ASSOC_CONTAINS, rootName);

        if (nodeRef == null) {
	        RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
	        return transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
		        @Override
		        public NodeRef execute () throws Throwable {
			        QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
			        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
			        QName nodeTypeQName = ContentModel.TYPE_FOLDER;

			        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
			        properties.put(ContentModel.PROP_NAME, rootName);
			        ChildAssociationRef childAssoc = nodeService.createNode(draftRef, assocTypeQName, assocQName, nodeTypeQName, properties);
			        return childAssoc.getChildRef ();
		        }
	        }, false, true);
        }
        return nodeRef;
    }

    @Override
    public String getDraftRootLabel(QName docType) {
        return getDraftRootLabel(docType.toPrefixString(namespaceService));
    }

    public String getDocumentsFolderPath() {
        NodeRef nodeRef = repositoryStructureHelper.getDocumentsRef();
        return nodeService.getPath(nodeRef).toPrefixString(namespaceService);
    }

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public NodeRef getDocumentFromPackageItems(NodeRef packageRef) {
		NodeRef documentRef = null;
		List<ChildAssociationRef> packageAssocs = nodeService.getChildAssocs(packageRef);
		if (packageAssocs != null && packageAssocs.size() == 1) {
			documentRef = packageAssocs.get(0).getChildRef();
		}
		if (documentRef != null && isDocument(documentRef)) {
			return documentRef;
		}
		return null;
	}

    @Override
    public List<NodeRef> getMembers(QName docType) {
        NodeRef membersUnit = documentMembersService.getMembersUnit(docType);
        List<NodeRef> allMembers = findNodesByAssociationRef(membersUnit, DocumentMembersService.ASSOC_UNIT_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
        List<NodeRef> result = new ArrayList<NodeRef>();
        for (NodeRef allMember : allMembers) {
            if (!isArchive(allMember)){
                result.add(allMember);
            }
        }
        return result;
    }

    @Override
    public List<NodeRef> getDocuments(List<QName> docTypes, List<String> paths, ArrayList<String> statuses) {
        return getDocumentsByFilter(docTypes, null, null, null, paths, statuses, null, null);
    }

    @Override
    public List<NodeRef> getDocumentsByFilter(List<QName> docTypes, QName dateProperty, Date begin, Date end, List<String> paths, List<String> statuses, Map<QName,List<NodeRef>> initiatorsList, List<NodeRef> docsList) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);

        String query = "";
        if (docTypes != null && !docTypes.isEmpty()) {
            boolean addOR = false;
            String typesFilter = "";
            for (QName type : docTypes) {
                typesFilter += (addOR ? " OR " : "") + " TYPE:\"" + type + "\"";
                addOR = true;
            }
            query += "(" + typesFilter + ")";
        }

        // пути
        if (paths != null && !paths.isEmpty()) {
            boolean addOR = false;
            String pathsFilter = "";
            for (String path : paths) {
                pathsFilter += (addOR ? " OR " : "") + "PATH:\"" + path + "//*\"";
                addOR = true;
            }
            query += " AND (" + pathsFilter + ")";
        }

        // Фильтр по датам
        if (dateProperty != null) {
            final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
            final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";

            String property = dateProperty.toPrefixString(namespaceService);
            property = property.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
            query += " AND @" + property + ":[" + MIN + " TO " + MAX + "]";
        }

        // фильтр по статусам
        if (statuses != null && !statuses.isEmpty()) {
            String statusesFilter = "";
            String statusesNotFilter = "";
            for (String status : statuses) {
                if (!status.trim().startsWith("!")) {
                    statusesFilter += " @lecm\\-statemachine\\:status:\"" + status.replace("!", "").trim() + "\"";
                } else {
                    statusesNotFilter += " @lecm\\-statemachine\\:status:\"" + status.replace("!", "").trim() + "\"";
                }
            }
            query += (!statusesFilter.isEmpty() ? (" AND (" + statusesFilter + ")") : "")  +
                    (!statusesNotFilter.isEmpty() ? (" AND NOT (" + statusesNotFilter  + ")") : "");
        }

        // фильтр по сотрудниками-создателям
        if (initiatorsList != null && !initiatorsList.isEmpty()) {
            String employeesFilter = "";

            boolean addOR = false;

            for (QName type : docTypes) {
                String authorProperty = getAuthorProperty(type);
                authorProperty = authorProperty.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                for (NodeRef employeeRef : initiatorsList.get(type)) {
                    employeesFilter += (addOR ? " OR " : "") + "@" + authorProperty + ":\"" + employeeRef.toString().replace(":", "\\:") + "\"";
                    addOR = true;
                }
            }

            if (employeesFilter.length() > 0) {
                query += " AND (" + employeesFilter + ")";
            }
        }

        // фильтр по конкретным документам (например, тем в которых данный сотрудник - участник)
        if (docsList != null && !docsList.isEmpty()) {
            boolean addOR = false;
            String docsFilter = "";
            for (NodeRef docRef : docsList) {
                docsFilter += (addOR ? " OR " : "") + "ID:" + docRef.toString().replace(":", "\\:");
                addOR = true;
            }
            query += " AND (" + docsFilter + ")";
        }

        ResultSet results = null;
        sp.setQuery(query);
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

    private String getDraftRootLabel(String docType) {
        QName typeQName = QName.createQName(docType, namespaceService);
        TypeDefinition definition = dictionaryService.getType(typeQName);
        String key = definition.getModel().getName().toPrefixString(namespaceService);
        key += ".type." + docType + ".title";
        key = StringUtils.replace(key, ":", "_");
        String label = I18NUtil.getMessage(key, I18NUtil.getLocale());
        return label != null ? label : key;
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

        nodeService.setProperty(documentNodeRef, DocumentService.PROP_RATED_PERSONS_COUNT, personsCount);
        nodeService.setProperty(documentNodeRef, DocumentService.PROP_RATING, rating.toString());
    }

}
