package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by APanyukov on 11.01.2017.
 */
public class ErrandsLimitationDatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private final static String CALENDAR_DAY_TYPE_STRING = "к.д";
    private final static String WORK_DAY_TYPE_STRING = "р.д";
    private final static String LIMITLESS_STRING = "Без срока";

    private PolicyComponent policyComponent;

    private NodeService nodeService;

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
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
        if ((newDateRadio != null && !newDateRadio.equals(oldDateRadio)) || (newDate != null && newDate.equals(oldDate))) {
            String dateText = "";

            if (newDate != null) {
                DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
                dateText = formater.format(newDate);
            } else if ("DAYS".equals(newDateRadio)) {
                Boolean isDraft = nodeService.hasAspect(nodeRef, StatemachineModel.ASPECT_IS_DRAFT);
                if (isDraft) {
                    String daysType = (String) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE);
                    Integer daysCount = (Integer) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);
                    if ("WORK".equals(daysType)) {
                        dateText = daysCount + " " + WORK_DAY_TYPE_STRING;
                    } else if ("CALENDAR".equals(daysType)) {
                        dateText = daysCount + " " + CALENDAR_DAY_TYPE_STRING;
                    }
                }
            } else if ("LIMITLESS".equals(newDateRadio)) {
                dateText = LIMITLESS_STRING;
            }

            nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TEXT, dateText);
        }


    }
}
