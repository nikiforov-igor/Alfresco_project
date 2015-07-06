package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentStampService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: pmelnikov
 * Date: 01.07.15
 * Time: 15:02
 */
public class DocumentStampPolicy extends BaseBean implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    final static protected Logger logger = LoggerFactory.getLogger(DocumentStampPolicy.class);

    private PolicyComponent policyComponent;
    private ContentService contentService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "contentService", contentService);

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                DocumentStampService.TYPE_STAMP, DocumentStampService.ASSOC_IMAGE, new JavaBehaviour(this, "onCreateAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                DocumentStampService.TYPE_STAMP, DocumentStampService.ASSOC_IMAGE, new JavaBehaviour(this, "onDeleteAssociation"));

    }


    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef targetFile = nodeAssocRef.getTargetRef();
        NodeRef stamp = nodeAssocRef.getSourceRef();

        if (!targetFile.equals(stamp) && nodeService.exists(targetFile)) {
            ContentReader reader = contentService.getReader(targetFile, ContentModel.PROP_CONTENT);
            ContentWriter writer = contentService.getWriter(stamp, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(reader.getMimetype());
            try (
                    InputStream is = reader.getContentInputStream();
                    OutputStream os = writer.getContentOutputStream()
            ) {
                IOUtils.copy(is, os);
            } catch (IOException e) {
                logger.error("Cannot copy content", e);
            }
            nodeService.deleteNode(targetFile);
            nodeService.createAssociation(stamp, stamp, DocumentStampService.ASSOC_IMAGE);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        NodeRef target = nodeAssocRef.getTargetRef();
        NodeRef stamp = nodeAssocRef.getSourceRef();
        if (target.equals(stamp)) {
            ContentWriter contentWriter = contentService.getWriter(stamp, ContentModel.PROP_CONTENT, true);
            contentWriter.putContent("");
        }
    }

}
