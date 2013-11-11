package ru.it.lecm.reports.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.*;
import ru.it.lecm.reports.api.model.*;
import ru.it.lecm.reports.api.model.AlfrescoAssocInfo.AssocKind;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO.IdRContent;
import ru.it.lecm.reports.api.model.ParameterType.Type;
import ru.it.lecm.reports.api.model.SubReportDescriptor.ItemsFormatDescriptor;
import ru.it.lecm.reports.model.impl.*;
import ru.it.lecm.reports.utils.Utils;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Создание ds-xml с описанием мета-данных полей и запроса.
 *
 * @author rabdullin
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

    public static final String XMLNODE_PARAMETER = "parameter";
    public static final String XMLATTR_PARAM_LABEL1 = "label1";
    public static final String XMLATTR_PARAM_LABEL2 = "label2";

    public static final String XMLATTR_PARAM_BOUND1 = "bound1";
    public static final String XMLATTR_PARAM_BOUND2 = "bound2";

    /* параметры запроса в xml-секции "query" */
    public static final String XMLNODE_QUERYDESC = "query.descriptor";
    public static final String XMLNODE_QUERY_OFFSET = "offset";
    public static final String XMLNODE_QUERY_LIMIT = "limit";
    public static final String XMLNODE_QUERY_PGSIZE = "pgsize";
    public static final String XMLNODE_QUERY_TEXT = "queryText";
    public static final String XMLNODE_QUERY_ALLVERSIONS = "allVersions";
    public static final String XMLNODE_QUERY_MULTIROW = "isMultiRow";
    public static final String XMLNODE_QUERY_ISCUSTOM = "isCustom";
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
    public final static String XMLATTR_QUERY_FLDNAME = "queryFldName";
    public final static String XMLATTR_EXPRESSION = "expression";
    public final static String XMLATTR_DISPLAYNAME = "displayName";
    public final static String XMLATTR_ORDER = "order";

    public final static String XMLATTR_VALUE_JAVACLASS = "javaValueClass";
    public final static String XMLATTR_JAVACLASS = "javaClass";

    public static final String XMLATTR_PARAM_TYPE = "paramType";
    public static final String XMLATTR_PARAM_ALFRESCO_TYPE = "alfrescoType";
    public static final String XMLNODE_ALFRESCO_ASSOC = "alfrescoAssoc";
    public static final String XMLATTR_ALFRESCO_ASSOC_NAME = "assocTypeName";
    public static final String XMLATTR_ALFRESCO_ASSOC_KIND = "assocKind"; // 11, 1M, M1, MM

    /* подотчёты  SubReports */
    public static final String XMLNODE_LIST_SUBREPORTS = "subreports";
    public static final String XMLNODE_SUBREPORT = "subreport";
    public static final String XMLNODE_SUBLIST_SOURCE = "sublist.source";
    public static final String XMLNODE_SUBLIST_TYPE = "sublist.type";
    public static final String XMLNODE_SUBLIST_ITEM = "sublist.item";

    public static final String XMLATTR_SUBREPORT_NAME = "name";
    public static final String XMLATTR_DESTCOLUMN_NAME = "destColumnName";

    /**
     * название атрибута, в котором указан класс бина для элемента строки subreport
     */
    public static final String XMLATTR_BEANCLASS = "beanclass";

    /**
     * описание формата
     */
    public static final String XMLNODE_FORMAT = "format";

    /**
     * название блока для описания формата
     */
    public final static String XMLATTR_FORMAT_ITEMDELIMITER = "itemDelimiter";
    public final static String XMLATTR_FORMAT_IFEMPTY = "ifEmpty";

    /* Класс по-умолчанию для колонки */
    private final static String DEFAULT_COLUMN_JAVACLASS = String.class.getName();
    private final static String DEFAULT_COLUMN_ALFRESCO_CLASS = "d:text";

    /**
     * Создать контент ds-xml файл.
     * Сейчас изменяет секции '<field>' и '<property name="dataSource" value="java-class">'.
     *
     * @param streamName название потока (для информации в журнал)
     * @param desc       описание, которое надо сохранить
     * @return поток с обновлёнными данными
     */
    public static ByteArrayOutputStream xmlCreateDSXML(String streamName, ReportDescriptor desc) {
        if (desc == null) {
            return null;
        }
        logger.debug("producing ds-xml " + streamName);

        try {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            // создание корневого элемента
            final Element rootElem = XmlHelper.ensureRoot(doc, XMLNODE_ROOT_DS_CONFIG);
            if (!XMLNODE_ROOT_DS_CONFIG.equals(rootElem.getNodeName())) { // for safety
                throw new RuntimeException("DS XML root '" + XMLNODE_ROOT_DS_CONFIG + "' element expected");
            }

			/* параметры xml-секции "report.descriptor" */
            final Element nodeRD = xmlCreateReportDescNode(doc, XMLNODE_REPORTDESC, desc);
            if (nodeRD != null) {
                rootElem.appendChild(nodeRD);
            }

			/* параметры запроса в xml-секции "query" */
            final Element nodeQuery = xmlCreateFlagsNode(doc, XMLNODE_QUERYDESC, desc.getFlags());
            if (nodeQuery != null) {
                rootElem.appendChild(nodeQuery);
            }

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

			/* параметры запроса в xml-секции "query" */
            // xmlAddSubReports( doc, XMLNODE_LIST_SUBREPORTS,
            // XMLNODE_SUBREPORT, desc.getDsDescriptor().getColumns());

			/*
			 * Колонки данных как ListOf<feild>
			 */
            if (desc.getDsDescriptor() != null) {
                xmlAddFieldsList(doc, rootElem, XMLNODE_LIST_FIELDS, XMLNODE_FIELD, desc.getDsDescriptor().getColumns());
            }

			/* Подотчёты */
            xmlAddSubreportsList(doc, rootElem, XMLNODE_LIST_SUBREPORTS, XMLNODE_SUBREPORT, desc.getSubreports());

			/* формирование результата */
            final ByteArrayOutputStream result = XmlHelper.serialize(doc);

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
     *
     * @param streamName название потока (для информации в журнал)
     * @return поток с обновлёнными данными
     */
    public static ReportDescriptor parseDSXML(InputStream data, final String streamName) {
        if (data == null) {

        }
        logger.debug("reading ds-xml " + streamName);

        try {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(data);

            // создание корневого элемента
            final Element rootElem = doc.getDocumentElement();
            if (!XMLNODE_ROOT_DS_CONFIG.equals(rootElem.getNodeName())) { // for safety
                throw new RuntimeException("DS XML root '" + XMLNODE_ROOT_DS_CONFIG + "' element expected at stream " + streamName);
            }

            final ReportDescriptorImpl result = new ReportDescriptorImpl();

			/* параметры xml-секции "report.descriptor" */
            parseReportDesc(result, rootElem, XMLNODE_REPORTDESC);
            if (result.getMnem() == null) { // название как суффикс в имени файла потока ...
                result.setMnem(extractReportName(streamName));
            }

			/* параметры запроса в xml-секции "query" */
            result.setFlags(parseReportFlags(rootElem, XMLNODE_QUERYDESC));

            // NOTE: когда появятся доп характеристики query внутри desc - сохранить их тут
			/* параметры cmis-соединения в xml-секции "cmis" */

            parseColumns(result.getDsDescriptor().getColumns(), rootElem, streamName);

            // подотчёты
            final List<SubReportDescriptor> subreports = parseSubreportsList(rootElem, XMLNODE_LIST_SUBREPORTS, XMLNODE_SUBREPORT);
            result.setSubreports(subreports);

            logger.debug("load SUCCESSFULL from ds-xml " + streamName);

            return result;
        } catch (Throwable t) {
            final String msg = "Problem loading ds-xml " + streamName;
            logger.error(msg, t);
            throw new RuntimeException(msg, t);
        }
    }

    private static Element xmlCreateReportDescNode(Document doc, String xmlNodeRDName, ReportDescriptor srcRDesc) {
        if (srcRDesc == null) {
            return null;
        }

        final Element result = XmlHelper.xmlCreateStdMnemoItem(doc, srcRDesc, xmlNodeRDName);

        // тип отчёта
        final Element nodeRType = xmlCreateReportTypeNode(doc, XMLNODE_REPORT_TYPE, srcRDesc.getReportType());
        if (nodeRType != null) {
            result.appendChild(nodeRType);
        }

        // провайдер
        final Element nodeProvider = xmlCreateReportProviderNode(doc, XMLNODE_REPORT_PROVIDER, srcRDesc.getProviderDescriptor());
        if (nodeProvider != null) {
            result.appendChild(nodeProvider);
        }

        // нативный шаблон отчёта
        final Element nodeTemplate = xmlCreateReportTemplateNode(doc, XMLNODE_REPORT_TEMPLATE, srcRDesc.getReportTemplate());
        if (nodeTemplate != null) {
            result.appendChild(nodeTemplate);
        }

        // набор данных ...
        final Element nodeDS = xmlCreateReportDSNode(doc, XMLNODE_REPORT_DS, srcRDesc.getDsDescriptor());
        if (nodeDS != null) {
            result.appendChild(nodeDS);
        }

        return result;
    }

    private static void parseReportDesc(ReportDescriptorImpl dest, Element srcRootElem, String xmlNodeNameReportDesc) {
        if (srcRootElem == null) {
            return;
        }

        final Element srcNode = XmlHelper.findNodeByName(srcRootElem, xmlNodeNameReportDesc);
        if (srcNode == null) {
            return;
        }

        XmlHelper.parseStdMnemoItem(dest, srcNode);

        // тип отчёта
        final Element nodeRType = XmlHelper.findNodeByName(srcNode, XMLNODE_REPORT_TYPE);
        dest.setReportType(parseReportType(nodeRType));

        // провайдер
        final Element nodeProvider = XmlHelper.findNodeByName(srcNode, XMLNODE_REPORT_PROVIDER);
        dest.setProviderDescriptor(parseProviderDescriptor(nodeProvider));

        // нативный шаблон отчёта
        final Element nodeTemplate = XmlHelper.findNodeByName(srcNode, XMLNODE_REPORT_TEMPLATE);
        dest.setReportTemplate(parseReportTemplate(nodeTemplate));

        // набор данных ...
        final Element nodeDS = XmlHelper.findNodeByName(srcNode, XMLNODE_REPORT_DS);
        dest.setDSDescriptor(parseDSDescriptor(nodeDS));
    }

    private static Element xmlCreateReportTypeNode(Document doc, String xmlNodeNameRType, ReportType rtypeDesc) {
        if (rtypeDesc == null) {
            return null;
        }
        return XmlHelper.xmlCreateStdMnemoItem(doc, rtypeDesc, xmlNodeNameRType);
    }

    private static ReportType parseReportType(Element srcNodeRType) {
        if (srcNodeRType == null) {
            return null;
        }

        final ReportTypeImpl result = new ReportTypeImpl();
        XmlHelper.parseStdMnemoItem(result, srcNodeRType);
        return result;
    }

    private static Element xmlCreateReportProviderNode(Document doc, String xmlNodeNameRP, ReportProviderDescriptor rpDesc) {
        if (rpDesc == null) {
            return null;
        }

        final Element result = XmlHelper.xmlCreateStdMnemoItem(doc, rpDesc, xmlNodeNameRP);
        XmlHelper.xmlAddClassNameAttr(result, rpDesc, XMLATTR_JAVACLASS, null);

        return result;
    }

    private static ReportProviderDescriptor parseProviderDescriptor(Element srcNodeProvider) {
        if (srcNodeProvider == null) {
            return null;
        }

        final ReportProviderDescriptorImpl result = new ReportProviderDescriptorImpl();
        XmlHelper.parseStdMnemoItem(result, srcNodeProvider);

        // java-класс провайдера ...
        final String javaClass = XmlHelper.getClassNameAttr(srcNodeProvider, XMLATTR_JAVACLASS, DEFAULT_COLUMN_JAVACLASS);
        result.setClassName(javaClass);

        return result;
    }

    private static Element xmlCreateReportTemplateNode(Document doc, String xmlNodeNameRT, ReportTemplate rtDesc) {
        if (rtDesc == null) {
            return null;
        }

        final Element result = XmlHelper.xmlCreateStdMnemoItem(doc, rtDesc, xmlNodeNameRT);
        if (rtDesc.getFileName() != null) {
            result.setAttribute(XMLATTR_FILENAME, rtDesc.getFileName());
        }

        return result;
    }

    private static ReportTemplate parseReportTemplate(Element srcNodeTemplate) {
        if (srcNodeTemplate == null) {
            return null;
        }

        final ReportTemplateImpl result = new ReportTemplateImpl();
        XmlHelper.parseStdMnemoItem(result, srcNodeTemplate);

        if (srcNodeTemplate.hasAttribute(XMLATTR_FILENAME)) {
            result.setFileName(srcNodeTemplate.getAttribute(XMLATTR_FILENAME));
        }

        return result;
    }

    private static Element xmlCreateReportDSNode(Document doc, String xmlNodeNameDS, DataSourceDescriptor dsDesc) {
        if (dsDesc == null) {
            return null;
        }

        // TODO: тут описания полей стоит включить, сейчас они включены уровнем выше

        return XmlHelper.xmlCreateStdMnemoItem(doc, dsDesc, xmlNodeNameDS);
    }

    private static DataSourceDescriptor parseDSDescriptor(Element srcNodeDS) {
        if (srcNodeDS == null) {
            return null;
        }

        final DataSourceDescriptorImpl result = new DataSourceDescriptorImpl();
        XmlHelper.parseStdMnemoItem(result, srcNodeDS);

        return result;
    }

    /**
     * Сформировать группу с описанием колонок:
     * <fields>
     * <field jrFldName="col_DocKind"
     * queryFldName="{lecm-contract:typeContract-assoc/cm:name}"
     * displayName="Вид договора"
     * inMainDoc="true"/>
     * ...
     * </fields>
     */
    private static void xmlAddFieldsList(Document doc, Element destRoot,
                                         String xmlNodeListName, String xmlNodeItemName,
                                         List<ColumnDescriptor> srcColumns) {
        final Element nodeFields = doc.createElement(xmlNodeListName);
        destRoot.appendChild(nodeFields);

        if (srcColumns == null || srcColumns.isEmpty()) {
            return;
        }

		/* вывод колонок ... */
        for (ColumnDescriptor cdesc : srcColumns) {
            final Element nodeColItem = xmlCreateColumnNode(doc, xmlNodeItemName, cdesc);
            nodeFields.appendChild(nodeColItem);
        }
    }

    /**
     * Формирование группы подотчётов:
     * <subreports>
     * <subreport name="xxx" destColumn="yyy"> ...
     * </subreport>
     * </subreports>
     */
    private static void xmlAddSubreportsList(Document doc, Element destRoot,
                                             String xmlNodeListName, String xmlNodeItemName,
                                             List<SubReportDescriptor> srcSubReports) {
        if (srcSubReports == null || srcSubReports.isEmpty()) {
            return;
        }

        final Element nodeFields = doc.createElement(xmlNodeListName);
        destRoot.appendChild(nodeFields);

		/* вывод колонок ... */
        for (SubReportDescriptor sdesc : srcSubReports) {
            final Element nodeColItem = xmlCreateSubreportNode(doc, xmlNodeItemName, sdesc);
            nodeFields.appendChild(nodeColItem);
        }
    }

    private static List<SubReportDescriptor> parseSubreportsList(Element srcRoot,
                                                                 String xmlNodeListName, String xmlNodeItemName) {
        // чтение мета-описаний полей ...
        final Element subreportsNode = (Element) XmlHelper.findNodeByAttr(srcRoot, xmlNodeListName, null, null);
        if (subreportsNode == null) {
            return null;
        }

        final List<Node> subreportsNodeList = XmlHelper.findNodesList(subreportsNode, xmlNodeItemName, null, null);
        return parseSubreports(subreportsNodeList);
    }

    private static List<SubReportDescriptor> parseSubreports(List<Node> subreportsNodeList) {
        if (subreportsNodeList == null || subreportsNodeList.isEmpty()) {
            return null;
        }

        final List<SubReportDescriptor> result = new ArrayList<SubReportDescriptor>();
        for (Node node : subreportsNodeList) {
            final SubReportDescriptorImpl desc = parseSubReportDescriptor((Element) node);
            if (desc != null) {
                result.add(desc);
            }
        }

        return result;
    }

    /**
     * Сохранение в xml атрибутов в списке флагов объекта destColumn
     *
     * @param result      целевой xml узел
     * @param srcFlags    исходный список атрибутов
     * @param stdSkipArgs названия атрибутов, которые надо (!) пропускать
     */
    private static void xmlAddFlagsAttributes(Document doc, Element result, Set<NamedValue> srcFlags, Set<String> stdSkipArgs) {
        if (srcFlags == null) {
            return;
        }
        for (NamedValue v : srcFlags) {
            final boolean isStdName = (stdSkipArgs != null) && stdSkipArgs.contains(v.getMnem());
            if (!isStdName) {
                XmlHelper.xmlCreatePlainNode(doc, result, v.getMnem(), v.getValue());
            }
        }
    }

    /**
     * Добавить групповой узел с флагами в виде подузлов:
     * <xmlFlagsGrpName>
     * <key1>value1</key1>
     * <key2>value2</key2>
     * ...
     * </xmlFlagsGrpName>
     *
     * @param xmlFlagsGrpName название группового узла
     * @param srcFlags        флаги
     * @param createEmptyToo  true, чтобы создавать узел даже если srcFlags пуст
     */
    private static Element xmlCreateFlagsAttributesGrp(Document doc, String xmlFlagsGrpName, Set<NamedValue> srcFlags, boolean createEmptyToo) {
        final Element result = doc.createElement(xmlFlagsGrpName);

        if (srcFlags == null || srcFlags.isEmpty()) {
            return (createEmptyToo) ? result : null;
        }

        xmlAddFlagsAttributes(doc, result, srcFlags, null);
        return result;
    }

    /**
     * Получение значений атрибутов в список флагов объекта destFld из мапы src
     *
     * @param destFlags   целевой набор флагов
     * @param src         исходный список атрибутов
     * @param stdSkipArgs названия пропускаемых атрибутов для списка флагов
     */
    private static void parseFlagsAttributes(Set<NamedValue> destFlags, NodeList src, Set<String> stdSkipArgs) {
        if (src == null) {
            return;
        }
        for (int i = 0; i < src.getLength(); i++) {
            final Node n = src.item(i);
            if (n == null) {
                continue;
            }
            // фильтра нет или значение не фильтруется
            final boolean isStdName = (stdSkipArgs != null) && stdSkipArgs.contains(n.getNodeName());
            if (!isStdName) {
                destFlags.add(new NamedValueImpl(n.getNodeName(), XmlHelper.getTagContent(n)));
            }
        } // for i
    }

    private static void parseFlagsAttributes(Set<NamedValue> destFlags, NamedNodeMap src, Set<String> stdSkipArgs) {
        if (src == null) {
            return;
        }
        for (int i = 0; i < src.getLength(); i++) {
            final Node n = src.item(i);
            if (n == null) {
                continue;
            }
            // фильтра нет или значение не фильтруется
            final boolean isStdName = (stdSkipArgs != null) && stdSkipArgs.contains(n.getNodeName());
            if (!isStdName) {
                destFlags.add(new NamedValueImpl(n.getNodeName(), XmlHelper.getTagContent(n)));
            }
        } // for i
    }

    private static Element xmlCreateSubreportNode(Document doc, String nodeName, SubReportDescriptor subreport) {
        if (subreport == null) {
            return null;
        }

        final Element result = doc.createElement(nodeName);

        result.setAttribute(XMLATTR_SUBREPORT_NAME, subreport.getMnem());
        result.setAttribute(XMLATTR_DESTCOLUMN_NAME, subreport.getDestColumnName());

        // save <list.source> as CDATA ... </>
        XmlHelper.xmlCreateCDataNode(doc, result, XMLNODE_SUBLIST_SOURCE, subreport.getSourceListExpression());

        if (subreport.getSourceListType() != null) {
            XmlHelper.xmlCreateCDataNode(doc, result, XMLNODE_SUBLIST_TYPE, subreport.getSourceListType());
        }

        // save <list.item beanClass="...">

        final Element listItem = doc.createElement(XMLNODE_SUBLIST_ITEM);
        result.appendChild(listItem);

        listItem.setAttribute(XMLATTR_BEANCLASS, subreport.getBeanClassName());

			/* отгрузка формата <format ... > */
        xmlCreateItemsFormat(doc, listItem, XMLNODE_FORMAT, subreport.getItemsFormat());

			/* отгрузка карты соответствий <map ...> для вложенных колонок ... */
        if (subreport.getSubItemsSourceMap() != null) {
            final Element mapNode = doc.createElement(XmlHelper.XMLNODE_MAP);
            listItem.appendChild(mapNode);
            XmlHelper.xmlAddMapItems(doc, mapNode, subreport.getSubItemsSourceMap());
        }


        // отгрузка обычных атрибутов ReportDescriptor ...
        xmlCreateReportDescNode(doc, XMLNODE_REPORT_DS, subreport);

        return result;
    }

    @SuppressWarnings({"unchecked"})
    private static SubReportDescriptorImpl parseSubReportDescriptor(Element srcNodeSubreport) {
        if (srcNodeSubreport == null) {
            return null;
        }

        final SubReportDescriptorImpl result = new SubReportDescriptorImpl();

        if (srcNodeSubreport.hasAttribute(XMLATTR_SUBREPORT_NAME)) {
            result.setMnem(Utils.trimmed(srcNodeSubreport.getAttribute(XMLATTR_SUBREPORT_NAME)));
        }

        if (srcNodeSubreport.hasAttribute(XMLATTR_DESTCOLUMN_NAME)) {
            result.setDestColumnName(Utils.trimmed(srcNodeSubreport.getAttribute(XMLATTR_DESTCOLUMN_NAME)));
        }

        // <list.source> as CDATA ... </>

        final String source = XmlHelper.findNodeChildValue(srcNodeSubreport, XMLNODE_SUBLIST_SOURCE);
        result.setSourceListExpression(Utils.trimmed(source));

        final String type = XmlHelper.findNodeChildValue(srcNodeSubreport, XMLNODE_SUBLIST_TYPE);
        result.setSourceListType(Utils.trimmed(type));

        if (Utils.isStringEmpty(result.getSourceListExpression()))
            logger.warn(String.format("(!?) Subreport '%s' xml-configured with empty association", result.getMnem()));


        // load <list.item beanClass="...">
        final Element nodeItem = XmlHelper.findNodeByName(srcNodeSubreport, XMLNODE_SUBLIST_ITEM);
        if (nodeItem != null) {
            if (nodeItem.hasAttribute(XMLATTR_BEANCLASS)) {
                result.setBeanClassName(nodeItem.getAttribute(XMLATTR_BEANCLASS));
            }

			/* формат <format ... > */
            final Element nodeFormat = XmlHelper.findNodeByName(nodeItem, XMLNODE_FORMAT);
            if (nodeFormat != null) {
                result.setItemsFormat(parseItemsFormat(nodeFormat));

                if (result.getItemsFormat() != null && !result.isUsingFormat()) {
                    logger.warn(String.format("Subreport '%s' has format when bean class is configured -> format ignored"
                            + "\n\t bean class using: '%s'\n\t format will be ignored ignored: '%s'"
                            , result.getMnem()
                            , result.getBeanClassName()
                            , result.getItemsFormat()
                    ));
                }
            }

			/* получение карты соответствий <map ...> для вложенных колонок ... */

            final Element nodeMap = XmlHelper.findNodeByName(nodeItem, XmlHelper.XMLNODE_MAP);
            if (nodeMap != null) {
                @SuppressWarnings("rawtypes")
                final Map map = XmlHelper.getNodeAsItemsMap(nodeMap);
                result.setSubItemsSourceMap(map);
            }
        }

        // отгрузка обычных атрибутов ReportDescriptor ...
        parseReportDesc(result, srcNodeSubreport, XMLNODE_REPORT_DS);

        return result;
    }

    private static Element xmlCreateItemsFormat(Document doc, Element parentNode, String nodeName, ItemsFormatDescriptor itemsFormat) {
        if (itemsFormat == null) {
            return null;
        }

        // save format string as CDATA
        final Element result = XmlHelper.xmlCreateCDataNode(doc, parentNode, nodeName, itemsFormat.getFormatString());

        // разделитель
        if (!Utils.isStringEmpty(itemsFormat.getItemsDelimiter())) {
            result.setAttribute(XMLATTR_FORMAT_ITEMDELIMITER, itemsFormat.getItemsDelimiter());
        }

        // обозначение для пустого списка ...
        if (!Utils.isStringEmpty(itemsFormat.getIfEmptyTag())) {
            result.setAttribute(XMLATTR_FORMAT_IFEMPTY, itemsFormat.getIfEmptyTag());
        }

        return result;
    }

    private static ItemsFormatDescriptor parseItemsFormat(Element srcNode) {
        if (srcNode == null) {
            return null;
        }

        final ItemsFormatDescriptorImpl result = new ItemsFormatDescriptorImpl();

        // строка форматирования свойств из CData или value ...
        result.setFormatString(Utils.dequote(Utils.trimmed(XmlHelper.getTagContent(srcNode))));

        // обозначение для пустого списка ...
        if (srcNode.hasAttribute(XMLATTR_FORMAT_IFEMPTY)) {
            result.setIfEmptyTag(srcNode.getAttribute(XMLATTR_FORMAT_IFEMPTY));
        }

        // разделитель элементов ...
        if (srcNode.hasAttribute(XMLATTR_FORMAT_ITEMDELIMITER)) {
            result.setItemsDelimiter(srcNode.getAttribute(XMLATTR_FORMAT_ITEMDELIMITER));
        }

        return result;
    }

    /**
     * Набор стандартных названий атрибутов
     */
    final static Set<String> STD_XML_FLD_ARGS = new HashSet<String>(Arrays.asList(
            XMLATTR_JR_FLDNAME
            , XMLATTR_QUERY_FLDNAME
            , XMLATTR_DISPLAYNAME
            , XMLATTR_JAVACLASS
    ));

    /**
     * Создание field-узла для колонки
     */
    private static Element xmlCreateColumnNode(Document doc, String nodeName, ColumnDescriptor column) {
        if (column == null) {
            return null;
        }

        final Element result = doc.createElement(nodeName);

        result.setAttribute(XMLATTR_JR_FLDNAME, column.getColumnName());
        result.setAttribute(XMLATTR_QUERY_FLDNAME, column.getExpression());
        result.setAttribute(XMLATTR_DISPLAYNAME, column.get("ru", ""));
        if (column.getOrder() != 0) {
            result.setAttribute(XMLATTR_ORDER, String.valueOf(column.getOrder()));
        }
        result.setAttribute(XMLATTR_PARAM_ALFRESCO_TYPE, column.getAlfrescoType() != null ? column.getAlfrescoType() : DEFAULT_COLUMN_ALFRESCO_CLASS);
        // DONE: save map-locale {column.getL18Items} like addL18Name( doc, result, column);
        XmlHelper.xmlAddL18Name(doc, result, column);

        // тип колонки ...
        XmlHelper.xmlAddClassNameAttr(result, column, XMLATTR_VALUE_JAVACLASS, DEFAULT_COLUMN_JAVACLASS);

        // result.setAttribute( XMLATTR_INMAINDOC, column.flags("inMainDoc"));

        // выгрузка остальных атрибутов ...
        xmlAddFlagsAttributes(doc, result, column.flags(), STD_XML_FLD_ARGS);

        // тип параметра для колонки ...

        final Element nodeParameter = xmlCreateParameterNode(doc, XMLNODE_PARAMETER, column.getParameterValue());
        if (nodeParameter != null) {
            result.appendChild(nodeParameter);
        }

        return result;
    }

    /**
     * Загрузить список метаописаний полей из указанного документа
     *
     * @param info название читаемого потока
     */
    public static void parseColumns(List<ColumnDescriptor> destColumns, Element srcRoot, String info) {
        destColumns.clear();

        // @param xmlNodeNameListFields название xml-группы с метаописаниями
        // @param xmlNodeNameField элементы внутри группы

        // чтение мета-описаний полей ...
        final Element fieldsNode = (Element) XmlHelper.findNodeByAttr(srcRoot, XMLNODE_LIST_FIELDS, null, null);
        final List<Node> fieldsNodeList = XmlHelper.findNodesList(fieldsNode, XMLNODE_FIELD, null, null);
        final List<ColumnDescriptor> newColumns = parseColumns(fieldsNodeList, info);
        if (newColumns != null) {
            destColumns.addAll(newColumns);
        }
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
                if (sname != null && sname.length() > 0) {
                    jrFldname = sname;
                }
            }

            // корректировка названия колонки для гарантии уникальности имени

            String nameUnique = jrFldname;
            int unique = 0;
            while (result.containsKey(nameUnique)) { // название вида "ABC_n" появится только при неуникальности
                unique++; // (!) нумерация колонок от единицы
                nameUnique = jrFldname + "_" + unique;
            }
            if (unique > 0) {
                logger.warn(String.format("Unique field name generated as '%s' (for base name '%s')", nameUnique, jrFldname));
            }
            jrFldname = nameUnique;

            // добавление новой jr-колонки
            final ColumnDescriptor column = new ColumnDescriptorImpl(jrFldname);
            result.put(column.getColumnName(), column);

            if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_QUERY_FLDNAME)) {
                final String queryFldName = fldNode.getAttribute(DSXMLProducer.XMLATTR_QUERY_FLDNAME);
                if (queryFldName != null && queryFldName.length() > 0) {
                    column.setExpression(queryFldName);
                }
            }

            // синоним "expression" для задания выражения (вместо "queryFldName") ...
            if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_EXPRESSION)) {
                final String queryFldName = fldNode.getAttribute(DSXMLProducer.XMLATTR_EXPRESSION);
                if (queryFldName != null && queryFldName.length() > 0) {
                    column.setExpression(queryFldName);
                }
            }

            // DISPLAY_NAME
            if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_DISPLAYNAME)) {
                final String displayName = fldNode.getAttribute(DSXMLProducer.XMLATTR_DISPLAYNAME);
                column.regItem("ru", displayName);
            }

            if (fldNode.hasAttribute(DSXMLProducer.XMLATTR_ORDER)) {
                final String sorder = fldNode.getAttribute(DSXMLProducer.XMLATTR_ORDER);
                if (!Utils.isStringEmpty(sorder)) {
                    column.setOrder(Integer.parseInt(sorder));
                }
            }

            // DONE: restore map-locale
            XmlHelper.parseL18(column, fldNode);

            // JAVA_CLASS тип колонки ...

            final String javaClass = XmlHelper.getClassNameAttr(fldNode, XMLATTR_VALUE_JAVACLASS, DEFAULT_COLUMN_JAVACLASS);
            column.setClassName(javaClass);


            final String alfrescoClass = XmlHelper.getClassNameAttr(fldNode, XMLATTR_PARAM_ALFRESCO_TYPE, DEFAULT_COLUMN_ALFRESCO_CLASS);
            column.setAlfrescoType(alfrescoClass);


            // подгрузка остальных атрибутов ...
            parseFlagsAttributes(column.flags(), fldNode.getAttributes(), STD_XML_FLD_ARGS);

            // тип параметра для колонки ...
            column.setParameterValue(parseParameterNode(fldNode, XMLNODE_PARAMETER));

        } // for

        return new ArrayList<ColumnDescriptor>(result.values());
    }

    private static Element xmlCreateParameterNode(Document doc, String xmlNodeName, ParameterTypedValue parameter) {
        if (parameter == null) {
            return null;
        }

        final Element result = doc.createElement(xmlNodeName);

        XmlHelper.xmlAddMnemAttr(result, parameter);

        if (parameter.getType() != null) {
            result.setAttribute(XMLATTR_PARAM_TYPE, parameter.getType().getMnemonic());
        }

		/* альфресковская ассоциация ... */

        final Element nodeAssoc = xmlCreateAssocNode(doc, XMLNODE_ALFRESCO_ASSOC, parameter.getAlfrescoAssoc());
        if (nodeAssoc != null)
            result.appendChild(nodeAssoc);


        XmlHelper.xmlAddL18Name(doc, result, parameter);
        XmlHelper.xmlAddL18Name(doc, result, parameter.getPrompt1(), XMLATTR_PARAM_LABEL1);
        XmlHelper.xmlAddL18Name(doc, result, parameter.getPrompt2(), XMLATTR_PARAM_LABEL2);

        parameter.setBound1(XmlHelper.getTagContent(doc, XMLATTR_PARAM_BOUND1, null, null));
        parameter.setBound2(XmlHelper.getTagContent(doc, XMLATTR_PARAM_BOUND2, null, null));

        return result;
    }

    private static Element xmlCreateAssocNode(Document doc, String xmlNodeName, AlfrescoAssocInfo assoc) {
        if (assoc == null) {
            return null;
        }

        final Element result = doc.createElement(xmlNodeName);

        if (assoc.getAssocTypeName() != null) {
            result.setAttribute(XMLATTR_ALFRESCO_ASSOC_NAME, assoc.getAssocTypeName());
        }
        if (assoc.getAssocKind() != null) {
            result.setAttribute(XMLATTR_ALFRESCO_ASSOC_KIND, assoc.getAssocKind().getMnemonic());
        }

        return result;
    }

    private static ParameterTypedValue parseParameterNode(Element srcColumnNode, String xmlNodeName) {
        if (srcColumnNode == null) {
            return null;
        }

        final Element nodeParameter = XmlHelper.findNodeByName(srcColumnNode, xmlNodeName);
        if (nodeParameter == null) {
            return null;
        }

        final ParameterTypedValueImpl result = new ParameterTypedValueImpl();
        XmlHelper.parseMnemAttr(result, nodeParameter);

		/* альфресковская ассоциация ... */
        result.setAlfrescoAssoc(parseAssocNode(nodeParameter, XMLNODE_ALFRESCO_ASSOC));

         /* тип параметра */
        Type parType = null;
        if (nodeParameter.hasAttribute(XMLATTR_PARAM_TYPE)) {
            parType = Type.findType(nodeParameter.getAttribute(XMLATTR_PARAM_TYPE));
        }
        result.setType(parType);


        XmlHelper.parseL18(result, nodeParameter);
        XmlHelper.parseL18(result.getPrompt1(), nodeParameter, XMLATTR_PARAM_LABEL1);
        XmlHelper.parseL18(result.getPrompt2(), nodeParameter, XMLATTR_PARAM_LABEL2);

        result.setBound1(XmlHelper.getTagContent(nodeParameter, XMLATTR_PARAM_BOUND1, null, null));
        result.setBound2(XmlHelper.getTagContent(nodeParameter, XMLATTR_PARAM_BOUND2, null, null));
        return result;
    }

    private static AlfrescoAssocInfoImpl parseAssocNode(Element srcAssocNode, String xmlNodeName) {
        if (srcAssocNode == null) {
            return null;
        }

        final Element nodeAssoc = XmlHelper.findNodeByName(srcAssocNode, xmlNodeName);
        if (nodeAssoc == null) {
            return null;
        }

        final AlfrescoAssocInfoImpl result = new AlfrescoAssocInfoImpl();

		/* альфресковский ассоциация параметра */
        if (nodeAssoc.hasAttribute(XMLATTR_ALFRESCO_ASSOC_NAME)) {
            result.setAssocTypeName(nodeAssoc.getAttribute(XMLATTR_ALFRESCO_ASSOC_NAME));
        }

		/* альфресковский ассоциация параметра */

        AssocKind kind = null;
        if (nodeAssoc.hasAttribute(XMLATTR_ALFRESCO_ASSOC_KIND)) {
            kind = AssocKind.findAssocKind(nodeAssoc.getAttribute(XMLATTR_ALFRESCO_ASSOC_KIND));
        }
        result.setAssocKind(kind);


        return result;
    }

    private static Element xmlCreateFlagsNode(Document doc, String xmlNodeQueryDesc, ReportFlags flags) {
        if (flags == null) {
            return null;
        }

        final Element result = doc.createElement(xmlNodeQueryDesc);

        XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_OFFSET, flags.getOffset());
        XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_LIMIT, flags.getLimit());
        XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_PGSIZE, flags.getPgSize());

        XmlHelper.xmlCreateCDataNode(doc, result, XMLNODE_QUERY_TEXT, flags.getText());
        XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_ALLVERSIONS, flags.isAllVersions());

        final String values = (flags.getSupportedNodeTypes() == null)
                ? null
                : StringUtils.collectionToCommaDelimitedString(flags.getSupportedNodeTypes());
        XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_PREFEREDTYPE, values);


        XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_MULTIROW, flags.isMultiRow());
        XmlHelper.xmlCreatePlainNode(doc, result, XMLNODE_QUERY_ISCUSTOM, flags.isCustom());

		/* включение атрибутов */

        final Element flagsGrp = xmlCreateFlagsAttributesGrp(doc, XMLNODE_FLAGS_MAP, flags.flags(), true);
        if (flagsGrp != null)
            result.appendChild(flagsGrp);


        return result;
    }

    private static ReportFlags parseReportFlags(Element root, String xmlNodeQueryDesc) {
        final Element curNode = (Element) XmlHelper.findNodeByAttr(root, xmlNodeQueryDesc, null, null);
        if (curNode == null) {
            return null;
        }

        final ReportFlagsImpl result = new ReportFlagsImpl();

        result.setOffset(XmlHelper.getNodeAsInt(curNode, XMLNODE_QUERY_OFFSET, result.getOffset()));
        result.setLimit(XmlHelper.getNodeAsInt(curNode, XMLNODE_QUERY_LIMIT, result.getLimit()));
        result.setPgSize(XmlHelper.getNodeAsInt(curNode, XMLNODE_QUERY_PGSIZE, result.getPgSize()));

        result.setText(XmlHelper.getNodeAsText(curNode, XMLNODE_QUERY_TEXT, result.getText()));
        result.setAllVersions(XmlHelper.getNodeAsBool(curNode, XMLNODE_QUERY_ALLVERSIONS, result.isAllVersions()));
        result.setPreferedNodeType(XmlHelper.getNodeAsText(curNode, XMLNODE_QUERY_PREFEREDTYPE
                , (result.getSupportedNodeTypes() == null ? null : StringUtils.collectionToCommaDelimitedString(result.getSupportedNodeTypes())))
        );

        result.setMultiRow(XmlHelper.getNodeAsBool(curNode, XMLNODE_QUERY_MULTIROW, result.isMultiRow()));
        result.setCustom(XmlHelper.getNodeAsBool(curNode, XMLNODE_QUERY_ISCUSTOM, result.isCustom()));

		/* включение атрибутов */

        // final Element flagsGrp = xmlCreateFlagsAttributesGrp( doc, XMLNODE_FLAGS_MAP, flags.flags() );
        final Element flagsGrp = XmlHelper.findNodeByName(curNode, XMLNODE_FLAGS_MAP);
        if (flagsGrp != null)
            parseFlagsAttributes(result.flags(), flagsGrp.getChildNodes(), null);


        return result;
    }

    /**
     * Выбираем название отчёта "ReportName" из названия файла/описателя вида "abc/ds-ReportName.xml".
     * Если нет префикса "ds-", то строка берётся от последнего символа "/", а если его нет - от начала строки.
     */
    public static String extractReportName(String dsFileName) {
        if (dsFileName == null) {
            return null;
        }
        int start = dsFileName.indexOf(PFX_DS);
        if (start >= 0) {
            // с символа сразу после "ds-" ...
            start += PFX_DS.length();
        } else if ((start = dsFileName.lastIndexOf("\\")) >= 0) {
            // после символа "\"
            start++;
        } else if ((start = dsFileName.lastIndexOf("/")) >= 0) {
            // после символа "/"
            start++;
        } else {
            start = 0; // если ничего нет - то с начала строки
        }

        int end = dsFileName.lastIndexOf(".");
        if (end < 0 || end < start) {
            // если нет точки или она слева от start - до конца строки
            end = dsFileName.length();
        }

        return dsFileName.substring(start, end);
    }

    /**
     * Проверить является ли указанное имя файла назвнаием мета-описания вида "ds-*.xml"
     *
     * @param testFileName имя файла (без путей)
     */
    public static boolean isDsConfigFileName(final String testFileName) {
        return (testFileName != null)
                && testFileName.startsWith(PFX_DS) // начинается с "ds-"
                && testFileName.endsWith(".xml");
    }

    /**
     * Получение названия стандартного файла с мета-описанием ("ds-xxx.xml"),
     * который соот-ет указанному отчёту
     *
     * @param reportCode код (мнемоника) отчёт
     * @return название вида "ds-reportCode.xml"
     */
    public static String makeDsConfigFileName(final String reportCode) {
        return String.format("%s%s.xml", PFX_DS, reportCode);
    }

    /**
     * Получить id названия ds-xml файла, в котором может храниться мета-
     * описание указанного отчёта.
     *
     * @param desc дескриптор отчёта
     * @return полное имя файла.
     */
    public static IdRContent makeDsXmlId(ReportDescriptor desc) {
        return IdRContent.createId(desc, makeDsConfigFileName(desc.getMnem()));
    }
}
