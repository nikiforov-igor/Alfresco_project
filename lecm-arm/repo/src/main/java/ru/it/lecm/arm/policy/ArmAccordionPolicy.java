package ru.it.lecm.arm.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.Types;

import java.util.List;

/**
 * User: AIvkin
 * Date: 07.02.14
 * Time: 10:49
 */
public class ArmAccordionPolicy implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {
	private PolicyComponent policyComponent;
	private PermissionService permissionService;
	private NodeService nodeService;
	private AuthorityService authorityService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init() {
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ArmService.TYPE_ARM_ACCORDION, ArmService.ASSOC_ACCORDION_BUSINESS_ROLES,
				new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				ArmService.TYPE_ARM_ACCORDION, ArmService.ASSOC_ACCORDION_BUSINESS_ROLES,
				new JavaBehaviour(this, "onDeleteAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef accordion = nodeAssocRef.getSourceRef();
		NodeRef businessRole = nodeAssocRef.getTargetRef();

		permissionService.setInheritParentPermissions(accordion, false);
		permissionService.setPermission(accordion, getAutorityByBusinessRole(businessRole), "LECM_BASIC_PG_Reader", true);
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef accordion = nodeAssocRef.getSourceRef();
		NodeRef businessRole = nodeAssocRef.getTargetRef();
		if(nodeService.exists(accordion)){
			List<AssociationRef> existAccordionBusinessRoles = nodeService.getTargetAssocs(accordion, ArmService.ASSOC_ACCORDION_BUSINESS_ROLES);
	
			permissionService.setInheritParentPermissions(accordion, existAccordionBusinessRoles.size() == 0);
			permissionService.deletePermission(accordion, getAutorityByBusinessRole(businessRole), "LECM_BASIC_PG_Reader");
		}
	}

	private String getAutorityByBusinessRole(NodeRef businessRole) {
		String roleIdentifier = (String) nodeService.getProperty(businessRole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		String roleName = Types.SGKind.SG_BR.getSGPos(roleIdentifier).getAlfrescoSuffix();
		return authorityService.getName(AuthorityType.GROUP, roleName);
	}
}
