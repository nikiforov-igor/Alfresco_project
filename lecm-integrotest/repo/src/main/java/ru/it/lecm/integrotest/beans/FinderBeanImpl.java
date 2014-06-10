package ru.it.lecm.integrotest.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.integrotest.FinderBean;
import ru.it.lecm.integrotest.utils.Utils;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * Для использования внутри контекстного xml, чтобы получать параметры типа QName
 * и выполнять поиск id объектов по названиям атрибутов.
 * Example:
 * 		<bean id="finder" class="ru.it.lecm.integrotest.beans.FinderBean">
 * 			<property name="nodeService" ref="nodeService"/>
 * 			<property name="nsService" ref="namespaceService"/>
 * 		</bean>
 * 
 * 		<!-- инициализация QName для поля должности-позиции -->
 * 		<bean id="propname_position"
 * 				class="org.alfresco.service.namespace.QName"
 * 				factory-bean="finder"
 * 				factory-method="makeQName">
 * 			<constructor-arg type="java.lang.String" value="lecm-orgstr:position" />
 * 		</bean>
 *
 * 		<!-- инициализация QName для типа 'Структурное подразделение' орг-штатки
 * 			тип и название атрибута указаны единой строкой с префиксом типа
 * 		  -->
 * 		<bean id="typename_OU"
 * 				factory-bean="finder"
 * 				factory-method="makeQName">
 * 			<constructor-arg type="java.lang.String" value="lecm-orgstr:organization-unit" />
 * 		</bean>
 *
 * 		<!-- в 2: инициализация QName для типа 'Штатное расписание' орг-штатки
 * 			тип и название атрибута указаны разыми параметрами, тип в виде префикса
 * 		  -->
 * 		<bean id="typename_staffList"
 * 				factory-bean="finder"
 * 				factory-method="makeQName">
 * 			<constructor-arg type="java.lang.String" value="lecm-orgstr" />
 * 			<constructor-arg type="java.lang.String" value="staff-list" />
 * 		</bean>
 * 
 * 		<!-- в 3: инициализация QName для типа 'Должность' орг-штатки
 * 			тип и название атрибута указаны разыми параметрами, тип в полной форме
 * 		  -->
 * 		<bean id="typename_staffPosition"
 * 				factory-bean="finder"
 * 				factory-method="makeQName">
 * 			<constructor-arg type="java.lang.String" value="http://www.it.ru/lecm/org/structure/1.0" />
 * 			<constructor-arg type="java.lang.String" value="staffPosition" />
 * 		</bean>
 * 
 * @author Ruslan
 *
 */
public class FinderBeanImpl extends BaseBean implements FinderBean {

