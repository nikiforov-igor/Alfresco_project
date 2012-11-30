package ru.it.lecm.statemachine.editor.script;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.it.lecm.base.statemachine.bean.StateMachineActions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 30.11.12
 * Time: 10:57
 */
public class BPMNGenerator {

	private String statemachineNodeRef;
	private NodeService nodeService;

	private final static String NAMESPACE_XLNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private final static String NAMESPACE_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	private final static String NAMESPACE_ACTIVITI ="http://activiti.org/bpmn";
	private final static String NAMESPACE_BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI";
	private final static String NAMESPACE_OMGDC = "http://www.omg.org/spec/DD/20100524/DC";
	private final static String NAMESPACE_OMGDI = "http://www.omg.org/spec/DD/20100524/DI";
	private final static String NAMESPACE_LECM = "http://www.it.ru/LogicECM/bpmn/1.0";

	public BPMNGenerator(String statemachineNodeRef, NodeService nodeService) {
		this.statemachineNodeRef = statemachineNodeRef;
		this.nodeService = nodeService;
	}

	public void generate() {
		QName PROP_ACTION_ID = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "actionId");
		QName PROP_ACTION_EXECUTION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "actionExecution");
		QName PROP_START_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "startStatus");
		QName PROP_USER_TRANSITION_LABEL = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "userTransitionLabel");
		QName PROP_WORKFLOW_ID = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "workflowId");

		QName ASSOC_TRANSITION_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionStatus");

		QName TYPE_USER_TRANSITION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "userTransition");
		QName TYPE_WORKFLOW_TRANSITION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionWorkflow");

		String ACTION_FINISH_STATE_WITH_TRANSITION = "FinishStateWithTransition";
		String ACTION_START_WORKFLOW = "StartWorkflow";
		String ACTION_WAIT_FOR_DOCUMENT_CHANGE = "WaitForDocumentChange";
		String ACTION_USER_WORKFLOW = "UserWorkflow";
		String ACTION_TRANSITION_ACTION = "TransitionAction";
		int elementCount = 0;

		statemachineNodeRef = "workspace://SpacesStore/33fdf975-d059-418c-9b22-0b068b7f3b3e";
		NodeRef nodeRef = new NodeRef(statemachineNodeRef);
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			dbfac.setNamespaceAware(true);
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element root = doc.createElementNS(NAMESPACE_XLNS, "definitions");
			root.setAttribute("xmlns:xsi", NAMESPACE_XSI);
			root.setAttribute("xmlns:activiti", NAMESPACE_ACTIVITI);
			root.setAttribute("xmlns:bpmndi", NAMESPACE_BPMNDI);
			root.setAttribute("xmlns:omgdc", NAMESPACE_OMGDC);
			root.setAttribute("xmlns:omgdi", NAMESPACE_OMGDI);
			root.setAttribute("xmlns:lecm", NAMESPACE_LECM);

			doc.appendChild(root);

			//create process
			Element process = doc.createElementNS(NAMESPACE_XLNS, "process");
			String processId = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			process.setAttributeNS(NAMESPACE_XSI, "id", processId);
			root.appendChild(process);

			//create start event
			Element startEvent = doc.createElementNS(NAMESPACE_XLNS, "startEvent");
			startEvent.setAttribute("id", "start");
			startEvent.setAttribute("activiti:formKey", "cwf:startTask");
			process.appendChild(startEvent);

			List<ChildAssociationRef> statuses = nodeService.getChildAssocs(nodeRef);
			for (ChildAssociationRef status : statuses) {
				String statusName = (String) nodeService.getProperty(status.getChildRef(), ContentModel.PROP_NAME);
				String statusVar = status.getChildRef().getId().replace("-", "_");
				if (statusName.equals("END")) {
					//create end event
					Element endEvent = doc.createElementNS(NAMESPACE_XLNS, "endEvent");
					endEvent.setAttributeNS(NAMESPACE_XLNS, "id", statusVar);
					process.appendChild(endEvent);
					continue;
				}
				//create status
				Element statusTask = doc.createElement("userTask");
				statusTask.setAttribute("id", statusVar);
				statusTask.setAttribute("activiti:assignee", "${bpm_assignee.properties.userName}");
				process.appendChild(statusTask);

				//create extention
				Element extention = doc.createElement("extensionElements");
				statusTask.appendChild(extention);

				//statemachine start event
				Element start = doc.createElement("lecm:event");
				start.setAttribute("on", "start");
				extention.appendChild(start);

				//statemachine take event
				Element take = doc.createElement("lecm:event");
				take.setAttribute("on", "take");
				extention.appendChild(take);

				//statemachine end event
				Element end = doc.createElement("lecm:event");
				end.setAttribute("on", "end");
				extention.appendChild(end);

				//setup start transition
				Boolean startStatus = (Boolean) nodeService.getProperty(status.getChildRef(), PROP_START_STATUS);
				if (startStatus != null && startStatus) {
					Element startFlow = doc.createElement("sequenceFlow");
					startFlow.setAttribute("id", "flow" + elementCount);
					elementCount++;
					startFlow.setAttribute("sourceRef", "start");
					startFlow.setAttribute("targetRef", statusVar);
					process.appendChild(startFlow);
				}

				//install setStatusAction
				Element setStatusAction = doc.createElement("lecm:action");
				setStatusAction.setAttribute("type", "StatusChange");
				Element attribute = doc.createElement("lecm:attribute");
				attribute.setAttribute("name", "status");
				attribute.setAttribute("value", statusName);
				setStatusAction.appendChild(attribute);
				start.appendChild(setStatusAction);

				//Sorting actions by execution type
				ArrayList<ChildAssociationRef> startActions = new ArrayList<ChildAssociationRef>();
				ArrayList<ChildAssociationRef> takeActions = new ArrayList<ChildAssociationRef>();
				ArrayList<ChildAssociationRef> endActions = new ArrayList<ChildAssociationRef>();

				List<ChildAssociationRef> actions = nodeService.getChildAssocs(status.getChildRef());
				StateMachineActions actionsBean = new StateMachineActions();
				for (ChildAssociationRef action : actions) {
					String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
					String execution = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_EXECUTION);
					String realExecution = actionsBean.getRealExecution(actionId, "user", execution);
					if (realExecution.equals("start")) {
						startActions.add(action);
					} else if (realExecution.equals("take")) {
						takeActions.add(action);
					} else if (realExecution.equals("end")) {
						endActions.add(action);
					}
				}

				Element exclusiveGateway = doc.createElement("exclusiveGateway");
				exclusiveGateway.setAttribute("id", statusVar + "_gateway");
				process.appendChild(exclusiveGateway);
				Element gatewayFlow = doc.createElement("sequenceFlow");
				gatewayFlow.setAttribute("id", "flow" + elementCount);
				elementCount++;
				gatewayFlow.setAttribute("sourceRef", statusVar);
				gatewayFlow.setAttribute("targetRef", statusVar + "_gateway");
				process.appendChild(gatewayFlow);

				//install start actions
				System.out.println("start");
				for (ChildAssociationRef action : startActions) {
					String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
					if (ACTION_FINISH_STATE_WITH_TRANSITION.equals(actionId)) {
						System.out.println("ACTION_FINISH_STATE_WITH_TRANSITION");
					} else if (ACTION_START_WORKFLOW.equals(actionId)) {
						System.out.println("ACTION_START_WORKFLOW");
					} else if (ACTION_TRANSITION_ACTION.equals(actionId)) {
						System.out.println("ACTION_TRANSITION_ACTION");
					} else if (ACTION_USER_WORKFLOW.equals(actionId)) {
						System.out.println("ACTION_USER_WORKFLOW");
					} else if (ACTION_WAIT_FOR_DOCUMENT_CHANGE.equals(actionId)) {
						System.out.println("ACTION_WAIT_FOR_DOCUMENT_CHANGE");
					}
				}

				//install take actions
				System.out.println("take");
				for (ChildAssociationRef action : takeActions) {
					String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
					String actionVar = action.getChildRef().getId().replace("-", "_");
					if (ACTION_FINISH_STATE_WITH_TRANSITION.equals(actionId)) {
/*						<lecm:action type="FinishStateWithTransition" variable="trans">
							<lecm:attribute name="agreement">
								<lecm:parameter name="labelId" value="На согласование"/>
								<lecm:parameter name="workflowId" value="activitiReview"/>
								<lecm:parameter name="variableValue" value="agreement"/>
								<lecm:workflowVariables>
									<lecm:input from="inputVariable" to="workflowInputVariable"/>
									<lecm:input to="workflowInputVariableValue" value="fromDescriptor"/>
									<lecm:output from="wf_reviewOutcome" to="agree"/>
									<lecm:output to="stateProcessInputVariableValue" value="fromDescriptor"/>
								</lecm:workflowVariables>
							</lecm:attribute>
							<lecm:attribute name="signing">
								<lecm:parameter name="labelId" value="На подписание"/>
								<lecm:parameter name="variableValue" value="signing"/>
							</lecm:attribute>
						</lecm:action>*/
						Element actionElement = doc.createElement("lecm:action");
						actionElement.setAttribute("type", ACTION_FINISH_STATE_WITH_TRANSITION);
						actionElement.setAttribute("variable", actionVar + "_var");
						take.appendChild(actionElement);

						int attributeCount = 0;
						List<ChildAssociationRef> transitions = nodeService.getChildAssocs(action.getChildRef());
						for (ChildAssociationRef transition : transitions) {
							QName type = nodeService.getType(transition.getChildRef());
							/*
								<lecm:attribute name="signing">
							 */
							attribute = doc.createElement("lecm:attribute");
							attribute.setAttribute("name", statusVar + elementCount);
							elementCount++;
							actionElement.appendChild(attribute);

							String labelId = (String) nodeService.getProperty(transition.getChildRef(), PROP_USER_TRANSITION_LABEL);
							Element parameter = doc.createElement("lecm:parameter");
							parameter.setAttribute("name", "labelId");
							parameter.setAttribute("value", labelId);
							attribute.appendChild(parameter);

							String variableValue = transition.getChildRef().getId().replace("-", "_");
							elementCount++;

							parameter = doc.createElement("lecm:parameter");
							parameter.setAttribute("name", "variableValue");
							parameter.setAttribute("value", variableValue);
							attribute.appendChild(parameter);

							AssociationRef statusRef = nodeService.getTargetAssocs(transition.getChildRef(), ASSOC_TRANSITION_STATUS).get(0);
							String target = statusRef.getTargetRef().getId().replace("-", "_");
							Element flow = doc.createElement("sequenceFlow");
							flow.setAttribute("id", "flow" + elementCount);
							elementCount++;
							flow.setAttribute("sourceRef", statusVar + "_gateway");
							flow.setAttribute("targetRef", target);
							flow.setTextContent("${" + actionVar + "_var = '" + variableValue + "'}");
							process.appendChild(flow);

							if (TYPE_WORKFLOW_TRANSITION.equals(type)) {
								String workflowId = (String) nodeService.getProperty(transition.getChildRef(), PROP_WORKFLOW_ID);
								parameter = doc.createElement("lecm:parameter");
								parameter.setAttribute("name", "workflowId");
								parameter.setAttribute("value", workflowId);
								attribute.appendChild(parameter);
							}
						}
					} else if (ACTION_START_WORKFLOW.equals(actionId)) {
						System.out.println("ACTION_START_WORKFLOW");
					} else if (ACTION_TRANSITION_ACTION.equals(actionId)) {
						System.out.println("ACTION_TRANSITION_ACTION");
					} else if (ACTION_USER_WORKFLOW.equals(actionId)) {
						System.out.println("ACTION_USER_WORKFLOW");
					} else if (ACTION_WAIT_FOR_DOCUMENT_CHANGE.equals(actionId)) {
						System.out.println("ACTION_WAIT_FOR_DOCUMENT_CHANGE");
					}

				}

				//install start actions
				System.out.println("end");
				for (ChildAssociationRef action : endActions) {
					String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
					if (ACTION_FINISH_STATE_WITH_TRANSITION.equals(actionId)) {
						System.out.println("ACTION_FINISH_STATE_WITH_TRANSITION");
					} else if (ACTION_START_WORKFLOW.equals(actionId)) {
						System.out.println("ACTION_START_WORKFLOW");
					} else if (ACTION_TRANSITION_ACTION.equals(actionId)) {
						System.out.println("ACTION_TRANSITION_ACTION");
					} else if (ACTION_USER_WORKFLOW.equals(actionId)) {
						System.out.println("ACTION_USER_WORKFLOW");
					} else if (ACTION_WAIT_FOR_DOCUMENT_CHANGE.equals(actionId)) {
						System.out.println("ACTION_WAIT_FOR_DOCUMENT_CHANGE");
					}
				}

				/*
				FinishStateWithTransition
				StartWorkflow
				WaitForDocumentChange
				UserWorkflow
				TransitionAction
                */
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("d:/1.xml"));

			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
