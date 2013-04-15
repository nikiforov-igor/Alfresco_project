package ru.it.lecm.statemachine.editor.export;

import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.statemachine.bean.DefaultStatemachinesImpl;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: pkotelnikova
 * Date: 26.02.13
 * Time: 17:45
 */
public class Import extends AbstractWebScript {
    private static final Log log = LogFactory.getLog(Export.class);

    private RepositoryStructureHelper repositoryStructureHelper;
    private NodeService nodeService;
    private DefaultStatemachinesImpl defaultStatemachines;

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDefaultStatemachines(DefaultStatemachinesImpl defaultStatemachines) {
        this.defaultStatemachines = defaultStatemachines;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String stateMachineId = req.getParameter("stateMachineId");
        boolean defaultStatemachine = Boolean.parseBoolean(req.getParameter("default"));
        if (stateMachineId == null) {
            log.error("No State Machine to import! stateMachineId is null.");
            return;
        }
        InputStream inputStream = null;
        try {
            if (!defaultStatemachine) {
                FormData formData = (FormData) req.parseContent();
                FormData.FormField[] fields = formData.getFields();

                inputStream = fields[0].getInputStream();
            } else {
                String path = defaultStatemachines.getPath(stateMachineId);
                inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
            }
            XMLImporter xmlImporter = new XMLImporter(inputStream, repositoryStructureHelper, nodeService, stateMachineId);
            xmlImporter.importStateMachine();
            xmlImporter.close();
        } catch (Exception e) {
            throw new IOException("Failed to import State Machine!", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
