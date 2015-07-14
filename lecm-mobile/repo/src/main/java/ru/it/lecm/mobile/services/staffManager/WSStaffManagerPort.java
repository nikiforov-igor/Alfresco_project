package ru.it.lecm.mobile.services.staffManager;

import javax.jws.WebParam;
import java.math.BigInteger;

/**
 * User: dbashmakov
 * Date: 14.07.2015
 * Time: 10:22
 */
@javax.jws.WebService(name = "StaffManagerServicePort",
        serviceName = "StaffManager",
        portName = "StaffManagerServicePort",
        targetNamespace = "urn:DefaultNamespace",
        endpointInterface = "ru.it.lecm.mobile.services.staffManager.WSStaffManager")
public class WSStaffManagerPort implements WSStaffManager {
    private ObjectFactory objectFactory;

    @Override
    public WSOFILE getitemfile() {
        return null;
    }

    @Override
    public WSOEDS getfakesign() {
        return null;
    }

    @Override
    public WSOPERSON getperson(@WebParam(name = "IDPERSON", partName = "IDPERSON")String idperson,
                               @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments,
                               @WebParam(name = "CONTEXT", partName = "CONTEXT") WSOCONTEXT context) {
        return objectFactory.createWSOPERSON(idperson);
    }

    @Override
    public WSOGROUP getgroup(String idgr, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getorganizations(boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOCOLLECTION getpersons(String iddep, boolean ismobject, BigInteger childslevel, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOGROUP getstructure(String idorg, BigInteger childslevel, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    @Override
    public WSOITEM getitem() {
        return null;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }
}
