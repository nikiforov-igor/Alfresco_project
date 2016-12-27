package ru.it.lecm.incoming.external;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: pmelnikov
 * Date: 28.01.14
 * Time: 9:28
 */
@Deprecated
public class RepositoryReceiver extends AbstractReceiver {
    @Override
    public void receive(NodeRef document) {
        throw new UnsupportedOperationException("Deprecated Action! Use 'create-incoming-from-doc.js' script instead!");
    }

}
