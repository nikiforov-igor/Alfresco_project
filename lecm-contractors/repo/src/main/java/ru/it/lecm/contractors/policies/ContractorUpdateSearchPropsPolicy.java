package ru.it.lecm.contractors.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 14.11.2016
 * Time: 10:23
 */
public class ContractorUpdateSearchPropsPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    private final QName[] AFFECTED_PROPERTIES = {Contractors.PROP_CONTRACTOR_FULLNAME, Contractors.PROP_CONTRACTOR_SHORTNAME};
    private final QName[] DIC_REPLACE_PROPERTIES = {Contractors.PROP_LEGALFORM_FULL_TITLE, Contractors.PROP_LEGALFORM_SHORT_TITLE};

    private final String SEARCH_POSTFIX = "-search";
    private final String OPF_DIC_NAME = "Контрагенты Организационно-правовые формы";

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private DictionaryBean dictionaryService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                Contractors.TYPE_CONTRACTOR, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (nodeService.exists(nodeRef)) {
            List<QName> changedProps = getAffectedProperties(before, after);
            Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
            NodeRef dicOPF = dictionaryService.getDictionaryByName(OPF_DIC_NAME);

            boolean hasChanges = false;
            for (QName changedProp : changedProps) {
                QName searchProp = QName.createQName(Contractors.CONTRACTOR_NAMESPACE, changedProp.getLocalName() + SEARCH_POSTFIX);

                final String currentValue = (String) properties.get(changedProp);

                //1. удалить спец символы
                String updatedValue = delNoDigOrLet(currentValue);
                //2. удалить все коды из справочника ОПФ
                if (dicOPF != null) {
                    updatedValue = delDicValuesFromString(updatedValue, dicOPF);
                }

                properties.put(searchProp, updatedValue);
                hasChanges = true;
            }
            if (hasChanges) {
                nodeService.setProperties(nodeRef, properties);
            }
        }
    }

    private List<QName> getAffectedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after) {
        List<QName> result = new ArrayList<>();
        for (QName affected : AFFECTED_PROPERTIES) {
            Object prev = before.get(affected);
            Object cur = after.get(affected);
            if (cur != null && !cur.equals(prev)) {
                result.add(affected);
            }
        }
        return result;
    }

    private String delNoDigOrLet(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isLetterOrDigit(ch) || Character.isSpaceChar(ch)) {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    private String delDicValuesFromString(String str, NodeRef dictionary) {
        List<NodeRef> children = dictionaryService.getChildren(dictionary);
        for (NodeRef child : children) {
            Map<QName, Serializable> recordProps = nodeService.getProperties(child);
            for (QName dicProp : DIC_REPLACE_PROPERTIES) {
                Object dicPropValue = recordProps.get(dicProp);
                if (dicPropValue != null && dicPropValue.toString().length() > 0) {
                    str = str.replaceAll(dicPropValue.toString(), "");
                }
            }
        }
        return str;
    }
}
