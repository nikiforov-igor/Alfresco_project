package ru.it.lecm.incoming.external;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: pmelnikov
 * Date: 29.01.14
 * Time: 9:00
 */
@Deprecated
public class EmailRepositoryReceiver extends AbstractReceiver {

    @Override
    public void receive(NodeRef document) {
        throw new UnsupportedOperationException("Deprecated Action! Use 'create-incoming-from-email.js' script instead!");
    }
}
