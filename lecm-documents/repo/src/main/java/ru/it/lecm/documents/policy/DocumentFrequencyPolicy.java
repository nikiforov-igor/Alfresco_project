package ru.it.lecm.documents.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentFrequencyAnalysisService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 19.03.13
 * Time: 15:43
 */
public class DocumentFrequencyPolicy implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy {

    public static final int MAX_COUNT = 1000;
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private OrgstructureBean orgstructureService;
    private DocumentFrequencyAnalysisService freaquencyService;

    final static protected Logger logger = LoggerFactory.getLogger(DocumentFrequencyPolicy.class);

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setFreaquencyService(DocumentFrequencyAnalysisService freaquencyService) {
        this.freaquencyService = freaquencyService;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DocumentFrequencyAnalysisService.TYPE_FREQUENCY_UNIT, new JavaBehaviour(this, "onCreateNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, DocumentFrequencyAnalysisService.TYPE_FREQUENCY_UNIT, new JavaBehaviour(this,"onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef employee = orgstructureService.getCurrentEmployee();
        if (employee != null) {
            nodeService.createAssociation(childAssocRef.getChildRef(), employee, DocumentFrequencyAnalysisService.ASSOC_UNIT_EMPLOYEE);
        }
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        Long prevCount = (Long) before.get(DocumentFrequencyAnalysisService.PROP_UNIT_COUNT);
        Long curCount = (Long) after.get(DocumentFrequencyAnalysisService.PROP_UNIT_COUNT);
        if (curCount != null && !curCount.equals(prevCount)){
            if (curCount >= MAX_COUNT){ // одно из значений достигло лимита - проведем нормирование
                String docType = (String) nodeService.getProperty(nodeRef, DocumentFrequencyAnalysisService.PROP_UNIT_DOC_TYPE);
                NodeRef employee = nodeService.getTargetAssocs(nodeRef, DocumentFrequencyAnalysisService.ASSOC_UNIT_EMPLOYEE).get(0).getTargetRef();
                List<NodeRef> freqUnits = freaquencyService.getFrequencyUnits(employee,docType);
                for (NodeRef freqUnit : freqUnits) {
                    Long count = (Long) nodeService.getProperty(freqUnit, DocumentFrequencyAnalysisService.PROP_UNIT_COUNT);
                    Long newCount = count/2;
                    nodeService.setProperty(freqUnit,DocumentFrequencyAnalysisService.PROP_UNIT_COUNT, newCount);
                }
            }
        }
    }
}
