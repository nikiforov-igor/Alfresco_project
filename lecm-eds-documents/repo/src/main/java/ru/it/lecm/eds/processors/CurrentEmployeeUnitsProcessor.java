package ru.it.lecm.eds.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by KKurets on 04.05.2017.
 *
 * Получение запроса с nodRef-ами всех подразделений для которых выполняется условие:
 * (текущий пользователь входит в подразделение и onlyBoss=false и includeHigherUnits=false)
 *  или (текущий пользователь является руководителем подразделения и onlyBoss=true и includeHigherUnits=false)
 *  или (текущий пользователь является руководителем дочернего подразделения и onlyBoss=true и includeHigherUnits=true)
 *
 * Usage example: {{CURRENT_EMPLOYEE_UNITS({onlyBoss:false,includeHigherUnits:false})}}
 *
 */
public class CurrentEmployeeUnitsProcessor extends SearchQueryProcessor {

    OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    public String getQuery(Map<String, Object> params) {
        final StringBuilder sbQuery = new StringBuilder();
        final boolean onlyBoss = extractParam("onlyBoss", params, boolean.class, false);
        final boolean includeHigherUnits = extractParam("includeHigherUnits", params, boolean.class, false);
        final List<NodeRef> units = orgstructureBean.getEmployeeUnits(orgstructureBean.getCurrentEmployee(), onlyBoss);
        final List<NodeRef> resultUnits = new ArrayList<>(units);

        if (includeHigherUnits) {
            for (NodeRef unit : units) {
                List<NodeRef> subUnits = orgstructureBean.getSubUnits(unit, false /*Включая неактивные подразделения*/);
                // выбираем дочерние подразделения без руководителя
                resultUnits.addAll(filterUnitsWithoutBoss(subUnits));
            }
        }
        appendUnitsToQuery(sbQuery, resultUnits);

        final String queryStr = sbQuery.toString();
        return queryStr.isEmpty() ? "\"NOT_REF\"" : queryStr;
    }

    private List<NodeRef> filterUnitsWithoutBoss(List<NodeRef> units) {
        final List<NodeRef> unitsWithoutBoss = new ArrayList<>();
        for (NodeRef subUnit : units) {
            if (orgstructureBean.getBossStaff(subUnit) == null) {
                unitsWithoutBoss.add(subUnit);
            }
        }
        return unitsWithoutBoss;
    }

    /**
     * Gets param from params Map and casts to target type. If param does not presented in params Map
     * or can't be cast to target type, then default value is returned.
     *
     * @param name   param name
     * @param params Map of all params
     * @param targetClass  a Class to cast param value to
     * @param defVal default value if param is absent or can't be cast
     * @param <T>    generic target type of param
     * @return casted param object or defVal if param is absent or can't be cast
     */
    private <T> T extractParam(final String name, final Map<String, Object> params, final Class<T> targetClass, final T defVal) {
        T val = null;
        if (params != null && params.get(name) != null) {
            try {
                val = (T) ConvertUtils.convert(params.get(name), targetClass);
            } catch (ConversionException ignore) {
            }
        }
        return val != null ? val : defVal;
    }

    private void appendUnitsToQuery(final StringBuilder sbQuery, final List<NodeRef> units) {
        if (units != null && units.size() > 0) {
            for (int i = 0; i < units.size(); i++) {
                sbQuery.append("\"*").append(units.get(i)).append("*\"");
                if (i < units.size() - 1) {
                    sbQuery.append(" OR ");
                }
            }
        }
    }

}
