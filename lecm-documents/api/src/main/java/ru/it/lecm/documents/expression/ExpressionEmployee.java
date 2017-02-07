package ru.it.lecm.documents.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.util.List;

/**
 * User: pmelnikov
 * Date: 05.04.13
 * Time: 9:00
 */
public class ExpressionEmployee {

    private ServiceRegistry serviceRegistry;
    private OrgstructureBean orgstructureBean;
    private static LecmPermissionService lecmPermissionService;
    private NodeRef employee;
    private static IDelegation delegationService;

	public ExpressionEmployee() {
	}

	public ExpressionEmployee(NodeRef employee, ServiceRegistry serviceRegistry, OrgstructureBean orgstructureBean) {
        this.employee = employee;
        this.serviceRegistry = serviceRegistry;
        this.orgstructureBean = orgstructureBean;
    }

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		ExpressionEmployee.lecmPermissionService = lecmPermissionService;
	}

    public void setDelegationService(IDelegation delegationService) {
        ExpressionEmployee.delegationService = delegationService;
    }
	/**
     * Проверяет наличие руководящей позиции у сотрудника
     * @return
     */
    public boolean isBoss() {
        return orgstructureBean.isBoss(employee, true);
    }

    /**
     * Проверяет наличие руководящей позиции у сотрудника по отношению к другому сотруднику
     * @param employee
     * @return
     */
    public boolean isBoss(ExpressionEmployee employee) {
        return orgstructureBean.hasSubordinate(this.employee, employee.getEmployee(), true);
    }

    /**
     * Проверяет наличие бизнес-роли у сотрудника
     * @param businessRole
     * @return
     */
    public boolean hasBusinessRole(String businessRole) {
        return orgstructureBean.isEmployeeHasBusinessRole(employee, businessRole, true, true);
    }

    public NodeRef getEmployee() {
        return employee;
    }

	/**
	 * Проверяет наличие динамической бизнес-роли у сотрудника
	 * @param document документ
	 * @param roleName имя роли
	 * @return
	 */
	public boolean hasDynamicBusinessRole(NodeRef document, String roleName) {
		return lecmPermissionService.hasEmployeeDynamicRole(document, orgstructureBean.getEmployeeLogin(employee), roleName);
	}

    /**
     * Проверяет является ли текущий сотрудник полным делегатом у заданного сотрудника
     * @param delegationOwner сотрудник, который мог делегировать полномочия
     * @return true - если есть активное делегирование, где текущий сотрудник является полным делегатом
     */
    public boolean hasActiveDelegation(NodeRef delegationOwner) {
        if (delegationOwner != null) {
            NodeRef delegationOpts = delegationService.getDelegationOpts(delegationOwner);
            boolean isDelegationActive = delegationService.isDelegationActive(delegationOpts);
            if (isDelegationActive) {
                List<AssociationRef> trusteeAssoc = serviceRegistry.getNodeService().getTargetAssocs(delegationOpts, IDelegation.ASSOC_DELEGATION_OPTS_TRUSTEE);
                if (!trusteeAssoc.isEmpty()) {
                    NodeRef effectiveEmployee = trusteeAssoc.get(0).getTargetRef();
                    return effectiveEmployee.equals(this.employee);
                }
            }
        }
        return false;
    }

    /**
     * Проверяет является ли текущий сотрудник администратором
     * @return true - если есть текущий сотрудник является администратором
     */
    public boolean isAdmin() {
        return lecmPermissionService.isAdmin(orgstructureBean.getEmployeeLogin(employee));
    }
}
