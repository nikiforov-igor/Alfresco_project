package ru.it.lecm.eds.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.List;
import java.util.Map;

/**
 * Created by KKurets on 04.05.2017.
 */
public class CurrentEmployeeUnitsProcessor extends SearchQueryProcessor {

    OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    /*
	 * Usage example: {{CURRENT_EMPLOYEE_UNITS({onlyBoss:false})}}
	 */

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        boolean onlyBoss;
        List<NodeRef> units;
        if (params != null && params.get("onlyBoss") != null) {
            onlyBoss = Boolean.parseBoolean(params.get("onlyBoss").toString());
        } else {
            onlyBoss = false;
        }
        units = orgstructureBean.getEmployeeUnits(orgstructureBean.getCurrentEmployee(), onlyBoss);

        if (units != null && units.size() > 0) {
            for (int i = 0; i < units.size(); i++) {
                sbQuery.append("\"*").append(units.get(i)).append("*\"");
                if (i < units.size() - 1) {
                    sbQuery.append(" OR ");
                }
            }
        } else {
            sbQuery.append("\"NOT_REF\"");
        }
        return sbQuery.toString();
    }

}
