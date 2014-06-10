package ru.it.lecm.arm.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.*;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.NativeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.arm.beans.ArmColumn;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.arm.beans.filters.ArmDocumentsFilter;
import ru.it.lecm.arm.filters.BaseQueryArmFilter;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.IOException;
import java.util.*;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:35
 */
public class ArmWebScriptBean extends BaseWebScript implements ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(ArmWebScriptBean.class);

    public static final String CLASS = "class";
    public static final String QUERY = "query";
    public static final String CUR_VALUE = "curValue";
    public static final String MULTIPLE = "multiple";
    public static final String BASE_QUERY_ARM_FILTER = "baseQueryArmFilter";

    private static ApplicationContext ctx;

	private ArmService armService;
	private DictionaryService dictionaryService;
	private NamespaceService namespaceService;
	private NodeService nodeService;
	private DocumentService documentService;

	public void setArmService(ArmService armService) {
		this.armService = armService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        ctx = appContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    @SuppressWarnings("unused")
	public ScriptNode getDictionaryArmSettings() {
		NodeRef dictionary = armService.getDictionaryArmSettings();

		return (dictionary == null) ? null : new ScriptNode(dictionary, serviceRegistry, getScope());
	}

    @SuppressWarnings("unused")
	public JSONObject getAvailableNodeFields(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		Collection<QName> availableTypes = new ArrayList<QName>();
		NodeRef ref = new NodeRef(nodeRef);
		List<String> existFields = new ArrayList<String>();
		if (this.nodeService.exists(ref)) {
			availableTypes = armService.getNodeTypesIncludeInherit(ref);

			List<ArmColumn> exitColumns = armService.getNodeColumns(ref);
			if (exitColumns != null) {
				for (ArmColumn column: exitColumns) {
					existFields.add(column.getField());
				}
			}
		}
		if (availableTypes.size() == 0) {
			availableTypes = documentService.getDocumentSubTypes();
		}
		Set<QName> listAvailableSet = new LinkedHashSet<QName>();
		listAvailableSet.add(DocumentService.TYPE_BASE_DOCUMENT);
		listAvailableSet.addAll(availableTypes);

		Set<ClassAttributeDefinition> attributes = new LinkedHashSet<ClassAttributeDefinition>();
		for (QName typeQName : listAvailableSet) {
			TypeDefinition type = dictionaryService.getType(typeQName);
			attributes.addAll(type.getProperties().values());
			attributes.addAll(type.getAssociations().values());

			List<AspectDefinition> defaultAspects = type.getDefaultAspects();
			if (defaultAspects != null) {
				for (AspectDefinition aspect: defaultAspects) {
					attributes.addAll(aspect.getProperties().values());
					attributes.addAll(aspect.getAssociations().values());
				}
			}
		}

		JSONObject result = new JSONObject();
		try {
			JSONArray fieldsJson = new JSONArray();

			for (ClassAttributeDefinition attr : attributes) {
				String attrName = attr.getName().toPrefixString(namespaceService);
				if (!existFields.contains(attrName) && !attrName.endsWith("-ref") && !attrName.endsWith("-text-content")) {
					JSONObject propJson = new JSONObject();
					propJson.put("title", attr.getTitle(dictionaryService) != null ? attr.getTitle(dictionaryService) : "");
					propJson.put("name", attrName);
					if (attr instanceof PropertyDefinition) {
						propJson.put("type", ((PropertyDefinition) attr).getDataType().getTitle(dictionaryService));
					} else if (attr instanceof AssociationDefinition) {
						propJson.put("type", ((AssociationDefinition) attr).getTargetClass().getTitle(dictionaryService));
					}

					fieldsJson.put(propJson);
				}
			}

			result.put("items", fieldsJson);
		} catch (JSONException e) {
			logger.error("Error create jsonObject", e);
		}
		return result;
	}

    @SuppressWarnings("unused")
	public Map<String, String> getTypes(String itemId, String destination) {
		Map<String, String> results = new HashMap<String, String>();

		NodeRef ref = null;
		if (itemId != null && NodeRef.isNodeRef(itemId)) {
			ref = nodeService.getPrimaryParent(new NodeRef(itemId)).getParentRef();
		} else if (destination != null && NodeRef.isNodeRef(destination)) {
			ref = new NodeRef(destination);
		}

		if (ref != null) {
			Collection<QName> types = armService.getNodeTypesIncludeInherit(ref);
			if (types == null || types.size() == 0) {
				types = documentService.getDocumentSubTypes();
			}
			if (types != null) {
				for (QName type : types) {
					TypeDefinition typeDef = dictionaryService.getType(type);
					results.put(type.toPrefixString(namespaceService), typeDef.getTitle());
				}
			}
		}
		return results;
	}

    @SuppressWarnings("unused")
	public ScriptNode getArmByCode(String code) {
		NodeRef arm = armService.getArmByCode(code);

		return (arm == null) ? null : new ScriptNode(arm, serviceRegistry, getScope());
	}

    @SuppressWarnings("unused")
    public String getQueryByFilter(Object objFilter) {
        String result = "";
        try {
            JSONObject filter = nativeObjectToJSON((NativeObject)objFilter);
            String filterId = filter.has(CLASS) ? (String) filter.get(CLASS) : null;

            ArmDocumentsFilter filterBean = null;
            List<String> values = new ArrayList<String>();
            if (filterId != null) {
                try {
                    filterBean = (ArmDocumentsFilter) getApplicationContext().getBean(filterId);
                } catch (BeansException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            } else { // не задано - берем по умолчанию
                filterBean = (BaseQueryArmFilter) getApplicationContext().getBean(BASE_QUERY_ARM_FILTER);
            }

            if (filterBean != null) {
                if (Boolean.valueOf(String.valueOf(filter.has(MULTIPLE) ? filter.get(MULTIPLE) : false))) {
                    Object curValues = filter.has(CUR_VALUE) ? filter.get(CUR_VALUE) : null;
                    if (curValues != null) {
                        if (curValues instanceof JSONArray) {
                            JSONArray currentValueArray = (JSONArray) filter.get(CUR_VALUE);
                            for (int j = 0; j < currentValueArray.length(); j++) {
                                String v = (String) currentValueArray.get(j);
                                values.add(v);
                            }
                        } else {
                            values.addAll(Arrays.asList(((String) curValues).split(",")));
                        }

                    }
                } else {
                    String currentValueStr = filter.has(CUR_VALUE) ? (String) filter.get(CUR_VALUE) : null;
                    if (currentValueStr != null) {
                        values.addAll(Arrays.asList(currentValueStr.split(",")));
                    }
                }

                String params = filter.has(QUERY) ? (String) filter.get(QUERY) : null;

                result = filterBean.getQuery(params, values);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return result;
    }

    private JSONObject nativeObjectToJSON(NativeObject nativeObject) throws IOException {
        JSONObject result = new JSONObject();
        final Object[] ids = nativeObject.getIds();
        for (Object id : ids) {
            String key = id.toString();
            Object value = nativeObject.get(key, nativeObject);
            try {
                result.put(key, value);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }
}
