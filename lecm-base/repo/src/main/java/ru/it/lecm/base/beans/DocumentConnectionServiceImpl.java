package ru.it.lecm.base.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 18.02.13
 * Time: 13:55
 */
public class DocumentConnectionServiceImpl extends BaseBean implements DocumentConnectionService {
	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;

	private final Object lock = new Object();

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	@Override
	public NodeRef getConnectionsRootRef() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DOCUMENT_CONNECTIONS_ROOT_NAME);
	}

	/**
	 * Получение узла подписки, в котором хрянится информация об подписках.
	 * Если такой узел отсутствует - он создаётся автоматически (внутри /CompanyHome)
	 * @return
	 */
	public NodeRef init() {
		final String rootName = DOCUMENT_CONNECTIONS_ROOT_NAME;
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef connectionsRef;
						synchronized (lock) {
							// еще раз пытаемся получить директорию (на случай если она уже была создана другим потоком
							connectionsRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
									rootName);
							if (connectionsRef == null) {
								QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
								QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
								QName nodeTypeQName = ContentModel.TYPE_FOLDER;

								Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, rootName);
								ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
								connectionsRef = associationRef.getChildRef();
							}
						}
						return connectionsRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}
}
