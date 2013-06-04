package ru.it.lecm.reports.jasper.config;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ru.it.lecm.reports.api.JRXField;
import ru.it.lecm.reports.jasper.utils.MacrosHelper;
import ru.it.lecm.reports.jasper.utils.XmlHelper;

/**
 * Реализация для чтения конфы из XML
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
	protected void setDefaults() {
		super.setDefaults();
		// final Map<String, >

		getArgs().put( XMLTAG_URL, null);
		getArgs().put( XMLTAG_USERNAME, null);
		getArgs().put( XMLTAG_PSW, null);

		getArgs().put( XMLTAG_QUERY, null);
		getArgs().put( XMLTAG_ALLVER, null);
	}

	/**
	 * @return название текущей конфигурации
	 */
	public String getConfigName() {
		return getArgs().get(JRDSConfigXML.TAG_CONFIGNAME);
	}

	/**
	 * @param название текущей конфигурации
	 */
	public void setConfigName(String value) {
		getArgs().put( JRDSConfigXML.TAG_CONFIGNAME, value);
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

		setDefaults();

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

			// чтение запроса... 
			setArgFromXmlNode(rootElem, XMLTAG_QUERY, XMLTAG_QUERY);

			// чтение URL ... 
			setArgFromXmlNode(rootElem, XMLTAG_URL, XMLTAG_URL);

			// чтение USER ... 
			setArgFromXmlNode(rootElem, XMLTAG_USERNAME, XMLTAG_USERNAME);

			// чтение PASSWORD ... 
			setArgFromXmlNode(rootElem, XMLTAG_PSW, XMLTAG_PSW);

			// чтение ALLVERSIONS ... 
			setArgFromXmlNode(rootElem, XMLTAG_ALLVER, XMLTAG_ALLVER);

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
	 * Найти вложенный узел по имени и получить занчение для аргумента 
	 * @param parentNode родительский узел
	 * @param childNodeTag название вложенного узла
	 * @param argName название аргумента
	 * @return значение аргумента или null, если нет такого вложенного или в нём пустое значение
	 */
	private String setArgFromXmlNode( Node parentNode, String childNodeTag, String argName) {
		String result = null;
		final Element vNode = (Element) XmlHelper.getTagNode(parentNode, childNodeTag, null, null);
		if (vNode != null) {
			result = XmlHelper.getTagContent(vNode);
			getArgs().put( argName, (result == null ? null : result.trim()) );
		}
		return result;
	}


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
			String jrFldname = "COL_"+ i; // default name for any field will be simple
			if (fldNode.hasAttribute(XMLATTR_JR_FIELDNAME)) {
				final String sname = fldNode.getAttribute(XMLATTR_JR_FIELDNAME);
				if (sname != null && sname.length() > 0)
					jrFldname = sname;
			}
			// корректировка названия колонки для уникальности имени 
			{
				String nameUnique = jrFldname;
				for(int unique = 1; this.getArgs().containsKey(nameUnique); unique++) {
					nameUnique = jrFldname+ "_"+ unique; // название вида "ABC_n" появится только при неуникальности
				}
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
	
			if (logger.isDebugEnabled())
				sb.append( String.format( "got column/field %s: %s/%s [%s] '%s'"
					, i, fld.getName(), fld.getValueLink(), fld.getValueClass(), fld.getDescription())); 

		} //for

		if (logger.isInfoEnabled()) {
			sb.append( String.format( "found fields: %s", i)); 
			logger.info( sb.toString());
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
