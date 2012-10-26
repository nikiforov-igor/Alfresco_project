package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 *
 * @author VLadimir Malygin
 * @since 19.10.2012 10:48:49
 * @see <p>mailto: <a href="mailto:vladimir.malygin@aplana.com">vladimir.malygin@aplana.com</a></p>
 */
public class GenerateTestUnit extends DeclarativeWebScript {

	private final static Logger logger = LoggerFactory.getLogger (GenerateTestUnit.class);

	private Repository repository;
	private ServiceRegistry serviceRegistry;

	@Override
	protected Map<String, Object> executeImpl (WebScriptRequest req, Status status, Cache cache) {

		TransactionService transactionService = serviceRegistry.getTransactionService ();

		logger.debug ("generating new test-unit object");

		NodeRef testUnit = transactionService.getRetryingTransactionHelper ().doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef> () {

			@Override
			public NodeRef execute () throws Throwable {
				NodeService nodeService = serviceRegistry.getNodeService ();

				NodeRef userHome = repository.getUserHome (repository.getPerson ());
				QName testUnit = QName.createQName ("http://www.it.ru/lecm/delegation/1.0", "test-unit");
				HashMap<QName, Serializable> properties = new HashMap<QName, Serializable> ();
				QName testUnitId = QName.createQName ("http://www.it.ru/lecm/delegation/1.0", "testUnitId");
				QName testUnitName = QName.createQName ("http://www.it.ru/lecm/delegation/1.0", "testUnitName");
				QName testUnitTitle = QName.createQName ("http://www.it.ru/lecm/delegation/1.0", "testUnitTitle");
				QName testUnitDate = QName.createQName ("http://www.it.ru/lecm/delegation/1.0", "testUnitDate");

				properties.put (testUnitId, UUID.randomUUID ().toString ());
				properties.put (testUnitName, "someData");
				properties.put (testUnitTitle, "this is some data. It is unique by its id and can be serialized to json");
				properties.put (testUnitDate, new Date ());

				ChildAssociationRef ref = nodeService.createNode (userHome, ContentModel.ASSOC_CONTAINS, QName.createQName ("http://www.it.ru/lecm/delegation/1.0", "testUnitAssoc"), testUnit, properties);
				return ref.getChildRef ();
			}
		});
		HashMap<String, Object> map = new HashMap<String, Object> ();
		map.put ("result", testUnit.toString ());
		map.put ("result2", testUnit);
		return map;
	}

	public void setRepository (Repository repository) {
		this.repository = repository;
	}

	public void setServiceRegistry (ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}
