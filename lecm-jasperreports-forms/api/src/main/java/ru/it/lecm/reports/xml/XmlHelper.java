package ru.it.lecm.reports.xml;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.domapi.XPathEvaluatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathResult;
import org.xml.sax.SAXException;
import ru.it.lecm.reports.api.model.JavaClassable;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.Mnemonicable;
import ru.it.lecm.reports.model.impl.L18Value;
import ru.it.lecm.reports.utils.Utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author rabdullin
 */
public class XmlHelper {

    public static final String XML_DEFAULT_ENCODING = "UTF-8";

    public static final String XMLATTR_MNEM = "mnem";

    /**
     * парсер org.w3c.dom так называет узлы с комментариями (при этом тип узла Node.ELEMENT_NODE=1)
     */
    public static final String XML_STD_NODENAME_COMMENT = "#comment";

    /**
     * парсер org.w3c.dom так называет CDATA-узлы (при этом тип узла Node.ELEMENT_NODE=1)
     */
    public static final String XML_STD_NODENAME_CDATA = "#cdata-section";

    public static final String XML_NODENAME_VALUE = "value";

    public static final String XMLNODE_MAP = "map";
    public static final String XMLNODE_LIST = "list";

    private static final DateFormat FORMAT_DATE = new SimpleDateFormat("yyyyMMdd");

    private static final String ATTR_DATE_STYLE = "style";
    private static final String ATTR_V_DATE_RELATIVE = "relative";
    private static final String ATTR_V_DATE_CURYEAR = "currentYear";
    final private static String xmlItemName = "item", xmlKeyName = "key", xmlValueName = "value";

    final private static Logger logger = LoggerFactory.getLogger(XmlHelper.class);

    private XmlHelper() {
    }

    public static String getTagContent(Node doc, String tag, String attr, String val) {
        final XPathEvaluator xpath = new XPathEvaluatorImpl();
        String query = tag;
        if (attr != null && val != null) {
            query += "[@" + attr + "='" + val + "']";
        }
        query += "/text()";
        final XPathResult result = (XPathResult) xpath.evaluate(query, doc, null, XPathResult.STRING_TYPE, null);
        return result != null ? result.getStringValue() : null;
    }


    /**
     * Найти список узлов с указанным названием и значением атрибута.
     *
     * @param cur  узел, внутри которого искать
     * @param tag  название узла
     * @param attr название атрибута для филтрования по значению или Null, чтобы не использовать фильтр
     * @param val  значение атрибута для отфильтровывания
     */
    public static List<Node> findNodesList(Node cur, String tag, String attr, String val) {
        if (cur == null) {
            return null;
        }

		/* были проблемы с поиском: ничего не находит (!?) внутри любых узлов не верхнего уровня
        final XPathEvaluator xpath = new XPathEvaluatorImpl();
		// v1:
		String query = tag;
		if (attr != null && val != null)
			query += "[@" + attr + "='" + val + "']";
		// v2:
		// String query = String.format( "//target[@name='%s']", tag);
		// if (attr != null && val != null)
		// 	query += "/property[@" + attr + "='" + val + "']";
		final XPathResult result = (XPathResult) xpath.evaluate(query,
				cur, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
		final List<Node> nodes = new ArrayList<Node>();
		while (true) {
			final Node node  = result.iterateNext();
			if ( node == null) break;
			nodes.add(node);
		}
		return (!nodes.isEmpty()) ? nodes : null;
		*/

        final List<Node> result = new ArrayList<Node>();
        // почленный поиск ...
        if (cur.getChildNodes() != null) {
            for (int i = 0; i < cur.getChildNodes().getLength(); i++) {
                final Node item = cur.getChildNodes().item(i);
                if (tag == null || tag.equals(item.getNodeName())) {
                    // по названию подходит - проверим атрибут ...
                    if (hasAttribute(item, attr, val)) {
                        result.add(item); // FOUND
                    }
                }
            }
        }
        return (result.isEmpty()) ? null : result;
    }


