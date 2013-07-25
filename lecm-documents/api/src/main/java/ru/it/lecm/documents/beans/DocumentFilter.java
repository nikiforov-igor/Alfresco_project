package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * User: DBashmakov
 * Date: 12.07.13
 * Time: 11:17
 */
public abstract class DocumentFilter {

    protected OrgstructureBean orgstructureService;
    protected DocumentService documentService;
    protected NamespaceService namespaceService;
    protected AuthenticationService authService;
    protected NodeService nodeService;

    public abstract String getId();

    /**
     регистрации фильтра в системе
     */
    void register() {
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
        PropertyCheck.mandatory(this, "documentService", documentService);
        PropertyCheck.mandatory(this, "authService", authService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        FiltersManager.registerFilter(this);
    }

    /**
     Метод для регистрации фильтра в системе
     */
    abstract public String getQuery(Object... args);

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
