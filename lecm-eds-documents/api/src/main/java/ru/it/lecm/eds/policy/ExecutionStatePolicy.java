package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;

/**
 * Created by APanyukov on 20.02.2017.
 */
public class ExecutionStatePolicy extends EDSBaseDocumentTypePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private String statusesOrder;

    public String getStatusesOrder() {
        return statusesOrder;
    }

    public void setStatusesOrder(String statusesOrder) {
        this.statusesOrder = statusesOrder;
    }

    public final void init() {
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
                        isAnyExecuted = errandStatus.equals("Исполнено");
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
