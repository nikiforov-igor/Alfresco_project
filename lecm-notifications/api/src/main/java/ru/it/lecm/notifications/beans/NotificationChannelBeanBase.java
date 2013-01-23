package ru.it.lecm.notifications.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 17:35
 * <p/>
 * Интерфейс для сервисов каналов уведомлений
 */
public interface NotificationChannelBeanBase {
	final DateFormat FolderNameFormatYear = new SimpleDateFormat("yyyy");
	final DateFormat FolderNameFormatMonth = new SimpleDateFormat("MM");
	final DateFormat FolderNameFormatDay = new SimpleDateFormat("DD");

	/**
	 * Отправка атомарного уведомления
	 *
	 * @param notification Атомарное уведомление
	 * @return true - если отправка успешна, false - если при отправки возникли ошибки
	 */
	public boolean sendNotification(NotificationUnit notification);
}
