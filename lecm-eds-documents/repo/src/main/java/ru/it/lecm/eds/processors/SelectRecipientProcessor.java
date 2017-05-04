package ru.it.lecm.eds.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.List;
import java.util.Map;

/**
 * Created by KKurets on 04.05.2017.
 */
public class SelectRecipientProcessor extends SearchQueryProcessor {

    OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    /*
	 * Usage example: {{SELECT_RECIPIENT({user:'*#current-user*', onlyBoss:false})}}
	 */

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Object user = params != null ? params.get("user") : null;
        Object onlyBoss = params != null ? params.get("onlyBoss") : null;

        if (user != null && onlyBoss != null) {
            sbQuery.append("\"").append(user.toString()).append("\"");
            List<NodeRef> units;
            if ((boolean) onlyBoss) {
                units = orgstructureBean.getEmployeeUnits(orgstructureBean.getCurrentEmployee(), true);
            } else {
                units = orgstructureBean.getEmployeeUnits(orgstructureBean.getCurrentEmployee(), false);
            }
            if (units != null && units.size() > 0) {
                for (int i = 0; i < units.size(); i++) {
                    sbQuery.append(" OR ").append("\"*").append(units.get(i)).append("*\"");
                }
            }
        } else {
            sbQuery.append("\"NOT_REF\"");
        }
        return sbQuery.toString();
    }

}
