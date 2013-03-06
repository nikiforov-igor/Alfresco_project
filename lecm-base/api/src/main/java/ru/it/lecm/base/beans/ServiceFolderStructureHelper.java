package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.ServiceFolder;

/**
 * вспомогательный интерфейс который добавляет возможность
 * получения (создания) папки для какого-либо сервиса (функционального модуля)
 * поскольку вся работа с папками ведется через BaseBean,
 * этот интерфейс не является публичным.
 * @author VLadimir Malygin
 * @since 06.03.2013 15:25:55
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public interface ServiceFolderStructureHelper extends RepositoryStructureHelper {

	/**
	 * получение ссылки на интересующую нас папку
	 * Если папка не существует, то она будет создана
	 * @param folder
	 * @return
	 */
	NodeRef getFolderRef (final ServiceFolder serviceFolder);
}
