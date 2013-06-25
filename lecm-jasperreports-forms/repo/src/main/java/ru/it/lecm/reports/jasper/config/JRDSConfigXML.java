package ru.it.lecm.reports.jasper.config;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.jasper.utils.MacrosHelper;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.reports.xml.XmlHelper;

/**
 * Реализация для чтения конфы из XML.
 * (!) При загрузке XML автоматом читаются:
 * 		1) Аргументы, перечисленные в getArgs (список задётся в setDefaults или может быть расширен провайдером)
 * 		2) Список метаописаний "fields.jasper", элементы "field", + атрибуты в каждом field.
 * 		3) Параметры из списка defaults (загружаются как отдельные блоки <xxx> ... </xxx>)
 * 		4) (!) Другие данные из XML игнорируются.
 * @author rabdullin
 */
public class JRDSConfigXML extends JRDSConfigBaseImpl {

	final static Logger logger = LoggerFactory.getLogger(JRDSConfigXML.class);


	// параметр с названием файла XML конфигурации
	final static public String TAG_CONFIGNAME = "xmlconfigName";

	/* Fields Descriptors - look at DSXMLProducer */
	/*
	final static private String XMLTAG_ROOT = "cmis-ds.config";

	final static public String XML_PATH_URL = "url";
	final static public String XML_PATH_USERNAME = "username";
	final static public String XML_PATH_PSW = "password";

	final static public String XMLNODE_QUERY = "query";
	final static public String XMLNODE_PATH_ALLVER = "allVersions";

	final static private String XMLNODE_LIST_FIELDS = "fields";
	final static private String XMLNITEM_FIELD = "field";
		final static private String XMLATTR_QUERY_FIELDNAME = "queryFldName";
		final static private String XMLATTR_DISPLAYNAME = "displayName";
		final static private String XMLATTR_JR_FIELDNAME = "jrFldName"; // если не указано - автоматически присваивается название @JR_COLNAMEPREFIX + N кол (от единицы), вида "COL_1", "COL_2" ...
		final static private String XMLATTR_JAVA_VALUECLASS = "javaValueClass";

	 */
		final static String JR_COLNAMEPREFIX = "COL_"; // префикс названий колонок для передачи в jasper

	@Override
	protected void setDefaults(Map<String, Object> defaults) {
		super.setDefaults(defaults);

		// "cmis"-section
		defaults.put( DSXMLProducer.XMLNODE_CMIS+ "/" + DSXMLProducer.XMLNODE_CMIS_URL, null);
		defaults.put( DSXMLProducer.XMLNODE_CMIS+ "/" + DSXMLProducer.XMLNODE_CMIS_USERNAME, null);
		defaults.put( DSXMLProducer.XMLNODE_CMIS+ "/" + DSXMLProducer.XMLNODE_CMIS_PASSWORD, null);

		// DEBUG
		// defaults.put( "A", null);
		// defaults.put( "query.descriptor", null);
		// defaults.put( "query.descriptor/BB", null);
		// defaults.put( "query.descriptor/queryText/CCC", null);
	}

	@Override
	public void clear() {
		super.clear();
	}

	/**
	 * @return название текущей конфигурации
	 */
	public String getConfigName() {
		return getstr(TAG_CONFIGNAME);
	}

	/**
	 * @param название текущей конфигурации
	 */
	public void setConfigName(String value) {
		getArgs().put( TAG_CONFIGNAME, value);
	}

	static final String PARAM_CMIS_XMLCONFIG = "CMIS_XMLCONFIG";
	static final String JRDS_CONFIG_ROOT = "/reportdefinitions/ds-config/";

