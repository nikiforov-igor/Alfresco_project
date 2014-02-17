package ru.it.lecm.arm.beans.node;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.arm.beans.ArmColumn;
import ru.it.lecm.arm.beans.ArmCounter;
import ru.it.lecm.arm.beans.ArmFilter;
import ru.it.lecm.arm.beans.childRules.ArmBaseChildRule;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 05.02.14
 * Time: 10:22
 */
public class ArmNode {
    private NodeRef nodeRef;
    private NodeRef armNodeRef;
    private String title;
    private List<String> types;
	private String searchQuery;
    private List<ArmColumn> columns;
    private List<ArmFilter> avaiableFilters;
    private ArmCounter counter;
    private boolean hasChilds = false;

    private ArmBaseChildRule nodeQuery;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getTypes() {
        return types;
    }

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public List<ArmColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ArmColumn> columns) {
        this.columns = columns;
    }

    public void setAvaiableFilters(List<ArmFilter> avaiableFilters) {
        this.avaiableFilters = avaiableFilters;
    }

    public List<ArmFilter> getAvaiableFilters() {
        return avaiableFilters;
    }

    public void setCounter(ArmCounter counter) {
        this.counter = counter;
    }

    public ArmCounter getCounter(){
        return counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmNode node = (ArmNode) o;
        return nodeRef.equals(node.nodeRef);
    }

    @Override
    public int hashCode() {
        int result = nodeRef != null ? nodeRef.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    public NodeRef getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(NodeRef nodeRef) {
        this.nodeRef = nodeRef;
    }

    public ArmBaseChildRule getNodeQuery() {
        return nodeQuery;
    }

    public void setNodeQuery(ArmBaseChildRule searchQuery) {
        this.nodeQuery = searchQuery;
    }

    public NodeRef getArmNodeRef() {
        return armNodeRef;
    }

    public void setArmNodeRef(NodeRef armNodeRef) {
        this.armNodeRef = armNodeRef;
    }

    public boolean isHasChilds() {
        return hasChilds;
    }

    public void setHasChilds(boolean hasChilds) {
        this.hasChilds = hasChilds;
    }
}
