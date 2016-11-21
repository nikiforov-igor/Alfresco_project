package ru.it.lecm.base.beans.getchildren;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.query.CannedQueryParameters;
import org.alfresco.query.CannedQuerySortDetails;
import org.alfresco.repo.domain.node.*;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.domain.query.CannedQueryDAO;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.node.getchildren.FilterSortNodeEntity;
import org.alfresco.repo.node.getchildren.GetChildrenCannedQuery;
import org.alfresco.repo.node.getchildren.GetChildrenCannedQueryParams;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.security.permissions.PermissionCheckedValue;
import org.alfresco.repo.security.permissions.impl.acegi.MethodSecurityBean;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.ParameterCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.Collator;
import java.util.*;
import org.apache.commons.lang.StringUtils;

/**
 * Класс, реализующий запрос и фильтрацию для получение списка child элементов ноды
 * @author dbashmakov
 *         Date: 11.02.13
 *         Time: 10:58
 */
public class GetLECMChildsCannedQuery extends GetChildrenCannedQuery {
	private static final transient Logger logger = LoggerFactory.getLogger(GetLECMChildsCannedQuery.class);

	private static final String QUERY_NAMESPACE = "alfresco.node";
	private static final String QUERY_SELECT_GET_CHILDREN_WITH_PROPS = "select_GetChildrenCannedQueryWithProps";
	private static final String QUERY_SELECT_GET_CHILDREN_WITHOUT_PROPS = "select_GetChildrenCannedQueryWithoutProps";

	private NodeDAO nodeDAO;
	private QNameDAO qnameDAO;
	private CannedQueryDAO cannedQueryDAO;
	private TenantService tenantService;
	private NodePropertyHelper nodePropertyHelper;
	private boolean applyPostQueryPermissions = false;
    private Set<QName> childAspectQNames;
    private PermissionService permissionService;
    private DictionaryService dictionaryService;

	public GetLECMChildsCannedQuery(NodeDAO nodeDAO, QNameDAO qnameDAO, CannedQueryDAO cannedQueryDAO, NodePropertyHelper nodePropertyHelper, TenantService tenantService, NodeService nodeService, PermissionService permissionService, DictionaryService dictionaryService, MethodSecurityBean<NodeRef> methodSecurity, CannedQueryParameters params, Set<QName> childAspectQNames) {
		super(nodeDAO, qnameDAO, cannedQueryDAO, nodePropertyHelper, tenantService, nodeService, methodSecurity, params);
		this.nodeDAO = nodeDAO;
		this.qnameDAO = qnameDAO;
		this.cannedQueryDAO = cannedQueryDAO;
		this.tenantService = tenantService;
		this.nodePropertyHelper = nodePropertyHelper;
        this.childAspectQNames = childAspectQNames;
        this.permissionService = permissionService;
        this.dictionaryService = dictionaryService;

		if ((params.getSortDetails() != null) && (params.getSortDetails().getSortPairs().size() > 0)) {
			applyPostQueryPermissions = true;
		}

		GetChildrenCannedQueryParams paramBean = (GetChildrenCannedQueryParams) params.getParameterBean();
		if ((paramBean.getFilterProps() != null) && (paramBean.getFilterProps().size() > 0)) {
			applyPostQueryPermissions = true;
		}
	}

	private boolean includeFilter(Map<QName, Serializable> propVals, List<FilterProp> filterProps) {
		for (FilterProp filterProp : filterProps) {
			Serializable propVal = propVals.get(filterProp.getPropName());
			if (propVal != null) {
				if (filterProp instanceof FilterPropLECM) {
					Serializable filter = filterProp.getPropVal();
					switch ((FilterPropLECM.FilterTypeLECM) filterProp.getFilterType()) {
						case EQUALS:
							if (!propVal.equals(filter)) {
								return false;
							}
							break;
						case NOT_EQUALS:
							if (propVal.equals(filter)) {
								return false;
							}
							break;
						default:
					}
				}
			} else {
				if (!((FilterPropLECM)filterProp).getDefaultValue()){
                    return false;
                }
			}
		}
		return true;
	}

