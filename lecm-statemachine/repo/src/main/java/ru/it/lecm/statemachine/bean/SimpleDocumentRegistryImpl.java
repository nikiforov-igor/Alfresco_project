package ru.it.lecm.statemachine.bean;

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;
import ru.it.lecm.statemachine.SimpleDocumentRegistry;
import ru.it.lecm.statemachine.SimpleDocumentRegistryItem;

import java.util.*;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 * Created by pmelnikov on 09.04.2015.
 */
public class SimpleDocumentRegistryImpl extends BaseBean implements SimpleDocumentRegistry {

    private Map<String, SimpleDocumentRegistryItem> types = new HashMap<>();
    private Repository repositoryHelper;
    private LecmPermissionService lecmPermissionService;
    private PermissionService permissionService;
    private NamespaceService namespaceService;
	private StateMachineServiceBean stateMachineService;

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public void registerDocument(String type, final SimpleDocumentRegistryItem item) throws LecmBaseException {
        String pathStr = "Документы без МС";
        String[] storePath = item.getStorePath().split("/");
        for (String pathItem : storePath) {
            if (!"".equals(pathItem)) {
                pathStr = pathItem;
                break;
            }
        }
        final String path = pathStr;
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
//                lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
//
//                    @Override
//                    public Void execute() throws Throwable {
                        NodeRef typeRoot = getFolder(repositoryHelper.getCompanyHome(), path);
                        if (typeRoot == null) {
                            List<String> paths = new ArrayList<>();
                            paths.add(path);
                            typeRoot = createPath(repositoryHelper.getCompanyHome(), paths);
                        }
                        Map<String, String> permissions = item.getPermissions();
                        Map<String, LecmPermissionService.LecmPermissionGroup> permissionGroups = new HashMap<>();
                        for (Map.Entry<String, String> role : permissions.entrySet()) {
                            LecmPermissionService.LecmPermissionGroup permissionGroup = lecmPermissionService.findPermissionGroup(role.getValue());
                            if (permissionGroup != null) {
                                permissionGroups.put(role.getKey(), permissionGroup);
                            }
                        }
                        //permissionService.clearPermission(typeRoot, PermissionService.ALL_AUTHORITIES);
                        Set<AccessPermission> allowedPermissions = permissionService.getAllSetPermissions(typeRoot);
                        for (AccessPermission permission : allowedPermissions) {
                            if (permission.isSetDirectly() && permission.getAuthority().startsWith(PermissionService.GROUP_PREFIX + Types.PFX_LECM)) {
                                permissionService.deletePermission(typeRoot, permission.getAuthority(), permission.getPermission());
                            }
                        }


                        if (!permissionGroups.isEmpty()) {
                            lecmPermissionService.rebuildStaticACL(typeRoot, permissionGroups);
                        }
                        item.setTypeRoot(typeRoot);
//                        return null;
//                    }
//                });
                return null;
            }
        });
        types.put(type, item);
    }

    @Override
    public boolean isSimpleDocument(QName type) {
//        return types.containsKey(type.toPrefixString(namespaceService));
		return stateMachineService.isSimpleDocument(type.toPrefixString(namespaceService));
    }

    public SimpleDocumentRegistryItem getRegistryItem(QName type) {
        return types.get(type.toPrefixString(namespaceService));
    }
	
	private void rebuildACL(String type, NodeRef typeRoot) {
		Map<String, String> permissions = stateMachineService.getPermissions(type);
		Map<String, LecmPermissionService.LecmPermissionGroup> permissionGroups = new HashMap<>();
		for (Map.Entry<String, String> role : permissions.entrySet()) {
			LecmPermissionService.LecmPermissionGroup permissionGroup = lecmPermissionService.findPermissionGroup(role.getValue());
			if (permissionGroup != null) {
				permissionGroups.put(role.getKey(), permissionGroup);
			}
		}
		
		Set<AccessPermission> allowedPermissions = permissionService.getAllSetPermissions(typeRoot);
		for (AccessPermission permission : allowedPermissions) {
			if (permission.isSetDirectly() && permission.getAuthority().startsWith(PermissionService.GROUP_PREFIX + Types.PFX_LECM)) {
				permissionService.deletePermission(typeRoot, permission.getAuthority(), permission.getPermission());
			}
		}

		if (!permissionGroups.isEmpty()) {
			lecmPermissionService.rebuildStaticACL(typeRoot, permissionGroups);
		}
	}

	@Override
	public void checkTypeFolder(String type, boolean forceRebuildACL) throws WriteTransactionNeededException {
		String pathStr = "Документы без МС";
		String archiveFolder = stateMachineService.getArchiveFolder(type);
		
		if (archiveFolder != null && !archiveFolder.isEmpty()) {
			String[] storePath = archiveFolder.split("/");
			for (String pathItem : storePath) {
				if (!"".equals(pathItem)) {
					pathStr = pathItem;
					break;
				}
			}
		}
		
		NodeRef typeRoot = getFolder(repositoryHelper.getCompanyHome(), pathStr);
		if (typeRoot == null) {
			List<String> paths = new ArrayList<>();
			paths.add(pathStr);
			typeRoot = createPath(repositoryHelper.getCompanyHome(), paths);
			
			rebuildACL(type, typeRoot);
		}
		
		if (forceRebuildACL) {
			rebuildACL(type, typeRoot);
		}
		
	}
	
}
