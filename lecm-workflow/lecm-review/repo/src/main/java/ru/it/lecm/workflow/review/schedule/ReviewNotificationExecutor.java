package ru.it.lecm.workflow.review.schedule;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.workflow.review.api.ReviewService;

import java.io.Serializable;
import java.util.*;

/**
 * Created by dkuchurkin on 11.05.2016.
 */
public class ReviewNotificationExecutor extends ActionExecuterAbstractBase {

    private NotificationsService notificationsService;
    private ReviewService reviewService;
    private NodeService nodeService;
    private IWorkCalendar calendarBean;

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();

        Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
        Date startDate = (Date) properties.get(ReviewService.PROP_REVIEW_TS_REVIEW_START_DATE);

        if (startDate != null) {
            String notificationTemplate;
            Date reviewTerm = calendarBean.getNextWorkingDateByDays(startDate, reviewService.getReviewTerm());

            if (reviewTerm.compareTo(now) > 0) {
                notificationTemplate = "REVIEW_APPROACHING_DEADLINE";
            } else {
                notificationTemplate = "REVIEW_EXCEEDED_DEADLINE";
            }

            NodeRef document = reviewService.getDocumentByReviewTableItem(nodeRef);

            List<AssociationRef> reviewerAssocs = nodeService.getTargetAssocs(nodeRef, ReviewService.ASSOC_REVIEW_TS_REVIEWER);
            NodeRef recipient = reviewerAssocs.get(0).getTargetRef();

            Map<String, Object> templateObjects = new HashMap<>();
            List<AssociationRef> initiatorAssocs = nodeService.getTargetAssocs(nodeRef, ReviewService.ASSOC_REVIEW_TS_INITIATOR);
            templateObjects.put("initiator", initiatorAssocs.get(0).getTargetRef());

            notificationsService.sendNotificationByTemplate(document, Collections.singletonList(recipient), notificationTemplate, templateObjects);
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }
}
