package ru.it.lecm.statemachine.editor.script;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * User: PMelnikov
 * Date: 06.12.12
 * Time: 14:49
 */
public class BPMNGraphGenerator {
    private static final transient Logger logger = LoggerFactory.getLogger(BPMNGraphGenerator.class);
	private ProcessEngine activitiProcessEngine;

	public void setActivitiProcessEngine(ProcessEngine activitiProcessEngine) {
		this.activitiProcessEngine = activitiProcessEngine;
	}

    public InputStream generate(InputStream bpmnInputStream) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            dbfac.setNamespaceAware(true);
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.parse(bpmnInputStream);
            Node root = doc.getFirstChild();
            Node process = null;

            //т.к. message может быть объявлен до process, то ищем process
            NodeList childNodes = root.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                if (childNodes.item(j).getLocalName().equals("process")) {
                    process = childNodes.item(j);
                }
            }

            mxGraph graph = new mxGraph();
            Object parent = graph.getDefaultParent();

            graph.getModel().beginUpdate();

            HashMap<String, Object> vertexes = new HashMap<>();
            HashMap<String, String> boundaryEvents = new HashMap<>();
            if (process != null) {
                NodeList nodeList = process.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node element = nodeList.item(i);
                    String elementName = element.getLocalName();
                    String elementId = element.getAttributes().getNamedItem("id").getNodeValue();
                    Object vertex = null;
                    switch (elementName) {
                        case "startEvent":
                            vertex = graph.insertVertex(parent, null, "", 20, 20, 35, 35, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/start.png;");
                            break;
                        case "endEvent": {
                            String title = element.getAttributes().getNamedItem("name").getNodeValue();
                            double width = 6.5 * title.length();
                            if (width < 35) {
                                width = 35;
                            }
                            vertex = graph.insertVertex(parent, null, title, 20, 20, width, 35, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/end.png;");
                            break;
                        }
                        case "exclusiveGateway":
                            vertex = graph.insertVertex(parent, null, "", 20, 20, 39, 39, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/gateway.png;");
                            break;
                        case "userTask": {
                            String title = element.getAttributes().getNamedItem("name").getNodeValue();
                            vertex = graph.insertVertex(parent, null, title, 20, 20, 105, 55, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/task.png;");
                            break;
                        }
                        case "boundaryEvent": {
                            String id = element.getAttributes().getNamedItem("id").getNodeValue();
                            String attachedToRef = element.getAttributes().getNamedItem("attachedToRef").getNodeValue();
                            boundaryEvents.put(id, attachedToRef);  //запоминаем, для подмены источника
                            break;
                        }
                    }

                    if (vertex != null) {
                        vertexes.put(elementId, vertex);
                    }

                }

                HashSet<String> flows = new HashSet<>();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node element = nodeList.item(i);
                    String elementName = element.getLocalName();
                    if ("sequenceFlow".equals(elementName)) {
                        String sourceId = element.getAttributes().getNamedItem("sourceRef").getNodeValue();
                        if (boundaryEvents.containsKey(sourceId)) {
                            //подменяем источник
                            sourceId = boundaryEvents.get(sourceId);
                        }
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
            }

            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
            layout.setFineTuning(true);
            layout.setOrientation(SwingConstants.WEST);
            layout.execute(parent);
            graph.getModel().endUpdate();
            BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
            ImageIO.write(image, "png", baos);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error(e.getMessage(), e);
        }

        return new ByteArrayInputStream(baos.toByteArray());
    }

	public InputStream generateByModel(BpmnModel model,String currentStatus, List<String> history){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		HashMap<String, Object> vertexes = new HashMap<>();
		HashMap<String, String> boundaryEvents = new HashMap<>();
		org.activiti.bpmn.model.Process process = model.getMainProcess();
		Collection<FlowElement> elements = process.getFlowElements();
		for (FlowElement flowElement : elements) {
			Object vertex = null;

			String image = "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/task.png;";
			if(flowElement.getName() != null && history.contains(flowElement.getName())){
				image = "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/task_prev.png;";
			}
			if(flowElement.getName() != null && flowElement.getName().equals(currentStatus)) {
				image = "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/task_active.png;";
			}
			Class type = flowElement.getClass();
			if(type.equals(StartEvent.class)) {
				vertex = graph.insertVertex(parent, null, "", 20, 20, 35, 35, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/start.png;");
			} else if (type.equals(EndEvent.class)){
				String title = flowElement.getName();
				double width = 6.5 * title.length();
				if (width < 35) {
					width = 35;
				}
				vertex = graph.insertVertex(parent, null, title, 20, 20, width, 35, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/end.png;");
			} else if (type.equals(ExclusiveGateway.class)) {
				vertex = graph.insertVertex(parent, null, "", 20, 20, 39, 39, "shape=image;image=/alfresco/templates/webscripts/ru/it/lecm/statemachine/editor/images/gateway.png;");
			} else if (type.equals(UserTask.class)){
				String title = flowElement.getName();
				vertex = graph.insertVertex(parent, null, title, 20, 20, 105, 55, image);
			} else if(type.equals(BoundaryEvent.class)) {
				BoundaryEvent bEvent = (BoundaryEvent) flowElement;
				String id = bEvent.getId();
				String attachedToRef = bEvent.getAttachedToRefId();
				boundaryEvents.put(id, attachedToRef);  //запоминаем, для подмены источника
			}
			vertexes.put(flowElement.getId(), vertex);
		}
		HashSet<String> flows = new HashSet<>();
		for(FlowElement flowElement : elements) {
			if (flowElement.getClass().equals(SequenceFlow.class)) {
				SequenceFlow sFlow = (SequenceFlow) flowElement;
				String sourceId = sFlow.getSourceRef();
				if (boundaryEvents.containsKey(sourceId)) {
					//подменяем источник
					sourceId = boundaryEvents.get(sourceId);
				}
				String targetId = sFlow.getTargetRef();
				String key = sourceId + "->" + targetId;
				if (!flows.contains(key)) {
					Object source = vertexes.get(sourceId);
					Object target = vertexes.get(targetId);
					graph.insertEdge(parent, null, "", source, target, "rounded=1;");
					flows.add(key);
				}
			}
		}

		graph.getModel().beginUpdate();


		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setFineTuning(true);
		layout.setOrientation(SwingConstants.WEST);
		layout.execute(parent);
		graph.getModel().endUpdate();
		BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
		try {
			ImageIO.write(image, "png", baos);
		} catch (IOException ex) {
			logger.error("Something gone wrong, while writting image...", ex);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
