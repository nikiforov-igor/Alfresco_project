package ru.it.lecm.documents.scripts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentStatusesFilterBean;
import ru.it.lecm.security.LecmPermissionService;

/**
 * User: orakovskaya
 * Date: 12.03.13
 */
public class DocumentWebScriptBean extends BaseWebScript {
    private static final Logger logger = LoggerFactory.getLogger(DocumentWebScriptBean.class);

    private DocumentService documentService;
	private NodeService nodeService;
    private LecmPermissionService lecmPermissionService;
    private NamespaceService namespaceService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public LecmPermissionService getLecmPermissionService() {
		return lecmPermissionService;
	}

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public String getRating(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getRating(documentRef);
        }
        return null;
    }
 
    public Integer getRatedPersonCount(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getRatedPersonCount(documentRef);
        }
        return null;
    }
 
    public Integer getMyRating(String documentNodeRef) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.getMyRating(documentRef);
        }
        return null;
    }

    public Integer setMyRating(String documentNodeRef, String rating) {
        ParameterCheck.mandatory("documentNodeRef", documentNodeRef);

        NodeRef documentRef = new NodeRef(documentNodeRef);
        if (this.nodeService.exists(documentRef)) {
            return this.documentService.setMyRating(documentRef, Integer.parseInt(rating));
        }
        return null;
    }

    public String getProperties(String nodeRef) {
        NodeRef documentRef = new NodeRef(nodeRef);
        JSONArray properties = new JSONArray();
        JSONObject object = new JSONObject();
        Map<QName, Serializable> data = documentService.getProperties(documentRef);
        try {
            for (Map.Entry<QName, Serializable> e: data.entrySet() ) {
                object.put(e.getKey().getLocalName(), e.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return properties.put(object).toString();
    }

    public ScriptNode createDocument(String type, Scriptable properties) {
        Map<String, String> property = takeProperties(Context.getCurrentContext().getElements(properties));
        NodeRef documentRef = documentService.createDocument(type, property);

        return new ScriptNode(documentRef, serviceRegistry, getScope());
    }

    public ScriptNode editDocument(String nodeRef, Scriptable properties) {
        NodeRef documentRef = new NodeRef(nodeRef);
        Map<String, String> property = takeProperties(Context.getCurrentContext().getElements(properties));
        documentRef = documentService.editDocument(documentRef, property);

        if (logger.isInfoEnabled()) {
        	final StringBuilder sb = lecmPermissionService.trackAllLecmPermissions("Before edit document permissions:", documentRef, new String[]{null});
        	logger.info(sb.toString());
        }

        return new ScriptNode(documentRef, serviceRegistry, getScope());
    }

    public ScriptNode getDraftsRoot() {
        return new ScriptNode(documentService.getDraftRoot(), serviceRegistry, getScope());
    }

    public String getDraftsPath(){
        return documentService.getDraftPath();
    }

    public String getDocumentsPath(){
        return documentService.getDocumentsFolderPath();
    }

    public ScriptNode getDraftRoot(String rootName) {
        ParameterCheck.mandatory("rootName", rootName);
        return new ScriptNode(documentService.getDraftRoot(rootName), serviceRegistry, getScope());
    }

    public String getDraftPath(String rootName) {
        ParameterCheck.mandatory("rootName", rootName);
        NodeRef draftRef = documentService.getDraftRoot(rootName);
        return nodeService.getPath(draftRef).toPrefixString(namespaceService);
    }

    private Map<String, String> takeProperties(Object[] object){
        Map<String, String> map =  new HashMap<String, String>();
        String[] string;
        String value = "";
        for (Object obj : object) {
            string = obj.toString().split("=");
            value = (string.length < 2) ? "" : string[1];
            map.put(string[0],value);
        }
        return map;
    }

    public Map<String, String> getFilters(String type){
        return DocumentStatusesFilterBean.getFilterForType(type);
    }

    public Map<String, String> getDefaultFilter(String type) {
        Map<String, String> filters = getFilters(type);
        HashMap<String, String> defaultFilter = new HashMap<String, String>();
        String defaultKey = DocumentStatusesFilterBean.getDefaultFilter(type);
        defaultFilter.put(defaultKey, filters.get(defaultKey));
        return defaultFilter;
    }

    /**
     * Получить количество участников для данного типа документа
     * @return
     */
    public Integer getAmountMembers(String type) {
        QName qNameType = QName.createQName(type, namespaceService);
        return documentService.getMembers(qNameType).size();
    }

    /**
     * Получить количество документов
     * @param path список путей поиска
     * @param statuses список значений для фильтрации
     * @return количество
     */
    public Integer getAmountDocuments(Scriptable types, Scriptable paths, Scriptable statuses) {
        List<String> docTypes = getElements(Context.getCurrentContext().getElements(types));
        List<QName> qNameTypes = new ArrayList<QName>();
        for (String docType : docTypes) {
            qNameTypes.add(QName.createQName(docType, namespaceService));
        }
        return documentService.getDocuments(qNameTypes, getElements(Context.getCurrentContext().getElements(paths)), getElements(Context.getCurrentContext().getElements(statuses))).size();
    }

    private ArrayList<String> getElements(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj instanceof String){
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }
}
