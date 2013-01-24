package ru.it.lecm.notifications.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 17:35
 * <p/>
 * Интерфейс для сервисов каналов уведомлений
 */
public abstract class NotificationChannelBeanBase extends BaseBean {

	/**
	 * Отправка атомарного уведомления
	 *
	 * @param notification Атомарное уведомление
	 * @return true - если отправка успешна, false - если при отправки возникли ошибки
	 */
	public abstract boolean sendNotification(NotificationUnit notification);

	/**
	 * Метод, возвращающий ссылку на директорию в директории "Уведомления" согласно заданным параметрам
	 *
	 * @param date               - текущая дата
	 * @param employeeName       - имя сотрудника
	 * @param root               - корень, относительно которого строится путь
	 * @return ссылка на директорию
	 */
	public NodeRef getFolder(final NodeRef root, final String employeeName, final Date date) {
		List<String> directoryPaths = new ArrayList<String>(3);
		if (employeeName != null) {
			directoryPaths.add(employeeName);
		}
		directoryPaths.addAll(getDateFolderPath(date));
		return getFolder(root, directoryPaths);
	}

	/**
	 * Метод, возвращающий ссылку на директорию в директории "Уведомления" согласно заданным параметрам
	 *
	 * @param employeeName       - имя сотрудника
	 * @param root               - корень, относительно которого строится путь
	 * @return ссылка на директорию
	 */
	public NodeRef getFolder(final NodeRef root, final String employeeName) {
		List<String> directoryPaths = new ArrayList<String>(3);
		if (employeeName != null) {
			directoryPaths.add(employeeName);
		}
		return getFolder(root, directoryPaths);
	}
}
