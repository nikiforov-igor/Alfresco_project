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
	private IDelegation delegationService;
	private NodeService nodeService;

	public void setPolicyComponent (PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDelegationService (IDelegation delegationService) {
		this.delegationService = delegationService;
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

	private void actualizeProcuracyActivity (final NodeRef nodeRef) {
		logger.info ("actualizing procuracy activity with nodeRef '{}'...", nodeRef);
		AuthenticationUtil.runAsSystem (new AuthenticationUtil.RunAsWork<Void> () {
			@Override
			public Void doWork () throws Exception {
				delegationService.actualizeProcuracyActivity (nodeRef);
				return null;
			}
		});
	}

	@Override
	public void onCreateAssociation (AssociationRef nodeAssocRef) {
		logger.info ("onCreateAssociation");
		NodeRef sourceRef = nodeAssocRef.getSourceRef ();
		NodeRef targetRef = nodeAssocRef.getTargetRef ();
		if (IDelegation.TYPE_PROCURACY.isMatch (nodeService.getType (sourceRef))) {
			actualizeProcuracyActivity (sourceRef);
		} else if (IDelegation.TYPE_PROCURACY.isMatch (nodeService.getType (targetRef))) {
			actualizeProcuracyActivity (targetRef);
		}
	}

	@Override
	public void onDeleteAssociation (AssociationRef nodeAssocRef) {
		logger.info ("onDeleteAssociation");
		NodeRef sourceRef = nodeAssocRef.getSourceRef ();
		NodeRef targetRef = nodeAssocRef.getTargetRef ();
		if (IDelegation.TYPE_PROCURACY.isMatch (nodeService.getType (sourceRef))) {
			actualizeProcuracyActivity (sourceRef);
		} else if (IDelegation.TYPE_PROCURACY.isMatch (nodeService.getType (targetRef))) {
			actualizeProcuracyActivity (targetRef);
		}
	}
}
