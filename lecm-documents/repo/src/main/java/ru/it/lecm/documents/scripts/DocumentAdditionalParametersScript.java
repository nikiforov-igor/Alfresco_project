package ru.it.lecm.documents.scripts;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.documents.beans.AdditionalParametersBeanImpl;

import java.io.IOException;
import java.util.Map;

/**
 * Created by pkotelnikova on 26.09.14.
 */
public class DocumentAdditionalParametersScript extends AbstractWebScript {
    private static final transient Logger logger = LoggerFactory.getLogger(DocumentAdditionalParametersScript.class);

    private static final String PROP_PREFIX = "prop_";
    private static final String ASSOC_PREFIX = "assoc_";
    private static final String SOURCE_DOCUMENT_PARAM = "nodeRef";
    private static final String TARGET_DOCUMENT_PARAM = "qName";

    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private AdditionalParametersBeanImpl parametersBean;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setParametersBean(AdditionalParametersBeanImpl parametersBean) {
        this.parametersBean = parametersBean;
    }

    @Override
    public void execute(WebScriptRequest reg, WebScriptResponse res) throws IOException {
        NodeRef sourceDocument = null;
        String sourceDocumentParam = reg.getParameter(SOURCE_DOCUMENT_PARAM);
        if (sourceDocumentParam != null && NodeRef.isNodeRef(sourceDocumentParam)) {
            sourceDocument = new NodeRef(sourceDocumentParam);
        }

        if (sourceDocument == null) {
            logger.error("Invalid sourceDocument sourceDocumentParam. " + sourceDocumentParam);
            return;
        }

        QName targetDocument = null;
        String targetDocumentParam = reg.getParameter(TARGET_DOCUMENT_PARAM);
        if (targetDocumentParam != null) {
            targetDocument = QName.createQName(targetDocumentParam, namespaceService);
        }

        if (targetDocument == null) {
            logger.error("Invalid targetDocument. " + targetDocumentParam);
            return;
        }

        Map<QName, String> parameters = parametersBean.execute(sourceDocument, targetDocument);

        JSONObject result = new JSONObject();
        try {
            for (Map.Entry<QName, String> entry : parameters.entrySet()) {
                QName qName = entry.getKey();

                String prefix = "";
                if (dictionaryService.getProperty(qName) != null) {
                    prefix = PROP_PREFIX;
                } else if (dictionaryService.getAssociation(qName) != null) {
                    prefix = ASSOC_PREFIX;
                } else {
                    continue;
                }

                String key = prefix + qName.toPrefixString(namespaceService).replace(":", "_");
                result.put(key, entry.getValue());
            }

            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(result.toString());
        } catch (JSONException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
