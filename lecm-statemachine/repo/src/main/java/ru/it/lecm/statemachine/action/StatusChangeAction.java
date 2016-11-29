package ru.it.lecm.statemachine.action;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.Expression;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.statemachine.StateField;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

//import org.activiti.engine.impl.util.xml.Element;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:53
 * <p/>
 * Действие машины состояний отвечающее за смену статуса у документа.
 */
public class StatusChangeAction extends StateMachineAction implements TaskListener {

    private String status = "UNKNOWN";
    private String processId = null;
    private String version = null;
    private boolean forDraft = false;
    private NodeRef statusFolder = null;
    private Map<String, LecmPermissionGroup> staticPrivileges = new HashMap<String, LecmPermissionGroup>();
    private Map<String, LecmPermissionGroup> dynamicPrivileges = new HashMap<String, LecmPermissionGroup>();
    private Set<StateField> fields = new HashSet<StateField>();
    private Set<StateField> categories = new HashSet<StateField>();
    private final static Object lock = new Object();

    private final static SimpleDateFormat statusDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private static final transient Logger logger = LoggerFactory.getLogger(StatusChangeAction.class);

    protected Expression draft;

    public void setDraft(Expression draft) {
        this.draft = draft;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        String definition = delegateTask.getProcessDefinitionId();

        forDraft = Boolean.parseBoolean(draft.getExpressionText());
        status = delegateTask.getName();
        version = definition.substring(definition.indexOf(":") + 1, definition.lastIndexOf(":"));
        processId = definition.substring(0, definition.indexOf(":"));

        logger.debug("!!!!!!!!!!!! delegateTask id: " + delegateTask.getId() + " ,name: " + delegateTask.getName() + " ,"
                + " processDefinitionId: " + delegateTask.getProcessDefinitionId() + " ,"
                + " processInstanceId: " + delegateTask.getProcessInstanceId() + "");
        //TODO List<String> vars = com.google.common.collect.Lists.newArrayList(getStateMachineHelper().getStateMecheneByName(processId).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(status).getStatusVars());
        List<String> vars = getStateMachineHelper().getStateMecheneByName(processId).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(status).getStatusVars();
        for (String var : vars) {
            delegateTask.getExecution().setVariable(var, "");
        }

        final NodeRef stm_document = ((ActivitiScriptNode) delegateTask.getExecution().getVariable("stm_document")).getNodeRef();
        final NodeService nodeService = getServiceRegistry().getNodeService();

        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
//                return getServiceRegistry().getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
//                    @Override
//                    public Void execute() throws Throwable {
                        try {
					        String statusValue = nodeService.getProperty(stm_document, StatemachineModel.PROP_STATUS).toString();
					        if (!status.equals(statusValue)) {
					            nodeService.setProperty(stm_document, StatemachineModel.PROP_STATUS, status);
					//            nodeService.setProperty(stm_document.getChildRef(), StatemachineModel.PROP_STATEMACHINE_VERSION, version);
					            //запись в БЖ
					            try {
					                if (!forDraft) {
					                    String initiator = getServiceRegistry().getAuthenticationService().getCurrentUserName();
					                    List<String> objects = new ArrayList<String>(1);
					                    objects.add(status);
					                    getBusinessJournalService().log(initiator, stm_document,
					                            EventCategory.CHANGE_DOCUMENT_STATUS,
					                            "#initiator перевел(а) документ \"#mainobject\" в статус \"#object1\"", objects);
					                }
					            } catch (Exception e) {
					                logger.error("Не удалось создать запись бизнес-журнала", e);
					            }
					        }
					
					        //Проверяем наличие аспекта указывающего на черновик
					        if (!nodeService.hasAspect(stm_document, StatemachineModel.ASPECT_IS_DRAFT)) {
					            nodeService.addAspect(stm_document, StatemachineModel.ASPECT_IS_DRAFT, null);
					        }
					        if (!nodeService.hasAspect(stm_document, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
					            nodeService.addAspect(stm_document, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK, null);
					        }
					        nodeService.setProperty(stm_document, StatemachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS, "activiti$" + delegateTask.getId());
					        //Устанавливаем флаг черновика
					        nodeService.setProperty(stm_document, StatemachineModel.PROP_IS_DRAFT, forDraft);
                        } catch (InvalidNodeRefException ex) {
                            logger.error("Ошибка при изменении изменении статуса", ex);
                            throw ex;
                        } catch (Exception e) {
                            logger.error("Ошибка при изменении изменении статуса", e);
                            throw e;
                        }
                        return null;
//                    }
//                }, false, true);
            }
        });
					
        ((ActivitiScriptNode) delegateTask.getExecution().getVariable("stm_document")).reset();
        //Если стартовый статус, то ничего никуда не перемещаем
        if (forDraft) return;

        final NodeRef storeFolder = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return getServiceRegistry().getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                    	NodeRef storeFolder = null;
                        try {
                            RepositoryStructureHelper repositoryStructureHelper = getRepositoryStructureHelper();
                            NodeRef documents = repositoryStructureHelper.getDocumentsRef();
                            //Существует ли папка processId
                            NodeRef processFolder = null;
                            if (documents != null && processId != null) {
                                processFolder = nodeService.getChildByName(documents, ContentModel.ASSOC_CONTAINS, processId);
                                if (processFolder == null) {
                                    processFolder = repositoryStructureHelper.createFolder(documents, processId);
                                }
                            }
                            NodeRef versionFolder = null;
                            if (processFolder != null && version != null) {
                                versionFolder = nodeService.getChildByName(processFolder, ContentModel.ASSOC_CONTAINS, version);
                                if (versionFolder == null) {
                                    versionFolder = repositoryStructureHelper.createFolder(processFolder, version);
                                }
                            }

                            if (versionFolder != null && !"UNKNOWN".equals(status)) {
                                //Создаем папку статуса
                                statusFolder = nodeService.getChildByName(versionFolder, ContentModel.ASSOC_CONTAINS, status);
                                if (statusFolder == null) {
                                    statusFolder = repositoryStructureHelper.createFolder(versionFolder, status);
                                    //Установка статических прав на папку статуса
                                    if (statusFolder != null && processId != null) {
                                        staticPrivileges = getStateMachineHelper().getStateMecheneByName(processId).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(status).getStaticRoles();
                                        //TODO
                                        execBuildInTransactStatic(statusFolder, staticPrivileges);
                                        //Выдача прав Read для System ???
                                        //getServiceRegistry().getPermissionService().setPermission(statusFolder, AuthenticationUtil.SYSTEM_USER_NAME, "Read", true);
                                        //Лучше сделать его оунером т.к. сейчас это пользователь который перевел документ в статус
                                        if (!nodeService.hasAspect(statusFolder, ContentModel.ASPECT_OWNABLE)) {
                                            HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>(1, 1.0f);
                                            properties.put(ContentModel.PROP_OWNER, AuthenticationUtil.SYSTEM_USER_NAME);
                                            nodeService.addAspect(statusFolder, ContentModel.ASPECT_OWNABLE, properties);
                                        }
                                    }
                                }
                            }

                            Date now = new Date();
                            List<String> directoryPaths = repositoryStructureHelper.getDateFolderPath(now);
                            storeFolder = repositoryStructureHelper.createPath(NamespaceService.CONTENT_MODEL_1_0_URI, statusFolder, directoryPaths);
                            
                            return storeFolder; 
                        } catch (InvalidNodeRefException ex) {
                            logger.error("Ошибка при изменении изменении статуса", ex);
                            throw ex;
                        } catch (Exception e) {
                            logger.error("Ошибка при изменении изменении статуса", e);
                            throw e;
                        }
                    }
                }, false, true);
            }
        });
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
//                return getServiceRegistry().getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
//                    @Override
//                    public Void execute() throws Throwable {
                        try {
                        	// Установка динамических ролей для файла
					        if (processId != null && !"UNKNOWN".equals(status)) {
					            dynamicPrivileges = getStateMachineHelper().getStateMecheneByName(processId).getVersionByNumber(version).getSettings().getSettingsContent().getStatusByName(status).getDynamicRoles();
					            //TODO
					            execBuildInTransactDynamic(stm_document, dynamicPrivileges);
					        } else {
					            logger.error("Ошибка установки динамических прав для документа " + stm_document);
					        }
					        //Перемещаем в нужную папку
					        if (storeFolder != null) {
					            String name = (String) nodeService.getProperty(stm_document, ContentModel.PROP_NAME);
					            nodeService.moveNode(stm_document, storeFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));
					        } else {
					        	Date now = new Date();
					            throw new Exception("Папка статуса " + status + "/" + statusDateFormat.format(now) + " для документа " + stm_document + " не создана");
					        }
                        } catch (InvalidNodeRefException ex) {
                            logger.error("Ошибка при изменении изменении статуса", ex);
                            throw ex;
                        } catch (Exception e) {
                            logger.error("Ошибка при изменении изменении статуса", e);
                            throw e;
                        }

                        return null;
