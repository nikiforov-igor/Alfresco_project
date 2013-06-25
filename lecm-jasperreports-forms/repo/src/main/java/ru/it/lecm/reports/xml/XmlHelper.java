package ru.it.lecm.reports.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

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

import ru.it.lecm.reports.jasper.utils.Utils;


/**
 * @author Ruslan
 */
public class XmlHelper {

	public static final String XML_DEFAULT_ENCODING = "UTF-8";
	public static String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

	final private static Logger logger = LoggerFactory.getLogger(XmlHelper.class);

	private XmlHelper() {
	}

	public static String getTagContent(Node doc, String tag, String attr, String val)
	{
		final XPathEvaluator xpath = new XPathEvaluatorImpl();
		String query = tag;
		if (attr != null && val != null)
			query += "[@" + attr + "='" + val + "']";
		query += "/text()";
		final XPathResult result = (XPathResult) xpath.evaluate(query,
				doc, null, XPathResult.STRING_TYPE, null);
		return result != null ? result.getStringValue() : null;
	}


	/**
	 * Найти список узлов с указанным названием и значением атрибута.
	 * @param doc узел, внутри которого искать
	 * @param tag название узла
	 * @param attr название атрибута для филтрования по значению или Null, чтобы не использовать фильтр
	 * @param val значение атрибута для отфильтровывания
	 * @return
	 */
	public static List<Node> getNodesList(Node doc, String tag, String attr, String val)
	{
		if (doc == null) return null;
		final XPathEvaluator xpath = new XPathEvaluatorImpl();
		String query = tag;
		if (attr != null && val != null)
			query += "[@" + attr + "='" + val + "']";
		final XPathResult result = (XPathResult) xpath.evaluate(query,
				doc, null, XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
		final List<Node> nodes = new ArrayList<Node>();
		while (true) {
			final Node node  = result.iterateNext();
			if ( node == null) break;
			nodes.add(node);
		}
		return (!nodes.isEmpty()) ? nodes : null;
	}


	/**
	 * Найти список узлов с указанным названием и значением атрибута.
	 * @param doc узел, внутри которого искать
	 * @param tag название узла
	 * @return
	 */
	public static List<Node> getNodesList(Node doc, String tag) {
		return getNodesList( null, tag, null, null);
	}


	/**
	 * Найти первый встреченный дочерний узел с именем из указанного списка
	 * @param node
	 * @param scanFor названия узлов, которые надо найти
	 * @return
	 */
	public static Node tryFindNextToFieldsXmlSection(Element node, String[] scanFor) {
		final NodeList list = node.getChildNodes();
		if (list != null) {
			final List<String> what = Arrays.asList(scanFor);
			for(int i = 0; i < list.getLength(); i++) {
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
	 * @param tag
	 * @return
	 */
	public static String getTagContent(Node tag)
	{
		if (tag == null)
			return null;

		if (tag instanceof org.w3c.dom.Attr)
			return ((org.w3c.dom.Attr) tag).getValue();

		final XPathEvaluator xpath = new XPathEvaluatorImpl();
		final XPathResult result = (XPathResult) xpath.evaluate("text()",
				tag, null, XPathResult.STRING_TYPE, null);
		return (result != null) ? result.getStringValue() : null;
		/*
		NodeList children = tag.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
			if (children.item(i).getNodeType() == Node.TEXT_NODE)
				return ((Text) children.item(i)).getData();
		throw new RuntimeException("XML error: " + tag + " tag should contain a text");
		 */
	}


	public static Node getTagNode(Node doc, String tag, String attr, String val)
	{
		final XPathEvaluator xpath = new XPathEvaluatorImpl();
		String query = tag;
		if (attr != null && val != null)
			query += "[@" + attr + "='" + val + "']";
		final XPathResult result = (XPathResult) xpath.evaluate(query,
				doc, null, XPathResult.ANY_UNORDERED_NODE_TYPE, null);
		return (result != null) ? result.getSingleNodeValue() : null;
	}

	public static boolean getAttributeBoolean(Element elem, String attr)
	{
		final XPathEvaluator xpath = new XPathEvaluatorImpl();
		final XPathResult result = (XPathResult) xpath.evaluate(attr,
				elem, null, XPathResult.STRING_TYPE, null);
		return Boolean.valueOf(result.getStringValue()).booleanValue();
	}

	public static String getAttributeString( final Element elem, final String attrName)
	{
		final XPathEvaluator xpath = new XPathEvaluatorImpl();
		final XPathResult result = (XPathResult) xpath.evaluate( attrName,
				elem, null, XPathResult.STRING_TYPE, null);
		return result.getStringValue();
	}

	private static final DateFormat FORMAT_DATE = new SimpleDateFormat("yyyyMMdd");

	private static final String ATTR_DATE_STYLE = "style";
	private static final String ATTR_V_DATE_RELATIVE = "relative";
	private static final String ATTR_V_DATE_CURYEAR = "currentYear";

	/**
	 * Получить значение даты заданной в XML узле как со стилем или непосредственно.
	 * xml-формат дат со стилем (т.е. при наличии атрибута "style"):
	 *    или относительная
	 *      <x style="relative">+days</x>
	 *    или
	 *      <x style="relative">-days</x>
	 *    или
	 *      <x style="currentYear"/>
	 * При отсутсивии атрибута "style" принимается что дата задана непосредственно в виде:
	 *      <x>YYYYMMDD</x>
	 * @param node xml-описание с атрибутом стиль или без него
	 * @return типизированная дата (с учётом стиля) или исключение при 
	 * неподдерживаемом стиле или ошибках разбора
	 * @throws ParseException 
	 */
	public static Date getDateValue(Node node) throws ParseException
	{
		if (node == null)
			return null;

		final String valueText = getTagContent(node);
		if (!((Element) node).hasAttribute(ATTR_DATE_STYLE)) {
			// если нет утрибута стиля даты -> указана сама дата
			if (valueText == null)
				throw new ParseException("Null value is invalid for the date", 0);

			return FORMAT_DATE.parse(valueText);
		}

		final String sdate = ((Element) node).getAttribute(ATTR_DATE_STYLE);
		if (ATTR_V_DATE_RELATIVE.equals(sdate)) { // указан стиль "относительная дата"
			if (valueText == null)
				throw new ParseException("Null value is invalid for the relative date", 0);

			final String offset = valueText.trim();

			// (RuSA) check "relative offset MUST start with +/-" 
			// (don't ask me why) ...
			if ( -1 == "+-".indexOf(offset.charAt(0)) )
				throw new ParseException("Invalid offset for current date: " + offset, 0);

			final double days = Double.parseDouble(offset);
			final Date date = new Date();
			date.setTime( date.getTime() + Math.round(days * 24 * 60 * 60 * 1000) );
			return date;
		} else if(ATTR_V_DATE_CURYEAR.equals(sdate)) {
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
	 * @param file source file
	 * @param schema schema instance
	 * @return whether file is valid
	 * @throws SAXException
	 */
	public static void validateFile(File file, Schema schema)
			throws SAXException {
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
	 * @param stream stream contained file
	 * @param schema schema instance
	 * @return whether file is valid
	 * @throws SAXException
	 */
	public static void validateFile(InputStream stream, Schema schema)
			throws SAXException {
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
	 * @param file source file that should be transformed
	 * @param fileTransformer transformation according to that transformer will be done
	 * @return result of transformation represented by stream
	 */
	public static ByteArrayOutputStream transformFile(File file,
			Transformer fileTransformer) {
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

	public static Document createDOMDocument(InputStream inputStream) {
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
	public static Document createDOMDocument(ByteArrayOutputStream source) {
		InputStream inputStream = new ByteArrayInputStream(source.toByteArray());
		return createDOMDocument(inputStream);
	}

	/**
	 * Serializes given DOM Document to XML file.
	 * @param document DOM document
	 * @return stream containing XML file of given document
	 */
	public static ByteArrayOutputStream serialize(Document document) {
		ByteArrayOutputStream result = null;
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
	 * @param doc
	 * @return существующий или добавленный элемент
	 */
	public static Element ensureRoot(Document doc, String nodeName) {
		Element result = doc.getDocumentElement();
		if (result == null) {
			result = 
			doc.createElement(nodeName); // doc.appendChild(doc.createElementNS(NS, "ds.config"));
			doc.appendChild(result);
			assert result == doc.getDocumentElement() : "Check ensure Root";
		}
		return result;
	}


	/**
	 * Создание узла с простым контентом
	 * @param document
	 * @param parentNode если указан не Null - родитель для ного узла
	 * @param nodeName название узла
	 * @param nodeValue значение для узла
	 * @return
	 */
	public static Element createPlainNode(final Document document
			, final Element parentNode, String nodeName, Object nodeValue)
	{
		final Element result = document.createElement(nodeName);
		// result.setAttribute(attr, value);
		// (-) result.setNodeValue( Utils.coalesce(nodeValue, ""));
		result.setTextContent( Utils.coalesce(nodeValue, ""));

		if (parentNode != null)
			parentNode.appendChild(result);

		return result;
	}

	/**
	 * Создание узла с CDATA-контентом
	 * @param document
	 * @param parentNode если указан не Null - родитель для ного узла
	 * @param nodeName название узла
	 * @param cdataText текст для CDATA узла
	 * @return
	 */
	public static Element createCDataNode(final Document document
			, final Element parentNode, String nodeName, String cdataText)
	{
		final Element result = document.createElement(nodeName);
		// result.setAttribute(attr, value);
		result.appendChild( document.createCDATASection(Utils.coalesce(cdataText, "")) );

		if (parentNode != null)
			parentNode.appendChild(result);

		return result;
	}

}
