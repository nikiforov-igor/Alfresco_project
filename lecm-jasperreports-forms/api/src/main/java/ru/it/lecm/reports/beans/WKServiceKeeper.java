package ru.it.lecm.reports.beans;

import org.alfresco.service.ServiceRegistry;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

public class WKServiceKeeper {

    private ServiceRegistry serviceRegistry;
    private SubstitudeBean substitudeService;
    private OrgstructureBean orgstructureService;
    private DocumentService documentService;
    private DocumentConnectionService documentConnectionService;

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public SubstitudeBean getSubstitudeService() {
        return substitudeService;
    }

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }

    public OrgstructureBean getOrgstructureService() {
        return this.orgstructureService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public DocumentConnectionService getDocumentConnectionService() {
        return documentConnectionService;
    }

    public void setDocumentConnectionService(
            DocumentConnectionService documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

}