    /**
     * Проверить содержит ли узел указанный атрибут
     *
     * @param attr искомое название атрибута, если NULL, то всегда подходит.
     * @param val  значение атрибута (пустая строка эквивалентна null и наоборот)
     * @return true, если содержит и false иначе, для искомого атрибута NULL, результат TRUE.
     */
    public static boolean hasAttribute(Node item, String attr, String val) {
        if (item == null) {
            return false; // NOT FOUND
        }

        if (attr == null) {
            return true; // OK
        }

        if (!item.hasAttributes()) {
            return false; // NOT FOUND
        }

        final Node attrNode = item.getAttributes().getNamedItem(attr);
        if (attrNode != null) { // проверка значения ...
            if (Utils.coalesce(val, "").equals(Utils.coalesce(attrNode.getNodeValue(), ""))) {
                return true; // FOUND
            }
        }
        return false; // NOT FOUND
    }


    /**
     * Найти список узлов с указанным названием и значением атрибута.
     *
     * @param cur узел, внутри которого искать
     * @param tag название узла
     */
    public static List<Node> findNodesList(Node cur, String tag) {
        return findNodesList(cur, tag, null, null);
    }

    /**
     * Найти именованный узел с указанным названием или вернуть null
     *
     * @return вернуть единственный узел или null если нет такого,
     *         (!) Если узлов с указанным именем окажется более одного поднимается исключение
     */
    public static Element findNodeByName(Node cur, String tag) {
        return findNodeByName(cur, tag, null, null);
    }

    /**
     * Найти именованный узел с указанным названием и значением атрибута или вернуть null.
     *
     * @param cur узел, внутри которого искать
     * @param tag название узла
     * @return вернуть единственный узел или null если нет такого,
     *         (!) Если узлов с указанным именем окажется более одного поднимается исключение
     */
    public static Element findNodeByName(Node cur, String tag, String attr, String val) {
        final List<Node> found = findNodesList(cur, tag, attr, val);
        if (found == null || found.isEmpty()) {
            return null;
        }
        if (found.size() != 1) {
            throw new RuntimeException(String.format("XML node '%s' must be single but found %d items", tag, found.size()));
        }
        return (Element) found.get(0);
    }


    /**
     * Найти первый встреченный дочерний узел с именем из указанного списка
     *
     * @param scanFor названия узлов, которые надо найти
     */
    public static Node tryFindNextToFieldsXmlSection(Element node, String[] scanFor) {
        final NodeList list = node.getChildNodes();
        if (list != null) {
            final List<String> what = Arrays.asList(scanFor);
            for (int i = 0; i < list.getLength(); i++) {
                final Node child = list.item(i);
                if (what.contains(child.getNodeName()))
                    return child; // FOUND
            }
        }
        return null; // NOT FOUND
    }

    /**
     * Получить текстовый контент узла (из вложенного CDATA, или простого текста).
     * Для атрибутов возвращается их value.
     */
    public static String getTagContent(Node tag) {
        if (tag == null) {
            return null;
        }

        if (tag instanceof org.w3c.dom.Attr) {
            return ((org.w3c.dom.Attr) tag).getValue();
        }

        final XPathEvaluator xpath = new XPathEvaluatorImpl();
        final XPathResult result = (XPathResult) xpath.evaluate("text()",
                tag, null, XPathResult.STRING_TYPE, null);
        return (result != null) ? result.getStringValue() : null;
    }


    public static Node findNodeByAttr(Node doc, String tag, String attr, String val) {
        final XPathEvaluator xpath = new XPathEvaluatorImpl();
        String query = tag;
        if (attr != null && val != null) {
            query += "[@" + attr + "='" + val + "']";
        }
        final XPathResult result = (XPathResult) xpath.evaluate(query, doc, null, XPathResult.ANY_UNORDERED_NODE_TYPE, null);
        return (result != null) ? result.getSingleNodeValue() : null;
    }

    public static boolean getAttributeBoolean(Element elem, String attr) {
        final String val = getAttributeString(elem, attr);
        return (val == null) ? null : Boolean.valueOf(val);
    }

    public static String getAttributeString(final Element elem, final String attrName) {
        final XPathEvaluator xpath = new XPathEvaluatorImpl();
        final XPathResult result = (XPathResult) xpath.evaluate(attrName, elem, null, XPathResult.STRING_TYPE, null);
        return result.getStringValue();
    }

