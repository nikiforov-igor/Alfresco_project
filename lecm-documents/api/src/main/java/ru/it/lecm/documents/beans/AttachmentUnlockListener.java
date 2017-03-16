package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * Created by azinovin on 28.11.2016.
 */
public interface AttachmentUnlockListener {
    void onAttachmentUnlocked(NodeRef attachmentRef);
}
