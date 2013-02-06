package ru.it.lecm.delegation;

import ru.it.lecm.businessjournal.beans.EventCategory;

/**
 *
 * @author VLadimir Malygin
 * @since 06.02.2013 14:04:04
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public interface DelegationEventCategory extends EventCategory {

	/**
	 * Изменение параметров делегирования
	 */
	String CHANGE_DELEGATION_OPTS = "CHANGE_DELEGATION_OPTS";

	/**
	 * Делегирование части полномочий
	 */
	String START_DELEGATE = "START_DELEGATE";

	/**
	 * Делегирование всех полномочий
	 */
	String START_DELEGATE_ALL = "START_DELEGATE_ALL";

	/**
	 * Отмена делегирования полномочий
	 */
	String STOP_DELEGATE = "STOP_DELEGATE";
}
