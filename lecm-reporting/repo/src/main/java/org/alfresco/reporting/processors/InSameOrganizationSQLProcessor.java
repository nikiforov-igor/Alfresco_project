package org.alfresco.reporting.processors;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.Map;
import java.util.Set;

/**
 * User: dbashmakov
 * Date: 16.12.2014
 * Time: 14:22
 */
public class InSameOrganizationSQLProcessor extends SearchQueryProcessor {
    public static final String TABLE_PARAM = "table";
    public static final String USERNAME_PARAM = "username";

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

        String userName = params != null && params.get(USERNAME_PARAM) != null ?
                params.get(USERNAME_PARAM).toString() : AuthenticationUtil.getFullyAuthenticatedUser();

        String tableName = params != null && params.get(TABLE_PARAM) != null ?
                params.get(TABLE_PARAM).toString() : "";

        Boolean useStrictAccess = params != null && params.get("strict") != null ? Boolean.valueOf(params.get("strict").toString()) : false;

        Set<String> auth = authorityService.getAuthoritiesForUser(userName);
        if (!auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")){
            if (tableName.length() > 0) {
                sbQuery.append(tableName).append(".");
            }
            sbQuery.append("lecm_orgstr_aspects_linked_organization_assoc=");
            NodeRef organization = orgstructureBean.getUserOrganization(userName);
            if (organization != null) {
                sbQuery.append("\'").append(organization.toString()).append("\'");
            } else {
                sbQuery.append("\'NOT_REF\'");
            }
            if (!useStrictAccess) {
                sbQuery.append(" OR ");
                if (tableName.length() > 0) {
                    sbQuery.append(tableName).append(".");
                }
                sbQuery.append("lecm_orgstr_aspects_linked_organization_assoc IS NULL ");
            }
        } else {
            sbQuery.append(" 1=1 ");
        }

        return sbQuery.toString();
    }
}
