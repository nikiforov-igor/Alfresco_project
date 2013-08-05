package ru.it.lecm.reports.generators;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.XmlHelper;

/**
 * Макрогенератор xml/jrxml на основе txml-шаблонов.
 * @author rabdullin
 * 
 * V2. Описание синтаксиса:
	01)
		<prototype>
			1) xml-секция с макроподстаночоной группой для каждой колонки
				<macro.field> ... </macro.field>
			2)описания  начальных значений автоматических переменых и 
			custom-макроконстант, которые могут использоваться в macro.field
			type (см MVType) может быть "ANY" | "string" | "int" | "long" | "double"
				<macro.vars>
					<var name="" value="" type="" startCalc="" preCalc="" postCalc="" />
					или так (выраждения не атрибуты, а дочерние узлы) 
					<var> 
						<calc.expressions>
							<start .../>
							<pre ... />
							<post ... />
						</calc.expressions> 
					</var> 
				</macro.vars>
		</prototype>

		эта группа используется для подстановки последовательно для каждой колонки выходного НД;
		при этом используются макросимволы (см п [02]) и блок описания констант.

		объекты, которыми оперируем при формировании выводимой строки:
			CELL.*, FLD.*, FLD.DESC.*
TODO: скорректировать (сейчас упрощено - нет CELL, нет необходимости использовать left/top/... - достаточно простых переменных)
		Автоматические переменные:
		02) @ABC	общее обозначение для макроса ABC
		02.0) @GUID		сгенерировать и подставить GUID
		02.1) @CELL.*   параметры ячейки
		   CELL  ячейка вывода (соот-но координаты CELL.LEFT, CELL.TOP и размерения CELL.WIDTH, CELL.HEIGHT),
					если не указаны в protoConst, то:
					   LEFT/TOP используются значения 2 для обоих,
					   для WIDTH/HEIGHT используются значения 50 и 25 соот-но

		02.2) @FLD.*    параметры поля
		   FLD   выводимые данные поля/колонки (FLD.LEFT, FLD.TOP, FLD.WIDTH, FLD.HEIGHT)
		   FLD.INDEX индекс текущей ячейки данных от нуля
				для табличного представления логично иметь:
					X.colDelta >= X.width, Y.colDelta = 0 (т.е. "в строку")
				для страничного представления логично иметь:
					X.colDelta = 0, Y.colDelta >= Y.height (т.е. "в столбец").

		02.3) @FLD.DESC.АБВ		характеристики текущей колонки, работает через spring-reflection для
				типа ColumnDescriptor, так что можно много чего. Стандартно:
					@FLD.DESC.columnName: string
					@FLD.DESC.expression: string
					@FLD.DESC.parameterValue.prompt1 / prompt2

todo:		02.4) @locale			локаль по-умолчанию (если не указана используется "ru-ru")
			+части локали
				@locale.country
				@locale.language

Пример: 
		<prototype>
			// описания переменных
			<macro.vars>
				<var name="CWIDTH" value="30" />
				<var name="CHEIGHT" value="26" />
				<var name="CTOP" value="" startCalc="3" />
				<var name="CLEFT" value="5" type="int" postCalc="@+CWIDTH" /> // относительное приращение после каждой макроподстановки очередного поля
				<var name="fontName" value="DejaVu Sans Mono" />
				<var name="fontSize" value="10" />
			</macro.vars>

			// макроподстановка для одного поля, которая будет автоматически 
			// применяться ко всем имющимся колонкам ...
			<macro.body.field>
				<textField isBlankWhenNull="true">
					<reportElement uuid="@GUID" isPrintRepeatedValues="false" x="@CLEFT" y="@CTOP" width="@CWIDTH" height="@CHEIGHT"/>

					<textElement textAlignment="Center" verticalAlignment="Top">
						<font fontName="@fontName" pdfEncoding="Cp1251" isPdfEmbedded="true"/>
					</textElement>
			
					<textFieldExpression><![CDATA["@FLD.desc.default"]]></textFieldExpression>
				</textField>
			</macro.body.field>
		</prototype>
 */
public class XMLMacroGenerator {

	private static final Logger logger = LoggerFactory.getLogger(XMLMacroGenerator.class);

	/** узел с описанием блока с прототипом ... */
	private static final String XMLNODE_PROTOTYPE = "prototype";

	/** узел с описанием переменных в блоке прототипа ... */
	private static final String XMLNODE_PROTOTYPE_VARS = "macro.vars";

	/** узел с описанием макрорасширяемого участка для каждой колонки в блоке прототипа ... */
	private static final String XMLNODE_PROTOTYPE_BODY_FIELD = "macro.body.field";

	/** узел с описанием переменной ... */
	private static final String XMLNODE_VAR = "var";
	private static final String XMLATTR_VAR_START_CALC = "startCalc";
	private static final String XMLATTR_VAR_PRE_CALC = "preCalc";
	private static final String XMLATTR_VAR_POST_CALC = "postCalc";
	private static final String XMLATTR_VAR_TYPE = "type";

	/**
	 * внутри выраждения для фаз в виде: <фаза value="xxx"/> или <фаза>xxx</фаза>
	 * где фаза это одно из "start", "pre", "post"
	 */
	private static final String XMLNODE_CALC_EXPRESSIONS = "calc.expressions"; 

	/** название переменной для auto-Guid */
	private static final String VNAME_GUID = "GUID";

	/** название переменной для auto cell field */
	private static final String VNAME_FLD = "FLD";

	/** название переменной для auto report desc */
	private static final String VNAME_RDesc = "RDesc";

