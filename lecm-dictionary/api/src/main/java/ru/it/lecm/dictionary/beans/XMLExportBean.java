package ru.it.lecm.dictionary.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.List;

/**
 * User: AZinovin
 * Date: 14.06.13
 * Time: 10:23
 */
public interface XMLExportBean {

    XMLExporter getXMLExporter(OutputStream outputStream) throws XMLStreamException;

    public interface XMLExporter {
        public void writeItems(NodeRef... nodes) throws XMLStreamException;

        public void writeItems(List<NodeRef> nodes) throws XMLStreamException;

        public void writeChildItems(NodeRef parentNode) throws XMLStreamException;

        void close() throws XMLStreamException;
    }
}
