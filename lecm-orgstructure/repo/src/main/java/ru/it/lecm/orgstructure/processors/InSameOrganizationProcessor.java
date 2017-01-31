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
    public static final String ORGANIZATION_PARAM = "organization";
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

        String userName = params != null && params.get(USERNAME_PARAM) != null ? params.get(USERNAME_PARAM).toString() : AuthenticationUtil.getFullyAuthenticatedUser();

        Object orgFieldShort = params != null && params.get(ORGANIZATION_FIELD_PARAM) != null ? params.get(ORGANIZATION_FIELD_PARAM) : DEFAULT_ORGANIZATION_FIELD;
        if (orgFieldShort.toString().trim().isEmpty()) {
            orgFieldShort = DEFAULT_ORGANIZATION_FIELD;
        }

        String organizationParam = params != null && params.get(ORGANIZATION_PARAM) != null ? params.get(ORGANIZATION_PARAM).toString() : null;

        Boolean useStrictAccess = params != null && params.get("strict") != null ? Boolean.valueOf(params.get("strict").toString()) : false;
        NodeRef organization;
        Set<String> auth = authorityService.getAuthoritiesForUser(userName);

        final String organizationProperty = orgFieldShort.toString().replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        sbQuery.append("@").append(organizationProperty).append(":");

        if (!AuthenticationUtil.isRunAsUserTheSystemUser() && !auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")) {
            organization = organizationParam != null && NodeRef.isNodeRef(organizationParam) ? new NodeRef(organizationParam) : orgstructureBean.getUserOrganization(userName);
            if (organization != null) {
                sbQuery.append("\"").append(organization.toString().replace(":", "\\:")).append("\"");
            } else {
                sbQuery.append("\"NOT_REF\"");
            }
        } else {
        	sbQuery.append("\"").append(organizationParam != null && NodeRef.isNodeRef(organizationParam) ? organizationParam.replace(":", "\\:") : "?*").append("\"");
//        	sbQuery.append("ISNODE:T");
        }
        if (!useStrictAccess) {
                sbQuery.append(" OR (ISNULL:").append("\"").append(organizationProperty).append("\"");
                sbQuery.append(" OR NOT EXISTS:").append("\"").append(organizationProperty).append("\")");
            }

        return sbQuery.toString();
    }
}
