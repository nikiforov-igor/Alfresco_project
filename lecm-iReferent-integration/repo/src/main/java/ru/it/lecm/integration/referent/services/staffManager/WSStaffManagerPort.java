package ru.it.lecm.integration.referent.services.staffManager;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.integration.referent.objects.*;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.jws.WebParam;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 24.09.13
 * Time: 16:25
 */

@javax.jws.WebService(name = "StaffManagerServicePort", serviceName = "StaffManager", portName = "StaffManagerServicePort", targetNamespace = "urn:DefaultNamespace", endpointInterface = "ru.it.lecm.integration.referent.services.staffManager.WSStaffManager")
public class WSStaffManagerPort implements WSStaffManager {
    private ObjectFactory objectFactory;
    private DocumentService documentService;
    private NodeService nodeService;
    private PersonService personService;
    private OrgstructureBean orgstructureService;

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public WSOGROUP getGroup(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return objectFactory.createWSOGROUP(id);
    }

    @Override
    public WSOCOLLECTION getOrganizations(@WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        NodeRef rootUnit = orgstructureService.getRootUnit();
        if (rootUnit != null) {
            WSOGROUP group = objectFactory.createWSOGROUP(rootUnit);
            WSOCOLLECTION.DATA data = objectFactory.createWSOCOLLECTIONDATA();
            data.getItem().add(group);
            return objectFactory.createWSOCOLLECTION(data);
        }
        return null;
    }

    @Override
    public WSOPERSON getPerson(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return objectFactory.createWSOPERSON(id);
    }

    @Override
    public WSOBJECT getPersonId(@WebParam(name = "PERSONLOGIN", partName = "PERSONLOGIN") String personlogin) {
        NodeRef employeeRef = orgstructureService.getEmployeeByPerson(personlogin);
        if (employeeRef != null) {
            return objectFactory.createWSOBJECT(employeeRef);
        }
        return null;
    }

    @Override
    public WSOCOLLECTION getPersons(@WebParam(name = "GROUPID", partName = "GROUPID") String groupid, @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL") int childslevel, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        WSOCOLLECTION.DATA data = objectFactory.createWSOCOLLECTIONDATA();
        List<NodeRef> employees;
        if (groupid != null) {
            if (NodeRef.isNodeRef(groupid)) {
                employees = orgstructureService.getUnitEmployees(new NodeRef(groupid));
            } else {
                employees = orgstructureService.getAllEmployees();
            }
            for (NodeRef employee : employees) {
                data.getItem().add(objectFactory.createWSOPERSON(employee));
            }
        }
        return objectFactory.createWSOCOLLECTION(data);
    }

    @Override
    public WSOGROUP getStructure(@WebParam(name = "GROUPID", partName = "GROUPID") String groupid, @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL") int childslevel, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        //TODO нужно переписать с учетом параметров
        return objectFactory.createWSOGROUP(groupid);
    }
}

