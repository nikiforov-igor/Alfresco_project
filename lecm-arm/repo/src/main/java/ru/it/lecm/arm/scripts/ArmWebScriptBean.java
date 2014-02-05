package ru.it.lecm.arm.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.*;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

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

	public void setArmService(ArmService armService) {
		this.armService = armService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public ScriptNode getDictionaryArmSettings() {
		NodeRef dictionary = armService.getDictionaryArmSettings();

		return (dictionary == null) ? null : new ScriptNode(dictionary, serviceRegistry, getScope());
	}

	public JSONObject getAvailableNodeFields(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		Set<ClassAttributeDefinition> attributes = new LinkedHashSet<ClassAttributeDefinition>();
		addTypeFields(DocumentService.TYPE_BASE_DOCUMENT, attributes);

		JSONObject result = new JSONObject();
		try {
			JSONArray fieldsJson = new JSONArray();

			for (ClassAttributeDefinition attr: attributes) {
				String attrName = attr.getName().toPrefixString(namespaceService);
				if (!attrName.endsWith("-ref") && !attrName.endsWith("-text-content")) {
					JSONObject propJson = new JSONObject();
					propJson.put("title", attr.getTitle());
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

	private void addTypeFields(QName typeQName, Set<ClassAttributeDefinition> attributes) {
		TypeDefinition type = dictionaryService.getType(typeQName);
		attributes.addAll(type.getProperties().values());
		attributes.addAll(type.getAssociations().values());

		Collection<QName> subTypes = dictionaryService.getSubTypes(typeQName, false);
		if (subTypes != null) {
			for (QName subType: subTypes) {
				addTypeFields(subType, attributes);
			}
		}
	}
}
