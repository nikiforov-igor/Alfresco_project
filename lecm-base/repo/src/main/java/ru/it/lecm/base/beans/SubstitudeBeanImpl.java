package ru.it.lecm.base.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.dictionary.beans.DictionaryBean;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 11:44
 */
public class SubstitudeBeanImpl extends BaseBean implements SubstitudeBean {

    public static final String DICTIONARY_TYPE_OBJECT_NAME = "Тип объекта";
    private ServiceRegistry serviceRegistry;
	private NamespaceService namespaceService;
    private DictionaryBean dictionaryService;
    private String dateFormat = "yyyy-MM-dd HH:mm";

	final private static Logger logger = LoggerFactory.getLogger(SubstitudeBeanImpl.class);

	/**
	 * Получение заголовка элемента в соответствии с форматной строкой.
	 * Выражения в форматной строке должны быть заключены в символы открытия (@see OPEN_SUBSTITUDE_SYMBOL) и закрытия (@see CLOSE_SUBSTITUDE_SYMBOL)
	 *
	 * @param node         элемент
	 * @param formatString форматная строка
	 * @return Заголовок элемента
	 */
	@Override
	public String formatNodeTitle(NodeRef node, String formatString) {
		if (node == null) {
			return "";
		}
		String result = formatString;
		List<String> nameParams = splitSubstitudeFieldsString(formatString, OPEN_SUBSTITUDE_SYMBOL, CLOSE_SUBSTITUDE_SYMBOL);
		for (String param : nameParams) {
			result = result.replace(OPEN_SUBSTITUDE_SYMBOL + param + CLOSE_SUBSTITUDE_SYMBOL, getSubstitudeField(node, param).toString());
		}
		return result;
	}

    @Override
    public String getObjectDescription(NodeRef object) {
        // получаем шаблон описания
        String templateString = getTemplateStringForObject(object);
        // формируем описание
        return formatNodeTitle(object, templateString);
    }

    public String getTemplateStringForObject(NodeRef object) {
        return getTemplateStringForObject(object, false);
    }

    public String getTemplateStringForObject(NodeRef object, boolean forList) {
        NodeRef objectTypeRef = getObjectTypeRef(object);
        return getTemplateStringByType(objectTypeRef, forList);
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
	 *
	 * @param node  элемент
	 * @param field выражение для элемента (ассоциации, условия и атрибуты)
	 * @return {Object}
	 */
	public Object getSubstitudeField(NodeRef node, String field) {
		NodeRef showNode = node;
		List<NodeRef> showNodes = new ArrayList<NodeRef>();
		String fieldName = null;
		List<String> transitions = new ArrayList<String>();

        Object result = "";

        boolean wrapAsLink = false;

        if (field.startsWith(WRAP_AS_LINK_SYMBOL)) {
            wrapAsLink = true;
            field = field.substring(1);
        }
        if(!field.startsWith(PSEUDO_PROPERTY_SYMBOL)) {
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
            for (String el : transitions) {
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
                        for (ChildAssociationRef child : childs) {
                            showNodes.add(child.getChildRef());
                        }
                        if (showNodes.isEmpty()) {
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
                            showNode = nodeRef;
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        logger.debug(String.format("Не найдено подходящего результата для [%s] по условиям [%s]", showNode, expressions));
                        showNode = null;
                        break;
                    }
                } else if (!showNodes.isEmpty()) {
                    for (NodeRef nodeRef : showNodes) {
                        if (!isArchive(nodeRef)) {
                            showNode = nodeRef;
                        }
                    }
                }
            }
        } else {
            fieldName = field;
            field = field.substring(1);
            if (field.toUpperCase().equals(AUTHOR)) {
                showNode = getDocumentAuthor(showNode);
                result = getObjectDescription(showNode);
            }
        }

		if (showNode != null) {
            if (!fieldName.contains("~")) {
                Object property = nodeService.getProperty(showNode, QName.createQName(fieldName, namespaceService));
                if (property != null) {
                    result = result.toString().isEmpty() ? property : result;
                    if (result instanceof Date) {
                        DateFormat dFormat = new SimpleDateFormat(dateFormat);
                        result = dFormat.format(result);
                    }
                } else {
                    result = "";
                }
            }
            if (wrapAsLink && !result.toString().isEmpty()) {
                SysAdminParams params = serviceRegistry.getSysAdminParams();
                String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
                result = "<a href=\"" + serverUrl + LINK_URL + "?nodeRef=" + showNode.toString() + "\">"
                        + result + "</a>";
            }
		}

		return result;
	}

    private NodeRef getDocumentAuthor(NodeRef document) {
        Object creator = nodeService.getProperty(document, ContentModel.PROP_CREATOR);
        NodeRef person = serviceRegistry.getPersonService().getPerson(creator.toString(), false);
        if (person != null) {
            List<AssociationRef> lRefs = nodeService.getSourceAssocs(person, ASSOC_EMPLOYEE_PERSON);
            for (AssociationRef lRef : lRefs) {
                if (!isArchive(lRef.getSourceRef())) {
                    return lRef.getSourceRef();
                }
            }
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
     * @param objectType - ссылка на тип объекта
     * @return сформированное описание или DEFAULT_OBJECT_TYPE_TEMPLATE, если для типа не задан шаблон
     */
    private String getTemplateStringByType(NodeRef objectType, boolean forList) {
        if (objectType != null) {
            Object template = nodeService.getProperty(objectType, forList ? PROP_OBJ_TYPE_LIST_TEMPLATE : PROP_OBJ_TYPE_TEMPLATE);
            return template != null ? (String) template : DEFAULT_OBJECT_TYPE_TEMPLATE;
        } else {
            return DEFAULT_OBJECT_TYPE_TEMPLATE;
        }
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
