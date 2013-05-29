package ru.it.lecm.statemachine.editor.script;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;
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
import java.util.HashSet;

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
					String title = element.getAttributes().getNamedItem("name").getNodeValue();
					double width = 6.5 * title.length();
					if (width < 35) {
						width = 35;
					}
					vertex = graph.insertVertex(parent, null, title, 20, 20, width, 35, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/end.png;");
				} else if ("exclusiveGateway".equals(elementName)) {
					vertex = graph.insertVertex(parent, null, "", 20, 20, 39, 39, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/gateway.png;");
				} else if ("userTask".equals(elementName)) {
					String title = element.getAttributes().getNamedItem("name").getNodeValue();
					vertex = graph.insertVertex(parent, null, title, 20, 20, 105, 55, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/task.png;");
				}

				vertexes.put(elementId, vertex);

			}

            HashSet<String> flows = new HashSet<String>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node element = nodeList.item(i);
				String elementName = element.getLocalName();
				if ("sequenceFlow".equals(elementName)) {
					String sourceId = element.getAttributes().getNamedItem("sourceRef").getNodeValue();
					String targetId = element.getAttributes().getNamedItem("targetRef").getNodeValue();
                    String key = sourceId + "->" + targetId;
                    if (!flows.contains(key)) {
                        Object source = vertexes.get(sourceId);
                        Object target = vertexes.get(targetId);
                        graph.insertEdge(parent, null, "", source, target, "rounded=1;");
                        flows.add(key);
                    }
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
    }
}
