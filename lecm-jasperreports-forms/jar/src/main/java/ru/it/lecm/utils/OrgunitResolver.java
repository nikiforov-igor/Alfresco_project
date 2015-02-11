package ru.it.lecm.utils;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * User: pmelnikov
 * Date: 17.12.14
 * Time: 14:46
 */
public class OrgunitResolver {


    private static OrgstructureBean orgstructureBean;
    private static NodeService nodeService;

    public static String getOrgunitCode(String nodeRef) {
        NodeRef node = new NodeRef(nodeRef);
        int level = getLevel(node);
        if (level < 4) {
            return "";
        } else {
            NodeRef orgunit = orgstructureBean.getPrimaryOrgUnit(node);
            int iteration = level - 4;
            for (int i = 0; i < iteration; i++) {
                orgunit = orgstructureBean.getParentUnit(orgunit);
            }
            return nodeService.getProperty(orgunit, OrgstructureBean.PROP_UNIT_CODE).toString();
        }
    }

    public static String getOrgunitName(String nodeRef) {
        NodeRef node = new NodeRef(nodeRef);
        int level = getLevel(node);
        if (level < 4) {
            return "";
        } else {
            NodeRef orgunit = orgstructureBean.getPrimaryOrgUnit(node);
            int iteration = level - 4;
            for (int i = 0; i < iteration; i++) {
                orgunit = orgstructureBean.getParentUnit(orgunit);
            }
            return nodeService.getProperty(orgunit, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME).toString();
        }
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    private static int getLevel(NodeRef node) {
        NodeRef orgunit = orgstructureBean.getPrimaryOrgUnit(node);
        NodeRef rootUnit = orgstructureBean.getRootUnit();
        int level = 1;
        while (!orgunit.equals(rootUnit)) {
            level++;
            orgunit = orgstructureBean.getParentUnit(orgunit);
        }
        return level;
    }

}