package ru.it.lecm.dictionary.beans;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.it.lecm.dictionary.ExportSettings;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 04.06.13
 * Time: 9:09
 */
public class ExportSettingsHolder implements ExportSettings {

    private static final transient Logger logger = LoggerFactory.getLogger(ExportSettingsHolder.class);

    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private DictionaryBean dictionaryBean;
    private FileFolderService fileFolderService;

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    private Map<String, List<String>> typeFields = new HashMap<>();

    @Override
    public List<String> getFieldsForType(String typeName) {
        List<String> fields = readSettingsFromRepository(typeName);
        if (fields.isEmpty()) {
            fields = typeFields.get(typeName);
            if (fields == null) {
                fields = new ArrayList<>();
            }
            if (fields.isEmpty()) {
                fields.add("cm:name");
                fields.add("cm:title");
                Map<QName, PropertyDefinition> allProperties = dictionaryService.getPropertyDefs(QName.createQName(typeName, namespaceService));

                for (QName property : allProperties.keySet()) {
                    String string = property.toPrefixString();
                    if (!string.equals("cm:content") && !string.endsWith("-ref") && !fields.contains(string)) {
                        fields.add(string);
                    }
                }


            }
        }
        return fields;
    }

    @Override
    public void addFieldsForType(String typeName, List<String> fields) {
        List<String> existingFields = typeFields.get(typeName);
        if (existingFields == null) {
            existingFields = new ArrayList<>();
        }
        for (String field : fields) {
            if (!existingFields.contains(field)) {
                existingFields.add(field);
            }
        }
        typeFields.put(typeName, existingFields);
    }

    private List<String> readSettingsFromRepository(String typeName) {
        List<String> fields = new ArrayList<>();
        NodeRef dictionariesRoot = dictionaryBean.getDictionariesRoot();
        NodeRef settingsRef = fileFolderService.searchSimple(dictionariesRoot, "exportSettings.xml");
        if (settingsRef != null) {
            try (InputStream inputStream = fileFolderService.getReader(settingsRef).getContentInputStream()) {
                    SAXParserFactory SAXFactory = SAXParserFactory.newInstance();
                    SAXParser parser = SAXFactory.newSAXParser();
                    TypeSettingsParserHandler handler = new TypeSettingsParserHandler(typeName);

                    parser.parse(inputStream, handler);

                    List<String> list = handler.getResult();
                    if (list != null && !list.isEmpty()) {
                        fields.addAll(list);
                    }

            } catch (IOException | ParserConfigurationException | SAXException e) {
                logger.error("Unable to read settings from file");
            }
        }
        return fields;
    }

    private class TypeSettingsParserHandler extends DefaultHandler {
        private String typeName;
        private ArrayList<String> result;
        private boolean typeFound = false;

        public TypeSettingsParserHandler(String typeName) {
            super();
            this.typeName = typeName;
        }

        public ArrayList<String> getResult() {
            return result;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {

            switch (qName.toLowerCase()) {
                case "type":
                    String type = attributes.getValue("id");
                    if (typeName.equalsIgnoreCase(type)) {
                        result = new ArrayList<>();
                        typeFound = true;
                    }
                    break;
                case "field":
                    if (typeFound) {
                        String id = attributes.getValue("id");
                        if (id != null && !id.trim().isEmpty()) {
                            result.add(id);
                        }
                    }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("type".equalsIgnoreCase(qName)) {
                typeFound = false;
            }
        }
    }
}
