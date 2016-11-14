package ru.it.lecm.base.extensions;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 *
 * @author VLadimir Malygin
 * @since 12.03.2013 16:12:37
 * @see
 * <p>
 * mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class RepositoryStructureHelperJavascriptExtension extends BaseScopableProcessorExtension {

    private ServiceRegistry serviceRegistry;
    private RepositoryStructureHelper repositoryStructureHelper;
	private LecmTransactionHelper lecmTransactionHelper;

	public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
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
        try {
            return new ScriptNode(repositoryStructureHelper.getDraftsRef(username), serviceRegistry, getScope());
        } catch (WriteTransactionNeededException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * получение ссылки на папку "черновики" для указанного пользователя
     *
     * @param userRef ссылки на cm:person
     * @return
     */
    public ScriptNode getDraftsRef(final ScriptNode personRef) {
        try {
            return new ScriptNode(repositoryStructureHelper.getDraftsRef(personRef.getNodeRef()), serviceRegistry, getScope());
        } catch (WriteTransactionNeededException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Получение папки пользователя для временного хранилища файлов, которая
     * очищается по расписанию
     *
     * @return папка временного хранилища файлов пользователя
     */
    public ScriptNode getUserTemp() {
        try {
			NodeRef userTemp = repositoryStructureHelper.getUserTemp(true);
			if(userTemp == null) {
				userTemp = lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>(){

					@Override
					public NodeRef execute() throws Throwable {
						// TODO: Зачем убрали транзакцию?
						return repositoryStructureHelper.createUserTemp();
					}

				});
			}
            return new ScriptNode(userTemp, serviceRegistry, getScope());
        } catch (WriteTransactionNeededException ex) {
            throw new RuntimeException(ex);
        }
    }
}
