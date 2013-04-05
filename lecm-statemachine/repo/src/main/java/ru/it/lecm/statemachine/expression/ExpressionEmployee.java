package ru.it.lecm.statemachine.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * User: pmelnikov
 * Date: 05.04.13
 * Time: 9:00
 */
public class ExpressionEmployee {

    private ServiceRegistry serviceRegistry;
    private OrgstructureBean orgstructureBean;
    private NodeRef employee;

    public ExpressionEmployee(NodeRef employee, ServiceRegistry serviceRegistry, OrgstructureBean orgstructureBean) {
        this.employee = employee;
        this.serviceRegistry = serviceRegistry;
        this.orgstructureBean = orgstructureBean;
    }

    /**
     * Проверяет наличие руководящей позиции у сотрудника
     * @return
     */
    public boolean isBoss() {
        return orgstructureBean.isBoss(employee);
    }

    /**
     * Проверяет наличие руководящей позиции у сотрудника по отношению к другому сотруднику
     * @param employee
     * @return
     */
    public boolean isBoss(ExpressionEmployee employee) {
        return orgstructureBean.hasSubordinate(this.employee, employee.getEmployee());
    }

    /**
     * Проверяет наличие бизнес-роли у сотрудника
     * @param businessRole
     * @return
     */
    public boolean hasBusinessRole(String businessRole) {
        return orgstructureBean.isEmployeeHasBusinessRole(employee, businessRole);
    }

    public NodeRef getEmployee() {
        return employee;
    }
}
