package ru.it.lecm.workflow.review.schedule;

import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.wcalendar.IWorkCalendar;
import ru.it.lecm.workflow.review.api.ReviewService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * Created by dkuchurkin on 11.05.2016.
 */
public class ReviewNotificationSchedule extends BaseTransactionalSchedule {

    private static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    private DocumentService documentService;
    private ReviewService reviewService;
    private IWorkCalendar calendarBean;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
        List<QName> types = Collections.singletonList(ReviewService.TYPE_REVIEW_TS_REVIEW_TABLE_ITEM);
        List<String> paths = Collections.singletonList(documentService.getDocumentsFolderPath());
        String filters = "@lecm\\-review\\-ts\\:review\\-state:\"NOT_REVIEWED\"";

        int reviewTerm = reviewService.getReviewTerm();
        int notificationTerm = reviewService.getReviewNotificationTerm();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date upDate = calendarBean.getNextWorkingDateByDays(calendar.getTime(), 0 - (reviewTerm - notificationTerm));
        String startDateUpLimit = (new SimpleDateFormat(DATE_FORMAT_ISO8601)).format(upDate);
        filters = filters + " AND @lecm\\-review\\-ts\\:review\\-start\\-date: [MIN to \"" + startDateUpLimit + "\"]";

        return documentService.getDocumentsByFilter(types, paths, null, filters, null);
    }

}
