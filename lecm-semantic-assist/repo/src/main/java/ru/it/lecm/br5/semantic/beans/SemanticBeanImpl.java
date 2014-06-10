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
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
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
import ru.it.lecm.documents.beans.DocumentService;
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
	private DictionaryService dictionaryService;

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

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
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
				logger.trace("Can't loadPerson: server is not available. ",soap_exception);
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
			if (nodeService.getType(documentRef).equals(ContentModel.TYPE_CONTENT)) { // документ alfresco
				result = refreshAlfDocumentTagsBr5(documentRef);
			}

			if (dictionaryService.isSubClass(nodeService.getType(documentRef), DocumentService.TYPE_BASE_DOCUMENT)) {
				result = refreshLECMDocumentTagsBr5(documentRef);
			}
		return result;
	}

	@Override
	public Boolean refreshAlfDocumentTagsBr5(NodeRef documentRef) throws DatatypeConfigurationException {
		Boolean result = false;
		if (!hasBr5Aspect(documentRef)) {
			return result;
		}
		ArrayOfDataItem docItems = null;
		try{
			if (nodeService.getType(documentRef).equals(ContentModel.TYPE_CONTENT)) { // документ alfresco
				String fileName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
				byte[] finalBinaryData = getBinaryFileData(documentRef);
				// получим список термов для документа и запишем в свойство tags документа lecm
				ArrayOfString finalFilesNames = new ArrayOfString();
				ArrayOfBase64Binary finalFilesContent = new ArrayOfBase64Binary();
				finalFilesNames.getString().add(fileName);
				finalFilesContent.getBase64Binary().add(finalBinaryData);
				docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 0);
				result = true;
			}
		} catch (SOAPFaultException soap_exception) {
			logger.trace("Can't refreshAlfDocumentTagsBr5: server is unavailable. ", soap_exception);
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

	@Override
	public Boolean refreshLECMDocumentTagsBr5(NodeRef documentRef) throws DatatypeConfigurationException {
		Boolean result = false;
		Boolean success = true;
		if (!hasBr5Aspect(documentRef)) {
			return result;
		}
		ArrayOfDataItem docItems = null;
		try{
			if (dictionaryService.isSubClass(nodeService.getType(documentRef), DocumentService.TYPE_BASE_DOCUMENT)) {
				//документ Lecm
				//запишем в массивы имена и содержимое всех файлов вложений
				ArrayOfString filesNames = new ArrayOfString();
				ArrayOfBase64Binary filesContent = new ArrayOfBase64Binary();
				List<NodeRef> categories = documentAttachmentsService.getCategories(documentRef);
				for (NodeRef category : categories) {
					String categoryName = (String) nodeService.getProperty(category, ContentModel.PROP_NAME);
					List<NodeRef> categoryAttachments = documentAttachmentsService.getAttachmentsByCategory(documentRef, categoryName);
					for (NodeRef categoryAttachment : categoryAttachments) {
						success = success && refreshAlfDocumentTagsBr5(categoryAttachment);//обновим теги у всех вложений
						filesNames.getString().add((String) nodeService.getProperty(categoryAttachment, ContentModel.PROP_NAME));
						filesContent.getBase64Binary().add(getBinaryFileData(categoryAttachment));
					}
				}
				ArrayOfString finalFilesNames = filesNames;
				ArrayOfBase64Binary finalFilesContent = filesContent;
				// получим список термов для документа и запишем в свойство tags документа lecm
				docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 0);
				result = true;
			}
		} catch (SOAPFaultException soap_exception) {
			logger.trace("Can't refreshAlfDocumentTagsBr5: server is unvailable. ");
		}

		if (result && success && docItems != null) {
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
			return loadOk;
		}
		//check aspect exist
		if (!hasBr5Aspect(documentRef)) {
			return loadOk;
		}

		if ((Boolean) nodeService.getProperty(documentRef, PROP_BR5_INTEGRATION_LOADED)){
			if (nodeService.getProperty(documentRef, ContentModel.PROP_VERSION_LABEL)!=null && nodeService.getProperty(documentRef, PROP_BR5_INTEGRATION_VERSION)!= null){
				if ( nodeService.getProperty(documentRef, PROP_BR5_INTEGRATION_VERSION).toString().equals(nodeService.getProperty(documentRef, ContentModel.PROP_VERSION_LABEL).toString())  ){
					return true;
				}
			}
			if ( null == nodeService.getProperty(documentRef, PROP_BR5_INTEGRATION_VERSION)){
				return true;
			}
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
					logger.debug("loadDocumentBr5: author of document " + documentRef.toString() + " is not employee. Load document fail");
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
			logger.trace("Can't loadDocumentBr5: server is unvailable.", soap_exception);
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
			logger.debug("loadDocumentBr5: documentFile is null!");
			return loadOk;
		}
		//check aspect exist
		if (!hasBr5Aspect(documentRef)) {
			logger.debug("loadDocumentBr5: documentFile has no aspect \"BR5 Integration\"!");
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
	public Map<String, Float> getExpertsTagsBr5(NodeRef expert, Integer maxCount) {
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
			Integer mc = maxCount!=null ? maxCount : 50;
			itemArray = itsWebService.getPersonSignificItems(expertInnerId, mc);
		} catch (SOAPFaultException soap_exception) {
			logger.trace("getExpertsTagsBr5: server is not available. ", soap_exception);
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
	public Map<String, Float> getDocumentTagsBr5(NodeRef documentRef, Integer maxCount) {
		Map<String, Float> tags = (Map<String,Float>) nodeService.getProperty(documentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS);
		if (tags == null){
			return null;
		}

		if ( maxCount==null || maxCount < 0 ){
			return tags;
		}
		int size = tags.size();
		int cnt = size > maxCount ? maxCount : size;
		if (cnt==size){
			return tags;
		}


		//отсортируем по наиболее важным тегам
		TreeMap<Float,List<String>> mweight = new TreeMap<Float,List<String>>();
		Set<String> terms = tags.keySet();
		for (String term : terms){
			float wg = tags.get(term);
			if (mweight.containsKey(wg)){
				mweight.get(wg).add(term);
			}
			else{
				List<String> lst = new ArrayList<String>();
				lst.add(term);
				mweight.put(wg,lst);
			}
		}
		//а теперь вытащим первые cnt тегов
		tags = new HashMap<String, Float>();
		int i = 0;
		Float mkey = mweight.lastKey();
		while (mweight.containsKey(mkey) ){
			List<String> termsLst = mweight.get(mkey);
			for (String term : termsLst) {
				if (i<cnt){
					tags.put(term,mkey);
					i++;
				}
			}
			if (mkey == 0) break;
			mkey = mweight.lowerKey(mkey);
		}
		return tags;
	}

	public List<Person> getExpertsByDocument(NodeRef document,Integer maxCount) {
		if (document == null) {
			logger.debug("getExpertsByDocument: document is null!");
			return new ArrayList<Person>();
		}
		Map<String, Float> tags = getDocumentTagsBr5(document,null);
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
			logger.trace("Can't getExpertsByDocument: server is not available. ", soap_exception);
			return new ArrayList<Person>();
		}

		List<Person> persons = new ArrayList<Person>();
		if (arrPersons != null) {
			persons = arrPersons.getPerson();
		}

		Integer mc = maxCount!=null ? maxCount : 50;
		int cnt = 0;
		int size = persons.size();
		if (size > 0){
			mc = size >=mc ? mc : size;
			persons = persons.subList(0, mc);
		}

		return persons;
	}

	@Override
	public SortedMap<Float, List<Map<String, String>>> getDataExpertsByDocument(NodeRef document, Integer maxCount) {
		if (document == null) {
			logger.debug("getDataExpertsByDocument: document is null!");
			return new TreeMap<Float, List<Map<String, String>>>();
		}

		TreeMap<Float, List<Map<String, String>>> mapResult = new TreeMap<Float, List<Map<String, String>>>();

		List<Person> persons = getExpertsByDocument(document, maxCount);
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
		Map<String,Float> tags = getDocumentTagsBr5(document,50); // ограничено 50 чтобы влезло в строку запроса
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
			Map<String,Float> tags = getDocumentTagsBr5(documentRef,null);
			if (tags != null && tags.size() > 0  && hasBr5Aspect(documentRef))
				return true;
			else
				return false;
		}
		return false;
	}
}
