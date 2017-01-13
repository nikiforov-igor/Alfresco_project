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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Created by APanyukov on 11.01.2017.
 */
public class ErrandsLimitationDatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

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
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, new JavaBehaviour(this, "onUpdateProperties"));
    }

    /*
        Заполнение атрибута текстового представления срока исполнения
     */
    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String oldDateRadio = (String) before.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
        String newDateRadio = (String) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
        Date oldDate = (Date) before.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
        Date newDate = (Date) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
        String oldDaysType = (String) before.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE);
        Integer oldDaysCount = (Integer) before.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);
        String newDaysType = (String) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE);
        Integer newDaysCount = (Integer) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);
        Boolean radioChanged = false;
        Boolean dateChanged = false;
        Boolean dayTypeChanged = false;
        Boolean dayCountChanged = false;

        if (!Objects.equals(newDateRadio, oldDateRadio)) {
            radioChanged = true;
        }
        if (!Objects.equals(newDate, oldDate)) {
            dateChanged = true;
        }
        if (!Objects.equals(newDaysType, oldDaysType)) {
            dayTypeChanged = true;
        }
        if (!Objects.equals(newDaysCount, oldDaysCount)) {
            dayCountChanged = true;
        }
        if (radioChanged || dateChanged || dayTypeChanged || dayCountChanged) {
            if (newDateRadio != null && ("LIMITLESS".equals(newDateRadio) || newDate != null || (newDaysType != null && newDaysCount != null))) {
                String dateText = null;
                Boolean changed = false;
                if (radioChanged && "LIMITLESS".equals(newDateRadio)) {
                    dateText = LIMITLESS_STRING;
                    nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, null);
                    changed = true;
                } else if ((radioChanged && "DATE".equals(newDateRadio) && newDate != null) || dateChanged) {
                    DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
                    dateText = formater.format(newDate);
                    changed = true;
                } else if ((radioChanged && "DAYS".equals(newDateRadio) && newDaysType != null && newDaysCount != null)
                        || dayTypeChanged || dayCountChanged) {
                    Boolean isDraft = stateMachineService.isDraft(nodeRef);
                    if (isDraft) {
                        if ("WORK".equals(newDaysType)) {
                            dateText = newDaysCount + " " + WORK_DAY_TYPE_STRING;
                        } else if ("CALENDAR".equals(newDaysType)) {
                            dateText = newDaysCount + " " + CALENDAR_DAY_TYPE_STRING;
                        }
                        nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE, null);
                    }
                    changed = true;
                }
                if (changed && dateText != null) {
                    nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TEXT, dateText);
                }
            }
        }
    }
}
