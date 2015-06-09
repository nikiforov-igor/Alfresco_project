/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.secretary.extensions;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretaryService;

/**
 *
 * @author ikhalikov
 */
public class SecretaryJavascriptExtension extends BaseWebScript {

	private SecretaryService secretaryService;
	private OrgstructureBean orgstructureService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setSecretaryService(SecretaryService secretaryService) {
		this.secretaryService = secretaryService;
	}

	public Scriptable getIgnoredStaffLists(ScriptNode employee) {
		NodeRef employeeNodeRef = employee.getNodeRef();
		return createScriptable(secretaryService.getChiefs(employeeNodeRef));
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

	public String getIgnoredEmployeesString(ScriptNode employee) {
		NodeRef employeeNodeRef = employee.getNodeRef();
		List<NodeRef> ignoredList = secretaryService.getChiefs(employeeNodeRef);
		ignoredList.add(employeeNodeRef);
		ignoredList.addAll(getAllBosses(employeeNodeRef));

		return Joiner.on(",").join(ignoredList);
	}

	public Scriptable getEmployeesByStaffList(Scriptable staffList) {
		List<NodeRef> staffs = getNodeRefsFromScriptableCollection(staffList);
		return createScriptable(secretaryService.employeesToStaff(staffs));
	}

	public ScriptNode getEffectiveEmployee(final ScriptNode employee) {
		return new ScriptNode(secretaryService.getEffectiveEmployee(employee.getNodeRef()), serviceRegistry, getScope());
	}
}
