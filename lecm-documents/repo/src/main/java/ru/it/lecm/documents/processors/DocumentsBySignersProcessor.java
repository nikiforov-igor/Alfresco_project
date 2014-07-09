package ru.it.lecm.documents.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessor;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 09.07.2014
 * Time: 11:56
 */
public class DocumentsBySignersProcessor extends SearchQueryProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentsBySignersProcessor.class);
    public static final String SIGNERS = "signers";

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Set<NodeRef> employeesSet = new HashSet<NodeRef>();
        Object emoployeesParam = params != null ? params.get(SIGNERS) : null;
        if (emoployeesParam != null) {
            if (emoployeesParam instanceof JSONArray) {
                try {
                    JSONArray employeesFilter = (JSONArray) emoployeesParam;
                    for (int j = 0; j < employeesFilter.length(); j++) {
                        String employee = ((String) employeesFilter.get(j)).trim();
                        if (NodeRef.isNodeRef(employee)) {
                            employeesSet.add(new NodeRef(employee));
                        }
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                logger.warn("DocumentsBySignersProcessor (DOCS_BY_SIGNERS) param 'signers' in not array. Employees list set to empty...");
            }
        }


        StringBuilder employeeQuery = new StringBuilder();
        for (NodeRef employeeRef : employeesSet) {
            employeeQuery.append("\"").append(employeeRef.toString().replace(":", "\\:")).append("\"").append(" OR ");
        }
        if (employeeQuery.length() > 0) {
            employeeQuery.delete(employeeQuery.length() - 4, employeeQuery.length());

            SearchParameters sp = new SearchParameters();
            sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
            sp.setQuery("TYPE:\"lecmSignResult:signResultItem\" and @lecmSignResult\\:signResultItemDecision:\"SIGNED\" and "
                    + "@lecm\\-workflow\\-result\\:workflow\\-result\\-item\\-employee\\-assoc\\-ref:" + "(" + employeeQuery + ")");

            List<NodeRef> resultItems = new ArrayList<>();
            ResultSet results = null;
            try {
                results = searchService.query(sp);
                for (ResultSetRow row : results) {
                    NodeRef currentNodeRef = row.getNodeRef();
                    resultItems.add(currentNodeRef);
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

            Set<NodeRef> documents = new HashSet<>();

            for (NodeRef resultItem : resultItems) {
                NodeRef listRef = nodeService.getPrimaryParent(resultItem).getParentRef();
                NodeRef signFolderRef = nodeService.getPrimaryParent(listRef).getParentRef();
                documents.add(nodeService.getPrimaryParent(signFolderRef).getParentRef());
            }

            for (NodeRef document : documents) {
                sbQuery.append("ID:\"").append(document.toString()).append("\" OR ");
            }

            if (sbQuery.length() > 0) {
                sbQuery.delete(sbQuery.length() - 4, sbQuery.length());
            }

            sbQuery.append(sbQuery.length() == 0 ?  "ID:\"NOT_REF\"" : ""); // выключать поиск, если документы не найдены (пустая строка)
        }

        return sbQuery.toString();
    }
}
