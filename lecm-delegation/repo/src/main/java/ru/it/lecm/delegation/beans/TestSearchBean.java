package ru.it.lecm.delegation.beans;

import java.io.Serializable;
import java.util.Map;

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

	/**
	 * Выполнить тест с указанным номером
	 * @param testnum
	 * @return
	 * @throws JSONException
	 */
	@Override
	public JSONObject runTest(int testnum) throws JSONException {
		switch (testnum) {
		case 1:	return doSearchTest( "New", false);
		case 2: return doSearchTest( "New", true);
		case 3: return doSearchTest( "Active", false);
		case 4: return doSearchTest( "Active", true);
		default:
			logger.error("skipping unknown test number: "+ testnum);
		}
		return null;
	}

	private JSONObject doSearchTest(final String status, final boolean flag)
			throws JSONException
	{
		final JSONObject result = new JSONObject();

		final NodeService nodeService = serviceRegistry.getNodeService();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();

		final NodeRef blanksRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, "Бланки-тест");
		final NodeRef commonFolder = nodeService.getChildByName(blanksRoot, ContentModel.ASSOC_CONTAINS, "Общая папка");


		/* параметры Lucene поиска */
		final SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);

		final StringBuilder sbQuery = new StringBuilder( "TYPE:\"{http://www.it.ru/lecm/model/blanks/1.0}blank\"");

		/* добавление условий поиска */
		sbQuery.append(String.format(" AND @%s:%s", "status", status));
		sbQuery.append(String.format(" AND @%s:%s", "flag", flag));

		/* запрос */
		final DurationLogger d = new DurationLogger();
		sp.setQuery(sbQuery.toString());

		final String msg = d.fmtDuration("{t} msec");
		d.markStart();
		logger.info( "searchTime "+ msg);
		result.put("searchTime", msg);

		/* поиск */
		ResultSet foundSet = null;
		try {
			// проходим по набору, эмулируя обращение к данным ...
			foundSet = serviceRegistry.getSearchService().query(sp);
			int i = 0;
			if (foundSet != null) {
				for(ResultSetRow row : foundSet) {
					i++;
					final Map<QName, Serializable> props = serviceRegistry.getNodeService().getProperties(row.getNodeRef());
					// final JSONObject obj = makeJson(props);
					// result.put(obj);
				}
			}
			final String info = String.format("found %s record(s)", i);
			logger.info( info);
			result.put("rows", info);
		} finally {
			final String info = d.fmtDuration("{t} msec");
			logger.info( "processTime "+ info);
			result.put("processTime", info);

			if(foundSet != null)
				foundSet.close();
		}

		final Map<QName, Serializable> props = nodeService.getProperties(commonFolder);
		final StringBuilder sb = Utils.makePropDump(props, "node "+ commonFolder.toString());
		logger.info(sb.toString());

		return result;
	}

}
