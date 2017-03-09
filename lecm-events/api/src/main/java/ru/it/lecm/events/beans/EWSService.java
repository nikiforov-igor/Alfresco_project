package ru.it.lecm.events.beans;

import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface EWSService {
	List<EmployeeAvailability> getEvents(NodeRef employeeRef, Date fromDate, Date toDate);
	List<EmployeeAvailability> getEvents(List<NodeRef> employeeRefList, Date fromDate, Date toDate);
}
