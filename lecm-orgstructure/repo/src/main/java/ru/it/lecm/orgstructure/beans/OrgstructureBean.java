package ru.it.lecm.orgstructure.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;

/**
 * @author dbashmakov
 *         Date: 27.11.12
 *         Time: 17:08
 */
public class OrgstructureBean {

	public static final String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";

	public static final String TYPE_ORGANIZATION = "organization";

	public static final String TYPE_EMPLOYEE = "employee";
	public static final String TYPE_STRUCTURE = "structure";
	public static final String TYPE_WRK_GROUP = "workGroup";
	public static final String TYPE_UNIT = "organization-unit";
	public static final String TYPE_STAFF_LIST = "staff-list";
	public static final String TYPE_POSITION = "staffPosition";
	public static final String TYPE_ROLE = "workRole";

	public static final String TYPE_DIRECTORY_EMPLOYEES = "employees";
	public static final String TYPE_DIRECTORY_STRUCTURE = "structure";
	public static final String TYPE_DIRECTORY_PERSONAL_DATA = "personal-data-container";
	/**
	 * Корневой узел Организации
	 */
	public static final String ORGANIZATION_ROOT_NAME = "Организация";
	public static final String STRUCTURE_ROOT_NAME = "Структура";
	public static final String EMPLOYEES_ROOT_NAME = "Сотрудники";
	public static final String PERSONAL_DATA_ROOT_NAME = "Персональные данные";

	public static final String DICTIONARIES_ROOT_NAME = "Dictionary";

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;

	private final Object lock = new Object();

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * Получение директории Организация.
	 * Если такой узел отсутствует - он НЕ создаётся.
	 *
	 * @return NodeRef
	 */
	public NodeRef getOrganizationRootRef() {
		final NodeService nodeService = serviceRegistry.getNodeService();
		repositoryHelper.init();

		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, ORGANIZATION_ROOT_NAME);
	}

	/**
	 * Получение узла Организация, в котором хрянится информация об Организации.
	 * Если такой узел отсутствует - он создаётся автоматически (внутри /CompanyHome).
	 *
	 * @return NodeRef
	 */
	public NodeRef ensureOrganizationRootRef() {
		final String rootName = ORGANIZATION_ROOT_NAME;
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		final NodeService nodeService = serviceRegistry.getNodeService();
		AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						NodeRef organizationRef;
						synchronized (lock) {
							// еще раз пытаемся получить директорию (на случай если она уже была создана другим потоком
							organizationRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, rootName);
							if (organizationRef == null) {
								QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
								QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
								QName nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_ORGANIZATION);

								Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, rootName);
								ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);

								/**
								 Структура директорий
								 Организация
								 ---Структура
								 ---Сотрудники
								 ---Персональные данные
								 */
								organizationRef = associationRef.getChildRef();
								// Структура
								assocTypeQName = ContentModel.ASSOC_CONTAINS;
								assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, STRUCTURE_ROOT_NAME);
								nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_STRUCTURE);
								properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, STRUCTURE_ROOT_NAME);
								nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, properties);
								// Сотрудники
								assocTypeQName = ContentModel.ASSOC_CONTAINS;
								assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, EMPLOYEES_ROOT_NAME);
								nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_EMPLOYEES);
								properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, EMPLOYEES_ROOT_NAME);
								nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, properties);
								// Персональные данные
								assocTypeQName = ContentModel.ASSOC_CONTAINS;
								assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, PERSONAL_DATA_ROOT_NAME);
								nodeTypeQName = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, TYPE_DIRECTORY_PERSONAL_DATA);
								properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
								properties.put(ContentModel.PROP_NAME, PERSONAL_DATA_ROOT_NAME);
								nodeService.createNode(organizationRef, assocTypeQName, assocQName, nodeTypeQName, properties);
							}
						}
						return organizationRef;
					}
				});
			}
		};
		return AuthenticationUtil.runAsSystem(raw);
	}
}
