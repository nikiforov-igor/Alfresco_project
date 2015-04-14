package ru.it.lecm.statemachine;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pmelnikov on 09.04.2015.
 *
 * Объект для хранения настроек типа документа без жизненного цикла
 *
 */
public class SimpleDocumentRegistryItem {

    /**
     * Путь к хранилищу документов
     */
    private String storePath = "";
    /**
     * Расширенный путь в хранилище документов
     */
    private String additionalPath = "";
    /**
     * Роли пользователей, которые могут создавать документ
     */
    private List<String> starters = new ArrayList<>();
    /**
     * Роли и их привелегии назначаемые на папку хранилища документов
     */
    private Map<String, String> permissions = new HashMap<>();
    /**
     * Если ли возможность создавать данный документ в АРМ
     */
    private boolean isNotArmCreated = false;
    /**
     * Ссылка на хранилище
     */
    private NodeRef typeRoot;

    public SimpleDocumentRegistryItem(String storePath) {
        this.storePath = storePath;
    }

    public boolean isNotArmCreated() {
        return isNotArmCreated;
    }

    public String getStorePath() {
        return storePath;
    }

    public List<String> getStarters() {
        return starters;
    }

    public Map<String, String> getPermissions() {
        return permissions;
    }

    public void setStarters(List<String> starters) {
        this.starters = starters;
    }

    public void setPermissions(Map<String, String> permissions) {
        this.permissions = permissions;
    }

    public void setNotArmCreated(boolean isNotArmCreated) {
        this.isNotArmCreated = isNotArmCreated;
    }

    public NodeRef getTypeRoot() {
        return typeRoot;
    }

    public void setTypeRoot(NodeRef typeRoot) {
        this.typeRoot = typeRoot;
    }

    public String getAdditionalPath() {
        return additionalPath;
    }

    public void setAdditionalPath(String additionalPath) {
        this.additionalPath = additionalPath;
    }
}
