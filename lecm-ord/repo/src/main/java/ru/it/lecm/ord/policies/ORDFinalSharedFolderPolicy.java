package ru.it.lecm.ord.policies;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author snovikov
 */
public class ORDFinalSharedFolderPolicy {
	private final static Logger logger = LoggerFactory.getLogger(ORDFinalSharedFolderPolicy.class);

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private OrgstructureBean orgstructureService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ORDModel.TYPE_ORD,
				new JavaBehaviour(this,"onUpdateProperties"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
				ORDModel.TYPE_ORD, EDSDocumentService.ASSOC_FILE_REGISTER,
				new JavaBehaviour(this, "onCreateAssociation"));

		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				ORDModel.TYPE_ORD, EDSDocumentService.ASSOC_FILE_REGISTER,
				new JavaBehaviour(this, "onDeleteAssociation"));

	}

	public void onUpdateProperties(NodeRef ord, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		if (before.get(DocumentService.PROP_IS_SHARED_FOLDER)!=null && after.get(DocumentService.PROP_IS_SHARED_FOLDER)!=null){
			if (!before.get(DocumentService.PROP_IS_SHARED_FOLDER).equals(after.get(DocumentService.PROP_IS_SHARED_FOLDER))){
				Boolean isShared = (Boolean) after.get(DocumentService.PROP_IS_SHARED_FOLDER);
				if (isShared){
					NodeRef rootUnit = orgstructureService.getRootUnit();
					List<NodeRef> targetUnit = Arrays.asList(rootUnit);
					nodeService.setAssociations(ord, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, targetUnit);
				}else{
					List<AssociationRef> fileRegisterAssocs = nodeService.getTargetAssocs(ord, EDSDocumentService.ASSOC_FILE_REGISTER);
					if (fileRegisterAssocs.size()>0){
						NodeRef fileRegister = fileRegisterAssocs.get(0).getTargetRef();
						NodeRef fileRegisterDicUnit = nodeService.getPrimaryParent(fileRegister).getParentRef();
						if (null!=fileRegisterDicUnit){
							List<AssociationRef> fileRegisterUnitAssocs = nodeService.getTargetAssocs(fileRegisterDicUnit, ORDModel.ASSOC_DOCUMENT_FILE_REGISTER_UNIT);
							if (fileRegisterUnitAssocs.size()>0){
								NodeRef fileRegisterUnit = fileRegisterUnitAssocs.get(0).getTargetRef();
								List<NodeRef> targetUnit = Arrays.asList(fileRegisterUnit);
								nodeService.setAssociations(ord, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, targetUnit);
							}
						}
					}else{
						//очистим ассоциацию на подразделение (по-умолчанию)
						List<AssociationRef> orgUnitAssocs = nodeService.getTargetAssocs(ord, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC);
						for (AssociationRef orgUnitAssoc:orgUnitAssocs){
							NodeRef orgUnit = orgUnitAssoc.getTargetRef();
							nodeService.removeAssociation(ord, orgUnit, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC);
						}
					}
				}
			}
		}
	}

	public void onCreateAssociation(AssociationRef nodeAssocRef) {
		NodeRef ord = nodeAssocRef.getSourceRef();
		NodeRef fileRegister = nodeAssocRef.getTargetRef();
		Boolean isShared = (Boolean) nodeService.getProperty(ord, DocumentService.PROP_IS_SHARED_FOLDER);
		if (null!=isShared && !isShared){
			NodeRef fileRegisterDicUnit = nodeService.getPrimaryParent(fileRegister).getParentRef();
			if (null!=fileRegisterDicUnit){
				List<AssociationRef> fileRegisterUnitAssocs = nodeService.getTargetAssocs(fileRegisterDicUnit, ORDModel.ASSOC_DOCUMENT_FILE_REGISTER_UNIT);
				if (fileRegisterUnitAssocs.size()>0){
					NodeRef fileRegisterUnit = fileRegisterUnitAssocs.get(0).getTargetRef();
					List<NodeRef> targetUnit = Arrays.asList(fileRegisterUnit);
					nodeService.setAssociations(ord, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, targetUnit);
				}
			}
		}
	}

	public void onDeleteAssociation(AssociationRef nodeAssocRef) {
		NodeRef ord = nodeAssocRef.getSourceRef();
		Boolean isShared = (Boolean) nodeService.getProperty(ord, DocumentService.PROP_IS_SHARED_FOLDER);
		if (null!=isShared && !isShared){
			//проверка, не была ли создана новая ассоциация после удаления предыдущей
			List<AssociationRef> fileRegisterAssocs = nodeService.getTargetAssocs(ord, EDSDocumentService.ASSOC_FILE_REGISTER);
			if (fileRegisterAssocs.isEmpty()){
				//очистим ассоциацию на подразделение (по-умолчанию)
				List<AssociationRef> orgUnitAssocs = nodeService.getTargetAssocs(ord, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC);
				for (AssociationRef orgUnitAssoc:orgUnitAssocs){
					NodeRef orgUnit = orgUnitAssoc.getTargetRef();
					nodeService.removeAssociation(ord, orgUnit, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC);
				}
			}
		}
	}
}
