package ru.it.lecm.reports.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.it.lecm.reports.api.model.AlfrescoAssocInfo;
import ru.it.lecm.reports.api.model.AlfrescoAssocInfo.AssocKind;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.NamedValue;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.ParameterTypedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFlags;
import ru.it.lecm.reports.api.model.ReportProviderDescriptor;
import ru.it.lecm.reports.api.model.ReportTemplate;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.model.impl.AlfrescoAssocInfoImpl;
import ru.it.lecm.reports.model.impl.ColumnDescriptorImpl;
import ru.it.lecm.reports.model.impl.DataSourceDescriptorImpl;
import ru.it.lecm.reports.model.impl.NamedValueImpl;
import ru.it.lecm.reports.model.impl.ParameterTypedValueImpl;
import ru.it.lecm.reports.model.impl.ReportDescriptorImpl;
import ru.it.lecm.reports.model.impl.ReportFlagsImpl;
import ru.it.lecm.reports.model.impl.ReportProviderDescriptorImpl;
import ru.it.lecm.reports.model.impl.ReportTemplateImpl;
import ru.it.lecm.reports.model.impl.ReportTypeImpl;
import ru.it.lecm.reports.utils.Utils;

/**
 * Создание ds-xml с описанием мета-данных полей и запроса.
 * 
 * @author rabdullin
 *
 */
public class DSXMLProducer {

	private static final Logger logger = LoggerFactory.getLogger(DSXMLProducer.class);

	public static final String XMLNODE_ROOT_DS_CONFIG = "ds.config";

	/**
	 * Префикс в имени файла с метаописанием отчёта
	 * Название имеет вид: "ds-ReportName.xml"
	 */
	final public static String PFX_DS = "ds-";

	public static final String XMLNODE_LIST_FIELDS = "fields";
	public static final String XMLNODE_FIELD = "field";
	public static final String XMLNODE_DATATYPE = "dataType";

	public static final String XMLNODE_PARAMETER = "parameter";
	public static final String XMLATTR_PARAM_LABEL1 = "label1";
	public static final String XMLATTR_PARAM_LABEL2 = "label2";

	/* параметры запроса в xml-секции "query" */
	public static final String XMLNODE_QUERYDESC = "query.descriptor";
	public static final String XMLNODE_QUERY_OFFSET = "offset";
	public static final String XMLNODE_QUERY_LIMIT = "limit";
	public static final String XMLNODE_QUERY_PGSIZE = "pgsize";
	public static final String XMLNODE_QUERY_TEXT = "queryText";
	public static final String XMLNODE_QUERY_ALLVERSIONS = "allVersions";
	public static final String XMLNODE_QUERY_MULTIROW = "isMultiRow";
	public static final String XMLNODE_QUERY_PREFEREDTYPE = "preferedType";


	/* группа с флагами */
	public static final String XMLNODE_FLAGS_MAP = "flags";

	/* параметры запроса в xml-секции "report" */
	public static final String XMLNODE_REPORTDESC = "report.descriptor";
	public static final String XMLNODE_REPORT_TYPE = "type"; // например, "JASPER"
	public static final String XMLNODE_REPORT_PROVIDER = "provider";
	public static final String XMLNODE_REPORT_TEMPLATE = "template";
	public static final String XMLNODE_REPORT_DS = "datasource.descriptor";
	public static final String XMLATTR_FILENAME = "filename";

	/* параметры cmis-соединения в xml-секции "cmis" */
	public static final String XMLNODE_CMIS = "cmis.connection";
	public static final String XMLNODE_CMIS_URL = "url";
	public static final String XMLNODE_CMIS_USERNAME = "username";
	public static final String XMLNODE_CMIS_PASSWORD = "password";

	public final static String XMLATTR_JR_FLDNAME = "jrFldName";
	public final static String XMLATTR_QUERY_FLDNAME ="queryFldName";
	public final static String XMLATTR_DISPLAYNAME = "displayName";
	public final static String XMLATTR_ORDER = "order";

	public final static String XMLATTR_VALUE_JAVACLASS = "javaValueClass";
	public final static String XMLATTR_JAVACLASS = "javaClass";