	/**
	 * Задать параметры из params.
	 * Загрузить XML-конфигурацию, если она указана в params.
	 */
	public void setArgsByJRParams(Map<String, ?> params)
			throws JRException
		{
			if (params == null || params.isEmpty())
				return;

			// сначала пробуем загрузить конфу из xml (если есть такой параметр)
			if ( params.containsKey(PARAM_CMIS_XMLCONFIG)) {
				final Object jrparam = params.get(PARAM_CMIS_XMLCONFIG);
				if (jrparam == null)
					throw new RuntimeException( String.format("Paramter '%s' must be set", PARAM_CMIS_XMLCONFIG));
				final String configName = MacrosHelper.getJRParameterValue(jrparam); 
				getArgs().put(TAG_CONFIGNAME, configName);
				loadConfig();
				return;
			}

			// в родительском method будут браться параметры "CMIS_XXX" по-отдельности 
			super.setArgsByJRParams(params);
		}

	public boolean loadConfig() throws JRException {
		// setConfigName(configName); // boolean loadConfigByName( String configName);
		final String configName = this.getConfigName();
		if (configName == null || configName.length() == 0) { 
			return true; // empty config is ok 
		}
		try {
			final String confLocation = JRDS_CONFIG_ROOT + configName;
			//				final File f = new File(confLocation);
			//				if (!f.exists())
			//					throw new FileNotFoundException( String.format( "xml config '%s'", f.getAbsolutePath()));
			//				logger.info( String.format( "Loading xml config [%s] from file '%s'", configName, f.getAbsolutePath()));
			//				final InputStream fin = new FileInputStream(f);
			final URL reportDefinitionURL = JRLoader.getResource(confLocation);
			if (reportDefinitionURL == null)
				throw new JRException( String.format("Report config missed - file not found at '%s'", confLocation));
			final InputStream fin = JRLoader.getInputStream(reportDefinitionURL);
			if (fin == null) throw new JRException( String.format( "Config not found at '%s'", reportDefinitionURL));
			try {
				xmlRead( fin, String.format( "config from '%s'", configName) );
				return true; // ONLY HERE IS OK
			} finally {
				IOUtils.closeQuietly(fin);
			}
		} catch (Throwable ex) { // catch (IOException ex)
			final String msg = String.format( "Fail to load xml config from '%s'", configName);
			logger.error( msg, ex);
			// throw new RuntimeException( msg, ex);// TODO: (?) иметь параметр silentExceptions : boolean 
		}
		return false; // if errors
	}

	/**
	 * @param xml 
	 */
/* 
 * XML Example:
<?xml version="1.0" encoding="UTF-8"?>

<ds.config>

	<cmis.connection>
		<url></url>
		<username></username>
		<password></password>
		<allVersions>false</allVersions>
	</cmis.connection>

	<fields>
		<field queryFldName="cmis:id" dislayName="" javaValueClass="Integer"/>
	</fields>

	<query.descriptor>
		<offset>0</offset>
		<limit>-1</limit>
		<pgsize>-1</pgsize>
		<queryText>
			<![CDATA[select * from lecm:documents $P{mdgfkahqf}
			]]>
		</queryText>	
	</query.descriptor>

</ds.config>
 */
	public void xmlRead(InputStream xml, String info) {
		logger.info("loading xml config: " + info);

		// this.clear(); очистит всё, НО (!) здесь не надо сбрасывать название конфигурации

		setDefaults(getArgs());

		if (xml == null)
			return;

		try { 
//			final InputSource src = new InputSource(xml);
//			src.setEncoding("UTF-8");
//			logger.info("Encodig set as: "+ src.getEncoding());
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml);

			final Element rootElem = doc.getDocumentElement();
			if (!DSXMLProducer.XMLNODE_ROOT_DS_CONFIG.equals(rootElem.getNodeName())) {
				throw new RuntimeException("Root '" + DSXMLProducer.XMLNODE_ROOT_DS_CONFIG + "' element expected");
			}

			/* 
			 * загрузка аргументов по-умолчанию "зарегеных" в args:
			 * если в имени аргумента есть "|", то это принимается за уровень 
			 * вложенности внутри узла с таким именем, т.е. 
			 * 		"A" это просто узел верхнего уровня с названием "А";
			 * 		"А/Б" это узел второго уровня: первый это "А" и внутри него надо искать "Б".
			 * 
			 */
			if (getArgs() != null) {
				final List<String> names = new ArrayList<String>( getArgs().keySet());
				for(String argname: names) {
					setArgFromXmlNode(rootElem, argname, argname);
				}
			}

			// чтение мета-описаний полей ... 
			{
				final Element fieldsNode  = (Element) XmlHelper.getTagNode(rootElem, DSXMLProducer.XMLNODE_LIST_FIELDS, null, null);
				final List<Node> fieldsNodeList = XmlHelper.getNodesList(fieldsNode, DSXMLProducer.XMLNODE_FIELD, null, null);
				if (fieldsNodeList == null || fieldsNodeList.isEmpty()) {
					logger.warn( String.format( "xml %s contains no fields at %s/%s", info, DSXMLProducer.XMLNODE_LIST_FIELDS, DSXMLProducer.XMLNODE_FIELD));
				} else
					xmlGetMetaFields( fieldsNodeList);
			}

			logger.info( "xml loaded successfully from "+ info);

		} catch (Throwable t) {
			final String msg = "Problem loading " + info;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
	}


