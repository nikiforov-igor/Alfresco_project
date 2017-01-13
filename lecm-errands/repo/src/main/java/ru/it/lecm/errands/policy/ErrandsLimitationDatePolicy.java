package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Created by APanyukov on 11.01.2017.
 */
public class ErrandsLimitationDatePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
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
                ErrandsService.TYPE_ERRANDS, new JavaBehaviour(this, "onUpdateProperties"));
    }

    /*
        Заполнение атрибута текстового представления срока исполнения
     */
    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String newDateRadio = (String) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
        Date newDate = (Date) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
        String newDaysType = (String) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE);
        Integer newDaysCount = (Integer) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);

        String oldLimitationDateText = (String) after.get(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TEXT);
        String newLimitationDateText = edsDocumentService.getExecutionDateText(newDateRadio, newDate, newDaysType, newDaysCount);
        if (!Objects.equals(oldLimitationDateText, newLimitationDateText)) {
            nodeService.setProperty(nodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TEXT, newLimitationDateText);
        }
    }
}
