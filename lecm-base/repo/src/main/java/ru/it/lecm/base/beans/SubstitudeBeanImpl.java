package ru.it.lecm.base.beans;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.getchildren.FilterPropLECM;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.expression.ExpressionNode;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 11:44
 */
public class SubstitudeBeanImpl extends BaseBean implements SubstitudeBean, ApplicationContextAware {
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
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                String number = docService.getDocumentActualNumber(object);
                if (number != null) { // пытаемся взять рег данные документа
                    return number;
                }
                return DocumentService.DEFAULT_REG_NUM;
            }
        },
        REGDATE {
            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                Date regDate = docService.getDocumentActualDate(object);
                if (regDate != null) {
                    DateFormat dFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    return dFormat.format(regDate);
                }
                return "";
            }


            @Override
            public Object getRealTypeValueByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                String dateValue = getFormatStringByPseudoProp(object, docService, services);
                if (!dateValue.isEmpty()) {
                    try {
                        return new SimpleDateFormat(Constants.DATE_FORMAT).parse(dateValue);
                    } catch (ParseException e) {
                        logger.debug(e.getMessage());
                    }
                }
                return null;
            }
        },
        REGISTRATOR {
            @Override
            public List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService) {
                NodeRef registratorRef = docService.getDocumentRegistrator(object);
                if (registratorRef != null) {
                    List<NodeRef> list = new ArrayList<NodeRef>();
                    list.add(registratorRef);
                    return list;
                }
                return null;
            }

            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                NodeRef registratorRef = docService.getDocumentRegistrator(object);
                if (registratorRef != null) {
                    return (String) services.getNodeService().getProperty(registratorRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
                }
                return "";
            }
        },
        PROJECT_REGDATE {
            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                Date regDate = docService.getProjectRegDate(object);
                if (regDate != null) {
                    DateFormat dFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    return dFormat.format(regDate);
                }

                return "";
            }

            @Override
            public Object getRealTypeValueByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                String dateValue = getFormatStringByPseudoProp(object, docService, services);
                if (!dateValue.isEmpty()) {
                    try {
                        return new SimpleDateFormat(Constants.DATE_FORMAT).parse(dateValue);
                    } catch (ParseException e) {
                        logger.debug(e.getMessage());
                    }
                }
                return null;
            }
        },

        DOC_REGDATE {
            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                Date regDate = docService.getDocumentRegDate(object);
                if (regDate != null) {
                    DateFormat dFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    return dFormat.format(regDate);
                }

                return "";
            }

            @Override
            public Object getRealTypeValueByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                String dateValue = getFormatStringByPseudoProp(object, docService, services);
                if (!dateValue.isEmpty()) {
                    try {
                        return new SimpleDateFormat(Constants.DATE_FORMAT).parse(dateValue);
                    } catch (ParseException e) {
                        logger.debug(e.getMessage());
                    }
                }
                return null;
            }
        },
        PROJECT_REGNUM {
            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                String number = docService.getProjectRegNumber(object);
                if (number != null) {
                    return number;
                }
                return DocumentService.DEFAULT_REG_NUM;
            }
        },
        DOC_REGNUM {
            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                String number = docService.getDocumentRegNumber(object);
                if (number != null) {
                    return number ;
                }
                return DocumentService.DEFAULT_REG_NUM;
            }
        },
        SHARE_CONTEXT {
            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                return services.getSysAdminParams().getShareContext();
            }
        },
        EMPTY {
            @Override
            public String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
                return "";
            }
        };

        public List<NodeRef> getObjectsByPseudoProp(NodeRef object, DocumentService docService) {
            return null;  //значение строковое, ноды нет
        }

        public abstract String getFormatStringByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services);

        public Object getRealTypeValueByPseudoProp(NodeRef object, DocumentService docService, ServiceRegistry services) {
            return getFormatStringByPseudoProp(object, docService, services);
        }

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

        private static class Constants {
            public static final String DATE_FORMAT = "dd.MM.yyyy";
        }
    }

    public static final String DICTIONARY_TYPE_OBJECT_NAME = "Тип объекта";
    private NamespaceService namespaceService;
    private DictionaryBean dictionaryService;
    private DictionaryService dictionary;
    private String dateFormat = "yyyy-MM-dd HH:mm";

    final private static Logger logger = LoggerFactory.getLogger(SubstitudeBeanImpl.class);
    private DocumentService documentService;

    private SimpleCache<String, NodeRef> objTypeCache;
    private SimpleCache<NodeRef, String> typeTemplateCache;
    private SimpleCache<NodeRef, String> typeListTemplateCache;

    private ApplicationContext applicationContext;
    private LecmPermissionService lecmPermissionService;

    public void setObjTypeCache(SimpleCache<String, NodeRef> objTypeCache) {
        this.objTypeCache = objTypeCache;
    }

    public void setTypeTemplateCache(SimpleCache<NodeRef, String> typeTemplateCache) {
        this.typeTemplateCache = typeTemplateCache;
    }

    public void setDictionary(DictionaryService dictionary) {
        this.dictionary = dictionary;
    }

    public void setTypeListTemplateCache(SimpleCache<NodeRef, String> typeListTemplateCache) {
        this.typeListTemplateCache = typeListTemplateCache;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

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
                if (node == null || formatString == null) {
                    return "";
                }
                String replacedFormatString = formatString.replaceAll("&#044;", ",");
                String dFormat = dateFormat;
                if (dFormat == null) {
                    dFormat = getDateFormat();
                }
                String result = replacedFormatString;
                List<String> nameParams = splitSubstitudeFieldsString(replacedFormatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
                for (String param : nameParams) {
                    if (param.startsWith("@")) {
                        String expression = param.substring(1);
                        result = result.replace(OPEN_SUBSTITUDE_SYMBOL + param + CLOSE_SUBSTITUDE_SYMBOL, documentService.execStringExpression(node, expression, false));
                    } else {
                        result = result.replace(OPEN_SUBSTITUDE_SYMBOL + param + CLOSE_SUBSTITUDE_SYMBOL, getSubstitudeField(node, param, dFormat, timeZoneOffset).toString());
                    }
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
    public Object getNodeFieldByFormat(NodeRef node, String formatString, boolean returnLastAssoc) {
        return getNodeFieldByFormat(node, formatString, null, null, returnLastAssoc);
    }

    @Override
    public Object getNodeFieldByFormat(final NodeRef node, final String formatString, final String dateFormat, final Integer timeZoneOffset, final boolean returnLastAssoc) {
        final AuthenticationUtil.RunAsWork<Object> substitudeString = new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                if (node == null || formatString == null) {
                    return null;
                }
                String replacedFormatString = formatString.replaceAll("&#044;", ",");
                if (isExpressionSyntax(replacedFormatString)) {
                    return executeExpression(node, replacedFormatString);
                }

                String dFormat = dateFormat;
                if (dFormat == null) {
                    dFormat = getDateFormat();
                }
                String result = replacedFormatString;

                List<String> nameParams = splitSubstitudeFieldsString(replacedFormatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
                //проверка на сложную строку (не одно поле) - определяем, что будет на выходе
                Boolean objectExpected = nameParams.size() == 1 && result.replace(OPEN_SUBSTITUDE_SYMBOL + nameParams.get(0) + CLOSE_SUBSTITUDE_SYMBOL, "").isEmpty();

                if (objectExpected) { //возвращаем объект
                    String expression = nameParams.get(0);
                    boolean isAssocField = expression.endsWith(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL);
                    if (isAssocField) {// список нод по ассоциации
                        return getAssocsByFormatString(node, expression);
                    } else {
                        return getSubstitudeField(node, expression, dFormat, timeZoneOffset, true);
                    }
                } else { //возвращаем строку
                    for (String param : nameParams) {
                        if (param.startsWith("@")) {
                            String expression = param.substring(1);
                            result = result.replace(OPEN_SUBSTITUDE_SYMBOL + param + CLOSE_SUBSTITUDE_SYMBOL, documentService.execStringExpression(node, expression, false));
                        } else {
                            Object transValue;
                            boolean isAssocField = param.endsWith(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL);
                            if (isAssocField) { // у нас ассоциация - соберем все значения вместе
                                transValue = formatAssocString(node, param);
                            } else {
                                transValue = getSubstitudeField(node, param, dFormat, timeZoneOffset).toString();
                            }
                            result = result.replace(OPEN_SUBSTITUDE_SYMBOL + param + CLOSE_SUBSTITUDE_SYMBOL, transValue.toString());
                        }
                    }
                }
                return result;
            }
        };
        return AuthenticationUtil.runAsSystem(substitudeString);
    }

    @Override
    public Object getNodeFieldByFormat(final NodeRef node, final String formatString, final String dateFormat, final Integer timeZoneOffset) {
        return getNodeFieldByFormat(node, formatString, dateFormat, timeZoneOffset, true);
    }

    private String formatAssocString(NodeRef node, String formatStr) {
        StringBuilder sb = new StringBuilder();
        List<NodeRef> assocsList = getAssocsByFormatString(node, formatStr);
        for (NodeRef nodeRef : assocsList) {
            sb.append(getObjectDescription(nodeRef));
            sb.append(SubstitudeBean.ASSOC_DELIMITER);
        }
        return sb.length() > 2 ? sb.toString().substring(0, sb.length() - 2) : sb.toString();
    }

    private boolean isExpressionSyntax(String fmt) {
        return (fmt != null) && fmt.startsWith(OPEN_SUBSTITUDE_SYMBOL + "@");
    }

    /**
     * Функция расширенной обработки. Выражение вида {@текст_выражения} исполняется и возвращается результат TRUE/FALSE
     */
    private Boolean executeExpression(final NodeRef node, String expression) {
        if ("".equals(expression)) {
            expression = "true";
        } else {
            //удаляем лишнее
            expression = expression.substring("{@".length(), expression.length() - 1);
        }
        try {
            StandardEvaluationContext context = new StandardEvaluationContext(new ExpressionNode(node));
            context.setBeanResolver(new BeanFactoryResolver(this.applicationContext));

            Boolean result = new SpelExpressionParser().parseExpression(expression).getValue(context, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            logger.error("Expression: " + expression + " has errors", e);
            return false;
        }
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
    private Object getSubstitudeField(NodeRef node, String field, String dateFormat, Integer timeZoneOffset, boolean returnRealTypes, boolean returnLastAssoc) {
        NodeRef showNode = node;
        List<NodeRef> showNodes = new ArrayList<>();
        String fieldName;
        String defaultValue = null;
        String fieldFormat = null;
        List<String> transitions = new ArrayList<>();

        Object result = "";

        boolean wrapAsLink = false;

        if (field.startsWith(WRAP_AS_LINK_SYMBOL)) {
            wrapAsLink = true;
            field = field.substring(1);
        }
        final boolean isPseudoProp = field.startsWith(PSEUDO_PROPERTY_SYMBOL);
        if (!isPseudoProp) { //если не псевдосвойств - идем по обычному пути
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

            if (fieldName.contains("|")) {  //проверяем есть ли значение по дефолту (условие проверки - не null в итоговом поле
                String[] values = fieldName.split("\\|");
                fieldName = values[0];
                defaultValue = values.length > 1 ? values[1] : null;
            }

            if (fieldName.contains(STRING_FORMAT_SYMBOL)) { //формат (для дат)
                String fieldValue = fieldName;
                fieldName = fieldValue.substring(0, fieldValue.indexOf(STRING_FORMAT_SYMBOL));
                fieldFormat = fieldValue.substring(fieldValue.indexOf(STRING_FORMAT_SYMBOL) + 1, fieldValue.length());
            }

            //проходим по всем переходам - на выходе имеем 1(!!!) результирующую ноду
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
                    boolean lastAssoc = false;
                    for (NodeRef nodeRef : showNodes) {
                        if (!isArchive(nodeRef)) {
                            boolean expressionsFalse = false;
                            for (Map.Entry<String, String> entry : expressions.entrySet()) {
                                if (!LAST_ASSOC_EXPR.equals(entry.getKey())) {
                                    Object currentPropertyValue =
                                            nodeService.getProperty(nodeRef, QName.createQName(entry.getKey(), namespaceService));
                                    if ((currentPropertyValue == null && !entry.getValue().toLowerCase().equals("null"))
                                            || !currentPropertyValue.toString().equals(entry.getValue())) {
                                        expressionsFalse = true;
                                        break;
                                    }
                                } else {
                                    lastAssoc = Boolean.valueOf(entry.getValue());
                                }
                            }
                            if (!expressionsFalse) {
                                showNode = nodeRef;
                                exist = true;
                                if (!lastAssoc) {
                                    break; // возвращаем первую подходящую, если флаг не говорит об обратном
                                }
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
                            if (!returnLastAssoc) {
                                logger.debug(String.format("Возвращаем первый найденный результат для [%s] по условиям [%s]", showNode, expressions));
                                break;
                            }
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
        } else { //для псевдосвойство - высчитывем его
            fieldName = field.substring(1);
            if (fieldName.contains("|")) {
                String[] values = fieldName.split("\\|");
                fieldName = values[0];
                defaultValue = values.length > 1 ? values[1] : null;
            }

            logger.debug(String.format("Вычисляем значение псевдосвойства [%s] для [%s]", fieldName, showNode));

            if (returnRealTypes) {
                result = getRealValueByPseudoProp(showNode, fieldName);
            } else {
                result = getFormatStringByPseudoProp(showNode, fieldName);
            }
        }

        result = getResultedValue(showNode, fieldName, defaultValue, returnRealTypes, result, timeZoneOffset);

        result = postProcessValue(result, showNode, returnRealTypes, fieldFormat, timeZoneOffset, dateFormat, wrapAsLink);
        return result;
    }

    private Object postProcessValue(Object result, NodeRef showNode, boolean returnRealTypes, String fieldFormat, Integer timeZoneOffset, String dateFormat, boolean wrapAsLink) {
        if (result != null) {//форматируем даты, если нашли значение
            if (result instanceof Date) {
                if (timeZoneOffset != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime((Date) result);
                    cal.add(Calendar.MILLISECOND, timeZoneOffset - TimeZone.getDefault().getRawOffset());
                    result = cal.getTime();
                }

                if (!returnRealTypes || fieldFormat != null) {
                    DateFormat dFormat = new SimpleDateFormat(fieldFormat != null ? fieldFormat : dateFormat);
                    result = dFormat.format(result);
                }
            } else if ("type_label".equals(fieldFormat)) {
                result = getTypeLabel(result.toString());
            }
        }

        if (showNode != null && result != null && !returnRealTypes) {//если возвращаем строку и надо обернуть как ссылку
            if (wrapAsLink && !result.toString().isEmpty()) {
                result = getUrlService().wrapperLink(showNode.toString(), result.toString());
            }
        }

        return result;
    }

    private Object getSubstitudeField(NodeRef node, String field, String dateFormat, Integer timeZoneOffset, boolean returnRealTypes) {
        return getSubstitudeField(node, field, dateFormat, timeZoneOffset, returnRealTypes, true);
    }

    private Object getResultedValue(NodeRef showNode, String fieldName, String defaultValue, boolean returnRealTypes, Object result, Integer timeZoneOffset) {
        if (showNode != null) { //если нода найдена или не были заданы переходы, или у нас псевдосвойство - вычисляем итоговое значение
            if (fieldName.equals("nodeRef")) {
                result = !returnRealTypes ? showNode.toString() : showNode;
            } else {
                if (result == null || result.toString().isEmpty()) {
                    //если у нас не псевдо или для псевдо не нашли значение
                    if (!fieldName.isEmpty()) {
                        //либо реальное свойство, либо дефолтное значение
                        Object property = nodeService.getProperty(showNode, QName.createQName(fieldName, namespaceService));
                        if (property != null) {
                            //если у нас свойство и мы получили значение - применяем возможный констрейнт
                            List<ConstraintDefinition> constraintDefinitionList = dictionary.getProperty(QName.createQName(fieldName, namespaceService)).getConstraints();
                            //ищем привязанный LIST_CONSTRAINT
                            if (constraintDefinitionList != null) {
                                for (ConstraintDefinition constraintDefinition : constraintDefinitionList) {
                                    Constraint constraint = constraintDefinition.getConstraint();
                                    if (constraint instanceof ListOfValuesConstraint) {
                                        //получаем локализованное значение для LIST_CONSTRAINT
                                        String constraintProperty = ((ListOfValuesConstraint) constraint).getDisplayLabel(property.toString(), dictionary);
                                        if (constraintProperty != null) {
                                            property = constraintProperty;
                                        }
                                    }
                                }
                            }
                            result = property;
                        } else {
                            if (defaultValue != null) {
                                //идём вглубь форматной строки
                                fieldName = defaultValue;
                                result = getSubstitudeField(showNode, fieldName, dateFormat, timeZoneOffset, returnRealTypes);
                            } else {
                                result = getSubstituteDefaultValue(null, returnRealTypes);
                            }
                        }
                    } else {
                        //не задано конечное свойство - берем либо описание, либо саму ноду
                        result = !returnRealTypes ? getObjectDescription(showNode) : showNode;
                    }
                }
            }
        } else {
            //поле не заполнено - возвращаем либо дефолтное (если есть), либо пустую строку/null
            result = getSubstituteDefaultValue(defaultValue, returnRealTypes);
        }
        return result;
    }

    private Object getSubstituteDefaultValue(Object defaultValue, boolean returnRealTypes) {
        return defaultValue != null ? defaultValue : (returnRealTypes ? null : "");
    }

    private Object getSubstitudeField(NodeRef node, String field, String dateFormat, Integer timeZoneOffset) {
        return getSubstitudeField(node, field, dateFormat, timeZoneOffset, false);
    }

    /**
     * Получение псевдо свойста или выполнение встроенной функции
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

    @Override
    public Object getRealValueByPseudoProp(NodeRef object, final String psedudoProp) {
        PseudoProps pseudo = PseudoProps.findProp(psedudoProp);
        if (pseudo != null) {
            return pseudo.getRealTypeValueByPseudoProp(object, documentService, serviceRegistry);
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

    private List<FilterPropLECM> getFilterExpression(String str) {
        List<FilterPropLECM> expressions = new ArrayList<>();
        if (str != null) {
            int openIndex = str.indexOf(OPEN_EXPRESSIONS_SYMBOL);
            int closeIndex = str.indexOf(CLOSE_EXPRESSIONS_SYMBOL);
            if (openIndex > -1 && closeIndex > -1) {
                String expressionsStr = str.substring(openIndex + 1, closeIndex);
                if (!expressionsStr.isEmpty()) {
                    String[] expressionsArray = expressionsStr.trim().split(SPLIT_EXPRESSION_SYMBOL);
                    for (String expr : expressionsArray) {
                        if (!expr.isEmpty()){
                            int equalsIndex = expr.indexOf(EQUALS_SYMBOL);
                            if (equalsIndex > 0) {
                                boolean isNotCase = expr.charAt(equalsIndex - 1) == '!';

                                String firstTerm = expr.substring(0, !isNotCase ? equalsIndex: equalsIndex - 1).trim();
                                String secondTerm = expr.substring(equalsIndex + 1).trim();
                                try {
                                    QName propName = !firstTerm.isEmpty() ? QName.createQName(firstTerm, namespaceService) : null;
                                    FilterPropLECM filter =
                                            new FilterPropLECM(propName, secondTerm,
                                                    !isNotCase ? FilterPropLECM.FilterTypeLECM.EQUALS : FilterPropLECM.FilterTypeLECM.NOT_EQUALS, Boolean.FALSE);
                                    expressions.add(filter);
                                } catch (NamespaceException ex) {
                                    logger.error(ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        return expressions;
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
        String shortTypeName = type.toPrefixString(namespaceService);
        // получаем Тип Объекта
        return getObjectTypeByClass(type,shortTypeName);
    }

    /**
     * Метод, возвращающий ссылку на объект справочника "Тип объекта"по заданному классу(типу)
     *
     * @param type - тип(класс) объекта
     * @return ссылка на объект справочника или NULL
     */
    private NodeRef getObjectTypeByClass(QName qnameType, String type) {
        // получаем Тип Объекта
        NodeRef typeRef = objTypeCache.get(type);
        if (typeRef == null) {
            typeRef = dictionaryService.getRecordByParamValue(DICTIONARY_TYPE_OBJECT_NAME, PROP_OBJ_TYPE_CLASS, type);
            if (typeRef == null) {
                boolean isDocument = dictionary.isSubClass(qnameType, DocumentService.TYPE_BASE_DOCUMENT);
                if (isDocument && !qnameType.equals(DocumentService.TYPE_BASE_DOCUMENT)) {
                    typeRef = getObjectTypeByClass(DocumentService.TYPE_BASE_DOCUMENT, DocumentService.TYPE_BASE_DOCUMENT.toPrefixString(namespaceService));
                }
            }
            objTypeCache.put(type, typeRef);
        }
        return typeRef;
    }

    /**
     * Метод возвращающий шаблон описание по типу объхекта
     *
     * @param objectType - ссылка на тип объекта
     * @return сформированное описание или DEFAULT_OBJECT_TYPE_TEMPLATE, если для типа не задан шаблон
     */
    private String getTemplateStringByType(NodeRef objectType, boolean forList, boolean returnDefaultIfNull) {
        String template;
        if (objectType != null) {
            template = forList ? typeListTemplateCache.get(objectType) : typeTemplateCache.get(objectType);
            if (template == null) {
                Object templateObj = nodeService.getProperty(objectType, forList ? PROP_OBJ_TYPE_LIST_TEMPLATE : PROP_OBJ_TYPE_TEMPLATE);
                if (templateObj != null) {
                    if (forList) {
                        typeListTemplateCache.put(objectType, templateObj.toString());
                    } else {
                        typeTemplateCache.put(objectType, templateObj.toString());
                    }
                }
                template = templateObj != null ? (String) templateObj : DEFAULT_OBJECT_TYPE_TEMPLATE;
            }
            return template;
        } else if (returnDefaultIfNull) {
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
            List<FilterPropLECM> expressions = getFilterExpression(el);
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
                            continue;
                        }
                    } else {
                        showNodes.add(nodeService.getPrimaryParent(nextNode).getParentRef());
                    }
                } else {
                    List<NodeRef> temps = findNodesByAssociationRef(nextNode, QName.createQName(el, namespaceService), null, ASSOCIATION_TYPE.TARGET);
                    if (temps.isEmpty()) {
                        List<ChildAssociationRef> childs = nodeService.getChildAssocs(nextNode, QName.createQName(el, namespaceService), RegexQNamePattern.MATCH_ALL, false);
                        if (!childs.isEmpty()) {
                            for (ChildAssociationRef child : childs) {
                                showNodes.add(child.getChildRef());
                            }
                        } else {
                            logger.debug("Не удалось получить список Child ассоциаций для [" + nextNode.toString() + "]");
                            continue;
                        }
                    } else {
                        showNodes.addAll(temps);
                    }
                }
                if (!expressions.isEmpty()) {
                    boolean exist = false;
                    for (NodeRef nodeRef : showNodes) {
                        boolean expressionsFalse = false;
                        for (FilterProp filterProp : expressions) {
                            Serializable propVal = nodeService.getProperty(nodeRef, filterProp.getPropName());
                            if (propVal != null) {
                                if (propVal instanceof Date) {
                                    DateFormat dFormat = new SimpleDateFormat(dateFormat);
                                    propVal = dFormat.format(propVal);
                                } else {
                                    propVal = propVal.toString();
                                }
                                Serializable filter = filterProp.getPropVal();
                                switch ((FilterPropLECM.FilterTypeLECM) filterProp.getFilterType()) {
                                    case EQUALS:
                                        if (!propVal.equals(filter)) {
                                            expressionsFalse = true;
                                        }
                                        break;
                                    case NOT_EQUALS:
                                        if (propVal.equals(filter)) {
                                            expressionsFalse = true;
                                        }
                                        break;
                                    default:
                                }
                            } else {
                                if (!filterProp.getPropVal().toString().toLowerCase().equals("null")) {
                                    expressionsFalse = true;
                                }
                            }
                            if (expressionsFalse) {
                                break;
                            }
                        }

                        if (!isArchive(nodeRef) && !expressionsFalse) {
                            filteredResults.add(nodeRef);
                            exist = true;
                        }
                    }
                    if (!exist) {
                        logger.debug(String.format("Не найдено подходящего результата для [%s] по условиям [%s]", nextNode, expressions));
                        break;
                    }
                } else if (!showNodes.isEmpty()) {
                    for (NodeRef nodeRef : showNodes) {
                        if (lecmPermissionService.hasReadAccess(nodeRef) && !isArchive(nodeRef)) {
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

    public String getTypeLabel(String type) {
        QName typeQName = QName.createQName(type, namespaceService);
        TypeDefinition definition = dictionary.getType(typeQName);
        String label = null;
        if (definition != null) {
            String key = definition.getModel().getName().toPrefixString(namespaceService);
            key += ".type." + type + ".title";
            key = StringUtils.replace(key, ":", "_");
            label = I18NUtil.getMessage(key, I18NUtil.getLocale());
        }
        return label != null ? label : type;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