    public static void xmlAddClassNameAttr(Element result, JavaClassable jclazz, String xmlAttrName, String defaultAttrValue) {
        if (jclazz != null && jclazz.className() != null) {
            if (defaultAttrValue != null && jclazz.className().equals(defaultAttrValue)) {
                return; // совпадает со значением по-умолчанию
            }
            result.setAttribute(xmlAttrName, jclazz.className());
        }
    }

    public static String getClassNameAttr(Element src, String xmlAttrName, String defaultClaszz) {
        String javaClass = defaultClaszz;
        if (src != null && src.hasAttribute(xmlAttrName)) {
            javaClass = src.getAttribute(xmlAttrName);
        }
        return javaClass;
    }

    public static boolean getNodeAsBool(Element srcGrpNode, String xmlSubnodeName, boolean vDefault) {
        final String valueText = getNodeAsText(srcGrpNode, xmlSubnodeName, null);
        if (valueText != null && valueText.length() > 0) {
            return Boolean.parseBoolean(valueText); // get as integer
        }
        return vDefault;
    }


    public static int getNodeAsInt(Element srcGrpNode, String xmlSubnodeName, int vDefault) {
        final String valueText = getNodeAsText(srcGrpNode, xmlSubnodeName, null);
        if (valueText != null && valueText.length() > 0) {
            return Integer.parseInt(valueText.trim()); // get as integer
        }
        return vDefault;
    }

    public static String getNodeAsText(Element srcGrpNode, String xmlSubnodeName, String vDefault) {
        final List<Node> nodes = findNodesList(srcGrpNode, xmlSubnodeName);
        if (nodes != null && nodes.size() > 0) {
            final Node node = nodes.get(0);
            final String valueText = getTagContent(node);
            if (valueText != null && valueText.length() > 0)
                return valueText;
        }

        return vDefault;
    }


    /**
     * Внутри указанного узла найти именованный субузел и вернуть его значение,
     * которое может быть указано как (в порядке убывания приоритета):
     * 1) АТРИБУТ "value",
     * 2) как text-значение дочернего узла с именем "value",
     * 3) как cdata-значение дочернего узла.
     * Узлы-комментарии пропускаются.
     */
    public static String findNodeChildValue(Element srcGrpNode, String xmlSubnodeName) {
        final Node subNode = findNodeByName(srcGrpNode, xmlSubnodeName);
        if (subNode != null && subNode.getChildNodes() != null) {
            for (int i = 0; i < subNode.getChildNodes().getLength(); i++) {
                final Node item = subNode.getChildNodes().item(i);

                if (item == null
                        || item.getNodeType() == Node.COMMENT_NODE
                        || XML_STD_NODENAME_COMMENT.equalsIgnoreCase(item.getNodeName())
                        ) // skip comments and ELEMENT_NODE with name="#comment" ...
                    continue;

                if (XML_NODENAME_VALUE.equalsIgnoreCase(item.getNodeName())) { // FOUND value-node
                    return XmlHelper.getTagContent(item);
                } else if (item.getNodeType() == Node.CDATA_SECTION_NODE
                        || XML_STD_NODENAME_CDATA.equalsIgnoreCase(item.getNodeName())) {
                    // NOTE: (!?) для CDATA-секции имеем тип ELEMENT_NODE и ловим её только по имени name="#cdata-section"
                    return item.getTextContent();
                }
            }
        }
        return null; // NOT FOUND
    }

