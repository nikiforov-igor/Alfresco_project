package ru.it.lecm.actions.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.*;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import ru.it.lecm.actions.bean.GroupActionsService;

/**
 * User: pmelnikov
 * Date: 19.02.14
 * Time: 16:56
 */
public class GroupActionsScript extends BaseWebScript {

    private GroupActionsService actionsService;
    private DocumentService documentService;
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;

    final private static Logger logger = LoggerFactory.getLogger(GroupActionsScript.class);

    public ScriptNode getHomeRef() {
        NodeRef home = actionsService.getHomeRef();
        return new ScriptNode(home, serviceRegistry, getScope());
    }

    public void setActionsService(GroupActionsService actionsService) {
        this.actionsService = actionsService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public Map<String, String> getTypes() {
        Map<String, String> results = new HashMap<String, String>();

        Collection<QName> types = documentService.getDocumentSubTypes();
        for (QName type : types) {
            TypeDefinition typeDef = dictionaryService.getType(type);
            results.put(type.toPrefixString(namespaceService), typeDef.getTitle());
        }

        List<String> aspects = actionsService.getAspects();
        for (String aspect : aspects) {
            QName aspectQName = QName.createQName(aspect, namespaceService);
            if (aspectQName != null) {
                ClassDefinition typeDef = dictionaryService.getClass(aspectQName);
                String title = typeDef.getTitle(dictionaryService);
                results.put(aspectQName.toPrefixString(namespaceService), title == null || title.equals("") ? aspect : title);
            }
        }

        return results;
    }

    public Scriptable getActiveGroupActions(String jsonItems, boolean group) {
        List<NodeRef> forItems = new ArrayList<NodeRef>();
        try {
            JSONArray array = new JSONArray(jsonItems);
            for (int i = 0; i < array.length(); i++) {
                if (NodeRef.isNodeRef(array.getString(i))) {
                    forItems.add(new NodeRef(array.getString(i)));
                }
            }
        } catch (JSONException e) {
            logger.error("Error while parsing input data", e);
        }
        return createScriptable(actionsService.getActiveGroupActions(forItems, group));
    }

}
