package ru.it.lecm.ord.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import ru.it.lecm.ord.api.ORDDocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author dbayandin
 */
public class ORDNotificationScheduler extends BaseTransactionalSchedule {

	private ORDDocumentService ordDocumentService;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy\\-M\\-dd'T'HH");

	private final static Logger logger = LoggerFactory.getLogger(ORDNotificationScheduler.class);

	public void setOrdDocumentService(ORDDocumentService ordDocumentService) {
		this.ordDocumentService = ordDocumentService;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public ORDNotificationScheduler() {
		super();
	}

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery;
		if (!ORDModel.ORD_STATUS.EXECUTION.getHistoryValue().equals(ordDocumentService.getOrdStatusName(ORDModel.ORD_STATUS.EXECUTION))) {
			String extendSearchQueryFormat = "TYPE:\"%1$s\" AND (=@%2$s:\"%3$s\" =@%2$s:\"%4$s\")";
			searchQuery = String.format(extendSearchQueryFormat, ORDModel.TYPE_ORD.toString(), StatemachineModel.PROP_STATUS, ordDocumentService.getOrdStatusName(ORDModel.ORD_STATUS.EXECUTION), ORDModel.ORD_STATUS.EXECUTION.getHistoryValue());
		} else {
			String simpleSearchQueryFormat = "TYPE:\"%s\" AND =@%s:\"%s\"";
			searchQuery = String.format(simpleSearchQueryFormat, ORDModel.TYPE_ORD.toString(), StatemachineModel.PROP_STATUS, ordDocumentService.getOrdStatusName(ORDModel.ORD_STATUS.EXECUTION));
		}
		sp.setQuery(searchQuery.replaceAll("-", "\\\\-"));
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
}
