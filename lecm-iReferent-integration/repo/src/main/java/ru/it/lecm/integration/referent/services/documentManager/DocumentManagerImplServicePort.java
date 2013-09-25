package ru.it.lecm.integration.referent.services.documentManager;

import javax.jws.WebParam;
import ru.it.lecm.integration.referent.objects.*;
/**
 * User: mshafeev
 * Date: 24.09.13
 * Time: 17:00
 */

@javax.jws.WebService(name = "DocumentManagerServicePort", serviceName = "DocumentManager", portName = "DocumentManagerServicePort", targetNamespace = "urn:DefaultNamespace", endpointInterface = "ru.it.lecm.integration.referent.services.documentManager.WSDocumentManager")
public class DocumentManagerImplServicePort implements WSDocumentManager {
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
}

