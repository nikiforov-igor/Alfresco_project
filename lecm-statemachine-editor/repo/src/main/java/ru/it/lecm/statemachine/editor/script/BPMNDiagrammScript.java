package ru.it.lecm.statemachine.editor.script;

import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: PMelnikov
 * Date: 29.11.12
 * Time: 16:43
 */
public class BPMNDiagrammScript extends AbstractWebScript {

	private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		FileInputStream inputStream = new FileInputStream("D:\\Project\\Application\\LogicECM\\lecm-contracts\\repo\\src\\main\\config\\models\\contracts.bpmn20.xml");

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
		}
	}
}
