package ru.it.lecm.arm.beans.childRules;

import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.02.14
 * Time: 15:55
 */
public class ArmStatusesChildRule extends ArmBaseChildRule {
	private enum Rule {
		ALL,
		ALL_NOT_ARCHIVE,
		ALL_ARCHIVE,
		SELECTED,
		EXCEPT_SELECTED
	}

	private String rule;
	private List<String> selectedStatuses;

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public List<String> getSelectedStatuses() {
		return selectedStatuses;
	}

	public void setSelectedStatuses(List<String> selectedStatuses) {
		this.selectedStatuses = selectedStatuses;
	}

	@Override
	public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
		return null;
	}
}
