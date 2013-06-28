package ru.it.lecm.contractors.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.io.Serializable;

/**
 * @author dgonchar
 */
public class ContractorsContractorPolicy implements NodeServicePolicies.OnUpdateNodePolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdateNodePolicy.QNAME,
                Contractors.TYPE_CONTRACTOR,
                new JavaBehaviour(this, "onUpdateNode"));
    }

    @Override
    public void onUpdateNode(NodeRef contractor) {

        /* Наследуем ИНН от материнской компании */
        Serializable currentContractorINN = nodeService.getProperty(contractor, Contractors.PROP_CONTRACTOR_INN);

        // Если есть такое свойство
        if(currentContractorINN != null) {

            // Если у текущего контрагента нет ИНН
            if(currentContractorINN.toString().isEmpty()) {

                NodeRef currentContractorParentRef = nodeService.getPrimaryParent(contractor).getParentRef();
                QName currentContractorParentType = nodeService.getType(currentContractorParentRef);

                // Если текущий контрагент является представителем
                if(currentContractorParentType.equals(Contractors.TYPE_CONTRACTOR)) {

                    Serializable currentContractorParentINN = nodeService.getProperty(currentContractorParentRef, Contractors.PROP_CONTRACTOR_INN);

                    // Если у материнской компании есть ИНН
                    if(!currentContractorParentINN.toString().isEmpty()) {

                        // То "унаследуем" ИНН от материнской компании
                        nodeService.setProperty(contractor, Contractors.PROP_CONTRACTOR_INN, currentContractorParentINN);
                    }
                }
            }

        }

        /* Формируем имя "файла" в репозитории на основе сокращённого наименования, убрав запрещённые символы */
        Serializable currentContractorShortName = nodeService.getProperty(contractor, Contractors.PROP_CONTRACTOR_SHORTNAME);

        // Если есть такое свойство
        if(currentContractorShortName != null) {
            String shortname = nodeService.getProperty(contractor, Contractors.PROP_CONTRACTOR_SHORTNAME).toString();
            String filename = shortname.replaceAll("[^_\\-\\dA-Za-zА-Яа-я ]", "");
            nodeService.setProperty(contractor, ContentModel.PROP_NAME, filename);
        }
    }
}