	public static QName PROP_EMPLOYEE_FIRSTNAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee-first-name");
	public static QName PROP_UNIT_CODE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "unit-code");

	final static Logger logger = LoggerFactory.getLogger(FinderBeanImpl.class);

	private NamespaceService ns;
	private SearchService searchService;

	public void init() {
		logger.debug("initializing");
		PropertyCheck.mandatory(this, "nodeService", this.nodeService);
		PropertyCheck.mandatory(this, "ns", this.ns);
		logger.info("initialized");
	}

	public NamespaceService getNsService() {
		return ns;
	}

	public void setNsService(NamespaceService value) {
		this.ns = value;
	}

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	public String resolveUri(String uriOrPrefix) {
		if (uriOrPrefix == null)
			return null;
		// принимаем, что полные имена обязательно содержат "/", а префиксы - никогда
		final boolean alreadyFullName  = uriOrPrefix.contains("/");
		return (alreadyFullName) ? uriOrPrefix : ns.getNamespaceURI(uriOrPrefix);
	}

	@Override
	public QName makeQName(String qname) {
		return QName.createQName( qname, ns);
	}

	@Override
	public QName makeQName(String uri, String part) {
		return QName.createQName( resolveUri(uri), part);
	}

	@Override
	public NodeRef findNodeByProp( String nodeType, String propName, String value) {
		return findNodeByProp( makeQName(nodeType), makeQName(propName), value);
	}

	// DONE: сделать поиск через SearchService или хотя бы отфильтровать по значениям в цикле
	@Override
	public NodeRef findNodeByProp( QName typeName, QName propName, String value) {
		final List<NodeRef> found = findNodesByProp(typeName, propName, value);

		// DONE: here can use check if result contains exactly one node
		if (found != null && found.size() != 1)
			logger.warn( String.format( "Single node needed but found %s", found.size()) );

		return (found != null && found.size() > 0) ? found.get(0) : null;
	}

	@Override
	public List<NodeRef> findNodesByProp( String nodeType, String propName, String value) {
		return findNodesByProp( makeQName(nodeType), makeQName(propName), value);
	}

	@Override
	public List<NodeRef> findNodesByProp( QName typeName, QName propName, String value) {
	
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( 
				"Exec search query :\n\t for type='%s'\n\t where '%s'='%s'", typeName, propName, value));
		}
		/*
		 * @NOTE: метод findNodes для nodeService явно поднимает исключение "java.lang.UnsupportedOperationException"
		final FindNodeParameters p = new FindNodeParameters();
		p.setNodeTypes( Arrays.asList(typeName));
		p.setLimit(1);
		List<NodeRef> found = null;
		try {
			found = nodeService.findNodes(p);
		} catch (Throwable ex) {
			logger.error( String.format( "Exception finding object\n\t of type:%s\n\t by field:%s\n\t with value:%s", typeName, prop, value), ex);
		}
		 */

		final Map<String, Object> args = new HashMap<String, Object>();
		args.put( propName.toString(), value);
		final NodeRef parentNode = null;

		try {
			final StringBuilder queryBuf = makeSearchQuery(new StringBuilder(), typeName.toString(), args, parentNode); 

			/* параметры Lucene поиска */
			final SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			// sp.setLanguage(SearchService.LANGUAGE_LUCENE); //  Lucene
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO); // FTS (!)

			/* добавление параметров pg_limit/pg_offset ...
			sp.setMaxItems(maxItems);
			sp.setSkipCount(offset);
			 */

			// Execute:
			// example: TYPE:"{http://www.it.ru/lecm/model/blanks/1.0}blank" AND @"{http://www.it.ru/lecm/model/blanks/1.0}status":New AND @"{http://www.it.ru/lecm/model/blanks/1.0}flag":false
			sp.setQuery(queryBuf.toString());

			final ResultSet found = searchService.query(sp);

			// return (found != null && !found.isEmpty()) ? found.get(0) : null;
			if (found != null) {
				final List<NodeRef> result = new ArrayList<NodeRef>(); 
				for(ResultSetRow row : found) {
					// return row.getNodeRef(); // found something ...
					result.add(row.getNodeRef());
				}
				if (result.size() > 0)
					return result;
			}
		} catch (LuceneQueryParserException ex) {
			final String info = String.format( 
					"Exec search query error:\n\t for type='%s'\n\t where '%s'='%s'"
					, typeName, propName, value);
			logger.error( info + ex.getMessage());
			// throw new RuntimeException( info, ex);
		}

		// nothing found -> ERROR 
		throw new RuntimeException( String.format( "Object not found: \n\t of type:%s\n\t by field:%s\n\t with value:%s", typeName, propName, value));
	}

	@Override
	public NodeRef findNodeByProp( Map<String,Object> args) {
		if (args == null)
			return null;
		return findNodeByProp( ""+ args.get(FinderBean.TYPE_NODE), ""+ args.get(FinderBean.PROP_NAME), ""+ args.get(FinderBean.PROP_VALUE));
	}

	@Override
	public NodeRef findOUByName(String value) {
		return findNodeByProp(OrgstructureBean.TYPE_ORGANIZATION_UNIT, PROP_UNIT_CODE, value);
	}

	@Override
	public NodeRef findDpByName(String value) {
		return findNodeByProp(OrgstructureBean.TYPE_POSITION, ContentModel.PROP_NAME, value);
	}

	@Override
	public NodeRef findEmployeeByName(String value) {
		return findNodeByProp(OrgstructureBean.TYPE_EMPLOYEE, PROP_EMPLOYEE_FIRSTNAME, value);
	}


	static String quots(final String value) {
		// return StringUtils.quote(value);
		return (value == null) ? null : '"'+ value + '"';
	}


	@Override
	public List<NodeRef> getParents(NodeRef ref) {
		if (ref == null)
			return null;
		final List<ChildAssociationRef> assocs = nodeService.getParentAssocs(ref);
		if (assocs == null || assocs.isEmpty())
			return null;
		final List<NodeRef> result = new ArrayList<NodeRef>();
		for(ChildAssociationRef a: assocs) {
			if (a.isPrimary()) // главный родитель - д.б. первым
				result.add(0, a.getParentRef());
			else 
				result.add(a.getParentRef());
		}

		return result;
	}

	/**
	 * Сформировать Search-запрос.
	 * @param dest целевой буфер
	 * @param typename искомый тип (возможно с префиксом)
	 * @param searchArgs список атрибутов и значений для поиска
	 * @param parentNode родительский узел, внутри которого надо выполнять поиск,
	 * если Null, то не используется.
	 * @return в буфере формируется текст запроса в виде: TYPE:[доверенность] AND [условия на атрибуты]
	 * пример:
	 * TYPE:"{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}procuracy" AND @{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}canpropogate:"false" AND @{http://www.it.ru/lecm/model/business/authority/delegations/structure/1.0}canpostprocess:"false"
	 */
	public StringBuilder makeSearchQuery(
				final StringBuilder dest
				, final String typename
				, final Map<String, Object> searchArgs
				, NodeRef parentNode
	) {
		/* фиксируем тип Доверенностей... */
		// dest.append( String.format( "TYPE:\"{%s}%s\"", uri, typename));
		dest.append( String.format( "TYPE:\"%s\"", resolveUri(typename)) );

		/* если задан родитель - добавляем ... */
		if (parentNode != null)
			dest.append( String.format( " AND PARENT:\"%s\"", parentNode));

		/* добавление условий поиска по атрибутам, если есть */
		if (searchArgs != null && searchArgs.entrySet() != null) {
			for (Map.Entry<String, Object> entry: searchArgs.entrySet()) {
				try {
					if (entry.getValue() == null) continue;

					// example: +TYPE:"sys:base" -@test\:two:"value_must_not_match"
					// example: @test\:one:"maymatch" OR @test\:two:"may_match"
					// example: @test\:one:"mustmatch" AND NOT @test\:two:"value_must_not_match"
					dest.append(String.format(" AND @%s:%s", resolveUri(entry.getKey()),  quots(entry.getValue().toString()) ));
			} catch (Throwable ex) {
					final String info = String.format( 
							"Build search query error for key='%s', value='%s'"
							, entry.getKey(), entry.getValue());
					logger.error( info, ex);
					throw new RuntimeException( info, ex);
				}
			}
		}
		return dest;
	}

	@Override
	public void ensureNodePresent( Map<String, Object> args, String argNodeId, String argNodeRef) {
		if (args == null) return;

		if (args.containsKey(argNodeId) && args.get(argNodeId) != null)
			// Основное значение уже заполнено ...
			return;

		if (!args.containsKey(argNodeRef) || args.get(argNodeRef) == null)
			// Нет основного знеачения, но и поискового тоже нет ...
			return;

		// выполняем поиск
		final Map<String, String> searchArgs = Utils.makeArgsMap( args.get(argNodeRef).toString() );
		final NodeRef ref = this.findNodeByProp( (Map) searchArgs);

		// вносим загруженное значение
		if (ref != null)	
			args.put( argNodeId,ref.getId());
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
