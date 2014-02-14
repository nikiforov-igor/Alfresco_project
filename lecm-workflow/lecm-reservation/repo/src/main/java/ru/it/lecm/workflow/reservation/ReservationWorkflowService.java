package ru.it.lecm.workflow.reservation;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.workflow.api.LecmWorkflowService;

/**
 *
 * @author vmalygin
 */
public interface ReservationWorkflowService extends LecmWorkflowService {

	void setReservationActive(final NodeRef bpmPackage, boolean isActive);

	List<NodeRef> getRegistrars(final NodeRef bpmPackage, final String registrarRole);

	void setEmptyRegnum(final NodeRef bpmPackage);

	Notification prepareNotificationAboutEmptyRegistrars(final NodeRef bpmPackage, final NodeRef reservateInitiator);
}
