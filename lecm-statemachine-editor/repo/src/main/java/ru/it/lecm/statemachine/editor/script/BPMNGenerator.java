package ru.it.lecm.statemachine.editor.script;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StatemachineActionConstants;
import ru.it.lecm.statemachine.editor.StatemachineEditorModel;

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
import java.util.HashSet;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 30.11.12
 * Time: 10:57
 */
public class BPMNGenerator {
	private static final transient Logger logger = LoggerFactory.getLogger(BPMNGenerator.class);

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

	public BPMNGenerator(String statemachineNodeRef, NodeService nodeService) {
		this.statemachineNodeRef = statemachineNodeRef;
		this.nodeService = nodeService;
	}

	public InputStream generate() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		NodeRef statusesRef = new NodeRef(statemachineNodeRef);
		try {
            logger.debug("Start diagram generating process");
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

			NodeRef rolesRef = nodeService.getChildByName(stateMachine, ContentModel.ASSOC_CONTAINS, "roles-list");
			List<ChildAssociationRef> roles = nodeService.getChildAssocs(rolesRef);

            Element extention = null;

			if (roles.size() > 0) {
				Element extentionElements = doc.createElement("extensionElements");
				startEvent.appendChild(extentionElements);

				extention = doc.createElement("lecm:extension");
				extentionElements.appendChild(extention);

				Element take = doc.createElement("lecm:event");
				take.setAttribute("on", "take");
				extention.appendChild(take);

				Element documentPermissionAction = doc.createElement("lecm:action");
				documentPermissionAction.setAttribute("type", "DocumentPermissionAction");
				for (ChildAssociationRef role : roles) {
                    Object value = nodeService.getProperty(role.getChildRef(), StatemachineEditorModel.PROP_CREATION_DOCUMENT);
					boolean starter =  value == null ? false : (Boolean) value;
					if (starter) {
						Element attribute = doc.createElement("lecm:attribute");
						attribute.setAttribute("name", "role");
						List<AssociationRef> roleAssoc = nodeService.getTargetAssocs(role.getChildRef(), StatemachineEditorModel.ASSOC_ROLE);
						NodeRef roleRef = roleAssoc.get(0).getTargetRef();
						String roleName = (String) nodeService.getProperty(roleRef, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
						attribute.setAttribute("value", roleName);
						documentPermissionAction.appendChild(attribute);
					}
				}

				take.appendChild(documentPermissionAction);

			}

            //Подготавливаем альтернативные пути старта
            NodeRef alternativesRef = nodeService.getChildByName(stateMachine, ContentModel.ASSOC_CONTAINS, "alternatives");
            List<ChildAssociationRef> alternatives = nodeService.getChildAssocs(alternativesRef);

            if (alternatives.size() > 0) {

                if (extention == null) {
                    Element extentionElements = doc.createElement("extensionElements");
                    startEvent.appendChild(extentionElements);
                    extention = doc.createElement("lecm:extension");
                    extentionElements.appendChild(extention);
                }

                Element end = doc.createElement("lecm:event");
                end.setAttribute("on", "end");
                extention.appendChild(end);

                Element choosePath = doc.createElement("lecm:action");
                choosePath.setAttribute("type", "ChooseStartPath");
                end.appendChild(choosePath);

                Element exclusiveGateway = doc.createElement("exclusiveGateway");
                exclusiveGateway.setAttribute("id", "start_gateway");
                process.appendChild(exclusiveGateway);

                process.appendChild(createFlow("start", "start_gateway"));
                for (ChildAssociationRef alternative : alternatives) {
                    List<AssociationRef> targets = nodeService.getTargetAssocs(alternative.getChildRef(), StatemachineEditorModel.ASSOC_ALTERNATIVE_STATUS);
                    if (targets.size() > 0) {
                        String statusVar = "id" + targets.get(0).getTargetRef().getId().replace("-", "");
                        process.appendChild(createFlow("start_gateway", statusVar, "${lecmStartDirection == \"" + statusVar + "\"}"));

                        Element attribute = doc.createElement("attribute");
                        attribute.setAttribute("expression", nodeService.getProperty(alternative.getChildRef(), StatemachineEditorModel.PROP_ALTERNATIVE_EXPRESSION).toString());
                        attribute.setAttribute("value", statusVar);
                        choosePath.appendChild(attribute);
                    }
                }
            }

			List<ChildAssociationRef> statuses = nodeService.getChildAssocs(statusesRef);

            NodeRef statemachines = nodeService.getPrimaryParent(stateMachine).getParentRef();
            NodeRef versions = nodeService.getChildByName(statemachines, ContentModel.ASSOC_CONTAINS, "versions");
            NodeRef statemachineVersions = nodeService.getChildByName(versions, ContentModel.ASSOC_CONTAINS, processId);
            Long versionValue = (Long) nodeService.getProperty(statemachineVersions, StatemachineEditorModel.PROP_LAST_VERSION);

            String version = "" + (++versionValue);

			for (ChildAssociationRef status : statuses) {
				String statusName = (String) nodeService.getProperty(status.getChildRef(), ContentModel.PROP_NAME);
				String statusVar = "id" + status.getChildRef().getId().replace("-", "");

				QName type = nodeService.getType(status.getChildRef());
				if (type.equals(StatemachineEditorModel.TYPE_END_EVENT)) {
					createEndEvent(process, status.getChildRef(), statusName, statusVar, stateMachine);
				} else if (type.equals(StatemachineEditorModel.TYPE_TASK_STATUS)) {
					createStateTask(process, status.getChildRef(), statusName, statusVar, version, alternatives.size() > 0);
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(baos);

			transformer.transform(source, result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
        logger.debug("Diagram generating is finished");
		return new ByteArrayInputStream(baos.toByteArray());
	}

	private void createStateTask(Element process, NodeRef status, String statusName, String statusVar, String version, boolean withAlternatives) {

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
		Boolean startStatus = (Boolean) nodeService.getProperty(status, StatemachineEditorModel.PROP_START_STATUS);
		if (startStatus != null && startStatus) {
            Element startFlow;
            if (withAlternatives) {
                startFlow = createFlow("start_gateway", statusVar);
            } else {
                startFlow = createFlow("start", statusVar);
            }
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

        //Static roles
		NodeRef staticRoles = nodeService.getChildByName(statusRoles, ContentModel.ASSOC_CONTAINS, "static");
		Element staticRoleElement = doc.createElement("static-roles");
		statusRolesElement.appendChild(staticRoleElement);

		List<ChildAssociationRef> permissions = nodeService.getChildAssocs(staticRoles);
		createRoleElement(staticRoleElement, permissions);

        //Dynamic Roles
		NodeRef dynamicRoles = nodeService.getChildByName(statusRoles, ContentModel.ASSOC_CONTAINS, "dynamic");
		Element dynamicRoleElement = doc.createElement("dynamic-roles");
		statusRolesElement.appendChild(dynamicRoleElement);

        permissions = nodeService.getChildAssocs(dynamicRoles);
		createRoleElement(dynamicRoleElement, permissions);

        //Fields
        NodeRef fields = nodeService.getChildByName(status, ContentModel.ASSOC_CONTAINS, "fields");
        if (fields != null) {
            Element fieldsElement = doc.createElement("fields");
            setStatusAction.appendChild(fieldsElement);

            List<ChildAssociationRef> fieldsItems = nodeService.getChildAssocs(fields);
            for (ChildAssociationRef fieldItem : fieldsItems) {
                String name = (String) nodeService.getProperty(fieldItem.getChildRef(), ContentModel.PROP_NAME);
                boolean isEditable = false;

                Object isEditableProperty = nodeService.getProperty(fieldItem.getChildRef(), StatemachineEditorModel.PROP_EDITABLE_FIELD);
                if (isEditableProperty != null) {
                    isEditable = (Boolean) isEditableProperty;
                }
                Element fieldElement = doc.createElement("field");
                fieldElement.setAttribute("name", name.replaceFirst("_", ":"));
                fieldElement.setAttribute("isEditable", "" + Boolean.toString(isEditable));
                fieldsElement.appendChild(fieldElement);
            }
        }

        //Categories
        NodeRef categories = nodeService.getChildByName(status, ContentModel.ASSOC_CONTAINS, "categories");
        if (categories != null) {
            Element categoriesElement = doc.createElement("attachmentCategories");
            setStatusAction.appendChild(categoriesElement);

            List<ChildAssociationRef> categoriesItems = nodeService.getChildAssocs(categories);
            for (ChildAssociationRef categoriesItem : categoriesItems) {
                String name = (String) nodeService.getProperty(categoriesItem.getChildRef(), ContentModel.PROP_NAME);
                boolean isEditable = false;

                Object isEditableProperty = nodeService.getProperty(categoriesItem.getChildRef(), StatemachineEditorModel.PROP_EDITABLE_FIELD);
                if (isEditableProperty != null) {
                    isEditable = (Boolean) isEditableProperty;
                }
                Element categoryElement = doc.createElement("attachmentCategory");
                categoryElement.setAttribute("name", name);
                categoryElement.setAttribute("isEditable", "" + Boolean.toString(isEditable));
                categoriesElement.appendChild(categoryElement);
            }
        }

        attribute = doc.createElement("lecm:attribute");
		attribute.setAttribute("name", "version");
		attribute.setAttribute("value", version);
		setStatusAction.appendChild(attribute);
		start.appendChild(setStatusAction);

		Boolean forDraft = (Boolean) nodeService.getProperty(status, StatemachineEditorModel.PROP_FOR_DRAFT);
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
			String actionId = (String) nodeService.getProperty(action.getChildRef(), StatemachineEditorModel.PROP_ACTION_ID);
			String actionVar = "id" + action.getChildRef().getId().replace("-", "");
			flows.addAll(createEvent(extentionElements, start, statusVar, action, actionId, actionVar));
		}

		//install take actions
		for (ChildAssociationRef action : takeActions) {
			String actionId = (String) nodeService.getProperty(action.getChildRef(), StatemachineEditorModel.PROP_ACTION_ID);
			String actionVar = "id" + action.getChildRef().getId().replace("-", "");
			flows.addAll(createEvent(extentionElements, take, statusVar, action, actionId, actionVar));

		}

		//install end actions
		for (ChildAssociationRef action : endActions) {
			String actionId = (String) nodeService.getProperty(action.getChildRef(), StatemachineEditorModel.PROP_ACTION_ID);
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

    private int getTimerDuration(NodeRef status) {
        try {
            String durationString = (String) nodeService.getProperty(status, StatemachineEditorModel.PROP_TIMER_DURATION);
            return durationString != null ? Integer.parseInt(durationString) : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private Boolean getStopSubWorkflowsProperty(NodeRef nodeRef) {
        Boolean property = (Boolean) nodeService.getProperty(nodeRef, StatemachineEditorModel.PROP_STOP_SUB_WORKFLOWS);
        return property == null ? Boolean.FALSE : property;
    }

    private Element createStopSubWorkflowsAttribute(NodeRef nodeRef) {
        Boolean stopSubWorkflows = getStopSubWorkflowsProperty(nodeRef);
        Element result = doc.createElement("lecm:attribute");
        result.setAttribute("name", StatemachineActionConstants.PROP_STOP_SUBWORKFLOWS);
        result.setAttribute("value", stopSubWorkflows.toString());
        return result;
    }

	private void prepareActions(NodeRef status, ArrayList<ChildAssociationRef> startActions, ArrayList<ChildAssociationRef> takeActions, ArrayList<ChildAssociationRef> endActions) {
		NodeRef actionsRef = nodeService.getChildByName(status, ContentModel.ASSOC_CONTAINS, "actions");
		List<ChildAssociationRef> actions = nodeService.getChildAssocs(actionsRef);
		for (ChildAssociationRef action : actions) {
			String execution = (String) nodeService.getProperty(action.getChildRef(), StatemachineEditorModel.PROP_ACTION_EXECUTION);
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
			String actionId = (String) nodeService.getProperty(action.getChildRef(), StatemachineEditorModel.PROP_ACTION_ID);
			String actionVar = "id" + action.getChildRef().getId().replace("-", "");
			createEvent(extentionElements, start, statusVar, action, actionId, actionVar);
		}

		//install ArchiveDocumentAction
		Element archiveDocumentAction = doc.createElement("lecm:action");
		archiveDocumentAction.setAttribute("type", "ArchiveDocumentAction");
		Element attribute = doc.createElement("lecm:attribute");
		attribute.setAttribute("name", "archiveFolder");
		String archiveFolder = (String) nodeService.getProperty(stateMachine, StatemachineEditorModel.PROP_ARCHIVE_FOLDER);
		attribute.setAttribute("value", archiveFolder);
		archiveDocumentAction.appendChild(attribute);

        attribute = doc.createElement("lecm:attribute");
        attribute.setAttribute("name", "archiveFolderAdditional");
        String archiveFolderAdditional = (String) nodeService.getProperty(stateMachine, StatemachineEditorModel.PROP_ARCHIVE_FOLDER_ADDITIONAL);
        attribute.setAttribute("value", archiveFolderAdditional);
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
		if (StatemachineEditorModel.ACTION_FINISH_STATE_WITH_TRANSITION.equals(actionId)) {
			return createActionFinishStateWithTransition(eventElement, statusVar, action, actionVar);
		} else if (StatemachineEditorModel.ACTION_START_WORKFLOW.equals(actionId)) {
			return createStartWorkflowAction(eventElement, action);
		} else if (StatemachineEditorModel.ACTION_TRANSITION_ACTION.equals(actionId)) {
			return createTransitionAction(eventElement, statusVar, action);
		} else if (StatemachineEditorModel.ACTION_USER_WORKFLOW.equals(actionId)) {
            createUserWorkflowAction(eventElement, action);
			return Collections.EMPTY_LIST;
		} else if (StatemachineEditorModel.ACTION_SCRIPT_ACTION.equals(actionId)) {
            createScriptAction(extensions, eventElement, action);
			return Collections.EMPTY_LIST;
		} else if (StatemachineEditorModel.ACTION_WAIT_FOR_DOCUMENT_CHANGE.equals(actionId)) {
			return createWaitForDocumentChangeEvent(eventElement, statusVar, action, actionVar);
		} else if (StatemachineEditorModel.ACTION_TIMER_ACTION.equals(actionId)) {
			return createTimerEvent(eventElement, statusVar, action, actionVar);
		}
		return Collections.EMPTY_LIST;
	}

    private List<Flow> createTimerEvent(Element eventElement, String statusVar, ChildAssociationRef action, String actionVar) {
        NodeRef actions = action.getParentRef();
        NodeRef status = nodeService.getPrimaryParent(actions).getParentRef();
        int timerDuration = getTimerDuration(status);
        if (timerDuration <= 0) {
            return new ArrayList<Flow>();
        }

        Element actionElement = doc.createElement("lecm:action");
        actionElement.setAttribute("type", StatemachineEditorModel.ACTION_TIMER_ACTION);
        eventElement.appendChild(actionElement);

        List<ChildAssociationRef> expressions = nodeService.getChildAssocs(action.getChildRef());
        if (expressions.size() == 0) {
            return new ArrayList<Flow>();
        }

        Element attribute = doc.createElement("lecm:attribute");
        attribute.setAttribute("name", StatemachineActionConstants.PROP_TIMER_DURATION);
        attribute.setAttribute("value", "" + timerDuration);
        actionElement.appendChild(attribute);

        Element expressionsElement = doc.createElement("lecm:expressions");
        expressionsElement.setAttribute("outputVariable", "var" + actionVar);
        actionElement.appendChild(expressionsElement);

        List<Flow> flows = createTransitionExpressionsFlows(statusVar, actionVar, expressions, expressionsElement);

        return flows;
    }

    private List<Flow> createTransitionExpressionsFlows(String statusVar, String actionVar, List<ChildAssociationRef> expressions, Element expressionsElement) {
        List<Flow> result = new ArrayList<Flow>();
        for (ChildAssociationRef expression : expressions) {
            Element expressionElement = doc.createElement("lecm:expression");
            String expressionValue = (String) nodeService.getProperty(expression.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_EXPRESSION);
            expressionElement.setAttribute("expression", expressionValue);
            List<AssociationRef> statuses = nodeService.getTargetAssocs(expression.getChildRef(), StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
            if (statuses.size() > 0) {
                AssociationRef statusRef = statuses.get(0);
                String target = "id" + statusRef.getTargetRef().getId().replace("-", "");
                expressionElement.setAttribute("outputValue", target);

                Boolean stopSubWorkflows = getStopSubWorkflowsProperty(expression.getChildRef());
                expressionElement.setAttribute(StatemachineActionConstants.PROP_STOP_SUBWORKFLOWS, stopSubWorkflows.toString());

                String var = "var" + actionVar;
                result.add(new Flow(statusVar, target, "${!empty " + var + " && " + var + " == '" + target + "'}"));
            }
            Object script = nodeService.getProperty(expression.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_DOCUMENT_CHANGE_SCRIPT);
            if (script != null) {
                CDATASection cdata = doc.createCDATASection(script.toString());
                expressionElement.appendChild(cdata);
            }
            expressionsElement.appendChild(expressionElement);
        }
        return result;
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
				actionElement.setAttribute("type", StatemachineEditorModel.ACTION_USER_WORKFLOW);
				String id = "id" + workflow.getChildRef().getId().replace("-", "");
				String workflowId = (String) nodeService.getProperty(workflow.getChildRef(), StatemachineEditorModel.PROP_WORKFLOW_ID);
				String workflowLabel = (String) nodeService.getProperty(workflow.getChildRef(), StatemachineEditorModel.PROP_WORKFLOW_LABEL);
				String assignee = (String) nodeService.getProperty(workflow.getChildRef(), StatemachineEditorModel.PROP_ASSIGNEE);

				attribute = doc.createElement("lecm:attribute");
				attribute.setAttribute("id", id);
				attribute.setAttribute("label", workflowLabel);
				attribute.setAttribute("workflowId", workflowId);
				attribute.setAttribute("assignee", assignee);
				actionElement.appendChild(attribute);
                //добавление условий
                appendConditionsElement(attribute, workflow.getChildRef());

                //добавление переменных
				appendWorkflowVariables(attribute, workflow.getChildRef());
			}
			eventElement.appendChild(actionElement);
		}
    }

    private List<Flow> createTransitionAction(Element eventElement, String statusVar, ChildAssociationRef action) {
		List<ChildAssociationRef> expressions = nodeService.getChildAssocs(action.getChildRef());
		List<Flow> flows = new ArrayList<Flow>();
		for (ChildAssociationRef expression : expressions) {
			Element actionElement = doc.createElement("lecm:action");
			actionElement.setAttribute("type", StatemachineEditorModel.ACTION_TRANSITION_ACTION);
			eventElement.appendChild(actionElement);

			String variableName = "id" + expression.getChildRef().getId().replace("-","");
			String expressionValue = (String) nodeService.getProperty(expression.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_EXPRESSION);

			Element attribute = doc.createElement("lecm:attribute");
			attribute.setAttribute("name", "variableName");
			attribute.setAttribute("value", variableName);
			actionElement.appendChild(attribute);

			attribute = doc.createElement("lecm:attribute");
			attribute.setAttribute("name", "expression");
			attribute.setAttribute("value", expressionValue);
			actionElement.appendChild(attribute);

            Element stopSubWorkflowsAttribute = createStopSubWorkflowsAttribute(expression.getChildRef());
            actionElement.appendChild(stopSubWorkflowsAttribute);

			AssociationRef statusRef = nodeService.getTargetAssocs(expression.getChildRef(), StatemachineEditorModel.ASSOC_TRANSITION_STATUS).get(0);
			String target = "id" + statusRef.getTargetRef().getId().replace("-", "");
			flows.add(new Flow(statusVar, target, "${!empty " + variableName + " && " + variableName + "}"));
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
			actionElement.setAttribute("type", StatemachineEditorModel.ACTION_WAIT_FOR_DOCUMENT_CHANGE);
			eventElement.appendChild(actionElement);

			Element expressionsElement = doc.createElement("lecm:expressions");
			expressionsElement.setAttribute("outputVariable", "var" + actionVar);
			actionElement.appendChild(expressionsElement);

            flows = createTransitionExpressionsFlows(statusVar, actionVar, expressions, expressionsElement);
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
			actionElement.setAttribute("type", StatemachineEditorModel.ACTION_START_WORKFLOW);
			String workflowId = (String) nodeService.getProperty(workflow.getChildRef(), StatemachineEditorModel.PROP_WORKFLOW_ID);
			String assignee = (String) nodeService.getProperty(workflow.getChildRef(), StatemachineEditorModel.PROP_ASSIGNEE);
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
		actionElement.setAttribute("type", StatemachineEditorModel.ACTION_FINISH_STATE_WITH_TRANSITION);
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

            Element parameter;
			List<AssociationRef> statusRef = nodeService.getTargetAssocs(transition.getChildRef(), StatemachineEditorModel.ASSOC_TRANSITION_STATUS);
            if (statusRef.size() > 0) {
                String target = "id" + statusRef.get(0).getTargetRef().getId().replace("-", "");

                parameter = doc.createElement("lecm:parameter");
                parameter.setAttribute("name", "variableValue");
                parameter.setAttribute("value", target);
                attribute.appendChild(parameter);

                String var = "var" + actionVar;
                flows.add(new Flow(statusVar, target, "${!empty " + var + " && " + var + " == '" + target + "'}"));
            }

			String labelId = (String) nodeService.getProperty(transition.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_LABEL);
			parameter = doc.createElement("lecm:parameter");
			parameter.setAttribute("name", "labelId");
			parameter.setAttribute("value", labelId);
			attribute.appendChild(parameter);

            Object formType = nodeService.getProperty(transition.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_FORM_TYPE);
            if (formType != null) {
                parameter = doc.createElement("lecm:parameter");
                parameter.setAttribute("name", "formType");
                parameter.setAttribute("value", formType.toString());
                attribute.appendChild(parameter);
            }

            Object formFolder = nodeService.getProperty(transition.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_FORM_FOLDER);
            if (formFolder != null) {
                parameter = doc.createElement("lecm:parameter");
                parameter.setAttribute("name", "formFolder");
                parameter.setAttribute("value", formFolder.toString());
                attribute.appendChild(parameter);
            }

            Object formConnection = nodeService.getProperty(transition.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_FORM_CONNECTION);
            if (formConnection != null) {
                parameter = doc.createElement("lecm:parameter");
                parameter.setAttribute("name", "formConnection");
                parameter.setAttribute("value", formConnection.toString());
                attribute.appendChild(parameter);
            }

            Object isSystemformConnection = nodeService.getProperty(transition.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_IS_SYSTEM_FORM_CONNECTION);
            if (isSystemformConnection != null) {
                parameter = doc.createElement("lecm:parameter");
                parameter.setAttribute("name", "systemFormConnection");
                parameter.setAttribute("value", isSystemformConnection.toString());
                attribute.appendChild(parameter);
            }

            Object transitionScript = nodeService.getProperty(transition.getChildRef(), StatemachineEditorModel.PROP_TRANSITION_SCRIPT);
            if (transitionScript != null) {
                parameter = doc.createElement("lecm:parameter");
                parameter.setAttribute("name", "script");
                CDATASection cdata = doc.createCDATASection(transitionScript.toString());
                parameter.appendChild(cdata);
                attribute.appendChild(parameter);
            }

            Boolean stopSubWorkflows = getStopSubWorkflowsProperty(transition.getChildRef());
            parameter = doc.createElement("lecm:parameter");
            parameter.setAttribute("name", StatemachineActionConstants.PROP_STOP_SUBWORKFLOWS);
            parameter.setAttribute("value", stopSubWorkflows.toString());
			attribute.appendChild(parameter);

            appendConditionsElement(attribute, transition.getChildRef());

			if (StatemachineEditorModel.TYPE_WORKFLOW_TRANSITION.equals(type)) {
				String workflowId = (String) nodeService.getProperty(transition.getChildRef(), StatemachineEditorModel.PROP_WORKFLOW_ID);
				parameter = doc.createElement("lecm:parameter");
				parameter.setAttribute("name", "workflowId");
				parameter.setAttribute("value", workflowId);
				attribute.appendChild(parameter);
			}
            appendWorkflowVariables(attribute, transition.getChildRef());
		}
		return flows;
	}

	private void appendWorkflowVariables(Element attribute, NodeRef workflow) {
        HashSet<QName> types = new HashSet<QName>();
        types.add(StatemachineEditorModel.TYPE_OUTPUT_VARIABLE);
        types.add(StatemachineEditorModel.TYPE_INPUT_VARIABLE);
        types.add(StatemachineEditorModel.TYPE_INPUT_FORM_VARIABLE);
		List<ChildAssociationRef> variables = nodeService.getChildAssocs(workflow, types);
		if (variables.size() > 0) {
			Element workflowVariables = doc.createElement("lecm:workflowVariables");
			attribute.appendChild(workflowVariables);
			for (ChildAssociationRef variable : variables) {
				QName variableType = nodeService.getType(variable.getChildRef());

				if (StatemachineEditorModel.TYPE_OUTPUT_VARIABLE.equals(variableType)) {
					String toType = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_OUTPUT_TO_TYPE);
					String toValue = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_OUTPUT_TO_VALUE);
					String fromType = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_OUTPUT_FROM_TYPE);
					String fromValue = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_OUTPUT_FROM_VALUE);

					Element variableElement = doc.createElement("lecm:output");
					variableElement.setAttribute("toType", toType);
					variableElement.setAttribute("toValue", toValue);
					variableElement.setAttribute("fromType", fromType);
					variableElement.setAttribute("fromValue", fromValue);
					workflowVariables.appendChild(variableElement);
				} else if (StatemachineEditorModel.TYPE_INPUT_VARIABLE.equals(variableType)) {
					String toType = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_INPUT_TO_TYPE);
					String toValue = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_INPUT_TO_VALUE);
					String fromType = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_INPUT_FROM_TYPE);
					String fromValue = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_INPUT_FROM_VALUE);

					Element variableElement = doc.createElement("lecm:input");
					variableElement.setAttribute("toType", toType);
					variableElement.setAttribute("toValue", toValue);
					variableElement.setAttribute("fromType", fromType);
					variableElement.setAttribute("fromValue", fromValue);
					workflowVariables.appendChild(variableElement);
                } else if (StatemachineEditorModel.TYPE_INPUT_FORM_VARIABLE.equals(variableType)) {
                    String toValue = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_FORM_INPUT_TO_VALUE);
                    String fromType = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_FORM_INPUT_FROM_TYPE);
                    String fromValue = (String) nodeService.getProperty(variable.getChildRef(), StatemachineEditorModel.PROP_FORM_INPUT_FROM_VALUE);

                    Element variableElement = doc.createElement("lecm:input");
                    variableElement.setAttribute("toType", "VARIABLE");
                    variableElement.setAttribute("toValue", toValue);
                    variableElement.setAttribute("fromType", fromType);
                    variableElement.setAttribute("fromValue", fromValue);
                    workflowVariables.appendChild(variableElement);
                }
			}
		}
	}

    private void appendConditionsElement(Element attribute, NodeRef parent) {
        HashSet<QName> types = new HashSet<QName>();
        types.add(StatemachineEditorModel.TYPE_CONDITION_ACCESS);
        List<ChildAssociationRef> conditions = nodeService.getChildAssocs(parent, types);
        if (conditions.size() > 0) {
            Element conditionElements = doc.createElement("conditions");
            attribute.appendChild(conditionElements);
            for (ChildAssociationRef condition : conditions) {
                String expression = (String) nodeService.getProperty(condition.getChildRef(), StatemachineEditorModel.PROP_CONDITION);
                String errorMessage = (String) nodeService.getProperty(condition.getChildRef(), StatemachineEditorModel.PROP_CONDITION_ERROR_MESSAGE);
                Boolean hideAction = (Boolean) nodeService.getProperty(condition.getChildRef(), StatemachineEditorModel.PROP_CONDITION_HIDE_ACTION);

                Element conditionElement = doc.createElement("condition");

                Element expressionElement = doc.createElement("expression");
                CDATASection cdata = doc.createCDATASection(expression);
                expressionElement.appendChild(cdata);
                conditionElement.appendChild(expressionElement);

                Element errorMessageElement = doc.createElement("errorMessage");
                cdata = doc.createCDATASection(errorMessage);
                errorMessageElement.appendChild(cdata);
                conditionElement.appendChild(errorMessageElement);

                Element hideActionElement = doc.createElement("hideAction");
                hideActionElement.setTextContent(hideAction.toString());
                conditionElement.appendChild(hideActionElement);

                conditionElements.appendChild(conditionElement);

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
            Element attribute;
            Element actionElement = doc.createElement("lecm:action");
            actionElement.setAttribute("type", StatemachineEditorModel.ACTION_SCRIPT_ACTION);
            eventElement.appendChild(actionElement);

            Element scriptElement = doc.createElement("script");
            String data = (String) nodeService.getProperty(script.getChildRef(), StatemachineEditorModel.PROP_ACTION_SCRIPT);
            CDATASection cdata = doc.createCDATASection(data);
            scriptElement.appendChild(cdata);
            actionElement.appendChild(scriptElement);
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
			AssociationRef role = nodeService.getTargetAssocs(permission.getChildRef(), StatemachineEditorModel.ASSOC_ROLE).get(0);
			String roleName = (String) nodeService.getProperty(role.getTargetRef(), OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
			String permissionTypeValue = (String) nodeService.getProperty(permission.getChildRef(), StatemachineEditorModel.PROP_PERMISSION_TYPE_VALUE);
			Element roleElement = doc.createElement("role");
			roleElement.setAttribute("name", roleName);
			roleElement.setAttribute("privilege", permissionTypeValue);
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
