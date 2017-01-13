package ru.it.lecm.resolutions.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.resolutions.api.ResolutionsService;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * User: AIvkin
 * Date: 13.01.2017
 * Time: 11:57
 */
public class ResolutionLimitationDatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    private PolicyComponent policyComponent;

    private NodeService nodeService;
    private EDSDocumentService edsDocumentService;

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setEdsDocumentService(EDSDocumentService edsDocumentService) {
        this.edsDocumentService = edsDocumentService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ResolutionsService.TYPE_RESOLUTION_DOCUMENT, new JavaBehaviour(this, "onUpdateProperties"));
    }

    /*
        Заполнение атрибута текстового представления срока исполнения
     */
    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String newDateRadio = (String) after.get(ResolutionsService.PROP_LIMITATION_DATE_RADIO);
        Date newDate = (Date) after.get(ResolutionsService.PROP_LIMITATION_DATE);
        String newDaysType = (String) after.get(ResolutionsService.PROP_LIMITATION_DATE_TYPE);
        Integer newDaysCount = (Integer) after.get(ResolutionsService.PROP_LIMITATION_DATE_DAYS);

        String oldLimitationDateText = (String) after.get(ResolutionsService.PROP_LIMITATION_DATE_TEXT);
        String newLimitationDateText = edsDocumentService.getExecutionDateText(newDateRadio, newDate, newDaysType, newDaysCount);
        if (!Objects.equals(oldLimitationDateText, newLimitationDateText)) {
            nodeService.setProperty(nodeRef, ResolutionsService.PROP_LIMITATION_DATE_TEXT, newLimitationDateText);
        }
    }
}
