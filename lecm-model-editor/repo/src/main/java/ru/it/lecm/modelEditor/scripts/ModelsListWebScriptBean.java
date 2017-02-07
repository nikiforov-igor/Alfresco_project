package ru.it.lecm.modelEditor.scripts;

import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.modelEditor.beans.ModelsListBeanImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * User: AIvkin
 * Date: 16.12.13
 * Time: 9:50
 */
public class ModelsListWebScriptBean extends BaseWebScript {
	private ModelsListBeanImpl modelListService;

	public void setModelListService(ModelsListBeanImpl modelListService) {
		this.modelListService = modelListService;
	}

	public JSONObject getModelsList(String parentType) {
		return modelListService.getModelsList(parentType);
	}
	
	public Map<String, String> getRegisteredTypes() {
		return  modelListService.getDocumentSubTypes();
	}
	
	public List<String> getCategories(String nodeRef, String documentType) {
		return modelListService.getCategories(nodeRef, documentType);
	}
	
	public JSONObject getAttrs(String nodeRef, String documentType) {
		return modelListService.getAttrs(nodeRef, documentType);
	}
	
	public JSONObject getAssocs(String nodeRef, String documentType) {
		return modelListService.getAssocs(nodeRef, documentType);
	}
	
	public JSONObject getAspects(String nodeRef, String documentType) {
		return modelListService.getAspects(nodeRef, documentType);
	}
	
	public JSONObject getModel(String documentType) {
		return modelListService.getModel(documentType);
	}
}
