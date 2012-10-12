package ru.it.lecm.business.calendar;

import java.util.Date;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.it.lecm.business.Utils;

/**
 * @author raa
 * Date: 2012/10/05
 */
public class BusinessCalendarBean 
		extends BaseProcessorExtension
		implements IBusinessCalendar
{

	protected static Log logger = LogFactory.getLog(BusinessCalendarBean.class);
	private ServiceRegistry serviceRegistry;

	@Override
	public String toString() {
		// return super.toString();
		return this.getClass().getName();
	}


	@Override
	public void register() {
		super.register();
		logger.info("registered");
	}

/*
	@Override
	public void setProcessor(Processor processor) {
		super.setProcessor(processor);
	}

	@Override
	public String getExtensionName() {
		return super.getExtensionName();
	}
 */

	@Override
	public void setExtensionName(String extension) {
		super.setExtensionName(extension);
		logger.info( String.format("extension name changed to '%s'", extension) );
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}


	// private final static String ROOTAPP = "app:company_home";
	private final static String MODELTYPE_PREFIX ="lecm-bscalendar:";

	private final static String MODELTYPE_CALENDAR = MODELTYPE_PREFIX+ "businessCalendar";
	private static final String MODELTYPE_NAME = MODELTYPE_PREFIX+ "descriptionCalendar";
	private static final String MODELTYPE_DESC = MODELTYPE_PREFIX+ "nameCalendar";

	private static final String MODELTYPE_OWNERTYPE = MODELTYPE_PREFIX+ "ownerType";
	private static final String MODELTYPE_MODERATORTYPE = MODELTYPE_PREFIX+ "moderatorTypeCalendar";


	// private final static StoreRef STOREREF = new StoreRef(ROOTAPP + '/'+ MODELTYPE_CALENDAR);
	private final static String CALENDAR_PATH = "//app:company_home/"+ MODELTYPE_CALENDAR; // "/app:company_home/st:sites/cm:fardo/cm:documentLibrary/fd:templates";

	@Override
	public JSONArray getCalendar(String calendarId) {
		final JSONArray result = new JSONArray();

		// final NodeRef root = serviceRegistry.getNodeService().getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		// final List<NodeRef> calendars = (root != null) ? root.getNodeRefs(calendarId) : null;

		final ResultSet answer = serviceRegistry.getSearchService().query(
				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE
				, SearchService.LANGUAGE_XPATH 
				, CALENDAR_PATH + "/*" 
						+ ( 
								(calendarId == null || calendarId.length() == 0)
								? "" : String.format("[@sys:id='%s']", calendarId)
							
						)
		);
		if (answer == null || !answer.hasMore()) {
			result.put("no data");
		} else { 
			for(NodeRef ref: answer.getNodeRefs()) {
				addCalendar( ref, result);
			}
		}
		return result;
	}


	private void addCalendar(NodeRef ref, JSONArray dest) {
		// if (ref == null) return;
		final JSONObject result = new JSONObject();

		final NodeService nodeService = serviceRegistry.getNodeService();
		final QName qType = nodeService.getType(ref);

		try {
			result.put("title", Utils.getElementName(nodeService, ref));
			result.put("nodeRef", ref.toString());
			result.put("type", qType.toString());
			result.put("isLeaf", false);

			result.put("name", Utils.getElementName( nodeService, ref, QName.createQName(MODELTYPE_NAME)));
			result.put("description", Utils.getElementName( nodeService, ref, QName.createQName(MODELTYPE_DESC)));
			result.put("ownertype", Utils.getElementName( nodeService, ref, QName.createQName(MODELTYPE_OWNERTYPE)));
			result.put("moderatortype", Utils.getElementName( nodeService, ref, QName.createQName(MODELTYPE_MODERATORTYPE)));
		} catch (JSONException ex) {
			logger.error(ex);
		}
	}


	@Override
	public String createCalendar(JSONArray data) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void updateCalendar(String calendarId, JSONArray data) {
		// TODO Auto-generated method stub
	}


	@Override
	public void deleteCalendar(String calendarId) {
		// TODO Auto-generated method stub
	}


	@Override
	public JSONArray getEvents(String calendarId, Date dateFrom, Date dateTo) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String createEvent(String calendarId, JSONArray data) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void updateEvent(String eventId, JSONArray data) {
		// TODO Auto-generated method stub
	}


	@Override
	public void deleteEvent(String eventId) {
		// TODO Auto-generated method stub

	}

/*
	public String getRoots(final String type, final String ref) {
		JSONArray nodes = new JSONArray();
		NodeService nodeService = serviceRegistry.getNodeService();
		if (ref == null) {
			return nodes.toString();
		}
		final NodeRef currentRef = new NodeRef(ref);
		if (type == null || type.equals("_ROOT_")) {
			JSONObject root;
			List<ChildAssociationRef> childs = nodeService.getChildAssocs(currentRef);
			for (ChildAssociationRef childAssociationRef : childs) {
				QName qType = nodeService.getType(childAssociationRef.getChildRef());
				String qTypeLocalName = qType.getLocalName();
				try {
					NodeRef cRef = childAssociationRef.getChildRef();

					root = new JSONObject();
					root.put(TITLE, getElementName(nodeService, cRef));
					root.put(NODE_REF, cRef.toString());
					root.put(TYPE, "lecm-orgstr:" + qTypeLocalName);
					root.put(IS_LEAF, false);

					// РЎРїРёСЃРѕРє СЃРїСЂР°РІРѕС‡РЅРёРєРѕРІ РїРѕ РєРѕС‚РѕСЂС‹Рј Р±СѓРґРµС‚ РІРµСЃС‚РёСЃСЊ СЂР°Р±РѕС‚Р°
					// TODO РІРѕР·РјРѕР¶РЅРѕ, РІС‹РЅРµСЃС‚Рё РІ Enum
					if (qTypeLocalName.equals(DIRECTORY_EMPLOYEES)) {
						root.put(CHILD_TYPE, TYPE_EMPLOYEE);
						root.put(DS_URI, DEFAULT_URI);
					} else if (qTypeLocalName.equals(DIRECTORY_PROJECTS)) {
						root.put(CHILD_TYPE, TYPE_PROJECT);
						root.put(DS_URI, PROJECTS_URI);
					} else if (qTypeLocalName.equals(DIRECTORY_STAFF_LIST)) {
						root.put(CHILD_TYPE, TYPE_POSITION);
						root.put(DS_URI, DEFAULT_URI);
					} else if (qTypeLocalName.equals(DIRECTORY_STRUCTURE)) {
						root.put(CHILD_TYPE, TYPE_UNIT);
						root.put(DS_URI, UNIT_EMPLOYEES_URI);
					}
					nodes.put(root);
				} catch (JSONException e) {
					logger.error(e);
				}
			}
		}
		return nodes.toString();
	}
 * */	
}