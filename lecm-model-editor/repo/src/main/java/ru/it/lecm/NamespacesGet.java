package ru.it.lecm;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.namespace.NamespaceService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class NamespacesGet extends DeclarativeWebScript {
	protected NamespaceService namespaceService;
	
	public void setNamespaceService(NamespaceService namespaceservice)
    {
        this.namespaceService = namespaceservice;
    }

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req,
			Status status, Cache cache) {
		Map<String, String> namespaces = new HashMap<String, String>();
		Map<String, Object> model = new HashMap<String, Object>();
		for(String prefix : namespaceService.getPrefixes()) {
			namespaces.put(prefix, namespaceService.getNamespaceURI(prefix));
		}
		
		model.put("namespaces", namespaces);
		return model;
	}
}
