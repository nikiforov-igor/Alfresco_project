package ru.it.lecm.eds.duties.reassign.executors;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.duties.reassign.beans.DutiesReassignService;
import ru.it.lecm.duties.reassign.executors.TaskExecutor;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.List;

/**
 * $Author:  AElkin
 * 19.02.2018 13:54
 */
public class SystemSettingsTaskExecutor extends TaskExecutor {

    private static final QName USER_SETTINGS_ASSOC_DEFAULT_INITIATOR = QName.createQName("http://www.it.ru/logicECM/errands/1.0", "user-settings-default-initiator-assoc");
    private static final String ATTRIBUTE = "attribute";
    private static final String SETTINGS = "settings";
    private static final String TYPE = "type";

    @Override
    public boolean execute(NodeRef task, JSONObject element) {
        boolean result = false;

        try {
            NodeRef fromAsssoc = nodeService.getTargetAssocs(task, DutiesReassignService.ASSOC_FROM).get(0).getTargetRef();
            NodeRef toAsssoc = nodeService.getTargetAssocs(task, DutiesReassignService.ASSOC_TO).get(0).getTargetRef();

            String settingsItemRef = element.has(NODE_REF) ? element.getString(NODE_REF) : null;
            NodeRef settingItem = (settingsItemRef != null && NodeRef.isNodeRef(settingsItemRef)) ? new NodeRef(settingsItemRef) : null;
            if (settingItem != null) {
                if (element.getString(TYPE).equals(USER_SETTINGS_ASSOC_DEFAULT_INITIATOR.toString())) {
                    nodeService.removeAssociation(settingItem, fromAsssoc, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR);
                    nodeService.createAssociation(settingItem, toAsssoc, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR);
                } else if (element.getString(TYPE).equals(EDSGlobalSettingsService.ASSOC_DUTY_REGISTRAR.toString())) {
                    nodeService.removeAssociation(settingItem, fromAsssoc, EDSGlobalSettingsService.ASSOC_DUTY_REGISTRAR);
                    nodeService.createAssociation(settingItem, toAsssoc, EDSGlobalSettingsService.ASSOC_DUTY_REGISTRAR);
                }

            } else {
                throw new IllegalStateException("Cannot find field \"nodeRef\" in elementData");
            }

            result = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return result;
    }

    @Override
    public List<JSONObject> calculateElements(NodeRef task) {
        NodeRef fromAsssoc = nodeService.getTargetAssocs(task, DutiesReassignService.ASSOC_FROM).get(0).getTargetRef();
        return handleElements(fromAsssoc.toString());
    }

    @Override
    public List<JSONObject> calculateElements(JSONObject taskData) {
        List<JSONObject> elements = new ArrayList<>();
        try {
            String fromAssocRef = taskData.has("assoc_lecm-duties-reassign_from-assoc") ?
                    taskData.getString("assoc_lecm-duties-reassign_from-assoc") : null;
            elements = handleElements(fromAssocRef);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

        return elements;
    }

    private List<JSONObject> handleElements(String fromAssocRef) {
        List<JSONObject> result = new ArrayList<>();
        if (fromAssocRef != null && !fromAssocRef.isEmpty()) {
            NodeRef fromAssoc = new NodeRef(fromAssocRef);
            //Дежурный регистратор
            List<AssociationRef> registars = nodeService.getSourceAssocs(fromAssoc, EDSGlobalSettingsService.ASSOC_DUTY_REGISTRAR);
            if (!registars.isEmpty()) {
                NodeRef registar = registars.get(0).getSourceRef();
                JSONObject element = new JSONObject();
                try {
                    element.put(NAME, registar.toString());
                    element.put(NODE_REF, registar.toString());
                    element.put(ID, registar.toString());
                    element.put(TYPE, EDSGlobalSettingsService.ASSOC_DUTY_REGISTRAR.toString());
                    element.put(ATTRIBUTE, I18NUtil.getMessage("lecm-eds-globset_model.association.lecm-eds-globset_duty-registrar-assoc.title"));
                    element.put(SETTINGS, I18NUtil.getMessage("system-settings-task-executor.static-path"));
                    result.add(element);
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            //Автор по умолчанию
            List<AssociationRef> authors = nodeService.getSourceAssocs(fromAssoc, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR);
            if (!registars.isEmpty()) {
                NodeRef author = authors.get(0).getSourceRef();
                JSONObject element = new JSONObject();
                try {
                    element.put(NAME, author.toString());
                    element.put(NODE_REF, author.toString());
                    element.put(ID, author.toString());
                    element.put(TYPE, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR.toString());
                    element.put(ATTRIBUTE, I18NUtil.getMessage("lecm-errands_model.association.lecm-errands_user-settings-default-initiator-assoc.title"));
                    String employeeName = (String) nodeService.getProperty(fromAssoc, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                    String settingsPath = String.format(I18NUtil.getMessage("system-settings-task-executor.dynamic-path"), employeeName);
                    element.put(SETTINGS, settingsPath);
                    result.add(element);
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    @Override
    protected String getSuccessLogTemplate(JSONObject jsonObject) throws JSONException {
        return null;
    }

    @Override
    protected String getFailureLogTemplate(JSONObject jsonObject) throws JSONException {
        return null;
    }
}
