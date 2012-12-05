package ru.it.lecm.statemachine.editor.script;

import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: PMelnikov
 * Date: 29.11.12
 * Time: 16:43
 */
public class BPMNDiagramScript extends AbstractWebScript {

	private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
	private NodeService nodeService;


	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String statemachineNodeRef = req.getParameter("statemachineNodeRef");
		String type = req.getParameter("type");
		if (statemachineNodeRef != null && "deploy".equals(type)) {
			InputStream is = new BPMNGenerator(statemachineNodeRef, nodeService).generate();
			FileOutputStream fos = new FileOutputStream("d:/2.xml");
			byte[] buf = new byte[8 * 1024];
			int c = -1;
			while ((c = is.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fos.flush();
			fos.close();
			is.close();
		}

		/*FileInputStream inputStream = new FileInputStream("D:\\Project\\Application\\LogicECM\\lecm-contracts\\repo\\src\\main\\config\\models\\contracts.bpmn20.xml");

		DeploymentEntity deployment = new DeploymentEntity();
		deployment.setId("preview");

		Context.setProcessEngineConfiguration(activitiProcessEngineConfiguration);
		BpmnParser bpmnParser = new BpmnParser(activitiProcessEngineConfiguration.getExpressionManager());
		BpmnParse bpmnParse = bpmnParser
				.createParse()
				.deployment(deployment)
				.sourceInputStream(inputStream);


		bpmnParse.execute();

		ProcessDefinitionEntity processDefinition = bpmnParse.getProcessDefinitions().get(0);
		if (processDefinition != null) {
			InputStream diagramm = ProcessDiagramGenerator.generatePngDiagram(processDefinition);
			FileOutputStream fos = new FileOutputStream("d:/1.png", false);
			byte[] buf = new byte[8 * 1024];
			int bytes = 0;
			while ((bytes = diagramm.read(buf)) != -1) {
				fos.write(buf, 0, bytes);
			}
			fos.flush();
			fos.close();
			diagramm.close();
		}*/
	}
}
