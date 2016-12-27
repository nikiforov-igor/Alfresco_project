package ru.it.lecm.incoming.external;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: pmelnikov
 * Date: 29.01.14
 * Time: 9:00
 */
@Deprecated
public class EmailRepositoryReceiver extends AbstractReceiver {
    private static final Logger logger = LoggerFactory.getLogger(EmailRepositoryReceiver.class);

    @Override
    public void receive(NodeRef document) {
        logger.error("Deprecated Action! Use 'create-incoming-from-email.js' script instead!");
    }
}
