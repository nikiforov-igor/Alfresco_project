package ru.it.lecm.contractors.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.PropertyMap;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 07.04.14
 * Time: 13:28
 */
public class ContractorsPolicy implements NodeServicePolicies.OnCreateNodePolicy,  NodeServicePolicies.OnUpdateNodePolicy {
    private LecmBasePropertiesService propertiesService;
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private OrgstructureBean orgstructureBean;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;

    }
    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "propertiesService", propertiesService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                Contractors.TYPE_CONTRACTOR, new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdateNodePolicy.QNAME,
                Contractors.TYPE_CONTRACTOR, new JavaBehaviour(this, "onUpdateNode", NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                Contractors.TYPE_CONTRACTOR, new JavaBehaviour(this, "onUpdateContractor", NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.contractors.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

            if (!enabled) {
                throw new IllegalStateException("Cannot read contractors properties");
            }
        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read contractors properties");
        }
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

                // Если текущий контрагент является адресантом
                if(currentContractorParentType.equals(Contractors.TYPE_CONTRACTOR)) {
                    Serializable currentContractorParentINN = nodeService.getProperty(currentContractorParentRef, Contractors.PROP_CONTRACTOR_INN);
                    // Если у материнской компании есть ИНН
                    if(currentContractorParentINN != null && !currentContractorParentINN.toString().isEmpty()) {
                        // То "унаследуем" ИНН от материнской компании
                        nodeService.setProperty(contractor, Contractors.PROP_CONTRACTOR_INN, currentContractorParentINN);
                    }
                }
            }
        }

        /* Формируем имя "файла" в репозитории на основе сокращённого наименования, убрав запрещённые символы */
        Serializable currentContractorShortName = nodeService.getProperty(contractor, Contractors.PROP_CONTRACTOR_SHORTNAME);

        // Если есть такое свойство
/*
        if(currentContractorShortName != null) {
            String shortname = nodeService.getProperty(contractor, Contractors.PROP_CONTRACTOR_SHORTNAME).toString();
            String filename = shortname.replaceAll("[^_\\-\\dA-Za-zА-Яа-я ]", "");
            nodeService.setProperty(contractor, ContentModel.PROP_NAME, filename);
        }
*/
    }


    public void onUpdateContractor(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        boolean enabled = false;
        try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.contractors.editor.enabled");
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read contractor properties");
        }
        if (enabled) {
			List<AssociationRef> linkedOrgAssocs = nodeService.getSourceAssocs(nodeRef, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);

            if (linkedOrgAssocs.size() > 0) {
                Iterator<AssociationRef> assocIterator = linkedOrgAssocs.iterator();
                NodeRef rootUnit = orgstructureBean.getRootUnit();
                boolean find = false;

                while (assocIterator.hasNext() && !find) {
                    NodeRef node = assocIterator.next().getSourceRef();
                    if (OrgstructureBean.TYPE_ORGANIZATION_UNIT.equals(nodeService.getType(node)) &&
                            rootUnit.equals(nodeService.getPrimaryParent(node).getParentRef())) {
                        find = true;
                        PropertyMap props = new PropertyMap();
                        props.put(OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME, after.get(Contractors.PROP_CONTRACTOR_FULLNAME));
                        props.put(OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME, after.get(Contractors.PROP_CONTRACTOR_SHORTNAME));
                        props.put(OrgstructureBean.PROP_UNIT_CODE, after.get(Contractors.PROP_CONTRACTOR_CODE));
                        nodeService.addProperties(node, props);
                    }
                }
            }

            final Boolean nowActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
            final Boolean oldActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
            final boolean changed = !((nowActive == oldActive) || (oldActive != null && oldActive.equals(nowActive)));

            //если контрагент удаляется
            if (changed && !nowActive) {
                // Провеяряем аспект у контрагента
                if (nodeService.hasAspect(nodeRef, OrgstructureAspectsModel.ASPECT_IS_ORGANIZATION)) {

                    boolean hasActiveOrgUnits = false;
                    Iterator<AssociationRef> assocIterator = linkedOrgAssocs.iterator();
                    NodeRef rootUnit = orgstructureBean.getRootUnit();
                    NodeRef orgUnit = null;

                    while (assocIterator.hasNext() && !hasActiveOrgUnits) {
                        NodeRef node = assocIterator.next().getSourceRef();
                        if (OrgstructureBean.TYPE_ORGANIZATION_UNIT.equals(nodeService.getType(node))) {
                            if (rootUnit.equals(nodeService.getPrimaryParent(node).getParentRef())) {
                                orgUnit = node;
                            } else {
                                Boolean isActive = (Boolean) nodeService.getProperty(node, BaseBean.IS_ACTIVE);
                                if (Boolean.TRUE.equals(isActive)) {
                                    hasActiveOrgUnits = true;
                                }
                            }
                        }
                    }

                    if (hasActiveOrgUnits) {
                        throw new IllegalStateException("Невозможно удалить контрагента! На него ссылается орг.единица!");
                    } else if (orgUnit != null) {
                        nodeService.setProperty(orgUnit, BaseBean.IS_ACTIVE, false);
                    }
                }
            }
        }
    }
}
