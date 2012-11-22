package ru.it.lecm.dictionary.imports;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.dictionary.export.ExportNamespace;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс загрузчик справочника из XML описания
 */
public class XmlDictionaryImporter {
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private Repository repositoryHelper;
	private QName itemsType = null;
	private InputStream inputStream;

	/**
	 * Конструктор загрузчика XML
	 * @param inputStream входной XML поток
	 * @param repositoryHelper      repositoryHelper
	 * @param nodeService           nodeService
	 * @param namespaceService      namespaceService
	 */
	public XmlDictionaryImporter(InputStream inputStream, Repository repositoryHelper, NodeService nodeService, NamespaceService namespaceService) {
		this.inputStream = inputStream;
		this.repositoryHelper = repositoryHelper;
		this.nodeService = nodeService;
		this.namespaceService = namespaceService;
	}

	/**
	 * Считывание справочника
	 * @throws XMLStreamException
	 */
	public void readDictionary() throws XMLStreamException {
		readDictionary(false);
	}

	/**
	 * Считывание справочника
	 * @param doNotUpdateIfExist не обновлять, если такой справочник уже существует, иначе обновить свойства справочника и элементы
	 * @throws XMLStreamException
	 */
	public void readDictionary(boolean doNotUpdateIfExist) throws XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlr = inputFactory.createXMLStreamReader(inputStream);
		NodeRef parentNodeRef;
		String str = "";

		while (xmlr.hasNext() && !str.equals(ExportNamespace.TAG_DICTIONARY)) {
			xmlr.nextTag();
			str = xmlr.getLocalName();
		}
		if (str.equals(ExportNamespace.TAG_DICTIONARY)) {
			String dictionaryName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
			if (dictionaryName == null || (doNotUpdateIfExist && nodeService.getChildByName(getDictionariesRoot(), ContentModel.ASSOC_CONTAINS, dictionaryName) != null)) {
				return;
			}
			xmlr.nextTag();
			Map<QName, Serializable> dicProps = getProperties(xmlr);
			String type = dicProps.get(ExportNamespace.PROP_TYPE).toString();
			itemsType = QName.createQName(type, namespaceService);
			parentNodeRef = createDictionary(dictionaryName, dicProps);
			readItems(xmlr, parentNodeRef);
		}
	}

	/** считывание элементов и создание
	 *
	 * @param xmlr XML Reader
	 * @param parent ссылка на родительский элемент
	 * @return true если элементы были создан
	 * @throws XMLStreamException
	 */

	private boolean readItems(XMLStreamReader xmlr, NodeRef parent) throws XMLStreamException {
		if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
				&& xmlr.getLocalName().equals(ExportNamespace.TAG_ITEMS))) {
			return false;
		}
		xmlr.nextTag();//входим в <items>
		try {
			while (readItem(xmlr, parent)) {
			}
			xmlr.nextTag();//выходим из </items>
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return true;
	}

	/** считывание элемента и создание
	 *
	 * @param xmlr XML Reader
	 * @param parent ссылка на родительский элемент
	 * @return true если элемент был создан
	 * @throws XMLStreamException
	 */
	private boolean readItem(XMLStreamReader xmlr, NodeRef parent) throws XMLStreamException {
		if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
				&& xmlr.getLocalName().equals(ExportNamespace.TAG_ITEM))) {
			return false;
		}
		String itemName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
		xmlr.nextTag();//property
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, itemName);
		properties.putAll(getProperties(xmlr));
		NodeRef current = createItem(parent, properties);
		readItems(xmlr, current);
		xmlr.nextTag();//выходим из </item>
		return true;
	}

	/** создание справочника, если существует - обновить свойства
	 *
	 * @param dictionaryName имя справочника
	 * @param dicProps свойства
	 * @return ссылка на справочник
	 */
	private NodeRef createDictionary(String dictionaryName, Map<QName, Serializable> dicProps) {
		final NodeRef root = getDictionariesRoot();
		NodeRef dictionary = nodeService.getChildByName(root, ContentModel.ASSOC_CONTAINS, dictionaryName);
		if (dictionary == null) {
			//создание справочника
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(ContentModel.PROP_NAME, dictionaryName);
			properties.putAll(dicProps);
			dictionary = nodeService.createNode(root, ContentModel.ASSOC_CONTAINS,
					QName.createQName(ExportNamespace.DICTIONARY_NAMESPACE_URI, dictionaryName),
					ExportNamespace.DICTIONARY,
					properties).getChildRef();
		} else {
			nodeService.addProperties(dictionary, dicProps);
		}
		return dictionary;
	}

	/** создание элемента, если существует - обновить свойства\
	 *
	 * @param parentNodeRef родительский элемент/справочник
	 * @param properties свойства
	 * @return ссылка на элемент
	 */
	private NodeRef createItem(NodeRef parentNodeRef, Map<QName, Serializable> properties) {
		String name = properties.get(ContentModel.PROP_NAME).toString();
		NodeRef node = nodeService.getChildByName(parentNodeRef, ContentModel.ASSOC_CONTAINS, name);
		if (node == null) {
			node = nodeService.createNode(parentNodeRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
				itemsType,
				properties).getChildRef();
		} else {
			nodeService.addProperties(node, properties);
		}
		return node;
	}

	/** получение корня справочников, если нет - создать
	 *
	 * @return ссылку на корневой контейнер справочников
	 */
	private NodeRef getDictionariesRoot() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		NodeRef dictionariesRoot = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, ExportNamespace.DICTIONARIES_ROOT_NAME);
		if (dictionariesRoot == null) {
			Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
			properties.put(ContentModel.PROP_NAME, ExportNamespace.DICTIONARIES_ROOT_NAME);
			dictionariesRoot = nodeService.createNode(companyHome, ContentModel.ASSOC_CONTAINS, QName.createQName(ExportNamespace.DICTIONARY_NAMESPACE_URI, ExportNamespace.DICTIONARIES_ROOT_NAME), ContentModel.TYPE_FOLDER, properties).getChildRef();
		}
		return dictionariesRoot;
	}

	/** считываем свойства из XML
	 *
 	 * @param xmlr XML Reader
	 * @return карта свойств
	 * @throws XMLStreamException
	 */
	private Map<QName, Serializable> getProperties(XMLStreamReader xmlr) throws XMLStreamException {
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		String value;
		String propName;
		while (XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
				&& xmlr.getLocalName().equals(ExportNamespace.TAG_PROPERTY)) {
			propName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
			xmlr.next();
			if (XMLStreamConstants.CHARACTERS == xmlr.getEventType()) {
				value = xmlr.getText();
				properties.put(QName.createQName(propName, namespaceService), value);
				xmlr.nextTag();
				xmlr.nextTag();//пропускаем закрывающий тэг
			}
		}
		return properties;
	}
}