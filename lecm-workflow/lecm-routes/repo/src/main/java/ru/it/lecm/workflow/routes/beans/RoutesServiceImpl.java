package ru.it.lecm.workflow.routes.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.approval.api.ApprovalAspectsModel;
import ru.it.lecm.workflow.approval.api.ApprovalService;
import ru.it.lecm.workflow.routes.api.RoutesModel;
import ru.it.lecm.workflow.routes.api.RoutesService;

/**
 *
 * @author vlevin
 */
public class RoutesServiceImpl extends BaseBean implements RoutesService {

	private final static Logger logger = LoggerFactory.getLogger(RoutesServiceImpl.class);
	public final static String ROUTES_FOLDER_ID = "ROUTES_FOLDER";
	private final String SEARCH_ROUTES_QUERY_FORMAT = "PARENT:\"%s\" AND +TYPE:\"%s\" AND (-ASPECT:\"sys:temporary\" OR -ASPECT:\"lecm-workflow:temp\")";

	private ApprovalService approvalService;
	private OrgstructureBean orgstructureService;
	private SearchService searchService;
	private CopyService copyService;

	public void setApprovalService(ApprovalService approvalService) {
		this.approvalService = approvalService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setCopyService(CopyService copyService) {
		this.copyService = copyService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ROUTES_FOLDER_ID);
	}

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "approvalService", approvalService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "searchService", searchService);
		PropertyCheck.mandatory(this, "copyService", copyService);
	}

	@Override
	public NodeRef getRoutesFolder() {
		return getServiceRootFolder();
	}

	@Override
	public NodeRef createNewTemporaryNode(NodeRef parentNode, QName nodeType) {
		NodeRef node = nodeService.createNode(parentNode, ContentModel.ASSOC_CONTAINS, getRandomQName(), nodeType).getChildRef();
		nodeService.addAspect(node, ContentModel.ASPECT_TEMPORARY, null);
		return node;
	}

	private QName getRandomQName() {
		return QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());
	}

	@Override
	public NodeRef getDocumentCurrentIteration(final NodeRef documentRef) {
		NodeRef documentApprovalFolder = approvalService.getDocumentApprovalFolder(documentRef);
		if (documentApprovalFolder == null) {
			return null;
		}
		Set<QName> types = new HashSet<>();
		types.add(RoutesModel.TYPE_ROUTE);
		List<ChildAssociationRef> children = nodeService.getChildAssocs(documentApprovalFolder, types);
		return children.isEmpty() ? null : children.get(0).getChildRef();
	}

	@Override
	public boolean archiveDocumentCurrentIteration(final NodeRef documentRef) {
		NodeRef documentCurrentIteration = getDocumentCurrentIteration(documentRef);
		if (documentCurrentIteration != null) {
			NodeRef documentApprovalHistoryFolder = approvalService.getDocumentApprovalHistoryFolder(documentRef);
			if (documentApprovalHistoryFolder == null) {
				documentApprovalHistoryFolder = approvalService.createDocumentApprovalHistoryFolder(documentRef);
			}
			int archiveSize = nodeService.getChildAssocs(documentApprovalHistoryFolder).size();
			NodeRef archivedIteration = nodeService.moveNode(documentCurrentIteration, documentApprovalHistoryFolder, ContentModel.ASSOC_CONTAINS, getRandomQName()).getChildRef();
			nodeService.setProperty(archivedIteration, ContentModel.PROP_TITLE, "Итерация " + (archiveSize + 1));

			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<NodeRef> getAllowedRoutesForCurrentUser() {
		return getAllowedRoutesForEmployee(orgstructureService.getCurrentEmployee());
	}

	@Override
	public List<NodeRef> getAllowedRoutesForEmployee(NodeRef employeeRef) {
		// TODO Добавить непосредственно фильтрацию
		List<NodeRef> nodes = new ArrayList<>();
		NodeRef parentContainer = getRoutesFolder();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(SEARCH_ROUTES_QUERY_FORMAT, parentContainer.toString(), RoutesModel.TYPE_ROUTE);
		sp.setQuery(searchQuery);
		ResultSet results = null;
		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				nodes.add(currentNodeRef);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return nodes;
	}

	@Override
	public NodeRef convertRouteToIteration(NodeRef documentNode, NodeRef routeNode) {
		NodeRef iteration;
		NodeRef approvalFolder = approvalService.getDocumentApprovalFolder(documentNode);
		if (approvalFolder == null) {
			approvalFolder = approvalService.createDocumentApprovalFolder(documentNode);
		} else {
			NodeRef documentCurrentIteration = getDocumentCurrentIteration(documentNode);
			if (documentCurrentIteration != null) {
				if (isIterationAvailableForArchiving(documentCurrentIteration)) {
					logger.info("Iteration for {} exists", documentNode);
					archiveDocumentCurrentIteration(documentNode);
				} else {
					throw new AlfrescoRuntimeException("Iteration {} is not available for archiving");
				}
			}
		}
		iteration = copyService.copy(routeNode, approvalFolder, ContentModel.ASSOC_CONTAINS, getRandomQName(), true);
		resolveIterationMacroses(iteration);

		return iteration;
	}

	private void resolveIterationMacroses(NodeRef iterationRef) {
		// TODO здесь мы будем резолвить макросы из маршрута в сотрудников.
		// пока просто удалим участников этапа с макросами
		List<NodeRef> stageItems = getAllStageItemsOfRoute(iterationRef);
		for (NodeRef stageItem : stageItems) {
			if (nodeService.getTargetAssocs(stageItem, RoutesModel.ASSOC_STAGE_ITEM_MACROS).size() > 0) {
				nodeService.addAspect(stageItem, ContentModel.ASPECT_TEMPORARY, null);
				nodeService.deleteNode(stageItem);
			}
		}
	}

	private List<NodeRef> getAllStageItemsOfRoute(NodeRef routeNode) {
		List<NodeRef> stageItems = new ArrayList<>();
		List<ChildAssociationRef> stageChildren;
		Set<QName> stageType = new HashSet<>();
		stageType.add(RoutesModel.TYPE_STAGE);
		Set<QName> stageItemType = new HashSet<>();
		stageItemType.add(RoutesModel.TYPE_STAGE_ITEM);
		stageChildren = nodeService.getChildAssocs(routeNode, stageType);

		for (ChildAssociationRef stageChild : stageChildren) {
			NodeRef stageNode = stageChild.getChildRef();
			List<ChildAssociationRef> stageItemChildren = nodeService.getChildAssocs(stageNode, stageItemType);

			for (ChildAssociationRef stageItemChild : stageItemChildren) {
				NodeRef stageItemNode = stageItemChild.getChildRef();
				stageItems.add(stageItemNode);
			}
		}

		return stageItems;
	}

	@Override
	public NodeRef createEmptyIteration(NodeRef documentNode) {
		NodeRef iterationNode;
		NodeRef approvalFolder = approvalService.getDocumentApprovalFolder(documentNode);
		if (approvalFolder == null) {
			approvalFolder = approvalService.createDocumentApprovalFolder(documentNode);
		} else {
			NodeRef documentCurrentIteration = getDocumentCurrentIteration(documentNode);
			if (documentCurrentIteration != null) {
				if (isIterationAvailableForArchiving(documentCurrentIteration)) {
					logger.info("Iteration for {} exists", documentNode);
					archiveDocumentCurrentIteration(documentNode);
				} else {
					throw new AlfrescoRuntimeException("Iteration {} is not available for archiving");
				}
			}
		}

		iterationNode = nodeService.createNode(approvalFolder, ContentModel.ASSOC_CONTAINS, getRandomQName(), RoutesModel.TYPE_ROUTE).getChildRef();

		return iterationNode;
	}

	private boolean isIterationAvailableForArchiving(NodeRef iterationNode) {
		boolean result;
		String approvalState = (String) nodeService.getProperty(iterationNode, ApprovalAspectsModel.PROP_APPROVAL_STATE);

		// можно архивировать, если не определено или не активно
		result = approvalState == null || !"ACTIVE".equals(approvalState);

		return result;
	}

}
