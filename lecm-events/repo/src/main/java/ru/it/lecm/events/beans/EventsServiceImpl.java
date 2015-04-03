package ru.it.lecm.events.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
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
         public List<NodeRef> getAvailableUserLocations() {
        List<NodeRef> results = null;
        NodeRef locationsDic = dictionaryBean.getDictionaryByName("Места проведения мероприятий");
        if (locationsDic != null) {
            results = new ArrayList<>();

            List<NodeRef> locations = dictionaryBean.getChildren(locationsDic);
            NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();
            int currentUserLocationPrivilegeLevel = getCurrentUserLocationPrivilegeLevel();

            for (NodeRef location: locations) {
                NodeRef locationOrganization = findNodeByAssociationRef(location, ASSOC_EVENT_LOCATION_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
                Integer locationPrivilegeLevel = (Integer) nodeService.getProperty(location, PROP_EVENT_LOCATION_PRIVILEGE_LEVEL);

                if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(locationOrganization) &&
                        (locationPrivilegeLevel == 0 || currentUserLocationPrivilegeLevel >= locationPrivilegeLevel)) {
                    results.add(location);
                }
            }
        }
        return results;
    }

    @Override
    public List<NodeRef> getAvailableUserResources() {
        List<NodeRef> results = null;
        NodeRef resourcesDic = dictionaryBean.getDictionaryByName("Ресурсы");
        if (resourcesDic != null) {
            results = new ArrayList<>();

            List<NodeRef> resources = dictionaryBean.getChildren(resourcesDic);
            NodeRef currentEmployeeOrganization = getCurrentEmployeeOrganization();
            int currentUserResourcesPrivilegeLevel = getCurrentUserResourcesPrivilegeLevel();

            for (NodeRef resource: resources) {
                NodeRef resourceOrganization = findNodeByAssociationRef(resource, ASSOC_EVENT_RESOURCE_ORGANIZATION, null, ASSOCIATION_TYPE.TARGET);
                Integer resourcePrivilegeLevel = (Integer) nodeService.getProperty(resource, PROP_EVENT_RESOURCE_PRIVILEGE_LEVEL);

                if (currentEmployeeOrganization != null && currentEmployeeOrganization.equals(resourceOrganization) &&
                        (resourcePrivilegeLevel == 0 || currentUserResourcesPrivilegeLevel >= resourcePrivilegeLevel)) {
                    results.add(resource);
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
        return getCurrentUserDicPrivilegeLevel("Уровни привилегий для выбора мест проведения", ASSOC_EVENT_LOCATION_PL_ROLE, PROP_EVENT_LOCATION_PL_LEVEL);
    }

    private int getCurrentUserResourcesPrivilegeLevel() {
        return getCurrentUserDicPrivilegeLevel("Уровни привилегий для выбора ресурсов", ASSOC_EVENT_RESOURCES_PL_ROLE, PROP_EVENT_RESOURCES_PL_LEVEL);
    }

    private int getCurrentUserDicPrivilegeLevel(String dicName, QName dicRoleAssoc, QName dicLevelProp) {
        int result = 0;
        NodeRef privilegeLevelDic = dictionaryBean.getDictionaryByName(dicName);
        if (privilegeLevelDic != null) {
            List<NodeRef> privilegeLevels = dictionaryBean.getChildren(privilegeLevelDic);
            for (NodeRef privilegeLevel: privilegeLevels) {
                NodeRef role = findNodeByAssociationRef(privilegeLevel, dicRoleAssoc, null, ASSOCIATION_TYPE.TARGET);
                if (role != null) {
                    String roleId = orgstructureBean.getBusinessRoleIdentifier(role);
                    if (roleId != null && orgstructureBean.isCurrentEmployeeHasBusinessRole(roleId)) {
                        Integer level = (Integer) nodeService.getProperty(privilegeLevel, dicLevelProp);
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
