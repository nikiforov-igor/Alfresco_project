package ru.it.lecm.contracts.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: mshafeev
 * Date: 16.04.13
 * Time: 14:16
 */
public class ContractsBeanImpl extends BaseBean {
    public static final String CONTRACTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";
    public static final String CONTRACTS_EXT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/ext/1.0";
    public static final String ADDITIONAL_DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/additional-document/1.0";
    public static final String CONTRACT_TABLES_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/table-structure/1.0";
    public static final String CONTRACT_DIC_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/dictionaries/1.0";

    public static final QName TYPE_CONTRACTS_DOCUMENT = QName.createQName(CONTRACTS_NAMESPACE_URI, "document");
    public static final QName TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocument");
    public static final QName TYPE_CONTRACT_STAGE = QName.createQName(CONTRACT_TABLES_NAMESPACE_URI, "stage");
    public static final QName TYPE_DASHLET_SETTINGS = QName.createQName(CONTRACTS_EXT_NAMESPACE_URI, "dashlet-settings");

    public static final QName PROP_START_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "startDate");
    public static final QName PROP_END_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "endDate");

    public static final QName PROP_STAGE_STATUS = QName.createQName(CONTRACT_TABLES_NAMESPACE_URI, "stage-status");
    public static final QName PROP_STAGE_START_DATE_REAL = QName.createQName(CONTRACT_TABLES_NAMESPACE_URI, "start-date-real");
    public static final QName PROP_STAGE_END_DATE_REAL = QName.createQName(CONTRACT_TABLES_NAMESPACE_URI, "end-date-real");
    public static final QName PROP_STAGE_START_DATE = QName.createQName(CONTRACT_TABLES_NAMESPACE_URI, "start-date");
    public static final QName PROP_STAGE_END_DATE = QName.createQName(CONTRACT_TABLES_NAMESPACE_URI, "end-date");

    public static final QName ASSOC_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "document-assoc");
    public static final QName ASSOC_CONTRACT_TYPE = QName.createQName(CONTRACTS_NAMESPACE_URI, "typeContract-assoc");
    public static final QName ASSOC_CONTRACT_SUBJECT = DocumentService.ASSOC_SUBJECT;
    public static final QName ASSOC_CONTRACT_PARTNER = QName.createQName(CONTRACTS_NAMESPACE_URI, "partner-assoc");
    public static final QName ASSOC_CONTRACT_REPRESENTATIVE = QName.createQName(CONTRACTS_NAMESPACE_URI, "representative-assoc");
    public static final QName ASSOC_CONTRACT_CURRENCY = QName.createQName(CONTRACTS_NAMESPACE_URI, "currency-assoc");

    public static final QName PROP_SUMMARY_CONTENT = QName.createQName(CONTRACTS_NAMESPACE_URI, "summaryContent");
    public static final QName PROP_SIGNATORY_COUNTERPARTY = QName.createQName(CONTRACTS_NAMESPACE_URI, "signatoryCounterparty");

    public static final QName TYPE_CONTRACTS_TYPE = QName.createQName(CONTRACT_DIC_NAMESPACE_URI, "contract-type");

    public static final String CONTRACTS_ROOT_NAME = "Сервис Договора";
    public static final String CONTRACTS_ROOT_ID = "CONTRACTS_ROOT_ID";
    public static final String CONTRACTS_DASHLET_SETTINGS_ID = "Настройки дашлета";

    public NodeRef dashletSettings = null;

    private SearchService searchService;
    private DocumentService documentService;
    private NamespaceService namespaceService;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public NodeRef getDraftRoot() {
        return documentService.getDraftRootByType(TYPE_CONTRACTS_DOCUMENT);
    }


    //TODO DONE Refactoring in progress...
    public NodeRef createDraftRoot() throws WriteTransactionNeededException {
        return documentService.createDraftRoot(TYPE_CONTRACTS_DOCUMENT);
    }

    public String getDraftPath() {
        return documentService.getDraftPathByType(TYPE_CONTRACTS_DOCUMENT);
    }

    public String getDocumentsFolderPath() {
        return documentService.getDocumentsFolderPath();
    }

    public NodeRef getDashletSettings() {
        return dashletSettings;
    }
	
	
    
	@Override
	public void initService() {
		super.initService();
		final NodeRef serviceRoot = getFolder(CONTRACTS_ROOT_ID);
		dashletSettings = nodeService.getChildByName(serviceRoot, ContentModel.ASSOC_CONTAINS, CONTRACTS_DASHLET_SETTINGS_ID);
		if (dashletSettings == null) {
			try {
				QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, CONTRACTS_DASHLET_SETTINGS_ID);
				Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
				properties.put(ContentModel.PROP_NAME, CONTRACTS_DASHLET_SETTINGS_ID);
				ChildAssociationRef childAssoc = nodeService.createNode(serviceRoot, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_DASHLET_SETTINGS, properties);
				dashletSettings = childAssoc.getChildRef();
			} catch(Exception e) {
				dashletSettings = nodeService.getChildByName(serviceRoot, ContentModel.ASSOC_CONTAINS, CONTRACTS_DASHLET_SETTINGS_ID);
			}
		}
	}

    /**
     * Поиск договоров
     *
     * @param path     путь где следует искать
     * @param statuses статусы договоров
     * @return List<NodeRef>
     */
    public List<NodeRef> getContracts(ArrayList<String> path, ArrayList<String> statuses) {
        return getContractsByFilter(null, null, null, path, statuses, null, null, false);
    }

    /**
     * Метод получения всех участников
     *
     * @return
     */
    public List<NodeRef> getAllMembers() {
        return documentService.getMembers(TYPE_CONTRACTS_DOCUMENT);
    }

    public List<NodeRef> getAllMembers(String sortColumnLocalName, final boolean sortAscending) {
        List<NodeRef> members = getAllMembers();
        final QName sortFieldQName = sortColumnLocalName != null && sortColumnLocalName.length() > 0 ? QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, sortColumnLocalName) : OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME;

        class NodeRefComparator<T extends Serializable & Comparable<T>> implements Comparator<NodeRef> {
            @Override
            public int compare(NodeRef nodeRef1, NodeRef nodeRef2) {
                Object object1 = nodeService.getProperty(nodeRef1, sortFieldQName);
                Object object2 = nodeService.getProperty(nodeRef2, sortFieldQName);
                T obj1 = (T) (object1 != null ? object1 : "");
                T obj2 = (T) (object2 != null ? object2 : "");
                return sortAscending ? obj1.compareTo(obj2) : obj2.compareTo(obj1);
            }
        }
        if (members.size() > 0 && nodeService.getProperties(members.get(0)).containsKey(sortFieldQName)) {
            Collections.sort(members, new NodeRefComparator<String>());
        }
        ;
        return members;
    }

    public List<NodeRef> getContractsByFilter(QName dateProperty, Date begin, Date
            end, List<String> paths, List<String> statuses, List<NodeRef> initiatorsList, List<NodeRef> docsList,
                                              boolean includeAdditional) {
        Map<QName, List<NodeRef>> initList = new HashMap<QName, List<NodeRef>>();
        List<QName> types = new ArrayList<QName>(2);
        types.add(TYPE_CONTRACTS_DOCUMENT);
        if (initiatorsList != null) {
            initList.put(TYPE_CONTRACTS_DOCUMENT, initiatorsList);
        }

        if (includeAdditional) {
            types.add(TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);
            if (initiatorsList != null) {
                initList.put(TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, initiatorsList);
            }
        }

        String filterQuery = "";

        // фильтр по сотрудниками-создателям
        if (initiatorsList != null && !initiatorsList.isEmpty()) {
            StringBuilder employeesFilter = new StringBuilder();

            boolean addOR = false;

            for (QName type : types) {
                String authorProperty = documentService.getAuthorProperty(type);
                authorProperty = authorProperty.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                for (NodeRef employeeRef : initList.get(type)) {
                    employeesFilter.append(addOR ? " OR " : "").append("@").append(authorProperty).append(":\"").append(employeeRef.toString().replace(":", "\\:")).append("\"");
                    addOR = true;
                }
            }

            if (employeesFilter.length() > 0) {
                filterQuery += employeesFilter;
            }
        }

        // фильтр по конкретным документам (например, тем в которых данный сотрудник - участник)
        if (docsList != null && !docsList.isEmpty()) {
            boolean addOR = false;
            StringBuilder docsFilter = new StringBuilder();
            for (NodeRef docRef : docsList) {
                docsFilter.append(addOR ? " OR " : "").append("ID:").append(docRef.toString().replace(":", "\\:"));
                addOR = true;
            }
            filterQuery += (filterQuery.length() > 0 ? " AND (" : "(") + docsFilter.toString() + ")";
        }

        // Фильтр по датам
        if (dateProperty != null) {
            final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
            final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";

            String property = dateProperty.toPrefixString(namespaceService);
            property = property.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
            filterQuery += (filterQuery.length() > 0 ? " AND " : "") + "@" + property + ":[\"" + MIN + "\" TO \"" + MAX + "\"]";
        }
        return documentService.getDocumentsByFilter(types, paths, statuses, filterQuery, null);
    }

    public List<NodeRef> getAllContractDocuments(NodeRef contractRef) {
        return findNodesByAssociationRef(contractRef, ASSOC_DOCUMENT, TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, ASSOCIATION_TYPE.SOURCE);
    }

    public List<NodeRef> getAdditionalDocs(String filter) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query;

        // формируем базовый запрос - ищем документы к договорам в папке Черновики и Документы
        query = "TYPE:\"" + TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT + "\" AND " +
                "(PATH:\"" + documentService.getDraftPath() + "//*\" OR PATH:\"" + documentService.getDocumentsFolderPath() + "//*\")";


        if (filter != null && filter.length() > 0) {
            query += " AND (" + filter + ") ";
        }

        query += (query.length() > 0 ? " AND " : "") + "{{IN_SAME_ORGANIZATION}}";

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

    public boolean isContract(NodeRef ref) {
        Set<QName> types = new HashSet<QName>();
        types.add(TYPE_CONTRACTS_DOCUMENT);
        return isProperType(ref, types);
    }

    public NodeRef dublicateContract(NodeRef nodeRef) {
        return documentService.duplicateDocument(nodeRef);
    }

    public String getAuthorProperty() {
        return documentService.getAuthorProperty(TYPE_CONTRACTS_DOCUMENT);
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
}
