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
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 11:08
 */
public class StateMachineCreateDocumentPolicy implements NodeServicePolicies.OnCreateNodePolicy {

	private static ServiceRegistry serviceRegistry;
	private static PolicyComponent policyComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		StateMachineCreateDocumentPolicy.serviceRegistry = serviceRegistry;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		StateMachineCreateDocumentPolicy.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				StatemachineModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));

	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeService nodeService = serviceRegistry.getNodeService();

		QName type = nodeService.getType(childAssocRef.getChildRef());
		List<String> prefixes = (List<String>) serviceRegistry.getNamespaceService().getPrefixes(type.getNamespaceURI());
		String stateMashineId = prefixes.get(0) + "_" + type.getLocalName();
		if (stateMashineId != null) {
			//append status aspect to new document
			HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
			aspectProps.put(StatemachineModel.PROP_STATUS, "NEW");
			nodeService.addAspect(childAssocRef.getChildRef(), StatemachineModel.ASPECT_STATUS, aspectProps);

			PersonService personService = serviceRegistry.getPersonService();
			NodeRef assigneeNodeRef = personService.getPerson("workflow");

			Map<QName, Serializable> workflowProps = new HashMap<QName, Serializable>(16);

			WorkflowService workflowService = serviceRegistry.getWorkflowService();

			NodeRef stateProcessPackage = workflowService.createPackage(null);
			nodeService.addChild(stateProcessPackage, childAssocRef.getChildRef(), ContentModel.ASSOC_CONTAINS, type);

			workflowProps.put(WorkflowModel.ASSOC_PACKAGE, stateProcessPackage);
			workflowProps.put(WorkflowModel.ASSOC_ASSIGNEE, assigneeNodeRef);

			// get the moderated workflow
			WorkflowDefinition wfDefinition = workflowService.getDefinitionByName("activiti$" + stateMashineId);
			if (wfDefinition == null) {
				throw new IllegalStateException("no workflow: " + stateMashineId);
			}
			// start the workflow
			final String currentUser = AuthenticationUtil.getFullyAuthenticatedUser();
			AuthenticationUtil.setFullyAuthenticatedUser("workflow");
			WorkflowPath path = null; 
			try {
				path = workflowService.startWorkflow(wfDefinition.getId(), workflowProps);
			} finally {
				AuthenticationUtil.setFullyAuthenticatedUser(currentUser);
			}
			List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(path.getId()); 
			for (WorkflowTask task : tasks) {
				workflowService.endTask(task.getId(), null);
			}
		}
	}

}
