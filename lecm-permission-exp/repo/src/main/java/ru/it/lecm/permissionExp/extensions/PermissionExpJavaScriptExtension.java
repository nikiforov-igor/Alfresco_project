/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.permissionExp.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.AuthorityService;
import org.json.JSONArray;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.json.JSONException;
import ru.it.lecm.base.beans.LecmURLService;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author ikhalikov
 */
public class PermissionExpJavaScriptExtension extends BaseScopableProcessorExtension {

    private ServiceRegistry serviceRegistry;
    private AuthorityService authorityService;
    private OrgstructureBean orgstructureService;
    private PersonService personService;
    private NodeService nodeService;
    private DictionaryBean dictionaryService;
    private LecmURLService urlService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setUrlService(LecmURLService urlService) {
        this.urlService = urlService;
    }

    public JSONArray getEmployees() throws JSONException {
        nodeService = serviceRegistry.getNodeService();
        List<NodeRef> employees = orgstructureService.getAllEmployees();
        JSONArray result = new JSONArray();
        for (NodeRef employeeRef : employees) {
            Map<String, String> employeeInfo = new HashMap<String, String>();
            employeeInfo.put("NodeRef", employeeRef.toString());
            employeeInfo.put("ShortName", (String) nodeService.getProperty(employeeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME));
            result.put(employeeInfo);
        }
        return result;
    }

    private String getLink(NodeRef nodeRef, String name) {
        String Url = urlService.getLinkWithContext(urlService.getLinkURL() + "?nodeRef=" + nodeRef);
        return "<a href=\"" + Url + "\">" + name + "</a>";
    }

    public String getRolesExplanation(final String nodeRef) {
        NodeRef employee = new NodeRef(nodeRef);
        nodeService = serviceRegistry.getNodeService();
        Set<NodeRef> directRoles = orgstructureService.getEmployeeDirectRoles(employee);
        Set<NodeRef> unitRoles = orgstructureService.getEmployeeUnitRoles(employee);
        Set<NodeRef> DPRoles = orgstructureService.getEmployeeDPRoles(employee);
        Set<NodeRef> WGRoles = orgstructureService.getEmployeeWGRoles(employee);
        Map<NodeRef, List<NodeRef>> delegateRoles = orgstructureService.getEmployeeDelegatedRolesWithOwner(employee);
        Set<NodeRef> subRoles = new HashSet<NodeRef>();


        String employeeName = (String) nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
        StringBuilder result = new StringBuilder();

        if (!directRoles.isEmpty()) {
            result.append("Сотрудник ").append(getLink(employee, employeeName)).append(" непосредственно включён в бизнес роли: <br>");

            for (NodeRef roleRef : directRoles) {
                result.append(getLink(roleRef, (String) nodeService.getProperty(roleRef, ContentModel.PROP_NAME))).append("<br>");
            }
        }


        if (!unitRoles.isEmpty()) {
            result.append("Через подразделения ");
            for (NodeRef unitRef : orgstructureService.getEmployeeUnits(employee, false)) {
                result.append(getLink(unitRef, (String)nodeService.getProperty(unitRef, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME))).append(" ");
            }
            result.append(" включён в роли: <br>");

            for (NodeRef roleRef : unitRoles) {
                result.append(getLink(roleRef, (String) nodeService.getProperty(roleRef, ContentModel.PROP_NAME))).append("<br>");
            }
        }

        if (!DPRoles.isEmpty()) {
            result.append("Через должностную позицию ");
            for (NodeRef DPRef : orgstructureService.getEmployeeStaffs(employee)) {
                List<AssociationRef> DPList = nodeService.getTargetAssocs(DPRef, OrgstructureBean.ASSOC_ELEMENT_MEMBER_POSITION);
                for (AssociationRef DP : DPList) {
                    result.append(getLink(DPRef, (String)nodeService.getProperty(DP.getTargetRef(), ContentModel.PROP_NAME))).append(" ");
                }
            }
            result.append(" включён в бизнес роли: <br>");
            for (NodeRef roleRef : DPRoles) {
                result.append(getLink(roleRef, (String) nodeService.getProperty(roleRef, ContentModel.PROP_NAME))).append("<br>");
            }
        }

        if (!WGRoles.isEmpty()) {
            result.append("Через рабочие группы включён в: <br>");

            for (NodeRef roleRef : WGRoles) {
                result.append(getLink(roleRef, (String) nodeService.getProperty(roleRef, ContentModel.PROP_NAME))).append("<br>");
            }
        }

        if (orgstructureService.isBoss(employee)) {

            for (NodeRef sub : orgstructureService.getBossSubordinate(employee)) {
                subRoles.addAll(orgstructureService.getEmployeeRoles(sub, true, true));
            }

            result.append("Как руководитель отделов ");
            for (NodeRef unitRef : orgstructureService.getEmployeeUnits(employee, true)) {
                result.append(getLink(unitRef, (String)nodeService.getProperty(unitRef, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME))).append(" ");
            }
            result.append(" включён в бизнес роли: <br>");

            for (NodeRef roleRef : subRoles) {
                result.append(getLink(roleRef, (String) nodeService.getProperty(roleRef, ContentModel.PROP_NAME))).append("<br>");
            }
        }

        if (!delegateRoles.isEmpty()) {
            result.append("Как делегат: <br>");

            for (Map.Entry<NodeRef, List<NodeRef>> entry : delegateRoles.entrySet()) {
                NodeRef owner = entry.getKey();
                List<NodeRef> list = entry.getValue();
                result.append("Для сотрудника ");
                result.append(getLink(owner, (String)nodeService.getProperty(owner, ContentModel.PROP_NAME))).append("<br>");
                for (NodeRef role : list) {
                    result.append(getLink(role, (String) nodeService.getProperty(role, ContentModel.PROP_NAME))).append("<br>");
                }

            }
        }



        return result.toString();
    }
}
