package ru.it.lecm.notifications.beans;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 17:35
 *
 * Интерфейс для сервисов каналов уведомлений
 */
public interface NotificationChannelBeanBase {

	/**
	 * Отправка атомарного уведомления
	 *
	 * @param notification Атомарное уведомление
	 * @return true - если отправка успешна, false - если при отправки возникли ошибки
	 */
	public boolean sendNotification(NotificationUnit notification);
}
