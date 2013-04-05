package ru.it.lecm.document.script;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {

    public static final String CONTRACTS = "Contracts";
    private DocumentService service;

    public void setService(DocumentService service) {
        this.service = service;
    }

    public ScriptNode getDraftRoot() {
        return new ScriptNode(service.getDraftRoot(CONTRACTS), serviceRegistry, getScope());
    }

    public String getDraftPath() {
        return service.getDraftPath(CONTRACTS);
    }

}
