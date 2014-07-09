package ru.it.lecm.documents.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: dbashmakov
 * Date: 07.07.2014
 * Time: 17:01
 */
public class EmployeesByUnitsProcessor extends SearchQueryProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EmployeesByUnitsProcessor.class);
    public static final String UNITS_PARAM = "units";
    public static final String PROPS_PARAM = "props";

    private OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Set<NodeRef> unitsSet = new HashSet<NodeRef>();
        Object unitsParam = params != null ? params.get(UNITS_PARAM) : null;
        if (unitsParam != null) {
            if (unitsParam instanceof JSONArray) {
                try {
                    JSONArray unitsFilter = (JSONArray) unitsParam;
                    for (int j = 0; j < unitsFilter.length(); j++) {
                        String unit = ((String) unitsFilter.get(j)).trim();
                        if (NodeRef.isNodeRef(unit)) {
                            unitsSet.add(new NodeRef(unit));
                        }
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                logger.warn("EmployeesByUnitsProcessor (EMPLOYEES_BY_UNITS) param 'units' in not array. Units list set to empty...");
            }
        }

        Set<String> propertiesSet = new HashSet<String>();
        Object propsParam = params != null ? params.get(PROPS_PARAM) : null;
        if (propsParam != null) {
            if (propsParam instanceof JSONArray) {
                try {
                    JSONArray propsFilter = (JSONArray) propsParam;
                    for (int j = 0; j < propsFilter.length(); j++) {
                        String prop = ((String) propsFilter.get(j)).trim();
                        if (!prop.isEmpty()) {
                            propertiesSet.add(prop);
                        }
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                logger.warn("EmployeesByUnitsProcessor (EMPLOYEES_BY_UNITS) param 'props' in not array. Props list set to empty...");
            }
        }

        //получаем список сотрудников по выбранным подразделениям
        Set<NodeRef> employeesSet = new HashSet<NodeRef>();

        for (NodeRef unitRef : unitsSet) {
            List<NodeRef> unitEmployees = orgstructureBean.getUnitEmployees(unitRef);
            employeesSet.addAll(unitEmployees);
        }

        if (employeesSet.size() > 0) {
            StringBuilder employeeQuery = new StringBuilder();
            for (NodeRef employeeRef : employeesSet) {
                employeeQuery.append("\"").append(employeeRef.toString().replace(":", "\\:")).append("\"").append(" OR ");
            }
            if (employeeQuery.length() > 0) {
                employeeQuery.delete(employeeQuery.length() - 4, employeeQuery.length());
            }

            for (String property : propertiesSet) {
                String employeeProperty = property.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                sbQuery.append("@").append(employeeProperty).append(":(").append(employeeQuery.toString()).append(")").append(" OR ");
            }
            if (sbQuery.length() > 0) {
                sbQuery.delete(sbQuery.length() - 4, sbQuery.length());
            }
        }

        sbQuery.append(sbQuery.length() > 0 ? "" : "ID:\"NOT_REF\"");

        return sbQuery.toString();
    }
}
