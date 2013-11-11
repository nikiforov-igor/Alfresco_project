package ru.it.lecm.dictionary.beans;

import com.ctc.wstx.exc.WstxParsingException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationExistsException;
import org.alfresco.service.cmr.repository.AssociationRef;
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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * User: AZinovin
 * Date: 14.06.13
 * Time: 10:24
 */
public class XMLImportBeanImpl implements XMLImportBean {
    private static final transient Logger logger = LoggerFactory.getLogger(XMLImporterImpl.class);

    private NodeService nodeService;
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private Repository repositoryHelper;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    @Override
    public XMLImporter getXMLImporter(InputStream inputStream) {
        return new XMLImporterImpl(inputStream);
    }

    public class XMLImporterImpl implements XMLImporter {
        private InputStream inputStream;
	    private XMLImporterInfo importInfo;

        /**
         * Конструктор загрузчика XML
         * @param inputStream входной XML поток
         */
        private XMLImporterImpl(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        /**
         * Считывание элементов из файла
         *
         * @param parentNodeRef родительский элемен, в котором будут созданы импортируемые
         * @throws javax.xml.stream.XMLStreamException
         */
        public XMLImporterInfo readItems(NodeRef parentNodeRef) throws XMLStreamException {
            return readItems(parentNodeRef, false);
        }

        /**
         * Считывание элементов из файла
         *
         * @param parentNodeRef родительский элемен, в котором будут созданы импортируемые
         * @param doNotUpdateIfExist не обновлять, если такой справочник уже существует, иначе обновить свойства справочника и элементы
         * @throws XMLStreamException
         */
        public XMLImporterInfo readItems(NodeRef parentNodeRef, boolean doNotUpdateIfExist) throws XMLStreamException {
	        this.importInfo = new XMLImporterInfo();

            logger.info("Importing dictionary. (doNotUpdateIfExist = {})", doNotUpdateIfExist);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlr = inputFactory.createXMLStreamReader(inputStream);
            try {
                String str = "";

                while (xmlr.hasNext() && !str.equals(ExportNamespace.TAG_ITEMS)) {
                    xmlr.nextTag();
                    str = xmlr.getLocalName();
                }
                if (str.equals(ExportNamespace.TAG_ITEMS)) {
                    readItems(xmlr, parentNodeRef, doNotUpdateIfExist);
                }
            } finally {
                xmlr.close();
            }

            return importInfo;
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
                while (true) {
                    if (!(readItem(xmlr, parent, doNotUpdateIfExist))) {
                        break;
                    }
                }
                xmlr.nextTag();//выходим из </items>
            } catch (WstxParsingException ignored) {
            } catch (XMLStreamException e) {
	            logger.error(e.getMessage(), e);
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
            String itemNameAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
            String itemTypeAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_TYPE);
            String parentPathAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_PATH);

            QName itemType = null;
            QName itemName = null;
            if (itemTypeAttr != null && !itemTypeAttr.isEmpty()) {
                itemType = QName.createQName(itemTypeAttr, namespaceService);
            }
            if (itemNameAttr != null && !itemNameAttr.isEmpty()) {
                itemName = QName.createQName(itemNameAttr, namespaceService);
            }
            xmlr.nextTag();//property
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
            if (itemName != null) {
                properties.put(ContentModel.PROP_NAME, itemName.getLocalName());
            }
            properties.putAll(getProperties(xmlr));
            if (parentPathAttr != null && parentPathAttr.length() > 0) {
                NodeRef actualParent = getNodeByPath(parentPathAttr);
                if (actualParent != null){
                    parent = actualParent;
                }
            }
            NodeRef current = createItem(parent, itemName, itemType, properties, doNotUpdateIfExist);
            readItems(xmlr, current, doNotUpdateIfExist);
            readAssocs(xmlr, current, doNotUpdateIfExist);
            xmlr.nextTag();//выходим из </item>
            return true;
        }

