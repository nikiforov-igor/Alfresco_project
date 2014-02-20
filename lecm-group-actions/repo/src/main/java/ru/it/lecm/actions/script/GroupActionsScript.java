package ru.it.lecm.actions.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.actions.bean.GroupActionsServiceImpl;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 19.02.14
 * Time: 16:56
 */
public class GroupActionsScript extends BaseWebScript {

    private GroupActionsServiceImpl actionsService;
    private DocumentService documentService;
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;

    public ScriptNode getHomeRef() {
        NodeRef home = actionsService.getHomeRef();
        return new ScriptNode(home, serviceRegistry, getScope());
    }

    public void setActionsService(GroupActionsServiceImpl actionsService) {
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
                AspectDefinition typeDef = dictionaryService.getAspect(aspectQName);
                String title = typeDef.getTitle();
                results.put(aspectQName.toPrefixString(namespaceService), title == null || title.equals("") ? aspect : title);
            }
        }

        return results;
    }

}
