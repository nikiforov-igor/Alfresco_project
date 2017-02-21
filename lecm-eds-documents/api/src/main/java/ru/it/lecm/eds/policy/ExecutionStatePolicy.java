package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.json.simple.JSONObject;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by APanyukov on 20.02.2017.
 */
public class ExecutionStatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private StateMachineServiceBean stateMachineService;
    private ErrandsService errandsService;
    private QName typeQName;

    public QName getTypeQName() {
        return typeQName;
    }

    public void setTypeQName(QName typeQName) {
        this.typeQName = typeQName;
    }

    public ErrandsService getErrandsService() {
        return errandsService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public StateMachineServiceBean getStateMachineService() {
        return stateMachineService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "stateMachineService", stateMachineService);
        PropertyCheck.mandatory(this, "errandsService", errandsService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                typeQName, new JavaBehaviour(this, "onUpdateProperties"));

    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        Integer oldChildChangeSignalCount = (Integer) before.get(EDSDocumentService.PROP_CHILD_CHANGE_SIGNAL_COUNT);
        Integer newChildChangeSignalCount = (Integer) after.get(EDSDocumentService.PROP_CHILD_CHANGE_SIGNAL_COUNT);
        if (nodeService.hasAspect(nodeRef, EDSDocumentService.ASPECT_CHILD_CHANGE_SIGNAL) && !Objects.equals(oldChildChangeSignalCount, newChildChangeSignalCount)
                && newChildChangeSignalCount != 0) {
            String executionState = String.valueOf(EDSDocumentService.EXECUTION_STATE.NOT_REQUIRED);
            JSONObject json = new JSONObject();
            List<NodeRef> childErrands = errandsService.getChildErrands(nodeRef);
            if (childErrands != null && childErrands.size() != 0) {
                List<String> statuses = stateMachineService.getStatuses(ErrandsService.TYPE_ERRANDS, true, true);
                Map<String, Integer> errandsCountByStatus = new HashMap<>();
                Boolean inProcess = false;
                Boolean isAnyExecuted = false;
                int finalCount = 0;
                for (NodeRef errand : childErrands) {
                    for (String status : statuses) {
                        if (nodeService.getProperty(errand, StatemachineModel.PROP_STATUS).equals(status)) {
                            if (errandsCountByStatus.containsKey(status)) {
                                errandsCountByStatus.put(status, errandsCountByStatus.get(status) + 1);
                            } else {
                                errandsCountByStatus.put(status, 1);
                            }
                        }
                    }
                    inProcess = !stateMachineService.isDraft(errand) && !stateMachineService.isFinal(errand);
                    isAnyExecuted = nodeService.getProperty(errand, StatemachineModel.PROP_STATUS).equals("Исполнено");
                    if (stateMachineService.isFinal(errand)) {
                        finalCount++;
                    }
                }
                Boolean allFinal = finalCount == childErrands.size();
                executionState = inProcess ? String.valueOf(EDSDocumentService.EXECUTION_STATE.IN_PROCESS) : allFinal && isAnyExecuted ? String.valueOf(EDSDocumentService.EXECUTION_STATE.COMPLETE) : executionState;
                json.putAll(errandsCountByStatus);
            }
            nodeService.setProperty(nodeRef, EDSDocumentService.PROP_EXECUTION_STATE, executionState);
            nodeService.setProperty(nodeRef, EDSDocumentService.PROP_EXECUTION_STATISTICS, json.toJSONString());
        }
    }
}
