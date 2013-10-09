package ru.it.lecm.reports.jasper.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jasperreports.engine.JRException;

import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.jasper.utils.MacrosHelper;
import ru.it.lecm.reports.xml.DSXMLProducer;
import ru.it.lecm.reports.xml.XmlHelper;

/**
 * Реализация для чтения конфы args из XML.
 * (!) При загрузке XML автоматом читаются:
 * 1) Аргументы, перечисленные в getArgs (список задётся в setDefaults или может быть расширен провайдером)
 * 2) Список метаописаний "fields.jasper", элементы "field", + атрибуты в каждом field.
 * 3) Параметры из списка defaults (загружаются как отдельные блоки <xxx> ... </xxx>)
 * 4) (!) Другие данные из XML игнорируются.
 *
 * @author rabdullin
 */
public class JRDSConfigXML extends JRDSConfigBaseImpl {

    final static Logger logger = LoggerFactory.getLogger(JRDSConfigXML.class);

    // параметр в this.args с названием файла XML конфигурации
    final static public String TAG_CONFIGNAME = "xmlconfigName";

    /**
     * Call-back для реакции на загрузку конфы или для выполнения сложной
     * прогрузки конфы
     *
     * @author rabdullin
     */
    public interface ConfigListener {
        /**
         * Загрузить из указанного узла
         *
         * @param rootElem
         * @param info     инфорация по читаемому потоку
         */
        void onLoad(Element rootElem, String info);
    }

    private ReportsManager reportManager;
    private Set<ConfigListener> cfgListeners;

    /**
     * Создание конфигуратора, с загрузкой необходимых конфигурационных файлов из указанного хранилища
     *
     * @param mgr
     */
    public JRDSConfigXML(ReportsManager mgr) {
        super();
        this.reportManager = mgr;
    }

	/* Fields Descriptors - look at DSXMLProducer */

    final static String JR_COLNAMEPREFIX = "COL_"; // префикс названий колонок для передачи в jasper

