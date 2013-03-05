package ru.it.lecm.statemachine.editor.export;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 26.02.13
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class Import extends AbstractWebScript {
    private static final Log log = LogFactory.getLog(Export.class);

    private Repository repositoryHelper;
    private NodeService nodeService;

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String stateMachineId = req.getParameter("stateMachineId");
        if (stateMachineId == null) {
            log.error("No State Machine to import! stateMachineId is null.");
            return;
        }
        InputStream inputStream = null;
        try {
            FormData formData = (FormData) req.parseContent();
            FormData.FormField[] fields = formData.getFields();

            inputStream = fields[0].getInputStream();
            XMLImporter xmlImporter = new XMLImporter(inputStream, repositoryHelper, nodeService, stateMachineId);
            xmlImporter.importStateMachine();
            xmlImporter.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
