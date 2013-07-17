package ru.it.lecm.contractors;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptException;

import ru.it.lecm.contractors.api.Contractors;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;

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

    public JSONObject getParentContractor(final JSONObject json) {
        NodeRef childContractor;

        try {
            childContractor = new NodeRef(json.getString("childRef"));
        }
        catch (JSONException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }

        Map<String, String> result = contractors.getParentContractor(childContractor);

        if( result.isEmpty() ) {
            result.put("status", "failure");
        } else {
            result.put("status", "success");
        }

        return new JSONObject(result);
    }

    public JSONObject getContractorForRepresentative(final JSONObject json) {
        NodeRef childContractor;

        try {
            childContractor = new NodeRef(json.getString("childRef"));
        }
        catch (JSONException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }

        Map<String, String> result = contractors.getContractorForRepresentative(childContractor);

        if( result.isEmpty() ) {
            result.put("status", "failure");
        } else {
            result.put("status", "success");
        }

        return new JSONObject(result);
    }

    public List<Object> getRepresentatives(final JSONObject json) {
        NodeRef targetContractor;

        try {
            targetContractor = new NodeRef(json.getString("targetContractor"));
        }
        catch (JSONException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }

        return contractors.getRepresentatives(targetContractor);
    }

	public JSONArray getBusyRepresentatives() {
		return contractors.getBusyRepresentatives();
	}
}
