package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
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
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.documents.constraints.ArmUrlConstraint;
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
	private DictionaryService dictionaryService;

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

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
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

    public Map<String, String> getFilters(String type, boolean forArchive){
        if (!forArchive) {
            return DocumentStatusesFilterBean.getFilterForType(type);
        } else {
            return DocumentStatusesFilterBean.getArchiveFilterForType(type);
        }
    }

    public Map<String, String> getDefaultFilter(String type, boolean forArchive) {
        Map<String, String> filters = getFilters(type, forArchive);
        HashMap<String, String> defaultFilter = new HashMap<String, String>();
        String defaultKey = DocumentStatusesFilterBean.getDefaultFilter(type, forArchive);
        if (filters != null) {
            if (defaultKey != null) {
                defaultFilter.put(defaultKey, filters.get(defaultKey));
            } else {
                for (Map.Entry<String, String> entry : filters.entrySet()) {
                    defaultFilter.put(entry.getKey(), entry.getValue());
                    break;
                }
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
    public Long getAmountDocuments(Scriptable types, Scriptable paths, Scriptable statuses, String filterStr) {
        List<String> docTypes = getElements(Context.getCurrentContext().getElements(types));
        List<QName> qNameTypes = new ArrayList<QName>();
        String queryFilter = "";

        if (filterStr != null && !filterStr.isEmpty()) {
            queryFilter = getFilterQuery(filterStr);
        }

        for (String docType : docTypes) {
            qNameTypes.add(QName.createQName(docType, namespaceService));
        }
        return documentService.getAmountDocumentsByFilter(qNameTypes,
                getElements(Context.getCurrentContext().getElements(paths)), getElements(Context.getCurrentContext().getElements(statuses)), queryFilter, null);
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
        if (qNameType != null) {
            return documentService.getAuthorProperty(qNameType);
        }
        return null;
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


	/**
	 * Оборачиваем узел в ссылку на document
	 * @param node
	 * @param description
	 * @return
	 */
	public String wrapperDocumentLink(ScriptNode node, String description) {
		return wrapperLink(node.getNodeRef().toString(), description, BaseBean.DOCUMENT_LINK_URL);
	}

    public String getFilterQuery(String filter) {
        String[] filterParts = filter.split("\\|");
        if (filterParts.length > 0) {
            String filterId = filterParts[0];
            String filterData = "";
            if (filterParts.length == 2) {
                filterData = filterParts[1];
            }

            DocumentFilter docFilter = FiltersManager.getFilterById(filterId);
            if (docFilter != null) {
                if (filterData.isEmpty()){
                    filterData = docFilter.getParamStr();
                }
                Object[] params = filterData != null ? filterData.split("/") : new Object[]{};
                return docFilter.getQuery(params);
            }
        } else {
            logger.warn("Check filter code!!! Filter=" + filter);
        }
        return "";
    }

    public ScriptNode dublicateDocument(String nodeRef) {
        ParameterCheck.mandatory("nodeRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        if (nodeService.exists(ref)) {
            NodeRef createdNode = documentService.duplicateDocument(ref);
            if (createdNode != null) {
                return new ScriptNode(createdNode, serviceRegistry, getScope());
            }
        }
        return null;
    }

	public String getArmUrl(String nodeRef) {
        ParameterCheck.mandatory("nodeRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        if (nodeService.exists(ref)) {
	        QName type = nodeService.getType(ref);
	        if (type != null) {
		        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_ARM_URL));
		        if (constraint != null && (constraint.getConstraint() instanceof ArmUrlConstraint)) {
			        return ((ArmUrlConstraint) constraint.getConstraint()).getArmUrl();
		        }
	        }
        }
        return null;
    }

    public String getPresentString(ScriptNode document) {
        return documentService.getPresentString(document.getNodeRef());
    }

    /**
     * Возвращает автора документа
     * @param document - текстовая ссылка на документ
     * @return ноду сотрудника-автор или null, если по какой-то причине нет такого свойства или сохранены битые данные
     */
    public ScriptNode getDocumentAuthor(String document) {
        ParameterCheck.mandatory("document", document);
        if (NodeRef.isNodeRef(document)) {
            NodeRef ref = new NodeRef(document);
            if (nodeService.exists(ref)) {
                NodeRef author = documentService.getDocumentAuthor(ref);
                return new ScriptNode(author, serviceRegistry, getScope());
            }
        }
        return null;
    }

	public String getDocumentRegNumber(ScriptNode document) {
		ParameterCheck.mandatory("document", document);
		if (document != null) {
			return documentService.getDocumentRegNumber(document.getNodeRef());
		}
		return null;
	}

    public void finalizeToUnit(ScriptNode document, Boolean sharedFolder, ScriptNode primaryUnit, Scriptable additionalUnits) {
        List<NodeRef> additionalUnitsRefs = getNodeRefsFromScriptableCollection(additionalUnits);
        documentService.finalizeToUnit(document.getNodeRef(), sharedFolder, primaryUnit.getNodeRef(), additionalUnitsRefs);
    }

    public void finalizeToUnit(ScriptNode document, Boolean sharedFolder, ScriptNode primaryUnit) {
        finalizeToUnit(document, sharedFolder, primaryUnit, null);
    }

    public void finalizeToUnit(ScriptNode document, ScriptNode primaryUnit, Scriptable additionalUnits) {
        finalizeToUnit(document, null, primaryUnit, additionalUnits);
    }

    public void finalizeToUnit(ScriptNode document, ScriptNode primaryUnit) {
        finalizeToUnit(document, null, primaryUnit, null);
    }
}
