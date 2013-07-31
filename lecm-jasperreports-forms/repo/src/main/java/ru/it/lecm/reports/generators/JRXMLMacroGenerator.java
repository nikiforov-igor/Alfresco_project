package ru.it.lecm.reports.generators;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.JRXMLProducer;
import ru.it.lecm.reports.xml.XmlHelper;

/**
 * Макрогенератор jrxml на основе txml-шаблонов.
 * @author rabdullin
 * 
 * V2. Описание синтаксиса:
	01)
		<prototype>
			<protoField> ... </protoField>  xml-секция с макроподстаночоной группой для каждой колонки
			<protoConst> ... </protoConst> описания  начальных значений автоматических переменых и 
				custom-макроконстант, которые могут использоваться в propField
		</prototype>

		эта группа используется для подстановки последовательно для каждой колонки выходного НД;
		при этом используются макросимволы (см п [02]) и блок описания констант.

		объекты, которыми оперируем при формировании выводимой строки:
			CELL.*, FLD.*, FLD.DESC.*

		Автоматические переменные:
		02) @ABC	общее обозначение для макроса ABC
		02.0) @GUID						сгенерировать и подставить GUID
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

Пример: (надо переписать, здесь старый)
	<prototype>
		<protoConst/>
		<protoField/>
	</proptotype>
 * 
 */
public class JRXMLMacroGenerator {
	private static final Logger logger = LoggerFactory.getLogger(JRXMLMacroGenerator.class);

	private static final String XMLNODE_PROTOTYPE = "proptotype";
	private static final String XMLNODE_PROTOTYPE_CONST = "protoConst";
	private static final String XMLNODE_PROTOTYPE_FIELD = "protoField";

	/** название переменной для autoGuid */
	private static final String VNAME_GUID = "GUID";

	private static final String VNAME_FLD = "FLD";

	/** активный описатель отчёта */
	private ReportDescriptor reportDesc;

	/** активный описатель отчёта */
	public ReportDescriptor getReportDesc() {
		return reportDesc;
	}

	/** активный описатель отчёта */
	public void setReportDesc(ReportDescriptor reportDesc) {
		this.reportDesc = reportDesc;
	}

	/**
	 * Создать xml-документ на основе шаблонного xml и текущего описателя reportDesc
	 * @param templateStm исходный макро-шаблон
	 * @return поток со сформированным xml
	 */
	public ByteArrayOutputStream xmlGenerateByTemplate( InputStream templateStm) {
		return xmlGenerateByTemplate(templateStm, Utils.coalesce( getReportDesc().getMnem(), "stream"));
	}

	/**
	 * Создать xml-документ на основе шаблонного xml и текущего описателя reportDesc
	 * @param templateStm исходный макро-шаблон
	 * @param streamName (информационное) название потока
	 * @return поток со сформированным xml
	 */
	public ByteArrayOutputStream xmlGenerateByTemplate( InputStream templateStm, String streamName) {
		if (templateStm == null)
			return null;
		logger.debug( String.format( "macro expanding template xml '%s' ...", streamName));
		// обновление мета-части описания ...
		return JRXMLProducer.updateJRXML(templateStm, streamName, getReportDesc());

		/* TODO: jrxml-генерация по шаблону ...
		try {
			//	final InputSource src = new InputSource(xml);
			//	src.setEncoding("UTF-8");
			//	logger.info("Encodig set as: "+ src.getEncoding());
			final Document docResult = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			final Document docMacro = XmlHelper.createDOMDocument(templateStm);

			// создание корневого элемента
			// final Element rootElem = XmlHelper.ensureRoot(docResult, "");
			final Node srcRoot = docMacro.getDocumentElement();
			final Node destRoot = srcRoot.cloneNode(false);
			docResult.appendChild(destRoot);

			final NodeList nodes = srcRoot.getChildNodes();
			if (nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					final Node srcNode = nodes.item(i);
					if ( XMLNODE_PROTOTYPE.equalsIgnoreCase(srcNode.getNodeName())) {
						// макроподстановка ...
						processMacros( docResult, destRoot, srcNode);
					} else { // клонирование обычного узла ...
						xmlCloneNodeDirectly( destRoot, srcNode, true);
					}
				}
			}

			// формирование результата
			final ByteArrayOutputStream result = XmlHelper.serialize( docResult);

			logger.info( String.format( "macro expantion SUCCESSFULL from template xml '%s' ...", streamName));

			return result;

		} catch (Throwable t) {
			final String msg = String.format( "Problem macro expanding template xml '%s' ...", streamName);
			logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
		*/
	}

	private static Node xmlCloneNodeDirectly( Node destParent, Node src, boolean deep) {
		if (src == null)
			return null;
		final Node result = src.cloneNode(deep);
		destParent.appendChild(result);
		return result;
	}

