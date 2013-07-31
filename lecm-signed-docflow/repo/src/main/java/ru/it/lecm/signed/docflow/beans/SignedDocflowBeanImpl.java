package ru.it.lecm.signed.docflow.beans;

import java.util.Set;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.signed.docflow.api.SignedDocflowBean;

/**
 *
 * @author vlevin
 */
public class SignedDocflowBeanImpl extends BaseBean implements SignedDocflowBean {

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
//		PropertyCheck.mandatory(this, "transactionService", transactionService);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		throw new UnsupportedOperationException("Not supported yet.");
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
}
