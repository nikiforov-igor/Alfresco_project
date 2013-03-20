package ru.it.lecm.base.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.base.ServiceFolder;

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

	/**
	 * карта с папками из декларативного описания бина
	 */
	private Map<String, String> folders;
	/**
	 * карта с папками для конкретного сервиса
	 */
	private final Map<String, ServiceFolder> serviceFolders = new HashMap<String, ServiceFolder> ();

	private ServiceFolderStructureHelper repositoryStructureHelper;
	protected NodeService nodeService;
	protected TransactionService transactionService;

	private final Object lock = new Object();

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

	public void setRepositoryStructureHelper (final ServiceFolderStructureHelper repositoryStructureHelper) {
		this.repositoryStructureHelper = repositoryStructureHelper;
	}

	@Override
	public void afterPropertiesSet () throws Exception {
		//когда все проперти проинициализируются, мы пробежимся по карте с папками и создадим их все
		if (folders != null) {
			for (Entry<String, String> entry : folders.entrySet ()) {
				String relativePath = entry.getValue ();
				ServiceFolder serviceFolder = new ServiceFolder (relativePath, null);
				NodeRef folderRef = repositoryStructureHelper.getFolderRef (serviceFolder);
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
						synchronized (lock) {
							directoryRef = root;
							for (String pathString : directoryPaths) {
								NodeRef pathDir = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
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
						}
						return directoryRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}

	/**
	 * Проверка строки на то, что она является ссылкой
	 * @param ref
	 * @return true - если является ссылкой
	 */
	public boolean isNodeRef(String ref){
		Pattern pattern = Pattern.compile("^[^\\:^ ]+\\:\\/\\/[^\\:^ ]+\\/[^ ]+$");
		Matcher matcher = pattern.matcher(ref);
		return matcher.find();
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
		return repositoryStructureHelper.getFolderRef (serviceFolders.get (folderId));
	}
}
