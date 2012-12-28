/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.wcalendar.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWCalCommon;

/**
 *
 * @author vlevin
 */
public abstract class AbstractWCalCommonBean implements IWCalCommon, AuthenticationUtil.RunAsWork<NodeRef> {

	protected final static String WCAL_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/1.0";
	protected final static String SHEDULE_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/shedule/1.0";
	protected final static String ABSENCE_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/absence/1.0";
	protected final static String CALENDAR_NAMESPACE = "http://www.it.ru/logicECM/model/work-calendar/calendar/1.0";
	protected Repository repository;
	protected NodeService nodeService;
	protected TransactionService transactionService;
	final private static Logger logger = LoggerFactory.getLogger(AbstractWCalCommonBean.class);

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRepositoryHelper(Repository repository) {
		this.repository = repository;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@Override
	public NodeRef getWCalendarContainer() {
		NodeRef calendarContainer = null;
		try {
			calendarContainer = doWork();
		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
		}
		return calendarContainer;
	}

	protected abstract Map<String, Object> containerParams();

	@Override
	public NodeRef doWork() throws Exception {
		repository.init();
		final NodeRef companyHome = repository.getCompanyHome();
		final Map<String, Object> params = containerParams();
		NodeRef container = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, (String) params.get("CONTAINER_NAME"));
		if (container == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
			container = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
				@Override
				public NodeRef execute() throws Throwable {
					NodeRef parentRef = companyHome; //the parent node
					QName assocTypeQName = ContentModel.ASSOC_CONTAINS; //the type of the association to create. This is used for verification against the data dictionary.
					QName assocQName = QName.createQName(WCAL_NAMESPACE, (String) params.get("CONTAINER_NAME")); //the qualified name of the association
					QName nodeTypeQName = (QName) params.get("CONTAINER_TYPE"); //a reference to the node type
					Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
					properties.put(ContentModel.PROP_NAME, (String) params.get("CONTAINER_NAME"));
					ChildAssociationRef associationRef = nodeService.createNode(parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
					NodeRef rootContainer = associationRef.getChildRef();
					logger.debug(String.format("container node '%s' created", rootContainer.toString()));
					return rootContainer;
				}
			});
		}
		return container;
	}
}
