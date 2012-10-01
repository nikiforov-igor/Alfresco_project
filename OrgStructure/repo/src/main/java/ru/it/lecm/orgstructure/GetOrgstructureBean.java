package ru.it.lecm.orgstructure;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author dbashmakov
 *         Date: 30.09.12
 */
public class GetOrgstructureBean extends BaseProcessorExtension {

	public static final String TYPE_ORGANIZATION = "_ORGANIZATION_";
	public static final String TYPE_EMPLOYEES = "_EMPLOYEES_";
	public static final String TYPE_PROJECTS = "_PROJECTS_";
	public static final String TYPE_PROJECT = "_PROJECT_";
	public static final String TYPE_STAFF_LIST = "_STAFF_LIST_";
	public static final String TYPE_UNIT = "_UNIT_";
	public static final String TYPE_EMP_ALL = "_EMPLOYEES_ALL_";
	public static final String TYPE_POSITION = "_POSITION_";

	public static final String DIRECTORY_EMPLOYEES = "employee-container";
	public static final String DIRECTORY_PROJECTS = "project-register";
	public static final String DIRECTORY_STAFF_LIST = "staff-list";

	private static final String ORGSTRUCTURE_NAMESPACE_URI = "lecm-orgstr";
	private static final QName DEFAULT_NAME = ContentModel.PROP_NAME;

	public static final String NODE_REF = "nodeRef";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String IS_LEAF = "isLeaf";

	private static ServiceRegistry serviceRegistry;

	private static Log logger = LogFactory.getLog(GetOrgstructureBean.class);

