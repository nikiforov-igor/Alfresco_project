package ru.it.lecm.user.profile.scripts;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * @param user - логин пользователя
     * @param key  - ключ настройки
     * @return json object с полем value, содержащим значение настройки
     */
    public Object getSettings(String user, String key) {
        JSONObject result = new JSONObject();
        String settings = service.getSettings(user, key);
        try {
            result.put("value", "");
            JSONObject settingJSON = new JSONObject(settings);
            result.put("value", settingJSON);
        } catch (JSONException ignored) {
        }

        return result;
    }

    /**
     * Сохранить настройки для пользователя
     *
     * @param user  - логин пользователя
     * @param key   - ключ настройки
     * @param value - значение для сохранения
     */
    public boolean setSettings(String user, String key, Object value) {
        return service.setSettings(user, key, value);
    }

    /**
     * Удалить настройки для пользователя
     *
     * @param user     - логин пользователя
     * @param category - категория/раздел для сохранения
     * @param key      - ключ настройки
     */
    public boolean deleteSettings(String user, String category, String key) {
        if (category != null) {
            return service.deleteSettings(user, category, key);
        } else {
            return service.deleteSettings(user, key);
        }
    }
}
