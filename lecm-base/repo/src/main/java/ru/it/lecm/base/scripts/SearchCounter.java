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
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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
    private OrgstructureBean orgstructureService;
    private NotificationsService notificationsService;
    private IWorkCalendar workCalendarService;

    public void setServiceRegistry(ServiceRegistry services) {
        this.services = services;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setWorkCalendarService(IWorkCalendar workCalendarService) {
        this.workCalendarService = workCalendarService;
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
                sp.addStore(store != null ? new StoreRef(store) : this.storeRef);
                sp.setLanguage(language != null ? language : SearchService.LANGUAGE_LUCENE);


                // обработка спец-выражений
                if (query.contains("#current-user")) {
                    query = query.replaceAll("#current-user", orgstructureService.getCurrentEmployee().toString());
                }
                if (query.contains("#current-date")) {
                    int limitDays = notificationsService.getSettingsNDays();
                    Date nextWorkDate = workCalendarService.getNextWorkingDate(new Date(), limitDays, Calendar.DAY_OF_MONTH);
                    query = query.replaceAll("#current-date", DocumentService.DateFormatISO8601.format(nextWorkDate));
                }

                sp.setQuery(query);
                if (namespace != null) {
                    sp.setNamespace(namespace);
                }

                if (maxResults > 0) {
                    sp.setLimit(maxResults);
                    sp.setLimitBy(LimitBy.FINAL_SIZE);
                }
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
                result = query(sp, exceptionOnError, maxResults, skipResults);

                logger.debug("COUNTER QUERY: " + query + ",/n RESULT:" + result);
            }
        }

        return result;
    }

    protected Long query(SearchParameters sp, boolean exceptionOnError, int maxResults, int skipCount) {
        // perform the search against the repo
        ResultSet results = null;
        sp.setLimitBy(LimitBy.FINAL_SIZE);
        try {
            sp.setMaxItems(0);
            sp.setSkipCount(0);

            results = this.services.getSearchService().query(sp);
            if (results instanceof SolrJSONResultSet) {
                return ((SolrJSONResultSet) results).getNumberFound();
            } else {
                sp.setMaxItems(maxResults);
                sp.setSkipCount(skipCount);
                results = this.services.getSearchService().query(sp);
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

    public boolean hasChildren(String parentRef, String childType) {
        return  hasChildren(parentRef, childType, null);
    }

    public boolean hasChildren(String parentRef, String childType, Object additionalQueryObj) {
        StringBuilder queryBuffer = new StringBuilder();

        String additionalQuery = null;

        if (additionalQueryObj != null) {
            if (additionalQueryObj instanceof String) {
                additionalQuery = (String) additionalQueryObj;
            } else if (additionalQueryObj instanceof Serializable) {
                Serializable obj = new ValueConverter().convertValueForRepo((Serializable) additionalQueryObj);
                if (obj instanceof Map) {
                    Map<Serializable, Serializable> def = (Map<Serializable, Serializable>) obj;
                    additionalQuery = (String) def.get("queryBuffer");
                }
            }
        }

        if (additionalQuery != null && !additionalQuery.isEmpty()) {
            // обработка спец-выражений
            if (additionalQuery.contains("#current-user")) {
                additionalQuery = additionalQuery.replaceAll("#current-user", orgstructureService.getCurrentEmployee().toString());
            }
            if (additionalQuery.contains("#current-date")) {
                int limitDays = notificationsService.getSettingsNDays();
                Date nextWorkDate = workCalendarService.getNextWorkingDate(new Date(), limitDays, Calendar.DAY_OF_MONTH);
                additionalQuery = additionalQuery.replaceAll("#current-date", DocumentService.DateFormatISO8601.format(nextWorkDate));
            }

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
            queryBuffer.append(" AND TYPE:\"").append(childType).append("\"");
        }
        queryBuffer.append(")");

        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

        sp.setQuery(queryBuffer.toString());

        logger.debug("Has Children queryBuffer: " + queryBuffer);

        // execute search based on search definition
        Long count = query(sp, true, -1, 0);

        return count > 0;
    }
}
