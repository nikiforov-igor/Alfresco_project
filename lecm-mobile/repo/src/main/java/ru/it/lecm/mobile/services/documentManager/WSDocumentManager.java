
package ru.it.lecm.mobile.services.documentManager;

import ru.it.lecm.mobile.objects.*;

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
@WebService(name = "WS_DocumentManager",
        targetNamespace = "urn:DefaultNamespace")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WSDocumentManager {


    /**
     * 
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOEDS
     */
    @WebMethod(operationName = "GETFAKESIGN")
    @WebResult(name = "GETFAKESIGNReturn", partName = "GETFAKESIGNReturn")
    public WSOEDS getfakesign();

    /**
     * 
     * @param docid
     * @param context
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOCOLLECTION
     */
    @WebMethod(operationName = "GETDISCUSSIONLIST")
    @WebResult(name = "GETDISCUSSIONLISTReturn", partName = "GETDISCUSSIONLISTReturn")
    public WSOCOLLECTION getdiscussionlist(
        @WebParam(name = "DOCID", partName = "DOCID")
        String docid,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param context
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOCOLLECTION
     */
    @WebMethod(operationName = "GETROUTES")
    @WebResult(name = "GETROUTESReturn", partName = "GETROUTESReturn")
    public WSOCOLLECTION getroutes(
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param route
     * @param context
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CREATEDOCUMENT")
    @WebResult(name = "CREATEDOCUMENTReturn", partName = "CREATEDOCUMENTReturn")
    public String createdocument(
        @WebParam(name = "ROUTE", partName = "ROUTE")
        WSOROUTE route,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param clientdocs
     * @param context
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOCOLLECTION
     */
    @WebMethod(operationName = "GETKILLDOCS")
    @WebResult(name = "GETKILLDOCSReturn", partName = "GETKILLDOCSReturn")
    public WSOCOLLECTION getkilldocs(
        @WebParam(name = "CLIENTDOCS", partName = "CLIENTDOCS")
        WSOCOLLECTION clientdocs,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param id
     * @param includeattachments
     * @param context
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOMDOCUMENT
     */
    @WebMethod(operationName = "GETDOCUMENT")
    @WebResult(name = "GETDOCUMENTReturn", partName = "GETDOCUMENTReturn")
    public WSOMDOCUMENT getdocument(
        @WebParam(name = "ID", partName = "ID")
        String id,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param typestatuspairs
     * @param includeattachments
     * @param deltaunid
     * @param context
     * @param ismobject
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOCOLLECTION
     */
    @WebMethod(operationName = "GETDOCUMENTSEX")
    @WebResult(name = "GETDOCUMENTSEXReturn", partName = "GETDOCUMENTSEXReturn")
    public WSOCOLLECTION getdocumentsex(
        @WebParam(name = "DELTAUNID", partName = "DELTAUNID")
        String deltaunid,
        @WebParam(name = "TYPESTATUSPAIRS", partName = "TYPESTATUSPAIRS")
        WSOCOLLECTION typestatuspairs,
        @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT")
        boolean ismobject,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param includeattachments
     * @param deltaunid
     * @param context
     * @param ismobject
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOCOLLECTION
     */
    @WebMethod(operationName = "GETDOCUMENTS")
    @WebResult(name = "GETDOCUMENTSReturn", partName = "GETDOCUMENTSReturn")
    public WSOCOLLECTION getdocuments(
        @WebParam(name = "DELTAUNID", partName = "DELTAUNID")
        String deltaunid,
        @WebParam(name = "ISMOBJECT", partName = "ISMOBJECT")
        boolean ismobject,
        @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS")
        boolean includeattachments,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @param docunid
     * @param personunid
     * @param context
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "DELEGATEDOC")
    @WebResult(name = "DELEGATEDOCReturn", partName = "DELEGATEDOCReturn")
    public boolean delegatedoc(
        @WebParam(name = "DOCUNID", partName = "DOCUNID")
        String docunid,
        @WebParam(name = "PERSONUNID", partName = "PERSONUNID")
        String personunid,
        @WebParam(name = "CONTEXT", partName = "CONTEXT")
        WSOCONTEXT context);

    /**
     * 
     * @return
     *     returns ru.it.lecm.mobile.services.documentManager.WSOITEM
     */
    @WebMethod(operationName = "GETITEM")
    @WebResult(name = "GETITEMReturn", partName = "GETITEMReturn")
    public WSOITEM getitem();

}
