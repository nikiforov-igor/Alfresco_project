package ru.it.lecm.arm.beans.childRules;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
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
    public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
        List<ArmNode> nodes = new ArrayList<ArmNode>();

        if (listQuery != null) {
            SearchParameters sp = new SearchParameters();
            sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

            String processedQuery = processorService.processQuery(listQuery);
            sp.setQuery(processedQuery);
            sp.addSort("@" + ContentModel.PROP_NAME, true);

            ResultSet results = null;
            try {
                results = searchService.query(sp);
                for (ResultSetRow row : results) {
                    ArmNode rowNode = service.wrapAnyNodeAsObject(row.getNodeRef(), node, getSubstituteString());
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

    @Override
    public List<NodeRef> getChildren(NodeRef node) {
        return null;
    }
}
