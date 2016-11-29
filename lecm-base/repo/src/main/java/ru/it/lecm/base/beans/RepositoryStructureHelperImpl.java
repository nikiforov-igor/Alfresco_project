package ru.it.lecm.base.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.ServiceFolder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author VLadimir Malygin
 * @since 01.03.2013 11:59:19
 * @see
 * <p>
 * mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
class RepositoryStructureHelperImpl implements ServiceFolderStructureHelper {

    private final static Logger logger = LoggerFactory.getLogger(RepositoryStructureHelperImpl.class);
    private final static char FOLDER_SEPARATOR = '/';
    private final static String COLLABORATOR = "Collaborator";
    private final static String CONSUMER = "Consumer";
    private final DateFormat FolderNameFormatYear = new SimpleDateFormat("yyyy");
    private final DateFormat FolderNameFormatMonth = new SimpleDateFormat("MM");
    private final DateFormat FolderNameFormatDay = new SimpleDateFormat("dd");

    private static NodeRef rootref = null;
	private static NodeRef homeRef = null;
    
    private Repository repository;
    private NodeService nodeService;
    private PermissionService permissionService;
    private PersonService personService;
    private TransactionService transactionService;
    protected AuthenticationService authService;
    private Repository repositoryHelper;
    private LecmTransactionHelper lecmTransactionHelper;

    String root;
    String home;
    String documents;
    String drafts;
    String usertemp;

    public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
        this.lecmTransactionHelper = lecmTransactionHelper;
    }

    public void setRepository(final Repository repository) {
        this.repository = repository;
    }

    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPermissionService(final PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setPersonService(final PersonService personService) {
        this.personService = personService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    /**
     * здесь происходит создание корневой папке и нарезание прав иерархия
     * следующая root
     */
    public final void init() {
        logger.debug("initializing RepositoryStructureHelper and creating default folders...");
        PropertyCheck.mandatory(this, "repository", repository);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "permissionService", permissionService);
        PropertyCheck.mandatory(this, "personService", personService);
        PropertyCheck.mandatory(this, "root", root);
        PropertyCheck.mandatory(this, "home", home);
        PropertyCheck.mandatory(this, "documents", documents);
        PropertyCheck.mandatory(this, "drafts", drafts);
        //repository.init ();
        //Init-метод. Так что транзакции точно нет, и точно нужна RW.
        //Создаём общесистемные папки. 
//////////////////////////////////////
//        AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {
//
//            @Override
//            public Void doWork() throws Exception {
//                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
//
//                    @Override
//                    public Void execute() throws Throwable {
//////////////////////////////////////
//                        NodeRef rootRef = getRootRef();
//                        if (null == rootRef) {
//                            rootRef = createRootFolder();
//                        }
//                        logger.debug("Root directory is {}. It's noderef is {}", root, rootRef);
//                        
//                        NodeRef homeRef = getHomeRef();
//                        if (null == homeRef) {
//                            homeRef = createHomeRef();
//                        }
//                        logger.debug("Home directory is {}. It's noderef is {}", home, homeRef);
//                        
//                        NodeRef documentsRef = getDocumentsRef();
//                        if (null == documentsRef) {
//                            documentsRef = createFolder(getRootRef(), documents);
//                        }
//                        logger.debug("Documents directory is {}. It's noderef is {}", documents, documentsRef);
//////////////////////////////////////
//                        return null;
//                    }
//
//                });
//                return null;
//            }
//
//        });
//////////////////////////////////////
        
    }

    /**
     * проверяет что объект имеет подходящий тип
     */
    private boolean isProperType(final NodeRef ref, final Set<QName> types) {
        if (ref != null) {
            return types.contains(nodeService.getType(ref));
        }
        return false;
    }

    /**
     * проверяет что объект имеет подходящий тип
     */
    private boolean isProperType(final NodeRef ref, final QName... types) {
        if (ref == null || types == null || types.length == 0) {
            return false;
        }
        final Set<QName> typeSet = new HashSet<QName>();
        Collections.addAll(typeSet, types);
        return isProperType(ref, typeSet);
    }

    /**
     * создаем папку у указанного родителя
     *
     * @param parentRef ссылка на родителя
     * @param folder имя папки без слешей и прочей ерунды
     * @return NodeRef свежесозданной папки
     */
    @Override
    public NodeRef createFolder(final NodeRef parentRef, final String folder) {//throws WriteTransactionNeededException {
        ParameterCheck.mandatory("parentRef", parentRef);
        ParameterCheck.mandatory("folder", folder);
        ParameterCheck.mandatory("transactionService", transactionService);
        ParameterCheck.mandatory("nodeService", nodeService);

//        try {
//            lecmTransactionHelper.checkTransaction(false);
//        } catch (TransactionNeededException ex) {
//            throw new WriteTransactionNeededException("Can't create \"" + folder + "\" in " + parentRef.toString());
//        }
        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, folder);
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, folder);
        ChildAssociationRef childAssoc;
        NodeRef childRef;
        try {
            childAssoc = nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
            childRef = childAssoc.getChildRef();
        } catch (DuplicateChildNodeNameException e) {
            //есть вероятность, что папка уже существует или создана другим потоком/транзакцией
            childRef = nodeService.getChildByName(parentRef, ContentModel.ASSOC_CONTAINS, folder);
            logger.debug("!!!!!!!!!!! Получил директорию без создания " + folder, e);
        }
        return childRef;
    }

    /**
     * получаем папку у указанного родителя
     *
     * @param parentRef ссылка на родителя
     * @param folder имя папки без слешей и прочей ерунды
     * @return NodeRef если папка есть, null в противном случае
     */
    //TODO DONE Refactoring in progress...
    //Не понял, зачем так сложно был сделан поиск папки.
