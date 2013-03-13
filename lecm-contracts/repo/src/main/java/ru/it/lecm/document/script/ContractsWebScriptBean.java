package ru.it.lecm.document.script;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import ru.it.lecm.document.beans.ContractsServiceImpl;

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

    public ScriptNode getDraftRoot() {
        return new ScriptNode(contractsService.getDraftRoot(), this.services, getScope());
    }

    public String getDraftPath() {
        return contractsService.getDraftPath();
    }

    public String getDocumentPath() {
        return contractsService.getDocumentPath();
    }
}