	/**
	 * Найти вложенный узел по имени и получить из него значение для аргумента.
	 * (!) XML-списки поддерживаются - списком будет считаться xml-узел, если в нём есть дочерний "<list>" или "<map>"
	 * Загруженный list или map стновится текущим значением соответствующего this.args(). 
	 * (!) Если таких вложенных узлов нет - текущее значение аргумента в this.args() НЕ ИЗМЕНЯЕТСЯ.
	 * (!) Если узел есть - значение изменяется (даже если оно будет null). 
	 * @param parentNode родительский xml-узел
	 * @param srcChildNodeTag название вложенного xml-узла:
	 * (!) если в аргумента есть "/", то это принимается за резделитель уровней
	 *     вложенности узлов, т.е. 
	 * 		"A" это просто узел с названием "А" первого уровня относительно parentNode;
	 * 		"А/Б" это узел второго уровня: первый это "А" и внутри него надо искать "Б";
	 * 		"А/Б/В" это тройная вложенность и т.д.
	 * 
	 * @param destArgName название аргумента
	 * @return значение аргумента (строка или список) или null, если нет 
	 * вложенного childNodeTag или в нём пустое значение.
	 */
	private Object setArgFromXmlNode( Node parentNode, String srcChildNodeTag, String destArgName) {
		if (srcChildNodeTag == null)
			return null;

		final String[] simpleNodeTags = srcChildNodeTag.split("/");
		if (simpleNodeTags == null || simpleNodeTags.length == 0)
			return null;

		Object result = null;
		Node curNode = parentNode;
		for (int i = 0; i < simpleNodeTags.length; i++) {
			curNode = (Element) XmlHelper.getTagNode(curNode, simpleNodeTags[i], null, null);
			if (curNode == null) {
				logger.warn( String.format( "XML did not contain items deeper than '%s' for full path '%s' -> got as NULL"
						, concat(simpleNodeTags, i)
						, concat(simpleNodeTags, simpleNodeTags.length)
						));
				return null;
			}
		}
		if (curNode != null) {
			result = getXmlNodeValue(curNode);
			getArgs().put( destArgName, result);
		}

		return result;
	}

