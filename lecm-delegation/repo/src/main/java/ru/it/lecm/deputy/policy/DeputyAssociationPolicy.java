/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.deputy.policy;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import java.util.List;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.deputy.DeputyService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author ikhalikov
 */
public class DeputyAssociationPolicy {

	private DeputyService deputyService;
	private PolicyComponent policyComponent;
	private NodeService nodeService;

	Function<NodeRef, String> getEmployeesName = new Function<NodeRef, String>() {

		@Override
		public String apply(NodeRef from) {
			return (String) nodeService.getProperty(from, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
		}
	};

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDeputyService(DeputyService deputyService) {
		this.deputyService = deputyService;
	}

	public void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, DeputyService.ASSOC_EMPLOYEE_TO_DEPUTY, new JavaBehaviour(this, "updateChiefsDeputies", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, DeputyService.ASSOC_EMPLOYEE_TO_DEPUTY, new JavaBehaviour(this, "onRemoveDeputy", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

	}


	public void onRemoveDeputy(AssociationRef nodeAssocRef) {

		NodeRef deputyNode = nodeAssocRef.getTargetRef();
		NodeRef chiefNode = nodeAssocRef.getSourceRef();
		NodeRef primaryDeputyEmployee = deputyService.getDeputyEmployee(deputyNode);

		List<NodeRef> chiefDeputies = deputyService.getDeputiesByChief(chiefNode);
		List<NodeRef> deputyChiefs = deputyService.getPrimaryChiefs(primaryDeputyEmployee);
		chiefDeputies.remove(primaryDeputyEmployee);

		updateRefs(chiefNode, primaryDeputyEmployee, chiefDeputies, deputyChiefs);

		nodeService.deleteNode(deputyNode);
	}

	public void updateChiefsDeputies(AssociationRef nodeAssocRef) {

		NodeRef deputyNode = nodeAssocRef.getTargetRef();
		NodeRef chiefNode = nodeAssocRef.getSourceRef();
		NodeRef deputyEmployeeNode = deputyService.getDeputyEmployee(deputyNode);

		List<NodeRef> chiefDeputies = deputyService.getDeputiesByChief(chiefNode);
		List<NodeRef> deputyChiefs = deputyService.getPrimaryChiefs(deputyEmployeeNode);

		updateRefs(chiefNode, deputyEmployeeNode, chiefDeputies, deputyChiefs);
	}

	private void updateRefs(NodeRef chiefRef, NodeRef deputyRef, List<NodeRef> deputies, List<NodeRef> chiefs) {

		String refContent = Joiner.on(";").join(deputies);
		String textContent = Joiner.on(";").join(Collections2.transform(deputies, getEmployeesName));
		nodeService.setProperty(chiefRef, DeputyService.PROP_DEPUTY_REF, refContent);
		nodeService.setProperty(chiefRef, DeputyService.PROP_DEPUTY_TEXT_CONTENT, textContent);

		refContent = Joiner.on(";").join(chiefs);
		textContent = Joiner.on(";").join(Collections2.transform(chiefs, getEmployeesName));
		nodeService.setProperty(deputyRef, DeputyService.PROP_CHIEF_REF, refContent);
		nodeService.setProperty(deputyRef, DeputyService.PROP_CHIEF_TEXT_CONTENT, textContent);
	}
}
