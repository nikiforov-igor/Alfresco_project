package ru.it.lecm.statemachine.editor.script;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.statemachine.LecmWorkflowDeployer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 29.11.12
 * Time: 16:43
 */
public class BPMNDiagramScript extends AbstractWebScript {

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
			String fileName = nodeService.getProperty(statemachine, ContentModel.PROP_NAME) + ".bpmn20.xml";
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
			InputStream is = new BPMNGenerator(statemachineNodeRef, nodeService).generate();
			writer.putContent(is);
			is.close();
			lecmWorkflowDeployer.redeploy();
		} else if (statemachineNodeRef != null && "diagram".equals(type)) {
			res.setContentEncoding("UTF-8");
			res.setContentType("image/png");
			// Create an XML stream writer
			OutputStream output = res.getOutputStream();
			InputStream bpmn = new BPMNGenerator(statemachineNodeRef, nodeService).generate();
			InputStream is = new BPMNGraphGenerator().generate(bpmn);
			byte[] buf = new byte[1 << 8];
			int c;
			while ((c = is.read(buf)) != -1) {
				output.write(buf, 0, c);
			}
			output.flush();
			output.close();
			is.close();
		}
	}
}
