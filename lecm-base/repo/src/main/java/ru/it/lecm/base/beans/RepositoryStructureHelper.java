package ru.it.lecm.base.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.ServiceFolder;

/**
 *
 * @author VLadimir Malygin
 * @since 01.03.2013 11:59:19
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class RepositoryStructureHelper implements IServiceFolderStructureHelper {

	private final static Logger logger = LoggerFactory.getLogger (RepositoryStructureHelper.class);
	private final static char FOLDER_SEPARATOR = '/';

	private static void debug (String format, Object... args) {
		if (logger.isDebugEnabled ()) {
			logger.debug (String.format (format, args));
		}
	}

	private static void trace (String format, Object... args) {
		if (logger.isTraceEnabled ()) {
			logger.trace (String.format (format, args));
		}
	}

	/**
	 * служебный класс для хранения ссылки на папку и флага ее создания
	 */
	private final static class FolderRef {
		private final NodeRef nodeRef;
		private final boolean created;

		public FolderRef (NodeRef nodeRef, boolean created) {
			this.nodeRef = nodeRef;
			this.created = created;
		}

		public NodeRef getNodeRef () {
			return nodeRef;
		}

		public boolean isCreated () {
			return created;
		}

		@Override
		public String toString () {
			return nodeRef.toString ();
		}
	}

	private Repository repository;
	private NodeService nodeService;
	private PermissionService permissionService;
	private PersonService personService;
	private TransactionService transactionService;

	String root;
	String home;
	String documents;
	String drafts;

	public void setRepository (final Repository repository) {
		this.repository = repository;
	}

	public void setNodeService (final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPermissionService (final PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setPersonService (final PersonService personService) {
		this.personService = personService;
	}

	public void setTransactionService (TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * здесь происходит создание корневой папке и нарезание прав
	 * иерархия следующая
	 * root
	 */
	public final void init () {
		debug ("initializing RepositoryStructureHelper and creating default folders...");
		PropertyCheck.mandatory (this, "repository", repository);
		PropertyCheck.mandatory (this, "nodeService", nodeService);
		PropertyCheck.mandatory (this, "permissionService", permissionService);
		PropertyCheck.mandatory (this, "personService", personService);
		PropertyCheck.mandatory (this, "root", root);
		PropertyCheck.mandatory (this, "home", home);
		PropertyCheck.mandatory (this, "documents", documents);
		PropertyCheck.mandatory (this, "drafts", drafts);
		repository.init ();
		debug ("Root directory is %s. It's noderef is %s", root, getRootRef ());
		debug ("Home directory is %s. It's noderef is %s", home, getHomeRef ());
		debug ("Documents directory is %s. It's noderef is %s", documents, getDocumentsRef ());
	}

	/**
	 * проверяет что объект имеет подходящий тип
	 */
	private boolean isProperType (final NodeRef ref, final Set<QName> types) {
		if (ref != null) {
			return types.contains (nodeService.getType (ref));
		}
		return false;
	}

	/**
	 * проверяет что объект имеет подходящий тип
	 */
	private boolean isProperType (final NodeRef ref, final QName... types) {
		if (ref == null || types == null || types.length == 0) {
			return false;
		}
		final Set<QName> typeSet = new HashSet<QName> ();
		Collections.addAll (typeSet, types);
		return isProperType (ref, typeSet);
	}

	/**
	 * получаем или создаем папку у указанного родителя
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 */
	private FolderRef getOrCreateFolder (final NodeRef parentRef, final String folder) {
		ParameterCheck.mandatory ("parentRef", parentRef);
		ParameterCheck.mandatory ("folder", folder);
		//проверяем а есть ли такая папка
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs (parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		FolderRef folderRef = null;
		if (childAssocs != null) {
			for (ChildAssociationRef childAssoc : childAssocs) {
				NodeRef childRef = childAssoc.getChildRef ();
				String name = (String) nodeService.getProperty (childRef, ContentModel.PROP_NAME);
				if (folder.equals (name)) {
					folderRef = new FolderRef (childRef, false);
					break;
				}
			}
		}
		if (folderRef == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			folderRef = transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<FolderRef> () {
				@Override
				public FolderRef execute () throws Throwable {
					QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, folder);
					Map<QName, Serializable> properties = new HashMap<QName, Serializable> ();
					properties.put (ContentModel.PROP_NAME, folder);
					ChildAssociationRef childAssoc = nodeService.createNode (parentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
					return new FolderRef (childAssoc.getChildRef (), true);
				}
			});
			trace ("NodeRef %s was sucessfully created for %s folder", folderRef, folder);
		} else {
			trace ("Folder %s already exists, it's noderef is %s", folder, folderRef);
		}
		return folderRef;
	}

	/**
	 * задаем имя корневой папки у которой будут отобраны все права
	 * иерархия: companyHome/root
	 * @param root
	 */
	public void setRoot (final String root) {
		this.root = root;
	}

	private NodeRef getRootRef () {
		final FolderRef rootRef = getOrCreateFolder (repository.getCompanyHome (), root);
		trace ("Root directory is %s. It's noderef is %s", root, rootRef);
		if (rootRef.isCreated ()) {
			//отбираем права у папки lecmRoot
			trace ("Try to modify root folder permissions...");
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
				@Override
				public Void execute () throws Throwable {
					permissionService.clearPermission (rootRef.getNodeRef (), PermissionService.ALL_AUTHORITIES);
					permissionService.setInheritParentPermissions (rootRef.getNodeRef (), false);
					return null;
				}
			});
			trace ("Root folder has no more permissions");
		}
		return rootRef.getNodeRef ();
	}

	/**
	 * задаем имя корневой папки с полными правами, по сути она будет являться
	 * настоящим корнем, в ней будут храниться папки по модулям
	 * иерархия: companyHome/root/home
	 * @param home
	 */
	public void setHome (final String home) {
		this.home = home;
	}

	@Override
	public NodeRef getHomeRef () {
		final FolderRef homeRef = getOrCreateFolder (getRootRef (), home);
		trace ("Home directory is %s. It's noderef is %s", home, homeRef);
		//для папки home выдаем полные права
		if (homeRef.isCreated ()) {
			trace ("Try to modify home folder permissions...");
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			transactionHelper.doInTransaction (new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
				@Override
				public Void execute () throws Throwable {
					permissionService.setPermission (homeRef.getNodeRef (), PermissionService.ALL_AUTHORITIES, "Collaborator", true);
					permissionService.setInheritParentPermissions (homeRef.getNodeRef (), false);
					return null;
				}
			});
			trace ("Home folder has %s permissions", "Collaborator");
		}
		return homeRef.getNodeRef ();
	}

	/**
	 * задаем имя для корневой папки documents, в которой будут храниться документы
	 * гуляющие по workflow
	 * иерархия: companyHome/root/documents
	 * @param documents
	 */
	public void setDocuments (final String documents) {
		this.documents = documents;
	}

	@Override
	public NodeRef getDocumentsRef () {
		NodeRef documentsRef = getOrCreateFolder (getRootRef (), documents).getNodeRef ();
		trace ("Documents directory is %s. It's noderef is %s", documents, documentsRef);
		//права не нарезаем, они по-умолчанию наследуют права root
		return documentsRef;
	}

	/**
	 * задаем имя корневой папки "черновики", в которой будут храниться документы созданные конкретным пользователем
	 * иерархия: companyHome/User Homes/%username%/черновики
	 * @param drafts
	 */
	public void setDrafts (final String drafts) {
		this.drafts = drafts;
	}

	@Override
	public NodeRef getDraftsRef (final String username) {
		ParameterCheck.mandatory ("username", username);
		return getDraftsRef (personService.getPerson (username, false));
	}

	@Override
	public NodeRef getDraftsRef (final NodeRef personRef) {
		ParameterCheck.mandatory ("personRef", personRef);
		NodeRef draftsRef;
		if (isProperType (personRef, ContentModel.TYPE_PERSON)) {
			NodeRef personHome = repository.getUserHome (personRef);
			draftsRef = getOrCreateFolder (personHome, drafts).getNodeRef ();
			trace ("Person drafts directory is %s. It's noderef is %s", drafts, draftsRef);
		} else {
			QName nodeType = nodeService.getType (personRef);
			logger.error (String.format ("NodeRef {%s}%s is not a %s and can't have home directory", nodeType, personRef, ContentModel.TYPE_PERSON.toPrefixString ()));
			draftsRef = null;
		}
		//права не нарезаем, потому что права по-умолчанию нас устраивают
		return draftsRef;
	}

	@Override
	public NodeRef getFolderRef (final ServiceFolder serviceFolder) {
		NodeRef candidateRef = serviceFolder.getFolderRef ();
		NodeRef folderRef = null;
		if (candidateRef != null && nodeService.exists (candidateRef)) {
			folderRef = candidateRef;
		} else {
			String relativePath = serviceFolder.getRelativePath ();
			String[] folders = StringUtils.split (relativePath, FOLDER_SEPARATOR);
			NodeRef parentRef = getHomeRef ();
			for (String folder : folders) {
				if (StringUtils.isEmpty (folder)) {
					logger.error ("Folder name can't be empty. Folder won't be created.");
				} else {
					folderRef = getOrCreateFolder (parentRef, folder).getNodeRef ();
					parentRef = folderRef;
				}
			}
		}
		return folderRef;
	}
}