	@Override
	protected List<NodeRef> queryAndFilter(CannedQueryParameters parameters) {
		Long start = (logger.isDebugEnabled() ? System.currentTimeMillis() : null);

		// Get parameters
		GetChildrenCannedQueryParams paramBean = (GetChildrenCannedQueryParams) parameters.getParameterBean();

		// Get parent node
		NodeRef parentRef = paramBean.getParentRef();
		ParameterCheck.mandatory("nodeRef", parentRef);
		Pair<Long, NodeRef> nodePair = nodeDAO.getNodePair(parentRef);
		if (nodePair == null) {
			throw new InvalidNodeRefException("Parent node does not exist: " + parentRef, parentRef);
		}
		Long parentNodeId = nodePair.getFirst();

		// Set query params - note: currently using SortableChildEntity to hold (supplemental-) query params
		FilterSortNodeEntity params = new FilterSortNodeEntity();

		// Set parent node id
		params.setParentNodeId(parentNodeId);

		// Get filter details
		final Set<QName> childNodeTypeQNames = paramBean.getChildTypeQNames();
		final List<FilterProp> filterProps = paramBean.getFilterProps();
		String pattern = paramBean.getPattern();

		// Get sort details
		CannedQuerySortDetails sortDetails = parameters.getSortDetails();
		@SuppressWarnings({"unchecked", "rawtypes"})
		final List<Pair<QName, CannedQuerySortDetails.SortOrder>> sortPairs = (List) sortDetails.getSortPairs();

		// Set sort / filter params
		// Note - need to keep the sort properties in their requested order
		List<QName> sortFilterProps = new ArrayList<QName>(filterProps.size() + sortPairs.size());
		for (Pair<QName, CannedQuerySortDetails.SortOrder> sort : sortPairs) {
			QName sortQName = sort.getFirst();
			if (!sortFilterProps.contains(sortQName)) {
				sortFilterProps.add(sortQName);
			}
		}
		for (FilterProp filter : filterProps) {
			QName filterQName = filter.getPropName();
			if (!sortFilterProps.contains(filterQName)) {
				sortFilterProps.add(filterQName);
			}
		}

		int filterSortPropCnt = sortFilterProps.size();

		if (filterSortPropCnt > MAX_FILTER_SORT_PROPS) {
			throw new AlfrescoRuntimeException("GetChildren: exceeded maximum number filter/sort properties: (max=" + MAX_FILTER_SORT_PROPS + ", actual=" + filterSortPropCnt);
		}

		filterSortPropCnt = setFilterSortParams(sortFilterProps, params);

		// Set child node type qnames (additional filter - performed by DB query)
        Set<Long> childNodeTypeQNameIds = new HashSet<>();
		if (childNodeTypeQNames != null) {
			childNodeTypeQNameIds = qnameDAO.convertQNamesToIds(childNodeTypeQNames, false);
			if (childNodeTypeQNameIds.size() > 0) {
				params.setChildNodeTypeQNameIds(new ArrayList<Long>(childNodeTypeQNameIds));
			}
		}

        final boolean checkByIds = childNodeTypeQNameIds.size()  > 0;

		if (pattern != null) {
			// TODO, check that we should be tied to the content model in this way. Perhaps a configurable property
			// name against which compare the pattern?
			Pair<Long, QName> nameQName = qnameDAO.getQName(ContentModel.PROP_NAME);
			if (nameQName == null) {
				throw new AlfrescoRuntimeException("Unable to determine qname id of name property");
			}
			params.setNamePropertyQNameId(nameQName.getFirst());
			params.setPattern(pattern);
		}

		final List<NodeRef> result;

		if (filterSortPropCnt > 0) {
			// filtered and/or sorted - note: permissions will be applied post query
			final List<FilterSortNode> children = new ArrayList<FilterSortNode>(100);

			FilterSortChildQueryCallback callback = new FilterSortChildQueryCallback() {
				public boolean handle(FilterSortNode node) {
                    AccessStatus status = permissionService.hasPermission(node.getNodeRef(), "Read");
                    if (AccessStatus.ALLOWED == status) {
                        // filter, if needed
                        if ((checkByIds || checkType(childNodeTypeQNames, node.getNodeRef()))
                                && includeAllFilters(includeFilter(node.getPropVals(), filterProps), node.getNodeRef())) {
                            children.add(node);
                        }
                    }
                    // More results
					return true;
				}
			};

			FilterSortResultHandler resultHandler = new FilterSortResultHandler(callback);
			cannedQueryDAO.executeQuery(QUERY_NAMESPACE, QUERY_SELECT_GET_CHILDREN_WITH_PROPS, params, 0, Integer.MAX_VALUE, resultHandler);
			resultHandler.done();

			if (sortPairs.size() > 0) {
				// sort
				Collections.sort(children, new PropComparatorAsc(sortPairs));
			}

			result = new ArrayList<NodeRef>(children.size());
			for (FilterSortNode child : children) {
				result.add(tenantService.getBaseName(child.getNodeRef()));
			}
		} else {
			// unsorted (apart from any implicit order) - note: permissions are applied during result handling to allow early cutoff

			final int requestedCount = parameters.getResultsRequired();

			final List<NodeRef> rawResult = new ArrayList<NodeRef>(Math.min(1000, requestedCount));
			UnsortedChildQueryCallback callback = new UnsortedChildQueryCallback() {
				public boolean handle(NodeRef nodeRef) {
					rawResult.add(tenantService.getBaseName(nodeRef));

					// More results ?
					return (rawResult.size() < requestedCount);
				}
			};

			UnsortedResultHandler resultHandler = new UnsortedResultHandler(callback);
			cannedQueryDAO.executeQuery(QUERY_NAMESPACE, QUERY_SELECT_GET_CHILDREN_WITHOUT_PROPS, params, 0, Integer.MAX_VALUE, resultHandler);
			resultHandler.done();

			// permissions have been applied
			result = PermissionCheckedValue.PermissionCheckedValueMixin.create(rawResult);
		}

		if (start != null) {
			logger.debug("Base query " + (filterSortPropCnt > 0 ? "(sort=y, perms=n)" : "(sort=n, perms=y)") + ": " + result.size() + " in " + (System.currentTimeMillis() - start) + " msecs");
		}

		return result;
	}