//    private NodeRef getFolder(final NodeRef parentRef, final String folder) {
//        ParameterCheck.mandatory("parentRef", parentRef);
//        ParameterCheck.mandatory("folder", folder);
//        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
//        NodeRef folderRef = null;
//        if (childAssocs != null) {
//            for (ChildAssociationRef childAssoc : childAssocs) {
//                NodeRef childRef = childAssoc.getChildRef();
//                String name = (String) nodeService.getProperty(childRef, ContentModel.PROP_NAME);
//                if (folder.equals(name)) {
//                    folderRef = childRef;
//                    logger.trace("Folder {} already exists, it's noderef is {}", folder, folderRef);
//                    break;
//                }
//            }
//        }
//        return folderRef;
//    }
    @Override
    public NodeRef getFolder(NodeRef parentRef, String folder) {
        ParameterCheck.mandatory("parentRef", parentRef);
        ParameterCheck.mandatory("folder", folder);
        NodeRef folderRef = nodeService.getChildByName(parentRef, ContentModel.ASSOC_CONTAINS, folder);
        return folderRef;
    }

    /**
     * задаем имя корневой папки у которой будут отобраны все права иерархия:
     * companyHome/root
     *
     * @param root
     */
    public void setRoot(final String root) {
        this.root = root;
    }

    /**
     * Создаёт корневую папку. Так как папка нужна всем и всегда, создаёт её
     * тоже система при любом обращении
     *
     * @return
     */
    private NodeRef createRootFolder() {//throws WriteTransactionNeededException {
        logger.trace("Try to create root folder...");
        //Создаём папку
        NodeRef folderRef = createFolder(repository.getCompanyHome(), root);
        //отбираем права у папки lecmRoot
        permissionService.clearPermission(folderRef, PermissionService.ALL_AUTHORITIES);
        permissionService.setInheritParentPermissions(folderRef, false);
        return folderRef;
    }

    //TODO DONE Refactoring in progress....
    private NodeRef getRootRef() {
    	if (rootref==null) {
    		rootref = getFolder(repository.getCompanyHome(), root);
    	}
        return rootref;
    }

    /**
     * задаем имя корневой папки с полными правами, по сути она будет являться
     * настоящим корнем, в ней будут храниться папки по модулям иерархия:
     * companyHome/root/home
     *
     * @param home
     */
    public void setHome(final String home) {
        this.home = home;
    }

    /**
     * Т.к. папка нужна всем и должна быть всегда, выполняем в транзакции.
     *
     * @return
     */
    private NodeRef createHomeRef() {// throws WriteTransactionNeededException {
        NodeRef folderRef = createFolder(getRootRef(), home);
        permissionService.setPermission(folderRef, PermissionService.ALL_AUTHORITIES, CONSUMER, true);
        permissionService.setInheritParentPermissions(folderRef, false);
        return folderRef;
    }

    /**
     * получение ссылки на корневую папку LECM если папки нет, то она создается
     *
     * @return
     */
    @Override
    public NodeRef getHomeRef() {
		if (homeRef == null) {
			homeRef = getFolder(getRootRef(), home);
		}
        return homeRef;
    }

    /**
     * задаем имя для корневой папки documents, в которой будут храниться
     * документы гуляющие по workflow иерархия: companyHome/root/documents
     *
     * @param documents
     */
    public void setDocuments(final String documents) {
        this.documents = documents;
    }

    @Override
    public NodeRef getDocumentsRef() {
    	return createPath(NamespaceService.CONTENT_MODEL_1_0_URI, getRootRef(), Arrays.asList(new String[]{documents}));
        //return getFolder(getRootRef(), documents);
    }

    /**
     * задаем имя корневой папки "черновики", в которой будут храниться
     * документы созданные конкретным пользователем иерархия: companyHome/User
     * Homes/%username%/черновики
     *
     * @param drafts
     */
    public void setDrafts(final String drafts) {
        this.drafts = drafts;
    }

    @Override
    public NodeRef getDraftsRef(final String username) throws WriteTransactionNeededException {
        ParameterCheck.mandatory("username", username);
        return getDraftsRef(personService.getPerson(username, false));
    }

    @Override
    public NodeRef getDraftsRef(final NodeRef personRef) throws WriteTransactionNeededException {
        ParameterCheck.mandatory("personRef", personRef);
        NodeRef draftsRef;
        if (isProperType(personRef, ContentModel.TYPE_PERSON)) {
            final NodeRef personHome = repository.getUserHome(personRef);
            draftsRef = getFolder(personHome, drafts);
            if (draftsRef == null) {
//                Возникла проблема, что методы, требующие корневой папки черновиков в итоге проваливаются сюда
//                В некоторых случаях без транзакции, поэтому пока оберну прямо здесь.
//                TODO: Найти место, где бы гарантированно создавать папку черновиков для пользователя и создавать там.
//                может быть при создании пользователя?
//                RetryingTransactionCallback<NodeRef> cb = new RetryingTransactionCallback<NodeRef>() {
//
//                    @Override
//                    public NodeRef execute() throws Throwable {
            	draftsRef = createFolder(personHome, drafts);
//                    }
//
//                };
//              draftsRef = createFolder(personHome, drafts);
//                draftsRef = lecmTransactionHelper.doInTransaction(cb, false);
            }
            logger.trace("Person drafts directory is {}. It's noderef is {}", drafts, draftsRef);
        } else {
            QName nodeType = nodeService.getType(personRef);
            logger.error("NodeRef [{}]{} is not a {} and can't have home directory", new Object[]{nodeType, personRef, ContentModel.TYPE_PERSON.toPrefixString()});
            draftsRef = null;
        }
        //права не нарезаем, потому что права по-умолчанию нас устраивают
        return draftsRef;
    }

    @Override
    public NodeRef getCompanyHomeRef() {
        return repository.getCompanyHome();
    }

    private NodeRef createServiceRef(ServiceFolder serviceFolder) {
            String relativePath = serviceFolder.getRelativePath();
            relativePath = root+FOLDER_SEPARATOR+home+FOLDER_SEPARATOR+relativePath;
            final String[] folders = StringUtils.split(relativePath, FOLDER_SEPARATOR);
            final NodeRef parentRef = repository.getCompanyHome();//getHomeRef();
            NodeRef result = null;
            if (folders.length > 0) {
//                result = AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>() {
//                    @Override
//                    public NodeRef doWork() throws Exception {
//                        return lecmTransactionHelper.doInRWTransaction(new RetryingTransactionCallback<NodeRef>() {
//                            @Override
//                            public NodeRef execute() throws Throwable {
                                return createPath(NamespaceService.CONTENT_MODEL_1_0_URI, parentRef, Arrays.asList(folders));
//                            }
//                        });
//                    }
//                });

            }
//            if (null == result) {
//                logger.error("Can't create service folder \"" + serviceFolder.getRelativePath() + "\"");
//            }
            return result;
    }

    @Override
    //TODO DONE т.к. Данный вариант метода используется для получения папок сервисов, можно создавть папку всем.
    public NodeRef getFolderRef(ServiceFolder serviceFolder) {
        NodeRef candidateRef = serviceFolder.getFolderRef();
        if (candidateRef != null && nodeService.exists(candidateRef)) {
            return candidateRef;
        } else {
            return createServiceRef(serviceFolder);
        }
    }

    //TODO DONE refactoring in progress...
    //Все методы, непосредственно выполняющие действия, вынесены в ru.it.lecm.base.beans.RepositoryStructureHelperImpl
    @Override
    public NodeRef createPath(String nameSpace, NodeRef root, List<String> directoryPaths) {//throws WriteTransactionNeededException {
//        try {
//            lecmTransactionHelper.checkTransaction(false);
//        } catch (TransactionNeededException ex) {
//            throw new WriteTransactionNeededException("Can't create path \"" + StringUtils.join(directoryPaths.toArray(), "/") + "\" in " + root.toString());
//        }
        NodeRef directoryRef = root;
        for (String pathString : directoryPaths) {
            NodeRef pathDir = getFolder(directoryRef, pathString);
            if (pathDir == null) {
                if (StringUtils.isEmpty(pathString)) {
                    logger.error("Folder name can't be empty. Folder won't be created.");
                    return null;
                } else {
                    directoryRef = createFolder(directoryRef, pathString);
                }
            } else {
                directoryRef = pathDir;
            }
        }
        return directoryRef;
    }

    public void setUsertemp(String usertemp) {
        this.usertemp = usertemp;
    }

    @Override
    public NodeRef getUserTemp(boolean createIfNotExist) throws WriteTransactionNeededException {
        return getUserTemp(repositoryHelper.getPerson(), createIfNotExist);
    }

    @Override
    public NodeRef getUserTemp(NodeRef person, boolean createIfNotExist) throws WriteTransactionNeededException {
//		TODO: Метод был типа getOrCreate, надо разделить. Создание вынесено в createUserTemp
        NodeRef userHome = repositoryHelper.getUserHome(person);
        NodeRef userTemp = null;
        if (userHome != null) {
            userTemp = getFolder(userHome, this.usertemp);
    }
        return userTemp;
    }

    @Override
    public NodeRef createUserTemp(NodeRef person) throws WriteTransactionNeededException {
            NodeRef userHome = repositoryHelper.getUserHome(person);
            return createFolder(userHome, this.usertemp);
    }

    @Override
    public NodeRef createUserTemp() throws WriteTransactionNeededException {
            NodeRef userHome = repositoryHelper.getUserHome(repositoryHelper.getPerson());
            return createFolder(userHome, this.usertemp);
    }

    @Override
    public List<String> getDateFolderPath(Date date) {
        List<String> result = new ArrayList<String>();
        result.add(FolderNameFormatYear.format(date));
        result.add(FolderNameFormatMonth.format(date));
        result.add(FolderNameFormatDay.format(date));
        return result;
    }
}
