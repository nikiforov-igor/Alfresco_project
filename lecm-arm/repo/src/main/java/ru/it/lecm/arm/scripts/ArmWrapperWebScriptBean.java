package ru.it.lecm.arm.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
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
    private NodeService nodeService;

    public void setArmWrapperService(ArmWrapperService armWrapperService) {
        this.armWrapperService = armWrapperService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @SuppressWarnings("unused")
    public List<JSONObject> getArmNodeChilds(ScriptNode node) {
        List<JSONObject> nodes = new ArrayList<JSONObject>();
        List<ArmNode> childNodes = armWrapperService.getChildNodes(node.getNodeRef(), nodeService.getPrimaryParent(node.getNodeRef()).getParentRef(), false);
        for (ArmNode childNode : childNodes) {
            JSONObject result = new JSONObject();
            try {
                result.put("title", childNode.getTitle());
                result.put("query", getFullQuery(childNode, true, false));

                nodes.add(result);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return nodes;
    }

    @SuppressWarnings("unused")
    public String getFullQuery(ScriptNode node) {
        return getFullQuery(node, true, true);
    }

    @SuppressWarnings("unused")
    public String getFullQuery(ScriptNode node, boolean includeTypes, boolean includeParentQuery) {
        StringBuilder builder = new StringBuilder();
        ArmNode armNode = armWrapperService.wrapArmNodeAsObject(node.getNodeRef());

        return getFullQuery(armNode, includeTypes, includeParentQuery);
    }

    @SuppressWarnings("unused")
    public String getFullQuery(ArmNode armNode, boolean includeTypes, boolean includeParentQuery) {
        StringBuilder builder = new StringBuilder();
        if (includeTypes) {
            List<String> types = armNode.getTypes();
            StringBuilder typesBuilder = new StringBuilder();
            for (String type : types) {
                typesBuilder.append("TYPE:\"").append(type).append("\"").append(" OR ");
            }
            if (typesBuilder.length() > 0) {
                typesBuilder.delete(typesBuilder.length() - 4, typesBuilder.length());
            }

            if (typesBuilder.length() > 0) {
                builder.append("(").append(typesBuilder.toString()).append(")");
            }
        }

        Object query = armNode.getSearchQuery();
        if (query != null && query.toString().length() > 0) {
            if (builder.length() > 0) {
                builder.append(" AND ");
            }
            builder.append("(").append(query.toString()).append(")");
        }
        if (includeParentQuery) {
            NodeRef parentNode = nodeService.getPrimaryParent(armNode.getNodeRef()).getParentRef();
            if (parentNode != null) {
                QName parentType = nodeService.getType(parentNode);
                if (parentType.equals(ArmService.TYPE_ARM_NODE) || parentType.equals(ArmService.TYPE_ARM_ACCORDION)) {
                    builder.append(getFullQuery(new ScriptNode(parentNode, serviceRegistry, getScope()), false, false));
                }
            }
        }
        return builder.toString();
    }
}
