package ru.it.lecm.events.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 25.03.2015
 * Time: 14:44
 */
public class EventsServiceImpl extends BaseBean implements EventsService {
    private DictionaryBean dictionaryBean;
    private OrgstructureBean orgstructureBean;

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(EVENTS_ROOT_ID);
    }

    @Override
    public NodeRef getEventLocation(NodeRef event) {
        return findNodeByAssociationRef(event, ASSOC_EVENT_LOCATION, TYPE_EVENT_LOCATION, ASSOCIATION_TYPE.TARGET);
    }

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    @Override
    public List<NodeRef> getAvailableEventLocations() {
        List<NodeRef> results = null;
        NodeRef locationsDic = dictionaryBean.getDictionaryByName("Места проведения мероприятий");
        if (locationsDic != null) {
            results = new ArrayList<>();

            List<NodeRef> locations = dictionaryBean.getChildren(locationsDic);
            NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();

            for (NodeRef location: locations) {
                NodeRef locationOrganization = findNodeByAssociationRef(location, ASSOC_EVENT_LOCATION_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
                Integer locationPrivelegeLevel = (Integer) nodeService.getProperty(location, PROP_EVENT_LOCATION_PRIVILEGE_LEVEL);

                if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(locationOrganization) &&
                        (locationPrivelegeLevel == 0 || getCurrentUserLocationPrivilegeLevel() >= locationPrivelegeLevel)) {
                    results.add(location);
                }
            }
        }
        return results;
    }

    private NodeRef getCurrentEmployeeOrganization() {
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        if (currentEmployee != null) {
            return orgstructureBean.getEmployeeOrganization(currentEmployee);
        }
        return null;
    }

    private int getCurrentUserLocationPrivilegeLevel() {
        int result = 0;
        NodeRef locationsPrivilegeLevelDic = dictionaryBean.getDictionaryByName("Уровни привилегий для выбора мест проведения");
        if (locationsPrivilegeLevelDic != null) {
            List<NodeRef> privilegeLevels = dictionaryBean.getChildren(locationsPrivilegeLevelDic);
            for (NodeRef privilegeLevel: privilegeLevels) {
                NodeRef role = findNodeByAssociationRef(privilegeLevel, ASSOC_EVENT_LOCATION_PL_ROLE, null, ASSOCIATION_TYPE.TARGET);
                if (role != null) {
                    String roleId = orgstructureBean.getBusinessRoleIdentifier(role);
                    if (roleId != null && orgstructureBean.isCurrentEmployeeHasBusinessRole(roleId)) {
                        Integer level = (Integer) nodeService.getProperty(privilegeLevel, PROP_EVENT_LOCATION_PL_LEVEL);
                        if (level != null && level > result) {
                            result = level;
                        }
                    }
                }
            }
        }
        return result;
    }
}
