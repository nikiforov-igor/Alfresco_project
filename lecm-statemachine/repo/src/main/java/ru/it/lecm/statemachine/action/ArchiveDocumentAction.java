package ru.it.lecm.statemachine.action;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.Expression;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;
import ru.it.lecm.statemachine.OrgUnitDynamicAuthority;
import ru.it.lecm.statemachine.StatemachineModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.activiti.engine.impl.util.xml.Element;

/**
 * User: PMelnikov Date: 10.01.13 Time: 14:32
 */
public class ArchiveDocumentAction extends StateMachineAction implements ExecutionListener {

    private final static String legacyAdditionalPathRegex = "\\{([\\w-]*?):([\\w-]*?)\\}";
    private final static Pattern legacyAdditionalPathPattern = Pattern.compile(legacyAdditionalPathRegex);
    private String archiveFolderPath = "/Archive";
    private String archiveFolderPathAdditional = "";
    private String status = "UNKNOWN";
    private String qnameArchivePath = null;
    protected Expression archiveFolder;
    protected Expression archiveFolderAdditional;

    private String DEFAULT_ARCHIVE_PATH = "/Архив";

    public void setArchiveFolder(Expression archiveFolder) {
        this.archiveFolder = archiveFolder;
    }

    public void setArchiveFolderAdditional(Expression archiveFolderAdditional) {
        this.archiveFolderAdditional = archiveFolderAdditional;
    }

    private static final transient Logger logger = LoggerFactory.getLogger(ArchiveDocumentAction.class);

