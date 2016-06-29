package ru.it.lecm.workflow.reservation;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public final class ReservationAspectsModel {

	public final static String RESERVATION_ASPECTS_NAMESPACE = "http://www.it.ru/logicECM/reservation-aspects/1.0";
	public final static String RESERVATION_ASPECTS_PREFIX = "lecm-reservation-aspects";
	public final static QName ASPECT_IS_RESERVATION_RUNNING = QName.createQName(RESERVATION_ASPECTS_NAMESPACE, "isReservationRunningAspect");
	public final static QName PROP_IS_RESERVATION_RUNNING = QName.createQName(RESERVATION_ASPECTS_NAMESPACE, "isReservationRunning");
	public final static QName PROP_RESERVE_TASK_MESSAGE = QName.createQName(RESERVATION_ASPECTS_NAMESPACE, "reserveTaskMessage");

	private ReservationAspectsModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ReservationAspectsModel class.");
	}
}
