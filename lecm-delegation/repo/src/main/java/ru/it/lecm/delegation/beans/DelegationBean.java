package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.utils.Utils;

public class DelegationBean
		extends BaseProcessorExtension
		implements IDelegation
{

	// Namespace URI of delegations model structure
	public static final String NSURI_DELEGATIONS = "http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0"; // prefix="lecm-ba"
	public static final String URI_DATA_PROCURACY = "lecm/delegation/"; // "lecm/business/authority/delegation/"
	public static final String TYPE_PROCURACY = "lecm-ba:procuracy";
	public static final String ASSOCNAME_PROCURACY = "PROCURACY";
	public static final String STATUS_ACTIVE = "Active";

	/*
	public static final String JSON_TITLE = "title";
	public static final String JSON_ISLEAF = "isLeaf";
	public static final String JSON_TYPE = "type"; 

	public static final String JSON_NODEREF = "nodeRef";
	public static final String JSON_DSURI = "dsUri";
	 */

	// public static final String x = "";

	/**
	 * Корневой узел доверенностей внутри организации
	 */
	final static public String NODE_DEFAULT_DELEGATIONS_ROOT = "DELEGATIONS_ROOT";


	/**
	 * Свойства Доверенности
	 */
	final public static QName NTYPE_PROCURACY = QName.createQName(NSURI_DELEGATIONS, TYPE_PROCURACY);

	/** datetime, Время начала действия */
	final static public QName PROP_DATEBEGIN = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:dateUTCBegin");

	/** datetime, Время окончания действия */
	final static public QName PROP_DATEEND = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:dateUTCEnd");

	/** datetime, Время прекращения */
	final static public QName PROP_DATEREVOKE = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:dateUTCRevoke");

	/** datetime, Комментарий и детали */
	final static public QName PROP_COMMENT = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:comment");

	/** boolean, Флаг возможности передачи доверенности */
	final static public QName PROP_CANPROPOGATE = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:canpropogate");

	/** boolean, Флаг для разрешения завершить доверенные задачи после отзыва Доверенности */
	final static public QName PROP_CANPOSTPROCESS = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:canpostprocess");

	/** text, Состояние Доверенности */
	final static public QName PROP_STATUS = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:status");

	/*
	final static public QName PROP_ = QName.createQName(NSURI_DELEGATIONS, "");
	 */

	/**
	 * Ссылки Доверенности
	 */
	final static public QName ASSOC_FROM_EMPLOYEE = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:fromEmployee"); // (1-1) Ссылки "От кого"
	final static public QName ASSOC_TO_EMPLOYEE = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:toEmployee"); // (1-1) Ссылки "Кому"
	final static public QName ASSOC_GIVE_ROLES = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:delegateRoles"); //  (1-M) Список делегируемых бизнес-ролей
	final static public QName ASSOC_PARENT_PROCURACY = QName.createQName(NSURI_DELEGATIONS, "lecm-ba:parentProcuracy"); // (1-M) Ссылка на основную родительскую доверенность (для доверенностей второго уровня)

	/*
	 * props
	 */
	private static Log logger = LogFactory.getLog(DelegationBean.class);
	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;
	private TransactionService transactionService;


	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public Repository getRepositoryHelper() {
		return this.repositoryHelper;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public TransactionService getTransactionService() {
		return this.transactionService;
	}

	/**
	 * Получение корневого узла (в Компании), в котором хрянятся все доверенности.
	 * Если такой узел отсутствует - он создаётся. 
	 * @param rootName название узла, если null, то используется по-умолчанию NODE_DEFAULT_DELEGATIONS_ROOT. 
	 * @return
	 */
	private NodeRef ensureDelegationsRootRef(String rootName) {
		if (rootName == null)
			rootName = NODE_DEFAULT_DELEGATIONS_ROOT;
		final NodeService nodeService = serviceRegistry.getNodeService();

		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();

		// массив, чтобы проще было использовать изнутри doInTransaction ...
		final NodeRef[] dictionariesRoot = { nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, rootName) };
		if (dictionariesRoot[0] == null) {
			// создание корневого узла для делегирований в Компании ...
			final Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
			properties.put(ContentModel.PROP_NAME, rootName);
			transactionService.getRetryingTransactionHelper().doInTransaction( new RetryingTransactionHelper.RetryingTransactionCallback<Object>() 
			{
				@Override
				public Object execute() throws Throwable {
					final ChildAssociationRef associationRef = nodeService.createNode(companyHome, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS, ContentModel.TYPE_FOLDER, properties);
					dictionariesRoot[0] = associationRef.getChildRef();
					return "ok";
				}
			});
		}
		return dictionariesRoot[0];
	}

	/*
	@Override
	public String getExtensionName() {
		return super.getExtensionName();
	}

	@Override
	public void register() {
		super.register();
	}
	 */

	/*
	static JSONObject makeJsonObj(
			final NodeService nodeService
			, final String qTypeLocalName
			, final NodeRef node
	) throws JSONException {
		final JSONObject result = new JSONObject();

		result.put(JSON_TITLE, Utils.getElementName(nodeService, node));
		result.put(JSON_NODEREF, node.toString());
		result.put(JSON_TYPE, qTypeLocalName);

		result.put(JSON_ISLEAF, false);
		result.put(JSON_DSURI, URI_DATA_PROCURACY);
		// root.put(JSON_CHILDTYPE, xxx);

		return result;
	}
	 */

	@Override
	public JSONObject getProcuracy(String procuracyId) {
		return getProcuracy(NODE_DEFAULT_DELEGATIONS_ROOT, procuracyId);
	}


	/**
	 * Получить ссылку на узел по id узла
	 * @param procuracyid
	 * @return
	 */
	static NodeRef makeFullRef(String procuracyid) {
		return new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, procuracyid);
	}


	@Override
	public String createProcuracy(JSONObject args) {

		final NodeService nodeService = serviceRegistry.getNodeService();

		final Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		try {
			Utils.setProps(props, NSURI_DELEGATIONS, args);
		} catch (Exception ex) {
			logger.error("setProperties errros: ", ex);
			return null;
		}

		/* (!) Обновление данных */
		final NodeRef rootDelegates = ensureDelegationsRootRef(NODE_DEFAULT_DELEGATIONS_ROOT);
		final ChildAssociationRef ref = nodeService.createNode (
					rootDelegates 
					, ContentModel.ASSOC_CONTAINS
					, QName.createQName (NSURI_DELEGATIONS, ASSOCNAME_PROCURACY)
					, QName.createQName (NSURI_DELEGATIONS, TYPE_PROCURACY)
					, props);
		nodeService.setProperties(ref.getChildRef(), props);

		final String result = ref.getChildRef().getId();
		logger.info("Procuracy created with id "+ result);
		return result;
	}


	/**
	 * Получить указанную доверенность
	 * @param rootName название корневого узла доверенностей
	 * @param procuracyId id Доверенности (id для строки storeType + "//" + storeId + "/" + id); 
	 * @return
	 */
	private JSONObject getProcuracy(String rootName, final String procuracyId) 
	{
		JSONObject result = new JSONObject();

		if (procuracyId == null)
			return result;

		final NodeRef currentRef = makeFullRef(procuracyId);
		final Map<QName, Serializable> props = serviceRegistry.getNodeService().getProperties(currentRef);
		result = makeJson(props);
		return result;
	}


	final static JSONObject makeJson(final Map<QName, Serializable> props) {
		final JSONObject result = new JSONObject();
		if (props != null) {
			for (Map.Entry<QName, Serializable> entry: props.entrySet()) {
				try {
					result.put( entry.getKey().getLocalName(), entry.getValue());
				} catch (JSONException ex) {
					logger.error("Problem loading procuracy node ", ex);
				}
			}
		}
		return result;
	}


	final static String escape(String value) {
		return (value == null) 
					? null
					: value
						.replaceAll("\"", "\\\"") // замена '"' на '\"'
						.replaceAll("\\", "\\\\") // замена '\' на '\\'
					;
	}


	@Override
	public JSONArray findProcuracyList(JSONObject searchArgs) {
		/* параметры Lucene поиска */
		final SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);

		final StringBuilder sbQuery = new StringBuilder("+TYPE:\"{"+ NSURI_DELEGATIONS+ "}content\"");

		/* добавление условий поиска */
		if (searchArgs != null) {
			for (String key: JSONObject.getNames(searchArgs)) {
				try {
					final Object value = searchArgs.get( key);
					if (value == null) continue;

					// example: +TYPE:"sys:base" -@test\:two:"mustnotmatch"
					// example: @test\:one:"maymatch" OR @test\:two:"maymatch"
					// example: @test\:one:"mustmatch" AND NOT @test\:two:"mustnotmatch"
					sbQuery.append(" AND @").append(escape(key)).append(":\"").append(escape(value.toString())).append("\"");
				} catch (JSONException ex) {
					logger.error(ex);
				}
			}
		}
		sp.setQuery(sbQuery.toString());

		/* поиск */
		final JSONArray result = new JSONArray();
		ResultSet found = null;
		try {
			found = serviceRegistry.getSearchService().query(sp);
			for(ResultSetRow row : found) {
				final Map<QName, Serializable> props = serviceRegistry.getNodeService().getProperties(row.getNodeRef());
				final JSONObject obj = makeJson(props);
				result.put(obj);
			}
		} finally {
			if(found != null)
				found.close();
		}

