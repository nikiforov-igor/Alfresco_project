package ru.it.lecm.subscriptions.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.subscriptions.beans.SubscriptionsBean;

/**
 * User: mShafeev
 * Date: 24.12.12
 * Time: 17:47
 */
public class SubscriptionsWebScriptBean extends BaseScopableProcessorExtension {


	/**
	 * Service registry
	 */
	protected ServiceRegistry services;

	/**
	 * Repository helper
	 */
	protected Repository repository;

	private SubscriptionsBean subscriptionsService;

	/**
	 * Set the service registry
	 *
	 * @param services the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) {
		this.services = services;
	}

	/**
	 * Set the repository helper
	 *
	 * @param repository the repository helper
	 */
	public void setRepositoryHelper(Repository repository) {
		this.repository = repository;
	}

	public void setSubscriptionsService(SubscriptionsBean subscriptionsService) {
		this.subscriptionsService = subscriptionsService;
	}

	/**
	 * Возвращает корневой узел подписчиков
	 *
	 * @return Созданный корневой узел подписчиков или Null, если произошла ошибка
	 */
	public ScriptNode getSubscriptions() {
		NodeRef subscribtions = subscriptionsService.ensureSubscriptionsRootRef();
		return new ScriptNode(subscribtions, services, getScope());
	}
}
