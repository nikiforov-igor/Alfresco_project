package ru.it.lecm.events.processor;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.List;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 29.04.2015
 * Time: 11:08
 */
public class CurrentResourcesProcessor extends SearchQueryProcessor{
    private static final Logger logger = LoggerFactory.getLogger(CurrentResourcesProcessor.class);

    private OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    public String getQuery(Map<String, Object> params) {

        NodeRef employee = orgstructureBean.getCurrentEmployee();
        String fieldName = (String) params.get("fieldName");
        fieldName = "@" + fieldName.replaceAll("-|:", "\\\\$0");

        StringBuffer result = new StringBuffer();
        List<AssociationRef> associationRefs = nodeService.getSourceAssocs(employee, EventsService.ASSOC_EVENT_RESOURCE_RESPONSIBLE);
        for (AssociationRef assoc : associationRefs) {
            Boolean available = (Boolean) nodeService.getProperty(assoc.getSourceRef(), EventsService.PROP_EVENT_RESOURCE_AVAILABLE);
            if (available != null && available) {
                result.append(fieldName).append(":\"*").append(assoc.getSourceRef().toString()).append("*\" OR ");
            }
        }

        if (result.length() > 0) {
            return "(" + result.substring(0, result.length() - 4) + ")";
        } else {
            return "ID:\"NOT_REF\"";
        }
    }
}
