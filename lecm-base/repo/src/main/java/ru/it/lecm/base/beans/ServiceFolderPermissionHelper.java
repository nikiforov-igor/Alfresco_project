package ru.it.lecm.base.beans;

import java.io.IOException;
import java.util.*;
import javax.transaction.UserTransaction;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
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
public class ServiceFolderPermissionHelper extends AbstractLifecycleBean {

	private NodeService nodeService;
	private BaseBean serviceBean;
	private List<String> permissionsList;
	private LecmPermissionService lecmPermissionService;
	private PermissionService permissionService;
	private AuthorityService authorityService;
	private TransactionService transactionService;
	final private static org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceFolderPermissionHelper.class);

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

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

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}
	
	public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
	
	@Override
	protected void onBootstrap(ApplicationEvent event)
	{
		Map<String, List<PermissionSettings>> permissions = parsePermissionsList(permissionsList);
		UserTransaction userTransaction = transactionService.getUserTransaction();
        try
        {
            userTransaction.begin();
            
    		applyPermissions(permissions);
			
    		userTransaction.commit();
        }
        catch(Throwable e)
        {
            // rollback the transaction
            try
            { 
                if (userTransaction != null) 
                {
                    userTransaction.rollback();
                }
            }
            catch (Exception ex)
            {
                // NOOP 
            }
            throw new AlfrescoRuntimeException("Service folders [applyPermissions] bootstrap failed", e);
        }
	}
    
    @Override
	protected void onShutdown(ApplicationEvent event)
	{
	    // NOOP
	}

	public void bootstrap() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "permissionService", permissionService);
		PropertyCheck.mandatory(this, "lecmPermissionService", lecmPermissionService);
		PropertyCheck.mandatory(this, "serviceBean", serviceBean);
		PropertyCheck.mandatory(this, "permissionsList", permissionsList);
	}

	private Map<String, List<PermissionSettings>> parsePermissionsList(List<String> permissionsList) {
		Map<String, List<PermissionSettings>> result = new HashMap<String, List<PermissionSettings>>();

		try {
			SAXParserFactory SAXFactory = SAXParserFactory.newInstance();
			SAXParser parser = SAXFactory.newSAXParser();
			SAXParserHandler handler = new SAXParserHandler(result);

			for(String permissionsFile : permissionsList) {
				ClassPathResource resource = new ClassPathResource (permissionsFile);
				parser.parse(resource.getInputStream(), handler);

			}
		} catch(ParserConfigurationException ex) {
			logger.error(ex.getMessage(), ex);
		} catch(SAXException ex) {
			logger.error(ex.getMessage(), ex);
		} catch(IOException ex) {
			logger.error(ex.getMessage(), ex);
		}

		return result;
	}

	private void applyPermissions(Map<String, List<PermissionSettings>> permissions) {
		Set<NodeRef> processedNodes = new HashSet<NodeRef>();
		for (Map.Entry<String, List<PermissionSettings>> businessRole : permissions.entrySet()) {
			final String businessRoleName = businessRole.getKey();
			final Types.SGPosition sgBusinessRole = Types.SGKind.SG_BR.getSGPos(businessRoleName);
			String shortName;
			if ("*".equals(businessRoleName)) {
				shortName = "EVERYONE";
			} else {
				shortName = sgBusinessRole.getAlfrescoSuffix();
			}

			for (PermissionSettings objectsPermissions : businessRole.getValue()) {
				final NodeRef objectNode = getObjectNodeRef(objectsPermissions.getObjectName());
				if (objectNode == null) {
					continue;
				}
				final String permissionGroup = objectsPermissions.getPermissionGroup();
				final boolean inheritPermissions = objectsPermissions.isInheritPermissions();
				if (!processedNodes.contains(objectNode)) {
					permissionService.clearPermission(objectNode, PermissionService.ALL_AUTHORITIES);
					permissionService.setInheritParentPermissions(objectNode, inheritPermissions);
					processedNodes.add(objectNode);
				}
				LecmPermissionGroup lecmPermissionGroup = lecmPermissionService.findPermissionGroup(permissionGroup);
				if (lecmPermissionGroup != null) {
					lecmPermissionService.grantAccessByPosition(lecmPermissionGroup, objectNode, sgBusinessRole);
				} else {
					String authority = authorityService.getName(AuthorityType.GROUP, shortName);
					permissionService.setPermission(objectNode, authority, permissionGroup, true);
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
			NodeRef elementNode = null;
			if (pathElement.equals("..")) {
				ChildAssociationRef parent = nodeService.getPrimaryParent(currentNode);
				 if (parent != null) {
					 elementNode = parent.getParentRef();
				 }
			} else {
				elementNode = nodeService.getChildByName(currentNode, ContentModel.ASSOC_CONTAINS, pathElement);
			}
			if (elementNode != null) {
				currentNode = elementNode;
			} else {
				currentNode = null;
				break;
			}
		}

		return currentNode;
	}

	private static class SAXParserHandler extends DefaultHandler {

		private final Map<String, List<PermissionSettings>> result;
		private List<PermissionSettings> permissionsList;
		String businessRoleName;

		private SAXParserHandler(Map<String, List<PermissionSettings>> result) {
			super();
			this.result = result;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if ("BusinessRole".equalsIgnoreCase(qName)) {
				businessRoleName = attributes.getValue("id");
				permissionsList = new ArrayList<PermissionSettings>();
			} else if ("Permission".equalsIgnoreCase(qName)) {
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
			if ("BusinessRole".equalsIgnoreCase(qName)) {
				result.put(businessRoleName, permissionsList);
			}
		}
	}

	private static class PermissionSettings {
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
