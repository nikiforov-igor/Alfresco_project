package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: orakovskaya
 * Date: 12.03.13
 */
public class DocumentWebScriptBean extends BaseScopableProcessorExtension {
    private DocumentService documentService;
    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;
    private static final Logger logger = LoggerFactory.getLogger(DocumentWebScriptBean.class);

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
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

    /**
     * Возвращает массив, пригодный для использования в веб-скриптах
     *
     * @return Scriptable
     */
    private Scriptable createScriptable(List<NodeRef> refs) {
        Object[] results = new Object[refs.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new ScriptNode(refs.get(i), this.serviceRegistry, getScope());
        }
        return Context.getCurrentContext().newArray(getScope(), results);
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

}
