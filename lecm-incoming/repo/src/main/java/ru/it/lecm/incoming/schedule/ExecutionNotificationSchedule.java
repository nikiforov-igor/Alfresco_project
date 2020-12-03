package ru.it.lecm.incoming.schedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.incoming.beans.IncomingServiceImpl;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: pmelnikov
 * Date: 21.01.14
 * Time: 13:17
 */
public class ExecutionNotificationSchedule extends BaseTransactionalSchedule {

    private DocumentService documentService;
    private IWorkCalendar calendarBean;
    private DocumentGlobalSettingsService documentGlobalSettings;

    public ExecutionNotificationSchedule() {
        super();
    }

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDocumentGlobalSettings(DocumentGlobalSettingsService documentGlobalSettings) {
        this.documentGlobalSettings = documentGlobalSettings;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
        return getIncomingOnExecution();
    }

    private List<NodeRef> getIncomingOnExecution() {
        Date start = new Date(0);

        Calendar calendar = Calendar.getInstance();
        int days = documentGlobalSettings.getSettingsNDays();
        Date end = calendarBean.getNextWorkingDate(new Date(), days, Calendar.DAY_OF_MONTH);
        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        end = calendar.getTime();

        String filters;
        List<QName> types = new ArrayList<QName>();
        List<String> paths = new ArrayList<String>();
        List<String> statuses = new ArrayList<String>();

        types.add(IncomingServiceImpl.TYPE_INCOMING);
        paths.add(documentService.getDocumentsFolderPath());

        DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        filters = "@lecm\\-statemachine\\-aspects\\:is\\-draft: false AND (NOT ASPECT: \"lecm-statemachine-aspects:is-final-aspect\" OR @lecm\\-statemachine\\-aspects\\:is\\-final: false) AND @lecm\\-eds\\-document\\:execution\\-date: [\"" + DateFormatISO8601.format(start) + "\" to \"" + DateFormatISO8601.format(end) + "\"]";
        return documentService.getDocumentsByFilter(types, paths, statuses, filters, null);
    }

}