    protected boolean includeAllFilters(boolean ret, NodeRef nodeRef) {
        try {
            boolean hasAspect = true;
            if (childAspectQNames != null) {
                if (childAspectQNames.size() > 1) {
                    Set<QName> nodeAspects = nodeService.getAspects(nodeRef);
                    hasAspect =  nodeAspects.removeAll(childAspectQNames);
                } else if (childAspectQNames.size() == 1) {
                    if (nodeService.hasAspect(nodeRef, childAspectQNames.iterator().next())) {
                        hasAspect = true;
                    }
                }
            }

            return ret && hasAspect;
        } catch (AccessDeniedException e) {
            // user may not have permission to determine the visibility of the node
            return ret;
        }
    }

    protected boolean checkType(Set<QName> mustBeTypes, NodeRef nodeRef) {
        try {
            boolean hasType = true;
            if (mustBeTypes != null) {
                hasType = false;
                QName nodeType = nodeService.getType(nodeRef);
                if (mustBeTypes.size() > 1) {
                    for (QName type : mustBeTypes) {
                        if (dictionaryService.isSubClass(nodeType, type)) {
                            hasType = true;
                            break;
                        }
                    }
                } else if (mustBeTypes.size() == 1) {
                    if (dictionaryService.isSubClass(nodeType, mustBeTypes.iterator().next())) {
                        hasType = true;
                    }
                }
            }

            return hasType;
        } catch (AccessDeniedException e) {
            // user may not have permission to determine the visibility of the node
            return false;
        }
    }

	private void preload(List<NodeRef> nodeRefs) {
		Long start = (logger.isTraceEnabled() ? System.currentTimeMillis() : null);

		nodeDAO.cacheNodes(nodeRefs);

		if (start != null) {
			logger.trace("Pre-load: " + nodeRefs.size() + " in " + (System.currentTimeMillis() - start) + " msecs");
		}
	}

	private int setFilterSortParams(List<QName> filterSortProps, FilterSortNodeEntity params) {
		int cnt = 0;

		for (QName filterSortProp : filterSortProps) {
			if (AuditablePropertiesEntity.getAuditablePropertyQNames().contains(filterSortProp)) {
				params.setAuditableProps(true);
				cnt++;
			} else if (filterSortProp.equals(SORT_QNAME_NODE_TYPE)) {
				params.setNodeType(true);
				cnt++;
			} else {
				Long sortQNameId = getQNameId(filterSortProp);
				if (sortQNameId != null) {
					switch (cnt) {
						case 0:
							params.setProp1qnameId(sortQNameId);
							break;
						case 1:
							params.setProp2qnameId(sortQNameId);
							break;
						case 2:
							params.setProp3qnameId(sortQNameId);
							break;
						default:
							// belts and braces
							throw new AlfrescoRuntimeException("GetChildren: unexpected - cannot set sort parameter: " + cnt);
					}
					cnt++;
				} else {
					logger.warn("Skipping filter/sort param - cannot find: " + filterSortProp);
				}
			}
		}

		return cnt;
	}

