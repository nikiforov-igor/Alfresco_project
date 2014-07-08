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
public class DocsAuthorsByUnitProcessor extends SearchQueryProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocsAuthorsByUnitProcessor.class);

    private OrgstructureBean orgstructureBean;

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Set<NodeRef> unitsSet = new HashSet<NodeRef>();
        Object unitsFilter = params != null ? params.get("units") : null;
        if (unitsFilter != null) {
            if (unitsFilter instanceof JSONArray) {
                try {
                    JSONArray currentUnitsFilter = (JSONArray) unitsFilter;
                    for (int j = 0; j < currentUnitsFilter.length(); j++) {
                        String unit = ((String) currentUnitsFilter.get(j)).trim();
                        if (NodeRef.isNodeRef(unit)) {
                            unitsSet.add(new NodeRef(unit));
                        }
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        String authorPropsStr = params != null ? (String) params.get("authorProps") : "lecm-document:author-assoc-ref";

        //получаем список сотрудников по выбранным подразделениям
        Set<NodeRef> employeesSet = new HashSet<NodeRef>();

        for (NodeRef unitRef : unitsSet) {
            List<NodeRef> unitEmployees = orgstructureBean.getUnitEmployees(unitRef);
            employeesSet.addAll(unitEmployees);
        }

        if (employeesSet.size() > 0) {
            for (String field : authorPropsStr.split(",")) {
                String authorProp = field.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                for (NodeRef employeeRef : employeesSet) {
                    sbQuery.append("@").append(authorProp).append(":\"").append(employeeRef.toString().replace(":", "\\:")).append("\"").append(" OR ");
                }
            }
        }
        if (unitsSet.size() > 0) { // подразделение выбрано - должны попасть только документы для сотрудников этих подразделений, значит добавляем условие
            sbQuery.append("ID:\"NOT_REF\"");
        }

        return sbQuery.toString();
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }
}
