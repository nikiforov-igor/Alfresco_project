package ru.it.lecm.arm.beans.query;

import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 9:57
 */
public abstract class ArmBaseQuery {
	private String searchQuery;

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

    abstract public List<ArmNode> build(ArmWrapperService service, ArmNode node);

    abstract public ArmBaseQuery getDuplicate();
}
