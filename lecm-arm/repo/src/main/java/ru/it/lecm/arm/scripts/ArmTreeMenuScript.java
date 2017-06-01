package ru.it.lecm.arm.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.arm.beans.*;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.search.ArmChildrenRequest;
import ru.it.lecm.arm.beans.search.ArmChildrenResponse;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.base.beans.evaluators.ValueEvaluatorsManager;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.templates.api.DocumentTemplateModel;
import ru.it.lecm.documents.templates.api.DocumentTemplateService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

/**
 * User: dbashmakov
 * Date: 10.12.13
 * Time: 12:49
 */
public class ArmTreeMenuScript extends AbstractWebScript {
    final private static Logger logger = LoggerFactory.getLogger(ArmTreeMenuScript.class);

    public static final String NODE_REF = "nodeRef";
    public static final String NODE_TYPE = "nodeType";
    public static final String ARM_NODE_REF = "armNodeRef";
    public static final String ARM_NODE_ID = "armNodeId";
    public static final String SEARCH_QUERY = "searchQuery";
    public static final String ARM_CODE = "armCode";
    public static final String ID = "id";
    public static final String TYPES = "types";
    public static final String COLUMNS = "columns";
    public static final String LABEL = "label";
    public static final String IS_LEAF = "isLeaf";
    private static final String FILTER = "filters";
    private static final String COUNTER = "counter";
    private static final String COUNTER_LIMIT = "counterLimit";
    private static final String COUNTER_DESC = "counterDesc";
    private static final String SEARCH_TYPE = "searchType";

	public static final String CREATE_TYPES = "createTypes";
	public static final String HTML_URL = "htmlUrl";
	public static final String REPORT_CODES = "reportCodes";

    public static final String RUN_AS = "runAs";
    public static final String MAX_ITEMS = "maxItems";
    public static final String SKIP_COUNT = "skipCount";
    public static final String SEARCH_TERM = "searchTerm";
    public static final String REAL_CHILDREN_COUNT = "realChildrenCount";

    private ArmWrapperServiceImpl service;
	private DictionaryService dictionaryService;
	private NamespaceService namespaceService;
	private StateMachineServiceBean stateMachineService;
	private DocumentService documentService;
    private LecmTransactionHelper lecmTransactionHelper;
	private TransactionService transtactionService;
    private ArmServiceImpl armService;
	private DocumentTemplateService documentTemplateService;
	private NodeService nodeService;
    private ValueEvaluatorsManager valueEvaluatorsManager;

    public void setArmService(ArmServiceImpl armService) {
        this.armService = armService;
    }

