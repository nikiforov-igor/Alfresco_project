package ru.it.lecm.dictionary.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

/**
 * User: AZinovin
 * Date: 14.06.13
 * Time: 10:23
 */
public interface XMLImportBean {

    XMLImporter getXMLImporter(InputStream inputStream);

    public interface XMLImporter {
        public XMLImporterInfo readItems(NodeRef parentNodeRef) throws XMLStreamException;

        public XMLImporterInfo readItems(NodeRef parentNodeRef, boolean doNotUpdateIfExist) throws XMLStreamException;
    }
}
