package ru.it.lecm.incoming.external;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 29.01.14
 * Time: 9:00
 */
public class EmailRepositoryReceiver extends AbstractReceiver {

    private OrgstructureBean orgstructureBean;
    private Contractors contractors;
    private DictionaryBean dictionaryService;

    @Override
    public void receive(NodeRef document) {
        if (nodeService.hasAspect(document, ContentModel.ASPECT_EMAILED)) {
            ExternalIncomingDocument incomingDocument = new ExternalIncomingDocument();
            ArrayList<NodeRef> attachments = new ArrayList<NodeRef>();
            List<AssociationRef> targets = nodeService.getTargetAssocs(document, ContentModel.ASSOC_ATTACHMENTS);
            for (AssociationRef target : targets) {
                attachments.add(target.getTargetRef());
            }
            incomingDocument.setContent(attachments);
            NodeRef deliveryType = dictionaryService.getRecordByParamValue("Способ доставки", DocumentService.PROP_DELIVERY_METHOD_CODE, "EMAIL");
            if (deliveryType != null) {
                incomingDocument.setDeliveryType(deliveryType);
            }

            Object originator = nodeService.getProperty(document, ContentModel.PROP_ORIGINATOR);
            if (originator != null) {
                String email = originator.toString();
                NodeRef addresser = contractors.getRepresentativeByEmail(email);
                if (addresser != null) {
                    incomingDocument.setAddresser(addresser);
                    NodeRef contractor = contractors.getContractor(addresser);
                    if (contractor != null) {
                        incomingDocument.setSenderOrganization(contractor);
                    }
                }
            }
            store(incomingDocument);
            nodeService.deleteNode(document);
        }
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setContractors(Contractors contractors) {
        this.contractors = contractors;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