	/** активный описатель отчёта */
	private ReportDescriptor reportDesc;
	private MacroValues globals; // глобальные переменные, здесь обычно reportDesc под именем "RD"

	public XMLMacroGenerator() {
	}

	public XMLMacroGenerator(ReportDescriptor rdesc) {
		setReportDesc( rdesc);
	}

	/** активный описатель отчёта */
	public ReportDescriptor getReportDesc() {
		return reportDesc;
	}

	/** активный описатель отчёта */
	public void setReportDesc(ReportDescriptor reportDesc) {
		this.reportDesc = reportDesc;
		if (this.reportDesc == null) {
			this.globals = null;
		} else {
			this.globals = new MacroValues();
			this.globals.addVar( new RefMacroVar<ReportDescriptor>(VNAME_RDesc, this.reportDesc));
		}
	}


	/**
	 * Создать xml-документ на основе шаблонного xml
	 * @param streamName
	 * @param templateStm
	 * @return поток со сформированным xml
	 */
	public ByteArrayOutputStream xmlGenerateByTemplate( String streamName, InputStream templateStm) {
		if (templateStm == null)
			return null;
		logger.debug( String.format( "macro expanding template xml '%s' ...", streamName));

		/*(@) обновление мета-части описания ... */
		// return JRXMLProducer.updateJRXML(templateStm, streamName, getReportDesc());

		//  xml-генерация по шаблону ...
		try {
			//	final InputSource src = new InputSource(xml);
			//	src.setEncoding("UTF-8");
			//	logger.info("Encodig set as: "+ src.getEncoding());

			final Document docResult = XmlHelper.parseDOMDocument(templateStm);
			processNode( docResult.getDocumentElement(), docResult, this.globals);

			// формирование результата
			final ByteArrayOutputStream result = XmlHelper.serialize( docResult);

			logger.info( String.format( "macro expantion SUCCESSFULL from template xml '%s' ...", streamName));

			return result;

		} catch (Throwable t) {
			final String msg = String.format( "Problem macro expanding template xml '%s' ...", streamName);
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}

	}

	/**
	 * Определить, является ли выражение вычисляемым.
	 * Сейчас вычислемыое выражение - это ссылка, которая начинается с символа "@" (PFX_REF_MARKER)
	 * @param expr
	 * @return true, если выражение является вычисляемым (содержит ссылку)
	 */
	static boolean isCalcExpression(String expr) {
		return (expr != null)
				// && expr.startsWith(MacroValue.PFX_REF_MARKER)
				&& expr.indexOf(MacroValue.PFX_REF_MARKER) != -1 // имеется внутри символ "@"
				;
	}

	/**
	 * Вернуть только вычисляемое выражение без префиксов '@'. 
	 * @return
	 */
	static String getPureCalcExpression(String value) {
		return (value == null || (value.length() < MacroValue.PFX_REF_MARKER.length()) ) 
					? value
					: value.substring( MacroValue.PFX_REF_MARKER.length() );
	}

	/**
	 * Склонировать узел с учётом макроподстановок и добавить созданный узел в destNode.
	 * @param srcNode обрабатываемый узел
	 * @param destDoc целевой документ
	 * @param curDictionary текущий макро-словарь 
	 */
	private void processNode( Node srcNode, Document destDoc, MacroValues curDictionary)
	{ 
		if (srcNode == null)
			return;

		if (curDictionary != null)
			// отработать вложенные атрибуты и значение ...
			doMacroExpand(srcNode, curDictionary);

		final boolean flagIsMacros = XMLNODE_PROTOTYPE.equalsIgnoreCase(srcNode.getNodeName());
		if (flagIsMacros) {
			// встречен очередной макоподстановочный узел - его надо будет расширять отдельно ...
			processMacroNode( srcNode, destDoc, curDictionary);
		} else { // обычный узел - отработка детей ...
			final NodeList srcNodes = srcNode.getChildNodes();
			if (srcNodes != null) {
				for (int i = 0; i < srcNodes.getLength(); i++) {
					processNode( srcNodes.item(i), destDoc, curDictionary);
				}
			}
		}
	}

	/**
	 * Выполнить макроподстановку значения и атрибутов указанного узла
	 * @param result
	 * @param curDictionary
	 */
	private void doMacroExpand(Node result, MacroValues curDictionary) {
		if (result == null)
			return;

		/* ЗНАЧЕНИЕ */
		simpleExpand(result, curDictionary);

		/* АТРИБУТЫ ПООТДЕЛЬНОСТИ */
		if (!result.hasAttributes() || result.getAttributes() == null)
			return;

		for (int i=0; i < result.getAttributes().getLength(); i++) {
			final Node item = result.getAttributes().item(i);
			simpleExpand(item, curDictionary);
		} // for
	}

