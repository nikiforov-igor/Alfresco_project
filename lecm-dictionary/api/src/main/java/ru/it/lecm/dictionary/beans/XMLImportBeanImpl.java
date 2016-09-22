package ru.it.lecm.dictionary.beans;

import com.ctc.wstx.exc.WstxParsingException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Base64;
import ru.it.lecm.dictionary.export.ExportNamespace;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * User: AZinovin
 * Date: 14.06.13
 * Time: 10:24
 */
public class XMLImportBeanImpl implements XMLImportBean {
    protected static final transient Logger logger = LoggerFactory.getLogger(XMLImporterImpl.class);

    protected NodeService nodeService;
    protected NamespaceService namespaceService;
    protected DictionaryService dictionaryService;
    private Repository repositoryHelper;
	private ContentService contentService;
	private MimetypeService mimetypeService;
    private SearchService searchService;

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

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setMimetypeService(MimetypeService mimetypeService) {
		this.mimetypeService = mimetypeService;
	}

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public XMLImporter getXMLImporter(InputStream inputStream) {
        return new XMLImporterImpl(inputStream);
    }

    public class XMLImporterImpl implements XMLImporter {
        private InputStream inputStream;
	    protected XMLImporterInfo importInfo;

	    protected Set<AssociationInfo> assocs;
	    
        /**
         * Конструктор загрузчика XML
         * @param inputStream входной XML поток
         */
        protected XMLImporterImpl(InputStream inputStream) {
            this.inputStream = inputStream;
	        this.assocs = new LinkedHashSet<>();
        }

        /**
         * Считывание элементов из файла
         *
         * @param parentNodeRef родительский элемен, в котором будут созданы импортируемые
         * @throws javax.xml.stream.XMLStreamException
         */
        public XMLImporterInfo readItems(NodeRef parentNodeRef) throws XMLStreamException {
            return readItems(parentNodeRef, UpdateMode.CREATE_NEW);
        }

        /**
         * Считывание элементов из файла
         *
         * @param parentNodeRef родительский элемен, в котором будут созданы импортируемые
         * @param updateMode режим обновления записей
         * @throws javax.xml.stream.XMLStreamException
         */
        public XMLImporterInfo readItems(NodeRef parentNodeRef, UpdateMode updateMode) throws XMLStreamException {
	        this.importInfo = new XMLImporterInfo();

            logger.trace("Importing dictionary. (updateMode = {})", updateMode);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlr = inputFactory.createXMLStreamReader(inputStream);
            try {
                String str = "";

                while (xmlr.hasNext() && !str.equals(ExportNamespace.TAG_ITEMS)) {
                    xmlr.nextTag();
                    str = xmlr.getLocalName();
                }

                if (str.equals(ExportNamespace.TAG_ITEMS)) {
                    readItems(xmlr, parentNodeRef, updateMode);
                }
	            createAssocs();
            } finally {
                xmlr.close();
            }

            return importInfo;
        }

        /** считывание элементов и создание
         *
         * @param xmlr XML Reader
         * @param parent ссылка на родительский элемент
         * @param updateMode режим обновления записей
         * @return true если элементы были создан
         * @throws javax.xml.stream.XMLStreamException
         */

