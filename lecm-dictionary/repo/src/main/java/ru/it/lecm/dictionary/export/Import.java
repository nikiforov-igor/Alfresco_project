package ru.it.lecm.dictionary.export;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * User: mShafeev
 * Date: 23.10.12
 * Time: 12:01
 */
public class Import extends AbstractWebScript {

    private static final String DICTIONARY_NAMESPACE_URI = "http://www.it.ru/lecm/dictionary/1.0";
    private static final QName DICTIONARY = QName.createQName(DICTIONARY_NAMESPACE_URI, "dictionary");
    private final static String DICTIONARIES_ROOT_NAME = "Dictionary";
	public static final String TAG_PROPERTY = "property";
	private String dictionaryType = "";

    NodeService nodeService;
    NamespaceService namespaceService;
    Repository repositoryHelper;


    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        JSONObject wf = new JSONObject();
        JSONArray compositions = new JSONArray();
        try {
            FormData formData = (FormData) req.parseContent();
            FormField[] fields = formData.getFields();

            String str;
            InputStream inputStream = fields[0].getInputStream();
            XMLStreamReader xmlr = inputFactory.createXMLStreamReader(inputStream);
            NodeRef parentNodeRef = null;

            while (true) {
	            str = getStartTag(xmlr);
	            if (str.equals("dictionary")) {
		            String dictionaryName = getAttributeValue(xmlr);
		            xmlr.nextTag();
		            xmlr.nextTag();
		            xmlr.nextTag();
		            xmlr.nextTag();
		            xmlr.nextTag();
		            Map<QName, Serializable> dicProps = getProperties(xmlr);
		            dictionaryType = dicProps.get(QName.createQName("lecm-dic:type", namespaceService)).toString();
		            //создание справочника
		            parentNodeRef = createDictionary(dictionaryName, dicProps);
		            str = getStartTag(xmlr);
	                if (str.equals("items")) {
		                xmlr.nextTag();
		                items(xmlr, parentNodeRef);
	                }
		            break;
	            } else {
	                xmlr.nextTag();
	            }
            }
            //Возможно необходимо выводить статистику по добавленым значениям
            wf.put("text", "Справочник успешно создан");
            compositions.put(wf);
            res.setContentEncoding("utf-8");
            res.getWriter().write(compositions.toString());

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void items(XMLStreamReader xmlr, NodeRef parent) throws XMLStreamException {
        String itemName;
	    Map<QName, Serializable> properties;
        try {
	        while (XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
			        && xmlr.getLocalName().equals("item")) {
		        itemName = getAttributeValue(xmlr);
		        xmlr.nextTag();
		        properties = getProperties(xmlr);
		        NodeRef current = createItem(parent, itemName, properties);
		        //Если нашли вложенный элемент
		        if (xmlr.getLocalName().equals("items")) {
			        if (!itemName.equals("")) {
				        xmlr.nextTag();
				        items(xmlr, current);
				        xmlr.nextTag();
				        xmlr.nextTag();
			        }
			        xmlr.nextTag();
		        }
		        xmlr.nextTag();
	        }
        } catch (XMLStreamException e) {
	        e.printStackTrace();
        }
    }

    private NodeRef createDictionary(String dictionaryName, Map<QName, Serializable> dicProps) {
        final NodeRef root = getDictionariesRoot();
        NodeRef dictionary = nodeService.getChildByName(root, ContentModel.ASSOC_CONTAINS, dictionaryName);
        if (dictionary == null) {
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
            properties.put(ContentModel.PROP_NAME, dictionaryName);
	        properties.putAll(dicProps);
            dictionary = nodeService.createNode(root, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(DICTIONARY_NAMESPACE_URI, dictionaryName),
                    DICTIONARY,
                    properties).getChildRef();
        }
        return dictionary;
    }

    private NodeRef createItem(NodeRef parentNodeRef, String name, Map<QName, Serializable> properties) {
	    properties.put(ContentModel.PROP_NAME, name);
	    return  nodeService.createNode(parentNodeRef, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                QName.createQName(dictionaryType, namespaceService),
                properties).getChildRef();
    }

    private NodeRef getDictionariesRoot() {
        repositoryHelper.init();
        final NodeRef companyHome = repositoryHelper.getCompanyHome();
        return nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
                DICTIONARIES_ROOT_NAME);
    }

    private String getStartTag(XMLStreamReader xmlr) {
        String string = "";
        switch (xmlr.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                string = xmlr.getLocalName();
                break;
        }
        return string;
    }

    private String getAttributeValue(XMLStreamReader xmlr) {
        String value = "";
        switch (xmlr.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                value = xmlr.getAttributeValue(0);
                break;
        }
        return value;
    }

    private Map<QName, Serializable> getProperties(XMLStreamReader xmlr) throws XMLStreamException {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        String value;
	    String propName;
	    while (XMLStreamConstants.START_ELEMENT == xmlr.getEventType()
			    && xmlr.getLocalName().equals(TAG_PROPERTY)) {
		    propName = xmlr.getAttributeValue("", "name");
		    xmlr.next();
		    if (XMLStreamConstants.CHARACTERS == xmlr.getEventType()) {
			    value = xmlr.getText();
			    properties.put(QName.createQName(propName, namespaceService), value);
			    xmlr.nextTag();
			    xmlr.nextTag();//пропускаем закрывающий тэг
		    }
	    }
	    return properties;
    }

}
