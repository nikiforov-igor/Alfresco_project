package ru.it.lecm.workflow.routes.beans;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.approval.api.ApprovalAspectsModel;
import ru.it.lecm.workflow.approval.api.ApprovalService;
import ru.it.lecm.workflow.routes.api.ConvertRouteToIterationResult;
import ru.it.lecm.workflow.routes.api.RoutesMacrosModel;
import ru.it.lecm.workflow.routes.api.RoutesModel;
import ru.it.lecm.workflow.routes.api.RoutesService;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author vlevin
 */
public class RoutesServiceImpl extends BaseBean implements RoutesService {

	private final static Logger logger = LoggerFactory.getLogger(RoutesServiceImpl.class);
	public final static String ROUTES_FOLDER_ID = "ROUTES_FOLDER";
	private final String SEARCH_ROUTES_QUERY_FORMAT = "(+TYPE:\"%s\") AND (-ASPECT:\"sys:temporary\" AND -ASPECT:\"lecm-workflow:temp\") AND (+PARENT:\"%s\") AND +ISNOTNULL:\"sys:node-dbid\"";
	private final static String CUSTOM_ITERATION_TITLE = "Индивидуальный маршрут";

	QName TYPE_CONTRACTOR = QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "contractor-type");

	private ApprovalService approvalService;
	private OrgstructureBean orgstructureService;
	private SearchService searchService;
	private CopyService copyService;
	private DocumentService documentService;
	private ScriptService scriptService;

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

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
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
		PropertyCheck.mandatory(this, "scriptService", scriptService);
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
			NodeRef sourceRoute = getSourceRouteForIteration(documentCurrentIteration);

			NodeRef archivedIteration = nodeService.moveNode(documentCurrentIteration, documentApprovalHistoryFolder, ContentModel.ASSOC_CONTAINS, getRandomQName()).getChildRef();
			String archivedIterationTitle;
			if (sourceRoute != null) {
				archivedIterationTitle = (String) nodeService.getProperty(sourceRoute, ContentModel.PROP_TITLE);
			} else {
				archivedIterationTitle = CUSTOM_ITERATION_TITLE;
			}

			nodeService.setProperty(archivedIteration, ContentModel.PROP_TITLE, archivedIterationTitle);

			List<NodeRef> stagesOfArchivedIteration = getAllStagesOfRoute(archivedIteration);
			for (NodeRef stage : stagesOfArchivedIteration) {
				boolean stageIsTemp = nodeService.hasAspect(stage, ContentModel.ASPECT_TEMPORARY);
				if (stageIsTemp) {
					nodeService.deleteNode(stage);
				}
			}

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
		String searchQuery = String.format(SEARCH_ROUTES_QUERY_FORMAT, RoutesModel.TYPE_ROUTE, parentContainer.toString());
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
		List<NodeRef> routeOrganizations = findNodesByAssociationRef(routeRef, RoutesModel.ASSOC_ROUTE_ORGANIZATION, TYPE_CONTRACTOR, ASSOCIATION_TYPE.TARGET);

		NodeRef routeOrganization = !routeOrganizations.isEmpty() ? routeOrganizations.get(0) : null;
		boolean isAllowed = routeOrganization == null;

		if (routeOrganization != null) {
			NodeRef employeeOrganization = orgstructureService.getOrganization(employeeRef);
			isAllowed = Objects.equals(routeOrganization, employeeOrganization);
		}

		if (isAllowed) { //прошли по организации
			List<NodeRef> routeUnits = findNodesByAssociationRef(routeRef, RoutesModel.ASSOC_ROUTE_ORGANIZATION_UNIT, OrgstructureBean.TYPE_ORGANIZATION_UNIT, ASSOCIATION_TYPE.TARGET);
			List<NodeRef> employeeUnits = orgstructureService.getEmployeeUnits(employeeRef, false);

			isAllowed = routeUnits.isEmpty();

			for (NodeRef routeUnit : routeUnits) {
				if (employeeUnits.contains(routeUnit)) {
					isAllowed = true;
					break;
				}
			}

			if (isAllowed) {
				String routeExpression = (String) nodeService.getProperty(routeRef, RoutesModel.PROP_ROUTE_AVAILABILITY_CONDITION);
				result = !(routeExpression != null && !routeExpression.isEmpty()) || documentService.execExpression(documentRef, routeExpression);
			}
		}

		return result;
	}

	@Override
	public ConvertRouteToIterationResult convertRouteToIteration(NodeRef documentNode, NodeRef routeNode) {
		NodeRef iteration;
		NodeRef approvalFolder = approvalService.getDocumentApprovalFolder(documentNode);
		ConvertRouteToIterationResult result = new ConvertRouteToIterationResult();
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
					throw new AlfrescoRuntimeException(String.format("Iteration %s is not available for archiving", documentCurrentIteration));
				}
			}
		}
		iteration = copyService.copy(routeNode, approvalFolder, ContentModel.ASSOC_CONTAINS, getRandomQName(), false);
		result.setIterationNode(iteration);
		List<NodeRef> routeStages = getAllStagesOfRoute(routeNode);
		for (NodeRef routeStage : routeStages) {
			if (!nodeService.hasAspect(routeStage, ContentModel.ASPECT_TEMPORARY)) {
				copyService.copy(routeStage, iteration, ContentModel.ASSOC_CONTAINS, getRandomQName(), true);
			}
		}
		Pair<List<NodeRef>, List<String>> resolveIterationMacrosesResultPair = resolveIterationMacroses(iteration, documentNode);
		result.setStageItems(resolveIterationMacrosesResultPair.getFirst());
		result.setScriptErrors(resolveIterationMacrosesResultPair.getSecond());

		return result;
	}

	private Pair<List<NodeRef>, List<String>> resolveIterationMacroses(NodeRef iterationRef, NodeRef documentNode) {
		List<NodeRef> stageItems = getAllStageItemsOfRoute(iterationRef);
		List<NodeRef> goodStageItems = new ArrayList<>();
		List<String> failedScripts = new ArrayList<>();
		for (NodeRef stageItem : stageItems) {
			if (nodeService.getTargetAssocs(stageItem, RoutesModel.ASSOC_STAGE_ITEM_MACROS).size() > 0) {
				try {
					boolean macrosResolved = resolveStageItemMacros(stageItem, documentNode);
					if (!macrosResolved) {
						continue;
					}
				} catch (ScriptException ex) {
					failedScripts.add(ex.getMessage());
					continue;
				}
			}
			goodStageItems.add(stageItem);
		}

		return new Pair<>(goodStageItems, failedScripts);
	}

	@Override
	public boolean resolveStageItemMacros(NodeRef stageItemNode, NodeRef documentNode) {
		boolean result = false;
		NodeRef employeeRef = null;
		NodeRef macrosNode = findNodeByAssociationRef(stageItemNode, RoutesModel.ASSOC_STAGE_ITEM_MACROS, RoutesMacrosModel.TYPE_MACROS, ASSOCIATION_TYPE.TARGET);
		if (macrosNode == null || !nodeService.exists(macrosNode)) {
			// что-то пошло не так
			permDeleteNode(stageItemNode);
		} else {
			String macrosString = (String) nodeService.getProperty(macrosNode, RoutesMacrosModel.PROP_MACROS_STRING);
			try {
				employeeRef = evaluateMacrosString(macrosString, documentNode, orgstructureService.getCurrentEmployee());
			} catch (ScriptException ex) {
				String macrosName = (String) nodeService.getProperty(macrosNode, ContentModel.PROP_NAME);
				String exceptionMsg = String.format("| %s | Error executing script:%n%s%n%s", macrosName, macrosString, ex.getMessage());
				permDeleteNode(stageItemNode);
				logger.warn("Error executing script {}. Macros node: {}", macrosString, macrosNode);
				throw new ScriptException(exceptionMsg);
			}
			if (employeeRef == null) {
				logger.warn("Script {} returned no employee. I'll delete stage item, related to macros node {}", macrosString, macrosNode);
				permDeleteNode(stageItemNode);
			} else {

				NodeRef stage = nodeService.getPrimaryParent(stageItemNode).getParentRef();
				List<ChildAssociationRef> stageItemsAssocs = new ArrayList<>(nodeService.getChildAssocsWithoutParentAssocsOfType(stage, RoutesModel.TYPE_STAGE_ITEM));

				List<NodeRef> employees = Lists.newArrayList(Iterables.filter(Iterables.transform(stageItemsAssocs, new Function<ChildAssociationRef, NodeRef>() {
					@Override
					public NodeRef apply(@Nullable ChildAssociationRef childAssociationRef) {
						List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(childAssociationRef.getChildRef(), RoutesModel.ASSOC_STAGE_ITEM_EMPLOYEE);
						if (targetAssocs != null && !targetAssocs.isEmpty()) {
							return targetAssocs.get(0).getTargetRef();
						} else {
							return null;
						}
					}
				}), new Predicate<NodeRef>() {
					@Override
					public boolean apply(@Nullable NodeRef nodeRef) {
						return nodeRef != null;
					}
				}));

				if (employees.contains(employeeRef)) {
					permDeleteNode(stageItemNode);
				} else {
					nodeService.createAssociation(stageItemNode, employeeRef, RoutesModel.ASSOC_STAGE_ITEM_EMPLOYEE);
					result = true;
				}
			}
		}
		return result;
	}

	private NodeRef evaluateMacrosString(String macrosString, NodeRef documentNode, NodeRef currentEmployee) {
		Map<String, Object> scriptModel = new HashMap<>(),
				returnModel = new HashMap<>();
		NodeRef resultEmployee;
		ValueConverter converter = new ValueConverter();

		scriptModel.put("model", returnModel);
		scriptModel.put("document", documentNode);
		scriptModel.put("currentEmployee", currentEmployee);

		scriptService.executeScriptString(macrosString, scriptModel);

		resultEmployee = (NodeRef) converter.convertValueForJava(returnModel.get("result"));

		return resultEmployee;
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
		props.put(ContentModel.PROP_TITLE, CUSTOM_ITERATION_TITLE);

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
					throw new AlfrescoRuntimeException(String.format("Iteration %s is not available for archiving", documentCurrentIteration));
				}
			}
		}

		iterationNode = nodeService.createNode(approvalFolder, ContentModel.ASSOC_CONTAINS, getRandomQName(), RoutesModel.TYPE_ROUTE, props).getChildRef();

		return iterationNode;
	}

	private NodeRef createIterationFromPrevious(NodeRef sourceIterationNode, NodeRef destinationParent) {
		/* делаем копию текущей итерации */
		NodeRef iterationNode = copyService.copyAndRename(sourceIterationNode, destinationParent, ContentModel.ASSOC_CONTAINS, getRandomQName(), true);
		/* сбрасываем статусы, даты, флаги и прочее */
		Map<QName, Serializable> iterationProps = nodeService.getProperties(iterationNode);
		iterationProps.put(ContentModel.PROP_NAME, UUID.randomUUID().toString());
		iterationProps.put(ApprovalAspectsModel.PROP_APPROVAL_STATE, "NEW");
		iterationProps.put(ApprovalAspectsModel.PROP_APPROVAL_DECISION, "NO_DECISION");
		iterationProps.remove(ApprovalAspectsModel.PROP_APPROVAL_HAS_COMMENT);
		iterationProps.remove(RoutesModel.PROP_ROUTE_START_DATE);
		iterationProps.remove(RoutesModel.PROP_ROUTE_COMPLETE_DATE);
		nodeService.setProperties(iterationNode, iterationProps);
		NodeRef initiatorEmployee = findNodeByAssociationRef(iterationNode, RoutesModel.ASSOC_ROUTE_INITIATOR_EMPLOYEE, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		if (initiatorEmployee != null) {
			nodeService.removeAssociation(iterationNode, initiatorEmployee, RoutesModel.ASSOC_ROUTE_INITIATOR_EMPLOYEE);
		}
		List<NodeRef> stages = getAllStagesOfRoute(iterationNode);
		for (NodeRef stageNode : stages) {
			Map<QName, Serializable> stageProps = nodeService.getProperties(stageNode);
			stageProps.put(ApprovalAspectsModel.PROP_APPROVAL_STATE, "NEW");
			stageProps.put(ApprovalAspectsModel.PROP_APPROVAL_DECISION, "NO_DECISION");
			stageProps.remove(ApprovalAspectsModel.PROP_APPROVAL_HAS_COMMENT);
			nodeService.setProperties(stageNode, stageProps);
			List<NodeRef> items = getAllStageItemsOfStage(stageNode);
			for (NodeRef itemNode : items) {
				Map<QName, Serializable> itemProps = nodeService.getProperties(itemNode);
				itemProps.put(ApprovalAspectsModel.PROP_APPROVAL_STATE, "NEW");
				itemProps.put(ApprovalAspectsModel.PROP_APPROVAL_DECISION, "NO_DECISION");
				itemProps.remove(ApprovalAspectsModel.PROP_APPROVAL_HAS_COMMENT);
				itemProps.remove(RoutesModel.PROP_STAGE_ITEM_START_DATE);
				itemProps.remove(RoutesModel.PROP_STAGE_ITEM_DUE_DATE);
				itemProps.remove(RoutesModel.PROP_STAGE_ITEM_COMPLETE_DATE);
				itemProps.remove(RoutesModel.PROP_STAGE_ITEM_USERNAME);
				itemProps.remove(RoutesModel.PROP_STAGE_ITEM_COMMENT);
				nodeService.setProperties(itemNode, itemProps);
			}
		}
		return iterationNode;
	}

	@Override
	public NodeRef createIterationFromPrevious(NodeRef documentNode) {
		NodeRef iterationNode = null, documentCurrentIteration;
		NodeRef approvalFolder = approvalService.getDocumentApprovalFolder(documentNode);
		if (approvalFolder != null) {
			/* находим текущую итерацию */
			documentCurrentIteration = getDocumentCurrentIteration(documentNode);
			if (documentCurrentIteration != null) {
				boolean readyForArchiving = isIterationAvailableForArchiving(documentCurrentIteration);
				boolean readyForDeleting = isIterationAvailableForDeleting(documentCurrentIteration);
				if (readyForArchiving) {
					iterationNode = createIterationFromPrevious(documentCurrentIteration, approvalFolder);
					archiveDocumentCurrentIteration(documentNode);
				} else if (readyForDeleting) {
					deleteDocumentCurrentIteration(documentNode);
				} else {
					throw new AlfrescoRuntimeException(String.format("Iteration %s is not available for archiving or deleting", documentCurrentIteration));
				}
			}
			if (iterationNode == null) {
				NodeRef archiveFolder = approvalService.getDocumentApprovalHistoryFolder(documentNode);
				if (archiveFolder != null) {
					/* находим последнюю архивную итерацию */
					Set<QName> types = new HashSet<>();
					types.add(RoutesModel.TYPE_ROUTE);
					List<ChildAssociationRef> children = nodeService.getChildAssocs(archiveFolder, types);
					if (children.size() > 0) {
						NodeRef lastArchiveIterationNode = children.get(children.size() - 1).getChildRef();
						iterationNode = createIterationFromPrevious(lastArchiveIterationNode, approvalFolder);
					} else {
						throw new AlfrescoRuntimeException(String.format("Document %s doesn't have any archive iterations", documentNode));
					}
				} else {
					throw new AlfrescoRuntimeException(String.format("Document %s doesn't have archive folder", documentNode));
				}
			}
		} else {
			throw new AlfrescoRuntimeException(String.format("Document %s doesn't have approval folder", documentNode));
		}
		if (iterationNode == null) {
			throw new AlfrescoRuntimeException(String.format("Failed to copy iteration from previous in document %s", documentNode));
		}
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

	@Override
	public NodeRef getDocumentByIteration(NodeRef iterationNode) {
		NodeRef approvalDir = nodeService.getPrimaryParent(iterationNode).getParentRef();
		NodeRef document = nodeService.getPrimaryParent(approvalDir).getParentRef();

		if (documentService.isDocument(document)) {
			return document;
		} else {
			return null;
		}
	}

	@Override
	public NodeRef getDocumentByStage(NodeRef stageNode) {
		return getDocumentByIteration(nodeService.getPrimaryParent(stageNode).getParentRef());
	}

	@Override
	public NodeRef getDocumentByStageItem(NodeRef stageItemNode) {
		return getDocumentByStage(nodeService.getPrimaryParent(stageItemNode).getParentRef());
	}

	@Override
	public boolean hasEmployeesInRoute(final NodeRef nodeRef) {
		return !isRouteEmpty(nodeRef, null, false, true);
	}

	@Override
	public boolean hasEmployeesInRoute(NodeRef route, NodeRef docForExpression) {
		return !isRouteEmpty(route, docForExpression, false, true);
	}

	@Override
	public boolean isRouteEmpty(final NodeRef nodeRef) {
		return isRouteEmpty(nodeRef, null, true, false);
	}

	@Override
	public boolean isRouteEmpty(NodeRef route, NodeRef docForExpression) {
		return isRouteEmpty(route, docForExpression, true, false);
	}

	private boolean isRouteEmpty(NodeRef nodeRef, NodeRef document, boolean checkExpressions, boolean checkEmployees) {
		boolean isEmptyByEmployees = true;
		boolean isEmptyByExpressions = false;/* По постановке - не пусто, если этапов нет*/

		NodeRef currentIterationRef = null;
		NodeRef documentForExpression = document;

		// Получаем маршрут + документ
		if (nodeRef != null) {
			QName type = nodeService.getType(nodeRef);
			if (!RoutesModel.TYPE_ROUTE.equals(type)) { // document
				currentIterationRef = getDocumentCurrentIteration(nodeRef);
				if (documentForExpression == null) {
					documentForExpression = nodeRef;
				}
			} else { // route
				currentIterationRef = nodeRef;
				if (documentForExpression == null) {
					documentForExpression = getDocumentByIteration(currentIterationRef);
				}
			}
		}

		if (currentIterationRef != null) {
			final List<NodeRef> stages = getAllStagesOfRoute(currentIterationRef);

			final int stagesCount = stages.size();
			int skippedCount = 0;

			for (NodeRef stage : stages) {
				if (checkExpressions && documentForExpression != null) {
					Object stageExpression = nodeService.getProperty(stage, RoutesModel.PROP_STAGE_EXPRESSION);
					if (stageExpression != null && !documentService.execExpression(documentForExpression, stageExpression.toString())) {
						skippedCount++;
						continue; //пропускаем те, что будут пропущены
					}
				}

				if (checkEmployees) {
					List<NodeRef> stageItems = getAllStageItemsOfStage(stage);
					for (NodeRef stageItem : stageItems) {
						List<AssociationRef> assocs = nodeService.getTargetAssocs(stageItem, RoutesModel.ASSOC_STAGE_ITEM_EMPLOYEE);
						if (assocs.size() > 0) {
							isEmptyByEmployees = false;
							break;
						}

						if (documentForExpression != null) {
							NodeRef employee = null;
							NodeRef macrosNode = findNodeByAssociationRef(stageItem, RoutesModel.ASSOC_STAGE_ITEM_MACROS, RoutesMacrosModel.TYPE_MACROS, ASSOCIATION_TYPE.TARGET);
							if (macrosNode != null && nodeService.exists(macrosNode)) {
								String macrosString = (String) nodeService.getProperty(macrosNode, RoutesMacrosModel.PROP_MACROS_STRING);
								try {
									employee = evaluateMacrosString(macrosString, documentForExpression, orgstructureService.getCurrentEmployee());
								} catch (ScriptException ex) {
									logger.warn("Error executing script {}. Macros node: {}", macrosString, macrosNode);
								}
								if (employee != null) {
									isEmptyByEmployees = false;
									break;
								}
							}
						}
					}
				}

			}
			isEmptyByExpressions = stagesCount > 0 && (skippedCount == stagesCount);
		}
		return (checkExpressions && isEmptyByExpressions) || (checkEmployees && isEmptyByEmployees);
	}

	@Override
	public boolean hasPotentialEmployeesInRoute(NodeRef routeRef, NodeRef documentNode) {
		boolean result = false;
		NodeRef employeeRef = null;
		List<NodeRef> stageItems = getAllStageItemsOfRoute(routeRef);
		for (NodeRef stageItem : stageItems) {
			NodeRef macrosNode = findNodeByAssociationRef(stageItem, RoutesModel.ASSOC_STAGE_ITEM_MACROS, RoutesMacrosModel.TYPE_MACROS, ASSOCIATION_TYPE.TARGET);
			if (macrosNode != null && nodeService.exists(macrosNode)) {
				String macrosString = (String) nodeService.getProperty(macrosNode, RoutesMacrosModel.PROP_MACROS_STRING);
				try {
					employeeRef = evaluateMacrosString(macrosString, documentNode, orgstructureService.getCurrentEmployee());
				} catch (ScriptException ex) {
					logger.warn("Error executing script {}. Macros node: {}", macrosString, macrosNode);
				}
				if (employeeRef == null) {
					logger.warn("Script {} returned no employee. I'll delete stage item, related to macros node {}", macrosString, macrosNode);
				} else {
					result = true;
					break; // Нашли хотя бы одного потенциального сотрудника в маршруте
				}
			}
		}
		return result;
	}

	@Override
	public String getApprovalState(NodeRef documentNode) {
		String result;
		NodeRef currentIteration = getDocumentCurrentIteration(documentNode);
		if (currentIteration != null) {
			String approvalState;
			approvalState = (String) nodeService.getProperty(currentIteration, ApprovalAspectsModel.PROP_APPROVAL_STATE);
			if ("COMPLETE".equals(approvalState)) {
				result = (String) nodeService.getProperty(currentIteration, ApprovalAspectsModel.PROP_APPROVAL_DECISION);
			} else {
				result = approvalState;
			}
		} else {
			result = "UNDEF";
		}

		return result;
	}

	@Override
	public List<NodeRef> getEmployeesByRoute(NodeRef route) {
		List<NodeRef> resultList = new ArrayList<>();

		List<NodeRef> stageItems = getAllStageItemsOfRoute(route);

		for (NodeRef stageItem : stageItems) {
			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(stageItem, RoutesModel.ASSOC_STAGE_ITEM_EMPLOYEE);
			if (targetAssocs != null && !targetAssocs.isEmpty()) {
				resultList.add(targetAssocs.get(0).getTargetRef());
			}
		}

		return resultList;
	}

	@Override
	public List<NodeRef> getEmployeesOfAllDocumentRoutes(NodeRef document) {
		List<NodeRef> resultList = new ArrayList<>();

		NodeRef approvalFolder = approvalService.getDocumentApprovalFolder(document);
		List<ChildAssociationRef> routesAssocs = nodeService.getChildAssocs(approvalFolder, Collections.singleton(RoutesModel.TYPE_ROUTE));

		for (ChildAssociationRef routesAssoc : routesAssocs) {
			resultList.addAll(getEmployeesByRoute(routesAssoc.getChildRef()));
		}

		return resultList;
	}
	
	@Override
	protected void onBootstrap(ApplicationEvent event) {
		lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
					@Override
					public Void doWork() throws Exception {
						getServiceRootFolder();
						return null;
					}
				});
			}
		});
	}

}
