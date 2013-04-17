package ru.it.lecm.contracts.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mshafeev
 * Date: 16.04.13
 * Time: 14:16
 */
public class ContractsBeanImpl extends BaseBean {

    private SearchService searchService;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    String CONTRACTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";

    QName TYPE_CONTRACTS_RECORD = QName.createQName(CONTRACTS_NAMESPACE_URI, "document");

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public List<NodeRef> getTotalContracts() {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        sp.setQuery("TYPE:\"" + TYPE_CONTRACTS_RECORD + "\"" + " -@lecm\\-statemachine\\:status:\"" + "Черновик" + "\"");
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
		System.out.println("2. Create Document On The Basis typeNodeRef=" + typeNodeRef + ", packageNodeRef=" + packageNodeRef);

	}
}
