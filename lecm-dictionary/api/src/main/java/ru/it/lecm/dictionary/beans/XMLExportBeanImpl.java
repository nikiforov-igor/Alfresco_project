package ru.it.lecm.dictionary.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.dictionary.ExportSettings;
import ru.it.lecm.dictionary.export.ExportNamespace;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * User: AZinovin
 * Date: 14.06.13
 * Time: 10:24
 */
public class XMLExportBeanImpl implements XMLExportBean {
	private static final transient Logger log = LoggerFactory.getLogger(XMLExportBeanImpl.class);

    private static final Set<String> ignoredNamespaces = new HashSet<String>();
    private static final Set<QName> ignoredTypes = new HashSet<QName>();

    static {
        ignoredNamespaces.add(NamespaceService.CONTENT_MODEL_1_0_URI);
        ignoredNamespaces.add(NamespaceService.SYSTEM_MODEL_1_0_URI);

        ignoredTypes.add(ContentModel.TYPE_THUMBNAIL);
        ignoredTypes.add(ContentModel.TYPE_FAILED_THUMBNAIL);
    }

    private ExportSettings exportSettings;
    protected NodeService nodeService;
    protected NamespaceService namespaceService;
	private ContentService contentService;

    public void setExportSettings(ExportSettings exportSettings) {
        this.exportSettings = exportSettings;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	@Override
    public XMLExporter getXMLExporter(OutputStream outputStream) throws XMLStreamException {
        return new XMLExporterImpl(outputStream);
    }

    public class XMLExporterImpl implements XMLExporter {

        protected XMLStreamWriter xmlw;

        protected XMLExporterImpl(OutputStream resOutputStream) throws XMLStreamException {
            // Create an output factory
            XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
            xmlw = xmlof.createXMLStreamWriter(resOutputStream);
            xmlw.writeStartDocument("1.0");
        }

        public void writeItems(NodeRef... nodes) throws XMLStreamException {
            writeItems(Arrays.asList(nodes));
        }

        public void writeItems(List<NodeRef> nodes) throws XMLStreamException {
            if (!nodes.isEmpty()) {
                xmlw.writeStartElement(ExportNamespace.TAG_ITEMS);
                {
                    for (NodeRef childRef : nodes) {
                        writeItem(childRef);
                    }
                }
                xmlw.writeEndElement();
            }
        }

        public void writeChildItems(NodeRef parentNode) throws XMLStreamException {
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(parentNode);
            List<NodeRef> nodes = new ArrayList<NodeRef>();
            if (!childAssocs.isEmpty()) {
                for (ChildAssociationRef subNodeAss : childAssocs) {
                    NodeRef childRef = subNodeAss.getChildRef();
                    nodes.add(childRef);
                }
            }
            writeItems(nodes);
        }

        private void writeItem(NodeRef childRef) throws XMLStreamException {
            if (isArchive(childRef)) {
                return;
            }
            QName typeQName = nodeService.getType(childRef);
            if (ignoredTypes.contains(typeQName)) {
                return;
            }
            xmlw.writeStartElement(ExportNamespace.TAG_ITEM);
            {
                String name = nodeService.getPrimaryParent(childRef).getQName().toPrefixString(namespaceService);
                xmlw.writeAttribute(ExportNamespace.ATTR_NAME, name);
                String typeAttr = typeQName.toPrefixString(namespaceService);
                xmlw.writeAttribute(ExportNamespace.ATTR_TYPE, typeAttr);
                List<String> fieldsForType = getFieldsForType(typeAttr);
                writeProperties(childRef, fieldsForType);
	            if (typeQName.equals(ContentModel.TYPE_CONTENT)) {
		            writeContent(childRef);
	            }
                writeChildItems(childRef);
                writeAssocs(childRef, fieldsForType);
            }
            xmlw.writeEndElement();
        }

        private void writeAssocs(NodeRef childRef, List<String> fieldsForType) throws XMLStreamException {
            List<AssociationRef> assocs = nodeService.getTargetAssocs(childRef, RegexQNamePattern.MATCH_ALL);
            if (!assocs.isEmpty()) {
                xmlw.writeStartElement(ExportNamespace.TAG_ASSOCS);
                {
                    for (AssociationRef assoc : assocs) {
                        writeAssoc(assoc, fieldsForType);
                    }
                }
                xmlw.writeEndElement();
            }
        }

        protected void writeAssoc(AssociationRef assoc, List<String> fieldsForType) throws XMLStreamException {
            QName assocTypeQName = assoc.getTypeQName();
            if (!isExportField(fieldsForType, assocTypeQName)) {
                return; //фильтр по имени ассоциации
            }
            NodeRef targetRef = assoc.getTargetRef();
            if (isArchive(targetRef)) {
                return; //не экспортировать архивные
            }
            xmlw.writeStartElement(ExportNamespace.TAG_ASSOC);
            xmlw.writeAttribute(ExportNamespace.ATTR_TYPE, assocTypeQName.toPrefixString(namespaceService));
            Path path = nodeService.getPath(targetRef);
            StringBuilder strPath = new StringBuilder();
            Iterator<Path.Element> iterator = path.iterator();
            if (iterator.hasNext()) {
                iterator.next();
            }
            if (iterator.hasNext()) {
                iterator.next();
            }
            while (iterator.hasNext()) {
                Path.Element element = iterator.next();
                ChildAssociationRef elementRef = ((Path.ChildAssocElement)element).getRef();
                Serializable nameProp = nodeService.getProperty(elementRef.getChildRef(), ContentModel.PROP_NAME);
                // use the name property if we are allowed access to it
                String elementString = nameProp.toString();
//            elementString = elementRef.getQName().getLocalName();
                strPath.append("/").append(elementString);
            }
            xmlw.writeAttribute(ExportNamespace.ATTR_PATH, strPath.toString());
            xmlw.writeEndElement();
        }

        private void writeProperties(NodeRef childRef, List fields) throws XMLStreamException {
            writeProperties(childRef, false, fields);
        }

        private void writeProperties(NodeRef childRef, boolean doNotFilterFields, List fields) throws XMLStreamException {
            //экспорт свойств справочника
            for (Map.Entry<QName, Serializable> entry : nodeService.getProperties(childRef).entrySet()) {
                QName qName = entry.getKey().getPrefixedQName(namespaceService);
                String value = entry.getValue() != null ? entry.getValue().toString() : null;
                if ((doNotFilterFields && !ignoredNamespaces.contains(qName.getNamespaceURI()))
                        || (!doNotFilterFields && isExportField(fields, qName))) {
                    writeProperty(qName, value);
                }
            }
        }

        protected boolean isExportField(List exportFieldsList, QName qName) {
            return exportFieldsList != null && exportFieldsList.contains(qName.toPrefixString(namespaceService));
        }

        private void writeProperty(QName name, String value) throws XMLStreamException {
            if (value != null) {
                xmlw.writeStartElement(ExportNamespace.TAG_PROPERTY);
                xmlw.writeAttribute(ExportNamespace.ATTR_NAME, name.toPrefixString());
                xmlw.writeCData(value);
                xmlw.writeEndElement();
            }
        }

	    private void writeContent(NodeRef fileRef) throws XMLStreamException {
		    ContentReader reader = contentService.getReader(fileRef, ContentModel.PROP_CONTENT);
		    String contentBase64 = "";
		    InputStream contentStream = reader.getContentInputStream();
		    try {
			    byte[] bytes = IOUtils.toByteArray(contentStream);
			    contentBase64 = Base64.encodeBase64String(bytes);
		    } catch (IOException e) {
			    log.error("Error read content", e);
		    } finally {
			    try {
				    contentStream.close();
			    } catch (IOException e) {
				    log.error("Error close content reader", e);
			    }
		    }

		    xmlw.writeStartElement(ExportNamespace.TAG_PROPERTY);
		    xmlw.writeAttribute(ExportNamespace.ATTR_NAME, ContentModel.PROP_CONTENT.toPrefixString(namespaceService));
		    xmlw.writeCData(contentBase64);
		    xmlw.writeEndElement();
	    }

        public void close() throws XMLStreamException {
            xmlw.writeEndDocument();
            // Close the writer to flush the output
            xmlw.close();
        }

        /**
         * Проверка элемента на архивность
         * @param ref Ссылка на элемент
         * @return true - если элемент архивный, иначе false
         */
        protected boolean isArchive(NodeRef ref){
	        boolean isArchive = StoreRef.STORE_REF_ARCHIVE_SPACESSTORE.equals (ref.getStoreRef ());
	        if (isArchive) {
		        return true;
	        } else {
		        Boolean isActive = (Boolean) nodeService.getProperty(ref, QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active"));
		        return isActive != null && !isActive;
	        }
        }

        //локальное кэширование списка выгружаемых атрибутов для текущей сессии экспорта
        private Map<String, List<String>> localFieldsForTypeCache = new HashMap<>();

        private List<String> getFieldsForType(String typeName) {
            if (!localFieldsForTypeCache.containsKey(typeName)) {
                localFieldsForTypeCache.put(typeName, exportSettings.getFieldsForType(typeName));
            }
            return localFieldsForTypeCache.get(typeName);
        }

    }
}
