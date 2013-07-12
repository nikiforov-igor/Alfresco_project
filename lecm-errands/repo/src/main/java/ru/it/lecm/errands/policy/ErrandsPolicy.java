
package ru.it.lecm.errands.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.beans.ErrandsServiceImpl;

import java.io.Serializable;
import java.util.Map;

/**
 * User: mshafeev
 * Date: 11.07.13
 * Time: 15:51
 */
public class ErrandsPolicy extends BaseBean
        implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    final static protected Logger logger = LoggerFactory.getLogger(ErrandsPolicy.class);

    private PolicyComponent policyComponent;
//    private DictionaryBean dictionaryService;
    private ErrandsService errandsService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

//    public void setDictionaryService(DictionaryBean dictionaryService) {
//        this.dictionaryService = dictionaryService;
//    }

    public void setErrandsService(ErrandsServiceImpl errandsService) {
        this.errandsService = errandsService;
    }

    final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ErrandsService.TYPE_ERRANDS, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        // Проверяем что нода только что создана и если не выбран инициатор, дописываем значение по умолчанию (автора)
        if (before.size() == 0 && (after.get(ErrandsService.TYPE_ERRANDS_INITIATOR_REF) == null)) {
            NodeRef initiator = new NodeRef(after.get(DocumentService.PROP_DOCUMENT_CREATOR_REF).toString());
            if (initiator != null) {
                nodeService.createAssociation(nodeRef, initiator, ErrandsService.ASSOC_ERRANDS_INITIATOR);
            }
        }
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }


}