	// TODO: clone-expand
	private void processMacros(Document destDoc, Node destRoot, Node srcMacroNode) {
		if (srcMacroNode == null)
			return;

		if (this.reportDesc == null || this.reportDesc.getDsDescriptor() == null
				|| this.reportDesc.getDsDescriptor().getColumns() == null) {
			// клонирование, т.к. нет данных
			logger.info( String.format( "No report columns descriptors present for report '%s' -> cloning macro node '%s'"
					, this.reportDesc.getMnem(), srcMacroNode.getNodeName()));
			xmlCloneNodeDirectly( destRoot, srcMacroNode, true);
			return;
		}

		final Node nodeConsts = XmlHelper.findNodeByName(srcMacroNode, XMLNODE_PROTOTYPE_CONST);
		final MacroValues mconsts = parseConsts( nodeConsts);
		{
			// добавление стандартных функций
			mconsts.add( VNAME_GUID, new GuidAutoValue());
		}

		final Node nodeMacroFld = XmlHelper.findNodeByName(srcMacroNode, XMLNODE_PROTOTYPE_FIELD);

		// применяем к каждой колонке НД макросы из всех вложенных узлов nodeFld
		MacroValues curValues = new MacroValues( mconsts);
		int index = -1;
		for (ColumnDescriptor colDesc: reportDesc.getDsDescriptor().getColumns()) {
			index++;

			// автоматическая переменная для колокни данных
			curValues.add( VNAME_FLD, new ColumnDescValue(index, colDesc)); // "FLD"

			// вычисление нового набора (на основе текущего)
			curValues = curValues.calcPreNext(); // применить авто-назначения ...

			doMacroExpantion( destDoc, destRoot, curValues, colDesc);

			// вычисление "пост-значений" (корректировка left/top/width/height)
			curValues.calcPostNext(); // обновить текущие значения "на месте" ...
		}

	}

	private void doMacroExpantion(Document destDoc, Node destRoot,
			MacroValues curValues, ColumnDescriptor colDesc) {
		// TODO Auto-generated method stub
		
	}

	private MacroValues parseConsts(Node nodeConsts) {
		final MacroValues result = new MacroValues();
		final List<Node> consts = XmlHelper.findNodesList(nodeConsts, "const");
		if (consts != null) {
			for (Node item: consts) {
				final MacroValue newConst = parseConst( consts, item);
				if (newConst != null)
					result.add(newConst);
			}
		}
		return result;
	}

	private MacroValue parseConst(List<Node> consts, Node item) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Набор именованных переменных
	 * Имена регистро независимые
	 * @author rabdullin
	 */
	private static class MacroValues {
		private Map<String, MacroValue> values;

		public MacroValues() {
		}

		public void calcPostNext() {
			// TODO Auto-generated method stub
		}

		public MacroValues calcPreNext() {
			// TODO Auto-generated method stub
			return null;
		}

		public void add(MacroValue newConst) {
			if (newConst != null)
				add(newConst.name(), newConst);
		}

		public void add(String name, MacroValue newConst) {
			if (newConst != null) {
				if (newConst.name() == null)
					newConst.setName(name);
				getValues().put( (name != null) ? name.toLowerCase() : null, newConst);
			}
		}

		/**
		 * Склонировать значения другого массива
		 */
		public MacroValues(MacroValues otherToClone) {
			super();
			cloneValues( getValues(), (otherToClone != null) ? otherToClone.values : null);
		}

		private static void cloneValues( Map<String, MacroValue> dest,
				Map<String, MacroValue> src) {
			if (src == null || src.isEmpty())
				return;

			for(Map.Entry<String, MacroValue> e: src.entrySet()) {
				dest.put( e.getKey(), e.getValue().cloneValue());
			}
		}

		public Map<String, MacroValue> getValues() {
			if (values == null)
				values =  new HashMap<String, MacroValue>();
			return values;
		}

		/**
		 * По указанному названию переменной вернуть её текущее значение или 
		 * вычислить выражение для выражений вида "A.B.C"
		 * @param substring
		 * @return
		 */
		public Object calcExpr(String expr) { 
			if (expr == null || expr.length() == 0)
				return expr;
			final int i = expr.indexOf('.');
			final String varname, tail;
			if (i < 0) {// точки нет - всё выражение это название переменной
				varname = expr.toLowerCase();
				tail = null;
			} else { // точка есть - берём имя до точки и выражение после точки
				varname = expr.substring(0, i).toLowerCase();
				tail = (i < expr.length() - 1) ? expr.substring(i+1) : null;
			}
			if (!this.getValues().containsKey(varname)) {
				logger.warn( String.format("Variable '%s' not found", varname));
				return varname + ( tail != null ? "." + tail : "");
			}
			final MacroValue found = this.getValues().get(varname);
			return beanGetExpr(found, tail);
		}

		public static Object beanGetExpr(MacroValue v, String expr) {
			if (v == null)
				return null;
			if (expr == null)
				return v.getValue();
			final ReflectionValue rv = new ReflectionValue( null, v.getValue() );
			rv.setExpression(expr);
			return rv.getSubst();
		}
	}

