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
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
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
	private DocumentService documentService;

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

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
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
		PropertyCheck.mandatory(this, "documentService", documentService);
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
		logger.debug("Archiving iteration for document {}", documentRef);

		boolean result;
		NodeRef documentCurrentIteration = getDocumentCurrentIteration(documentRef);
		if (documentCurrentIteration != null) {
			NodeRef documentApprovalHistoryFolder = approvalService.getDocumentApprovalHistoryFolder(documentRef);
			if (documentApprovalHistoryFolder == null) {
				documentApprovalHistoryFolder = approvalService.createDocumentApprovalHistoryFolder(documentRef);
			}
			int archiveSize = nodeService.getChildAssocs(documentApprovalHistoryFolder).size();
			NodeRef archivedIteration = nodeService.moveNode(documentCurrentIteration, documentApprovalHistoryFolder, ContentModel.ASSOC_CONTAINS, getRandomQName()).getChildRef();
			nodeService.setProperty(archivedIteration, ContentModel.PROP_TITLE, "Итерация " + (archiveSize + 1));

			result = true;
		} else {
			result = false;
		}

		return result;
	}

	@Override
	public List<NodeRef> getAllowedRoutesForCurrentUser(NodeRef documentRef) {
		return getAllowedRoutesForEmployee(orgstructureService.getCurrentEmployee(), documentRef);
	}

	@Override
	public List<NodeRef> getAllowedRoutesForEmployee(NodeRef employeeRef, NodeRef documentRef) {
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
				if (isRouteAllowedForEmployee(currentNodeRef, employeeRef, documentRef)) {
					nodes.add(currentNodeRef);
				}
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return nodes;
	}

	private boolean isRouteAllowedForEmployee(NodeRef routeRef, NodeRef employeeRef, NodeRef documentRef) {
		boolean result = false;
		List<NodeRef> routeUnits = findNodesByAssociationRef(routeRef, RoutesModel.ASSOC_ROUTE_ORGANIZATION_UNIT, OrgstructureBean.TYPE_ORGANIZATION_UNIT, ASSOCIATION_TYPE.TARGET);
		List<NodeRef> employeeUnits = orgstructureService.getEmployeeUnits(employeeRef, false);
		boolean unitMatched = routeUnits.isEmpty();

		for (NodeRef routeUnit : routeUnits) {
			if (employeeUnits.contains(routeUnit)) {
				unitMatched = true;
				break;
			}
		}

		if (unitMatched) {
			String routeExpression = (String) nodeService.getProperty(routeRef, RoutesModel.PROP_ROUTE_AVAILABILITY_CONDITION);
			if (routeExpression != null && !routeExpression.isEmpty()) {
				boolean expressionResult = documentService.execExpression(documentRef, routeExpression);
				result = expressionResult;
			} else {
				result = true;
			}
		}

		return result;
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
				if (isIterationAvailableForDeleting(documentCurrentIteration)) {
					deleteDocumentCurrentIteration(documentNode);
				} else if (isIterationAvailableForArchiving(documentCurrentIteration)) {
					archiveDocumentCurrentIteration(documentNode);
				} else {
					throw new AlfrescoRuntimeException("Iteration {} is not available for archiving");
				}
			}
		}
		iteration = copyService.copy(routeNode, approvalFolder, ContentModel.ASSOC_CONTAINS, getRandomQName(), false);
		List<NodeRef> routeStages = getAllStagesOfRoute(routeNode);
		for (NodeRef routeStage : routeStages) {
			if (!nodeService.hasAspect(routeStage, ContentModel.ASPECT_TEMPORARY)) {
				copyService.copy(routeStage, iteration, ContentModel.ASSOC_CONTAINS, getRandomQName(), true);
			}
		}
		resolveIterationMacroses(iteration);

		return iteration;
	}

	private void resolveIterationMacroses(NodeRef iterationRef) {
		List<NodeRef> stageItems = getAllStageItemsOfRoute(iterationRef);
		for (NodeRef stageItem : stageItems) {
			if (nodeService.getTargetAssocs(stageItem, RoutesModel.ASSOC_STAGE_ITEM_MACROS).size() > 0) {
				resolveStageItemMacros(stageItem);
			}
		}
	}

	@Override
	public void resolveStageItemMacros(NodeRef stageItemNode) {
		// TODO здесь мы будем резолвить макросы из маршрута в сотрудников.
		// пока просто подставим текущего пользователя
		nodeService.createAssociation(stageItemNode, orgstructureService.getCurrentEmployee(), RoutesModel.ASSOC_STAGE_ITEM_EMPLOYEE);
	}

	private List<NodeRef> getAllStagesOfRoute(NodeRef routeNode) {
		Set<QName> stageType = new HashSet<>();
		stageType.add(RoutesModel.TYPE_STAGE);

		List<NodeRef> stages = new ArrayList<>();
		List<ChildAssociationRef> stageChildren = nodeService.getChildAssocs(routeNode, stageType);

		for (ChildAssociationRef stageChild : stageChildren) {
			NodeRef stageNode = stageChild.getChildRef();
			stages.add(stageNode);
		}

		return stages;
	}

	private List<NodeRef> getAllStageItemsOfRoute(NodeRef routeNode) {
		List<NodeRef> stageItems = new ArrayList<>();
		List<NodeRef> stages = getAllStagesOfRoute(routeNode);

		for (NodeRef stageNode : stages) {
			stageItems.addAll(getAllStageItemsOfStage(stageNode));
		}

		return stageItems;
	}

	private List<NodeRef> getAllStageItemsOfStage(NodeRef stageNode) {
		Set<QName> stageItemType = new HashSet<>();
		stageItemType.add(RoutesModel.TYPE_STAGE_ITEM);

		List<NodeRef> stageItems = new ArrayList<>();

		List<ChildAssociationRef> stageItemChildren = nodeService.getChildAssocs(stageNode, stageItemType);
		for (ChildAssociationRef stageItemChild : stageItemChildren) {
			NodeRef stageItemNode = stageItemChild.getChildRef();
			stageItems.add(stageItemNode);
		}

		return stageItems;
	}

	@Override
	public NodeRef createEmptyIteration(NodeRef documentNode) {
		NodeRef iterationNode;
		NodeRef approvalFolder = approvalService.getDocumentApprovalFolder(documentNode);

		PropertyMap props = new PropertyMap();
		props.put(RoutesModel.PROP_ROUTE_EDITABLE, true);

		if (approvalFolder == null) {
			approvalFolder = approvalService.createDocumentApprovalFolder(documentNode);
		} else {
			NodeRef documentCurrentIteration = getDocumentCurrentIteration(documentNode);
			if (documentCurrentIteration != null) {
				if (isIterationAvailableForDeleting(documentCurrentIteration)) {
					deleteDocumentCurrentIteration(documentNode);
				} else if (isIterationAvailableForArchiving(documentCurrentIteration)) {
					archiveDocumentCurrentIteration(documentNode);
				} else {
					throw new AlfrescoRuntimeException("Iteration {} is not available for archiving");
				}
			}
		}

		iterationNode = nodeService.createNode(approvalFolder, ContentModel.ASSOC_CONTAINS, getRandomQName(), RoutesModel.TYPE_ROUTE, props).getChildRef();

		return iterationNode;
	}

	private boolean isIterationAvailableForArchiving(NodeRef iterationNode) {
		String approvalState = (String) nodeService.getProperty(iterationNode, ApprovalAspectsModel.PROP_APPROVAL_STATE);

		// можно архивировать, если не определено или не активно
		return approvalState == null || !"ACTIVE".equalsIgnoreCase(approvalState);
	}

	private boolean isIterationAvailableForDeleting(NodeRef iterationNode) {
		String approvalState = (String) nodeService.getProperty(iterationNode, ApprovalAspectsModel.PROP_APPROVAL_STATE);

		// можно удалять, если не определено или новое
		return approvalState == null || "NEW".equalsIgnoreCase(approvalState);
	}

	@Override
	public void deleteDocumentCurrentIteration(final NodeRef documentRef) {
		logger.debug("Deleting iteration for document {}", documentRef);

		NodeRef documentCurrentIteration = getDocumentCurrentIteration(documentRef);
		if (documentCurrentIteration != null) {
			permDeleteNode(documentCurrentIteration);
		}
	}

	private void permDeleteNode(NodeRef node) {
		nodeService.addAspect(node, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(node);
	}

	@Override
	public NodeRef getSourceRouteForIteration(NodeRef iterationNode) {
		NodeRef result = null;
		// итерацию откуда-то скопировали?
		if (nodeService.hasAspect(iterationNode, ContentModel.ASPECT_COPIEDFROM)) {
			NodeRef sourceRoute = findNodeByAssociationRef(iterationNode, ContentModel.ASSOC_ORIGINAL, RoutesModel.TYPE_ROUTE, ASSOCIATION_TYPE.TARGET);
			if (sourceRoute != null && nodeService.exists(sourceRoute)) {
				boolean routeEditable = (Boolean) nodeService.getProperty(sourceRoute, RoutesModel.PROP_ROUTE_EDITABLE);
				// если маршрут можно изменять, то проверяем дальше. иначе считаем его исходным.
				if (routeEditable) {
					// так как в итерацию могут быть добавлены новые этапы и согласующие, надо идти со стороны исходного маршрута
					boolean sourceRouteFound = true;
					List<NodeRef> sourceStages = getAllStagesOfRoute(sourceRoute);
					// бежим по этапам маршрута
					for (NodeRef sourceStage : sourceStages) {
						NodeRef iterationStage = null;
						List<NodeRef> copiedStages = findNodesByAssociationRef(sourceStage, ContentModel.ASSOC_ORIGINAL, RoutesModel.TYPE_STAGE, ASSOCIATION_TYPE.SOURCE);
						// бежим по всем копиям этапа маршрута (они будут находиться в итерациях)
						for (NodeRef copiedStage : copiedStages) {
							// проверяем, является ли родитель копии этапа нашей итерацией
							NodeRef copiedStageIteration = nodeService.getPrimaryParent(copiedStage).getParentRef();
							if (iterationNode.equals(copiedStageIteration)) {
								// если да, то запомнили этап итерации, он пригодится позже
								iterationStage = copiedStage;
								break;
							}
						}

						// запомненный этап итерации присутствует - пока ничего не разъехалось
						if (iterationStage != null) {
							List<NodeRef> sourceStageItems = getAllStageItemsOfStage(sourceStage);
							// бежим по всем согласующим этапа маршрута
							for (NodeRef sourceStageItem : sourceStageItems) {
								boolean stageItemFound = false;
								// все копии каждого согласующего этапа маршрута (согласующие итерации)
								List<NodeRef> copiedStageItems = findNodesByAssociationRef(sourceStageItem, ContentModel.ASSOC_ORIGINAL, RoutesModel.TYPE_STAGE_ITEM, ASSOCIATION_TYPE.SOURCE);
								for (NodeRef copiedStageItem : copiedStageItems) {
									// сопадает ли родитель согласующего итерации с запомненным ранее этапом итерации
									NodeRef copiedStageItemStage = nodeService.getPrimaryParent(copiedStageItem).getParentRef();
									if (iterationStage.equals(copiedStageItemStage)) {
										stageItemFound = true;
										break;
									}
								}

								// не нашли подходящего согласующего итерации. все плохо
								if (!stageItemFound) {
									sourceRouteFound = false;
									break;
								}
							}

						} else {
							// не нашли подходящего этапа итерации. все плохо
							sourceRouteFound = false;
							break;
						}
					}

					// нашли маршрут. все хорошо
					if (sourceRouteFound) {
						result = sourceRoute;
					}
				} else {
					result = sourceRoute;
				}
			}
		}

		return result;
	}

}
