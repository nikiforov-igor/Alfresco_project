package ru.it.lecm.arm.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
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
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.arm.beans.ArmColumn;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.arm.beans.ArmWrapperServiceImpl;
import ru.it.lecm.arm.beans.filters.ArmDocumentsFilter;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.filters.BaseQueryArmFilter;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
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
    private ArmWrapperServiceImpl armWrapperService;

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
                        values.add(currentValueStr);
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

    /**
     * Получение узла с настройками для узла АРМ
     * @param armNode узел
     */
    public ScriptNode getNodeUserSettings(ScriptNode armNode) {
        NodeRef settings = armService.getNodeUserSettings(armNode.getNodeRef());
        return (settings == null) ? null : new ScriptNode(settings, serviceRegistry, getScope());
    }

    /**
     * Создание узел с настройками для узла АРМ
     * @param armNode узел
     */
    public ScriptNode createNodeUserSettings(ScriptNode armNode) {
        NodeRef settings = null;
        try {
            settings = armService.createUserSettingsForNode(armNode.getNodeRef());
        } catch (WriteTransactionNeededException e) {
            logger.warn("Can not create user settings node");
        }

        return (settings == null) ? null : new ScriptNode(settings, serviceRegistry, getScope());
    }

    /**
     * Возвращает список столбцов (объектов из репозитория, ScriptNode) для заданного узла АРМ
     * @param armNode узел
     */
    @SuppressWarnings("unused")
    public Scriptable getNodeColumns(ScriptNode armNode) {
        List<NodeRef> columns = new ArrayList<>();
        if (armNode != null) {
            columns = armService.getNodeColumnsRefs(armNode.getNodeRef());
        }
        return createScriptable(columns);
    }

    /**
     * Возвращает список столбцов (объектов из репозитория, ScriptNode) для заданного узла АРМ
     * @param armNode узел
     */
    @SuppressWarnings("unused")
    public boolean saveUserColumnsSet(final ScriptNode armNode, final String columnsToSavedJSON) {
        final AuthenticationUtil.RunAsWork<Boolean> saveColumns = new AuthenticationUtil.RunAsWork<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                boolean result = false;
                ScriptNode settingsNode = getNodeUserSettings(armNode);
                if (settingsNode == null) {
                    settingsNode = createNodeUserSettings(armNode);
                }
                if (settingsNode != null) {
                    List<NodeRef> targetColumns = new ArrayList<>();
                    try {
                        JSONObject columnsSettings = new JSONObject(columnsToSavedJSON);
                        JSONArray selectedColumnsArray = (JSONArray) columnsSettings.get("selected");
                        for (int j = 0; j < selectedColumnsArray.length(); j++) {
                            String columnRef = (String) selectedColumnsArray.get(j);
                            if (NodeRef.isNodeRef(columnRef)) {
                                targetColumns.add(new NodeRef(columnRef));
                            }
                        }
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }

                    nodeService.setAssociations(settingsNode.getNodeRef(), ArmService.ASSOC_USER_NODE_COLUMNS, targetColumns);

                    result = true;
                }
                armService.invalidateCurrentUserCache();
                return result;
            }
        };

        return AuthenticationUtil.runAsSystem(saveColumns);
    }

    public JSONObject convertPathToNodes(String code, String path) {
        JSONObject result = new JSONObject();
        try {
            if (code != null && path != null) {
                String[] splitPath = path.split("/");
                NodeRef armRef = armService.getArmByCode(code);
                if (armRef != null) {
                    List<NodeRef> accordions = armService.getArmAccordions(armRef);
                    NodeRef accordion = null;
                    if (splitPath.length > 0) {
                        for (NodeRef accordionItem : accordions) {
                            String name = nodeService.getProperty(accordionItem, ContentModel.PROP_NAME).toString();
                            if (name.equals(splitPath[0])) {
                                accordion = accordionItem;
                                break;
                            }
                        }
                    }
                    if (accordion != null) {
                        result.put("accordion", accordion.getId());
                        StringBuilder nodePath = new StringBuilder(accordion.getId());
                        NodeRef prevNode = accordion;
                        NodeRef parentNode = armRef;
                        for (int i = 1; i < splitPath.length; i++) {
                            boolean isFind = false;
                            List<ArmNode> nodes =  armWrapperService.getChildNodes(prevNode, parentNode, true);
                            for (ArmNode node : nodes) {
                                if (!node.getNodeType().equals("lecm-arm:accordion") && node.getTitle().equals(splitPath[i])) {
                                    isFind = true;
                                    parentNode = prevNode;
                                    prevNode = node.getNodeRef();
                                    if (node.getNodeRef() == null) {
                                        nodePath.append(".").append(node.getArmNodeRef().getId()).append("-").append(node.getTitle()).append('-').append(node.getArmNodeRef().getId());
                                    } else {
                                        nodePath.append(".").append(node.getNodeRef().getId()).append('-').append(node.getArmNodeRef().getId());
                                    }
                                    break;
                                }
                            }
                            if (!isFind) {
                                break;
                            }
                        }
                        result.put("selected", nodePath.toString());
                        result.put("pageNum", 1);
                    }
                }
            }
        } catch (JSONException e) {
            logger.error("Cannot convert Path to Path Object because:", e);
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

    public void setArmWrapperService(ArmWrapperServiceImpl armWrapperService) {
        this.armWrapperService = armWrapperService;
    }

    public boolean checkShowCalendar(String code) {
        NodeRef armRef = armService.getArmByCode(code);
        Boolean showCalendar = (Boolean) nodeService.getProperty(armRef, ArmService.PROP_ARM_SHOW_CALENDAR);
        return armRef != null && showCalendar != null && showCalendar;
    }
    public boolean checkShowCreateButton(String code) {
        NodeRef armRef = armService.getArmByCode(code);
        return armRef != null && Boolean.TRUE.equals(nodeService.getProperty(armRef, ArmService.PROP_ARM_SHOW_CREATE_BUTTON));
    }
    /**
     * Возвращает список АРМов для меню
     */
    @SuppressWarnings("unused")
    public Scriptable getArmsForMenu() {
        List<NodeRef> arms = armService.getArmsForMenu();
        return arms != null ? createScriptable(arms) : null;
    }
}
