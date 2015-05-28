package ru.it.lecm.base.scripts;

import org.alfresco.service.cmr.dictionary.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 05.05.2015
 * Time: 11:23
 */
public class GetTypeFields extends DeclarativeWebScript {
    private final static Log logger = LogFactory.getLog(GetTypeFields.class);

    private final String STATEMACHINE_URI = "http://www.it.ru/logicECM/statemachine/1.0";
    private final QName PROP_STATUS = QName.createQName(STATEMACHINE_URI, "status");

    protected static final String PARAM_TYPE = "type";

    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();

        final List<JSONObject> fieldsSet = new ArrayList<>();
        model.put("fields", fieldsSet);

        String type = getParameter(req, PARAM_TYPE, null);

        if (type != null && type.length() > 0) {
            QName typeQName = QName.createQName(type, namespaceService);
            TypeDefinition typeDefinition = dictionaryService.getType(typeQName);

            if (typeDefinition != null) {
                Map<String, PropertyDefinition> properties = new HashMap<>();
                Map<String, AssociationDefinition> associations = new HashMap<>();

                Map<QName, PropertyDefinition> propsFromModel = typeDefinition.getProperties();
                for (QName qName : propsFromModel.keySet()) {
                    String propKey = qName.toPrefixString(namespaceService);
                    if (propKey.startsWith("lecm")) { // only LECM
                        properties.put(propKey, propsFromModel.get(qName));
                    }
                }
                Map<QName, AssociationDefinition> associationsFormModel = typeDefinition.getAssociations();
                for (QName qName : associationsFormModel.keySet()) {
                    String propKey = qName.toPrefixString(namespaceService);
                    if (propKey.startsWith("lecm")) { // only LECM
                        associations.put(propKey, associationsFormModel.get(qName));
                    }
                }

                List<AspectDefinition> defaultAspects = typeDefinition.getDefaultAspects(true);
                for (AspectDefinition defaultAspect : defaultAspects) {
                    Map<QName, PropertyDefinition> propsFromAspect = defaultAspect.getProperties();
                    for (QName qName : propsFromAspect.keySet()) {
                        String propKey = qName.toPrefixString(namespaceService);
                        if (propKey.startsWith("lecm")) { // only LECM
                            properties.put(propKey, propsFromAspect.get(qName));
                        }
                    }

                    Map<QName, AssociationDefinition> assocsFromAspect = defaultAspect.getAssociations();
                    for (QName qName : assocsFromAspect.keySet()) {
                        String propKey = qName.toPrefixString(namespaceService);
                        if (propKey.startsWith("lecm")) {  // only LECM
                            associations.put(propKey, assocsFromAspect.get(qName));
                        }
                    }
                }

                for (String propertyKey : properties.keySet()) {
                    JSONObject property = new JSONObject();
                    PropertyDefinition definition = properties.get(propertyKey);
                    if (!propertyKey.toLowerCase().endsWith("-ref") && !propertyKey.toLowerCase().endsWith("-text-content")) {
                        try {
                            String title = definition.getTitle(dictionaryService);
                            if (title != null) { // для реальный полей задан перевод и следовательно title
                                property.put("name", propertyKey);
                                property.put("type", definition.getDataType().getName().toPrefixString(namespaceService));
                                property.put("title", title);
                                fieldsSet.add(property);
                            }

                        } catch (JSONException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }

                // добавим поле Статус
                JSONObject statusProperty = new JSONObject();
                try {
                    PropertyDefinition definition = dictionaryService.getProperty(PROP_STATUS);
                    if (definition != null) {
                        String title = definition.getTitle(dictionaryService);
                        statusProperty.put("name", "lecm-statemachine:status");
                        statusProperty.put("type", "STATUS");
                        statusProperty.put("title", title);
                        fieldsSet.add(statusProperty);
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }

                for (String associationKey : associations.keySet()) {
                    JSONObject association = new JSONObject();
                    // проверим проперти -ref - если есть будем искать по нему
                    PropertyDefinition propertyDefinitionRef = properties.get(associationKey + "-ref");
                    if (propertyDefinitionRef != null) {
                        AssociationDefinition definition = associations.get(associationKey);
                        try {
                            String title = definition.getTitle(dictionaryService);
                            if (title != null) {
                                association.put("name", associationKey + "-ref");
                                association.put("type", definition.getTargetClass().getName().toPrefixString(namespaceService));
                                association.put("title", title);
                                fieldsSet.add(association);
                            }
                        } catch (JSONException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        Collections.sort(fieldsSet, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    return o1.getString("title").toUpperCase().compareTo(o2.getString("title").toUpperCase());
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
                return 0;
            }
        });
        return model;
    }


    private String getParameter(WebScriptRequest req, String name, String defaultValue) {
        String value = req.getParameter(name);
        if ((value == null || value.length() == 0) && defaultValue != null) {
            value = defaultValue;
        }

        if (logger.isDebugEnabled())
            logger.debug("Returning \"" + value + "\" from getParameter for \"" + name + "\"");

        return value;
    }
}
