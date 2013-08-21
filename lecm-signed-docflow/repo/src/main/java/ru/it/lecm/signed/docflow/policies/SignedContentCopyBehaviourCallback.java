package ru.it.lecm.signed.docflow.policies;

import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

/**
 *
 * @author vlevin
 */
public class SignedContentCopyBehaviourCallback extends DefaultCopyBehaviourCallback {

	@Override
	public Pair<AssocCopySourceAction, AssocCopyTargetAction> getAssociationCopyAction(
			QName classQName, CopyDetails copyDetails, CopyAssociationDetails assocCopyDetails) {
		AssociationRef assocRef = assocCopyDetails.getAssocRef();
		QName typeQName = assocRef.getTypeQName();
		if (typeQName.equals(SignedDocflowModel.ASSOC_SIGN_TO_CONTENT)) {
			return new Pair<AssocCopySourceAction, AssocCopyTargetAction>(
					AssocCopySourceAction.IGNORE,
					AssocCopyTargetAction.USE_COPIED_TARGET);
		} else {
			return new Pair<AssocCopySourceAction, AssocCopyTargetAction>(
					AssocCopySourceAction.COPY_REMOVE_EXISTING,
					AssocCopyTargetAction.USE_COPIED_OTHERWISE_ORIGINAL_TARGET);
		}
	}
}