    @Override
    public void notify(DelegateExecution execution) {
        status = execution.getCurrentActivityName();
        if (archiveFolder != null && archiveFolder.getValue(execution) != null)
            archiveFolderPath = archiveFolder.getExpressionText();
        if (archiveFolderAdditional != null && archiveFolderAdditional.getValue(execution) != null)
            archiveFolderPathAdditional = archiveFolderAdditional.getExpressionText();

        final NodeService nodeService = getServiceRegistry().getNodeService();

        final NodeRef document = ((ActivitiScriptNode) execution.getVariable("stm_document")).getNodeRef();
        final String name = (String) nodeService.getProperty(document, ContentModel.PROP_NAME);

        nodeService.setProperty(document, StatemachineModel.PROP_STATUS, status);

        //Проверяем наличие аспекта указывающего на финальный статус
        if (!nodeService.hasAspect(document, StatemachineModel.ASPECT_IS_FINAL)) {
            nodeService.addAspect(document, StatemachineModel.ASPECT_IS_FINAL, null);
        }
        //Устанавливаем флаг финального статуса
        nodeService.setProperty(document, StatemachineModel.PROP_IS_FINAL, true);

        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
                try {
                    //проверяем, нужно ли перемещать документ
                    if (!nodeService.hasAspect(document, DocumentService.ASPECT_DONT_MOVE_TO_ARCHIVE_FOLDER)) {
                        if (nodeService.hasAspect(document, DocumentService.ASPECT_FINALIZE_TO_UNIT)) {
                            List<AssociationRef> units = nodeService.getTargetAssocs(document, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC);
                            if (units.isEmpty()) {
                                throw new InvalidNodeRefException("У документа установлен аспект ASSOC_ORGANIZATION_UNIT_ASSOC, но отсутствуют подразделения", document);
                            }
                            NodeRef unit = units.get(0).getTargetRef();//TODO избавиться от get(0)
                            boolean isSharedFolder = Boolean.TRUE.equals(nodeService.getProperty(document, DocumentService.PROP_IS_SHARED_FOLDER));
                            List<AssociationRef> folders = nodeService.getTargetAssocs(unit, OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
                            ///////////////////////
                            NodeRef folder = null;
                            if (isSharedFolder) {
                                if (folders.size() > 0) {//TODO нет прав
                                    folder = nodeService.getChildByName(folders.get(0).getTargetRef(), ContentModel.ASSOC_CONTAINS, OrgstructureBean.ORGANIZATION_UNIT_SHARED_FOLDER_NAME);
                                }
                            } else {
                                if (folders.size() > 0) {//TODO нет прав
                                    folder = nodeService.getChildByName(folders.get(0).getTargetRef(), ContentModel.ASSOC_CONTAINS, OrgstructureBean.ORGANIZATION_UNIT_PRIVATE_FOLDER_NAME);
                                }
                            }
                            if (folder != null) {
                                folder = createAdditionalPath(document, folder);
                                nodeService.moveNode(document, folder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));
                            }
                            ////////////////////////
                            List<AssociationRef> additionalUnits = nodeService.getTargetAssocs(document, DocumentService.ASSOC_ADDITIONAL_ORGANIZATION_UNIT_ASSOC);
                            for (AssociationRef additionalUnit : additionalUnits) {
                                if (!additionalUnit.getTargetRef().equals(unit)) {
                                    List<AssociationRef> additionalFolders = nodeService.getTargetAssocs(additionalUnit.getTargetRef(), OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
                                    /////////////////////
                                    NodeRef additionalFolder = null;
                                    if (isSharedFolder) {
                                        if (additionalFolders.size() > 0) {
                                            additionalFolder = nodeService.getChildByName(additionalFolders.get(0).getTargetRef(), ContentModel.ASSOC_CONTAINS, OrgstructureBean.ORGANIZATION_UNIT_SHARED_FOLDER_NAME);
                                        }
                                    } else {
                                        if (additionalFolders.size() > 0) {
                                            additionalFolder = nodeService.getChildByName(additionalFolders.get(0).getTargetRef(), ContentModel.ASSOC_CONTAINS, OrgstructureBean.ORGANIZATION_UNIT_PRIVATE_FOLDER_NAME);
                                        }
                                    }
                                    if (additionalFolder != null) {
                                        additionalFolder = createAdditionalPath(document, additionalFolder);
                                        try {
                                            nodeService.addChild(additionalFolder, document, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(additionalFolder.getId())));
                                            String authority = getOrgstructureBean().getOrgstructureUnitAuthority(additionalUnit.getTargetRef(), isSharedFolder);
                                            getServiceRegistry().getPermissionService().setPermission(document, authority, "LECM_BASIC_PG_Reader", true);
                                        } catch (Exception e) {
                                            //TODO что за фигня System.out.println();
                                        }
                                    }
                                    /////////////////////
                                }
                            }
                        } else {
                            /////////////////////////////
                            NodeRef folder = createArchivePath(document);
                            nodeService.moveNode(document, folder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));
                            /////////////////////////////
                        }
                    }

                    //удаляем динамические пермиссии
                    //исключение: пермиссии, назначенные непосредственно сотруднику (с префиксом GROUP__LECM$SPEC%)
                    //            пермиссии участников также сохраняются (т.к. они выданы непосредственно сотруднику)
                    Set<AccessPermission> permissions = getServiceRegistry().getPermissionService().getAllSetPermissions(document);
                    for (AccessPermission permission : permissions) {
                        if (permission.isSetDirectly()
                                && !permission.getAuthority().startsWith(PermissionService.GROUP_PREFIX + Types.PFX_LECM + Types.SGKind.SG_SPEC.getSuffix())
                                && !permission.getAuthority().equals(OrgUnitDynamicAuthority.ORGUNIT_AUTHORITY)
                                && !permission.getAuthority().startsWith(LecmPermissionService.DYNAMIC_AUTH_PREFIX)) {
                            getServiceRegistry().getPermissionService().deletePermission(document, permission.getAuthority(), permission.getPermission());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while action execution", e);
                }
                return null;
            }
        });

        //Сбрасываем документ для скриптов
        ((ActivitiScriptNode) execution.getVariable("stm_document")).reset();

        try {
            String initiator = getServiceRegistry().getAuthenticationService().getCurrentUserName();
            List<String> objects = new ArrayList<String>(1);
            objects.add(status);
            getBusinessJournalService().log(initiator, document,
                    EventCategory.CHANGE_DOCUMENT_STATUS,
                    "#initiator перевел(а) документ \"#mainobject\" в статус \"#object1\". Регламентная работа по документу завершена.", objects);
        } catch (Exception e) {
            logger.error("Не удалось создать запись бизнес-журнала", e);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws InvalidNodeRefException {

    }

    @Override
    public void init(BaseElement actionElement, String processId) {

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
                        logger.warn("Archive folder \"" + archiveFolderPath + "\" removed or access denied");
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
        String rootFolder = getDocumentService().execStringExpression(node, archiveFolderPath);
        if (rootFolder.isEmpty()) {
            rootFolder = DEFAULT_ARCHIVE_PATH;
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
            getServiceRegistry().getPermissionService().setPermission(archiveFolder, AuthenticationUtil.SYSTEM_USER_NAME, "LECM_BASIC_PG_Reader", true);
        }
        return archiveFolder;
    }

    private NodeRef createAdditionalPath(NodeRef node, NodeRef rootPath) {
        String path = "";
        if (archiveFolderPathAdditional.matches(legacyAdditionalPathRegex)) {
            // fallback to legacy mechanism
            path = archiveFolderPathAdditional;
            Matcher matcher = legacyAdditionalPathPattern.matcher(path);
            while (matcher.find()) {
                String prefix = matcher.group(1);
                String attributeName = matcher.group(2);
                QName attribute = QName.createQName(prefix, attributeName, getServiceRegistry().getNamespaceService());
                String value = getServiceRegistry().getNodeService().getProperty(node, attribute).toString();
                path = path.replace("{" + prefix + ":" + attributeName + "}", value);
            }
        } else if(!archiveFolderPathAdditional.isEmpty()) {
            DocumentService documentService = getDocumentService();
            path = documentService.execStringExpression(node, archiveFolderPathAdditional);
        }

        try {
            StringTokenizer tokenizer = new StringTokenizer(path, "/");
            NodeService nodeService = getServiceRegistry().getNodeService();
            while (tokenizer.hasMoreTokens()) {
                String folderName = tokenizer.nextToken();
                if (!"".equals(folderName)) {
                    NodeRef folder = nodeService.getChildByName(rootPath, ContentModel.ASSOC_CONTAINS, folderName);
                    if (folder == null) {
                        folder = createFolder(rootPath, folderName);
                    }
                    rootPath = folder;
                }
            }
        } catch (Exception e) {
            logger.error("Error while create archive folder", e);
        }
        return rootPath;
    }

}
