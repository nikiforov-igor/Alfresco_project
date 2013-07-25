package ru.it.lecm.br5.semantic.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.soap.ArrayOfPersonAttrs;
import ru.it.soap.ItsWebServiceSoap;
import ru.it.soap.PersonAttrs;
import ru.it.soap.PersonLoad;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.br5.semantic.api.ConstantsBean;
import ru.it.lecm.br5.semantic.api.SemanticBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.soap.ArrayOfBase64Binary;
import ru.it.soap.ArrayOfDataItem;
import ru.it.soap.ArrayOfPerson;
import ru.it.soap.ArrayOfString;
import ru.it.soap.CommunicationLoad;
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
	protected NodeService nodeService;
	protected DocumentMembersService documentMembersService;
	protected DocumentAttachmentsService documentAttachmentsService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setItsWebService(ItsWebServiceSoap itsWebService) {
		this.itsWebService = itsWebService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Integer loadExpertBr5(NodeRef expert) {
		if (orgstructureService.isEmployee(expert)){
			String expLogin = orgstructureService.getEmployeeLogin(expert);
			PersonLoad pLoad = new PersonLoad();

			ArrayOfPersonAttrs pAttrs = new ArrayOfPersonAttrs();
			String firstName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
			//отчество
			String middleName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
			//фамилия
			String lastName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
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

	@Override
	public void loadExpertBr5Async(final NodeRef expert) {
		if (orgstructureService.isEmployee(expert)){
			String expLogin = orgstructureService.getEmployeeLogin(expert);
			final PersonLoad pLoad = new PersonLoad();

			ArrayOfPersonAttrs pAttrs = new ArrayOfPersonAttrs();
			String firstName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
			//отчество
			String middleName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
			//фамилия
			String lastName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
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

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						itsWebService.loadPerson(pLoad);
					} catch (SOAPFaultException soap_exception) {
						logger.error("Can't loadPerson: server is not available. ");
						soap_exception.printStackTrace();
					}

				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
		}
		else{
			logger.info("loadExpertBr5Async: nodeRef "+expert.toString()+" is not an expert");
		}
	}

	private byte[] getBinaryFileData (NodeRef file){
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

	@Override
	public void loadDocumentBr5Async(NodeRef documentRef) throws DatatypeConfigurationException {
		if (documentRef == null) {
			logger.info("loadDocumentBr5Async: documentFile is null!");
			return;
		}
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		final XMLGregorianCalendar xgregDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		// проверим к какому типу относится документ (Alfresco или LECM)
		if (nodeService.getType(documentRef).equals(ContentModel.PROP_CONTENT)){ // документ alfresco
			final String fileName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
			String authorName = (String)nodeService.getProperty(documentRef, ContentModel.PROP_CREATOR);
			NodeRef author = orgstructureService.getEmployeeByPerson(authorName);
			if (author == null) {
				logger.info("loadDocumentBr5Async: author of document "+documentRef.toString()+" is not employee. Load document fail");
				return;
			}
			final String expLogin = orgstructureService.getEmployeeLogin(author);

			final byte[] finalBinaryData = getBinaryFileData(documentRef);
			final NodeRef finalDocumentRef = documentRef;

			boolean loadOk = false;
			try {
				loadOk = itsWebService.loadDocument(expLogin, xgregDate, fileName, finalBinaryData);
				// получим список термов для документа и запишем в свойство tags документа lecm
				ArrayOfString finalFilesNames = new ArrayOfString();
				ArrayOfBase64Binary finalFilesContent = new ArrayOfBase64Binary();
				finalFilesNames.getString().add(fileName);
				finalFilesContent.getBase64Binary().add(finalBinaryData);
				ArrayOfDataItem docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 50);
				if (docItems!=null){
					List<DataItem> docItemsLst =  docItems.getDataItem();
					HashMap<String,Double> tags = new HashMap<String,Double>();
					for (DataItem  docItem : docItemsLst){
						tags.put(docItem.getSpelling(), (double) docItem.getSignific());
					}
					nodeService.setProperty(finalDocumentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, tags);
				}
			} catch (SOAPFaultException soap_exception) {
				logger.error("Can't loadDocumentBr5: server is not available. ");
				soap_exception.printStackTrace();
			}

			/*Runnable runnable = new Runnable() {
				@Override
				public void run() {
					boolean loadOk = false;
					try {
						loadOk = itsWebService.loadDocument(expLogin, xgregDate, fileName, finalBinaryData);
						// получим список термов для документа и запишем в свойство tags документа lecm
						ArrayOfString finalFilesNames = new ArrayOfString();
						ArrayOfBase64Binary finalFilesContent = new ArrayOfBase64Binary();
						finalFilesNames.getString().add(fileName);
						finalFilesContent.getBase64Binary().add(finalBinaryData);
						ArrayOfDataItem docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 0);
						List<DataItem> docItemsLst =  docItems.getDataItem();
						HashMap<String,Double> tags = new HashMap<String,Double>();
						for (DataItem  docItem : docItemsLst){
							tags.put(docItem.getSpelling(), (double) docItem.getSignific());
						}
						nodeService.setProperty(finalDocumentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, tags);
					} catch (SOAPFaultException soap_exception) {
						logger.error("Can't loadDocumentBr5: server is not available. ");
						soap_exception.printStackTrace();
					}
				}
			};
			Thread thread = new Thread(runnable);
			thread.start(); */
		}
		else{ // документ Lecm
			// найдем автора и участников документа lecm
			final String authorLogin = (String)nodeService.getProperty(documentRef, ContentModel.PROP_CREATOR);
			NodeRef author = orgstructureService.getEmployeeByPerson(authorLogin);
			List<NodeRef> members = documentMembersService.getDocumentMembers(documentRef);
			String membersLogins = "";
			for (NodeRef member : members){
				NodeRef memberEmployee = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE).get(0).getTargetRef();
				if (!memberEmployee.equals(author)){
					membersLogins += orgstructureService.getEmployeeLogin(memberEmployee)+";";
				}
			}

			final String finalMembersLogins = membersLogins;

			//запишем в массивы имена и содержимое всех файлов вложений
			ArrayOfString filesNames = new ArrayOfString();
			ArrayOfBase64Binary filesContent = new ArrayOfBase64Binary();
			List<NodeRef> categories = documentAttachmentsService.getCategories(documentRef);
			for (NodeRef category : categories){
				String categoryName = (String) nodeService.getProperty(category, ContentModel.PROP_NAME);
				List<NodeRef> categoryAttachments = documentAttachmentsService.getAttachmentsByCategory(documentRef, categoryName);
				for (NodeRef categoryAttachment : categoryAttachments){
					filesNames.getString().add((String) nodeService.getProperty(categoryAttachment, ContentModel.PROP_NAME));
					filesContent.getBase64Binary().add(getBinaryFileData(categoryAttachment));
				}
			}
			final ArrayOfString finalFilesNames = filesNames;
			final ArrayOfBase64Binary finalFilesContent = filesContent;
			final NodeRef finalDocumentRef = documentRef;

			boolean loadOk = false;
			try {
				// загрузим документ на сервер BR5
				loadOk = itsWebService.loadMail(authorLogin, finalMembersLogins, xgregDate, "", finalFilesNames, finalFilesContent);
				// получим список термов для документа и запишем в свойство tags документа lecm
				ArrayOfDataItem docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 50);
				if (docItems != null){
					List<DataItem> docItemsLst =  docItems.getDataItem();
					HashMap<String,Double> tags = new HashMap<String,Double>();
					for (DataItem  docItem : docItemsLst){
						tags.put(docItem.getSpelling(), (double) docItem.getSignific());
					}
					nodeService.setProperty(finalDocumentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, tags);
				}
			} catch (SOAPFaultException soap_exception) {
				logger.error("Can't loadDocumentBr5Async: server is not available. ");
				soap_exception.printStackTrace();
			}
			/*Runnable runnable = new Runnable(){
				@Override
				public void run (){
					boolean loadOk = false;
					try {
						// загрузим документ на сервер BR5
						loadOk = itsWebService.loadMail(authorLogin, finalMembersLogins, xgregDate, "", finalFilesNames, finalFilesContent);
						// получим список термов для документа и запишем в свойство tags документа lecm
						ArrayOfDataItem docItems = itsWebService.getTextItems("", finalFilesNames, finalFilesContent, 0);
						List<DataItem> docItemsLst =  docItems.getDataItem();
						HashMap<String,Double> tags = new HashMap<String,Double>();
						for (DataItem  docItem : docItemsLst){
							tags.put(docItem.getSpelling(), (double) docItem.getSignific());
						}
						nodeService.setProperty(finalDocumentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, tags);
					} catch (SOAPFaultException soap_exception) {
						logger.error("Can't loadDocumentBr5Async: server is not available. ");
						soap_exception.printStackTrace();
					}
				}
			};
			Thread thread = new Thread(runnable);
			thread.start(); */
		}
	}

	@Override
	public HashMap<String, HashMap<Double, Integer>> getExpertsTagsBr5WithCoefAndFont(NodeRef expert) {
		TreeMap<Double, List<String>> map1 = new TreeMap<Double, List<String>>();

		List<DataItem> itemList = getExpertsTagsBr5(expert);
		for (DataItem item : itemList) {
			double signific = item.getSignific();
			if (map1.get(signific) == null) {
				List<String> lst = new ArrayList<String>();
				lst.add(item.getSpelling());
				map1.put(signific, lst);
			} else {
				map1.get(signific).add(item.getSpelling());
			}
		}

		// получим отсортированный массив по весу ключевых слов и возьмем первые 50 слов
		Double key1 = map1.lastKey();
		Double key1_2 = map1.lastKey();
		int cnt_el = 0;
		while (key1 != null && cnt_el < 50) {
			cnt_el += map1.get(key1).size();
			key1 = map1.lowerKey(key1);
		}

		TreeMap<Double, List<String>> map2;
		if (key1 != null && key1_2 != null) {
			map2 = new TreeMap<Double, List<String>>(map1.subMap(key1, key1_2));
		} else {
			map2 = new TreeMap<Double, List<String>>(map1);
		}

		// расчитаем вес каждого слова, возьмем шрифт от 8 до 25
		// посчитаем промежуток с одинаковым шрифтом
		double B = 25;
		double A = 10;
		double C = map2.lastKey();
		double D = map2.firstKey();
		double N = (C-D)>0 ?(C-D) : 1;
		double T = (B-A)/N;
		int cur_font = 25;

		// HashMap<Слово, HashMap<Коэффициент, Шрифт>>
		HashMap<String, HashMap<Double, Integer>> map3 = new HashMap<String, HashMap<Double, Integer>>();
		Double key2 = map2.lastKey();
		while (key2 != null) {
    		cur_font = (int)(T*key2+(B-C*T));
			List<String> lst3 = map2.get(key2);
			int sz = lst3.size();
			while (sz > 0) {
				HashMap count_font_map = new HashMap<Double, Integer>();
				count_font_map.put(key2, cur_font);
				map3.put(lst3.get(sz - 1), count_font_map);
				sz--;
			}
			key2 = map2.lowerKey(key2);
		}

		return map3;
	}

	@Override
	public List<DataItem> getExpertsTagsBr5 (NodeRef expert){
		List<DataItem> itemList = new ArrayList<DataItem>();
		if (!nodeService.exists(expert) || !orgstructureService.isEmployee(expert)) {
			return itemList;
		}

		Integer expertInnerId = loadExpertBr5(expert); // получим id эксперта
		if (expertInnerId == null) {
			return itemList;
		}

		ArrayOfDataItem itemArray = null;
		try {
			itemArray = itsWebService.getPersonSignificItems(expertInnerId,0);
		} catch (SOAPFaultException soap_exception) {
			logger.error("getExpertsTagsBr5: server is not available. ");
			soap_exception.printStackTrace();
			return null;
		}

		itemList = itemArray.getDataItem();
		return itemList;
	}

	@Override
	public HashMap<String,Integer> getExpertsTagsBr5OnlyWithFont(NodeRef expert) {
		HashMap<String,Double> map1 = new HashMap<String,Double>();

		List<DataItem> itemList = getExpertsTagsBr5(expert);
		double value = 0;
		for (DataItem item : itemList) {
				String term = item.getSpelling();
				value = item.getSignific();
				map1.put(term,value);
		}

		//определим наименьший и наибольший весовой коэффициент, чтобы распределить шрифты для облака тегов
		TreeSet<Double> ar = new TreeSet<Double>(map1.values());
		double minCoef = ar.first();
		double maxCoef = ar.last();
		double B = 25;
		double A = 10;
		double C = maxCoef;
		double D = minCoef;
		double N = (C-D)>0 ?(C-D) : 1;
		double T = (B-A)/N;
		int cur_font = 25;

		// получим HashMap, в котором значениями уже будут размеры шрифтов
		HashMap<String,Integer> map2 = new HashMap<String,Integer>();
		Set<String> set_map1_keys = map1.keySet();
		int font_size = 0;
		double coef = 0.0;
		for (String map1_key : set_map1_keys){
			coef = map1.get(map1_key);
			font_size = (int) (T*coef+(B-C*T));
			map2.put(map1_key,font_size);
		}

		return map2;
	}

	@Override
	public boolean hasBr5Aspect(NodeRef documentRef){
		if (documentRef!= null){
			return nodeService.hasAspect(documentRef, ConstantsBean.ASPECT_BR5_INTEGRATION);
		}
		return false;
	}

	@Override
	public void setDocumentTags(NodeRef documentRef, HashMap<String,Double> tags){
		nodeService.setProperty(documentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, tags);
	}

	@Override
	public HashMap<String,Double> getDocumentTags(NodeRef documentRef){
		HashMap<String,Double> mp = (HashMap<String,Double>) nodeService.getProperty(documentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS);
		return mp;
	}

	@Override
	public HashMap<String,Integer> getDocumentTagsWithFont(NodeRef documentRef){
		HashMap<String,Double> map1 = getDocumentTags(documentRef);
		//определим наименьший и наибольший весовой коэффициент, чтобы распределить шрифты для облака тегов
		if (map1 != null){
			TreeSet<Double> ar = new TreeSet<Double>(map1.values());
			double minCoef = ar.first();
			double maxCoef = ar.last();
			double B = 25;
			double A = 10;
			double C = maxCoef;
			double D = minCoef;
			double N = (C-D)>0 ?(C-D) : 1;
			double T = (B-A)/N;
			int cur_font = 25;

			// получим HashMap, в котором значениями уже будут размеры шрифтов
			HashMap<String,Integer> map2 = new HashMap<String,Integer>();
			Set<String> set_map1_keys = map1.keySet();
			int font_size = 0;
			double coef = 0.0;
			for (String map1_key : set_map1_keys){
				coef = map1.get(map1_key);
				font_size = (int) (T*coef+(B-C*T));
				map2.put(map1_key,font_size);
			}

			return map2;
		}
		return null;
	}

	public List<Person> getExpertsByDocument(NodeRef document){
		if (document == null) {
			logger.info("getExpertsByDocument: document is null!");
			return new ArrayList<Person>();
		}
		// проверим к какому типу относится документ (Alfresco или LECM)
		ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();

		HashMap<String,Double> tags =  getDocumentTags(document);
		Set<String> tagSet = tags.keySet();
		String searchString = "";
		for (String tag: tagSet){
			searchString += tag+";";
		}
		ArrayOfPerson arrPersons = new ArrayOfPerson();
		try{
			arrPersons = itsWebService.searchExperts(searchString);
		} catch (SOAPFaultException soap_exception) {
			logger.error("Can't getExpertsByDocument: server is not available. ");
			soap_exception.printStackTrace();
			return new ArrayList<Person>();
		}

		List<Person> persons = new ArrayList<Person>();
		if (arrPersons != null){
			persons = arrPersons.getPerson();
		}

		return persons;
	}

	public TreeMap<Float,ArrayList<HashMap<String,String>>> getDataExpertsByDocument(NodeRef document){
		if (document == null) {
			logger.info("getDataExpertsByDocument: document is null!");
			return new TreeMap<Float,ArrayList<HashMap<String,String>>>();
		}

		TreeMap<Float,ArrayList<HashMap<String,String>>> mapResult = new TreeMap<Float,ArrayList<HashMap<String,String>>>();

		List<Person> persons = getExpertsByDocument(document);
		for (Person person : persons){
			HashMap<String,String> attrList = new HashMap<String,String>();
			String personLogin ="";
			List<PersonAttrs> personAttrs = person.getAttrs().getPersonAttrs();
			for (PersonAttrs personAttr : personAttrs){
				String name = personAttr.getName();
				if (name.equals("username")){
					personLogin = personAttr.getValue();
				}
			}

			NodeRef expert = orgstructureService.getEmployeeByPerson(personLogin);
			if (expert!=null){
				String firstName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
				firstName = firstName != null ? firstName : "";
				//отчество
				String middleName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
				middleName = middleName != null ? middleName : "";
				//фамилия
				String lastName = (String)nodeService.getProperty(expert, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
				lastName = lastName != null ? lastName : "";
				NodeRef staffRef = orgstructureService.getEmployeePrimaryStaff(expert);
				String staff = staffRef != null ? (String)nodeService.getProperty(staffRef, ContentModel.PROP_NAME) : "";

				NodeRef fotoRef = orgstructureService.getEmployeePhoto(expert);
				String sFotoRef = fotoRef != null ? fotoRef.toString() : "";
				attrList.put("firstName",firstName);
				attrList.put("middleName",middleName);
				attrList.put("lastName",lastName);
				attrList.put("staf",staff);
				attrList.put("fotoRef",sFotoRef);
				attrList.put("expertRef",expert.toString());

			}
			Float personRank = person.getRank();
			if (mapResult.containsKey(personRank)){
				mapResult.get(personRank).add(attrList);
			}
			else{
				ArrayList<HashMap<String,String>> arr = new ArrayList<HashMap<String,String>>();
				arr.add(attrList);
				mapResult.put(personRank, arr);
			}

		}

		return mapResult;

	}

	public List<NodeRef> getSimilarDocumentByTag(String tag){
		List<NodeRef> documentsList = new ArrayList<NodeRef>();
		SearchParameters sp = new SearchParameters();
		return documentsList;
	}


	public List<NodeRef> getSimilarDocumentByDocument(NodeRef document){
		List<NodeRef> documentsList = new ArrayList<NodeRef>();
		return documentsList;
	}

}
