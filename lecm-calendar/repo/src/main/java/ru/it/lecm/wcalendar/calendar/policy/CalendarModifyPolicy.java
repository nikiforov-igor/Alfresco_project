package ru.it.lecm.wcalendar.calendar.policy;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.wcalendar.CalendarCategory;
import ru.it.lecm.wcalendar.calendar.ICalendar;

/**
 * Полиси, срабатывающее на изменение проперти у объектов типа working-days и
 * non-working-days и добавляющее сообщение в бизнес-журнал.
 *
 * @author vlevin
 */
public class CalendarModifyPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private ICalendar wCalendarService;
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private final static Logger logger = LoggerFactory.getLogger(CalendarModifyPolicy.class);

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "WCalService", wCalendarService);

        logger.info("Initializing CalendarModifyPolicy");
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ICalendar.TYPE_SPECIAL_DAYS, new JavaBehaviour(this, "onUpdateProperties"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ICalendar.TYPE_CALENDAR, new JavaBehaviour(this, "onUpdateProperties"));
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setwCalendarService(ICalendar wCalService) {
        this.wCalendarService = wCalService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
        final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
        // для рабочих и выходных дней
        final String prevDay = (String) before.get(ICalendar.PROP_SPECIAL_DAY_DAY);
        final String curDay = (String) after.get(ICalendar.PROP_SPECIAL_DAY_DAY);
        final String prevReason = (String) before.get(ICalendar.PROP_SPECIAL_DAY_REASON);
        final String curReason = (String) after.get(ICalendar.PROP_SPECIAL_DAY_REASON);
        // для календарей на год
        final String prevComment = (String) before.get(ICalendar.PROP_CALENDAR_COMMENT);
        final String curComment = (String) after.get(ICalendar.PROP_CALENDAR_COMMENT);
        if ((curActive != null && curActive != prevActive) || (curDay != null && !curDay.equals(prevDay)) || (curReason != null && !curReason.equals(prevReason))
                || (curComment != null && !curComment.equals(prevComment))) {
            QName nodeType = nodeService.getType(nodeRef);
            if (nodeType.equals(ICalendar.TYPE_WORKING_DAYS) || nodeType.equals(ICalendar.TYPE_NON_WORKING_DAYS)) {
                String name = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
                String day = (String) nodeService.getProperty(nodeRef, ICalendar.PROP_SPECIAL_DAY_DAY);
                String tmp = day.substring(0, 2) + "." + day.substring(2, 4);
                nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, tmp);
            }
            wCalendarService.addBusinessJournalRecord(nodeRef, CalendarCategory.SET_CALENDAR);
            logger.debug(String.format("Policy CalendarModifyPolicy invoked on %s", nodeRef.toString()));
        }
    }
}
