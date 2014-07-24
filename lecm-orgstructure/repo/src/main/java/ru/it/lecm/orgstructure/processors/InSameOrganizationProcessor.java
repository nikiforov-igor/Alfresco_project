package ru.it.lecm.orgstructure.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.Map;

/**
 * User: dbashmakov
 * Date: 24.07.2014
 * Time: 11:01
 */
public class InSameOrganizationProcessor extends SearchQueryProcessor {
    public static final String USERNAME_PARAM = "username";
    public static final String ORGANIZATION_FIELD_PARAM = "org_field";
    public static final String DEFAULT_ORGANIZATION_FIELD = "lecm-orgstr-aspects:linked-organization-assoc-ref";

    private OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();

        Object userName = params != null ? params.get(USERNAME_PARAM) : null;

        Object orgFieldShort = params != null ? params.get(ORGANIZATION_FIELD_PARAM) : DEFAULT_ORGANIZATION_FIELD;
        if (orgFieldShort.toString().trim().isEmpty()) {
            orgFieldShort = DEFAULT_ORGANIZATION_FIELD;
        }

        NodeRef organization;
        NodeRef employee;
        if (userName != null) {
            organization = orgstructureBean.getUserOrganization(userName.toString());
            employee = orgstructureBean.getEmployeeByPerson(userName.toString());
        } else {
            employee = orgstructureBean.getCurrentEmployee();
            organization = orgstructureBean.getEmployeeOrganization(employee);
        }

        final String organizationProperty = orgFieldShort.toString().replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

        sbQuery.append("@").append(organizationProperty);

        if (!orgstructureBean.isEmployeeHasBusinessRole(employee, "BR_GLOBAL_ORGANIZATIONS_ACCESS")) {
            sbQuery.append(":").append("\"").append(organization.toString().replace(":", "\\:")).append("\"");
        } else {
            sbQuery.append(":(*)");
        }

        return sbQuery.toString();
    }
}
