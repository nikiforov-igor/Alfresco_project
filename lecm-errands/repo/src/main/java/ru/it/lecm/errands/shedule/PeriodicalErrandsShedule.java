package ru.it.lecm.errands.shedule;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * User: PMelnikov
 * Date: 05.08.14
 * Time: 15:24
 */
public class PeriodicalErrandsShedule extends BaseTransactionalSchedule {

    private final static Logger logger = LoggerFactory.getLogger(PeriodicalErrandsShedule.class);

    private DocumentService documentService;
    private ErrandsService errandsService;

    public PeriodicalErrandsShedule() {
        super();
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
        return getErrandsOnExecution();
    }

    private List<NodeRef> getErrandsOnExecution() {
        logger.info("Start periodical errands schedule");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date endPeriod = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        Date startPeriod = calendar.getTime();

        String startPeriodDate = BaseBean.DateFormatISO8601.format(startPeriod);
        String endPeriodDate = BaseBean.DateFormatISO8601.format(endPeriod);

        List<QName> types = new ArrayList<>(1);
        types.add(ErrandsService.TYPE_ERRANDS);
        List<String> statuses = new ArrayList<>(1);

        statuses.add(errandsService.getErrandStatusName(ErrandsService.ERRANDS_STATUSES.ERRAND_PERIODICALLY_STATUS));

       String filters = "@lecm\\-errands\\:period\\-start:[MIN to \"" + startPeriodDate + "\"] AND (@lecm\\-errands\\:period\\-end:[\"" + endPeriodDate + "\" to MAX] OR @lecm\\-errands\\:periodically\\-radio:" +
                "\"" + ErrandsService.PeriodicallyRadio.ENDLESS.toString() + "\" OR" +
                " @lecm\\-errands\\:periodically\\-radio:\"" + ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString() + "\")";

        Set<NodeRef> periodicalErrands = new HashSet<>(documentService.getDocumentsByFilter(types, null, statuses, filters, null));
        logger.info("Found " + periodicalErrands.size() + " periodical rules");


        // Фильтруем по количеству повторов
        periodicalErrands = periodicalErrands.stream().filter(new Predicate<NodeRef>() {
            @Override
            public boolean test(NodeRef nodeRef) {
                final String periodicallyRadio = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_PERIODICALLY_RADIO);
                if (ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString().equals(periodicallyRadio)) {
                    List<NodeRef> childErrands = errandsService.getChildErrands(nodeRef);
                    Integer reiterationCount = (Integer) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_REITERATION_COUNT);
                    return childErrands != null && reiterationCount != null && childErrands.size() < reiterationCount;
                }
                return true;
            }
        }).collect(Collectors.toSet());

        // Так же в результат добавляем периодические поручения, создание которых было отложено на сегодня
        final Map<String, Set<NodeRef>> delayedErrandsByDate = errandsService.getDelayedErrandsByDate();
        final String todayDateStr = DateFormatUtils.format(new Date(), "dd-MM-yyyy");
        final Set<NodeRef> delayedErrandsForToday = delayedErrandsByDate.get(todayDateStr);
        if (delayedErrandsForToday != null) {
            periodicalErrands.addAll(delayedErrandsForToday);
            // Удаляем из списка отложенных на сегодня, чтобы исключить повторную обработку
            delayedErrandsByDate.remove(todayDateStr);
            getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                @Override
                public Void execute() throws Throwable {
                    errandsService.setDelayedErrandsByDate(delayedErrandsByDate);
                    return null;
                }
            }, false, true);
            logger.debug("Found " + delayedErrandsForToday.size() + " delayed periodical errands.");
        }
        logger.info("Final list of periodical errands to process: {} ", periodicalErrands);

        return new ArrayList<NodeRef>(periodicalErrands);
    }
}
