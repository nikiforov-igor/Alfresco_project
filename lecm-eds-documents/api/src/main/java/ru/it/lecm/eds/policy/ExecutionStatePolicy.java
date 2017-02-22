package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
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
public class ExecutionStatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private String type;
    private String statusesOrder;
    private String executedStatus;
    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private NamespaceService namespaceService;
    private ErrandsService errandsService;
    private StateMachineServiceBean stateMachineService;

    public void setStatusesOrder(String statusesOrder) {
        this.statusesOrder = statusesOrder;
    }

    public void setExecutedStatus(String executedStatus) {
        this.executedStatus = executedStatus;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "type", type);
        PropertyCheck.mandatory(this, "executedStatus", executedStatus);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
        PropertyCheck.mandatory(this, "stateMachineService", stateMachineService);
        PropertyCheck.mandatory(this, "errandsService", errandsService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                QName.createQName(type, namespaceService), new JavaBehaviour(this, "onUpdateProperties"));

    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        Integer oldChildChangeSignalCount = (Integer) before.get(EDSDocumentService.PROP_CHILD_CHANGE_SIGNAL_COUNT);
        Integer newChildChangeSignalCount = (Integer) after.get(EDSDocumentService.PROP_CHILD_CHANGE_SIGNAL_COUNT);
        if (nodeService.hasAspect(nodeRef, EDSDocumentService.ASPECT_CHILD_CHANGE_SIGNAL) && !Objects.equals(oldChildChangeSignalCount, newChildChangeSignalCount)
                && newChildChangeSignalCount != 0) {
            String executionState = String.valueOf(EDSDocumentService.EXECUTION_STATE.NOT_REQUIRED);
            JSONArray jsonArray = new JSONArray();
            List<NodeRef> childErrands = errandsService.getChildErrands(nodeRef);
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
                        if (errandsCountByStatus.containsKey(errandStatus)) {
                            errandsCountByStatus.put(errandStatus, errandsCountByStatus.get(errandStatus) + 1);
                        } else {
                            errandsCountByStatus.put(errandStatus, 1);
                        }
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
                    jsonObject.put(entry.getKey(), entry.getValue());
                    jsonArray.add(jsonObject);
                }
            }
            nodeService.setProperty(nodeRef, EDSDocumentService.PROP_EXECUTION_STATE, executionState);
            nodeService.setProperty(nodeRef, EDSDocumentService.PROP_EXECUTION_STATISTICS, jsonArray.toJSONString());
        }
    }
}
