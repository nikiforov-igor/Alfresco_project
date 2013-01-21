package ru.it.lecm.security.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.afterinvocation.AfterInvocationProvider;

import org.alfresco.cmis.CMISResultSet;
import org.alfresco.repo.search.ResultSetRowIterator;
import org.alfresco.repo.search.SimpleResultSetMetaData;
import org.alfresco.repo.search.impl.lucene.PagingLuceneResultSet;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.repo.search.impl.querymodel.QueryEngineResults;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.PermissionCheckCollection;
import org.alfresco.repo.security.permissions.PermissionCheckValue;
import org.alfresco.repo.security.permissions.PermissionCheckedCollection.PermissionCheckedCollectionMixin;
import org.alfresco.repo.security.permissions.PermissionCheckedValue;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.repo.security.permissions.impl.acegi.ACLEntryVoterException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.PermissionEvaluationMode;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetMetaData;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDeniedException;

import ru.it.lecm.utils.StrUtils;
import ru.it.lecm.utils.alfresco.QNameResolver;

/**
 * Проверка прав доступа на узлы ПОСЛЕ выполнения вызова secured-методов служ
 * бинов. По мотивам org.alfresco.repo.security.permissions.impl.acegi.
 * ACLEntryAfterInvocationProvider Создан пдля проверки бремени на вызов
 * search.query
 */
