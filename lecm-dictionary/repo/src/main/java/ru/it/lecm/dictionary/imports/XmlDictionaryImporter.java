package ru.it.lecm.dictionary.imports;

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
 * Класс загрузчик справочника из XML описания
 */
public class XmlDictionaryImporter {
    private final Repository repositoryHelper;
    private NodeService nodeService;
	private NamespaceService namespaceService;
	private DictionaryService dictionaryService;
	private QName itemsType = null;
	private InputStream inputStream;
	private NodeRef dictionariesRoot;
    private static final transient Logger logger = LoggerFactory.getLogger(XmlDictionaryImporter.class);

    /**
	 * Конструктор загрузчика XML
     * @param inputStream входной XML поток
     * @param nodeService           nodeService
     * @param namespaceService      namespaceService
     * @param repositoryHelper      repositoryHelper
     * @param dictionariesRoot Корневая папка для словарей.
     */
	public XmlDictionaryImporter(InputStream inputStream, NodeService nodeService, NamespaceService namespaceService, DictionaryService dictionaryService, Repository repositoryHelper, NodeRef dictionariesRoot) {
		this.inputStream = inputStream;
		this.nodeService = nodeService;
		this.namespaceService = namespaceService;
		this.dictionaryService = dictionaryService;
		this.dictionariesRoot = dictionariesRoot;
		this.repositoryHelper = repositoryHelper;
	}

	/**
	 * Считывание справочника
	 * @throws XMLStreamException
	 */
	public void readDictionary() throws XMLStreamException {
		readDictionary(false);
	}

	/**
	 * Считывание справочника
	 * @param doNotUpdateIfExist не обновлять, если такой справочник уже существует, иначе обновить свойства справочника и элементы
	 * @throws XMLStreamException
	 */
	public void readDictionary(boolean doNotUpdateIfExist) throws XMLStreamException {
        logger.info("Importing dictionary. (doNotUpdateIfExist = {})", doNotUpdateIfExist);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlr = inputFactory.createXMLStreamReader(inputStream);
		NodeRef parentNodeRef;
		String str = "";

		while (xmlr.hasNext() && !(str.equals(ExportNamespace.TAG_DICTIONARY) || str.equals(ExportNamespace.TAG_ITEMS))) {
			xmlr.nextTag();
			str = xmlr.getLocalName();
		}
		if (str.equals(ExportNamespace.TAG_DICTIONARY)) {
			String dictionaryName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
			if (dictionaryName == null) {
				return;
			}
			xmlr.nextTag();
			Map<QName, Serializable> dicProps = getProperties(xmlr);
			parentNodeRef = createDictionary(dictionaryName, dicProps, doNotUpdateIfExist);
			String type = nodeService.getProperty(parentNodeRef, ExportNamespace.PROP_TYPE).toString();
			itemsType = QName.createQName(type, namespaceService);
            logger.info("Items type '{}'", itemsType);
			readItems(xmlr, parentNodeRef, doNotUpdateIfExist);
		} else if (str.equals(ExportNamespace.TAG_ITEMS)) {
            readItems(xmlr, getDictionariesRoot(), doNotUpdateIfExist);
        }
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
			e.printStackTrace();
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
        QName itemType = itemsType;
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
            e.printStackTrace();
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
                    result = this.nodeService.getChildByName(result, ContentModel.ASSOC_CONTAINS, name);
                }
                catch (AccessDeniedException ade)
                {
                    result = null;
                }
            }
        }
        return result;
    }

    /** создание справочника, если существует - обновить свойства
	 *
	 * @param dictionaryName имя справочника
	 * @param dicProps свойства
	 * @param doNotUpdateIfExist не обновлять существующие записи
	 * @return ссылка на справочник
	 */
	private NodeRef createDictionary(String dictionaryName, Map<QName, Serializable> dicProps, boolean doNotUpdateIfExist) {
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
            logger.info("Dictionary '{}' created", dictionaryName);
		} else if (!doNotUpdateIfExist) {
			nodeService.addProperties(dictionary, dicProps);
            logger.info("Dictionary '{}' updated", dictionaryName);
		}
		return dictionary;
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
            logger.info("Item '{}' created", name);
        } else if (!doNotUpdateIfExist) {
            nodeService.addProperties(node, properties);
            logger.info("Item '{}' updated", name);
        }
        return node;
    }

	/** получение корня справочников, если нет - создать
	 *
	 * @return ссылку на корневой контейнер справочников
	 */
	private NodeRef getDictionariesRoot() {
		return this.dictionariesRoot;
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
			if (XMLStreamConstants.CHARACTERS == xmlr.getEventType()) {
				value = xmlr.getText();
				properties.put(QName.createQName(propName, namespaceService), value);
				xmlr.nextTag();
			}
            xmlr.nextTag();//пропускаем закрывающий тэг
        }
		return properties;
	}
}