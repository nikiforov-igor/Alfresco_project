package ru.it.lecm.ord.api;

import java.util.Date;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author dbayandin
 */
public interface ORDNotificationService {

	public final static String DATE_FORMAT = "dd.MM.yyyy";
	
	//количество оставшихся до дедлайна рабочих дней, при котором надо начинать слать уведомления
	public final static int DEADLINE_ALARM_PERIOD = 2;
	
	public void notifyInitiatorDeadlineComing(NodeRef documentRef, NodeRef initiatorRef, Date deadlineDate);

	public void notifyAssigneeDeadlineComing(NodeRef documentRef, NodeRef employee, Date dueDate);

	public void notifyInitiatorDeadlineComing(NodeRef documentRef);
	
	public void notifyAssigneesDeadlineComing(NodeRef documentRef);
	
}
