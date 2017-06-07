package ru.it.lecm.user.profile.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONObject;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * User: dbashmakov
 * Date: 05.06.2017
 * Time: 9:05
 */
public interface UserSettingsService {
    String USER_SETTINGS_ID = "USER_SETTINGS_ID";
    String DEFAULT_ROOT = "default";

    /**
     * Получить настройки пользователя
     *
     * @param category - категория настроек
     * @return ID ноды с настройками или NULL - если ноды с настройками не существует
     */
    NodeRef getUserSettingsFile(final String category) throws WriteTransactionNeededException;

    /**
     * Получить значение сохраненной настройки
     *
     * @param key  - ключ настройки с префиксом-категорией. Формат: <код_категории>.<ключ_настройки>
     * @return сохраненное значение
     */
    JSONObject getSettings(final String key);

    /**
     * Получить сохраненную настройку
     *
     * @param category - категория настроек
     * @param key      - ключ настройки
     * @return сохраненное значение или NULL, если настройка не задана
     */
    JSONObject getSettings(String category, String key);

    /**
     * Сохранить настройку для пользователя
     *  @param key   - ключ настройки с префиксом-категорией. Формат: <код_категории>.<ключ_настройки>
     * @param value - значение для сохранения
     */
    boolean setSettings(String key, Object value);

    /**
     * Сохранить настройку для пользователя
     *  @param category - категория/раздел для сохранения
     * @param key      - ключ настройки
     * @param value    - значение для сохранения
     */
    boolean setSettings(final String category, final String key, final Object value) throws WriteTransactionNeededException;

    /**
     * Удалить настройку для пользователя
     *
     * @param key  - ключ настройки с префиксом-категорией. Формат: <код_категории>.<ключ_настройки>
     */
    boolean deleteSettings(final String key);

    /**
     * Удалить настройку для пользователя
     *  @param category - категория/раздел для сохранения
     * @param key      - ключ настройки
     */
    boolean deleteSettings(final String category, final String key) throws WriteTransactionNeededException;
}
