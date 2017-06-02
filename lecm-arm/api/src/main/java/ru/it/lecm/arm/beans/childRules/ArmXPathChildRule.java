package ru.it.lecm.arm.beans.childRules;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.search.ArmChildrenRequest;
import ru.it.lecm.arm.beans.search.ArmChildrenResponse;
import ru.it.lecm.base.beans.SearchQueryProcessorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: AIvkin
 * Date: 18.11.2014
 * Time: 17:29
 */
public class ArmXPathChildRule extends ArmBaseChildRule {
	private String rootXPath;
	private List<String> types;
	private String filter;
	private SearchQueryProcessorService processorService;

	protected NodeService nodeService;
	private SearchService searchService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public String getRootXPath() {
		return rootXPath;
	}

	public void setRootXPath(String rootXPath) {
		this.rootXPath = rootXPath;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void setProcessorService(SearchQueryProcessorService processorService) {
		this.processorService = processorService;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public ArmChildrenResponse build(ArmWrapperService service, ArmNode node, ArmChildrenRequest request) {
		List<ArmNode> nodes = new ArrayList<>();
		long totalChildren = -1;
		//шаблонный запрос из верхнего узла
		if (rootXPath != null) {
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

			StringBuilder query = new StringBuilder().append("PATH:\"").append(rootXPath).append("\" AND NOT @lecm\\-dic\\:active:false");
			if (types != null && types.size() > 0) {
				query.append(" AND (");
				for (int i = 0; i < types.size(); i++) {
					query.append("TYPE:\"").append(types.get(i).trim()).append("\"");
					if (i < types.size() - 1) {
						query.append(" OR ");
					}
				}
				query.append(")");
			}

			if(filter != null && !filter.isEmpty()) {
				query.append(" AND (").append(filter).append(")");
			}

			String preparedSearchTerm = request.getSearchTerm();
			if (preparedSearchTerm != null && preparedSearchTerm.length() > 0) {
				if (!preparedSearchTerm.contains("*")) {
					preparedSearchTerm = "*" + preparedSearchTerm + "*";
				}
				query.append(" AND @cm\\:name:\"" + preparedSearchTerm + "\"");
			}

			String processedQuery = processorService.processQuery(query.toString());
			sp.setQuery(processedQuery);

			if (request.getMaxItems() != -1) {
				totalChildren = searchCounter.query(sp, false, 0, 0);
				sp.setSkipCount(request.getSkipCount());
				sp.setMaxItems(request.getMaxItems());
			}

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

			Collections.sort(nodes, new Comparator<ArmNode>() {
				@Override
				public int compare(ArmNode o1, ArmNode o2) {
					return o1.getTitle().toUpperCase().compareTo(o2.getTitle().toUpperCase());
				}
			});
		}
		return new ArmChildrenResponse(nodes, totalChildren == -1 ? nodes.size() : totalChildren);
	}

	@Override
	public List<NodeRef> getChildren(NodeRef node) {
		List<NodeRef> nodes = new ArrayList<>();
		//шаблонный запрос из верхнего узла
		if (node != null) {
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

			StringBuilder query = new StringBuilder().append("PARENT:\"").append(node.toString()).append("\" AND NOT @lecm\\-dic:active:false");
			if (types != null && types.size() > 0) {
				query.append(" AND (");
				for (int i = 0; i < types.size(); i++) {
					query.append("TYPE:\"").append(types.get(i).trim()).append("\"");
					if (i < types.size() - 1) {
						query.append(" OR ");
					}
				}
				query.append(")");
			}

			if(filter != null && !filter.isEmpty()) {
				query.append(" AND (").append(filter).append(")");
			}

			String processedQuery = processorService.processQuery(query.toString());

			sp.setQuery(processedQuery);
			sp.addSort("@" + ContentModel.PROP_NAME, true);

			ResultSet results = null;
			try {
				results = searchService.query(sp);
				for (ResultSetRow row : results) {
					nodes.add(row.getNodeRef());
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
