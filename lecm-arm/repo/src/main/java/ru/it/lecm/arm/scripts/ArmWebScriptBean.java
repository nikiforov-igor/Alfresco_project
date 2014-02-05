package ru.it.lecm.arm.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
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
import java.util.HashSet;
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

		Set<PropertyDefinition> allProperties = new HashSet<PropertyDefinition>();
		Set<AssociationDefinition> allAssociations = new HashSet<AssociationDefinition>();

		addTypeFields(DocumentService.TYPE_BASE_DOCUMENT, allProperties, allAssociations);

		JSONObject result = new JSONObject();
		try {
			JSONArray fieldsJson = new JSONArray();

			for (PropertyDefinition prop: allProperties) {
				String propName = prop.getName().toPrefixString(namespaceService);
				if (!propName.endsWith("-ref") && !propName.endsWith("-text-content")) {
					JSONObject propJson = new JSONObject();
					propJson.put("title", prop.getTitle());
					propJson.put("name", propName);
					propJson.put("type", prop.getDataType().getTitle());

					fieldsJson.put(propJson);
				}
			}

			for (AssociationDefinition assoc: allAssociations) {
				JSONObject assocJson = new JSONObject();
				assocJson.put("title", assoc.getTitle());
				assocJson.put("name", assoc.getName().toPrefixString(namespaceService));
				assocJson.put("type", assoc.getTargetClass().getTitle());

				fieldsJson.put(assocJson);
			}

			result.put("items", fieldsJson);
		} catch (JSONException e) {
			logger.error("Error create jsonObject", e);
		}
		return result;
	}

	private void addTypeFields(QName typeQName, Set<PropertyDefinition> properties, Set<AssociationDefinition> associations) {
		TypeDefinition type = dictionaryService.getType(typeQName);
		properties.addAll(type.getProperties().values());
		associations.addAll(type.getAssociations().values());

		Collection<QName> subTypes = dictionaryService.getSubTypes(typeQName, false);
		if (subTypes != null) {
			for (QName subType: subTypes) {
				addTypeFields(subType, properties, associations);
			}
		}
	}
}
