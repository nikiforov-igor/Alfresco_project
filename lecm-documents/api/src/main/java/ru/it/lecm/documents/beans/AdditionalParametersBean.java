package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.Map;

/**
 * Created by pkotelnikova on 26.09.14.
 */
public interface AdditionalParametersBean {
    /**
     * Register executor
     *
     * @param documentType
     * @param executor
     */
    void register(QName documentType, AdditionalParametersExecutor executor);

    /**
     * Return data from sourceDocument mapped according to targetDocument type
     *
     * @param sourceDocument
     * @param targetDocument
     * @return
     */
    Map<QName, String> execute(NodeRef sourceDocument, QName targetDocument);
}
