package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by APanyukov on 11.01.2017.
 */
public class ErrandsLimitationDatePolicy implements NodeServicePolicies.OnUpdateNodePolicy {

    private final static String CALENDAR_DAY_TYPE_STRING = "к.д.";
    private final static String WORK_DAY_TYPE_STRING = "р.д.";
    private final static String LIMITLESS_STRING = "Без срока";

    private PolicyComponent policyComponent;

    private NodeService nodeService;

    private StateMachineServiceBean stateMachineService;

    public StateMachineServiceBean getStateMachineService() {
        return stateMachineService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "stateMachineService", stateMachineService);
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdateNodePolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, new JavaBehaviour(this, "onUpdateNode"));
    }

    /*
        Заполнение атрибута текстового представления срока исполнения
     */
    @Override
    public void onUpdateNode(NodeRef nodeRef) {
        String dueDateRadio = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
        Date dueDate = (Date) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
        String dueDateDaysType = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE);
        Integer dueDateDaysCount = (Integer) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);

        if (dueDateRadio != null) {
            String dateText = "";
            if ("DATE".equals(dueDateRadio) || dueDate != null) {
                DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
                dateText = formater.format(dueDate);
            } else if ("DAYS".equals(dueDateRadio)) {
                Boolean isDraft = stateMachineService.isDraft(nodeRef);
                if (dueDateDaysType != null && dueDateDaysCount != null) {
                    if (isDraft) {
                        if ("WORK".equals(dueDateDaysType)) {
                            dateText = dueDateDaysCount + " " + WORK_DAY_TYPE_STRING;
                        } else if ("CALENDAR".equals(dueDateDaysType)) {
                            dateText = dueDateDaysCount + " " + CALENDAR_DAY_TYPE_STRING;
                        }
                    }
                }
            } else if ("LIMITLESS".equals(dueDateRadio)) {
                dateText = LIMITLESS_STRING;
            }
            nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TEXT, dateText);
        }
    }
}
