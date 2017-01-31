package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Date;
import java.util.List;

/**
 * Класс отвечающий за создание структуры папок в хранилище он создает папки с
 * правильными правами на них
 *
 * @author VLadimir Malygin
 * @since 01.03.2013 10:25:22
 * @see
 * <p>
 * mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public interface RepositoryStructureHelper {

    /**
     * получение ссылки на корневую папку LECM если папки нет, то она создается
     *
     * @return
     */
    NodeRef getHomeRef();

    /**
     * получение ссылки на корневую папку для документов машины состояний если
     * папки нет, то она создается
     *
     * @return
     */
    NodeRef getDocumentsRef();

    /**
     * получение ссылки на папку "черновики" для указанного пользователя
     *
     * @param username логи пользователя
     * @return
     * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
     */
    NodeRef getDraftsRef(final String username) throws WriteTransactionNeededException;

    /**
     * получение ссылки на папку "черновики" для указанного пользователя
     *
     * @param personRef
     * @return
     * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
     */
    NodeRef getDraftsRef(final NodeRef personRef) throws WriteTransactionNeededException;

    /**
     * получение ссылки на корневую папку Alfresco ECM
     *
     * @return
     */
    NodeRef getCompanyHomeRef();

    /**
     * Получение папки пользователя для временного хранилища файлов, которая
     * очищается по расписанию
     *
     * @param person пользователь
     * @param createIfNotExist создавать если не найдена
     * @return идентификатор папки временного хранилища файлов пользователя
     * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
     */
    public NodeRef getUserTemp(NodeRef person, boolean createIfNotExist) throws WriteTransactionNeededException;

    /**
     * Получение папки текущего пользователя для временного хранилища файлов,
     * которая очищается по расписанию
     *
     * @param createIfNotExist создавать если не найдена
     * @return идентификатор папки временного хранилища файлов пользователя
     * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
     */
    public NodeRef getUserTemp(boolean createIfNotExist) throws WriteTransactionNeededException;
	/**
	 * Создание папки для временного хранения файлов для текущего пользователя
	 * @param person
	 * @return
	 * @throws WriteTransactionNeededException
	 */
	public NodeRef createUserTemp(NodeRef person) throws WriteTransactionNeededException;

	public NodeRef createUserTemp() throws WriteTransactionNeededException;

    /**
     * создаем папку у указанного родителя
     *
     * @param parentRef ссылка на родителя
     * @param folder имя папки без слешей и прочего
     * @return NodeRef свежесозданной папки
     * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
     */
    NodeRef createFolder(final NodeRef parentRef, final String folder) throws WriteTransactionNeededException;

    /**
     * Создаёт папки по пути указанному в directoryPaths.
     *
     * @param nameSpace
     * @param root ссылка на родителя
     * @param directoryPaths путь для создания
     * @return
     * @throws WriteTransactionNeededException
     */
    NodeRef createPath(String nameSpace, NodeRef root, List<String> directoryPaths) throws WriteTransactionNeededException;

    NodeRef getFolder( NodeRef parentRef, String folder);

    List<String> getDateFolderPath(Date date);
	
	/**
	 * Возвращает текстовое представление пути Business Platform/LECM
	 * @return 
	 */
	String getServicesHomePath();
}
