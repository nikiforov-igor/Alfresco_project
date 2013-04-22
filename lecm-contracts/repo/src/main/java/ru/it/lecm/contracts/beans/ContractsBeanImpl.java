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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: mshafeev
 * Date: 16.04.13
 * Time: 14:16
 */
public class ContractsBeanImpl extends BaseBean {
	public static final String CONTRACTS = "Contracts";
	public static final String DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE = "onBasis";
	public static final String CONTRACTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";
	public static final String ADDITIONAL_DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/additional-document/1.0";

	public static final QName TYPE_CONTRACTS_RECORD = QName.createQName(CONTRACTS_NAMESPACE_URI, "document");
	public static final QName TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocument");
	public static final QName ASSOC_ADDITIONAL_DOCUMENT_TYPE = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocumentType");
	public static final QName ASSOC_DELETE_REASON = QName.createQName(CONTRACTS_NAMESPACE_URI, "reasonDelete-assoc");

    private SearchService searchService;
	private DictionaryBean dictionaryService;
    private DocumentService documentService;
    private DocumentConnectionService documentConnectionService;
    private DocumentMembersService documentMembersService;

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

	public void createDocumentOnBasis(NodeRef typeRef, NodeRef documentRef) {
		ChildAssociationRef additionalDocumentAssociationRef = nodeService.createNode(getDraftRoot(),
				ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
				GUID.generate()), TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);

		if (additionalDocumentAssociationRef != null && additionalDocumentAssociationRef.getChildRef() != null) {
			NodeRef additionalDocumentRef = additionalDocumentAssociationRef.getChildRef();
			nodeService.createAssociation(additionalDocumentRef, typeRef, ASSOC_ADDITIONAL_DOCUMENT_TYPE);

			NodeRef connectionType = dictionaryService.getDictionaryValueByParam(
					DocumentConnectionService.DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME,
					DocumentConnectionService.PROP_CONNECTION_TYPE_CODE,
					DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE);

			if (connectionType != null) {
				documentConnectionService.createConnection(additionalDocumentRef, documentRef, connectionType);
			}
		}
	}

    public void appendDeleteReason(NodeRef reasonRef, NodeRef documentRef) {
        nodeService.createAssociation(documentRef, reasonRef, ASSOC_DELETE_REASON);
    }
}
