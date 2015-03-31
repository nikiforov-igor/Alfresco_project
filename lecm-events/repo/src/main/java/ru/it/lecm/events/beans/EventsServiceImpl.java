package ru.it.lecm.events.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

/**
 * User: AIvkin
 * Date: 25.03.2015
 * Time: 14:44
 */
public class EventsServiceImpl extends BaseBean implements EventsService {
    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(EVENTS_ROOT_ID);
    }

    @Override
    public NodeRef getEventLocation(NodeRef event) {
        return findNodeByAssociationRef(event, ASSOC_EVENT_LOCATION, TYPE_EVENT_LOCATION, ASSOCIATION_TYPE.TARGET);
    }
}
