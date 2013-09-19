package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;


/**
 * Класс отвечающий за создание структуры папок в хранилище
 * он создает папки с правильными правами на них
 * @author VLadimir Malygin
 * @since 01.03.2013 10:25:22
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public interface RepositoryStructureHelper {

	/**
	 * получение ссылки на корневую папку LECM
	 * если папки нет, то она создается
	 * @return
	 */
	NodeRef getHomeRef ();

	/**
	 * получение ссылки на корневую папку для документов машины состояний
	 * если папки нет, то она создается
	 * @return
	 */
	NodeRef getDocumentsRef ();

	/**
	 * получение ссылки на папку "черновики" для указанного пользователя
	 * @param username логи пользователя
	 * @return
	 */
	NodeRef getDraftsRef (final String username);

	/**
	 * получение ссылки на папку "черновики" для указанного пользователя
	 * @param userRef ссылки на cm:person
	 * @return
	 */
	NodeRef getDraftsRef (final NodeRef personRef);

    /**
     * получение ссылки на корневую папку Alfresco ECM
     * @return
     */
    NodeRef getCompanyHomeRef ();

	/**
	 * Получение папки пользователя для временного хранилища файлов, которая очищается по расписанию
	 * @param person пользователь
	 * @param createIfNotExist создавать если не найдена
	 * @return идентификатор папки временного хранилища файлов пользователя
	 */
	public NodeRef getUserTemp(NodeRef person, boolean createIfNotExist);

	/**
	 * Получение папки текущего пользователя для временного хранилища файлов, которая очищается по расписанию
	 * @param createIfNotExist создавать если не найдена
	 * @return идентификатор папки временного хранилища файлов пользователя
	 */
	public NodeRef getUserTemp(boolean createIfNotExist);
}
