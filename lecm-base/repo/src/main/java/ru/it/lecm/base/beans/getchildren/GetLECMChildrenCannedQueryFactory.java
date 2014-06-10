package ru.it.lecm.base.beans.getchildren;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.alfresco.model.ContentModel;

import org.alfresco.query.*;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.repo.domain.locale.LocaleDAO;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.node.NodePropertyHelper;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.domain.query.CannedQueryDAO;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.node.getchildren.GetChildrenCannedQueryParams;
import org.alfresco.repo.security.permissions.impl.acegi.MethodSecurityBean;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;

/**
 * Класс-фабрика, возвращающая поисковый запрос для поиска объектов
 * @author dbashmakov
 *         Date: 11.02.13
 *         Time: 12:10
 */
public class GetLECMChildrenCannedQueryFactory extends AbstractCannedQueryFactory<NodeRef> {
	private DictionaryService dictionaryService;
	private NodeDAO nodeDAO;
	private QNameDAO qnameDAO;
	private LocaleDAO localeDAO;
	private ContentDataDAO contentDataDAO;
	private CannedQueryDAO cannedQueryDAO;
	private TenantService tenantService;
	private NodeService nodeService;

	private MethodSecurityBean<NodeRef> methodSecurity;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}

	public void setQnameDAO(QNameDAO qnameDAO) {
		this.qnameDAO = qnameDAO;
	}

	public void setLocaleDAO(LocaleDAO localeDAO) {
		this.localeDAO = localeDAO;
	}

	public void setContentDataDAO(ContentDataDAO contentDataDAO) {
		this.contentDataDAO = contentDataDAO;
	}

	public void setCannedQueryDAO(CannedQueryDAO cannedQueryDAO) {
		this.cannedQueryDAO = cannedQueryDAO;
	}

	public void setTenantService(TenantService tenantService) {
		this.tenantService = tenantService;
	}

	public void setMethodSecurity(MethodSecurityBean<NodeRef> methodSecurity) {
		this.methodSecurity = methodSecurity;
	}

	@Override
	public CannedQuery<NodeRef> getCannedQuery(CannedQueryParameters parameters) {
		NodePropertyHelper nodePropertyHelper = new NodePropertyHelper(dictionaryService, qnameDAO, localeDAO, contentDataDAO);
		return new GetLECMChildsCannedQuery(nodeDAO, qnameDAO, cannedQueryDAO, nodePropertyHelper, tenantService, nodeService, methodSecurity, parameters);
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Retrieve an optionally filtered/sorted instance of a {@link CannedQuery} based on parameters including request for a total count (up to a given max)
	 * <p/>
	 * Note: if both filtering and sorting is required then the combined total of unique QName properties should be the 0 to 3.
	 *
	 * @param parentRef       parent node ref
	 * @param pattern         the pattern to use to filter children (wildcard character is '*')
	 * @param childTypeQNames type qnames of children nodes (pre-filter)
	 * @param filterProps     filter properties
	 * @param sortProps       sort property pairs (QName and Boolean - true if ascending)
	 * @param pagingRequest   skipCount, maxItems - optionally queryExecutionId and requestTotalCountMax
	 * @return an implementation that will execute the query
	 */
	public CannedQuery<NodeRef> getCannedQuery(NodeRef parentRef, String pattern, Set<QName> childTypeQNames, List<FilterProp> filterProps, List<Pair<QName, Boolean>> sortProps, PagingRequest pagingRequest) {
		ParameterCheck.mandatory("parentRef", parentRef);
		ParameterCheck.mandatory("pagingRequest", pagingRequest);

		int requestTotalCountMax = pagingRequest.getRequestTotalCountMax();

		// specific query params - context (parent) and inclusive filters (child types, property values)
		//GetChildrenCannedQueryParams paramBean = new GetChildrenCannedQueryParams(tenantService.getName(parentRef), childTypeQNames, filterProps, pattern);
		Set<QName> assocTypeQNames = new HashSet<QName>();
		Set<QName> inclusiveAspects = new HashSet<QName>();
		//TODO: Снова сотни заглушек
		assocTypeQNames.add(ContentModel.ASSOC_CONTAINS);
		GetChildrenCannedQueryParams paramBean = new GetChildrenCannedQueryParams(tenantService.getName(parentRef),
				assocTypeQNames, childTypeQNames, inclusiveAspects, inclusiveAspects, filterProps, pattern);

		// page details
		CannedQueryPageDetails cqpd = new CannedQueryPageDetails(pagingRequest.getSkipCount(), pagingRequest.getMaxItems(), CannedQueryPageDetails.DEFAULT_PAGE_NUMBER, CannedQueryPageDetails.DEFAULT_PAGE_COUNT);

		// sort details
		CannedQuerySortDetails cqsd = null;
		if (sortProps != null) {
			List<Pair<? extends Object, CannedQuerySortDetails.SortOrder>> sortPairs = new ArrayList<Pair<? extends Object, CannedQuerySortDetails.SortOrder>>(sortProps.size());
			for (Pair<QName, Boolean> sortProp : sortProps) {
				sortPairs.add(new Pair<QName, CannedQuerySortDetails.SortOrder>(sortProp.getFirst(), (sortProp.getSecond() ? CannedQuerySortDetails.SortOrder.ASCENDING : CannedQuerySortDetails.SortOrder.DESCENDING)));
			}

			cqsd = new CannedQuerySortDetails(sortPairs);
		}

		// create query params holder
		CannedQueryParameters params = new CannedQueryParameters(paramBean, cqpd, cqsd, requestTotalCountMax, pagingRequest.getQueryExecutionId());

		// return canned query instance
		return getCannedQuery(params);
	}

	/**
	 * Retrieve an unsorted instance of a {@link CannedQuery} based on parameters including request for a total count (up to a given max)
	 *
	 * @param parentRef       parent node ref
	 * @param childTypeQNames type qnames of children nodes
	 * @param pagingRequest   skipCount, maxItems - optionally queryExecutionId and requestTotalCountMax
	 * @return an implementation that will execute the query
	 */
	public CannedQuery<NodeRef> getCannedQuery(NodeRef parentRef, String pattern, Set<QName> childTypeQNames, PagingRequest pagingRequest) {
		return getCannedQuery(parentRef, pattern, childTypeQNames, null, null, pagingRequest);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
		PropertyCheck.mandatory(this, "tenantService", tenantService);
		PropertyCheck.mandatory(this, "nodeDAO", nodeDAO);
		PropertyCheck.mandatory(this, "qnameDAO", qnameDAO);
		PropertyCheck.mandatory(this, "localeDAO", localeDAO);
		PropertyCheck.mandatory(this, "contentDataDAO", contentDataDAO);
		PropertyCheck.mandatory(this, "cannedQueryDAO", cannedQueryDAO);
		PropertyCheck.mandatory(this, "methodSecurityInterceptor", methodSecurity);
	}
}

