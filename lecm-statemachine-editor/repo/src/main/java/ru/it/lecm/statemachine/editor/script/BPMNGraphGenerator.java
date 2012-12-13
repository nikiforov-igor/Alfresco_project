package ru.it.lecm.statemachine.editor.script;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 06.12.12
 * Time: 14:49
 */
public class BPMNGraphGenerator {

	public InputStream generate(InputStream bpmnInputStream) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			dbfac.setNamespaceAware(true);
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.parse(bpmnInputStream);
			Node root = doc.getFirstChild();
			Node process = root.getFirstChild();

			String processId = process.getAttributes().getNamedItem("id").getNodeValue();

			Element diagram = doc.createElement("bpmndi:BPMNDiagram");
			diagram.setAttribute("id", "BPMNDiagram_" + processId);
			root.appendChild(diagram);

			Element plane = doc.createElement("bpmndi:BPMNPlane");
			plane.setAttribute("bpmnElement", processId);
			plane.setAttribute("id", "BPMNPlane_" + processId);
			diagram.appendChild(plane);

			mxGraph graph = new mxGraph();
			Object parent = graph.getDefaultParent();

			graph.getModel().beginUpdate();

			HashMap<String, Object> vertexes = new HashMap<String, Object>();
			NodeList nodeList = process.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node element = nodeList.item(i);
				String elementName = element.getLocalName();
				String elementId = element.getAttributes().getNamedItem("id").getNodeValue();
				Object vertex = null;
				if ("startEvent".equals(elementName)) {
					vertex = graph.insertVertex(parent, null, "", 20, 20, 35, 35, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/start.png;");
				} else if ("endEvent".equals(elementName)) {
					vertex = graph.insertVertex(parent, null, "", 20, 20, 35, 35, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/end.png;");
				} else if ("exclusiveGateway".equals(elementName)) {
					vertex = graph.insertVertex(parent, null, "", 20, 20, 39, 39, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/gateway.png;");
				} else if ("userTask".equals(elementName)) {
					String title = element.getAttributes().getNamedItem("name").getNodeValue();
					vertex = graph.insertVertex(parent, null, title, 20, 20, 105, 55, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/task.png;");
				}

				vertexes.put(elementId, vertex);

			}

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node element = nodeList.item(i);
				String elementName = element.getLocalName();
				if ("sequenceFlow".equals(elementName)) {
					String sourceId = element.getAttributes().getNamedItem("sourceRef").getNodeValue();
					String targetId = element.getAttributes().getNamedItem("targetRef").getNodeValue();
					Object source = vertexes.get(sourceId);
					Object target = vertexes.get(targetId);
					graph.insertEdge(parent, null, "", source, target, "rounded=1;");
				}
			}

			mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
			layout.setFineTuning(true);
			layout.setOrientation(SwingConstants.WEST);
			layout.execute(parent);
			graph.getModel().endUpdate();
			BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
			ImageIO.write(image, "png", baos);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ByteArrayInputStream(baos.toByteArray());

		/*DeploymentEntity deployment = new DeploymentEntity();
		deployment.setId("preview");

		Context.setProcessEngineConfiguration(configuration);
		BpmnParser bpmnParser = new BpmnParser(configuration.getExpressionManager());
		BpmnParse bpmnParse = bpmnParser
				.createParse()
				.deployment(deployment)
				.sourceInputStream(new ByteArrayInputStream(baos.toByteArray()));

		bpmnParse.execute();

		ProcessDefinitionEntity processDefinition = bpmnParse.getProcessDefinitions().get(0);
		if (processDefinition != null) {
			return ProcessDiagramGenerator.generatePngDiagram(processDefinition);
		} else {
			return null;
		}*/
	}
   /*
	private Element drawShape(Document doc, Shape shape) {
		Element shapeElement = doc.createElement("bpmndi:BPMNShape");
		shapeElement.setAttribute("bpmnElement", shape.getId());
		shapeElement.setAttribute("id", "BPMNShape_" + shape.getId());

		Element bounds = doc.createElement("omgdc:Bounds");
		bounds.setAttribute("height", "" + shape.getHeight());
		bounds.setAttribute("width", "" + shape.getWidth());
		bounds.setAttribute("x", "" + shape.getX());
		bounds.setAttribute("y", "" + shape.getY());
		shapeElement.appendChild(bounds);
		return shapeElement;
	}  */

}
