package ru.it.lecm.statemachine.editor;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.SimpleDocumentRegistry;
import ru.it.lecm.statemachine.SimpleDocumentRegistryItem;

import java.util.*;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import ru.it.lecm.base.beans.LecmTransactionHelper;

/**
 * Created by pmelnikov on 14.04.2015.
 *
 * Объект для разворачивания реестра типов документов без жизненного цикла при старте приложении, либо при разворацивании в редакторе МС
 */
public class SimpleDocumentDeployer extends AbstractLifecycleBean {

    private NodeService nodeService;
    private SimpleDocumentRegistry simpleDocumentRegistry;
    private NamespaceService namespaceService;
    private RepositoryStructureHelper repositoryStructureHelper;
	private LecmTransactionHelper lecmTransactionHelper;

    private static final transient Logger logger = LoggerFactory.getLogger(SimpleDocumentDeployer.class);

	public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}
	
    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSimpleDocumentRegistry(SimpleDocumentRegistry simpleDocumentRegistry) {
        this.simpleDocumentRegistry = simpleDocumentRegistry;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void init() {
//        NodeRef home = repositoryStructureHelper.getHomeRef();
//        if (home != null) {
//            NodeRef statemachinesRef = nodeService.getChildByName(home, ContentModel.ASSOC_CONTAINS, "statemachines");
//            if (statemachinesRef != null) {
//                Set<QName> childTypes = new HashSet<>();
//                childTypes.add(StatemachineEditorModel.TYPE_STATEMACHINE);
//                List<ChildAssociationRef> statemachines = nodeService.getChildAssocs(statemachinesRef, childTypes);
//                for (ChildAssociationRef statemachine : statemachines) {
//                    Boolean isSimpleDocument = false;
//                    Object isSimpleDocumentObj = nodeService.getProperty(statemachine.getChildRef(), StatemachineEditorModel.PROP_SIMPLE_DOCUMENT);
//                    if (isSimpleDocumentObj != null) {
//                        isSimpleDocument = (Boolean) isSimpleDocumentObj;
//                    }
//                    if (isSimpleDocument) {
//                        try {
//                            appendType(statemachine.getChildRef());
//                        } catch (LecmBaseException e) {
//                            String name = nodeService.getProperty(statemachine.getChildRef(), ContentModel.PROP_NAME).toString();
//                            logger.error("Error while bootstrap statemachine for simple document " + name, e);
//                        }
//                    }
//                }
//            }
//        }
    }

    public void appendType(NodeRef statemachine) throws LecmBaseException {
        //Корень
        Object storePathObj = nodeService.getProperty(statemachine, StatemachineEditorModel.PROP_ARCHIVE_FOLDER);
        String storePath = "Архив";
        if (storePathObj != null) {
            storePath = storePathObj.toString();
        }

        Object isNotArmCreatedObj = nodeService.getProperty(statemachine, StatemachineEditorModel.PROP_NOT_ARM_CREATED);
        Boolean isNotArmCreated = false;
        if (isNotArmCreatedObj != null) {
            isNotArmCreated = (Boolean) isNotArmCreatedObj;
        }

        NodeRef rolesRef = nodeService.getChildByName(statemachine, ContentModel.ASSOC_CONTAINS, "roles-list");
        List<String> starters = new ArrayList<>();
        Map<String, String> permissions = new HashMap<>();
        if (rolesRef != null) {
            List<ChildAssociationRef> roles = nodeService.getChildAssocs(rolesRef);
            if (!roles.isEmpty()) {
                for (ChildAssociationRef permission : roles) {
                    AssociationRef role = nodeService.getTargetAssocs(permission.getChildRef(), StatemachineEditorModel.ASSOC_ROLE).get(0);
                    String roleName = (String) nodeService.getProperty(role.getTargetRef(), OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
                    String permissionTypeValue = (String) nodeService.getProperty(permission.getChildRef(), StatemachineEditorModel.PROP_STATIC_ROLE_PRIVILEGE);
                    permissions.put(roleName, permissionTypeValue);
                    Object value = nodeService.getProperty(permission.getChildRef(), StatemachineEditorModel.PROP_CREATION_DOCUMENT);
                    boolean starter =  value == null ? false : (Boolean) value;
                    if (starter) {
                        starters.add(roleName);
                    }
                }

            }
        }

        SimpleDocumentRegistryItem item = new SimpleDocumentRegistryItem(storePath);
        item.setNotArmCreated(isNotArmCreated);
        item.setStarters(starters);
        item.setPermissions(permissions);
        String typeName = nodeService.getProperty(statemachine, ContentModel.PROP_NAME).toString();
        simpleDocumentRegistry.registerDocument(typeName.replace("_",":"), item);
    }

	@Override
	protected void onBootstrap(ApplicationEvent ae) {
		lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				NodeRef home = repositoryStructureHelper.getHomeRef();
				if (home != null) {
					NodeRef statemachinesRef = nodeService.getChildByName(home, ContentModel.ASSOC_CONTAINS, "statemachines");
					if (statemachinesRef != null) {
						Set<QName> childTypes = new HashSet<>();
						childTypes.add(StatemachineEditorModel.TYPE_STATEMACHINE);
						List<ChildAssociationRef> statemachines = nodeService.getChildAssocs(statemachinesRef, childTypes);
						for (ChildAssociationRef statemachine : statemachines) {
							Boolean isSimpleDocument = false;
							Object isSimpleDocumentObj = nodeService.getProperty(statemachine.getChildRef(), StatemachineEditorModel.PROP_SIMPLE_DOCUMENT);
							if (isSimpleDocumentObj != null) {
								isSimpleDocument = (Boolean) isSimpleDocumentObj;
							}
							if (isSimpleDocument) {
								try {
									appendType(statemachine.getChildRef());
								} catch (LecmBaseException e) {
									String name = nodeService.getProperty(statemachine.getChildRef(), ContentModel.PROP_NAME).toString();
									logger.error("Error while bootstrap statemachine for simple document " + name, e);
								}
							}
						}
					}
				}
				return null;
			}
		});        
	}

	@Override
	protected void onShutdown(ApplicationEvent ae) {
	}
}