	/**
	 * Простой метод макро-расширения content-значения узла.
	 * (!) Макрорасширяемое значения имеют вид: @ссылка
	 *   , которые и будут текстуально заменяться на вычисленные
	 * @param item
	 * @param curDictionary 
	 * @return полученное content-значение узла (если изменений не было - текущее, если были - новое)  
	 */
	private Object simpleExpand(final Node item, MacroValues curDictionary) {
		if (item == null)
			return null;

		/*
		 * srcNode.getTextContent(): depends on Node type Content as
		 * 
		 * 1) ELEMENT_NODE, ATTRIBUTE_NODE, ENTITY_NODE, 
		 * ENTITY_REFERENCE_NODE, DOCUMENT_FRAGMENT_NODE 
		 *    concatenation of the textContent attribute value of every child 
		 *    node, excluding COMMENT_NODE and PROCESSING_INSTRUCTION_NODE 
		 *    nodes. This is the empty string if the node has no children.
		 * 
		 * 2) TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE, PROCESSING_INSTRUCTION_NODE 
		 *    nodeValue
		 *
		 * 3) DOCUMENT_NODE, DOCUMENT_TYPE_NODE, NOTATION_NODE
		 *    null
		 */
		final short ntype = item.getNodeType();
		final boolean isSupported = 
				(ntype == Node.ATTRIBUTE_NODE)
				|| (ntype == Node.CDATA_SECTION_NODE)
				|| (ntype == Node.TEXT_NODE)
		;

		if (!isSupported) 
			return null;

		final String value = item.getNodeValue();

		// if (Utils.isStringEmpty(value)) continue;
		if (!isCalcExpression(value))
			// если нечего вычислять - сразу возврат ...
			return value;

		if (curDictionary == null) {
			logger.warn(String.format( "No prototype section -> cannot expand xml node '%s' value '%s'", item.getNodeName(), value));
			return value;
		}

		final List<String> macroList = findMacroses(value);
		String result = value;
		for (final String macro: macroList) {
			// поиск подвыражения
			final Object newValue = curDictionary.calcExpr( getPureCalcExpression(macro));

			if (logger.isTraceEnabled())
				logger.trace(String.format( "changing xml node '%s' macros:\n\t from '%s'\n\t to '%s'", item.getNodeName(), value, newValue));

			// замена очередного макро подвыражения
			result = result.replaceAll(macro, Utils.coalesce(newValue, ""));
		}

		if (logger.isDebugEnabled())
			logger.debug(String.format( "changing xml node '%s' value:\n\t from '%s'\n\t to '%s'", item.getNodeName(), value, result));

		// item.setTextContent( result);
		item.setNodeValue(result);

		return result;
	}

	private static String MACRO_BREAKERS = "@ ;,-+*/=()^&%~!#?!\\<>{}'\"`\t\n";
	/**
	 * Выделение в строке всех макро подвыражений вида "@abc" (с возможными точками внутри)
	 * @param value
	 * @return инициализированный список подвыражений (с символами '@' в начале)
	 */
	private static List<String> findMacroses(String value) {
		final List<String> result = new ArrayList<String>();
		final int clen = MacroValue.PFX_REF_MARKER.length();
		if (value != null) {
			int iEnd = 0; // первый необработанный символ ... 
			while (iEnd < value.length()) {
				final int i = value.indexOf( MacroValue.PFX_REF_MARKER, iEnd);
				if (i == -1) break; // больше нет

				iEnd = i;
				// двойные "@" ...
				if (	(iEnd < value.length() - clen) 
						&& MacroValue.PFX_REF_MARKER.equals( value.substring(iEnd + 1, iEnd + 1 + clen)) 
				) {
					iEnd += 2 * clen;
					result.add( MacroValue.PFX_REF_MARKER + MacroValue.PFX_REF_MARKER);
					continue;
				}

				// ищем конец макровыражения ...
				do { iEnd++;
				} while (
						iEnd < value.length() 
						&& (-1 == MACRO_BREAKERS.indexOf( value.charAt(iEnd))) 
				);
				// вычленяем
				final String m = value.substring(i, iEnd);
				if (m.length() > 0) // регим макрос, если строка не пуста
					result.add( m);
			} 
		}
		return result;
	}

	/**
	 * Обработать очередную макроподстановочную часть
	 * @param srcMacroNode узел с макросом (внутри него будут <vars>)
	 * @param destDoc целевой документ
	 * @param parentVars текущий "уровень" макроса
	 */
	private void processMacroNode(Node srcMacroNode
			, Document destDoc
			, MacroValues parentVars
	)
	{
		if (srcMacroNode == null)
			return;

		if (this.reportDesc == null || this.reportDesc.getDsDescriptor() == null
				|| this.reportDesc.getDsDescriptor().getColumns() == null)
		{ // если нет данных колонок - предупреждение
			logger.info( String.format( "No report columns descriptors present for report '%s' -> producing empty macros"
					, this.reportDesc.getMnem(), srcMacroNode.getNodeName()));
		}

		{ // проверка наличия родителя ...
			final Node destRoot = srcMacroNode.getParentNode();
			if (destRoot == null)
				throw new RuntimeException( String.format( "Main XML document node cannot be '%s'", XMLNODE_PROTOTYPE));
		}

		/* УЗЕЛ - ЭТО МАКРОС ДЛЯ КОЛОНКИ */
		final MacroBlock block = new MacroBlock( destDoc, parentVars);
		block.parseNode(srcMacroNode);
		block.doListMacroExpantion(reportDesc.getDsDescriptor().getColumns());

	}


	/**
	 * Шаблонный блок для подстановки
	 */
	private class MacroBlock {

		private Document destDoc;
		private MacroValues parentVars;

		private Node destParent; // целевой родлитель, обычно это родительский для узла srcMacroPrototype
		private Node srcMacroPrototype; // исходный узел "prototype" - после него будут вставляться сгенерированные
		private Node srcVars; // исходный узел "macro.vars" внутри "prototype"
		private Node srcTemplate; // шаблонный узел "macro.field" внутри "prototype" - всех его детей надо будет отрабатывать как макрос ...

		private MacroValues curVars;

		public MacroBlock(Document adestDoc, MacroValues aparentVars) {
			this.destDoc = adestDoc;
			this.parentVars = aparentVars;
		}

