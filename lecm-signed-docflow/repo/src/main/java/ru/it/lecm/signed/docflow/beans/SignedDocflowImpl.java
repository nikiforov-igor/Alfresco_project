package ru.it.lecm.signed.docflow.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.signed.docflow.UnicloudService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;
import static ru.it.lecm.signed.docflow.api.SignedDocflow.ASSOC_SIGN_TO_CONTENT;

/**
 *
 * @author vlevin
 */
public class SignedDocflowImpl extends BaseBean implements SignedDocflow {

	public final static String SIGNED_DOCFLOW_FOLDER = "SIGNED_DOCFLOW_FOLDER";
	private OrgstructureBean orgstructureService;
	private UnicloudService unicloudService;

	public void setUnicloudService(UnicloudService unicloudService) {
		this.unicloudService = unicloudService;
	}

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
						if (!aspects.contains(ASPECT_ORGANIZATION_ATTRS)) {
							Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
							properties.put(PROP_OPERATOR_CODE, "");
							properties.put(PROP_PARTNER_KEY, "");
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
		PropertyCheck.mandatory(this, "unicloudService", unicloudService);

		addAttributesToOrganization();
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
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

	@Override
	public Map<String, Object> signContent(final Map<QName, Serializable> signatureProperties) {
		final String signatureContent = (String) signatureProperties.get(PROP_SIGNATURE_CONTENT);
		final String contentRefStr = (String) signatureProperties.remove(ASSOC_SIGN_TO_CONTENT);
		
		Map<String, Object> verifySignatureResponse = unicloudService.verifySignature(contentRefStr, signatureContent);
		if (verifySignatureResponse.containsKey("isSignatureValid")) {
			boolean isSignatureValid = (Boolean) verifySignatureResponse.get("isSignatureValid");
			if (isSignatureValid) {
				signatureProperties.put(PROP_IS_VALID, true);
				signatureProperties.put(PROP_UPDATE_DATE, signatureProperties.get(PROP_SIGNING_DATE));
				signatureProperties.put(PROP_IS_OUR, true); // CHANGE ME!
				NodeRef signaturesFolder = getSignedDocflowFolder();
				QName assocQName = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, UUID.randomUUID().toString());
				NodeRef signatureNode = nodeService.createNode(signaturesFolder, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_SIGN, signatureProperties).getChildRef();
				nodeService.createAssociation(signatureNode, new NodeRef(contentRefStr), ASSOC_SIGN_TO_CONTENT);
			} else {
				throw new WebScriptException(String.format("Error verifying signature: pair document [%s] signature was corrupted on the way to server", contentRefStr));
			}
		}
		return verifySignatureResponse;
	}
}
