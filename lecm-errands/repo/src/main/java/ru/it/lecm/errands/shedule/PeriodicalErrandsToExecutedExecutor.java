package ru.it.lecm.errands.shedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.List;

/**
 * User: mshafeev
 * Date: 24.07.13
 * Time: 15:48
 */
public class PeriodicalErrandsToExecutedExecutor extends ActionExecuterAbstractBase {

    private NodeService nodeService;
    private ErrandsService errandsService;
    private StateMachineServiceBean stateMachineService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    @Override
    protected void executeImpl(Action action, final NodeRef nodeRef) {
        int countFinalErrands = 0;
        List<NodeRef> childErrands = errandsService.getChildErrands(nodeRef);
        for (NodeRef childErrand: childErrands) {
            if (stateMachineService.isFinal(childErrand)) {
                countFinalErrands++;
            }
        }

        if (countFinalErrands == childErrands.size()) {
            nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_TRANSIT_TO_EXECUTED, true);
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }
}