	private Long getQNameId(QName sortPropQName) {
		if (sortPropQName.equals(SORT_QNAME_CONTENT_SIZE) || sortPropQName.equals(SORT_QNAME_CONTENT_MIMETYPE)) {
			sortPropQName = ContentModel.PROP_CONTENT;
		}

		Pair<Long, QName> qnamePair = qnameDAO.getQName(sortPropQName);
		return (qnamePair == null ? null : qnamePair.getFirst());
	}

	private class FilterSortNode {
		private NodeRef nodeRef;
		private Map<QName, Serializable> propVals; // subset of nodes properties - used for filtering and/or sorting

		public FilterSortNode(NodeRef nodeRef, Map<QName, Serializable> propVals) {
			this.nodeRef = nodeRef;
			this.propVals = propVals;
		}

		@Override
		public String toString() {
			return "FilterSortNode [nodeRef=" + nodeRef + ", propVals=" + propVals + "]";
		}

		public NodeRef getNodeRef() {
			return nodeRef;
		}

		public Serializable getVal(QName prop) {
			return propVals.get(prop);
		}

		public Map<QName, Serializable> getPropVals() {
			return propVals;
		}
	}

	private interface FilterSortChildQueryCallback {
		boolean handle(FilterSortNode node);
	}

	private interface UnsortedChildQueryCallback {
		boolean handle(NodeRef nodeRef);
	}

	private class FilterSortResultHandler implements CannedQueryDAO.ResultHandler<FilterSortNodeEntity> {
		private final FilterSortChildQueryCallback resultsCallback;
		private boolean more = true;

		private FilterSortResultHandler(GetLECMChildsCannedQuery.FilterSortChildQueryCallback resultsCallback) {
			this.resultsCallback = resultsCallback;
		}

		public boolean handleResult(FilterSortNodeEntity result) {
			// Do nothing if no further results are required
			if (!more) {
				return false;
			}

			Node node = result.getNode();
			NodeRef nodeRef = node.getNodeRef();

			Map<NodePropertyKey, NodePropertyValue> propertyValues = new HashMap<NodePropertyKey, NodePropertyValue>(3);

			NodePropertyEntity prop1 = result.getProp1();
			if (prop1 != null) {
				propertyValues.put(prop1.getKey(), prop1.getValue());
			}

			NodePropertyEntity prop2 = result.getProp2();
			if (prop2 != null) {
				propertyValues.put(prop2.getKey(), prop2.getValue());
			}

			NodePropertyEntity prop3 = result.getProp3();
			if (prop3 != null) {
				propertyValues.put(prop3.getKey(), prop3.getValue());
			}

			Map<QName, Serializable> propVals = nodePropertyHelper.convertToPublicProperties(propertyValues);

			// Add referenceable / spoofed properties (including spoofed name if null)
			ReferenceablePropertiesEntity.addReferenceableProperties(node, propVals);

			// special cases

			// MLText (eg. cm:title, cm:description, ...)
			for (Map.Entry<QName, Serializable> entry : propVals.entrySet()) {
				if (entry.getValue() instanceof MLText) {
					propVals.put(entry.getKey(), DefaultTypeConverter.INSTANCE.convert(String.class, (MLText) entry.getValue()));
				}
			}

			// ContentData (eg. cm:content.size, cm:content.mimetype)
			ContentData contentData = (ContentData) propVals.get(ContentModel.PROP_CONTENT);
			if (contentData != null) {
				propVals.put(SORT_QNAME_CONTENT_SIZE, contentData.getSize());
				propVals.put(SORT_QNAME_CONTENT_MIMETYPE, contentData.getMimetype());
			}

			// Auditable props (eg. cm:creator, cm:created, cm:modifier, cm:modified, ...)
			AuditablePropertiesEntity auditableProps = node.getAuditableProperties();
			if (auditableProps != null) {
				for (Map.Entry<QName, Serializable> entry : auditableProps.getAuditableProperties().entrySet()) {
					propVals.put(entry.getKey(), entry.getValue());
				}
			}

			// Node type
			Long nodeTypeQNameId = node.getTypeQNameId();
			if (nodeTypeQNameId != null) {
				Pair<Long, QName> pair = qnameDAO.getQName(nodeTypeQNameId);
				if (pair != null) {
					propVals.put(SORT_QNAME_NODE_TYPE, pair.getSecond());
				}
			}

			// Call back
			boolean more = resultsCallback.handle(new FilterSortNode(nodeRef, propVals));
			if (!more) {
				this.more = false;
			}

			return more;
		}

