package ru.it.lecm.reports.editor.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.reports.editor.ReportsEditorModel;

/**
 * User: dbashmakov
 * Date: 23.04.13
 * Time: 17:30
 */
public class ReportsEditorOnCreateAssocsPolicy extends LogicECMAssociationPolicy {
    @Override
    public final void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TYPE, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ReportsEditorModel.TYPE_REPORT_TYPE, new JavaBehaviour(this, "onCreateAssociation"));
    }
}
