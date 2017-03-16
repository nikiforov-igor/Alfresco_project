package ru.it.lecm.orgstructure.actions;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentGlobalSettingsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: pmelnikov
 * Date: 17.01.14
 * Time: 14:01
 */
public class SharedFolderNotificationAction extends ActionExecuterAbstractBase {
    private NodeService nodeService;
    private NotificationsService notificationService;
    private PermissionService permissionService;
    private OrgstructureBean orgstructureService;
    private AuthorityService authorityService;
    private DictionaryService dictionaryService;
    private DocumentGlobalSettingsService documentGlobalSettings;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentGlobalSettings(DocumentGlobalSettingsService documentGlobalSettings) {
        this.documentGlobalSettings = documentGlobalSettings;
    }

    @Override
    protected void executeImpl(Action action, final NodeRef nodeRef) {
        //Если оповещения отключены ничего не делаем
        if (!documentGlobalSettings.isEnablePassiveNotifications()) return;

        QName type = nodeService.getType(nodeRef);
        final boolean isContent = isContent(nodeRef);
        if (dictionaryService.isSubClass(type, DocumentService.TYPE_BASE_DOCUMENT) || isContent) {
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                @Override
                public Object doWork() throws Exception {
                    NodeRef sharedFolder = getSharedFolder(nodeRef);
                    List<NodeRef> employees = getReaders(sharedFolder);
                    String template;
                    if (isContent) {
                        template = "ORGUNIT_REPOSITORY_CONTENT_ADD";
                    } else {
                        template = "ORGUNIT_REPOSITORY_DOCUMENT_ADD";
                    }
                    notificationService.sendNotificationByTemplate(nodeRef, employees, template);
                    return null;
                }
            });
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    public void setNotificationService(NotificationsService notificationService) {
        this.notificationService = notificationService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    private NodeRef getSharedFolder(NodeRef document) {
        NodeRef unitFolder = getParentUnit(document);
        if (unitFolder != null) {
            return nodeService.getChildByName(unitFolder, ContentModel.ASSOC_CONTAINS, OrgstructureBean.ORGANIZATION_UNIT_SHARED_FOLDER_NAME);
        } else {
            return null;
        }
    }

    private NodeRef getParentUnit(NodeRef document) {
        NodeRef parent = nodeService.getPrimaryParent(document).getParentRef();
        if (parent != null) {
            List<AssociationRef> units = nodeService.getSourceAssocs(parent, OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
            if (units.size() == 0) {
                return getParentUnit(parent);
            } else {
                return parent;
            }
        } else {
            return null;
        }
    }

    private List<NodeRef> getReaders(NodeRef sharedFolder) {
        Long aclID = nodeService.getNodeAclId(sharedFolder);
        Set<String> readers = permissionService.getReaders(aclID);

        Set<String> users = new HashSet<String>();
        for (String reader : readers) {
            Set<String> authorities = authorityService.getContainedAuthorities(null, reader, false);
            for (String authority : authorities) {
                AuthorityType authorityType = AuthorityType.getAuthorityType(authority);
                if (authorityType.equals(AuthorityType.USER)) {
                    users.add(authority);
                }
            }
        }

        ArrayList<NodeRef> employees = new ArrayList<NodeRef>();
        for (String user : users) {
            NodeRef employee = orgstructureService.getEmployeeByPerson(user);
            if (employee != null) {
                employees.add(employee);
            }
        }
        return employees;
    }

    private boolean isContent(NodeRef document) {
        QName type = nodeService.getType(document);
        if (type.equals(ContentModel.TYPE_CONTENT)) {
            return !isBaseDocumentAttachment(document);
        } else {
            return false;
        }
    }

    private boolean isBaseDocumentAttachment(NodeRef document) {
        NodeRef parent = nodeService.getPrimaryParent(document).getParentRef();
        if (parent != null) {
            QName type = nodeService.getType(parent);
            if (dictionaryService.isSubClass(type, DocumentService.TYPE_BASE_DOCUMENT)) {
                return true;
            } else {
                return isBaseDocumentAttachment(parent);
            }
        } else {
            return false;
        }
    }

}