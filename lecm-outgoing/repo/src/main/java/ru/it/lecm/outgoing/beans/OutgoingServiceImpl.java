package ru.it.lecm.outgoing.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.outgoing.api.OutgoingService;

/**
 *
 * @author vmalygin
 */
public class OutgoingServiceImpl extends BaseBean implements OutgoingService {

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }
}
