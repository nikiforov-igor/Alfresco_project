package ru.it.lecm.documents.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsFakeWebScriptBean extends BaseWebScript {
    private DocumentService service;

    public void setService(DocumentService service) {
        this.service = service;
    }

    public ScriptNode getDraftRoot() {
        String rootName = service.getDraftRootLabel("lecm-contract-fake:document");
        return new ScriptNode(service.getDraftRoot(rootName), serviceRegistry, getScope());
    }

    public String getDraftPath() {
        return service.getDraftPath(service.getDraftRootLabel("lecm-contract-fake:document"));
    }

}
