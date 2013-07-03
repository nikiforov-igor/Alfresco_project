package ru.it.lecm.delegation.policies;

import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteAssociationPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.delegation.IDelegation;

/**
 * Policy которая меняет active true/false у доверенности в зависимости от наличия ассоциации на доверенное лицо
 * @author VLadimir Malygin
 * @since 27.12.2012 12:51:34
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class ProcuracyPolicy implements OnCreateAssociationPolicy, OnDeleteAssociationPolicy {

	private final static Logger logger = LoggerFactory.getLogger (ProcuracyPolicy.class);

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setPolicyComponent (PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService (NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init () {
		PropertyCheck.mandatory (this, "policyComponent", policyComponent);
		logger.info ("initializing ProcuracyPolicy.onCreateAssociation");
		policyComponent.bindAssociationBehaviour (OnCreateAssociationPolicy.QNAME, IDelegation.TYPE_PROCURACY, new JavaBehaviour (this, "onCreateAssociation"));
		logger.info ("initializing ProcuracyPolicy.onDeleteAssociation");
		policyComponent.bindAssociationBehaviour (OnDeleteAssociationPolicy.QNAME, IDelegation.TYPE_PROCURACY, new JavaBehaviour (this, "onDeleteAssociation"));
	}

	private void resetProcuracyActivity (final NodeRef procuracyRef) {
		logger.info ("actualizing procuracy activity with nodeRef '{}'...", procuracyRef);
		AuthenticationUtil.runAsSystem (new AuthenticationUtil.RunAsWork<Void> () {
			@Override
			public Void doWork () throws Exception {
				nodeService.setProperty(procuracyRef, BaseBean.IS_ACTIVE, false);
				return null;
			}
		});
	}

	private void resetProcuracyActivity(final AssociationRef procuracyAssocRef) {
		NodeRef sourceRef = procuracyAssocRef.getSourceRef ();
		NodeRef targetRef = procuracyAssocRef.getTargetRef ();
		if (IDelegation.TYPE_PROCURACY.isMatch (nodeService.getType (sourceRef))) {
			resetProcuracyActivity (sourceRef);
		} else if (IDelegation.TYPE_PROCURACY.isMatch (nodeService.getType (targetRef))) {
			resetProcuracyActivity (targetRef);
		}
	}

	@Override
	public void onCreateAssociation (AssociationRef nodeAssocRef) {
		logger.info ("onCreateAssociation");
		resetProcuracyActivity(nodeAssocRef);
	}

	@Override
	public void onDeleteAssociation (AssociationRef nodeAssocRef) {
		logger.info ("onDeleteAssociation");
		resetProcuracyActivity(nodeAssocRef);
	}
}
