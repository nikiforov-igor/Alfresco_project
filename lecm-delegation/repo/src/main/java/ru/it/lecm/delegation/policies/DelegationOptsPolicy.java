package ru.it.lecm.delegation.policies;

import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.delegation.IDelegation;

/**
 * Policy которая обеспечивает автоматическое создание параметров делегирования для сотрудников
 * @author VLadimir Malygin
 * @since 12.12.2012 12:21:40
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
//BeforeDeleteNodePolicy OnRestoreNodePolicy пока не буду реализовывать, потому что непонятно а надо ли
public class DelegationOptsPolicy implements OnCreateNodePolicy, OnUpdateNodePolicy/*, BeforeDeleteNodePolicy, OnRestoreNodePolicy*/ {

	private final static QName TYPE_EMPLOYEE = QName.createQName ("http://www.it.ru/lecm/org/structure/1.0", "employee");

	private final static Logger logger = LoggerFactory.getLogger (DelegationOptsPolicy.class);

	private PolicyComponent policyComponent;
	private IDelegation delegationService;

	public final void init () {
		PropertyCheck.mandatory (this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour (OnCreateNodePolicy.QNAME, TYPE_EMPLOYEE, new JavaBehaviour (this, "onCreateNode"));
		policyComponent.bindClassBehaviour (OnUpdateNodePolicy.QNAME, TYPE_EMPLOYEE, new JavaBehaviour (this, "onUpdateNode"));
//		policyComponent.bindClassBehaviour (BeforeDeleteNodePolicy.QNAME, TYPE_EMPLOYEE, new JavaBehaviour (this, "beforeDeleteNode"));
//		policyComponent.bindClassBehaviour (OnRestoreNodePolicy.QNAME, TYPE_EMPLOYEE, new JavaBehaviour (this, "onRestoreNode"));
	}

	public void setPolicyComponent (PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDelegationService (IDelegation delegationService) {
		this.delegationService = delegationService;
	}

	@Override
	public void onCreateNode (final ChildAssociationRef childAssocRef) {
		//TODO судя по всему эту полиси можно удалить
		final NodeRef nodeRef = childAssocRef.getChildRef ();
		logger.info ("new employee sucessfully created. It's  nodeRef is '{}'", nodeRef);
		AuthenticationUtil.runAsSystem (new AuthenticationUtil.RunAsWork<NodeRef> () {
			@Override
			public NodeRef doWork () throws Exception {
				return delegationService.getOrCreateDelegationOpts (nodeRef);
			}
		});
	}

	@Override
	public void onUpdateNode (final NodeRef nodeRef) {
		logger.info ("employee with nodeRef '{}' sucessfully updated", nodeRef);
		AuthenticationUtil.runAsSystem (new AuthenticationUtil.RunAsWork<NodeRef> () {
			@Override
			public NodeRef doWork () throws Exception {
				return delegationService.getOrCreateDelegationOpts (nodeRef);
			}
		});
	}

//	@Override
//	public void beforeDeleteNode (final NodeRef nodeRef) {
//		logger.info ("employee with nodeRef '{}' is going to be deleted", nodeRef);
//		delegationService.getOrCreateDelegationOpts (nodeRef);
//	}
//
//	@Override
//	public void onRestoreNode (final ChildAssociationRef childAssocRef) {
//		NodeRef nodeRef = childAssocRef.getChildRef ();
//		logger.info ("employee successfully restored. It's nodeRef '{}'", nodeRef);
//		delegationService.getOrCreateDelegationOpts (nodeRef);
//	}
}
