/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.scripts;

import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.removal.DocumentsRemovalService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;

/**
 *
 * @author ikhalikov
 */
public class OperativeStorageJavaScript extends BaseWebScript{

	private final static Logger logger = LoggerFactory.getLogger(OperativeStorageJavaScript.class);
	private OperativeStorageService operativeStorageService;
	private BehaviourFilter behaviourFilter;
    private DocumentsRemovalService documentRemovalService;
    private NodeService nodeService;

    public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	public void setOperativeStorageService(OperativeStorageService operativeStorageService) {
		this.operativeStorageService = operativeStorageService;
	}

	private NodeRef checkForDocumentsFolder(final NodeRef caseRef) {
		//Проверка на существование папки "Документы" у НД
		//На момент написания, все методы используются только в машине состояний
		//Если будет использоваться в обычных веб-скриптах, то
		//создание папки "Документы" необходимо оборачивать в транзакцию
		NodeRef docFolder = operativeStorageService.getDocuemntsFolder(caseRef);
		if(docFolder == null) {
			docFolder = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return operativeStorageService.createDocsFolder(caseRef);
				}
			});
		}

		return docFolder;
	}

	public ScriptNode getOperativeStorageFolder() {
		return new ScriptNode(operativeStorageService.getOperativeStorageFolder(), serviceRegistry, getScope());
	}

	public ScriptNode getNomenclatureFolder() {
		NodeRef nomenclatureFolder = operativeStorageService.getNomenclatureFolder();
		return nomenclatureFolder == null ? null : new ScriptNode(nomenclatureFolder, serviceRegistry, getScope());
	}

        public void updateNomenclatureCasePermissions(String nodeRefStr) {
            NodeRef nodeRef = new NodeRef(nodeRefStr);
            operativeStorageService.updatePermissions(nodeRef);
        }

	public ScriptNode getYearSection(ScriptNode node) {
		return new ScriptNode(operativeStorageService.getYearSection(node.getNodeRef()), serviceRegistry, getScope());
	}

	public void cleanVisibilityList(String nodeRefStr) {
		NodeRef nodeRef = new NodeRef(nodeRefStr);
		operativeStorageService.cleanVisibilityList(nodeRef);
	}

	public void grantAll(String nodeRefStr) {
		NodeRef nodeRef = new NodeRef(nodeRefStr);
		operativeStorageService.grantAll(nodeRef);
	}

	public void revokeAll(String nodeRefStr) {
		NodeRef nodeRef = new NodeRef(nodeRefStr);
		operativeStorageService.revokeAll(nodeRef);
	}

	public void grantAccessToAllArchivhists(String nodeRefStr) {
		NodeRef nodeRef = new NodeRef(nodeRefStr);
		operativeStorageService.grantPermissionsToAllArchivists(nodeRef);
	}

	public void moveToNomenclatureCase(final String docNodeRefStr, final String caseNodeRefStr) {

		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {

				NodeRef docNodeRef = new NodeRef(docNodeRefStr);
				NodeRef caseNodeRef = new NodeRef(caseNodeRefStr);

				//Проверка на существование папки "Документы" у НД
				if(operativeStorageService.getDocuemntsFolder(caseNodeRef) == null) {
					operativeStorageService.createDocsFolder(caseNodeRef);
				}

				behaviourFilter.disableBehaviour(DocumentService.TYPE_BASE_DOCUMENT);
				operativeStorageService.moveDocToNomenclatureCase(docNodeRef, caseNodeRef);
				behaviourFilter.enableBehaviour(DocumentService.TYPE_BASE_DOCUMENT);

				return null;
			}
		});

	}

	public void moveToNomenclatureCase(final ScriptNode doc) {
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {

				NodeRef docNodeRef = doc.getNodeRef();

				List<AssociationRef> assocList = nodeService.getTargetAssocs(docNodeRef, EDSDocumentService.ASSOC_FILE_REGISTER);
				if(assocList != null && !assocList.isEmpty()) {
					NodeRef caseRef = assocList.get(0).getTargetRef();

					//Проверка на существование папки "Документы" у НД
					if(operativeStorageService.getDocuemntsFolder(caseRef) == null) {
						operativeStorageService.createDocsFolder(caseRef);
					}

					behaviourFilter.disableBehaviour(DocumentService.TYPE_BASE_DOCUMENT);
					operativeStorageService.moveDocToNomenclatureCase(docNodeRef, caseRef);
					behaviourFilter.enableBehaviour(DocumentService.TYPE_BASE_DOCUMENT);
				}

				return null;
			}
		});
	}

    /**
     * Возвращает ссылку на справки для номенклатуры дел, если папки нет, создает ее
     * @param nodeRefStr
     * @return
     */
    public ScriptNode getReferencesFolder(String nodeRefStr) {
        NodeRef nodeRef = new NodeRef(nodeRefStr);
        NodeRef result = operativeStorageService.getReferencesFolder(nodeRef);
        if (result == null) {
            result = operativeStorageService.createReferencesFolder(nodeRef);
        }
        return new ScriptNode(result, serviceRegistry, getScope());
    }

    public ScriptNode getReferenceTemplate(String nodeRefStr) {
        NodeRef nodeRef = new NodeRef(nodeRefStr);
        return new ScriptNode(operativeStorageService.getReferenceTemplate(nodeRef), serviceRegistry, getScope());
    }

//    public void purge(final ScriptNode node) {
//        NodeRef nodeRef = node.getNodeRef();
//        QName qname = nodeService.getType(nodeRef);
//        if (qname.equals(FMSModels.TYPE_TEMPORARY_DOCUMENT)) {
//            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
//                @Override
//                public Void doWork() throws Exception {
//                    documentRemovalService.purge(node.getNodeRef());
//                    return null;
//                }
//            });
//        }
//    }

    public void setDocumentRemovalService(DocumentsRemovalService documentRemovalService) {
        this.documentRemovalService = documentRemovalService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

	public ScriptNode getSettings() {
		return new ScriptNode(operativeStorageService.getSettings(), serviceRegistry, getScope());
	}

	public boolean orgUnitAssociationExists(String nodeRefStr, String orgUnitRef) {
		return operativeStorageService.checkNDSectionAssociationExists(new NodeRef(orgUnitRef), new NodeRef(nodeRefStr));
	}

}
