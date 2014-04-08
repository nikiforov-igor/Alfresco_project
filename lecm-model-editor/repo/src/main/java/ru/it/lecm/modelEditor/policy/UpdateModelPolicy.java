package ru.it.lecm.modelEditor.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;

import java.io.Serializable;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 07.04.14
 * Time: 15:18
 */
public class UpdateModelPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {


    private LecmBasePropertiesService propertiesService;
    private PolicyComponent policyComponent;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }


    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ContentModel.TYPE_DICTIONARY_MODEL,
                new JavaBehaviour(this, "onUpdateProperties"));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.model.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

            if (!enabled) {
                throw new IllegalStateException("Cannot read model editor properties properties");
            }
        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read model editor properties properties");
        }
    }

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }
}
