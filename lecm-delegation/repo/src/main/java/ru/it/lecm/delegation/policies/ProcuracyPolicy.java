package ru.it.lecm.delegation.policies;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
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
public class ProcuracyPolicy implements OnUpdateNodePolicy, OnUpdatePropertiesPolicy, OnCreateAssociationPolicy, OnDeleteAssociationPolicy {

	private final static Logger logger = LoggerFactory.getLogger (ProcuracyPolicy.class);

	private final static QName TYPE_PROCURACY = QName.createQName ("http://www.it.ru/logicECM/model/delegation/1.0", "procuracy");

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
		logger.info ("initializing ProcuracyPolicy...");
		logger.info ("initializing OnUpdateNodePolicy");
		policyComponent.bindClassBehaviour (OnUpdateNodePolicy.QNAME, TYPE_PROCURACY, new JavaBehaviour (this, "onUpdateNode"));
		policyComponent.bindPropertyBehaviour (OnUpdateNodePolicy.QNAME, TYPE_PROCURACY, new JavaBehaviour (this, "onUpdateNode"));
		policyComponent.bindAssociationBehaviour (OnUpdateNodePolicy.QNAME, TYPE_PROCURACY, new JavaBehaviour (this, "onUpdateNode"));
		logger.info ("initializing OnUpdatePropertiesPolicy");
		policyComponent.bindClassBehaviour (OnUpdatePropertiesPolicy.QNAME, TYPE_PROCURACY, new JavaBehaviour (this, "onUpdateProperties"));
		logger.info ("initializing OnCreateAssociationPolicy");
		policyComponent.bindClassBehaviour (OnCreateAssociationPolicy.QNAME, TYPE_PROCURACY, new JavaBehaviour (this, "onCreateAssociation"));
		logger.info ("initializing OnDeleteAssociationPolicy");
		policyComponent.bindClassBehaviour (OnDeleteAssociationPolicy.QNAME, TYPE_PROCURACY, new JavaBehaviour (this, "onDeleteAssociation"));
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
	public void onUpdateNode (final NodeRef nodeRef) {
		actualizeProcuracyActivity (nodeRef);
	}

	@Override
	public void onUpdateProperties (NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		actualizeProcuracyActivity (nodeRef);
	}

	@Override
	public void onCreateAssociation (AssociationRef nodeAssocRef) {
		NodeRef sourceRef = nodeAssocRef.getSourceRef ();
		NodeRef targetRef = nodeAssocRef.getTargetRef ();
		if (TYPE_PROCURACY.isMatch (nodeService.getType (sourceRef))) {
			actualizeProcuracyActivity (sourceRef);
		} else if (TYPE_PROCURACY.isMatch (nodeService.getType (targetRef))) {
			actualizeProcuracyActivity (targetRef);
		}
	}

	@Override
	public void onDeleteAssociation (AssociationRef nodeAssocRef) {
		NodeRef sourceRef = nodeAssocRef.getSourceRef ();
		NodeRef targetRef = nodeAssocRef.getTargetRef ();
		if (TYPE_PROCURACY.isMatch (nodeService.getType (sourceRef))) {
			actualizeProcuracyActivity (sourceRef);
		} else if (TYPE_PROCURACY.isMatch (nodeService.getType (targetRef))) {
			actualizeProcuracyActivity (targetRef);
		}
	}
}
