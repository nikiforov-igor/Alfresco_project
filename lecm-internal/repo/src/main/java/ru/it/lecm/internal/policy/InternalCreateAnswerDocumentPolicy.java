package ru.it.lecm.internal.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.internal.api.InternalService;
import ru.it.lecm.workflow.api.WorkflowResultModel;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.workflow.signing.api.SigningAspectsModel;

/**
 * User: dbashmakov Date: 11.03.14 Time: 16:02
 */
public class InternalCreateAnswerDocumentPolicy implements ApplicationContextAware, NodeServicePolicies.OnCreateAssociationPolicy {

	private static final Logger logger = LoggerFactory.getLogger(InternalCreateAnswerDocumentPolicy.class);

	private ApplicationContext context;
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private DocumentMembersService documentMembersService;
	@Deprecated
	private Object deprecatedSigningWorkflowService;
	@Deprecated
	private Method getOrCreateSigningFolderContainer;

	final public void init() {
		initDeprecatedSigningWorkflowService();

		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME, InternalService.TYPE_INTERNAL, DocumentService.ASSOC_RESPONSE_TO, new JavaBehaviour(this, "onCreateAssociation"));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Deprecated
	private void initDeprecatedSigningWorkflowService() {
		try {
			deprecatedSigningWorkflowService = context.getBean("deprecated.signingWorkflowService");
			if (deprecatedSigningWorkflowService != null) {
				try {
					Class<?> deprecatedSigningWorkflowServiceClass = deprecatedSigningWorkflowService.getClass();
					getOrCreateSigningFolderContainer = deprecatedSigningWorkflowServiceClass.getDeclaredMethod("getOrCreateSigningFolderContainer", NodeRef.class);
				} catch (NoSuchMethodException | SecurityException ex) {
					logger.warn("Can't obtain deprecated method 'getOrCreateSigningFolderContainer' from bean 'deprecated.signingWorkflowService'. Caused by: {}", ex.getMessage());
					getOrCreateSigningFolderContainer = null;
				}
			}
		} catch (BeansException ex) {
			logger.warn("Can't obtain bean named 'deprecated.signingWorkflowService' from applicationContext. Caused by: {}", ex.getMessage());
			deprecatedSigningWorkflowService = null;
		}
	}

	@Deprecated
	private HashSet<NodeRef> getSignersUsingDepractedBean(final NodeRef targetRef) {

		HashSet<NodeRef> members;
		try {
			Object result = getOrCreateSigningFolderContainer.invoke(deprecatedSigningWorkflowService, targetRef);
			NodeRef signingContainer = (NodeRef) result;
			List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(signingContainer);

			members = new HashSet<>();
			for (ChildAssociationRef list : childAssocs) {
				List<ChildAssociationRef> users = nodeService.getChildAssocs(list.getChildRef());
				for (ChildAssociationRef user : users) {
					List<AssociationRef> employees = nodeService.getTargetAssocs(user.getChildRef(), WorkflowResultModel.ASSOC_WORKFLOW_RESULT_ITEM_EMPLOYEE);
					for (AssociationRef employee : employees) {
						members.add(employee.getTargetRef());
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			logger.error(ex.getMessage());
			logger.debug(ex.getMessage(), ex);
			members = new HashSet<>();
		}
		return members;
	}

	private HashSet<NodeRef> getSigners(final NodeRef targetRef) {
		HashSet<NodeRef> members = new HashSet<>();
		if (nodeService.hasAspect(targetRef, SigningAspectsModel.ASPECT_SIGNING_DETAILS)) {
			List<AssociationRef> assocs = nodeService.getTargetAssocs(targetRef, SigningAspectsModel.ASSOC_SIGNER_EMPLOYEE_ASSOC);
			for (AssociationRef assoc : assocs) {
				members.add(assoc.getTargetRef());
			}
		}
		return members;
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef targetRef = nodeAssocRef.getTargetRef();
		NodeRef internalRef = nodeAssocRef.getSourceRef();

		HashSet<NodeRef> members;
		if (deprecatedSigningWorkflowService != null && getOrCreateSigningFolderContainer != null) {
			members = getSignersUsingDepractedBean(targetRef);
		} else {
			members = getSigners(targetRef);
		}

		for (NodeRef member : members) {
			try {
				//транзакция должна быть: policy onCreateAssociation
				documentMembersService.addMemberWithoutCheckPermission(internalRef, member, new HashMap<QName, Serializable>());
			} catch (WriteTransactionNeededException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}
}