		/**
		 * Загрузить описание из xml узла типа "prototype"
		 * (!) Сам узел не будет удаляться из документа автоматом в doListMacroExpantion, иначе это надо сделать внешним кодом.
		 * (!) Генерация по описанию будет выполняться только при явном вызове doListMacroExpantion.
		 * @param asrcMacroPrototype
		 */
		public void parseNode(Node asrcMacroPrototype) {
			this.srcMacroPrototype = asrcMacroPrototype;
			this.destParent = this.srcMacroPrototype.getParentNode();
			this.srcTemplate = XmlHelper.findNodeByName(this.srcMacroPrototype, XMLNODE_PROTOTYPE_BODY_FIELD);

			/* ПЕРЕМЕННЫЕ И КОНСТАНТЫ*/
			this.srcVars = XmlHelper.findNodeByName(this.srcMacroPrototype, XMLNODE_PROTOTYPE_VARS);

			this.curVars = parseVars( this.srcVars);
			this.curVars.setOuterMacro( this.parentVars);
			// добавление стандартных функций
			this.curVars.addVar( VNAME_GUID, new GuidAutoValue());
		}


		/**
		 * Выполнить XML-макроподстановку для всех колонок из указанного списка.
		 * (!) XML Узел "prototype" с описанием будет УДАЛЁН АВТОМАТОМ.  
		 * @param columns
		 */
		public void doListMacroExpantion(List<ColumnDescriptor> columns) {
			if (columns != null) {
				// применяем к каждой колонке НД макросы из всех вложенных child-узлов nodeMacros ...
				int index = -1;
				this.curVars.calcAllNext(CalcPhase.start); // инициализация ...

				for (ColumnDescriptor colDesc: columns) {
					index++;
					this.doColumnMacroExpantion(colDesc, index);
				}
			}

			/* убрать прототип из документа через родителя ... */
			this.destParent.removeChild( this.srcMacroPrototype);
		}

		private void doColumnMacroExpantion(ColumnDescriptor colDesc, int index) {
			// автоматическая переменная для колокни данных
			curVars.addVar( VNAME_FLD, new ColumnDescValue(index, colDesc)); // "FLD"

			// вычисление значений перед генерацией ...
			curVars.calcAllNext(CalcPhase.pre); // применить авто-назначения ...

			// генерация ...
			if (srcTemplate != null && (srcTemplate.getChildNodes() != null) ) {
				// по всем вложенным узлам макроса ...
				for(int i = 0; i < srcTemplate.getChildNodes().getLength(); i++) {
					final Node tnode = srcTemplate.getChildNodes().item(i);
					final Node newNode = tnode.cloneNode(/*deep*/true);
					this.destParent.insertBefore(newNode, srcMacroPrototype);
					processNode(newNode, this.destDoc, this.curVars);
				}
			}

			// вычисление "пост-значений" (тут должна быть корректировка left/top/width/height)
			curVars.calcAllNext(CalcPhase.post); // обновить текущие значения "на месте" ...
		}
	}

	/**
	 * По узлу со списком переменных (<macro.vars/>) построить список объектов-переменных
	 * @param nodeVars
	 * @return инициализированный (!) список пеменных (возможно пустой, но всегда НЕ NULL)
	 */
	private static MacroValues parseVars(Node nodeVars) {
		final MacroValues result = new MacroValues();
		final List<Node> vars = XmlHelper.findNodesList(nodeVars, XMLNODE_VAR);
		if (vars != null) {
			for (Node item: vars) {
				final MacroValue newVar = parseVar( (Element) item);
				if (newVar != null)
					result.addVar(newVar);
			}
		}
		return result;
	}


	/**
	 * Разбор переменной вида:
	 * 	<var name="название" 
	 * 			value="значение"
	 * 			startCalc="Выражение-для-нач-вычислений(если value не достаточно)" 
	 * 			preCalc="Выражение-пост-вычислений"
	 * 			postCalc="Выражение-пред-вычислений" 
	 * 	>
	 * 		// (необязательно) можно фазы задавать и так:
	 * 		<calc.expressions>
	 * 			<phase name="start" value="" />
	 * 			<phase name="pre" value="" />
	 * 			<phase name="post" value="" />
	 * 			<start value="" />
	 * 			<pre value="" />
	 * 			<post value="" />
	 * 		</calc.expressions>
	 * </var>
	 */
	private static MacroValue parseVar(Element item) {
		if (item == null || !item.hasAttributes()) // simply skip it
			return null;
		final String name = Utils.coalesce( item.getAttribute("name"), item.getNodeName());
		if (Utils.isStringEmpty(name)) {
			logger.warn( String.format("Skip empty-named xml '%s'-item", item, XMLNODE_VAR));
			return null;
		}

		final MacroValue result = new MacroVarBase(name, getAttr(item, "value", null));
		result.setExpression( CalcPhase.start, getAttr(item, XMLATTR_VAR_START_CALC, null));
		result.setExpression( CalcPhase.pre,   getAttr(item, XMLATTR_VAR_PRE_CALC, null));
		result.setExpression( CalcPhase.post,  getAttr(item, XMLATTR_VAR_POST_CALC, null));
		result.setValType( MVType.find( getAttr(item, XMLATTR_VAR_TYPE, null)));

		// + выбираем узлы <calc.expressions>
		final Node exprNode = XmlHelper.findNodeByName( item, XMLNODE_CALC_EXPRESSIONS);
		if (exprNode != null) {
			for (CalcPhase phase: CalcPhase.values()) {
				final String expr = XmlHelper.getNodeAsText( (Element) exprNode, phase.name(), null);
				if (expr != null) { // имеется узел с выражением для данной фазы
					if (result.getExpression(phase) != null) {
						// повторное задание выражения - выдадим предупреждение ...
						logger.warn( String.format( 
								"Multiple assignment of expression for phase '%s':\n\t expression was '%s'\n\t and is replaced by xml node '%s' with expression value '%s'"
								, phase, result.getExpression(phase)
								, phase.name(), expr ));
					}
					result.setExpression(phase, expr);
				}
			}
		}

		return result;
	}

