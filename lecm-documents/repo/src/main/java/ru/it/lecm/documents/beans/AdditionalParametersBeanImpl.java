package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pkotelnikova on 26.09.14.
 */
public class AdditionalParametersBeanImpl implements AdditionalParametersBean {
    private final static Logger logger = LoggerFactory.getLogger(AdditionalParametersBeanImpl.class);

    private NodeService nodeService;
    private Map<QName, AdditionalParametersExecutor> executors = new HashMap<>();

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void register(QName documentType, AdditionalParametersExecutor executor) {
        executors.put(documentType, executor);
    }

    @Override
    public Map<QName, String> execute(NodeRef sourceDocument, QName targetDocument) {
        QName type = nodeService.getType(sourceDocument);
        AdditionalParametersExecutor executor = executors.get(type);
        if (executor == null) {
            logger.error("Executor not found for document type " + type + " (document ref: " + sourceDocument + ")");
            return new HashMap<>();
        }

        Map<QName, String> result = executor.execute(sourceDocument, targetDocument);
        return result;
    }
}
