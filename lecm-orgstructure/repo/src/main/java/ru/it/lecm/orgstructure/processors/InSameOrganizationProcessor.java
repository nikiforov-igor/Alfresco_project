package ru.it.lecm.orgstructure.processors;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.Map;
import java.util.Set;

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
    private AuthorityService authorityService;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }
    
    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();

        Object userName = params != null && params.get(USERNAME_PARAM) != null ? params.get(USERNAME_PARAM) != null : null;

        Object orgFieldShort = params != null && params.get(ORGANIZATION_FIELD_PARAM) != null ? params.get(ORGANIZATION_FIELD_PARAM) : DEFAULT_ORGANIZATION_FIELD;
        if (orgFieldShort.toString().trim().isEmpty()) {
            orgFieldShort = DEFAULT_ORGANIZATION_FIELD;
        }

        NodeRef organization;
        NodeRef employee;
        Set<String> auth;
        if (userName != null) {
        	auth = authorityService.getAuthoritiesForUser(userName.toString());
            employee = orgstructureBean.getEmployeeByPerson(userName.toString());
        } else {
        	auth = authorityService.getAuthoritiesForUser(AuthenticationUtil.getFullyAuthenticatedUser());
            employee = orgstructureBean.getCurrentEmployee();
        }

        final String organizationProperty = orgFieldShort.toString().replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        sbQuery.append("@").append(organizationProperty).append(":");

//        if (!orgstructureBean.isEmployeeHasBusinessRole(employee, "BR_GLOBAL_ORGANIZATIONS_ACCESS", false, false)) {
        if (!auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")) {
            organization = orgstructureBean.getEmployeeOrganization(employee);
            if (organization != null) {
                sbQuery.append("\"").append(organization.toString().replace(":", "\\:")).append("\"");
            } else {
                sbQuery.append("\"NOT_REF\"");
            }
        } else {
            sbQuery.append("\"*\"");
        }
        sbQuery.append(" OR ISNULL:").append("\"").append(organizationProperty).append("\"");

        return sbQuery.toString();
    }
}
