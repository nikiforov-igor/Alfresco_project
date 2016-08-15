package ru.it.lecm.documents.processors;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        List<NodeRef> documents = stateMachineService.getDocumentsWithFinishedTasks(login != null ? login : AuthenticationUtil.getFullyAuthenticatedUser(), filterTasks);
        for (NodeRef document : documents) {
            sbQuery.append("ID:\"").append(document.toString()).append("\" OR ");
        }

        sbQuery.append("ID:\"NOT_REF\""); // выключать поиск, если документы не найдены

        return sbQuery.toString();
    }
}