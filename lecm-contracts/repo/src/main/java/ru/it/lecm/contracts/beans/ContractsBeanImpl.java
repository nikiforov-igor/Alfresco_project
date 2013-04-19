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
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mshafeev
 * Date: 16.04.13
 * Time: 14:16
 */
public class ContractsBeanImpl extends BaseBean {
	public static final String CONTRACTS = "Contracts";
	public static final String DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_NAME = "На основании";
	public static final String CONTRACTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";

	public static final QName TYPE_CONTRACTS_RECORD = QName.createQName(CONTRACTS_NAMESPACE_URI, "document");
	public static final QName TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT = QName.createQName(CONTRACTS_NAMESPACE_URI, "additionalDocument");
	public static final QName ASSOC_ADDITIONAL_DOCUMENT_TYPE = QName.createQName(CONTRACTS_NAMESPACE_URI, "additionalDocumentType");
	public static final QName ASSOC_DELETE_REASON = QName.createQName(CONTRACTS_NAMESPACE_URI, "reasonDelete-assoc");

    private SearchService searchService;
	private DictionaryBean dictionaryService;
    private DocumentService documentService;
    private DocumentConnectionService documentConnectionService;

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

    public List<NodeRef> getContracts(String type, ArrayList<String> path, ArrayList<String> properties) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query = "";

        // формируем запрос
        type = type.replaceAll("([:])", "\\:");
        type = type.replaceAll("([-])", "\\-");
        query = "TYPE:\"" + type + "\"";

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

        if (properties.size() > 0) {
            query = query + " AND (";
            for (int i = 0; i < properties.size(); i++) {
                query = query + "@lecm\\-statemachine\\:status:\"" + properties.get(i) + "\"";
                if ((i + 1) < properties.size()) {
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

	public void createDocumentOnBasis(NodeRef typeRef, NodeRef documentRef) {
		ChildAssociationRef additionalDocumentAssociationRef = nodeService.createNode(getDraftRoot(), ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()), TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);
		if (additionalDocumentAssociationRef != null && additionalDocumentAssociationRef.getChildRef() != null) {
			NodeRef additionalDocumentRef = additionalDocumentAssociationRef.getChildRef();
			nodeService.createAssociation(additionalDocumentRef, typeRef, ASSOC_ADDITIONAL_DOCUMENT_TYPE);

			NodeRef connectionType = dictionaryService.getDictionaryValueByName(DocumentConnectionService.DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME, DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_NAME);
			if (connectionType != null) {
				documentConnectionService.createConnection(additionalDocumentRef, documentRef, connectionType);
			}
		}
	}

    public void appendDeleteReason(NodeRef reasonRef, NodeRef documentRef) {
        nodeService.createAssociation(documentRef, reasonRef, ASSOC_DELETE_REASON);
    }
}
