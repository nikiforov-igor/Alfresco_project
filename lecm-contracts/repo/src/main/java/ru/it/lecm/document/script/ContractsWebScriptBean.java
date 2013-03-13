package ru.it.lecm.document.script;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.document.beans.ContractsServiceImpl;

import java.util.List;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseScopableProcessorExtension {

    private ContractsServiceImpl contractsService;
    /**
     * Service registry
     */
    protected ServiceRegistry services;

    public void setService(ContractsServiceImpl service) {
        this.contractsService = service;
    }

    /**
     * Set the service registry
     *
     * @param services the service registry
     */
    public void setServiceRegistry(ServiceRegistry services) {
        this.services = services;
    }

    /**
     * Возвращает массив, пригодный для использования в веб-скриптах
     *
     * @return Scriptable
     */
    private Scriptable createScriptable(List<NodeRef> refs) {
        Object[] results = new Object[refs.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new ScriptNode(refs.get(i), contractsService.getServiceRegistry(), getScope());
        }
        return Context.getCurrentContext().newArray(getScope(), results);
    }

    public Scriptable getContracts(String nodeRef, String sortColumnName, boolean ascending) {
        ParameterCheck.mandatory("parentRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        List<NodeRef> records = contractsService.getContracts(ref, sortColumnName, ascending);

        return createScriptable(records);
    }

    public ScriptNode getDraftRoot() {
        return new ScriptNode(contractsService.getDraftRoot(), this.services, getScope());
    }
}
