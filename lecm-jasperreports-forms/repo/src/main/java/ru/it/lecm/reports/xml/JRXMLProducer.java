package ru.it.lecm.reports.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;

/**
 * Дописывание "fields" в jrxml-файлы.
 * @author rabdullin
 *
 */
public class JRXMLProducer {

	private static final Logger logger = LoggerFactory.getLogger(JRXMLProducer.class);

	public static final String XMLNODE_ROOT_JASPER = "jasperReport";

	/*
	 * example:
		<field name="col_DocKind" class="java.lang.String">
			<fieldDescription><![CDATA[Вид договора]]></fieldDescription>
		</field>
	 */
	private static final String XMLNODE_FIELD = "field";

	// example: <property name="dataSource" value="ru.it.lecm.reports.jasper.DSProviderReestrDogovorov"/>
	private static final String XMLNODE_PROPERTY = "property";
	private static final String XMLATTR_NAME = "name";
	private static final String XMLATTR_V_DS = "dataSource";
	private static final String XMLATTR_VALUE = "value";

	private static final String XMLATTR_CLASS = "class";
	private static final String XMLNODE_FIELD_DESCRIPTION = "fieldDescription";


	// private static final String XMLNODE_PARAMETER = "parameter";

	/**
	 * Выполнить патч указанного Jrxml-файла и сохранить результат в другой.
	 * @param inJrxmlFileName
	 * @param outJrxmlFileName
	 * @param desc мета-описание отчёта
	 */
	public static void patchJrxml( String inJrxmlFileName, String outJrxmlFileName, ReportDescriptor desc) {
		String stage = "prepare";
		try {
			stage = "open input file";
			final File f = new File(inJrxmlFileName);

			stage = String.format("open jrxml-file '%s'", f.getAbsolutePath());
			final InputStream fin = new BufferedInputStream(new FileInputStream(f));

			stage = String.format("updating jrxml data of '%s'", f.getAbsolutePath());
			final ByteArrayOutputStream outJrxml = updateJRXML( fin, f.getName(), desc);
			fin.close();

			stage = String.format("saving into jrxml-file '%s'", outJrxmlFileName);
			outJrxml.writeTo( new FileOutputStream(outJrxmlFileName));
			outJrxml.close();

			System.out.println( String.format( "\n successfully saved into jrxml '%s'", outJrxmlFileName));

		} catch (Exception e) {
			throw new RuntimeException( String.format("Exception at stage '%s': %s\n", stage, e.toString()), e);
		}
	}

	// TODO: доработать прототип
	/**
	 * Создание jrxml-файла с простой многоколоночной таблицей.
	 * @param inPrototypeFileName название файла с прототипом создаваемого файла.
	 * В нём отмечены все нужные секции и есть прототип колокни данных для 
	 * создания таблицы из всех колонок НД отчёта.
	 * @param outJrxmlFileName сгенерированный файл.
	 * @param desc мета-описание отчёта
	 */
	public static void createJrxml( String inPrototypeFileName, String outJrxmlFileName, ReportDescriptor desc) {
		String stage = "prepare";
		try {
			stage = "open input file";
			final File fInPrototype = new File(inPrototypeFileName);

			stage = String.format("open prototype file '%s'", fInPrototype.getAbsolutePath());
			final InputStream fin = new BufferedInputStream(new FileInputStream(fInPrototype));

			stage = String.format("generating jrxml data of prototype '%s'", fInPrototype.getAbsolutePath());
			final ByteArrayOutputStream outJrxml = updateJRXML( fin, fInPrototype.getName(), desc);
			fin.close();

			stage = String.format("saving into jrxml file '%s'", outJrxmlFileName);
			outJrxml.writeTo( new FileOutputStream(outJrxmlFileName));
			outJrxml.close();

			System.out.println( String.format( "\n successfully saved into jrxml '%s'", outJrxmlFileName));

		} catch (Exception e) {
			throw new RuntimeException( String.format("Exception at stage '%s': %s\n", stage, e.toString()), e);
		}
	}