	private static String getAttr(Element item, String attrName, String defaultObj) {
		final String result = (item.hasAttribute(attrName)) 
						? item.getAttribute(attrName)
						: defaultObj;
		return result;
	}

	/**
	 * Набор именованных переменных
	 * Имена регистро независимые
	 * @author rabdullin
	 */
	static class MacroValues {

		private Map<String, MacroValue> variables; // свои переменные
		private MacroValues outerMacro; // переменные выше по вложенности/"выше по стеку"

		public MacroValues() {
		}

		/**
		 * Склонировать значения другого массива
		 */
		public MacroValues(MacroValues otherToClone) {
			super();
			cloneVars( getVars(), (otherToClone != null) ? otherToClone.variables : null);
		}

		protected static void cloneVars( Map<String, MacroValue> dest,
				Map<String, MacroValue> src)
		{
			if (src == null || src.isEmpty())
				return;

			for(Map.Entry<String, MacroValue> e: src.entrySet()) {
				dest.put( e.getKey(), e.getValue().cloneValue());
			}
		}

		/**
		 * Получить проинициализированный список переменных
		 * @return
		 */
		public Map<String, MacroValue> getVars() {
			if (variables == null)
				variables =  new HashMap<String, MacroValue>();
			return variables;
		}

		/**
		 * Проверить имеется ли указанная переменная в этом списке (родительские не проверяются).
		 * @param varName название переменной
		 * @return true, если указанная переменная находится в этом списке
		 */
		public boolean contains(String varName) {
			return this.findVar(varName, false) != null;
		}

		/**
		 * @param var
		 * @return true, если указанный объект-переменная находится в этом списке
		 */
		public boolean contains(MacroValue var) {
			// return this.getVars().values().contains(var);
			return (var != null) && (this.findVar(var.name(), false) == var);
		}

		/**
		 * Найти переменную в списке (ignore-case search)
		 * @param varName имя пеменной 
		 * @param searchInOuter используется если переменной нет в этом списке:
		 * 		при true, будет выполнен поиск в outerMacro.
		 * @return
		 */
		public MacroValue findVar(String varName, boolean searchInOuter) {
			if (varName != null)
				varName = varName.toLowerCase();
			if (this.variables != null && this.variables.containsKey(varName))
				return this.variables.get(varName); // FOUND locally

			// try find outer ...
			return (searchInOuter && this.getOuterMacro() != null) 
					? this.getOuterMacro().findVar(varName, true) 
					: null;
		}

		/**
		 * Переменные выше по вложенности/"выше по стеку". Используются при поиске, когда нет соот-вий в своём списке.
		 * @return
		 */
		public MacroValues getOuterMacro() {
			return outerMacro;
		}

		/**
		 * Переменные выше по вложенности/"выше по стеку". Используются при поиске, когда нет соот-вий в своём списке.
		 * @param outerMacro
		 */
		public void setOuterMacro(MacroValues outerMacro) {
			this.outerMacro = outerMacro;
		}

		/**
		 * Добавить новое значение в список с именем newVar.name, null пропускаются.
		 * @param newVar
		 */
		public void addVar(MacroValue newVar) {
			if (newVar != null)
				addVar(newVar.name(), newVar);
		}

		/**
		 * Добавить новое значение в текущий список под указанным именем. 
		 * newVar == null пропускаются.
		 * @param varName имя переменной (допускается null)
		 * @param newVar переменная, если будет пустым newVar.name, то присваивается
		 * имя nameVar. (!) Автоматом преобразуется к нижнему регистру. 
		 */
		public void addVar(String varName, MacroValue newVar) {
			if (newVar != null) {
				if (varName != null)
					varName = varName.toLowerCase();
				if (newVar.name() == null) // задать имя переменной, если оно не было указано
					newVar.setName(varName);
				getVars().put( varName, newVar);
			}
		}

		/**
		 * Выполнить обновление значений переменных согласно указанной фазы.
		 * @param phase
		 */
		public void calcAllNext(CalcPhase phase) {
			for(MacroValue item: getVars().values()) {
				item.calcNext(phase, this);
			}
		}

