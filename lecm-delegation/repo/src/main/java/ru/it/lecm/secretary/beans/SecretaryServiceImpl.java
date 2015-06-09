/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.secretary.beans;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretarySecurityService;
import ru.it.lecm.secretary.SecretaryService;

/**
 *
 * @author ikhalikov
 */
public class SecretaryServiceImpl implements SecretaryService {

	private NodeService nodeService;
	private OrgstructureBean orgstructureService;
	private SecretarySecurityService secretarySecurityService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setSecretarySecurityService(SecretarySecurityService secretarySecurityService) {
		this.secretarySecurityService = secretarySecurityService;
	}

	@Override
	public List<NodeRef> getChiefs(NodeRef secretaryRef) {
		List<NodeRef> result = new ArrayList<>();
		List<NodeRef> subRes;

		subRes = getPrimaryChiefs(secretaryRef);
		if(!subRes.isEmpty()) {
			result.addAll(subRes);

			for (NodeRef subChief : subRes) {
				result.addAll(getChiefs(subChief));
			}

		}

		return result;
	}

	@Override
	public List<NodeRef> employeesToStaff(List<NodeRef> employees) {
		List<NodeRef> result = new ArrayList<>();

		for (NodeRef employee : employees) {
			result.addAll(orgstructureService.getEmployeeStaffs(employee));
		}

		return result;
	}

	@Override
	public List<NodeRef> staffToEmployees(List<NodeRef> staff) {
		List<NodeRef> result = new ArrayList<>();

		for (NodeRef staffPosition : staff) {
			result.add(orgstructureService.getEmployeeByPosition(staffPosition));
		}

		return result;
	}

	@Override
	public List<NodeRef> getPrimaryChiefs(NodeRef secretaryRef) {
		List<NodeRef> res = new ArrayList<>();
		List<AssociationRef> chiefAssocs = nodeService.getTargetAssocs(secretaryRef, ASSOC_CHIEF_ASSOC);

		for (AssociationRef chiefAssoc : chiefAssocs) {
			res.add(chiefAssoc.getTargetRef());
		}

		return res;
	}

	@Override
	public boolean isSecretary(NodeRef employeeRef) {
		return nodeService.hasAspect(employeeRef, ASPECT_IS_SECRETARY);
	}

	@Override
	public boolean isChief(NodeRef employeeRef) {
		return !nodeService.getSourceAssocs(employeeRef, ASSOC_CHIEF_ASSOC).isEmpty();
	}

	@Override
	public List<NodeRef> getSecretaries(NodeRef chiefRef) {
		List<NodeRef> secretaries = new ArrayList<>();
		List<AssociationRef> assocs = nodeService.getSourceAssocs(chiefRef, ASSOC_CHIEF_ASSOC);

		for (AssociationRef assoc : assocs) {
			secretaries.add(assoc.getSourceRef());
		}

		return secretaries;
	}

	private boolean removeSecretary(NodeRef chiefRef, NodeRef secretaryRef, boolean removeSG) {
		nodeService.removeAssociation(secretaryRef, chiefRef, ASSOC_CHIEF_ASSOC);
		nodeService.removeAspect(secretaryRef, ASPECT_IS_SECRETARY);
		if (removeSG) {
			secretarySecurityService.removeSecretary(chiefRef, secretaryRef);
		}
		return true;
	}

	@Override
	public boolean removeSecretary(NodeRef chiefRef, NodeRef secretaryRef) {
		return removeSecretary(chiefRef, secretaryRef, true);
	}

	@Override
	public void removeSecretaries(NodeRef chiefRef) {
		List<NodeRef> secretaries = getSecretaries(chiefRef);
		for (NodeRef secretary : secretaries) {
			removeSecretary(chiefRef, secretary, false);
		}
		secretarySecurityService.removeSGSecretary(chiefRef);
	}

	private NodeRef getTasksSecretary(NodeRef chief, boolean returnChief) {
		List<AssociationRef> assocs = nodeService.getSourceAssocs(chief, ASSOC_CAN_RECEIVE_TASKS_FROM_CHIEFS);
		NodeRef effectiveSecretary;
		if (assocs.isEmpty()) {
			effectiveSecretary = returnChief ? chief : null;
		} else {
			effectiveSecretary = assocs.get(0).getSourceRef();
		}
		return effectiveSecretary;
	}

	@Override
	public NodeRef getEffectiveEmployee(NodeRef chief) {
		return getTasksSecretary(chief, true);
	}

	@Override
	public NodeRef getTasksSecretary(NodeRef chief) {
		return getTasksSecretary(chief, false);
	}
}
