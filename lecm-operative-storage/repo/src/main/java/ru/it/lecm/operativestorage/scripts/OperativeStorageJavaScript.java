/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.operativestorage.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.removal.DocumentsRemovalService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.operativestorage.beans.OperativeStorageService;
import static ru.it.lecm.operativestorage.beans.OperativeStorageService.ASSOC_NOMENCLATURE_YEAR_SECTION_TO_ORGANIZATION;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

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
	private PermissionService permissionService;
	private RepositoryStructureHelper repositoryStructureHelper;

	public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
		this.repositoryStructureHelper = repositoryStructureHelper;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

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

	public void moveToNomenclatureCase(final ScriptNode doc, Scriptable units) {
		moveToNomenclatureCase(doc);
		List<NodeRef> unitsList = getNodeRefsFromScriptableCollection(units);

		List<AssociationRef> assocList = nodeService.getTargetAssocs(doc.getNodeRef(), EDSDocumentService.ASSOC_FILE_REGISTER);
		NodeRef caseRef;

		if(assocList!= null && !assocList.isEmpty()) {
			caseRef = assocList.get(0).getTargetRef();
			for (NodeRef unit : unitsList) {
				nodeService.createAssociation(caseRef, unit, OperativeStorageService.ASSOC_NOMENCLATURE_CASE_VISIBILITY_UNIT);
			}
		}


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

	public Scriptable getIgnoredYearsString() {
		boolean isCentralized = (boolean) nodeService.getProperty(operativeStorageService.getSettings(), OperativeStorageService.PROP_OPERATIVE_STORAGE_CENRALIZED);
		List<NodeRef> includedYears = new ArrayList<>();
		List<NodeRef> ignoredYears = new ArrayList<>();

		if(!isCentralized) {
			includedYears = operativeStorageService.getOrganizationsYearSections(null);
		}

		List<ChildAssociationRef> yearSectionsChildAssocs = nodeService.getChildAssocs(operativeStorageService.getNomenclatureFolder(), ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);

		for (ChildAssociationRef child : yearSectionsChildAssocs) {
			NodeRef potentialIgnoredYear = child.getChildRef();
			String potentialIgnoredYearStatus = (String) nodeService.getProperty(potentialIgnoredYear, OperativeStorageService.PROP_NOMENCLATURE_YEAR_SECTION_STATUS);
			if((!isCentralized && !includedYears.contains(potentialIgnoredYear)) || !potentialIgnoredYearStatus.equals("APPROVED")) {
				ignoredYears.add(potentialIgnoredYear);
			}
		}

		return createScriptable(ignoredYears);
	}

	public boolean isYearUniq(String year, String orgNodeRef) {
		boolean isCentralized = (boolean) nodeService.getProperty(operativeStorageService.getSettings(), OperativeStorageService.PROP_OPERATIVE_STORAGE_CENRALIZED);
		List<Integer> yearsList = new ArrayList<>();

		if(!isCentralized) {
			List<NodeRef> yearNodeRefList = operativeStorageService.getOrganizationsYearSections(new NodeRef(orgNodeRef));
			for (NodeRef yearNodeRef : yearNodeRefList) {
				yearsList.add((Integer) nodeService.getProperty(yearNodeRef, OperativeStorageService.PROP_NOMENCLATURE_YEAR_SECTION_YEAR));
			}
		} else {
			List<ChildAssociationRef> assocs = nodeService.getChildAssocs(operativeStorageService.getNomenclatureFolder(), ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
			for (ChildAssociationRef assoc : assocs) {
				yearsList.add((Integer) nodeService.getProperty(assoc.getChildRef(), OperativeStorageService.PROP_NOMENCLATURE_YEAR_SECTION_YEAR));
			}
		}

		return !yearsList.contains(Integer.parseInt(year));

	}

	public void createSectionByUnit(String yearRef) {
		NodeRef yearNodeRef = new NodeRef(yearRef);
		NodeRef organizationRef = nodeService.getTargetAssocs(yearNodeRef, ASSOC_NOMENCLATURE_YEAR_SECTION_TO_ORGANIZATION).get(0).getTargetRef();
		List<ChildAssociationRef> children = nodeService.getChildAssocs(organizationRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if(children != null) {
			for (ChildAssociationRef child : children) {
				if(OrgstructureBean.TYPE_ORGANIZATION_UNIT.equals(nodeService.getType(child.getChildRef()))) {
					operativeStorageService.createSectionByUnit(child.getChildRef(), yearNodeRef, true);
				}
			}
		}
	}

	public void moveNode(final ScriptNode document, final String path) {
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                StringTokenizer tokenizer = new StringTokenizer(path, "/");
                NodeRef nodeRef = repositoryStructureHelper.getCompanyHomeRef();
                while (tokenizer.hasMoreTokens()) {
                    String folder = tokenizer.nextToken();
                    if (!"".equals(folder)) {
                        NodeRef currentFolder = repositoryStructureHelper.getFolder(nodeRef, folder);
                        if (currentFolder == null) {
                            currentFolder = repositoryStructureHelper.createFolder(nodeRef, folder);
                        }
                        nodeRef = currentFolder;
                    }
                }
                ChildAssociationRef parent = nodeService.getPrimaryParent(document.getNodeRef());
                nodeService.moveNode(document.getNodeRef(), nodeRef, ContentModel.ASSOC_CONTAINS, parent.getQName());
                return null;
            }
        });
    }

    public void setPermission(final ScriptNode document, final String permission, final String authority) {
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                permissionService.setPermission(document.getNodeRef(), authority, permission, true);
                return null;
            }
        });
    }

}
