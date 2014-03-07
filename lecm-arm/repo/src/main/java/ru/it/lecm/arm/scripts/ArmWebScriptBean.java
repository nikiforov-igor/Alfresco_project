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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.arm.beans.ArmColumn;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.*;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:35
 */
public class ArmWebScriptBean extends BaseWebScript {
	private static final Logger logger = LoggerFactory.getLogger(ArmWebScriptBean.class);

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

	public ScriptNode getDictionaryArmSettings() {
		NodeRef dictionary = armService.getDictionaryArmSettings();

		return (dictionary == null) ? null : new ScriptNode(dictionary, serviceRegistry, getScope());
	}

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
					propJson.put("title", attr.getTitle() != null ? attr.getTitle() : "");
					propJson.put("name", attrName);
					if (attr instanceof PropertyDefinition) {
						propJson.put("type", ((PropertyDefinition) attr).getDataType().getTitle());
					} else if (attr instanceof AssociationDefinition) {
						propJson.put("type", ((AssociationDefinition) attr).getTargetClass().getTitle());
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

	public ScriptNode getArmByCode(String code) {
		NodeRef arm = armService.getArmByCode(code);

		return (arm == null) ? null : new ScriptNode(arm, serviceRegistry, getScope());
	}
}
