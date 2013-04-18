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

	public NodeRef getDraftRoot() {
		return  documentService.getDraftRoot(CONTRACTS);
	}

	public String getDraftPath() {
		return  documentService.getDraftPath(CONTRACTS);
	}

    public List<NodeRef> getContracts(String filter) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        if (filter != null && !filter.equals("")) {
            sp.setQuery("TYPE:\"" + TYPE_CONTRACTS_RECORD + "\"" + filter);
        } else {
            sp.setQuery("TYPE:\"" + TYPE_CONTRACTS_RECORD + "\"");
        }
        ResultSet results = null;

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

	public void createDocumentOnBasis(String typeNodeRef, String packageNodeRef) {
		if (typeNodeRef != null && packageNodeRef != null) {
			NodeRef typeRef = new NodeRef(typeNodeRef);
			NodeRef packageRef = new NodeRef(packageNodeRef);
			if (nodeService.exists(typeRef) && nodeService.exists(packageRef)) {
				NodeRef documentRef = null;
				List<ChildAssociationRef> packageAssocs = nodeService.getChildAssocs(packageRef);
				if (packageAssocs != null && packageAssocs.size() == 1) {
					documentRef = packageAssocs.get(0).getChildRef();
				}
				if (documentRef != null && documentService.isDocument(documentRef)) {
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
			}
		}
	}

    public void appendDeleteReason(String reasonNodeRef, String packageNodeRef) {
        if (reasonNodeRef != null && packageNodeRef != null) {
            NodeRef reasonRef = new NodeRef(reasonNodeRef);
            NodeRef packageRef = new NodeRef(packageNodeRef);
            if (nodeService.exists(reasonRef) && nodeService.exists(packageRef)) {
                NodeRef documentRef = null;
                List<ChildAssociationRef> packageAssocs = nodeService.getChildAssocs(packageRef);
                if (packageAssocs != null && packageAssocs.size() == 1) {
                    documentRef = packageAssocs.get(0).getChildRef();
                }
                if (documentRef != null && documentService.isDocument(documentRef)) {
                    nodeService.createAssociation(documentRef, reasonRef, ASSOC_DELETE_REASON);
                }
            }
        }
    }

}
