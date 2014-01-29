package ru.it.lecm.base.beans;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 11:44
 */
public class SubstitudeBeanImpl extends BaseBean implements SubstitudeBean {

    enum PseudoProps {
        AUTHOR {
            @Override
            public List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService) {
                List<NodeRef> result = new ArrayList<NodeRef>();
                NodeRef auth = docService.getDocumentAuthor(object);
                if (auth != null) {
                    result.add(auth);
                }
                return result;
            }

            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                NodeRef auth = docService.getDocumentAuthor(object);
                if (auth != null) {
                    return (String) services.getNodeService().getProperty(auth, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                }
                return "";
            }
        },
        REGNUM {
            @Override
            public List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService) {
                List<NodeRef> result = new ArrayList<NodeRef>();
                NodeRef number = docService.getDocumentRegData(object);
                if (number != null) { // пытаемся взять рег данные документа
                    result.add(number);
                } else {  // если их нет - берем рег данные проекта документа
                    number = docService.getDocumentProjectRegData(object);
                    if (number != null) {
                        result.add(number);
                    }
                }
                return result;
            }

            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                NodeRef number = docService.getDocumentRegData(object);
                if (number != null) { // пытаемся взять рег данные документа
                    return (String) services.getNodeService().getProperty(number, DocumentService.PROP_REG_DATA_NUMBER);
                } else {  // если их нет - берем рег данные проекта документа
                    number = docService.getDocumentProjectRegData(object);
                    if (number != null) {
                        return (String) services.getNodeService().getProperty(number, DocumentService.PROP_REG_DATA_NUMBER);
                    }
                }
                return DocumentService.DEFAULT_REG_NUM;
            }
        },
        REGDATE {
            @Override
            public List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService) {
                List<NodeRef> result = new ArrayList<NodeRef>();
                NodeRef number = docService.getDocumentRegData(object);
                if (number != null) { // пытаемся взять рег данные документа
                    result.add(number);
                } else {  // если их нет - берем рег данные проекта документа
                    number = docService.getDocumentProjectRegData(object);
                    if (number != null) {
                        result.add(number);
                    }
                }
                return result;
            }

            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                NodeRef number = docService.getDocumentRegData(object);
                if (number != null) { // пытаемся взять рег данные документа
                    return (String) services.getNodeService().getProperty(number, DocumentService.PROP_REG_DATA_DATE);
                } else {  // если их нет - берем рег данные проекта документа
                    number = docService.getDocumentProjectRegData(object);
                    if (number != null) {
                        return (String) services.getNodeService().getProperty(number, DocumentService.PROP_REG_DATA_DATE);
                    }
                }
                return "";
            }
        },
        PROJECT_REGNUM {
            @Override
            public List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService) {
                List<NodeRef> result = new ArrayList<NodeRef>();
                NodeRef number = docService.getDocumentProjectRegData(object);
                if (number != null) {
                    result.add(number);
                }
                return result;
            }

            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                NodeRef number = docService.getDocumentProjectRegData(object);
                if (number != null) {
                    return (String) services.getNodeService().getProperty(number, DocumentService.PROP_REG_DATA_NUMBER);
                }
                return DocumentService.DEFAULT_REG_NUM;
            }
        },
        DOC_REGNUM {
            @Override
            public List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService) {
                List<NodeRef> result = new ArrayList<NodeRef>();
                NodeRef number = docService.getDocumentRegData(object);
                if (number != null) {
                    result.add(number);
                }
                return result;
            }

            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                NodeRef number = docService.getDocumentRegData(object);
                if (number != null) {
                    return (String) services.getNodeService().getProperty(number, DocumentService.PROP_REG_DATA_NUMBER);
                }
                return DocumentService.DEFAULT_REG_NUM;
            }
        };

        public abstract List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService);
        public abstract String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services);

        public static PseudoProps findProp(String propName) {
            if (propName != null) {
                // поиск точного соот-вия
                for (PseudoProps v : values()) {
                    if (propName.equalsIgnoreCase(v.name())) {
                        return v;
                    }
                }
            }
            return null;
        }
    }

    public static final String DICTIONARY_TYPE_OBJECT_NAME = "Тип объекта";
    private ServiceRegistry serviceRegistry;
    private NamespaceService namespaceService;
    private DictionaryBean dictionaryService;
    private String dateFormat = "yyyy-MM-dd HH:mm";

    final private static Logger logger = LoggerFactory.getLogger(SubstitudeBeanImpl.class);
    private DocumentService documentService;

    /**
     * Получение заголовка элемента в соответствии с форматной строкой.
     * Выражения в форматной строке должны быть заключены в символы открытия (@see OPEN_SUBSTITUDE_SYMBOL) и закрытия (@see CLOSE_SUBSTITUDE_SYMBOL)
     *
     * @param node         элемент
     * @param formatString форматная строка
     * @return Заголовок элемента
     */
    @Override
    public String formatNodeTitle(final NodeRef node, final String formatString, final String dateFormat, final Integer timeZoneOffset) {
        final AuthenticationUtil.RunAsWork<String> substitudeString = new AuthenticationUtil.RunAsWork<String>() {
            @Override
            public String doWork() throws Exception {
                if (node == null) {
                    return "";
                }
                String dFormat = dateFormat;
                if (dFormat == null) {
                    dFormat = getDateFormat();
                }
                String result = formatString;
                List<String> nameParams = splitSubstitudeFieldsString(formatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
                for (String param : nameParams) {
                    result = result.replace(OPEN_SUBSTITUDE_SYMBOL + param + CLOSE_SUBSTITUDE_SYMBOL, getSubstitudeField(node, param, dFormat, timeZoneOffset).toString());
                }
                return result;
            }
        };
        return AuthenticationUtil.runAsSystem(substitudeString);
    }

    @Override
    public String formatNodeTitle(NodeRef node, String formatString) {
        return formatNodeTitle(node, formatString, null, null);
    }

    @Override
    public String getObjectDescription(NodeRef object) {
        // получаем шаблон описания
        String templateString = getTemplateStringForObject(object);
        // формируем описание
        return formatNodeTitle(object, templateString);
    }

    // в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public String getTemplateStringForObject(NodeRef object) {
        return getTemplateStringForObject(object, false);
    }

    @Override
    public String getTemplateStringForObject(NodeRef object, boolean forList) {
        return getTemplateStringForObject(object, forList, true);
    }

    @Override
    public String getTemplateStringForObject(NodeRef object, boolean forList, boolean returnDefaulIfNull) {
        NodeRef objectTypeRef = getObjectTypeRef(object);
        return getTemplateStringByType(objectTypeRef, forList, returnDefaulIfNull);
    }

    @Override
    public Object getNodeFieldByFormat(NodeRef node, String formatString) {
        return getNodeFieldByFormat(node, formatString, null, null);
    }

    @Override
    public Object getNodeFieldByFormat(final NodeRef node, final String formatString, final String dateFormat, final Integer timeZoneOffset) {
        final AuthenticationUtil.RunAsWork<Object> substitudeString = new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                if (node == null) {
                    return null;
                }
                String dFormat = dateFormat;
                if (dFormat == null) {
                    dFormat = getDateFormat();
                }
                String result = formatString;
                List<String> nameParams = splitSubstitudeFieldsString(formatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
                if (nameParams.size() > 1) { // сложная строка (не одно поле), значит у нас на выходе будет строка
                    for (String param : nameParams) {
                        result = result.replace(OPEN_SUBSTITUDE_SYMBOL + param + CLOSE_SUBSTITUDE_SYMBOL, getSubstitudeField(node, param, dFormat, timeZoneOffset).toString());
                    }
                } else if (nameParams.size() == 1) {
                    return getSubstitudeField(node, nameParams.get(0), dFormat, timeZoneOffset, true);
                }

                return result;
            }
        };
        return AuthenticationUtil.runAsSystem(substitudeString);
    }

    /**
     * Получение значения выражения для элемента.
     * Элементы в выражениях разделяются специальными символами (@see SPLIT_TRANSITIONS_SYMBOL)
     * Элементами выражения могут быть:
     * - Ссылка на родителя (@see SPLIT_TRANSITIONS_SYMBOL)
     * - Source ассоциация (..<Название ассоциации>)
     * - Target ассоциация (<Название ассоциации>)
     * -Child ассоциация (<Название ассоциации>)
     * Последним элементов выражения обязательно должен быть атрибут элемента
     * <p/>
     * Для ассоциаций можно указать условия.
     * Условия должно быть написано сразу после ассоциации, начиная с символа открытия (@see OPEN_EXPRESSIONS_SYMBOL) и заканчивая символом закрытия(@see OPEN_EXPRESSIONS_SYMBOL).
     * Условия должно содержать название атрибута и его значения, через знак равенства (@see EQUALS_SYMBOL).
     * Условий может быть несколько, в этом случае они должны разделяться специальным символом (@see SPLIT_EXPRESSION_SYMBOL).
     *
     * @param node  элемент
     * @param field выражение для элемента (ассоциации, условия и атрибуты)
     * @return {Object}
     */
    private Object getSubstitudeField(NodeRef node, String field, String dateFormat, Integer timeZoneOffset, boolean returnRealTypes) {
        NodeRef showNode = node;
        List<NodeRef> showNodes = new ArrayList<NodeRef>();
        String fieldName;
        String fieldFormat = null;
        List<String> transitions = new ArrayList<String>();

        Object result = "";

        boolean wrapAsLink = false;

        if (field.startsWith(WRAP_AS_LINK_SYMBOL)) {
            wrapAsLink = true;
            field = field.substring(1);
        }
        final boolean isPseudoProp = field.startsWith(PSEUDO_PROPERTY_SYMBOL);
        if (!isPseudoProp) {
            if (field.contains(SPLIT_TRANSITIONS_SYMBOL)) {
                int firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL);
                transitions.add(field.substring(0, firstIndex));
                int lastIndex = field.lastIndexOf(SPLIT_TRANSITIONS_SYMBOL);
                while (firstIndex != lastIndex) {
                    int oldFirstIndex = firstIndex;
                    firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL, firstIndex + 1);
                    transitions.add(field.substring(oldFirstIndex + 1, firstIndex));
                }
                fieldName = field.substring(lastIndex + 1, field.length());
            } else {
                fieldName = field;
            }

            if (fieldName.contains(STRING_FORMAT_SYMBOL)) {
                String fieldValue = fieldName;
                fieldName = fieldValue.substring(0, fieldValue.indexOf(STRING_FORMAT_SYMBOL));
                fieldFormat = fieldValue.substring(fieldValue.indexOf(STRING_FORMAT_SYMBOL) + 1, fieldValue.length());
            }

            for (String el : transitions) {
                showNodes.clear();
                Map<String, String> expressions = getExpression(el);
                if (!expressions.isEmpty()) {
                    el = el.substring(0, el.indexOf(OPEN_EXPRESSIONS_SYMBOL));
                }
                if (el.indexOf(PARENT_SYMBOL) == 0) {
                    String assocType = el.replace(PARENT_SYMBOL, "");
                    if (!assocType.isEmpty()) {
                        showNodes = findNodesByAssociationRef(showNode,
                                QName.createQName(assocType, namespaceService), null, ASSOCIATION_TYPE.SOURCE);
                        if (showNodes.isEmpty()) {
                            logger.debug("Не удалось получить список Source ассоциаций для [" + showNode.toString() + "]");
                            showNode = null;
                            break;
                        }
                    } else {
                        showNodes.add(nodeService.getPrimaryParent(showNode).getParentRef());
                    }
                } else {
                    List<NodeRef> temps = findNodesByAssociationRef(showNode, QName.createQName(el, namespaceService), null, ASSOCIATION_TYPE.TARGET);
                    if (temps.isEmpty()) {
                        List<ChildAssociationRef> childs = nodeService.getChildAssocs(showNode, QName.createQName(el, namespaceService), RegexQNamePattern.MATCH_ALL, false);
                        if (!childs.isEmpty()) {
                            for (ChildAssociationRef child : childs) {
                                showNodes.add(child.getChildRef());
                            }
                        } else {
                            logger.debug("Не удалось получить список Child ассоциаций для [" + showNode.toString() + "]");
                            showNode = null;
                            break;
                        }
                    } else {
                        showNodes.addAll(temps);
                    }
                }
                if (!expressions.isEmpty()) {
                    boolean exist = false;
                    for (NodeRef nodeRef : showNodes) {
                        if (!isArchive(nodeRef)) {
                            boolean expressionsFalse = false;
                            for (Map.Entry<String, String> entry : expressions.entrySet()) {
                                Object currentPropertyValue =
                                        nodeService.getProperty(nodeRef, QName.createQName(entry.getKey(), namespaceService));
                                if ((currentPropertyValue == null && !entry.getValue().toLowerCase().equals("null"))
                                        || !currentPropertyValue.toString().equals(entry.getValue())) {
                                    expressionsFalse = true;
                                    break;
                                }
                            }
                            if (!expressionsFalse) {
                                showNode = nodeRef;
                                exist = true;
                                break;
                            }
                        }
                    }
                    if (!exist) {
                        logger.debug(String.format("Не найдено подходящего результата для [%s] по условиям [%s]", showNode, expressions));
                        showNode = null;
                        break;
                    }
                } else if (!showNodes.isEmpty()) {
                    boolean exist = false;
                    for (NodeRef nodeRef : showNodes) {
                        if (!isArchive(nodeRef)) {
                            showNode = nodeRef;
                            exist = true;
                        }
                    }
                    if (!exist) {
                        logger.debug(String.format("Не найдено подходящего результата для [%s] по условиям [%s]", showNode, expressions));
                        showNode = null;
                        break;
                    }
                } else {
                    logger.debug(String.format("Не найдено подходящего результата для [%s] по условиям [%s]", showNode, expressions));
                    showNode = null;
                    break;
                }
            }
        } else {
            fieldName = field;
            field = field.substring(1);

            String fieldCode = "", defaultValue = "";

            if (field.contains("|")) {
                String[] values = field.split("\\|");
                fieldCode = values[0];
                defaultValue = values[1];
            }

            if (!fieldCode.isEmpty()) {
                field = fieldCode;
            }

            final String foundValue = getFormatStringByPseudoProp(showNode, field);
            if (foundValue != null) {
                result = foundValue;
            } else {
                if (defaultValue != null) {
                    result = defaultValue;
                }
            }
        }

        if (showNode != null) {
            if (!fieldName.contains("~")) {
                if (fieldName.equals("nodeRef")) {
                    result = returnRealTypes ? showNode : showNode.toString();
                } else {
                    Object property = nodeService.getProperty(showNode, QName.createQName(fieldName, namespaceService));
                    if (property != null) {
                        result = result.toString().isEmpty() ? property : result;
                        if (result instanceof Date) {
                            if (timeZoneOffset != null) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime((Date) result);
                                cal.add(Calendar.MILLISECOND, timeZoneOffset - TimeZone.getDefault().getRawOffset());
                                result = cal.getTime();
                            }

                            if (!returnRealTypes) {
                                DateFormat dFormat = new SimpleDateFormat(fieldFormat != null ? fieldFormat : dateFormat);
                                result = dFormat.format(result);
                            }
                        }
                    } else {
                        result = !returnRealTypes ? "" : null;
                    }
                }
            }
            if (!returnRealTypes) {
                if (wrapAsLink && !result.toString().isEmpty()) {
                    SysAdminParams params = serviceRegistry.getSysAdminParams();
                    String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
                    result = "<a href=\"" + serverUrl + LINK_URL + "?nodeRef=" + showNode.toString() + "\">"
                            + result + "</a>";
                }
            }
        }

        return result;
    }

    private Object getSubstitudeField(NodeRef node, String field, String dateFormat, Integer timeZoneOffset) {
        return getSubstitudeField(node, field, dateFormat, timeZoneOffset, false);
    }

    /**
     * Получение псевдо свойста или выполнение встроенной функции
     *
     *
     * @param object      исходный узел
     * @param psedudoProp мнемоника псевдо-свойства или функции (уже без всяких префиксных символов)
     * @return список узлов после выполнения функции (псевдо-свойства)
     */
    @Override
    public List<NodeRef> getObjectByPseudoProp(NodeRef object, final String psedudoProp) {
        PseudoProps pseudo = PseudoProps.findProp(psedudoProp);
        if (pseudo != null) {
            return pseudo.getObjectsByPseudoProp(object, documentService);
        }
        return null;
    }

    @Override
    public String getFormatStringByPseudoProp(NodeRef object, final String psedudoProp) {
        PseudoProps pseudo = PseudoProps.findProp(psedudoProp);
        if (pseudo != null) {
            return pseudo.getFormatStringByPseudoProp(object, documentService, serviceRegistry);
        }
        return null;
    }
    /**
     * Получение выражений из форматной строки
     *
     * @param str         форматная строка
     * @param openSymbol  символ открытия выражения
     * @param closeSymbol символ закрытия выражения
     * @return список строк с выражениями
     */
    public List<String> splitSubstitudeFieldsString(String str, String openSymbol, String closeSymbol) {
        List<String> results = new ArrayList<String>();
        if (str.contains(openSymbol) && str.contains(closeSymbol)) {
            int openIndex = str.indexOf(openSymbol);
            int closeIndex = str.indexOf(closeSymbol);
            results.add(str.substring(openIndex + 1, closeIndex));
            int lastOpenIndex = str.lastIndexOf(openSymbol);
            int lastCloseIndex = str.lastIndexOf(closeSymbol);
            while (openIndex != lastOpenIndex && closeIndex != lastCloseIndex) {
                openIndex = str.indexOf(openSymbol, openIndex + 1);
                closeIndex = str.indexOf(closeSymbol, closeIndex + 1);
                results.add(str.substring(openIndex + 1, closeIndex));
            }
        }
        return results;
    }

    /**
     * Получение условий
     *
     * @param str строка с условием
     * @return Map <field, value>
     */
    public Map<String, String> getExpression(String str) {
        Map<String, String> expressions = new HashMap<String, String>();
        int openIndex = str.indexOf(OPEN_EXPRESSIONS_SYMBOL);
        int closeIndex = str.indexOf(CLOSE_EXPRESSIONS_SYMBOL);
        if (openIndex > -1 && closeIndex > -1) {
            String expressionsStr = str.substring(openIndex + 1, closeIndex);
            if (!expressionsStr.isEmpty() && expressionsStr.contains(EQUALS_SYMBOL)) {
                int equalsIndex = -1;
                int endIndex = -1;
                int lastEqualsIndex = expressionsStr.lastIndexOf(EQUALS_SYMBOL);
                while (equalsIndex != lastEqualsIndex) {
                    int oldEndIndex = endIndex;
                    equalsIndex = expressionsStr.indexOf(EQUALS_SYMBOL, equalsIndex + 1);
                    endIndex = expressionsStr.indexOf(SPLIT_EXPRESSION_SYMBOL, endIndex + 1);
                    if (endIndex == -1) {
                        endIndex = expressionsStr.length();
                    }
                    expressions.put(expressionsStr.substring(oldEndIndex + 1, equalsIndex).trim(),
                            expressionsStr.substring(equalsIndex + 1, endIndex).trim());
                }
            }
        }
        return expressions;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void init() {
        namespaceService = getServiceRegistry().getNamespaceService();
        nodeService = getServiceRegistry().getNodeService();
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = !dateFormat.equals("${lecm.base.date.format}") ? dateFormat : this.dateFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Метод, возвращающий ссылку на объект справочника "Тип объекта" для заданного объекта
     *
     * @param nodeRef - ссылка на объект
     * @return ссылка на объект справочника или NULL
     */
    private NodeRef getObjectTypeRef(NodeRef nodeRef) {
        // получаем тип объекта
        QName type = nodeService.getType(nodeRef);
        String shortTypeName = type.toPrefixString(serviceRegistry.getNamespaceService());
        // получаем Тип Объекта
        return getObjectTypeByClass(shortTypeName);
    }

    /**
     * Метод, возвращающий ссылку на объект справочника "Тип объекта"по заданному классу(типу)
     *
     * @param type - тип(класс) объекта
     * @return ссылка на объект справочника или NULL
     */
    private NodeRef getObjectTypeByClass(String type) {
        return dictionaryService.getRecordByParamValue(DICTIONARY_TYPE_OBJECT_NAME, PROP_OBJ_TYPE_CLASS, type);
    }

    /**
     * Метод возвращающий шаблон описание по типу объхекта
     *
     * @param objectType - ссылка на тип объекта
     * @return сформированное описание или DEFAULT_OBJECT_TYPE_TEMPLATE, если для типа не задан шаблон
     */
    private String getTemplateStringByType(NodeRef objectType, boolean forList, boolean returnDefauktIfNull) {
        if (objectType != null) {
            Object template = nodeService.getProperty(objectType, forList ? PROP_OBJ_TYPE_LIST_TEMPLATE : PROP_OBJ_TYPE_TEMPLATE);
            return template != null ? (String) template : DEFAULT_OBJECT_TYPE_TEMPLATE;
        } else if (returnDefauktIfNull) {
            return forList ? DEFAULT_OBJECT_TYPE_LIST_TEMPLATE : DEFAULT_OBJECT_TYPE_TEMPLATE;
        }
        return null;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public List<NodeRef> getObjectsByTitle(NodeRef node, String formatString) {
        if (node == null) {
            return new ArrayList<NodeRef>();
        }
        List<NodeRef> result = new ArrayList<NodeRef>();
        List<String> nameParams = splitSubstitudeFieldsString(formatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
        for (String param : nameParams) {
            List<NodeRef> assocsRef = getAssocsByFormatString(node, param);
            if (assocsRef != null) {
                result.addAll(assocsRef);
            }
        }
        return result;
    }

    private List<NodeRef> getAssocsByFormatString(NodeRef node, String field) {

        List<NodeRef> showNodes = new ArrayList<NodeRef>();
        List<String> transitions = new ArrayList<String>();

        if (field.contains(SPLIT_TRANSITIONS_SYMBOL)) {
            int firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL);
            transitions.add(field.substring(0, firstIndex));
            int lastIndex = field.lastIndexOf(SPLIT_TRANSITIONS_SYMBOL);
            while (firstIndex != lastIndex) {
                int oldFirstIndex = firstIndex;
                firstIndex = field.indexOf(SPLIT_TRANSITIONS_SYMBOL, firstIndex + 1);
                transitions.add(field.substring(oldFirstIndex + 1, firstIndex));
            }
        }

        final List<NodeRef> nextTransionNodes = new ArrayList<NodeRef>();
        nextTransionNodes.add(node);

        for (String el : transitions) {
            Map<String, String> expressions = getExpression(el);
            if (!expressions.isEmpty()) {
                el = el.substring(0, el.indexOf(OPEN_EXPRESSIONS_SYMBOL));
            }

            Set<NodeRef> filteredResults = new HashSet<NodeRef>();

            for (NodeRef nextNode : nextTransionNodes) {
                if (el.indexOf(PARENT_SYMBOL) == 0) {
                    String assocType = el.replace(PARENT_SYMBOL, "");
                    if (!assocType.isEmpty()) {
                        showNodes = findNodesByAssociationRef(nextNode,
                                QName.createQName(assocType, namespaceService), null, ASSOCIATION_TYPE.SOURCE);
                        if (showNodes.isEmpty()) {
                            logger.debug("Не удалось получить список Source ассоциаций для [" + nextNode.toString() + "]");
                            break;
                        }
                    } else {
                        showNodes.add(nodeService.getPrimaryParent(nextNode).getParentRef());
                    }
                } else {
                    List<NodeRef> temps = findNodesByAssociationRef(nextNode, QName.createQName(el, namespaceService), null, ASSOCIATION_TYPE.TARGET);
                    if (temps.isEmpty()) {
                        List<ChildAssociationRef> childs = nodeService.getChildAssocs(nextNode, QName.createQName(el, namespaceService), RegexQNamePattern.MATCH_ALL, false);
                        for (ChildAssociationRef child : childs) {
                            showNodes.add(child.getChildRef());
                        }
                        if (showNodes.isEmpty()) {
                            logger.debug("Не удалось получить список Child ассоциаций для [" + nextNode.toString() + "]");
                            break;
                        }
                    } else {
                        showNodes.addAll(temps);
                    }
                }
                if (!expressions.isEmpty()) {
                    boolean exist = false;
                    for (NodeRef nodeRef : showNodes) {
                        boolean expressionsFalse = false;
                        for (Map.Entry<String, String> entry : expressions.entrySet()) {
                            Object currentPropertyValue =
                                    nodeService.getProperty(nodeRef, QName.createQName(entry.getKey(), namespaceService));
                            if ((currentPropertyValue == null && !entry.getValue().toLowerCase().equals("null"))
                                    || !currentPropertyValue.toString().equals(entry.getValue())) {
                                expressionsFalse = true;
                                break;
                            }
                        }
                        if (!isArchive(nodeRef) && !expressionsFalse) {
                            filteredResults.add(nodeRef);
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        logger.debug(String.format("Не найдено подходящего результата для [%s] по условиям [%s]", nextNode, expressions));
                        break;
                    }
                } else if (!showNodes.isEmpty()) {
                    for (NodeRef nodeRef : showNodes) {
                        if (!isArchive(nodeRef)) {
                            filteredResults.add(nodeRef);
                        }
                    }
                }
                showNodes.clear();
            }

            nextTransionNodes.clear();
            nextTransionNodes.addAll(filteredResults);
            filteredResults.clear();
        }
        return nextTransionNodes;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
