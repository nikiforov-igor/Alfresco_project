package ru.it.lecm.statemachine.editor.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.statemachine.bean.DefaultStatemachinesImpl;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: pkotelnikova
 * Date: 26.02.13
 * Time: 17:45
 */
public class Import extends AbstractWebScript {
	private static final transient Logger log = LoggerFactory.getLogger(Import.class);

    private RepositoryStructureHelper repositoryStructureHelper;
    private NodeService nodeService;
    private DefaultStatemachinesImpl defaultStatemachines;
    private ContentService contentService;
    private DictionaryBean serviceDictionary;

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDefaultStatemachines(DefaultStatemachinesImpl defaultStatemachines) {
        this.defaultStatemachines = defaultStatemachines;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setServiceDictionary(DictionaryBean serviceDictionary) {
        this.serviceDictionary = serviceDictionary;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        String stateMachineId = req.getParameter("stateMachineId");
        boolean defaultStatemachine = Boolean.parseBoolean(req.getParameter("default"));
        boolean versionStatemachine = Boolean.parseBoolean(req.getParameter("history"));
        if (stateMachineId == null) {
            log.error("No State Machine to import! stateMachineId is null.");
            return;
        }
        InputStream inputStream = null;
        try {
            if (defaultStatemachine) {
                String path = defaultStatemachines.getPath(stateMachineId);
                inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
            } else if (versionStatemachine) {
                String receivedVersion = req.getParameter("version");
                String nodeRef = req.getParameter("nodeRef");
                NodeRef statemachine = new NodeRef(nodeRef);
                NodeRef statemachines = nodeService.getPrimaryParent(statemachine).getParentRef();
                NodeRef versions = nodeService.getChildByName(statemachines, ContentModel.ASSOC_CONTAINS, "versions");
                NodeRef statemachineVersions = nodeService.getChildByName(versions, ContentModel.ASSOC_CONTAINS, stateMachineId);
                NodeRef version = nodeService.getChildByName(statemachineVersions, ContentModel.ASSOC_CONTAINS, "version_" + receivedVersion);
                NodeRef backup = nodeService.getChildByName(version, ContentModel.ASSOC_CONTAINS, "backup.xml");
                ContentReader reader = contentService.getReader(backup, ContentModel.PROP_CONTENT);
                inputStream = reader.getContentInputStream();
            } else {
                FormData formData = (FormData) req.parseContent();
                FormData.FormField[] fields = formData.getFields();
                inputStream = fields[0].getInputStream();
            }

            if (inputStream != null) {
                XMLImporter xmlImporter = new XMLImporter(inputStream, repositoryStructureHelper, nodeService, serviceDictionary, stateMachineId);
                xmlImporter.importStateMachine();
                xmlImporter.close();
            }
        } catch (Exception e) {
            throw new IOException("Failed to import State Machine!", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

}
