package ru.it.lecm.statemachine;


import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.DynamicAuthority;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.List;
import java.util.Set;

public class OrgUnitDynamicAuthority implements DynamicAuthority, InitializingBean {
    final static protected Logger logger = LoggerFactory.getLogger(OrgUnitDynamicAuthority.class);
    public static final String ORGUNIT_AUTHORITY = "ORGUNIT";

    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private AuthorityService authorityService;
	private OrgstructureBean orgstructureService;

    public OrgUnitDynamicAuthority() {
        super();
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

    public boolean hasAuthority(final NodeRef nodeRef, final String userName) {
        final long startTime = System.nanoTime();

        Boolean res = AuthenticationUtil.runAs(new RunAsWork<Boolean>() {

            public Boolean doWork() throws Exception {
                boolean result = true;
                if (userName.equalsIgnoreCase("System") || userName.equalsIgnoreCase("workflow") || nodeService.hasAspect(nodeRef, ContentModel.ASPECT_PENDING_DELETE)) {
                    result = false;
                } else {
                    if (nodeService.hasAspect(nodeRef, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                        Set<String> auth = authorityService.getAuthoritiesForUser(userName);
                        if (auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")) {//пользователь в группе LECM_GLOBAL_ORGANIZATIONS_ACCESS
                            result = false;
                        } else {
                            NodeRef organisation = null;
                            NodeRef org = orgstructureService.getUserOrganization(userName);
							
							List<AssociationRef> contractorAssoc = nodeService.getTargetAssocs(nodeRef, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
							if (!contractorAssoc.isEmpty()) {
								organisation = contractorAssoc.get(0).getTargetRef();
							}
							
                            if (org != null && organisation != null) {
                                if (org.equals(organisation)) result = false;
                            }
                        }
                    } else {
                        result = false;
                    }
                }

                long endTime = System.nanoTime();
                logger.debug("!!!!!!!!!!!!!!! Проверка прав пользователя " + userName + " на документ " + nodeRef + " длительность " + (endTime - startTime) / 1000000 + " ms");
                return result;
            }
        }, AuthenticationUtil.getSystemUserName());

        return res;
    }

    public String getAuthority() {
        return ORGUNIT_AUTHORITY;
    }

    public Set<PermissionReference> requiredFor() {
        return null;
    }

}