	/**
	 * Клонируемый объект со значением 
	 */
	private interface MacroValue {

		/** текущее значение */
		Object getValue();

		/** задать текущее значение */
		void setValue(Object value);

		/**
		 * Вычисление следующего значения согласно своим установкам и приданному списку
		 * @param list приданный список значений (для ссылок на них)
		 */
		void adjustNext(MacroValues list);

		/** 
		 * подставляемое значение - обычно getValue().toString()
		 * фактически значение выражения expression, применённое к текущему 
		 * состоянию объекта
		 */
		String getSubst();

		/** Вычислительное выражение
		 */
		String getExpression();
		void setExpression(String expr);

		/** создать объект - копию */
		MacroValue cloneValue();

		/** название значения (переменной) */
		String name();
		void setName( String aname);
	}

	/**
	 * Имя даёт уникальность. Регистр роли НЕ играет.
	 * @author rabdullin
	 */
	private static abstract class MacroValueBase implements MacroValue {

		private String name, expression;

		public MacroValueBase() {
		}

		public MacroValueBase(String name) {
			this(name, null);
		}

		public MacroValueBase(String name, String expression) {
			super();
			this.name = name;
			this.expression = expression;
		}

		@Override
		abstract public Object getValue();

		@Override
		abstract public void setValue(Object value);

		@Override
		public MacroValue cloneValue() {
			try {
				return (MacroValue) this.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException( String.format("Fail to clone item %s of class %s", this.name(), this.getClass()), e);
			}
		}

		@Override
		public String name() {
			return this.name;
		}

		@Override
		public void setName(String aname) {
			this.name = aname;
		}

		@Override
		public void adjustNext(MacroValues list) {
		}

		@Override
		public String getSubst() {
			return Utils.coalesce(getValue(), name() );
		}

		@Override
		public String getExpression() {
			return this.expression;
		}

		@Override
		public void setExpression(String expr) {
			this.expression = expr;
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
			final MacroValueBase other = (MacroValueBase) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equalsIgnoreCase(other.name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append( String.format( "%s [name '%s'", this.getClass().getSimpleName(), name));
			builder.append( String.format( ", expr '%s'", expression));
			builder.append("]");
			return builder.toString();
		}

	}

	// константное выражение, для простоты тут только строковое
	private static class ConstValue extends MacroValueBase {

		private Object obj;

		public ConstValue() {
		}

//		public ConstValue(String name) {
//			this(name, null);
//		}

		public ConstValue(String name, Object obj) {
			super(name);
			this.obj = obj;
		}

		/**
		 * здесь вычисляет значение на основании своего выражения:
		 * если выражение начинается с '@', то выполняет нужные вычисления
		 * по списку list
		 */
		@Override
		public void adjustNext(MacroValues list) {
			final String expr = getExpression();
			if (expr == null || list == null)
				return;
			if( expr.startsWith("@")) { // вычисления
				setValue( list.calcExpr( expr.substring(1)));
			}
		}

		@Override
		public Object getValue() {
			return obj;
		}

		@Override
		public void setValue(Object value) {
			this.obj = value;
		}

		@Override
		public boolean equals(Object another) {
			if (this == another)
				return true;
			if (!super.equals(another)) 
				return false;
			final ConstValue other = (ConstValue) another;
			if (this.obj == null) {
				if (other.obj != null)
					return false;
			} else if (!this.obj.equals(other.obj))
				return false;
			return true;
		}

	}

	private static class GuidAutoValue extends MacroValueBase {

		private GuidAutoValue() {
			super();
		}

		private GuidAutoValue(String name) {
			super(name);
		}

		@Override
		public Object getValue() {
			return java.util.UUID.randomUUID();
		}

		@Override
		public void setValue(Object value) {
			// ignore
		}

	}

	/**
	 * В своей основе имеет базовый объект (getValue()), к которому при вызове 
	 * getSubs() применяется механизм reflection для получения его свойств, 
	 * задаваемых выражением expression.
	 * @author rabdullin
	 *
	 */
	private static class ReflectionValue extends ConstValue {

		public ReflectionValue() {
		}

		public ReflectionValue(String name) {
			super(name, null);
		}

		public ReflectionValue(String name, Object obj) {
			super(name, obj);
		}

		@Override
		public String getSubst() {
			try {
				return (getExpression() != null)
							? Utils.coalesce( PropertyUtils.getProperty(this.getValue(), getExpression()), name() )
							: super.getSubst();
			} catch (Throwable ex) {
				throw new RuntimeException( String.format("Problem getting from '%s' by %s", this.getValue(), getExpression()), ex);
			}
		}

	}

	// контейнер для прикрепления свойств (left-top-width-height) к разным объектам
	private static class RectXtender {
		public int left, top, width, height;
	}

	private static class ColumnDescXtender extends RectXtender {
		public ColumnDescriptor desc;
		public int index;

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

	}

	private static class ColumnDescValue extends ReflectionValue {

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
