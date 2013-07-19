package ru.it.lecm.contracts.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import ru.it.lecm.base.beans.BaseBean;
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
	public static final String ADDITIONAL_DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/additional-document/1.0";

	public static final QName TYPE_CONTRACTS_DOCUMENT = QName.createQName(CONTRACTS_NAMESPACE_URI, "document");
	public static final QName TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocument");
    public static final QName PROP_START_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "startDate");
    public static final QName PROP_END_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "endDate");

    public static final QName ASSOC_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "document-assoc");
	public static final QName ASSOC_CONTRACT_TYPE = QName.createQName(CONTRACTS_NAMESPACE_URI, "typeContract-assoc");
	public static final QName ASSOC_CONTRACT_SUBJECT = QName.createQName(CONTRACTS_NAMESPACE_URI, "subjectContract-assoc");
	public static final QName ASSOC_CONTRACT_PARTNER = QName.createQName(CONTRACTS_NAMESPACE_URI, "partner-assoc");
	public static final QName ASSOC_CONTRACT_REPRESENTATIVE = QName.createQName(CONTRACTS_NAMESPACE_URI, "representative-assoc");
	public static final QName ASSOC_CONTRACT_CURRENCY = QName.createQName(CONTRACTS_NAMESPACE_URI, "currency-assoc");

	public static final QName PROP_SUMMARY_CONTENT = QName.createQName(CONTRACTS_NAMESPACE_URI, "summaryContent");
	public static final QName PROP_SIGNATORY_COUNTERPARTY = QName.createQName(CONTRACTS_NAMESPACE_URI, "signatoryCounterparty");

    private SearchService searchService;
    private DocumentService documentService;

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
		return  documentService.getDraftRootByType(TYPE_CONTRACTS_DOCUMENT);
	}

	public String getDraftPath() {
		return  documentService.getDraftPathByType(TYPE_CONTRACTS_DOCUMENT);
	}

    public String getDocumentsFolderPath(){
        return documentService.getDocumentsFolderPath();
    }

    /**
     * Поиск договоров
     * @param path путь где следует искать
     * @param statuses статусы договоров
     * @return List<NodeRef>
     */
    public List<NodeRef> getContracts(ArrayList<String> path, ArrayList<String> statuses) {
        return getContractsByFilter(null, null, null, path, statuses, null, null, false);
    }

    /**
     * Метод получения всех участников
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
                T obj1 = (T) nodeService.getProperty(nodeRef1, sortFieldQName);
                T obj2 = (T) nodeService.getProperty(nodeRef2, sortFieldQName);

                return sortAscending ? obj1.compareTo(obj2) : obj2.compareTo(obj1);
            }
        }
        if (members.size() > 0 && nodeService.getProperties(members.get(0)).containsKey(sortFieldQName)){
            Collections.sort(members, new NodeRefComparator<String>());
        };
        return members;
    }

    public List<NodeRef> getContractsByFilter(QName dateProperty, Date begin, Date end, List<String> paths, List<String> statuses, List<NodeRef> initiatorsList, List<NodeRef> docsList, boolean includeAdditional) {
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

        return documentService.getDocumentsByFilter(types, dateProperty, begin, end, paths, statuses, initList, docsList);
    }

	public List<NodeRef> getAllContractDocuments(NodeRef contractRef) {
		return findNodesByAssociationRef(contractRef, ASSOC_DOCUMENT, TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, ASSOCIATION_TYPE.SOURCE);
	}

    public List<NodeRef> getAdditionalDocs(String filter) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query = "";

        // формируем базовый запрос - ищем документы к договорам в папке Черновики и Документы
        query = "TYPE:\"" + TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT + "\" AND " +
                "(PATH:\"" + documentService.getDraftPath() + "//*\" OR PATH:\"" + documentService.getDocumentsFolderPath() + "//*\")";


        if (filter != null && filter.length() > 0) {
            query +=  filter ;
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

	public boolean isContract(NodeRef ref) {
		Set<QName> types = new HashSet<QName>();
		types.add(TYPE_CONTRACTS_DOCUMENT);
		return isProperType(ref, types);
	}

	public NodeRef dublicateContract(NodeRef nodeRef) {
		if (isContract(nodeRef)) {
			Map<QName, Serializable> properties = new HashMap<QName, Serializable> ();
			properties.put (PROP_SUMMARY_CONTENT, nodeService.getProperty(nodeRef, PROP_SUMMARY_CONTENT));
			properties.put (PROP_SIGNATORY_COUNTERPARTY, nodeService.getProperty(nodeRef, PROP_SIGNATORY_COUNTERPARTY));

			ChildAssociationRef createdNodeAssoc = nodeService.createNode(getDraftRoot(),
					ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
					GUID.generate()), TYPE_CONTRACTS_DOCUMENT, properties);

			if (createdNodeAssoc != null && createdNodeAssoc.getChildRef() != null) {
				NodeRef createdNode = createdNodeAssoc.getChildRef();
				nodeService.createAssociation(createdNode, findNodeByAssociationRef(nodeRef, ASSOC_CONTRACT_TYPE, null, ASSOCIATION_TYPE.TARGET), ASSOC_CONTRACT_TYPE);
				nodeService.createAssociation(createdNode, findNodeByAssociationRef(nodeRef, ASSOC_CONTRACT_PARTNER, null, ASSOCIATION_TYPE.TARGET), ASSOC_CONTRACT_PARTNER);
				nodeService.createAssociation(createdNode, findNodeByAssociationRef(nodeRef, ASSOC_CONTRACT_SUBJECT, null, ASSOCIATION_TYPE.TARGET), ASSOC_CONTRACT_SUBJECT);
				NodeRef contractRepresentative = findNodeByAssociationRef(nodeRef, ASSOC_CONTRACT_REPRESENTATIVE, null, ASSOCIATION_TYPE.TARGET);
				if (contractRepresentative != null) {
					nodeService.createAssociation(createdNode, contractRepresentative, ASSOC_CONTRACT_REPRESENTATIVE);
				}
				NodeRef currency = findNodeByAssociationRef(nodeRef, ASSOC_CONTRACT_CURRENCY, null, ASSOCIATION_TYPE.TARGET);
				if (currency != null) {
					nodeService.createAssociation(createdNode, currency, ASSOC_CONTRACT_CURRENCY);
				}
				return createdNode;
			}
		}
		return null;
	}

    public String getAuthorProperty() {
        return documentService.getAuthorProperty(TYPE_CONTRACTS_DOCUMENT);
    }
}
