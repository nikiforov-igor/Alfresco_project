package ru.it.lecm.arm.beans.search;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Запрос на получение списка дочерних элементов
 * User: pmelnikov
 * Date: 31.05.2017
 */
public class ArmChildrenRequest {

    private NodeRef nodeRef;
    private NodeRef parentRef;
    private boolean onlyMeta = false;
    private int maxItems = -1;
    private int skipCount = 0;
    private String searchTerm = "";

    public ArmChildrenRequest(NodeRef nodeRef, NodeRef parentRef) {
        this.nodeRef = nodeRef;
        this.parentRef = parentRef;
    }

    public ArmChildrenRequest(NodeRef nodeRef, NodeRef parentRef, boolean onlyMeta) {
        this.nodeRef = nodeRef;
        this.parentRef = parentRef;
        this.onlyMeta = onlyMeta;
    }

    public NodeRef getNodeRef() {
        return nodeRef;
    }

    public NodeRef getParentRef() {
        return parentRef;
    }

    public boolean isOnlyMeta() {
        return onlyMeta;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public int getSkipCount() {
        return skipCount;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setNodeRef(NodeRef nodeRef) {
        this.nodeRef = nodeRef;
    }

    public void setParentRef(NodeRef parentRef) {
        this.parentRef = parentRef;
    }

    public void setOnlyMeta(boolean onlyMeta) {
        this.onlyMeta = onlyMeta;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
