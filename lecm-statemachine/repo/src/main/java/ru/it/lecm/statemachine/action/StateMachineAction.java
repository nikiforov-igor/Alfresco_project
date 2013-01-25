package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.security.events.INodeACLBuilder;
import ru.it.lecm.statemachine.bean.StateMachineActions;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:29
 */
abstract public class StateMachineAction {

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private INodeACLBuilder lecmAclBuilderBean;

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setLecmAclBuilderBean(INodeACLBuilder lecmAclBuilderBean) {
		this.lecmAclBuilderBean = lecmAclBuilderBean;
	}

	public INodeACLBuilder getLecmAclBuilderBean() {
		return lecmAclBuilderBean;
	}

	abstract public void execute(DelegateExecution execution);

	abstract public void init(Element actionElement, String processId);

	public String getActionName() {
		return StateMachineActions.getActionName(getClass());
	}

	protected NodeRef createFolder(NodeRef parent, String name) {
		return createFolder(parent, name, null);
	}

	protected NodeRef createFolder(final NodeRef parent, final String name, String uuid) {
		final NodeService nodeService = getServiceRegistry().getNodeService();
		final HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
		props.put(ContentModel.PROP_NAME, name);
		if (uuid != null) {
			props.put(ContentModel.PROP_NODE_UUID, uuid);
		}
		ChildAssociationRef childAssocRef = serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef>() {
			@Override
			public ChildAssociationRef execute() throws Throwable {
				ChildAssociationRef childAssocRef = nodeService.createNode(
						parent,
						ContentModel.ASSOC_CONTAINS,
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
						ContentModel.TYPE_FOLDER,
						props);
				return childAssocRef;
			}
		}, false, true);
		return childAssocRef.getChildRef();
	}

	protected NodeRef getCompanyHome() {
		return repositoryHelper.getCompanyHome();
	}
}
