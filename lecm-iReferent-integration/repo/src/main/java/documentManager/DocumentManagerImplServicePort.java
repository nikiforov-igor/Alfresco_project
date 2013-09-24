package documentManager;

import javax.jws.WebParam;

/**
 * User: mshafeev
 * Date: 24.09.13
 * Time: 17:00
 */

@javax.jws.WebService(name = "DocumentManagerServicePort", serviceName = "DocumentManager", portName = "DocumentManagerServicePort", targetNamespace = "urn:DefaultNamespace", endpointInterface = "documentManager.WSDocumentManager")
public class DocumentManagerImplServicePort implements documentManager.WSDocumentManager {
    @Override
    public WSOMDOCUMENT getdocument(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "IN_CONTEXT", partName = "IN_CONTEXT") WSOCONTEXT inCONTEXT) {
        return null;
    }

    @Override
    public WSOCOLLECTION getdocuments(@WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "IN_CONTEXT", partName = "IN_CONTEXT") WSOCONTEXT inCONTEXT) {
        return null;
    }

    @Override
    public WSOFILE getfile(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "IN_CONTEXT", partName = "IN_CONTEXT") WSOCONTEXT inCONTEXT) {
        return null;
    }

    @Override
    public String ping() {
        return "BAAAAHH!!";
    }

}

