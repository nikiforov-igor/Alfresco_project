package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
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
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

import ru.it.lecm.delegation.ITestSearch;
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.events.INodeACLBuilder;
import ru.it.lecm.security.events.INodeACLBuilder.StdPermission;
import ru.it.lecm.security.events.IOrgStructureNotifiers;
import ru.it.lecm.security.impl.OrgStrucureAfterInvocationProvider;
import ru.it.lecm.utils.DurationLogger;
import ru.it.lecm.utils.alfresco.SearchHelper;
import ru.it.lecm.utils.alfresco.Utils;

public class TestSearchBean extends AbstractLifecycleBean implements ITestSearch 
{

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

	private AuthorityService authorityService;

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

	@Override
	public void setConfig(JSONObject config) throws JSONException {
		this.args = config; 
	}

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
		final String found = this.args.optString(argName, argDefault);
		if (echoObj != null)
			echoObj.put("called_"+ argName, found);
		return found != null ? found.trim() : null;
	}

	/**
	 * Получение из this.args именованного аргумента типа SGPosition, как вложенного json под-объекта
	 * @param argName
	 * @param echoObj
	 * @return
	 * @throws JSONException
	 */
	SGPosition echoGetArgSGPosition(String argName, JSONObject echoObj) throws JSONException {
		final JSONObject jdata = this.args.optJSONObject(argName);
		if (jdata == null)
			return null;
		final SGKind kind = SGKind.valueOf(jdata.optString("sgKind", "no sgKind").toUpperCase());
		final SGPosition result;
		switch(kind) {
			case SG_DP:
				result = SGKind.getSGDeputyPosition( jdata.optString("id", null), jdata.optString("userId", null));
				break;
			case SG_BRME: 
				result = SGKind.getSGBusinessRolePos( jdata.optString("id", null), jdata.optString("roleCode", null));
				break;
			default: result = kind.getSGPos( jdata.optString("id", null));
		}
		if (echoObj != null)
			echoObj.put("called_"+ argName, result);
		return result;
	}

	public static NodeRef makeFullRef(String procuracyid) {
		return new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, procuracyid);
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
				if (foundSet instanceof OrgStrucureAfterInvocationProvider.FilteringResultSet)
					i_rawLength = ((OrgStrucureAfterInvocationProvider.FilteringResultSet)foundSet).rawLength();
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
		Set<String> found = this.authorityService.findAuthorities(AuthorityType.USER, parentName, true, shortName, null);
		if (found != null && found.size() > 0)
			return true;
		// есть групповая авторизация? ...
		found = this.authorityService.findAuthorities(AuthorityType.GROUP, parentName, true, shortName, null);
		if (found != null && found.size() > 0)
			return true;
		// есть не группа? ...
		found = this.authorityService.findAuthorities(AuthorityType.ROLE, parentName, true, shortName, null);
		if (found != null && found.size() > 0)
			return true;
		return false; // NOT FOUND
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

	/**
	 * json-параметры теста орг-штатки:
	 *   "oper" = ( см ORGOPER_XXX = 7 операций)
	 *   Остальные параметры зависят от самих операции:
	 *     1-2) "oper"= orgNodeCreated или orgNodeDeactivated:
	 *     		objPos: типа "sgPosition" с парой атрибутов
	 *     			"objPos"::"sgKind" = enum SGKind = (
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

		// получение бина
		final IOrgStructureNotifiers noti = (IOrgStructureNotifiers) getApplicationContext().getBean("lecmSecurityGroupsBean"); // getApplicationContext().getBean(IOrgStructureNotifiers.class);
		logger.info("bean found class "+ noti.getClass().getName());

		// выполнение Операции
		final String oper = echoGetArg("oper", "", result);

		if (ORGOPER_NODECREATED.equalsIgnoreCase(oper)) {
			final SGPosition obj = echoGetArgSGPosition( "obj", result);
			final String res = noti.orgNodeCreated(obj);
			logger.info( String.format("%s return '%s'", oper, res));
			result.put("return", res);
		} 
		else if (ORGOPER_NODEDEACTIVATED.equalsIgnoreCase(oper)) {
			final SGPosition obj = echoGetArgSGPosition( "obj", result);
			noti.orgNodeDeactivated(obj);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		} 
		else if (ORGOPER_EMPLOYEETIE.equalsIgnoreCase(oper)) {
			final String employeeId = echoGetArg( "employeeId", null, result);
			final String userLogin = echoGetArg( "alfrescoUserLogin", null, result);
			noti.orgEmployeeTie(employeeId, userLogin);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		} 
		else if (ORGOPER_SGINCLUDE.equalsIgnoreCase(oper)) {
			final SGPosition child = echoGetArgSGPosition( "child", result);
			final SGPosition parent= echoGetArgSGPosition( "parent", result);
			noti.sgInclude(child, parent);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		} 
		else if (ORGOPER_SGEXCLUDE.equalsIgnoreCase(oper)) {
			final SGPosition child = echoGetArgSGPosition( "child", result);
			final SGPosition oldParent= echoGetArgSGPosition( "oldParent", result);
			noti.sgInclude(child, oldParent);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		} 
		else if (ORGOPER_BRASSIGNED.equalsIgnoreCase(oper)) {
			final String roleCode = echoGetArg( "roleCode", null, result);
			final SGPosition obj = echoGetArgSGPosition( "obj", result);
			noti.orgBRAssigned(roleCode, obj);
			logger.info( String.format("%s called", oper));
			result.put("return", "void");
		} 
		else if (ORGOPER_BRREMOVED.equalsIgnoreCase(oper)) {
			final String roleCode = echoGetArg( "roleCode", null, result);
			final SGPosition obj = echoGetArgSGPosition( "obj", result);
			noti.orgBRRemoved(roleCode, obj);
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
	 * Сформировать карту "БР-Доступ" согласно списку, заданному в виде строки.
	 * @param value список через ';' из записей "бизнес-роль:доступ;..."
	 *  	где роль = название роли (мнемоника),
	 *  		доступ = (noaccess | readonly | full)
	 * 			если доступ опущен, принимается за пустой
	 * @return
	 */
	final static Map<String, StdPermission> makeBRoleMapping(String value) {
		final Map<String, StdPermission> result = new HashMap<String, StdPermission>();
		final String[] parts = value.split(";");
		if (parts != null) {
			for(String broleAccess: parts) {
				try {
					final String[] roleAcc = broleAccess.split(":");
					if (roleAcc.length == 0) continue;
					final String brole = roleAcc[0].trim();
					final StdPermission access = (roleAcc.length > 1) ? StdPermission.findPermission(roleAcc[1].trim()) : null;
					result.put( brole, (access != null) ? access : StdPermission.noaccess);
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
		final INodeACLBuilder builder = (INodeACLBuilder) getApplicationContext().getBean("lecmAclBuilderBean");
		logger.info("bean found class "+ builder.getClass().getName());

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
					logger.info( String.format( "<RebuildACL> for node id %s as %s", id, roleAccessMap));

					final Map<String, StdPermission> accessMap = makeBRoleMapping(roleAccessMap); 
					if (ACLOPER_REBUILDSTATIC.equalsIgnoreCase(oper))
						builder.rebuildStaticACL( ref, accessMap);
					else
						builder.rebuildACL( ref, accessMap);
				} else if (ACLOPER_GRANT_DYNAROLE.equalsIgnoreCase(oper) || ACLOPER_REVOKE_DYNAROLE.equalsIgnoreCase(oper)) {
					final String roleCode = echoGetArg( "roleCode", null, result);
					final String userId = echoGetArg( "userId", null, result);
					logger.info( String.format( "<%s> for node id %s, user <%s>, role <%s>", oper, id, userId, roleCode));

					if (ACLOPER_GRANT_DYNAROLE.equalsIgnoreCase(oper))
						builder.grantDynamicRole(roleCode, ref, userId);
					else
						builder.revokeDynamicRole(roleCode, ref, userId);
				} else { logger.warn("Unsupported operation "+ oper); }
			}
			logger.info( String.format( "done test oper <%s> for node id %s", oper, id));
		}

		return result;
	}

}
