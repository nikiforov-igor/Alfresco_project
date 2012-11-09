package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

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
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.delegation.ITestSearch;
import ru.it.lecm.delegation.utils.DurationLogger;
import ru.it.lecm.delegation.utils.Utils;

public class TestSearchBean implements ITestSearch 
{

	final private static Logger logger = LoggerFactory.getLogger (TestSearchBean.class);

	private ServiceRegistry serviceRegistry;
	private Repository repositoryHelper;

	private NodeService fastNodeService;
	private NodeService secureNodeService;

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
		final boolean needPermChk = (args != null && args.optBoolean("secure", false));
		switch (testnum) {
			case 1:	result = doSearchTest( "New", false, needPermChk); break;
			case 2: result = doSearchTest( "New", true, needPermChk); break;
			case 3: result = doSearchTest( "Active", false, needPermChk); break;
			case 4: result = doSearchTest( "Active", true, needPermChk); break;
			default:
				result = new JSONObject();
				result.put( "error", "invalid test number: "+ testnum);
				logger.error("skipping unknown test number: "+ testnum);
		}
		if (result != null)
			result.put("testNum", testnum);
		return result;
	}

	private JSONObject doSearchTest(final String status, final boolean flag, final boolean permcheck)
			throws JSONException
	{
		final JSONObject result = new JSONObject();
		logger.info( "performing test with security check="+ permcheck+ ", args: \n\t"+ args);

		// final NodeService nodeService = serviceRegistry.getNodeService();
		final NodeService nodesrv = (permcheck) ? this.secureNodeService : this.fastNodeService;

		final NodeRef companyHome = repositoryHelper.getCompanyHome();

		final NodeRef blanksRoot = nodesrv.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, "Бланки-тест");
		final NodeRef commonFolder = nodesrv.getChildByName(blanksRoot, ContentModel.ASSOC_CONTAINS, "Общая папка");

		/* параметры Lucene поиска */
		final SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		// sp.setLanguage(SearchService.LANGUAGE_LUCENE); //  Lucene
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO); // FTS (!)

		final StringBuilder sbQuery = new StringBuilder();

		final String namespace = "http://www.it.ru/lecm/model/blanks/1.0";
		final String typename = "blank";

		/* строго задаём тип */
		sbQuery.append(String.format("TYPE:\"{%s}%s\"", namespace, typename));

		/* добавление условий поиска */
		sbQuery.append(String.format(" AND @{%s}%s:\"%s\"", namespace, "status", status));
		sbQuery.append(String.format(" AND @{%s}%s:\"%s\"", namespace, "flag", flag));

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
			// проходим по набору, эмулируя обращение к данным ...
			final StringBuilder sb = new StringBuilder( "permissions: \n");
			foundSet = serviceRegistry.getSearchService().query(sp);
			int i_total = 0, i_allow = 0;
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
					// final JSONObject obj = makeJson(props);
					// result.put(obj);
				}
			}
			logger.info( String.format("found %s record(s)", i_total));
			logger.debug( sb.toString() );
			{ // формирование сводки по сканированным данным ...
				final JSONObject rowsInfo = new JSONObject();
				rowsInfo.put("found", i_total);
				rowsInfo.put("allowed", i_allow);
				rowsInfo.put("denied", i_total - i_allow);
				result.put("rows", rowsInfo);
			}
		} finally {
			final String info = d.fmtDuration("{t} msec");
			logger.info( "processTime "+ info);
			result.put("processTime", info);

			if(foundSet != null)
				foundSet.close();
		}

		final Map<QName, Serializable> props = nodesrv.getProperties(commonFolder);
		final StringBuilder sb = Utils.makePropDump(props, "node "+ commonFolder.toString());
		logger.info(sb.toString());

		return result;
	}

}
