/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.internal.policy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.internal.api.InternalService;

/**
 *
 * @author ikhalikov
 */
public class InternalCreatePolicy implements NodeServicePolicies.OnCreateNodePolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                InternalService.TYPE_INTERNAL, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef ref = childAssocRef.getChildRef();
		List<AssociationRef> recipientsList = nodeService.getTargetAssocs(ref, EDSDocumentService.ASSOC_RECIPIENTS);
		for (AssociationRef associationRef : recipientsList) {
			createAnswer(ref, associationRef.getTargetRef());
		}
	}

	private void createAnswer(NodeRef doc, NodeRef employeeRef){
		AssociationRef table = nodeService.getTargetAssocs(doc, InternalService.ASSOC_ANSWER_TABLE).get(0);
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		props.put(InternalService.PROP_ANSWER_TABLE_STATUS, false);
		props.put(InternalService.PROP_ANSWER_TABLE_EMPLOYEE_ASSOC_REF, employeeRef);
		props.put(InternalService.PROP_ANSWER_TABLE_ANSWER, "Готовится ответ");

		ChildAssociationRef child = nodeService.createNode(table.getTargetRef(), ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()), InternalService.TYPE_ANSWER, props);
		nodeService.createAssociation(child.getChildRef(), employeeRef, InternalService.ASSOC_ANSWER_TABLE_EMPLOYEE);
	}

}
