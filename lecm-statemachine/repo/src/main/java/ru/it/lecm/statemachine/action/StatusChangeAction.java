package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:53
 * <p/>
 * Действие машины состояний отвечающее за смену статуса у документа.
 */
public class StatusChangeAction extends StateMachineAction {

	private String status = "UNKNOWN";
	private NodeRef folder = null;
	private String uuid = null;
	private boolean startStatus = false;
	private ArrayList<Permission> dynamicRoles = new ArrayList<Permission>();

	private static Log logger = LogFactory.getLog(StatusChangeAction.class);

	@Override
	public void init(Element action, String processId) {
		List<Element> attributes = action.elements("attribute");
		for (Element attribute : attributes) {
			String name = attribute.attribute("name");
			String value = attribute.attribute("value");
			if ("status".equalsIgnoreCase(name)) {
				status = value;
			} else if ("uuid".equalsIgnoreCase(name)) {
				uuid = value;
				folder = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, uuid);
			} else if ("startStatus".equalsIgnoreCase(name)) {
				startStatus = Boolean.parseBoolean(value);
			}
		}

		//Инициализация ролей
		Element roles = action.element("roles");
		if (roles != null) {
			Element staticRoleElement = roles.element("static-roles");
			ArrayList<Permission> permissions = initPermissions(staticRoleElement);

			Element dynamicRoleElement = roles.element("dynamic-roles");
			dynamicRoles = initPermissions(dynamicRoleElement);
		}

		//Если начальный статус, то папки для него не требуется
		if (startStatus) return;

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
				} catch (FileNotFoundException e) {
					logger.error("Set Status Exception", e);
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

	}

	@Override
	public void execute(DelegateExecution execution) {
		NodeRef nodeRef = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		NodeService nodeService = getServiceRegistry().getNodeService();
		//Выставляем статус
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		for (ChildAssociationRef child : children) {
			nodeService.setProperty(child.getChildRef(), QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "status"), status);
		}

		//Если стартовый статус, то ничего никуда не перемещаем
		if (startStatus) return;

		//Перемещаем в нужную папку
		for (ChildAssociationRef child : children) {
			String name = (String) nodeService.getProperty(child.getChildRef(), ContentModel.PROP_NAME);
			nodeService.moveNode(child.getChildRef(), folder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));
		}

		//Установка динамических ролей для файла
	}


	private void checkStatus(NodeRef processFolder) {
		NodeService nodeService = getServiceRegistry().getNodeService();
		NodeRef existsFolder = nodeService.getChildByName(processFolder, ContentModel.ASSOC_CONTAINS, status);
		if (existsFolder != null && !existsFolder.equals(folder)) {
			try {
				getServiceRegistry().getFileFolderService().rename(existsFolder, existsFolder.getId());
			} catch (FileNotFoundException e) {
				logger.error("Set Status Exception", e);
				throw new AlfrescoRuntimeException("Set Status Exception", e);
			}
		}
	}

	/**
	 * Инициализирует список роле из элемента role
	 * @param rolesElement
	 * @return Список прав доступа для ролей
	 */
	private ArrayList<Permission> initPermissions(Element rolesElement) {
		ArrayList<Permission> permissions = new ArrayList<Permission>();
		if (rolesElement != null) {
			List<Element> roleElements = rolesElement.elements("role");
			for (Element roleElement : roleElements) {
				String role = roleElement.attribute("name");
				String permission = roleElement.attribute("permission");
				permissions.add(new Permission(role, permission));
			}
		}
		return permissions;
	}


	private class Permission {

		private String role;
		private String value;

		private Permission(String role, String value) {
			this.role = role;
			this.value = value;
		}

		public String getRole() {
			return role;
		}

		public String getValue() {
			return value;
		}
	}

}
