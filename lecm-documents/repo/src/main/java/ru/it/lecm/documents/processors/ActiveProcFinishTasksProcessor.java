package ru.it.lecm.documents.processors;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
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
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 11:08
 */
public class ActiveProcFinishTasksProcessor extends SearchQueryProcessor{
    private static final Logger logger = LoggerFactory.getLogger(ActiveProcFinishTasksProcessor.class);

    private StateMachineServiceBean stateMachineService;
    private OrgstructureBean orgstructureBean;

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

	@Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Set<String> filterTasks = null;
        Object tasksFilter = params != null ? params.get("tasks") : null;
        if (tasksFilter != null) {
            filterTasks = new HashSet<>();
            if (tasksFilter instanceof JSONArray) {
                try {
                    JSONArray currentTasksFilter = (JSONArray) tasksFilter;
                    for (int j = 0; j < currentTasksFilter.length(); j++) {
                        String task = (String) currentTasksFilter.get(j);
                        filterTasks.add(task.trim());
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                String[] tasksIds = tasksFilter.toString().split(",");
                for (String taskId : tasksIds) {
                    if (!taskId.isEmpty()) {
                        filterTasks.add(taskId.trim());
                    }
                }
            }
        }
        Object userFilter = params != null ? params.get("user") : null;
        String login = null;
        if (userFilter != null && NodeRef.isNodeRef((String) userFilter)) {
            login = orgstructureBean.getEmployeeLogin(new NodeRef((String) userFilter));
        }

        // активные документы
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        sp.setQuery("TYPE:\"lecm-document:base\" AND NOT @lecm\\-statemachine\\-aspects\\:is\\-final:true");

        List<NodeRef> documents = new ArrayList<>();
        ResultSet results = null;
        boolean hasNodes = true;
        int skipCountOffset = 0;

        try {
            while (hasNodes) {
                sp.setSkipCount(skipCountOffset);

                results = searchService.query(sp);

                for (ResultSetRow row : results) {
                    if (orgstructureBean.hasAccessToOrgElement(row.getNodeRef())) {
                        documents.add(row.getNodeRef());
                    }
                }

                hasNodes = results.length() > 0;
                skipCountOffset += results.length();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (results != null) {
                results.close();
            }
        }

        List<NodeRef> processDocs = stateMachineService.getDocumentsWithFinishedTasks(documents, login != null ? login : AuthenticationUtil.getFullyAuthenticatedUser(), filterTasks);
        for (NodeRef document : processDocs) {
            sbQuery.append("ID:\"").append(document.toString()).append("\" OR ");
        }

        sbQuery.append("ID:\"NOT_REF\""); // выключать поиск, если документы не найдены

        return sbQuery.toString();
    }
}
