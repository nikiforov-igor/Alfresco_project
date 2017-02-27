package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;

/**
 * Created by APanyukov on 20.02.2017.
 */
public class ErrandsExecutionStatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    private String statusesOrder;
    private String executedStatus;
    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private ErrandsService errandsService;
    private StateMachineServiceBean stateMachineService;

    public void setStatusesOrder(String statusesOrder) {
        this.statusesOrder = statusesOrder;
    }

    public void setExecutedStatus(String executedStatus) {
        this.executedStatus = executedStatus;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "executedStatus", executedStatus);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "stateMachineService", stateMachineService);
        PropertyCheck.mandatory(this, "errandsService", errandsService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String oldErrandStatus = (String) before.get(StatemachineModel.PROP_STATUS);
        String newErrandStatus = (String) after.get(StatemachineModel.PROP_STATUS);
        if (!Objects.equals(oldErrandStatus, newErrandStatus)) {
            NodeRef baseDoc = errandsService.getBaseDocument(nodeRef);
            if (baseDoc != null && nodeService.hasAspect(baseDoc, EDSDocumentService.ASPECT_EXECUTION_STATE)) {
                calculateExecutionStatistic(baseDoc);
            }
        }
    }

    private void calculateExecutionStatistic(NodeRef document) {
        String executionState = String.valueOf(EDSDocumentService.EXECUTION_STATE.NOT_REQUIRED);
        JSONArray jsonArray = new JSONArray();
        List<NodeRef> childErrands = errandsService.getChildErrands(document);
        if (childErrands != null && childErrands.size() != 0) {
            List<String> statuses;
            if (statusesOrder != null && !Objects.equals(statusesOrder, "")) {
                statuses = Arrays.asList(statusesOrder.split(","));
            } else {
                statuses = stateMachineService.getStatuses(ErrandsService.TYPE_ERRANDS, true, true);
            }
            Map<String, Integer> errandsCountByStatus = new LinkedHashMap<>();
            for (String status : statuses) {
                errandsCountByStatus.put(status, 0);
            }
            Boolean inProcess = false;
            Boolean isAnyExecuted = false;
            Boolean allFinal = true;

            for (NodeRef errand : childErrands) {
                String errandStatus = (String) nodeService.getProperty(errand, StatemachineModel.PROP_STATUS);
                if (statuses.contains(errandStatus)) {
                    errandsCountByStatus.put(errandStatus, errandsCountByStatus.get(errandStatus) + 1);
                }
                if (!inProcess) {
                    inProcess = !stateMachineService.isDraft(errand) && !stateMachineService.isFinal(errand);
                }
                if (!isAnyExecuted) {
                    isAnyExecuted = errandStatus.equals(executedStatus);
                }
                if (!stateMachineService.isFinal(errand)) {
                    allFinal = false;
                }
            }
            executionState = String.valueOf(EDSDocumentService.EXECUTION_STATE.computeState(allFinal, isAnyExecuted, inProcess));
            for (Map.Entry<String, Integer> entry : errandsCountByStatus.entrySet()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("state", entry.getKey());
                jsonObject.put("count", entry.getValue());
                jsonArray.add(jsonObject);
            }
        }
        nodeService.setProperty(document, EDSDocumentService.PROP_EXECUTION_STATE, executionState);
        nodeService.setProperty(document, EDSDocumentService.PROP_EXECUTION_STATISTICS, jsonArray.toJSONString());
    }
}
