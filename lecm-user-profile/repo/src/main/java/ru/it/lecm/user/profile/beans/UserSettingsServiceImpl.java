package ru.it.lecm.user.profile.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 30.05.2017
 * Time: 16:21
 */
public class UserSettingsServiceImpl extends BaseBean implements UserSettingsService {
    final private static Logger logger = LoggerFactory.getLogger(UserSettingsServiceImpl.class);
    final private ObjectMapper jsonMapper = new ObjectMapper();

    private int directoriesDeep = 3;

    private SimpleCache<String, String> userSettingsCache;
    private ContentService contentService;

    public void setDirectoriesDeep(int directoriesDeep) {
        this.directoriesDeep = directoriesDeep;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setUserSettingsCache(SimpleCache<String, String> userSettingsCache) {
        this.userSettingsCache = userSettingsCache;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(USER_SETTINGS_ID);
    }

    /**
     * Получить настройки пользователя
     *
     * @param category - категория настроек
     * @return ID ноды с настройками или NULL - если ноды с настройками не существует
     */
    public NodeRef getUserSettingsFile(final String category) throws WriteTransactionNeededException {
        NodeRef userFolder = getUserSettingsFolder(AuthenticationUtil.getFullyAuthenticatedUser(), false);
        if (userFolder != null) {
            return nodeService.getChildByName(userFolder, ContentModel.ASSOC_CONTAINS, category);
        }
        return null;
    }

    /**
     * Получить значение сохраненной настройки
     *
     * @param key  - ключ настройки с префиксом-категорией. Формат: <код_категории>.<ключ_настройки>
     * @return сохраненное значение
     */
    public String getSettings(final String key) {
        String category = key.split("\\.")[0];
        String actualKey = key;
        if (!category.equals(key)) {
            actualKey = key.substring(category.length() + 1);
        } else {
            category = DEFAULT_ROOT;
        }
        return getSettings(category, actualKey);
    }

    /**
     * Получить сохраненную настройку
     *
     * @param category - категория настроек
     * @param key      - ключ настройки
     * @return сохраненное значение или NULL, если настройка не задана
     */
    public String getSettings(final String category, final String key) {
        String settingsKey = getCashKey(AuthenticationUtil.getFullyAuthenticatedUser(), category, key);
        if (userSettingsCache.contains(settingsKey)) {
            return userSettingsCache.get(settingsKey);
        }

        NodeRef settingsNode = getUserSettingsFile(category);
        if (null == settingsNode) {
            return null;
        }
        ContentReader configReader = contentService.getReader(settingsNode, ContentModel.PROP_CONTENT);
        String jsonContent = configReader.getContentString();
        try {
            String value = getValueByKey(jsonMapper.readTree(jsonContent), key);
            userSettingsCache.put(settingsKey, value);
            return value;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Сохранить настройку для пользователя
     *  @param key   - ключ настройки с префиксом-категорией. Формат: <код_категории>.<ключ_настройки>
     * @param value - значение для сохранения
     */
    public boolean setSettings(final String key, final Object value) {
        String category = key.split("\\.")[0];
        String actualKey = key;
        if (!category.equals(key)) {
            actualKey = key.substring(category.length() + 1);
        } else {
            category = DEFAULT_ROOT;
        }
        return setSettings(category, actualKey, value);
    }

    /**
     * Сохранить настройку для пользователя
     *  @param category - категория/раздел для сохранения
     * @param key      - ключ настройки
     * @param value    - значение для сохранения
     */
    public boolean setSettings(final String category, final String key, final Object value) throws WriteTransactionNeededException {
        return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                final NodeRef userFolder = getUserSettingsFolder(AuthenticationUtil.getFullyAuthenticatedUser(), true);
                if (userFolder != null) {
                    JsonNode settingsJSON;

                    NodeRef userSettingsNode = getUserSettingsFile(category);
                    if (null == userSettingsNode) {
                        PropertyMap propertyMap = new PropertyMap();
                        propertyMap.put(ContentModel.PROP_NAME, category);
                        propertyMap.put(ContentModel.PROP_IS_INDEXED, false);
                        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, category);
                        userSettingsNode = nodeService.createNode(userFolder, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_CONTENT, propertyMap).getChildRef();
                        settingsJSON = jsonMapper.createObjectNode();
                    } else {
                        ContentReader reader = contentService.getReader(userSettingsNode, ContentModel.PROP_CONTENT);
                        settingsJSON = jsonMapper.readTree(reader.getContentString());
                    }

                    settingsJSON = getUpdatedConfig(settingsJSON, key, value);

                    ContentWriter writer = contentService.getWriter(userSettingsNode, ContentModel.PROP_CONTENT, true);
                    if (writer != null) {
                        writer.setEncoding("UTF-8");
                        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                        writer.putContent(settingsJSON.toString());
                    }
                    userSettingsCache.clear();
                } else {
                    logger.error("Cannot get user settings folder!");
                    return false;
                }

                return true;
            }
        });
    }

    /**
     * Удалить настройку для пользователя
     *
     * @param key  - ключ настройки с префиксом-категорией. Формат: <код_категории>.<ключ_настройки>
     */
    public boolean deleteSettings(final String key) {
        return setSettings(key, jsonMapper.createObjectNode());
    }

    /**
     * Удалить настройку для пользователя
     *  @param category - категория/раздел для сохранения
     * @param key      - ключ настройки
     */
    public boolean deleteSettings(final String category, final String key) throws WriteTransactionNeededException {
        return setSettings(category, key, jsonMapper.createObjectNode());
    }

    private List<String> getUserFolderPath(String user) {
        List<String> result = new ArrayList<>();
        if (user != null && !user.isEmpty()) {
            for (int i = 0; i < user.length() && i < directoriesDeep; i++) {
                result.add(String.valueOf(user.charAt(i)).toUpperCase());
            }
            result.add(user);
        }
        return result;
    }

    private String getValueByKey(JsonNode config, String keyValue) {
        String[] keysPath = keyValue.split("\\.");
        for (String key : keysPath) {
            JsonNode node = config.path(key);
            if (node.isMissingNode()) {
                return null;
            }
            config = node;
        }
        return config.toString();
    }

    private JsonNode getUpdatedConfig(JsonNode config, String keyValue, Object value) {
        String[] keysPath = keyValue.split("\\.");
        JsonNode innerConfig = config;
        String lastKey = keysPath[keysPath.length - 1];
        for (String key : keysPath) {
            if (!key.equals(lastKey)) {
                JsonNode node = innerConfig.path(key);
                if (node.isMissingNode()) {
                    node = jsonMapper.createObjectNode();
                    ((ObjectNode) innerConfig).put(key, node);
                }
                innerConfig = node;
            } else {
                Object valueToSave = convertValueToObject(value);
                if (valueToSave instanceof JsonNode) {
                    ((ObjectNode) innerConfig).put(key, (JsonNode) valueToSave);
                } else {
                    ((ObjectNode) innerConfig).put(key, valueToSave.toString());
                }
            }
        }

        return config;
    }

    private Object convertValueToObject(Object valueToConvert) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(valueToConvert.toString());
        } catch (IOException ignored) {
        }

        if (valueToConvert instanceof Date) {
            valueToConvert = DateFormatISO8601.format(valueToConvert);
        } else {
            valueToConvert = valueToConvert.toString();
        }

        return valueToConvert;
    }

    private NodeRef getUserSettingsFolder(String user, boolean createIfNotExists) {
        List<String> directoryPaths = getUserFolderPath(user);
        NodeRef userSettingsDir = getFolder(getServiceRootFolder(), directoryPaths);
        if (null == userSettingsDir && createIfNotExists) {
            logger.debug("User settings folder not found. Trying to create.");
            userSettingsDir = createPath(getServiceRootFolder(), directoryPaths);

            serviceRegistry.getPermissionService().setPermission(userSettingsDir, user, "LECM_BASIC_PG_Owner", true);
            serviceRegistry.getPermissionService().setInheritParentPermissions(userSettingsDir, false);
            logger.debug("Folder created. Ref=\"{}\"", userSettingsDir);
        }
        return userSettingsDir;
    }

    private String getCashKey(String user, String category, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(user);
        sb.append(".");
        if (category != null && !category.isEmpty()) {
            sb.append(category);
        } else {
            sb.append(DEFAULT_ROOT);
        }
        sb.append(".");
        if (key != null && !key.isEmpty()) {
            sb.append(key);
        } else {
            sb.append(DEFAULT_ROOT);
        }
        return sb.toString();
    }
}