		/**
		 * Вычислить значение выражения с учётом текеузщего значения переменной
		 * @param curValue переменная (может быть Null)
		 * @param expr выражение вида:
		 *    a			значение пеменной с названием "a" 
		 *    a.b.c		у переменной "a", свойство "b", и в нём свойство "c"
		 *    +xxx или -xxx		
		 *    			математические выражение для вычислений относительно текущего значения value  
		 * @return
		 */
		public Object calcExpr( MacroValue curValue, String expr) {
			if (expr == null || expr.trim().length() == 0)
				return expr;

			expr = expr.trim();
			final Object curVal = (curValue == null) ? null : curValue.getValue();

			// знаки "+"/"-" отработаем как относительные смещения отдельно ...
			final boolean isPlus = expr.charAt(0) == '+';
			final boolean isMinus = expr.charAt(0) == '-'; 
			final boolean isDelta = isPlus || isMinus ;
	
			// вычисления выражения без знака
			final Object result = calcExpr( (isDelta) ? expr.substring(1) : expr);

			if (isDelta) { // выражение это смещение -> привлекаем curValue ...
				Double delta = getAsDoubleSafely(result, null);

				if (delta == null) // если delta null, то прежнее значение оставить
					return curVal;

				if (isMinus)
					delta = -delta;

				final Double cur = getAsDoubleSafely( curVal, null);
				if (cur == null) 
					return delta; // прежнего значения не было -> вернуть новое значение

				// здесь оба значения не null -> складываем и приводим к нужному типу ...
				final Double vnew = cur + delta;
				if (curVal instanceof String)
					return vnew.toString();
				if (curVal instanceof Integer) return vnew.intValue(); 
				if (curVal instanceof Long) return vnew.longValue(); 
				if (curVal instanceof Byte) return vnew.byteValue();
				// иначе как double ...
				return vnew;
			}

			return result;

		}

		private static Double getAsDoubleSafely(Object v, Double vDefault) {
			if (v instanceof Number)
				return ((Number) v).doubleValue();
			// конвертирование в число через строковое представление ...
			Double result = vDefault;
			if (v != null) {
				try {
					result = Double.parseDouble( v.toString());
				} catch(NumberFormatException ex) {
					logger.error( String.format( "Invalid double value '%s' -> used as '%s'", v, result));
				}
			}
			return result;
		}

		/**
		 * По указанному выражению провести вычисления.
		 * @param expr вычисляемое выражение. Сейчас это название переменной,
		 * текущее значение которой требуется или выражение вида "A.B.C" (для
		 * вычислений using reflections)
		 * @return
		 */
		public Object calcExpr(String expr) { 
			if (expr == null || expr.trim().length() == 0)
				return expr;
			if (MacroValue.PFX_REF_MARKER.equals(expr)) // замена двойных @ на пусто
				return "";
			final int i = expr.indexOf('.');
			final String varname, tail;
			if (i < 0) {// точки нет - всё выражение это название переменной
				varname = expr.trim().toLowerCase();
				tail = null;
			} else { // точка есть - берём имя до точки и выражение после точки
				varname = expr.substring(0, i).trim().toLowerCase();
				tail = (i < expr.length() - 1) ? expr.substring(i+1).trim() : null;
			}

			final MacroValue foundvar = this.findVar(varname, true); // используем поиск у себя и "снаружи"
			if (foundvar == null) {
				logger.warn( String.format("Variable '%s' not found -> value used as '%s'", varname, expr));
				// return varname + ( tail != null ? "." + tail : "");
				return expr;
			}
			return reflectExpr(foundvar, tail);
		}

		/**
		 * Выполнить вычисление reflect-выражения для указанной переменной.
		 * @param var
		 * @param expr выражение в стиле spring для получения свойств переменной или null/пусто для получения значения переменной.
		 * @return
		 */
		public static Object reflectExpr(MacroValue var, String expr) {
			if (var == null)
				return null;

			if (Utils.isStringEmpty(expr)) // если не задано выражение -> значение самой переменной
				return var.getValue();

			// применяем рефлексию к (!) контейнерному значению
			final Object container = var.getReflectContainer();
			if (container == null)
				return null;
			final String msg = String.format("%s failed for container class %s", expr, container.getClass().getName());
			try {
				return PropertyUtils.getProperty(container, expr.trim());
			} catch (IllegalAccessException ex) {
				logger.error( msg, ex);
				return msg;
			} catch (InvocationTargetException ex) {
				logger.error( msg, ex);
				return msg;
			} catch (NoSuchMethodException ex) {
				logger.error( msg, ex);
				return msg;
			}
		}

	}

	/**
	 * Вычислительные фазы
	 * @author Ruslan
	 *
	 */
	enum CalcPhase {
		start	// начальная инициализация
		, pre 	// внутри итераций: перед назначением новой колонки
		, post 	// внутри итераций: после назначения новой колонки
	}

	enum MVType {
		  ANY(null)
		, STRING(String.class)
		, INT(Integer.class)
		, LONG(Long.class)
		, DOUBLE(Double.class)
		;

		final private Class<?> type;

		private MVType(Class<?> type) {
			this.type = type;
		}

		/**
		 * Найти по имени или типу
		 * @param v
		 * @return
		 */
		public static MVType find(String v) {
			if (v != null) {
				v = v.trim().toUpperCase();
				if (v.length() > 0) {
					for(MVType item: values()) {
						if ( v.equalsIgnoreCase(item.name()) )
							return item; // FOUND BY NAME
						// по типу сравнение для хотя бы пары букв ...
						if (	item.type != null && v.length() >=2
								&& item.type.getSimpleName().toUpperCase().contains(v)
							)
							return item; // FOUND BY PART OF TYPENAME
					}
					// нет такой строки - поднимаем исключение ...
					final String msg = String.format( "%s: Unknown enum item '%s' none of [%s]", MVType.class, v, Utils.getAsString(values()));
					logger.warn( msg);
					// return ANY;
					throw new RuntimeException( msg);
				}
			}
			return null; // NOT FOUND
		}

		@Override
		public String toString() {
			// return String.format( "%s(%s -> %s)", name(), type, (type == null) ? "NULL" : type.getSimpleName() );
			return String.format( "%s(%s)", name(), type);
		}

