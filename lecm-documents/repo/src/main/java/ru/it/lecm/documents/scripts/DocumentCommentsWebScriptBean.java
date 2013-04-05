package ru.it.lecm.documents.scripts;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentCommentsServiceImpl;

/**
 * User: mshafeev
 * Date: 04.04.13
 * Time: 14:25
 */
public class DocumentCommentsWebScriptBean extends BaseWebScript {

    private DocumentCommentsServiceImpl documentCommentsService;

    public void setDocumentCommentsService(DocumentCommentsServiceImpl documentCommentsService) {
        this.documentCommentsService = documentCommentsService;
    }

    public boolean isChangeComment(String nodeRef) {
        return documentCommentsService.isPermissionChangeComment(new NodeRef(nodeRef));
    }


}
