package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentService;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {

    public static final String CONTRACTS = "Contracts";
    private DocumentService documentService;
    private ContractsBeanImpl contractService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setContractService(ContractsBeanImpl contractService) {
        this.contractService = contractService;
    }

    public ScriptNode getDraftRoot() {
        return new ScriptNode(documentService.getDraftRoot(CONTRACTS), serviceRegistry, getScope());
    }

    public String getDraftPath() {
        return documentService.getDraftPath(CONTRACTS);
    }

    public String getTotalContracts() {
        return "" + contractService.getTotalContracts().size();
    }
}
