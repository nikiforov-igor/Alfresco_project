package ru.it.lecm.arm.beans.childRules;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.search.ArmChildrenRequest;
import ru.it.lecm.arm.beans.search.ArmChildrenResponse;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private StateMachineServiceBean stateMachineService;

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

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
    public ArmChildrenResponse build(ArmWrapperService service, ArmNode node, ArmChildrenRequest request) {
        List<ArmNode> nodes = new ArrayList<ArmNode>();
        Set<String> allStatuses = new HashSet<String>();
        List<String> avaiableTypes = new ArrayList<String>();

        if (node != null) {
            avaiableTypes.addAll(node.getTypes());
        }

        switch (Rule.valueOf(getRule())) {
            case SELECTED: {
                List<String> statuses = getSelectedStatuses();
                if (statuses != null) {
                    for (String status : statuses) {
                        ArmNode childNode = service.wrapStatusAsObject(status, node);
                        nodes.add(childNode);
                    }
                }
                break;
            }
            case EXCEPT_SELECTED: {
                List<String> excludedStatuses = getSelectedStatuses();
                for (String docType : avaiableTypes) {
                    List<String> statusesForType = stateMachineService.getStatuses(docType, true, true);
                    for (String s : statusesForType) {
                        allStatuses.add(s);
                    }
                }

                if (excludedStatuses != null) {
                    for (String exStatus : excludedStatuses) {
                        allStatuses.remove(exStatus);
                    }
                }
                break;
            }
            case ALL_ARCHIVE: {
                for (String docType : avaiableTypes) {
                    List<String> statusesForType = stateMachineService.getStatuses(docType, false, true);
                    for (String s : statusesForType) {
                        allStatuses.add(s);
                    }
                }
                break;
            }
            case ALL_NOT_ARCHIVE: {
                for (String docType : avaiableTypes) {
                    List<String> statusesForType = stateMachineService.getStatuses(docType, true, false);
                    for (String s : statusesForType) {
                        allStatuses.add(s);
                    }
                }
                break;
            }
            case ALL: {
                for (String docType : avaiableTypes) {
                    List<String> statusesForType = stateMachineService.getStatuses(docType, true, true);
                    for (String s : statusesForType) {
                        allStatuses.add(s);
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
        if (node != null) {
            for (String st : allStatuses) {
                ArmNode childNode = service.wrapStatusAsObject(st, node);
                nodes.add(childNode);
            }
        }
        return new ArmChildrenResponse(nodes);
    }

    @Override
    public List<NodeRef> getChildren(NodeRef node) {
        return null;
    }

    public String getQuery() {
		StringBuilder queryBuilder = new StringBuilder();
		switch (Rule.valueOf(rule)) {
			case SELECTED: {
				if (selectedStatuses != null) {
					for (String status: selectedStatuses) {
						if (queryBuilder.length() > 0) {
							queryBuilder.append(" OR ");
						}
						queryBuilder.append("@lecm\\-statemachine\\:status:\"").append(status).append("\"");
					}
				}
				break;
			}
			case EXCEPT_SELECTED: {
				if (selectedStatuses != null) {
					for (String status: selectedStatuses) {
						if (queryBuilder.length() > 0) {
							queryBuilder.append(" AND ");
						}
						queryBuilder.append("NOT @lecm\\-statemachine\\:status:\"").append(status).append("\"");
					}
				}
				break;
			}
			case ALL_ARCHIVE: {
				queryBuilder.append("@lecm\\-statemachine\\-aspects\\:is\\-final:true");
				break;
			}
			case ALL_NOT_ARCHIVE: {
				queryBuilder.append("NOT (@lecm\\-statemachine\\-aspects\\:is\\-final:true)");
				break;
			}
			case ALL: {
				break;
			}
			default: {
				break;
			}
		}
		return queryBuilder.length() > 0 ? queryBuilder.toString() : null;
	}
}
