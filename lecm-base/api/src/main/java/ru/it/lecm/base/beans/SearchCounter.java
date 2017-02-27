package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;

/**
 *
 * @author ikhalikov
 */
public interface SearchCounter {
	
	Long query(SearchParameters sp, boolean exceptionOnError, int maxResults, int skipCount);
	Long query(String query, boolean useFilterByOrg, boolean onlyInSameOrg);
	
	boolean hasChildren(String parentRef, String childType);
	boolean hasChildren(String parentRef, String childType, boolean activeOnly);
	boolean hasChildren(String parentRef, String childType, String additionalQuery);
	boolean hasChildren(String parentRef, String childType, String additionalQuery, boolean activeOnly);
	
	StoreRef getStoreRef();
}
