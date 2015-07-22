
package ru.it.lecm.mobile.services.documentManager;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.mobile.objects.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.List;


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
public class WSDocumentManagerPort implements WSDocumentManager {

    private ObjectFactory objectFactory;
    private DocumentService documentService;
    private NamespaceService namespaceService;

    public WSOEDS getfakesign() {
        return null;
    }

    public WSOCOLLECTION getdiscussionlist(String docid, WSOCONTEXT context) {
        return null;
    }

    public WSOCOLLECTION getroutes(WSOCONTEXT context) {
/*        WSOCOLLECTION routes = objectFactory.createWSOCOLLECTION();
        routes.getDATA().add(objectFactory.createWSOROUTE(QName.createQName("lecm-errands:document", namespaceService)));
        routes.setCOUNT((short) routes.getDATA().size());
        return routes;*/
        return null;
    }

    public String createdocument(WSOROUTE route, WSOCONTEXT context) {
        return null;
    }

    public WSOCOLLECTION getkilldocs(WSOCOLLECTION clientdocs, WSOCONTEXT context) {
        return objectFactory.createWSOCOLLECTION();
    }

    public WSOMDOCUMENT getdocument(String id, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    public WSOCOLLECTION getdocumentsex(String deltaunid, WSOCOLLECTION typestatuspairs, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        final List<Object> documentTypes = typestatuspairs.getDATA();
        final AuthenticationUtil.RunAsWork<WSOCOLLECTION> runner = new AuthenticationUtil.RunAsWork<WSOCOLLECTION>() {
            @Override
            public WSOCOLLECTION doWork() throws Exception {
                WSOCOLLECTION data = objectFactory.createWSOCOLLECTION();
                for (Object typeObject : documentTypes) {
                    String[] typeStruct = ((String) typeObject).split("\\|");
                    List<QName> types = new ArrayList<>(1);
                    types.add(QName.createQName(typeStruct[2], namespaceService));

                    List<String> statuses = new ArrayList<>(1);
                    statuses.add(typeStruct[1]);

                    List<SearchParameters.SortDefinition> sort = new ArrayList<>(1);
                    sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ContentModel.PROP_MODIFIED.toString(), false));
                    List<NodeRef> documents = documentService.getDocumentsByFilter(types, null, statuses, null, sort);
/*
                    int maxItem = 3;
                    int itemNum = 0;
*/
                    for (NodeRef document : documents) {
                        data.getDATA().add(objectFactory.createWSODOCUMENT(document));
/*
                        itemNum++;
                        if (itemNum >= maxItem) {
                            break;
                        }
*/
                    }
                }
                data.setCOUNT((short) data.getDATA().size());
                return data;
            }
        };

        return AuthenticationUtil.runAs(runner,
                context.getUSERID()
        );
    }

    public WSOCOLLECTION getdocuments(String deltaunid, boolean ismobject, boolean includeattachments, WSOCONTEXT context) {
        return null;
    }

    public boolean delegatedoc(String docunid, String personunid, WSOCONTEXT context) {
        return false;
    }

    public WSOITEM getitem() {
        return null;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
}