package ru.it.lecm.arm.beans.childRules;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.search.ArmChildrenRequest;
import ru.it.lecm.arm.beans.search.ArmChildrenResponse;
import ru.it.lecm.base.beans.SearchQueryProcessorService;

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
    private SearchQueryProcessorService processorService;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setProcessorService(SearchQueryProcessorService processorService) {
        this.processorService = processorService;
    }

    @Override
    public ArmChildrenResponse build(ArmWrapperService service, ArmNode node, ArmChildrenRequest request) {
        List<ArmNode> nodes = new ArrayList<>();
        long totalChildren = -1;

        if (listQuery != null) {
            SearchParameters sp = new SearchParameters();
            sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
            String queryString = service.formatQuery(listQuery, request.getNodeRef());

            String searchQuery = getSearchQuery(request.getSearchTerm());
            if (StringUtils.isNotEmpty(searchQuery)) {
                queryString += " AND " + searchQuery;
            }

            String processedQuery = processorService.processQuery(queryString);
            sp.setQuery(processedQuery);

            if (request.getMaxItems() != -1) {
                sp.setSkipCount(request.getSkipCount());
                sp.setMaxItems(request.getMaxItems());
            }

            addSort(sp);

            ResultSet results = null;
            try {
                results = searchService.query(sp);
                for (ResultSetRow row : results) {
                    ArmNode rowNode = service.wrapAnyNodeAsObject(row.getNodeRef(), node, getSubstituteString());
                    nodes.add(rowNode);
                }
                totalChildren = results.getNumberFound();
            } finally {
                if (results != null) {
                    results.close();
                }
            }
        }
        return new ArmChildrenResponse(nodes, totalChildren == -1 ? nodes.size() : totalChildren);
    }

    @Override
    public List<NodeRef> getChildren(NodeRef node) {
        return null;
    }
}
