package ru.it.lecm.incoming.external;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.ArrayList;

/**
 * User: pmelnikov
 * Date: 28.01.14
 * Time: 9:28
 */
public class RepositoryReceiver extends AbstractReceiver {

    @Override
    public void receive(NodeRef document) {
        ExternalIncomingDocument incomingDocument = new ExternalIncomingDocument();
        ArrayList<NodeRef> attachments = new ArrayList<NodeRef>();
        attachments.add(document);
        incomingDocument.setContent(attachments);
        store(incomingDocument);
    }

}
