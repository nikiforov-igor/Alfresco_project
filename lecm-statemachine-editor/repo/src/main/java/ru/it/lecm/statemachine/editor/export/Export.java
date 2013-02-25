package ru.it.lecm.statemachine.editor.export;

import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 18.02.13
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class Export extends AbstractWebScript {
    private static final Log log = LogFactory.getLog(Export.class);
    private static final String STATUSES_NODE_REF = "statusesNodeRef";

    private NodeService nodeService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String statusesNodeRef = req.getParameter(STATUSES_NODE_REF);
        if (statusesNodeRef == null) {
            // TO DO: log or exception
        }

        OutputStream resOutputStream = null;
        try {
            res.setContentEncoding("UTF-8");
            res.setContentType("text/xml");
            res.addHeader("Content-Disposition", "attachment; filename=StateMachineExport.xml");

            resOutputStream = res.getOutputStream();

            XMLWriter xmlWriter = new XMLWriter(resOutputStream, nodeService);
            xmlWriter.write(statusesNodeRef);
            xmlWriter.close();

            resOutputStream.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resOutputStream != null) {
                resOutputStream.close();
            }
        }

        log.info("Export complete");
    }
}
