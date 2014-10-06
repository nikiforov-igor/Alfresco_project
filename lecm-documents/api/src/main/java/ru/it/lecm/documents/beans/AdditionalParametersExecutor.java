package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import java.util.Map;

/**
 * Created by pkotelnikova on 26.09.14.
 */
public abstract class AdditionalParametersExecutor {
    private NamespaceService namespaceService;
    private AdditionalParametersBean parametersBean;
    private String documentType;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setParametersBean(AdditionalParametersBean parametersBean) {
        this.parametersBean = parametersBean;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void init() {
        PropertyCheck.mandatory(this, "documentType", documentType);
        QName qName = QName.createQName(documentType, namespaceService);
        parametersBean.register(qName, this);
    }

    /**
     * Return data from sourceDocument mapped according to targetDocument type
     *
     * @param sourceDocument
     * @param targetDocument
     * @return
     */
    public abstract Map<QName, String> execute(NodeRef sourceDocument, QName targetDocument);
}
