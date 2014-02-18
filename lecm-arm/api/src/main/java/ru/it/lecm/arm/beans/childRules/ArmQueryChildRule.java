package ru.it.lecm.arm.beans.childRules;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 9:59
 */
public class ArmQueryChildRule extends ArmBaseChildRule {
	private String listQuery;

	public void setListQuery(String listQuery) {
		this.listQuery = listQuery;
	}

    private SearchService searchService;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
        List<ArmNode> nodes = new ArrayList<ArmNode>();

        if (listQuery != null) {
            SearchParameters sp = new SearchParameters();
            sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

            sp.setQuery(listQuery);

            ResultSet results = null;
            try {
                results = searchService.query(sp);
                for (ResultSetRow row : results) {
                    ArmNode rowNode = service.wrapAnyNodeAsObject(row.getNodeRef(), node);
                    nodes.add(rowNode);
                }
            } finally {
                if (results != null) {
                    results.close();
                }
            }
        }

        return nodes;
    }
}
