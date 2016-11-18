package ru.it.lecm.contracts.schedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 * User: AZinovin
 * Date: 12.05.16
 * Time: 10:51
 */
public class ContractStageEndDateNotificationSchedule extends BaseTransactionalSchedule {

    public static final QName TYPE_CONTRACT_STAGE = QName.createQName("http://www.it.ru/logicECM/contract/table-structure/1.0", "stage");
    private DocumentService documentService;

    private NotificationsService notificationsService;

    DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public ContractStageEndDateNotificationSchedule() {
        super();
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final String MIN = DateFormatISO8601.format(calendar.getTime());

        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, notificationsService.getSettingsNDays());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        final String MAX = DateFormatISO8601.format(calendar.getTime());

        String filters;
        List<QName> types = new ArrayList<>();
        List<String> paths = new ArrayList<>();

        types.add(TYPE_CONTRACT_STAGE);

        paths.add(documentService.getDocumentsFolderPath());

        filters = "@lecm\\-contract\\-table\\-structure\\:end\\-date: [\"" + MIN + "\" to \"" + MAX + "\"]  AND NOT lecm\\-contract\\-table\\-structure\\:stage\\-status:\"Закрыт\"";

        return documentService.getDocumentsByFilter(types, paths, null, filters, null);
    }

}