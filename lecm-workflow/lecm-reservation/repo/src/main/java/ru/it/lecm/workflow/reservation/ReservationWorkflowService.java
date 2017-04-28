package ru.it.lecm.workflow.reservation;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.workflow.api.LecmWorkflowService;

/**
 *
 * @author vmalygin
 */
public interface ReservationWorkflowService extends LecmWorkflowService {
	String RESERVATION_ASPECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/reservation-aspects/1.0";

	QName PROP_IS_RESERVATION_RUNNING = QName.createQName(RESERVATION_ASPECTS_NAMESPACE_URI, "isReservationRunning");

	void setReservationActive(final NodeRef bpmPackage, boolean isActive);

	List<NodeRef> getRegistrars(final NodeRef bpmPackage, final String registrarRole);

	void setEmptyRegnum(final NodeRef bpmPackage);

	Notification prepareNotificationAboutEmptyRegistrars(final NodeRef bpmPackage, final NodeRef reservateInitiator);

	boolean isReservationRunning(NodeRef document);
}
