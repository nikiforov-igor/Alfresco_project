package ru.it.lecm.orgstructure.scripts.diagram;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: pmelnikov
 * Date: 28.05.13
 * Time: 13:54
 */
public class DiagramScript extends AbstractWebScript {

    private OrgstructureBean orgstructureBean;
    private NodeService nodeService;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        res.setContentType("image/svg+xml");
        String rootStr = req.getParameter("root");
        NodeRef rootRef = null;
        if (NodeRef.isNodeRef(rootStr)) {
            rootRef = new NodeRef(rootStr);
        }
        // Create an XML stream writer
        try (OutputStream output = res.getOutputStream()) {
            new DiagramGenerator().generate(output, orgstructureBean, nodeService, rootRef);

            output.flush();
            output.close();
        }
    }
}