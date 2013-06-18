package ru.it.lecm.statemachine.editor.export;

import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final transient Logger log = LoggerFactory.getLogger(Export.class);
    private static final String STATUSES_NODE_REF = "statusesNodeRef";

    private NodeService nodeService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String statusesNodeRef = req.getParameter(STATUSES_NODE_REF);
        if (statusesNodeRef == null) {
            log.error("No State Machine to export! statusesNodeRef is null.");
            return;
        }

        OutputStream resOutputStream = null;
        try {
            res.setContentEncoding("UTF-8");
            res.setContentType("text/xml");
            res.addHeader("Content-Disposition", "attachment; filename=StateMachineExport.xml");

            resOutputStream = res.getOutputStream();

            XMLExporter xmlExporter = new XMLExporter(resOutputStream, nodeService);
            xmlExporter.write(statusesNodeRef);
            xmlExporter.close();

            resOutputStream.flush();
        } catch (XMLStreamException e) {
	        log.error(e.getMessage(), e);
        } catch (IOException e) {
	        log.error(e.getMessage(), e);
        } finally {
            if (resOutputStream != null) {
                resOutputStream.close();
            }
        }

        log.info("Export complete");
    }
}