public class OrgStrucureAfterInvocationProvider
	extends MethodInvocationBaseBean implements AfterInvocationProvider
{
	// static protected Log log =
	// LogFactory.getLog(OrgStrucureAfterInvocationProvider.class);

	/*
	 * import org.alfresco.repo.security.permissions.impl.acegi.
	 * MarkingAfterInvocationProvider; import
	 * org.alfresco.repo.security.permissions
	 * .impl.acegi.MethodSecurityInterceptor;
	 */

	public static final String ORG_AFTER_ACL_NODE = "ORG_AFTER_ACL_NODE";
	public static final String ORG_AFTER_ACL_PARENT = "ORG_AFTER_ACL_PARENT";

	private int maxPermissionChecks = Integer.MAX_VALUE;
	private long maxPermissionCheckTimeMillis = Long.MAX_VALUE;

	// private boolean optimisePermissionsCheck = true;
	private int optimisePermissionsBulkFetchSize;

	/**
	 * Список не проверяемых классов, для которых ничего не выполняется
	 */
	private QNameResolver unfilteredFor = new QNameResolver();

	final private Set<String> restrictedIds = new HashSet<String>();

	/**
	 * Types and aspects for which we will abstain on voting if they are
	 * present.
	 * 
	 * @param abstainFor
	 */
	public void setUnfilteredFor(Set<String> unfilteredFor) {
		this.unfilteredFor.setSynonyms(unfilteredFor);
	}

	public void setMaxPermissionChecks(int maxPermissionChecks) {
		this.maxPermissionChecks = maxPermissionChecks;
	}

	/**
	 * Set the max time for permission checks
	 * 
	 * @param maxPermissionCheckTimeMillis
	 */
	public void setMaxPermissionCheckTimeMillis(
			long maxPermissionCheckTimeMillis) {
		this.maxPermissionCheckTimeMillis = maxPermissionCheckTimeMillis;
	}

	public void setOptimisePermissionsBulkFetchSize(
			int optimisePermissionsBulkFetchSize) {
		this.optimisePermissionsBulkFetchSize = optimisePermissionsBulkFetchSize;
	}

	public void setRestrictedIds(Collection<String> values) {
		this.restrictedIds.clear();
		if (values != null)
			this.restrictedIds.addAll(values);
		log.info("current node restricted list is "
				+ StrUtils.nvl(this.restrictedIds, "Empty"));
	}

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		if (unfilteredFor != null) {
			unfilteredFor.resolve(this.nspr);
			log.warn("Configured unfiltered types list: "
					+ unfilteredFor.toString());
		} else
			log.warn("Configured unfiltered types is empty");
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return (attribute.getAttribute() != null)
				&& (attribute.getAttribute().startsWith(ORG_AFTER_ACL_NODE) 
						|| 
						attribute.getAttribute().startsWith(ORG_AFTER_ACL_PARENT));
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return MethodInvocation.class.isAssignableFrom(clazz);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object decide(Authentication authentication, Object hookObject,
			ConfigAttributeDefinition config, Object returnedObject)
					throws AccessDeniedException {
		final MethodInvocation mi = (MethodInvocation) hookObject;

		final StringBuilder sb = new StringBuilder();
		try {
			if (log.isDebugEnabled())
				sb.append("Method: " + mi.getMethod().toString());

			if (!"query".equals(mi.getMethod().getName()))
				return returnedObject;

			if (AuthenticationUtil.isRunAsUserTheSystemUser()) {
				if (log.isDebugEnabled())
					sb.append("Allowing system user access");
				return returnedObject;
			}

			if (returnedObject == null) {
				if (log.isDebugEnabled())
					sb.append("Allowing null object access");
				return null;
			}

			if (PermissionCheckedValue.class.isAssignableFrom(returnedObject
					.getClass())) {
				// The security provider was not already present
				return decide(authentication, hookObject, config,
						(PermissionCheckedValue) returnedObject);
			}

			if (PermissionCheckValue.class.isAssignableFrom(returnedObject
					.getClass())) {
				return decide(authentication, hookObject, config,
						(PermissionCheckValue) returnedObject);
			}

			if (StoreRef.class.isAssignableFrom(returnedObject.getClass())) {
				return decide(authentication, hookObject, config,
						nodeService.getRootNode((StoreRef) returnedObject))
						.getStoreRef();
			}

			if (NodeRef.class.isAssignableFrom(returnedObject.getClass())) {
				return decide(authentication, hookObject, config,
						(NodeRef) returnedObject);
			}

			if (Pair.class.isAssignableFrom(returnedObject.getClass())) {
				return decide(authentication, hookObject, config,
						(Pair) returnedObject);
			}

			if (ChildAssociationRef.class.isAssignableFrom(returnedObject
					.getClass())) {
				return decide(authentication, hookObject, config,
						(ChildAssociationRef) returnedObject);
			}

			if (SolrJSONResultSet.class.isAssignableFrom(returnedObject
					.getClass())) {
				// return returnedObject;
				final SolrJSONResultSet rs = (SolrJSONResultSet) returnedObject;
				return decide(authentication, returnedObject, config, rs);
			}

			if (CMISResultSet.class.isAssignableFrom(returnedObject.getClass())) {
				return returnedObject;
			}

			if (PagingLuceneResultSet.class.isAssignableFrom(returnedObject
					.getClass())) {
				return decide(authentication, hookObject, config,
						(PagingLuceneResultSet) returnedObject);
			}

			if (ResultSet.class.isAssignableFrom(returnedObject.getClass())) {
				return decide(authentication, hookObject, config,
						(ResultSet) returnedObject);
			}

			if (QueryEngineResults.class.isAssignableFrom(returnedObject
					.getClass())) {
				return decide(authentication, hookObject, config,
						(QueryEngineResults) returnedObject);
			}

			if (Collection.class.isAssignableFrom(returnedObject.getClass())) {
				return decide(authentication, hookObject, config,
						(Collection) returnedObject);
			}

			if (returnedObject.getClass().isArray()) {
				return decide(authentication, hookObject, config,
						(Object[]) returnedObject);
			}

			if (log.isDebugEnabled())
				sb.append("Uncontrolled object - access allowed for "
						+ hookObject.getClass().getName());
			return returnedObject;
		} catch (AccessDeniedException ade) {
			if (log.isDebugEnabled())
				sb.append("Access denied");
			ade.printStackTrace();
			throw ade;
		} catch (RuntimeException re) {
			if (log.isDebugEnabled())
				sb.append("Access denied by runtime exception");
			re.printStackTrace();
			throw re;
		} finally {
			if (log.isDebugEnabled())
				log.debug(sb.toString());
		}
	}

	private NodeRef decide(Authentication authentication, Object object,
			ConfigAttributeDefinition config, NodeRef returnedObject)
					throws AccessDeniedException {
		if (returnedObject == null)
			return null;

		if (isUnfiltered(returnedObject))
			return returnedObject;

		final List<ConfigAttributeDefintion> supportedDefinitions = extractSupportedDefinitions(config);

		if (supportedDefinitions.size() == 0)
			return returnedObject;

		for (ConfigAttributeDefintion cad : supportedDefinitions) {
			NodeRef testNodeRef = null;

			if (cad.typeString.equals(ORG_AFTER_ACL_NODE))
				testNodeRef = returnedObject;
			else if (cad.typeString.equals(ORG_AFTER_ACL_PARENT))
				testNodeRef = nodeService.getPrimaryParent(returnedObject).getParentRef();

			if (!hasAccess(testNodeRef, cad.required.toString()))
				throw new AccessDeniedException("Access Denied");
		}

		return returnedObject;
	}

	private boolean hasAccess(NodeRef ref, String authority) {

		if (isUnfiltered(ref))
			return true;

		if (this.restrictedIds.contains(ref.getId()))
			return false;

		// >>>
		{
			/* Node ID filter test */
			if (this.restrictedIds.contains(ref.getId())) {
				// отладочный DENY ...
				if (log.isDebugEnabled())
					log.debug(String.format(
							"DENY_BY_ID for node [%s]\r\n", ref.getId()));
				return false;
			}

			/* properties accessor test */
			if (this.supportingTypes.chkNodeOrAspectsIsOfType(ref, nodeService, false)) {
				// получение аспектов ...
				final Set<QName> aspects = this.nodeService.getAspects(ref);
				// получение свойства для тестовой проверки ...
				final Object value = this.nodeService.getProperty(ref, PROP_FLAG);
				if (!Boolean.TRUE.equals(value))
					return false;
			}
		}
		// <<<

		return (ref == null)
				|| (authority != null && (permissionService.hasPermission(ref, authority) != AccessStatus.DENIED) )
				|| (authority == null && permissionService.hasReadPermission(ref) != AccessStatus.DENIED)
				;
	}

	protected boolean isUnfiltered(NodeRef returnedObject) {
		if (returnedObject == null || !nodeService.exists(returnedObject)) {
			// Standard practice for non-existent NodeRef is to pass it as
			// unfiltered.
			// See PermissionServiceImpl.hasPermission
			// See ALF-5559: Permission interceptors can fail if Lucene returns
			// invalid NodeRefs
			return true;
		}
		return unfilteredFor.chkNodeOrAspectsIsOfType(returnedObject,
				nodeService, false);
	}

	private PermissionCheckedValue decide(Authentication authentication,
			Object object, ConfigAttributeDefinition config,
			PermissionCheckedValue returnedObject) throws AccessDeniedException {
		// This passes as it has already been filtered
		// TODO: Get the filter that was applied and double-check
		return returnedObject;
	}

	private PermissionCheckValue decide(Authentication authentication,
			Object object, ConfigAttributeDefinition config,
			PermissionCheckValue returnedObject) throws AccessDeniedException {
		// Get the wrapped value
		final NodeRef nodeRef = returnedObject.getNodeRef();
		decide(authentication, object, config, nodeRef);
		// This passes
		return returnedObject;
	}

	@SuppressWarnings("rawtypes")
	private Pair decide(Authentication authentication, Object object,
			ConfigAttributeDefinition config, Pair returnedObject)
					throws AccessDeniedException {
		final NodeRef nodeRef = (NodeRef) returnedObject.getSecond();
		decide(authentication, object, config, nodeRef);
		// the noderef was allowed
		return returnedObject;
	}

	@SuppressWarnings("rawtypes")
	protected List<ConfigAttributeDefintion> extractSupportedDefinitions(
			ConfigAttributeDefinition config) 
	{
		final List<ConfigAttributeDefintion> definitions = new ArrayList<ConfigAttributeDefintion>();
		final Iterator iter = config.getConfigAttributes();

		while (iter.hasNext()) {
			final ConfigAttribute attr = (ConfigAttribute) iter.next();
			if (this.supports(attr))
				definitions.add(new ConfigAttributeDefintion(attr));
		}
		return definitions;
	}

	private ChildAssociationRef decide(Authentication authentication,
			Object object, ConfigAttributeDefinition config,
			ChildAssociationRef returnedObject) throws AccessDeniedException {
		if (returnedObject == null)
			return null;

		final List<ConfigAttributeDefintion> supportedDefinitions = extractSupportedDefinitions(config);

		if (supportedDefinitions.size() == 0)
			return returnedObject;

		for (ConfigAttributeDefintion cad : supportedDefinitions) {
			NodeRef testNodeRef = null;

			if (ORG_AFTER_ACL_NODE.equals(cad.typeString))
				testNodeRef = ((ChildAssociationRef) returnedObject).getChildRef();
			else if (ORG_AFTER_ACL_PARENT.equals(cad.typeString))
				testNodeRef = ((ChildAssociationRef) returnedObject).getParentRef();

			if (!hasAccess(testNodeRef, cad.required.toString()))
				throw new AccessDeniedException("Access Denied");
		}

		return returnedObject;
	}

	private ResultSet decide(Authentication authentication, Object object,
			ConfigAttributeDefinition config,
			PagingLuceneResultSet returnedObject) throws AccessDeniedException {
		final ResultSet raw = returnedObject.getWrapped();
		final ResultSet filteredForPermissions = decide(authentication, object, config, raw);
		final PagingLuceneResultSet newPaging = new PagingLuceneResultSet(
				filteredForPermissions, returnedObject.getResultSetMetaData().getSearchParameters(), nodeService);
		return newPaging;
	}

	private ResultSet decide(Authentication authentication, Object object,
			ConfigAttributeDefinition config, ResultSet returnedObject)
					throws AccessDeniedException

					{
		if (returnedObject == null)
			return null;

		final FilteringResultSet filteringResultSet = new FilteringResultSet(returnedObject);

		final List<ConfigAttributeDefintion> supportedDefinitions = extractSupportedDefinitions(config);

		Integer maxSize = null;
		if (returnedObject.getResultSetMetaData().getSearchParameters().getMaxItems() >= 0)
			maxSize = new Integer(returnedObject.getResultSetMetaData().getSearchParameters().getMaxItems());
		if ((maxSize == null)&& (returnedObject.getResultSetMetaData().getSearchParameters().getLimitBy() == LimitBy.FINAL_SIZE))
			maxSize = new Integer(returnedObject.getResultSetMetaData().getSearchParameters().getLimit());
		// Allow for skip
		if ((maxSize != null)
				&& (returnedObject.getResultSetMetaData().getSearchParameters().getSkipCount() >= 0))
			maxSize = new Integer(maxSize + returnedObject.getResultSetMetaData().getSearchParameters().getSkipCount());

		int maxChecks = maxPermissionChecks;
		if (returnedObject.getResultSetMetaData().getSearchParameters().getMaxPermissionChecks() >= 0)
			maxChecks = returnedObject.getResultSetMetaData().getSearchParameters().getMaxPermissionChecks();

		long maxCheckTime = maxPermissionCheckTimeMillis;
		if (returnedObject.getResultSetMetaData().getSearchParameters().getMaxPermissionCheckTimeMillis() >= 0)
			maxCheckTime = returnedObject.getResultSetMetaData().getSearchParameters().getMaxPermissionCheckTimeMillis();

		if (supportedDefinitions.size() == 0) {
			if (maxSize == null)
				return returnedObject;
			for (int i = 0; i < maxSize.intValue(); i++) {
				filteringResultSet.setIncluded(i, true);
			}
			filteringResultSet.setResultSetMetaData(
					new SimpleResultSetMetaData(
						(returnedObject.length() > maxSize.intValue() ? LimitBy.FINAL_SIZE : LimitBy.UNLIMITED),
						PermissionEvaluationMode.EAGER,
						returnedObject.getResultSetMetaData().getSearchParameters()));
		}

		if (returnedObject.length() > 0) {
			// force prefetch before starting record time
			boolean builkFetch = returnedObject.getBulkFetch();
			returnedObject.setBulkFetch(false);
			returnedObject.getNodeRef(returnedObject.length() - 1);
			returnedObject.setBulkFetch(builkFetch);
		}

		// record the start time
		long startTimeMillis = System.currentTimeMillis();
		// set the default, unlimited resultset type
		filteringResultSet.setResultSetMetaData(new SimpleResultSetMetaData(
				LimitBy.UNLIMITED, PermissionEvaluationMode.EAGER,
				returnedObject.getResultSetMetaData().getSearchParameters()));

		// use the result set to do bulk loading
		boolean oldBulkFetch = returnedObject.setBulkFetch(true);
		int oldFetchSize = returnedObject.setBulkFetchSize(optimisePermissionsBulkFetchSize);

		int i_out = 0, i_all = 0;
		try {
			for (int i = 0; i < returnedObject.length(); i++) {
				i_all++;
				long currentTimeMillis = System.currentTimeMillis();
				if (i >= maxChecks) {
					log.warn("maxChecks exceeded (" + maxChecks + ")", new Exception("Back Trace"));
					filteringResultSet.setResultSetMetaData(
						new SimpleResultSetMetaData(
							LimitBy.NUMBER_OF_PERMISSION_EVALUATIONS,
							PermissionEvaluationMode.EAGER,
							returnedObject.getResultSetMetaData().getSearchParameters()));
					break;
				}

				if ((currentTimeMillis - startTimeMillis) > maxCheckTime) {
					log.warn("maxCheckTime exceeded ("+ (currentTimeMillis - startTimeMillis) + " milliseconds)", new Exception("Back Trace"));
					filteringResultSet .setResultSetMetaData(
						new SimpleResultSetMetaData(
							LimitBy.NUMBER_OF_PERMISSION_EVALUATIONS,
							PermissionEvaluationMode.EAGER,
							returnedObject.getResultSetMetaData().getSearchParameters()));
					break;
				}

				// All permission checks must pass
				filteringResultSet.setIncluded(i, true);

				final NodeRef nodeRef = returnedObject.getNodeRef(i);

				if (filteringResultSet.getIncluded(i)
						// && permissionService.hasReadPermission(nodeRef) == AccessStatus.DENIED
						&& !hasAccess(nodeRef, null)
					) {
					filteringResultSet.setIncluded(i, false);
					i_out++;
				}

				// Bug out if we are limiting by size
				if ((maxSize != null)
						&& (filteringResultSet.length() > maxSize.intValue())) {
					// Remove the last match to fix the correct size
					if (filteringResultSet.getIncluded(i)) {
						filteringResultSet.setIncluded(i, false);
						i_out++;
					}
					filteringResultSet.setResultSetMetaData(
						new SimpleResultSetMetaData(
							LimitBy.FINAL_SIZE,
							PermissionEvaluationMode.EAGER,
							returnedObject.getResultSetMetaData().getSearchParameters()));
					break;
				}
			}
		} finally {
			// put them back to how they were
			returnedObject.setBulkFetch(oldBulkFetch);
			returnedObject.setBulkFetchSize(oldFetchSize);
			log.info(String.format("Filtered out %s of %s (%s available)", i_out, i_all, i_all - i_out));
		}

		return filteringResultSet;
	}

	private QueryEngineResults decide(Authentication authentication,
			Object object, ConfigAttributeDefinition config,
			QueryEngineResults returnedObject) throws AccessDeniedException
	{
		final Map<Set<String>, ResultSet> map = returnedObject.getResults();
		final Map<Set<String>, ResultSet> answer = new HashMap<Set<String>, ResultSet>(map.size(), 1.0f);

		for (Set<String> group : map.keySet()) {
			final ResultSet raw = map.get(group);
			ResultSet permed;
			if (PagingLuceneResultSet.class.isAssignableFrom(raw.getClass()))
				permed = decide(authentication, object, config, (PagingLuceneResultSet) raw);
			else
				permed = decide(authentication, object, config, raw);
			answer.put(group, permed);
		}
		return new QueryEngineResults(answer);
	}

	final private static String NAMESPACE = "http://www.it.ru/lecm/model/blanks/1.0";
	// final private static String TYPENAME = "blank";

	final private static QName PROP_FLAG = QName.createQName(NAMESPACE, "flag");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection decide(Authentication authentication, Object object,
			ConfigAttributeDefinition config, Collection returnedObject)
					throws AccessDeniedException {
		if (returnedObject == null)
			return null;

		final List<ConfigAttributeDefintion> supportedDefinitions = extractSupportedDefinitions(config);
		if (log.isDebugEnabled())
			log.debug("Entries are " + supportedDefinitions);

		if (supportedDefinitions.size() == 0)
			return returnedObject;

		// Default to the system-wide values and we'll see if they need to be
		// reduced
		long targetResultCount = returnedObject.size();
		int maxPermissionChecks = Integer.MAX_VALUE;
		long maxPermissionCheckTimeMillis = this.maxPermissionCheckTimeMillis;
		if (returnedObject instanceof PermissionCheckCollection<?>) {
			PermissionCheckCollection permissionCheckCollection = (PermissionCheckCollection) returnedObject;
			// Get values
			targetResultCount = permissionCheckCollection.getTargetResultCount();
			if (permissionCheckCollection.getCutOffAfterCount() > 0)
				maxPermissionChecks = permissionCheckCollection.getCutOffAfterCount();
			if (permissionCheckCollection.getCutOffAfterTimeMs() > 0)
				maxPermissionCheckTimeMillis = permissionCheckCollection.getCutOffAfterTimeMs();
		}

		// Start timer and counter for cut-off
		boolean cutoff = false;
		long startTimeMillis = System.currentTimeMillis();
		int count = 0;

		// Keep values explicitly
		List<Object> keepValues = new ArrayList<Object>(returnedObject.size());

		for (Object curObject : returnedObject) {
			// if the maximum result size or time has been exceeded, then we
			// have to remove only
			long currentTimeMillis = System.currentTimeMillis();

			if (keepValues.size() >= targetResultCount)
				// We have enough results. We stop without cutoff.
				break;
			if (count >= maxPermissionChecks) {
				// We have been cut off by count
				cutoff = true;
				if (log.isDebugEnabled())
					log.debug("decide (collection) cut-off: " + count
							+ " checks exceeded " + maxPermissionChecks
							+ " checks");
				break;
			}
			if ((currentTimeMillis - startTimeMillis) > maxPermissionCheckTimeMillis) {
				// We have been cut off by time
				cutoff = true;
				if (log.isDebugEnabled())
					log.debug("decide (collection) cut-off: "
							+ (currentTimeMillis - startTimeMillis)
							+ "ms exceeded " + maxPermissionCheckTimeMillis
							+ "ms");
				break;
			}

			boolean allowed = true;
			for (ConfigAttributeDefintion cad : supportedDefinitions) {
				NodeRef testNodeRef = null;
				if (ORG_AFTER_ACL_NODE.equals(cad.typeString)) {
					if (StoreRef.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = nodeService.getRootNode((StoreRef) curObject);
					else if (NodeRef.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = (NodeRef) curObject;
					else if (ChildAssociationRef.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = ((ChildAssociationRef) curObject).getChildRef();
					else if (Pair.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = (NodeRef) ((Pair) curObject).getSecond();
					else if (PermissionCheckValue.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = ((PermissionCheckValue) curObject).getNodeRef();
					else
						throw new ACLEntryVoterException(
								"The specified parameter is not recognized: "+ curObject.getClass());
				} else if (ORG_AFTER_ACL_PARENT.equals(cad.typeString)) {
					if (StoreRef.class.isAssignableFrom(curObject.getClass()))
						// Will be allowed
						testNodeRef = null;
					else if (NodeRef.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = nodeService.getPrimaryParent( (NodeRef) curObject).getParentRef();
					else if (ChildAssociationRef.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = ((ChildAssociationRef) curObject).getParentRef();
					else if (Pair.class.isAssignableFrom(curObject.getClass()))
						testNodeRef = (NodeRef) ((Pair) curObject).getSecond();
					else if (PermissionCheckValue.class.isAssignableFrom(curObject.getClass())) {
						NodeRef nodeRef = ((PermissionCheckValue) curObject).getNodeRef();
						testNodeRef = nodeService.getPrimaryParent(nodeRef).getParentRef();
					} else
						throw new ACLEntryVoterException( "The specified parameter is recognized: "+ curObject.getClass());
				}

				if (log.isDebugEnabled())
					log.debug("\t" + cad.typeString + " test on " + testNodeRef
							+ " from " + curObject.getClass().getName());

				// >>>
				if (!hasAccess(testNodeRef, cad.required.toString())) {
					allowed = false;
					break; // for
				}
			} // for

			// Failure or success, increase the count
			count++;

			if (allowed)
				keepValues.add(curObject);
		}

		// Work out how many were left unchecked (for whatever reason)
		int sizeOriginal = returnedObject.size();
		int checksRemaining = sizeOriginal - count;
		// Note: There are use-cases where unmodifiable collections are passing
		// through.
		// So make sure that the collection needs modification at all
		if (keepValues.size() < sizeOriginal) {
			// There are values that need to be removed. We have to modify the
			// collection.
			try {
				returnedObject.clear();
				returnedObject.addAll(keepValues);
			} catch (UnsupportedOperationException e) {
				throw new AccessDeniedException(
						"Permission-checked list must be modifiable", e);
			}
		}

		// Attach the extra permission-check data to the collection
		return PermissionCheckedCollectionMixin.create(returnedObject, cutoff,
				checksRemaining, sizeOriginal);
	}

	private Object[] decide(Authentication authentication, Object object,
			ConfigAttributeDefinition config, Object[] returnedObject)
					throws AccessDeniedException {
		if (returnedObject == null)
			return null;
		final Collection<?> filtered = decide(authentication, returnedObject,
				config, Arrays.asList(returnedObject));

		if (filtered == null)
			return null;

		final boolean changed = filtered.size() != returnedObject.length;
		if (!changed) // no changes -> keep original result
			return returnedObject;

		return filtered.toArray();
	}

	private class ConfigAttributeDefintion {
		String typeString;
		SimplePermissionReference required;

		ConfigAttributeDefintion(ConfigAttribute attr) {
			final StringTokenizer st = new StringTokenizer(attr.getAttribute(),
					".", false);
			if (st.countTokens() != 3)
				throw new ACLEntryVoterException(
						"There must be three . separated tokens in each config attribute");
			typeString = st.nextToken();
			String qNameString = st.nextToken();
			String permissionString = st.nextToken();

			if (!(typeString.equals(ORG_AFTER_ACL_NODE) || typeString.equals(ORG_AFTER_ACL_PARENT)))
				throw new ACLEntryVoterException(String.format(
						"Invalid type: must be %s or %s", ORG_AFTER_ACL_NODE,
						ORG_AFTER_ACL_PARENT));

			QName qName = QName.createQName(qNameString, nspr);

			required = SimplePermissionReference.getPermissionReference(qName,
					permissionString);
		}
	}

	public static class FilteringResultSet implements ResultSet {
		private ResultSet unfiltered;

		private BitSet inclusionMask;

		private ResultSetMetaData resultSetMetaData;

		public FilteringResultSet(ResultSet unfiltered) {
			super();
			this.unfiltered = unfiltered;
			inclusionMask = new BitSet(unfiltered.length());
		}

		// public FilteringResultSet(ResultSet unfiltered, BitSet inclusionMask)
		// {
		// super();
		// this.unfiltered = unfiltered;
		// this.inclusionMask = inclusionMask;
		// }
		//
		// /* package */ResultSet getUnFilteredResultSet()
		// {
		// return unfiltered;
		// }

		/* package */void setIncluded(int i, boolean excluded) {
			inclusionMask.set(i, excluded);
		}

		/* package */boolean getIncluded(int i) {
			return inclusionMask.get(i);
		}

		/**
		 * @return filtered length
		 */
		public int length() {
			return inclusionMask.cardinality();
		}

		/**
		 * @return ulfiltered source data rows
		 */
		public int rawLength() {
			assert( unfiltered.length() == inclusionMask.length());
			return inclusionMask.length();
		}

		private int translateIndex(int n) {
			if (n <= length()) {
				int count = -1;
				for (int i = 0, l = unfiltered.length(); i < l; i++) {
					if (inclusionMask.get(i))
						count++;
					if (count == n)
						return i;
				}
			}
			throw new IndexOutOfBoundsException();
		}

		public NodeRef getNodeRef(int n) {
			return unfiltered.getNodeRef(translateIndex(n));
		}

		public float getScore(int n) {
			return unfiltered.getScore(translateIndex(n));
		}

		public void close() {
			unfiltered.close();
		}

		public ResultSetRow getRow(int i) {
			return unfiltered.getRow(translateIndex(i));
		}

		public List<NodeRef> getNodeRefs() {
			ArrayList<NodeRef> answer = new ArrayList<NodeRef>(length());
			for (ResultSetRow row : this) {
				answer.add(row.getNodeRef());
			}
			return answer;
		}

		public List<ChildAssociationRef> getChildAssocRefs() {
			ArrayList<ChildAssociationRef> answer = new ArrayList<ChildAssociationRef>(
					length());
			for (ResultSetRow row : this) {
				answer.add(row.getChildAssocRef());
			}
			return answer;
		}

		public ChildAssociationRef getChildAssocRef(int n) {
			return unfiltered.getChildAssocRef(translateIndex(n));
		}

		public ListIterator<ResultSetRow> iterator() {
			return new FilteringIterator();
		}

		class FilteringIterator implements ResultSetRowIterator {
			// -1 at the start
			int underlyingPosition = -1;

			public boolean hasNext() {
				return inclusionMask.nextSetBit(underlyingPosition + 1) != -1;
			}

			public ResultSetRow next() {
				underlyingPosition = inclusionMask.nextSetBit(underlyingPosition + 1);
				if (underlyingPosition == -1)
					throw new IllegalStateException();
				return unfiltered.getRow(underlyingPosition);
			}

			public boolean hasPrevious() {
				if (underlyingPosition > 0) {
					for (int i = underlyingPosition - 1; i >= 0; i--) {
						if (inclusionMask.get(i))
							return true;
					}
				}
				return false;
			}

			public ResultSetRow previous() {
				if (underlyingPosition > 0) {
					for (int i = underlyingPosition - 1; i >= 0; i--)
						if (inclusionMask.get(i)) {
							underlyingPosition = i;
							return unfiltered.getRow(underlyingPosition);
						}
				}
				throw new IllegalStateException();
			}

			public int nextIndex() {
				return inclusionMask.nextSetBit(underlyingPosition + 1);
			}

			public int previousIndex() {
				if (underlyingPosition > 0) {
					for (int i = underlyingPosition - 1; i >= 0; i--)
						if (inclusionMask.get(i))
							return i;
				}
				return -1;
			}

			/*
			 * Mutation is not supported
			 */
			public void remove() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			public void set(ResultSetRow o) {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			public void add(ResultSetRow o) {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			public boolean allowsReverse() {
				return true;
			}

			public ResultSet getResultSet() {
				return FilteringResultSet.this;
			}
		}

		public ResultSetMetaData getResultSetMetaData() {
			return resultSetMetaData;
		}

		public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
			this.resultSetMetaData = resultSetMetaData;
		}

		public int getStart() {
			throw new UnsupportedOperationException();
		}

		public boolean hasMore() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Bulk fetch results in the cache
		 * 
		 * @param bulkFetch
		 */
		public boolean setBulkFetch(boolean bulkFetch) {
			return unfiltered.setBulkFetch(bulkFetch);
		}

		/**
		 * Do we bulk fetch
		 * 
		 * @return - true if we do
		 */
		public boolean getBulkFetch() {
			return unfiltered.getBulkFetch();
		}

		/**
		 * Set the bulk fetch size
		 * 
		 * @param bulkFetchSize
		 */
		public int setBulkFetchSize(int bulkFetchSize) {
			return unfiltered.setBulkFetchSize(bulkFetchSize);
		}

		/**
		 * Get the bulk fetch size.
		 * 
		 * @return the fetch size
		 */
		public int getBulkFetchSize() {
			return unfiltered.getBulkFetchSize();
		}

		@Override
		public List<Pair<String, Integer>> getFieldFacet(String field) {
			return unfiltered.getFieldFacet(field);
		}

	}
}