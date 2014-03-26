package ru.it.lecm.outgoing.policy;

import java.util.Arrays;
import java.util.List;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteAssociationPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.outgoing.api.OutgoingModel;

/**
 *
 * @author vmalygin
 */
public class OutgoingFileRegisterAssocPolicy implements OnCreateAssociationPolicy, OnDeleteAssociationPolicy {

	private final static String DOCUMENT_FILE_REGISTER_NAMESPACE = "http://www.it.ru/logicECM/document/dictionaries/fileRegister/1.0";
	private final static QName ASSOC_DOCUMENT_FILE_REGISTER_UNIT = QName.createQName(DOCUMENT_FILE_REGISTER_NAMESPACE, "organization-unit-assoc");


	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private OrgstructureBean orgstructureService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);

		policyComponent.bindAssociationBehaviour(OnCreateAssociationPolicy.QNAME, OutgoingModel.TYPE_OUTGOING, EDSDocumentService.ASSOC_FILE_REGISTER, new JavaBehaviour(this, "onCreateAssociation", NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindAssociationBehaviour(OnDeleteAssociationPolicy.QNAME, OutgoingModel.TYPE_OUTGOING, EDSDocumentService.ASSOC_FILE_REGISTER, new JavaBehaviour(this, "onDeleteAssociation", NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef outgoingRef = nodeAssocRef.getSourceRef();
		NodeRef fileRegisterRef = nodeAssocRef.getTargetRef();
		NodeRef fileRegisterDicUnit = nodeService.getPrimaryParent(fileRegisterRef).getParentRef();
		if (fileRegisterDicUnit != null) {
			List<AssociationRef> fileRegisterUnitAssocs = nodeService.getTargetAssocs(fileRegisterDicUnit, ASSOC_DOCUMENT_FILE_REGISTER_UNIT);
			if (fileRegisterUnitAssocs.size() > 0) {
				NodeRef fileRegisterUnit = fileRegisterUnitAssocs.get(0).getTargetRef();
				List<NodeRef> targetUnit = Arrays.asList(fileRegisterUnit);
				nodeService.setAssociations(outgoingRef, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, targetUnit);
			}
		}

	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef outgoingRef = nodeAssocRef.getSourceRef();
		NodeRef fileRegisterRef = nodeAssocRef.getTargetRef();
		NodeRef rootUnit = orgstructureService.getRootUnit();
		List<NodeRef> targetUnit = Arrays.asList(rootUnit);
		nodeService.setAssociations(outgoingRef, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, targetUnit);
	}
}
