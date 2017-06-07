package ru.it.lecm.user.profile.scripts;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.user.profile.beans.UserSettingsService;

/**
 * User: dbashmakov
 * Date: 30.05.2017
 * Time: 16:40
 */
public class UserSettingsWebScriptBean extends BaseWebScript {
    private UserSettingsService service;

    public void setService(UserSettingsService service) {
        this.service = service;
    }

    /**
     * Получить сохраненные настройки
     *
     * @param key  - ключ настройки
     * @return json object с полем value, содержащим значение настройки
     */
    public Object getSettings(String key) {
        JSONObject result = service.getSettings(key);
        return result != null ? result : new JSONObject();
    }

    /**
     * Сохранить настройки для пользователя
     *
     * @param key   - ключ настройки
     * @param value - значение для сохранения
     */
    public boolean setSettings(String key, Object value) {
        return service.setSettings(key, value);
    }

    /**
     * Удалить настройки для пользователя
     *
     * @param key      - ключ настройки
     */
    public boolean deleteSettings(String key) {
        return service.deleteSettings(key);
    }
}
