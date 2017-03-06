/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.businessjournal.beans;

import ru.it.lecm.businessjournal.beans.util.CustomURLBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.alfresco.service.cmr.repository.NodeRef;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord.Field;

/**
 *
 * @author ikhalikov
 */
public class RESTClient extends AbstractBusinessJournalService implements BusinessJournalService {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RESTClient.class);

	private ObjectMapper mapper;
	private Client client;
	private String serviceAddress;
	private String serviceProtocol;
	private String serviceHost;
	private String servicePort;
	private String serviceName;
	private SimpleDateFormat dateFormat;
	private Properties globalProps;

	public void setGlobalProps(Properties globalProps) {
		this.globalProps = globalProps;
	}

	public void setServiceProtocol(String serviceProtocol) {
		this.serviceProtocol = serviceProtocol;
	}

	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void init() {
		bjRootID = BJ_ROOT_ID;
		bjArchiveID = BJ_ARCHIVE_ROOT_ID;

		ClientConfig clientConfig = new DefaultClientConfig();
		mapper = new ObjectMapper();
		dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		mapper.setDateFormat(dateFormat);
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);

		String serviceAddressTmpl = "%s://%s:%s/%s/rest";
		if(serviceProtocol == null || serviceHost == null || servicePort == null) {
			String protocol = (String) globalProps.get("alfresco.protocol");
			String host = (String) globalProps.get("alfresco.host");
			String port = (String) globalProps.get("alfresco.port");
			serviceAddress = String.format(serviceAddressTmpl, protocol, host, port, serviceName);
		} else {
			serviceAddress = String.format(serviceAddressTmpl, serviceProtocol, serviceHost, servicePort, serviceName);
		}
	}

	@Override
	public void saveToStore(BusinessJournalRecord record) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public NodeRef getServiceRootFolder() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<BusinessJournalRecord> getRecordsByInterval(Date begin, Date end) {

		CustomURLBuilder builder = new CustomURLBuilder(serviceAddress);
		if (begin != null) {
			builder.gt("date", begin.toString());
		}
		if (end != null) {
			builder.and().ls("date", end.toString());
		}

		WebResource webResource = null;
		webResource = client.resource(builder.toString());
		String response = webResource.accept("application/json").get(String.class);
		List<BusinessJournalRecord> res = new ArrayList<>();;
		try {
			res = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
					List.class, BusinessJournalRecord.class));
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}
		return res;
	}

	@Override
	public List<BusinessJournalRecord> getRecordsByParams(String objectTypeRefs, String eventCategories, Date begin, Date end, String whoseKey, Boolean checkMainObject) {
		return getRecordsByParams(objectTypeRefs, eventCategories, begin, end, whoseKey, checkMainObject, null, null);
	}

	@Override
	public List<BusinessJournalRecord> getRecordsByParams(String objectTypeRefs, String eventCategories, Date begin, Date end, String whoseKey, Boolean checkMainObject, Integer skipCount, Integer maxItems) {
		CustomURLBuilder builder = new CustomURLBuilder(serviceAddress);
		builder.path("ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord");

		List<String> objectTypes = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(objectTypeRefs, ",");
		while (st.hasMoreTokens()) {
			String nodeRefStr = st.nextToken().trim();
			if (NodeRef.isNodeRef(nodeRefStr) && nodeService.exists(new NodeRef(nodeRefStr))) {
				objectTypes.add(nodeRefStr);
			}
		}

		List<String> eventCats = new ArrayList<>();
		if (eventCategories != null) {
			StringTokenizer stEventCategories = new StringTokenizer(eventCategories, ",");
			while (stEventCategories.hasMoreTokens()) {
				String nodeRefStr = stEventCategories.nextToken().trim();
				if (NodeRef.isNodeRef(nodeRefStr) && nodeService.exists(new NodeRef(nodeRefStr))) {
					eventCats.add(nodeRefStr);
				}
			}
		}

		List<String> employees = new ArrayList<>();
		if (whoseKey != null && !whoseKey.isEmpty()) {
			switch (WhoseEnum.valueOf(whoseKey.toUpperCase())) {
				case MY: {
					NodeRef employee = orgstructureService.getCurrentEmployee();
					employees.add(employee.toString());
					break;
				}
				case DEPARTMENT: {
					NodeRef boss = orgstructureService.getCurrentEmployee();

					if (boss != null) {
						for (NodeRef employee : orgstructureService.getBossSubordinate(boss)) {
							employees.add(employee.toString());
						}
						employees.add(boss.toString());
					}
					break;
				}
				case ORGANIZATION: {
					NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
					if (currentEmployee != null) {
						NodeRef organization = orgstructureService.getEmployeeOrganization(currentEmployee);
						if (organization != null) {
							for (NodeRef employee : orgstructureService.getOrganizationEmployees(organization)) {
								employees.add(employee.toString());
							}
						}
					}
					break;
				}
				case CONTROL: {
					//todo
					break;
				}
				default: {

				}
			}
		}

		boolean isFiltered = false;

		if (objectTypes.size() == 1)  {
			isFiltered = true;
			builder.and().eq("objectType", "'" + objectTypes.get(0) + "'");
		}

		if (employees.size() == 1 && !isFiltered) {
			isFiltered = true;
			builder.and().eq("initiator", "'" + employees.get(0) + "'");
		}

		if (eventCats.size() == 1 && !isFiltered) {
			builder.and().eq("eventCategory", "'" + eventCats.get(0) + "'");
		}

		builder.sort("date", false);

		if (skipCount != null && maxItems != null) {
			builder.range(skipCount, skipCount + maxItems);
		}
		WebResource webResource = client.resource(builder.toString().replace(" ", "+"));
		String response = webResource.accept("application/json").get(String.class);
		List<BusinessJournalRecord> res = new ArrayList<>();;
		try {
			res = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
					List.class, BusinessJournalRecord.class));
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}

		List<BusinessJournalRecord> filtered = new ArrayList<>();
		for (BusinessJournalRecord record : res) {
			NodeRef initiator = record.getInitiator();
			if (employees.isEmpty() || (initiator != null && employees.contains(initiator.toString()))) {
				String recordEventCategory = record.getEventCategory() != null ? record.getEventCategory().toString() : null;
				if (eventCats.isEmpty() || eventCats.contains(recordEventCategory)) {
					String recordObjectType = record.getObjectType() != null ? record.getObjectType().toString() : null;
					if (objectTypes.isEmpty() || objectTypes.contains(recordObjectType)) {
						if ((begin == null || record.getDate().after(begin)) && (end != null && record.getDate().before(end))) {
							filtered.add(record);
						}
					}
				}
			}
		}
		return filtered;
	}

	@Override
	@Deprecated
	public boolean moveRecordToArchive(Long recordId) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public BusinessJournalRecord getNodeById(Long nodeId) {
		CustomURLBuilder builder = new CustomURLBuilder(serviceAddress);
		builder.path("ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord");

		builder.eq("nodeId", nodeId.toString());

		WebResource webResource = client.resource(builder.toString());
		String response = webResource.accept("application/json").get(String.class);
		List<BusinessJournalRecord> res = new ArrayList<>();;
		try {
			res = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
					List.class, BusinessJournalRecord.class));
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}

		if (res.isEmpty()) {
			return null;
		}
		return res.get(0);
	}

	@Override
	public List<BusinessJournalRecord> getStatusHistory(NodeRef nodeRef, String sortColumnLocalName, boolean sortAscending) {
		List<NodeRef> categories = new ArrayList<>();
		NodeRef changeCategory = getEventCategoryByCode("CHANGE_DOCUMENT_STATUS");
		if (changeCategory != null) {
			categories.add(changeCategory);
		}
		NodeRef addCategory = getEventCategoryByCode("ADD");
		if (addCategory != null) {
			categories.add(addCategory);
		}

		CustomURLBuilder builder = new CustomURLBuilder(serviceAddress);
		builder.path("ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord");

		builder.and().eq("mainObject", "'" + nodeRef.toString() + "'");

		builder.sort("date", sortAscending);
		WebResource webResource = client.resource(builder.toString());
		String response = webResource.accept("application/json").get(String.class);
		List<BusinessJournalRecord> res = new ArrayList<>();;
		try {
			res = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
					List.class, BusinessJournalRecord.class));
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}

		List<BusinessJournalRecord> filtered = new ArrayList<>();
		for (BusinessJournalRecord record : res) {
			if (categories.isEmpty() || categories.contains(record.getEventCategory())) {
				filtered.add(record);
			}
		}

		return filtered;

	}

	@Override
	public List<BusinessJournalRecord> getHistory(NodeRef nodeRef, String sortColumnLocalName, boolean sortAscending, boolean includeSecondary, boolean showInactive) {

		CustomURLBuilder builder = new CustomURLBuilder(serviceAddress);
		builder.path("ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord");

		builder.eq("mainObject", "'" + nodeRef.toString() + "'");
		builder.sort("date", sortAscending);

		WebResource webResource = client.resource(builder.toString());
		String response = webResource.accept("application/json").get(String.class);
		List<BusinessJournalRecord> res = new ArrayList<>();;
		try {
			res = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
					List.class, BusinessJournalRecord.class));
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}

		if (includeSecondary) {
			CustomURLBuilder secondary = new CustomURLBuilder(serviceAddress);
			secondary.path("ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord");

			secondary.eq("object1Id", "'" + nodeRef.toString() + "'");

			webResource = client.resource(secondary.toString());
			response = webResource.accept("application/json").get(String.class);
			List<BusinessJournalRecord> secondaryRecords = new ArrayList<>();;
			try {
				secondaryRecords = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
						List.class, BusinessJournalRecord.class));
			} catch (IOException ex) {
				logger.error("Something gone wrong, while getting BJ records", ex);
			}
			res.addAll(secondaryRecords);
		}

		return res;
	}

	@Override
	public List<BusinessJournalRecord> getLastRecords(int maxRecordsCount, boolean includeFromArchive) {

		CustomURLBuilder builder = new CustomURLBuilder(serviceAddress);
		builder.path("ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord");

		if (!includeFromArchive) {
			builder.eq("isActive", "true");
		}

		builder.range(0, maxRecordsCount);

		WebResource webResource = client.resource(builder.toString());
		String response = webResource.accept("application/json").get(String.class);
		List<BusinessJournalRecord> res = new ArrayList<>();;
		try {
			res = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
					List.class, BusinessJournalRecord.class));
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}
		return res;

	}

	@Override
	public List<BusinessJournalRecord> getRecords(Field sortField, boolean ascending, int startIndex, int maxResults, Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived) {

		CustomURLBuilder builder = new CustomURLBuilder(serviceAddress);
		builder.path("ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord");

		Date begin = null;
		Date end = null;
		String objectType = "";
		String initiator = "";
		String eventCategory = "";
		String mainObject = "";
		boolean isFiltered = false;

		List<Field> allowedToDBSearch = Arrays.asList(Field.DATE, Field.EVENT_CATEGORY, Field.INITIATOR, Field.OBJECT_TYPE, Field.MAIN_OBJECT);

		for (Map.Entry<Field, String> entry : filter.entrySet()) {
			if (entry.getKey() == null) {
				continue;
			}
			Field field = entry.getKey();
			String value = entry.getValue();
			if (!allowedToDBSearch.contains(field)) {
				continue;
			}
			if (!value.isEmpty()) {
				if (Field.DATE.equals(field)) {
					String[] dates = value.split("\\|");
					SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					try {
						begin = dtFormat.parse(dates[0]);
						end = dtFormat.parse(dates[1]);
					} catch (ParseException ex) {
						logger.error("Can't parse dates", ex);
					}
				} else {
					if (isFiltered) {
						switch (field) {
							case INITIATOR:
								initiator = value;
								break;
							case OBJECT_TYPE:
								objectType = value;
								break;
							case EVENT_CATEGORY:
								eventCategory = value;
								break;
							case MAIN_OBJECT:
								mainObject = value;
								break;
						}
					} else {
						builder.andOr(andFilter).eq(getSortField(field), "'" + value + "'");
						isFiltered = true;
					}
				}
			}
		}

		if (!builder.isHaveFilter()) {
			builder.eq("dummy", "0");
		}

		builder.sort("date", ascending);
		builder.range(startIndex, startIndex + maxResults);


		WebResource webResource = client.resource(builder.toString().replace(" ", "+"));
		String response = webResource.accept("application/json").get(String.class);
		List<BusinessJournalRecord> res = new ArrayList<>();
		try {
			res = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(
					List.class, BusinessJournalRecord.class));
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}

		String descr = filter.get(Field.RECORD_DESCRIPTION);
		List<BusinessJournalRecord> filtered = new ArrayList<>();
		for (BusinessJournalRecord record : res) {
			if (initiator.isEmpty() || initiator.equals(record.getInitiator().toString())) {
				if (mainObject.isEmpty() || mainObject.equals(record.getMainObject().toString())) {
					if (eventCategory.isEmpty() || eventCategory.equals(record.getEventCategory().toString())) {
						if (objectType.isEmpty() || objectType.equals(record.getObjectType().toString())) {
							if ((begin != null && record.getDate().after(begin) || begin == null) && (end != null && record.getDate().before(end) || end == null)) {
								if (descr == null || (descr != null && !descr.isEmpty() && record.getRecordDescription().matches(".*" + descr + ".*"))) {
									filtered.add(record);
								}
							}
						}
					}
				}
			}
		}

		return filtered;
	}

	@Override
	public Integer getRecordsCount(Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived) {
		String query = serviceAddress + "/ru.it.lecm.businessjournal.remote.RecordsCount/0";
		WebResource webResource = client.resource(query);
		String response = webResource.accept("application/json").get(String.class);
		try {
			Map<String, Object> data = mapper.readValue(response, Map.class);
			return (Integer) data.get("count");
		} catch (IOException ex) {
			logger.error("Something gone wrong, while getting BJ records", ex);
		}
		return null;

	}

	@Override
	@Deprecated
	public List<BusinessJournalRecord> getRecordsAfter(Long lastRecordId) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private String getSortField(BusinessJournalRecord.Field field) {
		String sortColumn = field.toString().toLowerCase();
		if (field.equals(BusinessJournalRecord.Field.OBJECT_1_TEXT)) {
			sortColumn = "obj_1_string_value";
		}
		if (field.equals(BusinessJournalRecord.Field.OBJECT_2_TEXT)) {
			sortColumn = "obj_2_string_value";
		}
		if (field.equals(BusinessJournalRecord.Field.OBJECT_3_TEXT)) {
			sortColumn = "obj_3_string_value";
		}
		if (field.equals(BusinessJournalRecord.Field.OBJECT_4_TEXT)) {
			sortColumn = "obj_4_string_value";
		}
		if (field.equals(BusinessJournalRecord.Field.OBJECT_5_TEXT)) {
			sortColumn = "obj_5_string_value";
		}
		if (field.equals(BusinessJournalRecord.Field.MAIN_OBJECT_TEXT)) {
			sortColumn = "mainObjectText";
		}
		if (field.equals(BusinessJournalRecord.Field.INITIATOR)) {
			sortColumn = "initiator";
		}
		if (field.equals(BusinessJournalRecord.Field.MAIN_OBJECT)) {
			sortColumn = "mainObject";
		}
		if (field.equals(BusinessJournalRecord.Field.EVENT_CATEGORY)) {
			sortColumn = "eventCategory";
		}
		if (field.equals(BusinessJournalRecord.Field.OBJECT_TYPE)) {
			sortColumn = "objectType";
		}
		if (field.equals(BusinessJournalRecord.Field.RECORD_DESCRIPTION)) {
			sortColumn = "recordDescription";
		}
		return sortColumn;
	}

}
