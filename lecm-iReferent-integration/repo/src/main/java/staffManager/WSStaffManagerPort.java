package staffManager;

import javax.jws.WebParam;

/**
 * User: dbashmakov
 * Date: 24.09.13
 * Time: 16:25
 */

@javax.jws.WebService(name = "StaffManagerServicePort", serviceName = "StaffManager", portName = "StaffManagerServicePort", targetNamespace = "urn:DefaultNamespace", endpointInterface = "staffManager.WSStaffManager")
public class WSStaffManagerPort implements staffManager.WSStaffManager{
    private ObjectFactory objectFactory;

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public WSOGROUP getGroup(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        WSOGROUP group =  objectFactory.createWSOGROUP();
        group.setTYPE("TYPE");
        group.setTITLE("TITLE");
        group.setID("ID");
        group.setLEADER(objectFactory.createWSOPERSON());
        return group;
    }

    @Override
    public WSOCOLLECTION getOrganizations(@WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return objectFactory.createWSOCOLLECTION();
    }

    @Override
    public WSOPERSON getPerson(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        WSOPERSON person =  objectFactory.createWSOPERSON();
        person.setTITLE("PERSON_TITLE");
        person.setID("PERSON_ID");
        person.setFIRSTNAME("IVAN");
        person.setMIDDLENAME("IVANOVICH");
        person.setLASTNAME("IVANOV");
        return person;
    }

    @Override
    public WSOBJECT getPersonId(@WebParam(name = "PERSONLOGIN", partName = "PERSONLOGIN") String personlogin) {
        WSOBJECT id = objectFactory.createWSOBJECT();
        id.setID("ID");
        return id;
    }

    @Override
    public WSOCOLLECTION getPersons(@WebParam(name = "GROUPID", partName = "GROUPID") String groupid, @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL") int childslevel, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return objectFactory.createWSOCOLLECTION();
    }

    @Override
    public WSOGROUP getStructure(@WebParam(name = "GROUPID", partName = "GROUPID") String groupid, @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL") int childslevel, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return getGroup(null,true,null);
    }

    @Override
    public String ping() {
        return "BOOM!!";
    }
}

