package ru.it.lecm.base.scripts;

import org.alfresco.repo.web.scripts.dictionary.DictionaryWebServiceBase;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.*;

/**
 * User: dbashmakov
 * Date: 26.04.2017
 * Time: 14:10
 */
public class GetRegisteredTypes extends DictionaryWebServiceBase {
    private final static Log logger = LogFactory.getLog(GetRegisteredTypes.class);

    private static final String PAR_CLASS_NAME = "classname";
    private static final String PAR_RECURSIVE = "recursive";
    private static final String PAR_WITH_PRIMITIVES = "withPrimitives";
    private static final String PAR_PRIMITIVES_FIRST = "primFirst";
    private static final String PAR_EXCLUDE_NAMESPACES = "exclNsp";

    private static final List<JSONObject> PRIMITIVES_LIST = new ArrayList<>();

    public final void init() {
        addPrimitiveValue("d:text");
        addPrimitiveValue("d:mltext");
        addPrimitiveValue("d:int");
        addPrimitiveValue("d:float");
        addPrimitiveValue("d:long");
        addPrimitiveValue("d:boolean");
        addPrimitiveValue("d:date");
        addPrimitiveValue("d:datetime");
        addPrimitiveValue("d:double");
        addPrimitiveValue("d:content");
        addPrimitiveValue("d:any");
        addPrimitiveValue("d:qname");
        addPrimitiveValue("d:noderef");
        addPrimitiveValue("d:category");
        addPrimitiveValue("d:locale");
    }

    private static String getLabel(MessageLookup messageLookup, String name) {
        String key = "lecm.primitive.type.label." + name;
        String message = messageLookup.getMessage(key, I18NUtil.getLocale());
        return message == null ? name : message;
    }

    private void addPrimitiveValue(String type) {
        try {
            JSONObject primitiveType = new JSONObject();
            primitiveType.put("name", getLabel(dictionaryservice, type.replace(":", "_")) + " - " + type);
            primitiveType.put("value", type);
            PRIMITIVES_LIST.add(primitiveType);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected final Comparator<JSONObject> JSON_BY_NAME_COMPARATOR = new Comparator<JSONObject>() {
        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            try {
                return o1.getString("name").toUpperCase().compareTo(o2.getString("name").toUpperCase());
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
            return 0;
        }
    };

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();
        final List<JSONObject> resultSet = new ArrayList<>();

        final List<JSONObject> typesSet = new ArrayList<>();

        String className = req.getServiceMatch().getTemplateVars().get(PAR_CLASS_NAME);
        String excludeNamespaces = req.getParameter(PAR_EXCLUDE_NAMESPACES);
        Set<String> excludedSet = new HashSet<>(Arrays.asList(excludeNamespaces != null ? excludeNamespaces.split(",") : new String[0]));
        boolean recursive = true;
        String recursiveValue = req.getParameter(PAR_RECURSIVE);
        if (recursiveValue != null) {
            recursive = Boolean.valueOf(recursiveValue);
        }
        boolean withPrimitives = true;
        String withPrimitivesValue = req.getParameter(PAR_WITH_PRIMITIVES);
        if (withPrimitivesValue != null) {
            withPrimitives = Boolean.valueOf(withPrimitivesValue);
        }
        boolean primitivesFirst = Boolean.valueOf(req.getParameter(PAR_PRIMITIVES_FIRST));
        //validate the className
        if (isValidClassname(className)) {
            QName classQName = QName.createQName(getFullNamespaceURI(className));
            if (isValidType(className)) {
                Collection<QName> subTypes = this.dictionaryservice.getSubTypes(classQName, recursive);
                for (QName subType : subTypes) {
                    try {
                        JSONObject type = new JSONObject();
                        String title = this.dictionaryservice.getClass(subType).getTitle(this.dictionaryservice);
                        String value = subType.toPrefixString(namespaceService);
                        if (excludedSet.isEmpty() || !excludedSet.contains(value.split(":")[0])) {
                            type.put("name", (title != null ? title : value) + " - " + value);
                            type.put("value", value);
                            typesSet.add(type);
                        }
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        } else {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Check the className - " + className + " parameter in the URL");
        }

        if (primitivesFirst) {
            if (withPrimitives) {
                Collections.sort(PRIMITIVES_LIST, JSON_BY_NAME_COMPARATOR);
                resultSet.addAll(PRIMITIVES_LIST);
            }
            Collections.sort(typesSet, JSON_BY_NAME_COMPARATOR);
            resultSet.addAll(typesSet);
        } else {
            if (withPrimitives) {
                resultSet.addAll(PRIMITIVES_LIST);
            }
            resultSet.addAll(typesSet);
            Collections.sort(resultSet, JSON_BY_NAME_COMPARATOR);
        }
        model.put("types", resultSet);
        return model;
    }

    private boolean isValidType(String classname) {
        try {
            QName qname = QName.createQName(this.getFullNamespaceURI(classname));
            return this.dictionaryservice.getClass(qname) != null;
        } catch (InvalidQNameException e) {
            // ignore
        }
        return false;
    }
}
