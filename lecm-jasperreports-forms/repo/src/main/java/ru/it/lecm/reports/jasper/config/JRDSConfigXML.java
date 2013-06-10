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

import ru.it.lecm.reports.api.JRXField;
import ru.it.lecm.reports.jasper.utils.MacrosHelper;
import ru.it.lecm.reports.jasper.utils.XmlHelper;

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

	final static public String XMLTAG_URL = "url";
	final static public String XMLTAG_USERNAME = "username";
	final static public String XMLTAG_PSW = "password";

	final static public String XMLTAG_QUERY = "query";
	final static public String XMLTAG_ALLVER = "allVersions";

	/* Fields Descriptors */
	final static private String XMLTAG_LIST_FIELDS = "fields.jasper";
	final static private String XMLTAG_FIELD = "field";
		final static private String XMLATTR_QUERY_FILEDNAME = "queryFldName";
		final static private String XMLATTR_DISPLAYNAME = "displayName";
		final static private String XMLATTR_JR_FIELDNAME = "jrFldName"; // если не указано - автоматически присваивается название @JR_COLNAMEPREFIX + N кол (от единицы), вида "COL_1", "COL_2" ...
		final static private String XMLATTR_JAVA_VALUECLASS = "javaValueClass";
		final static String JR_COLNAMEPREFIX = "COL_"; // префикс названий колонок для передачи в jasper

	@Override
	protected void setDefaults(Map<String, Object> defaults) {
		super.setDefaults(defaults);

		defaults.put( XMLTAG_QUERY, null);
		defaults.put( XMLTAG_URL, null);
		defaults.put( XMLTAG_ALLVER, null);

		defaults.put( XMLTAG_USERNAME, null);
		defaults.put( XMLTAG_PSW, null);
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

	final static private String XMLTAG_ROOT = "cmis-ds.config";

	/**
	 * @param xml 
	 */
/* 
 * XML Example:
<?xml version="1.0" encoding="UTF-8"?>

<cmis-ds.config>

	<url></url>

	<username></username>

	<password></password>

	<fields.jasper>
		<jrfield queryFldName="cmis:id" dislayName="" javaValueClass="Integer"/>
	</fields.jasper>

	<allVersions>false</allVersions>

	<query>
		<![CDATA[
			select * from lecm:documents $P{mdgfkahqf}
		]]>
	</query>	

</cmis-ds.config>
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
			if (!XMLTAG_ROOT.equals(rootElem.getNodeName())) {
				throw new RuntimeException("Root '" + XMLTAG_ROOT + "' element expected");
			}

			// загрузка аргументов по-умолчанию "зарегеных" в args
			if (getArgs() != null) {
				final List<String> names = new ArrayList<String>( getArgs().keySet());
				for(String argname: names) {
					setArgFromXmlNode(rootElem, argname, argname);
				}
			}

			// чтение мета-описаний полей ... 
			{
				final Element fieldsNode  = (Element) XmlHelper.getTagNode(rootElem, XMLTAG_LIST_FIELDS, null, null);
				final List<Node> fieldsNodeList = XmlHelper.getNodesList(fieldsNode, XMLTAG_FIELD, null, null);
				if (fieldsNodeList == null || fieldsNodeList.isEmpty()) {
					logger.warn( String.format( "xml %s contains no fields at %s/%s", info, XMLTAG_LIST_FIELDS, XMLTAG_FIELD));
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
	 * (!) Списки поддерживаются  - списком будет считаться атрибут, если в нём есть узел "<list>"
	 * (!) Если узла с таким именем нет - текущее значение аргумента НЕ ИЗМЕНЯЕТСЯ.
	 * (!) Если узел есть - значение изменяется (даже если оно будет null). 
	 * @param parentNode родительский узел
	 * @param srcChildNodeTag название вложенного узла
	 * @param destArgName название аргумента
	 * @return значение аргумента (строка или список) или null, если нет 
	 * вложенного childNodeTag или в нём пустое значение.
	 */
	private Object setArgFromXmlNode( Node parentNode, String srcChildNodeTag, String destArgName) {
		Object result = null;
		final Element vNode = (Element) XmlHelper.getTagNode(parentNode, srcChildNodeTag, null, null);
		if (vNode != null) {
			result = getXmlNodeValue(vNode);
			getArgs().put( destArgName, result);
		}
		return result;
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
	final static Set<String> STD_SML_ARGS = new HashSet<String>( Arrays.asList( XMLATTR_JR_FIELDNAME, XMLATTR_QUERY_FILEDNAME, XMLATTR_DISPLAYNAME, XMLATTR_JAVA_VALUECLASS));

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
			if (fldNode.hasAttribute(XMLATTR_JR_FIELDNAME)) {
				final String sname = fldNode.getAttribute(XMLATTR_JR_FIELDNAME);
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
			final JRXField fld = this.addField(jrFldname);

			if (fldNode.hasAttribute(XMLATTR_QUERY_FILEDNAME)) {
				final String queryFldName = fldNode.getAttribute(XMLATTR_QUERY_FILEDNAME);
				if (queryFldName != null && queryFldName.length() > 0)
					fld.setValueLink( queryFldName);
			}

			// DISPLAY_NAME
			if (fldNode.hasAttribute(XMLATTR_DISPLAYNAME)) {
				final String displayName = fldNode.getAttribute(XMLATTR_DISPLAYNAME);
				fld.setDescription(displayName);
			}

			// JAVA_CLASS
			Class<?> javaClass = String.class; // default class
			if (fldNode.hasAttribute(XMLATTR_JAVA_VALUECLASS)) {
				final String vClazz = fldNode.getAttribute(XMLATTR_JAVA_VALUECLASS);
				javaClass = getJavaClassByName( vClazz, javaClass);
			}
			fld.setValueClass(javaClass);
	
			// подгрузка остальных атрибутов ...
			takeAttributes( fld, fldNode.getAttributes(), STD_SML_ARGS);

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
	private void takeAttributes( JRXField dest, NamedNodeMap src,
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
