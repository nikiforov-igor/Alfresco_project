package ru.it.lecm.security.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.SpringSecurityMessageSource;

import ru.it.lecm.utils.alfresco.QNameResolver;

/**
 * Реализация custom-логики для днамической проверки доступа к методам (actions)
 * 
 * см также org.alfresco.repo.security.permissions.impl.acegi.ACLEntryVoter.java 
 * (использовался как пример реализации)
 * Фактически OrgStructureVoter реализует доступ аналогично ACLEntryVoter, но
 * с возможностью custom-проверки узла.
 * 
 */
public class OrgStructureVoter 
		extends MethodInvocationBaseBean 
		implements AccessDecisionVoter {

	// protected static Log log = LogFactory.getLog(OrgStructureVoter.class);

	private static final String ACL_ORG_NODE = "ORG_ACL_NODE";
	private static final String ACL_ORG_PARENT = "ORG_ACL_PARENT";
	private static final String ACL_ORG_ROLEMETHOD = "ORG_ACL_ROLE";

	private static final String ACL_ORG_ALLOW = "ORG_ACL_ALLOW";
	private static final String ACL_ORG_DENY = "ORG_ACL_DENY";

	/*TODO:
	 * 		MethodSecurityInterceptor ints;
	 * 		MarkingAfterInvocationProvider p;
	 */

	/**
	 * Набор классов, для которых голосование заканчивается с результатом "воздержался"/"не голосую"
	 */
	private QNameResolver abstainFor = new QNameResolver();

	public Set<String> getAbstainFor() {
		return this.abstainFor.getSynonyms();
	}

	public void setAbstainFor(Set<String> abstainFor) {
		this.abstainFor.setSynonyms( abstainFor);
	}

	private static Set<String> attrTypes = null;

	/**
	 * @return список поддерживаемых типов атрибутов
	 */
	public static Set<String> getSupportedConfigAttributeTypes() {
		if (attrTypes == null) {
			attrTypes = Collections.unmodifiableSet(new HashSet<String>() {
				private static final long serialVersionUID = 1L;
				{
					add(ACL_ORG_NODE);
					add(ACL_ORG_PARENT);
					add(ACL_ORG_ALLOW);
					add(ACL_ORG_ROLEMETHOD);
					add(ACL_ORG_DENY);
				}
			});
		}
		return attrTypes;
	}


	@Override
	public void afterPropertiesSet() throws Exception
	{
		super.afterPropertiesSet();

		if (authorityService == null)
			throw new IllegalArgumentException("There must be an authority service");

		if(abstainFor != null) {
			abstainFor.resolve(this.nspr);
			log.warn( "Configured abstaining class list: "+ abstainFor.toString());
		} else 
			log.warn("Configured abstaining class list is empty");
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		final boolean yes = (attribute.getAttribute() != null)
					&& (   attribute.getAttribute().startsWith(ACL_ORG_NODE)
						|| attribute.getAttribute().startsWith(ACL_ORG_PARENT)
						|| attribute.getAttribute().startsWith(ACL_ORG_ROLEMETHOD)
						|| attribute.getAttribute().equals(ACL_ORG_ALLOW)
						|| attribute.getAttribute().equals(ACL_ORG_DENY)
					);
		if (yes && log.isTraceEnabled())
			log.trace( String.format( "ConfigAttribute [%s] %s", attribute, (yes ? "SUPPORTED" : "NOT SUPPORTED")));
		return yes;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean supports( Class clazz) {
		return (MethodInvocation.class.isAssignableFrom(clazz));
	}

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	final private static String NAMESPACE = "http://www.it.ru/lecm/model/blanks/1.0";
	// final private static String TYPENAME = "blank";

	final private static QName PROP_FLAG =  QName.createQName(NAMESPACE, "flag");
	// final private static QName PROP_STATUS =  QName.createQName(NAMESPACE, "status");

	@Override
	public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config) {
		final MethodInvocation invokeMethod = (MethodInvocation) object;

		final StringBuilder sb = new StringBuilder(); 

		try {

			if (log.isDebugEnabled()) {
				sb.append( String.format( "Method:\r\n\t%s\r\n", invokeMethod.getMethod().toString() ));
			}

			if (AuthenticationUtil.isRunAsUserTheSystemUser()) {
				if (log.isDebugEnabled())
					sb.append("\tAccess Granted for the SystemUser \r\n");
				return AccessDecisionVoter.ACCESS_GRANTED;
			}

//			/* хард-код для проверки блокирования доступа на чтение некоторых хардкод-карточек... */
//			if ( "getProperties".equals(invocation.getMethod().getName())) {
//				// final Class<?>[] params = invocation.getMethod().getParameterTypes();
//				final NodeRef ref = (NodeRef) invocation.getArguments()[0];
//				final Set<QName> aspects = this.nodeService.getAspects(ref);
//				if (ref.getId().equals("3f291fff-a155-44ba-b6cc-3e335a294851")
//						||
//					ref.getId().equals("9d14efd7-a030-40a7-9689-496d046bb0f5")
//						||
//					ref.getId().equals("21c15f28-20f7-43c3-b5a3-a5dbe5e71917")
//				) { // отладочный DENY ...
//					sb.append( String.format( "DENY for node [%s]\r\n", ref.getId()));
////					throw new AccessDeniedException(messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
//					return AccessDecisionVoter.ACCESS_DENIED;
//				}
//			}

			// TODO: (!) cache supportedDefinitions by key=MethodInvocation
			final List<ConfigAttributeDefintion> supportedDefinitions = extractSupportedDefinitions(config);
			if (supportedDefinitions.size() == 0)
				return AccessDecisionVoter.ACCESS_ABSTAIN;

			/*
			// for testing timing of "simple call"
			if (true) {
				// return ACCESS_ABSTAIN; // this may cause "Access denied"
				return ACCESS_GRANTED;
			}				
			 */

			final Method method = invokeMethod.getMethod();
			final Class<?>[] params = method.getParameterTypes();

			/*
			 * Здесь кодирована некоторая проверка, достаточно витиеватая, чтобы 
			 * приблизится к реальностям бизнес-логики :
			 * фильтр 
			 * 		для конкретного метода (хотя метод указан и в public-services-security-context.xml,
			 * но т.к. проверку можно повесить ещё и на другие методы, здесь 
			 * отсекаем возможные другие);
			 * 		для конкретного типа документов;
			 * 		выполняем проверку значения некоторого атрибута ... 
			 */
			if ( "getProperties".equals(method.getName())) {
				// final Class<?>[] params = invocation.getMethod().getParameterTypes();
				final NodeRef ref = (NodeRef) invokeMethod.getArguments()[0];
				final QName atype =  nodeService.getType(ref);

				if (log.isDebugEnabled())
					sb.append( String.format( "emulating check for node {%s} of type [%s] ... \r\n", ref.getId(), atype));

				if (this.supportingTypes.chkNodeOrAspectsIsOfType(ref, nodeService, false)) {
					// получение аспектов ...
					final Set<QName> aspects = this.nodeService.getAspects(ref);

					// получение свойства для тестовой проверки ... 
					final Object value = this.nodeService.getProperty(ref, PROP_FLAG);
					final boolean allowed = (Boolean.TRUE.equals(value));
					if (log.isDebugEnabled())
						// здесь можно вернуть return (allowed) ? ACCESS_GRANTED : ACCESS_DENIED;
						sb.append( String.format( "check for node {%s} is %s (aspect count %s)\r\n"
							, ref.getId()
							, String.valueOf(allowed).toUpperCase()
							, (aspects != null ? aspects.size() : "NULL")
						));
				}
			} else 
				log.info( String.format( "(!) method '%s' is not 'getProperties' !?", method.getName()));

			Boolean hasMethodEntry = null;

			for (ConfigAttributeDefintion cad : supportedDefinitions)
			{
				NodeRef testNodeRef = null;

				if (cad.typeString.equals(ACL_ORG_DENY)) {
					return AccessDecisionVoter.ACCESS_DENIED;

				} else if (cad.typeString.equals(ACL_ORG_ALLOW)) {
					return AccessDecisionVoter.ACCESS_GRANTED;

				} else if (cad.typeString.equals(ACL_ORG_ROLEMETHOD)) {
					if(hasMethodEntry == null)
						hasMethodEntry = Boolean.FALSE;

					if (cad.authority.equals(AuthenticationUtil.getRunAsUser()))
						hasMethodEntry = Boolean.TRUE;
					else if(authorityService.getAuthorities().contains(cad.authority))
						hasMethodEntry = Boolean.TRUE;

				} else if (cad.parameter >= invokeMethod.getArguments().length) {
					// check args for case "PARENT_XXX" and "NODE_XXX"
					log.warn( String.format( "rule %S \n\t is not aplliable for short arguments list (%s args) -> skipped", cad.toString(), invokeMethod.getArguments().length ));
					continue;

				} else if (cad.typeString.equals(ACL_ORG_NODE)) { 
					// (!) Check NodeRef or StoreRef
					if (StoreRef.class.isAssignableFrom(params[cad.parameter])) {
						/* Test StoreRef ... */
						final StoreRef storeRef = (StoreRef) invokeMethod.getArguments()[cad.parameter];
						if (storeRef != null) {
							if (nodeService.exists(storeRef))
								testNodeRef = nodeService.getRootNode(storeRef);
							if (log.isDebugEnabled())
								sb.append( String.format( "\tPermission test against the storeR " +
										"ef [%s] -> using permissions on the root node {%s} \r\n"
										, storeRef, testNodeRef));
						}

					} else if (NodeRef.class.isAssignableFrom(params[cad.parameter])) {
						/* Test NodeRef... */
						testNodeRef = (NodeRef) invokeMethod.getArguments()[cad.parameter];
						if (log.isDebugEnabled())
							getNodeInfo(testNodeRef, sb);

					} else if (ChildAssociationRef.class.isAssignableFrom(params[cad.parameter])) {
						if (invokeMethod.getArguments()[cad.parameter] != null) {
							testNodeRef = ((ChildAssociationRef) invokeMethod.getArguments()[cad.parameter]).getChildRef();
							if (log.isDebugEnabled())
								getNodeInfo(testNodeRef, sb, "for childRef ");
						}
					}

					else {
						throw new OrgStructureVoterException("The specified parameter is not a NodeRef/StoreRef or ChildAssociationRef");
					}

				} else if (cad.typeString.equals(ACL_ORG_PARENT)) {
					// There is no point having parent permissions for store refs
					if (NodeRef.class.isAssignableFrom(params[cad.parameter])) {
						final NodeRef child = (NodeRef) invokeMethod.getArguments()[cad.parameter];
						if (child != null) {
							testNodeRef = nodeService.getPrimaryParent(child).getParentRef();
							if (log.isDebugEnabled())
								getNodeInfo(testNodeRef, sb, "for parent ");
						}

					} else if (ChildAssociationRef.class.isAssignableFrom(params[cad.parameter])) {
						if (invokeMethod.getArguments()[cad.parameter] != null) {
							testNodeRef = ((ChildAssociationRef) invokeMethod.getArguments()[cad.parameter]).getParentRef();
							if (log.isDebugEnabled())
								getNodeInfo(testNodeRef, sb, "for parent of child assoc ");
						}

					} else
						throw new OrgStructureVoterException("The specified parameter is not a parent NodeRef/ChildAssociationRef");
				}

				if (testNodeRef != null) {

					if (log.isDebugEnabled())
						sb.append("\t\tNode ref is not null \r\n");

					// now we know the node - we can abstain for certain types and aspects (eg. RM)
					if(!abstainFor.isEmpty()) {
						// For AVM we can not get type and aspect without going through internal AVM security checks
						// AVM is never excluded
						if(!testNodeRef.getStoreRef().getProtocol().equals(StoreRef.PROTOCOL_AVM)) {
							// check node exists (note: for AVM deleted nodes, will skip the abstain check, since exists/getType is accessed via AVMNodeService)
							if (nodeService.exists(testNodeRef)) {
								if (abstainFor.chkNodeOrAspectsIsOfType(testNodeRef, nodeService, false)) {
									if (log.isDebugEnabled())
										sb.append( String.format( "Node {%s} type/aspects is at abstain list -> abstained (unchecked) node", testNodeRef));
									continue; // TODO: (?) or return AccessDecisionVoter.ACCESS_ABSTAIN;
								}
							}
						}
					}

					// доп бизнес-логика для провеки доступа к узлу
					// TODO: AccessChecker must return {null | ACCESS_GRANTED | ACCESS_DENIED | ACCESS_ABSTAIN }
					// final Integer customResult = (customChecker.checked(testNodeRef, cad.required.toString()));
					// if (customResult != null) return customResult;

					if (permissionService.hasPermission(testNodeRef, cad.required.toString()) == AccessStatus.DENIED) {
						if (log.isDebugEnabled()) {
							sb.append( String.format( "\t\t Permission denied for node {%s} \r\n", testNodeRef));
							// Thread.dumpStack();
						}
						return AccessDecisionVoter.ACCESS_DENIED;
					}

				}
			}

			return ((hasMethodEntry == null) || (hasMethodEntry.booleanValue()))
						? AccessDecisionVoter.ACCESS_GRANTED
						: AccessDecisionVoter.ACCESS_DENIED;
		} finally {
			if (log.isDebugEnabled() && sb.length() > 0)
				log.debug(sb.toString());
		}
	}


	private List<ConfigAttributeDefintion> extractSupportedDefinitions(ConfigAttributeDefinition config)
	{
		final List<ConfigAttributeDefintion> definitions = new ArrayList<ConfigAttributeDefintion>(10);
		final Iterator<?> iter = config.getConfigAttributes();
		while (iter.hasNext()) {
			final ConfigAttribute attr = (ConfigAttribute) iter.next();
			if (this.supports(attr))
				definitions.add(new ConfigAttributeDefintion(attr));
		}
		return definitions;
	}

	private class ConfigAttributeDefintion {
		String typeString;
		SimplePermissionReference required;
		int parameter;
		String authority;

		ConfigAttributeDefintion(ConfigAttribute attr)
		{
			final StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
			if (st.countTokens() < 1) 
				throw new OrgStructureVoterException( String.format("There must be at least one token in a config attribute '%s'", attr.getAttribute()));

			typeString = st.nextToken();

			final Set<String> supported = getSupportedConfigAttributeTypes();
			if (!supported.contains(typeString))
				throw new OrgStructureVoterException( String.format( "Invalid type [%s] must be one of %s", typeString, supported));

			if (typeString.equals(ACL_ORG_NODE) || typeString.equals(ACL_ORG_PARENT)) {
				if (st.countTokens() != 3)
					throw new OrgStructureVoterException( String.format(
							"There must be four . separated tokens in config attribute: type [%s] has %s\r\t config attr is '%s'"
							, typeString, st.countTokens(), attr.getAttribute() 
							));
				final String numberString = st.nextToken();
				final String qNameString = st.nextToken();
				final String permissionString = st.nextToken();

				parameter = Integer.parseInt(numberString);

				final QName qName = QName.createQName(qNameString, nspr);

				required = SimplePermissionReference.getPermissionReference(qName, permissionString);
			}
			else if (typeString.equals(ACL_ORG_ROLEMETHOD)) {
				if (st.countTokens() != 1)
					throw new OrgStructureVoterException( String.format(
							"There must be two . separated tokens in each group or role config attribute: type [%s] has %s\r\t config attr is '%s'"
							, typeString, st.countTokens(), attr.getAttribute() 
							));
				authority = st.nextToken();
			} // else seems to be DENY/ALLOW

		}

		@Override
		public String toString() {
			return "["
					+ "type " + typeString
					+ ", req " + required.toString() 
					+ ", argIndex " + parameter
					+ ", authority " + authority 
					+ "]";
		}

	}

}
