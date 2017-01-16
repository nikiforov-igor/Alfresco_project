package ru.it.lecm.eds.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.eds.api.EDSDocumentService;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * User: AIvkin
 * Date: 14.01.2017
 * Time: 12:52
 */
public class ComplexDatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    private PolicyComponent policyComponent;

    private NodeService nodeService;
    private NamespaceService namespaceService;
    private EDSDocumentService edsDocumentService;

    private String type;
    private String propDateRadio;
    private String propDate;
    private String propDateDaysCount;
    private String propDateDaysType;
    private String propDateText;

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setEdsDocumentService(EDSDocumentService edsDocumentService) {
        this.edsDocumentService = edsDocumentService;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPropDateRadio(String propDateRadio) {
        this.propDateRadio = propDateRadio;
    }

    public void setPropDate(String propDate) {
        this.propDate = propDate;
    }

    public void setPropDateDaysCount(String propDateDaysCount) {
        this.propDateDaysCount = propDateDaysCount;
    }

    public void setPropDateDaysType(String propDateDaysType) {
        this.propDateDaysType = propDateDaysType;
    }

    public void setPropDateText(String propDateText) {
        this.propDateText = propDateText;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                QName.createQName(type, namespaceService), new JavaBehaviour(this, "onUpdateProperties"));
    }

    /*
        Заполнение атрибута текстового представления срока исполнения
     */
    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String newDateRadio = (String) after.get(QName.createQName(propDateRadio, namespaceService));
        Date newDate = (Date) after.get(QName.createQName(propDate, namespaceService));
        String newDaysType = (String) after.get(QName.createQName(propDateDaysType, namespaceService));
        Integer newDaysCount = (Integer) after.get(QName.createQName(propDateDaysCount, namespaceService));

        QName propDateTextQName = QName.createQName(propDateText, namespaceService);

        String oldLimitationDateText = (String) after.get(propDateTextQName);
        String newLimitationDateText = edsDocumentService.getComplexDateText(newDateRadio, newDate, newDaysType, newDaysCount);
        if (!Objects.equals(oldLimitationDateText, newLimitationDateText)) {
            nodeService.setProperty(nodeRef, propDateTextQName, newLimitationDateText);
        }
    }
}
