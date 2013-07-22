package ru.it.lecm.br5.semantic.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.soap.ArrayOfDataItem;
import ru.it.soap.CommunicationLoad;
import ru.it.soap.DataItem;

/**
 *
 * @author snovikov
 */
public class SemanticBeanImpl extends BaseBean implements ConstantsBean, SemanticBean {
	private static final Logger logger = LoggerFactory.getLogger(SemanticBeanImpl.class);

	protected ItsWebServiceSoap itsWebService;
	protected OrgstructureBean orgstructureService;
	private NodeService nodeService;

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setItsWebService(ItsWebServiceSoap itsWebService) {
		this.itsWebService = itsWebService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
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
			/*PersonAttrs dsf1 = new PersonAttrs();
			 dsf1.setName("surname");
			 PersonAttrs dsf2 = new PersonAttrs();
			 dsf3.setName("secname");*/
			PersonAttrs dsf4 = new PersonAttrs();
			dsf4.setName("username");
			dsf4.setValue(expLogin);
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
			/*PersonAttrs dsf1 = new PersonAttrs();
			 dsf1.setName("surname");
			 PersonAttrs dsf2 = new PersonAttrs();
			 dsf3.setName("secname");*/
			PersonAttrs dsf4 = new PersonAttrs();
			dsf4.setName("username");
			dsf4.setValue(expLogin);
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

	@Override
	public void loadDocumentBr5Async(NodeRef expert, NodeRef documentFile) throws DatatypeConfigurationException {
		if (documentFile == null) {
			logger.info("loadDocumentBr5Async: documentFile is null!");
			return;
		}
		if (expert == null || !orgstructureService.isEmployee(expert)) {
			logger.info("loadDocumentBr5Async: expert is not an employee!");
			return;
		}
		ContentService contentService = serviceRegistry.getContentService();
		NodeRef nodeRef = documentFile;
		NodeService nodeService = serviceRegistry.getNodeService();
		final String fileName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		final String expLogin = orgstructureService.getEmployeeLogin(expert);

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		final XMLGregorianCalendar xgregDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
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
			return;
		} finally {
			IOUtils.closeQuietly(originalInputStream);
			IOUtils.closeQuietly(outputStream);
		}

		final byte[] finalBinaryData = binaryData;

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				boolean loadOk = false;
				try {
					loadOk = itsWebService.loadDocument(expLogin, xgregDate, fileName, finalBinaryData);
				} catch (SOAPFaultException soap_exception) {
					logger.error("Can't loadDocumentBr5: server is not available. ");
					soap_exception.printStackTrace();
				}
				logger.info("File ( " + fileName + ") load = " + loadOk);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	@Override
	public HashMap<String, HashMap<Double, Integer>> getExpertsTagsBr5(NodeRef expert) {
		TreeMap<Double, List<String>> map1 = new TreeMap<Double, List<String>>();

		List<String> expertItems = new ArrayList<String>();
		if (!nodeService.exists(expert) || !orgstructureService.isEmployee(expert)) {
			return null;
		}
		Integer expertInnerId = loadExpertBr5(expert); // получим id эксперта
		if (expertInnerId == null) {
			return null;
		}

		ArrayOfDataItem itemArray = null;
		try {
			itemArray = itsWebService.getPersonSignificItems(expertInnerId);
		} catch (SOAPFaultException soap_exception) {
			logger.error("Can't getPersonSignificItems: server is not available. ");
			soap_exception.printStackTrace();
			return null;
		}

		List<DataItem> itemList = itemArray.getDataItem();
		for (DataItem item : itemList) {
			//expertItems.add(item.getSpelling());
			double totCnt = item.getCoef();
			if (map1.get(totCnt) == null) {
				List<String> lst = new ArrayList<String>();
				lst.add(item.getSpelling());
				map1.put(totCnt, lst);
			} else {
				map1.get(totCnt).add(item.getSpelling());
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
		double T = (B-A)/(C-D);
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
	public HashMap<String,Integer> getExpertsTagsBr5OnlyWithFont(NodeRef expert) {
		HashMap<String,Double> map1 = new HashMap<String,Double>();

		List<String> expertItems = new ArrayList<String>();
		if (!nodeService.exists(expert) || !orgstructureService.isEmployee(expert)) {
			return null;
		}
		Integer expertInnerId = loadExpertBr5(expert); // получим id эксперта
		if (expertInnerId == null) {
			return null;
		}

		ArrayOfDataItem itemArray = null;
		try {
			itemArray = itsWebService.getPersonSignificItems(expertInnerId);
		} catch (SOAPFaultException soap_exception) {
			logger.error("Can't getPersonSignificItems: server is not available. ");
			soap_exception.printStackTrace();
			return null;
		}

		List<DataItem> itemList = itemArray.getDataItem();
		double value = 0;
		for (DataItem item : itemList) {
				String term = item.getSpelling();
				value = item.getCoef();
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
		double T = (B-A)/(C-D);
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
		HashMap<String,Double> mp = new HashMap<String,Double>();
		mp.put("Ключ_1", 1.7);
		mp.put("Ключ_2", 3231.73);
		nodeService.setProperty(documentRef, ConstantsBean.PROP_BR5_INTEGRATION_TAGS, mp);
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
		TreeSet<Double> ar = new TreeSet<Double>(map1.values());
		double minCoef = ar.first();
		double maxCoef = ar.last();
		double B = 25;
		double A = 10;
		double C = maxCoef;
		double D = minCoef;
		double T = (B-A)/(C-D);
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



}
