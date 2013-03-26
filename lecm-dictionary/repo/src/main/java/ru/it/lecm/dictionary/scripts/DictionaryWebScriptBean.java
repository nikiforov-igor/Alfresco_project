package ru.it.lecm.dictionary.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.List;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public class DictionaryWebScriptBean extends BaseWebScript {

    private DictionaryBean dictionaryService;

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public ScriptNode getDictionaryByName(String name) {
        NodeRef dictionary = dictionaryService.getDictionaryByName(name);

        return (dictionary == null) ? null : new ScriptNode(dictionary, serviceRegistry, getScope());
    }

    public Scriptable getChildren(String parent) {
        List<NodeRef> children = dictionaryService.getChildren(new NodeRef(parent));

        return createScriptable(children);
    }

}
