package ru.it.lecm.statemachine.action.script;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.context.Context;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.repo.workflow.activiti.script.DelegateExecutionScriptBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;

import java.util.HashMap;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 21.01.14
 * Time: 9:06
 */
public class WorkflowScript extends DelegateExecutionScriptBase {

    private Map<String, Object> variables = new HashMap<String, Object>();
    private AlfrescoProcessEngineConfiguration config = null;

    public WorkflowScript(Map<String, Object> variables, AlfrescoProcessEngineConfiguration config) {
        this.variables = variables;
        this.config = config;
    }

    public void notify(DelegateExecution execution) throws Exception {
        Context.setProcessEngineConfiguration(config);
        runScript(execution);
        Context.removeProcessEngineConfiguration();
    }

    protected Map<String, Object> getInputMap(DelegateExecution execution, String runAsUser) {
        HashMap<String, Object> scriptModel = new HashMap<String, Object>(1);

        // Add current logged-in user and it's user home
        ActivitiScriptNode personNode = getPersonNode(runAsUser);
        if (personNode != null) {
            ServiceRegistry registry = getServiceRegistry();
            scriptModel.put(PERSON_BINDING_NAME, personNode);
            NodeRef userHomeNode = (NodeRef) registry.getNodeService().getProperty(personNode.getNodeRef(), ContentModel.PROP_HOMEFOLDER);
            if (userHomeNode != null) {
                scriptModel.put(USERHOME_BINDING_NAME, new ActivitiScriptNode(userHomeNode, registry));
            }
        }

        // Add activiti-specific objects
        scriptModel.put(EXECUTION_BINDING_NAME, execution);

        // Add all workflow variables to model
        for (Map.Entry<String, Object> varEntry : variables.entrySet()) {
            scriptModel.put(varEntry.getKey(), varEntry.getValue());
        }

        return scriptModel;
    }
}
