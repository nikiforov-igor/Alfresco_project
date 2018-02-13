package ru.it.lecm.errands.shedule;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * User: AIvkin
 * Date: 09.11.2017
 * Time: 14:37
 */
public class PeriodicalErrandsToExecutedShedule extends BaseTransactionalSchedule {

    private final static Logger logger = LoggerFactory.getLogger(PeriodicalErrandsToExecutedShedule.class);

    private DocumentService documentService;
    private ErrandsService errandsService;

    public PeriodicalErrandsToExecutedShedule() {
        super();
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
        return getErrandsOnExecution();
    }

    private List<NodeRef> getErrandsOnExecution() {
        logger.debug("Start periodical errands transit to executed schedule");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currentDate = calendar.getTime();

        List<QName> types = new ArrayList<>(1);
        types.add(ErrandsService.TYPE_ERRANDS);
        List<String> statuses = new ArrayList<>(1);
        statuses.add(errandsService.getErrandStatusName(ErrandsService.ERRANDS_STATUS.PERIODICALLY));

        String filters = "@lecm\\-errands\\:period\\-end:[MIN to \"" + BaseBean.DateFormatISO8601.format(currentDate) + "\"] OR " +
                " @lecm\\-errands\\:periodically\\-radio:\"" + ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString() + "\"";

        List<NodeRef> periodicalErrands = documentService.getDocumentsByFilter(types, null, statuses, filters, null);

        // Фильтруем по количеству повторов/ по наличию отложенных
        periodicalErrands = periodicalErrands.stream().filter(new Predicate<NodeRef>() {
            @Override
            public boolean test(NodeRef nodeRef) {
                boolean isReiterationCountValid = true;
                final String periodicallyRadio = (String) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_PERIODICALLY_RADIO);
                if (ErrandsService.PeriodicallyRadio.REPEAT_COUNT.toString().equals(periodicallyRadio)) {
                    List<NodeRef> childErrands = errandsService.getChildErrands(nodeRef);
                    Integer reiterationCount = (Integer) nodeService.getProperty(nodeRef, ErrandsService.PROP_ERRANDS_REITERATION_COUNT);
                    isReiterationCountValid = childErrands != null && reiterationCount != null && childErrands.size() == reiterationCount;
                }
                boolean isDelayedCountValid = !errandsService.hasDelayedPeriodicalErrands(nodeRef);

                return isReiterationCountValid && isDelayedCountValid;
            }
        }).collect(Collectors.toList());

        logger.debug("Found " + periodicalErrands.size() + " periodical rules");
        return periodicalErrands;
    }
}
