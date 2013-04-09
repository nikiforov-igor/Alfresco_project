package ru.it.lecm.contractors;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptException;

import ru.it.lecm.contractors.api.Contractors;

public class ContractorsJavascriptExtension extends BaseScopableProcessorExtension {

    Contractors contractors;

    public void setContractors(Contractors contractors) {
        this.contractors = contractors;
    }

    public String assignAsPrimaryRepresentative(final JSONObject json) {
        NodeRef contractorRef;
        NodeRef representativeToAssignAsPrimaryRef;

        try {
            representativeToAssignAsPrimaryRef = new NodeRef(json.getString("representativeToAssignAsPrimary"));
        }
        catch (JSONException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }

        contractors.assignAsPrimaryRepresentative(representativeToAssignAsPrimaryRef);

        return "Check It! I'm done!";
    }
}
