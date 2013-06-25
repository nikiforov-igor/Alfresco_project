package ru.it.lecm.reports.xml;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;

/**
 * Создание ds-xml с описанием мета-данных полей и запроса.
 * 
 * @author rabdullin
 *
 */
public class DSXMLProducer {

	private static final Logger logger = LoggerFactory.getLogger(DSXMLProducer.class);

	public static final String XMLNODE_ROOT_DS_CONFIG = "ds.config";

	public static final String XMLNODE_LIST_FIELDS = "fields";
	public static final String XMLNODE_FIELD = "field";

	/* параметры запроса в xml-секции "query" */
	public static final String XMLNODE_QUERYDESC = "query.descriptor";
	public static final String XMLNODE_QUERY_OFFSET = "offset";
	public static final String XMLNODE_QUERY_LIMIT = "limit";
	public static final String XMLNODE_QUERY_PGSIZE = "pgsize";
	public static final String XMLNODE_QUERY_TEXT = "queryText";
	public static final String XMLNODE_QUERY_ALLVERSIONS = "allVersions";

	/* параметры cmis-соединения в xml-секции "cmis" */
	public static final String XMLNODE_CMIS = "cmis.connection";
	public static final String XMLNODE_CMIS_URL = "url";
	public static final String XMLNODE_CMIS_USERNAME = "username";
	public static final String XMLNODE_CMIS_PASSWORD = "password";

	public final static String XMLATTR_JR_FLDNAME = "jrFldName";
	public final static String XMLATTR_QUERY_FLDNAME ="queryFldName";
	public final static String XMLATTR_DISPLAYNAME = "displayName";
	public final static String XMLATTR_JAVACLASS = "javaValueClass";
	// public final static String XMLATTR_INMAINDOC = "inMainDoc";

	/* Класс по-умолчанию для колонки */
	private final static String DEFAULT_COLUMN_JAVACLASS = String.class.getName();

	/**
	 * Создать контент ds-xml файл.
	 * Сейчас изменяет секции '<field>' и '<property name="dataSource" value="java-class">'. 
	 * @param streamName название потока (для информации в журнал)
	 * @param desc описание, которое надо сохранить
	 * @return поток с обновлёнными данными
	 */
	public static ByteArrayOutputStream createDSXML( String streamName, ReportDescriptor desc ) {
		if (desc == null)
			return null;
		logger.debug("producing ds-xml " + streamName);

		try { 
			//	final InputSource src = new InputSource(xml);
			//	src.setEncoding("UTF-8");
			//	logger.info("Encodig set as: "+ src.getEncoding());
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			// создание корневого элемента
			final Element rootElem = XmlHelper.ensureRoot(doc, XMLNODE_ROOT_DS_CONFIG);
			if (!XMLNODE_ROOT_DS_CONFIG.equals(rootElem.getNodeName())) { // for safety
				throw new RuntimeException("DS XML root '" + XMLNODE_ROOT_DS_CONFIG + "' element expected");
			}

			/* параметры запроса в xml-секции "query" */
			{
				final Element nodeQuery = doc.createElement(XMLNODE_QUERYDESC);
				rootElem.appendChild(nodeQuery);

				// TODO: когда появятся характеричтики query внутри desc - тут их использовать
				XmlHelper.createPlainNode(doc, nodeQuery, XMLNODE_QUERY_OFFSET, desc.getFlags().getOffset());
				XmlHelper.createPlainNode(doc, nodeQuery, XMLNODE_QUERY_LIMIT, desc.getFlags().getLimit());
				XmlHelper.createPlainNode(doc, nodeQuery, XMLNODE_QUERY_PGSIZE, desc.getFlags().getPgSize());
				XmlHelper.createCDataNode(doc, nodeQuery, XMLNODE_QUERY_TEXT, desc.getFlags().getText());
				XmlHelper.createPlainNode(doc, nodeQuery, XMLNODE_QUERY_ALLVERSIONS, desc.getFlags().isAllVersions());
			}

			// TODO: когда появятся характеричтики query внутри desc - тут их использовать
			/* параметры cmis-соединения в xml-секции "cmis" */
			/*
			{
				final Element nodeCMIS = doc.createElement(XMLNODE_CMIS);
				rootElem.appendChild(nodeCMIS);
				XmlHelper.createPlainNode(doc, nodeCMIS, XMLNODE_CMIS_URL, desc.getCmisFlags().getURL());
				XmlHelper.createPlainNode(doc, nodeCMIS, XMLNODE_CMIS_USERNAME, desc.getCmisFlags().getUsername());
				XmlHelper.createPlainNode(doc, nodeCMIS, XMLNODE_CMIS_PASSWORD, desc.getCmisFlags().getPassword());
			}
			 */

			/*
			 * ListOf<feild>
			 */
			addFieldsList( doc, rootElem, XMLNODE_LIST_FIELDS, XMLNODE_FIELD, desc.getDsDescriptor().getColumns());

			/* формирование результата */
			final ByteArrayOutputStream result = XmlHelper.serialize( doc);

			logger.info("produced SUCCESSFULL of ds-xml " + streamName);

			return result;

		} catch (Throwable t) {
			final String msg = "Problem producing ds-xml " + streamName;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
	}

	/**
	 * Сформировать группу с описанием колонок:
	 *	<fields>
	 *		<field jrFldName="col_DocKind"
	 *			queryFldName="{lecm-contract:typeContract-assoc/cm:name}"
	 *			displayName="Вид договора"
	 *			inMainDoc="true"/>
	 *		...
	 *	</fields>
	 */
	private static void addFieldsList(Document doc, Element rootElem,
			String xmlNodeList, String xmlNodeItem,
			List<ColumnDescriptor> columns) 
	{
		final Element nodeFields = doc.createElement(xmlNodeList);
		rootElem.appendChild(nodeFields);

		if (columns == null || columns.isEmpty())
			return;

		/* вывод колонок ... */
		for(ColumnDescriptor cdesc: columns) {
			final Element nodeColItem = createColumnNode(doc, xmlNodeItem, cdesc);
			nodeFields.appendChild(nodeColItem);
		}
	}

	/**
	 * Создание field-узла для колонки
	 * @param doc
	 * @param nodeName
	 * @param column
	 * @return
	 */
	private static Element createColumnNode( Document doc, String nodeName, ColumnDescriptor column) 
	{
		if (column == null)
			return null;

		final Element result = doc.createElement(nodeName);

		result.setAttribute( XMLATTR_JR_FLDNAME, column.getColumnName());
		result.setAttribute( XMLATTR_QUERY_FLDNAME, column.getExpression());
		result.setAttribute( XMLATTR_DISPLAYNAME, column.get( "ru", ""));

		if (!DEFAULT_COLUMN_JAVACLASS.equals(column.className()))
			result.setAttribute( XMLATTR_JAVACLASS, column.className());

		// result.setAttribute( XMLATTR_INMAINDOC, column.flags("inMainDoc"));

		return result;
	}

}
