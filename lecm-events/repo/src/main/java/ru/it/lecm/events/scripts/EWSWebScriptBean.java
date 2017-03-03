package ru.it.lecm.events.scripts;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ISO8601DateFormat;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
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

	private ArrayList<NativeObject> processEvents(List<EmployeeAvailability> availabilities) {
		ArrayList<NativeObject> results = new ArrayList<>();
		for(EmployeeAvailability availability : availabilities) {
			List<EWSEvent> events = availability.getEvents();
			NativeObject obj = new NativeObject();
			NativeArray arr = new NativeArray(events.size());
			for (EWSEvent event : events) {
				NativeObject busyTime = new NativeObject();
				busyTime.put("title", busyTime, "");
				busyTime.put("start", busyTime, ISO8601DateFormat.format(event.getStart()));
				busyTime.put("startDate", busyTime, DateFormatISO8601.format(event.getStart()));
				busyTime.put("end", busyTime, ISO8601DateFormat.format(event.getEnd()));
				busyTime.put("endDate", busyTime, DateFormatISO8601.format(event.getEnd()));
			}
			obj.put("employee", obj, availability.getEmployeeRef().toString());
			obj.put("busytime", obj, arr);
			results.add(obj);
		}
		return results;
	}

	public Scriptable getEvents(ScriptNode employee, String fromDate, String toDate) {
		List<EmployeeAvailability> availabilities = ewsService.getEvents(employee.getNodeRef(), ISO8601DateFormat.parse(fromDate), ISO8601DateFormat.parse(toDate));
		return (Scriptable)getValueConverter().convertValueForScript(serviceRegistry, getScope(), null, processEvents(availabilities));
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
		return (Scriptable)getValueConverter().convertValueForScript(serviceRegistry, getScope(), null, processEvents(availabilities));
	}
}