    /**
     * Получить значение даты заданной в XML узле как со стилем или непосредственно.
     * xml-формат дат со стилем (т.е. при наличии атрибута "style"):
     * или относительная
     * <x style="relative">+days</x>
     * или
     * <x style="relative">-days</x>
     * или
     * <x style="currentYear"/>
     * При отсутсивии атрибута "style" принимается что дата задана непосредственно в виде:
     * <x>YYYYMMDD</x>
     *
     * @param node xml-описание с атрибутом стиль или без него
     * @return типизированная дата (с учётом стиля) или исключение при
     *         неподдерживаемом стиле или ошибках разбора
     * @throws ParseException
     */
    public static Date getNodeAsDate(Node node) throws ParseException {
        if (node == null) {
            return null;
        }

        final String valueText = getTagContent(node);
        if (!((Element) node).hasAttribute(ATTR_DATE_STYLE)) {
            // если нет утрибута стиля даты -> указана сама дата
            if (valueText == null) {
                throw new ParseException("Null value is invalid for the date", 0);
            }

            return FORMAT_DATE.parse(valueText);
        }

        final String sdate = ((Element) node).getAttribute(ATTR_DATE_STYLE);
        if (ATTR_V_DATE_RELATIVE.equals(sdate)) { // указан стиль "относительная дата"
            if (valueText == null) {
                throw new ParseException("Null value is invalid for the relative date", 0);
            }

            final String offset = valueText.trim();

            // (RuSA) check "relative offset MUST start with +/-"
            // (don't ask me why) ...
            if (-1 == "+-".indexOf(offset.charAt(0))) {
                throw new ParseException("Invalid offset for current date: " + offset, 0);
            }

            final double days = Double.parseDouble(offset);
            final Date date = new Date();
            date.setTime(date.getTime() + Math.round(days * 24 * 60 * 60 * 1000));
            return date;
        } else if (ATTR_V_DATE_CURYEAR.equals(sdate)) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }
        throw new ParseException("Unknown date style: " + sdate, 0);
    }

    /**
     * Checks whether given file correspond to given schema
     *
     * @param file   source file
     * @param schema schema instance
     * @throws SAXException
     */
    public static void validateFile(File file, Schema schema) throws SAXException {
        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(file));
        } catch (SAXException ex) {
            logger.error("Input document is invalid", ex);
            throw ex;
        } catch (IOException ex) {
            logger.error("Error during source file validation", ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Checks whether given file correspond to given schema
     *
     * @param stream stream contained file
     * @param schema schema instance
     * @throws SAXException
     */
    public static void validateFile(InputStream stream, Schema schema) throws SAXException {
        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(stream));
        } catch (SAXException ex) {
            logger.error("Input document is invalid", ex);
            throw ex;
        } catch (IOException ex) {
            logger.error("Error during source file validation", ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Transforms given file according to given transformer.
     *
     * @param file            source file that should be transformed
     * @param fileTransformer transformation according to that transformer will be done
     * @return result of transformation represented by stream
     */
    public static ByteArrayOutputStream transformFile(File file, Transformer fileTransformer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            fileTransformer.transform(new StreamSource(file), new StreamResult(out));
        } catch (TransformerException ex) {
            logger.error("Error during transformation of file "
                    + file.getName());
            return null;
        }
        return out;
    }

    public static ByteArrayOutputStream transformFile(InputStream stream,
                                                      Transformer fileTransformer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            fileTransformer.transform(new StreamSource(stream), new StreamResult(out));
        } catch (TransformerException ex) {
            logger.error("Error during transformation");
            return null;
        }
        return out;
    }

    public static Document parseDOMDocument(InputStream inputStream) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            document = factory.newDocumentBuilder().parse(inputStream);
        } catch (ParserConfigurationException ex) {
            logger.error("Create DOM document error", ex);
            return null;
        } catch (IOException ex) {
            logger.error("Create DOM document error", ex);
            return null;
        } catch (SAXException ex) {
            logger.error("Create DOM document error", ex);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                logger.error("createDOMDocument: error during stream closing");
            }
        }
        return document;
    }

    /**
     * Creates <code>Document</code> instance that represents DOM structure of
     * given source represented by stream.
     *
     * @param source source file according to that <code>Document</code> will be created
     * @return Document instance
     */
    public static Document parseDOMDocument(ByteArrayOutputStream source) {
        final InputStream inputStream = new ByteArrayInputStream(source.toByteArray());
        return parseDOMDocument(inputStream);
    }

    /**
     * Serializes given DOM Document to XML file.
     *
     * @param document DOM document
     * @return stream containing XML file of given document
     */
    public static ByteArrayOutputStream serialize(Document document) {
        ByteArrayOutputStream result;
        try {
            result = new ByteArrayOutputStream();
            final OutputFormat format = new OutputFormat("XML", XML_DEFAULT_ENCODING, true);
            final XMLSerializer serializer = new XMLSerializer(result, format);
            serializer.serialize(document.getDocumentElement());
        } catch (IOException ex) {
            logger.error("Error during serialization xml", ex);
            return null;
        }
        return result;
    }


    /**
     * Гарантировать наличие элемента первого уровня, в который вложено всё остальное.
     *
     * @param doc
     * @return существующий или добавленный элемент
     */
    public static Element ensureRoot(Document doc, String nodeName) {
        Element result = doc.getDocumentElement();
        if (result == null) {
            result = doc.createElement(nodeName);
            doc.appendChild(result);
            assert result == doc.getDocumentElement() : "Check ensure Root";
        }
        return result;
    }


    /**
     * Создание узла с простым контентом
     *
     * @param parentNode если указан не Null - родитель для ного узла
     * @param nodeName   название узла
     * @param nodeValue  значение для узла
     */
    public static Element xmlCreatePlainNode(final Document document
            , final Element parentNode, String nodeName, Object nodeValue) {
        final Element result = document.createElement(nodeName);
        result.setTextContent(Utils.coalesce(nodeValue, ""));

        if (parentNode != null) {
            parentNode.appendChild(result);
        }

        return result;
    }

    /**
     * Создание узла с CDATA-контентом
     *
     * @param parentNode если указан не Null - родитель для ного узла
     * @param nodeName   название узла
     * @param cdataText  текст для CDATA узла
     */
    public static Element xmlCreateCDataNode(final Document document, final Element parentNode, String nodeName, String cdataText) {
        final Element result = document.createElement(nodeName);
        result.appendChild(document.createCDATASection(Utils.coalesce(cdataText, "")));

        if (parentNode != null) {
            parentNode.appendChild(result);
        }

        return result;
    }


    /**
     * Добавить карту-список как вложенные items-элементы в destRoot в виде:
     * <item key="..." value="..." />
     */
    public static void xmlAddMapItems(Document doc, Element destParent, Map<String, String> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<String, String> e : map.entrySet()) {
            //= doc.createElement(xmlItemName);
            final Element node  = XmlHelper.xmlCreateCDataNode(doc, destParent, xmlItemName, e.getValue());
            node.setAttribute(xmlKeyName, e.getKey());
            //node.setAttribute(xmlValueName, e.getValue());
            //destParent.appendChild(node);
        }
    }

    /**
     * Загрузить как Map items-узлы среди дочерних для mapNode
     */
    public static Map<String, Object> getNodeAsItemsMap(Node mapNode) {
        if (mapNode == null) {
            return null;
        }
        final Map<String, Object> result = new HashMap<String, Object>();
        final NodeList children = mapNode.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                final Node childNode = children.item(i);
                if (!xmlItemName.equalsIgnoreCase(childNode.getNodeName())) {
                    // skip non-item elements
                    continue;
                }

                final Node keyNode = (childNode.getAttributes() == null) ? null : childNode.getAttributes().getNamedItem(xmlKeyName);
                if (keyNode == null) {
                    final String info = String.format("XML map-node '%s'::'%s' has no 'key' attribute for item [%s]", mapNode.getNamespaceURI(), mapNode.getNodeName(), i);
                    logger.error(info);
                    throw new RuntimeException(info);
                }
                // получение данных чтобы не пропустить значение для такой конструкции:
                //    <item key="rowSum.show">true</item>
                // т.е. когда значение задано внутри
                final Node valueNode = (childNode.getAttributes() == null) ? null : childNode.getAttributes().getNamedItem(xmlValueName);
                final Object value = (valueNode != null) ? valueNode.getNodeValue() : getNodeAsSmart(childNode); // getTagContent(childNode);
                result.put(getTagContent(keyNode), value);
            } // for
        }
        return result;
    }

    public static List<Object> getNodeAsItemsList(Node listNode) {
        if (listNode == null) {
            return null;
        }
        final List<Object> result = new ArrayList<Object>();
        final NodeList children = listNode.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                final Node childNode = children.item(i);
                if (!xmlItemName.equalsIgnoreCase(childNode.getNodeName())) {
                    // skip non-item elements
                    continue;
                }
                result.add(getNodeAsSmart(childNode));
            } // for
        }
        return result;
    }

    /**
     * Из указанного узла выбрать list, map или значение.
     *
     * @param node
     * @return List, если у node имеется вложенный узел <list>
     *         Map, если имеется у node имеется вложенный узел <map>
     *         и простое текстовое значение иначе
     */
    public static Object getNodeAsSmart(Node node) {
        Object result = null;
        if (node != null) {
            final Element vListNode = (node.hasChildNodes())
                    ? (Element) XmlHelper.findNodeByAttr(node, XMLNODE_LIST, null, null)
                    : null;
            final Element vMapNode = (node.hasChildNodes())
                    ? (Element) XmlHelper.findNodeByAttr(node, XMLNODE_MAP, null, null)
                    : null;
            if (vListNode != null) { // загружаем список ...
                result = getNodeAsItemsList(vListNode);
            } else if (vMapNode != null) { // загружаем мапу ...
                result = getNodeAsItemsMap(vMapNode);
            } else { // принимаем что узел содержит строку
                final String data = getTagContent(node);
                result = (data == null ? null : data.trim());
            }
        }
        return result;
    }

    /**
     * Добавить подузел "l18" с локализацией
     *
     * @param doc        xml документ
     * @param destParent родительский узел внутри, которого требуется создать новый
     * @param srcItem    сериализуемый объект
     * @return созданный подузел (будет включен внутрь destParent)
     */
    public static Element xmlAddL18Name(Document doc, Element destParent, L18able srcItem) {
        return xmlAddL18Name(doc, destParent, srcItem, "l18");
    }

    /**
     * Добавить подузел nodeName с локализацией
     *
     * @param doc        xml документ
     * @param destParent родительский узел внутри, которого требуется создать новый
     * @param srcItem    сериализуемый объект
     * @return созданный подузел (будет включен внутрь destParent)
     */
    public static Element xmlAddL18Name(Document doc, Element destParent, L18able srcItem, final String nodeName) {
        if (srcItem == null || srcItem.getL18items() == null || srcItem.getL18items().isEmpty()) {
            return null;
        }
        final Element result = doc.createElement(nodeName);
        destParent.appendChild(result);

        xmlAddMapItems(doc, result, srcItem.getL18items());

        return result;
    }

    public static L18able getNodeAsL18(Element srcRoot) {
        final L18Value result = new L18Value();
        parseL18(result, srcRoot, "l18");
        return result;
    }

    public static void parseL18(L18able dest, Element srcRoot) {
        parseL18(dest, srcRoot, "l18");
    }

    public static void parseL18(L18able dest, Element srcRoot, final String nodeName) {
        if (srcRoot == null) {
            return;
        }

        final Element src = findNodeByName(srcRoot, nodeName);
        if (src == null) {
            return;
        }

        final Map<String, Object> found = getNodeAsItemsMap(src);
        if (found != null) {
            for (Map.Entry<String, Object> e : found.entrySet())
                dest.getL18items().put(e.getKey(), Utils.coalesce(e.getValue(), null));
        }
    }

    public static void xmlAddMnemAttr(Element dest, Mnemonicable mnemo) {
        if (mnemo.getMnem() != null) {
            dest.setAttribute(XMLATTR_MNEM, mnemo.getMnem());
        }
    }

    public static void parseMnemAttr(Mnemonicable destMnemo, Element src) {
        if (src != null && src.hasAttribute(XMLATTR_MNEM)) {
            destMnemo.setMnem(src.getAttribute(XMLATTR_MNEM));
        }
    }

    public static Element xmlCreateStdMnemoItem(Document doc, Mnemonicable objMnemo, String xmlNodeName) {
        if (objMnemo == null) {
            return null;
        }
        final Element result = doc.createElement(xmlNodeName);
        xmlAddMnemAttr(result, objMnemo);
        if (objMnemo instanceof L18able) {
            xmlAddL18Name(doc, result, (L18able) objMnemo);
        }
        return result;
    }

    public static void parseStdMnemoItem(Mnemonicable destObjMnemo, Element srcNode) {
        if (destObjMnemo == null) {
            return;
        }
        if (srcNode != null) {
            parseMnemAttr(destObjMnemo, srcNode);
            if (destObjMnemo instanceof L18able) {
                parseL18((L18able) destObjMnemo, srcNode);
            }
        }
    }
}
