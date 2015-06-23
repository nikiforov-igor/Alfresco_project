package ru.it.lecm.secretary.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.secretary.SecretaryService;

import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 16.06.2015
 * Time: 11:35
 */
public class GetSecretaryQueryProcessor extends SearchQueryProcessor {
    private SecretaryService secretaryService;
    private OrgstructureBean orgstructureBean;

    public void setSecretaryService(SecretaryService secretaryService) {
        this.secretaryService = secretaryService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();

        Object employeeParam = params != null ? params.get("employee") : null;
        NodeRef employee;
        if (employeeParam != null && NodeRef.isNodeRef((String) employeeParam)) {
            employee = new NodeRef((String) employeeParam);
        } else {
            employee = orgstructureBean.getCurrentEmployee();
        }

        boolean asList = false;
        Object asListParam = params != null ? params.get("asList") : null;
        if (asListParam != null) {
            asList = (boolean) asListParam;
        }
        if (employee != null) {
            List<NodeRef> secretaries = secretaryService.getSecretaries(employee);
            if (secretaries.isEmpty()) {
                sbQuery.append(asList? "''" : "'NOT_REF'");
            }
            for (NodeRef secretary : secretaries) {
                if (sbQuery.length() > 0) {
                    sbQuery.append(asList ? "," : " OR ");
                }
                sbQuery.append(!asList ? "'*" : "").append(secretary.toString()).append(!asList ? "*'" : "");
            }
        }

        return sbQuery.toString();
    }
}