	/**
	 * Внести изменения в шаблонный xml.
	 * Сейчас изменяет секции '<field>' и '<property name="dataSource" value="java-class">'. 
	 * @param jrxml поток с файлом, который надо "патчить"
	 * @param streamName название потока (для информации в журнал)
	 * @param desc описание, которое надо внести
	 * @return поток с обновлёнными данными
	 */
	public static ByteArrayOutputStream updateJRXML( InputStream jrxml, String streamName
			, ReportDescriptor desc ) {
		if (jrxml == null)
			return null;
		if (desc == null)
			return null;
		logger.debug("updating jrxml " + streamName);

		try { 
			//	final InputSource src = new InputSource(xml);
			//	src.setEncoding("UTF-8");
			//	logger.info("Encodig set as: "+ src.getEncoding());
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(jrxml);

			// проверка корневого элемента
			final Element rootElem = doc.getDocumentElement();
			if (!XMLNODE_ROOT_JASPER.equals(rootElem.getNodeName())) {
				throw new RuntimeException("JRXML root '" + XMLNODE_ROOT_JASPER + "' element expected");
			}

			/*
		 	<property name="dataSource" value="ru.it.lecm.reports.jasper.DSProviderReestrDogovorov"/>
			 */
			replaceInListByAttrName( doc, rootElem, XMLNODE_PROPERTY, XMLATTR_NAME, XMLATTR_V_DS, XMLATTR_VALUE, desc.getProviderDescriptor().className());

			/*
			 * ListOf
			<field name="col_Regnum" class="java.lang.String">
				<fieldDescription><![CDATA[Регистрационный номер договора]]></fieldDescription>
			</field>
			 */
			replaceFieldList( doc, rootElem, desc.getDsDescriptor().getColumns());

			/* формирование результата */
			final ByteArrayOutputStream result = XmlHelper.serialize( doc);

			logger.info("updated SUCCESSFULL for jrxml " + streamName);

			return result;

		} catch (Throwable t) {
			final String msg = "Problem updating jrxml " + streamName;
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
	}


	/**
	 * Выполнить обновление или добавление дочернего списочного XML-элемента
	 * Пример: чтобы обоновить
	 *    <property name="dataSource" value="ru.it.lecm.XXX"/>
	 * выполняем вызов со следующими аргументами:
	 *    replaceInListByAttrName( rootNode, "property", "name", "dataSource", "value", "ru.it.lecm.XXX");
	 * @param document
	 * @param parentNode
	 * @param xmlName название "списочного" атрибута
	 * @param attrMakerName название атрибута для отбора узла
	 * @param attrMarkerValue значение атрибута для отбора узла
	 * @param attrDestName название атрибута для внесения данных
	 * @param attrDestValue значение -//-
	 */
	public static Element replaceInListByAttrName( final Document document
			, final Element parentNode
			, String xmlName, String attrMakerName, String attrMarkerValue
			, String attrDestName, String attrDestValue) 
	{
		final List<Node> properties = XmlHelper.findNodesList(parentNode, xmlName, attrMakerName, attrMarkerValue);

		final Element dsNode;
		if (properties != null && !properties.isEmpty()) {
			// такой узел уже имеется ...
			dsNode = (Element) properties.get(0);
			if (dsNode.hasAttribute(attrDestName))
				dsNode.removeAttribute(attrDestName);
		} else{ // создание узла ...
			dsNode = document.createElement(XMLNODE_FIELD); // "<field>"
			dsNode.setAttribute(attrMakerName, attrMarkerValue);
		}

		dsNode.setAttribute(attrDestName, attrDestValue);

		return dsNode;
	}

	/** 
	 * Название xml-элементов, перед которыми только и могут быть вставлены "field"
	 * (!) порядок для iReport важен
	 */
	final static String[] INSERT_BEFORE_XMLNODES = { "variable", "background", "title", "pageHeader", "columnHeader", "detail", "columnFooter", "pageFooter", "summary"};


	/**
	 * Сформировать вложенные узлы '<field>' согласно указанному списку описаний полей
	 * @param document
	 * @param destNode
	 * @param columns
	 */
	public static void replaceFieldList(Document document, Element destNode, List<ColumnDescriptor> columns) {
		if (destNode == null || columns == null)
			return;

		/* удаление прежнего списка полей ... */
		final List<Node> fields = XmlHelper.findNodesList(destNode, XMLNODE_FIELD);
		if (fields != null) {
			int i = 0;
			for (Node child: fields) {
				++i;
				destNode.removeChild(child);
			}
			logger.debug( String.format( "previous list of fields cleared: %s items", i));
		}

		/* создание новых узлов ... */
		final List<Element> newFields = new ArrayList<Element>();
		for (ColumnDescriptor coldesc: columns) {
			newFields.add( makeFieldNode(coldesc, document));
		}
		logger.debug( String.format( "new list of fields created: %s items", newFields.size() ));

		/* добавление новых узлов ... */
		final Node refChild = XmlHelper.tryFindNextToFieldsXmlSection( destNode, INSERT_BEFORE_XMLNODES);
		for (Element item: newFields) {
			// добавление СТРОГО перед этой секцией или в конец (когда refChild == null)...
			destNode.insertBefore( item, refChild);
		}
		logger.info( String.format( "new list of fields inserted: %s items", newFields.size() ));
	}

	/**
	 * Получить xml-описатель колонки данных в виде:
	 *    <field name="col_Regnum" class="java.lang.String">
	 *       <fieldDescription><![CDATA[Регистрационный номер договора]]></fieldDescription>
	 *    </field>
	 * @param coldesc
	 * @param document
	 * @return
	 */
	public static Element makeFieldNode(ColumnDescriptor coldesc, Document document) {
		// Element valueElement = document.createElementNS(NS, VALUE_TAG);
		// Text valueText = document.createTextNode(value);
		final Element result = document.createElement(XMLNODE_FIELD); // "<field>"

		result.setAttribute(XMLATTR_NAME, coldesc.getColumnName());
		result.setAttribute(XMLATTR_CLASS, coldesc.className());

		{ // описатель поля - как вложенный элемент ...
			final Element child = document.createElement(XMLNODE_FIELD_DESCRIPTION); // "<fieldDescription>"
			child.appendChild( document.createCDATASection(coldesc.getExpression()) );
			result.appendChild(child);
		}

		return result;
	}

}
