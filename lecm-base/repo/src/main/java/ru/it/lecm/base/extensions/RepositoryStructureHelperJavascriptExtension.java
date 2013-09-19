package ru.it.lecm.base.extensions;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.RepositoryStructureHelper;

/**
 *
 * @author VLadimir Malygin
 * @since 12.03.2013 16:12:37
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class RepositoryStructureHelperJavascriptExtension extends BaseScopableProcessorExtension {

	private ServiceRegistry serviceRegistry;
	private RepositoryStructureHelper repositoryStructureHelper;

	public void setServiceRegistry (ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryStructureHelper (RepositoryStructureHelper repositoryStructureHelper) {
		this.repositoryStructureHelper = repositoryStructureHelper;
	}

	/**
	 * получение ссылки на корневую папку LECM
	 * если папки нет, то она создается
	 * @return
	 */
	public ScriptNode getHomeRef () {
		return new ScriptNode (repositoryStructureHelper.getHomeRef (), serviceRegistry, getScope ());
	}

	/**
	 * получение ссылки на корневую папку для документов машины состояний
	 * если папки нет, то она создается
	 * @return
	 */
	public ScriptNode getDocumentsRef () {
		return new ScriptNode (repositoryStructureHelper.getDocumentsRef (), serviceRegistry, getScope ());
	}

	/**
	 * получение ссылки на папку "черновики" для указанного пользователя
	 * @param username логи пользователя
	 * @return
	 */
	public ScriptNode getDraftsRef (final String username) {
		return new ScriptNode (repositoryStructureHelper.getDraftsRef (username), serviceRegistry, getScope ());
	}

	/**
	 * получение ссылки на папку "черновики" для указанного пользователя
	 * @param userRef ссылки на cm:person
	 * @return
	 */
	public ScriptNode getDraftsRef (final ScriptNode personRef) {
		return new ScriptNode (repositoryStructureHelper.getDraftsRef (personRef.getNodeRef ()), serviceRegistry, getScope ());
	}

	/**
	 * Получение папки пользователя для временного хранилища файлов, которая очищается по расписанию
	 * @return папка временного хранилища файлов пользователя
	 */
	public ScriptNode getUserTemp() {
		return new ScriptNode(repositoryStructureHelper.getUserTemp(true), serviceRegistry, getScope());
	}
}
