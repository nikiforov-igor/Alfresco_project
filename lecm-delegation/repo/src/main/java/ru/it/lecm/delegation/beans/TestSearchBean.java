package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.ITestSearch;
import ru.it.lecm.integrotest.ExecutorBean;
import ru.it.lecm.integrotest.ExecutorBean.StepResult;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.events.IOrgStructureNotifiers;
import ru.it.lecm.utils.DurationLogger;
import ru.it.lecm.utils.SearchHelper;
import ru.it.lecm.utils.Utils;

public class TestSearchBean extends AbstractLifecycleBean implements ITestSearch
{
	// Namespace URI of delegations model structure
	// "http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0";
	public static final String PREFIX_PROCURACY = "lecm-d8n"; // "lecm-ba";
	public static final String URI_DATA_PROCURACY = "lecm/delegation/";

//	public static final String TYPE_DELEGATION_OPTS = "delegation-opts";
//	public static final String TYPE_PROCURACY = "procuracy";

	public static final String NAME_PROCURACY = "PROCURACY";
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
//	final public static QName NTYPE_PROCURACY = QName.createQName(NSURI_DELEGATIONS, TYPE_PROCURACY);
//
//	/** datetime, Время начала действия */
//	final static public QName PROP_DATEBEGIN = QName.createQName(NSURI_DELEGATIONS, "dateUTCBegin");
//
//	/** datetime, Время окончания действия */
//	final static public QName PROP_DATEEND = QName.createQName(NSURI_DELEGATIONS, "dateUTCEnd");
//
//	/** datetime, Время прекращения */
//	final static public QName PROP_DATEREVOKE = QName.createQName(NSURI_DELEGATIONS, "dateUTCRevoke");
//
//	/** datetime, Комментарий и детали */
//	final static public QName PROP_COMMENT = QName.createQName(NSURI_DELEGATIONS, "comment");
//
//	/** boolean, Флаг возможности передачи доверенности */
//	final static public QName PROP_CANPROPOGATE = QName.createQName(NSURI_DELEGATIONS, "canpropogate");
//
//	/** boolean, Флаг для разрешения завершить доверенные задачи после отзыва Доверенности */
//	final static public QName PROP_CANPOSTPROCESS = QName.createQName(NSURI_DELEGATIONS, "canpostprocess");
//

	/*
	final static public QName PROP_ = QName.createQName(NSURI_DELEGATIONS, "");
	 */

	private static final String NAMESPACE = "http://www.it.ru/lecm/model/blanks/1.0";
	private static final String TYPENAME = "blank";

	/** название параметра в аргументах args с названием рабочей папки */
	private static final String ARGNAME_FOLDER = "folder";

	/** название по-умолчани для рабочей папки */
	private static final String DEFAULT_FOLDERNAME = "Общая папка";


	final private static Logger logger = LoggerFactory.getLogger (TestSearchBean.class);

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;

	/**
	 * fastNodeService не выполняет проверку прав досту (bean_id="nodeService")
	 * secureNodeService выполняет (bean_id="NodeService")
	 */
	private NodeService fastNodeService;
	private NodeService secureNodeService;
	private NamespaceService namespaceService;

	private AuthorityService authorityService;
	private WorkflowService workflowService;
	private LecmPermissionService lecmPermissionService;

	private JSONObject args;

	@Override
	protected void onBootstrap(ApplicationEvent event) {
		logger.info("started up ");
	}

