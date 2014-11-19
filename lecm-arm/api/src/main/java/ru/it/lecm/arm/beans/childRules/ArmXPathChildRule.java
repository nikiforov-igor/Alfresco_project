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

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 18.11.2014
 * Time: 17:29
 */
public class ArmXPathChildRule extends ArmBaseChildRule {
	private String rootXPath;
	private List<String> types;
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

	@Override
	public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
		List<ArmNode> nodes = new ArrayList<>();
		//шаблонный запрос из верхнего узла
		if (rootXPath != null) {
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

			String query = "PATH:\"" + rootXPath + "\" AND (ISNULL:\"lecm\\-dic:active\" OR lecm\\-dic:active:true)";
			if (types != null && types.size() > 0) {
				query += " AND (";
				for (int i = 0; i < types.size(); i++) {
					query += "TYPE:\"" + types.get(i).trim() + "\"";
					if (i < types.size() - 1) {
						query += " OR ";
					}
				}
				query += ")";
			}

			sp.setQuery(query);
			sp.addSort("@" + ContentModel.PROP_NAME, true);

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

	@Override
	public List<NodeRef> getChildren(NodeRef node) {
		List<NodeRef> nodes = new ArrayList<>();
		//шаблонный запрос из верхнего узла
		if (node != null) {
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

			String query = "PARENT:\"" + node.toString() + "\" AND (ISNULL:\"lecm\\-dic:active\" OR lecm\\-dic:active:true)";
			if (types != null && types.size() > 0) {
				query += " AND (";
				for (int i = 0; i < types.size(); i++) {
					query += "TYPE:\"" + types.get(i).trim() + "\"";
					if (i < types.size() - 1) {
						query += " OR ";
					}
				}
				query += ")";
			}

			sp.setQuery(query);
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
