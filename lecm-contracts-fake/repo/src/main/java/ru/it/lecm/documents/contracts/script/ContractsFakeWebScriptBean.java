package ru.it.lecm.documents.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsFakeWebScriptBean extends BaseWebScript {
    private DocumentService service;
    private NamespaceService namespaceService;

    public void setService(DocumentService service) {
        this.service = service;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public ScriptNode getDraftRoot() {
        QName fakeContractType = QName.createQName("lecm-contract-fake:document", namespaceService);
        if (fakeContractType != null) {
            return new ScriptNode(service.getDraftRootByType(fakeContractType), serviceRegistry, getScope());
        }
        return null;
    }

    public String getDraftPath() {
        QName fakeContractType = QName.createQName("lecm-contract-fake:document", namespaceService);
        if (fakeContractType != null) {
            return service.getDraftPathByType(fakeContractType);
        }
        return null;
    }
}
