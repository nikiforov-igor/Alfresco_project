package ru.it.lecm.statemachine.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 11:08
 */
public class StateMachineCreateDocumentPolicy implements NodeServicePolicies.OnCreateNodePolicy {

	private ServiceRegistry serviceRegistry;
	private PolicyComponent policyComponent;

	private String[] supportNodeTypes;
	private NamespaceService nsService;

	final static Logger logger = LoggerFactory.getLogger(StateMachineCreateDocumentPolicy.class);

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public String[] getSupportNodeTypes() {
		return supportNodeTypes;
	}

	public void setSupportNodeTypes(String[] supportNodeTypes) {
		this.supportNodeTypes = supportNodeTypes;
	}

	public NamespaceService getNsService() {
		return nsService;
	}

	public void setNsService(NamespaceService nameService) {
		this.nsService = nameService;
	}

	public final void init() {
		logger.debug( "Installing Policy ...");

		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		PropertyCheck.mandatory(this, "nsService", nsService);
		PropertyCheck.mandatory(this, "supportNodeTypes", supportNodeTypes);

		// policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME, StatemachineModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));
		final Collection<QName> list = makeTypesList();
		if (list == null || list.isEmpty()) {
			logger.warn( "no 'supportNodeTypes' configured -> policy disabled");
			return;
		}
		for (QName typeName: list) {
			policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME, typeName, new JavaBehaviour(this, "onCreateNode"));
			logger.info( String.format("Policy installed for node type {%s}", typeName));
		}
	}

	/**
	 * Построить список типов как QName вместо строковых ссылок...
	 * @return
	 */
	private List<QName> makeTypesList() {
		if (supportNodeTypes == null || supportNodeTypes.length < 1)
			return null;

		final List<QName> result = new ArrayList<QName>();
		for(String qname: this.supportNodeTypes)
			result.add(QName.createQName(qname, this.nsService));
		return result;
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeService nodeService = serviceRegistry.getNodeService();

		final NodeRef docRef = childAssocRef.getChildRef();

		QName type = nodeService.getType(docRef);
		List<String> prefixes = (List<String>) serviceRegistry.getNamespaceService().getPrefixes(type.getNamespaceURI());
		String stateMashineId = prefixes.get(0) + "_" + type.getLocalName();
		if (stateMashineId != null) {
			//append status aspect to new document
			HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
			aspectProps.put(StatemachineModel.PROP_STATUS, "NEW");
			nodeService.addAspect( docRef, StatemachineModel.ASPECT_STATUS, aspectProps);

			PersonService personService = serviceRegistry.getPersonService();
			NodeRef assigneeNodeRef = personService.getPerson("workflow");

			if (assigneeNodeRef == null) {
				throw new IllegalStateException("no system user 'workflow' found -> use admin console and add this user");
			}

			Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>(16);

			WorkflowService workflowService = serviceRegistry.getWorkflowService();

			NodeRef stateProcessPackage = workflowService.createPackage(null);
			nodeService.addChild(stateProcessPackage, docRef, ContentModel.ASSOC_CONTAINS, type);

			workflowProps.put(WorkflowModel.ASSOC_PACKAGE, stateProcessPackage);
			workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, assigneeNodeRef);

			// get the moderated workflow
			WorkflowDefinition wfDefinition = workflowService.getDefinitionByName("activiti$" + stateMashineId);
			if (wfDefinition == null) {
                wfDefinition = workflowService.getDefinitionByName("activiti$default_statemachine");
			}
			if (wfDefinition == null) {
				throw new IllegalStateException("no workflow: " + stateMashineId);
			}
			// start the workflow
			final String currentUser = AuthenticationUtil.getFullyAuthenticatedUser();
			AuthenticationUtil.setFullyAuthenticatedUser("workflow");
			WorkflowPath path = null; 
			try {
				path = workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
				logger.info( String.format( "workflow {%s} started for document {%s}", path, docRef));
			} finally {
				AuthenticationUtil.setFullyAuthenticatedUser(currentUser);
			}
			aspectProps = new HashMap<QName, Serializable>();
			aspectProps.put(StatemachineModel.PROP_STATEMACHINE_ID, path.getInstance().getId());
			nodeService.addAspect(childAssocRef.getChildRef(), StatemachineModel.ASPECT_STATEMACHINE, aspectProps);

			List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(path.getId());
			for (WorkflowTask task : tasks) {
				workflowService.endTask(task.getId(), null);
			}
		}
	}

}
