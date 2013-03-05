package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.ServiceFolder;

/**
 * Класс отвечающий за создание структуры папок в хранилище
 * он создает папки с правильными правами на них
 * @author VLadimir Malygin
 * @since 01.03.2013 10:25:22
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public interface IRepositoryStructureHelper {

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
	 * получение ссылки на интересующую нас папку
	 * Если папка не существует, то она будет создана
	 * @param folder
	 * @return
	 */
	NodeRef getFolderRef (final ServiceFolder serviceFolder);
}
