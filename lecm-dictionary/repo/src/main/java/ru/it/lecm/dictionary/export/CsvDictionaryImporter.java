package ru.it.lecm.dictionary.export;

import com.csvreader.CsvReader;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mShafeev
 * Date: 09.11.12
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
public class CsvDictionaryImporter {
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private Repository repositoryHelper;
	private QName itemsType = null;
	private InputStream inputStream;
	private NodeRef parentNodeRef;

	public CsvDictionaryImporter(InputStream inputStream, Repository repositoryHelper, NodeService nodeService,
	                            NamespaceService namespaceService, NodeRef parentNodeRef) {
		this.inputStream = inputStream;
		this.repositoryHelper = repositoryHelper;
		this.nodeService = nodeService;
		this.namespaceService = namespaceService;
		this.parentNodeRef = parentNodeRef;
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

	public void readDictionary() throws IOException {
		CsvReader csvr = new CsvReader(inputStream, ';', Charset.defaultCharset());
		String str = "";
		ArrayList<String> header = new ArrayList<String>();
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//		NodeRef nodeRef = new NodeRef("workspace://SpacesStore/c6f662d7-1143-497a-b22e-edf086e0ef5b");

		csvr.readHeaders();
		for (int i = 0; i < csvr.getHeaderCount(); i++){
			header.add(csvr.getHeader(i));
//			value = xmlr.getText();
//			properties.put(QName.createQName(propName, namespaceService), value);
		}
		itemsType = QName.createQName(nodeService.getProperty(parentNodeRef, ExportNamespace.PROP_TYPE).toString(),
				namespaceService);
		while(csvr.readRecord()){
			for (int i = 0; i < csvr.getColumnCount()-1; i++) {
				csvr.get(i);
				properties.put(QName.createQName(header.get(i), namespaceService), csvr.get(i));
			}
			createItem(parentNodeRef, properties);
		}

//		if (str.equals(ExportNamespace.TAG_DICTIONARY)) {
//			String dictionaryName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
//			xmlr.nextTag();
//			Map<QName, Serializable> dicProps = getProperties(xmlr);
//			String type = dicProps.get(ExportNamespace.PROP_TYPE).toString();
//			itemsType = QName.createQName(type, namespaceService);
//			parentNodeRef = createDictionary(dictionaryName, dicProps);
//			readItems(xmlr, parentNodeRef);
//		}
	}

//	private Map<QName, Serializable> getProperties() {
//		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//		String value;
//		String propName;
//		while (XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
//				&& xmlr.getLocalName().equals(ExportNamespace.TAG_PROPERTY)) {
//			propName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
//			xmlr.next();
//			if (XMLStreamConstants.CHARACTERS == xmlr.getEventType()) {
//				value = xmlr.getText();
//				properties.put(QName.createQName(propName, namespaceService), value);
//				xmlr.nextTag();
//				xmlr.nextTag();//пропускаем закрывающий тэг
//			}
//		}
//		return properties;
//	}

//	private boolean readItem(XMLStreamReader xmlr, NodeRef parent) throws XMLStreamException {
//		if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
//				&& xmlr.getLocalName().equals(ExportNamespace.TAG_ITEM))) {
//			return false;
//		}
//		String itemName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
//		xmlr.nextTag();//property
//		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
//		properties.put(ContentModel.PROP_NAME, itemName);
//		properties.putAll(getProperties(xmlr));
//		NodeRef current = createItem(parent, properties);
//		readItems(xmlr, current);
//		xmlr.nextTag();//выходим из </item>
//		return true;
//	}
}
