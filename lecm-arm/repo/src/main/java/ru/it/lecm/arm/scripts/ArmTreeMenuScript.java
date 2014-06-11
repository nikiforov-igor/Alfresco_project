package ru.it.lecm.arm.scripts;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.arm.beans.ArmColumn;
import ru.it.lecm.arm.beans.ArmFilter;
import ru.it.lecm.arm.beans.ArmFilterValue;
import ru.it.lecm.arm.beans.ArmWrapperServiceImpl;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;
import ru.it.lecm.base.beans.LecmTransactionHelper;

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

	public static final String CREATE_TYPES = "createTypes";
	public static final String HTML_URL = "htmlUrl";
	public static final String REPORT_CODES = "reportCodes";

    private ArmWrapperServiceImpl service;
	private DictionaryService dictionaryService;
	private NamespaceService namespaceService;
	private StateMachineServiceBean stateMachineService;
	private DocumentService documentService;
        private LecmTransactionHelper lecmTransactionHelper;
	private TransactionService transtactionService;

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

	@Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        List<JSONObject> nodes = new ArrayList<JSONObject>();

        String nodeRef = req.getParameter(NODE_REF);
        String armNodeRef = req.getParameter(ARM_NODE_REF);
        String armCode = req.getParameter(ARM_CODE);

        if (nodeRef == null) { // получаем список корневых узлов - аккордеонов для заданного АРМ
            List<ArmNode> accordions = service.getAccordionsByArmCode(armCode);
	        Map<String, Boolean> isStarterHash = new HashMap<String, Boolean>();
            for (ArmNode accordion : accordions) {
                nodes.add(toJSON(accordion, true, isStarterHash));
            }
        } else {
            // получение списка дочерних элементов
            if (NodeRef.isNodeRef(nodeRef)) {
                List<ArmNode> childs = service.getChildNodes(new NodeRef(nodeRef), new NodeRef(armNodeRef));
                for (ArmNode child : childs) {
                    nodes.add(toJSON(child, false, null));
                }
            }
        }
        try {
            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(nodes.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private JSONObject toJSON(ArmNode node, boolean isAccordionNode, Map<String, Boolean> isStarterHash) {
        JSONObject result = new JSONObject();
        try {
            result.put(ID, node.getNodeRef() != null ?
                    node.getNodeRef().getId() :
                    (node.getArmNodeRef() != null ?
                            node.getArmNodeRef().getId() + "-" + node.getTitle():
                            node.getTitle()));
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
            }
            result.put(FILTER, getFiltersJSON(node.getAvaiableFilters()));

            if (node.getSearchQuery() != null) {
                result.put(SEARCH_QUERY, node.getSearchQuery());
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
	        result.put(REPORT_CODES,node.getReportCodes());
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
					QName fieldQName = QName.createQName(fieldName, namespaceService);

					String type = "";
					String formsName = "";
					String dataType = "";

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

					columnJSON.put("type", type);
					columnJSON.put("name", fieldName);
					columnJSON.put("formsName", formsName);
					columnJSON.put("label", column.getTitle());
					columnJSON.put("nameSubstituteString", column.getFormatString());
					columnJSON.put("dataType", dataType);
					columnJSON.put("sortable", column.isSortable());

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
				if (typeDefinition != null) {
					try {
						boolean isStarter;
						if (isStarterHash.containsKey(type)) {
							isStarter = isStarterHash.get(type);
						} else {
							isStarter = stateMachineService.isStarter(type);
							isStarterHash.put(type, isStarter);
						}
						JSONObject json = new JSONObject();
						json.put("type", type);
						json.put("disabled", !isStarter);
						json.put("label", typeDefinition.getTitle(dictionaryService));
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
}
