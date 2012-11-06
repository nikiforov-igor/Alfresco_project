package ru.it.lecm.dictionary.export;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmlDictionaryExporter {

	private XMLStreamWriter xmlw;
	private List<String> fields;
	private NodeService nodeService;
	private NamespacePrefixResolver namespaceService;
	private static final Set<String> ignoredNamespaces = new HashSet<String>();

	static {
		ignoredNamespaces.add(NamespaceService.CONTENT_MODEL_1_0_URI);
		ignoredNamespaces.add(NamespaceService.SYSTEM_MODEL_1_0_URI);
	}

	public XmlDictionaryExporter(OutputStream resOutputStream, List<String> fields, NodeService nodeService, NamespaceService namespaceService) throws XMLStreamException {
		// Create an output factory
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		xmlw = xmlof.createXMLStreamWriter(resOutputStream);
		this.fields = fields;
		this.nodeService = nodeService;
		this.namespaceService = namespaceService;
		xmlw.writeStartDocument("1.0");
	}

	public void writeDictionary(NodeRef nodeRef) throws XMLStreamException {
		xmlw.writeStartElement("dictionary");
		{
			String name = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
			xmlw.writeAttribute("name", name);
			writeProperties(nodeRef, true);
			//Обход по дереву
			writeItems(nodeRef);
		}
		xmlw.writeEndElement();

	}

	private void writeItems(NodeRef node) throws XMLStreamException {
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node);
		if (!childAssocs.isEmpty()) {
			xmlw.writeStartElement("items");
			{
				for (ChildAssociationRef subNodeAss : childAssocs) {
					NodeRef childRef = subNodeAss.getChildRef();
					writeItem(childRef);
				}
			}
			xmlw.writeEndElement();
		}
	}

	private void writeItem(NodeRef childRef) throws XMLStreamException {
		xmlw.writeStartElement("item");
		{
			String name = nodeService.getPrimaryParent(childRef).getQName().getLocalName();
			xmlw.writeAttribute("name", name);
			writeProperties(childRef);
		}
		writeItems(childRef);
		xmlw.writeEndElement();
	}

	private void writeProperties(NodeRef childRef) throws XMLStreamException {
		writeProperties(childRef, false);
	}

	private void writeProperties(NodeRef childRef, boolean doNotFilterFields) throws XMLStreamException {
		//экспорт свойств справочника
		for (Map.Entry<QName, Serializable> entry : nodeService.getProperties(childRef).entrySet()) {
			QName qName = entry.getKey().getPrefixedQName(namespaceService);
			String value = entry.getValue().toString();
			if ((doNotFilterFields && !ignoredNamespaces.contains(qName.getNamespaceURI()))
				|| (!doNotFilterFields && fields != null && fields.contains(qName.getPrefixString()))) {
				writeProperty(qName, value);
			}
		}
	}

	private void writeProperty(QName name, String value) throws XMLStreamException {
		xmlw.writeStartElement("property");
		xmlw.writeAttribute("name", name.toPrefixString());
		xmlw.writeCharacters(value);
		xmlw.writeEndElement();
	}

	public void close() throws XMLStreamException {
		xmlw.writeEndDocument();
		// Close the writer to flush the output
		xmlw.close();
	}
}