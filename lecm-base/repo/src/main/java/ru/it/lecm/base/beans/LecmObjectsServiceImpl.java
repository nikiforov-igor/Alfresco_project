package ru.it.lecm.base.beans;

import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.query.CannedQueryFactory;
import org.alfresco.query.CannedQueryResults;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.registry.NamedObjectRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.getchildren.GetLECMChildrenCannedQueryFactory;
import ru.it.lecm.base.beans.getchildren.GetLECMChildsCannedQuery;

/**
 * @author dbashmakov
 *         Date: 08.02.13
 *         Time: 15:01
 */
public class LecmObjectsServiceImpl extends BaseBean implements LecmObjectsService  {
	private static final Logger logger = LoggerFactory.getLogger(LecmObjectsServiceImpl.class);
	private static final String CANNED_QUERY_FILEFOLDER_LIST = "lecmGetChildrenCannedQueryFactory";
	public static final String LECM = "lecm";

	private NamedObjectRegistry<CannedQueryFactory<NodeRef>> cannedQueryRegistry;
	private DictionaryService dictionaryService;
	private NamespaceService namespaceService;

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setCannedQueryRegistry(NamedObjectRegistry<CannedQueryFactory<NodeRef>> cannedQueryRegistry) {
		this.cannedQueryRegistry = cannedQueryRegistry;
	}

	@Override
	public PagingResults<NodeRef> list(NodeRef contextNodeRef, QName childType, List<FilterProp> filterProps, List<Pair<QName, Boolean>> sortProps, PagingRequest pagingRequest) {
		ParameterCheck.mandatory("contextNodeRef", contextNodeRef);
		ParameterCheck.mandatory("pagingRequest", pagingRequest);

		Set<QName> searchTypeQNames = new HashSet<QName>();
		if (childType != null) {
			searchTypeQNames.add(childType);
		} else {
			// ищем все  наши объекты
			searchTypeQNames = buildLecmObjectTypes();
		}

		// execute query
		final CannedQueryResults<NodeRef> results = listImpl(contextNodeRef, searchTypeQNames, filterProps, sortProps, pagingRequest);
		return getPagingResults(pagingRequest, results);
	}

	private Set<QName> buildLecmObjectTypes() {
		Set<QName> lecmObjectQNames = new HashSet<QName>(50);
		Collection<QName> qnames = dictionaryService.getSubTypes(ContentModel.TYPE_CMOBJECT, true);
		for (QName qname : qnames) {
			if (qname.toPrefixString(namespaceService).startsWith(LECM)) {
				lecmObjectQNames.add(qname);
			}
		}
		return lecmObjectQNames;
	}

	private CannedQueryResults<NodeRef> listImpl(NodeRef contextNodeRef, Set<QName> searchTypeQNames, List<FilterProp> filterProps, List<Pair<QName, Boolean>> sortProps, PagingRequest pagingRequest) {
		Long start = (logger.isDebugEnabled() ? System.currentTimeMillis() : null);

		// get canned query
		GetLECMChildrenCannedQueryFactory getChildrenCannedQueryFactory = (GetLECMChildrenCannedQueryFactory) cannedQueryRegistry.getNamedObject(CANNED_QUERY_FILEFOLDER_LIST);

		GetLECMChildsCannedQuery cq = (GetLECMChildsCannedQuery) getChildrenCannedQueryFactory.getCannedQuery(contextNodeRef, null, searchTypeQNames, filterProps, sortProps, pagingRequest);

		// execute canned query
		CannedQueryResults<NodeRef> results = cq.execute();

		if (start != null) {
			int cnt = results.getPagedResultCount();
			int skipCount = pagingRequest.getSkipCount();
			int maxItems = pagingRequest.getMaxItems();
			boolean hasMoreItems = results.hasMoreItems();
			Pair<Integer, Integer> totalCount = (pagingRequest.getRequestTotalCountMax() > 0 ? results.getTotalResultCount() : null);
			int pageNum = (skipCount / maxItems) + 1;

			logger.debug("List: " + cnt + " items in " + (System.currentTimeMillis() - start) + " msecs [pageNum=" + pageNum + ",skip=" + skipCount + ",max=" + maxItems + ",hasMorePages=" + hasMoreItems + ",totalCount=" + totalCount + ",parentNodeRef=" + contextNodeRef + "]");
		}

		return results;
	}

	private PagingResults<NodeRef> getPagingResults(PagingRequest pagingRequest, final CannedQueryResults<NodeRef> results) {
		final List<NodeRef> nodeRefs;
		if (results.getPageCount() > 0) {
			nodeRefs = results.getPages().get(0);
		} else {
			nodeRefs = Collections.emptyList();
		}

		// set total count
		final Pair<Integer, Integer> totalCount;
		if (pagingRequest.getRequestTotalCountMax() > 0) {
			totalCount = results.getTotalResultCount();
		} else {
			totalCount = null;
		}

		return new PagingResults<NodeRef>() {
			@Override
			public String getQueryExecutionId() {
				return results.getQueryExecutionId();
			}

			@Override
			public List<NodeRef> getPage() {
				return nodeRefs;
			}

			@Override
			public boolean hasMoreItems() {
				return results.hasMoreItems();
			}

			@Override
			public Pair<Integer, Integer> getTotalResultCount() {
				return totalCount;
			}
		};
	}
}
