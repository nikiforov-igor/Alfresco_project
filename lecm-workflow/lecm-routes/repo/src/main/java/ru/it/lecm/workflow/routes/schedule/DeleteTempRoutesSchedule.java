package ru.it.lecm.workflow.routes.schedule;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.workflow.routes.api.RoutesModel;

/**
 *
 * @author vlevin
 */
public class DeleteTempRoutesSchedule extends BaseTransactionalSchedule {

	private final String searchQueryFormat = "(+TYPE:\"%s\" OR +TYPE:\"%s\") AND (+ASPECT:\"sys:temporary\" OR +ASPECT:\"lecm-workflow:temp\")";
	private final static Logger logger = LoggerFactory.getLogger(DeleteTempRoutesSchedule.class);

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, RoutesModel.TYPE_ROUTE, RoutesModel.TYPE_STAGE);
		logger.trace("Searching temp routes to be deleted: " + searchQuery);
		sp.setQuery(searchQuery);
		ResultSet results = null;
		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				nodes.add(currentNodeRef);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return nodes;
	}

	public DeleteTempRoutesSchedule() {
		super();
	}

}
