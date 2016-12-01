package ru.it.lecm.base.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import ru.it.lecm.base.ServiceFolder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;

/**
 * User: AIvkin Date: 27.12.12 Time: 15:12
 */
public abstract class BaseBean extends AbstractLifecycleBean implements InitializingBean, LecmService {

    public static final String DICTIONARY_NAMESPACE = "http://www.it.ru/lecm/dictionary/1.0";
    public static final String LINKS_NAMESPACE = "http://www.it.ru/logicECM/links/1.0";
    public static final QName IS_ACTIVE = QName.createQName(DICTIONARY_NAMESPACE, "active");
    public static final QName ASPECT_ACTIVE = QName.createQName(DICTIONARY_NAMESPACE, "aspect_active");

    private static final Logger logger = LoggerFactory.getLogger(BaseBean.class);

    final DateFormat FolderNameFormatYear = new SimpleDateFormat("yyyy");
    final DateFormat FolderNameFormatMonth = new SimpleDateFormat("MM");
    final DateFormat FolderNameFormatDay = new SimpleDateFormat("dd");

    public static final QName TYPE_BASE_DOCUMENT = QName.createQName("http://www.it.ru/logicECM/document/1.0", "base");

    public static final String LINK_URL = "/share/page/view-metadata";
    public static final String DETAILS_LINK_URL = "/share/page/document-details";
    public static final String WORKFLOW_LINK_URL = "/share/page/workflow-details";
    public static final String DOCUMENT_ATTACHMENT_LINK_URL = "/share/page/document-attachment";

    public static final QName TYPE_BASE_LINK = QName.createQName(LINKS_NAMESPACE, "link");
    public static final QName PROP_BASE_LINK_URL = QName.createQName(LINKS_NAMESPACE, "url");

    public static final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateFormat DateFormatISO8601_SZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    /**
     * карта с папками из декларативного описания бина
     */
    private Map<String, String> folders;
    /**
     * карта с папками для конкретного сервиса
     */
    final protected Map<String, ServiceFolder> serviceFolders = new HashMap<String, ServiceFolder>();

    protected RepositoryStructureHelper repositoryStructureHelper;
    protected NodeService nodeService;
    protected TransactionService transactionService;
    protected ServiceRegistry serviceRegistry;
    protected AuthenticationService authService;
    protected LecmTransactionHelper lecmTransactionHelper;
	protected LecmServicesRegistry lecmServicesRegistry;
	protected Repository repository;

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setLecmServicesRegistry(LecmServicesRegistry lecmServicesRegistry) {
		this.lecmServicesRegistry = lecmServicesRegistry;
	}
	
