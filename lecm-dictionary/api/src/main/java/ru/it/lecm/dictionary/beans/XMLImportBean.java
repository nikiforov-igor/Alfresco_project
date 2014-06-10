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

        public XMLImporterInfo readItems(NodeRef parentNodeRef, XMLImportBean.UpdateMode updateMode) throws XMLStreamException;
    }

    public enum UpdateMode {
        CREATE_NEW("CreateNew", false, false),
        CREATE_OR_UPDATE("CreateOrUpdate", true, false),
        REWRITE_CHILDREN("RewriteChildren", true, true);
        private final String strValue;
        private final boolean updateProperties;
        private final boolean rewriteChildren;

        UpdateMode(String strValue, boolean updateProperties, boolean rewriteChildren) {
            this.strValue = strValue.toLowerCase();
            this.updateProperties = updateProperties;
            this.rewriteChildren = rewriteChildren;
        }

        public boolean isUpdateProperties() {
            return updateProperties;
        }

        public boolean isRewriteChildren() {
            return rewriteChildren;
        }

        public static UpdateMode valueOf(String strValue, UpdateMode defaultValue) {
            for (int i = 0; i < values().length; i++) {
                UpdateMode updateMode = values()[i];
                if (updateMode.strValue.equals(strValue.toLowerCase())
                        || updateMode.name().equalsIgnoreCase(strValue)) {
                    return updateMode;
                }
            }
            return defaultValue == null ? UpdateMode.CREATE_NEW : defaultValue;
        }
    }
}
