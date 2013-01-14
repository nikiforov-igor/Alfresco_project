package ru.it.lecm.delegation.beans;

import java.io.Serializable;
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

import ru.it.lecm.delegation.ITestSearch;
import ru.it.lecm.security.impl.OrgStrucureAfterInvocationProvider;
import ru.it.lecm.utils.DurationLogger;
import ru.it.lecm.utils.alfresco.SearchHelper;
import ru.it.lecm.utils.alfresco.Utils;

public class TestSearchBean implements ITestSearch
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
		final JSONObject result;
		// org.alfresco.repo.security.permissions.impl.acegi.MethodSecurityInterceptor;
		final boolean needPermChk = (args != null && args.optBoolean("secure", false));
		switch (testnum) {
			case 1:	result = doSearchTest( "New", false, needPermChk); break;
			case 2: result = doSearchTest( "New", true, needPermChk); break;
			case 3: result = doSearchTest( "Active", false, needPermChk); break;
			case 4: result = doSearchTest( "Active", true, needPermChk); break;
			case 5: result = doCreateSGTest(); break;
			case 6: result = doCreateNestedSGTest(); break;
			default:
				result = new JSONObject();
				result.put( "error", "invalid test number: "+ testnum);
				logger.error("skipping unknown test number: "+ testnum);
		}
		if (result != null)
			result.put("testNum", testnum);
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


	private boolean hasAuth(String shortName, String parentName) {
		// есть авторизация? ...
		final Set<String> found = this.authorityService.findAuthorities(AuthorityType.GROUP, parentName, true, shortName, null);
		return (found != null && found.size() > 0);
		/*
		// есть групповая авторизация? ...
		Set<String> found = this.authorityService.findAuthorities(AuthorityType.GROUP, parentName, true, shortName, null);
		if (found != null && found.size() > 0)
			return true;
		// есть не группа? ...
		found = this.authorityService.findAuthorities(AuthorityType.ROLE, parentName, true, shortName, null);
		if (found != null && found.size() > 0)
			return true;
		return false; // NOT FOUND
		 */
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

}
