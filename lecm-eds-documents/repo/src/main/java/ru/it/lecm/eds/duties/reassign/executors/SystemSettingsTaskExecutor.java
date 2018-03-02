package ru.it.lecm.eds.duties.reassign.executors;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsTaskExecutor.class);

    private static final QName USER_SETTINGS_ASSOC_DEFAULT_INITIATOR = QName.createQName("http://www.it.ru/logicECM/errands/1.0", "user-settings-default-initiator-assoc");
    private static final String ATTRIBUTE = "attribute";
    private static final String SETTINGS = "settings";
    private static final String TYPE = "type";

    private static final String SUCCEESS_LOG_TEMPLATE = "Сотрудник #object2 изменен на сотрудника #object3 в настройке %s";
    private static final String FAIL_LOG_TEMPLATE = "Не удалось изменить сотрудника #object2 на сотрудника #object3 в настройке %s";

    @Override
    public boolean execute(NodeRef task, JSONObject element) {
        AuthenticationUtil.RunAsWork<Boolean> raw = () -> {
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
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

    @Override
    public List<JSONObject> calculateElements(NodeRef task) {
        NodeRef fromAsssoc = nodeService.getTargetAssocs(task, DutiesReassignService.ASSOC_FROM).get(0).getTargetRef();
        return handleElements(fromAsssoc.toString(), null);
    }

    @Override
    public List<JSONObject> calculateElements(JSONObject taskData) {
        List<JSONObject> elements = new ArrayList<>();
        try {
            String fromAssocRef = taskData.has("assoc_lecm-duties-reassign_from-assoc") ?
                    taskData.getString("assoc_lecm-duties-reassign_from-assoc") : null;
            final String searchTerm = taskData.has(SEARCH_TERM) && !taskData.getString(SEARCH_TERM).isEmpty() ? taskData.getString(SEARCH_TERM).toLowerCase() : null;
            elements = handleElements(fromAssocRef, searchTerm);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

        return elements;
    }

    private List<JSONObject> handleElements(String fromAssocRef, String searchTerm) {
        AuthenticationUtil.RunAsWork<List<JSONObject>> raw = () -> {
            List<JSONObject> elementsInWork = new ArrayList<>();
            if (fromAssocRef != null && !fromAssocRef.isEmpty()) {
                NodeRef fromAssoc = new NodeRef(fromAssocRef);
                //Дежурный регистратор
                List<AssociationRef> registarSettingList = nodeService.getSourceAssocs(fromAssoc, EDSGlobalSettingsService.ASSOC_DUTY_REGISTRAR);
                if (!registarSettingList.isEmpty()) {
                    NodeRef registarSetting = registarSettingList.get(0).getSourceRef();
                    String attribute = I18NUtil.getMessage("lecm-eds-globset_model.association.lecm-eds-globset_duty-registrar-assoc.title");
                    String settings = I18NUtil.getMessage("system-settings-task-executor.assoc-duty-registrar-path");
                    if (searchTerm == null ||
                            attribute.toLowerCase().contains(searchTerm) ||
                            settings.toLowerCase().contains(searchTerm)
                            ) {
                        JSONObject element = new JSONObject();
                        try {
                            element.put(NAME, registarSetting.toString());
                            element.put(NODE_REF, registarSetting.toString());
                            element.put(ID, registarSetting.toString());
                            element.put(TYPE, EDSGlobalSettingsService.ASSOC_DUTY_REGISTRAR.toString());
                            element.put(ATTRIBUTE, attribute);
                            element.put(SETTINGS, settings);
                            elementsInWork.add(element);
                        } catch (JSONException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }

                //Автор по умолчанию
                List<AssociationRef> authorSettingList = nodeService.getSourceAssocs(fromAssoc, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR);
                for (AssociationRef authorSettingAssoc : authorSettingList) {
                    NodeRef authorSetting = authorSettingAssoc.getSourceRef();
                    String attribute = I18NUtil.getMessage("lecm-errands_model.association.lecm-errands_user-settings-default-initiator-assoc.title");
                    String userSettingsName = ((String) nodeService.getProperty(authorSetting, ContentModel.PROP_NAME)).replace("_Settings", "");
                    NodeRef employee = orgstructureBean.getEmployeeByPerson(userSettingsName);
                    if (employee != null) {
                        String employeeName = (String) nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                        String settingsPath = String.format(I18NUtil.getMessage("system-settings-task-executor.user-settings-assoc-default-initiator-path"), employeeName);
                        if (searchTerm == null ||
                                attribute.toLowerCase().contains(searchTerm) ||
                                settingsPath.toLowerCase().contains(searchTerm)
                                ) {
                            JSONObject element = new JSONObject();
                            try {
                                element.put(NAME, authorSetting.toString());
                                element.put(NODE_REF, authorSetting.toString());
                                element.put(ID, authorSetting.toString());
                                element.put(TYPE, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR.toString());
                                element.put(ATTRIBUTE, attribute);
                                element.put(SETTINGS, settingsPath);
                                elementsInWork.add(element);
                            } catch (JSONException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
            return elementsInWork;
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

    @Override
    protected String getSuccessLogTemplate(JSONObject element) throws JSONException {
        return String.format(SUCCEESS_LOG_TEMPLATE, element.getString(ATTRIBUTE));
    }

    @Override
    protected String getFailureLogTemplate(JSONObject element) throws JSONException {
        return String.format(FAIL_LOG_TEMPLATE, element.getString(ATTRIBUTE));
    }
}
