package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.IDelegationDescriptor;
import ru.it.lecm.delegation.ITestSearch;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.utils.DurationLogger;
import ru.it.lecm.utils.alfresco.Utils;

public class DelegationBean extends BaseProcessorExtension implements IDelegation, AuthenticationUtil.RunAsWork<NodeRef>, IDelegationDescriptor {

	// Namespace URI of delegations model structure
	// "http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0";
	public static final String NSURI_DELEGATIONS = "http://www.it.ru/logicECM/model/delegation/1.0"; // prefix PREFIX_PROCURACY
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
	// Статус параметров делегирования
	/** text, Состояние Доверенности */
	final static public QName PROP_STATUS = QName.createQName(NSURI_DELEGATIONS, "delegation-opts-status");

	/*
	final static public QName PROP_ = QName.createQName(NSURI_DELEGATIONS, "");
	 */

	/**
	 * Ссылки Доверенности
	 */


	// 	(1-1) делегирующее лицо. Тот человек, чьи бизнес роли и права будут переданы (из type"lecm-d8n:delegation-opts")
	final static public QName ASSOC_FROM_EMPLOYEE = QName.createQName(NSURI_DELEGATIONS, "delegation-opts-owner-assoc");

	// (1-M) коллекция доверенностей (из type"lecm-d8n:delegation-opts")
	final static public QName ASSOC_PROCURACY_LIST = QName.createQName(NSURI_DELEGATIONS, "delegation-opts-procuracy-assoc");

	//  (M-1) Список делегируемых бизнес-ролей (из type"lecm-d8n:procuracy")
	final static public QName ASSOC_GIVE_ROLE = QName.createQName(NSURI_DELEGATIONS, "business-role-assoc");

	// (M-1) Сотрудник который является доверенным лицом (из type"lecm-d8n:procuracy")
	final static public QName ASSOC_TO_EMPLOYEE = QName.createQName(NSURI_DELEGATIONS, "trustee-assoc");

	private final static String CONTAINER = "DelegationOptionsContainer";
	private final static String DELEGATION_NAMESPACE = "http://www.it.ru/logicECM/model/delegation/1.0";
	private final static String ORGSTRUCTURE_NAMESPACE = "http://www.it.ru/lecm/org/structure/1.0";
	private final static String DICTIONARY_NAMESPACE = "http://www.it.ru/lecm/dictionary/1.0";
	private final static QName TYPE_DELEGATION_OPTS_CONTAINER = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-container");
	private final static QName TYPE_DELEGATION_OPTS = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts");
	private final static QName TYPE_PROCURACY = QName.createQName (DELEGATION_NAMESPACE, "procuracy");
	private final static QName TYPE_EMPLOYEE = QName.createQName (ORGSTRUCTURE_NAMESPACE, "employee");
	private final static QName ASSOC_DELEGATION_OPTS_OWNER = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-owner-assoc");
	private final static QName ASSOC_DELEGATION_OPTS_CONTAINER = QName.createQName (DELEGATION_NAMESPACE, "container-delegation-opts-assoc");
	private final static QName ASSOC_EMPLOYEE_PERSON = QName.createQName (ORGSTRUCTURE_NAMESPACE, "employee-person-assoc");
	private final static QName ASSOC_DELEGATION_OPTS_PROCURACY = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-procuracy-assoc");
	private final static QName ASSOC_PROCURACY_BUSINESS_ROLE = QName.createQName (DELEGATION_NAMESPACE, "procuracy-business-role-assoc");
	private final static QName ASSOC_PROCURACY_TRUSTEE = QName.createQName (DELEGATION_NAMESPACE, "procuracy-trustee-assoc");
	private final static QName PROP_ACTIVE = QName.createQName (DICTIONARY_NAMESPACE, "active");

	private static enum ASSOCIATION_TYPE {
		SOURCE,
		TARGET
	};
	/*
	 * props
	 */
	final private static Logger logger = LoggerFactory.getLogger (DelegationBean.class);

