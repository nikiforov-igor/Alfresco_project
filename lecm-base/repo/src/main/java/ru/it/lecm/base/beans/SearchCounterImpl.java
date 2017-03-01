/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.beans;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ikhalikov
 */
public class SearchCounterImpl implements SearchCounter {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchCounterImpl.class);

    private SearchService searchService;
    private StoreRef storeRef;
    private SearchQueryProcessorService processorService;

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setStoreRef(StoreRef storeRef) {
		this.storeRef = storeRef;
	}

	public void setProcessorService(SearchQueryProcessorService processorService) {
		this.processorService = processorService;
	}

	@Override
	public StoreRef getStoreRef() {
		return storeRef;
	}
	
	@Override
	public Long query(String query, boolean useFilterByOrg, boolean onlyInSameOrg) {
		SearchParameters sp = new SearchParameters();
		
		sp.addStore(this.storeRef);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		
		if (useFilterByOrg) {
            query += " AND {{IN_SAME_ORGANIZATION({strict:" + onlyInSameOrg + "})}} ";
        }
		
		query = processorService.processQuery(query);
		
		sp.setQuery(query);
		sp.setLimit(Integer.MAX_VALUE);
		
		return query(sp, false, -1, 0);
	}

	@Override
	public Long query(SearchParameters sp, boolean exceptionOnError, int maxResults, int skipCount) {
		// perform the search against the repo
        ResultSet results = null;
        sp.setLimitBy(LimitBy.FINAL_SIZE);
        try {
            sp.setMaxItems(0);
            sp.setSkipCount(0);

            results = searchService.query(sp);
            if (results instanceof SolrJSONResultSet) {
                return ((SolrJSONResultSet) results).getNumberFound();
            } else {
                sp.setMaxItems(maxResults);
                sp.setSkipCount(skipCount);
                results = searchService.query(sp);
                return (long) results.length();
            }
        } catch (Exception err) {
            if (exceptionOnError) {
                throw new AlfrescoRuntimeException("Failed to execute search: " + sp.getQuery(), err);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Failed to execute search: " + sp.getQuery(), err);
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }

        return 0L;
	}

	@Override
	public boolean hasChildren(String parentRef, String childType) {
		return hasChildren(parentRef, childType, false);
	}

	@Override
	public boolean hasChildren(String parentRef, String childType, boolean activeOnly) {
		return  hasChildren(parentRef, childType, null, activeOnly);
	}

	@Override
	public boolean hasChildren(String parentRef, String childType, String additionalQuery) {
		return hasChildren(parentRef, childType, additionalQuery, false);
	}

	@Override
	public boolean hasChildren(String parentRef, String childType, String additionalQuery, boolean activeOnly) {
		StringBuilder queryBuffer = new StringBuilder();

        if (additionalQuery != null && !additionalQuery.isEmpty()) {
            // обработка спец-выражений
            additionalQuery = processorService.processQuery(additionalQuery);

            queryBuffer.append("(").append(additionalQuery).append(")");
        }

        if (queryBuffer.length() > 0) {
            queryBuffer.append(" AND (");
        } else {
            queryBuffer.append("(");
        }

        //родитель
        queryBuffer.append("PARENT:\"").append(parentRef).append("\"");

        if (childType != null && !childType.isEmpty()) {
            StringBuilder typesQuery = new StringBuilder();
            String[] types = childType.split(",");
            for (String type : types) {
                if (typesQuery.length() > 0) {
                    typesQuery.append(" OR ");
                }
                typesQuery.append("TYPE:\"").append(type).append("\"");
            }
            if (typesQuery.length() > 0) {
                queryBuffer.append(" AND (").append(typesQuery.toString()).append(")");
            }
        }
        queryBuffer.append(" AND NOT @lecm\\-dic\\:active:false");
        queryBuffer.append(")");


        SearchParameters sp = new SearchParameters();
        sp.addStore(this.storeRef);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

        sp.setQuery(queryBuffer.toString());

        logger.debug("Has Children queryBuffer: " + queryBuffer);

        // execute search based on search definition
        Long count = query(sp, true, -1, 0);

        return count > 0;
	}
	
}
