package ru.it.lecm.base.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.base.ServiceFolder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ParameterCheck;

/**
 * User: AIvkin
 * Date: 27.12.12
 * Time: 15:12
  */
public abstract class BaseBean implements InitializingBean {
	public static  final QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	final DateFormat FolderNameFormatYear = new SimpleDateFormat("yyyy");
	final DateFormat FolderNameFormatMonth = new SimpleDateFormat("MM");
	final DateFormat FolderNameFormatDay = new SimpleDateFormat("dd");

    public static final QName TYPE_BASE_DOCUMENT = QName.createQName("http://www.it.ru/logicECM/document/1.0", "base");

	public static final String LINK_URL = "/share/page/view-metadata";
	public static final String DOCUMENT_LINK_URL = "/share/page/document";
	public static final String WORKFLOW_LINK_URL = "/share/page/workflow-details";
	public static final String DOCUMENT_ATTACHMENT_LINK_URL = "/share/page/document-attachment";

	/**
	 * карта с папками из декларативного описания бина
	 */
	private Map<String, String> folders;
	/**
	 * карта с папками для конкретного сервиса
	 */
	private final Map<String, ServiceFolder> serviceFolders = new HashMap<String, ServiceFolder> ();

	protected RepositoryStructureHelper repositoryStructureHelper;
	protected NodeService nodeService;
	protected TransactionService transactionService;
    protected ServiceRegistry serviceRegistry;
	protected AuthenticationService authService;
	protected List<String> notificationChannels;

	private final static Object lock = new Object();

	protected static enum ASSOCIATION_TYPE {
		SOURCE,
		TARGET
	}