    public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
        this.lecmTransactionHelper = lecmTransactionHelper;
    }

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

    public void setRepositoryStructureHelper(final RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
	protected void onBootstrap(ApplicationEvent event)
	{
    	// NOOP
	}
    
    @Override
	protected void onShutdown(ApplicationEvent event)
	{
	    // NOOP
	}
    
    @Override
    public void afterPropertiesSet() throws Exception {
		lecmServicesRegistry.register(this);
		
    	if (folders != null) {
	    	for (Entry<String, String> entry : folders.entrySet()) {
				String relativePath = entry.getValue();
				final ServiceFolder serviceFolder = new ServiceFolder(relativePath, null);
				serviceFolders.put(entry.getKey(), serviceFolder);
			}
    	}
    }

    /**
     * проверяет что объект имеет подходящий тип
     *
     * @param ref
     * @param types
     * @return
     */
    public boolean isProperType(NodeRef ref, Set<QName> types) {
        if (ref != null) {
            final QName type = nodeService.getType(ref);
            return types.contains(type);
        }
        return false;
    }

    public boolean isProperType(NodeRef ref, QName... types) {
        if (ref == null || types == null || types.length == 0) {
            return false;
        }
        final Set<QName> typeSet = new HashSet<QName>();
        Collections.addAll(typeSet, types);
        return isProperType(ref, typeSet);
    }

    /**
     * Проверка элемента на архивность
     *
     * @param ref Ссылка на элемент
     * @return true - если элемент архивный, иначе false
     */
    public boolean isArchive(NodeRef ref) {
        boolean isArchive = StoreRef.STORE_REF_ARCHIVE_SPACESSTORE.equals(ref.getStoreRef());
        if (isArchive) {
            return true;
        } else {
            Boolean isActive = (Boolean) nodeService.getProperty(ref, IS_ACTIVE);
            return isActive != null && !isActive;
        }
    }

    /**
     * Проверка является ли текущий пользователь автором узла
     *
     * @param ref Ссылка на элемент
     * @return true - если элемент архивный, иначе false
     */
    public boolean isOwnNode(NodeRef ref) {
        return ref != null && nodeService.exists(ref)
                && nodeService.getProperty(ref, ContentModel.PROP_CREATOR).equals(authService.getCurrentUserName());
    }

    /**
     * получение связанной ноды по ассоциации. Для типа связи 1:1, 1:0, 0:1
     *
     * @param nodeRef исходная нода
     * @param assocTypeName имя типа ассоциации
     * @param typeName имя типа данных который завязан на ассоциацию
     * @param type направление ассоциации source или target
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
     * @param nodeRef исходная нода
     * @param assocTypeName имя типа ассоциации
     * @param typeName имя типа данных который завязан на ассоциацию
     * @param type направление ассоциации source или target
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
                if (typeName != null) {
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

    public NodeRef getFolder(NodeRef root, List<String> directoryPaths) {
        return getFolder(NamespaceService.CONTENT_MODEL_1_0_URI, root, directoryPaths);
    }

    /**
     * Метод, возвращающий ссылку на директорию согласно заданным параметрам
     *
     * @param nameSpace - name space для сохранения
     * @param root - корень, относительно которого строится путь
     * @param directoryPaths - список папок
     * @return ссылка на директорию
     */
    //TODO DONE refactoring in progress...
    // Выделен метод создания путей. методы типа get теперь только возвращают найденный путь, иначе null
    public NodeRef getFolder(String nameSpace, NodeRef root, List<String> directoryPaths) {
        NodeRef directoryRef = root;
        for (String pathString : directoryPaths) {
            NodeRef pathDir = getFolder(directoryRef, pathString);
            if (pathDir == null) {
                return null;
            } else {
                directoryRef = pathDir;
            }
        }
        return directoryRef;
    }

    //TODO DONE refactoring in progress...
    /**
     * Создаёт папку по указанному пути.
     *
     * @param nameSpace
     * @param root - корневой узел пути
     * @param directoryPaths - путь
     * @return
     * @throws WriteTransactionNeededException
     */
    public NodeRef createPath(String nameSpace, NodeRef root, List<String> directoryPaths) throws WriteTransactionNeededException {
        try {
            lecmTransactionHelper.checkTransaction(false);
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create path \"" + StringUtils.join(directoryPaths.toArray(), "/") + "\" in " + root.toString());
        }
            NodeRef directoryRef = root;
            for (String pathString : directoryPaths) {
                NodeRef pathDir = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
                if (pathDir == null) {
                    QName assocQName = QName.createQName(nameSpace, pathString);
                    Map<QName, Serializable> properties = new HashMap<>(1);
                    properties.put(ContentModel.PROP_NAME, pathString);
                    try {
                        directoryRef = nodeService.createNode(directoryRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
                    } catch (DuplicateChildNodeNameException e) {
                        //есть вероятность, что папка создана другим потоком/транзакцией
                        directoryRef = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
                    }
                } else {
                    directoryRef = pathDir;
                }
            }
            return directoryRef;
        }

    /**
     * Создаёт папку по указанному пути.
     *
     * @param root - корневой узел пути
     * @param directoryPaths - путь
     * @return
     * @throws WriteTransactionNeededException
     */
    public NodeRef createPath(NodeRef root, List<String> directoryPaths) throws WriteTransactionNeededException {
        return createPath(NamespaceService.CONTENT_MODEL_1_0_URI, root, directoryPaths);
    }

    /**
     * Папки создаются относительно папки Home
     *
     * @param folders карта с папками которые мы хотим создать
     */
    public void setFolders(final Map<String, String> folders) {
        this.folders = folders;
    }

    /**
     * получение созданной папки
     *
     * @param folderId
     * @return
     * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
     */
    public NodeRef getFolder(final String folderId) {
        final ServiceFolderStructureHelper serviceFolderStructureHelper = (ServiceFolderStructureHelper) repositoryStructureHelper;
        return serviceFolderStructureHelper.getFolderRef(serviceFolders.get(folderId));
    }

    /**
     * Оборачиваем узел в ссылку html страницы
     *
     * @param nodeRef
     * @param description
     * @param linkUrl
     * @return
     */
    public String wrapperLink(NodeRef nodeRef, String description, String linkUrl) {
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        return "<a href=\"" + serverUrl + linkUrl + "?nodeRef=" + nodeRef + "\">"
                + description + "</a>";
    }

    /**
     * Каждый сервис живет в каталоге /app:company_home/cm:Business
     * platform/cm:LECM/имя_сервиса Данный сервис должен возвращать NodeRef на
     * этот каталог сервиса.
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
     * @throws ru.it.lecm.base.beans.WriteTransactionNeededException
     */
    //TODO DONE Refactoring in progress...
    protected NodeRef createFolder(NodeRef parentRef, String folder) throws WriteTransactionNeededException {
		return repositoryStructureHelper.createFolder(parentRef, folder);
    }

    /**
     * получаем папку у указанного родителя
     *
     * @param parentRef ссылка на родителя
     * @param folder имя папки без слешей и прочей ерунды
     * @return NodeRef если папка есть, null в противном случае
     */
    protected NodeRef getFolder(NodeRef parentRef, String folder) {
        return repositoryStructureHelper.getFolder(parentRef, folder);
    }


    //TODO DONE refactoring in progress...
    public NodeRef createNode(final NodeRef rootFolder, final QName type, final String name, final Map<QName, Serializable> properties) throws WriteTransactionNeededException {
        try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create node");
        }
        QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
        QName assocQName;
        if (name != null) {
            assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
        } else {
            assocQName = generateRandomQName();
        }

        Map<QName, Serializable> props = properties;
        if (props == null) {
            props = new HashMap<QName, Serializable>();
        }
        if (props.get(ContentModel.PROP_NAME) == null && name != null) {
            props.put(ContentModel.PROP_NAME, name);
        }

        ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, type, props);
        return associationRef.getChildRef();

    }

    /**
     * Скрывает указанную ноду. Нода перестает отрисовываться в репозитории и
     * индексироваться. При применении к папке на вложенные ноды НЕ ДЕЙСТВУЕТ
     *
     * @param nodeRef идентификатор ноды, которую требуется скрыть
     * @param disableNodeIndex
     */
    protected void hideNode(NodeRef nodeRef, boolean disableNodeIndex) {
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_VISIBILITY_MASK, 0);
        nodeService.addAspect(nodeRef, ContentModel.ASPECT_HIDDEN, props);
        if (disableNodeIndex) {
            disableNodeIndex(nodeRef);
        }
    }

    /**
     * Отключить индексирование ноды. При применении к папке на вложенные ноды
     * НЕ ДЕЙСТВУЕТ
     *
     * @param nodeRef идентификатор ноды
     */
    protected void disableNodeIndex(NodeRef nodeRef) {
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
        props.put(ContentModel.PROP_IS_INDEXED, Boolean.FALSE);
        props.put(ContentModel.PROP_IS_CONTENT_INDEXED, Boolean.FALSE);
        nodeService.addAspect(nodeRef, ContentModel.ASPECT_INDEX_CONTROL, props);
    }

	protected QName generateRandomQName() {
		return QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
	}
	
	// "Свой" метод для создания сервисных папок. 
	// Цель - избавиться от цепочки транзакций и ненужных вызовов
	// TODO: Попытаться как-то отрефакторить метод, ибо страшный получился
	private void createServiceFolders() {
		String rootPath = repositoryStructureHelper.getServicesHomePath();
		for (Entry<String, ServiceFolder> serviceFolder : serviceFolders.entrySet()) {
			ServiceFolder folder = serviceFolder.getValue();
			String relativePath = new StringBuilder(rootPath).append('/').append(folder.getRelativePath()).toString();
			String[] folders = StringUtils.split(relativePath, '/');
			NodeRef parent = repository.getCompanyHome();
			
			for (String repoFolder : folders) {
				NodeRef pathDir = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, repoFolder);
				if (pathDir == null) {
					if (StringUtils.isEmpty(repoFolder)) {
						logger.error("Folder name can't be empty. Folder won't be created.");
						break;
					} else {
						QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, repoFolder);
						Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
						properties.put(ContentModel.PROP_NAME, repoFolder);
						ChildAssociationRef childAssoc;
						NodeRef childRef;
						try {
							childAssoc = nodeService.createNode(parent, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
							childRef = childAssoc.getChildRef();
						} catch (DuplicateChildNodeNameException e) {
							//есть вероятность, что папка уже существует или создана другим потоком/транзакцией
							childRef = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, repoFolder);
							logger.debug("!!!!!!!!!!! Получил директорию без создания " + folder, e);
						}
						pathDir = childRef;
					}
				}
				
				parent = pathDir;
			}
		}
	}

	@Override
	public void initService() {
		createServiceFolders();	
		initServiceImpl();
	}
	
	public void initServiceImpl() {
		// DO NOTHING
	};
	
}
