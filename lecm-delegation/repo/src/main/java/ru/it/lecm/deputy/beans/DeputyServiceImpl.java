/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.deputy.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.transaction.UserTransaction;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.deputy.DeputyService;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author ikhalikov
 */
public class DeputyServiceImpl extends BaseBean implements DeputyService {

	private OrgstructureBean orgstructureService;
	private DictionaryBean lecmDictionaryService;
	private String defultSubjectDictionaryName;
	private NodeRef deputySettingsNode;

	public void setDefultSubjectDictionaryName(String defultSubjectDictionaryName) {
		this.defultSubjectDictionaryName = defultSubjectDictionaryName;
	}

	public void setLecmDictionaryService(DictionaryBean lecmDictionaryService) {
		this.lecmDictionaryService = lecmDictionaryService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getDeputyFolder();
	}

	@Override
	public NodeRef getDeputyFolder() {
		return getFolder(DEPUTY_FOLDER);
	}
	
	@Override
	protected void onBootstrap(ApplicationEvent event)
	{
		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
        transactionHelper.setForceWritable(true);
        transactionHelper.doInTransaction(
			new RetryingTransactionHelper.RetryingTransactionCallback<Object>(){
				@Override
				public Object execute() throws Throwable {
					return AuthenticationUtil.runAs(new RunAsWork<Object>()
			        {
			            public Object doWork()
			            {
							UserTransaction userTransaction = transactionService.getUserTransaction();
					        try
					        {
					            userTransaction.begin();
								if (getDeputySettingsNode() == null) {
									createDeputySettingsNode();
								}
								userTransaction.commit();
					        }
					        catch(Throwable e)
					        {
					            // rollback the transaction
					            try
					            { 
					                if (userTransaction != null) 
					                {
					                    userTransaction.rollback();
					                }
					            }
					            catch (Exception ex)
					            {
					                // NOOP 
					            }
					            throw new AlfrescoRuntimeException("Service folders [deputy-settings-node] bootstrap failed", e);
					        }
							return null;
			            }
			        }, AuthenticationUtil.getSystemUserName());
				}
			},false,true);
	}

	@Override
	public NodeRef getDeputySettingsFolder() {
		return getFolder(DEPUTY_SETTINGS_FOLDER);
	}

	@Override
	public NodeRef getDeputySettingsNode() {
		if (deputySettingsNode == null) {
			NodeRef settingsFolder = getDeputySettingsFolder();
			List<ChildAssociationRef> settingsNodeAssocs = null;
			if(settingsFolder!=null)
				settingsNodeAssocs = nodeService.getChildAssocs(settingsFolder);

			if (settingsNodeAssocs != null && !settingsNodeAssocs.isEmpty()) {
//				return settingsNodeAssocs.get(0).getChildRef();
				deputySettingsNode = settingsNodeAssocs.get(0).getChildRef();
			}
			
		}
		return deputySettingsNode;
	}

