package ru.it.lecm.events.ews;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.events.beans.EWSService;
import ru.it.lecm.events.beans.EmployeeAvailability;

/**
 *
 * @author vmalygin
 */
public class EWSDummyServiceImpl implements EWSService {

	private final static Logger logger = LoggerFactory.getLogger(EWSServiceImpl.class);

	@Override
	public List<EmployeeAvailability> getEvents(NodeRef employeeRef, Date fromDate, Date toDate) {
		logger.warn("Receiving availability information from MS Exchange server is disabled. Please activate \"lecm.events.ews.enabled\" option.");
		return new ArrayList<>();
	}

	@Override
	public List<EmployeeAvailability> getEvents(List<NodeRef> employeeRefList, Date fromDate, Date toDate) {
		logger.warn("Receiving availability information from MS Exchange server is disabled. Please activate \"lecm.events.ews.enabled\" option.");
		return new ArrayList<>();
	}
}
