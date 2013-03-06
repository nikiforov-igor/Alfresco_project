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
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
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
public class RepositoryStructureHelperImpl implements ServiceFolderStructureHelper {

	private final static Logger logger = LoggerFactory.getLogger (RepositoryStructureHelperImpl.class);
	private final static char FOLDER_SEPARATOR = '/';
	private final static String COLLABORATOR = "Collaborator";

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
		logger.debug ("initializing RepositoryStructureHelper and creating default folders...");
		PropertyCheck.mandatory (this, "repository", repository);
		PropertyCheck.mandatory (this, "nodeService", nodeService);
		PropertyCheck.mandatory (this, "permissionService", permissionService);
		PropertyCheck.mandatory (this, "personService", personService);
		PropertyCheck.mandatory (this, "root", root);
		PropertyCheck.mandatory (this, "home", home);
		PropertyCheck.mandatory (this, "documents", documents);
		PropertyCheck.mandatory (this, "drafts", drafts);
		repository.init ();
		logger.debug ("Root directory is {}. It's noderef is {}", root, getRootRef ());
		logger.debug ("Home directory is {}. It's noderef is {}", home, getHomeRef ());
		logger.debug ("Documents directory is {}. It's noderef is {}", documents, getDocumentsRef ());
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
	 * создаем папку у указанного родителя
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef свежесозданной папки
	 */
	private NodeRef createFolder (final NodeRef parentRef, final String folder) {
		ParameterCheck.mandatory ("parentRef", parentRef);
		ParameterCheck.mandatory ("folder", folder);
		NodeRef folderRef = AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {
			@Override
			public NodeRef doWork () throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
				return transactionHelper.doInTransaction (new RetryingTransactionCallback<NodeRef> () {
					@Override
					public NodeRef execute () throws Throwable {
						QName assocQName = QName.createQName (NamespaceService.CONTENT_MODEL_1_0_URI, folder);
						Map<QName, Serializable> properties = new HashMap<QName, Serializable> ();
						properties.put (ContentModel.PROP_NAME, folder);
						ChildAssociationRef childAssoc = nodeService.createNode (parentRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties);
						return childAssoc.getChildRef ();
					}
				});
			}
		});
		logger.trace ("NodeRef {} was sucessfully created for {} folder", folderRef, folder);
		return folderRef;
	}

	/**
	 * получаем папку у указанного родителя
	 * @param parentRef ссылка на родителя
	 * @param folder имя папки без слешей и прочей ерунды
	 * @return NodeRef если папка есть, null в противном случае
	 */
	private NodeRef getFolder (final NodeRef parentRef, final String folder) {
		ParameterCheck.mandatory ("parentRef", parentRef);
		ParameterCheck.mandatory ("folder", folder);
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs (parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		NodeRef folderRef = null;
		if (childAssocs != null) {
			for (ChildAssociationRef childAssoc : childAssocs) {
				NodeRef childRef = childAssoc.getChildRef ();
				String name = (String) nodeService.getProperty (childRef, ContentModel.PROP_NAME);
				if (folder.equals (name)) {
					folderRef = childRef;
					logger.trace ("Folder {} already exists, it's noderef is {}", folder, folderRef);
					break;
				}
			}
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
		NodeRef rootRef = getFolder (repository.getCompanyHome (), root);
		if (rootRef == null) {
			final NodeRef folderRef = createFolder (repository.getCompanyHome (), root);
			//отбираем права у папки lecmRoot
			logger.trace ("Try to modify root folder permissions...");
			rootRef = AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {
				@Override
				public NodeRef doWork () throws Exception {
					RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
					return transactionHelper.doInTransaction (new RetryingTransactionCallback<NodeRef> () {
						@Override
						public NodeRef execute () throws Throwable {
							permissionService.clearPermission (folderRef, PermissionService.ALL_AUTHORITIES);
							permissionService.setInheritParentPermissions (folderRef, false);
							return folderRef;
						}
					});
				}
			});
			logger.trace ("Root folder has no more permissions");
		}
		logger.trace ("Root directory is {}. It's noderef is {}", root, rootRef);
		return rootRef;
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
		NodeRef homeRef = getFolder (getRootRef (), home);
		if (homeRef == null) {
			final NodeRef folderRef = createFolder (getRootRef (), home);
			//для папки home выдаем полные права
			logger.trace ("Try to modify home folder permissions...");
			homeRef = AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {
				@Override
				public NodeRef doWork () throws Exception {
					RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
					return transactionHelper.doInTransaction (new RetryingTransactionCallback<NodeRef> () {
						@Override
						public NodeRef execute () throws Throwable {
							permissionService.setPermission (folderRef, PermissionService.ALL_AUTHORITIES, COLLABORATOR, true);
							permissionService.setInheritParentPermissions (folderRef, false);
							return folderRef;
						}
					});
				}
			});
			logger.trace ("Home folder has {} permissions", COLLABORATOR);
		}
		logger.trace ("Home directory is {}. It's noderef is {}", home, homeRef);
		return homeRef;
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
		NodeRef documentsRef = getFolder (getRootRef (), documents);
		if (documentsRef == null) {
			documentsRef = createFolder (getRootRef (), documents);
		}
		//права не нарезаем, они по-умолчанию наследуют права root
		logger.trace ("Documents directory is {}. It's noderef is {}", documents, documentsRef);
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
			draftsRef = getFolder (personHome, drafts);
			if (draftsRef == null) {
				draftsRef = createFolder (personHome, drafts);
			}
			logger.trace ("Person drafts directory is {}. It's noderef is {}", drafts, draftsRef);
		} else {
			QName nodeType = nodeService.getType (personRef);
			logger.error ("NodeRef [{}]{} is not a {} and can't have home directory", new Object[] {nodeType, personRef, ContentModel.TYPE_PERSON.toPrefixString ()});
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
					folderRef = getFolder (parentRef, folder);
					if (folderRef == null) {
						folderRef = createFolder (parentRef, folder);
					}
					parentRef = folderRef;
				}
			}
		}
		return folderRef;
	}
}
