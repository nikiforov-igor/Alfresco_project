package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.repo.jscript.app.BasePropertyDecorator;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * User: dbashmakov
 * Date: 22.05.13
 * Time: 15:09
 */
public class EmployeeNamePropertyDecorator extends BasePropertyDecorator {
    private ServiceRegistry services;

    private PersonService personService = null;
	private NodeService nodeService = null;

    private OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.services = serviceRegistry;
        this.personService = serviceRegistry.getPersonService();
		this.nodeService = serviceRegistry.getNodeService();
    }


	@Override
	@SuppressWarnings("unchecked")
    public JSONAware decorate(QName propertyName, NodeRef nodeRef, Serializable value) {
        String username = value.toString();
        String firstName;
        String middleName;
        String lastName;
        String ref = "";
		JSONObject map = new JSONObject();
        map.put("userName", username);

        if (this.personService.personExists(username) && !username.equals("System")) {
            NodeRef employeeRef = orgstructureService.getEmployeeByPerson(username);
            if (employeeRef != null) {
                Map<QName, Serializable> properties = this.nodeService.getProperties(employeeRef);
                firstName = (String) properties.get(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
                lastName = (String) properties.get(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
                middleName = (String) properties.get(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
                ref = employeeRef.toString();
            } else {
                NodeRef personRef = this.personService.getPerson(username);
                Map<QName, Serializable> properties = this.nodeService.getProperties(personRef);
                firstName = (String) properties.get(ContentModel.PROP_FIRSTNAME);
                lastName = (String) properties.get(ContentModel.PROP_LASTNAME);
                middleName = "";
                ref = personRef.toString();
            }
        } else if (username.equals("System") || username.startsWith("System@")) {
            firstName = "System";
            lastName = "";
            middleName = "";
        } else {
            map.put("isDeleted", true);
            return map;
        }

        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("middleName", middleName);
        map.put("nodeRef", ref);
        map.put("displayName", ((lastName != null ? lastName + " " : "") + (firstName != null ? firstName + " " : "") + (middleName != null ? middleName + " " : "")).replaceAll("^\\s+|\\s+$", ""));
        return map;
    }

}

