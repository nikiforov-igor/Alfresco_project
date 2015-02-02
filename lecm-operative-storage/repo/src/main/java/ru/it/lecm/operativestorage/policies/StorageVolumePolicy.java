/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.policies;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;

/**
 *
 * @author ikhalikov
 */
public class StorageVolumePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, OperativeStorageService.TYPE_NOMENCLATURE_CASE_VOLUME, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	private void updateVolumesCount(NodeRef ndNodeRef) {
		Set<QName> types = new HashSet<QName>();
		types.add(OperativeStorageService.TYPE_NOMENCLATURE_CASE_VOLUME);

		int volumeCount = 0;

		List<ChildAssociationRef> volumes = nodeService.getChildAssocs(ndNodeRef, types);
		for(ChildAssociationRef volume : volumes) {
			Boolean status = (Boolean) nodeService.getProperty(volume.getChildRef(), BaseBean.IS_ACTIVE);
			if(status == null || status){
				volumeCount++;
			}
		}

		nodeService.setProperty(ndNodeRef, OperativeStorageService.PROP_NOMENCLATURE_VOLUMES_NUMBER, volumeCount);
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		NodeRef parent = nodeService.getParentAssocs(nodeRef).get(0).getParentRef();
		updateVolumesCount(parent);
	}

}
