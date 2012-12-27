package ru.it.lecm.dictionary.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.List;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public class DictionaryWebScriptBean extends BaseScopableProcessorExtension {
    /**
     * Service registry
     */
    protected ServiceRegistry services;
    private DictionaryBean dictionaryService;

    /**
     * Set the service registry
     *
     * @param services the service registry
     */
    public void setServiceRegistry(ServiceRegistry services) {
        this.services = services;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public ScriptNode getDictionaryByName(String name) {
        NodeRef dictionary = dictionaryService.getDictionaryByName(name);

        return (dictionary == null) ? null : new ScriptNode(dictionary, services, getScope());
    }

    public Scriptable getChildren(String parent) {
        List<NodeRef> children = dictionaryService.getChildren(new NodeRef(parent));

        return createScriptable(children);
    }

    /**
     * Возвращает массив, пригодный для использования в веб-скриптах
     *
     * @return Scriptable
     */
    private Scriptable createScriptable(List<NodeRef> refs) {
        Object[] results = new Object[refs.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new ScriptNode(refs.get(i), services, getScope());
        }
        return Context.getCurrentContext().newArray(getScope(), results);
    }
}