	// public final static String XMLATTR_INMAINDOC = "inMainDoc";

	// public static final String XMLATTR_MNEM = "mnem";
	public static final String XMLATTR_PARAM_TYPE = "paramType";
	public static final String XMLATTR_PARAM_ALFRESCO_TYPE = "alfrescoType";
	public static final String XMLNODE_ALFRESCO_ASSOC = "alfrescoAssoc";
	public static final String XMLATTR_ALFRESCO_ASSOC_NAME = "assocTypeName";
	public static final String XMLATTR_ALFRESCO_ASSOC_KIND = "assocKind"; // 11, 1M, M1, MM

	/* Класс по-умолчанию для колонки */
	private final static String DEFAULT_COLUMN_JAVACLASS = String.class.getName();
	private final static String DEFAULT_COLUMN_ALFRESCO_CLASS = "d:text";
	/**
	 * Создать контент ds-xml файл.
	 * Сейчас изменяет секции '<field>' и '<property name="dataSource" value="java-class">'. 
	 * @param streamName название потока (для информации в журнал)
	 * @param desc описание, которое надо сохранить
	 * @return поток с обновлёнными данными
	 */
	public static ByteArrayOutputStream xmlCreateDSXML( String streamName, ReportDescriptor desc ) {
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

			/* параметры xml-секции "report.descriptor" */
			final Element nodeRD = xmlCreateReportDescNode( doc, XMLNODE_REPORTDESC, desc);
			if (nodeRD != null)
				rootElem.appendChild(nodeRD);


			/* параметры запроса в xml-секции "query" */
			final Element nodeQuery = xmlCreateFlagsNode( doc, XMLNODE_QUERYDESC, desc.getFlags());
			if (nodeQuery != null)
				rootElem.appendChild(nodeQuery);

			// NOTE: когда появятся доп характеристики query внутри desc - сохранить их тут
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
			xmlAddFieldsList( doc, rootElem, XMLNODE_LIST_FIELDS, XMLNODE_FIELD, desc.getDsDescriptor().getColumns());

			/* формирование результата */
			final ByteArrayOutputStream result = XmlHelper.serialize( doc);

			logger.debug("produced SUCCESSFULL of ds-xml " + streamName);

			return result;

		} catch (Throwable t) {
			final String msg = "Problem producing ds-xml " + streamName;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
	}

