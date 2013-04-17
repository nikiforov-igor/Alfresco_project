package ru.it.lecm.workflow;

import org.activiti.engine.delegate.DelegateTask;
import org.alfresco.repo.workflow.activiti.tasklistener.ScriptTaskListener;
import org.alfresco.service.ServiceRegistry;
import ru.it.lecm.documents.beans.DocumentConnectionService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 09.04.13
 * Time: 14:03
 */
public class LecmScriptTaskListener extends ScriptTaskListener {

    private static HashMap<String, Object> objects = new HashMap<String, Object>();
    private static ServiceRegistry serviceRegistry;

    @Override
    protected Map<String, Object> getInputMap(DelegateTask delegateTask, String runAsUser) {
        Map<String, Object> scriptModel = super.getInputMap(delegateTask, runAsUser);
        scriptModel.putAll(objects);
        scriptModel.put("artifact", new Artifact());
        return scriptModel;
    }

    public void register(String objectName, Object object) {
        objects.put(objectName, object);
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        LecmScriptTaskListener.serviceRegistry = serviceRegistry;
    }

}