/*
		try {
		} catch (JSONException ex) {
			logger.error(ex);
		}
 */
		return result;
	}


	@Override
	public void updateProcuracy(final String procuracyId, final JSONObject args) {
		transactionService.getRetryingTransactionHelper ().doInTransaction (
				new RetryingTransactionHelper.RetryingTransactionCallback<Object> () {
					@Override
					public Object execute() throws Throwable {
						internalUpdateProcuracy(procuracyId, args);
						return "ok";
					}
				});
	}

	private void internalUpdateProcuracy(final String procuracyId, final JSONObject args) {
		// if (procuracyId == null) throw new InvalidActivityException("No UID for node");

		/*
		final JSONArray curData  = new JSONArray( getProcuracy(procuracyId) );
		if (isEquals( curData, args)) {
			logger.warn( String.format( "node %s has no changes -> no action performed", procuracyId));
			return;
		}
		 */

		final NodeService nodeService = serviceRegistry.getNodeService();
		final LockService lockService = serviceRegistry.getLockService();

		final NodeRef currentRef = makeFullRef(procuracyId);
		// lockService.checkForLock(currentRef);
		lockService.lock(currentRef, LockType.WRITE_LOCK); // блокировка узла перед обновлением
		try {

			// текущие значения свойств ...
			final Map<QName, Serializable> props = serviceRegistry.getNodeService().getProperties( currentRef);

			// TODO: если изменений не было (свойства + ссылки assoc) -> выходим
			/*
			if (isEquals( props, args)) {
				logger.warn( String.format( "node %s has no changes -> no action performed", procuracyId));
				return;
			}
			 */

			// отзываем прошлые права ...
			revokeAccessRights(currentRef);
			logger.debug( "pre-update: grants revoked from the node id=" + currentRef.getId());

			// обновление данных - замена свойств по именам ... 
			/*
			final Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			// для свойств объекта явно задаём новые ...
			props.put( ContentModel.PROP_NAME, args.getString("NAME") );

			props.put( PROP_DATEBEGIN, getArgsDate( args, PROP_DATEEND.getLocalName()));
			props.put( PROP_DATEEND, getArgsDate( args, PROP_DATEEND.getLocalName()));
			// props.put( PROP_DATEREVOKE, getArgsDate( args, PROP_DATEREVOKE.getLocalName()));

			props.put( PROP_COMMENT, args.getString(PROP_COMMENT.getLocalName() ) );
			props.put( PROP_CANPROPOGATE, args.getBoolean( PROP_CANPROPOGATE.getLocalName()));
			props.put( PROP_CANPOSTPROCESS, args.getBoolean( PROP_CANPOSTPROCESS.getLocalName()));

			props.put( PROP_STATUS, args.getString( PROP_STATUS.getLocalName()));
			 */
			Utils.setProps(props, NSURI_DELEGATIONS, args);

			/* (!) Обновление данных */
			nodeService.setProperties(currentRef, props);
			// nodeService.setAssociations(arg0, arg1, arg2)
			// nodeService.removeAssociation(arg0, arg1, arg2)
			// nodeService.getAssoc(Long)
			// nodeService.getSourceAssocs(arg0, arg1);
			logger.debug( "update: properties updated for node id=" + currentRef.getId());

			/* ообновление ассоциаций ... */
			updateAcccossiations( ASSOC_FROM_EMPLOYEE, currentRef, args);
			updateAcccossiations( ASSOC_TO_EMPLOYEE, currentRef, args);
			updateAcccossiations( ASSOC_GIVE_ROLES, currentRef, args);
			updateAcccossiations( ASSOC_PARENT_PROCURACY, currentRef, args);

			/* (!) выдаём новые права если статус активный ... */
			final Serializable status = (props.containsKey(PROP_STATUS)) ? props.get(PROP_STATUS) : null;
			if (STATUS_ACTIVE.equals(status)) {
				logger.debug( "post-update: granting permissons for the node id=" + currentRef.getId());
				grantAccessRights(currentRef);
				logger.debug( "post-update: granted permissons for the node id=" + currentRef.getId());
			} else {
				logger.warn( "post-update: status is "+ status+" -> NO permissons granted for the node id=" + currentRef.getId());
			}

			logger.info( "node updated successfully id=" + currentRef.getId());
		} catch (JSONException ex) {
			logger.error(ex);
		} finally  {
			lockService.unlock(currentRef);
		}
	}