	@Override
	protected void onShutdown(ApplicationEvent event) {
		logger.info("shutdown");
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public Repository getRepositoryHelper() {
		return repositoryHelper;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public NodeService getFastNodeService() {
		return fastNodeService;
	}

	public void setFastNodeService(NodeService fastNodeService) {
		this.fastNodeService = fastNodeService;
	}

	public NodeService getSecureNodeService() {
		return secureNodeService;
	}

	public void setSecureNodeService(NodeService secureNodeService) {
		this.secureNodeService = secureNodeService;
	}

	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setNamespaceService (NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public NamespaceService getNamespaceService() {
		return namespaceService;
	}

	public LecmPermissionService getLecmPermissionService() {
		return lecmPermissionService;
	}

	public void setLecmPermissionService(LecmPermissionService value) {
		this.lecmPermissionService = value;
	}

	@Override
	public void setConfig(JSONObject config) throws JSONException {
		this.args = config;
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

	/**
	 * Получить ссылку на узел по id узла
	 * @param nodeid
	 * @return
	 */
	static NodeRef makeFullRef(String nodeid) {
		return new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeid);
	}

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

	// org.alfresco.util.ISO8601DateFormat;
	final static DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

	/**
	 * Преобразование в значение, пригодное для json-передачи
	 * Сейчас преобразуются только типы Date в формат ISO8601
	 * @param value
	 * @return
	 */
	static Object convertModelValueIntoJson(Object value) {
		return (value instanceof Date)
				? DateFormatISO8601.format((Date) value)
				: value;
	}

	/**
	 * Получить свойства в json-виде пригодном для отправки в форму ввода
	 * @param props
	 * @param assoclist набор полей-ассоциаций
	 * @return массив вида
				"prop_lecm-ba_dateUTCBegin": {
					"value": sss, //значение свойства
					"displayValue": sss //отображаемое значение свойства
				}
				, ... other properties ...
	 */
	static JSONObject makeJsonPropeties(
			Map<QName, Serializable> props
			, Collection<QName> assoclist
			, NamespaceService nss
			)
	{
		final JSONObject result = new JSONObject();
		if (props != null) {
			for (Map.Entry<QName, Serializable> entry: props.entrySet()) {
				try {
					final JSONObject propObj = new JSONObject();
					final Object value = convertModelValueIntoJson( entry.getValue());
					propObj.put( "value", value);
					propObj.put( "displayValue", value);

					final String prefix = (assoclist != null && assoclist.contains(entry.getKey()))
								? PREFIX_ASSOC : PREFIX_PROP;
					final String propName = normalizePropName( entry.getKey(), prefix, nss);
					result.put( propName, propObj);
				} catch (JSONException ex) {
					logger.error("Problem loading procuracy node ", ex);
				}
			}
		}
		return result;
	}


	private final static String PREFIX_PROP = "prop";
	private final static String PREFIX_ASSOC = "assoc";

	// return example: "prop_lecm-ba_dateUTCBegin", "assoc_lecm-ba_fromEmployee"
	static String normalizePropName(QName key, String prefix, NamespaceService nss) {
		// final Collection<String> prefixes = nss.getPrefixes(key.getNamespaceURI());
		final QName prefixed = key.getPrefixedQName(nss);
		return String.format( "%s_%s_%s", prefix, QName.splitPrefixedQName(prefixed.toPrefixString())[0], key.getLocalName() ) ;
	}


	/*
	final static String escape(final String value) {
		return (value == null)
					? null
					: value
						.replaceAll("\"", "\\\"") // замена '"' на '\"'
						.replaceAll("\\", "\\\\") // замена '\' на '\\'
					;
	}
	 */


	static String quots(final String value) {
		// return StringUtils.quote(value);
		return (value == null) ? null : '"'+ value + '"';
	}

	/**
	 * Сформировать запрос к Доверенностям.
	 * @param dest целевой буфер
	 * @param namespace namespace для поиска
	 * @param typename искомый тип внутри namespace
	 * @param searchArgs список атрибутов и значений для поиска
	 * @param parentNode
	 * @return в буфере формируется текст запроса в виде: TYPE:[доверенность] AND [условия на атрибуты]
	 * пример:
	 * TYPE:"{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}procuracy" AND @{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}canpropogate:"false" AND @{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}canpostprocess:"false"
	 */
	static StringBuilder makeSearchQuery(
				final StringBuilder dest
				, final String namespace
				, final String typename
				, JSONObject searchArgs
				, NodeRef parentNode
	) {
		/* фиксируем тип Доверенностей... */
		dest.append( String.format( "TYPE:\"{%s}%s\"", namespace, typename));

		/* если задан родитель - добавляем ... */
		if (parentNode != null)
			dest.append( String.format( " AND PARENT:\"%s\"", parentNode));

		/* добавление условий поиска по атрибутам, если есть */
		if (searchArgs != null && JSONObject.getNames(searchArgs) != null) {
			for (String key: JSONObject.getNames(searchArgs)) {
				try {
					final Object value = searchArgs.get( key);
					if (value == null) continue;

					// example: +TYPE:"sys:base" -@test\:two:"value_must_not_match"
					// example: @test\:one:"maymatch" OR @test\:two:"may_match"
					// example: @test\:one:"mustmatch" AND NOT @test\:two:"value_must_not_match"
					dest.append(String.format(" AND @%s:%s", key,  quots(value.toString()) ));
				} catch (JSONException ex) {
					logger.error("", ex);
				}
			}
//			org.alfresco.repo.security.permissions.impl.PermissionServiceImpl impl;
//			org.alfresco.repo.security.permissions.impl.acegi.ACLEntryVoter aclVoter;
//			net.sf.acegisecurity.vote.RoleVoter grpVoter;
//			org.alfresco.repo.security.permissions.impl.acegi.MethodSecurityInterceptor metinter;

			/*
			 * нужен воутер, чтобы убирать права (sec-группу) при доступе к
			 * некоторым методам. Логика примерно такая "у текущего пользователя
			 * и документа есть одинаковый атрибут".
			 * Реализовать можнно так: иметь явную sec-group для прользователя,
			 * которую мы будем ассоциировать с подразделением, в документ
			 * включим аспект, который будет относиться к этой группе.
			 */
		}
		return dest;
	}

	/**
	 * Сформировать запрос к Доверенностям.
	 * @param dest целевой буфер
	 * @param searchArgs список атрибутов и значений для поиска
	 * @param parentNode родительский узел для поиска внутри него, если null, то не используется
	 * @return в буфере формируется текст запроса в виде: TYPE:[доверенность] AND [условия на атрибуты]
	 * пример:
	 * TYPE:"{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}procuracy" AND @{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}canpropogate:"false" AND @{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}canpostprocess:"false"
	 */
	static StringBuilder makeSearchQuery4Procuracy(final StringBuilder dest
			, JSONObject searchArgs, NodeRef parentNode) {
		return makeSearchQuery( dest, IDelegation.DELEGATION_NAMESPACE, "procuracy", searchArgs, parentNode);
	}

	// пока непонятная секция
	static private class JsonMetaDataSection extends JSONObject {
		public JsonMetaDataSection(final NodeRef parent)  throws JSONException {
			put( "parent", new JsonParent(parent));
		}
	}

	static class JsonParent extends JSONObject {
		private NodeRef parent;

		public NodeRef getParent() {
			return this.parent;
		}

		public JsonParent(final NodeRef parent) throws JSONException {
			this.parent = parent;
			//ссылка на родительский контейнер в котором лежат элементы ("workspace://SpacesStore/%parentId%")
			// put( "nodeRef", parent.getStoreRef().toString() + "/"+ parent.getId());
			put( "nodeRef", parent);
			put( "permissions", getPermissions(parent) ); // права родительского контейнера
		}

		/**
		 * Получить права на узел
		 * @param node
		 * @return
		 * @throws JSONException
		 */
		static JSONObject getPermissions(final NodeRef node) throws JSONException {
			final JSONObject result = new JSONObject();
			//нас интересует есть ли право создавать детишек в родительском контейнернере
			result.put( "userAccess", makePermission("create", true));
			return result;
		}

		static JSONObject makePermission(String permName, boolean enabled) throws JSONException {
			final JSONObject result = new JSONObject();
			result.put( permName, enabled);
			return result;
		}

	}

	JSONObject makeResult(final JSONObject result, ResultSet foundSet
			, NodeRef parent) throws JSONException
	{
		result.put( "totalRecords", (foundSet == null) ? 0 : foundSet.length() ); //общее кол-во строк
		result.put( "startIndex", 0); //всегда ноль,
		result.put( "metadata", new JsonMetaDataSection(parent));

		result.put( "items", makeItems(foundSet)); // массив с данными (JSONArray) которые мы отображаем в таблице
		return result;
	}

	/**
	 * @param foundSet
	 * @return массив объектов вида:
	 * 	{
			"nodeRef": xxx,//ссылка на элемент (workspace://SpacesStore/%elementId%
			"itemData": { //свойства объета которые мы возвращаем, свойства описываются по принципу prop_префиксМодели_имяСвойства (например prop_lecm-ba_dateUTCBegin)
				"prop_lecm-ba_dateUTCBegin": { //чем value отличается от displayValue я не знаю, в тех примерах что я видел value=displayValueб как-то так
					"value": sss, //значение свойства
					"displayValue": xxx //отображаемое значение свойства
				}
				, ... other properties ...
			}
		}
	 * @throws JSONException
	 */
	JSONArray makeItems(ResultSet foundSet) throws JSONException {
		final JSONArray result = new JSONArray();
		if (foundSet != null) {
			for(ResultSetRow row : foundSet) {
				final Map<QName, Serializable> props = fastNodeService.getProperties(row.getNodeRef());

				final Set<QName> assocs = new HashSet <QName>();
				{	// добавление ассоциаций ...
					final List<AssociationRef> ca = fastNodeService.getTargetAssocs(row.getNodeRef(), RegexQNamePattern.MATCH_ALL);
					if (ca != null)
						for (AssociationRef ref: ca)
							assocs.add(ref.getTypeQName());
				}

				{
					final JSONObject obj = new JSONObject();
					obj.put("nodeRef", row.getNodeRef());
					obj.put("itemData", makeJsonPropeties(props, assocs, namespaceService));

					// TODO: временный хардкод для того чтобы actionset-ы в таблице заработали
					obj.put ("permissions", new JSONObject ("{\"userAccess\":{\"create\": true, \"edit\":true, \"delete\":true}}"));
					result.put(obj);
				}

			}
		}
		return result;
	}

	final private static String ARG_TESTNAME = "testName";

	final private static String PREFIX_TEST = "test";
	public static Integer findTestNum(String s) {
		if (s != null) {
			try {
				// final String[] sN = s.toLowerCase().split(PREFIX_TEST);
				if (s.toLowerCase().startsWith(PREFIX_TEST))
					return Integer.parseInt( s.substring(PREFIX_TEST.length()));
			} catch (NumberFormatException ex) {
				logger.info("test '"+ s + "' is not numbered test -> skipped");
			}
		}
		return null; // not a number
	}

	@Override
	public JSONObject test(JSONObject args) {
		JSONObject result = new JSONObject();

		final DurationLogger d = new DurationLogger();
		try {
			if (args == null) {
				result.put("message", "No arguments");
				return result;
			}

			final String testName = args.optString(ARG_TESTNAME, null);
			if (testName == null) {
				result.put("message", "Argument '"+ ARG_TESTNAME+ "' not specified");
				return result;
			}

			logger.info( "performing test '"+ testName+ "'");
			setConfig(args);
			final Integer n = findTestNum(testName);
			if (n != null) {
				copyJson( result, runTest(n.intValue()));
			} else
				result.put("message", "Argument '"+ ARG_TESTNAME+ "' calls unknown function '"+ testName+ "'");

		} catch(Throwable ex) {
			logger.error( String.format("Exception in args ", args), ex);
			try {
				result.put( "error", ex.toString());
			} catch(JSONException e2) {
				logger.error("double-exception", e2);
			}
			throw new RuntimeException("Fail test", ex);
		} finally {
			final String msg = d.fmtDuration( "{t} msec");
			logger.info( "testTime " + msg);
			try {
				result.put( "testTime", msg);
			} catch(JSONException ex) {
				logger.error("finalization errror", ex);
			}
		}

		return result;
	}

	static void copyJson(JSONObject dest, JSONObject source) throws JSONException {
		if (dest != null && source != null && source.keys() != null)
			for(String key: JSONObject.getNames(source))
				dest.put(key, source.get(key));
			}

/*
	@Override
	public void setStatusProcuracy(String procuracyId, DelegationStatus status) {
		// TODO Auto-generated method stub
		final NodeService nodeService = serviceRegistry.getNodeService();
	}
 */

	static Serializable getArgsDate(JSONObject args, String propName)
			throws JSONException {
		return getArgsDate(args, propName, null);
	}

	static Serializable getArgsDate(JSONObject args, String propName
			, Date dtDefault
		) throws JSONException
	{
		return (args == null || !args.has(propName)) ? dtDefault : (Date) args.get(propName);
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

		// получить текущий список связей ...
		final List<ChildAssociationRef> listOld = fastNodeService.getChildAssocs( srcParentRef, assocTypeName, RegexQNamePattern.MATCH_ALL);
		final List<String> oldIds = Utils.makeIdList(listOld);

		// список на удаление: то что было, а теперь нет ...
		final List<String> toDel = Utils.getDiffer(oldIds, newDstIds);

		// список на вставку: чего не было, но стало ...
		final List<String> toAdd = Utils.getDiffer(newDstIds, oldIds);
		// остальное не трогать )

		// удаление прежних ...
		for (String childId: toDel) {
			final ChildAssociationRef ref = findRefByChildId( listOld, childId);
			fastNodeService.removeChildAssociation( ref);
			logger.debug( String.format( "node id=%s: reference removed %s", srcParentRef.getId(), ref.getQName(), ref.getChildRef().getId()));
		}

		// создание новых ссылок ...
		for (String childId: toAdd) {
			final NodeRef childRef = makeFullRef( childId);
			final ChildAssociationRef ref = fastNodeService.addChild( srcParentRef
						, childRef
						, assocTypeName
						, QName.createQName(IDelegation.DELEGATION_NAMESPACE, "child"));
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

	/*
	public class SecureAccessor
		implements net.sf.acegisecurity.afterinvocation.AfterInvocationProvider
	{
		// net.sf.acegisecurity.afterinvocation.AfterInvocationProvider
		// org.alfresco.repo.security.permissions.dynamic.OwnerDynamicAuthority;

		private static final String AFTER_ACL_NODE = "AFTER_ACL_NODE";
		private static final String AFTER_ACL_PARENT = "AFTER_ACL_PARENT";

		@Override
		public boolean supports(ConfigAttribute attribute)
		{
			return (attribute.getAttribute() != null)
					&& (
						attribute.getAttribute().startsWith(AFTER_ACL_NODE)
						|| attribute.getAttribute().startsWith(AFTER_ACL_PARENT)
					);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean supports(Class clazz)
		{
			return (MethodInvocation.class.isAssignableFrom(clazz));
		}

		@Override
		public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject)
				throws AccessDeniedException
		{
			logger.info("checking object "+ (returnedObject == null ? "NULL" : returnedObject.getClass() + " \n"+ returnedObject.toString()));
			return returnedObject;
		}

	}
	*/


	/**
	 * Выполнить тест с указанным номером
	 * @param testnum
	 * @return
	 * @throws JSONException
	 */
	@Override
	public JSONObject runTest(int testnum) throws JSONException {
		JSONObject result;
		try {
			// org.alfresco.repo.security.permissions.impl.acegi.MethodSecurityInterceptor;
			final boolean needPermChk = (args != null && args.optBoolean("secure", false));
			switch (testnum) {
			case 1:	result = doSearchTest( "New", false, needPermChk); break;
			case 2: result = doSearchTest( "New", true, needPermChk); break;
			case 3: result = doSearchTest( "Active", false, needPermChk); break;
			case 4: result = doSearchTest( "Active", true, needPermChk); break;
			case 5: result = doCreateSGTest(); break;
			case 6: result = doCreateNestedSGTest(); break;
			case 7: result = doOrgStrucNotifyTest(); break;
			case 8: result = doCheckACLTest(); break;
			case 9: result = doIntegroTest(); break; 
			case 10: result = doWorkFlowTest(); break;
			default:
				result = new JSONObject();
				result.put( "error", "invalid test number: "+ testnum);
				logger.error("skipping unknown test number: "+ testnum);
			}
		} catch (Throwable t) {
			logger.error("exception "+ t.getMessage(), t);
			result = new JSONObject();
			result.put("exception", t.getMessage());
		}
		if (result != null)
			result.put("testNum", testnum);
		return result;
	}


	/**
	 * Получение указанного аргумента из this.args
	 * @param argName название аргумента
	 * @param argDefault значение по-умолчанию
	 * @param echoObj (необ) объект, в который выдать найденное значение,
	 * если echoObj == null, то не используется
	 * @return значение аргумента без незначащих пробелов
	 * @throws JSONException
	 */
	String echoGetArg( String argName, String argDefault, JSONObject echoObj
		) throws JSONException {
		return echoGetArg( this.args, argName, argDefault, echoObj); 
	}
	static String echoGetArg( JSONObject args, String argName, String argDefault, JSONObject echoObj
				) throws JSONException {
		final String found = args.optString(argName, argDefault);
		if (echoObj != null)
			echoObj.put("called_"+ argName, found);
		return found != null ? found.trim() : null;
	}

	boolean echoGetArgBool( String argName, boolean argDefault, JSONObject echoObj
		) throws JSONException {
		return echoGetArgBool(args, argName, argDefault, echoObj);
	}

	static boolean echoGetArgBool( JSONObject args, String argName, boolean argDefault, JSONObject echoObj
			) throws JSONException {
		final boolean found = args.optBoolean(argName, argDefault);
		if (echoObj != null)
			echoObj.put("called_"+ argName, found);
		return found;
	}

	NodeRef echoGetNodeRef( String argName, String argDefault, JSONObject echoObj
		) throws JSONException {
		return echoGetNodeRef(args, argName, argDefault, echoObj);
	}

	static NodeRef echoGetNodeRef(  JSONObject args, String argName, String argDefault, JSONObject echoObj
			) throws JSONException {
		final String id = echoGetArg( args, argName, argDefault, echoObj);
		return (id != null && id.length() > 0) ? makeFullRef(id) : null;
	}

	/**
	 * Получение из this.args именованного аргумента типа SGPosition, как вложенного json под-объекта
	 * @param argName название аргумента внутри this.args
	 * @param echoObj объект, в который выводится эхо получаемых значений
	 * @param lecmPermService служба работы с разрешениями
	 * @return
	 * @throws JSONException
	 */
	SGPosition echoGetArgSGPosition(String argName, JSONObject echoObj
			, LecmPermissionService lecmPermService) throws JSONException {
		final JSONObject jdata = this.args.optJSONObject(argName);
		if (jdata == null)
			return null;
		final SGKind kind = SGKind.valueOf(jdata.optString("sgKind", "no sgKind").toUpperCase());
		final SGPosition result;
		switch(kind) {
			case SG_DP:
				result = SGKind.getSGDeputyPosition( jdata.optString("id", null), jdata.optString("displayName", null), jdata.optString("userLogin", null), jdata.optString("userId", null));
				break;
			case SG_BRME:
				result = SGKind.getSGMyRolePos( jdata.optString("id", null), jdata.optString("userLogin", null), jdata.optString("roleCode", null));
				break;
			case SG_SPEC:
				final NodeRef node = echoGetNodeRef( jdata, "nodeRef", null, echoObj);// (!) надо брать из объекта jdata, а не из this!
				result = SGKind.getSGSpecialUserRole(
						echoGetArg(jdata, "id", null, echoObj)
						, lecmPermService.findPermissionGroup( echoGetArg( jdata, "permGroup", null, echoObj))
						, node
						, echoGetArg( jdata, "userLogin", null, echoObj));

				break;
			default: result = kind.getSGPos( jdata.optString("id", null), jdata.optString("displayName", null));
		}
		if (echoObj != null)
			echoObj.put("called_"+ argName, result);
		return result;
	}

	/**
	 * param: см также this.args
	 * @param status
	 * @param flag
	 * @param permcheck
	 * @return
	 * @throws JSONException
	 */
	private JSONObject doSearchTest(final String status, final boolean flag, final boolean permcheck)
			throws JSONException
	{
		final JSONObject result = new JSONObject();
		logger.info( "performing test with security check="+ permcheck+ ", args: \n\t"+ args);

		// final NodeService nodeService = serviceRegistry.getNodeService();
		final NodeService nodesrv = (permcheck) ? this.secureNodeService : this.fastNodeService;

		final NodeRef companyHome = repositoryHelper.getCompanyHome();

		final NodeRef blanksRoot = nodesrv.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, "Бланки-тест");
		final String folderName = getFolderName();
		final NodeRef commonFolder = nodesrv.getChildByName(blanksRoot, ContentModel.ASSOC_CONTAINS, folderName);

		/* параметры Lucene поиска */
		final SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		// sp.setLanguage(SearchService.LANGUAGE_LUCENE); //  Lucene
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO); // FTS (!)

		final StringBuilder sbQuery = new StringBuilder();

		/* строго задаём тип */
		sbQuery.append(String.format("TYPE:\"{%s}%s\"", NAMESPACE, TYPENAME));

		/* добавление условий поиска */
		sbQuery.append(String.format(" AND @{%s}%s:\"%s\"", NAMESPACE, "status", status));
		sbQuery.append(String.format(" AND @{%s}%s:\"%s\"", NAMESPACE, "flag", flag));

		/* добавление параметров pg_limit/offset ... */
		SearchHelper.assignArgs(sp, this.args);

		/* запрос */
		final DurationLogger d = new DurationLogger();
		sp.setQuery(sbQuery.toString()); // example: TYPE:"{http://www.it.ru/lecm/model/blanks/1.0}blank" AND @"{http://www.it.ru/lecm/model/blanks/1.0}status":New AND @"{http://www.it.ru/lecm/model/blanks/1.0}flag":false

		final String msg = d.fmtDuration("{t} msec");
		d.markStart();
		logger.info( "searchTime "+ msg);
		result.put("searchTime", msg);

		/* поиск */
		ResultSet foundSet = null;
		try {
			// проходим по набору perms, эмулируя обращение к данным ...
			final StringBuilder sb = new StringBuilder( "permissions: \n");
			foundSet = serviceRegistry.getSearchService().query(sp);
			int i_total = 0, i_allow = 0, i_rawLength = -1;
			if (foundSet != null) {
				for(ResultSetRow row : foundSet) {
					i_total++;
					final NodeRef nr = row.getNodeRef();

					final Map<QName, Serializable> props = nodesrv.getProperties(nr);

					final Set<AccessPermission> perms = serviceRegistry.getPermissionService().getPermissions(nr);
					final boolean hasAccess = (perms != null) && ((props != null) && props.size() > 0);

					if (logger.isDebugEnabled()) {
						final Set<AccessPermission> permsAll = serviceRegistry.getPermissionService().getAllSetPermissions(nr);
						sb.append( String.format("\t[%s]\t%s\n\t%s\n\t%s\n", i_total, nr.getId(), perms, permsAll));
					}

					if (hasAccess) i_allow++;
				}
			}
			logger.info( String.format("found %s record(s)", i_total));

			if (logger.isDebugEnabled())
				logger.debug( sb.toString() );

			{ // формирование сводки по сканированным данным ...
				final JSONObject rowsInfo = new JSONObject();
				rowsInfo.put("found", i_total);
				rowsInfo.put("allowed", i_allow);
				rowsInfo.put("denied", i_total - i_allow);
				rowsInfo.put("raw_length", i_rawLength);
				result.put("rows", rowsInfo);
			}
		} finally {
			final String info = d.fmtDuration("{t} msec");
			logger.info( "processTime "+ info);
			result.put("processTime", info);

			if(foundSet != null)
				foundSet.close();
		}

		if (logger.isInfoEnabled()) {
			final Map<QName, Serializable> props = nodesrv.getProperties(commonFolder);
			final StringBuilder sb = Utils.makePropDump(props, "node "+ commonFolder.toString());
			logger.info(sb.toString());
		}

		return result;
	}

	/**
	 * Получить название папки, внутри которой происходит тестирование объектов
	 * @return
	 */
	private String getFolderName() {
		if (this.args != null && args.has(ARGNAME_FOLDER)) {
			try {
				return args.getString(ARGNAME_FOLDER);
			} catch (JSONException ex) {
				logger.error( String.format("Error getting argument '%s' (default will be '%s'):", ARGNAME_FOLDER, DEFAULT_FOLDERNAME), ex);
			}
		}
		return DEFAULT_FOLDERNAME;
	}

	private final static String ARG_SG_ROOTNAME = "sg.rootName";
	private static final String ARG_SG_ROOTNAME_DEFAULT = "SG_ME_";
	private final static String ARG_SG_N0 = "sg.n0";
	private final static String ARG_SG_N  = "sg.n";
	private final static String ARG_SG_LEVELS = "sg.levels";


	/**
	 * Проверить существование авторизации с названием shortName
	 * @param shortName
	 * @param parentName родитель, внутри которого искать, может быть Null
	 * @return
	 */
	private boolean hasAuth(String shortName, String parentName) {
		// есть user-авторизация? ...
//		Set<String> found = this.authorityService.findAuthorities(AuthorityType.USER, parentName, true, shortName, null);
//		if (found != null && found.size() > 0)
//			return true;
		if (!this.authorityService.authorityExists( authorityService.getName( AuthorityType.USER, shortName)))
			return false;
		// есть групповая авторизация? ...
//		found = this.authorityService.findAuthorities(AuthorityType.GROUP, parentName, true, shortName, null);
//		if (found != null && found.size() > 0)
//			return true;
		if (!this.authorityService.authorityExists( authorityService.getName( AuthorityType.GROUP, shortName)))
			return false;
		// есть не группа? ...
		return this.authorityService.authorityExists( authorityService.getName( AuthorityType.ROLE, shortName));
	}

	/**
	 * Выполняется создание SG-групп вида (см параметры this.args):
	 * 		args['sg.rootName'] + "_" + (args['sg.n0'] + i)
	 * , где i принимает значения от 0 до args['sg.n']
	 * @return
	 * @throws JSONException
	 */
	private JSONObject doCreateSGTest() throws JSONException
	{
		final JSONObject result = new JSONObject();

		final DurationLogger d = new DurationLogger();
		try {
			final int n0 = this.args.optInt(ARG_SG_N0, 0);
			final int n = this.args.optInt(ARG_SG_N, 1);
			final String rootName = this.args.optString(ARG_SG_ROOTNAME, ARG_SG_ROOTNAME_DEFAULT);
			logger.info( String.format("Security-group testing:\n\trootName:%s\n\t[start..stop]: [%s..%s]", rootName, n0, n0+n));

			int i_total = 0;
			String lastName = "";
			for (int i=0; i < n; i++) {
				final String gname = String.format("%s%s", rootName, i+n0);
				lastName = gname;

				if (hasAuth(gname, null)) {
					logger.info( String.format("already exists '%s' -> skipped", gname));
					continue;
				}

				final String newname = this.authorityService.createAuthority(AuthorityType.GROUP, gname);
				logger.info( String.format("created '%s' as '%s'", gname, newname));

				i_total++;
			}

			{ // формирование сводки по отработанным данным ...
				result.put("processed", i_total);
				result.put("nameExample", lastName);
			}

		} catch (JSONException ex) {
			logger.error("SG test problem:", ex);
			throw ex;
		} catch (Throwable ex) {
			logger.error("SG test problem:", ex);
			result.put("exception", ex.toString());
		}
		finally {
			final String info = d.fmtDuration("SecurityGroup test time is {t}, msec");
			logger.info( "processTime "+ info);
			result.put("processTime", info);
		}

		return result;
	}

	/**
	 * Выполняется создание SG-групп вида (см параметры this.args):
	 * 		args['sg.rootName'] + "_" + (args['sg.n0'] + i)
	 * , где i принимает значения от 0 до args['sg.n']
	 * и для них создаются вложенные до уровня sg.level
	 * @return
	 * @throws JSONException
	 */
	private JSONObject doCreateNestedSGTest() throws JSONException
	{
		final JSONObject result = new JSONObject();

		final DurationLogger d = new DurationLogger();
		try {
			final int n0 = this.args.optInt(ARG_SG_N0, 0);
			final int n = this.args.optInt(ARG_SG_N, 1);
			final int levels = this.args.optInt(ARG_SG_LEVELS, 1);
			final String rootName = this.args.optString(ARG_SG_ROOTNAME, ARG_SG_ROOTNAME_DEFAULT);
			logger.info( String.format("Security-group testing:\n\trootName:%s\n\t[start..stop]: [%s..%s]", rootName, n0, n0+n));

			int i_total = 0;
			String lastName = "";
			for (int i=0; i < n; i++) {
				final String gname = String.format("%s%s", rootName, i+n0);
				lastName = gname;

				if (!hasAuth(gname, null)) {
					logger.info( String.format("not exists '%s' -> skipped", gname));
					continue;
				}

				String curParent = "GROUP_" + gname;
				for (int lev = 2; lev <= levels; lev++) {
					final String deepName = String.format( "%s_%s", gname, UUID.randomUUID ().toString ());
					final String newname = this.authorityService.createAuthority(AuthorityType.GROUP, deepName);
					this.authorityService.addAuthority(curParent, newname);
					curParent = newname;
					logger.info( String.format("nested created '%s' as '%s'", deepName, newname));
				}

				i_total++;
			}

			{ // формирование сводки по отработанным данным ...
				result.put("processed", i_total);
				result.put("nameExample", lastName);
			}

		} catch (JSONException ex) {
			logger.error("SG test problem:", ex);
			throw ex;
		} catch (Throwable ex) {
			logger.error("SG test problem:", ex);
			result.put("exception", ex.toString());
		}
		finally {
			final String info = d.fmtDuration("SecurityGroup test time is {t}, msec");
			logger.info( "processTime "+ info);
			result.put("processTime", info);
		}

		return result;
	}

	final static String ORGOPER_NODECREATED = "orgNodeCreated";
	final static String ORGOPER_NODEDEACTIVATED = "orgNodeDeactivated";
	final static String ORGOPER_EMPLOYEETIE = "orgEmployeeTie";

	final static String ORGOPER_SGINCLUDE = "sgInclude";
	final static String ORGOPER_SGEXCLUDE = "sgExclude";

	final static String ORGOPER_BRASSIGNED = "orgBRAssigned";
	final static String ORGOPER_BRREMOVED = "orgBRRemoved";

	final static String ORGOPER_GRANTACCESS = "grantAccess";
	final static String ORGOPER_REVOKEACCESS = "revokeAccess";

	/**
	 * json-параметры теста орг-штатки:
	 *   "oper" = ( см ORGOPER_XXX = 7 операций)
	 *   Остальные параметры зависят от самих операции:
	 *     1-2) "oper"= orgNodeCreated или orgNodeDeactivated:
	 *     		obj: типа "sgPosition" с парой атрибутов
	 *     			"obj"::"sgKind" = enum SGKind = (
	 *     				SG_ME		личная группа Сотрудника-пользователя
	 *     				SG_DP		группа Должностной позиции
	 *     				SG_OU		группа Подразделения
	 *     				SG_SV		группа Руководящая (связана с Подразделением и Должностью)
	 *     				SG_BR		группа бизнес-роли
	 *     				SG_BRME		личная группа Сотрудника-пользователя для конкретной бизнес-роли
	 *     			)
	 *      		"objPos"::"id" id объекта орг-штатки
	 *      		"objPos"::"roleCode" (используется при sgKind=SG_BRME) код бизнес роли
	 *    3) "oper"= orgEmployeeTie:
	 *     		"employeeId": id Сотрудника
	 *     		"alfrescoUserLogin": login user of alfresco
	 *    4) oper=sgInclude:
	 *     		"child": тип "sgPosition" (т.е. "child"::"sgKind" и "child"::"objId")
	 *     		"parent": тип "sgPosition"
	 *    5) oper=sgExclude:
	 *     		"child": тип "sgPosition"
	 *     		"oldParent": тип "sgPosition"
	 *    6-7) oper=orgBRAssigned и orgBRRemoved:
	 *     		"broleCode": код роли для операции
	 *     		"obj": тип "sgPosition"
	 * @return
	 * @throws JSONException
	 */
	private JSONObject doOrgStrucNotifyTest() throws JSONException {
		final JSONObject result = new JSONObject();

		// получение бинов ...
		final IOrgStructureNotifiers notiServ = (IOrgStructureNotifiers) getApplicationContext().getBean(IOrgStructureNotifiers.BEAN_NAME); // getApplicationContext().getBean(IOrgStructureNotifiers.class);
		PropertyCheck.mandatory(this, IOrgStructureNotifiers.BEAN_NAME, notiServ);

		final LecmPermissionService lecmServ = (LecmPermissionService)  getApplicationContext().getBean(LecmPermissionService.BEAN_NAME);
		PropertyCheck.mandatory(this, LecmPermissionService.BEAN_NAME, lecmServ);

		logger.info(
				"\n\tbean IOrgStructureNotifiers found class: "+ notiServ.getClass().getName()
				+"\n\tbean LecmPermissionService found class: "+ lecmServ.getClass().getName()
		);

		// выполнение Операции
		final String oper = echoGetArg("oper", "", result);

		if (ORGOPER_NODECREATED.equalsIgnoreCase(oper)) {
			final SGPosition obj = echoGetArgSGPosition( "obj", result, lecmServ);
			final String res = notiServ.orgNodeCreated(obj);
			logger.info( String.format("%s return '%s'", oper, res));
			result.put("return", res);
		}
		else if (ORGOPER_NODEDEACTIVATED.equalsIgnoreCase(oper)) {
			final SGPosition obj = echoGetArgSGPosition( "obj", result, lecmServ);
			notiServ.orgNodeDeactivated(obj);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		}
		else if (ORGOPER_EMPLOYEETIE.equalsIgnoreCase(oper)) {
			final String employeeId = echoGetArg( "employeeId", null, result);
			final String userLogin = echoGetArg( "alfrescoUserLogin", null, result);
			final boolean isActive = echoGetArgBool( "isActive", true, result);
			notiServ.orgEmployeeTie(employeeId, userLogin, isActive);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		}
		else if (ORGOPER_SGINCLUDE.equalsIgnoreCase(oper)) {
			final SGPosition child = echoGetArgSGPosition( "child", result, lecmServ);
			final SGPosition parent= echoGetArgSGPosition( "parent", result, lecmServ);
			notiServ.sgInclude(child, parent);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		}
		else if (ORGOPER_SGEXCLUDE.equalsIgnoreCase(oper)) {
			final SGPosition child = echoGetArgSGPosition( "child", result, lecmServ);
			final SGPosition oldParent= echoGetArgSGPosition( "oldParent", result, lecmServ);
			notiServ.sgInclude(child, oldParent);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		}
		else if (ORGOPER_BRASSIGNED.equalsIgnoreCase(oper)) {
			final String roleCode = echoGetArg( "roleCode", null, result);
			final SGPosition obj = echoGetArgSGPosition( "obj", result, lecmServ);
			notiServ.orgBRAssigned(roleCode, obj);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		}
		else if (ORGOPER_BRREMOVED.equalsIgnoreCase(oper)) {
			final String roleCode = echoGetArg( "roleCode", null, result);
			final SGPosition obj = echoGetArgSGPosition( "obj", result, lecmServ);
			notiServ.orgBRRemoved(roleCode, obj);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		}

		else if (ORGOPER_GRANTACCESS.equalsIgnoreCase(oper) || ORGOPER_REVOKEACCESS.equalsIgnoreCase(oper) ) {
			final boolean grant = ORGOPER_GRANTACCESS.equalsIgnoreCase(oper);
			final String employeeId = echoGetArg( "id", null, result);
			final NodeRef node = echoGetNodeRef( "nodeId", echoGetArg( "node", null, result), result); // "nodeId" или "node"
			final LecmPermissionGroup permissionGroup = lecmServ.findPermissionGroup( echoGetArg( "permGroup", null, result));
			if (grant)
				lecmServ.grantAccess( permissionGroup, node, employeeId);
			else
				lecmServ.revokeAccess( permissionGroup, node, employeeId);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		}

		else { // UNKNOWN OPER
			result.put("called_oper", "skipped unknown oper "+ oper);
			logger.warn( String.format("Skipping unsupported operation '%s'", oper));
		}

		return result;
	}

	/**
	 * Получить название полномочия, исходя из упрощённого:
	 *   ---------------------------------
	 *    Упрощённое				Полное
	 *   ---------------------------------
	 *    r или						Read
	 *    rw | full | w | +			Write
	 *    deny | -					deny
	 *    остальное					как есть
	 *   ---------------------------------
	 * @param simplePermName
	 * @return
	 */
	final static String findPermission(String simplePermName) {
		if (simplePermName != null && simplePermName.trim().length() > 0) {
			String normName = simplePermName.trim().toLowerCase();
			if ("read".equals(normName) || "r".equals(normName))
				return "Read";
			if ("write".equals(normName) || "w".equals(normName)|| "rw".equals(normName) || "+".equals(normName))
				return "Write";
			if ("deny".equals(normName) || "-".equals(normName))
				return "deny";
			return simplePermName;
		}
		return simplePermName;
	}

	/**
	 * Сформировать карту "БР-Доступ" согласно списку, заданному в виде строки.
	 * @param value список через ';' из записей "бизнес-роль:доступ;..."
	 *  	где роль = название роли (мнемоника),
	 *  		доступ = (noaccess | readonly | full)
	 * 			если доступ опущен, принимается за пустой
	 * @return
	 */
	final Map<String, LecmPermissionGroup> makeBRoleMapping(String value) {
		final Map<String, LecmPermissionGroup> result = new HashMap<String, LecmPermissionGroup>();
		final String[] parts = value.split(";");
		if (parts != null) {
			for(String broleAccess: parts) {
				try {
					final String[] roleAcc = broleAccess.split(":");
					if (roleAcc.length == 0) continue;
					final String brole = roleAcc[0].trim();
					final String access = (roleAcc.length > 1) ? findPermission(roleAcc[1].trim()) : null;
					if ("deny".equalsIgnoreCase(access))
						continue; // skip
					// result.put( brole, (access != null) ? access : StdPermission.noaccess);
					result.put( brole, this.lecmPermissionService.findPermissionGroup(access));
				} catch(Throwable t) {
					logger.error( String.format("Check invalid map point '%s',\n\t expected to be 'BRole:access'\n\t\t, where access is (noaccess | readonly | full),\n\t\t BRole = mnemonic of business role"
							, broleAccess), t);
				}
			}
		}
		return result;
	}

	final static String ACLOPER_REBUILD = "rebuild";
	final static String ACLOPER_REBUILDYNAMIC = "rebuildDynamic";
	final static String ACLOPER_REBUILDSTATIC = "rebuildStatic";

	final static String ACLOPER_GRANT_DYNAROLE = "grantDynaRole";
	final static String ACLOPER_REVOKE_DYNAROLE = "revokeDynaRole";

	final private static String OPER_MIXED = ACLOPER_REBUILD
			+ "\t"+ ACLOPER_REBUILDYNAMIC
			+ "\t"+ ACLOPER_REBUILDSTATIC

			+ "\t"+ ACLOPER_GRANT_DYNAROLE
			+ "\t"+ ACLOPER_REVOKE_DYNAROLE
			;

	/**
	 * json-параметры ACL test:
	 *   1) "id": id документа
	 *   2) "oper" = ( rebuild, grantDynaRole, revokeDynaRole)
	 *   Остальные параметры зависят от операции:
	 *     "oper"=rebuild:
	 *     		"status": текущий статус документа - для него будет выполнена нарезка прав
	 *     "oper"= grantDynaRole | revokeDynaRole
	 *     		"roleCode": код роли
	 *     		"userId": id Сотрудника (или логин)
	 * @return
	 * @throws JSONException
	 */
	private JSONObject doCheckACLTest() throws JSONException {
		final JSONObject result = new JSONObject();

		// получение бина
		logger.info("bean found class "+ this.lecmPermissionService.getClass().getName());

		// выполнение Операции
		final String oper = echoGetArg("oper", ACLOPER_REBUILD, result);

		if (!(OPER_MIXED.toLowerCase().contains(oper.toLowerCase())) ) {
			result.put("called_oper", "skipped unknown oper "+ oper);
			logger.warn( String.format("Skipping unsupported operation '%s'", oper));
		} else {
			final String id = echoGetArg( "id", null, result); // TODO: (?) передавать сразу nodeRef
			if (id != null) {
				final NodeRef ref = makeFullRef(id);
				result.put("using_nodeRef", ref);

				if ( 	ACLOPER_REBUILD.equalsIgnoreCase(oper)
						|| ACLOPER_REBUILDYNAMIC.equalsIgnoreCase(oper)
						|| ACLOPER_REBUILDSTATIC.equalsIgnoreCase(oper)
					) {
					// final String status = echoGetArg( "status", null, result);
					// final String lifeCycle = echoGetArg( "lifeCycle", null, result);
					final String roleAccessMap = echoGetArg( "roleAccessMap", null, result);
					logger.info( String.format( "<RebuildACL> for node id '%s' as %s", id, roleAccessMap));

					final Map<String, LecmPermissionGroup> accessMap = makeBRoleMapping(roleAccessMap);
					if (ACLOPER_REBUILDSTATIC.equalsIgnoreCase(oper))
						this.lecmPermissionService.rebuildStaticACL( ref, accessMap);
					else
						this.lecmPermissionService.rebuildACL( ref, accessMap);
				} else if (ACLOPER_GRANT_DYNAROLE.equalsIgnoreCase(oper) || ACLOPER_REVOKE_DYNAROLE.equalsIgnoreCase(oper)) {
					final String roleCode = echoGetArg( "roleCode", null, result);
					final String userId = echoGetArg( "userId", null, result);
					// final StdPermission access = StdPermission.findPermission( echoGetArg("access", null, result) );
					final String access = findPermission( echoGetArg("access", null, result) );
					logger.info( String.format( "<%s> for node id '%s', user <%s>, role <%s>, access <%s>", oper, id, userId, roleCode, access));

					if (ACLOPER_GRANT_DYNAROLE.equalsIgnoreCase(oper))
						this.lecmPermissionService.grantDynamicRole(roleCode, ref, userId, this.lecmPermissionService.findPermissionGroup(access) );
					else
						this.lecmPermissionService.revokeDynamicRole(roleCode, ref, userId);
				} else { logger.warn("Unsupported operation "+ oper); }
			}
			logger.info( String.format( "done test oper <%s> for node id '%s'", oper, id));
		}

		return result;
	}

	private JSONObject doIntegroTest() throws JSONException {
		final JSONObject result = new JSONObject();

		// получение бина
		final ExecutorBean execBean = (ExecutorBean) getApplicationContext().getBean("lecmIntegroTestBean"); // getApplicationContext().getBean(ExecutorBean.class);
		logger.info("bean found with class "+ execBean.getClass().getName());

		final List<StepResult> runList;
		if (args.has("numStep")) {
			final int stepNum = Integer.parseInt( echoGetArg("numStep", null, result));
			runList = Arrays.asList( execBean.runStep(stepNum));
		} else {
			runList = execBean.runAll(); //  (!) основные тесты
		}
		// final StringBuilder sb = new StringBuilder( execBean.getClass().getSimpleName()+ ".runAll called, result is:\n ");
		result.put("called_info", execBean.getClass().getSimpleName()+ ".runAll called");

		// logger.info( sb.toString());
		// result.put("return", sb.toString());
		addStepResults( result, runList, "test");

		logger.info(result.toString());

		return result;
	}


	private void addStepResults(JSONObject result, List<StepResult> runList, String prefix)
			throws JSONException
	{
		if (runList == null) {
			// sb.append("NULL");
			result.put( "result", "NULL");
		} else {
			final JSONArray steps = new JSONArray();
			int i = 0;
			for(StepResult sr: runList) {
				i++;
				// sb.append( String.format( "\t [%d] \t %s\n", i, (sr == null ? "NULL": sr.toString())) );

				final JSONObject step = new JSONObject();
				steps.put(step);

				step.put( prefix+ "-Num", i);
				step.put( prefix+ "-Name", String.format( "test_%d", i));

				if (sr == null) {
					step.put( prefix+ "-Result", "NULL");
				} else {
					step.put( prefix+ "-Result", sr.getCode());
					step.put( "info", sr.getInfo() );
					if (sr.getRtmError() != null) {
						step.put("rtmError", sr.getRtmError());
					}
					if (sr.getNestedResults().size() == 0) {
						step.put( prefix+ "s", "EMPTY");
					} else {
						final JSONObject nested = new JSONObject();
						addStepResults(nested, sr.getNestedResults(), "action");
						step.put( prefix+ "s", nested);
					}
				}
		}
			result.put("steps", steps);
		}
	}

	/**
	 * Метод получения списка задач Activity, связанных с данным узлом.
	 * Входной аргумент - узел для инспектирования: 
	 * 		"nodeRef":"workspace://SpacesStore/2bab80c5-8e43-41a8-ac53-1e4d337c9869"
	 * @return (wf_count:count, wf_list[id:desc])
	 * @throws JSONException
	 */
	private JSONObject doWorkFlowTest() throws JSONException {
		final JSONObject result = new JSONObject();

		final NodeRef nodeRef = echoGetNodeRef( "nodeRef", null, result);
		final Map<String, String> workflowList = new HashMap<String, String>();
		if (nodeRef != null) {
			final List<WorkflowInstance> wfInstances = workflowService.getWorkflowsForContent(nodeRef, true);
			if (wfInstances != null) { 
				for (WorkflowInstance wfInstance : wfInstances) {
					workflowList.put( wfInstance.getId(), wfInstance.getDescription());
				}
			}
		}
		result.put( "wf_count", workflowList.size());
		result.put( "wf_list", workflowList);
		return result;
	}

}
