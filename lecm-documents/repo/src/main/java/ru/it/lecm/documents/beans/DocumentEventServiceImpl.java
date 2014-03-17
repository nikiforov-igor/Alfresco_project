package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * User: pmelnikov
 * Date: 13.03.14
 * Time: 10:10
 */
public class DocumentEventServiceImpl implements DocumentEventService {

    private NodeService nodeService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }


    @Override
    public void subscribe(NodeRef object, NodeRef listener) {
        if (!nodeService.hasAspect(object, ASPECT_EVENT_LISTENERS)) {
            nodeService.addAspect(object, ASPECT_EVENT_LISTENERS, new HashMap<QName, Serializable>());
        }

        nodeService.createAssociation(object, listener, ASSOC_EVENT_LISTENERS);

        if (!nodeService.hasAspect(listener, ASPECT_EVENT_SENDER)) {
            HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
            props.put(PROP_EVENT_SENDER, "");
            nodeService.addAspect(listener, ASPECT_EVENT_SENDER, props);
        }
    }

    @Override
    public void unsubscribe(NodeRef object, NodeRef listener) {
        if (nodeService.hasAspect(object, ASPECT_EVENT_LISTENERS)) {
            nodeService.removeAssociation(object, listener, ASSOC_EVENT_LISTENERS);
        }
    }

    @Override
    public Set<NodeRef> getEventSenders(NodeRef listener) {
        String sendersStr = (String) nodeService.getProperty(listener, PROP_EVENT_SENDER);
		Set<NodeRef> result = new HashSet<NodeRef>();
		if (null != sendersStr){
			String[] senders = sendersStr.split(",");
			for (String sender : senders) {
				if (NodeRef.isNodeRef(sender.trim())) {
					result.add(new NodeRef(sender.trim()));
				}
			}
		}
        return result;
    }

    @Override
    public void removeEventSender(NodeRef listener, NodeRef sender) {
        Set<NodeRef> nodes = getEventSenders(listener);
        nodes.remove(sender);
        String senders = "";
        for (NodeRef node : nodes) {
            if (senders.length() == 0) {
                senders = node.toString();
            } else {
                senders += "," + node.toString();
            }
        }
        nodeService.setProperty(listener, PROP_EVENT_SENDER, senders);
    }

}
