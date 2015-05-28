package ru.it.lecm.search.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.search.beans.SearchEditorService;

/**
 * User: DBashmakov
 * Date: 08.05.2015
 * Time: 14:21
 */
public class SearchEditorWebScript extends BaseWebScript {
    private SearchEditorService searchQueriesService;

    public void setSearchQueriesService(SearchEditorService searchQueriesService) {
        this.searchQueriesService = searchQueriesService;
    }

    public ScriptNode getStore() {
        NodeRef rootFolder = searchQueriesService.getStoreFolder();
        return new ScriptNode(rootFolder, serviceRegistry, getScope());
    }

}