    @Override
    protected void setDefaults(Map<String, Object> defaults) {
        super.setDefaults(defaults);

        // "cmis"-section
        defaults.put(DSXMLProducer.XMLNODE_CMIS + "/" + DSXMLProducer.XMLNODE_CMIS_URL, null);
        defaults.put(DSXMLProducer.XMLNODE_CMIS + "/" + DSXMLProducer.XMLNODE_CMIS_USERNAME, null);
        defaults.put(DSXMLProducer.XMLNODE_CMIS + "/" + DSXMLProducer.XMLNODE_CMIS_PASSWORD, null);
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
     * @param value текущей конфигурации
     */
    public void setConfigName(String value) {
        getArgs().put(TAG_CONFIGNAME, value);
    }

    public ReportsManager getReportManager() {
        return reportManager;
    }

    public void setReportManager(ReportsManager reportMgr) {
        this.reportManager = reportMgr;
    }

    static final String PARAM_CMIS_XMLCONFIG = "CMIS_XMLCONFIG";

    /**
     * Задать параметры из params.
     * Загрузить XML-конфигурацию, если она указана в params.
     */
    public void setArgsByJRParams(Map<String, ?> params) throws JRException {
        if (params == null || params.isEmpty()) {
            return;
        }

        // сначала пробуем загрузить конфу из xml (если есть такой параметр)
        if (params.containsKey(PARAM_CMIS_XMLCONFIG)) {
            final Object jrparam = params.get(PARAM_CMIS_XMLCONFIG);
            if (jrparam == null) {
                throw new RuntimeException(String.format("Paramter '%s' cannot be empty or must be absent", PARAM_CMIS_XMLCONFIG));
            }
            final String configName = MacrosHelper.getJRParameterValue(jrparam);
            getArgs().put(TAG_CONFIGNAME, configName);
            loadConfig();
            return;
        }

        // в родительском method будут браться параметры "CMIS_XXX" по-отдельности
        super.setArgsByJRParams(params);
    }

    public boolean loadConfig() throws JRException {
        PropertyCheck.mandatory(this, "reportManager", getReportManager());

        final String configName = this.getConfigName();
        if (configName == null || configName.length() == 0) {
            return true; // empty config is ok
        }
        try {
            final byte[] data = this.getReportManager().loadDsXmlBytes(DSXMLProducer.extractReportName(configName));
            if (data == null) {
                throw new JRException(String.format("DS-config not found for report '%s'", configName));
            }
            final InputStream fin = new ByteArrayInputStream(data);
            try {
                xmlRead(fin, String.format("config from '%s'", configName));
                return true; // ONLY HERE IS OK
            } finally {
                IOUtils.closeQuietly(fin);
            }
        } catch (Throwable ex) { // catch (IOException ex)
            final String msg = String.format("Fail to load xml config from '%s'", configName);
            logger.error(msg, ex);
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
        setDefaults(getArgs());

        if (xml == null) {
            return;
        }

        try {
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
                final List<String> names = new ArrayList<String>(getArgs().keySet());
                for (String argname : names) {
                    setArgFromXmlNode(rootElem, argname, argname);
                }
            }

            // чтение мета-описаний полей ...
            xmlGetMetaFields(rootElem, info);

            // call-back
            notifyXmlConfigListeners(rootElem, info);

        } catch (Throwable t) {
            final String msg = "Problem loading " + info;
            logger.error(msg, t);
            throw new RuntimeException(msg, t);
        }
    }


    private void notifyXmlConfigListeners(Element rootElem, String info) {
        if (cfgListeners != null) {
            for (ConfigListener conf : cfgListeners) {
                conf.onLoad(rootElem, info);
            } // for
        }
    }

    public void addListener(ConfigListener listener) {
        if (listener == null) {
            return;
        }

        if (this.cfgListeners == null) {
            this.cfgListeners = new HashSet<ConfigListener>(1);
        }
        this.cfgListeners.add(listener);
    }


    public void remListener(ConfigListener listener) {
        if ((listener != null) && (this.cfgListeners != null)) {
            this.cfgListeners.remove(listener);
        }
    }

    /**
     * Найти вложенный узел по имени и получить из него значение для аргумента.
     * (!) XML-списки поддерживаются - списком будет считаться xml-узел, если в нём есть дочерний "<list>" или "<map>"
     * Загруженный list или map становится текущим значением соответствующего this.args().
     * (!) Если таких вложенных узлов нет - текущее значение аргумента в this.args() НЕ ИЗМЕНЯЕТСЯ.
     * (!) Если узел есть - значение изменяется (даже если оно будет null).
     *
     * @param parentNode      родительский xml-узел
     * @param srcChildNodeTag название вложенного xml-узла:
     *                        (!) если в аргумента есть "/", то это принимается за резделитель уровней
     *                        вложенности узлов, т.е.
     *                        "A" это просто узел с названием "А" первого уровня относительно parentNode;
     *                        "А/Б" это узел второго уровня: первый это "А" и внутри него надо искать "Б";
     *                        "А/Б/В" это тройная вложенность и т.д.
     * @param destArgName     название аргумента
     * @return значение аргумента (строка или список) или null, если нет
     *         вложенного childNodeTag или в нём пустое значение.
     */
    private Object setArgFromXmlNode(Node parentNode, String srcChildNodeTag, String destArgName) {
        if (srcChildNodeTag == null) {
            return null;
        }

        final String[] simpleNodeTags = srcChildNodeTag.split("/");
        if (simpleNodeTags == null || simpleNodeTags.length == 0) {
            return null;
        }

        Object result = null;
        Node curNode = parentNode;
        for (int i = 0; i < simpleNodeTags.length; i++) {
            curNode = XmlHelper.findNodeByAttr(curNode, simpleNodeTags[i], null, null);
            if (curNode == null) {
                logger.warn(String.format("XML node '%s' did not contain items deeper than '%s' for full path '%s' -> got as NULL"
                        , curNode, concat(simpleNodeTags, i)
                        , concat(simpleNodeTags, simpleNodeTags.length)
                ));
                return null;
            }
        }
        if (curNode != null) {
            result = XmlHelper.getNodeAsSmart(curNode);
            getArgs().put(destArgName, result);
        }

        return result;
    }

    private static String concat(String[] items, int len) {
        final StringBuilder result = new StringBuilder();
        if (len == 0) {
            result.append(String.format("/(%s)", len));
        } else {
            for (int i = 0; i < len; i++) {
                result.append(String.format("/(%s)", i)).append(items[i]);
            }
        }
        return result.toString();
    }

    private void xmlGetMetaFields(Element rootElem, String info) {
        final List<ColumnDescriptor> found = new ArrayList<ColumnDescriptor>(5);
        DSXMLProducer.parseColumns(found, rootElem, info);
        for (ColumnDescriptor column : found) {
            // добавление новой jr-колонки
            final DataFieldColumn fld = DataFieldColumn.createDataField(column);
            addField(fld);
        } // for
    }
}
