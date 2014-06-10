package ru.it.lecm.base.scripts;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: PMelnikov Date: 12.04.13 Time: 16:43
 */
public class LecmRepositoryWebScriptBean extends BaseWebScript {

    private RepositoryStructureHelper repositoryStructureHelper;
    private ServiceRegistry serviceRegistry;

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * получение ссылки на корневую папку LECM если папки нет, то она создается
     *
     * @return
     */
    public ScriptNode getHomeRef() {
        return new ScriptNode(repositoryStructureHelper.getHomeRef(), serviceRegistry, getScope());
    }

    /**
     * получение ссылки на корневую папку для документов машины состояний если
     * папки нет, то она создается
     *
     * @return
     */
    public ScriptNode getDocumentsRef() {
        return new ScriptNode(repositoryStructureHelper.getDocumentsRef(), serviceRegistry, getScope());
    }

    /**
     * получение ссылки на папку "черновики" для указанного пользователя
     *
     * @param username логи пользователя
     * @return
     */
    public ScriptNode getDraftsRef(final String username) {
        ScriptNode node = null;
        try {
            node = new ScriptNode(repositoryStructureHelper.getDraftsRef(username), serviceRegistry, getScope());
        } catch (WriteTransactionNeededException ex) {
            throw new RuntimeException(ex);
        }
        return node;
    }

    /**
     * получение ссылки на папку "черновики" для указанного пользователя
     *
     * @param userRef ссылки на cm:person
     * @return
     */
    public ScriptNode getDraftsRef(final ScriptNode personRef) {
        ScriptNode node = null;
        try {
            node = new ScriptNode(repositoryStructureHelper.getDraftsRef(personRef.getNodeRef()), serviceRegistry, getScope());
        } catch (WriteTransactionNeededException ex) {
            throw new RuntimeException(ex);
        }
        return node;
    }

}
