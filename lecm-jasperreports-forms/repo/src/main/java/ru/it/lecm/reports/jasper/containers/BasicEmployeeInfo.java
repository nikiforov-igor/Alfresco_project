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
 	public static final QName PROP_EMPLOYEE_NAME_FIRST = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-first-name");
 	public static final QName PROP_EMPLOYEE_NAME_MIDDLE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-middle-name");
 	public static final QName PROP_EMPLOYEE_NAME_LAST = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-last-name");

	final public NodeRef employeeId;

	/** ФИО */
	public String firstName, middleName, lastName;

	/** Название основной должности */
	public String staffName;

	/** id основной должности */
	public NodeRef staffId;

	/** Название подразделения по основной должности */
	public String unitName;

	/** id подразделения по основной должности */
	public NodeRef unitId;

	/** характеристики связанного системного пользователя */ 
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

	/**
	 * @return Фамилия И.О.
	 */
	public String ФамилияИО() {
		/// JASPER jrxml format: $F{col_Employee.LastName}+ " "+ ($F{col_Employee.FirstName}+ " ").substring( 0, 1 )+ "."+ ($F{col_Employee.MiddleName}+ " ").substring(0,1) + "."
		final StringBuilder result = new StringBuilder();
		result.append( Фамилия());
		// Далее первые буквы Имени и отчества в верхнем регистре
		if (!Utils.isStringEmpty(this.firstName))
			result.append( String.format(" %s.", ИмяИнициал()) );
		if (!Utils.isStringEmpty(this.middleName))
			result.append( String.format("%s.", ОтчествоИнициал()) );
		return result.toString();
	}

	/**
	 * @return (none null) Фамилия или пустая строка 
	 */
	public String Фамилия() {
		return Utils.coalesce( this.lastName, "");
	}

	/**
	 * @return (none null) Первая буква фамилии в верхнем регистре или пусто 
	 */
	public String ФамилияИнициал() {
		return (Utils.isStringEmpty(this.lastName))
				? ""
				: this.lastName.toUpperCase().substring(0,1);
	}

	/**
	 * @return (none null) Имя или пустая строка 
	 */
	public String Имя() {
		return Utils.coalesce( this.firstName, "");
	}

	/**
	 * @return (none null) Первая буква имени в верхнем регистре или пусто 
	 */
	public String ИмяИнициал() {
		return (Utils.isStringEmpty(this.firstName))
				? ""
				: this.firstName.toUpperCase().substring(0,1);
	}

	/**
	 * @return (none null) Отчество или пустая строка 
	 */
	public String Отчество() {
		return Utils.coalesce(this.middleName, "");
	}

	/**
	 * @return (none null) Первая буква отчества в верхнем регистре или пусто 
	 */
	public String ОтчествоИнициал() {
		return (Utils.isStringEmpty(this.middleName))
				? ""
				: this.middleName.toUpperCase().substring(0,1);
	}

	/**
	 * Подгрузить данные для employeeId.
	 * @param nodeSrv обязательный параметр
	 * @param orgSrv необязательный, задаётся если требуются параметры об Организации и Должности Сотрудника
	 */
	public void loadProps(NodeService nodeSrv, OrgstructureBean orgSrv) {
		if (employeeId != null) {
			this.firstName = Utils.coalesce( nodeSrv.getProperty( employeeId, PROP_EMPLOYEE_NAME_FIRST), "");
			this.middleName = Utils.coalesce( nodeSrv.getProperty( employeeId, PROP_EMPLOYEE_NAME_MIDDLE), "");
			this.lastName = Utils.coalesce( nodeSrv.getProperty( employeeId, PROP_EMPLOYEE_NAME_LAST), "");

			this.unitName = "";
			this.staffName = "";
			if (orgSrv != null) {
				final List<NodeRef> staffList = orgSrv.getEmployeeStaffs(employeeId);
				if (staffList != null && !staffList.isEmpty()) {
					// идём по олжностям пока не встретим с не пустым названием ... 
					for(NodeRef staffRef: staffList) {
						this.staffId = staffRef; // занимаемая Должность

						// название Подразделения ...
						this.unitId = orgSrv.getUnitByStaff(this.staffId);
						this.unitName = (this.unitId == null) ? "" : Utils.coalesce( nodeSrv.getProperty( this.unitId, PROP_ORGUNIT_NAME), "");

						// получить словарное значение Должности по штатной позиции 
						final NodeRef dpId = orgSrv.getPositionByStaff(this.staffId);
						this.staffName = (dpId == null) ? "" : Utils.coalesce( nodeSrv.getProperty( dpId, PROP_DP_INFO), "");

						if ( !Utils.isStringEmpty(this.unitName) && !Utils.isStringEmpty(this.staffName))
							break; // нашли непустую ...
					}
				}
			}
		}
	}
}