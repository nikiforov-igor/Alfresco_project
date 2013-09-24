package staffManager;

import javax.jws.WebParam;

/**
 * User: dbashmakov
 * Date: 24.09.13
 * Time: 16:25
 */

@javax.jws.WebService(name = "StaffManagerServicePort", serviceName = "StaffManager", portName = "StaffManagerServicePort", targetNamespace = "urn:DefaultNamespace", endpointInterface = "staffManager.WSStaffManager")
public class WSStaffManagerPort implements staffManager.WSStaffManager{
    @Override
    public WSOGROUP getGroup(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        WSOGROUP wsogroup = new WSOGROUP();
        wsogroup.setID("TEST");
        wsogroup.setTITLE("TITLE-TEST");
        wsogroup.setTYPE("ONLY TEST TYPE");
        return wsogroup;
    }

    @Override
    public WSOCOLLECTION getOrganizations(@WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WSOPERSON getPerson(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WSOBJECT getPersonId(@WebParam(name = "PERSONLOGIN", partName = "PERSONLOGIN") String personlogin) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WSOCOLLECTION getPersons(@WebParam(name = "GROUPID", partName = "GROUPID") String groupid, @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL") int childslevel, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WSOGROUP getStructure(@WebParam(name = "GROUPID", partName = "GROUPID") String groupid, @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL") int childslevel, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String ping() {
        return "BOOM!!";
    }
}

