package ru.it.lecm.contractors.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;

/**
 * User: pmelnikov
 * Date: 07.04.14
 * Time: 13:28
 */
public class ContractorsCreatePolicy implements NodeServicePolicies.OnCreateNodePolicy {


    private LecmBasePropertiesService propertiesService;
    private PolicyComponent policyComponent;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }


    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "contractor-type"),
                new JavaBehaviour(this, "onCreateNode"));
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

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }
}
