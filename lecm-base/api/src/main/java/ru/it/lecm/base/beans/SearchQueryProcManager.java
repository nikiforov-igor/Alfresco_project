package ru.it.lecm.base.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 9:56
 */
public class SearchQueryProcManager {
    private final static Logger logger = LoggerFactory.getLogger(SearchQueryProcManager.class);

    private Map<String, SearchQueryProcessor> registeredProcessors = new HashMap<String, SearchQueryProcessor>();

    public void resisterProcessor(SearchQueryProcessor newProcessor) {
        if (newProcessor != null) {
            final String procId = newProcessor.getId();
            if (procId != null && !procId.isEmpty()) {
                if (getProcessorById(procId) == null) {
                    getRegisteredProcessors().put(procId, newProcessor);
                } else {
                    logger.debug("Search Query Processor with id= " + procId + " already registered. New registration skipped...");
                }
            } else {
                logger.debug("Search Query Processor with class= " + newProcessor.getClass().getName() + " has not ID. Registration skipped...");
            }
        }
    }

    public SearchQueryProcessor getProcessorById(String procId) {
        return getRegisteredProcessors().get(procId);
    }

    public Map<String, SearchQueryProcessor> getRegisteredProcessors() {
        return registeredProcessors;
    }
}
