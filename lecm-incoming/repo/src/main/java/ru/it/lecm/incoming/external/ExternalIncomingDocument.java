package ru.it.lecm.incoming.external;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 28.01.14
 * Time: 9:05
 */
public class ExternalIncomingDocument {

    //Доставка по электронной почте
    private NodeRef deliveryType = null;
    //Адресант
    private NodeRef addresser = null;
    //Организация отрправитель
    private NodeRef senderOrganization = null;
    //Вложения
    List<NodeRef> content = new ArrayList<NodeRef>();

    public NodeRef getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(NodeRef deliveryType) {
        this.deliveryType = deliveryType;
    }

    public NodeRef getAddresser() {
        return addresser;
    }

    public void setAddresser(NodeRef addresser) {
        this.addresser = addresser;
    }

    public List<NodeRef> getContent() {
        return content;
    }

    public void setContent(List<NodeRef> content) {
        this.content = content;
    }

    public NodeRef getSenderOrganization() {
        return senderOrganization;
    }

    public void setSenderOrganization(NodeRef senderOrganization) {
        this.senderOrganization = senderOrganization;
    }
}
