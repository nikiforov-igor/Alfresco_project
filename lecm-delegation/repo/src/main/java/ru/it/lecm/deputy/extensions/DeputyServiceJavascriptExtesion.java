/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.deputy.extensions;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.deputy.DeputyService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author ikhalikov
 */
public class DeputyServiceJavascriptExtesion extends BaseWebScript {

	DeputyService deputyService;
	OrgstructureBean orgstructureService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setDeputyService(DeputyService deputyService) {
		this.deputyService = deputyService;
	}

	public ScriptNode getSettings() {
		return new ScriptNode(deputyService.getDeputySettingsNode(), serviceRegistry, getScope());
	}

	public ScriptNode addCompleteDeputy(ScriptNode chiefRef, ScriptNode deputyRef) {
		return new ScriptNode(deputyService.createFullDeputy(chiefRef.getNodeRef(), deputyRef.getNodeRef()), serviceRegistry, getScope());
	}

	public void removeDeputy(ScriptNode chiefRef, ScriptNode deputyEmployeeRef) {
		deputyService.removeFullDeputy(chiefRef.getNodeRef(), deputyEmployeeRef.getNodeRef());
	}

	public ScriptNode addDeputy(ScriptNode chiefRef, ScriptNode deputyEmployeeRef, String subjectsList) {
		List<NodeRef> subjects = new ArrayList<>();
		String[] tmp = subjectsList.split(",");
		for (String nodeStr : tmp) {
			subjects.add(new NodeRef(nodeStr));
		}

		return new ScriptNode(deputyService.createDeputy(chiefRef.getNodeRef(), deputyEmployeeRef.getNodeRef(), subjects), serviceRegistry, getScope());
	}

	private List<NodeRef> getUnitBosses(NodeRef unitRef) {
		List<NodeRef> result = new ArrayList<>();
		NodeRef bossStaff = orgstructureService.getBossStaff(unitRef);
		NodeRef bossEmployee = orgstructureService.getEmployeeByPosition(bossStaff);
		if(bossEmployee != null) {
			result.add(bossEmployee);
		}

		NodeRef parentUnit;

		while((parentUnit = orgstructureService.getParentUnit(unitRef)) != null) {
			bossStaff = orgstructureService.getBossStaff(parentUnit);
			bossEmployee = orgstructureService.getEmployeeByPosition(bossStaff);
			if(bossEmployee != null) {
				result.add(bossEmployee);
			}
			unitRef = parentUnit;
		}

		return result;
	}

	private List<NodeRef> getAllBosses(NodeRef employee) {
		List<NodeRef> employeeUnits = orgstructureService.getEmployeeUnits(employee, false);
		List<NodeRef> result = new ArrayList<>();
		for (NodeRef employeeUnit : employeeUnits) {
			result.addAll(getUnitBosses(employeeUnit));
		}

		return result;
	}

	public String getIgnoredString(ScriptNode employee) {
		NodeRef employeeNodeRef = employee.getNodeRef();
		List<NodeRef> ignoredList = deputyService.getAllChiefs(employeeNodeRef);
		ignoredList.add(employeeNodeRef);
		ignoredList.addAll(getAllBosses(employeeNodeRef));

		return Joiner.on(",").join(ignoredList);
	}

	public boolean isDeputyAcceptable(ScriptNode docNodeRef, ScriptNode deputyRef) {
		return deputyService.isDeputyAcceptable(docNodeRef.getNodeRef(), deputyRef.getNodeRef());
	}

	public Scriptable getChiefs(ScriptNode employeeRef) {
		return createScriptable(deputyService.getPrimaryChiefs(employeeRef.getNodeRef()));
	}

}
