package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
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
import ru.it.lecm.documents.beans.DocumentsPermissionsBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: orakovskaya
 * Date: 12.03.13
 */
public class DocumentWebScriptBean extends BaseWebScript {
    private static final Logger logger = LoggerFactory.getLogger(DocumentWebScriptBean.class);

    private DocumentService documentService;
	private NodeService nodeService;
    private NamespaceService namespaceService;
    private PreferenceService preferenceService;

    private AuthenticationService authService;
    private OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
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

    public ScriptNode createDocument(String type, Scriptable properties, Scriptable associations) {
        Map<String, String> mapProperties = new HashMap<String, String>();
        Map<String, String> mapAssociation = new HashMap<String,String>();
        if (properties != null) {
            mapProperties = takeProperties(Context.getCurrentContext().getElements(properties));
        }
        if (associations != null) {
            mapAssociation = takeProperties(Context.getCurrentContext().getElements(associations));
        }

        NodeRef documentRef = documentService.createDocument(type, mapProperties, mapAssociation);

        return new ScriptNode(documentRef, serviceRegistry, getScope());
    }

    public ScriptNode editDocument(String nodeRef, Scriptable properties) {
      return editDocument(nodeRef, Context.getCurrentContext().getElements(properties));
    }

	public ScriptNode editDocument(String nodeRef, Object[] properties) {
		NodeRef documentRef = new NodeRef(nodeRef);
		Map<String, String> property = takeProperties(properties);
		documentRef = documentService.editDocument(documentRef, property);
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

    public ScriptNode getDraftRoot(String rootType) {
        ParameterCheck.mandatory("rootType", rootType);
        QName rootQNameType = QName.createQName(rootType, namespaceService);
        if (rootQNameType != null) {
            return new ScriptNode(documentService.getDraftRootByType(rootQNameType), serviceRegistry, getScope());
        } else {
            return null;
        }
    }

    public String getDraftPath(String rootType) {
        ParameterCheck.mandatory("rootType", rootType);
        QName rootQNameType = QName.createQName(rootType, namespaceService);
        if (rootQNameType != null) {
            return documentService.getDraftPathByType(rootQNameType);
        }
        return null;
    }

    private Map<String, String> takeProperties(Object[] object){
        List<String> list = getElements(object);
        Map<String, String> map =  new HashMap<String, String>();
        String[] string;
        String value;
        for (String str : list) {
            if (!str.equals("")) {
                string = str.split("=");
                value = (string.length < 2) ? "" : string[1];
                map.put(string[0],value);
            }
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
        if (filters != null) {
            if (defaultKey != null) {
                defaultFilter.put(defaultKey, filters.get(defaultKey));
            }
        } else {
            defaultFilter.put(defaultKey, "*");
        }
        return defaultFilter;
    }

    /**
     * Получить количество участников для данного типа документа
     * @return список nodeRef
     */
    public Integer getAmountMembers(String type) {
        QName qNameType = QName.createQName(type, namespaceService);
        return documentService.getMembers(qNameType).size();
    }

    /**
     * Получить количество документов
     * @return количество
     */
    public Integer getAmountDocuments(Scriptable types, Scriptable paths, Scriptable statuses, boolean considerFilter) {
        List<String> docTypes = getElements(Context.getCurrentContext().getElements(types));
        List<QName> qNameTypes = new ArrayList<QName>();
        for (String docType : docTypes) {
            qNameTypes.add(QName.createQName(docType, namespaceService));
        }

        Map<QName, List<NodeRef>> employeesMap = null;
        if (considerFilter) {
            employeesMap = new HashMap<QName, List<NodeRef>>();
            String username = authService.getCurrentUserName();
            if (username != null) {
                NodeRef currentEmployee = orgstructureService.getEmployeeByPerson(username);
                for (QName type : qNameTypes) {
                    String typeStr = type.toPrefixString(namespaceService).replace(":","_");
                    Map<String, Serializable> typePrefs = preferenceService.getPreferences(username, DocumentService.PREF_DOCUMENTS + "." + typeStr);
                    Serializable key = typePrefs.get(DocumentService.PREF_DOCUMENTS + "." + typeStr + DocumentService.PREF_DOC_LIST_AUTHOR);
                    String filterKey = key != null ? (String)key : null;
                    List<NodeRef> employees = new ArrayList<NodeRef>();
                    if (filterKey != null) {
                        switch(DocumentService.AuthorEnum.valueOf(filterKey.toUpperCase())) {
                            case MY : {
                                employees.add(currentEmployee);
                                break;
                            }
                            case DEPARTMENT: {
                                List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(currentEmployee);
                                employees.addAll(departmentEmployees);
                                //departmentEmployees.add(employee);
                                break;
                            }
                            case FAVOURITE: {
                                break;
                            }
                            case ALL: {
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                    if (employees.size() > 0) {
                        employeesMap.put(type, employees);
                    }
                }
            }

        }
        return documentService.getDocumentsByFilter(qNameTypes, null, null, null,
                getElements(Context.getCurrentContext().getElements(paths)), getElements(Context.getCurrentContext().getElements(statuses)), employeesMap, null).size();
    }

    public List<String> getAccessPermissionsList(String type) {
        return DocumentsPermissionsBean.getPermissionsByType(type);
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
	        logger.error(e.getMessage(), e);
        }
        return properties.put(object).toString();
    }

    public String getAuthorProperty(String docType) {
        QName qNameType = QName.createQName(docType, namespaceService);
        return documentService.getAuthorProperty(qNameType);
    }

    private ArrayList<String> getElements(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj != null && obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj != null && obj instanceof String){
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }
}
