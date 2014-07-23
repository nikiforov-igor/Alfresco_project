package ru.it.lecm.documents.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.documents.beans.DocumentFrequencyAnalysisService;

import java.util.Map;
import java.util.Set;

/**
 * User: azinovin
 * Date: 17.06.2014
 * Time: 14:20
 */
public class LastDocumentsProcessor extends SearchQueryProcessor {

    private DocumentFrequencyAnalysisService documentFrequencyAnalysisService;

    public void setDocumentFrequencyAnalysisService(DocumentFrequencyAnalysisService documentFrequencyAnalysisService) {
        this.documentFrequencyAnalysisService = documentFrequencyAnalysisService;
    }

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        Set<NodeRef> docsRefs = documentFrequencyAnalysisService.getLastDocuments().keySet();
        for (NodeRef docsRef : docsRefs) {
            sbQuery.append("ID:").append(docsRef.toString().replace(":", "\\:")).append(" OR ");
        }

        sbQuery.append("ID:\"NOT_REF\""); // выключать поиск, если документы не найдены
        return sbQuery.toString();
    }
}
