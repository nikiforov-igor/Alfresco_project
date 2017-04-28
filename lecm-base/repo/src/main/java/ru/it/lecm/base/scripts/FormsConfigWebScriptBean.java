/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.base.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 *
 * @author ikhalikov
 */
public class FormsConfigWebScriptBean extends DeclarativeWebScript{

	private DictionaryService dictionaryService;
	private NamespaceService nameSpaceService;

	public void setNameSpaceService(NamespaceService nameSpaceService) {
		this.nameSpaceService = nameSpaceService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String typeName = req.getParameter("typeName");
		Map<String, Object> result = new HashMap<String, Object>();
		QName type = QName.createQName(typeName, nameSpaceService);
		List<String> parents = getParents(type);
		result.put("result", new JSONArray(parents));
		return result;
	}

	private List<String> getParents(QName name) {
		List<String> result = new ArrayList<>();
		TypeDefinition typeDef = dictionaryService.getType(name);
		if (typeDef != null) {
			QName parent = typeDef.getParentName();
			if(parent != null){
				result.add(parent.toPrefixString());
				result.addAll(getParents(parent));
			}
		}
		return result;
	}
}
