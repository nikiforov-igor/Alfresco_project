package ru.it.lecm.reports.jasper.containers;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.utils.Utils;

/**
 * Контейнер для типизирования данных по Сотрудникам
 * 
 * Уникальность/Сравнение строго по employeeId.
 *
 * @author rabdullin
 */
public class BasicEmployeeInfo {
	// название Должностной Позиции
	// "lecm-orgstr:staffPosition"::"lecm-orgstr:staffPosition-code"
	public static final QName PROP_DP_INFO = ContentModel.PROP_NAME; // QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "staffPosition-code");

	// "lecm-orgstr:organization-element"::"element-short-name"
	public static final QName PROP_ORGUNIT_NAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "element-short-name");

	// "lecm-orgstr:organization-unit"::"unit-code"
	public static final QName PROP_ORGUNIT_CODE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "unit-code");

	// <!-- Сотрудник организации -->
 	public static final QName PROP_EMPLOYEE_NAME1 = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-first-name");
 	public static final QName PROP_EMPLOYEE_NAME2 = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-middle-name");
 	public static final QName PROP_EMPLOYEE_NAME3 = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-last-name");

	final public NodeRef employeeId;

	// ФИО
	public String firstName, middleName, lastName;

	// Название основной должности и соот-го подразделения
	public String staffName, unitName;
	public NodeRef staffId, unitId;

	// характеристики связанного системного пользователя 
	public String userLogin;

	public BasicEmployeeInfo(NodeRef employeeId) {
		super();
		this.employeeId = employeeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((employeeId == null) ? 0 : employeeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BasicEmployeeInfo other = (BasicEmployeeInfo) obj;
		if (employeeId == null) {
			if (other.employeeId != null)
				return false;
		} else if (!employeeId.equals(other.employeeId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BasicEmployeeInfo [employeeId=" + employeeId + ", firstName="
				+ firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", staffName=" + staffName + ", unitName="
				+ unitName + "]";
	}

	public void loadProps(NodeService nodeSrv, OrgstructureBean orgSrv) {
		if (employeeId != null) {
			this.firstName = Utils.coalesce( nodeSrv.getProperty( employeeId, PROP_EMPLOYEE_NAME1), "");
			this.middleName = Utils.coalesce( nodeSrv.getProperty( employeeId, PROP_EMPLOYEE_NAME2), "");
			this.lastName = Utils.coalesce( nodeSrv.getProperty( employeeId, PROP_EMPLOYEE_NAME3), "");

			if (orgSrv != null) {
				final List<NodeRef> staffList = orgSrv.getEmployeeStaffs(employeeId);
				if (staffList != null && !staffList.isEmpty()) {
					this.staffId = staffList.get(0); // занимаемая Должность

					// название Подразделения ...
					this.unitId = orgSrv.getUnitByStaff(this.staffId);
					this.unitName = (this.unitId == null) ? "" : Utils.coalesce( nodeSrv.getProperty( this.unitId, PROP_ORGUNIT_NAME), "");

					// получить словарное значение Должности по штатной позиции 
					final NodeRef dpId = orgSrv.getPositionByStaff(this.staffId);
					this.staffName = (dpId == null) ? "" : Utils.coalesce( nodeSrv.getProperty( dpId, PROP_DP_INFO), "");
				}
			}
		}
	}
}