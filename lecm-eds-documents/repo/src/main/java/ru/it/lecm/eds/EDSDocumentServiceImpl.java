package ru.it.lecm.eds;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSDocumentService;

/**
 * User: dbashmakov
 * Date: 24.01.14
 * Time: 12:48
 */
public class EDSDocumentServiceImpl extends BaseBean implements EDSDocumentService {

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public void sendChildChangeSignal(NodeRef baseDoc) {
        Integer currentCount = (Integer) nodeService.getProperty(baseDoc, PROP_CHILD_CHANGE_SIGNAL_COUNT);
        if (currentCount != null) {
            currentCount++;
        } else {
            currentCount = 1;
        }

        nodeService.setProperty(baseDoc, PROP_CHILD_CHANGE_SIGNAL_COUNT, currentCount);
    }
}
