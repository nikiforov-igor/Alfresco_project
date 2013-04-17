package ru.it.lecm.base.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import javax.xml.parsers.SAXParserFactory;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.security.PermissionService;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.security.Types;

/**
 * Бин, позволяющий каждому сервису указывать, какая permissionGroup для какой
 * бизнес-роли устанавливается для каждой конкретной папки (или даже ноды) в
 * рабочей папки сервиса (/app:company_home/cm:Business
 * platform/cm:LECM/названиеСервиса)
 *
 * @author vlevin
 */
public class ServiceFolderPermissionHelper extends BaseBean {

	private BaseBean serviceBean;
	private List<String> permissionsList;
	private LecmPermissionService lecmPermissionService;
	private PermissionService permissionService;
	final private static org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceFolderPermissionHelper.class);

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setServiceBean(BaseBean serviceBean) {
		this.serviceBean = serviceBean;
	}

	public void setPermissionsList(List<String> permissionsList) {
		this.permissionsList = permissionsList;
	}

	public void bootstrap() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "permissionService", permissionService);
		PropertyCheck.mandatory(this, "lecmPermissionService", lecmPermissionService);
		PropertyCheck.mandatory(this, "serviceBean", serviceBean);
		PropertyCheck.mandatory(this, "permissionsList", permissionsList);
		Map<String, List<PermissionSettings>> permissions = parsePermissionsList(permissionsList);
		applyPermissions(permissions);
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	private Map<String, List<PermissionSettings>> parsePermissionsList(List<String> permissionsList) {
		Map<String, List<PermissionSettings>> result = new HashMap<String, List<PermissionSettings>>();
		SAXParserFactory SAXFactory = SAXParserFactory.newInstance();

		for (String permissionsFile : permissionsList) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(permissionsFile);
			try {
				SAXFactory.newSAXParser().parse(inputStream, new SAXParserHandler(result));
			} catch (ParserConfigurationException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (SAXException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}

		}
		return result;
	}

	private void applyPermissions(Map<String, List<PermissionSettings>> permissions) {
		for (Map.Entry<String, List<PermissionSettings>> businessRole : permissions.entrySet()) {
			final String businessRoleName = businessRole.getKey();
			final Types.SGPosition sgBusinessRole = Types.SGKind.SG_BR.getSGPos(businessRoleName);

			for (PermissionSettings objectsPermissions : businessRole.getValue()) {
				final NodeRef objectNode = getObjectNodeRef(objectsPermissions.getObjectName());
				if (objectNode == null) {
					return;
				}
				final String permissionGroup = objectsPermissions.getPermissionGroup();
				final boolean inheritPermissions = objectsPermissions.isInheritPermissions();
				permissionService.clearPermission(objectNode, PermissionService.ALL_AUTHORITIES);
				permissionService.setInheritParentPermissions(objectNode, inheritPermissions);
				LecmPermissionGroup lecmPermissionGroup = lecmPermissionService.findPermissionGroup(permissionGroup);
				if (lecmPermissionGroup != null) {
					lecmPermissionService.grantAccessByPosition(lecmPermissionGroup, objectNode, sgBusinessRole);
				}
			}
		}
	}

	private NodeRef getObjectNodeRef(String objectName) {
		final NodeRef serviceRootNode = serviceBean.getServiceRootFolder();
		if (serviceRootNode == null) {
			throw new IllegalArgumentException("Service bean '" + serviceBean.getClass().getName() + "' has no root folder!");
		}

		if (".".equals(objectName)) {
			return serviceRootNode;
		}
		NodeRef currentNode = serviceRootNode;
		String[] splittedName = objectName.split("/");
		for (String pathElement : splittedName) {
			NodeRef elementNode = nodeService.getChildByName(currentNode, ContentModel.ASSOC_CONTAINS, pathElement);
			if (elementNode != null) {
				currentNode = elementNode;
			} else {
				currentNode = null;
				break;
			}
		}

		return currentNode;
	}

	private class SAXParserHandler extends DefaultHandler {

		private Map<String, List<PermissionSettings>> result;
		private List<PermissionSettings> permissionsList;
		String businessRoleName;

		private SAXParserHandler(Map<String, List<PermissionSettings>> result) {
			this.result = result;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if (qName.equalsIgnoreCase("BusinessRole")) {
				businessRoleName = attributes.getValue("id");
				permissionsList = new ArrayList<PermissionSettings>();
			} else if (qName.equalsIgnoreCase("Permission")) {
				PermissionSettings permissionsSettings = new PermissionSettings();
				String object = attributes.getValue("object");
				String permissionGroup = attributes.getValue("permissionGroup");
				String inheritPermissions = attributes.getValue("inheritPermissions");
				permissionsSettings.setObjectName(object);
				permissionsSettings.setPermissionGroup(permissionGroup);
				if ("false".equalsIgnoreCase(inheritPermissions)) {
					permissionsSettings.setInheritPermissions(false);
				} else {
					permissionsSettings.setInheritPermissions(true);
				}
				permissionsList.add(permissionsSettings);

			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (qName.equalsIgnoreCase("BusinessRole")) {
				result.put(businessRoleName, permissionsList);
			}
		}
	}

	private class PermissionSettings {
		private String objectName;
		private String permissionGroup;
		private boolean inheritPermissions;

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		public void setPermissionGroup(String permissionGroup) {
			this.permissionGroup = permissionGroup;
		}

		public void setInheritPermissions(boolean inheritPermissions) {
			this.inheritPermissions = inheritPermissions;
		}

		public String getObjectName() {
			return objectName;
		}

		public String getPermissionGroup() {
			return permissionGroup;
		}

		public boolean isInheritPermissions() {
			return inheritPermissions;
		}
	}
}
