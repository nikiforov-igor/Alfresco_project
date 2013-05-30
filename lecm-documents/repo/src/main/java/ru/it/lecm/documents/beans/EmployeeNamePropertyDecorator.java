package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.app.PropertyDecorator;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 22.05.13
 * Time: 15:09
 */
public class EmployeeNamePropertyDecorator implements PropertyDecorator {
    private ServiceRegistry services;
    private NodeService nodeService = null;
    private PersonService personService = null;

    private OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.services = serviceRegistry;
        this.nodeService = serviceRegistry.getNodeService();
        this.personService = serviceRegistry.getPersonService();
    }

    public Serializable decorate(NodeRef nodeRef, String propertyName, Serializable value) {
        String username = value.toString();
        String firstName;
        String lastName;
        String ref = "";
        Map<String, Serializable> map = new LinkedHashMap<String, Serializable>(4);
        map.put("userName", username);

        if (this.personService.personExists(username)) {
            NodeRef employeeRef = orgstructureService.getEmployeeByPerson(username);
            if (employeeRef != null) {
                Map<QName, Serializable> properties = this.nodeService.getProperties(employeeRef);
                firstName = (String) properties.get(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
                lastName = (String) properties.get(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
                ref = employeeRef.toString();
            } else {
                NodeRef personRef = this.personService.getPerson(username);
                Map<QName, Serializable> properties = this.nodeService.getProperties(personRef);
                firstName = (String) properties.get(ContentModel.PROP_FIRSTNAME);
                lastName = (String) properties.get(ContentModel.PROP_LASTNAME);
                ref = personRef.toString();
            }
        } else if (username.equals("System") || username.startsWith("System@")) {
            firstName = "System";
            lastName = "User";
        } else {
            map.put("isDeleted", true);
            return (Serializable) map;
        }

        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("nodeRef", ref);
        map.put("displayName", (firstName != null ? firstName + " " : "" + (lastName != null ? lastName : "")).replaceAll("^\\s+|\\s+$", ""));
        return (Serializable) map;
    }
}

