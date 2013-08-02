package ru.it.lecm.br5.semantic.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.soap.ArrayOfPersonAttrs;
import ru.it.soap.ItsWebServiceSoap;
import ru.it.soap.PersonAttrs;
import ru.it.soap.PersonLoad;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.br5.semantic.api.ConstantsBean;
import static ru.it.lecm.br5.semantic.api.ConstantsBean.PROP_BR5_INTEGRATION_LOADED;
import static ru.it.lecm.br5.semantic.api.ConstantsBean.PROP_BR5_INTEGRATION_VERSION;
import ru.it.lecm.br5.semantic.api.SemanticBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.soap.ArrayOfBase64Binary;
import ru.it.soap.ArrayOfDataItem;
import ru.it.soap.ArrayOfPerson;
import ru.it.soap.ArrayOfString;
import ru.it.soap.DataItem;
import ru.it.soap.Person;

/**
 *
 * @author snovikov
 */
public class SemanticBeanImpl extends BaseBean implements ConstantsBean, SemanticBean {

	private static final Logger logger = LoggerFactory.getLogger(SemanticBeanImpl.class);
	protected ItsWebServiceSoap itsWebService;
	protected OrgstructureBean orgstructureService;
	protected DocumentMembersService documentMembersService;
	protected DocumentAttachmentsService documentAttachmentsService;
	private SearchService searchService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setItsWebService(ItsWebServiceSoap itsWebService) {
		this.itsWebService = itsWebService;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 *
	 * @param expert - employee nodeRef
	 * @return br5 user identifier
	 */
	@Override
	public Integer loadExpertBr5(NodeRef expert) {
		if (orgstructureService.isEmployee(expert)) {
			String expLogin = orgstructureService.getEmployeeLogin(expert);
			PersonLoad pLoad = new PersonLoad();

			ArrayOfPersonAttrs pAttrs = new ArrayOfPersonAttrs();
			String firstName = (String) nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
			//отчество
			String middleName = (String) nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
			//фамилия
			String lastName = (String) nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
			PersonAttrs dsf1 = new PersonAttrs();
			dsf1.setName("surname");
			dsf1.setValue(lastName);
			PersonAttrs dsf2 = new PersonAttrs();
			dsf2.setName("secname");
			dsf2.setValue(middleName);
			PersonAttrs dsf3 = new PersonAttrs();
			dsf3.setName("name");
			dsf3.setValue(firstName);
			PersonAttrs dsf4 = new PersonAttrs();
			dsf4.setName("username");
			dsf4.setValue(expLogin);
			pAttrs.getPersonAttrs().add(dsf1);
			pAttrs.getPersonAttrs().add(dsf2);
			pAttrs.getPersonAttrs().add(dsf3);
			pAttrs.getPersonAttrs().add(dsf4);
			pLoad.setAttrs(pAttrs);
			Integer res = null;
			try {
				res = itsWebService.loadPerson(pLoad);
			} catch (SOAPFaultException soap_exception) {
				logger.error("Can't loadPerson: server is not available. ");
				soap_exception.printStackTrace();
				return null;
			}
			return res;
		}
		return null;
	}

	private byte[] getBinaryFileData(NodeRef file) {
		ContentService contentService = serviceRegistry.getContentService();

		ContentReader reader = contentService.getReader(file, ContentModel.PROP_CONTENT);
		InputStream originalInputStream = reader.getContentInputStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final int BUF_SIZE = 1 << 8; //1KiB buffer
		byte[] buffer = new byte[BUF_SIZE];
		int bytesRead = -1;
		byte[] binaryData = null;
		try {
			while ((bytesRead = originalInputStream.read(buffer)) > -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			binaryData = outputStream.toByteArray();
		} catch (IOException e) {
			logger.error(e.toString());
			return null;
		} finally {
			IOUtils.closeQuietly(originalInputStream);
			IOUtils.closeQuietly(outputStream);
		}

		return binaryData;

	}

	/**
	 * Refresh document tags
	 *
	 * @param documentRef
	 * @return is successful
	 * @throws DatatypeConfigurationException
	 */
	@Override
	public Boolean refreshDocumentTagsBr5(NodeRef documentRef) throws DatatypeConfigurationException {
		Boolean result = false;
		if (!hasBr5Aspect(documentRef)) {
			return result;
		}
		ArrayOfDataItem docItems = null;
		try {
			if (nodeService.getType(documentRef).equals(ContentModel.TYPE_CONTENT)) { // документ alfresco
				String fileName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
				byte[] finalBinaryData = getBinaryFileData(documentRef);
				// получим список термов для документа и запишем в свойство tags документа lecm
				ArrayOfString finalFilesNames = new ArrayOfString();
				ArrayOfBase64Binary finalFilesContent = new ArrayOfBase64Binary();
				finalFilesNames.getString().add(fileName);
				finalFilesContent.getBase64Binary().add(finalBinaryData);
				docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 50);
			} else {
				//документ Lecm
				//запишем в массивы имена и содержимое всех файлов вложений
				ArrayOfString filesNames = new ArrayOfString();
				ArrayOfBase64Binary filesContent = new ArrayOfBase64Binary();
				List<NodeRef> categories = documentAttachmentsService.getCategories(documentRef);
				for (NodeRef category : categories) {
					String categoryName = (String) nodeService.getProperty(category, ContentModel.PROP_NAME);
					List<NodeRef> categoryAttachments = documentAttachmentsService.getAttachmentsByCategory(documentRef, categoryName);
					for (NodeRef categoryAttachment : categoryAttachments) {
						filesNames.getString().add((String) nodeService.getProperty(categoryAttachment, ContentModel.PROP_NAME));
						filesContent.getBase64Binary().add(getBinaryFileData(categoryAttachment));
					}
				}
				ArrayOfString finalFilesNames = filesNames;
				ArrayOfBase64Binary finalFilesContent = filesContent;
				// получим список термов для документа и запишем в свойство tags документа lecm
				docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 50);
			}
			result = true;
		} catch (SOAPFaultException soap_exception) {
			logger.error("Can't loadDocumentBr5: server is unvailable. ");
			soap_exception.printStackTrace();
		}
		if (result && docItems != null) {
			List<DataItem> docItemsLst = docItems.getDataItem();
			HashMap<String, Float> tags = new HashMap<String, Float>();
			for (DataItem docItem : docItemsLst) {
				tags.put(docItem.getSpelling(), docItem.getSignific());
			}
			nodeService.setProperty(documentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, tags);
		}

		return result;
	}