		/**
		 * Приведение значения в тип, соот-щий type
		 * @param v исходное значение
		 * @return значение v, преобразованное в тип type, 
		 * null сохраняется как был, если type null или ANY, то тоже сохраняется.
		 */
		public Object convert(Object v) {
			if (v == null || this == ANY || this.type == null)
				return v;

			// преобразование в строку ...
			final String sv = v.toString();
			if (this == STRING || sv == null)
				return sv;

			// преобразование в Double ...
			final Double dv = Double.parseDouble(sv.trim());
			switch(this) {
				case DOUBLE: 
					return dv;
				case INT: 
					return dv.intValue();
				case LONG: 
					return dv.longValue();
			}

			// return as is ...
			return v;
		}
	} 

	/**
	 * Клонируемый объект со значением 
	 */
	interface MacroValue {

		/**
		 * Префикс выражений, которые являются ссылками на другие переменные.
		 */
		public static final String PFX_REF_MARKER = "@";

		/** название значения (переменной) */
		String name();
		void setName( String aname);

		/** текущее значение для функции может меняться при последовательных вызовах */
		Object getValue();

		/** задать текущее значение */
		void setValue(Object value);

		/**
		 * Тип значения для getValue (в getValue будет выполняться приведение к этому типу).
		 * Если null - приведения не производится.
		 * @return
		 */
		MVType getValType();

		/**
		 * Тип значения для getValue (в getValue будет выполняться приведение к этому типу).
		 * Если null - приведения не производится.
		 * @param type
		 */
		void setValType(MVType type);

		/**
		 * Значение для разименований сложных relect-выражений относительно 
		 * значения самой переменной (например, для вычислений вида "x.abc.def").
		 * Обычно это тоже самое что и getValue().
		 * @return
		 */
		Object getReflectContainer();

		/** 
		 * Вычислительное выражение применяемое в указанной фазе вычислений: 
		 *    (phase=start) до установки начального значения,
		 *    (phase=pre) перед итерацией (до изменения текущей колонки),
		 *    (phase=post) после итерации (после изменения текущей колонки).
		 * 
		 * Значение expr если начинается с символа PFX_REF_MARKER='@', то является 
		 * ссылкой на именованную пемеременную (непосредственно на её значение 
		 * или на её свойства), иначе - константа, которая присвоится value.
		 * 
		 * Выражения имеет смысл использовать когда простого value не достаточно
		 * (например, для вычисляемых ссылок на другие именованные переменные). 
		 */
		String getExpression(CalcPhase phase);
		void setExpression(CalcPhase phase, String expr);

		/** создать объект - копию */
		MacroValue cloneValue();

		/**
		 * Вычисление следующего значения согласно своим установкам и приданному списку
		 * @param list приданный список значений (для ссылок на них)
		 */
		void calcNext( CalcPhase phase, MacroValues list);
	}

	/**
	 * Именованная переменная с вычисляемым скалярным значением.
	 * Имя даёт уникальность. Регистр символов в имени роли НЕ играет.
	 * @author rabdullin
	 */
	static class MacroVarBase
			implements MacroValue, Serializable 
	{
		private static final long serialVersionUID = 1L;

		private String name;
		private Map<CalcPhase, String> expressions;
		private Object obj;
		private MVType valType = null; // MVType.ANY;

		public MacroVarBase() {
		}

		public MacroVarBase(String name) {
			this(name, null);
		}

		public MacroVarBase(String name, Object obj) {
			super();
			this.name = name;
			this.obj = obj;
		}

		@Override 
		public MacroVarBase clone() throws CloneNotSupportedException {
			// final MacroValueConstBase result = (MacroValueConstBase) super.clone();
			try {
				final MacroVarBase result = (MacroVarBase) this.getClass().getConstructor(null).newInstance(null);
				result.name = this.name;
				if (this.expressions != null)
					result.getExprMap().putAll( this.expressions);
				result.obj = this.obj;
				result.valType = this.valType;

				return result;
			} catch (Exception ex) {
				final CloneNotSupportedException cloneErr = new CloneNotSupportedException( String.format( "Fail to clone item of class %s", this.getClass().getName()));
				cloneErr.initCause(ex);
				throw cloneErr;
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final MacroVarBase other = (MacroVarBase) obj;

			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equalsIgnoreCase(other.name))
				return false;

			if (this.obj == null) {
				if (other.obj != null)
					return false;
			} else if (!this.obj.equals(other.obj))
				return false;

			return true;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append( String.format( "%s [name '%s'", this.getClass().getSimpleName(), name));
			builder.append("\n\t, expressions ").append(getExprMap());
			builder.append( String.format( "\n\t, value '%s'", getValue()));
			builder.append( String.format( "\n\t, prefered type %s", getValType()));
			builder.append("\n]");
			return builder.toString();
		}

		@Override
		public MVType getValType() {
			return valType;
		}

		@Override
		public void setValType(MVType valType) {
			this.valType = valType;
		}

		@Override
		public Object getValue() {
			// приведение типа ...
			if (this.valType != null)
				return this.valType.convert(obj);
			return obj;
		}

		@Override
		// @NOTE: если понадобится строгая типизация MacroValue, то здесь самое место выполнить приведение типа
		public void setValue(Object value) {
			this.obj = value;
		}

		@Override
		public Object getReflectContainer() {
			return getValue();
		}

		@Override
		public String name() {
			return this.name;
		}

		@Override
		public void setName(String aname) {
			this.name = aname;
		}

		protected Map<CalcPhase, String> getExprMap() {
			if (this.expressions == null)
				this.expressions = new HashMap<CalcPhase, String>();
			return this.expressions;
		}

		@Override
		public String getExpression(CalcPhase phase) {
			return getExprMap().get(phase);
		}

		@Override
		public void setExpression(CalcPhase phase, String expression) {
			getExprMap().put( phase, (expression != null && expression.length() == 0) ? null : expression);
		}

		@Override
		public MacroValue cloneValue() {
			try {
				return (MacroValue) this.clone();
			} catch (Exception e) {
				throw new RuntimeException( String.format("Fail to clone item %s of class %s", this.name(), this.getClass()), e);
			}
		}

		/**
		 * здесь вычисляет значение на основании своего заданного для фазы выражения:
		 * а) если выражение пустое НИЧЕГО не меняется,
		 * б) если выражение начинается с '@', то выполняет нужные вычисления с 
		 * использованием списка list,
		 * в) иначе присваивается значение из expr.
		 */
		@Override
		public void calcNext(CalcPhase phase, MacroValues list) {
			final String expr = getExpression(phase);
			if (expr == null)
				return;
			if( list != null && isCalcExpression(expr)) { // вычисления
				setValue( list.calcExpr( this, expr.substring(1)));
			} else { // простое присвоение
				setValue(expr);
			}
		}

	}