	private static String concat(String[] items, int len) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < len; i++) {
			result.append('/').append( items[i] );
		}
		return result.toString();
	}

	private Object getXmlNodeValue(Node node) {
		Object result = null;
		if (node != null) {
			final Element vListNode = (node.hasChildNodes()) 
					? (Element) XmlHelper.getTagNode(node, "list", null, null)
					: null;
			final Element vMapNode = (node.hasChildNodes()) 
							? (Element) XmlHelper.getTagNode(node, "map", null, null)
							: null;
			if (vListNode != null) { // загружаем список ...
				result = getXmlList(vListNode);
			} else if (vMapNode != null) { // загружаем мапу ...
				result = getXmlMap(vMapNode);
			} else { // принимаем что узел содержит строку
				final String data = XmlHelper.getTagContent(node);
				result = (data == null ? null : data.trim());
			}
		}
		return result;
	}

	private List<Object> getXmlList(Node listNode) {
		if (listNode == null)
			return null;
		final List<Object> result = new ArrayList<Object>();
		final NodeList children = listNode.getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				final Node childNode = children.item(i); 
				if (!"item".equalsIgnoreCase(childNode.getNodeName()) ) // skip non-item elements
					continue;
				result.add( getXmlNodeValue(childNode));
			} // for
		}
		return result;
	}

	private Map<String, Object> getXmlMap(Node mapNode) {
		if (mapNode == null)
			return null;
		final Map<String, Object> result = new HashMap<String, Object>();
		final NodeList children = mapNode.getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				final Node childNode = children.item(i);
				if (!"item".equalsIgnoreCase(childNode.getNodeName()) ) // skip non-item elements
					continue;
				final Object value = getXmlNodeValue(childNode);

				final Node keyNode = (childNode.getAttributes() == null) ? null : childNode.getAttributes().getNamedItem("key");
				if (keyNode == null) {
					final String info = String.format("XML map-node '%s'::'%s' has no 'key' attribute for item [%s]", mapNode.getNamespaceURI(), mapNode.getNodeName(), i);
					logger.error(info);
					throw new RuntimeException( info);
				}

				result.put( XmlHelper.getTagContent(keyNode), value);
			} // for
		}
		return result;
	}

	/** Набор стандартных названий атрибутов */
	final static Set<String> STD_XML_ARGS = new HashSet<String>( Arrays.asList( 
			  DSXMLProducer.XMLATTR_JR_FLDNAME
			, DSXMLProducer.XMLATTR_QUERY_FLDNAME
			, DSXMLProducer.XMLATTR_DISPLAYNAME
			, DSXMLProducer.XMLATTR_JAVACLASS 
		));

	private void xmlGetMetaFields(List<Node> fieldsNodes) {
		if (fieldsNodes == null || fieldsNodes.isEmpty()) {
			return;
		}

		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Node node: fieldsNodes) {
			i++;
			final Element fldNode = (Element) node;

			// FIELD_QUERY_NAME
			String jrFldname = "COL_"+ i; // default name for any field will be simple "col_nn"
			if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_JR_FLDNAME)) {
				final String sname = fldNode.getAttribute(DSXMLProducer.XMLATTR_JR_FLDNAME);
				if (sname != null && sname.length() > 0)
					jrFldname = sname;
			}
			// корректировка названия колонки для гарантии уникальности имени 
			{
				String nameUnique = jrFldname;
				int unique = 0;
				while(this.getArgs().containsKey(nameUnique)) { // название вида "ABC_n" появится только при неуникальности
					unique++; // (!) нумерация колонок от единицы
					nameUnique = jrFldname+ "_"+ unique;
				}
				if (unique > 0) 
					logger.warn( String.format("Unique field name generated as '%s' (for base name '%s')", nameUnique, jrFldname));
				jrFldname = nameUnique;
			}
			// добавление новой jr-колонки
			final DataFieldColumn fld = this.addField(jrFldname);

			if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_QUERY_FLDNAME)) {
				final String queryFldName = fldNode.getAttribute(DSXMLProducer.XMLATTR_QUERY_FLDNAME);
				if (queryFldName != null && queryFldName.length() > 0)
					fld.setValueLink( queryFldName);
			}

			// DISPLAY_NAME
			if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_DISPLAYNAME)) {
				final String displayName = fldNode.getAttribute(DSXMLProducer.XMLATTR_DISPLAYNAME);
				fld.setDescription(displayName);
			}

			// JAVA_CLASS
			Class<?> javaClass = String.class; // default class
			if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_JAVACLASS)) {
				final String vClazz = fldNode.getAttribute(DSXMLProducer.XMLATTR_JAVACLASS);
				javaClass = getJavaClassByName( vClazz, javaClass);
			}
			fld.setValueClass(javaClass);
	
			// подгрузка остальных атрибутов ...
			takeAttributes( fld, fldNode.getAttributes(), STD_XML_ARGS);

			// журналирование
			if (logger.isDebugEnabled())
				sb.append( String.format( "got column/field %s: %s/%s [%s] '%s'"
					, i, fld.getName(), fld.getValueLink(), fld.getValueClass(), fld.getDescription())); 

		} //for

		if (logger.isInfoEnabled()) {
			sb.append( String.format( "found fields: %s", i)); 
			logger.info( sb.toString());
		}
	}


	/**
	 * Получение значений атрибутов в список флагов объекта destFld из мапы src
	 * @param dest целевой объект
	 * @param src исходный список атрибутов
	 * @param stdSkipArgs названия атрибутов, которые надо (!) пропускать
	 */
	private void takeAttributes( DataFieldColumn dest, NamedNodeMap src,
			Set<String> stdSkipArgs) {
		if (src == null)
			return;
		for( int ind = 0; ind < src.getLength(); ind++) {
			final Node n = src.item(ind);
			if (n == null) continue;
			// фильтра нет или значение не фильтруется
			final boolean isStdName = (stdSkipArgs != null) && stdSkipArgs.contains(n.getNodeName());
			if (!isStdName)
				dest.addFlag(n.getNodeName(), n.getNodeValue());
		}
	}

	// для возможности задавать типы алиасами
	private static Map<String, Class<?>> knownTypes = null;

	/**
	 * По короткому названию класса вернуть известный класс или null, если такого не зарегистрировано.
	 * @param vClazzAlias проверяемый алиас класса
	 * @return 
	 */
	private static Class<?> findKnownType(String vClazzAlias) {
		if (vClazzAlias == null)
			return null;

		if (knownTypes == null) {
			knownTypes = new HashMap<String, Class<?>>();

			// TODO: (RUSA) вынести всё это в бины

			knownTypes.put("integer", Integer.class);
			knownTypes.put("int", Integer.class);

			knownTypes.put("bool", Boolean.class);
			knownTypes.put("boolean", Boolean.class);
			// knownTypes.put("yesno", Boolean.class);
			// knownTypes.put("logical", Boolean.class);

			knownTypes.put("long", Long.class);
			knownTypes.put("longint", Long.class);

			knownTypes.put("id", String.class);
			knownTypes.put("string", String.class);

			knownTypes.put("date", Date.class);

			knownTypes.put("numeric", Number.class);
			knownTypes.put("number", Number.class);
			knownTypes.put("float", Float.class);
			knownTypes.put("double", Double.class);
		}

		final String skey = vClazzAlias.toLowerCase();
		return (knownTypes.containsKey(skey)) ? knownTypes.get(skey) : null;
	}

	/**
	 * По  названию класса вернуть известный класс или defaultClass.
	 * @param vClazzOrAlias проверяемое название класса - полное имя типа или алиас
	 * @return 
	 */
	public static Class<?> getJavaClassByName( final String vClazz, final Class<?> defaultClass)
	{
		Class<?> byAlias = findKnownType(vClazz);
		if (byAlias != null)
			return byAlias;

		// не найдено синононима -> ищем по полному имени
		if (vClazz != null) {
			try {
				return Class.forName(vClazz);
			} catch (ClassNotFoundException ex) {
				// ignore class name fail by default
				logger.warn( String.format( "Unknown java class '%s' -> used as '%s'", vClazz, defaultClass));
			}
		}

		return defaultClass;
	}

}