		public void done() {
		}
	}

	private class UnsortedResultHandler implements CannedQueryDAO.ResultHandler<NodeEntity> {
		private final GetLECMChildsCannedQuery.UnsortedChildQueryCallback resultsCallback;

		private boolean more = true;

		private static final int BATCH_SIZE = 256 * 4;
		private final List<NodeRef> nodeRefs;

		private UnsortedResultHandler(UnsortedChildQueryCallback resultsCallback) {
			this.resultsCallback = resultsCallback;

			nodeRefs = new LinkedList<NodeRef>();
		}

		public boolean handleResult(NodeEntity result) {
			// Do nothing if no further results are required
			if (!more) {
				return false;
			}

			NodeRef nodeRef = result.getNodeRef();

			if (nodeRefs.size() >= BATCH_SIZE) {
				// batch
				preloadAndApplyPermissions();
			}

			nodeRefs.add(nodeRef);

			return more;
		}

		private void preloadAndApplyPermissions() {
			preload(nodeRefs);

			// TODO track total time for incremental permission checks ... and cutoff (eg. based on some config)
			List<NodeRef> results = applyPostQueryPermissions(nodeRefs, nodeRefs.size());

			for (NodeRef nodeRef : results) {
				// Call back
				boolean more = resultsCallback.handle(nodeRef);
				if (!more) {
					this.more = false;
					break;
				}
			}

			nodeRefs.clear();
		}

		public void done() {
			if (nodeRefs.size() >= 0) {
				// finish batch
				preloadAndApplyPermissions();
			}
		}
	}

	private class PropComparatorAsc implements Comparator<FilterSortNode> {
		private List<Pair<QName, CannedQuerySortDetails.SortOrder>> sortProps;
		private Collator collator;

		public PropComparatorAsc(List<Pair<QName, CannedQuerySortDetails.SortOrder>> sortProps) {
			this.sortProps = sortProps;
			this.collator = Collator.getInstance(); // note: currently default locale
		}

		public int compare(FilterSortNode n1, FilterSortNode n2) {
			return compareImpl(n1, n2, sortProps);
		}

		private int compareImpl(FilterSortNode node1In, FilterSortNode node2In, List<Pair<QName, CannedQuerySortDetails.SortOrder>> sortProps) {
			Object pv1 = null;
			Object pv2 = null;

			QName sortPropQName = (QName) sortProps.get(0).getFirst();
			boolean sortAscending = (sortProps.get(0).getSecond() == CannedQuerySortDetails.SortOrder.ASCENDING);

			FilterSortNode node1 = node1In;
			FilterSortNode node2 = node2In;

			if (sortAscending == false) {
				node1 = node2In;
				node2 = node1In;
			}

			int result = 0;

			pv1 = node1.getVal(sortPropQName);
			pv2 = node2.getVal(sortPropQName);

			if (pv1 == null) {
				return (pv2 == null ? 0 : -1);
			} else if (pv2 == null) {
				return 1;
			}

			if (pv1 instanceof String) {
				result = collator.compare((String) pv1, (String) pv2); // TODO use collation keys (re: performance)
			} else if (pv1 instanceof Date) {
				result = (((Date) pv1).compareTo((Date) pv2));
			} else if (pv1 instanceof Long) {
				result = (((Long) pv1).compareTo((Long) pv2));
			} else if (pv1 instanceof Integer) {
				result = (((Integer) pv1).compareTo((Integer) pv2));
			} else if (pv1 instanceof QName) {
				result = (((QName) pv1).compareTo((QName) pv2));
			} else if (pv1 instanceof Boolean) {
                result = (((Boolean) pv1).compareTo((Boolean) pv2));
			} else if (pv1 instanceof Float) {
                result = (((Float) pv1).compareTo((Float) pv2));
			} else if (pv1 instanceof Double) {
                result = (((Double) pv1).compareTo((Double) pv2));
			} else if (pv1 instanceof List) {
				String str1 = StringUtils.join((ArrayList) pv1, ",");
				String str2 = StringUtils.join((ArrayList) pv2, ",");

				result = collator.compare(str1, str2); // TODO use collation keys (re: performance)
            } else {
				// TODO other comparisons
				throw new RuntimeException("Unsupported sort type: " + pv1.getClass().getName());
			}

			if ((result == 0) && (sortProps.size() > 1)) {
				return compareImpl(node1In, node2In, sortProps.subList(1, sortProps.size()));
			}

			return result;
		}
	}
}