	public NodeService getNodeService() {
		return this.nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setRepositoryStructureHelper (final RepositoryStructureHelper repositoryStructureHelper) {
		this.repositoryStructureHelper = repositoryStructureHelper;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	public List<String> getNotificationChannels() {
		return notificationChannels;
	}

	public void setNotificationChannels(List<String> notificationChannels) {
		this.notificationChannels = notificationChannels;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void afterPropertiesSet () throws Exception {
		//когда все проперти проинициализируются, мы пробежимся по карте с папками и создадим их все
		final ServiceFolderStructureHelper serviceFolderStructureHelper = (ServiceFolderStructureHelper) repositoryStructureHelper;
		if (folders != null) {
			for (Entry<String, String> entry : folders.entrySet ()) {
				String relativePath = entry.getValue ();
				ServiceFolder serviceFolder = new ServiceFolder (relativePath, null);
				NodeRef folderRef = serviceFolderStructureHelper.getFolderRef (serviceFolder);
				serviceFolder.setFolderRef (folderRef);
				serviceFolders.put (entry.getKey (), serviceFolder);
			}
		}
	}

	/**
	 * проверяет что объект имеет подходящий тип
	 */
	public boolean isProperType(NodeRef ref, Set<QName> types) {
		if (ref != null) {
			final QName type = nodeService.getType(ref);
			return types.contains(type);
		}
		return false;
	}

	public boolean isProperType(NodeRef ref, QName ... types) {
		if (ref == null || types == null || types.length == 0)
			return false;
		final Set<QName> typeSet = new HashSet<QName>();
		Collections.addAll(typeSet, types);
		return isProperType(ref, typeSet);
	}

	/**
	 * Проверка элемента на архивность
	 * @param ref Ссылка на элемент
	 * @return true - если элемент архивный, иначе false
	 */
	public boolean isArchive(NodeRef ref){
		boolean isArchive = StoreRef.STORE_REF_ARCHIVE_SPACESSTORE.equals (ref.getStoreRef ());
		Boolean isActive = (Boolean) nodeService.getProperty(ref, IS_ACTIVE);
		return isArchive || (isActive != null && !isActive);
	}

	/**
	 * Проверка элемента на архивность
	 * @param ref Ссылка на элемент
	 * @return true - если элемент архивный, иначе false
	 */
	public boolean isOwnNode(NodeRef ref){
		return ref != null && nodeService.exists(ref) &&
				nodeService.getProperty(ref, ContentModel.PROP_CREATOR).equals(authService.getCurrentUserName());
	}

	/**
	 * получение связанной ноды по ассоциации. Для типа связи 1:1, 1:0, 0:1
	 *
	 * @param nodeRef       исходная нода
	 * @param assocTypeName имя типа ассоциации
	 * @param typeName      имя типа данных который завязан на ассоциацию
	 * @param type          направление ассоциации source или target
	 * @return найденный NodeRef или null
	 */
	public NodeRef findNodeByAssociationRef(NodeRef nodeRef, QNamePattern assocTypeName, QNamePattern typeName, ASSOCIATION_TYPE type) {
		List<AssociationRef> associationRefs;

		switch (type) {
			case SOURCE:
				associationRefs = nodeService.getSourceAssocs(nodeRef, assocTypeName);
				break;
			case TARGET:
				associationRefs = nodeService.getTargetAssocs(nodeRef, assocTypeName);
				break;
			default:
				associationRefs = new ArrayList<AssociationRef>();
		}
		NodeRef foundNodeRef = null;
		for (AssociationRef associationRef : associationRefs) {
			NodeRef assocNodeRef;
			switch (type) {
				case SOURCE:
					assocNodeRef = associationRef.getSourceRef();
					break;
				case TARGET:
					assocNodeRef = associationRef.getTargetRef();
					break;
				default:
					assocNodeRef = null;
					break;
			}
			if (assocNodeRef != null) {
				if (typeName != null) {
					QName foundType = nodeService.getType(assocNodeRef);
					if (typeName.isMatch(foundType)) {
						foundNodeRef = assocNodeRef;
					}
				} else {
					foundNodeRef = assocNodeRef;
				}
			}
		}
		return foundNodeRef;
	}

	/**
	 * получение связанных нод по ассоциации. Для множественных связей
	 *
	 * @param nodeRef       исходная нода
	 * @param assocTypeName имя типа ассоциации
	 * @param typeName      имя типа данных который завязан на ассоциацию
	 * @param type          направление ассоциации source или target
	 * @return список NodeRef
	 */
	public List<NodeRef> findNodesByAssociationRef(NodeRef nodeRef, QNamePattern assocTypeName, QNamePattern typeName, ASSOCIATION_TYPE type) {
		List<AssociationRef> associationRefs;

		switch (type) {
			case SOURCE:
				associationRefs = nodeService.getSourceAssocs(nodeRef, assocTypeName);
				break;
			case TARGET:
				associationRefs = nodeService.getTargetAssocs(nodeRef, assocTypeName);
				break;
			default:
				associationRefs = new ArrayList<AssociationRef>();
		}
		List<NodeRef> foundNodeRefs = new ArrayList<NodeRef>();
		for (AssociationRef associationRef : associationRefs) {
			NodeRef assocNodeRef;
			switch (type) {
				case SOURCE:
					assocNodeRef = associationRef.getSourceRef();
					break;
				case TARGET:
					assocNodeRef = associationRef.getTargetRef();
					break;
				default:
					assocNodeRef = null;
					break;
			}
			if (assocNodeRef != null) {
				if (typeName != null){
					QName foundType = nodeService.getType(assocNodeRef);
					if (typeName.isMatch(foundType)) {
						foundNodeRefs.add(assocNodeRef);
					}
				} else {
					foundNodeRefs.add(assocNodeRef);
				}
			}
		}
		return foundNodeRefs;
	}

	public List<String> getDateFolderPath(Date date) {
		List<String> result = new ArrayList<String>();
		result.add(FolderNameFormatYear.format(date));
		result.add(FolderNameFormatMonth.format(date));
		result.add(FolderNameFormatDay.format(date));
		return result;
	}

	public NodeRef getFolder(final NodeRef root, final List<String> directoryPaths) {
	 	return getFolder(NamespaceService.CONTENT_MODEL_1_0_URI, root, directoryPaths);
	}

	/**
	 * Метод, возвращающий ссылку на директорию согласно заданным параметрам
	 *
	 * @param nameSpace          - name space для сохранения
	 * @param root               - корень, относительно которого строится путь
	 * @param directoryPaths     - список папок
	 * @return ссылка на директорию
	 */
    public NodeRef getFolder(final String nameSpace, final NodeRef root, final List<String> directoryPaths) {
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        // имя директории "Корень/Тип Объекта/Категория события/ГГГГ/ММ/ДД"
                        NodeRef directoryRef;
                        directoryRef = root;
                        for (String pathString : directoryPaths) {
                            NodeRef pathDir = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
                            if (pathDir == null) {
                                synchronized (lock) {
                                    pathDir = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
                                    if (pathDir == null) {
                                        QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                        QName assocQName = QName.createQName(nameSpace, pathString);
                                        QName nodeTypeQName = ContentModel.TYPE_FOLDER;
                                        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                        properties.put(ContentModel.PROP_NAME, pathString);
                                        ChildAssociationRef result = nodeService.createNode(directoryRef, assocTypeQName, assocQName, nodeTypeQName, properties);
                                        directoryRef = result.getChildRef();
                                    } else {
                                        directoryRef = pathDir;
                                    }
                                }
                            } else {
                                directoryRef = pathDir;
                            }
                        }
                        return directoryRef;
                    }
                });
            }
        };
        return AuthenticationUtil.runAsSystem(raw);
    }

	/**
	 * Папки создаются относительно папки Home
	 * @param folders карта с папками которые мы хотим создать
	 */
	public void setFolders (final Map<String, String> folders) {
		this.folders = folders;
	}

	/**
	 * получение созданной папки
	 * @param folderId
	 * @return
	 */
	public NodeRef getFolder (final String folderId) {
		final ServiceFolderStructureHelper serviceFolderStructureHelper = (ServiceFolderStructureHelper) repositoryStructureHelper;
		return serviceFolderStructureHelper.getFolderRef (serviceFolders.get (folderId));
	}

    /**
     * Оборачиваем узел в ссылку html страницы
     * @param nodeRef
     * @param description
     * @param linkUrl
     * @return
     */
    public String wrapperLink(NodeRef nodeRef, String description, String linkUrl) {
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        return  "<a href=\"" + serverUrl + linkUrl + "?nodeRef=" + nodeRef + "\">"
                + description + "</a>";
    }

    /**
     * Проверка является ли текущий пользователь автором узла
     * @param nodeRef
     * @return
     */
    public boolean isAuthorNode (NodeRef nodeRef) {
        String person = authService.getCurrentUserName();
        return person.equals(nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR));
    }

	/**
	 * Каждый сервис живет в каталоге /app:company_home/cm:Business
	 * platform/cm:LECM/имя_сервиса
	 * Данный сервис должен возвращать NodeRef на этот каталог сервиса.
	 *
	 * @return Ссылка на каталог, в котором находятся все данные сервиса
	 */
	abstract public NodeRef getServiceRootFolder();

	/**
	 * создаем папку у указанного родителя
	 *
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef свежесозданной папки
	 */
	protected NodeRef createFolder(final NodeRef parentRef, final String folder) {
		ParameterCheck.mandatory("parentRef", parentRef);
		ParameterCheck.mandatory("folder", folder);
		NodeRef folderRef = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, folder);
						Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
						properties.put(ContentModel.PROP_NAME, folder);
						ChildAssociationRef childAssoc = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
						return childAssoc.getChildRef();
					}
				});
			}
		});
		return folderRef;
	}

	/**
	 * получаем папку у указанного родителя
	 *
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef если папка есть, null в противном случае
	 */
	protected NodeRef getFolder(final NodeRef parentRef, final String folder) {
		NodeRef folderRef = null;
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if (childAssocs != null) {
			for (ChildAssociationRef childAssoc : childAssocs) {
				NodeRef childRef = childAssoc.getChildRef();
				if (folder.equals(nodeService.getProperty(childRef, ContentModel.PROP_NAME))) {
					folderRef = childRef;
					break;
				}
			}
		}
		return folderRef;
	}
}
