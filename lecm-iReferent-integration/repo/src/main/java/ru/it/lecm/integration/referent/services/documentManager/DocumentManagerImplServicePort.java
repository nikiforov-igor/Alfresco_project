package ru.it.lecm.integration.referent.services.documentManager;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.integration.referent.objects.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mshafeev
 * Date: 24.09.13
 * Time: 17:00
 */

@javax.jws.WebService(name = "DocumentManagerServicePort", serviceName = "DocumentManager", portName = "DocumentManagerServicePort", targetNamespace = "urn:DefaultNamespace", endpointInterface = "ru.it.lecm.integration.referent.services.documentManager.WSDocumentManager")
public class DocumentManagerImplServicePort implements WSDocumentManager {

    @Resource
    WebServiceContext wsContext;

    private NodeService nodeService;
    private DocumentService documentService;
    private ObjectFactory objectFactory;
    private AuthenticationService authenticationService;
    private AuthorityService authorityService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setWsContext(WebServiceContext wsContext) {
        this.wsContext = wsContext;
    }

    @Override
    public WSOMDOCUMENT getdocument(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "IN_CONTEXT", partName = "IN_CONTEXT") WSOCONTEXT inCONTEXT) {
        return objectFactory.createWSOMDOCUMENT(id);
    }

    @Override
    public WSOCOLLECTION getdocuments(@WebParam(name = "ISMOBJECT", partName = "ISMOBJECT") boolean ismobject, @WebParam(name = "INCLUDEATTACHMENTS", partName = "INCLUDEATTACHMENTS") boolean includeattachments, @WebParam(name = "IN_CONTEXT", partName = "IN_CONTEXT") WSOCONTEXT inCONTEXT) {
        MessageContext msgCtxt = wsContext.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)msgCtxt.get(MessageContext.SERVLET_REQUEST);

        final AuthenticationUtil.RunAsWork<WSOCOLLECTION> runner = new AuthenticationUtil.RunAsWork<WSOCOLLECTION>() {
            @Override
            public WSOCOLLECTION doWork() throws Exception {
                List<QName> types = new ArrayList<QName>(1);
                QName contractType = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "document");
                types.add(contractType);

                List<String> paths = new ArrayList<String>(2);
                paths.add(documentService.getDraftPathByType(contractType));
                paths.add(documentService.getDocumentsFolderPath());

                WSOCOLLECTION.DATA data = objectFactory.createWSOCOLLECTIONDATA();
                List<NodeRef> documents = documentService.getDocumentsByFilter(types, paths, null, null, null);
                for (NodeRef document : documents) {
                    data.getItem().add(objectFactory.createWSODOCUMENTIN(document));
                }
                return objectFactory.createWSOCOLLECTION(data);
            }
        };

        return AuthenticationUtil.runAs(runner,
                inCONTEXT.getUSERID()
        );
    }

    @Override
    public WSOFILE getfile(@WebParam(name = "ID", partName = "ID") String id, @WebParam(name = "IN_CONTEXT", partName = "IN_CONTEXT") WSOCONTEXT inCONTEXT) {
        final String fileID = id;
        final AuthenticationUtil.RunAsWork<WSOFILE> runner = new AuthenticationUtil.RunAsWork<WSOFILE>() {
            @Override
            public WSOFILE doWork() throws Exception {
                return objectFactory.createWSOFILE(fileID);
            }
        };
        return AuthenticationUtil.runAs(runner,
                inCONTEXT.getUSERID()
        );
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }
}

