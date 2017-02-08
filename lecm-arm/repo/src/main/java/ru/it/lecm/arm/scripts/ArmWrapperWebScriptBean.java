package ru.it.lecm.arm.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.base.beans.BaseWebScript;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DBashmakov
 * Date: 18.04.2014
 * Time: 11:40
 */
public class ArmWrapperWebScriptBean extends BaseWebScript {
    final private static Logger logger = LoggerFactory.getLogger(ArmWrapperWebScriptBean.class);

    private ArmWrapperService armWrapperService;
    private ArmService armService;
    private NodeService nodeService;

    public void setArmWrapperService(ArmWrapperService armWrapperService) {
        this.armWrapperService = armWrapperService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @SuppressWarnings("unused")
    public List<JSONObject> getArmNodeChilds(ScriptNode node, boolean withOwnQueryOnly) {
        List<JSONObject> nodes = new ArrayList<>();
        List<ArmNode> childNodes = armWrapperService.getChildNodes(node.getNodeRef(), nodeService.getPrimaryParent(node.getNodeRef()).getParentRef(), false);
        for (ArmNode childNode : childNodes) {
            String ownQuery = getFullQuery(childNode, false, false);
            if ((!withOwnQueryOnly || (ownQuery != null && !"".equals(ownQuery.trim()))) && childNode.getNodeType().equals("lecm-arm:node")) {
                JSONObject result = new JSONObject();
                try {
                    result.put("title", childNode.getTitle());
                    result.put("query", getFullQuery(childNode, true, false));

                    nodes.add(result);
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return nodes;
    }

    /**
     * Получение списка значений корневых узлов АРМ
     */
    public Scriptable getAccordeons(String armRef) {
        List<NodeRef> nodes = new ArrayList<NodeRef>();
        if (armRef != null && NodeRef.isNodeRef(armRef)) {
            nodes.addAll(armService.getArmAccordions(new NodeRef(armRef)));
        }
        return createScriptable(nodes);
    }

    @SuppressWarnings("unused")
    public String getFullQuery(ScriptNode node) {
        return getFullQuery(node, true, true);
    }

    @SuppressWarnings("unused")
    public String getFullQuery(ScriptNode node, boolean includeTypes, boolean includeParentQuery) {
        ArmNode armNode = armWrapperService.wrapArmNodeAsObject(node.getNodeRef());
        return getFullQuery(armNode, includeTypes, includeParentQuery);
    }

    @SuppressWarnings("unused")
    public String getFullQuery(ArmNode armNode, boolean includeTypes, boolean includeParentQuery) {
        return armWrapperService.getFullQuery(armNode, includeTypes, includeParentQuery);
    }

    public void setArmService(ArmService armService) {
        this.armService = armService;
    }
}