	/**
	 * КЛасс для вычисления GUIDов
	 */
	static class GuidAutoValue extends MacroVarBase {

		private static final long serialVersionUID = 1L;

		private GuidAutoValue() {
			super();
		}

		private GuidAutoValue(String name) {
			super(name);
		}

		@Override 
		public GuidAutoValue clone() {
			final GuidAutoValue result = new GuidAutoValue(this.name());
//			if (this.getExprMap() != null)
//				result.getExprMap().putAll( this.getExprMap());
//			result.setValue( this.getValue());
			return result;
		}

		@Override
		public Object getValue() {
			return java.util.UUID.randomUUID();
		}

		@Override
		public void setValue(Object value) {
			// ignore
		}

		@Override
		/**
		 * Вычислительный контейнер - сам объект.
		 */
		public Object getReflectContainer() {
			return this;
		}

	}

	/**
	 * Контейнер для получения свойств другого объекта.
	 * Здесь выражение value является не константой, а ссылочным выражением 
	 * относительно базового объекта (base), наример, "abc.def".
	 * Переменная-обёртка Базового объекта (base), к которому применяется 
	 * механизм reflection для получения его свойств, задаваемых выражением 
	 * expression. Таким образом значение value вычисляется всегда косвенно.
	 * @author rabdullin
	 *
	 */
	public class RefMacroVar<T extends Object> 
			extends MacroVarBase
	{
		private static final long serialVersionUID = 1L;

		private T base;
		private String expression;// последнее выражение присвоенное в calcNext

		public RefMacroVar() {
		}

		public RefMacroVar(String name) {
			this(name, null);
		}

		public RefMacroVar(String name, T obj) {
			super(name, null);
			this.base = obj;
		}

		@Override 
		public RefMacroVar<T> clone() throws CloneNotSupportedException {
			// final ReferencedExpressionValue<T> result = new ReferencedExpressionValue<T>(this.name());
			final RefMacroVar<T> result = (RefMacroVar<T>) super.clone();
			// if (this.getExprMap() != null) result.getExprMap().putAll( this.getExprMap());
			// result.setValue( this.getValue());
			result.base = this.base;
			result.expression = this.expression;
			return result;
		}

		public void calcNext(CalcPhase phase, MacroValues list) {
			final String expr = getExpression(phase);
			if (expr != null && list != null) {

				// замена базового объекта ...
				setBase( (T) list.calcExpr(expr));

				// замена текущего выражения относительно base-объекта
				expression = expr;
			}
		}

		public String getExpression() {
			return expression; // (String) getValue();
		}

		public Object getBase() {
			return base;
		}

		public void setBase(T base) {
			this.base = base;
		}

		@Override
		public Object getReflectContainer() {
			return getBase();
		}

		@Override
		public Object getValue() {
			String expr = getExpression();
			try {
				return ( getBase() != null && !Utils.isStringEmpty(expr))
							? PropertyUtils.getProperty(this.getBase(), expr)
							: null; // super.getSubst();
			} catch (Throwable ex) {
				throw new RuntimeException( String.format("Problem getting from var '%s' expression '%s'", this.getBase(), expr), ex);
			}
		}

	}

	/** контейнер для прикрепления свойств (left-top-width-height) к разным объектам */
	public class RectXtender {
		private int left, top, width, height;

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
		}

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
		
	}


	public class ColumnDescXtender extends RectXtender {
		private ColumnDescriptor desc;
		private int index;

		public ColumnDescXtender() {
			super();
		}

		public ColumnDescXtender(ColumnDescriptor desc) {
			this( -1, desc);
		}

		public ColumnDescXtender(int colIndex, ColumnDescriptor desc) {
			super();
			this.desc = desc;
			this.index = colIndex;
		}

		public ColumnDescriptor getDesc() {
			return desc;
		}

		public void setDesc(ColumnDescriptor desc) {
			this.desc = desc;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

	}

	public class ColumnDescValue
			extends RefMacroVar<ColumnDescXtender>
	{
		private static final long serialVersionUID = 1L;

		public ColumnDescValue(String name, int index, ColumnDescriptor coldesc) {
			super(name, new ColumnDescXtender(index, coldesc));
		}

		public ColumnDescValue(ColumnDescriptor coldesc) {
			this( -1, coldesc);
		}

		public ColumnDescValue(int index, ColumnDescriptor coldesc) {
			this( coldesc == null ? null : coldesc.getColumnName(), -1, coldesc);
		}

	}
}
