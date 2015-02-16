package ru.it.lecm.reports.jasper.containers;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.utils.Utils;

/**
 * Контейнер для типизирования данных по Сотрудникам
 * <p/>
 * Уникальность/Сравнение строго по employeeId.
 *
 * @author rabdullin
 */
public class BasicEmployeeInfo {
    // название Должностной Позиции
    public static final QName PROP_DP_INFO = ContentModel.PROP_NAME;

    public static final QName PROP_ORGUNIT_NAME = OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME;

    // <!-- Сотрудник организации -->
    public static final QName PROP_EMPLOYEE_NAME_FIRST = OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME;
    public static final QName PROP_EMPLOYEE_NAME_MIDDLE = OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME;
    public static final QName PROP_EMPLOYEE_NAME_LAST = OrgstructureBean.PROP_EMPLOYEE_LAST_NAME;

    public NodeRef employeeId = null;

    /**
     * ФИО
     */
    public String firstName = null;
    public String middleName = null;
    public String lastName = null;

    /**
     * Название основной должности
     */
    public String staffName = null;

    /**
     * id основной должности
     */
    public NodeRef staffId = null;

    /**
     * Название подразделения по основной должности
     */
    public String unitName = null;

    /**
     * id подразделения по основной должности
     */
    public NodeRef unitId = null;

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
        final StringBuilder result = new StringBuilder();
        result.append(Фамилия());
        // Далее первые буквы Имени и отчества в верхнем регистре
        if (!Utils.isStringEmpty(firstName)) {
            result.append(String.format(" %s.", ИмяИнициал()));
        }
        if (!Utils.isStringEmpty(middleName)) {
            result.append(String.format("%s.", ОтчествоИнициал()));
        }
        return result.toString();
    }

    /**
     * @return (none null) Фамилия или пустая строка
     */
    public String Фамилия() {
        return Utils.coalesce(lastName, "");
    }

    /**
     * @return (none null) Имя или пустая строка
     */
    public String Имя() {
        return Utils.coalesce(firstName, "");
    }

    /**
     * @return (none null) Первая буква имени в верхнем регистре или пусто
     */
    public String ИмяИнициал() {
        return (Utils.isStringEmpty(firstName))
                ? ""
                : firstName.toUpperCase().substring(0, 1);
    }

    /**
     * @return (none null) Отчество или пустая строка
     */
    public String Отчество() {
        return Utils.coalesce(middleName, "");
    }

    /**
     * @return (none null) Первая буква отчества в верхнем регистре или пусто
     */
    public String ОтчествоИнициал() {
        return (Utils.isStringEmpty(middleName))
                ? ""
                : middleName.toUpperCase().substring(0, 1);
    }

    /**
     * Подгрузить данные для employeeId.
     *
     * @param nodeSrv обязательный параметр
     * @param orgSrv  необязательный, задаётся если требуются параметры об Организации и Должности Сотрудника
     */
    public void loadProps(NodeService nodeSrv, OrgstructureBean orgSrv) {
        if (employeeId != null) {
            firstName = Utils.coalesce(nodeSrv.getProperty(employeeId, PROP_EMPLOYEE_NAME_FIRST), "");
            middleName = Utils.coalesce(nodeSrv.getProperty(employeeId, PROP_EMPLOYEE_NAME_MIDDLE), "");
            lastName = Utils.coalesce(nodeSrv.getProperty(employeeId, PROP_EMPLOYEE_NAME_LAST), "");

            unitName = "";
            staffName = "";
            if (orgSrv != null) {
                final NodeRef staffList = orgSrv.getEmployeePrimaryStaff(employeeId);
                if (staffList != null) {
                    // занимаемая Должность
                    staffId = staffList;
                    // получить словарное значение Должности по штатной позиции
                    NodeRef dpId = orgSrv.getPositionByStaff(staffList);
                    staffName = (dpId == null) ? "" : Utils.coalesce(nodeSrv.getProperty(dpId, PROP_DP_INFO), "");

                    unitId = orgSrv.getUnitByStaff(staffList);

                    // название Подразделения ...
                    unitName = (unitId != null) ? Utils.coalesce(nodeSrv.getProperty(unitId, PROP_ORGUNIT_NAME), "") : "";
                }
            }
        }
    }
}