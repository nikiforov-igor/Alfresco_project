package ru.it.lecm.contractors;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.contractors.api.Contractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContractorsJavascriptExtension extends BaseWebScript {

    Contractors contractors;

    public void setContractors(Contractors contractors) {
        this.contractors = contractors;
    }

    public String assignAsPrimaryRepresentative(final JSONObject json) {
        NodeRef contractorRef;
        NodeRef representativeToAssignAsPrimaryRef;

        try {
            representativeToAssignAsPrimaryRef = new NodeRef(json.getString("representativeToAssignAsPrimary"));
        } catch (JSONException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }

        contractors.assignAsPrimaryRepresentative(representativeToAssignAsPrimaryRef);

        return "Check It! I'm done!";
    }

    public JSONObject getParentContractor(final JSONObject json) {
        NodeRef childContractor;

        try {
            childContractor = new NodeRef(json.getString("childRef"));
        } catch (JSONException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }

        Map<String, String> result = contractors.getParentContractor(childContractor);

        if (result.isEmpty()) {
            result.put("status", "failure");
        } else {
            result.put("status", "success");
        }

        return new JSONObject(result);
    }

    public Scriptable getContractorsForRepresentative(String representative) {
        ParameterCheck.mandatory("representative", representative);

        NodeRef representativeRef = new NodeRef(representative);
        List<NodeRef> results = contractors.getContractorsForRepresentative(representativeRef);
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

    public List<Object> getRepresentatives(final JSONObject json) {
        NodeRef targetContractor;

        try {
            targetContractor = new NodeRef(json.getString("targetContractor"));
        } catch (JSONException ex) {
            throw new WebScriptException(ex.getMessage(), ex);
        }

        return contractors.getRepresentatives(targetContractor);
    }

    public List<Object> getRepresentatives(final String contractor) {
        NodeRef targetContractor = NodeRef.isNodeRef(contractor) ? new NodeRef(contractor) : null;
        return targetContractor != null ? contractors.getRepresentatives(targetContractor) : new ArrayList<>();
    }

    public JSONArray getBusyRepresentatives() {
        return contractors.getBusyRepresentatives();
    }

    public ScriptNode getMainRepresentative(final String contractor) {
        NodeRef targetContractor = NodeRef.isNodeRef(contractor) ? new NodeRef(contractor) : null;
        List<Object> representatives = targetContractor != null ? contractors.getRepresentatives(targetContractor) : new ArrayList<>();
        for (Object representative : representatives) {
            Map<String, Object> representativeObj = (Map<String, Object>) representative;
            if (representativeObj != null) {
                Object isPrimary = representativeObj.get("isPrimary");
                if (isPrimary != null) {
                    if ((Boolean) isPrimary) {
                        return new ScriptNode(new NodeRef(representativeObj.get("nodeRef").toString()), serviceRegistry, getScope());
                    }
                }
            }
        }
        return null;
    }

    public String formatContractorName(final String originalStr) {
        return contractors.formatContractorName(originalStr);
    }
}
