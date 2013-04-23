package ru.it.lecm.contracts.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
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
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: mshafeev
 * Date: 16.04.13
 * Time: 14:16
 */
public class ContractsBeanImpl extends BaseBean {
	public static final String CONTRACTS = "Contracts";
	public static final String DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE = "onBasis";
	public static final String CONTRACTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";
	public static final String CONTRACTS_ASPECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/aspects/1.0";
	public static final String ADDITIONAL_DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/additional-document/1.0";

	public static final QName TYPE_CONTRACTS_RECORD = QName.createQName(CONTRACTS_NAMESPACE_URI, "document");
	public static final QName TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocument");
    public static final QName TYPE_CONTRACTS_START_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "startDate");
    public static final QName TYPE_CONTRACTS_END_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "endDate");

    public static final QName ASSOC_ADDITIONAL_DOCUMENT_TYPE = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocumentType");
    public static final QName ASSOC_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "document-assoc");
	public static final QName ASSOC_DELETE_REASON = QName.createQName(CONTRACTS_ASPECTS_NAMESPACE_URI, "reasonDelete-assoc");

	public static final QName ASPECT_CONTRACT_DELETED = QName.createQName(CONTRACTS_ASPECTS_NAMESPACE_URI, "deleted");

    private SearchService searchService;
	private DictionaryBean dictionaryService;
    private DocumentService documentService;
    private DocumentConnectionService documentConnectionService;
    private DocumentMembersService documentMembersService;
    private OrgstructureBean orgstructureService;

    DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

	@Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

	public DocumentService getDocumentService() {
		return documentService;
	}

	public NodeRef getDraftRoot() {
		return  documentService.getDraftRoot(CONTRACTS);
	}

	public String getDraftPath() {
		return  documentService.getDraftPath(CONTRACTS);
	}

    /**
     * Поиск договоров
     * @param path путь где следует искать
     * @param statuses статусы договоров
     * @return
     */
    public List<NodeRef> getContracts(ArrayList<String> path, ArrayList<String> statuses) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query = "";

        // формируем запрос
        query = "TYPE:\"" + TYPE_CONTRACTS_RECORD + "\"";

        if (path.size() > 0) {
            query = query + " AND (";
            for (int i = 0; i < path.size(); i++) {
                query = query + "PATH:\"" + path.get(i) + "//*\"";
                if ((i + 1) < path.size()) {
                    query = query + " OR ";
                }
            }
            query = query + ")";
        }

        if (statuses.size() > 0) {
            query = query + " AND (";
            for (int i = 0; i < statuses.size(); i++) {
                query = query + "@lecm\\-statemachine\\:status:\"" + statuses.get(i).trim() + "\"";
                if ((i + 1) < statuses.size()) {
                    query = query + " OR ";
                }
            }
            query = query + ")";
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

    /**
     * Метод получения всех участников всех договоров
     * @param path
     * @param statuses
     * @return
     */
    public List<NodeRef> getAllMembers(ArrayList<String> path, ArrayList<String> statuses) {
        Set<NodeRef> members = new HashSet<NodeRef>();

        for (NodeRef nodeRef : getContracts(path,statuses)) {
            for (NodeRef memberRef : documentMembersService.getDocumentMembers(nodeRef)) {
                for (AssociationRef employee : nodeService.getTargetAssocs(memberRef, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE)) {
                    members.add(employee.getTargetRef());
                }
            }
        }
        return new ArrayList<NodeRef>(members);
    }

	public String createDocumentOnBasis(NodeRef typeRef, NodeRef documentRef) {
		ChildAssociationRef additionalDocumentAssociationRef = nodeService.createNode(getDraftRoot(),
				ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
				GUID.generate()), TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);

		if (additionalDocumentAssociationRef != null && additionalDocumentAssociationRef.getChildRef() != null) {
			NodeRef additionalDocumentRef = additionalDocumentAssociationRef.getChildRef();
			nodeService.createAssociation(additionalDocumentRef, typeRef, ASSOC_ADDITIONAL_DOCUMENT_TYPE);
			nodeService.createAssociation(additionalDocumentRef, documentRef, ASSOC_DOCUMENT);

			NodeRef connectionType = dictionaryService.getDictionaryValueByParam(
					DocumentConnectionService.DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME,
					DocumentConnectionService.PROP_CONNECTION_TYPE_CODE,
					DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE);

			if (connectionType != null) {
				documentConnectionService.createConnection(additionalDocumentRef, documentRef, connectionType);
			}
            return additionalDocumentRef.toString();
		}
        return null;
    }

    public void appendDeleteReason(NodeRef reasonRef, NodeRef documentRef) {
	    nodeService.addAspect(documentRef, ASPECT_CONTRACT_DELETED, null);
        nodeService.createAssociation(documentRef, reasonRef, ASSOC_DELETE_REASON);
    }

    public List<NodeRef> getContractsByFilter(Date begin, Date end, List<NodeRef> employeesList, List<NodeRef> docsList) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query = "";

        // формируем базовый запрос - ищем договора и документы к договорам в папке Черновики и Документы
        query = "(TYPE:\"" + TYPE_CONTRACTS_RECORD + "\" OR TYPE:\"" + TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT + "\") AND " +
                "(PATH:\"" + documentService.getDraftPath() + "//*\" OR PATH:\"" + documentService.getDocumentsFolderPath() + "//*\")";

        final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
        final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";

        // Фильтр по датам последнего изменения статуса
        query += " AND @lecm\\-document\\:status-changed-date:[" + MIN + " TO " + MAX + "]";

        // фильтр по сотрудниками-создателям
        if (employeesList != null && !employeesList.isEmpty()) {
            boolean addOR = false;
            String employeesFilter = "";
            for (NodeRef employeeRef : employeesList) {
                String personName = orgstructureService.getEmployeeLogin(employeeRef);
                if (personName != null && !personName.isEmpty()) {
                    employeesFilter += (addOR ? " OR " : "") + "@cm\\:creator:\'" + personName + "\'";
                    addOR = true;
                }
            }
            query += " AND (" + employeesFilter + ")";
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

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

	public List<NodeRef> getAllContractDocuments(NodeRef contractRef) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		List<AssociationRef> allAdditionalDocument = nodeService.getSourceAssocs(contractRef,ASSOC_DOCUMENT);

        if (allAdditionalDocument != null && allAdditionalDocument.size() > 0) {
            for (AssociationRef document : allAdditionalDocument ) {
                result.add(document.getSourceRef());
            }
        }
		return result;
	}
}
