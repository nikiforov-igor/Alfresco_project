package ru.it.lecm.signed.docflow.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author vlevin
 */
public class SignedDocflowImpl extends BaseBean implements SignedDocflow {

	public final static String SIGNED_DOCFLOW_FOLDER = "SIGNED_DOCFLOW_FOLDER";

	private OrgstructureBean orgstructureService;

	private void addAttributesToOrganization() {
		AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {

			@Override
			public Void doWork() throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				return transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {

					@Override
					public Void execute() throws Throwable {
						NodeRef organizationRef = orgstructureService.getOrganization();
						Set<QName> aspects = nodeService.getAspects(organizationRef);
						if(!aspects.contains(ASPECT_ORGANIZATION_ATTRS)) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
							properties.put(PROP_OPERATOR_CODE, "2AE"); //временно для тестов
							properties.put(PROP_PARTNER_KEY, "C570E2AB-C55F-49BE-B56A-233046F829A8"); //временно для тестов
							properties.put(PROP_ORGANIZATION_ID, "");
							properties.put(PROP_ORGANIZATION_EDO_ID, "");
							nodeService.addAspect(organizationRef, ASPECT_ORGANIZATION_ATTRS, properties);
						}
						return null;
					}
				});
			}
		});
	}

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);

		addAttributesToOrganization();
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public NodeRef getSignedDocflowFolder() {
		return getFolder(SIGNED_DOCFLOW_FOLDER);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getSignedDocflowFolder();
	}

	@Override
	public boolean isDocflowable(NodeRef nodeRef) {
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		return aspects.contains(ASPECT_DOCFLOWABLE);
	}

	@Override
	public boolean isSignable(NodeRef nodeRef) {
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		return aspects.contains(ASPECT_SIGNABLE);
	}

	@Override
	public void addDocflowableAspect(NodeRef nodeRef) {
		nodeService.addAspect(nodeRef, ASPECT_DOCFLOWABLE, null);
	}

	@Override
	public void removeDocflowableAspect(NodeRef nodeRef) {
		nodeService.removeAspect(nodeRef, ASPECT_DOCFLOWABLE);
	}

	@Override
	public void addSignableAspect(NodeRef nodeRef) {
		nodeService.addAspect(nodeRef, ASPECT_SIGNABLE, null);
	}

	@Override
	public void removeSignableAspect(NodeRef nodeRef) {
		nodeService.removeAspect(nodeRef, ASPECT_SIGNABLE);
	}
}
