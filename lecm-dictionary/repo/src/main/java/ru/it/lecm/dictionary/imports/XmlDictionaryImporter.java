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

public class XmlDictionaryImporter {
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private Repository repositoryHelper;
	private QName itemsType = null;
	private InputStream inputStream;

	public XmlDictionaryImporter(InputStream inputStream, Repository repositoryHelper, NodeService nodeService, NamespaceService namespaceService) {
		this.inputStream = inputStream;
		this.repositoryHelper = repositoryHelper;
		this.nodeService = nodeService;
		this.namespaceService = namespaceService;
	}

	public void readDictionary() throws XMLStreamException {
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
			xmlr.nextTag();
			Map<QName, Serializable> dicProps = getProperties(xmlr);
			String type = dicProps.get(ExportNamespace.PROP_TYPE).toString();
			itemsType = QName.createQName(type, namespaceService);
			parentNodeRef = createDictionary(dictionaryName, dicProps);
			readItems(xmlr, parentNodeRef);
		}
	}

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

	private NodeRef getDictionariesRoot() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
				ExportNamespace.DICTIONARIES_ROOT_NAME);
	}

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