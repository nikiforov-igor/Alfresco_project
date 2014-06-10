package ru.it.lecm.statemachine.script;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.actions.bean.GroupActionsService;
import ru.it.lecm.documents.beans.DocumentConnectionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 14.05.2014
 * Time: 9:45
 */
public class PostActionsScript extends DeclarativeWebScript  {

    private NodeService nodeService;
    private GroupActionsService groupActionsService;
    private DocumentConnectionService documentConnectionService;
    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setGroupActionsService(GroupActionsService groupActionsService) {
        this.groupActionsService = groupActionsService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        String label = req.getParameter("label");

        NodeRef fromNode = null;
        if (req.getParameter("fromNodeRef") != null && NodeRef.isNodeRef(req.getParameter("fromNodeRef"))) {
            fromNode = new NodeRef(req.getParameter("fromNodeRef"));
        }

        NodeRef toNode = null;
        if (req.getParameter("toNodeRef") != null && NodeRef.isNodeRef(req.getParameter("toNodeRef"))) {
            toNode = new NodeRef(req.getParameter("toNodeRef"));
        }

        if (label != null && fromNode != null && toNode != null) {
            List<NodeRef> actions = groupActionsService.getActiveActions(fromNode);
            NodeRef activeAction = null;
            for (NodeRef action : actions) {
                String actionLabel = nodeService.getProperty(action, ContentModel.PROP_NAME).toString();
                if (actionLabel.equals(label)) {
                    activeAction = action;
                    break;
                }
            }
            if (activeAction != null) {
                String connectionType = nodeService.getProperty(activeAction, GroupActionsService.PROP_DOCUMENT_CONNECTION).toString();
                Boolean isSystem = (Boolean) nodeService.getProperty(activeAction, GroupActionsService.PROP_DOCUMENT_CONNECTION_SYSTEM);
                documentConnectionService.createConnection(toNode, fromNode, connectionType, isSystem, true);
            }
        }
        return new HashMap<String, Object>();
    }

}