	/**
	 * Создать контент ds-xml файл.
	 * Сейчас изменяет секции '<field>' и '<property name="dataSource" value="java-class">'. 
	 * @param streamName название потока (для информации в журнал)
	 * @param desc описание, которое надо сохранить
	 * @return поток с обновлёнными данными
	 */
	public static ReportDescriptor parseDSXML( InputStream data, final String streamName ) {
		if (data == null)
			return null;
		logger.debug("reading ds-xml " + streamName);

		try { 
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(data);

			// создание корневого элемента
			final Element rootElem = doc.getDocumentElement();
			if (!XMLNODE_ROOT_DS_CONFIG.equals(rootElem.getNodeName())) { // for safety
				throw new RuntimeException("DS XML root '" + XMLNODE_ROOT_DS_CONFIG + "' element expected at stream "+ streamName);
			}

			final ReportDescriptorImpl result = new ReportDescriptorImpl();

			/* параметры xml-секции "report.descriptor" */
			parseReportDesc( result, rootElem, XMLNODE_REPORTDESC);
			if (result.getMnem() == null) { // название как суффикс в имени файла потока ...
				result.setMnem( extractReportName(streamName));
			}

			/* параметры запроса в xml-секции "query" */
			result.setFlags( parseReportFlags( rootElem, XMLNODE_QUERYDESC) );

			// NOTE: когда появятся доп характеристики query внутри desc - сохранить их тут
			/* параметры cmis-соединения в xml-секции "cmis" */

			/* ListOf<feild> */
			parseColumns( result.getDsDescriptor().getColumns(), rootElem, streamName);

			logger.debug("load SUCCESSFULL from ds-xml " + streamName);

			return result;

		} catch (Throwable t) {
			final String msg = "Problem loading ds-xml " + streamName;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
	}

	private static Element xmlCreateReportDescNode(Document doc,
			String xmlNodeRDName, ReportDescriptor srcRDesc) {

		if (srcRDesc == null)
			return null;

		final Element result = XmlHelper.xmlCreateStdMnemoItem( doc, srcRDesc, xmlNodeRDName);

		{ // тип отчёта
			final Element nodeRType = xmlCreateReportTypeNode( doc, XMLNODE_REPORT_TYPE, srcRDesc.getReportType());
			if (nodeRType != null)
				result.appendChild(nodeRType);
		}

		{ // провайдер
			final Element nodeProvider = xmlCreateReportProviderNode( doc, XMLNODE_REPORT_PROVIDER, srcRDesc.getProviderDescriptor());
			if (nodeProvider != null)
				result.appendChild(nodeProvider);
		}


		{ // нативный шаблон отчёта
			final Element nodeTemplate = xmlCreateReportTemplateNode( doc, XMLNODE_REPORT_TEMPLATE, srcRDesc.getReportTemplate());
			if (nodeTemplate != null)
				result.appendChild(nodeTemplate);
		}


		{ // набор данных ...
			final Element nodeDS = xmlCreateReportDSNode( doc, XMLNODE_REPORT_DS, srcRDesc.getDsDescriptor());
			if (nodeDS != null)
				result.appendChild(nodeDS);
		}

		return result;
	}

	private static void parseReportDesc( ReportDescriptorImpl dest
			, Element srcRootElem, String xmlNodeNameReportDesc )
	{
		if (srcRootElem == null)
			return;

		final Element srcNode = XmlHelper.findNodeByName( srcRootElem, xmlNodeNameReportDesc);
		if (srcNode == null)
			return;

		XmlHelper.parseStdMnemoItem(dest, srcNode);

		{ // тип отчёта
			final Element nodeRType = XmlHelper.findNodeByName( srcRootElem, XMLNODE_REPORT_TYPE);
			dest.setReportType( parseReportType(nodeRType) );
		}

		{ // провайдер
			final Element nodeProvider = XmlHelper.findNodeByName( srcRootElem, XMLNODE_REPORT_PROVIDER);
			dest.setProviderDescriptor( parseProviderDescriptor(nodeProvider));
		}


		{ // нативный шаблон отчёта
			final Element nodeTemplate = XmlHelper.findNodeByName( srcRootElem, XMLNODE_REPORT_TEMPLATE);
			dest.setReportTemplate( parseReportTemplate(nodeTemplate));
		}


		{ // набор данных ...
			final Element nodeDS = XmlHelper.findNodeByName( srcRootElem, XMLNODE_REPORT_DS);
			dest.setDSDescriptor( parseDSDescriptor(nodeDS));
		}
	}

	private static Element xmlCreateReportTypeNode(Document doc,
			String xmlNodeNameRType, ReportType rtypeDesc) {
		if (rtypeDesc == null)
			return null;
		return XmlHelper.xmlCreateStdMnemoItem( doc, rtypeDesc, xmlNodeNameRType);
	}

	private static ReportType parseReportType(Element srcNodeRType) {
		if (srcNodeRType == null)
			return null;

		final ReportTypeImpl result = new ReportTypeImpl();
		XmlHelper.parseStdMnemoItem( result, srcNodeRType);
		return result;
	}


	private static Element xmlCreateReportProviderNode(Document doc,
			String xmlNodeNameRP, ReportProviderDescriptor rpDesc)
	{
		if (rpDesc == null)
			return null;

		// final Element result = doc.createElement(xmlNodeNameRP);
		final Element result = XmlHelper.xmlCreateStdMnemoItem( doc, rpDesc, xmlNodeNameRP);
		XmlHelper.xmlAddClassNameAttr(result, rpDesc, XMLATTR_JAVACLASS, null);

		return result;
	}

	private static ReportProviderDescriptor parseProviderDescriptor(Element srcNodeProvider) {

		if (srcNodeProvider == null)
			return null;

		final ReportProviderDescriptorImpl result = new ReportProviderDescriptorImpl();
		XmlHelper.parseStdMnemoItem( result, srcNodeProvider);

		{ // java-класс провайдера ...
			final String javaClass = XmlHelper.getClassNameAttr( srcNodeProvider, XMLATTR_JAVACLASS, DEFAULT_COLUMN_JAVACLASS);
			result.setClassName(javaClass);
		}

		return result;
	}

	private static Element xmlCreateReportTemplateNode(Document doc,
			String xmlNodeNameRT, ReportTemplate rtDesc) {
		if (rtDesc == null)
			return null;

		final Element result = XmlHelper.xmlCreateStdMnemoItem( doc, rtDesc, xmlNodeNameRT);
		if (rtDesc.getFileName() != null)
			result.setAttribute(XMLATTR_FILENAME, rtDesc.getFileName());

		return result;
	}

	private static ReportTemplate parseReportTemplate(Element srcNodeTemplate) {
		if (srcNodeTemplate == null)
			return null;

		final ReportTemplateImpl result = new ReportTemplateImpl();
		XmlHelper.parseStdMnemoItem( result, srcNodeTemplate);

		if (srcNodeTemplate.hasAttribute(XMLATTR_FILENAME))
			result.setFileName( srcNodeTemplate.getAttribute(XMLATTR_FILENAME));

		return result;
	}


	private static Element xmlCreateReportDSNode(Document doc,
			String xmlNodeNameDS, DataSourceDescriptor dsDesc)
	{
		if (dsDesc == null)
			return null;

		final Element result = XmlHelper.xmlCreateStdMnemoItem( doc, dsDesc, xmlNodeNameDS);
		// TODO: тут описания полей стоит включить, сейчас они включены уровнем выше

		return result;
	}

	private static DataSourceDescriptor parseDSDescriptor(Element srcNodeDS) {
		if (srcNodeDS == null)
			return null;

		final DataSourceDescriptorImpl result = new DataSourceDescriptorImpl();
		XmlHelper.parseStdMnemoItem( result, srcNodeDS);

		return result;
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
	private static void xmlAddFieldsList(Document doc, Element destRoot,
			String xmlNodeList, String xmlNodeItem,
			List<ColumnDescriptor> srcColumns) 
	{
		final Element nodeFields = doc.createElement(xmlNodeList);
		destRoot.appendChild(nodeFields);

		if (srcColumns == null || srcColumns.isEmpty())
			return;

		/* вывод колонок ... */
		for(ColumnDescriptor cdesc: srcColumns) {
			final Element nodeColItem = xmlCreateColumnNode(doc, xmlNodeItem, cdesc);
			nodeFields.appendChild(nodeColItem);
		}
	}

	/**
	 * Сохранение в xml атрибутов в списка флагов объекта destColumn
	 * @param result целевой xml узел
	 * @param srcFlags исходный список атрибутов
	 * @param stdSkipArgs названия атрибутов, которые надо (!) пропускать
	 */
	private static void xmlAddFlagsAttributes( Document doc, Element result
			, Set<NamedValue> srcFlags, Set<String> stdSkipArgs) {
		if (srcFlags == null)
			return;
		for(NamedValue v: srcFlags) {
			final boolean isStdName = (stdSkipArgs != null) && stdSkipArgs.contains(v.getMnem());
			if (!isStdName)
				XmlHelper.xmlCreatePlainNode( doc, result, v.getMnem(), v.getValue());
		}
	}

	/**
	 * Добавить групповой узел с флагами в виде подузлов:
	 *    <xmlFlagsGrpName>
	 *       <key1>value1</key1> 
	 *       <key2>value2</key2>
	 *       ... 
	 *    </xmlFlagsGrpName>
	 * @param doc
	 * @param xmlFlagsGrpName название группового узла
	 * @param srcFlags флаги
	 * @return
	 */
	private static Element xmlCreateFlagsAttributesGrp( Document doc
			, String xmlFlagsGrpName, Set<NamedValue> srcFlags)
	{
		if (srcFlags == null || srcFlags.isEmpty())
			return null;

		final Element result = doc.createElement(xmlFlagsGrpName);
		xmlAddFlagsAttributes( doc, result, srcFlags, null);
		return result;
	}

	/**
	 * Получение значений атрибутов в список флагов объекта destFld из мапы src
	 * @param destFlags целевой набор флагов
	 * @param src исходный список атрибутов
	 * @param stdSkipArgs названия пропускаемых атрибутов для списка флагов
	 */
	private static void parseFlagsAttributes( Set<NamedValue> destFlags
			, NodeList src, Set<String> stdSkipArgs) {
		if (src == null)
			return;
		for( int i = 0; i < src.getLength(); i++) {
			final Node n = src.item(i);
			if (n == null) continue;
			// фильтра нет или значение не фильтруется
			final boolean isStdName = (stdSkipArgs != null) && stdSkipArgs.contains(n.getNodeName());
			if (!isStdName)
				destFlags.add( new NamedValueImpl(n.getNodeName(), n.getNodeValue()) );
		}
	}

	/** Набор стандартных названий атрибутов */
	final static Set<String> STD_XML_FLD_ARGS = new HashSet<String>( Arrays.asList( 
			XMLATTR_JR_FLDNAME
			, XMLATTR_QUERY_FLDNAME
			, XMLATTR_DISPLAYNAME
			, XMLATTR_JAVACLASS 
			));

	/**
	 * Создание field-узла для колонки
	 * @param doc
	 * @param nodeName
	 * @param column
	 * @return
	 */
	private static Element xmlCreateColumnNode(Document doc, String nodeName, ColumnDescriptor column) {
		if (column == null)
			return null;

		final Element result = doc.createElement(nodeName);

		result.setAttribute(XMLATTR_JR_FLDNAME, column.getColumnName());
		result.setAttribute(XMLATTR_QUERY_FLDNAME, column.getExpression());
		result.setAttribute(XMLATTR_DISPLAYNAME, column.get("ru", ""));
		if (column.getOrder() != 0)
			result.setAttribute(XMLATTR_ORDER, String.valueOf(column.getOrder()));
		result.setAttribute(XMLATTR_PARAM_ALFRESCO_TYPE, column.getAlfrescoType() != null ? column.getAlfrescoType() : DEFAULT_COLUMN_ALFRESCO_CLASS);
		// DONE: save map-locale {column.getL18Items} like addL18Name( doc, result, column);
		XmlHelper.xmlAddL18Name(doc, result, column);

		// тип колонки ...
		XmlHelper.xmlAddClassNameAttr(result, column, XMLATTR_VALUE_JAVACLASS, DEFAULT_COLUMN_JAVACLASS);

		// result.setAttribute( XMLATTR_INMAINDOC, column.flags("inMainDoc"));

		// выгрузка остальных атрибутов ...
		xmlAddFlagsAttributes(doc, result, column.flags(), STD_XML_FLD_ARGS);

		// тип параметра для колонки ...
		{
			final Element nodeParameter = xmlCreateParameterNode(doc, XMLNODE_PARAMETER, column.getParameterValue());
			if (nodeParameter != null)
				result.appendChild(nodeParameter);
		}

		return result;
	}

	/**
	 * Загрузить список метаописаний полей из указанного документа
	 * @param srcRoot
	 * @param info название читаемого потока
	 * @param destColumns
	 */
	public static void parseColumns( List<ColumnDescriptor> destColumns, 
			Element srcRoot, String info)
	{
		destColumns.clear();

		// @param xmlNodeNameListFields название xml-группы с метаописаниями
		// @param xmlNodeNameField элементы внутри группы

		// чтение мета-описаний полей ... 
		final Element fieldsNode  = (Element) XmlHelper.findNodeByAttr(srcRoot, DSXMLProducer.XMLNODE_LIST_FIELDS, null, null);
		final List<Node> fieldsNodeList = XmlHelper.findNodesList(fieldsNode, DSXMLProducer.XMLNODE_FIELD, null, null);
		final List<ColumnDescriptor> newColumns = parseColumns( fieldsNodeList, info);
		if (newColumns != null)
			destColumns.addAll(newColumns);
	}

	public static List<ColumnDescriptor> parseColumns(List<Node> fieldsNodes, String info) {
		if (fieldsNodes == null || fieldsNodes.isEmpty()) {
			logger.warn(String.format("ds xml %s does not contains any fields at %s[%s]", info, XMLNODE_LIST_FIELDS, XMLNODE_FIELD));
			return null;
		}

		final LinkedHashMap<String, ColumnDescriptor> result = new LinkedHashMap<String, ColumnDescriptor>(10);

		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Node node : fieldsNodes) {
			i++;
			final Element fldNode = (Element) node;

			// FIELD_JR_FLDNAME
			String jrFldname = "COL_" + i; // default name for any field will be simple "col_nn"
			if (fldNode.hasAttribute(XMLATTR_JR_FLDNAME)) {
				final String sname = fldNode.getAttribute(XMLATTR_JR_FLDNAME);
				if (sname != null && sname.length() > 0)
					jrFldname = sname;
			}

			// корректировка названия колонки для гарантии уникальности имени
			{
				String nameUnique = jrFldname;
				int unique = 0;
				while (result.containsKey(nameUnique)) { // название вида "ABC_n" появится только при неуникальности
					unique++; // (!) нумерация колонок от единицы
					nameUnique = jrFldname + "_" + unique;
				}
				if (unique > 0)
					logger.warn(String.format("Unique field name generated as '%s' (for base name '%s')", nameUnique, jrFldname));
				jrFldname = nameUnique;
			}
			// добавление новой jr-колонки
			final ColumnDescriptor column = new ColumnDescriptorImpl(jrFldname);
			result.put(column.getColumnName(), column);

			if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_QUERY_FLDNAME)) {
				final String queryFldName = fldNode.getAttribute(DSXMLProducer.XMLATTR_QUERY_FLDNAME);
				if (queryFldName != null && queryFldName.length() > 0)
					column.setExpression(queryFldName);
			}

			// DISPLAY_NAME
			if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_DISPLAYNAME)) {
				final String displayName = fldNode.getAttribute(DSXMLProducer.XMLATTR_DISPLAYNAME);
				column.regItem("ru", displayName);
			}
			// result.setAttribute( XMLATTR_INMAINDOC, column.flags("inMainDoc"));

			if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_ORDER)) {
				final String sorder = fldNode.getAttribute(DSXMLProducer.XMLATTR_ORDER);
				if (!Utils.isStringEmpty(sorder)) {
					column.setOrder( Integer.parseInt(sorder));
				}
			}

			// DONE: restore map-locale
			XmlHelper.parseL18(column, fldNode);

			// JAVA_CLASS тип колонки ...
			{
				final String javaClass = XmlHelper.getClassNameAttr(fldNode, XMLATTR_VALUE_JAVACLASS, DEFAULT_COLUMN_JAVACLASS);
				column.setClassName(javaClass);
			}

			{
				final String alfrescoClass = XmlHelper.getClassNameAttr(fldNode, XMLATTR_PARAM_ALFRESCO_TYPE, DEFAULT_COLUMN_ALFRESCO_CLASS);
				column.setAlfrescoType(alfrescoClass);
			}

			// подгрузка остальных атрибутов ...
			parseFlagsAttributes(column.flags(), fldNode.getChildNodes(), STD_XML_FLD_ARGS);

			// тип параметра для колонки ...
			column.setParameterValue(parseParameterNode(fldNode, XMLNODE_PARAMETER));

			// журналирование
			if (logger.isDebugEnabled())
				sb.append(String.format("got column/field %s: %s/%s [%s] '%s'"
						, i, column.getColumnName(), column.getExpression(), column.className(), column.get(null, null)));

		} //for

		if (logger.isDebugEnabled()) {
			sb.append(String.format("load %d fields from %s", i, info));
			logger.debug(sb.toString());
		}

		return new ArrayList<ColumnDescriptor>(result.values());
	}

	private static Element xmlCreateParameterNode( Document doc,
			String xmlNodeName, ParameterTypedValue parameter) 
	{
		if (parameter == null)
			return null;

		final Element result = doc.createElement(xmlNodeName);

		XmlHelper.xmlAddMnemAttr( result, parameter);

		if (parameter.getType() != null)
			result.setAttribute( XMLATTR_PARAM_TYPE, parameter.getType().getMnemonic());

		/* альфресковская ассоциация ... */
		{
			final Element nodeAssoc = xmlCreateAssocNode(doc, XMLNODE_ALFRESCO_ASSOC, parameter.getAlfrescoAssoc());
			if (nodeAssoc != null)
				result.appendChild(nodeAssoc);
		}

		XmlHelper.xmlAddL18Name( doc, result, parameter);
		XmlHelper.xmlAddL18Name( doc, result, parameter.getPrompt1(), XMLATTR_PARAM_LABEL1);
		XmlHelper.xmlAddL18Name( doc, result, parameter.getPrompt2(), XMLATTR_PARAM_LABEL2);

		return result;
	}

	private static Element xmlCreateAssocNode(Document doc, String xmlNodeName, AlfrescoAssocInfo assoc) {
		if (assoc == null)
			return null;

		final Element result = doc.createElement(xmlNodeName);

		if (assoc.getAssocTypeName() != null)
			result.setAttribute( XMLATTR_ALFRESCO_ASSOC_NAME, assoc.getAssocTypeName());
		if (assoc.getAssocKind() != null)
			result.setAttribute( XMLATTR_ALFRESCO_ASSOC_KIND, assoc.getAssocKind().getMnemonic());

		return result;
	}

	private static ParameterTypedValue parseParameterNode( Element srcColumnNode, String xmlNodeName) 
	{
		if (srcColumnNode == null)
			return null;

		// final Element result = doc.createElement(xmlNodeName);
		final Element nodeParameter = XmlHelper.findNodeByName( srcColumnNode, xmlNodeName);
		if (nodeParameter == null)
			return null;

		final ParameterTypedValueImpl result = new ParameterTypedValueImpl( );
		XmlHelper.parseMnemAttr( result, nodeParameter);

		/* альфресковская ассоциация ... */
		result.setAlfrescoAssoc( parseAssocNode(nodeParameter, XMLNODE_ALFRESCO_ASSOC));

		{ /* тип параметра */
			Type parType = null;
			if (nodeParameter.hasAttribute( XMLATTR_PARAM_TYPE)) {
				parType = Type.findType( nodeParameter.getAttribute( XMLATTR_PARAM_TYPE) );
			}
			result.setType( parType);
		}

		XmlHelper.parseL18( result, nodeParameter);
		XmlHelper.parseL18( result.getPrompt1(), nodeParameter, XMLATTR_PARAM_LABEL1);
		XmlHelper.parseL18( result.getPrompt2(), nodeParameter, XMLATTR_PARAM_LABEL2);

		return result;
	}

	private static AlfrescoAssocInfoImpl parseAssocNode(Element srcAssocNode,
			String xmlNodeName) {
		/*
	private static Element xmlCreateAssocNode(Document doc, String xmlNodeName, AlfrescoAssocInfo assoc) {
		if (assoc == null)
			return null;

		final Element result = doc.createElement(xmlNodeName);

		if (assoc.getAssocTypeName() != null)
			result.setAttribute( XMLATTR_ALFRESCO_ASSOC_NAME, assoc.getAssocTypeName());
		if (assoc.getAssocKind() != null)
			result.setAttribute( XMLATTR_ALFRESCO_ASSOC_KIND, assoc.getAssocKind().getMnemonic());

		return result;
	}

		 * */
		if (srcAssocNode == null)
			return null;

		final Element nodeAssoc = XmlHelper.findNodeByName( srcAssocNode, xmlNodeName);
		if (nodeAssoc == null)
			return null;

		final AlfrescoAssocInfoImpl result = new AlfrescoAssocInfoImpl();

		/* альфресковский ассоциация параметра */
		if (nodeAssoc.hasAttribute( XMLATTR_ALFRESCO_ASSOC_NAME)) {
			result.setAssocTypeName( nodeAssoc.getAttribute( XMLATTR_ALFRESCO_ASSOC_NAME) );
		}

		/* альфресковский ассоциация параметра */
		{
			AssocKind kind = null;
			if (nodeAssoc.hasAttribute( XMLATTR_ALFRESCO_ASSOC_KIND)) {
				kind = AssocKind.findAssocKind(nodeAssoc.getAttribute( XMLATTR_ALFRESCO_ASSOC_KIND));
			}
			result.setAssocKind( kind);
		}

		return result;
	}

	private static Element xmlCreateFlagsNode( Document doc, String xmlNodeQueryDesc, ReportFlags flags) {
		if (flags == null)
			return null;

		final Element result = doc.createElement(xmlNodeQueryDesc);

		XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_OFFSET,flags.getOffset());
		XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_LIMIT, flags.getLimit());
		XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_PGSIZE, flags.getPgSize());

		XmlHelper.xmlCreateCDataNode(doc, result, XMLNODE_QUERY_TEXT, flags.getText());
		XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_ALLVERSIONS, flags.isAllVersions());
		XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_PREFEREDTYPE, flags.getPreferedNodeType());

		XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_MULTIROW, flags.isMultiRow());

		/* включение атрибутов */
		{
			final Element flagsGrp = xmlCreateFlagsAttributesGrp( doc, XMLNODE_FLAGS_MAP, flags.flags() );
			if (flagsGrp != null)
				result.appendChild(flagsGrp);
		}

		return result;
	}

	private static ReportFlags parseReportFlags( Element root, String xmlNodeQueryDesc) {
		final Element curNode = (Element) XmlHelper.findNodeByAttr(root, xmlNodeQueryDesc, null, null);
		if (curNode == null)
			return null;

		final ReportFlagsImpl result = new ReportFlagsImpl();

		result.setOffset( XmlHelper.getNodeAsInt( curNode, XMLNODE_QUERY_OFFSET, result.getOffset()) );
		result.setLimit( XmlHelper.getNodeAsInt( curNode, XMLNODE_QUERY_LIMIT, result.getLimit()) );
		result.setPgSize( XmlHelper.getNodeAsInt( curNode, XMLNODE_QUERY_PGSIZE, result.getPgSize()) );

		result.setText( XmlHelper.getNodeAsText( curNode, XMLNODE_QUERY_TEXT, result.getText()) );
		result.setAllVersions( XmlHelper.getNodeAsBool( curNode, XMLNODE_QUERY_ALLVERSIONS, result.isAllVersions() ));
		result.setPreferedNodeType( XmlHelper.getNodeAsText( curNode, XMLNODE_QUERY_PREFEREDTYPE, result.getPreferedNodeType()) );

		result.setMultiRow( XmlHelper.getNodeAsBool( curNode, XMLNODE_QUERY_MULTIROW, result.isMultiRow()) );

		/* включение атрибутов */
		{
			// final Element flagsGrp = xmlCreateFlagsAttributesGrp( doc, XMLNODE_FLAGS_MAP, flags.flags() );
			final Element flagsGrp = XmlHelper.findNodeByName(curNode, XMLNODE_FLAGS_MAP);
			if (flagsGrp != null)
				parseFlagsAttributes(result.flags(), flagsGrp.getChildNodes(), null);
		}

		return result;
	}

	/**
	 * Выбираем название отчёта "ReportName" из названия файла/описателя вида "abc/ds-ReportName.xml".
	 * Если нет префикса "ds-", то строка берётся от последнего символа "/", а если его нет - от начала строки.  
	 * @param dsFileName
	 * @return
	 */
	public static String extractReportName( String dsFileName) {
		if (dsFileName == null)
			return null;
		int start = dsFileName.indexOf(PFX_DS);
		if (start >= 0) // с символа сразу после "ds-" ...
			start+= PFX_DS.length();
		else if ( (start = dsFileName.lastIndexOf("\\")) >= 0) // после символа "\"
			start++;
		else if ( (start = dsFileName.lastIndexOf("/")) >= 0) // после символа "/"
			start++;
		else
			start = 0; // если ничего нет - то с начала строки 

		int end = dsFileName.lastIndexOf(".");
		if (end < 0 || end < start) // если нет точки или она слева от start - до конца строки
			end = dsFileName.length();

		return dsFileName.substring(start, end);
	}

}
