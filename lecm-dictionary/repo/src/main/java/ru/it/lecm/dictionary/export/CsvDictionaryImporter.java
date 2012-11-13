package ru.it.lecm.dictionary.export;

import com.csvreader.CsvReader;
import org.alfresco.model.ContentModel;
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
 * User: mShafeev
 * Date: 09.11.12
 * Time: 10:08
 */
public class CsvDictionaryImporter {
	private NodeService nodeService;
	private NamespaceService namespaceService;
	private QName itemsType = null;
	private InputStream inputStream;
	private NodeRef parentNodeRef;

	public CsvDictionaryImporter(InputStream inputStream, NodeService nodeService,
	                            NamespaceService namespaceService, NodeRef parentNodeRef) {
		this.inputStream = inputStream;
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
		ArrayList<String> header = new ArrayList<String>();
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

		csvr.readHeaders();
		for (int i = 0; i < csvr.getHeaderCount(); i++){
			header.add(csvr.getHeader(i));
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
	}
}
