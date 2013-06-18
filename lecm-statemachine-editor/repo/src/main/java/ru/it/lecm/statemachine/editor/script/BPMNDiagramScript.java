package ru.it.lecm.statemachine.editor.script;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.statemachine.LecmWorkflowDeployer;
import ru.it.lecm.statemachine.editor.StatemachineEditorModel;
import ru.it.lecm.statemachine.editor.export.XMLExporter;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 29.11.12
 * Time: 16:43
 */
public class BPMNDiagramScript extends AbstractWebScript {
	private static final transient Logger logger = LoggerFactory.getLogger(BPMNDiagramScript.class);

	private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
	private NodeService nodeService;
	private LecmWorkflowDeployer lecmWorkflowDeployer;
	private Repository repositoryHelper;
	private ContentService contentService;


	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

	public void setLecmWorkflowDeployer(LecmWorkflowDeployer lecmWorkflowDeployer) {
		this.lecmWorkflowDeployer = lecmWorkflowDeployer;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String statemachineNodeRef = req.getParameter("statemachineNodeRef");
		String type = req.getParameter("type");
		if (statemachineNodeRef != null && "deploy".equals(type)) {
			NodeRef statemachine = new NodeRef(statemachineNodeRef);
			statemachine = nodeService.getPrimaryParent(statemachine).getParentRef();
            String machineName = nodeService.getProperty(statemachine, ContentModel.PROP_NAME).toString();
			String fileName = machineName + ".bpmn20.xml";
			NodeRef companyHome = repositoryHelper.getCompanyHome();
			NodeRef workflowFolder = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, LecmWorkflowDeployer.WORKFLOW_FOLDER);
			NodeRef file = nodeService.getChildByName(workflowFolder, ContentModel.ASSOC_CONTAINS, fileName);
			if (file == null) {
				HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
				props.put(ContentModel.PROP_NAME, fileName);
				ChildAssociationRef childAssocRef = nodeService.createNode(
						workflowFolder,
						ContentModel.ASSOC_CONTAINS,
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(fileName)),
						ContentModel.TYPE_CONTENT,
						props);
				file = childAssocRef.getChildRef();
			}
			ContentWriter writer = contentService.getWriter(file, ContentModel.PROP_CONTENT, true);
			writer.setMimetype("text/xml");
			ByteArrayInputStream is = (ByteArrayInputStream) new BPMNGenerator(statemachineNodeRef, nodeService).generate();
			writer.putContent(is);
			is.close();
            NodeRef statemachines = nodeService.getPrimaryParent(statemachine).getParentRef();
            NodeRef restore = nodeService.getChildByName(statemachines, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.FOLDER_RESTORE);
            if (restore == null) {
                String folderName = StatemachineEditorModel.FOLDER_RESTORE;
                HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
                props.put(ContentModel.PROP_NAME, folderName);
                ChildAssociationRef childAssocRef = nodeService.createNode(
                        statemachines,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(folderName)),
                        ContentModel.TYPE_FOLDER,
                        props);
                restore = childAssocRef.getChildRef();
            }
            ByteArrayOutputStream restoreOut = new ByteArrayOutputStream();
            try {
                XMLExporter exporter = new XMLExporter(restoreOut, nodeService);
                exporter.write(statemachineNodeRef);
            } catch (XMLStreamException e) {
	            logger.error(e.getMessage(), e);
            }
            NodeRef restoreFile = nodeService.getChildByName(restore, ContentModel.ASSOC_CONTAINS, machineName + ".xml");
            if (restoreFile == null) {
                HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
                props.put(ContentModel.PROP_NAME, machineName + ".xml");
                ChildAssociationRef childAssocRef = nodeService.createNode(
                        restore,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(machineName + ".xml")),
                        ContentModel.TYPE_CONTENT,
                        props);
                restoreFile = childAssocRef.getChildRef();
            }
            writer = contentService.getWriter(restoreFile, ContentModel.PROP_CONTENT, true);
            writer.setMimetype("text/xml");
            is = new ByteArrayInputStream(restoreOut.toByteArray());
            writer.putContent(is);
            is.close();
            lecmWorkflowDeployer.redeploy();
		} else if (statemachineNodeRef != null && "diagram".equals(type)) {
			res.setContentType("image/png");
			// Create an XML stream writer
			OutputStream output = res.getOutputStream();
			InputStream bpmn = new BPMNGenerator(statemachineNodeRef, nodeService).generate();
			InputStream is = new BPMNGraphGenerator().generate(bpmn);
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
}