/*
	@Override
	public void setStatusProcuracy(String procuracyId, DelegationStatus status) {
		// TODO Auto-generated method stub
		final NodeService nodeService = serviceRegistry.getNodeService();
	}
 */

	/**
	 * Обновить список ассоциаций
	 * @param assocTypeName
	 * @param currentRef
	 * @param args
	 * @throws JSONException 
	 */
	private void updateAcccossiations(QName assocTypeName,
			NodeRef nodeRef, JSONObject args) throws JSONException {
		final Object jlist = args.get(assocTypeName.getLocalName());
		if (jlist == null)
			return;
		final List<String> ids = new ArrayList<String>();
		if (jlist instanceof Collection) {
			ids.addAll( (Collection<String>)jlist );
		} else
			ids.add(jlist.toString());
		updateAcccossiations(assocTypeName, nodeRef, ids);
	}

	static final Serializable getArgsDate(JSONObject args, String propName)
			throws JSONException {
		return getArgsDate(args, propName, null);
	}

	static final Serializable getArgsDate(JSONObject args, String propName
			, Date dtDefault
		) throws JSONException
	{
		return (args == null || !args.has(propName)) ? dtDefault : (Date) args.get(propName);
	}

	@Override
	public void deleteProcuracy(String procuracyId) {
		if (procuracyId == null)
			return;
		final NodeService nodeService = serviceRegistry.getNodeService();
		nodeService.deleteNode( new NodeRef(procuracyId));
	}


	/**
	 * 
	 * @param date проверяемая дата
	 * @return true, если date это рабочий день, false иначе.
	 */
	protected boolean isWorkDay(Date date) {
		// TODO: Использование нормального бизнес-календаря
		return true;
	}


	/**
	 * Раздать права согласно текущему состоянию указанной Доверенности
	 * @param procuracyRef
	 */
	protected void grantAccessRights(NodeRef procuracyRef) {
		// TODO: прописать права на бизнес роли ...
		logger.warn( String.format( "NOT YET IMPLEMENTED: granting access rights by procuracy %s", procuracyRef));
	}

	/**
	 * Отобрать права согласно текущему состоянию указанной Доверенности
	 * @param procuracyRef
	 */
	protected void revokeAccessRights(NodeRef procuracyRef) {
		// TODO: прописать права на бизнес роли ...
		logger.warn( String.format( "NOT YET IMPLEMENTED: revoking access rights by procuracy %s", procuracyRef));
	}


	// assocId => assocIdName
	protected void updateAcccossiations(QName assocTypeName, final NodeRef srcParentRef,
			List<String> newDstIds) 
	{
		// TODOL: locks

		// session.nodeService
		final NodeService nodeService = serviceRegistry.getNodeService();

		// получить текущий список связей ...
		final List<ChildAssociationRef> listOld = nodeService.getChildAssocs( srcParentRef, assocTypeName, RegexQNamePattern.MATCH_ALL);
		final List<String> oldIds = Utils.makeIdList(listOld);

		// список на удаление: то что было, а теперь нет ...
		final List<String> toDel = Utils.getDiffer(oldIds, newDstIds);

		// список на вставку: чего не было, но стало ...
		final List<String> toAdd = Utils.getDiffer(newDstIds, oldIds);
		// остальное не трогать )

		// удаление прежних ...
		for (String childId: toDel) {
			final ChildAssociationRef ref = findRefByChildId( listOld, childId);
			nodeService.removeChildAssociation( ref);
			logger.debug( String.format( "node id=%s: reference removed %s", srcParentRef.getId(), ref.getQName(), ref.getChildRef().getId()));
		}

		// создание новых ссылок ...
		for (String childId: toAdd) {
			final NodeRef childRef = makeFullRef( childId);
			final ChildAssociationRef ref = nodeService.addChild( srcParentRef
						, childRef
						, assocTypeName
						, QName.createQName(NSURI_DELEGATIONS, "child"));
			logger.debug( String.format( "node id=%s: reference added %s", srcParentRef.getId(), ref.getQName(), ref.getChildRef().getId()));
			// ChildAssociationRef association = nodeService.createNode( srcParentRef, ContentModel.ASSOC_CONTAINS, assocTypeName, QName.createQName( URI_DATA_PROCURACY, "child"));
		}

		// TODO: check unlock
	}

	static ChildAssociationRef findRefByChildId( Collection<ChildAssociationRef> list, String childId) {
		if (list != null && childId != null)
			for (ChildAssociationRef item: list)
				if (childId.equals(item.getChildRef().getId()))
					return item; // FOUND
		return null; // NOT FOUND
	}
}
