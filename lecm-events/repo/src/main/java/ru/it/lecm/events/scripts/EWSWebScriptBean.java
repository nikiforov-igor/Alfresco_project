package ru.it.lecm.events.scripts;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ISO8601DateFormat;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.events.beans.EWSEvent;
import ru.it.lecm.events.beans.EWSService;
import ru.it.lecm.events.beans.EmployeeAvailability;

/**
 *
 * @author vmalygin
 */
public class EWSWebScriptBean extends BaseWebScript {

	private EWSService ewsService;

	public void setEwsService(EWSService ewsService) {
		this.ewsService = ewsService;
	}

	private Scriptable processEvents(List<EmployeeAvailability> availabilities) {
		Object[] results = new Object[availabilities.size()];
		int i = 0;
		for(EmployeeAvailability availability : availabilities) {
			List<EWSEvent> events = availability.getEvents();
			Scriptable obj = Context.getCurrentContext().newObject(getScope());
			Object[] arr = new Object[events.size()];
			int j = 0;
			for (EWSEvent event : events) {
				Scriptable busyTime = Context.getCurrentContext().newObject(getScope());
				busyTime.put("title", busyTime, "");
				busyTime.put("start", busyTime, ISO8601DateFormat.format(event.getStart()));
				busyTime.put("startDate", busyTime, DateFormatISO8601TZ.format(event.getStart()));
				busyTime.put("end", busyTime, ISO8601DateFormat.format(event.getEnd()));
				busyTime.put("endDate", busyTime, DateFormatISO8601TZ.format(event.getEnd()));
				arr[j++] = busyTime;
			}
			obj.put("employee", obj, availability.getEmployeeRef().toString());
			obj.put("busytime", obj, Context.getCurrentContext().newArray(getScope(), arr));
			results[i++] = obj;
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}

	public Scriptable getEvents(ScriptNode employee, String fromDate, String toDate) {
		List<EmployeeAvailability> availabilities = ewsService.getEvents(employee.getNodeRef(), ISO8601DateFormat.parse(fromDate), ISO8601DateFormat.parse(toDate));
		return processEvents(availabilities);
	}

	public Scriptable getEvents(Scriptable employees, String fromDate, String toDate) {
		List<Object> list = (List<Object>)getValueConverter().convertValueForJava(employees);
		List<NodeRef> employeeRefList = new ArrayList<>(list.size());
		for (Object item : list) {
			if (item instanceof NodeRef) {
				employeeRefList.add((NodeRef)item);
			} else {
				employeeRefList.add(new NodeRef(item.toString()));
			}
		}
		List<EmployeeAvailability> availabilities = ewsService.getEvents(employeeRefList, ISO8601DateFormat.parse(fromDate), ISO8601DateFormat.parse(toDate));
		return processEvents(availabilities);
	}
}
