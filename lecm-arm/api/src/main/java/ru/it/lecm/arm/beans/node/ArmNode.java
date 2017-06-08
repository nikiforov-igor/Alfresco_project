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
    private String nodeType;
    private NodeRef armNodeRef;
    private String title;
    private List<String> types;
    private String searchType;
	private String searchQuery;
    private List<ArmColumn> columns;
    private List<ArmFilter> avaiableFilters;
    private ArmCounter counter;
    private boolean hasChilds = false;
	private List<String> createTypes;
	private String htmlUrl;
    private Integer maxItemsCount;
    private Integer realChildrenCount;
	private String reportCodes;
    private NodeRef runAsEmployee;

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

    public Integer getRealChildrenCount() {
        return realChildrenCount;
    }

    public void setRealChildrenCount(Integer realChildrenCount) {
        this.realChildrenCount = realChildrenCount;
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

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
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

	public List<String> getCreateTypes() {
		return createTypes;
	}

	public void setCreateTypes(List<String> createTypes) {
		this.createTypes = createTypes;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}

    public Integer getMaxItemsCount() {
        return maxItemsCount;
    }

    public void setMaxItemsCount(Integer maxItemsCount) {
        this.maxItemsCount = maxItemsCount;
    }

    public String getReportCodes() {
        return reportCodes;
    }

    public void setReportCodes(String reportCodes) {
        this.reportCodes = reportCodes;
    }

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

    public NodeRef getRunAsEmployee() {
        return runAsEmployee;
    }

    public void setRunAsEmployee(NodeRef runAsEmployee) {
        this.runAsEmployee = runAsEmployee;
    }

}
