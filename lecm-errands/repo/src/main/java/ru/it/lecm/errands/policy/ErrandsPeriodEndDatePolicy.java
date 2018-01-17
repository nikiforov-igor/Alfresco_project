package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.time.DateUtils;
import ru.it.lecm.errands.ErrandsService;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Created by APanyukov on 15.01.2018.
 */
public class ErrandsPeriodEndDatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    private NodeService nodeService;
    private PolicyComponent policyComponent;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, new JavaBehaviour(this, "onUpdateProperties"));
    }


    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String newDateRadio = (String) after.get(ErrandsService.PROP_ERRANDS_PERIODICALLY_RADIO);
        String newDuringType = (String) after.get(ErrandsService.PROP_ERRANDS_PERIOD_DURING_TYPE);
        Integer newDuringCount = (Integer) after.get(ErrandsService.PROP_ERRANDS_PERIOD_DURING);
        Date oldPeriodEndDate = (Date) after.get(ErrandsService.PROP_ERRANDS_PERIOD_END);
        String oldPeriodEndDateText = (String) after.get(ErrandsService.PROP_ERRANDS_PERIOD_END_TEXT);

        Date newPeriodEndDate = null;
        if (newDateRadio != null) {
            String newPeriodEndDateText = null;
            if (Objects.equals(ErrandsService.PeriodicallyRadio.DATERANGE.toString(), newDateRadio)) {
                newPeriodEndDate = (Date) after.get(ErrandsService.PROP_ERRANDS_PERIOD_END);
            } else if (Objects.equals(ErrandsService.PeriodicallyRadio.ENDLESS.toString(), newDateRadio)) {
                newPeriodEndDate = null;
            } else if (Objects.equals(ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString(), newDateRadio)) {
                newPeriodEndDate = null;
                Integer repeatCount = (Integer) after.get(ErrandsService.PROP_ERRANDS_REITERATION_COUNT);
                if (repeatCount != null) {
                    newPeriodEndDateText = "После " + repeatCount + " повторений";
                }
            } else if (Objects.equals(ErrandsService.PeriodicallyRadio.DURING.toString(), newDateRadio)) {
                Date startPeriodDate = (Date) after.get(ErrandsService.PROP_ERRANDS_PERIOD_START);
                if (Objects.equals(newDuringType, ErrandsService.PeriodDuringType.DAYS.toString())) {
                    newPeriodEndDate = DateUtils.addDays(startPeriodDate, newDuringCount);
                } else if (Objects.equals(newDuringType, ErrandsService.PeriodDuringType.WEEKS.toString())) {
                    newPeriodEndDate = DateUtils.addWeeks(startPeriodDate, newDuringCount);
                } else if (Objects.equals(newDuringType, ErrandsService.PeriodDuringType.MONTHS.toString())) {
                    newPeriodEndDate = DateUtils.addMonths(startPeriodDate, newDuringCount);
                } else if (Objects.equals(newDuringType, ErrandsService.PeriodDuringType.YEARS.toString())) {
                    newPeriodEndDate = DateUtils.addYears(startPeriodDate, newDuringCount);
                }
            }
            if (newPeriodEndDate != null) {
                DateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
                newPeriodEndDateText = formater.format(newPeriodEndDate);
            } else {
                nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_PERIOD_ENDLESS, Objects.equals(ErrandsService.PeriodicallyRadio.ENDLESS.toString(), newDateRadio));
            }
            if (!Objects.equals(oldPeriodEndDate, newPeriodEndDate)) {
                nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_PERIOD_END, newPeriodEndDate);
            }
            if (!Objects.equals(oldPeriodEndDateText, newPeriodEndDateText)) {
                nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_PERIOD_END_TEXT, newPeriodEndDateText);
            }
        }
    }
}
