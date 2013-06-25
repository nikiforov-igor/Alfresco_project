package ru.it.lecm.reports.jasper;

import java.util.Date;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.reports.jasper.utils.Utils;

/**
 * Провайдер для построения отчёта «Сроки прохождения маршрута»
 * 
 * Фильтры по:
 *   •	Вид договора
 *   •	Тематика договора
 *   •	Контрагент
 *   •	Дата регистрации проекта (Период)
 *
 * Измерение (одно из):
 *   •	Вид договора
 *   •	Тематика договора
 *   •	Контрагент
 *   •	Инициатор
 * 
 * @author rabdullin
 */
public class DSProviderDocflowStatusTimings extends DSProviderDocflowStatusCounters
{

	// см как основной ru.it.lecm.reports.jasper.DSProviderReestrDogovorov

	// private static final Logger logger = LoggerFactory.getLogger(DSProviderDocflowStatusTimings.class);

	public DSProviderDocflowStatusTimings() {
		super();
		super.setPreferedType(TYPE_CONRACT);
	}


	final static String ATTR_STATUS_CHANGED = "lecm-document:status-changed-date"; // атрибут со временем изменения статуса

	/**
	 * Вычисление суммарной длительности
	 */
	@Override
	protected int adjustStatistic( final DocStatusGroup group, final String statusName, NodeRef docId)
	{
		/* время смены статуса */
		final QName qnSTATUS_CHANGED = QName.createQName(ATTR_STATUS_CHANGED, getServices().getServiceRegistry().getNamespaceService());
		final Date dtChanged  = (Date) getServices().getServiceRegistry().getNodeService().getProperty( docId, qnSTATUS_CHANGED);

		/* счётчик длительности */
		final Date now = new Date();
		final int daysDelta = Math.round( Utils.calcDurationInDays(dtChanged, now, 0)); // ceil ?

		return group.incCounter(statusName, daysDelta);
	}
}