        private boolean readAssocs(XMLStreamReader xmlr, NodeRef parent, boolean doNotUpdateIfExist) throws XMLStreamException {
            if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                    && xmlr.getLocalName().equals(ExportNamespace.TAG_ASSOCS))) {
                return false;
            }
            xmlr.nextTag();//входим в <assocs>
            try {
                while (true) {
                    if (!(readAssoc(xmlr, parent, doNotUpdateIfExist))) break;
                }
                xmlr.nextTag();//выходим из </assocs>
            } catch (XMLStreamException e) {
	            logger.error(e.getMessage(), e);
            }
            return true;
        }

        private boolean readAssoc(XMLStreamReader xmlr, NodeRef parent, boolean doNotUpdateIfExist) throws XMLStreamException {
            if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                    && xmlr.getLocalName().equals(ExportNamespace.TAG_ASSOC))) {
                return false;
            }
            String assocTypeAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_TYPE);
            String assocPathAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_PATH);
            QName assocType = QName.createQName(assocTypeAttr, namespaceService);
            NodeRef targetRef = getNodeByPath(assocPathAttr);
            if (targetRef != null) {
                AssociationDefinition associationDefinition = dictionaryService.getAssociation(assocType);
                List<AssociationRef> existingAssocs = nodeService.getTargetAssocs(parent, assocType);
                boolean create = true;
                if (doNotUpdateIfExist) {
                    for (AssociationRef existingAssoc : existingAssocs) {
                        if (existingAssoc.getTargetRef().equals(targetRef)) {
                            create = false;
                            break;
                        }
                        if (!associationDefinition.isTargetMany()) {
                            create = false;
                            break;
                        }
                    }
                } else if (!associationDefinition.isTargetMany()) {
                    for (AssociationRef existingAssoc : existingAssocs) {
                        nodeService.removeAssociation(parent, existingAssoc.getTargetRef(), assocType);
                    }
                }
                if (create) {
                    try {
                        nodeService.createAssociation(parent, targetRef, assocType);
                    } catch (AssociationExistsException ignored) {
                        logger.warn("Skip create association: {}. Already exist.", new AssociationRef(parent, assocType, targetRef));
                    }
                }
            } else {
	            this.importInfo.addAssocNotFoundError(assocTypeAttr, assocPathAttr);
            }
            xmlr.nextTag();//выходим из </assoc>
            xmlr.nextTag();//выходим из </assoc>
            return true;
        }

        private NodeRef getNodeByPath(String path) {
            NodeRef result = null;
            StringTokenizer t = new StringTokenizer(path, "/");
            if (t.hasMoreTokens())
            {
                result = repositoryHelper.getCompanyHome();
                while (t.hasMoreTokens() && result != null)
                {
                    String name = t.nextToken();
                    try
                    {
                        result = nodeService.getChildByName(result, ContentModel.ASSOC_CONTAINS, name);
                    }
                    catch (AccessDeniedException ade)
                    {
                        result = null;
                    }
                }
            }
            return result;
        }

        /** создание элемента, если существует - обновить свойства\
         *
         *
         * @param parentNodeRef родительский элемент/справочник
         * @param assocQName название ассоциации, если не задано - будет <code>ContentModel.ASSOC_CONTAINS</code>
         * @param itemType  тип создаваемого элемента
         * @param properties свойства
         * @param doNotUpdateIfExist не обновлять существующие записи   @return ссылка на элемент
         * */
        private NodeRef createItem(NodeRef parentNodeRef, QName assocQName, QName itemType, Map<QName, Serializable> properties, boolean doNotUpdateIfExist) {
            String name = null;
            NodeRef node = null;
            Serializable nameProp = properties.get(ContentModel.PROP_NAME);
            if (nameProp != null) {
                name = nameProp.toString();
                node = nodeService.getChildByName(parentNodeRef, ContentModel.ASSOC_CONTAINS, name);
            }
            if (node == null) {
                if (assocQName != null) {
                    name = assocQName.getLocalName();
                    node = nodeService.getChildByName(parentNodeRef, ContentModel.ASSOC_CONTAINS, name);
                }
            }
            if (node == null) {
                if (assocQName == null) {
                    assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name == null ? parentNodeRef.getId() : name);
                }
                node = nodeService.createNode(parentNodeRef, ContentModel.ASSOC_CONTAINS,
                        assocQName,
                        itemType,
                        properties).getChildRef();
	            this.importInfo.setCreatedElementsCount(this.importInfo.getCreatedElementsCount() + 1);
                logger.info("Item '{}' created", name);
            } else if (!doNotUpdateIfExist) {
                nodeService.addProperties(node, properties);
	            this.importInfo.setUpdatedElementsCount(this.importInfo.getUpdatedElementsCount() + 1);
                logger.info("Item '{}' updated", name);
            }
            return node;
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
                if (XMLStreamConstants.CHARACTERS == xmlr.getEventType()
                        || XMLStreamConstants.CDATA == xmlr.getEventType()) {
                    value = xmlr.getText();
                    properties.put(QName.createQName(propName, namespaceService), value);
                    xmlr.nextTag();
                }
                xmlr.nextTag();//пропускаем закрывающий тэг
            }
            return properties;
        }
    }
}
