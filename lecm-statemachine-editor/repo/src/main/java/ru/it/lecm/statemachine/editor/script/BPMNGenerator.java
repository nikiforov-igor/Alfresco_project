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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
	private Document doc;
	private int elementCount = 0;

	private final static String NAMESPACE_XLNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private final static String NAMESPACE_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	private final static String NAMESPACE_ACTIVITI ="http://activiti.org/bpmn";
	private final static String NAMESPACE_BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI";
	private final static String NAMESPACE_OMGDC = "http://www.omg.org/spec/DD/20100524/DC";
	private final static String NAMESPACE_OMGDI = "http://www.omg.org/spec/DD/20100524/DI";
	private final static String NAMESPACE_LECM = "http://www.it.ru/LogicECM/bpmn/1.0";

	private final static QName PROP_ACTION_ID = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "actionId");
	private final static QName PROP_ACTION_EXECUTION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "actionExecution");
	private final static QName PROP_START_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "startStatus");
	private final static QName PROP_USER_TRANSITION_LABEL = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "userTransitionLabel");
	private final static QName PROP_WORKFLOW_TRANSITION_LABEL = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionWorkflowLabel");
	private final static QName PROP_WORKFLOW_ID = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "workflowId");
	private final static QName PROP_ASSIGNEE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "assignee");
	private final static QName PROP_INPUT_WORKFLOW_TO_VARIABLE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputWorkflowToVariable");
	private final static QName PROP_INPUT_WORKFLOW_VARIABLE_TYPE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputWorkflowVariableType");
	private final static QName PROP_INPUT_WORKFLOW_VARIABLE_VALUE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputWorkflowVariableValue");
	private final static QName PROP_OUTPUT_WORKFLOW_TO_VARIABLE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputWorkflowToVariable");
	private final static QName PROP_OUTPUT_WORKFLOW_VARIABLE_TYPE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputWorkflowVariableType");
	private final static QName PROP_OUTPUT_WORKFLOW_VARIABLE_VALUE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputWorkflowVariableValue");
	private final static QName PROP_TRANSITION_EXPRESSION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionExpression");

	private final static String VARIABLE = "VARIABLE";
	private final static String VALUE = "VALUE";

	private final static QName ASSOC_TRANSITION_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionStatus");

	private final static QName TYPE_WORKFLOW_TRANSITION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionWorkflow");
	private final static QName TYPE_OUTPUT_WORKFLOW_VARIABLE_DATA = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputWorkflowVariableData");
	private final static QName TYPE_INPUT_WORKFLOW_VARIABLE_DATA = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputWorkflowVariableData");

	private final static String ACTION_FINISH_STATE_WITH_TRANSITION = "FinishStateWithTransition";
	private final static String ACTION_START_WORKFLOW = "StartWorkflow";
	private final static String ACTION_WAIT_FOR_DOCUMENT_CHANGE = "WaitForDocumentChange";
	private final static String ACTION_USER_WORKFLOW = "UserWorkflow";
	private final static String ACTION_TRANSITION_ACTION = "TransitionAction";

	public BPMNGenerator(String statemachineNodeRef, NodeService nodeService) {
		this.statemachineNodeRef = statemachineNodeRef;
		this.nodeService = nodeService;
	}

	public InputStream generate() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		NodeRef nodeRef = new NodeRef(statemachineNodeRef);
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			dbfac.setNamespaceAware(true);
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element root = doc.createElementNS(NAMESPACE_XLNS, "definitions");
			root.setAttribute("xmlns:xsi", NAMESPACE_XSI);
			root.setAttribute("xmlns:activiti", NAMESPACE_ACTIVITI);
			root.setAttribute("xmlns:bpmndi", NAMESPACE_BPMNDI);
			root.setAttribute("xmlns:omgdc", NAMESPACE_OMGDC);
			root.setAttribute("xmlns:omgdi", NAMESPACE_OMGDI);
			root.setAttribute("xmlns:lecm", NAMESPACE_LECM);
			root.setAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema");
			root.setAttribute("expressionLanguage", "http://www.w3.org/1999/XPath");
			root.setAttribute("targetNamespace", "http://www.activiti.org/test");

			doc.appendChild(root);

			//create process
			Element process = doc.createElementNS(NAMESPACE_XLNS, "process");
			String processId = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			process.setAttributeNS(NAMESPACE_XSI, "id", processId);
			root.appendChild(process);

			//create start event
			Element startEvent = doc.createElementNS(NAMESPACE_XLNS, "startEvent");
			startEvent.setAttribute("id", "start");
			startEvent.setAttribute("activiti:formKey", "lecm-statemachine:startTask");
			process.appendChild(startEvent);

			List<ChildAssociationRef> statuses = nodeService.getChildAssocs(nodeRef);
			for (ChildAssociationRef status : statuses) {
				String statusName = (String) nodeService.getProperty(status.getChildRef(), ContentModel.PROP_NAME);
				String statusVar = "id" + status.getChildRef().getId().replace("-", "");
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
				Element extentionElements = doc.createElement("extensionElements");
				statusTask.appendChild(extentionElements);

				Element extention = doc.createElement("lecm:extension");
				extentionElements.appendChild(extention);

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
					Element startFlow = createFlow("start", statusVar);
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

				Element gatewayFlow = createFlow(statusVar, statusVar + "_gateway");
				process.appendChild(gatewayFlow);

				//install start actions
				for (ChildAssociationRef action : startActions) {
					String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
					String actionVar = "id" + action.getChildRef().getId().replace("-", "");
					createEvent(process, start, statusVar, action, actionId, actionVar);
				}

				//install take actions
				for (ChildAssociationRef action : takeActions) {
					String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
					String actionVar = "id" + action.getChildRef().getId().replace("-", "");
					createEvent(process, take, statusVar, action, actionId, actionVar);

				}

				//install end actions
				for (ChildAssociationRef action : endActions) {
					String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
					String actionVar = "id" + action.getChildRef().getId().replace("-", "");
					createEvent(process, end, statusVar, action, actionId, actionVar);
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(baos);

			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}

	private void createEvent(Element process, Element eventElement, String statusVar, ChildAssociationRef action, String actionId, String actionVar) {
		if (ACTION_FINISH_STATE_WITH_TRANSITION.equals(actionId)) {
			createActionFinishStateWithTransition(process, eventElement, statusVar, action, actionVar);
		} else if (ACTION_START_WORKFLOW.equals(actionId)) {
			createaStartWorkflowAction(eventElement, action);
		} else if (ACTION_TRANSITION_ACTION.equals(actionId)) {
			createTransitionAction(process, statusVar, action);
		} else if (ACTION_USER_WORKFLOW.equals(actionId)) {
			System.out.println("ACTION_USER_WORKFLOW");
		} else if (ACTION_WAIT_FOR_DOCUMENT_CHANGE.equals(actionId)) {
			createWaitForDocumentChangeEvent(process, eventElement, statusVar, action, actionVar);
		}
	}

	private void createTransitionAction(Element process, String statusVar, ChildAssociationRef action) {
		List<ChildAssociationRef> expressions = nodeService.getChildAssocs(action.getChildRef());
		for (ChildAssociationRef expression : expressions) {
			String expressionValue = (String) nodeService.getProperty(expression.getChildRef(), PROP_TRANSITION_EXPRESSION);
			AssociationRef statusRef = nodeService.getTargetAssocs(expression.getChildRef(), ASSOC_TRANSITION_STATUS).get(0);
			String target = "id" + statusRef.getTargetRef().getId().replace("-", "");
			Element flow = createFlow(statusVar + "_gateway", target, "${" + expressionValue + "}");
			process.appendChild(flow);
		}
	}

	/*
			<lecm:action type="WaitForDocumentChange">
			<lecm:expressions outputVariable="signed">
			<lecm:expression expression="#lecm_contract_regnum.equals('3')" outputValue="true"/>
			<lecm:expression expression="#lecm_contract_regnum.equals('5')" outputValue="false"/>
			</lecm:expressions>
			</lecm:action>
	*/
	private void createWaitForDocumentChangeEvent(Element process, Element eventElement, String statusVar, ChildAssociationRef action, String actionVar) {
		List<ChildAssociationRef> expressions = nodeService.getChildAssocs(action.getChildRef());
		if (expressions.size() > 0) {
			Element actionElement = doc.createElement("lecm:action");
			actionElement.setAttribute("type", ACTION_WAIT_FOR_DOCUMENT_CHANGE);
			eventElement.appendChild(actionElement);

			Element expressionsElement = doc.createElement("lecm:expressions");
			expressionsElement.setAttribute("outputVariable","var" + actionVar);
			actionElement.appendChild(expressionsElement);

			for (ChildAssociationRef expression : expressions) {
				Element expressionElement = doc.createElement("lecm:expression");
				String expressionValue = (String) nodeService.getProperty(expression.getChildRef(), PROP_TRANSITION_EXPRESSION);
				expressionElement.setAttribute("expression", expressionValue);
				AssociationRef statusRef = nodeService.getTargetAssocs(expression.getChildRef(), ASSOC_TRANSITION_STATUS).get(0);
				String target = "id" + statusRef.getTargetRef().getId().replace("-", "");
				expressionElement.setAttribute("outputValue", target);

				expressionsElement.appendChild(expressionElement);
				Element flow = createFlow(statusVar + "_gateway", target, "${var" + actionVar + " == '" + target + "'}");
				process.appendChild(flow);
			}
		}
	}


	/**
		  <lecm:action type="StartWorkflow">
		  <lecm:attribute name="workflowId" value="activitiReview"/>
		  <lecm:attribute name="assignee" value="admin"/>
		  </lecm:action>
	 */
	private void createaStartWorkflowAction(Element eventElement, ChildAssociationRef action) {
		List<ChildAssociationRef> workflows = nodeService.getChildAssocs(action.getChildRef());
		for (ChildAssociationRef workflow : workflows) {
			Element attribute;
			Element actionElement = doc.createElement("lecm:action");
			actionElement.setAttribute("type", ACTION_START_WORKFLOW);
			String workflowId = (String) nodeService.getProperty(workflow.getChildRef(), PROP_WORKFLOW_ID);
			String assignee = (String) nodeService.getProperty(workflow.getChildRef(), PROP_ASSIGNEE);
			attribute = doc.createElement("lecm:attribute");
			attribute.setAttribute("workflowId", workflowId);
			actionElement.appendChild(attribute);
			attribute = doc.createElement("lecm:attribute");
			attribute.setAttribute("assignee", assignee);
			actionElement.appendChild(attribute);
			eventElement.appendChild(actionElement);
		}
	}

	/**
	    <lecm:action type="FinishStateWithTransition" variable="trans">
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
	private void createActionFinishStateWithTransition(Element process, Element take, String statusVar, ChildAssociationRef action, String actionVar) {
		Element attribute;
		Element actionElement = doc.createElement("lecm:action");
		actionElement.setAttribute("type", ACTION_FINISH_STATE_WITH_TRANSITION);
		actionElement.setAttribute("variable", "var" + actionVar);
		take.appendChild(actionElement);

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

			String variableValue = "id" + transition.getChildRef().getId().replace("-", "");
			elementCount++;

			Element parameter = doc.createElement("lecm:parameter");
			parameter.setAttribute("name", "variableValue");
			parameter.setAttribute("value", variableValue);
			attribute.appendChild(parameter);

			AssociationRef statusRef = nodeService.getTargetAssocs(transition.getChildRef(), ASSOC_TRANSITION_STATUS).get(0);
			String target = "id" + statusRef.getTargetRef().getId().replace("-", "");
			Element flow = createFlow(statusVar + "_gateway", target, "${var" + actionVar + " == '" + variableValue + "'}");
			process.appendChild(flow);

			if (TYPE_WORKFLOW_TRANSITION.equals(type)) {
				String labelId = (String) nodeService.getProperty(transition.getChildRef(), PROP_WORKFLOW_TRANSITION_LABEL);
				parameter = doc.createElement("lecm:parameter");
				parameter.setAttribute("name", "labelId");
				parameter.setAttribute("value", labelId);
				attribute.appendChild(parameter);

				String workflowId = (String) nodeService.getProperty(transition.getChildRef(), PROP_WORKFLOW_ID);
				parameter = doc.createElement("lecm:parameter");
				parameter.setAttribute("name", "workflowId");
				parameter.setAttribute("value", workflowId);
				attribute.appendChild(parameter);
				List<ChildAssociationRef> variables = nodeService.getChildAssocs(transition.getChildRef());
				if (variables.size() > 0) {
					Element workflowVariables = doc.createElement("lecm:workflowVariables");
					attribute.appendChild(workflowVariables);
					for (ChildAssociationRef variable : variables) {
						QName variableType = nodeService.getType(variable.getChildRef());

						if (TYPE_OUTPUT_WORKFLOW_VARIABLE_DATA.equals(variableType)) {
							String toVariable = (String) nodeService.getProperty(variable.getChildRef(), PROP_OUTPUT_WORKFLOW_TO_VARIABLE);
							String variableOutputType = (String) nodeService.getProperty(variable.getChildRef(), PROP_OUTPUT_WORKFLOW_VARIABLE_TYPE);
							String value = (String) nodeService.getProperty(variable.getChildRef(), PROP_OUTPUT_WORKFLOW_VARIABLE_VALUE);
							if (VARIABLE.equals(variableOutputType)) {
								Element variableElement = doc.createElement("lecm:output");
								variableElement.setAttribute("from", toVariable);
								variableElement.setAttribute("to", value);
								workflowVariables.appendChild(variableElement);
							} else if (VALUE.equals(variableOutputType)) {
								Element variableElement = doc.createElement("lecm:output");
								variableElement.setAttribute("to", toVariable);
								variableElement.setAttribute("value", value);
								workflowVariables.appendChild(variableElement);
							}
						} else if (TYPE_INPUT_WORKFLOW_VARIABLE_DATA.equals(variableType)) {
							String toVariable = (String) nodeService.getProperty(variable.getChildRef(), PROP_INPUT_WORKFLOW_TO_VARIABLE);
							String variableInputType = (String) nodeService.getProperty(variable.getChildRef(), PROP_INPUT_WORKFLOW_VARIABLE_TYPE);
							String value = (String) nodeService.getProperty(variable.getChildRef(), PROP_INPUT_WORKFLOW_VARIABLE_VALUE);
							if (VARIABLE.equals(variableInputType)) {
								Element variableElement = doc.createElement("lecm:input");
								variableElement.setAttribute("from", toVariable);
								variableElement.setAttribute("to", value);
								workflowVariables.appendChild(variableElement);
							} else if (VALUE.equals(variableInputType)) {
								Element variableElement = doc.createElement("lecm:input");
								variableElement.setAttribute("to", toVariable);
								variableElement.setAttribute("value", value);
								workflowVariables.appendChild(variableElement);
							}
						}
					}
				}
			} else {
				String labelId = (String) nodeService.getProperty(transition.getChildRef(), PROP_USER_TRANSITION_LABEL);
				parameter = doc.createElement("lecm:parameter");
				parameter.setAttribute("name", "labelId");
				parameter.setAttribute("value", labelId);
				attribute.appendChild(parameter);
			}
		}
	}

	/**
	 * Добавляем коннектор без условия
	 * @param sourceRef исходный элемент
	 * @param targetRef конечный элемент
	 * @return Flow Element
	 */
	private Element createFlow(String sourceRef, String targetRef) {
		return createFlow(sourceRef, targetRef, null);
	}

	/**
	 * Добавляем коннектор с условным переходом
	 * @param sourceRef исходный элемент
	 * @param targetRef конечный элемент
	 * @param content условие перехода
	 * @return Flow Element
	 */
	private Element createFlow(String sourceRef, String targetRef, String content) {
		Element flow = doc.createElement("sequenceFlow");
		flow.setAttribute("id", "flow" + elementCount);
		elementCount++;
		flow.setAttribute("sourceRef", sourceRef);
		flow.setAttribute("targetRef", targetRef);
		if (content != null) {
			Element expression = doc.createElement("conditionExpression");
			expression.setAttribute("xsi:type", "tFormalExpression");
			expression.setTextContent(content);
			flow.appendChild(expression);
		}
		return flow;
	}

}
