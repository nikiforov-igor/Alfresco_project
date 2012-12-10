package ru.it.lecm.security.impl;

import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Базовый бин для обеспечения проверок в системе безопасности.
 * Имеет набор служб (NodeService, PermissionService, NamespacePrefixResolver, AuthorityService)
 * @author rabdullin
 *
 */
public abstract class MethodInvocationBaseBean implements InitializingBean {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * WKS-Службы Alfresco
	 */
	protected PermissionService permissionService;
	protected NamespacePrefixResolver nspr;
	protected NodeService nodeService;
	protected AuthorityService authorityService;

	/**
	 * Набор классов, для которых только и надо выполнять проверку, для 
	 * остальных проверка не выполняется и результат "не голосую".
	 * Пустой список означает что проверять надо все классы.
	 */
	protected QNameResolver supportingTypes = new QNameResolver();

	@Override
	public void afterPropertiesSet() throws Exception {
		// if (permissionService == null) throw new IllegalArgumentException("There must be a permission service");
		if (nspr == null)
			throw new IllegalArgumentException("There must be a namespace service");
		if (nodeService == null)
			throw new IllegalArgumentException("There must be a node service");
		if (permissionService == null)
			throw new IllegalArgumentException("There must be a permission service");

		if(supportingTypes != null) {
			supportingTypes.resolve(this.nspr);
			log.warn( "Configured supporting types list: "+ supportingTypes.toString());
		} else 
			log.warn("Configured supporting types list is empty");
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public NamespacePrefixResolver getNamespacePrefixResolver() {
		return nspr;
	}

	public void setNamespacePrefixResolver(NamespacePrefixResolver nspr) {
		this.nspr = nspr;
	}

	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}


	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public Set<String> getSupportingTypes() {
		return this.supportingTypes.getSynonyms();
	}

	public void setSupportingTypes(Set<String> filteredTypes) {
		this.supportingTypes.setSynonyms( filteredTypes);
	}


	protected void getNodeInfo(final NodeRef nodeRef, StringBuilder sb) {
		getNodeInfo(nodeRef, sb, "");
	}

	protected void getNodeInfo(final NodeRef nodeRef, StringBuilder sb, final String title) {
		sb.append( String.format( "\tPermission test %sfor %sexisting node [%s]"
				, (title == null ? "" : title)
				, (nodeService.exists(nodeRef)? "": "non-") // "existsing" / "non-existing"
				, nodeService.getPath(nodeRef)
				));
	}
}