	@Override
	public NodeRef createDeputySettingsNode() {
		NodeRef settingsFolder = getDeputySettingsFolder();

		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, "deputy-settings-node");
		ChildAssociationRef settingsNode = nodeService.createNode(settingsFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(DEPUTY_NAMESPACE, "deputy-settings-assoc"), TYPE_DEPUTY_SETTINGS, properties);
		NodeRef dicNodeRef = lecmDictionaryService.getDictionaryByName(defultSubjectDictionaryName);
		if(dicNodeRef != null) {
			nodeService.createAssociation(settingsNode.getChildRef(), dicNodeRef, ASSOC_SETTINGS_DICTIONARY);
		}
		return settingsNode.getChildRef();
	}

	@Override
	public NodeRef createDeputy(NodeRef chiefNodeRef, NodeRef deputyNodeRef, List<NodeRef> subjects) {
		NodeRef deputyFolder = getDeputyFolder();
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
		NodeRef deputyNode = nodeService.createNode(deputyFolder, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_DEPUTY_NODE).getChildRef();

		nodeService.createAssociation(deputyNode, deputyNodeRef, ASSOC_DEPUTY_EMPLOYEE);

		if(subjects != null) {
			for (NodeRef subject : subjects) {
				nodeService.createAssociation(deputyNode, subject, ASSOC_DEPUTY_SUBJECT);
			}
		}

		nodeService.createAssociation(chiefNodeRef, deputyNode, ASSOC_EMPLOYEE_TO_DEPUTY);

		return deputyNode;

	}

	@Override
	public NodeRef createFullDeputy(NodeRef chiefNodeRef, NodeRef deputyNodeRef) {
		return createDeputy(chiefNodeRef, deputyNodeRef, null);
	}

	@Override
	public void removeDeputy(NodeRef deputyRef) {
		nodeService.deleteNode(deputyRef);
	}

	@Override
	public void removeFullDeputy(NodeRef chiefRef, NodeRef deputyRef) {
		List<AssociationRef> deputyAssocs = nodeService.getTargetAssocs(chiefRef, ASSOC_EMPLOYEE_TO_DEPUTY);
		for (AssociationRef deputyAssoc : deputyAssocs) {
			NodeRef deputyNodeRef = deputyAssoc.getTargetRef();
			if(Boolean.TRUE.equals(nodeService.getProperty(deputyNodeRef, PROP_DEPUTY_COMPLETE))) {
				List<AssociationRef> deputyEmployeeAssocs = nodeService.getTargetAssocs(deputyNodeRef, ASSOC_DEPUTY_EMPLOYEE);
				if(deputyEmployeeAssocs != null && !deputyEmployeeAssocs.isEmpty()) {
					if(deputyRef.equals(deputyEmployeeAssocs.get(0).getTargetRef())) {
						removeDeputy(deputyNodeRef);
					}
				}
			}
		}
	}

	@Override
	public List<NodeRef> employeesToStaff(List<NodeRef> employees) {
		List<NodeRef> result = new ArrayList<>();

		for (NodeRef employee : employees) {
			result.addAll(orgstructureService.getEmployeeStaffs(employee));
		}

		return result;
	}

	@Override
	public List<NodeRef> getPrimaryChiefs(NodeRef deputyEmployeeRef) {
		List<AssociationRef> deputyAssocs = nodeService.getSourceAssocs(deputyEmployeeRef, ASSOC_DEPUTY_EMPLOYEE);
		List<NodeRef> result = new ArrayList<>();

		for (AssociationRef deputyAssoc : deputyAssocs) {
			NodeRef deputyNode = deputyAssoc.getSourceRef();
			List<AssociationRef> chiefAssocs = nodeService.getSourceAssocs(deputyNode, ASSOC_EMPLOYEE_TO_DEPUTY);
			if(chiefAssocs != null && !chiefAssocs.isEmpty()) {
				result.add(chiefAssocs.get(0).getSourceRef());
			}
		}

		return result;
	}

	@Override
	public List<NodeRef> getAllChiefs(NodeRef deputyEmployeeRef) {
		List<AssociationRef> deputyAssocs = nodeService.getSourceAssocs(deputyEmployeeRef, ASSOC_DEPUTY_EMPLOYEE);
		List<NodeRef> result = new ArrayList<>();

		for (AssociationRef deputyAssoc : deputyAssocs) {
			NodeRef deputyNode = deputyAssoc.getSourceRef();
			List<AssociationRef> chiefAssocs = nodeService.getSourceAssocs(deputyNode, ASSOC_EMPLOYEE_TO_DEPUTY);
			if(chiefAssocs != null && !chiefAssocs.isEmpty()) {
				NodeRef chiefNodeRef = chiefAssocs.get(0).getSourceRef();
				result.add(chiefNodeRef);
				result.addAll(getAllChiefs(chiefNodeRef));
			}
		}

		return result;
	}

	@Override
	public boolean isDeputyAcceptable(NodeRef docNodeRef, NodeRef deputyNodeRef) {
		List<AssociationRef> subjects = nodeService.getTargetAssocs(deputyNodeRef, ASSOC_DEPUTY_SUBJECT);
		List<AssociationRef> docAssocs = nodeService.getTargetAssocs(docNodeRef, RegexQNamePattern.MATCH_ALL);

		if(subjects != null && !subjects.isEmpty()) {
			for (AssociationRef subject : subjects) {
				NodeRef subjectRef = subject.getTargetRef();
				for (AssociationRef docAssoc : docAssocs) {
					NodeRef docAssocRef = docAssoc.getTargetRef();
					if(lecmDictionaryService.isDictionaryValue(docAssocRef)) {
						List<NodeRef> dicChildren = lecmDictionaryService.getChildren(docAssocRef);
						if(docAssocRef.equals(subjectRef) || dicChildren.contains(subjectRef)) {
							return true;
						}
					}
				}
			}
		} else {
			// Нет ассоциаци на тематики -> заместитель полный
			return true;
		}

		return false;
	}

	@Override
	public NodeRef getDeputyEmployee(NodeRef deputyNode) {
		List<AssociationRef> employeeAssocs = nodeService.getTargetAssocs(deputyNode, ASSOC_DEPUTY_EMPLOYEE);

		if(employeeAssocs != null && !employeeAssocs.isEmpty()) {
			return employeeAssocs.get(0).getTargetRef();
		}

		return null;
	}

	@Override
	public List<NodeRef> getDeputiesByChief(NodeRef chiefNodeRef) {
		List<AssociationRef> deputiesAssocs = nodeService.getTargetAssocs(chiefNodeRef, DeputyService.ASSOC_EMPLOYEE_TO_DEPUTY);
		List<NodeRef> result = new ArrayList<>();

		for (AssociationRef deputiesAssoc : deputiesAssocs) {
			NodeRef deputyEmployee = getDeputyEmployee(deputiesAssoc.getTargetRef());
			result.add(deputyEmployee);
		}

		return result;
	}

	@Override
	public void deleteAllSubjectDeputies() {
		NodeRef rootFolder = getDeputyFolder();
		List<ChildAssociationRef> deputyNodesAssocs = nodeService.getChildAssocs(rootFolder, new HashSet<QName>(Arrays.asList(DeputyService.TYPE_DEPUTY_NODE)));
		for (ChildAssociationRef deputyNodesAssoc : deputyNodesAssocs) {
			NodeRef deputyNode = deputyNodesAssoc.getChildRef();
			List<AssociationRef> chiefAssocs = nodeService.getSourceAssocs(deputyNode, DeputyService.ASSOC_EMPLOYEE_TO_DEPUTY);
			if(chiefAssocs != null && !chiefAssocs.isEmpty()) {
				NodeRef chiefRef = chiefAssocs.get(0).getSourceRef();
				nodeService.removeAssociation(chiefRef, deputyNode, DeputyService.ASSOC_EMPLOYEE_TO_DEPUTY);
			}
		}
	}
}
