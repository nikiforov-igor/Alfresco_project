package ru.it.lecm.statemachine.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.security.events.INodeACLBuilder;
import ru.it.lecm.security.events.INodeACLBuilder.StdPermission;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:53
 * <p/>
 * Действие машины состояний отвечающее за смену статуса у документа.
 */
public class StatusChangeAction extends StateMachineAction {

	private String status = "UNKNOWN";
	private String processId = null;
	private NodeRef folder = null;
	private String uuid = null;
	private boolean forDraft = false;
	private Map<String,INodeACLBuilder.StdPermission> dynamicPermissions = new HashMap<String, INodeACLBuilder.StdPermission>();

	private static Log logger = LogFactory.getLog(StatusChangeAction.class);

	@Override
	public void init(Element action, String processId) {
		this.processId = processId;
		List<Element> attributes = action.elements("attribute");
		for (Element attribute : attributes) {
			String name = attribute.attribute("name");
			String value = attribute.attribute("value");
			if ("status".equalsIgnoreCase(name)) {
				status = value;
			} else if ("uuid".equalsIgnoreCase(name)) {
				uuid = value;
				folder = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, uuid);
			} else if ("forDraft".equalsIgnoreCase(name)) {
				forDraft = Boolean.parseBoolean(value);
			}
		}

		//Инициализация ролей
		Map<String, INodeACLBuilder.StdPermission> staticPermissions = new HashMap<String, INodeACLBuilder.StdPermission>();
		Element roles = action.element("roles");
		if (roles != null) {
			Element staticRoleElement = roles.element("static-roles");
			staticPermissions = initPermissions(staticRoleElement);

			Element dynamicRoleElement = roles.element("dynamic-roles");
			dynamicPermissions = initPermissions(dynamicRoleElement);
		}

		//Если начальный статус, то папки для него не требуется
		if (forDraft) return;

		// NOTE: теперь этот метод не нужно вызывать, т.к. права задаются во время смены статуса
		// getLecmAclBuilderBean().regAccessMatrix(processId, status, permissions);

		//Проверяем существует ли папка для этого статуса
		NodeService nodeService = getServiceRegistry().getNodeService();
		if (nodeService.exists(folder)) {
			//если существует проверяем не переименован ли статус
			String name = (String) nodeService.getProperty(folder, ContentModel.PROP_NAME);
			if (!name.equals(status)) {
				//Если изменился - переименовываем
				//проверяем наличие такого же названия статуса
				NodeRef processFolder = nodeService.getPrimaryParent(folder).getParentRef();
				checkStatus(processFolder);
				//Переименовываем
				try {
					getServiceRegistry().getFileFolderService().rename(folder, status);
				} catch (Exception e) {
					//logger.error("Set Status Exception", e);
				}
			}
		} else {
			//Если статус не существует проверяем всю структуру папок

			NodeRef companyHome = getCompanyHome();

			//Существует ли папка documents
			NodeRef documents = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,"documents");
			if (documents == null) {
				//Создаем папку
				documents = createFolder(companyHome, "documents");
			}

			//Существует ли папка processId
			NodeRef processFolder = nodeService.getChildByName(documents, ContentModel.ASSOC_CONTAINS, processId);
			if (processFolder == null) {
				//Создаем папку
				processFolder = createFolder(documents, processId);
			}

			//Существует ли папка с именем нового статуса, если существует переименовываем
			checkStatus(processFolder);

