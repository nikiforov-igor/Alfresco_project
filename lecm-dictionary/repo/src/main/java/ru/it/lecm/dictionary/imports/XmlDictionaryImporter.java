package ru.it.lecm.dictionary.imports;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private QName itemsType = null;
	private InputStream inputStream;
	private NodeRef dictionariesRoot;
    private static final transient Logger logger = LoggerFactory.getLogger(XmlDictionaryImporter.class);

    /**
	 * Конструктор загрузчика XML
	 * @param inputStream входной XML поток
	 * @param nodeService           nodeService
	 * @param namespaceService      namespaceService
	 * @param rootDir Корневая папка для словарей.
	 */
	public XmlDictionaryImporter(InputStream inputStream, NodeService nodeService, NamespaceService namespaceService, NodeRef dictionariesRoot) {
		this.inputStream = inputStream;
		this.nodeService = nodeService;
		this.namespaceService = namespaceService;
		this.dictionariesRoot = dictionariesRoot;
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
        logger.info("Importing dictionary. (doNotUpdateIfExist = {})", doNotUpdateIfExist);
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
			if (dictionaryName == null) {
				return;
			}
			xmlr.nextTag();
			Map<QName, Serializable> dicProps = getProperties(xmlr);
			parentNodeRef = createDictionary(dictionaryName, dicProps, doNotUpdateIfExist);
			String type = nodeService.getProperty(parentNodeRef, ExportNamespace.PROP_TYPE).toString();
			itemsType = QName.createQName(type, namespaceService);
            logger.info("Items type '{}'", itemsType);
			readItems(xmlr, parentNodeRef, doNotUpdateIfExist);
		}
	}

	/** считывание элементов и создание
	 *
	 * @param xmlr XML Reader
	 * @param parent ссылка на родительский элемент
	 * @param doNotUpdateIfExist не обновлять существующие записи
	 * @return true если элементы были создан
	 * @throws XMLStreamException
	 */

	private boolean readItems(XMLStreamReader xmlr, NodeRef parent, boolean doNotUpdateIfExist) throws XMLStreamException {
		if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
				&& xmlr.getLocalName().equals(ExportNamespace.TAG_ITEMS))) {
			return false;
		}
		xmlr.nextTag();//входим в <items>
		try {
			while (readItem(xmlr, parent, doNotUpdateIfExist)) {
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
	 * @param doNotUpdateIfExist не обновлять существующие записи
	 * @return true если элемент был создан
	 * @throws XMLStreamException
	 */
	private boolean readItem(XMLStreamReader xmlr, NodeRef parent, boolean doNotUpdateIfExist) throws XMLStreamException {
		if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
				&& xmlr.getLocalName().equals(ExportNamespace.TAG_ITEM))) {
			return false;
		}
		String itemName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
		xmlr.nextTag();//property
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, itemName);
		properties.putAll(getProperties(xmlr));
		NodeRef current = createItem(parent, properties, doNotUpdateIfExist);
		readItems(xmlr, current, doNotUpdateIfExist);
		xmlr.nextTag();//выходим из </item>
		return true;
	}

	/** создание справочника, если существует - обновить свойства
	 *
	 * @param dictionaryName имя справочника
	 * @param dicProps свойства
	 * @param doNotUpdateIfExist не обновлять существующие записи
	 * @return ссылка на справочник
	 */
	private NodeRef createDictionary(String dictionaryName, Map<QName, Serializable> dicProps, boolean doNotUpdateIfExist) {
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
            logger.info("Dictionary '{}' created", dictionaryName);
		} else if (!doNotUpdateIfExist) {
			nodeService.addProperties(dictionary, dicProps);
            logger.info("Dictionary '{}' updated", dictionaryName);
		}
		return dictionary;
	}

	/** создание элемента, если существует - обновить свойства\
	 *
	 * @param parentNodeRef родительский элемент/справочник
	 * @param properties свойства
	 * @param doNotUpdateIfExist не обновлять существующие записи
	 * @return ссылка на элемент
	 */
	private NodeRef createItem(NodeRef parentNodeRef, Map<QName, Serializable> properties, boolean doNotUpdateIfExist) {
		String name = properties.get(ContentModel.PROP_NAME).toString();
		NodeRef node = nodeService.getChildByName(parentNodeRef, ContentModel.ASSOC_CONTAINS, name);
		if (node == null) {
			node = nodeService.createNode(parentNodeRef, ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
				itemsType,
				properties).getChildRef();
            logger.info("Item '{}' created", name);
		} else if (!doNotUpdateIfExist) {
			nodeService.addProperties(node, properties);
            logger.info("Item '{}' updated", name);
		}
		return node;
	}

	/** получение корня справочников, если нет - создать
	 *
	 * @return ссылку на корневой контейнер справочников
	 */
	private NodeRef getDictionariesRoot() {
		return this.dictionariesRoot;
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