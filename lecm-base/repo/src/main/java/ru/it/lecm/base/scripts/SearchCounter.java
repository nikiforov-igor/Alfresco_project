package ru.it.lecm.base.scripts;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 07.10.13
 * Time: 15:45
 */
public class SearchCounter extends BaseScopableProcessorExtension {

    private static Log logger = LogFactory.getLog(SearchCounter.class);

    protected ServiceRegistry services;
    protected StoreRef storeRef;

    public void setServiceRegistry(ServiceRegistry services) {
        this.services = services;
    }

    public void setStoreUrl(String storeRef) {
        // ensure this is not set again by a script instance!
        if (this.storeRef != null) {
            throw new IllegalStateException("Default store URL can only be set once.");
        }
        this.storeRef = new StoreRef(storeRef);
    }

    public Long query(Object search) {
        Long result = 0L;

        if (search instanceof Serializable) {
            Serializable obj = new ValueConverter().convertValueForRepo((Serializable) search);
            if (obj instanceof Map) {
                Map<Serializable, Serializable> def = (Map<Serializable, Serializable>) obj;

                // test for mandatory values
                String query = (String) def.get("query");
                if (query == null || query.length() == 0) {
                    throw new AlfrescoRuntimeException("Failed to search: Missing mandatory 'query' value.");
                }

                // collect optional values
                String store = (String) def.get("store");
                String language = (String) def.get("language");
                String namespace = (String) def.get("namespace");
                String onerror = (String) def.get("onerror");

                // Выставляем в 0 - чтобы получать только общее число записей
                int maxResults = 0;
                int skipResults = 0;

                SearchParameters sp = new SearchParameters();
                sp.addStore(store != null ? new StoreRef(store) : this.storeRef);
                sp.setLanguage(language != null ? language : SearchService.LANGUAGE_LUCENE);
                sp.setQuery(query);

                if (namespace != null) {
                    sp.setNamespace(namespace);
                }
                sp.setMaxItems(maxResults);
                sp.setLimitBy(LimitBy.FINAL_SIZE);

                sp.setSkipCount(skipResults);
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
                result = query(sp, exceptionOnError);
            }
        }

        return result;
    }

    protected Long query(SearchParameters sp, boolean exceptionOnError) {
        // perform the search against the repo
        ResultSet results = null;
        try {
            results = this.services.getSearchService().query(sp);
            if (results instanceof SolrJSONResultSet) {
                return ((SolrJSONResultSet) results).getNumberFound();
            } else {
                return (long) results.length();
            }
        } catch (Throwable err) {
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
}
