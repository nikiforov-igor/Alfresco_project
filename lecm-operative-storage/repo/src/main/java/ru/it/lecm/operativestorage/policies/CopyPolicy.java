/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.policies;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.CopyServicePolicies.OnCopyCompletePolicy;
import org.alfresco.repo.copy.CopyServicePolicies.OnCopyNodePolicy;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;

/**
 *
 * @author ikhalikov
 */
public class CopyPolicy implements OnCopyNodePolicy, OnCopyCompletePolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private DictionaryService dictionaryService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindClassBehaviour(OnCopyNodePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE,
				new JavaBehaviour(this, "getCopyCallback"));

		policyComponent.bindClassBehaviour(OnCopyNodePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_YEAR_SECTION,
				new JavaBehaviour(this, "getCopyCallback"));

		policyComponent.bindClassBehaviour(OnCopyCompletePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE,
				new JavaBehaviour(this, "onCopyComplete"));

		policyComponent.bindClassBehaviour(OnCopyCompletePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_YEAR_SECTION,
				new JavaBehaviour(this, "onCopyComplete"));

		policyComponent.bindClassBehaviour(OnCopyCompletePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_UNIT_SECTION,
				new JavaBehaviour(this, "onCopyComplete"));

		policyComponent.bindClassBehaviour(OnCopyNodePolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_UNIT_SECTION,
				new JavaBehaviour(this, "getCopyCallback"));

	}

	@Override
	public CopyBehaviourCallback getCopyCallback(QName classRef, CopyDetails copyDetails) {
		return new NomenclatureCaseCopyBehaviour();
	}

	@Override
	public void onCopyComplete(QName classRef, NodeRef sourceNodeRef, NodeRef targetNodeRef, boolean copyToNewNode, Map<NodeRef, NodeRef> copyMap) {

		if(classRef.equals(OperativeStorageService.TYPE_NOMENCLATURE_CASE)) {
			nodeService.setProperty(targetNodeRef, OperativeStorageService.PROP_NOMENCLATURE_CASE_STATUS, "PROJECT");
		}

		if(classRef.equals(OperativeStorageService.TYPE_NOMENCLATURE_UNIT_SECTION)) {
			nodeService.setProperty(targetNodeRef, OperativeStorageService.PROP_NOMENCLATURE_UNIT_SECTION_STATUS, "PROJECT");
		}

		if(classRef.equals(OperativeStorageService.TYPE_NOMENCLATURE_YEAR_SECTION)) {
			Map<QName, Serializable> props = nodeService.getProperties(targetNodeRef);
			Integer oldYear = (Integer) nodeService.getProperty(sourceNodeRef, OperativeStorageService.PROP_NOMENCLATURE_YEAR_SECTION_YEAR);

			props.put(OperativeStorageService.PROP_NOMENCLATURE_YEAR_SECTION_YEAR, oldYear + 1);
			props.put(OperativeStorageService.PROP_NOMENCLATURE_YEAR_SECTION_STATUS, "PROJECT");
			props.put(OperativeStorageService.PROP_NOMENCLATURE_CASE_YEAR_COMMENT, "Создано автоматически на основании номенклатуры предыдущего года");

			nodeService.setProperties(targetNodeRef, props);
		}

	}

	private class NomenclatureCaseCopyBehaviour extends DefaultCopyBehaviourCallback {

		@Override
		public Pair<CopyBehaviourCallback.AssocCopySourceAction, CopyBehaviourCallback.AssocCopyTargetAction> getAssociationCopyAction(
				QName classQName, CopyDetails copyDetails, CopyBehaviourCallback.CopyAssociationDetails assocCopyDetails) {

			AssociationRef assocRef = assocCopyDetails.getAssocRef();
			QName typeQName = assocRef.getTypeQName();
			if (OperativeStorageService.ASSOC_NOMENCLATURE_CASE_YEAR.equals(typeQName)) {
				return new Pair<>(
						CopyBehaviourCallback.AssocCopySourceAction.IGNORE,
						CopyBehaviourCallback.AssocCopyTargetAction.USE_COPIED_TARGET
				);
			} else {
				return new Pair<>(
						CopyBehaviourCallback.AssocCopySourceAction.COPY_REMOVE_EXISTING,
						CopyBehaviourCallback.AssocCopyTargetAction.USE_COPIED_OTHERWISE_ORIGINAL_TARGET);
			}

		}

		@Override
		public CopyBehaviourCallback.ChildAssocCopyAction getChildAssociationCopyAction(
				QName classQName, CopyDetails copyDetails, CopyBehaviourCallback.CopyChildAssociationDetails childAssocCopyDetails) {

			ChildAssociationRef assocRef = childAssocCopyDetails.getChildAssocRef();
			NodeRef child = assocRef.getChildRef();
			QName type = nodeService.getType(child);
			if (OperativeStorageService.TYPE_NOMENCLATURE_VOLUME.equals(type) || OperativeStorageService.TYPE_NOMENCLATURE_UNIT_SECTION.equals(type) || OperativeStorageService.TYPE_NOMENCLATURE_CASE.equals(type)) {
				return ChildAssocCopyAction.COPY_CHILD;
			}
			return ChildAssocCopyAction.IGNORE;


		}


	}





}
