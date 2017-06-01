package ru.it.lecm.arm.beans.childRules;

import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.ScriptService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.search.ArmChildrenRequest;
import ru.it.lecm.arm.beans.search.ArmChildrenResponse;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aleksey on 15.02.16.
 */
public class ArmScriptChildRule extends ArmBaseChildRule {
    private String script;
    private ScriptService scriptService;
    private OrgstructureBean orgstructureService;

    public OrgstructureBean getOrgstructureService() {
        return orgstructureService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public ScriptService getScriptService() {
        return scriptService;
    }

    public void setScriptService(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public ArmChildrenResponse build(ArmWrapperService service, ArmNode node, ArmChildrenRequest request) {
        List<ArmNode> nodes = new ArrayList<ArmNode>();
        ArrayList<NodeRef> results = null;

        if (script != null) {
            results = runScript(script, node.getNodeRef(), orgstructureService.getCurrentEmployee());
            for (NodeRef nodeRef : results) {
                ArmNode rowNode = service.wrapAnyNodeAsObject(nodeRef, node, getSubstituteString());
                nodes.add(rowNode);
            }
        }
        return new ArmChildrenResponse(nodes, nodes.size());
    }

    private ArrayList<NodeRef> runScript(String macrosString, NodeRef documentNode, NodeRef currentEmployee) {
        Map<String, Object> scriptModel = new HashMap<>(),
                returnModel = new HashMap<>();
        ValueConverter converter = new ValueConverter();

        scriptModel.put("model", returnModel);
        scriptModel.put("document", documentNode);
        scriptModel.put("currentEmployee", currentEmployee);

        scriptService.executeScriptString(macrosString, scriptModel);

        return (ArrayList<NodeRef>) converter.convertValueForJava(returnModel.get("result"));
    }

    @Override
    public List<NodeRef> getChildren(NodeRef node) {
        return null;
    }
}
