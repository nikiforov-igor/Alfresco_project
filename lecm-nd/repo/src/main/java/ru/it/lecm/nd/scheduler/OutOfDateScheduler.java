/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.nd.scheduler;

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
import ru.it.lecm.nd.api.NDDocumentService;
import ru.it.lecm.nd.api.NDModel;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author ikhalikov
 */
public class OutOfDateScheduler extends BaseTransactionalSchedule {

	private NDDocumentService ndDocumentService;
	private final static Logger logger = LoggerFactory.getLogger(OutOfDateScheduler.class);

	public void setNdDocumentService(NDDocumentService ndDocumentService) {
		this.ndDocumentService = ndDocumentService;
	}

	public OutOfDateScheduler() {
		super();
	}

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery;
		if (!NDModel.ND_STATUS.PUT_IN_WORK.getHistoryValue().equals(ndDocumentService.getNDStatusName(NDModel.ND_STATUS.PUT_IN_WORK))) {
			String extendSearchQueryFormat = "TYPE:\"%1$s\" AND @%2$s:[MIN TO NOW] AND (@%3$s:\"%4$s\" @%3$s:\"%5$s\")";
			searchQuery = String.format(extendSearchQueryFormat, NDModel.TYPE_ND.toString(), NDModel.PROP_ND_END, StatemachineModel.PROP_STATUS, ndDocumentService.getNDStatusName(NDModel.ND_STATUS.ACTIVE), NDModel.ND_STATUS.PUT_IN_WORK.getHistoryValue());
		} else {
			String simpleSearchQueryFormat = "TYPE:\"%s\" AND @%s:[MIN TO NOW] AND @%s:\"%s\"";
			searchQuery = String.format(simpleSearchQueryFormat, NDModel.TYPE_ND.toString(), NDModel.PROP_ND_END, StatemachineModel.PROP_STATUS, ndDocumentService.getNDStatusName(NDModel.ND_STATUS.ACTIVE));
		}

		sp.setQuery(searchQuery.replaceAll("-", "\\\\-"));
		ResultSet results = null;
		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				nodes.add(currentNodeRef);
			}
		} catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.error(ex.getMessage(), ex);
			} else {
				logger.error(ex.getMessage());
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return nodes;
	}

}
