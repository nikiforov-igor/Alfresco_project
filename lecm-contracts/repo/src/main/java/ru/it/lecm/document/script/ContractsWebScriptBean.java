package ru.it.lecm.document.script;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.document.beans.ContractsServiceImpl;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {

    private ContractsServiceImpl contractsService;

    public void setService(ContractsServiceImpl service) {
        this.contractsService = service;
    }

    public ScriptNode getDraftRoot() {
        return new ScriptNode(contractsService.getDraftRoot(), serviceRegistry, getScope());
    }

    public String getDraftPath() {
        return contractsService.getDraftPath();
    }

    public String getDocumentPath() {
        return contractsService.getDocumentPath();
    }
}