	public String get(final String type, final String ref) {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = serviceRegistry.getNodeService();
		if (type != null && ref != null) {
			final NodeRef currentRef = new NodeRef(ref);
			if (type.equals("_ROOT_")) {
				String orgName = getElementName(nodeService, currentRef, QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-full-name"));
				JSONObject element = new JSONObject();
				try {
					element.put(TITLE, orgName);
					element.put(NODE_REF, ref);
					element.put(TYPE, TYPE_ORGANIZATION);
					element.put(IS_LEAF, false);

					nodes.put(element);
				} catch (JSONException e) {
					logger.error(e);
				}
			} else if (type.equals(TYPE_ORGANIZATION)) {
				JSONObject element = new JSONObject();
				try {
					element.put(TITLE, "Структура");
					element.put(NODE_REF, ref);
					element.put(TYPE, TYPE_UNIT);
					element.put(IS_LEAF, false);
					
					nodes.put(element);
				} catch (JSONException e) {
					logger.error(e);
				}

				List<ChildAssociationRef> childs = nodeService.getChildAssocs(currentRef);
				for (ChildAssociationRef childAssociationRef : childs) {
					QName qType = nodeService.getType(childAssociationRef.getChildRef());
					try {
						if (qType.getLocalName().equals(DIRECTORY_EMPLOYEES)) {
							NodeRef cRef = childAssociationRef.getChildRef();

							element = new JSONObject();
							element.put(TITLE, getElementName(nodeService, cRef));
							element.put(NODE_REF, cRef.toString());
							element.put(TYPE, TYPE_EMPLOYEES);
							element.put(IS_LEAF, false);

							nodes.put(element);
						} else if (qType.getLocalName().equals(DIRECTORY_PROJECTS)) {
							NodeRef cRef = childAssociationRef.getChildRef();

							element = new JSONObject();
							element.put(TITLE, getElementName(nodeService, cRef));
							element.put(NODE_REF, cRef.toString());
							element.put(TYPE, TYPE_PROJECTS);
							element.put(IS_LEAF, false);

							nodes.put(element);
						} else if (qType.getLocalName().equals(DIRECTORY_STAFF_LIST)) {
							NodeRef cRef = childAssociationRef.getChildRef();

							element = new JSONObject();
							element.put(TITLE, getElementName(nodeService, cRef));
							element.put(NODE_REF, cRef.toString());
							element.put(TYPE, TYPE_STAFF_LIST);
							element.put(IS_LEAF, false);

							nodes.put(element);
						}
					} catch (JSONException e) {
						logger.error(e);
					}
				}
			} else {
					if (type.equalsIgnoreCase(TYPE_UNIT)) {
						SearchParameters sp = new SearchParameters();
						sp.addStore(currentRef.getStoreRef());
						sp.setLanguage(SearchService.LANGUAGE_LUCENE);
						sp.setQuery("PARENT: \"" + ref + "\" AND TYPE:\"lecm-orgstr:organization-unit\"");
						ResultSet results = null;
						try {
							results = serviceRegistry.getSearchService().query(sp);
							for (ResultSetRow row : results) {
								NodeRef unitRef = row.getNodeRef();
								JSONObject unit = new JSONObject();
								try {
									unit.put(NODE_REF, unitRef.toString());
									unit.put(TYPE, TYPE_UNIT);
									unit.put(TITLE, getElementName(
											nodeService, unitRef, QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-full-name")));
									unit.put(IS_LEAF, nodeService.getChildAssocs(
											unitRef, RegexQNamePattern.MATCH_ALL, RegexQNamePattern.MATCH_ALL, false).isEmpty());

									nodes.put(unit);
								} catch (JSONException e) {
									logger.error(e);
								}
							}
						} finally {
							if (results != null) {
								results.close();
							}
						}
					} else if (type.equals(TYPE_EMPLOYEES)) {
						JSONObject employees = new JSONObject();
						try {
							employees.put(TITLE, "Все");
							employees.put(NODE_REF, ref);
							employees.put(TYPE, TYPE_EMP_ALL);
							employees.put(IS_LEAF, true);

							nodes.put(employees);
						} catch (JSONException e) {
							logger.error(e);
						}
					} else if (type.equals(TYPE_PROJECTS)) {
						SearchParameters sp = new SearchParameters();
						sp.addStore(currentRef.getStoreRef());
						sp.setLanguage(SearchService.LANGUAGE_LUCENE);
						sp.setQuery("PARENT: \"" + ref + "\" AND TYPE:\"lecm-orgstr:project\"");
						ResultSet results = null;
						try {
							results = serviceRegistry.getSearchService().query(sp);
							for (ResultSetRow row : results) {
								NodeRef projectRef = row.getNodeRef();
								JSONObject project = new JSONObject();
								try {
									project.put(NODE_REF, projectRef.toString());
									project.put(TYPE, TYPE_PROJECT);
									project.put(TITLE, getElementName(
											nodeService, projectRef, QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "element-full-name")));
									project.put(IS_LEAF, true);

									nodes.put(project);
								} catch (JSONException e) {
									logger.error(e);
								}
							}
						} finally {
							if (results != null) {
								results.close();
							}
						}
					} else if (type.equals(TYPE_STAFF_LIST)) {
						SearchParameters sp = new SearchParameters();
						sp.addStore(currentRef.getStoreRef());
						sp.setLanguage(SearchService.LANGUAGE_LUCENE);
						sp.setQuery("PARENT: \"" + ref + "\" AND TYPE:\"lecm-orgstr:position\"");
						ResultSet results = null;
						try {
							results = serviceRegistry.getSearchService().query(sp);
							for (ResultSetRow row : results) {
								NodeRef positionRef = row.getNodeRef();
								JSONObject position = new JSONObject();
								try {
									position.put(NODE_REF, positionRef.toString());
									position.put(TYPE, TYPE_POSITION);
									position.put(TITLE, getElementName(nodeService,positionRef));
									position.put(IS_LEAF, true);

									nodes.put(position);
								} catch (JSONException e) {
									logger.error(e);
								}
							}
						} finally {
							if (results != null) {
								results.close();
							}
						}
					}
			}
		}
		return nodes.toString();
	}

	private String getElementName(final NodeService service, final NodeRef ref, QName property, QName defaultProperty) {
		String value = null;
		if (property != null) {
			value = (String) service.getProperty(ref, property);
		}
		return value != null ? value : (String) service.getProperty(ref, defaultProperty);
	}

	private String getElementName(final NodeService service, final NodeRef ref, QName property) {
		return getElementName(service, ref, property, DEFAULT_NAME);
	}

	private String getElementName(final NodeService service, final NodeRef ref) {
		return getElementName(service, ref, null, DEFAULT_NAME);
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}