        private boolean readItems(XMLStreamReader xmlr, NodeRef parent, UpdateMode updateMode) throws XMLStreamException {
            if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                    && xmlr.getLocalName().equals(ExportNamespace.TAG_ITEMS))) {
                return false;
            }
            String itemUpdateModeAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_UPDATE_MODE);
            if (itemUpdateModeAttr != null) {
                updateMode = UpdateMode.valueOf(itemUpdateModeAttr, updateMode);
                logger.trace("Update mode changed to '{}'", updateMode);
            }
            xmlr.nextTag();//входим в <items>
            try {
                while (true) {
                    if (!(readItem(xmlr, parent, updateMode))) {
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
         * @param updateMode режим обновления записей
         * @return true если элемент был создан
         * @throws javax.xml.stream.XMLStreamException
         */
        private boolean readItem(XMLStreamReader xmlr, NodeRef parent, UpdateMode updateMode) throws XMLStreamException {
            if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                    && xmlr.getLocalName().equals(ExportNamespace.TAG_ITEM))) {
                return false;
            }
            String itemNameAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
            String itemTypeAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_TYPE);

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
            int createdElementsCount = this.importInfo.getCreatedElementsCount();
            NodeRef current = createItem(parent, itemName, itemType, properties, updateMode);
            boolean isNewNode = this.importInfo.getCreatedElementsCount() != createdElementsCount;  //отслеживаем факт создания новой ноды
            if (updateMode.isRewriteChildren()) {
                List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(current);
                for (ChildAssociationRef childAssoc : childAssocs) {
                    if (nodeService.exists(childAssoc.getChildRef()) && !nodeService.hasAspect(childAssoc.getChildRef(), ContentModel.ASPECT_PENDING_DELETE)) {
                        nodeService.deleteNode(childAssoc.getChildRef());
                    }
                }
            }
            readItems(xmlr, current, updateMode);
            readAssocs(xmlr, current, isNewNode ? UpdateMode.CREATE_OR_UPDATE : updateMode);    //для новых элементов всегда заполняем оссоциации
            xmlr.nextTag();//выходим из </item>
            return true;
        }

        private boolean readAssocs(XMLStreamReader xmlr, NodeRef parent, UpdateMode updateMode) throws XMLStreamException {
            if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                    && xmlr.getLocalName().equals(ExportNamespace.TAG_ASSOCS))) {
                return false;
            }
            xmlr.nextTag();//входим в <assocs>
            try {
                while (true) {
                    if (!(readAssoc(xmlr, parent, updateMode))) break;
                }
                xmlr.nextTag();//выходим из </assocs>
            } catch (XMLStreamException e) {
	            logger.error(e.getMessage(), e);
            }
            return true;
        }

        protected boolean readAssoc(XMLStreamReader xmlr, NodeRef parent, UpdateMode updateMode) throws XMLStreamException {
            if (!(XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                    && xmlr.getLocalName().equals(ExportNamespace.TAG_ASSOC))) {
                return false;
            }
            String assocTypeAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_TYPE);
            String assocPathAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_PATH);
            String assocXPathAttr = xmlr.getAttributeValue("", ExportNamespace.ATTR_XPATH);

            this.assocs.add(new AssociationInfo(parent, assocTypeAttr, assocPathAttr, assocXPathAttr, updateMode));

            xmlr.nextTag();//выходим из </assoc>
            xmlr.nextTag();//выходим из </assoc>
            return true;
        }

	    protected void createAssocs() {
		    for (AssociationInfo associationInfo: this.assocs) {
                createAssoc(associationInfo.getParentRef(), associationInfo.getAssocType(), associationInfo.getAssocPath(), associationInfo.getAssocXPath(), associationInfo.getUpdateMode());
		    }
	    }

	    private void createAssoc(NodeRef parent,  String assocTypeAttr, String assocPathAttr, String assocXPath,  UpdateMode updateMode) {
		    QName assocType = QName.createQName(assocTypeAttr, namespaceService);

            NodeRef targetRef = null;
            if (StringUtils.isNotEmpty(assocXPath)) {
                targetRef = getNodeByXPath(assocXPath);
            }
            if (StringUtils.isNotEmpty(assocPathAttr) && targetRef == null) {
                targetRef = getNodeByPath(assocPathAttr);
            }

		    if (targetRef != null) {
			    AssociationDefinition associationDefinition = dictionaryService.getAssociation(assocType);
			    List<AssociationRef> existingAssocs = nodeService.getTargetAssocs(parent, assocType);
			    boolean create = false;
			    if (updateMode.isUpdateProperties()) {
                    create = true;
				    for (AssociationRef existingAssoc : existingAssocs) {
					    if (existingAssoc.getTargetRef().equals(targetRef)) {
						    create = false;
						    break;
					    }
					    if (!associationDefinition.isTargetMany()) {
                            //очищаем только если есть изменения и ассоциация не множественная
                            nodeService.removeAssociation(parent, existingAssoc.getTargetRef(), assocType);
						    break;
					    }
				    }
			    }
			    if (create) {
				    try {
					    nodeService.createAssociation(parent, targetRef, assocType);
				    } catch (AssociationExistsException ignored) {
					    logger.trace("Skip create association: {}. Already exist.", new AssociationRef(parent, assocType, targetRef));
				    }
			    }
		    } else {
			    this.importInfo.addAssocNotFoundError(assocTypeAttr, assocPathAttr);
		    }
	    }

        protected NodeRef getNodeByPath(String path) {
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
                        List<ChildAssociationRef> assocList = nodeService.getChildAssocs(result);
                        result = nodeService.getChildByName(result, ContentModel.ASSOC_CONTAINS, name);
                        logger.trace("getChildByName: " + name + ", result: " + result);
                        result = checkChildSearch(result, assocList, name);
                    }
                    catch (AccessDeniedException ade)
                    {
                        result = null;
                    }
                }
            }
            logger.debug("getNodeByPath: " + path + ", result: " + result);
            return result;
        }

        private NodeRef getNodeByXPath(String xPath) {

            List<NodeRef> nodeRefs = searchService.selectNodes(repositoryHelper.getCompanyHome(), xPath, null, namespaceService, false);

            if (nodeRefs == null || nodeRefs.isEmpty()) {
                return null;
            }

            return nodeRefs.get(0);
        }

        private NodeRef checkChildSearch(NodeRef checkedChildRef, List<ChildAssociationRef> assocList, String name) {
            NodeRef result = checkedChildRef;
            boolean needToSearchAgain = (checkedChildRef == null);
            QName orgElementNameQName = QName.createQName("http://www.it.ru/lecm/org/structure/1.0", "element-full-name");
            QName orgElementActiveQName = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");
            if (!needToSearchAgain) {
                Serializable orgElementNameProp = nodeService.getProperty(checkedChildRef, orgElementNameQName);
                Serializable orgElementActiveProp = nodeService.getProperty(checkedChildRef, orgElementActiveQName);
                if (orgElementNameProp != null && orgElementActiveProp != null) {
                    String orgElementName = orgElementNameProp.toString();
                    String orgElementActive = orgElementNameProp.toString();
                    if (!name.equals(orgElementName) || !"true".equalsIgnoreCase(orgElementActive)) {
                        needToSearchAgain = true;
                        logger.debug("getChildByName: wrong result for orgElement, real name: " + orgElementName);
                    }
                }
            }
            if (needToSearchAgain) {
                boolean isResultCorrect = false;
                for (ChildAssociationRef assocRef : assocList) {
                    try {
                        String childName = nodeService.getProperty(assocRef.getChildRef(), orgElementNameQName).toString();
                        String childActive = nodeService.getProperty(assocRef.getChildRef(), orgElementActiveQName).toString();
                        if (name.equals(childName) && "true".equalsIgnoreCase(childActive)) {
                            result = assocRef.getChildRef();
                            isResultCorrect = true;
                        }
                    } catch (Exception ex) {
                    }
                }
                if (isResultCorrect) {
                    logger.debug("getChildByName: replace result for orgElement name");
                } else {
                    logger.debug("getChildByName: left wrong result, cant find child for orgElement");
                }
            }
            if (result == null) {
                for (ChildAssociationRef assocRef : assocList) {
                    logger.debug("Existing assoc child: " + assocRef.getQName() + ", nodeRef: " + assocRef.getChildRef() + ", nodeName: " + nodeService.getProperty(assocRef.getChildRef(), ContentModel.PROP_NAME));
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
         * @param updateMode режим обновления записей
         * @return ссылка на элемент
         * */
        private NodeRef createItem(NodeRef parentNodeRef, QName assocQName, QName itemType, Map<QName, Serializable> properties, UpdateMode updateMode) {
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

	            String contentValue = null;
	            if (itemType.equals(ContentModel.TYPE_CONTENT)) {
		            contentValue = (String) properties.get(ContentModel.PROP_CONTENT);
		            properties.remove(ContentModel.PROP_CONTENT);
	            }

                node = nodeService.createNode(parentNodeRef, ContentModel.ASSOC_CONTAINS,
                        assocQName,
                        itemType,
                        properties).getChildRef();

	            if (contentValue != null) {
		            ContentWriter writer = contentService.getWriter(node, ContentModel.PROP_CONTENT, true);

		            String mimeType = mimetypeService.guessMimetype((String) nameProp);
		            if (mimeType != null) {
			            writer.setMimetype(mimeType);
		            }

		            InputStream is = new ByteArrayInputStream(Base64.decode(contentValue));
		            writer.putContent(is);
	            }

	            this.importInfo.setCreatedElementsCount(this.importInfo.getCreatedElementsCount() + 1);
                logger.trace("Item '{}' created", name);
            } else if (updateMode.isUpdateProperties()) {
                nodeService.addProperties(node, properties);
	            this.importInfo.setUpdatedElementsCount(this.importInfo.getUpdatedElementsCount() + 1);
                logger.trace("Item '{}' updated", name);
            }
            return node;
        }

        /** считываем свойства из XML
         *
         * @param xmlr XML Reader
         * @return карта свойств
         * @throws javax.xml.stream.XMLStreamException
         */
        private Map<QName, Serializable> getProperties(XMLStreamReader xmlr) throws XMLStreamException {
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
            String propName;
            while (XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                    && xmlr.getLocalName().equals(ExportNamespace.TAG_PROPERTY)) {
                propName = xmlr.getAttributeValue("", ExportNamespace.ATTR_NAME);
                xmlr.next();
                getProperty(xmlr, properties, propName);
	            xmlr.nextTag();//пропускаем закрывающий тэг
            }
            return properties;
        }
    }
        
    private void getProperty (XMLStreamReader xmlr, Map<QName, Serializable> properties, String propName) throws XMLStreamException {
    	// Получить содержимое поля
    	String value = getValue(xmlr);
    	
    	// Попробовать получить локализированные значения поля
    	boolean hasLocales = false;
        MLText mlTextValue = new MLText();
        while (XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
                && xmlr.getLocalName().equals(ExportNamespace.TAG_VALUE)) {
        	String langName = xmlr.getAttributeValue("", ExportNamespace.ATTR_LANG);
        	xmlr.next();
        	
        	String mlValue = getValue(xmlr);
            if (mlValue != null) {
            	mlTextValue.addValue(new Locale(langName), mlValue);
            	hasLocales = true;
            }
            if (XMLStreamConstants.START_ELEMENT != xmlr.getEventType()) {
            	xmlr.nextTag();
            }
        }
        
        QName propQName = QName.createQName(propName, namespaceService);
        if (hasLocales) {
            // Если получены локализированные значения поля, то записать их
        	properties.put(propQName, mlTextValue);
        } else if (value != null) {
        	// Если локализированные значения не получены, то записать нелокализированное содержимое поля
			value = value.trim();
			// проверяем на multy-value
			final int valueLength = value.length();
			if (valueLength > 1 && value.charAt(0) == '[' && value.charAt(valueLength - 1) == ']') {
				String valueToSplit = value.substring(1, valueLength - 1);
				String[] splittedValue = StringUtils.split(valueToSplit, ", ");
				if (splittedValue != null) {
					properties.put(propQName, new ArrayList<Serializable>(Arrays.asList(splittedValue)));
				}
			} else {
				properties.put(propQName, value);
			}
        }
    }
    
    private String getValue(XMLStreamReader xmlr) throws XMLStreamException {
    	String value = null;
        while (XMLStreamConstants.CHARACTERS == xmlr.getEventType()
                || XMLStreamConstants.CDATA == xmlr.getEventType()) {
            String str = xmlr.getText();
            if (value == null) {
                value = str;
            } else {
                value += str;
            }
            xmlr.next();
        }
        return value;
    }
    
    /**
     * Вспомогательный клас для запоминания ассоциаций, которые необходимо создать
     */
    protected class AssociationInfo {
        private NodeRef parentRef;
        private String assocType;
        private String assocPath;
        private String assocXPath;
        private UpdateMode updateMode;

        public AssociationInfo(NodeRef parentRef, String assocType, String assocPath, UpdateMode updateMode) {
            this.parentRef = parentRef;
            this.assocType = assocType;
            this.assocPath = assocPath;
            this.updateMode = updateMode;
        }

        public AssociationInfo(NodeRef parentRef, String assocType, String assocPath, String assocXPath, UpdateMode updateMode) {
            this.parentRef = parentRef;
            this.assocType = assocType;
            this.assocPath = assocPath;
            this.assocXPath = assocXPath;
            this.updateMode = updateMode;
        }

        public NodeRef getParentRef() {
            return parentRef;
        }

        public String getAssocType() {
            return assocType;
        }

        public String getAssocPath() {
            return assocPath;
        }

        public String getAssocXPath() {
            return assocXPath;
        }

        public UpdateMode getUpdateMode() {
            return updateMode;
        }
    }
}
