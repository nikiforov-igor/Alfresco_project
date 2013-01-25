package ru.it.lecm.businessjournal.schedule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * @author dbashmakov
 *         Date: 23.01.13
 *         Time: 17:24
 */
public class BusinessJournalArchiverSettings {

	private final String cronExpression = "0 0 2 */%s * ?";
	private String deep = "20"; // lecm.business-journal.archive.deep, default: 20 дней
	private String period = "5"; //lecm.business-journal.archive.period  - default: раз в 5 дней
	private NodeRef archiveSettingsRef;
	private BusinessJournalService businessJournalService;
	private TransactionService transactionService;
	private NodeService nodeService;

	public String getCronExpression() {
		return String.format(cronExpression, period);
	}

	public void setDeep(String archiveDeep) {
		this.deep = !archiveDeep.equals("${lecm.business-journal.archive.deep}") ? archiveDeep : this.deep;
	}

	public String getDeep() {
		return this.deep;
	}

	public String getPeriod() {
		return this.period;
	}
	public void setPeriod(String period) {
		this.period = !period.equals("${lecm.business-journal.archive.period}")? period  : this.period;
	}

	public void init() {
		final NodeRef bjRef = businessJournalService.getBusinessJournalDirectory();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef archiverRef = nodeService.getChildByName(bjRef, ContentModel.ASSOC_CONTAINS, BusinessJournalService.BJ_ARCHIVER_SETTINGS_NAME);
						if (archiverRef == null) {
							QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
							QName assocQName = QName.createQName(BusinessJournalService.BJ_NAMESPACE_URI, BusinessJournalService.BR_ARCHIVE_SETTINGS_ASSOC_QNAME);
							QName nodeTypeQName = BusinessJournalService.TYPE_ARCHIVER_SETTINGS;

							Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
							properties.put(ContentModel.PROP_NAME, BusinessJournalService.BJ_ARCHIVER_SETTINGS_NAME);
							properties.put(BusinessJournalService.PROP_ARCHIVER_DEEP, getDeep());
							properties.put(BusinessJournalService.PROP_ARCHIVER_PERIOD, getPeriod());

							ChildAssociationRef associationRef = nodeService.createNode(bjRef, assocTypeQName, assocQName, nodeTypeQName, properties);
							archiverRef = associationRef.getChildRef();
						}
						return archiverRef;
					}
				});
			}
		};
		archiveSettingsRef = AuthenticationUtil.runAsSystem(raw);
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public NodeRef getArchiveSettingsRef() {
		return archiveSettingsRef;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
}
