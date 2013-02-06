package ru.it.lecm.statemachine.editor.script;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

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
import java.util.Collections;
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
	private final static String NAMESPACE_ACTIVITI = "http://activiti.org/bpmn";
	private final static String NAMESPACE_BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI";
	private final static String NAMESPACE_OMGDC = "http://www.omg.org/spec/DD/20100524/DC";
	private final static String NAMESPACE_OMGDI = "http://www.omg.org/spec/DD/20100524/DI";
	private final static String NAMESPACE_LECM = "http://www.it.ru/LogicECM/bpmn/1.0";

	private final static QName TYPE_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "taskStatus");
	private final static QName TYPE_END_EVENT = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "endEvent");

	private final static QName PROP_ACTION_ID = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "actionId");
	private final static QName PROP_ACTION_EXECUTION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "actionExecution");
	private final static QName PROP_STATUS_UUID = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "statusUUID");
	private final static QName PROP_START_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "startStatus");
	private final static QName PROP_FOR_DRAFT = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "forDraft");
	private final static QName PROP_TRANSITION_LABEL = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionLabel");
	private final static QName PROP_WORKFLOW_ID = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "workflowId");
	private final static QName PROP_ASSIGNEE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "assignee");

	private final static QName PROP_INPUT_TO_TYPE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputToType");
	private final static QName PROP_INPUT_TO_VALUE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputToValue");
	private final static QName PROP_INPUT_FROM_TYPE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputFromType");
	private final static QName PROP_INPUT_FROM_VALUE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputFromValue");

	private final static QName PROP_OUTPUT_TO_TYPE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputToType");
	private final static QName PROP_OUTPUT_TO_VALUE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputToValue");
	private final static QName PROP_OUTPUT_FROM_TYPE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputFromType");
	private final static QName PROP_OUTPUT_FROM_VALUE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputFromValue");

	private final static QName PROP_TRANSITION_EXPRESSION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionExpression");
	private final static QName PROP_ACTION_SCRIPT = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "actionScript");
	private final static QName PROP_WORKFLOW_LABEL = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "workflowLabel");
	private final static QName PROP_ARCHIVE_FOLDER = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "archiveFolder");
	private final static QName PROP_CREATION_DOCUMENT = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "creationDocument");
	private final static QName PROP_PERMISSION_TYPE_VALUE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "permissionTypeValue");

	private final static String VARIABLE = "VARIABLE";
	private final static String VALUE = "VALUE";

	private final static QName ASSOC_TRANSITION_STATUS = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionStatus");
	private final static QName ASSOC_ROLE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "role-assoc");

	private final static QName TYPE_WORKFLOW_TRANSITION = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "transitionWorkflow");
	private final static QName TYPE_OUTPUT_VARIABLE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "outputVariable");
	private final static QName TYPE_INPUT_VARIABLE = QName.createQName("http://www.it.ru/logicECM/statemachine/editor/1.0", "inputVariable");

	private final static String ACTION_FINISH_STATE_WITH_TRANSITION = "FinishStateWithTransition";
	private final static String ACTION_SCRIPT_ACTION = "ScriptAction";
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
		NodeRef statusesRef = new NodeRef(statemachineNodeRef);
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

			NodeRef stateMachine = nodeService.getPrimaryParent(statusesRef).getParentRef();

			//create process
			Element process = doc.createElementNS(NAMESPACE_XLNS, "process");
			String processId = (String) nodeService.getProperty(stateMachine, ContentModel.PROP_NAME);
			process.setAttributeNS(NAMESPACE_XSI, "id", processId);
			root.appendChild(process);

			//create start event
			Element startEvent = doc.createElementNS(NAMESPACE_XLNS, "startEvent");
			startEvent.setAttribute("id", "start");
			startEvent.setAttribute("activiti:formKey", "lecm-statemachine:startTask");
			process.appendChild(startEvent);

			NodeRef rolesRef = nodeService.getChildByName(stateMachine, ContentModel.ASSOC_CONTAINS, "roles");
			List<ChildAssociationRef> roles = nodeService.getChildAssocs(rolesRef);

			if (roles.size() > 0) {
				Element extentionElements = doc.createElement("extensionElements");
				startEvent.appendChild(extentionElements);

				Element extention = doc.createElement("lecm:extension");
				extentionElements.appendChild(extention);

				Element take = doc.createElement("lecm:event");
				take.setAttribute("on", "take");
				extention.appendChild(take);

				Element documentPermissionAction = doc.createElement("lecm:action");
				documentPermissionAction.setAttribute("type", "DocumentPermissionAction");
				for (ChildAssociationRef role : roles) {
					boolean starter =  (Boolean) nodeService.getProperty(role.getChildRef(), PROP_CREATION_DOCUMENT);
					if (starter) {
						Element attribute = doc.createElement("lecm:attribute");
						attribute.setAttribute("name", "role");
						List<AssociationRef> roleAssoc = nodeService.getTargetAssocs(role.getChildRef(), ASSOC_ROLE);
						NodeRef roleRef = roleAssoc.get(0).getTargetRef();
						String roleName = (String) nodeService.getProperty(roleRef, ContentModel.PROP_NAME);
						attribute.setAttribute("value", roleName);
						documentPermissionAction.appendChild(attribute);
					}
				}

				take.appendChild(documentPermissionAction);

			}

			List<ChildAssociationRef> statuses = nodeService.getChildAssocs(statusesRef);
			for (ChildAssociationRef status : statuses) {
				String statusName = (String) nodeService.getProperty(status.getChildRef(), ContentModel.PROP_NAME);
				String statusVar = "id" + status.getChildRef().getId().replace("-", "");

				QName type = nodeService.getType(status.getChildRef());
				if (type.equals(TYPE_END_EVENT)) {
					createEndEvent(process, status.getChildRef(), statusName, statusVar, stateMachine);
				} else if (type.equals(TYPE_STATUS)) {
					createStateTask(process, status.getChildRef(), statusName, statusVar);
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

	private void createStateTask(Element process, NodeRef status, String statusName, String statusVar) {
		//create status
		Element statusTask = doc.createElement("userTask");
		statusTask.setAttribute("id", statusVar);
		statusTask.setAttribute("activiti:assignee", "${bpm_assignee.properties.userName}");
		statusTask.setAttribute("name", statusName);
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
		Boolean startStatus = (Boolean) nodeService.getProperty(status, PROP_START_STATUS);
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

		//Roles for status
		NodeRef statusRoles = nodeService.getChildByName(status, ContentModel.ASSOC_CONTAINS, "roles");
		Element statusRolesElement = doc.createElement("roles");
		setStatusAction.appendChild(statusRolesElement);

		NodeRef staticRoles = nodeService.getChildByName(statusRoles, ContentModel.ASSOC_CONTAINS, "static");
		Element staticRoleElement = doc.createElement("static-roles");
		statusRolesElement.appendChild(staticRoleElement);

		List<ChildAssociationRef> permissions = nodeService.getChildAssocs(staticRoles);
		createRoleElement(staticRoleElement, permissions);

		NodeRef dynamicRoles = nodeService.getChildByName(statusRoles, ContentModel.ASSOC_CONTAINS, "dynamic");
		Element dynamicRoleElement = doc.createElement("dynamic-roles");
		statusRolesElement.appendChild(dynamicRoleElement);

		permissions = nodeService.getChildAssocs(dynamicRoles);
		createRoleElement(dynamicRoleElement, permissions);

		String statusUUID = (String) nodeService.getProperty(status, PROP_STATUS_UUID);
		attribute = doc.createElement("lecm:attribute");
		attribute.setAttribute("name", "uuid");
		attribute.setAttribute("value", statusUUID);
		setStatusAction.appendChild(attribute);
		start.appendChild(setStatusAction);

		Boolean forDraft = (Boolean) nodeService.getProperty(status, PROP_FOR_DRAFT);
		attribute = doc.createElement("lecm:attribute");
		attribute.setAttribute("name", "forDraft");
		if (forDraft != null) {
			attribute.setAttribute("value", forDraft.toString());
		} else {
			attribute.setAttribute("value", "false");
		}
		setStatusAction.appendChild(attribute);
		start.appendChild(setStatusAction);

		//Sorting actions by execution type
		ArrayList<ChildAssociationRef> startActions = new ArrayList<ChildAssociationRef>();
		ArrayList<ChildAssociationRef> takeActions = new ArrayList<ChildAssociationRef>();
		ArrayList<ChildAssociationRef> endActions = new ArrayList<ChildAssociationRef>();

		prepareActions(status, startActions, takeActions, endActions);

		List<Flow> flows = new ArrayList<Flow>();

		//install start actions
		for (ChildAssociationRef action : startActions) {
			String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
			String actionVar = "id" + action.getChildRef().getId().replace("-", "");
			flows.addAll(createEvent(extentionElements, start, statusVar, action, actionId, actionVar));
		}

		//install take actions
		for (ChildAssociationRef action : takeActions) {
			String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
			String actionVar = "id" + action.getChildRef().getId().replace("-", "");
			flows.addAll(createEvent(extentionElements, take, statusVar, action, actionId, actionVar));

		}

		//install end actions
		for (ChildAssociationRef action : endActions) {
			String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
			String actionVar = "id" + action.getChildRef().getId().replace("-", "");
			flows.addAll(createEvent(extentionElements, end, statusVar, action, actionId, actionVar));
		}

		if (flows.size() == 1) {
			Flow flow = flows.get(0);
			Element flowElement = createFlow(flow.getSourceRef(), flow.getTargetRef(), flow.getContent());
			process.appendChild(flowElement);
		} else if (flows.size() > 1) {
			Element exclusiveGateway = doc.createElement("exclusiveGateway");
			exclusiveGateway.setAttribute("id", statusVar + "_gateway");
			process.appendChild(exclusiveGateway);

			Element gatewayFlow = createFlow(statusVar, statusVar + "_gateway");
			process.appendChild(gatewayFlow);

			for (Flow flow : flows) {
				Element flowElement = createFlow(flow.getSourceRef() + "_gateway", flow.getTargetRef(), flow.getContent());
				process.appendChild(flowElement);
			}

		}
	}

	private void prepareActions(NodeRef status, ArrayList<ChildAssociationRef> startActions, ArrayList<ChildAssociationRef> takeActions, ArrayList<ChildAssociationRef> endActions) {
		NodeRef actionsRef = nodeService.getChildByName(status, ContentModel.ASSOC_CONTAINS, "actions");
		List<ChildAssociationRef> actions = nodeService.getChildAssocs(actionsRef);
		for (ChildAssociationRef action : actions) {
			String execution = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_EXECUTION);
			if (execution.equals("start")) {
				startActions.add(action);
			} else if (execution.equals("take")) {
				takeActions.add(action);
			} else if (execution.equals("end")) {
				endActions.add(action);
			}
		}
	}

	/**
	 * Создание элемента EndEvent
	 * @param process
	 * @param statusVar
	 * @param stateMachine
	 */
	private void createEndEvent(Element process, NodeRef status, String statusName, String statusVar, NodeRef stateMachine) {
		//create end event
		Element endEvent = doc.createElementNS(NAMESPACE_XLNS, "endEvent");
		endEvent.setAttribute("id", statusVar);
		endEvent.setAttribute("name", statusName);
		process.appendChild(endEvent);

		//create extention
		Element extentionElements = doc.createElement("extensionElements");
		endEvent.appendChild(extentionElements);

		Element extention = doc.createElement("lecm:extension");

		//statemachine start event
		Element start = doc.createElement("lecm:event");
		start.setAttribute("on", "start");
		extention.appendChild(start);

		//statemachine end event
		Element end = doc.createElement("lecm:event");
		end.setAttribute("on", "end");
		extention.appendChild(end);

		ArrayList<ChildAssociationRef> endActions = new ArrayList<ChildAssociationRef>();
		prepareActions(status, null, null, endActions);

		//install end actions
		for (ChildAssociationRef action : endActions) {
			String actionId = (String) nodeService.getProperty(action.getChildRef(), PROP_ACTION_ID);
			String actionVar = "id" + action.getChildRef().getId().replace("-", "");
			createEvent(extentionElements, end, statusVar, action, actionId, actionVar);
		}

		//install ArchiveDocumentAction
		Element archiveDocumentAction = doc.createElement("lecm:action");
		archiveDocumentAction.setAttribute("type", "ArchiveDocumentAction");
		Element attribute = doc.createElement("lecm:attribute");
		attribute.setAttribute("name", "archiveFolder");
		String archiveFolder = (String) nodeService.getProperty(stateMachine, PROP_ARCHIVE_FOLDER);
		attribute.setAttribute("value", archiveFolder);
		archiveDocumentAction.appendChild(attribute);

		attribute = doc.createElement("lecm:attribute");
		attribute.setAttribute("name", "status");
		attribute.setAttribute("value", statusName);
		archiveDocumentAction.appendChild(attribute);

		end.appendChild(archiveDocumentAction);
		extentionElements.appendChild(extention);
	}

	/**
	 * @param eventElement
	 * @param statusVar
	 * @param action
	 * @param actionId
	 * @param actionVar
	 * @return true
	 */
	private List<Flow> createEvent(Element extensions, Element eventElement, String statusVar, ChildAssociationRef action, String actionId, String actionVar) {
		if (ACTION_FINISH_STATE_WITH_TRANSITION.equals(actionId)) {
			return createActionFinishStateWithTransition(eventElement, statusVar, action, actionVar);
		} else if (ACTION_START_WORKFLOW.equals(actionId)) {
			return createStartWorkflowAction(eventElement, action);
		} else if (ACTION_TRANSITION_ACTION.equals(actionId)) {
			return createTransitionAction(statusVar, action);
		} else if (ACTION_USER_WORKFLOW.equals(actionId)) {
            createUserWorkflowAction(eventElement, action);
			return Collections.EMPTY_LIST;
		} else if (ACTION_SCRIPT_ACTION.equals(actionId)) {
            createScriptAction(extensions, eventElement, action);
			return Collections.EMPTY_LIST;
		} else if (ACTION_WAIT_FOR_DOCUMENT_CHANGE.equals(actionId)) {
			return createWaitForDocumentChangeEvent(eventElement, statusVar, action, actionVar);
		}
		return Collections.EMPTY_LIST;
	}

    /*
        <lecm:action type="UserWorkflow" >
            <lecm:attribute label="На подпись" workflowId="activitiReview" assignee="admin" />
        </lecm:action>
    */
    private void createUserWorkflowAction(Element eventElement, ChildAssociationRef action) {
		List<ChildAssociationRef> workflows = nodeService.getChildAssocs(action.getChildRef());
		if (workflows.size() > 0) {
			Element actionElement = doc.createElement("lecm:action");
			for (ChildAssociationRef workflow : workflows) {
				Element attribute;
				actionElement.setAttribute("type", ACTION_USER_WORKFLOW);
				String id = "id" + workflow.getChildRef().getId().replace("-", "");
				String workflowId = (String) nodeService.getProperty(workflow.getChildRef(), PROP_WORKFLOW_ID);
				String workflowLabel = (String) nodeService.getProperty(workflow.getChildRef(), PROP_WORKFLOW_LABEL);
				String assignee = (String) nodeService.getProperty(workflow.getChildRef(), PROP_ASSIGNEE);
				attribute = doc.createElement("lecm:attribute");
				attribute.setAttribute("id", id);
				attribute.setAttribute("label", workflowLabel);
				attribute.setAttribute("workflowId", workflowId);
				attribute.setAttribute("assignee", assignee);
				actionElement.appendChild(attribute);
				appendWorkflowVariables(attribute, workflow.getChildRef());
			}
			eventElement.appendChild(actionElement);
		}
    }


    private List<Flow> createTransitionAction(String statusVar, ChildAssociationRef action) {
		List<ChildAssociationRef> expressions = nodeService.getChildAssocs(action.getChildRef());
		List<Flow> flows = new ArrayList<Flow>();
		for (ChildAssociationRef expression : expressions) {
			String expressionValue = (String) nodeService.getProperty(expression.getChildRef(), PROP_TRANSITION_EXPRESSION);
			AssociationRef statusRef = nodeService.getTargetAssocs(expression.getChildRef(), ASSOC_TRANSITION_STATUS).get(0);
			String target = "id" + statusRef.getTargetRef().getId().replace("-", "");
			flows.add(new Flow(statusVar, target, "${" + expressionValue + "}"));
		}
		return flows;
	}

	/*
			<lecm:action type="WaitForDocumentChange">
			<lecm:expressions outputVariable="signed">
			<lecm:expression expression="#lecm_contract_regnum.equals('3')" outputValue="true"/>
			<lecm:expression expression="#lecm_contract_regnum.equals('5')" outputValue="false"/>
			</lecm:expressions>
			</lecm:action>
	*/
	private List<Flow> createWaitForDocumentChangeEvent(Element eventElement, String statusVar, ChildAssociationRef action, String actionVar) {
		List<Flow> flows = new ArrayList<Flow>();
		List<ChildAssociationRef> expressions = nodeService.getChildAssocs(action.getChildRef());
		if (expressions.size() > 0) {
			Element actionElement = doc.createElement("lecm:action");
			actionElement.setAttribute("type", ACTION_WAIT_FOR_DOCUMENT_CHANGE);
			eventElement.appendChild(actionElement);

			Element expressionsElement = doc.createElement("lecm:expressions");
			expressionsElement.setAttribute("outputVariable", "var" + actionVar);
			actionElement.appendChild(expressionsElement);

			for (ChildAssociationRef expression : expressions) {
				Element expressionElement = doc.createElement("lecm:expression");
				String expressionValue = (String) nodeService.getProperty(expression.getChildRef(), PROP_TRANSITION_EXPRESSION);
				expressionElement.setAttribute("expression", expressionValue);
				AssociationRef statusRef = nodeService.getTargetAssocs(expression.getChildRef(), ASSOC_TRANSITION_STATUS).get(0);
				String target = "id" + statusRef.getTargetRef().getId().replace("-", "");
				expressionElement.setAttribute("outputValue", target);

				expressionsElement.appendChild(expressionElement);
				String var = "var" + actionVar;
				flows.add(new Flow(statusVar, target, "${!empty " + var + " && " + var + " == '" + target + "'}"));
			}
		}
		return flows;
	}


	/**
	 * <lecm:action type="StartWorkflow">
	 * <lecm:attribute name="workflowId" value="activitiReview"/>
	 * <lecm:attribute name="assignee" value="admin"/>
	 * </lecm:action>
	 */
	private List<Flow> createStartWorkflowAction(Element eventElement, ChildAssociationRef action) {
		List<ChildAssociationRef> workflows = nodeService.getChildAssocs(action.getChildRef());
		for (ChildAssociationRef workflow : workflows) {
			Element attribute;
			Element actionElement = doc.createElement("lecm:action");
			actionElement.setAttribute("type", ACTION_START_WORKFLOW);
			String workflowId = (String) nodeService.getProperty(workflow.getChildRef(), PROP_WORKFLOW_ID);
			String assignee = (String) nodeService.getProperty(workflow.getChildRef(), PROP_ASSIGNEE);
			attribute = doc.createElement("lecm:attribute");
			attribute.setAttribute("name", "id");
			attribute.setAttribute("value", "id" + workflow.getChildRef().getId().replace("-", ""));
			actionElement.appendChild(attribute);
			attribute = doc.createElement("lecm:attribute");
			attribute.setAttribute("name", "workflowId");
			attribute.setAttribute("value", workflowId);
			actionElement.appendChild(attribute);
			attribute = doc.createElement("lecm:attribute");
			attribute.setAttribute("name", "assignee");
			attribute.setAttribute("value", assignee);
			actionElement.appendChild(attribute);
			eventElement.appendChild(actionElement);
			appendWorkflowVariables(actionElement, workflow.getChildRef());
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * <lecm:action type="FinishStateWithTransition" variable="trans">
	 * <lecm:attribute name="agreement">
	 * <lecm:parameter name="labelId" value="На согласование"/>
	 * <lecm:parameter name="workflowId" value="activitiReview"/>
	 * <lecm:parameter name="variableValue" value="agreement"/>
	 * <lecm:workflowVariables>
	 * <lecm:input from="inputVariable" to="workflowInputVariable"/>
	 * <lecm:input to="workflowInputVariableValue" value="fromDescriptor"/>
	 * <lecm:output from="wf_reviewOutcome" to="agree"/>
	 * <lecm:output to="stateProcessInputVariableValue" value="fromDescriptor"/>
	 * </lecm:workflowVariables>
	 * </lecm:attribute>
	 * <lecm:attribute name="signing">
	 * <lecm:parameter name="labelId" value="На подписание"/>
	 * <lecm:parameter name="variableValue" value="signing"/>
	 * </lecm:attribute>
	 * </lecm:action>
	 */
	private List<Flow> createActionFinishStateWithTransition(Element take, String statusVar, ChildAssociationRef action, String actionVar) {
		List<Flow> flows = new ArrayList<Flow>();
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

			elementCount++;

			AssociationRef statusRef = nodeService.getTargetAssocs(transition.getChildRef(), ASSOC_TRANSITION_STATUS).get(0);
			String target = "id" + statusRef.getTargetRef().getId().replace("-", "");

			Element parameter = doc.createElement("lecm:parameter");
			parameter.setAttribute("name", "variableValue");
			parameter.setAttribute("value", target);
			attribute.appendChild(parameter);

			String labelId = (String) nodeService.getProperty(transition.getChildRef(), PROP_TRANSITION_LABEL);
			parameter = doc.createElement("lecm:parameter");
			parameter.setAttribute("name", "labelId");
			parameter.setAttribute("value", labelId);
			attribute.appendChild(parameter);

			String var = "var" + actionVar;
			flows.add(new Flow(statusVar, target, "${!empty " + var + " && " + var + " == '" + target + "'}"));

			if (TYPE_WORKFLOW_TRANSITION.equals(type)) {
				String workflowId = (String) nodeService.getProperty(transition.getChildRef(), PROP_WORKFLOW_ID);
				parameter = doc.createElement("lecm:parameter");
				parameter.setAttribute("name", "workflowId");
				parameter.setAttribute("value", workflowId);
				attribute.appendChild(parameter);
				appendWorkflowVariables(attribute, transition.getChildRef());
			}
		}
		return flows;
	}

	private void appendWorkflowVariables(Element attribute, NodeRef workflow) {
		List<ChildAssociationRef> variables = nodeService.getChildAssocs(workflow);
		if (variables.size() > 0) {
			Element workflowVariables = doc.createElement("lecm:workflowVariables");
			attribute.appendChild(workflowVariables);
			for (ChildAssociationRef variable : variables) {
				QName variableType = nodeService.getType(variable.getChildRef());

				if (TYPE_OUTPUT_VARIABLE.equals(variableType)) {
					String toType = (String) nodeService.getProperty(variable.getChildRef(), PROP_OUTPUT_TO_TYPE);
					String toValue = (String) nodeService.getProperty(variable.getChildRef(), PROP_OUTPUT_TO_VALUE);
					String fromType = (String) nodeService.getProperty(variable.getChildRef(), PROP_OUTPUT_FROM_TYPE);
					String fromValue = (String) nodeService.getProperty(variable.getChildRef(), PROP_OUTPUT_FROM_VALUE);

					Element variableElement = doc.createElement("lecm:output");
					variableElement.setAttribute("toType", toType);
					variableElement.setAttribute("toValue", toValue);
					variableElement.setAttribute("fromType", fromType);
					variableElement.setAttribute("fromValue", fromValue);
					workflowVariables.appendChild(variableElement);
				} else if (TYPE_INPUT_VARIABLE.equals(variableType)) {
					String toType = (String) nodeService.getProperty(variable.getChildRef(), PROP_INPUT_TO_TYPE);
					String toValue = (String) nodeService.getProperty(variable.getChildRef(), PROP_INPUT_TO_VALUE);
					String fromType = (String) nodeService.getProperty(variable.getChildRef(), PROP_INPUT_FROM_TYPE);
					String fromValue = (String) nodeService.getProperty(variable.getChildRef(), PROP_INPUT_FROM_VALUE);

					Element variableElement = doc.createElement("lecm:input");
					variableElement.setAttribute("toType", toType);
					variableElement.setAttribute("toValue", toValue);
					variableElement.setAttribute("fromType", fromType);
					variableElement.setAttribute("fromValue", fromValue);
					workflowVariables.appendChild(variableElement);
				}
			}
		}
	}

	/**
     * Метод добавляет расширение Activiti BPM для Alfresco для выполнения произвольного скрипта
     * @param extensions
     * @param eventElement
     * @param action
     */
    private void createScriptAction(Element extensions, Element eventElement, ChildAssociationRef action) {
        List<ChildAssociationRef> scripts = nodeService.getChildAssocs(action.getChildRef());
        for (ChildAssociationRef script : scripts) {
            String on = eventElement.getAttribute("on");
            String eventType = "create";
            if (on.equals("start")) {
                eventType = "create";
            } else if (on.equals("end")) {
                eventType = "complete";
            }
            Element extension = doc.createElement("activiti:taskListener");
            extension.setAttribute("event", eventType);
            extension.setAttribute("class", "org.alfresco.repo.workflow.activiti.tasklistener.ScriptTaskListener");
            extensions.appendChild(extension);

            Element activitiField = doc.createElement("activiti:field");
			activitiField.setAttribute("name", "script");
            Element activitiString = doc.createElement("activiti:string");
            String data = (String) nodeService.getProperty(script.getChildRef(),PROP_ACTION_SCRIPT);
            CDATASection cdata = doc.createCDATASection(data);
            activitiString.appendChild(cdata);
            activitiField.appendChild(activitiString);
            extension.appendChild(activitiField);
        }
    }

	/**
	 * Добавляем коннектор без условия
	 *
	 * @param sourceRef исходный элемент
	 * @param targetRef конечный элемент
	 * @return Flow Element
	 */
	private Element createFlow(String sourceRef, String targetRef) {
		return createFlow(sourceRef, targetRef, null);
	}

	/**
	 * Добавляем коннектор с условным переходом
	 *
	 * @param sourceRef исходный элемент
	 * @param targetRef конечный элемент
	 * @param content   условие перехода
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
			CDATASection cdata = doc.createCDATASection(content);
			expression.appendChild(cdata);
			flow.appendChild(expression);
		}
		return flow;
	}

	/**
	 * Создает список ролей
	 * @param rolesElement основной элемент
	 * @param permissions - список ролей с правами доступа
	 */
	private void createRoleElement(Element rolesElement, List<ChildAssociationRef> permissions) {
		for (ChildAssociationRef permission : permissions) {
			AssociationRef role = nodeService.getTargetAssocs(permission.getChildRef(), ASSOC_ROLE).get(0);
			String roleName = (String) nodeService.getProperty(role.getTargetRef(), OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
			String permissionTypeValue = (String) nodeService.getProperty(permission.getChildRef(), PROP_PERMISSION_TYPE_VALUE);
			Element roleElement = doc.createElement("role");
			roleElement.setAttribute("name", roleName);
			roleElement.setAttribute("permission", permissionTypeValue);
			rolesElement.appendChild(roleElement);
		}
	}


	private class Flow {

		private String sourceRef;
		private String targetRef;
		private String content;

		private Flow(String sourceRef, String targetRef, String content) {
			this.sourceRef = sourceRef;
			this.targetRef = targetRef;
			this.content = content;
		}

		public String getSourceRef() {
			return sourceRef;
		}

		public String getTargetRef() {
			return targetRef;
		}

		public String getContent() {
			return content;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Flow flow = (Flow) o;

			if (content != null ? !content.equals(flow.content) : flow.content != null) return false;
			if (sourceRef != null ? !sourceRef.equals(flow.sourceRef) : flow.sourceRef != null) return false;
			if (targetRef != null ? !targetRef.equals(flow.targetRef) : flow.targetRef != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = sourceRef != null ? sourceRef.hashCode() : 0;
			result = 31 * result + (targetRef != null ? targetRef.hashCode() : 0);
			result = 31 * result + (content != null ? content.hashCode() : 0);
			return result;
		}
	}

}