        public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
            this.lecmTransactionHelper = lecmTransactionHelper;
        }

	public void setTranstactionService(TransactionService transtactionService) {
		this.transtactionService = transtactionService;
	}

    public void setService(ArmWrapperServiceImpl service) {
        this.service = service;
    }

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentTemplateService(DocumentTemplateService documentTemplateService) {
		this.documentTemplateService = documentTemplateService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        List<JSONObject> nodes = new ArrayList<JSONObject>();
        JSONObject result = new JSONObject();

        String nodeRef = req.getParameter(NODE_REF);
        String armNodeRef = req.getParameter(ARM_NODE_REF);
        String armCode = req.getParameter(ARM_CODE);
        String runAsBoss = req.getParameter(RUN_AS);
        String searchTerm = req.getParameter(SEARCH_TERM);
        int maxItems = -1, skipCount = 0;
        try {
            maxItems = Integer.parseInt(req.getParameter(MAX_ITEMS));
        } catch (NumberFormatException e) {
            logger.warn("### maxItems were not provided and set to default");
        }

        try {
            skipCount = Integer.parseInt(req.getParameter(SKIP_COUNT));
        } catch (NumberFormatException e) {
            logger.warn("### skipCount were not provided and set to default");
        }

        JSONObject parentNodeInfo = new JSONObject();
        if (nodeRef == null) { // получаем список корневых узлов - аккордеонов для заданного АРМ
            List<ArmNode> accordions = service.getAccordionsByArmCode(armCode);
	        Map<String, Boolean> isStarterHash = new HashMap<String, Boolean>();
            for (ArmNode accordion : accordions) {
                nodes.add(toJSON(accordion, true, isStarterHash, null));
            }
        } else {
            // получение списка дочерних элементов
            if (NodeRef.isNodeRef(nodeRef)) {
                ArmChildrenRequest request = new ArmChildrenRequest(new NodeRef(nodeRef), new NodeRef(armNodeRef));
                request.setMaxItems(maxItems);
                request.setSkipCount(skipCount);
                request.setSearchTerm(searchTerm);
                ArmChildrenResponse childs = service.getChildNodes(request);
                for (ArmNode child : childs.getNodes()) {
                    nodes.add(toJSON(child, false, null, runAsBoss));
                }
                long realChildrenCount = childs.getChildCount();
                try {
                    parentNodeInfo.put("parentNodeRealChildrenCount", realChildrenCount);
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        try {
            result.put("parentNodeInfo", parentNodeInfo);
            result.put("children", nodes);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        try {
            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(result.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private JSONObject toJSON(ArmNode node, boolean isAccordionNode, Map<String, Boolean> isStarterHash, String runAs) {
        JSONObject result = new JSONObject();
        try {
            String nodeId = (node.getNodeRef() != null ?
                    node.getNodeRef().getId() :
                    (node.getArmNodeRef() != null ?
                            node.getArmNodeRef().getId() + "-" + node.getTitle():
                            node.getTitle()));
            if (node.getRunAsEmployee() == null && (runAs == null || runAs.isEmpty())) {
                result.put(ID, nodeId);
            } else {
                String employeeId =
                        node.getRunAsEmployee()!= null ?
                                node.getRunAsEmployee().getId()  :
                                (NodeRef.isNodeRef(runAs) ? new NodeRef(runAs).getId() : runAs);
                result.put(ID, nodeId + "-" + employeeId);
            }
            result.put(NODE_REF, node.getNodeRef() != null ? node.getNodeRef().toString() : null);
	        result.put(NODE_TYPE, node.getNodeType() != null ? node.getNodeType() : null);

            if (node.getArmNodeRef() != null) {
                result.put(ARM_NODE_REF, node.getArmNodeRef().toString());
                result.put(ARM_NODE_ID, node.getArmNodeRef().getId());
            }

            result.put(TYPES, listToString(node.getTypes()));
            result.put(COLUMNS, getColumnsJSON(node.getColumns()));
            result.put(LABEL, node.getTitle());
            if (!isAccordionNode) {
                result.put(IS_LEAF, !service.hasChildNodes(node));
                boolean isAggregate = false;
                if (node.getNodeRef() != null) {
                    Object isAggregationNode = armService.getCachedProperties(node.getNodeRef()).get(ArmService.PROP_IS_AGGREGATION_NODE);
                    isAggregate = Boolean.TRUE.equals(isAggregationNode);
                }

                result.put("isAggregate", isAggregate);

            }
            result.put(FILTER, getFiltersJSON(node.getAvaiableFilters()));

            if (node.getSearchQuery() != null) {
                String query = node.getSearchQuery();
                if (node.getRunAsEmployee()!= null || (runAs != null && !runAs.isEmpty())) {
                    String employeeRef = node.getRunAsEmployee()!= null ? node.getRunAsEmployee().toString()  : runAs;
                    if (query.contains("#boss-ref")) {
                        query = query.replaceAll("#boss-ref", employeeRef);
                    }
                }
                result.put(SEARCH_QUERY, query);
            }

            if (node.getCounter() != null) {
                result.put(COUNTER, true);
                result.put(COUNTER_DESC, node.getCounter().getDescription());
                result.put(COUNTER_LIMIT, node.getCounter().getQuery());
            }
	        if (isAccordionNode) {
		        result.put(CREATE_TYPES, getCreateTypes(node, isStarterHash));
	        }
	        result.put(HTML_URL,node.getHtmlUrl());
            result.put(MAX_ITEMS, node.getMaxItemsCount());
            result.put(REAL_CHILDREN_COUNT, node.getRealChildrenCount());
	        result.put(REPORT_CODES,node.getReportCodes());
	        result.put(SEARCH_TYPE,node.getSearchType());

            result.put(RUN_AS, node.getRunAsEmployee() != null ? node.getRunAsEmployee().toString() : runAs);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    private String listToString(List<String> values) {
        StringBuilder result = new StringBuilder();
        for (String anArray : values) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(anArray);
        }
        return result.toString();
    }

	private JSONArray getColumnsJSON(List<ArmColumn> columns) throws JSONException {
		JSONArray results = new JSONArray();
		if (columns != null) {
			for (ArmColumn column: columns) {
				String fieldName = column.getField();
				if (fieldName != null) {
					JSONObject columnJSON = new JSONObject();

					String type = "";
					String formsName;
					String dataType = "";

					if (column.isCounter()) {
						type = "counter";
						formsName = fieldName;
					} else {
						QName fieldQName = QName.createQName(fieldName, namespaceService);
						PropertyDefinition prop = dictionaryService.getProperty(fieldQName);
						if (prop != null) {
							type = "property";
							formsName = "prop_" + fieldName.replace(":", "_");
							dataType = prop.getDataType().getName().getLocalName();
						} else {
							AssociationDefinition assoc = dictionaryService.getAssociation(fieldQName);
							if (assoc != null) {
								type = "association";
								formsName = "assoc_" + fieldName.replace(":", "_");
								dataType = assoc.getTargetClass().getName().toPrefixString(namespaceService);
							} else {
								formsName = "prop_" + fieldName.replace(":", "_");
							}
						}
					}
					columnJSON.put("id", column.getId());
					columnJSON.put("type", type);
					columnJSON.put("name", fieldName);
					columnJSON.put("formsName", formsName);
					columnJSON.put("label", column.getTitle());
					columnJSON.put("nameSubstituteString", column.getFormatString());
					columnJSON.put("dataType", dataType);
					columnJSON.put("sortable", column.isSortable());
					columnJSON.put("isMarker", column.isMarker());
					columnJSON.put("markerIcon", column.getMarkerIcon());
					columnJSON.put("markerHTML", URLEncoder.encodeUriComponent(column.getMarkerHTML()));

					results.put(columnJSON);
				}
			}
		}
		return results;
	}

	private JSONArray getCreateTypes(ArmNode node, Map<String, Boolean> isStarterHash) throws JSONException {
		List<JSONObject> results = new ArrayList<>();
		List<String> allTypes = node.getCreateTypes();
		if (allTypes != null) {
			for (String type : allTypes) {
				final QName typeQName = QName.createQName(type, namespaceService);
				TypeDefinition typeDefinition = dictionaryService.getType(typeQName);
                boolean notArmCreate = false;
                try {
                    notArmCreate = stateMachineService.isNotArmCreate(type);
                } catch (Exception ignored) {
                    //игнорируем любые ошибки внутри машины состояний
                }
                if (typeDefinition != null && !notArmCreate) {
					try {
						boolean isStarter;
						if (isStarterHash.containsKey(type)) {
							isStarter = isStarterHash.get(type);
						} else {
                            isStarter = false;
                            try {
                                isStarter = stateMachineService.isStarter(type);
                            } catch (Exception e) {
                                //игнорируем любые ошибки внутри машины состояний, строчка станет неактивной
                            }
                            isStarterHash.put(type, isStarter);
						}
						JSONObject json = new JSONObject();
						json.put("type", type);
						json.put("disabled", !isStarter);
						json.put("label", typeDefinition.getTitle(dictionaryService));
                        json.put("page", documentService.getCreateUrl(typeQName));
						json.put("templates", getTemplates(type));

                        results.add(json);
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
					}
				}
			}
		}
		Collections.sort(results, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject a, JSONObject b) {
				String valA = "";
				String valB = "";

				try {
					valA = (String) a.get("label");
					valB = (String) b.get("label");
				} catch (JSONException e) {
					logger.error("JSONException in combineJSONArrays sort section", e);
				}

				return valA.compareTo(valB);
			}
		});
		return new JSONArray(results);
	}

	private JSONArray getTemplates(String type) throws JSONException {
		List<NodeRef> templateRefs = documentTemplateService.getDocumentTemplatesForType(type);
		JSONArray templates = new JSONArray();
		for (NodeRef templateRef : templateRefs) {
			Map<QName, Serializable> props = nodeService.getProperties(templateRef);
			String name = (String)props.get(ContentModel.PROP_TITLE);
			JSONArray attributes = new JSONArray((String)props.get(DocumentTemplateModel.PROP_DOCUMENT_TEMPLATE_ATTRIBUTES));
            for (int i = 0; i < attributes.length(); i++) {
                JSONObject attrConfig = attributes.getJSONObject(i).getJSONObject("initial");
                try {
                    JSONObject attrValue = new JSONObject(attrConfig.getString("value"));
                    attrConfig.put("value", valueEvaluatorsManager.evaluate(attrValue));
                } catch (JSONException ignored) {}
            }
			JSONObject template = new JSONObject();
			template.put("name", name);
            template.put("ref", templateRef.toString());
			template.put("attributes", attributes);
			templates.put(template);
		}
		return templates;
	}

    private JSONArray getFiltersJSON(List<ArmFilter> filters) throws JSONException {
        JSONArray results = new JSONArray();
        if (filters != null) {
            for (ArmFilter filter : filters) {
                JSONObject filterJSON = new JSONObject();
                filterJSON.put("name", filter.getTitle());
                filterJSON.put("code", filter.getCode());
                filterJSON.put("class", filter.getFilterClass());
                filterJSON.put("query", filter.getQuery());
                filterJSON.put("multiple", filter.isMultipleSelect());

                JSONArray valuesArray = new JSONArray();
                if (!filter.getValues().isEmpty()) {
                    for (ArmFilterValue armFilterValue : filter.getValues()) {
                        JSONObject valueJSON = new JSONObject();
                        valueJSON.put("name", armFilterValue.getTitle());
                        valueJSON.put("code", armFilterValue.getCode());

                        valuesArray.put(valueJSON);
                    }
                }
                filterJSON.put("values", valuesArray);

                results.put(filterJSON);
            }
        }
        return results;
    }

    public void setValueEvaluatorsManager(ValueEvaluatorsManager valueEvaluatorsManager) {
        this.valueEvaluatorsManager = valueEvaluatorsManager;
    }
}