	private Boolean loadAlfDocumentBr5(NodeRef documentRef, String extMembers) throws DatatypeConfigurationException {
		boolean loadOk = false;
		if (documentRef == null) {
			logger.info("loadDocumentBr5: documentFile is null!");
			return loadOk;
		}
		//check aspect exist
		if (!hasBr5Aspect(documentRef)) {
			logger.info("loadDocumentBr5: documentFile has no aspect \"BR5 Integration\"!");
			return loadOk;
		}
		if (((Boolean) nodeService.getProperty(documentRef, PROP_BR5_INTEGRATION_LOADED))
			&& nodeService.getProperty(documentRef, PROP_BR5_INTEGRATION_VERSION).toString().equals(nodeService.getProperty(documentRef, ContentModel.PROP_VERSION_LABEL).toString())) {
			return true;
		}
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar xgregDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		try {
			// проверим к какому типу относится документ (Alfresco или LECM)
			if (nodeService.getType(documentRef).equals(ContentModel.TYPE_CONTENT)) { // документ alfresco
				String fileName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
				String authorName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_CREATOR);
				NodeRef author = orgstructureService.getEmployeeByPerson(authorName);
				if (author == null) {
					logger.info("loadDocumentBr5: author of document " + documentRef.toString() + " is not employee. Load document fail");
					return loadOk;
				}
				String expLogin = orgstructureService.getEmployeeLogin(author);
				String members = (null == extMembers || extMembers.trim().isEmpty()) ? "" : extMembers;
				byte[] binaryData = getBinaryFileData(documentRef);

				ArrayOfString filesNames = new ArrayOfString();
				ArrayOfBase64Binary filesContent = new ArrayOfBase64Binary();
				filesNames.getString().add(fileName);
				filesContent.getBase64Binary().add(binaryData);
				loadOk = itsWebService.loadMail(expLogin, members, xgregDate, "", filesNames, filesContent);
			}
		} catch (SOAPFaultException soap_exception) {
			logger.error("Can't loadDocumentBr5: server is unvailable.");
			soap_exception.printStackTrace();
		}
		if (loadOk) {
			nodeService.setProperty(documentRef, PROP_BR5_INTEGRATION_LOADED, true);
			nodeService.setProperty(documentRef, PROP_BR5_INTEGRATION_VERSION, nodeService.getProperty(documentRef, ContentModel.PROP_VERSION_LABEL));
		}
		return loadOk;
	}

	private Boolean loadLECMDocumentBr5(NodeRef documentRef) throws DatatypeConfigurationException {
		Boolean loadOk = false;
		Boolean success = true;
		if (documentRef == null) {
			logger.info("loadDocumentBr5: documentFile is null!");
			return loadOk;
		}
		//check aspect exist
		if (!hasBr5Aspect(documentRef)) {
			logger.info("loadDocumentBr5: documentFile has no aspect \"BR5 Integration\"!");
			return loadOk;
		}
		List<NodeRef> members = documentMembersService.getDocumentMembers(documentRef);
		String membersLogins = "";
		for (NodeRef member : members) {
			NodeRef memberEmployee = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE).get(0).getTargetRef();
			membersLogins += orgstructureService.getEmployeeLogin(memberEmployee) + ";";
		}

		List<NodeRef> categories = documentAttachmentsService.getCategories(documentRef);
		for (NodeRef category : categories) {
			String categoryName = (String) nodeService.getProperty(category, ContentModel.PROP_NAME);
			List<NodeRef> categoryAttachments = documentAttachmentsService.getAttachmentsByCategory(documentRef, categoryName);
			for (NodeRef categoryAttachment : categoryAttachments) {
				success = success && loadAlfDocumentBr5(categoryAttachment, membersLogins);
			}
		}
		if (success) {
			nodeService.setProperty(documentRef, PROP_BR5_INTEGRATION_LOADED, true);
			loadOk = true;
		}
		return loadOk;
	}

	@Override
	public Boolean loadDocumentBr5(NodeRef documentFile) throws DatatypeConfigurationException {
		if (nodeService.getType(documentFile).equals(ContentModel.TYPE_CONTENT)) {
			return loadAlfDocumentBr5(documentFile, null);
		} else {
			return loadLECMDocumentBr5(documentFile);
		}
	}

	@Override
	public Map<String, Integer> normalizeTags(Map<String, Float> tags, Integer maxFontSize, Integer minFontSize) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		int maxFS = (null == maxFontSize) ? DEFAULT_MAX_FONT_SIZE : maxFontSize;
		int minFS = (null == minFontSize) ? DEFAULT_MIN_FONT_SIZE : minFontSize;
		float maxValue = 0;
		float minValue = 0;
		if (tags != null){
			List<Float> values = new ArrayList<Float>(tags.values());
			for (Float value : values) {
				maxValue = Math.max(maxValue, value);
				minValue = Math.min(minValue, value);
			}
			float divider = (maxFS - minFS) > 0 ? (maxFS - minFS) : 1;
			float scaleFactor = (maxFS - minFS) / (divider);
			for (String tag : tags.keySet()) {
				int fontSize = Math.round(scaleFactor * tags.get(tag) + (maxFS - maxValue * scaleFactor));
				result.put(tag, fontSize);
			}
		}
		return result;
	}

	@Override
	public Map<String, Float> getExpertsTagsBr5(NodeRef expert) {
		Map<String, Float> result = new HashMap<String, Float>();
		if (!nodeService.exists(expert) || !orgstructureService.isEmployee(expert)) {
			return result;
		}

		Integer expertInnerId = loadExpertBr5(expert); // получим id эксперта
		if (expertInnerId == null) {
			return result;
		}

		ArrayOfDataItem itemArray = null;
		try {
			itemArray = itsWebService.getPersonSignificItems(expertInnerId, 0);
		} catch (SOAPFaultException soap_exception) {
			logger.error("getExpertsTagsBr5: server is not available. ");
			soap_exception.printStackTrace();
			return result;
		}

		List<DataItem> itemList = itemArray.getDataItem();
		for (DataItem dataItem : itemList) {
			result.put(dataItem.getSpelling(),dataItem.getSignific());
		}
		return result;
	}

	@Override
	public boolean hasBr5Aspect(NodeRef documentRef) {
		if (documentRef != null) {
			return nodeService.hasAspect(documentRef, ConstantsBean.ASPECT_BR5_INTEGRATION);
		}
		return false;
	}

	@Override
	public void setDocumentTags(NodeRef documentRef, Map<String, Float> tags) {
		if (Serializable.class.isInstance(tags)) {
			nodeService.setProperty(documentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, (Serializable) tags);
		}
	}

	@Override
	public Map<String, Float> getDocumentTagsBr5(NodeRef documentRef) {
		Map<String, Float> mp = (Map<String,Float>) nodeService.getProperty(documentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS);
		return mp;
	}

	public List<Person> getExpertsByDocument(NodeRef document) {
		if (document == null) {
			logger.info("getExpertsByDocument: document is null!");
			return new ArrayList<Person>();
		}
		Map<String, Float> tags = getDocumentTagsBr5(document);
		StringBuilder searchString = new StringBuilder();
		Iterator<String> it = tags.keySet().iterator();
		if (it.hasNext()) {
			searchString.append(it.next());
		}
		while (it.hasNext()) {
			searchString.append(it.next()).append(";");
		}
		ArrayOfPerson arrPersons;
		try {
			arrPersons = itsWebService.searchExperts(searchString.toString());
		} catch (SOAPFaultException soap_exception) {
			logger.error("Can't getExpertsByDocument: server is not available. ");
			soap_exception.printStackTrace();
			return new ArrayList<Person>();
		}

		List<Person> persons = new ArrayList<Person>();
		if (arrPersons != null) {
			persons = arrPersons.getPerson();
		}
		return persons;
	}

	@Override
	public SortedMap<Float, List<Map<String, String>>> getDataExpertsByDocument(NodeRef document) {
		if (document == null) {
			logger.info("getDataExpertsByDocument: document is null!");
			return new TreeMap<Float, List<Map<String, String>>>();
		}

		TreeMap<Float, List<Map<String, String>>> mapResult = new TreeMap<Float, List<Map<String, String>>>();

		List<Person> persons = getExpertsByDocument(document);
		for (Person person : persons) {
			HashMap<String, String> attrList = new HashMap<String, String>();
			String personLogin = "";
			List<PersonAttrs> personAttrs = person.getAttrs().getPersonAttrs();
			for (PersonAttrs personAttr : personAttrs) {
				String name = personAttr.getName();
				if (name.equals("username")) {
					personLogin = personAttr.getValue();
				}
			}

			NodeRef expert = orgstructureService.getEmployeeByPerson(personLogin);
			if (expert != null) {
				String firstName = (String) nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
				firstName = firstName != null ? firstName : "";
				//отчество
				String middleName = (String) nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
				middleName = middleName != null ? middleName : "";
				//фамилия
				String lastName = (String) nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
				lastName = lastName != null ? lastName : "";
				NodeRef staffRef = orgstructureService.getEmployeePrimaryStaff(expert);
				String staff = staffRef != null ? (String) nodeService.getProperty(staffRef, ContentModel.PROP_NAME) : "";

				NodeRef fotoRef = orgstructureService.getEmployeePhoto(expert);
				String sFotoRef = fotoRef != null ? fotoRef.toString() : "";
				attrList.put("firstName", firstName);
				attrList.put("middleName", middleName);
				attrList.put("lastName", lastName);
				attrList.put("staf", staff);
				attrList.put("fotoRef", sFotoRef);
				attrList.put("expertRef", expert.toString());

			}
			Float personRank = person.getRank();
			if (mapResult.containsKey(personRank)) {
				mapResult.get(personRank).add(attrList);
			} else {
				ArrayList<Map<String, String>> arr = new ArrayList<Map<String, String>>();
				arr.add(attrList);
				mapResult.put(personRank, arr);
			}

		}

		return mapResult;

	}

	public List<NodeRef> searchNodeRefs(String query){
		List<NodeRef> nodeRefs = new ArrayList<NodeRef>();
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		ResultSet resultSet = null;
		try {
			resultSet = searchService.query(sp);
			if (resultSet != null) {
				List<NodeRef> refs = resultSet.getNodeRefs();
				for (NodeRef ref : refs) {
					nodeRefs.add(ref);
				}
			}
		} catch (LuceneQueryParserException e) {
			logger.error("Error while getting exist connection types", e);
		} catch (Exception e) {
			logger.error("Error while getting exist connection types", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return nodeRefs;
	}

	@Override
	public String getQueryByTag(String tag, String docType){
		String ntag = (tag!=null)?tag:"";
		String query = "";
		if (docType.equals("alfresco")){
			query = "TYPE:\"cm:content\" AND ASPECT:\"{http://www.it.ru/lecm/br5/semantic/aspects/1.0}br5\" AND "+"\""+ntag+"\"";
		}
		if (docType.equals("lecm")){
			query = "ASPECT:\"{http://www.it.ru/lecm/br5/semantic/aspects/1.0}br5\" AND "+ntag;
		}
		return query;
	}

	@Override
	public List<NodeRef> getSimilarDocumentsByTag(String tag,String docType){
        String query = getQueryByTag(tag,docType);
		List<NodeRef> documentsList = searchNodeRefs(query);
		return documentsList;
	}

	@Override
	public List<String> getSimilarDocumentsByTagStr(String tag, String docType){
		List<NodeRef> documentsList = getSimilarDocumentsByTag(tag, docType);
		List<String> sDocumentList = new ArrayList<String>();
		for (NodeRef document : documentsList){
			sDocumentList.add(document.toString());
		}
		return sDocumentList;
	}

	@Override
	public String getQueryByDocument(NodeRef document,String docType){
		if (document == null){
			return "";
		}
		// получим список тегов документа
		Map<String,Float> tags = getDocumentTagsBr5(document);
		Set<String> tagSet = tags.keySet();
		//String subSearchString = "";
		StringBuilder subSearchString = new StringBuilder("ASPECT:\"{http://www.it.ru/lecm/br5/semantic/aspects/1.0}br5\"");
		if (tagSet.size() != 0){
			subSearchString.append(" AND (");
			for (String tag: tagSet){
				subSearchString.append("\"").append(tag).append("\"").append("^").append(tags.get(tag).toString()).append(" OR ");
				//subSearchString.append(tag).append(" AND ");
			}
			subSearchString.delete(subSearchString.length()-4, subSearchString.length());
		}
		subSearchString.append(")");

		String query = subSearchString.toString();
		return query;
	}

	@Override
	public List<NodeRef> getSimilarDocumentsByDocument(NodeRef document,String docType){
		String query = getQueryByDocument(document,docType);
		List<NodeRef> documentsList = searchNodeRefs(query);
		return documentsList;
	}

	@Override
	public List<String> getSimilarDocumentsByDocumentStr(NodeRef document,String docType){
		List<NodeRef> documentsList = getSimilarDocumentsByDocument(document,docType);
		List<String> sDocumentList = new ArrayList<String>();
		for (NodeRef documentRef : documentsList){
			sDocumentList.add(documentRef.toString());
		}
		return sDocumentList;
	}

	@Override
	public void refreshDocument(NodeRef documentRef) {
		try {
			loadDocumentBr5(documentRef);
			refreshDocumentTagsBr5(documentRef);
		} catch (DatatypeConfigurationException ex) {
			logger.error(ex.getMessage());
		}
	}

	@Override
	public boolean hasDocumentTags(NodeRef documentRef) {
		if (documentRef != null) {
			Map<String,Float> tags = getDocumentTagsBr5(documentRef);
			if (tags != null && tags.size() > 0  && hasBr5Aspect(documentRef))
				return true;
			else
				return false;
		}
		return false;
	}
}
