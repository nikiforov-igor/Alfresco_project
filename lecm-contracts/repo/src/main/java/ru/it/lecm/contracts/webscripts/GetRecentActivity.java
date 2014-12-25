package ru.it.lecm.contracts.webscripts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;

/**
 *
 * @author vlevin
 */
public class GetRecentActivity extends DeclarativeWebScript {

	private BusinessJournalService businessJournalService;
	private SearchService searchService;
	private int maxDays;
	private int maxRecords;

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setMaxDays(String maxDaysStr) {
		try {
			maxDays = Integer.parseInt(maxDaysStr);
		} catch (NumberFormatException e) {
			maxDays = 30;
		}
	}

	public void setMaxRecords(String maxRecordsStr) {
		try {
			maxRecords = Integer.parseInt(maxRecordsStr);
		} catch (NumberFormatException e) {
			maxRecords = 1000;
		}
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		final Map<String, Object> result = new HashMap<>();
		final JSONArray json = new JSONArray();
		result.put("result", json);

		final List<BusinessJournalRecord> bjRecords = new ArrayList<>();
		final List<BusinessJournalRecord> bjRecordsResult;

		final DateFormat solrDateFormat = new SimpleDateFormat("yyyy\\-MM\\-dd");
		final DateFormat bjDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		final DateFormat jsonDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		final List<NodeRef> desiredDocuments = new ArrayList<>();

		final Date now = new Date();
		final Date minDate = DateUtils.addDays(now, -1 * maxDays);

		final SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

		final String searchQuery = String.format("+TYPE:\"%s\" OR +TYPE:\"%s\" AND (NOT @lecm\\-statemachine\\-aspects\\:is\\-final:true OR (@lecm\\-statemachine\\-aspects\\:is\\-final:true AND %s:[%s TO %s]))",
				ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT, ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT,
				ContentModel.PROP_MODIFIED, solrDateFormat.format(minDate), solrDateFormat.format(now));

		sp.setQuery(searchQuery);
		ResultSet results = null;

		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				desiredDocuments.add(currentNodeRef);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}

		for (NodeRef document : desiredDocuments) {
			Map<BusinessJournalRecord.Field, String> filter = new HashMap<>();

			filter.put(BusinessJournalRecord.Field.MAIN_OBJECT, document.toString());
			filter.put(BusinessJournalRecord.Field.DATE, bjDateFormat.format(minDate) + "\\|" + bjDateFormat.format(now));

			bjRecords.addAll(businessJournalService.getRecords(BusinessJournalRecord.Field.DATE, false, 0, 1000, filter, true, false));
		}

		Collections.sort(bjRecords, new Comparator<BusinessJournalRecord>() {

			@Override
			public int compare(BusinessJournalRecord first, BusinessJournalRecord second) {
				// сначала новые
				return -1 * first.getDate().compareTo(second.getDate());
			}
		});

		if (bjRecords.size() > maxRecords) {
			bjRecordsResult = bjRecords.subList(0, maxRecords - 1);
		} else {
			bjRecordsResult = bjRecords;
		}

		for (BusinessJournalRecord record : bjRecordsResult) {
			Map<String, String> object = new HashMap<>();
			object.put("date", jsonDateFormat.format(record.getDate()));
			object.put("record", record.getRecordDescription());
			object.put("initiator", record.getInitiatorText() != null ? record.getInitiatorText() : "");
			object.put("initiatorRef", record.getInitiator() != null ? record.getInitiator().toString() : "");

			json.put(new JSONObject(object));
		}

		return result;
	}

}
