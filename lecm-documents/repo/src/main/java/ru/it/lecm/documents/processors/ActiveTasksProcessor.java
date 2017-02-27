package ru.it.lecm.documents.processors;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
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
public class ActiveTasksProcessor extends SearchQueryProcessor{
    private static final Logger logger = LoggerFactory.getLogger(ActiveTasksProcessor.class);

    private StateMachineServiceBean stateMachineService;
    private OrgstructureBean orgstructureBean;
    private DocumentGlobalSettingsService documentGlobalSettings;

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setDocumentGlobalSettings(DocumentGlobalSettingsService documentGlobalSettings) {
        this.documentGlobalSettings = documentGlobalSettings;
    }

	@Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Set<String> tasksNames = new HashSet<>();
        Object tasksParam = params != null ? params.get("tasks") : null;

        if (tasksParam != null) {
            if (tasksParam instanceof JSONArray) {
                try {
                    JSONArray currentTasksFilter = (JSONArray) tasksParam;
                    for (int j = 0; j < currentTasksFilter.length(); j++) {
                        String task = (String) currentTasksFilter.get(j);
                        tasksNames.add(task.trim());
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                String[] tasksIds = tasksParam.toString().split(",");
                for (String taskId : tasksIds) {
                    if (!taskId.isEmpty()) {
                        tasksNames.add(taskId.trim());
                    }
                }
            }
        }
        Object userParam = params != null ? params.get("user") : null;
        String login = null;
        if (userParam != null && NodeRef.isNodeRef((String) userParam)) {
            login = orgstructureBean.getEmployeeLogin(new NodeRef((String) userParam));
        }
	    boolean onlyUrgentAndOverdue = false;
	    if (params != null && params.get("onlyUrgentAndOverdue") != null) {
		    onlyUrgentAndOverdue = (boolean) params.get("onlyUrgentAndOverdue");
	    }

	    Integer remainingDays = null;
	    if (onlyUrgentAndOverdue) {
			remainingDays = documentGlobalSettings.getSettingsNDays();
	    } else if (params != null && params.get("remainingDays") != null) {
			remainingDays = (Integer) params.get("remainingDays");
	    }

        List<NodeRef> documents = stateMachineService.getDocumentsWithActiveTasks(login != null ? login : AuthenticationUtil.getFullyAuthenticatedUser(), tasksNames, remainingDays);
        for (NodeRef document : documents) {
            sbQuery.append("ID:\"").append(document.toString()).append("\" OR ");
        }

        sbQuery.append("ID:\"NOT_REF\""); // выключать поиск, если документы не найдены

        return sbQuery.toString();
    }
}
