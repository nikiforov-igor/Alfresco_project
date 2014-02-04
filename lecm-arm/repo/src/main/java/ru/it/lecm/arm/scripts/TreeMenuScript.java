package ru.it.lecm.arm.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.arm.beans.ArmServiceImpl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 10.12.13
 * Time: 12:49
 */
public class TreeMenuScript extends AbstractWebScript {
    final private static Logger logger = LoggerFactory.getLogger(TreeMenuScript.class);

    public static final String NODE_REF = "nodeRef";
    public static final String ID = "id";
    public static final String CHILD_TYPE = "childType";
    public static final String LABEL = "label";
    public static final String IS_LEAF = "isLeaf";
    private static final String FILTER = "filter";
    private static final String COUNTER = "counterValue";

    private NodeService nodeService;
    private NamespaceService namespaceService;
    private ArmServiceImpl service;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setService(ArmServiceImpl service) {
        this.service = service;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        List<JSONObject> nodes = new ArrayList<JSONObject>();

        String nodeRef = req.getParameter(NODE_REF);

        if (nodeRef == null) {
            List<NodeRef> roots = service.getRoots();
            for (NodeRef root : roots) {
                nodes.add(getJSONNode(root));
            }
            //For Test
            for (int i = 0; i < 3; i++) {
                nodes.add(getJSONNode(String.valueOf(i), null, "lecm-document:base", "Узел " + i , false, "", 0L));
            }
        } else {
            if (NodeRef.isNodeRef(nodeRef)) {
                List<NodeRef> childs = service.getChilds(new NodeRef(nodeRef));
                for (NodeRef child : childs) {
                    nodes.add(getJSONNode(child));
                }

            } else {
                //For Test
                for (int i = 0; i < 2; i++) {
                    nodes.add(getJSONNode(String.valueOf(i), null, "lecm-document:base", "Дочерний Узел " + i , false, "", 0L));
                }
            }
        }
        try {
            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(nodes.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private JSONObject getJSONNode(NodeRef node) {
        JSONObject result = new JSONObject();
        try {
            result.put(ID, node.getId());
            result.put(NODE_REF, node.toString());
            result.put(CHILD_TYPE, service.getChildTypes(node));
            result.put(LABEL, nodeService.getProperty(node, ContentModel.PROP_NAME));
            result.put(IS_LEAF, service.hasChild(node));
            result.put(FILTER, service.getNodeFilter(node));
            if (service.isCounterEnable(node)) {
                result.put(COUNTER, service.getNodeCounterValue(node));
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    //TODO for tests only
    private JSONObject getJSONNode(String id, String nodeRef, String childs, String label, boolean isLeaf, String filter, Long counter) {
        JSONObject result = new JSONObject();
        try {
            result.put(ID, id);
            result.put(NODE_REF, nodeRef);
            result.put(CHILD_TYPE, childs);
            result.put(LABEL, label);
            result.put(IS_LEAF, isLeaf);
            result.put(FILTER, filter);
            result.put(COUNTER, counter);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }
}
