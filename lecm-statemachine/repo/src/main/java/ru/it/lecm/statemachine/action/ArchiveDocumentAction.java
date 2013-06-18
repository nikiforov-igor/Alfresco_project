package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: PMelnikov
 * Date: 10.01.13
 * Time: 14:32
 */
public class ArchiveDocumentAction extends StateMachineAction {

    private String archiveFolderPath = "/Archive";
    private String archiveFolderPathAdditional = "";
    private String status = "UNKNOWN";
    private String qnameArchivePath = null;

	private static final transient Logger logger = LoggerFactory.getLogger(ArchiveDocumentAction.class);

    @Override
    public void execute(DelegateExecution execution) {
        try {
            NodeService nodeService = getServiceRegistry().getNodeService();
            NodeRef wPackage = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
            List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
            for (ChildAssociationRef document : documents) {
                String name = (String) nodeService.getProperty(document.getChildRef(), ContentModel.PROP_NAME);
                nodeService.setProperty(document.getChildRef(), QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "status"), status);
                NodeRef folder = createArchivePath(document.getChildRef());
                nodeService.moveNode(document.getChildRef(), folder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));

                Set<AccessPermission> permissions = getServiceRegistry().getPermissionService().getAllSetPermissions(document.getChildRef());
                for (AccessPermission permission : permissions) {
                    if (permission.getPosition() == 0) {
                        getServiceRegistry().getPermissionService().deletePermission(document.getChildRef(), permission.getAuthority(), permission.getPermission());
                        getServiceRegistry().getPermissionService().setPermission(document.getChildRef(), permission.getAuthority(), "LECM_BASIC_PG_Reader", true);
                    }
                }

                try {
                    String initiator = getServiceRegistry().getAuthenticationService().getCurrentUserName();
                    List<String> objects = new ArrayList<String>(1);
                    objects.add(status);
                    getBusinessJournalService().log(initiator, document.getChildRef(),
                            EventCategory.CHANGE_DOCUMENT_STATUS,
                            "#initiator перевел(а) документ \"#mainobject\" в статус \"#object1\". Регламентная работа по документу завершена.", objects);
                } catch (Exception e) {
                    logger.error("Не удалось создать запись бизнес-журнала", e);
                }

            }
        } catch (Exception e) {
            logger.error("Error while move to archive folder", e);
        }

    }

    @Override
    public void init(Element actionElement, String processId) {
        List<Element> attributes = actionElement.elements("attribute");
        for (Element attribute : attributes) {
            String name = attribute.attribute("name");
            String value = attribute.attribute("value");
            if ("archiveFolder".equalsIgnoreCase(name)) {
                archiveFolderPath = value;
            } else if ("archiveFolderAdditional".equalsIgnoreCase(name)) {
                archiveFolderPathAdditional = value;
            } else if ("status".equalsIgnoreCase(name)) {
                status = value;
            }
        }
    }

    public String getArchiveFolderPath() {
        if (qnameArchivePath == null) {
            qnameArchivePath = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<String>() {
                @Override
                public String doWork() throws Exception {
                    String result = null;
                    try {
                        NodeService nodeService = getServiceRegistry().getNodeService();
                        NodeRef folderRef = getRepositoryStructureHelper().getCompanyHomeRef();
                        StringTokenizer tokenizer = new StringTokenizer(archiveFolderPath, "/");
                        while (tokenizer.hasMoreTokens()) {
                            String folderName = tokenizer.nextToken();
                            if (!"".equals(folderName)) {
                                folderRef = nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, folderName);
                            }
                        }
                        result = nodeService.getPath(folderRef).toPrefixString(getServiceRegistry().getNamespaceService());
                    } catch (Exception e) {
                        logger.error("Archive folder \"" + archiveFolderPath + "\" removed or access denied");
                    }
                    return result;
                }
            });
        }
        return qnameArchivePath;
    }

    public String getStatusName() {
        return status;
    }

    private NodeRef createArchivePath(NodeRef node) {
        //Проверяем структуру
        Pattern pattern = Pattern.compile("\\{(.*?):(.*?)\\}");
        String rootFolder = archiveFolderPath;

        String path = archiveFolderPathAdditional;
        Matcher matcher = pattern.matcher(path);
        while (matcher.find()) {
            String prefix = matcher.group(1);
            String attributeName = matcher.group(2);
            QName attribute = QName.createQName(prefix, attributeName, getServiceRegistry().getNamespaceService());
            String value = getServiceRegistry().getNodeService().getProperty(node, attribute).toString();
            path = path.replace("{" + prefix + ":" + attributeName + "}", value);
        }

        NodeService nodeService = getServiceRegistry().getNodeService();
        NodeRef archiveFolder = getRepositoryStructureHelper().getCompanyHomeRef();
        //Создаем основной путь до папки
        boolean isCreated = false;
        try {
            StringTokenizer tokenizer = new StringTokenizer(rootFolder, "/");
            while (tokenizer.hasMoreTokens()) {
                String folderName = tokenizer.nextToken();
                if (!"".equals(folderName)) {
                    NodeRef folder = nodeService.getChildByName(archiveFolder, ContentModel.ASSOC_CONTAINS, folderName);
                    if (folder == null) {
                        folder = createFolder(archiveFolder, folderName);
                        isCreated = true;
                    }
                    archiveFolder = folder;
                }
            }
        } catch (Exception e) {
            logger.error("Error while create archive folder", e);  //To change body of catch statement use File | Settings | File Templates.
        }

        //Если была создана новая архивная папка сбрасываем ей права доступа и добавляем системные
        if (isCreated && !archiveFolderPath.equals("/")) {
            getServiceRegistry().getPermissionService().setInheritParentPermissions(archiveFolder, false);
            Set<AccessPermission> permissions = getServiceRegistry().getPermissionService().getAllSetPermissions(archiveFolder);
            for (AccessPermission permission : permissions) {
                getServiceRegistry().getPermissionService().deletePermission(archiveFolder, permission.getAuthority(), permission.getPermission());
            }
            getServiceRegistry().getPermissionService().setPermission(archiveFolder, AuthenticationUtil.SYSTEM_USER_NAME, PermissionService.READ, true);
        }
        try {
            StringTokenizer tokenizer = new StringTokenizer(path, "/");
            while (tokenizer.hasMoreTokens()) {
                String folderName = tokenizer.nextToken();
                if (!"".equals(folderName)) {
                    NodeRef folder = nodeService.getChildByName(archiveFolder, ContentModel.ASSOC_CONTAINS, folderName);
                    if (folder == null) {
                        folder = createFolder(archiveFolder, folderName);
                    }
                    archiveFolder = folder;
                }
            }
        } catch (Exception e) {
            logger.error("Error while create archive folder", e);
        }
        return archiveFolder;
    }

}
