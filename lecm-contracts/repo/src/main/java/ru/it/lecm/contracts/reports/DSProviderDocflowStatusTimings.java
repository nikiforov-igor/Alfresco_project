package ru.it.lecm.contracts.reports;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.reports.utils.Utils;

import java.util.Date;

/**
 * Провайдер для построения отчёта «Сроки прохождения маршрута»
 * <p/>
 * Фильтры по:
 * •	Вид договора
 * •	Тематика договора
 * •	Контрагент
 * •	Дата регистрации проекта (Период)
 * <p/>
 * Измерение (одно из):
 * •	Вид договора
 * •	Тематика договора
 * •	Контрагент
 * •	Инициатор
 *
 * @author rabdullin
 */
public class DSProviderDocflowStatusTimings extends DSProviderDocflowStatusCounters {
    public DSProviderDocflowStatusTimings() {
        super();
    }

    /**
     * Вычисление суммарной длительности
     */
    @Override
    protected int adjustStatistic(final DocStatusGroup group, final String statusName, NodeRef docId) {
        /* время смены статуса */
        final Date dtChanged = (Date) getServices().getServiceRegistry().getNodeService().getProperty(docId, DocumentService.PROP_STATUS_CHANGED_DATE);

		/* счётчик длительности */
        final int daysDelta = Math.round(Utils.calcDurationInDays(dtChanged, new Date(), 0)); // ceil ?
        return group.incCounter(statusName, daysDelta);
    }
}
