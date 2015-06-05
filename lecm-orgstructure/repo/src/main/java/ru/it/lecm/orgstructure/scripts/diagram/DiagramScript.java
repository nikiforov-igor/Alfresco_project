package ru.it.lecm.orgstructure.scripts.diagram;

import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.IOException;
import java.io.InputStream;
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
        // Create an XML stream writer
        OutputStream output = res.getOutputStream();
        InputStream is = new DiagramGenerator().generate(orgstructureBean, nodeService);
        byte[] buf = new byte[8 * 1024];
        int c;
        int len = 0;
        while ((c = is.read(buf)) != -1) {
            output.write(buf, 0, c);
            len += c;
        }
        res.setHeader("Content-length", "" + len);
        output.flush();
        output.close();
        is.close();
    }
}