package ru.it.lecm.documents.scripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.documents.beans.DocumentConnectionService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 14.05.2014
 * Time: 9:45
 */
public class DocumentConnectionScript extends DeclarativeWebScript  {

    private NodeService nodeService;
    private DocumentConnectionService documentConnectionService;
    public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        String connectionType = req.getParameter("connectionType");
        Boolean isSystem = Boolean.valueOf(req.getParameter("connectionIsSystem"));

        NodeRef fromNode = null;
        if (req.getParameter("fromNodeRef") != null && NodeRef.isNodeRef(req.getParameter("fromNodeRef"))) {
            fromNode = new NodeRef(req.getParameter("fromNodeRef"));
        }

        NodeRef toNode = null;
        if (req.getParameter("toNodeRef") != null && NodeRef.isNodeRef(req.getParameter("toNodeRef"))) {
            toNode = new NodeRef(req.getParameter("toNodeRef"));
        }
        if (connectionType != null) {
            documentConnectionService.createConnection(toNode, fromNode, connectionType, isSystem, true);
        }
        return new HashMap<String, Object>();
    }

}
