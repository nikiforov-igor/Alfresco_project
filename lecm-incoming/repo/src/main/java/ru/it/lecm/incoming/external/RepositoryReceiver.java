package ru.it.lecm.incoming.external;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: pmelnikov
 * Date: 28.01.14
 * Time: 9:28
 */
@Deprecated
public class RepositoryReceiver extends AbstractReceiver {
    private static final Logger logger = LoggerFactory.getLogger(EmailRepositoryReceiver.class);

    @Override
    public void receive(NodeRef document) {
        logger.error("Deprecated Action! Use 'create-incoming-from-doc.js' script instead!");
    }

}
