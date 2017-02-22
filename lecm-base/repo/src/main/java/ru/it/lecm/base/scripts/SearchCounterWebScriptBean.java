/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.scripts;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Scriptable;

import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.SearchCounter;
import ru.it.lecm.base.beans.SearchQueryProcessorService;

/**
 *
 * @author ikhalikov
 */
public class SearchCounterWebScriptBean extends BaseWebScript {
	
	private static Log logger = LogFactory.getLog(SearchCounterWebScriptBean.class);
	
	private SearchCounter searchCounter;
	private SearchQueryProcessorService processorService;

	public void setSearchCounter(SearchCounter searchCounter) {
		this.searchCounter = searchCounter;
	}

	public void setProcessorService(SearchQueryProcessorService processorService) {
		this.processorService = processorService;
	}
	
	public Long query(Scriptable search) {
		return query(search, true);
	}
		
	public Long query(Scriptable search, boolean useFilterByOrg) {
		return query(search, useFilterByOrg, false);
	}

	public Long query(Scriptable search, boolean useFilterByOrg, boolean onlyInSameOrg) {
		Long result;
        // test for mandatory values
		Map<String, Object> def = (Map<String, Object>) getValueConverter().convertValueForJava(search);
		
		String query = (String) def.get("query");
        if (query == null || query.length() == 0) {
            throw new AlfrescoRuntimeException("Failed to search: Missing mandatory 'query' value.");
        }

        // collect optional values
        String store = (String) def.get("store");
        String language = (String) def.get("language");
        String namespace = (String) def.get("namespace");
        String onerror = (String) def.get("onerror");
        Map<Serializable, Serializable> page = (Map<Serializable, Serializable>) def.get("page");

        // Выставляем в 0 - чтобы получать только общее число записей
        int maxResults = -1;
        int skipResults = 0;

        if (page != null) {
            if (page.get("maxItems") != null) {
                Object maxItems = page.get("maxItems");
                if (maxItems instanceof Number) {
                    maxResults = ((Number) maxItems).intValue();
                } else if (maxItems instanceof String) {
                    maxResults = Integer.parseInt((String) maxItems);
                }
            }
            if (page.get("skipCount") != null) {
                Object skipCount = page.get("skipCount");
                if (skipCount instanceof Number) {
                    skipResults = ((Number) page.get("skipCount")).intValue();
                } else if (skipCount instanceof String) {
                    skipResults = Integer.parseInt((String) skipCount);
                }
            }
        }

        SearchParameters sp = new SearchParameters();
        sp.addStore(store != null ? new StoreRef(store) : searchCounter.getStoreRef());
        sp.setLanguage(language != null ? language : SearchService.LANGUAGE_LUCENE);

        if (useFilterByOrg) {
            query = query + " AND {{IN_SAME_ORGANIZATION({strict:" + onlyInSameOrg + "})}} ";
        }

        query = processorService.processQuery(query);

        sp.setQuery(query);
        if (namespace != null) {
            sp.setNamespace(namespace);
        }

        sp.setLimit(Integer.MAX_VALUE);
        if (skipResults > 0) {
            sp.setSkipCount(skipResults);
        }
        // error handling opions
        boolean exceptionOnError = true;
        if (onerror != null) {
            if (onerror.equals("exception")) {
                // default value, do nothing
            } else if (onerror.equals("no-results")) {
                exceptionOnError = false;
            } else {
                throw new AlfrescoRuntimeException("Failed to search: Unknown value supplied for 'onerror': " + onerror);
            }
        }

        // execute search based on search definition        
		result = searchCounter.query(sp, exceptionOnError, maxResults, skipResults);

        logger.debug("COUNTER QUERY: " + query + ",/n RESULT:" + result);

        return result;
	}
	
	public boolean hasChildren(String parentRef, String childType) {
		return searchCounter.hasChildren(parentRef, childType);
	}
	
	public boolean hasChildren(String parentRef, String childType, boolean activeOnly) {
		return searchCounter.hasChildren(parentRef, childType, activeOnly);
	}
	
	public boolean hasChildren(String parentRef, String childType, Object additionalQueryObj) {		
		return searchCounter.hasChildren(parentRef, childType, getAdditionalQuery(additionalQueryObj));
	}
	
	public boolean hasChildren(String parentRef, String childType, Object additionalQueryObj, boolean activeOnly) {
		return searchCounter.hasChildren(parentRef, childType, getAdditionalQuery(additionalQueryObj), activeOnly);
	}
	
	private String getAdditionalQuery(Object additionalQueryObj) {
		Object subRes = getValueConverter().convertValueForJava(additionalQueryObj);
		
		if (subRes instanceof String) {
			return (String) subRes;
		} else if (subRes instanceof Map) {
			Map<String, Object> properties = (Map<String, Object>) subRes;
			return (String) properties.get("queryBuffer");
		}
		
		return null;
	}
	
}