	private Repository repository;
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private TransactionService transactionService;
	private OrgstructureBean orgstructureService;

	private ITestSearch tester;

	public void setRepositoryHelper (Repository repository) {
		this.repository = repository;
	}

	public void setNodeService (NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNamespaceService (NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setTransactionService (TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public ITestSearch getTester() {
		return tester;
	}

	public void setTester(ITestSearch tester) {
		this.tester = tester;
	}

	public void setOrgstructureService (OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
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
	 * @param procuracyid
	 * @return
	 */
	static NodeRef makeFullRef(String procuracyid) {
		return new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, procuracyid);
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
			org.alfresco.repo.security.permissions.impl.PermissionServiceImpl impl;
			org.alfresco.repo.security.permissions.impl.acegi.ACLEntryVoter aclVoter;
			net.sf.acegisecurity.vote.RoleVoter grpVoter;
			org.alfresco.repo.security.permissions.impl.acegi.MethodSecurityInterceptor metinter;

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
		return makeSearchQuery( dest, NSURI_DELEGATIONS, "procuracy", searchArgs, parentNode);
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
				final Map<QName, Serializable> props = nodeService.getProperties(row.getNodeRef());

				final Set<QName> assocs = new HashSet <QName>();
				{	// добавление ассоциаций ...
					final List<AssociationRef> ca = nodeService.getTargetAssocs(row.getNodeRef(), RegexQNamePattern.MATCH_ALL);
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

	private enum TestAction {
		  test1
		, test2
		, test3
		, test4
		, test5
		, test6
		;

		public boolean eq(Object s) {
			return (s != null) && this.name().equalsIgnoreCase(s.toString());
		}
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

			final Object testName = (args.has(ARG_TESTNAME)) ? args.get(ARG_TESTNAME) : null;
			if (testName == null) {
				result.put("message", "Argument '"+ ARG_TESTNAME+ "' not specified");
				return result;
			}

			logger.info( "performing test '"+ testName+ "'");
			tester.setConfig(args);
			if (TestAction.test1.eq(testName)) {
				copyJson( result, tester.runTest(1));
			} else if (TestAction.test2.eq(testName)) {
				copyJson( result, tester.runTest(2));
			} else if (TestAction.test3.eq(testName)) {
				copyJson( result, tester.runTest(3));
			} else if (TestAction.test4.eq(testName)) {
				copyJson( result, tester.runTest(4));
			} else if (TestAction.test5.eq(testName)) {
				copyJson( result, tester.runTest(5));
			} else if (TestAction.test6.eq(testName)) {
				copyJson( result, tester.runTest(6));
			}
			else
				result.put("message", "Argument '"+ ARG_TESTNAME+ "' calls unknown function '"+ testName+ "'");

		} catch(Throwable ex) {
			logger.error( String.format("Exception in args ", args), ex);
			// result.put( "error", ex.toString());
			throw new RuntimeException("Fail test", ex);
		} finally {
			final String msg = d.fmtDuration( "{t} msec");
			logger.info( "testTime " + msg);
			try {
				result.put( "testTime", msg);
			} catch(JSONException ex) {
				logger.error("", ex);
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

	public final void bootstrap () {
		PropertyCheck.mandatory (this, "repository", repository);
		PropertyCheck.mandatory (this, "nodeService", nodeService);
		PropertyCheck.mandatory (this, "namespaceService", namespaceService);
		PropertyCheck.mandatory (this, "transactionService", transactionService);

		//создание контейнера для хранения параметров делегирования
		AuthenticationUtil.runAsSystem (this);

		//возможно здесь еще будет штука для создания параметров делегирования для уже существующих пользователей
	}

	@Override
	public NodeRef doWork () throws Exception {
		repository.init ();
		final NodeRef companyHome = repository.getCompanyHome ();
		NodeRef container = nodeService.getChildByName (companyHome, ContentModel.ASSOC_CONTAINS, CONTAINER);
		if (container == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
			container = transactionHelper.doInTransaction (new RetryingTransactionCallback<NodeRef> () {
				@Override
				public NodeRef execute () throws Throwable {
					NodeRef parentRef = companyHome; //the parent node
					QName assocTypeQName = ContentModel.ASSOC_CONTAINS; //the type of the association to create. This is used for verification against the data dictionary.
					QName assocQName = QName.createQName (DELEGATION_NAMESPACE, CONTAINER); //the qualified name of the association
					QName nodeTypeQName = TYPE_DELEGATION_OPTS_CONTAINER; //a reference to the node type
					// создание корневого узла для делегирований в Компании ...
					Map<QName, Serializable> properties = new HashMap<QName, Serializable> (1); //optional map of properties to keyed by their qualified names
					properties.put (ContentModel.PROP_NAME, CONTAINER);
					ChildAssociationRef associationRef = nodeService.createNode (parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
					NodeRef delegationRoot = associationRef.getChildRef ();
					logger.debug (String.format ("container node '%s' created", delegationRoot.toString ()));
					return delegationRoot;
				}
			});
		}
		return container;
	}

	@Override
	public NodeRef getDelegationOptsContainer () {
		NodeRef delegationOptsContainer = null;
		try {
			delegationOptsContainer = doWork ();
		} catch (Exception ex) {
			logger.warn (ex.getMessage (), ex);
		}
		return delegationOptsContainer;
	}

	@Override
	public QName getDelegationOptsItemType () {
		return TYPE_DELEGATION_OPTS;
	}

	@Override
	public IDelegationDescriptor getDelegationDescriptor () {
		return this;
	}

	/**
	 * получение связанной ноды по ассоциации. Для типа связи 1:1, 1:0, 0:1
	 * @param nodeRef исходная нода
	 * @param assocTypeName имя типа ассоциации
	 * @param typeName  имя типа данных который завязан на ассоциацию
	 * @param type направление ассоциации source или target
	 * @return найденный NodeRef или null
	 */
	private NodeRef findNodeAssociationRef (NodeRef nodeRef, QNamePattern assocTypeName, QNamePattern typeName, ASSOCIATION_TYPE type) {
		List<AssociationRef> associationRefs;

		switch (type) {
			case SOURCE: associationRefs = nodeService.getSourceAssocs (nodeRef, assocTypeName);
				break;
			case TARGET: associationRefs = nodeService.getTargetAssocs (nodeRef, assocTypeName);
				break;
			default: associationRefs = new ArrayList<AssociationRef> ();
		}
		NodeRef foundNodeRef = null;
		for (AssociationRef associationRef : associationRefs) {
			NodeRef assocNodeRef;
			switch (type) {
				case SOURCE: assocNodeRef = associationRef.getSourceRef ();
					break;
				case TARGET: assocNodeRef = associationRef.getTargetRef ();
					break;
				default: assocNodeRef = null;
					break;
			}
			if (assocNodeRef != null) {
				QName foundType = nodeService.getType (assocNodeRef);
				if (typeName.isMatch (foundType)) {
					foundNodeRef = assocNodeRef;
				}
			}
		}
		return foundNodeRef;
	}

	@Override
	public NodeRef getOrCreateDelegationOpts (NodeRef employeeNodeRef) {
		//делаем поиск по всем delegation-opts, если не нашли то создаем новую
		NodeRef delegationOptsNodeRef = findNodeAssociationRef (employeeNodeRef, ASSOC_DELEGATION_OPTS_OWNER, TYPE_DELEGATION_OPTS, ASSOCIATION_TYPE.SOURCE);

		//создаем новый delegation-opts так как его нет
		if (delegationOptsNodeRef == null) {
			delegationOptsNodeRef = createDelegationOpts (employeeNodeRef);
		}
		return delegationOptsNodeRef;
	}

	private NodeRef createDelegationOpts (final NodeRef employeeNodeRef) {
		Serializable employeeName = nodeService.getProperty (employeeNodeRef, ContentModel.PROP_NAME);
		String delegationOptsName = String.format ("параметры делегирования для %s", employeeName);
		//создание ноды и установка ей ассоциации
		NodeRef parentRef = getDelegationOptsContainer (); //the parent node
		QName assocTypeQName = ASSOC_DELEGATION_OPTS_CONTAINER; //the type of the association to create. This is used for verification against the data dictionary.
		QName assocQName = QName.createQName (DELEGATION_NAMESPACE, delegationOptsName); //the qualified name of the association
		QName nodeTypeQName = TYPE_DELEGATION_OPTS; //a reference to the node type
		Map<QName, Serializable> properties = new HashMap<QName, Serializable> (); //optional map of properties to keyed by their qualified names
		properties.put (ContentModel.PROP_NAME, delegationOptsName);
		NodeRef delegationOptsNodeRef = nodeService.createNode (parentRef, assocTypeQName, assocQName, nodeTypeQName, properties).getChildRef ();
		nodeService.createAssociation (delegationOptsNodeRef, employeeNodeRef, ASSOC_DELEGATION_OPTS_OWNER);
		return delegationOptsNodeRef;
	}

	@Override
	public NodeRef getDelegationOptsByPerson (final NodeRef personNodeRef) {
		//смотрим у person ассоциацию на employee если нету то null, иначе ищем по employee
		NodeRef employeeRef = AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {

			@Override
			public NodeRef doWork () throws Exception {
				return findNodeAssociationRef (personNodeRef, ASSOC_EMPLOYEE_PERSON, TYPE_EMPLOYEE, ASSOCIATION_TYPE.SOURCE);
			}
		});
		return (employeeRef != null) ? getDelegationOptsByEmployee (employeeRef) : null;
	}

	@Override
	public NodeRef getDelegationOptsByEmployee (final NodeRef employeeNodeRef) {
		//смотрим у employee ассоциацию на delegation-opts если нету то null, иначе возвращаем delegation-opts
		return AuthenticationUtil.runAsSystem (new RunAsWork<NodeRef> () {

			@Override
			public NodeRef doWork () throws Exception {
				return getOrCreateDelegationOpts (employeeNodeRef);
			}
		});
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByEmployee (final NodeRef employeeNodeRef, final boolean onlyActive) {
		//получаем все бизнес роли
		Set<NodeRef> uniqueBusinessRoleNodeRefs = new HashSet<NodeRef> ();
		List<NodeRef> result = new ArrayList<NodeRef> ();
		List<NodeRef> businessRoleNodeRefs = orgstructureService.getBusinesRoles (onlyActive);
		if (businessRoleNodeRefs != null) {
			//пробегаемся по всем бизнес ролям которые есть в системе и находим employees которые с ними связаны
			for (NodeRef businessRoleNodeRef : businessRoleNodeRefs) {
				List<NodeRef> employeeNodeRefs = orgstructureService.getEmployeesByBusinessRole (businessRoleNodeRef);
				//если текущая бизнес роль связана с одним и только одним пользователем, то мы ее рассматриваем
				//остальные пропускаем
				if (employeeNodeRefs != null && employeeNodeRefs.size () == 1 && employeeNodeRef.equals (employeeNodeRefs.get (0))) {
					uniqueBusinessRoleNodeRefs.add (businessRoleNodeRef);
				}
			}
			result.addAll (uniqueBusinessRoleNodeRefs);
		}
		return result;
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByPerson (NodeRef personNodeRef, final boolean onlyActive) {
		NodeRef employeeNodeRef = findNodeAssociationRef (personNodeRef, ASSOC_EMPLOYEE_PERSON, TYPE_EMPLOYEE, ASSOCIATION_TYPE.SOURCE);
		return getUniqueBusinessRolesByEmployee (employeeNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getUniqueBusinessRolesByDelegationOpts (NodeRef delegationOptsNodeRef, final boolean onlyActive) {
		NodeRef employeeNodeRef = findNodeAssociationRef (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_OWNER, TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
		return getUniqueBusinessRolesByEmployee (employeeNodeRef, onlyActive);
	}

	private boolean isArchive (NodeRef nodeRef) {
		boolean isArchive = StoreRef.STORE_REF_ARCHIVE_SPACESSTORE.equals (nodeRef.getStoreRef ());
		return isArchive || !isActive (nodeRef);
	}

	private boolean isActive (NodeRef nodeRef) {
		Boolean isActive = (Boolean) nodeService.getProperty (nodeRef, PROP_ACTIVE);
		return isActive != null && isActive;
	}

	@Override
	public List<NodeRef> getProcuraciesByPerson (NodeRef personNodeRef, boolean onlyActive) {
		NodeRef delegationOptsNodeRef = getDelegationOptsByPerson (personNodeRef);
		return getProcuraciesByDelegationOpts (delegationOptsNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getProcuraciesByEmployee (final NodeRef employeeNodeRef, final boolean onlyActive) {
		//получаем параметры делегирования для сотрудника
		NodeRef delegationOptsNodeRef = getDelegationOptsByEmployee (employeeNodeRef);
		return getProcuraciesByDelegationOpts (delegationOptsNodeRef, onlyActive);
	}

	@Override
	public List<NodeRef> getProcuraciesByDelegationOpts (NodeRef delegationOptsNodeRef, boolean onlyActive) {
		List<NodeRef> procuracyNodeRefs = new ArrayList<NodeRef> ();
		List<ChildAssociationRef> childAssociationRefs = nodeService.getChildAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
		if (childAssociationRefs != null) {
			for (ChildAssociationRef childAssociationRef : childAssociationRefs) {
				NodeRef procuracyNodeRef = childAssociationRef.getChildRef ();
				//по идее еще и бизнес роль на активность надо проверять
				if (!onlyActive || !isArchive (procuracyNodeRef)) {
					procuracyNodeRefs.add (procuracyNodeRef);
				} else if (isActive (procuracyNodeRef)) {
					procuracyNodeRefs.add (procuracyNodeRef);
				}
			}
		}
		return procuracyNodeRefs;
	}

	@Override
	public List<NodeRef> createEmptyProcuracies (final NodeRef delegationOptsNodeRef, final List<NodeRef> businessRoleNodeRefs) {
		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper ();
		return transactionHelper.doInTransaction (new RetryingTransactionCallback<List<NodeRef>> () {

			@Override
			public List<NodeRef> execute () throws Throwable {
				List<NodeRef> procuracyNodeRefs = new ArrayList<NodeRef> ();
				for (NodeRef businessRoleNodeRef : businessRoleNodeRefs) {
					NodeRef parentRef = delegationOptsNodeRef; //the parent node
					QName assocTypeQName = ASSOC_DELEGATION_OPTS_PROCURACY; //the type of the association to create. This is used for verification against the data dictionary.
					QName assocQName = QName.createQName (DELEGATION_NAMESPACE, "доверенность_" + UUID.randomUUID ().toString ()); //the qualified name of the association
					QName nodeTypeQName = TYPE_PROCURACY; //a reference to the node type
					Map<QName, Serializable> properties = new HashMap<QName, Serializable> (1); //optional map of properties to keyed by their qualified names
					properties.put (PROP_ACTIVE, false);
					ChildAssociationRef associationRef = nodeService.createNode (parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
					NodeRef procuracyNodeRef = associationRef.getChildRef ();
					nodeService.createAssociation (procuracyNodeRef, businessRoleNodeRef, ASSOC_PROCURACY_BUSINESS_ROLE);
					procuracyNodeRefs.add (procuracyNodeRef);
				}
				return procuracyNodeRefs;
			}
		});
	}

	//проперти и ассоциации которые будут использоваться при сохранении
	private final static QName PROP_DELEGATION_OPTS_STATUS = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-status");
	private final static QName PROP_DELEGATION_OPTS_CAN_DELEGATE_ALL = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-can-delegate-all");
	private final static QName PROP_DELEGATION_OPTS_CAN_TRANSFER_RIGHTS = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-can-transfer-rights");
	private final static QName ASSOC_DELEGATION_OPTS_TRUSTEE = QName.createQName (DELEGATION_NAMESPACE, "delegation-opts-trustee-assoc");

	/**
	 * поиск значения проверти или ассоциации в JSON объекте который пришел с формы
	 * @param options JSON объект по которому искать
	 * @param pattern кусочек имени по которому пытаться искать
	 * @return Object если нашел, null в противном случае
	 */
	private <T>T findInOptions (final JSONObject options, final String pattern, final String typeName) {
		Object result = null;
		try {
			Iterator<Object> itr = options.keys ();
			while (itr.hasNext ()) {
				String key = itr.next ().toString ();
				if (key.contains (pattern)) {
					if ("Boolean".equals (typeName)) {
						result = options.getBoolean (key);
					} else if ("Double".equals (typeName)) {
						result = options.getDouble (key);
					} else if ("Integer".equals (typeName)) {
						result = options.getInt (key);
					} else if ("JSONArray".equals (typeName)) {
						result = options.getJSONArray (key);
					} else if ("JSONObject".equals (typeName)) {
						result = options.getJSONObject (key);
					} else if ("Long".equals (typeName)) {
						result = options.getLong (key);
					} else if ("String".equals (typeName)) {
						result = options.getString (key);
					} else {
						result = options.get (key);
					}
				}
			}
		} catch (JSONException ex) {
			logger.error (ex.getMessage (), ex);
		}
		return (T) result;
	}

	@Override
	public String saveDelegationOpts (final NodeRef delegationOptsNodeRef, final JSONObject options) {
		//получаем все properties
		Map<QName, Serializable> properties = nodeService.getProperties (delegationOptsNodeRef);
		//если статус "не настроено" то меняет его на "новое"
		Serializable status = properties.get (PROP_DELEGATION_OPTS_STATUS);
		if (DELEGATION_OPTS_STATUS.NOT_SET.equals (status.toString ())) {
			nodeService.setProperty (delegationOptsNodeRef, PROP_DELEGATION_OPTS_STATUS, DELEGATION_OPTS_STATUS.NEW.toString ());
		}
		//передавать права на документы подчиненных
		String propCanTransfer = PROP_DELEGATION_OPTS_CAN_TRANSFER_RIGHTS.getLocalName ();
		Boolean canTransfer = findInOptions (options, propCanTransfer, "Boolean");
		if (canTransfer != null) {
			nodeService.setProperty (delegationOptsNodeRef, PROP_DELEGATION_OPTS_CAN_TRANSFER_RIGHTS, canTransfer);
		}
		//делегировать все функции
		String propCanDelegate = PROP_DELEGATION_OPTS_CAN_DELEGATE_ALL.getLocalName ();
		Boolean canDelegate = findInOptions (options, propCanDelegate, "Boolean");
		if (canDelegate != null) {
			nodeService.setProperty (delegationOptsNodeRef, PROP_DELEGATION_OPTS_CAN_DELEGATE_ALL, canDelegate);
		}
		//ссылка на доверенное лицо
		if (canDelegate != null && canDelegate) {
			//получаем ссылку на доверенное лицо из options
			String propTrustee = ASSOC_DELEGATION_OPTS_TRUSTEE.getLocalName ();
			String trusteeRef = findInOptions (options, propTrustee + "_added", "String");
			if (trusteeRef != null) {
				//переназначем ассоциацию на доверенное лицо
				List<NodeRef> trusteeRefs = NodeRef.getNodeRefs (trusteeRef);
				nodeService.setAssociations (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE, trusteeRefs);
				//получить список доверенностей таких что бизнес роли у них active = true
				List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
				if (childAssocs != null) {
					for (ChildAssociationRef childAssoc : childAssocs) {
						NodeRef procuracyNodeRef = childAssoc.getChildRef ();
						nodeService.setAssociations (procuracyNodeRef, ASSOC_PROCURACY_TRUSTEE, trusteeRefs);
					}
				}
			}
		} else { //если галка "делегировать все функции" снята то удаляем ассоциацию
			//по ассоциации получаем ссылку на доверенное лицо, если она есть
			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
			//если по ассоциациям кто-то есть, берем его и удаляем.
			if (targetAssocs != null && !targetAssocs.isEmpty ()) {
				for (AssociationRef targetAssoc : targetAssocs) {
					NodeRef trusteeNodeRef = targetAssoc.getTargetRef ();
					nodeService.removeAssociation (delegationOptsNodeRef, trusteeNodeRef, ASSOC_DELEGATION_OPTS_TRUSTEE);
					//все доверенности которые участвуют с этим доверенным лицом переводим в статус active = false
					//ассоциацию с доверенным лицом также разрываем
					List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs (delegationOptsNodeRef, ASSOC_DELEGATION_OPTS_PROCURACY, RegexQNamePattern.MATCH_ALL);
					if (childAssocs != null) {
						for (ChildAssociationRef childAssoc : childAssocs) {
							NodeRef procuracyNodeRef = childAssoc.getChildRef ();
							List<AssociationRef> procuracyTargetRefs = nodeService.getTargetAssocs (procuracyNodeRef, ASSOC_PROCURACY_TRUSTEE);
							if (procuracyTargetRefs != null) {
								for (AssociationRef targetRef : procuracyTargetRefs) {
									if (trusteeNodeRef.equals (targetRef.getTargetRef ())) {
										nodeService.removeAssociation (procuracyNodeRef, trusteeNodeRef, ASSOC_PROCURACY_TRUSTEE);
									}
								}
							}
						}
					}
				}
			}
		}
		return "";
	}

	@Override
	public void actualizeProcuracyActivity (final NodeRef procuracyNodeRef) {
		List<AssociationRef> associationRefs = nodeService.getTargetAssocs (procuracyNodeRef, ASSOC_PROCURACY_TRUSTEE);
		if (associationRefs == null || associationRefs.isEmpty ()) {
			nodeService.setProperty (procuracyNodeRef, PROP_ACTIVE, false);
		} else {
			nodeService.setProperty (procuracyNodeRef, PROP_ACTIVE, true);
		}
	}

	@Override
	public void deleteProcuracies (final JSONArray nodeRefs) {
		for (int i = 0; i < nodeRefs.length (); ++i) {
			NodeRef nodeRef = null;
			try {
				nodeRef = new NodeRef (nodeRefs.getJSONObject (i).getString ("nodeRef"));
			} catch (JSONException ex) {
				logger.error (ex.getMessage (), ex);
			}
			if (nodeRef != null) {
				List<AssociationRef> associationRefs = nodeService.getTargetAssocs (nodeRef, ASSOC_PROCURACY_TRUSTEE);
				if (associationRefs != null) {
					for (AssociationRef associationRef : associationRefs) {
						nodeService.removeAssociation (nodeRef, associationRef.getTargetRef (), ASSOC_PROCURACY_TRUSTEE);
					}
				}
			}
		}
	}

	@Override
	public DELEGATION_OPTS_STATUS activateDelegationForEmployee (final NodeRef employeeRef) {
		NodeRef delegationOptsRef = getDelegationOptsByEmployee (employeeRef);
		Serializable oldStatus = nodeService.getProperty (delegationOptsRef, PROP_DELEGATION_OPTS_STATUS);
		nodeService.setProperty (delegationOptsRef, PROP_STATUS, DELEGATION_OPTS_STATUS.ACTIVE.toString ());
		return DELEGATION_OPTS_STATUS.get (oldStatus.toString ());
	}

	@Override
	public DELEGATION_OPTS_STATUS closeDelegationForEmployee (final NodeRef employeeRef) {
		NodeRef delegationOptsRef = getDelegationOptsByEmployee (employeeRef);
		Serializable oldStatus = nodeService.getProperty (delegationOptsRef, PROP_DELEGATION_OPTS_STATUS);
		nodeService.setProperty (delegationOptsRef, PROP_STATUS, DELEGATION_OPTS_STATUS.CLOSED.toString ());
		return DELEGATION_OPTS_STATUS.get (oldStatus.toString ());
	}
}
