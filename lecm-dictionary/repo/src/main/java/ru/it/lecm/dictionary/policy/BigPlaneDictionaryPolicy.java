package ru.it.lecm.dictionary.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.regnumbers.counter.CounterFactory;
import ru.it.lecm.regnumbers.counter.CounterType;

import java.util.ArrayList;
import java.util.List;

public class BigPlaneDictionaryPolicy extends BaseBean implements NodeServicePolicies.OnCreateNodePolicy {

    private final static Logger logger = LoggerFactory.getLogger(BigPlaneDictionaryPolicy.class);

    private PolicyComponent policyComponent;
    private DictionaryBean dictionaryBean;
    private CounterFactory counterFactory;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setCounterFactoryImpl(CounterFactory counterFactory) {
        this.counterFactory = counterFactory;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DictionaryBean.TYPE_BIG_PLANE_DICTIONARY_VALUE,
                new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        try {
            NodeRef parent = childAssocRef.getParentRef();
            NodeRef dicItem = childAssocRef.getChildRef();
            if (dictionaryBean.isDictionary(parent)) {
                List<String> path = new ArrayList<>();
                Long counter = (counterFactory.getCounter(CounterType.DOCTYPE_PLAIN, dicItem, null).getValue()) / 1000;
                path.add("000" + counter.toString());
                NodeRef storeRef = getFolder(parent, path);
                if (storeRef == null) {
                    storeRef = createPath(parent, path);
                }
                String name = nodeService.getProperty(dicItem, ContentModel.PROP_NAME).toString();
                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
                nodeService.moveNode(dicItem, storeRef, ContentModel.ASSOC_CONTAINS, assocQName);
            }
        } catch (WriteTransactionNeededException e) {
            logger.error("Can't move big plane dictionary value", e);
        }
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }
}
