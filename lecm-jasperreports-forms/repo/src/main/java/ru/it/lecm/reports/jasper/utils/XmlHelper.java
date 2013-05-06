package ru.it.lecm.reports.jasper.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.xpath.domapi.XPathEvaluatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathResult;


/**
 *
 * @author Ruslan
 */
public class XmlHelper {

	final static Logger logger = LoggerFactory.getLogger(XmlHelper.class);

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


	public static String getTagContent(Node tag)
	{
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
	private static final String ATTR_DATE_RELATIVE = "relative";
	private static final String ATTR_DATE_CUR_YEAR = "currentYear";

	/**
	 * �������� �������� ���� � ����:
	 *      <x>YYYYMMDD</x>
	 * ���
	 *      <x style=relative>+days</x>
	 * ���
	 *      <x style=relative>-days</x>
	 * ���
	 *      <x style=currentYear/>
	 * @param node
	 * @return
	 * @throws ParseException 
	 */
	public static Date getDateValue(Node node) throws ParseException
	{
		if (node == null)
			return null;

		final String valueText = getTagContent(node);
		if (!((Element) node).hasAttribute(ATTR_DATE_STYLE)) {
			// ���� ��� �������� ����� ���� -> ������� ���� ����
			if (valueText == null)
				throw new ParseException("Null value is invalid for the date", 0);

			return FORMAT_DATE.parse(valueText);
		}

		final String sdate = ((Element) node).getAttribute(ATTR_DATE_STYLE);
		if (ATTR_DATE_RELATIVE.equals(sdate)) { // ������ ����� "������������� ����"
			final String offset = valueText.trim();

			if (valueText == null)
				throw new ParseException("Null value is invalid for the relative date", 0);

			// (RuSA) check "relative offset MUST start with +/-" 
			// (don't ask me why) ...
			if ( -1 == "+-".indexOf(offset.charAt(0)) )
				throw new ParseException("Invalid offset for current date: " + offset, 0);

			final double days = Double.parseDouble(offset);
			final Date date = new Date();
			date.setTime( date.getTime() + Math.round(days * 24 * 60 * 60 * 1000) );
			return date;
		} else if(ATTR_DATE_CUR_YEAR.equals(sdate)) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);	
			return calendar.getTime();
		}
		throw new ParseException("Unknown date variable style: " + sdate, 0);
	}

}
