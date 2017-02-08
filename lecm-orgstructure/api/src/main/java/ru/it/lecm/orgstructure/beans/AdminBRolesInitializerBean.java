package ru.it.lecm.orgstructure.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import ru.it.lecm.base.beans.BaseBean;

/**
 * User: dbashmakov
 * Date: 04.04.13
 * Time: 10:27
 */
public abstract class AdminBRolesInitializerBean extends BaseBean {

    public static final String ADMIN = "admin";
    protected OrgstructureBean orgstructureBean;
    protected List<String> businessRoles;
    protected PersonService personService;

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setBusinessRoles(List<String> businessRoles) {
        this.businessRoles = businessRoles;
    }

    public List<String> getBusinessRoles() {
        return businessRoles;
    }

    public void init() {
//        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
//            @Override
//            public NodeRef doWork() throws Exception {
//                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//                    @Override
//                    public NodeRef execute() throws Throwable {
//                        NodeRef adminEmployee = getAdminEmployee();
//                        for (String businessRole : getBusinessRoles()) {
//                            addBusinessRole(adminEmployee, businessRole);
//                        }
//                        return null;
//                    }
//                });
//            }
//        };
//        AuthenticationUtil.runAsSystem(raw);
    }
    
    @Override
	protected void onShutdown(ApplicationEvent event)
	{
	    // NOOP
	}

    protected void addBusinessRole(NodeRef adminEmployee, String businessRole) {
        NodeRef bRole = orgstructureBean.getBusinessRoleByIdentifier(businessRole);
        if (bRole != null) {
            orgstructureBean.includeEmployeeIntoBusinessRole(bRole, adminEmployee);
        }
    }

    protected NodeRef getAdminEmployee() {
        NodeRef adminEmployee = orgstructureBean.getEmployeeByPerson(ADMIN, false);
        if (adminEmployee == null) {// создаем дефолтного сотрудника
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "adminEmployee");
            HashMap<QName,Serializable> properties = new HashMap<QName, Serializable>(1);
            properties.put(ContentModel.PROP_NAME, "Admin Admin Admin");
            properties.put(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME, "Admin");
            properties.put(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME, "Admin");
            properties.put(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME, "Admin");
            adminEmployee = nodeService.createNode(orgstructureBean.getEmployeesDirectory(), ContentModel.ASSOC_CONTAINS, assocQName, OrgstructureBean.TYPE_EMPLOYEE, properties).getChildRef();

            NodeRef adminPerson = personService.getPerson(ADMIN, false);
            // связываем новосозданного сотрудника с person
            nodeService.createAssociation(adminEmployee, adminPerson, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
        }
        return adminEmployee;
    }

    protected void initServiceImpl() {
	NodeRef adminEmployee = getAdminEmployee();
        for (String businessRole : getBusinessRoles()) {
            addBusinessRole(adminEmployee, businessRole);
        }
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }
}