			//Создаем папку статуса
			createFolder(processFolder, status, uuid);
		}

		//Установка статических прав на папку статуса
		execBuildInTransactStatic(folder, staticPermissions);
	}

	@Override
	public void execute(DelegateExecution execution) {
		final NodeRef nodeRef = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		final NodeService nodeService = getServiceRegistry().getNodeService();
		//Выставляем статус
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		for (ChildAssociationRef child : children) {
			nodeService.setProperty(child.getChildRef(), QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "status"), status);
			//запись в БЖ
			try {
				String initiator = getServiceRegistry().getAuthenticationService().getCurrentUserName();
				List<String> objects = new ArrayList<String>(1);
				objects.add(status);
				if (forDraft) { // если стартовый статус - генерируем событие о создании в начальном статусе
					getBusinessJournalService().log(initiator, child.getChildRef(),
							EventCategory.ADD,
							"Создан новый документ \"#mainobject\" в статусе \"#object1\"", objects);
				} else { // о переводе в другой статус
					getBusinessJournalService().log(initiator, child.getChildRef(),
							EventCategory.CHANGE_DOCUMENT_STATUS,
							"Сотрудник #initiator перевел документ \"#mainobject\" в статус \"#object1\"", objects);
				}
			} catch (Exception e) {
				logger.error("Не удалось создать запись бизнес-журнала", e);
			}
		}

		//Если стартовый статус, то ничего никуда не перемещаем
		if (forDraft) return;

		//Перемещаем в нужную папку
                
                final List<ChildAssociationRef> c = children;
                AuthenticationUtil.runAsSystem( new AuthenticationUtil.RunAsWork<Void>() {

                @Override
                public Void doWork() throws Exception {
                    for (ChildAssociationRef child : c) {
			String name = (String) nodeService.getProperty(child.getChildRef(), ContentModel.PROP_NAME);
			nodeService.moveNode(child.getChildRef(), folder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));
		}
                    return null;
                }
            });
		

		// Установка динамических ролей для файла
		children = nodeService.getChildAssocs(nodeRef);
		execBuildInTransactDynamic(children, dynamicPermissions);
	}


	private void checkStatus(NodeRef processFolder) {
		NodeService nodeService = getServiceRegistry().getNodeService();
		NodeRef existsFolder = nodeService.getChildByName(processFolder, ContentModel.ASSOC_CONTAINS, status);
		if (existsFolder != null && !existsFolder.equals(folder)) {
			try {
				getServiceRegistry().getFileFolderService().rename(existsFolder, existsFolder.getId());
			} catch (Exception e) {
				//logger.error("Set Status Exception", e);
				//throw new AlfrescoRuntimeException("Set Status Exception", e);
			}
		}
	}

	/**
	 * Инициализирует список ролей из элемента role
	 * @param rolesElement                      INodeACLBuilder.StdPermission.valueOf(value)
	 * @return Список прав доступа для ролей
	 */
	private Map<String, INodeACLBuilder.StdPermission> initPermissions(Element rolesElement) {
		Map<String, INodeACLBuilder.StdPermission> permissions = new HashMap<String, INodeACLBuilder.StdPermission>();
		if (rolesElement != null) {
			List<Element> roleElements = rolesElement.elements("role");
			for (Element roleElement : roleElements) {
				String role = roleElement.attribute("name");
				INodeACLBuilder.StdPermission permission;
				try {
					permission = INodeACLBuilder.StdPermission.valueOf(roleElement.attribute("permission"));
				} catch (IllegalArgumentException e) {
					permission = INodeACLBuilder.StdPermission.noaccess;
				}
				permissions.put(role, permission);
			}
		}
		return permissions;
	}

	private void execBuildInTransactStatic(NodeRef node
			, final Map<String, INodeACLBuilder.StdPermission> permissions) {
		final INodeACLBuilder permissionsBuilder = getLecmAclBuilderBean();
		getServiceRegistry().getTransactionService().getRetryingTransactionHelper().doInTransaction(
				new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
					@Override
					public Object execute() throws Throwable {
						permissionsBuilder.rebuildStaticACL(folder, permissions);
						return null;
					}
				}, false, true);
	}

	private void execBuildInTransactDynamic(final List<ChildAssociationRef> children
			, final Map<String, StdPermission> permissions) {
		final INodeACLBuilder permissionsBuilder = getLecmAclBuilderBean();
		getServiceRegistry().getTransactionService().getRetryingTransactionHelper().doInTransaction(
				new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
					@Override
					public Object execute() throws Throwable {
						for (ChildAssociationRef child : children) {
							permissionsBuilder.rebuildStaticACL(child.getChildRef(), permissions);
						}
						return null;
					}
				}, false, true);
	}

}