//                    }
//                }, false, true);
            }
        });
    }

    @Override
    public void init(BaseElement action, String processId) {
//        this.processId = processId;
//        List<Element> attributes = action.elements("attribute");
//        for (Element attribute : attributes) {
//            String name = attribute.attribute("name");
//            String value = attribute.attribute("value");
//            if ("status".equalsIgnoreCase(name)) {
//                status = value;
//            } else if ("version".equalsIgnoreCase(name)) {
//                version = value;
//            } else if ("forDraft".equalsIgnoreCase(name)) {
//                forDraft = Boolean.parseBoolean(value);
//            }
//        }
//        //Инициализация полей для редактирования
//        Element fieldsRoot = action.element("fields");
//        if (fieldsRoot != null) {
//            List<Element> fieldElements = fieldsRoot.elements("field");
//            for (Element fieldElement : fieldElements) {
//                String name = fieldElement.attribute("name");
//                boolean isEditable = Boolean.parseBoolean(fieldElement.attribute("isEditable"));
//                fields.add(new StateFieldImpl(name, isEditable));
//            }
//        }
//
//        //Инициализация категорий для редактирования
//        Element categoriesRoot = action.element("attachmentCategories");
//        if (categoriesRoot != null) {
//            List<Element> categoriesElements = categoriesRoot.elements("attachmentCategory");
//            for (Element categoryElement : categoriesElements) {
//                String name = categoryElement.attribute("name");
//                boolean isEditable = Boolean.parseBoolean(categoryElement.attribute("isEditable"));
//                categories.add(new StateFieldImpl(name, isEditable));
//            }
//        }
//
//        //Инициализация ролей
//        Element roles = action.element("roles");
//        if (roles != null) {
//            Element staticRoleElement = roles.element("static-roles");
//            staticPrivileges = initPrivileges(staticRoleElement);
//
//            Element dynamicRoleElement = roles.element("dynamic-roles");
//            dynamicPrivileges = initPrivileges(dynamicRoleElement);
//        }
//
//        //Если начальный статус, то папки для него не требуется
//        if (forDraft) return;
//
//        //Проверяем существует ли папка для этого статуса
//        NodeService nodeService = getServiceRegistry().getNodeService();
//        //Если статус не существует проверяем всю структуру папок
//
//        NodeRef documents = getRepositoryStructureHelper().getDocumentsRef();
//
//        //Существует ли папка processId
//        NodeRef processFolder = nodeService.getChildByName(documents, ContentModel.ASSOC_CONTAINS, processId);
//        if (processFolder == null) {
//            //Создаем папку
//            synchronized (lock) {
//                processFolder = nodeService.getChildByName(documents, ContentModel.ASSOC_CONTAINS, processId);
//                if (processFolder == null) {
//                    processFolder = createFolder(documents, processId);
//                }
//            }
//        }
//
//        if (version != null) {
//            NodeRef versionFolder = nodeService.getChildByName(processFolder, ContentModel.ASSOC_CONTAINS, version);
//
//            if (versionFolder == null) {
//                synchronized (lock) {
//                    versionFolder = nodeService.getChildByName(processFolder, ContentModel.ASSOC_CONTAINS, version);
//                    if (versionFolder == null) {
//                        versionFolder = createFolder(processFolder, version);
//                    }
//                }
//            }
//
//            //Создаем папку статуса
//            statusFolder = nodeService.getChildByName(versionFolder, ContentModel.ASSOC_CONTAINS, status);
//            if (statusFolder == null) {
//                synchronized (lock) {
//                    statusFolder = nodeService.getChildByName(versionFolder, ContentModel.ASSOC_CONTAINS, status);
//                    if (statusFolder == null) {
//                        statusFolder = createFolder(versionFolder, status);
//                        //Установка статических прав на папку статуса
//                        execBuildInTransactStatic(statusFolder, staticPrivileges);
//                        getServiceRegistry().getPermissionService().setPermission(statusFolder, AuthenticationUtil.SYSTEM_USER_NAME, "Read", true);
//                    }
//                }
//            }
//        }
    }

    public boolean isForDraft() {
        return forDraft;
    }

    @Override
    public void execute(DelegateExecution execution) {
//        final NodeRef nodeRef = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
//        NodeService nodeService = getServiceRegistry().getNodeService();
//        //Выставляем статус
//        ChildAssociationRef child = nodeService.getChildAssocs(nodeRef).get(0);
//
//        String statusValue = nodeService.getProperty(child.getChildRef(), StatemachineModel.PROP_STATUS).toString();
//        if (!status.equals(statusValue)) {
//            nodeService.setProperty(child.getChildRef(), StatemachineModel.PROP_STATUS, status);
//            nodeService.setProperty(child.getChildRef(), StatemachineModel.PROP_STATEMACHINE_VERSION, version);
//            //запись в БЖ
//            try {
//                String initiator = getServiceRegistry().getAuthenticationService().getCurrentUserName();
//                List<String> objects = new ArrayList<String>(1);
//                objects.add(status);
//                if (!forDraft) {
//                    getBusinessJournalService().log(initiator, child.getChildRef(),
//                            EventCategory.CHANGE_DOCUMENT_STATUS,
//                            "#initiator перевел(а) документ \"#mainobject\" в статус \"#object1\"", objects);
//                }
//            } catch (Exception e) {
//                logger.error("Не удалось создать запись бизнес-журнала", e);
//            }
//        }
//
//        //Проверяем наличие аспекта указывающего на черновик
//        if (!nodeService.hasAspect(child.getChildRef(), StatemachineModel.ASPECT_IS_DRAFT)) {
//            nodeService.addAspect(child.getChildRef(), StatemachineModel.ASPECT_IS_DRAFT, null);
//        }
//        //Устанавливаем флаг черновика
//        nodeService.setProperty(child.getChildRef(), StatemachineModel.PROP_IS_DRAFT, forDraft);
//
//        //Если стартовый статус, то ничего никуда не перемещаем
//        if (forDraft) return;
//
//        //Перемещаем в нужную папку
//        String name = (String) nodeService.getProperty(child.getChildRef(), ContentModel.PROP_NAME);
//        nodeService.moveNode(child.getChildRef(), statusFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));
//
//        // Установка динамических ролей для файла
//        execBuildInTransactDynamic(child.getChildRef(), dynamicPrivileges);
    }


    public String getStatus() {
        return status;
    }

    public Set<StateField> getFields() {
        return fields;
    }

    public String getVersion() {
        return version;
    }

    public Set<StateField> getCategories() {
        return categories;
    }

    public Map<String, LecmPermissionGroup> getDynamicPrivileges() {
        return dynamicPrivileges;
    }

    /**
     * Инициализирует список ролей из элемента role
     *
     * @param rolesElement
     * @return Список прав доступа для ролей
     */
//    private Map<String, LecmPermissionGroup> initPrivileges(Element rolesElement) {
//        Map<String, LecmPermissionGroup> permissions = new HashMap<String, LecmPermissionGroup>();
//        if (rolesElement != null) {
//            List<Element> roleElements = rolesElement.elements("role");
//            for (Element roleElement : roleElements) {
//                String role = roleElement.attribute("name");
//                String privilege = roleElement.attribute("privilege");
//                LecmPermissionGroup permissionGroup = getLecmPermissionService().findPermissionGroup(privilege);
//                permissions.put(role, permissionGroup);
//            }
//        }
//        return permissions;
//    }
    private void execBuildInTransactStatic(NodeRef node, final Map<String, LecmPermissionGroup> permissions) {
        final LecmPermissionService permissionsBuilder = getLecmPermissionService();
        permissionsBuilder.rebuildStaticACL(statusFolder, permissions);
    }

    private void execBuildInTransactDynamic(final NodeRef child, final Map<String, LecmPermissionGroup> permissions) {
        final LecmPermissionService permissionsBuilder = getLecmPermissionService();
        permissionsBuilder.rebuildACL(child, permissions);
    }

}
