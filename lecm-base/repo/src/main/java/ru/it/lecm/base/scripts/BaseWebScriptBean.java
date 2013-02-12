package ru.it.lecm.base.scripts;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptPagingNodes;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.Pair;
import org.mozilla.javascript.Context;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.getchildren.FilterPropLECM;
import ru.it.lecm.base.beans.LecmObjectsService;

/**
 * @author dbashmakov
 *         Date: 08.02.13
 *         Time: 12:53
 */
public class BaseWebScriptBean extends BaseScopableProcessorExtension {
	private NamespaceService namespaceService;
	private ServiceRegistry serviceRegistry;
	private LecmObjectsService lecmObjectsService;

	final int REQUEST_MAX = 1000;

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setLecmObjectsService(LecmObjectsService lecmObjectsService) {
		this.lecmObjectsService = lecmObjectsService;
	}

	public ScriptPagingNodes getChilds(ScriptNode node, String childQNameType, int maxItems, int skipCount, String sortProp, Boolean sortAsc, Boolean onlyActive) {
		return getChilds(node.getNodeRef(), childQNameType, maxItems, skipCount, sortProp, sortAsc, onlyActive);
	}

	public ScriptPagingNodes getChilds(final NodeRef nodeRef, final String childQNameType, final int maxItems, final int skipCount, final String sortProp, final Boolean sortAsc, final Boolean onlyActive) {
		Object[] results;

		QName childType = null;
		if (childQNameType != null) {
			childType = QName.createQName(childQNameType, namespaceService);
		}

		List<Pair<QName, Boolean>> sortProps = null; // note: null sortProps => get all in default sort order
		if (sortProp != null) {
			sortProps = new ArrayList<Pair<QName, Boolean>>(1);
			sortProps.add(new Pair<QName, Boolean>(QName.createQName(sortProp, namespaceService), sortAsc));
		}

		List<FilterProp> filter = new ArrayList<FilterProp>();
		if (onlyActive) {
			filter.add(new FilterPropLECM(BaseBean.IS_ACTIVE, Boolean.TRUE, FilterPropLECM.FilterTypeLECM.EQUALS, Boolean.TRUE));
		}

		PagingRequest pageRequest = new PagingRequest(skipCount, maxItems, null);
		pageRequest.setRequestTotalCountMax(REQUEST_MAX);

		PagingResults<NodeRef> pageOfNodeInfos = null;
		FileFilterMode.setClient(FileFilterMode.Client.script);
		try {
			pageOfNodeInfos = lecmObjectsService.list(nodeRef, childType, filter, sortProps, pageRequest);
		} finally {
			FileFilterMode.clearClient();
		}

		List<NodeRef> nodeInfos = pageOfNodeInfos.getPage();
		int size = nodeInfos.size();
		results = new Object[size];
		for (int i = 0; i < size; i++) {
			NodeRef ref = nodeInfos.get(i);
			results[i] = new ScriptNode(ref, serviceRegistry, getScope());
		}

		int totalResultCountLower = -1;
		int totalResultCountUpper = -1;

		Pair<Integer, Integer> totalResultCount = pageOfNodeInfos.getTotalResultCount();
		if (totalResultCount != null) {
			totalResultCountLower = (totalResultCount.getFirst() != null ? totalResultCount.getFirst() : -1);
			totalResultCountUpper = (totalResultCount.getSecond() != null ? totalResultCount.getSecond() : -1);
		}

		return new ScriptPagingNodes(Context.getCurrentContext().newArray(getScope(), results), pageOfNodeInfos.hasMoreItems(), totalResultCountLower, totalResultCountUpper);
	}
}
