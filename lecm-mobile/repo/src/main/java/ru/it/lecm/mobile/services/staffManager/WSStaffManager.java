
package ru.it.lecm.mobile.services.staffManager;

import java.math.BigInteger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "WS_StaffManager", targetNamespace = "urn:DefaultNamespace")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WSStaffManager {


    /**
     * 
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOFILE
     */
    @WebMethod(operationName = "GETITEMFILE")
    @WebResult(name = "GETITEMFILEReturn", partName = "GETITEMFILEReturn")
    public WSOFILE getitemfile();

    /**
     * 
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOEDS
     */
    @WebMethod(operationName = "GETFAKESIGN")
    @WebResult(name = "GETFAKESIGNReturn", partName = "GETFAKESIGNReturn")
    public WSOEDS getfakesign();

    /**
     * 
     * @param includeattachments
     * @param idperson
     * @param context
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOPERSON
     */
    @WebMethod(operationName = "GETPERSON")
    @WebResult(name = "GETPERSONReturn", partName = "GETPERSONReturn")
    public WSOPERSON getperson(
        @WebParam(name = "IDPERSON", partName = "IDPERSON")
        String idperson,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param includeattachments
     * @param context
     * @param idgr
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOGROUP
     */
    @WebMethod(operationName = "GETGROUP")
    @WebResult(name = "GETGROUPReturn", partName = "GETGROUPReturn")
    public WSOGROUP getgroup(
        @WebParam(name = "IDGR", partName = "IDGR")
        String idgr,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param includeattachments
     * @param context
     * @param ismobject
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOCOLLECTION
     */
    @WebMethod(operationName = "GETORGANIZATIONS")
    @WebResult(name = "GETORGANIZATIONSReturn", partName = "GETORGANIZATIONSReturn")
    public WSOCOLLECTION getorganizations(
        @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT")
        boolean ismobject,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param includeattachments
     * @param iddep
     * @param context
     * @param childslevel
     * @param ismobject
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOCOLLECTION
     */
    @WebMethod(operationName = "GETPERSONS")
    @WebResult(name = "GETPERSONSReturn", partName = "GETPERSONSReturn")
    public WSOCOLLECTION getpersons(
        @WebParam(name = "IDDEP", partName = "IDDEP")
        String iddep,
        @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT")
        boolean ismobject,
        @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL")
        BigInteger childslevel,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param includeattachments
     * @param idorg
     * @param context
     * @param childslevel
     * @param ismobject
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOGROUP
     */
    @WebMethod(operationName = "GETSTRUCTURE")
    @WebResult(name = "GETSTRUCTUREReturn", partName = "GETSTRUCTUREReturn")
    public WSOGROUP getstructure(
        @WebParam(name = "IDORG", partName = "IDORG")
        String idorg,
        @WebParam(name = "CHILDSLEVEL", partName = "CHILDSLEVEL")
        BigInteger childslevel,
        @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT")
        boolean ismobject,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @return
     *     returns ru.it.lecm.mobile.services.staffManager.WSOITEM
     */
    @WebMethod(operationName = "GETITEM")
    @WebResult(name = "GETITEMReturn", partName = "GETITEMReturn")
    public WSOITEM getitem();

}
