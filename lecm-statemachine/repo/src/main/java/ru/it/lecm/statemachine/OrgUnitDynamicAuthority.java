package ru.it.lecm.statemachine;


import java.util.Set;

import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.DynamicAuthority;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class OrgUnitDynamicAuthority implements DynamicAuthority, InitializingBean
{
	final static protected Logger logger = LoggerFactory.getLogger(OrgUnitDynamicAuthority.class);
	
	private NodeService nodeService;
	private DictionaryService dictionaryService;
	private AuthorityService authorityService;
	private SimpleCache<String, NodeRef> userOrganizationsCache;
	
    public OrgUnitDynamicAuthority()
    {
        super();
    }

    public void afterPropertiesSet() throws Exception
    {
        
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
    
    public void setUserOrganizationsCache(SimpleCache<String, NodeRef>  userOrganizationsCache) {
        this.userOrganizationsCache = userOrganizationsCache;
    }

    public boolean hasAuthority(final NodeRef nodeRef, final String userName)
    {
    	final long startTime = System.nanoTime();
        
    	Boolean res = AuthenticationUtil.runAs(new RunAsWork<Boolean>(){
    		
    		public Boolean doWork() throws Exception
    		{
    			boolean result = true;
    			if (userName.equalsIgnoreCase("System") || userName.equalsIgnoreCase("workflow")) {
    				result = false;
    			} else {
	    			Set<String> auth = authorityService.getAuthoritiesForUser(userName);
		            if (auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")) {//пользователь в группе LECM_GLOBAL_ORGANIZATIONS_ACCESS
		            	result = false;
				    } else {
		    			QName refType = nodeService.getType(nodeRef);
		    			Boolean isDocument = (refType != null && dictionaryService.isSubClass(refType, DocumentService.TYPE_BASE_DOCUMENT));
		    			if(isDocument) {
						   	NodeRef organisation = null;
						   	NodeRef org = null;
						   	if (userOrganizationsCache.contains(userName)) {
						    	org = userOrganizationsCache.get(userName);
							    if (nodeService.hasAspect(nodeRef, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
							    	List<AssociationRef> contractorAssoc = nodeService.getTargetAssocs(nodeRef, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
							    	if (!contractorAssoc.isEmpty()) {
							    		organisation = contractorAssoc.get(0).getTargetRef();
							    	}
							    }
						   	}
						    if(org!=null&&organisation!=null) {
						    	if(org.equals(organisation)) result = false;
						    }
		                } else {
		                	result = false;
		                }
				    }
    			}
    			long endTime = System.nanoTime();
    			logger.debug("!!!!!!!!!!!!!!! Проверка прав пользователя "+userName+" на документ "+nodeRef + " длительность " + (endTime - startTime)/1000000 + " ms");
                return result;
                
            }
        }, AuthenticationUtil.getSystemUserName());
		
    	return res;
    }

    public String getAuthority()
    {
       return "ORGUNIT";
    }

    public Set<PermissionReference> requiredFor()
    {
        return null;
    }

}